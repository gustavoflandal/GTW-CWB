<%@page import="com.consilux.model.MonitoramentoPMESP"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>

<meta http-equiv="refresh" content="60" >

<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	List<MonitoramentoPMESP> lista = MonitoramentoPMESP.ObterMonitoramento();
%>
<c:set var="lista" value="<%=lista%>" />

<table class="tabela_branca" width="100%">
 <thead>
  <tr>
<th class="head_tabela">Nº</th>
<th class="head_tabela">Local</th>
<th class="head_tabela">Data Conexão</th>
<!-- <th class="head_tabela">Desconexões (24 H)</th> -->
<th class="head_tabela" colspan="3">Desconexões (24 H)</th>
<th class="head_tabela">Estado</th>
<th class="head_tabela">Endereço IP</th>
<th class="head_tabela">Recebidos</th>
<!-- <th class="head_tabela">Transmitidos</th> -->
<th class="head_tabela" colspan="2">Média/Maior Atraso (24 H)</th>
<th class="head_tabela" colspan="2">Placas Lidas/Perda (24 H)</th>
<th class="head_tabela">Último Recebido</th>
<th class="head_tabela">Data Atualização</th>
</tr>
</thead>

<c:forEach var="local" varStatus="linhaInfo" items="<%=lista%>">
<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />

<tr>
<td class="${css_td}" align="center">${linhaInfo.count}</td>
<td class="${css_td}" align="center"><abbr title="${local.idLocal} - ${local.nome}">${local.idLocal}</abbr></td>
<td class="${css_td}" align="center"><fmt:formatDate value="${local.dataConexao}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
<td class="${css_td}" align="center">${local.desconexoes}</td>
<td class="${css_td}" align="right">${local.tempoOffline}</td>
<td class="${css_td}" align="left">(${local.tempoOfflinePorc} %)</td>
<c:if test="${local.dataDesconexao == null}">
<td class="${css_td}" style="color: green;" align="center">${local.estado}</td>
</c:if>
<c:if test="${local.dataDesconexao != null}">
<td class="${css_td}" style="color: red;" align="center">${local.estado}</td>
</c:if>
<td class="${css_td}" align="center">${local.enderecoIp}</td>
<td class="${css_td}" align="center">${local.movimentosRecebidos}</td>
<%-- <td class="${css_td}" align="center">${local.movimentosTransmitidos}</td> --%>
<td class="${css_td}" align="center">${local.atraso}</td>
<td class="${css_td}" align="center">${local.maximo}</td>
<td class="${css_td}" align="center">${local.placaLida}</td>
<td class="${css_td}" align="center">${local.perda} (${local.perdaPorc} %)</td>
<td class="${css_td}" align="center"><fmt:formatDate value="${local.dataUltimoMovimento}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
<td class="${css_td}" align="center"><fmt:formatDate value="${local.dataAtualizado}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
</tr>

</c:forEach>

</table>

<%@ include file="/includes/rodape.jsp" %>