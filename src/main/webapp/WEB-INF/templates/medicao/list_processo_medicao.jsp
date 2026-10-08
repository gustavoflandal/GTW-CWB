<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<script type="text/javascript">
	function mostraDetalhes(idProcessoMedicao) {
		window.open("/medicao/processo_medicao.jsp?id_processo_medicao="+idProcessoMedicao,"Detalhes","width=700, height=630");
	}
</script>
<table class="tabela_branca" width="100%">
    <tr>
        <td align="center">
	        <table class="tabela_lista" width="600">
	            <tr>
	                <th class="head_tabela" width="10%">Nº Processo</th>
                    <th class="head_tabela" width="20%">Mes/Ano</th>
                    <th class="head_tabela" width="30%">Data Criação</th>
                    <th class="head_tabela" width="40%">Estágio Atual</th>
	            </tr>
                <c:set var="contaRegistros" value="0" />
	            <c:forEach var="processoMedicao" varStatus="linhaInfo" items="${processos_medicao}">
	                <tr>
	                    <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${processoMedicao.id})">${processoMedicao.id}</a></td>
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${processoMedicao.id})"><fmt:formatNumber value="${processoMedicao.mes}" pattern="00"/>/<fmt:formatNumber value="${processoMedicao.ano}" pattern="0000"/></a></td>
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${processoMedicao.id})"><fmt:formatDate value="${processoMedicao.dataCriacao}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></a></td>
                        <td class="${css_td}" align="center">${processoMedicao.estagioProcesso}</td>
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
