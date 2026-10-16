package muralha.digital.alerta.operacional;

import muralha.digital.auditoria.AuditoriaService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aplica os tratamentos automáticos configurados para um tipo de alerta.
 * Cada tratamento é avaliado; os marcados como automáticos são executados
 * ao chegar o alerta.
 */
public final class ExecutorTratamento {

    private static final Logger logger = Logger.getLogger(ExecutorTratamento.class);

    private ExecutorTratamento() {}

    public static class TratamentoConfigurado {
        private final TratamentoAlerta tratamento;
        private final boolean automatico;
        private final String parametrosJson;

        public TratamentoConfigurado(TratamentoAlerta tratamento, boolean automatico, String parametrosJson) {
            this.tratamento = tratamento;
            this.automatico = automatico;
            this.parametrosJson = parametrosJson;
        }

        public TratamentoAlerta getTratamento() { return tratamento; }
        public boolean isAutomatico() { return automatico; }
        public String getParametrosJson() { return parametrosJson; }
    }

    public static List<TratamentoConfigurado> tratamentosDo(Connection conn, UUID idTipo) throws SQLException {
        List<TratamentoConfigurado> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT tratamento, automatico, parametros_json "
                + "FROM muralha.tipo_alerta_tratamento "
                + "WHERE id_tipo_alerta_ocorrencia = ? AND ativo = 1")) {
            ps.setString(1, idTipo.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TratamentoAlerta t = TratamentoAlerta.interpretar(rs.getString("tratamento"));
                    if (t != null) {
                        lista.add(new TratamentoConfigurado(t, rs.getBoolean("automatico"), rs.getString("parametros_json")));
                    }
                }
            }
        }
        return lista;
    }

    public static int aplicarAutomaticos(Connection conn, UUID idAlerta, UUID idTipo) {
        int aplicados = 0;
        try {
            List<TratamentoConfigurado> tratamentos = tratamentosDo(conn, idTipo);
            for (TratamentoConfigurado tc : tratamentos) {
                if (!tc.isAutomatico()) continue;
                switch (tc.getTratamento()) {
                    case REGISTRO_ESTATISTICO:
                        AuditoriaService.registrarSistema("alerta-tratamento-automatico",
                            "REGISTRO_ESTATISTICO", idAlerta.toString(), "alerta " + idAlerta);
                        aplicados++;
                        break;
                    case PRIORIZAR_AUDITORIA:
                        AuditoriaService.registrarSistema("alerta-tratamento-automatico",
                            "PRIORIZAR_AUDITORIA", idAlerta.toString(), "alerta " + idAlerta);
                        aplicados++;
                        break;
                    case GERAR_OCORRENCIA:
                        gerarOcorrenciaAutomatica(conn, idAlerta);
                        aplicados++;
                        break;
                    default:
                        logger.info("Tratamento " + tc.getTratamento() + " requer ação manual para alerta " + idAlerta);
                        break;
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao aplicar tratamentos automáticos: " + e.getMessage(), e);
        }
        return aplicados;
    }

    private static void gerarOcorrenciaAutomatica(Connection conn, UUID idAlerta) throws SQLException {
        UUID statusOcorrencia = UUID.fromString("15EBBA5F-C805-449E-83CC-227ED3B3AD3C");
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE muralha.alerta SET id_status_alerta = ? WHERE id = ? AND id_status_alerta = ?")) {
            ps.setString(1, statusOcorrencia.toString());
            ps.setString(2, idAlerta.toString());
            ps.setString(3, "5479C6D9-7381-4492-99BE-442EF2E741B0");
            ps.executeUpdate();
        }
    }
}
