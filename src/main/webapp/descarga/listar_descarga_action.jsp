<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>

<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>

<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.descarga.Descarga"%>
<%@page import="com.consilux.model.descarga.beans.DescargaBean"%>

<%@page import="com.consilux.model.MensagemJS"%>
<%
	String sDataIni = request.getParameter("dataini");
	String sDataFim = request.getParameter("datafim");
	
	if (sDataIni == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataIni)) {
	    new Mensagem(response).showErro("Data inicial enviada inválida!");
	    return;
	}
	else if (sDataFim == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataFim)) {
	    new Mensagem(response).showErro("Data final enviada inválida!");
	    return;
	}
	    
	Date dtIni; 
	Date dtFim;
	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	try {
		dtIni = df.parse(sDataIni + " 00:00:00");
		dtFim = df.parse(sDataFim + " 23:59:59");
	} catch (Exception e) {
		throw new ServletException(e);
	}
	
    
    Map<String,Object> mFiltro = new HashMap<String,Object>();
    mFiltro.put("data_ini", new Timestamp(dtIni.getTime()));
    mFiltro.put("data_fim", new Timestamp(dtFim.getTime()));

    List<DescargaBean> descargas = Descarga.buscarDescargaPor(mFiltro);
%>

<c:set var="descargas" value="<%=descargas%>" />
<script type="text/javascript">
	function mostraDetalhes(idDescarga) {
		window.open("/descarga/descarga.jsp?id_descarga="+idDescarga,"Detalhes","width=700, height=330");
	}
</script>
<table class="tabela_branca" width="100%">
    <tr>
        <td align="center">
	        <table class="tabela_lista" width="600">
	            <tr>
                    <th class="head_tabela" width="20%">Código Descarga</th>
                    <th class="head_tabela" width="25%">Data Criação</th>
                    <th class="head_tabela" width="20%">Total Infrações</th>
	            </tr>
                <c:set var="contaRegistros" value="0" />
	            <c:forEach var="descarga" varStatus="linhaInfo" items="${descargas}">
	                <tr>
	                    <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${descarga.id})">${descarga.id}</a></td>
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${descarga.id})"><fmt:formatDate value="${descarga.dataCriacao}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></a></td>
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${descarga.id})">${descarga.totalInfracoes}</a></td>
                        <c:set var="contaRegistros" value="${linhaInfo.count}" />
	                </tr>
	            </c:forEach>
	        </table>
        </td>
    </tr>
</table>
<script type="text/javascript">
	var div_conta_registros = parent.document.getElementById("div_conta_registros");
	if (div_conta_registros)
		div_conta_registros.innerHTML = '${contaRegistros}';
</script>
<%@ include file="/includes/rodape.jsp" %>
