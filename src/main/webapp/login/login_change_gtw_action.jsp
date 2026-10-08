
<%@page import="com.consilux.model.Evento"%>
<%@page import="com.consilux.model.EventoCSX.TipoEvento"%>
<%@page import="com.consilux.model.EventoCSX"%>
<%@page import="com.consilux.model.beans.UsuarioBean"%><%@page import="java.sql.Connection"%>
<%@page import="com.consilux.model.LogonLogoff"%>
<%@page import="com.consilux.lib.Conexao"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="java.net.URLDecoder"%>
<%
    Conexao conexao = Conexao.initConexao();

	String sSenhaAnt = request.getParameter("senha_ant");
	String sSenhaNova = request.getParameter("senha_nova");
	String sSenhaNovaConfirma = request.getParameter("senha_confirma");
	Usuario usu = (Usuario)request.getSession().getAttribute("[usuario]");
	
	if (sSenhaAnt == null || !Pattern.matches("\\p{Print}{1,10}",sSenhaAnt.toUpperCase())) {
		new Mensagem(response).showErro("Senha enviada invalida!");
		return;
	}
	else if (sSenhaNova == null || !Pattern.matches("\\p{Print}{1,10}",sSenhaNova.toUpperCase())) {
		new Mensagem(response).showErro("Senha enviada invalida!");
		return;
	}
	else if (!sSenhaNova.equals(sSenhaNovaConfirma)){
		new Mensagem(response).showErro("Senha não é igual a confirmação!");
		return;
	}
	
	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("usuario",usu.getUsuario());
	mFiltro.put("ativo",1);
	List<Usuario> usus = Usuario.buscaUsuarioPor(mFiltro);
	
	if (usus.size() == 0) {
		new Mensagem(response).showErro("Usuário ou senha antiga incorreta.");
		return;
	}
	
	Usuario usuario = usus.get(0);
	if (!usuario.comparaSenha(sSenhaAnt)) {
		new Mensagem(response).showErro("Usuário ou senha antiga incorreta");
		return;
	}
	else {
		UsuarioBean usuarioBean = new UsuarioBean();
		usuario.getToUsuarioBean(usuarioBean);
		
		usuarioBean.setSenha(sSenhaNova);
		usuarioBean.setAlterarSenha(false);
		
		usuario.setFromUsuarioBean(usuarioBean);
		usuario.alteraUsuario();
		
		// Armazena um evento desta operação.

		EventoCSX eventoCSX = new EventoCSX(EventoCSX.TipoEvento.UPDATE_USER,
				usu.getUsuario(), usu.getUsuario(), "");
		
		Evento.incluirEventoCSX(eventoCSX);		
		
	}
	
	new Mensagem(response).showSucesso("Senha alterada com sucesso!");
%>
