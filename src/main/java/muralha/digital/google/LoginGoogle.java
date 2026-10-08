package muralha.digital.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.consilux.infra.SessaoConstantes;
import com.consilux.infra.SessaoFinalizaManager;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.LogonLogoff;

@WebServlet("/callback")
public class LoginGoogle extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LoginGoogle.class);
	private static final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");
    private static final String REDIRECT_URI = "http://localhost:8080/callback";

	// Helper to get cookie value
	private String getCookieValue(HttpServletRequest request, String name) {
		if (request.getCookies() != null) {
			for (javax.servlet.http.Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(name)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		String sessionState = getCookieValue(request, "oauth_state");

		// Optionally, clear the cookie after use
		javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("oauth_state", "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		if (code == null || state == null || !state.equals(sessionState)) {
			response.sendRedirect("/login/google/login_google.html?error=InvalidState");
			return;
		}

		// Exchange code for access token
		String tokenEndpoint = "https://oauth2.googleapis.com/token";
		String params = "code=" + URLEncoder.encode(code, "UTF-8") +
						"&client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8") +
						"&client_secret=" + URLEncoder.encode(CLIENT_SECRET, "UTF-8") +
						"&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8") +
						"&grant_type=authorization_code";

		URL url = new URL(tokenEndpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		try (OutputStream os = conn.getOutputStream()) {
			os.write(params.getBytes());
		}

		int status = conn.getResponseCode();
		InputStream is = (status == 200) ? conn.getInputStream() : conn.getErrorStream();
		StringBuilder resp = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) resp.append(line);
		}
		if (status != 200) {
			logger.error("Google error: " + resp.toString());
			response.sendRedirect("/login/google/login_google.html?error=GoogleAuthFailed");
			return;
		}
		JSONObject json = new JSONObject(resp.toString());
		String accessToken = json.getString("access_token");

		// Get user info
		URL userInfoUrl = new URL("https://www.googleapis.com/oauth2/v3/userinfo");
		HttpURLConnection userConn = (HttpURLConnection) userInfoUrl.openConnection();
		userConn.setRequestProperty("Authorization", "Bearer " + accessToken);

		StringBuilder userResp = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(userConn.getInputStream()))) 
		{
			String line;
			while ((line = br.readLine()) != null) userResp.append(line);
		}
		JSONObject userJson = new JSONObject(userResp.toString());
		logger.info(userJson);

		// Recupera o destino do fluxo (novo_acesso ou padrão)
		String oauthDestino = getCookieValue(request, "oauth_destino");
		// Limpa o cookie após uso
		javax.servlet.http.Cookie destinoCookie = new javax.servlet.http.Cookie("oauth_destino", "");
		destinoCookie.setMaxAge(0);
		response.addCookie(destinoCookie);

		String federated_id =  userJson.getString("sub");

		// Tenta criar sessão normalmente
		if (!cria_sessao_usuario(request, response, federated_id)) 
		{
			// Se for fluxo de novo acesso, salva dados no localStorage e redireciona
			if ("novo_acesso".equals(oauthDestino)) {
				String nome = userJson.optString("name", "");
				String email = userJson.optString("email", "");
				String foto = userJson.optString("picture", "");
				String googleId = federated_id;

				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write(
					"<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Novo Acesso</title></head><body>" +
					"<script>" +
					"localStorage.setItem('novoUsuarioGoogle', JSON.stringify({" +
						"nome: " + toJsString(nome) + "," +
						"email: " + toJsString(email) + "," +
						"googleId: " + toJsString(googleId) + "," +
						"foto: " + toJsString(foto) +
					"}));" +
					"window.location.href = '/login/google/novo_acesso.html';" +
					"</script>" +
					"</body></html>"
				);
				return;
			} else {
				response.sendRedirect("/login/google/login_google.html?error=UserNotFound");
				return;
			}
		}
		
		response.sendRedirect("/login/abertura-sistemas.jsp");
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    response.setHeader("Cache-control", "no-cache, no-store");
	    response.setHeader("Pragma", "no-cache");
	    response.setHeader("Expires", "-1");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	    response.setHeader("Access-Control-Max-Age", "86400");

	    boolean resultado = false;
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line;
	        try (BufferedReader reader = request.getReader()) {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	        }
	        JSONObject json = new JSONObject(sb.toString());
	        String nome = json.optString("nome", "");
	        String email = json.optString("email", "");
	        String usuario = json.optString("usuario", "");
	        String telefone = json.optString("telefone", "");
	        String googleId = json.optString("googleId", "");

	        Connection conn = null;
	        PreparedStatement ps = null;
	        ResultSet rs = null;
	        try {
	            conn = com.consilux.lib.Conexao.getConexao();
	            String sql = "INSERT INTO sis_usuario (usuario, senha, nome, email, ativo, alterar_senha, telefone, google_id, federated_provider) VALUES (?, '', ?, ?, 1, 1, ?, ?, 'google')";
	            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	            ps.setString(1, usuario);
	            ps.setString(2, nome);
	            ps.setString(3, email);
	            ps.setString(4, telefone);
	            ps.setString(5, googleId);
	            int rows = ps.executeUpdate();
	            if (rows > 0) {
	                rs = ps.getGeneratedKeys();
	                int idUsuario = -1;
	                if (rs.next()) {
	                    idUsuario = rs.getInt(1);
	                }
	                if (idUsuario > 0) {
	                    // Inserir na tabela sis_usuario_grupo
	                    PreparedStatement psGrupo = null;
	                    try {
	                        String sqlGrupo = "INSERT INTO sis_usuario_grupo (id_usuario, id_grupo) VALUES (?, 46)";
	                        psGrupo = conn.prepareStatement(sqlGrupo);
	                        psGrupo.setInt(1, idUsuario);
	                        int rowsGrupo = psGrupo.executeUpdate();
	                        resultado = rowsGrupo > 0;
	                    } finally {
	                        if (psGrupo != null) try { psGrupo.close(); } catch (SQLException e) {}
	                    }
	                }
	            }
	        } catch (Exception ex) {
	            logger.error("Erro ao inserir novo usuário Google ou grupo", ex);
	            resultado = false;
	        } finally {
	            if (rs != null) try { rs.close(); } catch (SQLException e) {}
	            if (ps != null) try { ps.close(); } catch (SQLException e) {}
	            if (conn != null) try { conn.close(); } catch (SQLException e) {}
	        }
	    } catch (Exception e) {
	        logger.error("Erro no doPost de LoginGoogle", e);
	        resultado = false;
	    }
	    JSONObject resp = new JSONObject();
	    resp.put("resultado", resultado);
	    response.getWriter().write(resp.toString());
	}

	private boolean cria_sessao_usuario(HttpServletRequest request, 
									  HttpServletResponse response, 
									  String google_id) 
	{
		try {
		
			com.consilux.model.Usuario usuario;
			
			try {
				usuario = com.consilux.model.Usuario.buscaUsuarioPorGoogleID(google_id);
			} catch (ConexaoException ex) {
				return false;
			}
			
			if ( usuario == null) {
				return false;	    		
			}
			 	
			HttpSession sessaoAntiga = request.getSession();
			SessaoFinalizaManager.removeSessaoFinaliza(sessaoAntiga); 
			sessaoAntiga.invalidate();
			
			HttpSession sessaoNova = request.getSession(true);
			sessaoNova.setAttribute(SessaoConstantes.SESSAO_USUARIO,usuario);
			
			LogonLogoff ll = LogonLogoff.inserirLogonLogoff(usuario.getId(), request.getRemoteAddr(), "0.0.0.0");
			
			SessaoFinalizaManager.adicSessaoFinaliza(sessaoNova, ll);
			return true;
		
		}catch (Exception e) {
			logger.error("Erro ao executar cria_sessao_usuario()" + e.getMessage(), e);
			return false;
		} 
	}

	// Adicionar método auxiliar para escapar strings JS
	private String toJsString(String s) {
		if (s == null) return "''";
		return "'" + s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "") + "'";
	}
}