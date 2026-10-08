
<%@page import="com.consilux.model.ReposicionamentoInfracao"%>
<%@page import="com.consilux.model.InfracaoSimplificada"%><%@page import="com.consilux.model.Mensagem"%><%@page import="com.consilux.model.Inconsistencia"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@page import="com.consilux.model.Processo"%>

<script type="text/javascript" src="/js/funcoes.js"></script>

<%

	ReposicionamentoInfracao reposicionamento = (ReposicionamentoInfracao) request.getSession().getAttribute("[reposicionamento]");

	if( reposicionamento == null){
		new Mensagem(response).showErro("Reposionamento não iniciado!");
		return;
	}

	List<InfracaoSimplificada> infracoes = reposicionamento.getInfracoesSelecionadas(); 
	List<Processo> processos = Processo.buscaTodosProcessos();

%>

<c:set var="infracoes" value="<%=infracoes%>" />
<c:set var="processos" value="<%=processos%>" />

<br/>

<table class="tabela_branca" width="100%">
	<tr>
		<form id="" action="/processo/ReposicionarInfracoes" method="get">
			<td align="center">
				<table class="tabela_lista" width="950px">
				    <tr>
				        <td class="head_tabela" colspan="2">Reposicionamento das Infrações</td>
				    </tr>
				    <tr> 
						<td class="dado_lista_tabela_claro" align="left" width="50%"> 
							<b>Mover Infrações para:</b>
							<select id="sel_processo" name="id_processo" style="width: 250px" >
				                <c:forEach var="processo" items="${processos}">
									<c:set var="selecionado" value="${id_processo == processo.idProcesso ? 'selected' : ''}" />
				                    	<option value="${processo.idProcesso}" ${selecionado}>${processo.nome}</option>
				             	</c:forEach>
		                     </select>
						</td>
						<td align="right" width="50%">
							<button id="btConfirma" onclick="enviar('btConfirma','frm_confirma_reposicionamento','div_mens');">Confirmar</button>
				        	<button onclick="window.location = '/processo/FinalizarReposicionamento'; return false">Cancelar</button>
							&nbsp;
				        	<button onclick="back(); return false">Voltar</button>
						</td>
				    </tr>
				</table>
			</td>
		    <tr>
				<td class="corpo_mensagem" align="left"><div id="div_mens"></div></td>
		    </tr>
		</form>
	</tr>
    <tr>
    	<td>
    	&nbsp 	<!-- deixar espaço entre o título e a lista de infrações-->
    	</td>
    </tr>
	<tr>
		<td align="center">
			<table class="tabela_lista" width="950">
				<tr>
					<th class="head_tabela" width="10%">Cód. Local</th>
					<th class="head_tabela" width="40%">Local</th>
					<th class="head_tabela" width="15%">Data Infração</th>
					<th class="head_tabela" width="10%">Cód. Enquadramento</th>
					<th class="head_tabela" width="10%">Cód. Infração</th>
				</tr>
				<c:forEach var="infracao" varStatus="linhaInfo" items="${infracoes}">
					<tr>
						<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						
						<td class="${css_td}" align="center">${infracao.idLocal}</td>
						<td class="${css_td}" align="left">${infracao.local}</td>
						<td class="${css_td}" align="center"><fmt:formatDate value="${infracao.data}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td class="${css_td}" align="center">${infracao.idEnquadramento}</td>
						<td class="${css_td}" align="center">${infracao.id}</td>
					</tr><!--
		                  <c:set var="idInfracaoAnterior" value="${infracao.id}" />-->
				</c:forEach>
			</table>
		</td>
	</tr>
</table>

		


<%@ include file="/includes/rodape.jsp" %>