<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>

<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<br/>
<br/>
<br/>
<script type="text/javascript">
	function preenchePeriodo(e) {
		var txt_dataini = document.getElementById("txt_dataini");
		var txt_datafim = document.getElementById("txt_datafim");
	
	    if (txt_dataini.value != "") {
	        if (txt_datafim.value == "")
	        	txt_datafim.value = txt_dataini.value;
	    }
	    else {
	    	txt_datafim.value = "";
	    }
	}
</script>

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_relatorio" action="/relatorio/rj/ExportarArquivoFluxo15MinutosDER" method="get" target="_blank">
			    <table class="tabela_branca" width="400">
					
                    <tr>
                        <th class="head_tabela" width="100%" colspan="4">Relatório de Fluxo 15 minutos por Classificação</th>
                    </tr>
                    <tr></tr>
                    <tr></tr>
                    <tr>
                        <td class="label_campo" colspan="2" style="text-align: right;">Data inicial:</td>
                        <td class="valor_campo" colspan="2">
                            <input id="txt_dataini" type="text" name="dataini" class="campo_texto" maxlength="10" style="width: 80px" onblur="preenchePeriodo();" value="${dataIni}">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataini'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo" colspan="2" style="text-align: right;">Data final:</td>
                        <td class="valor_campo" colspan="2">
                            <input id="txt_datafim" type="text" name="datafim" class="campo_texto" maxlength="10" style="width: 80px" onblur="preenchePeriodo();" value="${dataFim}">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_datafim'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
				</table>
				<table class="tabela_branca" width="400">
					<tr>
						<td class="box_botoes" colspan="4" width="100%" >
							<button id="btEnvio" onclick="this.submit();" >Gerar Relatório</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="4" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
// 	ajustaDatas();
    document.getElementById("btEnvio").disabled = false;
</script>