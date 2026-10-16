package muralha.digital.alerta.operacional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Constrói filtros adicionais para a consulta de histórico de alertas (5.3.6.2).
 * Complementa os filtros já existentes em AlertaOcorrenciaServlet com:
 * localidade, usuário responsável, resultado do tratamento e exportação CSV.
 */
public final class FiltroHistorico {

    private final String sqlExtra;
    private final List<Object> parametros;

    private FiltroHistorico(String sqlExtra, List<Object> parametros) {
        this.sqlExtra = sqlExtra;
        this.parametros = parametros;
    }

    public String getSqlExtra() { return sqlExtra; }
    public List<Object> getParametros() { return parametros; }
    public boolean temFiltro() { return !sqlExtra.isEmpty(); }

    public static FiltroHistorico de(String localidade, String usuarioResp, String resultadoTratamento) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (localidade != null && !localidade.trim().isEmpty()) {
            sql.append(" AND lv.id_localidade = ?");
            try {
                params.add(Integer.parseInt(localidade.trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Localidade inválida: " + localidade);
            }
        }

        if (usuarioResp != null && !usuarioResp.trim().isEmpty()) {
            sql.append(" AND a.id_usuario = ?");
            try {
                params.add(Integer.parseInt(usuarioResp.trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Usuário inválido: " + usuarioResp);
            }
        }

        if (resultadoTratamento != null && !resultadoTratamento.trim().isEmpty()) {
            String r = resultadoTratamento.trim().toUpperCase();
            if ("PENDENTE".equals(r) || "OCORRENCIA".equals(r) || "DESCARTADO".equals(r) || "VINCULADO".equals(r)) {
                sql.append(" AND sa.status = ?");
                params.add(r);
            } else {
                throw new IllegalArgumentException("Resultado de tratamento desconhecido: " + resultadoTratamento);
            }
        }

        return new FiltroHistorico(sql.toString(), params);
    }
}
