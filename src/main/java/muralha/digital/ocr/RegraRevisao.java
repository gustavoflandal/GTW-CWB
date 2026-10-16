package muralha.digital.ocr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Regras puras do encaminhamento automático para revisão manual (TR 5.7.4.3 e 5.2.2.5):
 * divergência entre o OCR do equipamento e o central, score abaixo do limiar e, se configurado,
 * passagem sem placa lida ou score ausente.
 */
public final class RegraRevisao {

    /** Motivo do encaminhamento, na ordem de prioridade. */
    public enum Motivo {
        SEM_LEITURA("Passagem sem placa lida"),
        DIVERGENCIA_CAMPO_CENTRAL("Placa do equipamento difere da leitura central"),
        SCORE_BAIXO("Score de OCR abaixo do limiar"),
        SCORE_AUSENTE("Score de OCR não informado");

        private final String descricao;

        Motivo(String descricao) { this.descricao = descricao; }

        public String getDescricao() { return descricao; }
    }

    /** Resultado da avaliação: motivo nulo = não vai para revisão. */
    public static final class Avaliacao {
        private final Motivo motivo;
        private final Double scoreMinimo;
        private final String detalhe;

        Avaliacao(Motivo motivo, Double scoreMinimo, String detalhe) {
            this.motivo = motivo;
            this.scoreMinimo = scoreMinimo;
            this.detalhe = detalhe;
        }

        public Motivo getMotivo() { return motivo; }

        public Double getScoreMinimo() { return scoreMinimo; }

        public String getDetalhe() { return detalhe; }

        public boolean encaminhar() { return motivo != null; }
    }

    private RegraRevisao() {}

    /**
     * @param leituras         leituras da passagem (embarcadas e centrais)
     * @param limiar           score mínimo (0-100) do equipamento
     * @param revisarSemScore  se a ausência de score também encaminha para revisão
     * @param revisarSemLeitura se a passagem sem nenhuma placa lida encaminha para revisão
     */
    public static Avaliacao avaliar(List<LeituraPlaca> leituras, double limiar, boolean revisarSemScore,
            boolean revisarSemLeitura) {
        Double minimo = null;
        boolean algumaPlaca = false;
        boolean semScore = false;
        String piorPlaca = null;
        Map<String, LeituraPlaca> embarcadas = new HashMap<>();
        List<LeituraPlaca> centrais = new ArrayList<>();

        for (LeituraPlaca l : leituras) {
            if (l.getOrigem() == LeituraPlaca.Origem.CENTRAL) { centrais.add(l); continue; }
            if (l.getOrigem() != LeituraPlaca.Origem.EMBARCADA) continue;
            embarcadas.put(chave(l), l);
            if (l.getPlaca() == null) continue;
            algumaPlaca = true;
            if (l.getScore() == null) {
                semScore = true;
            } else if (minimo == null || l.getScore() < minimo) {
                minimo = l.getScore();
                piorPlaca = l.getPlaca();
            }
        }

        if (!algumaPlaca) {
            return revisarSemLeitura ? new Avaliacao(Motivo.SEM_LEITURA, null, "Nenhuma placa lida pelo equipamento.")
                : new Avaliacao(null, null, null);
        }
        for (LeituraPlaca c : centrais) {
            LeituraPlaca e = embarcadas.get(chave(c));
            if (e != null && e.getPlaca() != null && c.getPlaca() != null && !e.getPlaca().equals(c.getPlaca())) {
                return new Avaliacao(Motivo.DIVERGENCIA_CAMPO_CENTRAL, minimo,
                    "Equipamento leu " + e.getPlaca() + "; leitura central " + c.getPlaca() + " (" + c.getTipo() + ").");
            }
        }
        if (minimo != null && minimo < limiar) {
            return new Avaliacao(Motivo.SCORE_BAIXO, minimo,
                "Placa " + piorPlaca + " com score " + formatar(minimo) + " (limiar " + formatar(limiar) + ").");
        }
        if (semScore && revisarSemScore) {
            return new Avaliacao(Motivo.SCORE_AUSENTE, minimo, "Equipamento não informou o score da leitura.");
        }
        return new Avaliacao(null, minimo, null);
    }

    private static String chave(LeituraPlaca l) { return l.getTipo() + "#" + l.getOrdem(); }

    static String formatar(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.format(java.util.Locale.ROOT, "%.2f", v);
    }

    /**
     * Leituras embarcadas a partir das colunas da passagem: placa (tração, ordem 1) e
     * placa_frontal (ordem 2, só se houver). {@code scores[i]} vale para a leitura de ordem i+1.
     */
    public static List<LeituraPlaca> leiturasDaPassagem(String placa, String placaFrontal, Double[] scores) {
        List<LeituraPlaca> r = new ArrayList<>();
        r.add(new LeituraPlaca(1, LeituraPlaca.Tipo.TRACAO, placa, valor(scores, 0), LeituraPlaca.Origem.EMBARCADA));
        if (placaFrontal != null && !placaFrontal.trim().isEmpty()) {
            r.add(new LeituraPlaca(2, LeituraPlaca.Tipo.FRONTAL, placaFrontal, valor(scores, 1), LeituraPlaca.Origem.EMBARCADA));
        }
        return r;
    }

    private static Double valor(Double[] scores, int i) {
        return scores == null || i >= scores.length ? null : scores[i];
    }

    /**
     * Scores do JSON de ia.veiculo_caracteristica.dado_bruto: número (vale para a ordem 1) ou lista
     * (um por ordem). Escala 1 converte 0-1 para 0-100. Valores fora de 0-100 são ignorados.
     */
    public static Double[] scoresDoDadoBruto(String json, String campo, double escala) {
        if (json == null || json.trim().isEmpty() || campo == null) return new Double[0];
        JsonElement atual;
        try {
            atual = JsonParser.parseString(json);
        } catch (RuntimeException e) {
            return new Double[0];
        }
        for (String parte : campo.split("\\.")) {
            if (atual == null || !atual.isJsonObject()) return new Double[0];
            atual = atual.getAsJsonObject().get(parte);
        }
        if (atual == null || atual.isJsonNull()) return new Double[0];
        if (atual.isJsonArray()) {
            JsonArray a = atual.getAsJsonArray();
            Double[] r = new Double[a.size()];
            for (int i = 0; i < a.size(); i++) r[i] = numero(a.get(i), escala);
            return r;
        }
        return new Double[] { numero(atual, escala) };
    }

    private static Double numero(JsonElement e, double escala) {
        if (e == null || !e.isJsonPrimitive()) return null;
        try {
            double v = e.getAsDouble() * (escala == 1 ? 100 : 1);
            return v < 0 || v > 100 ? null : v;
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
