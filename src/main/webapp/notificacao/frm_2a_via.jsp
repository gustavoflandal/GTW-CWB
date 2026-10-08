<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_2a_via" action="/notificacao/NotificacaoPDF" method="get" target="_blank">
				<table class="tabela_branca" width="250">
					<tr>
						<th class="head_tabela" width="100%" colspan="5">Emissão de Segunda Via</th>
					</tr>
					<tr>
						<td class="label_campo" width="50%">Auto de Infração:</td>
						<td class="valor_campo" width="50%"><input id="txt_auto" type="text" name="id_auto" class="campo_texto" maxlength="15"/></td>
					</tr>
					<tr>
						<td class="label_campo" width="50%">Tipo</td>
						<td class="valor_campo" width="50%">
							<input id="tipo_nai" type="radio" name="tipo_notif" class="campo_texto" value="NAI"/>NAI&nbsp;&nbsp;
							<input id="tipo_nip" type="radio" name="tipo_notif" class="campo_texto" value="NIP"/>NIP
						</td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="5" width="100%">
							<button onclick="document.getElementById('frm_2a_via').submit()">Visualizar</button><br>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="2" width="100%">(necessário Acrobat Reader)</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
