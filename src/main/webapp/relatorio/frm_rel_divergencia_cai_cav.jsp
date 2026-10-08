<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/includes/cabecalho.jsp"%>
<%
String sDataIni = request.getParameter("dataini") != null ? request.getParameter("dataini").trim() : null;
sDataIni = sDataIni != null && sDataIni.length() == 0 ? null : sDataIni;
String dataIni = sDataIni != null ? sDataIni.toString() : "";
	
String sDataFim = request.getParameter("datafim") != null ? request.getParameter("datafim").trim() : null;
sDataFim = sDataFim != null && sDataFim.length() == 0 ? null : sDataFim;
String dataFim = sDataFim != null ? sDataFim.toString() : "";	
%>
<c:set var="dataIni" value="<%=dataIni%>" />
<c:set var="dataFim" value="<%=dataFim%>" />
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<br/><br/><br/>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_filtro_relatorio" action="" method="get">
			    <table class="tabela_branca" width="350">
                    <tr><th class="head_tabela" width="100%" colspan="3">Auditoria de Imagens - Divergência CAI-CAV</th></tr>
					<tr><td>&nbsp;</td></tr>
                    <tr>
                        <td class="label_campo" width="40%" style="text-align: right;">Data inicial:</td>
                        <td class="valor_campo" width="60%">
                            <input id="txt_dataini" type="text" name="dataini" class="campo_texto" maxlength="10" style="width: 110px" onblur="preenchePeriodo();" value="${dataIni}">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataini'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo" width="40%" style="text-align: right;">Data final:</td>
                        <td class="valor_campo" width="60%">
                            <input id="txt_datafim" type="text" name="datafim" class="campo_texto" maxlength="10" style="width: 110px" onblur="preenchePeriodo();" value="${dataFim}">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_datafim'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
				</table>
			</form>
			<form id="frm_rel_RelatorioDivergenciaCAI_CAV" action="/relatorio/RelatorioDivergenciaCAI_CAV" method="get" target="_blank">
				<table class="tabela_branca" width="350">
					<tr>
						<td class="box_botoes" colspan="1" width="100%" >
							<button id="btEnvio" onclick="setField();this.submit();">Gerar Relatório</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="2" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
				<input type="hidden" name="dataini" />
				<input type="hidden" name="datafim" />
			</form>
		</td>
	</tr>
</table>
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
	function setField() {
		var frm_filtro_relatorio = document.forms["frm_filtro_relatorio"];
		var frm_rel_RelatorioDivergenciaCAI_CAV = document.forms["frm_rel_RelatorioDivergenciaCAI_CAV"];
		var dataini_set = frm_filtro_relatorio["dataini"];
		var datafim_set = frm_filtro_relatorio["datafim"];
		
		frm_rel_RelatorioDivergenciaCAI_CAV["dataini"].value = dataini_set.value;
		frm_rel_RelatorioDivergenciaCAI_CAV["datafim"].value = datafim_set.value;
	}
    
    document.getElementById("btEnvio").disabled = false;
</script>