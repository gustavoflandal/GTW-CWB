<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="com.consilux.model.InfracoesSemEscala"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" 
	pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	List<InfracoesSemEscala> ise = InfracoesSemEscala.ObterInfracoesSemEscala();
%>

<c:set var="ise" value="<%=ise%>" />
<script type="text/javascript">
	function mostraDetalhes(linha) {
		var local = document.getElementById("local_"+linha).innerHTML;
		var data = document.getElementById("data_"+linha).innerHTML;
		window.open("/infracao/listar_infracao_completa_action.jsp?id_local="+local+"&id_pista=0&data_infracao_ini="+data+"&hora_infracao_ini=00:00&data_infracao_fim="+data+"&hora_infracao_fim=23:59&id_enquadramento=0&id_processo=20","Visualizar Infrações","width=1100, height=700");
	}
// 	function inconsistirImagens() {
// 		var 
// 	}
</script>

<br />
<br />

<form id="frm_movimentos" action="/processo/InconsistirImagensForaEscala" method="get" target="_blank">
<div align="center">

<button name="inconsistir"  onclick="processa();" value="1">Inconsistir imagens Fora de Escala</button>

<br />
<br />

<TABLE>

<THEAD>
<TR>
<TH>Nº</TH>
<TH>Data</TH>
<TH>Local</TH>
<TH>Nº de Imagens</TH>
<TH>Visualizar</TH>
<TH>Selecionar</TH>
</TR>
</THEAD>

<TBODY>

<c:forEach var="item" items="${ise}" varStatus="counter">
<c:set var="css_td" value="${counter.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
<TR>
<TD class="${css_td}" style="text-align: center;">${counter.count}</TD>
<TD class="${css_td}"><label id="data_${counter.count}"><fmt:formatDate value="${item.data}" type="date" pattern="dd/MM/yyyy" /></label></TD>
<TD class="${css_td}"><label id="local_${counter.count}">${item.idLocal}</label> - ${item.descricaoLocal}</TD>
<TD class="${css_td}" style="text-align: center;">${item.numeroImagens}</TD>
<TD class="${css_td}" style="text-align: center;"><a class='link_td' href="javascript:mostraDetalhes(${counter.count})">Visualizar</a></TD>
<TD class="${css_td}" style="text-align: center;"><input type="checkbox" id="sel_${counter.count}" name="sel_imagens" value="${item.data}_${item.idLocal}" /></TD>
<TD>
</TR>
</c:forEach>

</TBODY>

</TABLE>

</DIV>
</form>

<%@ include file="/includes/rodape.jsp" %>