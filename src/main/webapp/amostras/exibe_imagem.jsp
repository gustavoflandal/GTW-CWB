<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
%>
<!-- Rodapé inicia o html e também o body -->    
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@page import="com.google.gwt.core.client.GWT"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="java.util.Date"%><html>
<%
	String tmpString = request.getParameter("data_ini");
	String sDataIni =  tmpString != null ? tmpString.trim() : null;
	tmpString = request.getParameter("data_fim");
	String sDataFim = tmpString != null ? tmpString.trim() : null;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Long longIni = Long.parseLong(sDataIni);
	Long longFim = Long.parseLong(sDataFim);
	
	Integer cursorAmostra = 0;
	tmpString = request.getParameter("cursor_amostra");
	if (tmpString != null && tmpString.length() > 0 &&  Pattern.matches("\\d*", tmpString))
		cursorAmostra = Integer.parseInt(tmpString.trim());
	
	//////////////////////////////////////////////////////////////
	// Novos parâmetros, que estão vindo na request do JSP
	tmpString = request.getParameter("dia");
	String sDia = tmpString != null ? tmpString.trim() : null;
	Long longDia = Long.parseLong(sDia);
	
	Integer idLocal = 0;
	tmpString = request.getParameter("id_local");
	if (tmpString != null && tmpString.length() > 0 &&  Pattern.matches("\\d*", tmpString))
		idLocal = Integer.parseInt(tmpString.trim());

	Integer idPista = 0;
	tmpString = request.getParameter("id_pista");
	if (tmpString != null && tmpString.length() > 0 &&  Pattern.matches("\\d*", tmpString))
		idPista = Integer.parseInt(tmpString.trim());

	Integer metrologica = 0;
	tmpString = request.getParameter("metrologica");
	if (tmpString != null && tmpString.length() > 0 &&  Pattern.matches("\\d*", tmpString))
		metrologica = Integer.parseInt(tmpString.trim());
	
	Integer pontosAte = null;
	tmpString = request.getParameter("pontos_ate");
	if (tmpString != null && tmpString.length() > 0 &&  Pattern.matches("\\d*", tmpString))
		pontosAte = Integer.parseInt(tmpString.trim());
%>

	<c:set var="dataIni" value="<%=longIni%>" />
	<c:set var="dataFim" value="<%=longFim%>" />
	<c:set var="cursorAmostra" value="<%=cursorAmostra%>" />
	<c:set var="dia" value="<%=longDia%>" />
	<c:set var="idLocal" value="<%=idLocal%>" />
	<c:set var="idPista" value="<%=idPista%>" />
	<c:set var="metrologica" value="<%=metrologica%>" />
	<c:set var="pontosAte" value="<%=pontosAte%>" />
	
	 <script type="text/javascript">
		var data_ini = ${dataIni};
		var data_fim = ${dataFim};
		var cursor_amostra = ${cursorAmostra};
		var dia = ${dia};
		var id_local = ${idLocal};
		var id_pista = ${idPista};
		var metrologica = ${metrologica};
		<c:if test="${pontosAte != null}" >var pontos_ate = ${pontosAte};</c:if> 
	 </script>
 
	 <meta name="gwt:property" content="locale=pt_BR">
	 <link rel="stylesheet" href="/css/gtw-gwt.css" media="screen" type="text/css">
     <script type="text/javascript" language="javascript" src="/GtwWidgets/GtwWidgets.nocache.js"></script>
     
 	<div id="EXIBE_IMAGEM_AMOSTRA"></div>
 	
<%-- Rodapé fecha o body e também o html --%> 	
<%@ include file="/includes/rodape.jsp" %>