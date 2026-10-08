<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.ferramenta.AgendaEstatico"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@include file="/includes/cabecalho.jsp" %>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>

<%
	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("grupo", ((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
    List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,4);
    if(locais.size()==0){
    	locais = LocalVigente.buscaLocalVigenteCAV();
    }
%>

<c:set var="locais" value="<%=locais%>" />

<script type="text/javascript">
	ajustaDatas();
	document.getElementById("btGravar").disabled = false;
	
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
        if (porCodigo) {
            sel_local.selectedIndex = 0;

            for (var i=0;i<sel_local.options.length;i++) {
                if (sel_local.options[i].value == txt_local.value)
                    sel_local.selectedIndex = i;
            }
        }
        else
            txt_local.value = sel_local.value;
	 }
    function consultar() {
    	
        var frame_listar_agenda = document.getElementById("frame_listar_isento");
        var txt_local = document.getElementById("txt_local");
        var txt_dtReferencia = document.getElementById("txt_dtReferencia");
        frame_listar_agenda.src ="/cadastro/frm_listar_AgendaEstatico_action.jsp?idLocal=" + txt_local.value + "&dtReferencia="+txt_dtReferencia.value;
    }   
</script>

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_cad_AgendaEstatico" action="/Ferramentas/CadAgendaEstatico" method="Post" target="_blank">
                <table class="tabela_branca" width="500">
					
					<tr><td>&nbsp;</td> </tr>
					
                     <tr>
                        <th class="head_tabela" width="100%" colspan="5">Processo de Medição - Equipamentos Estaticos</th>
                    </tr>
					<tr><td>&nbsp;</td> </tr>
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">Cadastro de Agenda/Escala Mensal</th>
                    </tr>
                    <tr><td>&nbsp;</td> </tr>
                    
                    <tr>
                    	<td class="label_campo" colspan="3">Local:</td>
						<td class="valor_campo" width="15%"><input id="txt_local" type="text" 
												name="id_local" class="campo_texto" maxlength="4" 
												onblur="ajustaLocal(true)" value="${id_local}"/></td>
						
						<td class="valor_campo" >
							<select id="sel_local" name="local" onchange="ajustaLocal(false)">
								<option value="0" selected="selected">--local--</option>
								<c:forEach var="local" items="${locais}">
									<option value="${local.idLocal}">${local.nome}</option>
								</c:forEach>
							</select>
						</td>
                    </tr>
                    
                    <tr>
                        <td class="label_campo" colspan="3">Data Referência:</td>
                        <td class="valor_campo" colspan="2">
                            <input id="txt_dtReferencia" type="text" name="dtReferencia" class="campo_texto" maxlength="10" style="width: 110px" onblur="preenchePeriodo();">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dtReferencia'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td class="label_campo" colspan="3">Hora Início:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_hora_inicio" type="text" name="horaInicio" class="campo_texto" maxlength="5" style="width: 40px">
                            <img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_inicio'), 'hh:mm')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo" colspan="3">Hora Fim:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_hora_fim" type="text" name="horaFim" class="campo_texto" maxlength="5" style="width: 40px">
                            <img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_fim'), 'hh:mm')">
                        </td>
                    </tr>				
					<tr>
						<td class="box_botoes" width="25%" colspan="3" style="text-align: left;" >
							<button onclick="consultar()">Gravar Escala</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="3" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
	
	<tr style="height: 100%">
		<td align="center" valign="top" style="height: 100%">
	            <table class="tabela_lista" width="95%" style="height: 100%">
	           		<tr>
	            		<td class="box_botoes" style="text-align: left;">
							<button onclick="consultar()">Consultar Escala</button>
						</td>
					</tr>
					<tr>
						<td class="label_campo" style="text-align: left;" >
							<input   type="checkbox" name="ckbNaoLib" disabled="disabled">Somente Itens da Agenda NÃO Liberado</input>
						</td>
	            	</tr>
					<tr>
						<td class="valor_campo" colspan="3" width="100%" >
						   <iframe width="100%" height="300" id="frame_listar_isento"></iframe>
						</td>
					</tr>
					<tr>
						<td class="box_botoes" style="text-align: left;">
							<button onclick="consultar()" disabled="disabled">Liberar os itens Selecionados</button>
						</td>
					</tr>
			</table>
		</td>
	</tr>
</table>
