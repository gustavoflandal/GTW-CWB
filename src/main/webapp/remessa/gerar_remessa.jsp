<%@page import="com.consilux.model.RelatorioRemessaAutomatica"%>
<%@page import="com.consilux.servlet.remessa.GerarRemessa"%>
<%@page import="com.consilux.model.Inconsistencia"%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="com.consilux.model.Processamento.EtapaProcesso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%!
	private String obterString(HttpServletRequest request, String nomeParametro) {
		Object param = request.getParameter(nomeParametro);
		if (param != null) {
			String sParam = ((String) param).trim();
			if (sParam.length() > 0)
				return sParam;
		}
		return null;		
	}
%>
<%

	String sDataIni = obterString(request, "dataini");
	String sDataFim = obterString(request, "datafim");
	String sInfracoes = obterString(request, "infracoes");
	String sDataRemessa = obterString(request, "dataremessa");
	
	sInfracoes = sInfracoes == "" || sInfracoes == null ? "280" : sInfracoes;
	
	String sEnquadramento = request.getParameter("id_enquadramento") != null ? request.getParameter("id_enquadramento").trim() : null;
	sEnquadramento = sEnquadramento != null && sEnquadramento.length() == 0 ? null : sEnquadramento;
	Integer idEnquadramento = sEnquadramento != null ? Integer.valueOf(sEnquadramento) : 999999;
	
	String sInconsistencia = request.getParameter("id_inconsistencia") != null ? request.getParameter("id_inconsistencia").trim() : null;
	sInconsistencia = sInconsistencia != null && sInconsistencia.length() == 0 ? null : sInconsistencia;
	Integer idInconsistencia = sInconsistencia != null ? Integer.valueOf(sInconsistencia) : 999999;

	String sResidual = "true";
	Boolean residual = true;
	
	String sMostrarInconsistencia = request.getParameter("mostrar_inconsistencia") != null ? request.getParameter("mostrar_inconsistencia").trim() : null;
	sMostrarInconsistencia = sMostrarInconsistencia != null && sMostrarInconsistencia.length() == 0 ? null : sMostrarInconsistencia;
	Boolean mostrarInconsistencia = sMostrarInconsistencia != null ? Boolean.valueOf(sMostrarInconsistencia) : false;
	
	if (mostrarInconsistencia == false) {
		idInconsistencia = -1;
	}

	//List<Processo> processosRemessa = Processo.buscaProcessosRemessa();
	EtapaProcesso etapa = EtapaProcesso.REMESSA_GERAL;

	List<Enquadramento> enquadramentos = Enquadramento.buscaEnquadramentoDisponivelPorIdProcesso(etapa.getId());
	List<Inconsistencia> inconsistencias = Inconsistencia.buscaTodasInconsistenciaPorEtapaProcesso(etapa);
	
	Inconsistencia i = new Inconsistencia(99, "Vel. 100% Acima da Vel. Regul.");
	inconsistencias.add(0, i);

	RelatorioInfracoesNaRemessa rinr = new RelatorioInfracoesNaRemessa();
	List<RelatorioInfracoesNaRemessa> lrir = rinr.buscaRelatorio();
	
	int totalImagens = 0;
	for(RelatorioInfracoesNaRemessa a : lrir){
		totalImagens = totalImagens + a.getTotal();
	}
	
	//Relatório de infrações por Inconsistencia
	RelatorioInfracoesNaRemessa relInconsistencia = new RelatorioInfracoesNaRemessa();
	List<RelatorioInfracoesNaRemessa> lRelInconsistencia = relInconsistencia.buscaRelatorioInconsistencia();
	
	
	//Agendamentos pendentes de geração
	RelatorioRemessaAutomatica rra = new RelatorioRemessaAutomatica();
	List<RelatorioRemessaAutomatica> lrra = rra.buscaRelatorioGeracaoPendente();
	
	boolean possuiAgendamentoPendente = lrra.size() > 0;
	
	int totalImagensInconsistencia = 0;
	for(RelatorioInfracoesNaRemessa b : lRelInconsistencia){
		totalImagensInconsistencia = totalImagensInconsistencia + b.getTotal();
	}
%>
<c:set var="sDataIni" value="<%=sDataIni%>" />
<c:set var="sDataFim" value="<%=sDataFim%>" />
<c:set var="sInfracoes" value="<%=sInfracoes%>" />
<c:set var="sDataRemessa" value="<%=sDataRemessa%>" />
<c:set var="idEnquadramento" value="<%=idEnquadramento%>" />
<c:set var="idInconsistencia" value="<%=idInconsistencia%>" />
<c:set var="residual" value="<%=residual%>" />
<c:set var="mostrarInconsistencia" value="<%=mostrarInconsistencia%>" />
<c:set var="lrir" value="<%=lrir%>" />
<c:set var="ti" value="<%=totalImagens%>" />
<c:set var="enquadramentos" value="<%=enquadramentos%>" />
<c:set var="lRelInconsistencia" value="<%=lRelInconsistencia%>" />
<c:set var="totalInconsistencia" value="<%=totalImagensInconsistencia%>" />
<c:set var="inconsistencias" value="<%=inconsistencias%>" />
<c:set var="lrra" value="<%=lrra%>" />
<c:set var="possuiAgendamentoPendente" value="<%=possuiAgendamentoPendente%>" />
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@page import="com.consilux.model.Processo"%>
<%@page import="com.consilux.model.RelatorioInfracoesNaRemessa"%>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript">
	function ajustaDatas() {
		var txt_dataremessa = document.getElementById("txt_dataremessa");
		var dtAgora = new Date();
		
		if (txt_dataremessa.value == "") {
			txt_dataremessa.value = adicZeroEsquerda(dtAgora.getDate(),2)+"/"+adicZeroEsquerda(dtAgora.getMonth()+1,2)+"/"+dtAgora.getFullYear();
		}
	}
	
	function ajustaValor() {
	    var sel_inconsistencia = document.getElementById("sel_inconsistencia");
	    var mostrarInconsistencia = document.getElementById("id_mostra_inc"); 
	
	    if (sel_inconsistencia != null) {
		    if (mostrarInconsistencia.checked == false) {
		    	sel_inconsistencia.selectedIndex = 0;
		    }
	    }
	}
	
	function gerarRemessa(tipo_geracao) {
		/*
		* Tipo de geração:
		*	1: Manual
		*	2: Automática
		*/
		
		var frm_list_remessa = document.forms["frm_list_remessa"];
		var frm_gerar_remessa = document.forms["frm_gerar_remessa"];
		
		var dataini_set = frm_list_remessa["dataini"];
		var datafim_set = frm_list_remessa["datafim"];
		var infracoes_set = frm_list_remessa["infracoes"];
		var dataremessa_set = frm_list_remessa["dataremessa"];
		var id_enquadramento_set = frm_list_remessa["id_enquadramento"];
		var residual_set = frm_list_remessa["residual"];
		var mostrar_inconsistencia_set = frm_list_remessa["mostrar_inconsistencia"];
		if (mostrar_inconsistencia_set.checked == true) {
			var id_inconsistencia_set = frm_list_remessa["id_inconsistencia"];
		}

		
		frm_gerar_remessa["dataini"].value = dataini_set.value;
		frm_gerar_remessa["datafim"].value = datafim_set.value;
		frm_gerar_remessa["infracoes"].value = infracoes_set.value;
		frm_gerar_remessa["dataremessa"].value = dataremessa_set.value;
		frm_gerar_remessa["id_enquadramento"].value = id_enquadramento_set.value;
		frm_gerar_remessa["residual"].value = residual_set.value;
		if (mostrar_inconsistencia_set.checked == true) {
			frm_gerar_remessa["id_inconsistencia"].value = id_inconsistencia_set.value;
		}
		frm_gerar_remessa["tipo_geracao"].value = tipo_geracao;
		
		var gerar = false;
		var ok = false;
		
		if (tipo_geracao == 1) {
			gerar = true;
		} else if (tipo_geracao == 2) {
			ok = confirm("Confirma a geração AUTOMÁTICA de remessas?");
			if (ok) {
	        	gerar = true;
	        }
		}
		
		if (gerar) {
			document.getElementById('frm_gerar_remessa').submit();
		} else {
			return false;
        }
		
	}
	
</script>

<br />
<br />

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_list_remessa" action="" method="get">
				<table class="tabela_branca" width="500">
					<tr>
						<th class="head_tabela" width="100%" colspan="3">Movimento de Lote</th>
					</tr>
					<tr>
						<td class="label_campo" width="35%">Período:</td>
                        <td class="valor_campo">
                            <input id="txt_dataini" type="text" name="dataini" class="campo_texto" maxlength="10" style="width: 110px" value="${sDataIni}">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataini'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Até</td>
                        <td class="valor_campo">
                            <input id="txt_datafim" type="text" name="datafim" class="campo_texto" maxlength="10" style="width: 110px" value="${sDataFim}">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_datafim'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
                    
                    <tr>
                    	<td class="label_campo">Infrações</td>
                    	<td class="valor_campo">
                    		<input id="txt_infracoes" type="text" name="infracoes" class="campo_texto" maxlength="4" style="width: 110px" value="${sInfracoes}">
                    	</td>
                    </tr>
                    
					<tr>
						<td class="label_campo">Enquadramento:</td>
						<td class="valor_campo" colspan="2">
                            <select id="sel_enquadramento" name="id_enquadramento" style="width: 350px">
		                        <c:forEach var="enquadramento" items="${enquadramentos}">
		                            <option value="${enquadramento.idEnquadramento}" ${idEnquadramento == enquadramento.idEnquadramento ? 'selected' : ''}>${enquadramento.descricao}</option>
		                        </c:forEach>
	                        </select>
                        </td>
					</tr>
					
					<c:if test="${mostrarInconsistencia == true}">
					<tr>
						<td class="label_campo">Inconsistência:</td>
                        <td class="valor_campo" colspan="2">
                            <select id="sel_inconsistencia" name="id_inconsistencia" style="width: 350px">
                            	<option value="-1">-- Selecione uma inconsistência --</option>
		                        <c:forEach var="inconsistencia" items="${inconsistencias}">
		                        	<c:if test="${inconsistencia.idInconsistencia > 0}">
		                            	<option value="${inconsistencia.idInconsistencia}" ${idInconsistencia == inconsistencia.idInconsistencia ? 'selected' : ''}>${inconsistencia.descricao}</option>
		                            </c:if>
		                        </c:forEach>
	                        </select>
                        </td>
					</tr>
					</c:if>

                    <tr>
                        <td class="label_campo">Agendar para:</td>
                        <td class="valor_campo">
                            <input id="txt_dataremessa" type="text" name="dataremessa" class="campo_texto" maxlength="10" style="width: 110px" value="${sDataRemessa}">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataremessa'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
					<tr>
                        <td class="valor_campo" colspan="2">
                        	<br>
							<input type="checkbox" name="residual" ${residual == true ? 'checked' : ''} value="true" style="vertical-align: middle;">Movimento residual
                        </td>
                        <td class="valor_campo" colspan="1">
                        	<br>
							<input id="id_mostra_inc" type="checkbox" name="mostrar_inconsistencia" ${mostrarInconsistencia == true ? 'checked' : ''} value="true" onclick="ajustaValor();submit();" style="vertical-align: middle;">Mostrar por Inconsistência
                        </td>
					</tr>
				</table>
			</form>
			
			
			<form id="frm_gerar_remessa" action="/remessa/GerarRemessa" method="get">
				<table class="tabela_branca" width="500">
					<tr>
						<td class="box_botoes" colspan="1" width="50%" >
							<button id="btEnvioManual" onclick="gerarRemessa(1);return false;">Gerar Manual</button>
						</td>
						<td class="box_botoes" colspan="1" width="50%" >
							<button id="btEnvioAutomatico" onclick="gerarRemessa(2);return false;" title="Gerar automaticamente movimentos de lote para todas infrações de todos os enquadramentos e período disponíveis para geração de lotes.">Gerar Automático</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="2" width="100%">(necessário Zip Manager)</td>
					</tr>
				</table>
				<input type="hidden" name="dataini" />
				<input type="hidden" name="datafim" />
				<input type="hidden" name="infracoes" />
				<input type="hidden" name="dataremessa" />
				<input type="hidden" name="id_enquadramento" />
				<input type="hidden" name="id_inconsistencia" />
				<input type="hidden" name="residual" />
				<input type="hidden" name="tipo_geracao" />
			</form>
		</td>
	</tr>
</table>

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
		<table class="tabela_branca" width="500">
			<tr>
				<th class="head_tabela" width="100%" colspan="5">Agendamentos pendentes de geração</th>
			</tr>
			<c:if test="${possuiAgendamentoPendente == false}">
				<tr align="center" >
					<th class="dado_lista_tabela_claro" align="center" width="80%">Não há agendamentos pendentes!</th>
				</tr>
			</c:if>
			<c:if test="${possuiAgendamentoPendente == true}">
				<tr align="center" >
					<th class="head_tabela" align="center" width="18%">Data solicitação</th>
					<th class="head_tabela" align="center" width="18%">Data inicial</th>
					<th class="head_tabela" align="center" width="18%">Data final</th>
					<th class="head_tabela" align="center" width="20%">Qtde. Infrações</th>
					<th class="head_tabela" align="center" width="26%">Status</th>
				</tr>
				<c:forEach var="rra" varStatus="linhaInfo" items="${lrra}">
					<tr align="center" >
						<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						<td class="${css_td}" align="center" ><fmt:formatDate pattern="dd/MM/yyyy" value="${rra.dataSolicitacao}" type="date" /></td>
						<td class="${css_td}" align="center" ><fmt:formatDate pattern="dd/MM/yyyy" value="${rra.dataInicial}" type="date" /></td>
						<td class="${css_td}" align="center" ><fmt:formatDate pattern="dd/MM/yyyy" value="${rra.dataFinal}" type="date" /></td>
						<td class="${css_td}" align="center" >${rra.totalInfracoes}</td>
						<td class="${css_td}" align="center" >${rra.status}</td>
					</tr>
				</c:forEach>
			</c:if>
		</table>
		</td>
	</tr>
</table>

<br/>
<br/>

<c:if test="${mostrarInconsistencia == false}">
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
		<table class="tabela_branca" width="500">
			<tr>
				<th class="head_tabela" width="100%" colspan="2">Imagens Disponíveis para Gerar Movimento</th>
			</tr>
				<tr align="center" >
					<th class="head_tabela" align="center" width="40%">Data</th>
					<th class="head_tabela" align="center" width="40%">Quantidade de Infrações</th>

				</tr>
				<c:set var="enquadramentoAnterior" value="" />
				<c:forEach var="rir" varStatus="linhaInfo" items="${lrir}">
					<c:if test="${rir.nome != enquadramentoAnterior}">
						<tr><td class="head_tabela" align="left" colspan="2" bgcolor="darkgray">${rir.nome}</td></tr>
					</c:if>
					
					<tr align="center" >
						<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						<td class="${css_td}" align="center" width="40%"><fmt:formatDate pattern="dd/MM/yyyy" value="${rir.data}" type="date" /></td>
						<td class="${css_td}" align="center" width="40%">${rir.total}</td>
					</tr>
					<c:set var="enquadramentoAnterior" value="${rir.nome}" />
				</c:forEach>
				<tr align="center" >
					<th class="head_tabela" align="center" colspan="2" width="100%">Total</th>
				</tr>
				<tr align="center" >
					<td class="${css_td}" align="center" colspan="2" width="100%">${ti}</td>
				</tr>
		</table>
		</td>
	</tr>
</table>
</c:if>


<!-- INCONSISTENCIA -->
<c:if test="${mostrarInconsistencia == true}">
<!-- <br/><br/><br/> -->

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
		<table class="tabela_branca" width="500">
			<tr>
				<th class="head_tabela" width="100%" colspan="2">Imagens Disponíveis para Gerar Movimento (Inconsistência)</th>
			</tr>
				<tr align="center" >
					<th class="head_tabela" align="center" width="40%">Data</th>
					<th class="head_tabela" align="center" width="40%">Quantidade de Infrações</th>

				</tr>
				<c:set var="inconsistenciaAnterior" value="" />
				<c:set var="enquadramentoAnterior" value="" />
				<c:set var="quebraLinha" value="0" />
				<c:forEach var="relInconsistencia" varStatus="linhaInfo" items="${lRelInconsistencia}">
					<c:if test="${relInconsistencia.descricaoInconsistencia != inconsistenciaAnterior}">
						<c:if test="${quebraLinha == 1}">
							<tr><td class="dado_lista_tabela_claro" align="left" colspan="2" bgcolor="withe"></td></tr>
							<tr><td class="dado_lista_tabela_claro" align="left" colspan="2" bgcolor="withe"></td></tr>
							<tr><td class="dado_lista_tabela_claro" align="left" colspan="2" bgcolor="withe"></td></tr>
							<tr><td class="dado_lista_tabela_claro" align="left" colspan="2" bgcolor="withe"></td></tr>
							<tr><td class="dado_lista_tabela_claro" align="left" colspan="2" bgcolor="withe"></td></tr>
							<tr><td class="dado_lista_tabela_claro" align="left" colspan="2" bgcolor="withe"></td></tr>
							<tr><td class="dado_lista_tabela_claro" align="left" colspan="2" bgcolor="withe"></td></tr>
							<tr><td class="dado_lista_tabela_claro" align="left" colspan="2" bgcolor="withe"></td></tr>
							<tr><td class="dado_lista_tabela_claro" align="left" colspan="2" bgcolor="withe"></td></tr>
							<tr><td class="dado_lista_tabela_claro" align="left" colspan="2" bgcolor="withe"></td></tr>
						</c:if>
						<c:set var="quebraLinha" value="1" />
						<tr><td class="head_tabela" align="left" colspan="2" bgcolor="darkgray">${relInconsistencia.descricaoInconsistencia}</td></tr>
					</c:if>
					<c:if test="${(relInconsistencia.nome != enquadramentoAnterior) || ((relInconsistencia.nome == enquadramentoAnterior) && (relInconsistencia.descricaoInconsistencia != inconsistenciaAnterior))}">
						<tr><td class="head_tabela" align="left" colspan="2" bgcolor="darkgray">${relInconsistencia.nome}</td></tr>
					</c:if>
					
					<tr align="center" >
						<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						<td class="${css_td}" align="center" width="40%"><fmt:formatDate pattern="dd/MM/yyyy" value="${relInconsistencia.data}" type="date" /></td>
						<td class="${css_td}" align="center" width="40%">${relInconsistencia.total}</td>
					</tr>
					<c:set var="inconsistenciaAnterior" value="${relInconsistencia.descricaoInconsistencia}" />
					<c:set var="enquadramentoAnterior" value="${relInconsistencia.nome}" />
				</c:forEach>
				<tr align="center" >
					<th class="head_tabela" align="center" colspan="2" width="100%">Total</th>
				</tr>
				<tr align="center" >
					<td class="${css_td}" align="center" colspan="2" width="100%">${totalInconsistencia}</td>
				</tr>
		</table>
		</td>
	</tr>
</table>
</c:if>

<script type="text/javascript">
	ajustaDatas();
</script>

<%@ include file="/includes/rodape.jsp" %>

