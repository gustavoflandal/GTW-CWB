<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="com.consilux.model.Inconsistencia"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>

<%@include file="/includes/cabecalho.jsp"%>
<% 
	String sDataIni = request.getParameter("dataini") != null ? request.getParameter("dataini").trim() : null;
	sDataIni = sDataIni != null && sDataIni.length() == 0 ? null : sDataIni;
	
	String sDataFim = request.getParameter("datafim") != null ? request.getParameter("datafim").trim() : null;
	sDataFim = sDataFim != null && sDataFim.length() == 0 ? null : sDataFim;
	
	String sTipoPeriodo = request.getParameter("selTipoPeriodo") != null ? request.getParameter("selTipoPeriodo").trim() : null;
	sTipoPeriodo = sTipoPeriodo != null ? sTipoPeriodo : "chkPeriodoInfracao";
	
	List<LocalVigente> locais = LocalVigente.buscaLocalVigenteCAV(0);
	List<LocalVigente> equipamentos = LocalVigente.buscaLocalVigenteCAVEquipamento(0, 0, 0); 
	List<Inconsistencia> inconsistencias = Inconsistencia.buscaTodasInconsistencias();
	List<Enquadramento> enquadramentos = Enquadramento.buscaTodosEnquadramentos();
	List<Usuario> operadores = Usuario.listarUsuariosCAI();
	List<Usuario> auditores = Usuario.listarAuditoresCAV();
%>
<c:set var="locais" value="<%=locais%>"/>
<c:set var="equipamentos" value="<%=equipamentos%>"/>
<c:set var="inconsistencias" value="<%=inconsistencias%>"/>
<c:set var="enquadramentos" value="<%=enquadramentos%>"/>
<c:set var="operadores" value="<%=operadores%>"/>
<c:set var="auditores" value="<%=auditores%>"/>

<c:set var="dataIni" value="<%=sDataIni%>"/>
<c:set var="dataFim" value="<%=sDataFim%>"/>
<c:set var="tipoPeriodo" value="<%=sTipoPeriodo%>"/>

<BR/>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_dados" action="" method="get">
			
				<table class="tabela_branca">
					<tr>
						<th class="head_tabela" colspan="3">Relatório Quantitativo por Motivo de Invalidação</th>
					</tr>
	
					<tr><td>&nbsp;</td> </tr>
	
					<tr>
						<td class="label_campo">Período de:</td>
						<td class="valor_campo">
							<input id="txt_data_ini" type="text" name="dataini" maxlength="10" style="width: 80px" value="${data_infracao_ini}"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_ini'), 'dd/mm/yyyy')">
							&nbsp;At&eacute;&nbsp;
							<input id="txt_data_fim" type="text" name="datafim" maxlength="10" style="width: 80px" value="${data_infracao_fim}"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_fim'), 'dd/mm/yyyy')">
						</td>
						<td class="valor_campo" >
							<input type="radio" id="sel_tipo_periodo" name="selTipoPeriodo" value="chkPeriodoInfracao" ${tipoPeriodo == "chkPeriodoInfracao" ? 'checked="checked"' : ''} style="vertical-align: top;">Data da infração
							&nbsp;&nbsp;&nbsp;
							<input type="radio" id="sel_tipo_periodo" name="selTipoPeriodo" value="chkPeriodoAuditoria" ${tipoPeriodo == "chkPeriodoAuditoria" ? 'checked="checked"' : ''} style="vertical-align: top;">Data da auditoria
                        </td>
					</tr>
					
					<tr>
						<td class="label_campo">Motivo Inconsistência:</td>
						<td class="valor_campo" colspan="2">
							<select id="id_sel_inconsistencia" name="sel_inconsistencia" size="4" multiple="multiple">
                              	<c:forEach var="inconsistencia" items="${inconsistencias}">
                                  	<option value="${inconsistencia.idInconsistencia}">${inconsistencia.descricao}</option>
                              	</c:forEach>
							</select>
						</td>
					</tr>
					
					<tr>
					    <td class="label_campo">Local:</td>
					    <td class="valor_campo" colspan="2">
						    <select id="id_sel_cod_prodam" name="sel_cod_prodam" size="4" multiple="multiple">
								<c:forEach var="localProdam" items="${locais}">
									<option value="${localProdam.idLocal}">${localProdam.idLocal} - ${localProdam.nome}</option>
								</c:forEach>
							</select>
					    </td>
					</tr>
					
					<tr>
					    <td class="label_campo">Equipamento:</td>
					    <td class="valor_campo" colspan="2">
						    <select id="id_sel_cod_equip_prodam" name="sel_cod_equip_prodam" size="4" multiple="multiple">
								<c:forEach var="equipamentoProdam" items="${equipamentos}">
                                  	<option value="${equipamentoProdam.idLocalEquipamento}">${equipamentoProdam.idLocalEquipamento} - ${equipamentoProdam.nome}</option>
                              	</c:forEach>
							</select>
					    </td>
					</tr>
					
					<tr>
					    <td class="label_campo">Enquadramento:</td>
					    <td class="valor_campo" colspan="2">
						    <select id="id_sel_enquadramento" name="sel_enquadramento" size="4" multiple="multiple">
                              	<c:forEach var="enquadramento" items="${enquadramentos}">
                              		<c:if test="${enquadramento.idEnquadramento > 1}">
                                  		<option value="${enquadramento.idEnquadramento}">${enquadramento.idEnquadramento} - ${enquadramento.descricao}</option>
                                  	</c:if>
                              	</c:forEach>
                          	</select>
					    </td>
					</tr>
					
					<tr><td>&nbsp;</td> </tr>
					<tr>
						<td></td>
						<td class="label_campo">Operador:</td>
						<td class="label_campo">Auditor:</td>
					</tr>
					<tr>
						<td></td>
						<td class="valor_campo" colspan="1">
							<select id="id_sel_operador" name="sel_operador" size="4" multiple="multiple">
								<c:forEach var="operador" items="${operadores}">
									<option value="${operador.id}">${operador.nome}</option>
								</c:forEach>
							</select>
						</td>
<!-- 					</tr> -->
					
<!-- 					<tr> -->
<!-- 						<td class="label_campo">Auditor:</td> -->
						<td class="valor_campo" colspan="1">
							<select id="id_sel_auditor" name="sel_auditor" size="4" multiple="multiple">
								<c:forEach var="auditor" items="${auditores}">
									<option value="${auditor.id}">${auditor.codigoAgente} - ${auditor.nome}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					
					<tr><td>&nbsp;</td> </tr>
					
					<tr>
						<td class="label_campo">Agrupar por:</td>
						
						<td class="valor_campo">
							<input type="radio" name="selAgruparDiaMes" value="chkAgruparDia" style="vertical-align: bottom;">Dia
							&nbsp;&nbsp;&nbsp;
							<input type="radio" name="selAgruparDiaMes" value="chkAgruparMes" style="vertical-align: bottom;">Mês
							&nbsp;&nbsp;&nbsp;
							<input type="radio" name="selAgruparDiaMes" value="chkAgruparNenhum" checked="checked" style="vertical-align: bottom;">Nenhum
                        </td>
					</tr>
					
				</table>
			</form>
			<form id="frm_rel_RelatorioInfracoesCAV" action="/relatorio/RelatorioInfracoesCAV" method="get" target="_blank">
				<table class="tabela_branca" width="37%" colspan="3">
					<tr>
						<td class="valor_campo">
							<input type="checkbox" name="selAgruparLocal" value="chkLocal" style="vertical-align: middle;">Local
                        </td>
					</tr>
					<tr>
						<td class="valor_campo">
							<input type="checkbox" name="selAgruparEquipamento" value="chkEquipamento" style="vertical-align: middle;">Equipamento
                        </td>
					</tr>
					<tr>
						<td class="valor_campo">
							<input type="checkbox" name="selAgruparEnquadramento" value="chkEnquadramento" style="vertical-align: middle;">Enquadramento
                        </td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="3">
							<button id="btEnvio" onclick="setField();this.form.submit();">Gerar Relatório</button>
						</td>
					</tr>
				</table>
				<input type="hidden" name="dataini"/>
				<input type="hidden" name="datafim"/>
				<input type="hidden" name="selTipoPeriodo"/>
				<input type="hidden" name="sel_inconsistencia"/>
				<input type="hidden" name="sel_cod_prodam"/>
				<input type="hidden" name="sel_cod_equip_prodam"/>
				<input type="hidden" name="sel_enquadramento"/>
				<input type="hidden" name="sel_operador"/>
				<input type="hidden" name="sel_auditor"/>
				<input type="hidden" name="selAgruparDiaMes"/>
				<input type="hidden" name="selAgruparLocalEnquadramento"/>
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script>

	function setField() {
		
		var el_cod_prodam = document.getElementById("id_sel_cod_prodam");
		var sel_cod_prodam = getSelectedValues(el_cod_prodam);
		
		var el_cod_equip_prodam = document.getElementById("id_sel_cod_equip_prodam");
		var sel_cod_equip_prodam = getSelectedValues(el_cod_equip_prodam);
		
		var el_inconsistencia = document.getElementById("id_sel_inconsistencia");
		var sel_inconsistencia = getSelectedValues(el_inconsistencia);
		
		var el_enquadramento = document.getElementById("id_sel_enquadramento");
		var sel_enquadramento = getSelectedValues(el_enquadramento);
		
		var el_operador = document.getElementById("id_sel_operador");
		var sel_operador = getSelectedValues(el_operador);
		
		var el_auditor = document.getElementById("id_sel_auditor");
		var sel_auditor = getSelectedValues(el_auditor);
		
		var frm_dados_relatorio = document.forms["frm_dados"];
		var frm_rel_RelatorioInfracoesCAV = document.forms["frm_rel_RelatorioInfracoesCAV"];
		var dataini_set = frm_dados_relatorio["dataini"];
		var datafim_set = frm_dados_relatorio["datafim"];
		var selTipoPeriodo_set = frm_dados_relatorio["selTipoPeriodo"];
		var selAgruparDiaMes_set = frm_dados_relatorio["selAgruparDiaMes"];
		var selAgruparLocalEnquadramento_set = frm_dados_relatorio["selAgruparLocalEnquadramento"];
		
		frm_rel_RelatorioInfracoesCAV["dataini"].value = dataini_set.value;
		frm_rel_RelatorioInfracoesCAV["datafim"].value = datafim_set.value;
		frm_rel_RelatorioInfracoesCAV["selTipoPeriodo"].value = selTipoPeriodo_set.value;
		frm_rel_RelatorioInfracoesCAV["sel_inconsistencia"].value = sel_inconsistencia;
		frm_rel_RelatorioInfracoesCAV["sel_cod_prodam"].value = sel_cod_prodam;
		frm_rel_RelatorioInfracoesCAV["sel_cod_equip_prodam"].value = sel_cod_equip_prodam;
		frm_rel_RelatorioInfracoesCAV["sel_enquadramento"].value = sel_enquadramento;
		frm_rel_RelatorioInfracoesCAV["sel_operador"].value = sel_operador;
		frm_rel_RelatorioInfracoesCAV["sel_auditor"].value = sel_auditor;
		frm_rel_RelatorioInfracoesCAV["selAgruparDiaMes"].value = selAgruparDiaMes_set.value;
		frm_rel_RelatorioInfracoesCAV["selAgruparLocalEnquadramento"] = selAgruparLocalEnquadramento_set;
		
	}

	function getSelectedValues(select) {
		var selectedValues = [];
		var result;
	  	var options = select && select.options;
	  	var opt;
	
	  	for (var i=0, iLen=options.length; i<iLen; i++) {
	    	opt = options[i];
	
	    	if (opt.selected) {
	    		selectedValues.push(opt.value);
	    	}
	  	}
	  	
	  	result = selectedValues.join(",");
	  	
	  	return result;
	}
	
	function ajustaSelectsLocaisProdam() {
		frm_dados.submit();
	}
 
document.getElementById("btEnvio").disabled = false;

</script>

<%@ include file="/includes/rodape.jsp" %>
