<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
	String sMensagem = request.getParameter("m");
	String sUrlSim = request.getParameter("psim");
	String sUrlNao = request.getParameter("pnao");
%>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="400">
				<tr>
					<th class="head_tabela" width="100%" colspan="2">Confirmação.</th>
				</tr>
				<tr>
					<td class="corpo_mensagem" width="100%" colspan="2"><%=sMensagem%></td>
				</tr>
				<tr>
					<td class="box_botoes" width="50%" style="text-align: right;">
						<button id="btCancelar" onclick="document.location.href = '<%=sUrlNao%>';">N&atilde;o</button>
					</td>
					<td class="box_botoes" width="50" style="text-align: left;">
						<button id="btOk" onclick="document.location.href = '<%=sUrlSim%>';">Sim</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
	document.getElementById("btCancelar").focus();
</script>
<%@ include file="/includes/rodape.jsp" %>
