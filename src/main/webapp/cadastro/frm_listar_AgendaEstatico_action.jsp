<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="com.consilux.model.ferramenta.AgendaEstatico"%>


<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@include file="/includes/cabecalho_vazio.jsp"%>
<%@include file="/includes/rodape.jsp"%>

<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>

<%
	String sLocal = request.getParameter("idLocal");
	if (sLocal == null || !Pattern.matches("[0-9]{1,8}", sLocal)) {
		new MensagemJS(response).showErro("Local selecionado inválido!");
	}
	
	String sDtRef = request.getParameter("dtReferencia");
	if (sDtRef == null) {
		new MensagemJS(response).showErro("Data de referência inválida!");
	}
	
	ArrayList listAgenda = new ArrayList();
    AgendaEstatico agenda = new AgendaEstatico(); 
    listAgenda = agenda.consultaEscala(sLocal, sDtRef);    
%>

<c:set var="agenda" value="<%=listAgenda%>" />

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="100%">
				<tr>
					<th class="head_tabela" width="5%">Liberar</th>
					<th class="head_tabela" width="5%">Cod. Local</th>
					<th class="head_tabela" width="40%">Nome Local</th>
					<th class="head_tabela" width="7%">Data Referência</th>
					<th class="head_tabela" width="7%">Data Operação</th>
					<th class="head_tabela" width="6%">Hora Inicio</th>
					<th class="head_tabela" width="6%">Hora Fim</th>
					<th class="head_tabela" width="6%">Status</th>
					<th class="head_tabela" width="15%">Operador de Cadastro</th>
					<th class="head_tabela" width="9%">Alteração</th>
				</tr>
				<c:set var="contaRegistros" value="0" />
				<c:forEach var="agenda" varStatus="linhaInfo" items="${agenda}">
					<tr>
						<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						
						<c:set var="k" value="$(k+1)"/>  
        				<td><input type="checkbox" name="chkValidacao" id="$k" 
        						   value="${agenda.agendaItem.hraInicio}" onclick="obterMarcados()"
        						   disabled="disabled"></td>  
						<td class="${css_td}" align="center">${agenda.idLocal}</td>
						<td class="${css_td}" align="left">${agenda.deescricao}</td>
						<td class="${css_td}" align="center"><fmt:formatDate value="${agenda.dtReferencia}" type="Date" pattern="dd/MM/yyyy" /></td>
						<td class="${css_td}" align="center"><fmt:formatDate value="${agenda.agendaItem.dtOperacao}" type="Date" pattern="dd/MM/yyyy" /></td>
						<td class="${css_td}" align="center"><fmt:formatDate value="${agenda.agendaItem.hraInicio}" type="time" /></td>
						<td class="${css_td}" align="center"><fmt:formatDate value="${agenda.agendaItem.hraFim}" type="time" /></td>
						<td class="${agenda.agendaItem.status == 0 ? 'linhaInativo' : 'linhaAtivo'}" align="left">${agenda.agendaItem.statusDesc} </td>
						<td class="${css_td}" align="left">${agenda.operador}</td>
						<td><input type="button"  
								   id="btnAlterar" 
								   value="${agenda.agendaItem.status == 1 ? 'ALTERAR' : 'INATIVADO'}"
								   <c:if test="${agenda.agendaItem.status == 0}">
        	            				<c:out value="disabled='disabled' class='linha'"/>
								   </c:if>
								   onclick="AtualizarItem('${agenda.idAgendaEstatico}', 
								   						  '${agenda.dtReferencia}', 
								   						  '${agenda.idLocal}', 
								   						  '${agenda.agendaItem.idAgendaEstaticoItem}')"/>
								   						   
						<c:set var="contaRegistros" value="${linhaInfo.count}" />
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
</table>

<script type="text/javascript">

	var div_conta_registros = parent.document
			.getElementById("div_conta_registros");
	if (div_conta_registros)
		div_conta_registros.innerHTML = '${contaRegistros}';
		
	// essa função recebe o nome "comun" ao checkboxes e a quantidade dos mesmos  
    function verificar( nome, quantidade ) {  
          
        saida = "Os checkboxes checados são:";  
          
        // itera baseado na quantidade de elementos  
        for ( i = 0; i < quantidade; i++ ) {  
  
            // obtém cada elemento pelo id  
            checkBox = document.getElementById( nome + ( i + 1 ) );  
            // se o checkbox estiver marcado, adiciona mais uma linha na string de saida.  
            if ( checkBox.checked ) {  
                saida += "\n" + checkBox.value;  
            }  
        }  
        // mostra a saída  
        alert( saida );  
    }  
	
    function obterMarcados() {  
    	  var listaMarcados = document.getElementsByTagName("INPUT");  
    	  for (loop = 0; loop < listaMarcados.length; loop++) {  
    	     var item = listaMarcados[loop];  
    	     if (item.type == "checkboxValidacao" && item.checked) {  
    	       alert(item.value);  
    	     }  
    	  }  
    	} 

  function desativarItem(id, dtRef, hraIni, hraFim) {  
	  
	alert(id + " * " + dtRef + " * " + hraIni + " * " + hraFim);  	  

       new Ajax.Request('frm_cadAgendaEstatico.jsp', {  
           method: 'post',  
           parameters: {  
         	  dtReferenciaItem: dtRef,  
         	  idAgendaEstaticoItem: id
           },  
           onComplete: function(transport) {                          
               alert("Hi")  
           },
           onFailure: function(transport) {                          
               alert("Hi")  
           } 
       });      
   }  
  	
  	function AtualizarItem(idAgenda, dtRef, idLocal, idAgendaItem) {
	window.open("/cadastro/frm_AtualizaAgendaEstatico.jsp?idAgendaEstatico="+idAgenda+
														  "&dtReferenciaItem="+dtRef+
														  "&idLocalItem="+idLocal+
														  "&idAgendaItem="+idAgendaItem,
														  "","width=550, height=300");
	}
</script>

<style> 
	.linhaAtivo{ 
		background-color:green; 
		text-align: center;
		color: white;
	} 
	
	.linhaInativo{ 
		background-color:red; 
		text-align: center;
		color: white;
	} 
</style>
