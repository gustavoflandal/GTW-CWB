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

import muralha.digital.boletim.BoletimDocumento;
import muralha.digital.boletim.BoletimDocumentos;

public class RegistroDeFatoDocumentos {

    private static Logger logger = LogManager.getLogger(RegistroDeFatoDocumentos.class);

    public static void inserirListaDocumentos(List<RegistroDeFatoDocumento> documentos) throws ConexaoException, SQLException {

        String sql = "INSERT INTO muralha.registro_fato_documento " +
                     "(id_registro_fato, tipo, dir_arquivo, detalhamento, id_atendimento) " +
                     "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            for (RegistroDeFatoDocumento doc : documentos) {
                ps.setLong(1, doc.getIdRegistroFato());
                ps.setString(2, doc.getTipo());
                ps.setString(3, doc.getDirArquivo());
                ps.setString(4, doc.getDetalhamento());

                // id_atendimento pode ser null, usar setObject para evitar erro
                if (doc.getIdAtendimento() != null) {
                    ps.setInt(5, doc.getIdAtendimento());
                } else {
                    ps.setNull(5, java.sql.Types.INTEGER);
                }

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            logger.error("Erro ao inserir documentos", e);
            throw new SQLException("Erro ao cadastrar documentos do registro de fato!", e);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("Erro ao fechar conexão/recursos", e);
            }
        }
    }
    
    public static RegistroDeFatoDocumento ObterPorId(Integer id) throws ConexaoException, SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuilder sbSQL = new StringBuilder();
			sbSQL.append("SELECT id, id_registro_fato, tipo, dir_arquivo, detalhamento ");
			sbSQL.append("FROM muralha.registro_fato_documento WHERE id = ?");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id);

			rs = ps.executeQuery();

			if (rs.next()) {
				RegistroDeFatoDocumento documento = new RegistroDeFatoDocumento();
				documento.setId(rs.getInt("id"));
				documento.setIdRegistroFato(rs.getLong("id_registro_fato"));
				documento.setTipo(rs.getString("tipo"));
				documento.setDirArquivo(rs.getString("dir_arquivo"));
				documento.setDetalhamento(rs.getString("detalhamento"));
				return documento;
			}

			return null;

		} catch (Exception e) {
			String msgErro = "Erro ao buscar RegistroDeFatoDocumento por ID!";
			Logger.getLogger(BoletimDocumentos.class).error(msgErro + ": " + e.getMessage(), e);
			return null;

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				Logger.getLogger(BoletimDocumentos.class).error("Erro ao fechar conexão!", e);
			}
		}
	}
    
    public static void removerDocumento(int idDocumento) throws ConexaoException, SQLException {
        String sql = "DELETE FROM muralha.registro_fato_documento WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDocumento);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erro ao remover documento do registro de fato", e);
            throw e;
        }
    }
    
    public static void inserirDocumento(RegistroDeFatoDocumento doc) throws ConexaoException, SQLException {
        String sql = "INSERT INTO muralha.registro_fato_documento " +
                     "(id_registro_fato, tipo, dir_arquivo, detalhamento, id_atendimento) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, doc.getIdRegistroFato());
            ps.setString(2, doc.getTipo());
            ps.setString(3, doc.getDirArquivo());
            ps.setString(4, doc.getDetalhamento());

            // id_atendimento pode ser null, usar setObject
            if (doc.getIdAtendimento() != null) {
                ps.setInt(5, doc.getIdAtendimento());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("Erro ao inserir documento do registro de fato", e);
            throw new SQLException("Erro ao cadastrar documento do registro de fato!", e);
        }
    }
}
