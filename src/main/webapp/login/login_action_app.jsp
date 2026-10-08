<%@page import="com.consilux.infra.SessaoConstantes"%>
<%@page import="com.consilux.infra.SessaoFinalizaAdapter"%>
<%@page import="com.consilux.infra.SessaoFinalizaManager"%>
<%@page import="com.consilux.infra.exception.ConexaoException"%>
<%@page import="java.sql.Connection"%>
<%@ page import="com.consilux.infra.AESGCMUtil" %>
<%@page import="com.consilux.model.LogonLogoff"%>
<%@page import="com.consilux.lib.Conexao"%>
<%@page import="java.util.List"%>
<%@page import="org.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="java.net.URLDecoder"%>
<%@ page import="javax.crypto.SecretKey" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%
    Conexao conexao = Conexao.initConexao();

	String sIP     = request.getParameter("ip");
	String user = request.getParameter("user"); //username
	String secretKey = request.getParameter("secretKey"); //Texto Criptografado
	String urlService = request.getParameter("urlService"); //Url de redirecionamento
	String nonce = request.getParameter("nonce"); // Nonce
	String tag = request.getParameter("tag"); //Tag    
	
	
	if (user == null || user.trim().isEmpty() || "0".equals(user)) {
		new Mensagem(response).showErroMuralha("Usuário não informado!");
		return;
	}
	
	Usuario usuario;
	int idLogin = Integer.parseInt(user);		
	
	try {
		usuario = Usuario.buscaUsuarioPorIdUsuario(idLogin);
	} catch (ConexaoException ex) {
		new Mensagem(response).showErroMuralha("Não foi possível conectar-se ao banco de dados.");
		return;
	}
		
	if (usuario == null) {
		new Mensagem(response).showErroMuralha("Usuário ou senha incorretos.");
		return;
	}
	
	String md5 = usuario.getSenhaMD5();
	
	System.out.println("secretKey: " + secretKey);
	System.out.println("nonce: " + nonce);
	System.out.println("tag: " + tag);
	System.out.println("md5: " + md5);
	
	String result = AESGCMUtil.decryptAESGCM(secretKey, nonce, tag, md5);
	
	JSONObject json = new JSONObject(result);
    String password = json.getString("password");
		
  	if (!usuario.comparaSenha(password)) {
		new Mensagem(response).showErroMuralha("Usuário ou senha incorretos.");
		return;
	}
	else {
		//Cria a sessão aqui porque esta pagina só deve criar uma sessão depois que a autenticação estiver ok:
		HttpSession sessaoAntiga = request.getSession(false);
		if (sessaoAntiga != null) {
			SessaoFinalizaManager.removeSessaoFinaliza(sessaoAntiga); //Garante que os listeners de finalização da sessão antiga seja executado.
			sessaoAntiga.invalidate();
		}
		
		HttpSession sessaoNova = request.getSession(true);
		sessaoNova.setAttribute(SessaoConstantes.SESSAO_USUARIO, usuario);
		LogonLogoff ll = LogonLogoff.inserirLogonLogoff(usuario.getId(), request.getRemoteAddr(), sIP);
		
		if (ll == null) {
			new Mensagem(response).showErroMuralha("Host não reconhecido!");
			return;
		}
		
		SessaoFinalizaManager.adicSessaoFinaliza(sessaoNova, ll);
		
		%>
			<!DOCTYPE html>
			<html>
			<head><meta charset="UTF-8"></head>
			<body>
				<script type="text/javascript">
					window.location.replace("<%= urlService %>");
				</script>
			</body>
			</html>
		<%
		return; // Importante: não continuar a execução do JSP
	}
%>