<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	List<Logradouro> logradouros = Logradouro.buscaTodosLogradouros();
%>

<%@page import="java.util.List"%>
<%@page import="com.consilux.model.Logradouro"%>

<c:set var="logradouros" value="<%=logradouros%>" />

<script type="text/javascript" language="javascript" src="../GtwWidgets/GtwWidgets.nocache.js"></script>
<script src="http://maps.google.com/maps?gwt=1&amp;file=api&amp;v=2"></script>
<div id="CADASTRO_ACIDENTE_PLACE_HOLDER"></div>
<!--<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_logradouro" action="grava_acidente.jsp" method="post">
				<table width="600">
					<tr>
						<th width="100%" colspan="6">Formulário de Cadastro de Acidente</th>
					</tr>
			        <tr>
			            <td align="center">
			                <table width="600">
                                <tr>
                                    <td class="label_campo" width="30%">Logradouro:</td>
                                    <td class="valor_campo" width="70%">
				                        <select id="sel_logradouro" name="idLogradouro">
					                        <c:forEach var="logradouro" items="${logradouros}">
					                            <option value="${logradouro.id}">${logradouro.descricao}</option>
					                        </c:forEach>
				                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label_campo">Tipo Gravidade:</td>
                                    <td class="valor_campo">
				                        <select id="sel_tipo_gravidade" name="tipoGravidade">
				                            <option value="1">Com vítima</option>
				                            <option value="2">Sem vítima</option>
				                            <option value="3">Vítima fatal</option>
				                        </select>
									</td>
                                </tr>
                                <tr>
                                    <td class="label_campo">Tipo Iluminação:</td>
                                    <td class="valor_campo">
				                        <select id="sel_tipo_iluminacao" name="tipoIluminacao">
				                            <option value="1">Dia</option>
				                            <option value="2">Noite</option>
				                            <option value="3">Luz Artificial</option>
				                        </select>
									</td>
                                </tr>
                                <tr>
                                    <td class="label_campo">Tipo Área:</td>
                                    <td class="valor_campo">
				                        <select id="sel_tipo_area" name="tipoArea">
				                            <option value="1">Rural</option>
				                            <option value="2">Urbana</option>
				                        </select>
									</td>
                                </tr>
                                <tr>
                                    <td class="label_campo">Tipo Equipamento Segurança:</td>
                                    <td class="valor_campo">
				                        <select id="sel_tipo_equipamento_seguranca" name="tipoEquipamentoSeguranca">
				                            <option value="1" selected="selected">Sem Equipamento</option>
				                            <option value="2">Cinto</option>
				                            <option value="3">Capacete</option>
				                        </select>
									</td>
                                </tr>
                                <tr>
                                    <td class="label_campo">Sexo:</td>
                                    <td class="valor_campo">
				                        <select id="sel_sexo" name="sexo">
				                            <option value="1">Masculino</option>
				                            <option value="2">Feminino</option>
				                        </select>
									</td>
                                </tr>
                                <tr>
                                    <td class="label_campo">Faixa Etária</td>
                                    <td class="valor_campo">
				                        <select id="sel_faixa_etaria" name="faixaEtaria">
				                            <option value="1">1 - 10 anos</option>
				                            <option value="2">11 - 15 anos</option>
				                            <option value="3">16 - 20 anos</option>
				                            <option value="4">20 - 25 anos</option>
				                            <option value="5">25 - 50 anos</option>
				                            <option value="6">Maior que 50 anos</option>
				                        </select>
									</td>
                                </tr>
                                <tr>
                                    <td class="label_campo">Tipo Veículo</td>
                                    <td class="valor_campo">
				                        <select id="sel_tipo_veiculo" name="tipoVeiculo">
				                            <option value="1">Moto</option>
				                            <option value="2">Passaeio</option>
				                            <option value="3">Caminhão</option>
				                            <option value="4">Ônibus</option>
				                        </select>
									</td>
                                </tr>
                                <tr>
                                    <td class="label_campo">Tipo Acidente</td>
                                    <td class="valor_campo">
				                        <select id="sel_tipo_acidente" name="tipoAcidente">
				                            <option value="1">Atropelamento</option>
				                            <option value="2">Capotamento</option>
				                            <option value="3">Tombamento</option>
				                            <option value="4">Colisão Frontal</option>
				                            <option value="5">Colisão Traseira</option>
				                        </select>
									</td>
                                </tr>
                                <tr>
                                    <td class="label_campo" colspan="2">Decrição Geral:</td>
                                </tr>
                                <tr>
                                    <td class="valor_campo" colspan="2">
				                        <textarea id="txa_descricao_geral" name="descricaoGeral" rows="6" cols="20">
				                        </textarea> 
									</td>
                                </tr>
			                    <tr>
			                        <td class="box_botoes" colspan="2">
			                            <button onclick="document.getElementById('frm_usuario').submit()">Incluir</button><br>
	                                    <input type="hidden" name="id" value="<jsp:getProperty name="acidente" property="id" />">
			                        </td>
			                    </tr>
			                </table>
			            </td>
			        </tr>
				</table>
			</form>
		</td>
	</tr>
</table>-->
