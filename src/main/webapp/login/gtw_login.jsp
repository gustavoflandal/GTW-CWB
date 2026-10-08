<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%
	//Finalizando uma possível sessão ativa:
	session.invalidate();

	String retUrl = request.getParameter("p") != null ? request.getParameter("p") : "";
	try {
		retUrl = URLEncoder.encode(retUrl, "UTF-8");
	}
	catch(Exception e) { }
//	Date date = ConfiguracaoEquipamento.buscaDataModificacaoConfigEquip( 840 );
%>
<%@ include file="/includes/cabecalho.jsp" %>	
<br />
<br />
<br />
<form id="frm_login" action="/login/login_action.jsp" method="post" name="frm_login">
	<table class="tabela_branca" width="100%">
		<tr>
			<td align="center">
				<table class="tabela_branca" width="200">
					<tr>
						<td class="label_campo" width="40%">Usuário:</td>
						<td class="valor_campo" width="60%"><input type="text" id="txt_login" name="login" class="campo_texto" width="40%" maxlength="25" onkeypress="return enter(event)" /></td>
					</tr>
					<tr>
						<td class="label_campo" width="40%">Senha:</td>
						<td class="valor_campo" width="60%"><input type="password" id="txt_senha" name="senha" class="campo_texto" maxlength="10" onkeypress="return enter(event)" /></td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="2" width="100%">
							<button name="btn_entrar" onclick="document.getElementById('frm_login').submit()">Entrar</button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="texto_pequeno" colspan="2" align="center" width="100%">
				<br>
				<br>
				Melhor visualizado no Firefox 2.0 ou superior.
			</td>
		</tr>
	</table>
	<input type="hidden" name="retUrl" value="<%=retUrl%>"/>
</form>
<script type="text/javascript">
	var txt_login = document.getElementById("txt_login");
	var txt_senha = document.getElementById("txt_senha");
	nome_browser = navigator.userAgent.toLowerCase();

	if (nome_browser.indexOf("firefox") == -1) {
		alert("O GTW pode não ser compatível com este browser.")
	}

	function enter(e) {
		if (e.keyCode == 13) {
			if (txt_login.value == "")
				txt_login.focus();
			else if (txt_senha.value == "")
				txt_senha.focus();
			else	
				document.getElementById("frm_login").submit();
			return false;
		}
	}

	document.getElementById("txt_login").focus();	
</script>
<%@ include file="/includes/rodape.jsp" %>	
