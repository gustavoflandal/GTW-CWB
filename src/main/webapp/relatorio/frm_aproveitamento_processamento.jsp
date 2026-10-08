<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>

<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>

<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
			<form name="frm_aproveitamento_processamento" action="/relatorio/ProcessamentoAproveitamento" method="GET">
			<table class="tabela_branca" width="280px">
				<tr>
					<td align="center" valign="top">
			            <table class="tabela_branca" width="100%">
							<tr>
								<th class="head_tabela" width="100%" colspan="2">Filtros do Relatório</th>
							</tr>
			    			<tr>
								<td class="label_campo">Data Inicial</td>
			                    <td class="valor_campo"">
									<input id="txt_data_ini" type="text" name="data_ini" maxlength="10" style="width: 80px" >
									<img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_ini'), 'dd/mm/yyyy')">
								</td>
			    			</tr>
			    			<tr>
								<td class="label_campo">Data Final</td>
								<td class="valor_campo"">
			                    	<input id="txt_data_fim" type="text" name="data_fim" maxlength="10" style="width: 80px">
			                    	<img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_fim'), 'dd/mm/yyyy')">
								</td>
			    			</tr>
							<tr>
								<td class="box_botoes" colspan="2">
									<button onclick="frm_aproveitamento_imagens_agrupado.submit()">Visualizar</button>
								</td>
							</tr>
	    				</table>
					</td>
				</tr>
			</table>
			</form>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
