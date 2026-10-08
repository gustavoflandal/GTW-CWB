package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class RegistroDeFatoLinks {

    private static Logger logger = LogManager.getLogger(RegistroDeFatoLinks.class);

    /**
     * Insere uma lista de links associados ao registro de fato.
     */
    public static void inserirListaLinks(List<RegistroDeFatoLink> links) throws ConexaoException, SQLException {
        String sql = "INSERT INTO muralha.registro_fato_link (id_registro_fato, url, detalhamento) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            for (RegistroDeFatoLink link : links) {
                ps.setLong(1, link.getIdRegistroFato());
                ps.setString(2, link.getUrl());
                ps.setString(3, link.getDetalhamento());

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Erro ao inserir links do registro de fato", e);
            throw new SQLException("Erro ao cadastrar os links do registro de fato!", e);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("Erro ao fechar conexão/recursos", e);
            }
        }
    }

    /**
     * Insere um único link no banco.
     */
    public static void inserirLink(RegistroDeFatoLink link) throws ConexaoException, SQLException {
        String sql = "INSERT INTO muralha.registro_fato_link (id_registro_fato, url, detalhamento) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            ps.setLong(1, link.getIdRegistroFato());
            ps.setString(2, link.getUrl());
            ps.setString(3, link.getDetalhamento());

            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Erro ao inserir link do registro de fato", e);
            throw new SQLException("Erro ao cadastrar o link do registro de fato!", e);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("Erro ao fechar conexão/recursos", e);
            }
        }
    }
    
    public static void removerLink(int id) throws SQLException, ConexaoException {
        String sql = "DELETE FROM muralha.registro_fato_link WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
