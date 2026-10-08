<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_grupo" action="conf_grupo.jsp" method="post">
				<table width="400">
					<tr>
						<th width="100%" colspan="2">Formulário de Cadastro de Grupo</th>
					</tr>
			        <tr>
			            <td align="center">
			                <table width="400">
                                <tr>
                                    <td class="label_campo" width="20%">Descricao:</td>
                                    <td class="valor_campo" width="80%"><input id="txt_descricao" type="text" name="descricao" maxlength="20" value="<jsp:getProperty name="grupo" property="descricao" />"/></td>
                                </tr>
			                    <tr>
			                        <td class="box_botoes" colspan="4" width="100%">
			                            <button onclick="document.getElementById('frm_grupo').submit()">Avançar</button><br>
                                        <input type="hidden" name="id" value="<jsp:getProperty name="grupo" property="id" />">
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
