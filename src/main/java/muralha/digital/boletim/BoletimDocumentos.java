package muralha.digital.boletim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class BoletimDocumentos {

	private static final Logger logger = LogManager.getLogger(BoletimDocumentos.class);

	public BoletimDocumentos() {
		super();
	}

	public static boolean inserirBoletimDocumento(BoletimDocumento documento) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			sbSQL.append("INSERT INTO muralha.boletim_documento ");
			sbSQL.append("(id_boletim, tipo, dir_arquivo, detalhamento) ");
			sbSQL.append("VALUES (?, ?, ?, ?)");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// 1. id_boletim
			if (documento.getIdBoletim() != null) {
				ps.setInt(1, documento.getIdBoletim());
			} else {
				ps.setNull(1, java.sql.Types.INTEGER);
			}

			// 2. tipo
			if (documento.getTipo() != null && !documento.getTipo().trim().isEmpty()) {
				ps.setString(2, documento.getTipo());
			} else {
				ps.setNull(2, java.sql.Types.VARCHAR);
			}

			// 3. dir_arquivo
			if (documento.getDirArquivo() != null && !documento.getDirArquivo().trim().isEmpty()) {
				ps.setString(3, documento.getDirArquivo());
			} else {
				ps.setNull(3, java.sql.Types.VARCHAR);
			}

			// 4. detalhamento
			if (documento.getDetalhamento() != null && !documento.getDetalhamento().trim().isEmpty()) {
				ps.setString(4, documento.getDetalhamento());
			} else {
				ps.setNull(4, java.sql.Types.VARCHAR);
			}

			int rowsAffected = ps.executeUpdate();
			return rowsAffected == 1;

		} catch (Exception e) {
			String msgErro = "Erro ao inserir BoletimDocumento!";
			logger.error(msgErro + ": " + e.getMessage(), e);
			return false;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão com banco de dados!", e);
			}
		}
	}

	public static boolean inserirListaBoletimDocumento(List<BoletimDocumento> lista)
			throws ConexaoException, SQLException {
		boolean sucessoTotal = true;

		for (BoletimDocumento item : lista) {
			boolean sucesso = inserirBoletimDocumento(item);
			if (!sucesso) {
				sucessoTotal = false;
				logger.warn("Falha ao inserir item de documento: " + item);
			}
		}

		return sucessoTotal;
	}

	public static BoletimDocumento ObterPorId(Integer id) throws ConexaoException, SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuilder sbSQL = new StringBuilder();
			sbSQL.append("SELECT id, id_boletim, tipo, dir_arquivo, detalhamento ");
			sbSQL.append("FROM muralha.boletim_documento WHERE id = ?");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id);

			rs = ps.executeQuery();

			if (rs.next()) {
				BoletimDocumento documento = new BoletimDocumento();
				documento.setId(rs.getInt("id"));
				documento.setIdBoletim(rs.getInt("id_boletim"));
				documento.setTipo(rs.getString("tipo"));
				documento.setDirArquivo(rs.getString("dir_arquivo"));
				documento.setDetalhamento(rs.getString("detalhamento"));
				return documento;
			}

			return null;

		} catch (Exception e) {
			String msgErro = "Erro ao buscar BoletimDocumento por ID!";
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
}
