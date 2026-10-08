<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/includes/cabecalho.jsp"%>
<br/><br/><br/>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_filtro_relatorio" action="" method="get">
			    <table class="tabela_branca" width="300">
                    <tr><th class="head_tabela" width="100%" colspan="2">Fixo/Barreira - Fluxo na Via</th></tr>
					<tr><td>&nbsp;</td></tr>
                    <tr>
                        <td class="label_campo" width="45%" style="text-align: right;">Mês:</td>
                        <td class="valor_campo" width="55%">
                            <input id="txt_mes" type="text" name="mes" class="campo_texto" maxlength="2" style="width: 50px">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo" width="45%" style="text-align: right;">Ano:</td>
                        <td class="valor_campo" width="55%">
                            <input id="txt_ano" type="text" name="ano" class="campo_texto" maxlength="4" style="width: 50px">
                        </td>
                    </tr>
				</table>
			</form>
			<form id="frm_rel_RelatorioFluxoVia" action="/relatorio/RelatoriosMedicao_CAV_CAI" method="get" target="_blank">
				<table class="tabela_branca" width="300">
					<tr>
						<td class="box_botoes" colspan="1" width="100%" >
							<button id="btEnvio" onclick="setField();this.submit();">Gerar Medição</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="2" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
				<input type="hidden" name="mes"/>
				<input type="hidden" name="ano"/>
				<input type="hidden" name="selRelatorio" value="chkFluxoFixo">
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function setField() {
		var frm_filtro_relatorio = document.forms["frm_filtro_relatorio"];
		var frm_rel_RelatorioFluxoVia = document.forms["frm_rel_RelatorioFluxoVia"];
		var mes_set = frm_filtro_relatorio["mes"];
		var ano_set = frm_filtro_relatorio["ano"];
		
		frm_rel_RelatorioFluxoVia["mes"].value = mes_set.value;
		frm_rel_RelatorioFluxoVia["ano"].value = ano_set.value;
	}
    
    document.getElementById("btEnvio").disabled = false;
</script>