<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_usuario" action="conf_usuario.jsp" method="post">
				<table width="400">
					<tr>
						<th width="100%" colspan="6">Formulário de Cadastro de Usuário</th>
					</tr>
			        <tr>
			            <td align="center">
			                <table width="400">
                                <tr>
                                    <td class="label_campo" width="20%">Nome:</td>
                                    <td class="valor_campo" width="80%" colspan="3"><input id="txt_nome" type="text" name="nome" maxlength="60" value="<jsp:getProperty name="usuario" property="nome" />"/></td>
                                </tr>
                                <tr>
                                    <td class="label_campo" width="20%">Email:</td>
                                    <td class="valor_campo" width="60%" colspan="2"><input id="txt_email" type="text" name="email" maxlength="50" value="<jsp:getProperty name="usuario" property="email" />"/></td>
                                    <td width="20%" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
			                    <tr>
			                        <td class="label_campo" width="20%">Usuário:</td>
			                        <td class="valor_campo" width="30%"><input type="text" id="txt_login" name="usuario" width="40%" maxlength="25" value="<jsp:getProperty name="usuario" property="usuario"/>"/></td>
                                    <td width="50%" colspan="2"></td>
			                    </tr>
			                    <tr>
			                        <td class="label_campo" width="20%">Senha:</td>
			                        <td class="valor_campo" width="30%"><input type="password" name="senha" maxlength="10" onkeypress="enter(event)" /></td>
                                    <td width="50%" colspan="2"></td>
			                    </tr>
			                    <tr>
                                    <td class="label_campo" width="20%"></td>
			                        <td class="valor_campo" width="80%" colspan="3">
			                             <input id="cb_alterar_senha" type="checkbox" name="alterarSenha">
			                             <script type="text/javascript">document.getElementById("cb_alterar_senha").checked = <jsp:getProperty name="usuario" property="alterarSenha"/></script>
			                             Alterar Senha
			                        </td>
			                    </tr>
			                    <tr>
			                        <td class="valor_campo" width="20%"></td>
                                    <td class="valor_campo" width="80%" colspan="3">
                                         <input id="cb_ativo" type="checkbox" name="ativo">
                                         <script type="text/javascript">document.getElementById("cb_ativo").checked = <jsp:getProperty name="usuario" property="ativo"/></script>
                                         Ativo
                                    </td>
			                    <tr>
			                        <td class="box_botoes" colspan="4" width="100%">
			                            <button onclick="document.getElementById('frm_usuario').submit()">Avançar</button><br>
                                        <input type="hidden" name="id" value="<jsp:getProperty name="usuario" property="id" />">
			                        </td>
			                    </tr>
			                </table>
			            </td>
			        </tr>
				</table>
			</form>
		</td>
	</tr>
</table>
