package muralha.digital.ocr;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Filtros da fila de revisão de placas (TR 5.7.5.4): período da passagem, equipamentos, score,
 * situação, motivo, tipo de intervenção (fila, lote ou consulta) e operador.
 */
public final class FiltroRevisao {

    public static final int PERIODO_MAXIMO_DIAS = 92;
    public static final Set<String> SITUACOES = Set.of("PENDENTE", "CORRIGIDA", "CONFIRMADA");
    public static final Set<String> MOTIVOS = Set.of("SEM_LEITURA", "DIVERGENCIA_CAMPO_CENTRAL", "SCORE_BAIXO", "SCORE_AUSENTE", "MANUAL");
    public static final Set<String> INTERVENCOES = Set.of("FILA", "LOTE", "CONSULTA");

    private final String sql;
    private final List<Object> parametros;

    private FiltroRevisao(String sql, List<Object> parametros) {
        this.sql = sql;
        this.parametros = parametros;
    }

    /** @throws IllegalArgumentException com mensagem em português */
    public static FiltroRevisao de(String dtInicio, String dtFim, String idLocais, String situacao, String motivo,
            String scoreMin, String scoreMax, String intervencao, String idOperador) {
        LocalDate ini = data(dtInicio, "inicial"), fim = data(dtFim, "final");
        if (fim.isBefore(ini)) throw new IllegalArgumentException("A data final é anterior à inicial.");
        if (ChronoUnit.DAYS.between(ini, fim) + 1 > PERIODO_MAXIMO_DIAS) {
            throw new IllegalArgumentException("O período máximo é de " + PERIODO_MAXIMO_DIAS + " dias.");
        }
        StringBuilder sb = new StringBuilder("r.dt_passagem >= ? AND r.dt_passagem < ?");
        List<Object> ps = new ArrayList<>();
        ps.add(java.sql.Date.valueOf(ini));
        ps.add(java.sql.Date.valueOf(fim.plusDays(1)));

        Set<Integer> locais = new LinkedHashSet<>();
        if (idLocais != null) {
            for (String t : idLocais.split(",")) {
                if (t.trim().isEmpty()) continue;
                try { int v = Integer.parseInt(t.trim()); if (v > 0) locais.add(v); }
                catch (NumberFormatException e) { throw new IllegalArgumentException("Equipamento inválido: " + t.trim()); }
            }
        }
        if (locais.size() > 500) throw new IllegalArgumentException("Selecione no máximo 500 equipamentos.");
        if (!locais.isEmpty()) {
            sb.append(" AND r.id_local IN (");
            int i = 0;
            for (Integer id : locais) { sb.append(i++ == 0 ? "?" : ",?"); ps.add(id); }
            sb.append(')');
        }
        String s = codigo(situacao, SITUACOES, "Situação");
        if (s != null) { sb.append(" AND r.situacao = ?"); ps.add(s); }
        String m = codigo(motivo, MOTIVOS, "Motivo");
        if (m != null) { sb.append(" AND r.motivo = ?"); ps.add(m); }
        String iv = codigo(intervencao, INTERVENCOES, "Tipo de intervenção");
        if (iv != null) { sb.append(" AND r.origem_intervencao = ?"); ps.add(iv); }
        Double min = score(scoreMin), max = score(scoreMax);
        if (min != null && max != null && min > max) throw new IllegalArgumentException("Score mínimo maior que o máximo.");
        if (min != null) { sb.append(" AND r.score_minimo >= ?"); ps.add(min); }
        if (max != null) { sb.append(" AND r.score_minimo <= ?"); ps.add(max); }
        if (idOperador != null && !idOperador.trim().isEmpty()) {
            try { ps.add(Integer.parseInt(idOperador.trim())); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Operador inválido."); }
            sb.append(" AND r.id_usuario_tratamento = ?");
        }
        return new FiltroRevisao(sb.toString(), ps);
    }

    private static LocalDate data(String t, String nome) {
        if (t == null || t.trim().isEmpty()) throw new IllegalArgumentException("Informe a data " + nome + ".");
        try { return LocalDate.parse(t.trim()); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Data " + nome + " inválida (use aaaa-mm-dd)."); }
    }

    private static String codigo(String v, Set<String> validos, String nome) {
        if (v == null || v.trim().isEmpty()) return null;
        String u = v.trim().toUpperCase();
        if (!validos.contains(u)) throw new IllegalArgumentException(nome + " inválido: " + v.trim());
        return u;
    }

    private static Double score(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        try {
            double d = Double.parseDouble(v.trim().replace(',', '.'));
            if (d < 0 || d > 100) throw new IllegalArgumentException("Score deve estar entre 0 e 100.");
            return d;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Score inválido: " + v.trim());
        }
    }

    /** Condição sobre o alias {@code r} (muralha.vtr_revisao_placa). */
    public String getSql() { return sql; }

    public List<Object> getParametros() { return parametros; }
}
