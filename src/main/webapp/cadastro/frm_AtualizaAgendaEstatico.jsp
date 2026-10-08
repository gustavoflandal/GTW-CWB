<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="com.consilux.model.ferramenta.AgendaEstatico"%>
<%@page import="com.consilux.model.LocalVigente"%>

<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@include file="/includes/cabecalho_vazio.jsp"%>
<%@include file="/includes/rodape.jsp"%>

<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>

<%
	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("grupo", ((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
	List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,4);
	if(locais.size()==0){
		locais = LocalVigente.buscaLocalVigenteCAV();
	}
    
	String sDtRef = request.getParameter("dtReferenciaItem");
	String sIdAgendaEstatico = request.getParameter("idAgendaEstatico");
	String sIdAgendaItem = request.getParameter("idAgendaItem");
	String sIdLocal = request.getParameter("idLocalItem");
	
	//divido data em partes 
	String yyyy = sDtRef.substring(0, 4);  
	String mm = sDtRef.substring(5, 7); 
	String dd = sDtRef.substring(8, 10); 
	sDtRef = dd + "/" + mm + "/" + yyyy; 
%>

<c:set var="locais" value="<%=locais%>" />
<c:set var="valDtRef" value="<%=sDtRef%>" />
<c:set var="valIdLocal" value="<%=sIdLocal%>" />
<c:set var="ValIdAgendaEstatico" value="<%=sIdAgendaEstatico%>" />
<c:set var="ValIdAgendaItem" value="<%=sIdAgendaItem%>" />


<script type="text/javascript">
	
	window.onload = initPage;
	document.getElementById("btGravar").disabled = false;
	
	function initPage(){  
		document.getElementById("txt_local").value = "${valIdLocal}";
		document.getElementById("txt_dtReferencia").value = "${valDtRef}";
		document.getElementById("txt_hora_inicio").value = "hh:mm";
		document.getElementById("txt_hora_fim").value = "hh:mm";
	   	ajustaLocal('${valIdLocal}');
	    ajustarDatas();
	}  


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
	 
	  	function desabilitarItem() {
	  		
	  		alert("Entou em desabilitarItem: " + idAgendaItem)
	  		

		}
</script>

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_cad_AtualizaEstatico" >
                <table class="tabela_branca" width="500">
					
					<tr><td>&nbsp;</td> </tr>
					
                     <tr>
                        <th class="head_tabela" width="100%" colspan="5">Processo de Medição - Equipamentos Estaticos</th>
                    </tr>
					<tr><td>&nbsp;</td> </tr>
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">ALTERAÇÃO DE ESCALA MENSAL</th>
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
                            <input id="txt_dtReferencia" type="text" name="dtReferencia"  class="campo_texto" maxlength="10" style="width: 110px"  onblur="preenchePeriodo();" disabled="disabled">
                        </td>
                    </tr>
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td class="label_campo" colspan="3">Hora Início:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_hora_inicio" type="text" name="horaInicio" class="campo_texto" maxlength="5" style="width: 50px">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo" colspan="3">Hora Fim:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_hora_fim" type="text" name="horaFim" class="campo_texto" maxlength="5" style="width: 50px">
                        </td>
                    </tr>		
                    <tr><td>&nbsp;</td></tr>		
					<tr>
						<td class="box_botoes" width="40%" colspan="4" style="text-align: left;" >
						<td><input type="button" value="Alterar Escala" 
								   id="btnAlterar" 
								   onclick="AtualizarItem()"/> 
							
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

  	function AtualizarItem() {

  		var idLocal = document.getElementById('txt_local')
  		var dtRef = document.getElementById('txt_dtReferencia')
  		var hraIni = document.getElementById('txt_hora_inicio')
  		var hraF = document.getElementById('txt_hora_fim')
  		
	  		var jqxhr = $.get('/Ferramentas/CadAgendaEstatico', {
				  acao: "desabilitarItem",
			  	  idLocal: idLocal.value,
				  dtRef: dtRef.value,
				  hraIni: hraIni.value,
				  hraF: hraF.value,
				  idAgendaAlteracao: "${ValIdAgendaEstatico}",
				  idAgendaItemAlteracao: "${ValIdAgendaItem}"
			}, function(xml){
				var erro = $("RETORNO",xml).text();
				alert(erro);
			});
		}
  	
 </script>
