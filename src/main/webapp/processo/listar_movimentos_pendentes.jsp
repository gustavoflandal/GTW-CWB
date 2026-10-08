<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="com.consilux.servlet.ferramentas.Agendador"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.consilux.model.Remessa"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	String sNumReg = request.getParameter("num_reg") != null ? request.getParameter("num_reg").trim() : null;
	sNumReg = sNumReg != null && sNumReg.length() == 0 ? null : sNumReg;
	Integer numReg = sNumReg != null ? Integer.valueOf(sNumReg) : 10;
	
	String sMovLote = request.getParameter("mov_lote") != null ? request.getParameter("mov_lote").trim() : null;
	sMovLote = sMovLote != null && sMovLote.length() == 0 ? null : sMovLote;
	Integer movLote = sMovLote != null ? Integer.valueOf(sMovLote) : 0;

	HashMap<Integer, Remessa> remessas_ht = Remessa.buscarRemessa(false, numReg, 2, movLote, 1);
	String data_atualizacao_str = SimpleDateFormat.getInstance().format(Calendar.getInstance().getTime());
	
	Date proxima_execucao = Agendador.ProximaExecucao(Agendador.GRUPO_PROCESSAMENTO, Agendador.JOB_PROCESSA_MOVIMENTOS_VALIDADOS);
	String proxima_execucao_str = "N/D";
	if(proxima_execucao != null)			
		proxima_execucao_str = SimpleDateFormat.getInstance().format(proxima_execucao);
	
	Map.Entry<Integer, Integer> total_infracao = Remessa.ObterTotalDisponivel();
	Integer total_lote = total_infracao.getValue();
	Integer total_validavel = total_infracao.getKey();
	Integer atual_lote = 0;
	Integer atual_validavel = 0;
	if(numReg > 0) {
		Map.Entry<Integer, Integer> atual_infracao = Remessa.ObterTotalDisponivelEm(remessas_ht);
		atual_lote = atual_infracao.getValue();
		atual_validavel = atual_infracao.getKey();
	}
%>
<c:set var="num_reg" value="<%=numReg%>" />
<c:set var="mov_lote" value="<%=movLote%>" />
<c:set var="remessas" value="<%=remessas_ht.values()%>" />
<c:set var="data_atualizacao_str" value="<%=data_atualizacao_str%>" />
<c:set var="proxima_execucao_str" value="<%=proxima_execucao_str%>" />
<c:set var="total_lote" value="<%=total_lote%>" />
<c:set var="total_validavel" value="<%=total_validavel%>" />
<c:set var="atual_lote" value="<%=atual_lote%>" />
<c:set var="atual_validavel" value="<%=atual_validavel%>" />

<link REL=StyleSheet HREF="/css/jquery-ui.css" TYPE="text/css">
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>

<style>
.ui-dialog { z-index: 1000000 !important ;}

.ui-dialog-titlebar-close {
    visibility: hidden;
}

.ui-dialog-buttonset button:not(:first-child) {
    margin-left: 10px;
}
</style>

<br/>
<div class="valor_campo" style="text-align: center;">Última atualização: ${data_atualizacao_str}
&nbsp;&nbsp;<a href="#" onclick="window.location.reload(true);">Atualizar</a>
<br/>
Próximo envio automático de Movimentos Validados: ${proxima_execucao_str}
<br/>
<form id="frm_list" action="" method="get">
<LABEL>Número de registros inicial:</LABEL>
<select id="sel_num_reg" name="num_reg" style="width: 200px" onchange="validaCombos('numReg');submit();">
   <option value="10" ${num_reg == 10 ? 'selected' : ''}>10</option>
   <option value="20" ${num_reg == 20 ? 'selected' : ''}>20</option>
   <option value="30" ${num_reg == 30 ? 'selected' : ''}>30</option>
   <option value="40" ${num_reg == 40 ? 'selected' : ''}>40</option>
   <option value="50" ${num_reg == 50 ? 'selected' : ''}>50</option>
   <option value="60" ${num_reg == 60 ? 'selected' : ''}>60</option>
   <option value="70" ${num_reg == 70 ? 'selected' : ''}>70</option>
   <option value="80" ${num_reg == 80 ? 'selected' : ''}>80</option>
   <option value="90" ${num_reg == 90 ? 'selected' : ''}>90</option>
   <option value="100" ${num_reg == 100 ? 'selected' : ''}>100</option>
   <option value="200" ${num_reg == 200 ? 'selected' : ''}>200</option>
   <option value="300" ${num_reg == 300 ? 'selected' : ''}>300</option>
   <option value="400" ${num_reg == 400 ? 'selected' : ''}>400</option>
   <option value="500" ${num_reg == 500 ? 'selected' : ''}>500</option>
   <option value="1000" ${num_reg == 1000 ? 'selected' : ''}>1000</option>
   <option value="0" ${num_reg == 0 ? 'selected' : ''}>TODOS</option>
</select>
<br/>
<LABEL>Movimentos de Lote:</LABEL>
<select id="sel_mov_lote" name="mov_lote" style="width: 246px; margin-top: 3px" onchange="validaCombos('movLote');submit();">
	<option value="0" ${mov_lote == 0 ? 'selected' : ''}>TODOS</option>
   	<option value="1" ${mov_lote == 1 ? 'selected' : ''}>DISPONÍVEIS PARA LIBERAÇÃO</option>
   	<option value="2" ${mov_lote == 2 ? 'selected' : ''}>DISPONÍVEIS PARA ENVIO AO APAIT</option>
   	<option value="3" ${mov_lote == 3 ? 'selected' : ''}>PROCESSANDO</option>
</select>
</form>
<br/>
<LABEL>Total de imagens nos lotes:</LABEL> &nbsp; ${total_validavel} / ${total_lote}
<br/>
<c:if test="${atual_lote > 0}">
<LABEL>Total de imagens nos primeiros ${num_reg} lotes: </LABEL> &nbsp; ${atual_validavel} / ${atual_lote}
</c:if>
</div>
<br/>

<form id="frm_opcoes">
<div align="center">

<button name="mov_limpar"  onclick="processa('mov_limpar'); return false;" value="1" style="background-color: #CC6600; color: #FFFFFF">Limpar Amostra do Lote</button>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<button name="mov_liberar" onclick="processa('mov_liberar'); return false;" value="1" style="background-color: #999900; color: #FFFFFF">Liberar Movimentos de Lote</button>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<button name="mov_enviar"  onclick="processa('mov_enviar'); return false;" value="1" style="background-color: #4C9900; color: #FFFFFF">Enviar Lotes Validados para o APAIT</button>
<br />
<br />
<button name="cbTudo" onclick="selTudo(this); return false;">
<c:if test="${atual_lote > 0}">
Selecionar primeiros ${num_reg} lotes 
</c:if>
<c:if test="${atual_lote == 0}">
Selecionar Todos lotes 
</c:if>
</button>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<button name="mov_bloquear" onclick="processa('mov_bloquear'); return false;" value="1">Bloquear Movimento de Lote</button>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<BUTTON name="mov_desbloquear" onclick="processa('mov_desbloquear'); return false;" value="1">Desbloquear Movimento de Lote</BUTTON>
<br/>
<br/>
</div>
</form>

<form id="frm_movimentos" action="/processo/LiberarMovimentosLote" method="get" target="_blank">
<div align="center">
<input type="hidden" name="mov_limpar"/>
<input type="hidden" name="mov_liberar"/>
<input type="hidden" name="mov_enviar"/>
<input type="hidden" name="mov_bloquear"/>
<input type="hidden" name="mov_desbloquear"/>
<input type="hidden" name="desbloquear_ultimo"/>
<TABLE class="tabela_lista">
<THEAD>
<TR>
<TH>Nº</TH>
<TH>Identificação do Lote</TH>
<TH>Revisão</TH>
<TH>Infração mais antiga</TH>
<TH>Primeira Auditoria</TH>
<TH>Bloqueado</TH>
<TH>Total de imagens do lote</TH>
<TH>Total de imagens na amostra</TH>
<TH>Total de imagens auditadas</TH>
<TH>Estado do lote</TH>
<TH>Selecionar</TH>
</TR>
</THEAD>
<c:forEach var="r" items="${remessas}" varStatus="counter">
<c:set var="css_td" value="${counter.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
<TR>
<TD class="${css_td}">${counter.count}</TD>
<TD class="${css_td}">${r.descricaoApaitAlt}</TD>
<TD class="${css_td}"><center>${r.revisao}</center></TD>
<TD class="${css_td}" style="text-align: center;">${r.dataMinInfracaoStr}</TD>
<TD class="${css_td}">${r.primeiroUsuarioValidacao}</TD>

<c:if test="${r.qtdeInfracoesJanela > 0}">
<c:if test="${r.qtdeInfracoesJanela == 1 || r.qtdeInfracoesJanela == 3}">
<TD class="${css_td}"><strong>Processando</strong><br/>(${r.nomeUsuarioJanela.trim()})</TD>
</c:if>
<c:if test="${r.qtdeInfracoesJanela == 2}">
<TD class="${css_td}"><strong>Sim</strong></TD>
</c:if>
</c:if>
<c:if test="${r.qtdeInfracoesJanela == 0}">
<TD class="${css_td}">Não</TD>
</c:if>

<TD class="${css_td}" style="text-align: center;">${r.totalInfracoesValidaveis} / ${r.totalInfracao}</TD>
<c:choose>
<c:when test="${r.tamanhoAmostraReal >= 0}">
<TD class="${css_td}" style="text-align: center;">${r.tamanhoAmostraValidavel} / ${r.tamanhoAmostraReal}</TD>
</c:when>
<c:otherwise>
<TD class="${css_td}" style="text-align: center;">N/D</TD>
</c:otherwise>
</c:choose>
<TD class="${css_td}" style="text-align: center;">${r.totalInfracoesValidadas}</TD>
<TD class="${css_td}" style="text-align: center;">${r.estadoLoteBD}</TD>
<TD class="${css_td}" style="text-align: center;"><input type="checkbox" id="movimento_${r.idRemessa}" name="sel_movimento" value="${r.idRemessa}" /></TD>
</TR>
</c:forEach>
</TABLE>
<br/>
</div>
</form>
<form id="frm_modal" hidden="hidden">
<div id="dialog">
    <div class="title">
    	<table>
    		<tr>
    			<td width="100%" colspan="2">Desbloquear Movimentos de Lote:
    			</td>
    		</tr>
    		<tr>
    			<td width="5%" colspan="1" align="right">1.</td>
    			<td width="95%" colspan="1"><strong>TODOS:</strong> libera o movimento de lote para todos os auditores.</td>
    		</tr>
    		<tr>
    			<td width="5%" colspan="1" align="right">2.</td>
    			<td width="95%" colspan="1"><strong>ÚLTIMO:</strong> libera o movimento de lote apenas para o último auditor que o validou.</td>
    		</tr>
    	</table>
    </div>
</div>
</form> 
<%@include file="/includes/rodape.jsp"%>
<script type="text/javascript">
	function selTudo(cbTudo) {
		var aChecks = document.getElementsByName("sel_movimento");
		for (i=0;i<aChecks.length;i++) {
			aChecks[i].checked = (aChecks[i].checked ? false : true);
		}
	}
	function validaCombos(sOrigem) {
		var numReg = document.getElementsByName("num_reg");
	    var numRegSel = document.getElementById("sel_num_reg").value;
	    var movLote = document.getElementsByName("mov_lote");
	    var movLoteSel = document.getElementById("sel_mov_lote").value;
	    if (sOrigem == "numReg") {
	 		if (numRegSel > 0) {
		    	document.getElementById("sel_mov_lote").value = 0;
		    }
	    }else if (sOrigem == "movLote") {
			if (movLoteSel > 0) {
		    	document.getElementById("sel_num_reg").value = 0;
		    }else if (numRegSel == 0 && movLoteSel == 0) {
		    	document.getElementById("sel_num_reg").value = 10;
	    	}
	    }
	    
	}
	function processa(sOrigem) {
		var frm_opcoes = document.forms["frm_opcoes"];
		var frm_movimentos = document.forms["frm_movimentos"];
		var mov_limpar_set = frm_opcoes["mov_limpar"];
		var mov_liberar_set = frm_opcoes["mov_liberar"];
		var mov_enviar_set = frm_opcoes["mov_enviar"];
		var mov_bloquear_set = frm_opcoes["mov_bloquear"];
		var mov_desbloquear_set = frm_opcoes["mov_desbloquear"];
		
		var sel_movimento = frm_movimentos["sel_movimento"];
		var desbloquear_ultimo = frm_movimentos["desbloquear_ultimo"];
		
		if (sOrigem.trim() == "mov_limpar") {
			frm_movimentos["mov_limpar"].value = mov_limpar_set.value;
			frm_movimentos["mov_liberar"].value = '';
			frm_movimentos["mov_enviar"].value = '';
			frm_movimentos["mov_bloquear"].value = '';
			frm_movimentos["mov_desbloquear"].value = '';
		}
		if (sOrigem.trim() == "mov_liberar") {
			frm_movimentos["mov_limpar"].value = '';
			frm_movimentos["mov_liberar"].value = mov_liberar_set.value;
			frm_movimentos["mov_enviar"].value = '';
			frm_movimentos["mov_bloquear"].value = '';
			frm_movimentos["mov_desbloquear"].value = '';
		}
		if (sOrigem.trim() == "mov_enviar") {
			frm_movimentos["mov_limpar"].value = '';
			frm_movimentos["mov_liberar"].value = '';
			frm_movimentos["mov_enviar"].value = mov_enviar_set.value;
			frm_movimentos["mov_bloquear"].value = '';
			frm_movimentos["mov_desbloquear"].value = '';
		}
		if (sOrigem.trim() == "mov_bloquear") {
			frm_movimentos["mov_limpar"].value = '';
			frm_movimentos["mov_liberar"].value = '';
			frm_movimentos["mov_enviar"].value = '';
			frm_movimentos["mov_bloquear"].value = mov_bloquear_set.value;
			frm_movimentos["mov_desbloquear"].value = '';
		}
		if (sOrigem.trim() == "mov_desbloquear") {
			frm_movimentos["mov_limpar"].value = '';
			frm_movimentos["mov_liberar"].value = '';
			frm_movimentos["mov_enviar"].value = '';
			frm_movimentos["mov_bloquear"].value = '';
			frm_movimentos["mov_desbloquear"].value = mov_desbloquear_set.value;
		}
		
		if (sOrigem.trim() == "mov_limpar") {
			if (confirm("Deseja limpar a amostra?")) {
				document.getElementById('frm_movimentos').submit();
			} else {
				return false;
			}
		} else if (sOrigem.trim() == "mov_desbloquear") {
			$( "#dialog" ).dialog({
			  	title: "Desbloquear Movimentos de Lote",
			  	resizable: false,
			    height:200,
			    width:720,
			    modal: true,
			    buttons: [
							{
								text: "TODOS",
								width: 100,
					      		click: function() {
					      			desbloquear_ultimo.value = "false";
									document.getElementById('frm_movimentos').submit();
									$( this ).dialog( "close" );
			      				}
			    			},
			    			{
								text: "ÚLTIMO",
								width: 100,
					      		click: function() {
					      			desbloquear_ultimo.value = "true";
					      			document.getElementById('frm_movimentos').submit();
					      			$( this ).dialog( "close" );
			      				}
			    			},
			    			{
								text: "CANCELAR",
								width: 100,
					      		click: function() {
			        				$( this ).dialog( "close" );
			        				return false;
			      				}
			    			}
			  		   	]});
		} else {
			document.getElementById('frm_movimentos').submit();
		}
	}
</script>