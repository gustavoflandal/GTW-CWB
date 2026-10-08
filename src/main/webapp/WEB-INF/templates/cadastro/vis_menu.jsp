<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_menu" action="${frm_action}" method="post">
				<table width="400">
					<tr>
						<th width="100%" colspan="2">Menu</th>
					</tr>
			        <tr>
			            <td align="center">
			                <table width="400">
                                <tr>
                                    <td class="label_campo" width="25%">Descricao:</td>
                                    <td class="visualiza_campo" width="75%" colspan="3"><jsp:getProperty name="menu" property="descricao" /></td>
                                </tr>
			                    <tr>
			                        <td class="box_botoes" colspan="2" width="100%">
			                            <input type="button" onclick="history.back()" value="Voltar" />&nbsp;
			                            <c:if test="${frm_action != null}">
			                                 <button onclick="document.getElementById('frm_menu').submit()">Concluir</button>
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
