package muralha.digital.integridade;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import muralha.digital.util.HashUtil;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Regras puras (sem banco) da conferência de integridade das imagens recebidas (TR 5.2.1.2).
 * O hash é sempre recalculado sobre os bytes gravados e comparado com o hash da origem;
 * nunca se grava "OK" sem comparação.
 */
public final class ConferenciaIntegridade {

    private static final Pattern HEX = Pattern.compile("[0-9a-f]+");

    private ConferenciaIntegridade() {}

    /**
     * Confere uma imagem.
     * @param imagem     bytes gravados em veiculo_tempo_real_imagem
     * @param origem     origem configurada para o equipamento
     * @param hashOrigem hash obtido da origem (nulo se não recebido)
     */
    public static ResultadoConferencia conferir(byte[] imagem, OrigemHash origem, String hashOrigem) {
        if (imagem == null || imagem.length == 0) {
            return new ResultadoConferencia(StatusIntegridade.ERRO_LEITURA,
                "Imagem vazia ou ilegível no banco.", null, normalizar(hashOrigem));
        }
        String sha256 = HashUtil.sha256Hex(imagem);

        if (origem == null || origem.getTipo() == OrigemHash.Tipo.AUSENTE) {
            return new ResultadoConferencia(StatusIntegridade.SEM_CONFERENCIA,
                "Equipamento sem origem de hash configurada.", sha256, null);
        }
        String esperado = normalizar(hashOrigem);
        if (esperado == null) {
            return new ResultadoConferencia(StatusIntegridade.SEM_CONFERENCIA,
                "Hash de origem não recebido (" + origem.formatar() + ").", sha256, null);
        }
        String algoritmo = algoritmoPorTamanho(esperado);
        if (algoritmo == null || !HEX.matcher(esperado).matches()) {
            return new ResultadoConferencia(StatusIntegridade.DIVERGENTE,
                "Hash de origem em formato inválido (esperado SHA-256, SHA-384 ou SHA-512 em hexadecimal).",
                sha256, esperado);
        }
        String calculado = "SHA-256".equals(algoritmo) ? sha256 : HashUtil.hex(imagem, algoritmo);
        if (calculado.equals(esperado)) {
            return new ResultadoConferencia(StatusIntegridade.INTEGRO, null, sha256, esperado);
        }
        return new ResultadoConferencia(StatusIntegridade.DIVERGENTE,
            algoritmo + " calculado difere do hash de origem: imagem corrompida ou adulterada.", sha256, esperado);
    }

    /** Algoritmo SHA-2 pelo tamanho do hash em hexadecimal (64, 96 ou 128); nulo se outro tamanho. */
    public static String algoritmoPorTamanho(String hashHex) {
        if (hashHex == null) return null;
        switch (hashHex.length()) {
            case 64: return "SHA-256";
            case 96: return "SHA-384";
            case 128: return "SHA-512";
            default: return null;
        }
    }

    /**
     * Extrai o hash do JSON de ia.veiculo_caracteristica.dado_bruto.
     * O campo é um caminho com pontos. Se o valor for uma lista, usa o elemento do índice da
     * imagem; se for um texto, vale só para a imagem de índice 0.
     * @return o hash, ou nulo se o JSON, o campo ou o elemento não existirem
     */
    public static String extrairDoDadoBruto(String json, String campo, int indiceImagem) {
        if (json == null || json.trim().isEmpty() || campo == null) return null;
        JsonElement atual;
        try {
            atual = JsonParser.parseString(json);
        } catch (RuntimeException e) {
            return null;
        }
        for (String parte : campo.split("\\.")) {
            if (atual == null || !atual.isJsonObject()) return null;
            JsonObject obj = atual.getAsJsonObject();
            atual = obj.get(parte);
        }
        if (atual == null || atual.isJsonNull()) return null;
        if (atual.isJsonArray()) {
            JsonArray lista = atual.getAsJsonArray();
            if (indiceImagem < 0 || indiceImagem >= lista.size()) return null;
            JsonElement item = lista.get(indiceImagem);
            return item.isJsonPrimitive() ? item.getAsString() : null;
        }
        if (atual.isJsonPrimitive()) {
            return indiceImagem == 0 ? atual.getAsString() : null;
        }
        return null;
    }

    private static String normalizar(String hash) {
        if (hash == null) return null;
        String h = hash.trim().toLowerCase(Locale.ROOT);
        return h.isEmpty() ? null : h;
    }
}
