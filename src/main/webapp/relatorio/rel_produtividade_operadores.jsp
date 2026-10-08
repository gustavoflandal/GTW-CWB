<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%
	String sIdUsuario = request.getParameter("usuario");
	String sDataIni = request.getParameter("dataini");
	String sDataFim = request.getParameter("datafim");
	
	if (sIdUsuario == null || !ExpValida.NATURAL_COM_ZERO.validar(sIdUsuario)) {
	    new Mensagem(response).showErro("Usuário enviado inválido!");
	    return;
	}
	else if (sDataIni == null || !ExpValida.DATA.validar(sDataIni)) {
	    new Mensagem(response).showErro("Data inicial enviada inválida!");
	    return;
	}
	else if (sDataFim == null ||  !ExpValida.DATA.validar(sDataFim)) {
	    new Mensagem(response).showErro("Data final enviada inválida!");
	    return;
	}
	    
	Date dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataIni+" 00:00:00");
	Date dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataFim+" 23:59:59");
	
	Integer idUsuario;
	if (Integer.valueOf(sIdUsuario) > 0)
		idUsuario = Integer.valueOf(sIdUsuario);
	else
		idUsuario = null;

	List<RelatorioProdutividadeOperadores> lrpo;
	RelatorioProdutividadeOperadores rpo = new RelatorioProdutividadeOperadores();
	lrpo = rpo.buscaRelatorio(dtIni, dtFim, idUsuario);
	
%>
<%@page import="com.consilux.model.Acesso"%>
<%@page import="com.consilux.model.RelatorioProdutividadeOperadores"%>

<%@page import="com.consilux.infra.ExpValida"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%><c:set var="lrpo" value="<%=lrpo%>" />
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
		<table class="tabela_branca" width="500">
			<tr>
				<th class="head_tabela" width="100%" colspan="3">Relatório de Produtividade dos Operadores</th>
			</tr>
				<tr align="center" >
					<th class="head_tabela" align="center" width="15%">Data</th>
					<th class="head_tabela" align="center" width="45%">Usuário</th>
					<th class="head_tabela" align="center" width="20%">Total</th>
				</tr>
				<c:set var="processoAnterior" value="" />
				<c:forEach var="rip" varStatus="linhaInfo" items="${lrpo}">
					<c:if test="${rip.processo != processoAnterior}">
						<tr><td class="head_tabela" align="center" colspan="3" bgcolor="darkgray">${rip.processo}</td></tr>
					</c:if>
				<tr align="center" >
					<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
					<td class="${css_td}" align="center" width="15%"><fmt:formatDate value="${rip.data}" type="date" pattern="dd/MM/yyyy" /></td>
					<td class="${css_td}" align="center" width="45%">${rip.usuario}</td>
					<td class="${css_td}" align="center" width="20%">${rip.total}</td>
				</tr>
				<c:set var="processoAnterior" value="${rip.processo}" />
				</c:forEach>
		</table>
		</td>
	</tr>
</table>

<%@ include file="/includes/rodape.jsp"%>
