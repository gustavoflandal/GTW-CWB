<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="java.util.Collection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="/js/CsxRemoteObject.js"></script>
<script type="text/javascript" src="NfAutuacaoInfracao.js"></script>


<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
			<form id="NfAutuacaoInfracao" action="/controleAIT/NotificacaoPDF" method="get" target="_blank">
				<table class="tabela_branca" width="350px">
					<tr>
						<th class="head_tabela" width="100%" colspan="5" >Gerencia NAI</th>
					</tr>
					
					<tr>
						<td class="label_campo" width="30%">Cód. Infração:</td>
						<td class="valor_campo" width="50%"><input id="txt_auto" type="text" name="id_auto" class="campo_texto" maxlength="15"/></td>
					</tr>
					
					<tr>
						<td class="label_campo" width="30%">Status AR:</td>
						<td class="valor_campo" width="70%">
							<select id="sel_id_status" name="status_notif" style="width: 50%">
								<option value="0" >Pendente</option>
								<option value="1">Recebido</option>
								<option value="2">Sem Retorno</option>
							</select>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" align="right">
							<input id="btnPesquisar" type="button" value="Pesquisar" onclick="btnPesquisar_OnClick()"/>
						</td>
					</tr>				
				</table>
			</form>
			<div style="width: 100%" id="divTable"></div>
		</td>
	</tr>
</table>










<%@ include file="/includes/rodape.jsp" %>