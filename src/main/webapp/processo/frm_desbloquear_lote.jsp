<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@include file="/includes/cabecalho_vazio.jsp"%>

<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>

<%
	String mov_desbloquear = request.getParameter("mov_desbloquear");
	String[] sel_movimento = request.getParameterValues("sel_movimento");
%>

<c:set var="mov_desbloquear" value="<%=mov_desbloquear%>" />
<c:set var="sel_movimento" value="<%=sel_movimento%>" />

<script type="text/javascript">
	
</script>

<body onunload="window.opener.location.reload(true)">

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_dados" >
				
				<input type="hidden" id="mov_desbloquear" name="movDesbloquear" value="${mov_desbloquear}"></input>
                <input type="hidden" id="sel_movimento" name="selMovimento" value="${sel_movimento}"></input>
                
                <table class="tabela_branca" width="400">
					
					<tr><td>&nbsp;</td> </tr>
					
                    <tr>
                        <th class="head_tabela" width="100%" colspan="4">Desbloquear Lote</th>
                    </tr>
                    
                    <tr><td>&nbsp;</td> </tr>
                    
  					<tr>
                        <td class="valor_campo" colspan="4">
							<input type="checkbox" id="chk_todos_auditores" name="chkTodosAuditores" value="1" style="vertical-align: middle;">Liberar lote para todos os auditores
                        </td>
					</tr>
					
					<tr>
                        <td class="valor_campo" colspan="4">
							<input type="checkbox" id="chk_ultimo_auditor" name="chkUltimoAuditor" value="1" style="vertical-align: middle;">Liberar lote para o último auditor
                        </td>
					</tr>
                    
                    <tr><td>&nbsp;</td></tr>		

					<tr>
						<td class="valor_campo" colspan="2" style="text-align: right;">
							<button onclick="desbloquear()">Desbloquear</button>
						</td>
						<td class="valor_campo" colspan="2" style="text-align: left;">
							<button onclick="window.close()">Fechar</button>
						</td> 
					</tr>
				</table>
			</form>
			<form id="frm_desbloquear_lote" action="/processo/LiberarMovimentosLote" method="get" target="_blank">
			
				<input type="hidden" id="mov_desbloquear" name="movDesbloquear"></input>
                <input type="hidden" id="sel_movimento" name="selMovimento"></input>
                <input type="hidden" id="chk_todos_auditores" name="chkTodosAuditores"></input>
                <input type="hidden" id="chk_ultimo_auditor" name="chkUltimoAuditor"></input>
                
			</form>
		</td>
	</tr>
</table>

<script type="text/javascript">

  	function desbloquear() {

		var frm_dados = document.forms["frm_dados"];
		var frm_desbloquear_lote = document.forms["frm_desbloquear_lote"];
		
		var mov_desbloquear_set = frm_dados["mov_desbloquear"];
		var sel_movimento_set = frm_dados["sel_movimento"];
		var chk_todos_auditores_set = frm_dados["chk_todos_auditores"];
		var chk_ultimo_auditor_set = frm_dados["chk_ultimo_auditor"];

		frm_desbloquear_lote["mov_desbloquear"].value = mov_desbloquear_set.value;
		frm_desbloquear_lote["sel_movimento"].value = sel_movimento_set;
		frm_desbloquear_lote["chk_todos_auditores"].value = chk_todos_auditores_set.value;
		frm_desbloquear_lote["chk_ultimo_auditor"].value = chk_ultimo_auditor_set.value;
  		
		
  		var params = {
  			mov_desbloquear: mov_desbloquear.value,
  			sel_movimento: sel_movimento.value,
  			chkTodosAuditores: chk_todos_auditores.value,
  			chkUltimoAuditor: chk_ultimo_auditor.value
        };
  		
		$.get('/processo/LiberarMovimentosLote', params, function(xml){
		});
  	}
  	
	function desbloquearLote() {
		var frm_dados = document.forms["frm_dados"];
		var frm_desbloquear_lote = document.forms["frm_desbloquear_lote"];
		
		var mov_desbloquear_set = frm_dados["mov_desbloquear"];
		var sel_movimento_set = frm_dados["sel_movimento"];
		var chk_todos_auditores_set = frm_dados["chk_todos_auditores"];
		var chk_ultimo_auditor_set = frm_dados["chk_ultimo_auditor"];

		
		frm_desbloquear_lote["mov_desbloquear"].value = mov_desbloquear_set.value;
		frm_desbloquear_lote["sel_movimento"].value = sel_movimento_set;
		frm_desbloquear_lote["chk_todos_auditores"].value = chk_todos_auditores_set.value;
		frm_desbloquear_lote["chk_ultimo_auditor"].value = chk_ultimo_auditor_set.value;
		
		frm_desbloquear_lote.submit();
		
	}
  		
 </script>

<%@include file="/includes/rodape.jsp"%>