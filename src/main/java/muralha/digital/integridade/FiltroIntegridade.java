package muralha.digital.integridade;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Filtros da consulta de integridade (período, equipamentos, status), validados e convertidos
 * em cláusula WHERE parametrizada.
 */
public final class FiltroIntegridade {

    public static final int PERIODO_MAXIMO_DIAS = 92;
    public static final int MAXIMO_EQUIPAMENTOS = 500;

    private final LocalDate inicio;
    private final LocalDate fim;
    private final List<Integer> idLocais;
    private final StatusIntegridade status;

    private FiltroIntegridade(LocalDate inicio, LocalDate fim, List<Integer> idLocais, StatusIntegridade status) {
        this.inicio = inicio;
        this.fim = fim;
        this.idLocais = idLocais;
        this.status = status;
    }

    /**
     * @param dtInicio  data inicial (yyyy-MM-dd), obrigatória
     * @param dtFim     data final (yyyy-MM-dd), obrigatória, inclusiva
     * @param idLocais  ids de equipamento separados por vírgula (vazio = todos)
     * @param status    status de integridade (vazio = todos)
     * @throws IllegalArgumentException com mensagem em português se algum filtro for inválido
     */
    public static FiltroIntegridade de(String dtInicio, String dtFim, String idLocais, String status) {
        LocalDate ini = data(dtInicio, "inicial");
        LocalDate fim = data(dtFim, "final");
        if (fim.isBefore(ini)) throw new IllegalArgumentException("A data final é anterior à inicial.");
        if (ChronoUnit.DAYS.between(ini, fim) + 1 > PERIODO_MAXIMO_DIAS) {
            throw new IllegalArgumentException("O período máximo é de " + PERIODO_MAXIMO_DIAS + " dias.");
        }

        Set<Integer> locais = new LinkedHashSet<>();
        if (idLocais != null && !idLocais.trim().isEmpty()) {
            for (String p : idLocais.split(",")) {
                String t = p.trim();
                if (t.isEmpty()) continue;
                try {
                    int id = Integer.parseInt(t);
                    if (id > 0) locais.add(id);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Equipamento inválido: " + t);
                }
            }
            if (locais.size() > MAXIMO_EQUIPAMENTOS) {
                throw new IllegalArgumentException("Selecione no máximo " + MAXIMO_EQUIPAMENTOS + " equipamentos.");
            }
        }

        StatusIntegridade st = null;
        if (status != null && !status.trim().isEmpty()) {
            st = StatusIntegridade.deTexto(status);
            if (st == null) throw new IllegalArgumentException("Status inválido: " + status);
        }
        return new FiltroIntegridade(ini, fim, Collections.unmodifiableList(new ArrayList<>(locais)), st);
    }

    private static LocalDate data(String texto, String nome) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe a data " + nome + ".");
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data " + nome + " inválida (use aaaa-mm-dd).");
        }
    }

    /** Cláusula e parâmetros (na ordem dos ?). */
    public static final class Clausula {
        public final String sql;
        public final List<Object> parametros;

        Clausula(String sql, List<Object> parametros) {
            this.sql = sql;
            this.parametros = parametros;
        }
    }

    /**
     * Monta "col_data &gt;= ? AND col_data &lt; ? [AND col_local IN (...)] [AND col_status = ?]".
     * O fim é exclusivo (dia seguinte à data final). Os nomes de coluna são do código, nunca do usuário.
     * @param colunaStatus nulo para ignorar o filtro de status (ex.: na view agregada)
     */
    public Clausula clausula(String colunaData, String colunaLocal, String colunaStatus) {
        StringBuilder sb = new StringBuilder();
        List<Object> ps = new ArrayList<>();
        sb.append(colunaData).append(" >= ? AND ").append(colunaData).append(" < ?");
        ps.add(java.sql.Date.valueOf(inicio));
        ps.add(java.sql.Date.valueOf(fim.plusDays(1)));
        if (!idLocais.isEmpty()) {
            sb.append(" AND ").append(colunaLocal).append(" IN (");
            for (int i = 0; i < idLocais.size(); i++) {
                sb.append(i == 0 ? "?" : ",?");
                ps.add(idLocais.get(i));
            }
            sb.append(")");
        }
        if (status != null && colunaStatus != null) {
            sb.append(" AND ").append(colunaStatus).append(" = ?");
            ps.add(status.name());
        }
        return new Clausula(sb.toString(), ps);
    }

    public LocalDate getInicio() { return inicio; }

    public LocalDate getFim() { return fim; }

    public List<Integer> getIdLocais() { return idLocais; }

    public StatusIntegridade getStatus() { return status; }
}
