<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<br/>
<br/>
<br/>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_solicitacao_auditoria" action="/SolicitacaoAuditoria" method="get" target="_blank">
				<table class="tabela_branca" width="300">
					<tr>
						<th class="head_tabela" width="100%" colspan="2">Solicitação de Auditoria</th>
					</tr>
					<tr>
	                    <td class="label_campo" width="40%">Data:</td>
	                    <td class="valor_campo" width="60%">
                            <input id="txt_data_imagens" type="text" name="data_imagens" maxlength="10" style="width: 80px" ">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_imagens'), 'dd/mm/yyyy')">
                        </td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="2">
							<button onclick="document.getElementById('frm_solicitacao_auditoria').submit()">Visualizar</button><br>
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
