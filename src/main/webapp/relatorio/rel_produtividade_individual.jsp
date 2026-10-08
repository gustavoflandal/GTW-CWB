<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%
	
	Usuario usu = (Usuario) request.getSession().getAttribute("[usuario]");	

	if (!new Acesso(request, response, true).verificaAcesso(false)){
		RequestDispatcher rd = request.getRequestDispatcher("/login/login.jsp");
		rd.forward(request, response);	
	}

	List<RelatorioProdutividadeOperadores> lrpo;
	RelatorioProdutividadeOperadores rpo = new RelatorioProdutividadeOperadores();
	lrpo = rpo.buscaRelatorio(usu.getId());
	
%>
<%@page import="com.consilux.model.Acesso"%>
<%@page import="com.consilux.model.RelatorioProdutividadeOperadores"%>
<c:set var="lrpo" value="<%=lrpo%>" />
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
