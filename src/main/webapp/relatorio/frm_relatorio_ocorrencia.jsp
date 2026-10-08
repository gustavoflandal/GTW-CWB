<%@page import="com.consilux.model.LocalVigente"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@include file="/includes/cabecalho.jsp"%>
<% 

String sEquipamento = request.getParameter("equipamento") != null ? request.getParameter("equipamento").trim() : null;
sEquipamento = sEquipamento != null && sEquipamento.length() == 0 ? null : sEquipamento;
Integer Equipamento = sEquipamento != null ? Integer.valueOf(sEquipamento) : 0;

String sIdLocal = request.getParameter("id_local") != null ? request.getParameter("id_local").trim() : null;
sIdLocal = sIdLocal != null && sIdLocal.length() == 0 ? null : sIdLocal;
Integer IdLocal = sIdLocal != null ? Integer.valueOf(sIdLocal) : 0;

String sDataInfracaoIni = request.getParameter("data_ocorrencia_ini") != null ? request.getParameter("data_ocorrencia_ini").trim() : null;
sDataInfracaoIni = sDataInfracaoIni != null && sDataInfracaoIni.length() == 0 ? null : sDataInfracaoIni;

String sHoraInfracaoIni = request.getParameter("hora_ocorrencia_ini") != null ? request.getParameter("hora_ocorrencia_ini").trim() : null;
sHoraInfracaoIni = sHoraInfracaoIni != null && sHoraInfracaoIni.length() == 0 ? null : sHoraInfracaoIni;

String sDataInfracaoFim = request.getParameter("data_ocorrencia_fim") != null ? request.getParameter("data_ocorrencia_fim").trim() : null;
sDataInfracaoFim = sDataInfracaoFim != null && sDataInfracaoFim.length() == 0 ? null : sDataInfracaoFim;

String sHoraInfracaoFim = request.getParameter("hora_ocorrencia_fim") != null ? request.getParameter("hora_ocorrencia_fim").trim() : null;
sHoraInfracaoFim = sHoraInfracaoFim != null && sHoraInfracaoFim.length() == 0 ? null : sHoraInfracaoFim;


List<LocalVigente> locais = LocalVigente.buscaLocalVigenteCAV(Equipamento);

List<LocalVigente> equipamentos = LocalVigente.buscaLocalVigenteCAVEquipamento(Equipamento, IdLocal, 0); 

%>


<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>

<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>

<script>
function ajustaLocal(porCodigo) {
	var frm_dados_ocorrencia = document.getElementById("frm_dados_ocorrencia");
    var txt_local = document.getElementById("txt_local");
    var sel_local = document.getElementById("sel_local");
    
    if (porCodigo) {
    	sel_local.selectedIndex = 0;

        for (var i=0;i<sel_local.options.length;i++) {
            if (sel_local.options[i].value == txt_local.value) {
                sel_local.selectedIndex = i;
                break;
            }
        }
    }
    else {
    	if (sel_local.selectedIndex == 0)
    		txt_local.value = "";
    	else 
        	txt_local.value = sel_local.value;
    }

    frm_dados_ocorrencia.submit();
 }
 
function ajustaLocalAlt() {
	var frm_dados = document.getElementById("frm_dados_ocorrencia");
    var txt_local = document.getElementById("txt_local");
    var sel_local = document.getElementById("sel_local");
    
    sel_local.selectedIndex = 0;

    for (var i=0;i<sel_local.options.length;i++) {
        if (sel_local.options[i].value == txt_local.value) {
            sel_local.selectedIndex = i;
            break;
        }
    }

 }
 
 function redirect(elem){
     elem.setAttribute("/relatorio/frm_relatorio_ocorrencia_action.jsp","frm_relatorio_ocorrencia_action.jsp");
     elem.submit();
}

</script>

<c:set var="locais" value="<%=locais%>" />
<c:set var="equipamentos" value="<%=equipamentos%>" />
<c:set var="equipamento" value="<%=Equipamento%>" />
<c:set var="id_local" value="<%=sIdLocal%>" />
<c:set var="data_ocorrencia_ini" value="<%=sDataInfracaoIni%>" />
<c:set var="hora_ocorrencia_ini" value="<%=sHoraInfracaoIni%>" />
<c:set var="data_ocorrencia_fim" value="<%=sDataInfracaoFim%>" />
<c:set var="hora_ocorrencia_fim" value="<%=sHoraInfracaoFim%>" />

<BR /><BR />

<center>

<form id="frm_dados_ocorrencia" name="frm_dados" action="/relatorio/frm_relatorio_ocorrencia_action.jsp" target="listar_ocorrencia" method="post">

<table class="tabela_grid" width="90%" style="height: 100%">
<thead>
  <tr>
    <th colspan="3">Relatório de Ocorrências</th>
  </tr>
</thead>
<tbody>

<tr>
    <td class="label_campo">Código Local:</td>
	<td class="valor_campo"><input id="txt_local" type="text" name="id_local" class="campo_texto" maxlength="4" onblur="ajustaLocal(true)" value="${id_local}"/></td>
    <td>
	    <select id="sel_local" name="local" onchange="ajustaLocal(false)">
			<option value="0" ${id_local == 0 ? 'selected' : ''}>--TODOS--</option>
			<c:forEach var="local" items="${locais}">
				<option value="${local.idLocal}" ${id_local == local.idLocal ? 'selected' : ''}>${local.idLocal} - ${local.tipoEquipamentoStr} - ${local.nome}</option>
			</c:forEach>
		</select>
    </td>
</tr>

<tr>
	<td class="label_campo">Data Ocorrência:</td>
	<td class="valor_campo">
		<input id="txt_data_ocorrencia_ini" type="text" name="data_ocorrencia_ini" maxlength="10" style="width: 80px" value="${data_ocorrencia_ini}"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_ocorrencia_ini'), 'dd/mm/yyyy')">
	</td>
	<td class="valor_campo">
		&nbsp;At&eacute;&nbsp;
		<input id="txt_data_ocorrencia_fim" type="text" name="data_ocorrencia_fim" maxlength="10" style="width: 80px" value="${data_ocorrencia_fim}"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_ocorrencia_fim'), 'dd/mm/yyyy')">
	</td>
</tr>

<tr>
	<td class="label_campo">Período:</td>
	<td class="valor_campo">
		<input id="txt_hora_ocorrencia_ini" type="text" name="hora_ocorrencia_ini" class="campo_texto" maxlength="5" style="width: 40px" value="${hora_ocorrencia_ini}"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_ocorrencia_ini'), 'hh:mm')">
	</td>
	<td class="valor_campo">
		&nbsp;At&eacute;&nbsp;
		<input id="txt_hora_ocorrencia_fim" type="text" name="hora_ocorrencia_fim" class="campo_texto" maxlength="5" style="width: 40px" value="${hora_ocorrencia_fim}"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_ocorrencia_fim'), 'hh:mm')">
	</td>
</tr>

<tr>
	<td class="valor_campo" colspan="3" style="width: 400px;"><BR /></td>
</tr>	

<tr>
	<td class="valor_campo" colspan="3" style="text-align: center;">
	<button id="consultar" name="consultar" onclick="frm_dados.submit()">Consultar</button>
<!-- 	<button id="consultar" name="consultar" onclick="redirect(this)">Consultar</button> -->
	</td>
</tr>

</tbody>
</table>

<table class="tabela_lista" width="90%" style="height: 100%">
	<tr style="height: 100%">
		<td class="valor_campo" colspan="3" style="height: 300px">
   			<iframe name="listar_ocorrencia" width="100%" id="frame_listar_ocorrencia" style="height: 100%"></iframe>
		</td>
	</tr>
    <tr>
        <td class="label_campo" width="15%">
            Registros encontrados:
        </td>
        <td class="valor_campo" width="85%">
            <div id="div_conta_registros">0</div>
        </td>
    </tr>
</table>

</form>

</center>

<input id="txt_local_h" type="hidden" name="id_local_h" value="${id_local}"/>

<script type="text/javascript">
     document.getElementById("sel_equipamento").focus();
</script>
<%@ include file="/includes/rodape.jsp" %>
