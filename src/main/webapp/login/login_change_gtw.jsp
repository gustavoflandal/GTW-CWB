<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%@ include file="/includes/cabecalho.jsp" %>	
<br />
<br />
<br />
<form id="frm_login" action="/login/login_change_gtw_action.jsp" method="post">
	<table class="tabela_branca" width="100%">
		<tr>
			<td align="center">
				<table class="tabela_branca" width="250">
					<tr>
						<td class="label_campo" width="40%">Senha Antiga:</td>
						<td class="valor_campo" width="60%"><input type="password" id="txt_senha_ant" name="senha_ant" class="campo_texto" maxlength="20" onkeypress="return enter(event)" autocomplete="off"/></td>
					</tr>
					<tr>
						<td class="label_campo" width="40%">Senha Nova:</td>
						<td class="valor_campo" width="60%"><input type="password" id="txt_senha_nova" name="senha_nova" class="campo_texto" maxlength="10" onkeypress="return enter(event)" autocomplete="off"/></td>
					</tr>
					<tr>
						<td class="label_campo" width="40%">Confirma:</td>
						<td class="valor_campo" width="60%"><input type="password" id="txt_senha_confirma" name="senha_confirma" class="campo_texto" maxlength="10" onkeypress="return enter(event)" autocomplete="off"/></td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="2" width="100%">
							<button onclick="document.getElementById('frm_login').submit()">Alterar</button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</form>
<script type="text/javascript">
	var txt_senha_ant = document.getElementById("txt_senha_ant");
	var txt_senha_nova = document.getElementById("txt_senha_nova");
	var txt_senha_confirma = document.getElementById("txt_senha_confirma");
	
	function enter(e) {
		if (e.keyCode == 13) {
			if (txt_senha_ant.value == "")
				txt_senha_ant.focus();
			else if (txt_senha_nova.value == "")
				txt_senha_nova.focus();
			else if (txt_senha_confirma.value == "")
				txt_senha_confirma.focus();
			else	
				document.getElementById("frm_login").submit();
			return false;
		}
	}

	txt_senha_ant.focus();	
</script>
<%@ include file="/includes/rodape.jsp" %>	
