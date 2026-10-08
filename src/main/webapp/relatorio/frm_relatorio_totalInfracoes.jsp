<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<br/>
<br/>
<br/>
<script type="text/javascript">
	function ajustaDatas() {
		var txt_dataini = document.getElementById("txt_dataini");
		var txt_datafim = document.getElementById("txt_datafim");
		var dtAgora = new Date();
		
		if (txt_dataini.value == "") {
			txt_dataini.value = "01/"+adicZeroEsquerda(dtAgora.getMonth()+1,2)+"/"+dtAgora.getFullYear();
		}
		if (txt_datafim.value == "") {
			txt_datafim.value = adicZeroEsquerda(dtAgora.getDate(),2)+"/"+adicZeroEsquerda(dtAgora.getMonth()+1,2)+"/"+dtAgora.getFullYear();
		}
	}
	
	function limpaRadio() {
		
		if(document.getElementById("rd_Fixo").checked){
			document.getElementById("rd_FixoHidden").checked = true;
		}else{
			document.getElementById("rd_FixoHidden").checked = false;
		}
		
		if(document.getElementById("rd_Estatico").checked ){
			document.getElementById("rd_EstaticoHidden").checked =true;
		}else{
			document.getElementById("rd_EstaticoHidden").checked =false;
		}
		
		document.getElementById("rd_Fixo").checked = false;
		document.getElementById("rd_Estatico").checked = false;
		
		this.submit();
	 
	}

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
			<form id="frm_relatorio_TotalInfracoes" action="/relatorio/RelatorioTotalInfracoes" method="get" target="_blank">
                <table class="tabela_branca" width="500">
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">Processo de Medição - Relatório B</th>
                    </tr>
					<tr><td>&nbsp;</td> </tr>
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">Total de Imagens</th>
                    </tr>
                    <tr><td>&nbsp;</td> </tr>
                    <tr>
                        <td class="label_campo">Data inicial:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_dataini" type="text" name="dataini" class="campo_texto" maxlength="10" style="width: 110px" onblur="preenchePeriodo();">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataini'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Data final:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_datafim" type="text" name="datafim" class="campo_texto" maxlength="10" style="width: 110px" onblur="preenchePeriodo();">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_datafim'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
                    

				
                    <tr>
                    	<td class="valor_campo" colspan="4">
                         	<input id="rd_Fixo" name="relFixo" type="radio" value="1"  /> Radares Fixos e Lombadas
                    	</td>
           
                    	<td class="valor_campo" colspan="4">
                         	<input id="rd_Estatico" name="relEstatico" type="radio" value="1"  /> Radares Estáticos
                    	</td>
                    </tr>

                    <tr>
                    	<td class="valor_campo" colspan="4" hidden="true">
                         	<input id="rd_FixoHidden" name="relFixoHidden" type="radio" value="1"  /> Radares Fixos e Lombadas
                    	</td>
           
                    	<td class="valor_campo" colspan="4" hidden="true">
                         	<input id="rd_EstaticoHidden" name="relEstaticoHidden" type="radio" value="1"  /> Radares Estáticos
                    	</td>
                    </tr>

					<tr>
						<td class="box_botoes" colspan="5" width="100%">
							<button id="btEnvio" onclick="limpaRadio(); ">Gerar Medição</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="3" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
	ajustaDatas();
    document.getElementById("btEnvio").disabled = false;
</script>