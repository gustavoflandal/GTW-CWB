<%@page import="java.util.regex.Pattern"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="com.consilux.model.RecursosC006_C008"%>


<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@include file="/includes/cabecalho_vazio.jsp"%>
<%@include file="/includes/rodape.jsp"%>

<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="/js/tableToExcel.js"></script>

<%
	String sTipoRemessa = request.getParameter("tipoRemessa");
	String sSerie = request.getParameter("serie");
	String sAutos = request.getParameter("autos");
	String sPlaca = request.getParameter("placa");
	
	if (sTipoRemessa != null && sTipoRemessa.trim() == "") {
		new MensagemJS(response).showErro("Selecione o Tipo da Remessa!");
		return;
	}
	
	if (sSerie != null && sSerie.trim() == "") {
		new MensagemJS(response).showErro("Informe a Série!");
		return;
	}

	if ( (sAutos != null && sAutos.trim() == "") && (sPlaca != null && sPlaca.trim() == "") ) {
		new MensagemJS(response).showErro("Pesquisa muito abrangente. Informe o(s) número(s) do(s) auto(s) ou a placa do veículo!");
		return;
	}
	
	if (sAutos != null && sAutos.trim() != "") {
		if (!Pattern.matches("([1-9][0-9]{0,8})+(,([1-9][0-9]{0,8})+)*",sAutos)) {
			new MensagemJS(response).showErro("Identificadores do auto enviado invalido!");
			return;
		}
	}
	
	
	ArrayList listRecurso = new ArrayList();
	RecursosC006_C008 recurso = new RecursosC006_C008(); 
    listRecurso = recurso.obterRecurso(sTipoRemessa, sSerie, sAutos, sPlaca);    
%>

<c:set var="recursos" value="<%=listRecurso%>" />

<!-- <div id="dvBtnExportar"> -->
<!-- 	<table class="tabela_branca" width="100%"> -->
<!-- 		<tr> -->
<!-- 			<td align="center"> -->
<!-- 				<input type="button" id="btnExport" onclick="tableToExcel('dvData', 'Recursos')" value="Exportar para Excel" /> -->
<!-- 			</td> -->
<!-- 		</tr> -->
<!-- 	</table> -->
<!-- </div> -->

<div id="dvData">
	<table class="tabela_branca" width="100%">
		<tr>
			<td align="center">
				<table class="tabela_lista" width="100%">
					<tr>
						<th class="head_tabela" width="20%">Contrato</th>
						<th class="head_tabela" width="35%">Numero AIT</th>
						<th class="head_tabela" width="25%">Placa</th>
						<th class="head_tabela" width="20%">Descarga</th>
	
					</tr>
					<c:set var="contaRegistros" value="0" />
					<c:forEach var="recurso" varStatus="linhaInfo" items="${recursos}">
						<tr>
							<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
							
							<td class="${css_td}" align="center">${recurso.contrato}</td>
							<td class="${css_td}" align="center">${recurso.numeroAit}</td>
							<td class="${css_td}" align="center">${recurso.placa}</td>
							<td class="${css_td}" align="center">${recurso.idDescarga}</td>
									   						   
							<c:set var="contaRegistros" value="${linhaInfo.count}" />
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	</table>
</div>
