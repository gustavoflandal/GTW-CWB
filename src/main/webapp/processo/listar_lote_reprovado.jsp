<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.consilux.model.LoteReprovado"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	String sNumReg = request.getParameter("num_reg") != null ? request.getParameter("num_reg").trim() : null;
	sNumReg = sNumReg != null && sNumReg.length() == 0 ? null : sNumReg;
	Integer numReg = sNumReg != null ? Integer.valueOf(sNumReg) : 10;
	
	HashMap<Integer, LoteReprovado> lotes_ht = LoteReprovado.obterLoteReprovado(numReg);
%>
<script type="text/javascript">
	function selTudo(cbTudo) {
		var aChecks = document.getElementsByName("sel_movimento");
		for (i=0;i<aChecks.length;i++) {
			aChecks[i].checked = (aChecks[i].checked ? false : true);
		}
	}
	
	function mostraDetalhes(idRemessa) {
		window.open("/processo/listar_lote_reprovado_detalhe.jsp?id_remessa="+idRemessa,"Detalhes","width=700, height=160");
	}
	
	window.onload = function(){
		var aChecks = document.getElementsByName("sel_movimento");
		for (i=0;i<aChecks.length;i++) {
			aChecks[i].checked = false;
		}
	};
</script>

<c:set var="num_reg" value="<%=numReg%>" />
<c:set var="lotes" value="<%=lotes_ht.values()%>" />
<c:set var="total_lotes" value="<%=lotes_ht.size()%>" />


<script type="text/javascript">
	function processa() {
		document.getElementById('frm_movimentos').submit();
	}
</script>

<table class="tabela_branca" width="100%">
	<tr></tr>
	<tr></tr>
	<tr>
		<th class="head_tabela">Reposicionamento de Lotes Reprovados</th>
	</tr>
</table>

<br />

<div class="valor_campo" style="text-align: center;">

<form id="frm_list" action="" method="get">

	<label>Número de registros inicial:</label>
	
	<select id="sel_num_reg" name="num_reg" style="width: 100px" onchange="submit();">
<%-- 		<c:if test="${total_lotes < 10}"><option value="${total_lotes}" ${num_reg == total_lotes ? 'selected' : ''}>${total_lotes}</option></c:if> --%>
	   	<option value="10" ${num_reg == 10 ? 'selected' : ''}>10</option>
	   	<option value="20" ${num_reg == 20 ? 'selected' : ''}>20</option>
	   	<option value="30" ${num_reg == 30 ? 'selected' : ''}>30</option>
	   	<option value="40" ${num_reg == 40 ? 'selected' : ''}>40</option>
	   	<option value="50" ${num_reg == 50 ? 'selected' : ''}>50</option>
	   	<option value="0" ${num_reg == 0 ? 'selected' : ''}>TODOS</option>
	</select>

	<br />
	
</form>

</div>

<br />

<form id="frm_movimentos" action="/processo/ReposicionarLoteReprovado" method="get" target="_blank">
<div align="center">

<c:if test="${total_lotes > 0}">
	<button name="cbTudo" onclick="selTudo(this); return false;">
		<c:choose>
		    <c:when test="${total_lotes < num_reg}">
		    	<c:choose>
		    		<c:when test="${total_lotes == 1}"> Selecionar lote </c:when>
		    		<c:otherwise> Selecionar primeiros ${total_lotes} lotes </c:otherwise>
		    	</c:choose>
		    </c:when>
		    <c:when test="${num_reg > 0}"> Selecionar primeiros ${num_reg} lotes </c:when>
		    <c:otherwise> Selecionar todos os lotes </c:otherwise>
		</c:choose>
	</button>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

	<button name="mov_reposicionar" onclick="processa();" value="1">Reposicionar Lotes</button>
</c:if>

<br />
<br />

<table class="tabela_lista">
<THEAD>
<TR>
<TH>Nº</TH>
<TH>Id. Remessa</TH>
<TH>Tipo</TH>
<TH>Código Externo</TH>
<TH>Data de Infração mais Antiga</TH>
<TH>Data Processo</TH>
<TH>Data Atualização</TH>
<TH>Mensagem</TH>
<TH>Revisão</TH>
<TH>Atraso</TH>
<TH>Atraso Processo</TH>
<TH>Total Infrações</TH>
<TH>Selecionar</TH>
<TH>Detalhes</TH>
</TR>
</THEAD>

<c:forEach var="lr" items="${lotes}" varStatus="counter">
<c:set var="css_td" value="${counter.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
<TR>
<TD class="${css_td}" style="text-align: center;">${counter.count}</TD>
<TD class="${css_td}" style="text-align: center;">${lr.idRemessa}</TD>
<TD class="${css_td}" style="text-align: center;">${lr.tipo}</TD>
<TD class="${css_td}" style="text-align: center;">${lr.codigoExterno}</TD>
<TD class="${css_td}" style="text-align: center;"><fmt:formatDate value="${lr.dataInicial}" type="date" pattern="dd/MM/yyyy" /></TD>
<TD class="${css_td}" style="text-align: center;"><fmt:formatDate value="${lr.dataProcesso}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></TD>
<TD class="${css_td}" style="text-align: center;"><fmt:formatDate value="${lr.dataAtualizacao}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></TD>
<TD class="${css_td}" style="text-align: center;">${lr.mensagem}</TD>
<TD class="${css_td}" style="text-align: center;">${lr.revisao}</TD>
<TD class="${css_td}" style="text-align: center;">${lr.atraso}</TD>
<TD class="${css_td}" style="text-align: center;">${lr.atrasoProcesso}</TD>
<TD class="${css_td}" style="text-align: center;">${lr.totalInfracao}</TD>
<TD class="${css_td}" style="text-align: center;"><c:if test="${lr.reposicionar}"><input type="checkbox" id="movimento_${lr.idRemessa}" name="sel_movimento" value="${lr.idRemessa}" /></c:if></TD>
<TD class="${css_td}" style="text-align: center;"><c:if test="${lr.detalhes}"><a class='link_td' href="javascript:mostraDetalhes(${lr.idRemessa})">Exibir</a></c:if></TD>
</TR>

</c:forEach>

</table>

<br />

</div>
</form> 

<%@include file="/includes/rodape.jsp" %>