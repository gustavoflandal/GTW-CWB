<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>
<%
	String sSemCabecalho = request.getParameter("sc");
	if (sSemCabecalho != null && "true".equals(sSemCabecalho)) {
%>
	<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%
	} else {
%>
	<%@ include file="/includes/cabecalho_sem_acesso.jsp" %>
<%
	}
	String sMensagem = request.getParameter("m");
	String sUrlVolta = request.getParameter("p");
%>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="400">
				<tr>
					<th class="head_tabela" width="100%">A operação não pode ser concluída.</th>
				</tr>
				<tr>
					<td class="corpo_mensagem" width="100%"><%=sMensagem%></td>
				</tr>
				<tr>
					<td class="box_botoes" width="100%">
						<button id="btVoltar" onclick="document.location.href = '<%=sUrlVolta%>';">Voltar</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
	document.getElementById("btVoltar").focus();
</script>
<%@ include file="/includes/rodape.jsp" %>
