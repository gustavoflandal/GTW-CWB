package muralha.digital.motivosolicitacaorelatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class MotivosSolicitacoesRelatorios {

    private static Logger logger = LogManager.getLogger(MotivosSolicitacoesRelatorios.class);

    public static Boolean RegistrarMotivoSolicitacao(int id_usuario, String motivo, String placa, String tipoSolicitacao) throws ConexaoException, SQLException {

        String sql = "INSERT INTO muralha.motivo_solicitacao_relatorio (id_usuario, motivo, data_criacao, placa, tipo_solicitacao) VALUES (?, ?, GETDATE(), ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id_usuario);
            ps.setString(2, motivo);
            
            if (placa != null && !placa.trim().isEmpty()) {
                ps.setString(3, placa);
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } 

            if (tipoSolicitacao != null && !tipoSolicitacao.isEmpty()) {
                ps.setInt(4, Integer.parseInt(tipoSolicitacao));
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            int linhasAfetadas = ps.executeUpdate();
            return linhasAfetadas > 0;

        } catch (Exception e) {
            logger.error("Erro ao registrar o motivo da solicitação do relatório! " + e.getMessage(), e);
            return false;
        }
    }
}