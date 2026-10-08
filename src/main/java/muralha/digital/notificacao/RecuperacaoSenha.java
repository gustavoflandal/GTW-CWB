package muralha.digital.notificacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import muralha.digital.acessos.Usuario;

public class RecuperacaoSenha {

	private static final Logger logger = Logger.getLogger(RecuperacaoSenha.class);

	public static Usuario buscarUsuarioPorApelido(String nomeUsuario) {
		Usuario usuario = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			sql.append("  CASE WHEN email IS NOT NULL THEN 's' ELSE 'n' END AS possui_email, ");
			sql.append("  CASE WHEN telefone IS NOT NULL THEN 's' ELSE 'n' END AS possui_telefone, ");
			sql.append("  email, ");
			sql.append("  telefone, ");
	        sql.append("  id_usuario ");
			sql.append("FROM sis_usuario ");
			sql.append("WHERE usuario = ?");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, nomeUsuario);
			rs = ps.executeQuery();

			if (rs.next()) {
				usuario = new Usuario(
						0,
						nomeUsuario,
						null,
						null,
						true,
						"");

				usuario.setIdUsuario(rs.getInt("id_usuario"));
				usuario.setTelefone(rs.getString("telefone"));
				usuario.setEmail(rs.getString("email"));
				usuario.setPossuiEmail("s".equalsIgnoreCase(rs.getString("possui_email")));
				usuario.setPossuiTelefone("s".equalsIgnoreCase(rs.getString("possui_telefone")));
			}

			logger.info("Usuário encontrado: nome " + nomeUsuario);
		} catch (Exception e) {
			logger.error("Erro ao buscar usuário simples por nome: " + nomeUsuario, e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos (buscarUsuarioPorNome): " + e.getMessage(), e);
			}
		}

		return usuario;
	}

	public static int validarToken(String token) {
		int idUsuario = -1;

		String sql = "SELECT id_usuario FROM sis_usuario_recupera_senha WHERE token = ? AND ativo = 1";

		try (Connection conn = Conexao.getConexao();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, token);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					idUsuario = rs.getInt("id_usuario");

					String update = "UPDATE sis_usuario_recupera_senha SET ativo = 0, data_utilizacao = GETDATE() WHERE token = ?";
					try (PreparedStatement ps2 = conn.prepareStatement(update)) {
						ps2.setString(1, token);
						ps2.executeUpdate();
					}
				}
			}

		} catch (Exception e) {
			logger.error("Erro ao validar token de recuperação de senha", e);
		}

		return idUsuario;
	}

	public String criarTokenRecuperacaoSenha(String idUsuario) {
		String token = UUID.randomUUID().toString();

		String sql = "INSERT INTO sis_usuario_recupera_senha (id_usuario, token, ativo, data_criacao, data_utilizacao) "
				+
				"VALUES (?, ?, 1, GETDATE(), NULL)";

		try (Connection conn = Conexao.getConexao();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, idUsuario);
			ps.setString(2, token);
			ps.executeUpdate();

		} catch (Exception e) {
			logger.error("Erro ao criar token de recuperação de senha", e);
			return null;
		}

		return token;
	}

}