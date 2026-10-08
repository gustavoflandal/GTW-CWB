<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_logradouro" action="conf_logradouro.jsp" method="post">
				<table width="400">
					<tr>
						<th width="100%" colspan="6">Formulário de Cadastro de Logradouro</th>
					</tr>
			        <tr>
			            <td align="center">
			                <table width="400">
                                <tr>
                                    <td class="label_campo" width="20%">Descrição:</td>
                                    <td class="valor_campo" width="80%" colspan="3"><input id="txt_descricao" type="text" name="descricao" maxlength="200" size="50" value="<jsp:getProperty name="logradouro" property="descricao" />"/></td>
                                </tr>
                                <tr>
                                    <td class="label_campo" width="20%">Latitude:</td>
                                    <td class="valor_campo" width="30%" colspan="2"><input id="txt_latitude" type="text" name="latitude" maxlength="100" size="50" value="<jsp:getProperty name="logradouro" property="latitude" />"/></td>
                                </tr>
                                <tr>
                                    <td class="label_campo" width="20%">Longitude:</td>
                                    <td class="valor_campo" width="30%" colspan="2"><input id="txt_longitude" type="text" name="longitude" maxlength="100" size="50" value="<jsp:getProperty name="logradouro" property="longitude" />"/></td>
                                </tr>
			                    <tr>
			                        <td class="box_botoes" colspan="4" width="100%">
			                            <button onclick="document.getElementById('frm_usuario').submit()">Avançar</button><br>
	                                       <input type="hidden" name="id" value="<jsp:getProperty name="logradouro" property="id" />">
			                        </td>
			                    </tr>
			                </table>
			            </td>
			        </tr>
				</table>
			</form>
			<table width="400">
				<tr>
					<td align="center">
						<div id="CADASTRO_ACIDENTE_PLACE_HOLDER"></div>
			            <script src="http://maps.google.com/maps?gwt=1&amp;file=api&amp;v=2"></script>
						<script src="../GtwWidgets/GtwWidgets.nocache.js"></script>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
