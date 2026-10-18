package muralha.digital.alerta.operacional;

import muralha.digital.auditoria.AuditoriaService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Executa um tratamento automático configurado para o tipo de um alerta.
 * Chamado por {@link JobTratamentoAutomatico}; cada execução fica registrada em
 * muralha.alerta_tratamento_execucao para não se repetir.
 */
public final class ExecutorTratamento {

    static final String STATUS_PENDENTE = "5479C6D9-7381-4492-99BE-442EF2E741B0";
    static final String STATUS_OCORRENCIA = "15EBBA5F-C805-449E-83CC-227ED3B3AD3C";

    private ExecutorTratamento() {}

    /** Executa o tratamento e devolve o resultado registrado na tabela de execuções. */
    public static String executar(Connection conn, UUID idAlerta, TratamentoAlerta tratamento) throws SQLException {
        switch (tratamento) {
            case REGISTRO_ESTATISTICO:
            case PRIORIZAR_AUDITORIA:
                AuditoriaService.registrarSistema("alerta-tratamento-automatico",
                    tratamento.name(), idAlerta.toString(), tratamento.getDescricao());
                return "Registrado na auditoria";
            case GERAR_OCORRENCIA:
                return gerarOcorrencia(conn, idAlerta)
                    ? "Alerta passou de PENDENTE para OCORRÊNCIA"
                    : "Sem efeito: o alerta já não estava pendente";
            default:
                return "Requer ação do operador";
        }
    }

    private static boolean gerarOcorrencia(Connection conn, UUID idAlerta) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE muralha.alerta SET id_status_alerta = ? WHERE id = ? AND id_status_alerta = ?")) {
            ps.setString(1, STATUS_OCORRENCIA);
            ps.setString(2, idAlerta.toString());
            ps.setString(3, STATUS_PENDENTE);
            return ps.executeUpdate() > 0;
        }
    }
}
