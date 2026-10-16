<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@taglib uri="/WEB-INF/fn.tld" prefix="fn" %>
<%-- Resultado de RelatorioAlteracaoConfigEquip (menu 201). Só exibe; a consulta está no servlet. --%>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="100%">
				<tr>
					<th class="head_tabela" colspan="11">
						Alterações de Configuração de Equipamento &mdash;
						<c:out value="${periodo_ini}" /> a <c:out value="${periodo_fim}" />
					</th>
				</tr>
				<tr>
					<th class="head_tabela">Local</th>
					<th class="head_tabela">Versão</th>
					<th class="head_tabela">Nome</th>
					<th class="head_tabela">Versão do local gravada em</th>
					<th class="head_tabela">Configuração</th>
					<th class="head_tabela">Série</th>
					<th class="head_tabela">Ativo</th>
					<th class="head_tabela">Em operação</th>
					<th class="head_tabela">Vigência</th>
					<th class="head_tabela">Configuração alterada em</th>
					<th class="head_tabela">Alterada por</th>
				</tr>
				<c:forEach var="l" varStatus="linhaInfo" items="${linhas}">
					<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
					<tr>
						<td class="${css_td}" align="center">${l.id_local}</td>
						<td class="${css_td}" align="center">${l.sequencia_local}</td>
						<td class="${css_td}"><c:out value="${l.nome}" /></td>
						<td class="${css_td}" align="center"><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${l.data_atualizacao}" type="both" /></td>
						<td class="${css_td}" align="center">${l.id_configuracao_equipamento}</td>
						<td class="${css_td}" align="center"><c:out value="${l.serie_equipamento}" /></td>
						<td class="${css_td}" align="center">${l.ativo ? 'Sim' : 'Não'}</td>
						<td class="${css_td}" align="center">${l.em_operacao ? 'Sim' : 'Não'}</td>
						<td class="${css_td}" align="center">
							<fmt:formatDate pattern="dd/MM/yyyy" value="${l.data_inicio}" type="date" />
							<c:if test="${not empty l.data_fim}"> a <fmt:formatDate pattern="dd/MM/yyyy" value="${l.data_fim}" type="date" /></c:if>
						</td>
						<td class="${css_td}" align="center"><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${l.data_modificacao}" type="both" /></td>
						<td class="${css_td}" align="center"><c:out value="${l.usuario_modificacao}" /></td>
					</tr>
				</c:forEach>
				<c:if test="${empty linhas}">
					<tr><td class="dado_lista_tabela_claro" colspan="11" align="center">Nenhuma alteração no período.</td></tr>
				</c:if>
			</table>
			<p class="label_campo" align="left">
				${fn:length(linhas)} registro(s).
				<c:if test="${limite_atingido}"> Limite de exibição atingido; reduza o período ou escolha um local.</c:if>
				O banco guarda só a última alteração de cada configuração (data e usuário), sem o valor anterior dos parâmetros.
			</p>
			<button onclick="history.back(); return false;">Voltar</button>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp"%>
