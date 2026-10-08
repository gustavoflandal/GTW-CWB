<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<table class="tabela_branca" width="100%">
    <tr>
        <td align="center">
	        <table class="tabela_lista" width="500">
	            <tr>
	                <th class="head_tabela" width="10%">Nº Solicitação</th>
                    <th class="head_tabela" width="40%">Data Solicitação</th>
                    <th class="head_tabela" width="30%">Data Imagens</th>
                    <th class="head_tabela" width="20%">Ação</th>
	            </tr>
                <c:set var="contaRegistros" value="0" />
	            <c:forEach var="solicitacao_auditoria" varStatus="linhaInfo" items="${solicitacoes_auditoria}">
	                <tr>
	                    <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                        <td class="${css_td}" align="center">${solicitacao_auditoria.idSolicitacaoAuditoria}</td>
                        <td class="${css_td}" align="center"><fmt:formatDate value="${solicitacao_auditoria.dataGeracao}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                        <td class="${css_td}" align="center"><fmt:formatDate value="${solicitacao_auditoria.dataImagens}" type="date" pattern="dd/MM/yyyy" /></td>
                        <td class="${css_td}" align="center"><a class='link_td' href="/processo/SolicitacaoAuditoriaServlet?id_solicitacao_auditoria=${solicitacao_auditoria.idSolicitacaoAuditoria}">Visualizar</a></td>
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
