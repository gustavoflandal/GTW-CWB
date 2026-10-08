package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class RegistroDeFatoEnderecos {

    private static Logger logger = LogManager.getLogger(RegistroDeFatoEnderecos.class);

    /**
     * Insere um único endereço.
     */
    public static void inserirEndereco(RegistroDeFatoEndereco endereco) throws ConexaoException, SQLException {
        String sql = "INSERT INTO muralha.registro_fato_endereco "
                   + "(id_registro_fato, id_tipo_evento, id_cidade, cep, bairro, rua, numero, complemento, latitude, longitude) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, endereco.getIdRegistroFato());
            ps.setInt(2, endereco.getIdTipoEvento());
            ps.setInt(3, endereco.getIdCidade());
            ps.setString(4, endereco.getCep());
            ps.setString(5, endereco.getBairro());
            ps.setString(6, endereco.getRua());
            ps.setInt(7, endereco.getNumero());
            ps.setString(8, endereco.getComplemento());

            if (endereco.getLatitude() != null) {
                ps.setBigDecimal(9, endereco.getLatitude());
            } else {
                ps.setNull(9, java.sql.Types.DECIMAL);
            }

            if (endereco.getLongitude() != null) {
                ps.setBigDecimal(10, endereco.getLongitude());
            } else {
                ps.setNull(10, java.sql.Types.DECIMAL);
            }

            ps.executeUpdate();
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao inserir endereço do registro de fato", e);
            throw e;
        }
    }

    /**
     * Insere uma lista de endereços em batch.
     */
    public static void inserirListaEnderecos(List<RegistroDeFatoEndereco> enderecos) throws ConexaoException, SQLException {
        String sql = "INSERT INTO muralha.registro_fato_endereco "
                   + "(id_registro_fato, id_tipo_evento, id_cidade, cep, bairro, rua, numero, complemento, latitude, longitude) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (RegistroDeFatoEndereco endereco : enderecos) {
                ps.setLong(1, endereco.getIdRegistroFato());
                ps.setInt(2, endereco.getIdTipoEvento());
                ps.setInt(3, endereco.getIdCidade());
                ps.setString(4, endereco.getCep());
                ps.setString(5, endereco.getBairro());
                ps.setString(6, endereco.getRua());
                ps.setInt(7, endereco.getNumero());
                ps.setString(8, endereco.getComplemento());

                if (endereco.getLatitude() != null) {
                    ps.setBigDecimal(9, endereco.getLatitude());
                } else {
                    ps.setNull(9, java.sql.Types.DECIMAL);
                }

                if (endereco.getLongitude() != null) {
                    ps.setBigDecimal(10, endereco.getLongitude());
                } else {
                    ps.setNull(10, java.sql.Types.DECIMAL);
                }

                ps.addBatch();
            }

            ps.executeBatch();
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao inserir lista de endereços do registro de fato", e);
            throw e;
        }
    }

    /**
     * Altera um endereço existente pelo ID.
     */
    public static void alterarEndereco(RegistroDeFatoEndereco endereco) throws ConexaoException, SQLException {
        String sql = "UPDATE muralha.registro_fato_endereco SET "
                   + "id_tipo_evento = ?, id_cidade = ?, cep = ?, bairro = ?, rua = ?, numero = ?, complemento = ?, latitude = ?, longitude = ? "
                   + "WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, endereco.getIdTipoEvento());
            ps.setInt(2, endereco.getIdCidade());
            ps.setString(3, endereco.getCep());
            ps.setString(4, endereco.getBairro());
            ps.setString(5, endereco.getRua());
            ps.setInt(6, endereco.getNumero());
            ps.setString(7, endereco.getComplemento());

            if (endereco.getLatitude() != null) {
                ps.setBigDecimal(8, endereco.getLatitude());
            } else {
                ps.setNull(8, java.sql.Types.DECIMAL);
            }

            if (endereco.getLongitude() != null) {
                ps.setBigDecimal(9, endereco.getLongitude());
            } else {
                ps.setNull(9, java.sql.Types.DECIMAL);
            }

            ps.setLong(10, endereco.getId());

            ps.executeUpdate();
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao alterar endereço do registro de fato", e);
            throw e;
        }
    }
    
    public static void alterarEnderecoParcial(RegistroDeFatoEndereco endereco, long idRegistroFato) throws ConexaoException, SQLException {
        String sql = "UPDATE muralha.registro_fato_endereco SET "
                   + "bairro = ?, rua = ?, numero = ?, complemento = ?, latitude = ?, longitude = ? "
                   + "WHERE id_registro_fato = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, endereco.getBairro());
            ps.setString(2, endereco.getRua());
            ps.setInt(3, endereco.getNumero());
            ps.setString(4, endereco.getComplemento());

            if (endereco.getLatitude() != null) {
                ps.setBigDecimal(5, endereco.getLatitude());
            } else {
                ps.setNull(5, java.sql.Types.DECIMAL);
            }

            if (endereco.getLongitude() != null) {
                ps.setBigDecimal(6, endereco.getLongitude());
            } else {
                ps.setNull(6, java.sql.Types.DECIMAL);
            }

            ps.setLong(7, idRegistroFato);

            ps.executeUpdate();
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao alterar endereço do registro de fato", e);
            throw e;
        }
    }

    /**
     * Remove um endereço pelo ID.
     */
    public static void removerEndereco(Integer idEndereco) throws ConexaoException, SQLException {
        if (idEndereco == null) return;

        String sql = "DELETE FROM muralha.registro_fato_endereco WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idEndereco);
            ps.executeUpdate();
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao remover endereço do registro de fato", e);
            throw e;
        }
    }
}
