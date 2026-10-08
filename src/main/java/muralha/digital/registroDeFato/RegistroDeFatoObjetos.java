package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class RegistroDeFatoObjetos {

    private static Logger logger = LogManager.getLogger(RegistroDeFatoObjetos.class);

    /**
     * Insere uma lista de objetos associados ao registro de fato.
     */
    public static void inserirListaObjetos(List<RegistroDeFatoObjeto> objetos) throws ConexaoException, SQLException {
        String sql = "INSERT INTO muralha.registro_fato_objeto (id_registro_fato, tipo, descricao) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            for (RegistroDeFatoObjeto objeto : objetos) {
                ps.setLong(1, objeto.getIdRegistroFato());
                ps.setString(2, objeto.getTipo());
                ps.setString(3, objeto.getDescricao());

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Erro ao inserir objetos do registro de fato", e);
            throw new SQLException("Erro ao cadastrar os objetos do registro de fato!", e);
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
     * Insere um único objeto no banco.
     */
    public static void inserirObjeto(RegistroDeFatoObjeto objeto) throws ConexaoException, SQLException {
        String sql = "INSERT INTO muralha.registro_fato_objeto (id_registro_fato, tipo, descricao) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            ps.setLong(1, objeto.getIdRegistroFato());
            ps.setString(2, objeto.getTipo());
            ps.setString(3, objeto.getDescricao());

            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Erro ao inserir objeto do registro de fato", e);
            throw new SQLException("Erro ao cadastrar o objeto do registro de fato!", e);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("Erro ao fechar conexão/recursos", e);
            }
        }
    }
    
    public static void removerObjeto(Integer idObjeto) throws ConexaoException, SQLException {
        if (idObjeto == null) return;

        String sql = "DELETE FROM muralha.registro_fato_objeto WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idObjeto);
            ps.executeUpdate();
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao remover objeto do registro de fato", e);
            throw e;
        }
    }
}
