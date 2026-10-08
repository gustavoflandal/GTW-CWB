<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.model.Cadastro"%>
<%@page import="com.consilux.model.CadastroBD"%>
<%@page import="com.consilux.model.InfracaoCompletaLista"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.Processamento.EtapaProcesso"%>
<%@page import="com.consilux.model.ExportaRemessa"%>
<%@page import="com.consilux.model.Remessa"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%
	String sTipoRemessa = request.getParameter("tipo_remessa");
	String sDataIni = request.getParameter("dataini");
	String sHoraIni = request.getParameter("horaini");
	String sDataFim = request.getParameter("datafim");
	String sHoraFim = request.getParameter("horafim");
	
	if (sTipoRemessa != null && sTipoRemessa.length() == 0 ) {
		new Mensagem(response).showErro("Identificador do tipo-remessa enviado inválido!");
		return;
	}
	else if (sDataIni == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataIni)) {
	    new Mensagem(response).showErro("Data inicial enviada inválida!");
	    return;
	}
	else if (sHoraIni == null || !Pattern.matches("([01][0-9]|2[0-3]):([0-5][0-9])",sHoraIni)) {
	    new Mensagem(response).showErro("Hora inicial enviada inválida!");
	    return;
	}
	else if (sDataFim == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataFim)) {
	    new Mensagem(response).showErro("Data final enviada inválida!");
	    return;
	}
	else if (sHoraFim == null || !Pattern.matches("([0-1][0-9]|2[0-3]):([0-5][0-9])",sHoraFim)) {
	    new Mensagem(response).showErro("Hora final enviada inválida!");
	    return;
	}
	    
	Date dtIni; 
	Date dtFim;
	
	try {
		dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataIni+" "+sHoraIni+":00");
		dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataFim+" "+sHoraFim+":59");
	} catch (Exception e) {
		throw new ServletException(e);
	}
	
    
    Map<String,Object> mFiltro = new HashMap<String,Object>();
    if (!"0".equals(sTipoRemessa))
    	mFiltro.put("tipo", sTipoRemessa);
    mFiltro.put("data_ini", new Timestamp(dtIni.getTime()));
    mFiltro.put("data_fim", new Timestamp(dtFim.getTime()));

    List<Remessa> remessas = Remessa.buscarRemessaPor(mFiltro);
    
%>


<c:set var="remessas" value="<%=remessas%>" />
<script type="text/javascript">
	function mostraDetalhes(idRemessa) {
		window.open("/relatorio/RelatorioValidacao?id_remessa="+idRemessa,"Relatório de Validação","width=700, height=160");
	}
</script>
<table class="tabela_branca" width="100%">
    <tr>
        <td align="center">
	        <table class="tabela_lista" width="600">
	            <tr>
	                <th class="head_tabela" width="20%">Nº Movimento</th>
                    <th class="head_tabela" width="20%">Código</th>
                    <th class="head_tabela" width="25%">Data</th>
                    <th class="head_tabela" width="20%">Total Infrações</th>
                    <th class="head_tabela" width="15%">Enquadramento</th>   
	            </tr>
                <c:set var="contaRegistros" value="0" />
	            <c:forEach var="remessa" varStatus="linhaInfo" items="${remessas}">
	                <tr>
	                    <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${remessa.idRemessa})">${remessa.idRemessa}</a></td>
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${remessa.idRemessa})">${remessa.codigoExterno}</a></td>
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${remessa.idRemessa})"><fmt:formatDate value="${remessa.data}" type="date" pattern="dd/MM/yyyy" /></a></td>
                        <td class="${css_td}" align="center">${remessa.totalInfracao}</td>
                        <td class="${css_td}" align="center">${remessa.idEnquadramento}</td>    
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
