<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%
	String sIncluirExpirados = request.getParameter("incluirExpirados");
	
	if (sIncluirExpirados != null && !"1".equals(sIncluirExpirados)) {
	    new Mensagem(response).showErro("Filtro enviado inválido!");
	    return;
	}
	
	Boolean incluirExpirados = false;
	
	if ("1".equals(sIncluirExpirados))
		incluirExpirados = true;
	
    Filtro f = new Filtro();
    List<FiltroBean> filtroBean = f.getTodosFiltros(incluirExpirados);
    
    Date agora = new Date();
    
    Integer id_usuario = ((Usuario)session.getAttribute("[usuario]")).getId();
    
    Boolean desenvolvedor = false;
    
   	Configuracao conf = ConfiguracaoProvider.getInstance();
   	desenvolvedor = Usuario.usuarioPertenceAoGrupo(id_usuario, conf.getIdGrupoDesenvolvedores());
    
%>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>

<%@page import="com.consilux.model.Usuario"%>
<%@page import="com.consilux.model.Processo"%>
<%@page import="com.consilux.model.Filtro"%>

<%@page import="com.consilux.model.beans.UsuarioBean"%>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<%@page import="com.consilux.model.beans.FiltroBean"%>
<%@page import="com.consilux.model.Grupo"%>


<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%><script type="text/javascript" src="/js/calendario.js"></script>

<c:set var="filtroBean" value="<%=filtroBean%>" />
<c:set var="agora" value="<%=agora%>" />
<c:set var="desenvolvedor" value="<%=desenvolvedor%>" />

<c:set var="mostra_detalhes" value="<%=true%>" />
<%@ include file="/ferramenta/visualiza_filtros_atrib.jsp"%>
<%@ include file="/includes/rodape.jsp" %>