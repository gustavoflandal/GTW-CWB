<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<br/>
<br/>
<br/>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_vencimento_afericoes" action="/relatorio/RelatorioVencimentoAfericoes" method="get" target="_blank">
				<table class="tabela_branca" width="300">
					<tr>
						<th class="head_tabela" width="100%" colspan="2">Relatório de Vencimento de Aferições</th>
					</tr>
					<tr>
						<td class="box_botoes" colspan="2">
							<button onclick="document.getElementById('frm_vencimento_afericoes').submit()">Visualizar</button><br>
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
<%@ include file="/includes/rodape.jsp" %>