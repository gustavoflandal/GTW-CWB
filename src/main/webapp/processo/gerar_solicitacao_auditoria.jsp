<%@page import="com.consilux.model.relatorio.RelatorioInfracoesAuditoria"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
	List<RelatorioInfracoesAuditoria> lia = RelatorioInfracoesAuditoria.buscaRelatorio();
%>
<c:set var="lia" value="<%=lia%>" />
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_solicitacao_auditoria" action="/processo/GerarSolicitacaoAuditoria" method="get" target="_blank">
				<table class="tabela_branca" width="300">
					<tr>
						<th class="head_tabela" width="100%" colspan="2">Solicitação de Auditoria</th>
					</tr>
					<tr>
	                    <td class="label_campo" width="40%">Data:</td>
	                    <td class="valor_campo" width="60%">
                            <input id="txt_data_imagens" type="text" name="data_imagens" maxlength="10" style="width: 80px" ">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_imagens'), 'dd/mm/yyyy')">
                        </td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="2">
							<button onclick="document.getElementById('frm_solicitacao_auditoria').submit()">Gerar</button><br>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="2">(necessário Acrobat Reader)</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>


<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
		<table class="tabela_branca" width="800">
			<tr>
				<th class="head_tabela" width="100%" colspan="8">Imagens Disponíveis para Auditoria</th>
			</tr>
				<tr align="center" >
					<th class="head_tabela" align="center" width="70%" colspan="2">Enquadramento</th>
					<th class="head_tabela" align="center" colspan="3" width="15%">Disponível</th>
					<th class="head_tabela" align="center" colspan="3" width="15%">Necessita solicitação</th>

				</tr>
				<tr align="center" >
					<th class="head_tabela" align="center" width="10%">Código</th>
					<th class="head_tabela" align="center" width="60%">Descricao</th>
					<th class="head_tabela" align="center" width="5%">Inc.</th>
					<th class="head_tabela" align="center" width="5%">Cons.</th>
					<th class="head_tabela" align="center" width="5%">Total</th>
					<th class="head_tabela" align="center" width="5%">Inc.</th>
					<th class="head_tabela" align="center" width="5%">Cons.</th>
					<th class="head_tabela" align="center" width="5%">Total</th>
				</tr>
				<c:set var="dataImagem" value="" />
				<c:forEach var="rsa" varStatus="linhaInfo" items="${lia}">
					<c:if test="${rsa.dataImagem != dataImagem}">
						<c:if test="${dataImagem != ''}">
							<tr align="center" >
								<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center" colspan="2">TOTAL</td>
								<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalInconsistenteDisponivel}</td>
								<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalConsistenteDisponivel}</td>
								<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalDisponivel}</td>
								<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalInconsistenteNecessita}</td>
								<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalConsistenteNecessita}</td>
								<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalNecessita}</td>
							</tr>
							<c:set var="totalInconsistenteDisponivel" value="0" />
							<c:set var="totalConsistenteDisponivel" value="0" />
							<c:set var="totalDisponivel" value="0" />
							<c:set var="totalInconsistenteNecessita" value="0" />
							<c:set var="totalConsistenteNecessita" value="0" />
							<c:set var="totalNecessita" value="0" />
						</c:if>
						<tr><td class="head_tabela" align="center" colspan="8" bgcolor="darkgray"><fmt:formatDate value="${rsa.dataImagem}" type="date" pattern="dd/MM/yyyy" /></td></tr>
					</c:if>
					
					<tr align="center" >
						<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						<td class="${css_td}" align="center">${rsa.idEnquadramento}</td>
						<td class="${css_td}" align="left">${rsa.descricaEnquadramento}</td>
						<td class="${css_td}" align="center">${rsa.totalInconsistenteDisponivel}</td>
						<td class="${css_td}" align="center">${rsa.totalConsistenteDisponivel}</td>
						<td class="${css_td}" style="font-weight: bold;" align="center">${rsa.totalImagensDisponivel}</td>
						<td class="${css_td}" align="center">${rsa.totalInconsistenteDisponivel - rsa.totalInconsistenteJaSolicitado}</td>
						<td class="${css_td}" align="center">${rsa.totalConsistenteDisponivel - rsa.totalConsistenteJaSolicitado}</td>
						<td class="${css_td}" style="font-weight: bold;" align="center">${rsa.totalImagensDisponivel - rsa.totalImagensJaSolicitado}</td>
					</tr>
					<c:set var="totalInconsistenteDisponivel" value="${totalInconsistenteDisponivel + rsa.totalInconsistenteDisponivel}" />
					<c:set var="totalConsistenteDisponivel" value="${totalConsistenteDisponivel + rsa.totalConsistenteDisponivel}" />
					<c:set var="totalDisponivel" value="${totalDisponivel + rsa.totalImagensDisponivel}" />
					<c:set var="totalInconsistenteNecessita" value="${totalInconsistenteNecessita + rsa.totalInconsistenteDisponivel - rsa.totalInconsistenteJaSolicitado}" />
					<c:set var="totalConsistenteNecessita" value="${totalConsistenteNecessita + rsa.totalConsistenteDisponivel - rsa.totalConsistenteJaSolicitado}" />
					<c:set var="totalNecessita" value="${totalNecessita + rsa.totalImagensDisponivel - rsa.totalImagensJaSolicitado}" />
					<c:set var="dataImagem" value="${rsa.dataImagem}" />
				</c:forEach>
				<tr align="center" >
					<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center" colspan="2">TOTAL</td>
					<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalInconsistenteDisponivel}</td>
					<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalConsistenteDisponivel}</td>
					<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalDisponivel}</td>
					<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalInconsistenteNecessita}</td>
					<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalConsistenteNecessita}</td>
					<td class="dado_lista_tabela_escuro" style="font-weight: bold;" align="center">${totalNecessita}</td>
				</tr>
		</table>
		</td>
	</tr>
</table>

<%@ include file="/includes/rodape.jsp" %>
