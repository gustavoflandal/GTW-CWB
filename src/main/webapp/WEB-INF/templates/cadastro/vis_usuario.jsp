<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_usuario" action="${frm_action}" method="post">
				<table width="400">
					<tr>
						<th width="100%" colspan="6">Usuário</th>
					</tr>
			        <tr>
			            <td align="center">
			                <table width="400">
                                <tr>
                                    <td class="label_campo" width="25%">Nome:</td>
                                    <td class="visualiza_campo" width="75%" colspan="3"><jsp:getProperty name="usuario" property="nome" /></td>
                                </tr>
                                <tr>
                                    <td class="label_campo" width="25%">Email:</td>
                                    <td class="visualiza_campo" width="55%" colspan="2"><jsp:getProperty name="usuario" property="email" /></td>
                                    <td width="20%" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
			                    <tr>
			                        <td class="label_campo" width="25%">Usuário:</td>
			                        <td class="visualiza_campo" width="25%"><jsp:getProperty name="usuario" property="usuario"/></td>
                                    <td width="50%" colspan="2"></td>
			                    </tr>
			                    <tr>
			                        <td class="label_campo" width="25%">Senha:</td>
			                        <td class="visualiza_campo" width="25%">********</td>
                                    <td width="50%" colspan="2"></td>
			                    </tr>
			                    <tr>
                                    <td class="label_campo" width="25%">Alterar Senha:</td>
			                        <td class="visualiza_campo" width="75%" colspan="3"><jsp:getProperty name="usuario" property="alterarSenhaStr" /></td>
			                    </tr>
			                    <tr>
			                        <td class="label_campo" width="25%">Ativo:</td>
                                    <td class="visualiza_campo" width="75%" colspan="3"><jsp:getProperty name="usuario" property="ativoStr" /></td>
                                </tr>
			                    <tr>
			                        <td class="box_botoes" colspan="4" width="100%">
			                            <input type="button" onclick="history.back()" value="Voltar" />&nbsp;
			                            <c:if test="${frm_action != null}">
			                                 <button onclick="document.getElementById('frm_usuario').submit()">Concluir</button>
                                        </c:if>
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
