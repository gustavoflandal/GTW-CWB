<%@ include file="/includes/cabecalho.jsp" %>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>

<%
boolean mostrar_todos = request.getParameter("mostrar_todos") != null && request.getParameter("mostrar_todos").equals("1"); 
%>
<c:set var="mostrar_todos" value="<%=mostrar_todos%>" />

<center>

<form>

<table>
<tr><td>Período:</td><td>
<td><input id="txt_data_inicio" type="text" name="data_inicio" maxlength="10" style="width: 80px" onblur="preenchePeriodo();" value="${data_inicio}"></td>
<td><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_inicio'), 'dd/mm/yyyy')"></td>
<td>Até:</td>
<td><input id="txt_data_fim" type="text" name="data_fim" maxlength="10" style="width: 80px" onblur="preenchePeriodo();" value="${data_fim}"></td>
<td><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_fim'), 'dd/mm/yyyy')"></td>
<td><input type="checkbox" name="mostrar_todos" value="1" ${mostrar_todos ? 'checked' : ''}/>Mostrar Todos</td>
<td><button>Filtrar</button></td>
</tr>
</table>

<br/>
<br/>

<table>
<thead><tr><th>Data da Falha</th><th>Data Início</th><th>Data Previsto</th><th>Data Conclusão</th><th width="200px">Descrição</th><th>Estado</th><th>Ação</th></tr></thead>

<tbody>
<tr>
<td>29/06/2016 11:40:00</td><td>30/06/2016</td><td>01/07/2016</td><td>N/D</td><td>Problema com Iluminador</td><td>Aberto</td><td>Visualizar&nbsp;Fechar</td></tr>


</tbody>

</table>

</form>


</center>

<%@ include file="/includes/rodape.jsp" %>