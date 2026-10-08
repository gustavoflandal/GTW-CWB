<%@page import="com.consilux.model.medicao.DadosEficienciaEquipamento"%>
<%@page import="com.consilux.model.medicao.ItemEficienciaEquipamento"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>

<%
	
	String sDataIni = request.getParameter("dataini") != null ? request.getParameter("dataini").trim() : null;
	sDataIni = sDataIni != null && sDataIni.length() == 0 ? null : sDataIni;
	String dataIni = sDataIni != null ? sDataIni.toString() : "";
	
	String sDataFim = request.getParameter("datafim") != null ? request.getParameter("datafim").trim() : null;
	sDataFim = sDataFim != null && sDataFim.length() == 0 ? null : sDataFim;
	String dataFim = sDataFim != null ? String.valueOf(sDataFim) : "";	

	String sLocal = request.getParameter("local") != null ? request.getParameter("local").trim() : null;
	sLocal = sLocal != null && sLocal.length() == 0 ? null : sLocal;
	Integer idLocalProdam = sLocal != null ? Integer.valueOf(sLocal) : 0;
	
	String sEquipamento = request.getParameter("equipamento") != null ? request.getParameter("equipamento").trim() : null;
	sEquipamento = sEquipamento != null && sEquipamento.length() == 0 ? null : sEquipamento;
	Integer idEquipamentoProdam = sLocal != null ? Integer.valueOf(sEquipamento) : 0;
	
	String sEnquadramento = request.getParameter("enquadramento") != null ? request.getParameter("enquadramento").trim() : null;
	sDataFim = sEnquadramento != null && sEnquadramento.length() == 0 ? null : sEnquadramento;
	String enquadramento = sEnquadramento != null ? String.valueOf(sEnquadramento) : "";

	List<LocalVigente> locaisProdam = LocalVigente.buscaLocalVigenteCAV();
	List<LocalVigente> equipamentosProdam = LocalVigente.buscaLocalVigenteCAVEquipamento(0, idLocalProdam, 0);
	List<ItemEficienciaEquipamento> enquadramentosAtivos = DadosEficienciaEquipamento.enquadramentosAtivos();

%>

<c:set var="locaisProdam" value="<%=locaisProdam%>" />
<c:set var="equipamentosProdam" value="<%=equipamentosProdam%>" />
<c:set var="enquadramentosAtivos" value="<%=enquadramentosAtivos%>" />
<c:set var="idLocalProdam" value="<%=idLocalProdam%>" />
<c:set var="idEquipamentoProdam" value="<%=idEquipamentoProdam%>" />
<c:set var="dataIni" value="<%=dataIni%>" />
<c:set var="dataFim" value="<%=dataFim%>" />
<c:set var="enquadramento" value="<%=enquadramento%>" />

<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<br/>
<br/>
<br/>
<script type="text/javascript">
// 	function ajustaDatas() {
// 		var txt_dataini = document.getElementById("txt_dataini");
// 		var txt_datafim = document.getElementById("txt_datafim");
// 		var dtAgora = new Date();
		
// 		if (txt_dataini.value == "") {
// 			txt_dataini.value = "01/"+adicZeroEsquerda(dtAgora.getMonth()+1,2)+"/"+dtAgora.getFullYear();
// 		}
// 		if (txt_datafim.value == "") {
// 			txt_datafim.value = adicZeroEsquerda(dtAgora.getDate(),2)+"/"+adicZeroEsquerda(dtAgora.getMonth()+1,2)+"/"+dtAgora.getFullYear();
// 		}
// 	}

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
	
    function ajustaLocal(porCodigo) {
        var txt_local = document.getElementById("txt_local");
        var sel_local = document.getElementById("sel_local");
        var txt_local_old = document.getElementById("txt_local_old");
        
        if (porCodigo) {
            sel_local.selectedIndex = 0;

            for (var i=0;i<sel_local.options.length;i++) {
                if (sel_local.options[i].value == txt_local.value)
                    sel_local.selectedIndex = i;
            }
        }
        else {
            txt_local.value = sel_local.value;
        }
        
        if (txt_local_old.value != txt_local.value) {
        	txt_local_old.value = txt_local.value;
        	document.getElementById("frm_filtro_relatorio").submit();
        }
    }
    
	function setField() {
		var frm_filtro_relatorio = document.forms["frm_filtro_relatorio"];
		var frm_rel_RelatorioEnquadramentoHabilitadoEfetivo = document.forms["frm_rel_RelatorioEnquadramentoHabilitadoEfetivo"];
		
		var dataini_set = frm_filtro_relatorio["dataini"];
		var datafim_set = frm_filtro_relatorio["datafim"];
		var local_set = frm_filtro_relatorio["local"];
		var equipamento_set = frm_filtro_relatorio["equipamento"];
		var enquadramento_set = frm_filtro_relatorio["enquadramento"];

		
		frm_rel_RelatorioEnquadramentoHabilitadoEfetivo["dataini"].value = dataini_set.value;
		frm_rel_RelatorioEnquadramentoHabilitadoEfetivo["datafim"].value = datafim_set.value;
		frm_rel_RelatorioEnquadramentoHabilitadoEfetivo["local"].value = local_set.value;
		frm_rel_RelatorioEnquadramentoHabilitadoEfetivo["equipamento"].value = equipamento_set.value;
		frm_rel_RelatorioEnquadramentoHabilitadoEfetivo["enquadramento"].value = enquadramento_set.value;
		
	}
    
</script>

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_filtro_relatorio" action="" method="get">
			    <table class="tabela_branca" width="700">
					
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">Relatório de Eficiência do Equipamento</th>
                    </tr>
					<tr><td>&nbsp;</td> </tr>
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">Captadas/Consistentes</th>
                    </tr>
                    <tr><td>&nbsp;</td> </tr>
                    
                    <tr>
                    	<td class="label_campo" colspan="1">Local Prodam:</td>
                        <td class="valor_campo" colspan="2">
                        	<input id="txt_local" type="text" name="id_local" class="campo_texto" maxlength="4" onblur="ajustaLocal(true)" value="${idLocalProdam == 0 ? '' : idLocalProdam}"/>
                        	<input id="txt_local_old" type="hidden" name="id_local_old" value="${idLocalProdam == 0 ? '' : idLocalProdam}" />
                        </td>
                      	<td class="valor_campo" colspan="4">
                          	<select id="sel_local" name="local" onchange="ajustaLocal(false);">
                              	<option value="0" selected="selected">--Local Prodam--</option>
                              	<c:forEach var="localProdam" items="${locaisProdam}">
                                  	<option value="${localProdam.idLocal}" ${idLocalProdam == localProdam.idLocal ? 'selected' : ''}>${localProdam.nome}</option>
                              	</c:forEach>
                          	</select>
                      	</td>
                    </tr>
                    
                    <tr>
                    	<td class="label_campo" colspan="1">Equipamento Prodam:</td>
<%--                         <td class="valor_campo" width="15%"><input id="txt_equipamento" type="text" name="id_equipamento" class="campo_texto" maxlength="4" value="${idEquipamentoProdam}"/></td> --%>
                      	<td class="valor_campo" colspan="4">
                          	<select id="sel_equipamento" name="equipamento">
                              	<option value="0" selected="selected">--Equipamento Prodam--</option>
                              	<c:forEach var="equipamentoProdam" items="${equipamentosProdam}">
                                  	<option value="${equipamentoProdam.idLocalEquipamento}" ${idEquipamentoProdam == equipamentoProdam.idLocalEquipamento ? 'selected' : ''}>${equipamentoProdam.idLocalEquipamento}</option>
                              	</c:forEach>
                          	</select>
                      	</td>
                    </tr>
                    
                    <tr>
                    	<td class="label_campo" colspan="1">Enquadramento:</td>
                      	<td class="valor_campo" colspan="4">
                          	<select id="sel_enquadramento" name="enquadramento">
                              	<option value="" selected="selected">--Enquadramento--</option>
                              	<c:forEach var="enquadramentoAtivo" items="${enquadramentosAtivos}">
                                  	<option value="${enquadramentoAtivo.enquadramentoAtivo}" ${enquadramento == enquadramentoAtivo.enquadramentoAtivo ? 'selected' : ''}>${enquadramentoAtivo.enquadramentoAtivo}</option>
                              	</c:forEach>
                          	</select>
                      	</td>
                    </tr>
                    
                    <tr>
                        <td class="label_campo" colspan="1">Data inicial:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_dataini" type="text" name="dataini" class="campo_texto" maxlength="10" style="width: 110px" onblur="preenchePeriodo();" value="${dataIni}">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataini'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo" colspan="1">Data final:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_datafim" type="text" name="datafim" class="campo_texto" maxlength="10" style="width: 110px" onblur="preenchePeriodo();" value="${dataFim}">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_datafim'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
				</table>
			</form>
			
			<form id="frm_rel_RelatorioEnquadramentoHabilitadoEfetivo" action="/relatorio/RelatorioEnquadramentoHabilitadoEfetivo" method="get" target="_blank">
				<table class="tabela_branca" width="700">
					<tr>
						<td class="box_botoes" colspan="5" width="100%" >
							<button id="btEnvio" onclick="setField();this.submit();" >Gerar Relatório</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="3" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
				<input type="hidden" name="dataini" />
				<input type="hidden" name="datafim" />
				<input type="hidden" name="local" />
				<input type="hidden" name="equipamento" />
				<input type="hidden" name="enquadramento" />
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
// 	ajustaDatas();
    document.getElementById("btEnvio").disabled = false;
</script>