<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<br>
<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
			<form name="frm_cadastrar_inibicao" action="/ferramenta/CadastrarInibicao" method="post" accept-charset="ISO-8859-1">
			<table class="tabela_lista" width="800">
				<tr>
					<th class="head_tabela" width="100%" colspan="5">Inibição de Registros</t>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 1ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" colspan="4" width="70%">&nbsp;Nome para a Inibição</td>
					<td class="label_campo" width="30%">Classe</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 2ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
	
	
	
				<tr>
					<td colspan="4" class="visualiza_campo">
						&nbsp;${nome_inibicao}
					</td>
					<td class="visualiza_campo">
						${classe_veiculo}
					</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 3ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" colspan="3" width="55%">&nbsp;Local</td>
					<td class="label_campo" width="15%">Pista</td>
					<td class="label_campo">&nbsp;</td>
				</tr>
	
	
				<%-- ***************************************************************** --%>
				<%-- 4ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
				<tr>
	
	
	
					<td class="valor_campo" colspan="3">
					<table class="tabela_branca" width="100%">
						<tr>
							<td class="visualiza_campo" width="20%">
								${serie_equipamento}
							</td>
							<td class="visualiza_campo" width="80%">
								${sLocal}
							</td>
						</tr>
					</table>
					</td>
					<td class="visualiza_campo">
						${pista}
					</td>
					<td>&nbsp;</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 5ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" colspan="3">&nbsp;Data das Imagens:</td>
					<td class="label_campo" colspan="2">Período do Dia:</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 4ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
	
	
	
	
				<tr>
					<td class="visualiza_campo" colspan="3"> 
						&nbsp;${data_ini}
					Até
						${data_fim}
					</td>
					<td class="visualiza_campo" colspan="2">
						${hora_ini}
					Até
						${hora_fim}
					</td>
				</tr>
				
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
				
				<tr>
					<td class="valor_campo" colspan="5">&nbsp;Enquadramentos Selecionados:</td>
				</tr>
				<tr>
					<td class="visualiza_campo" colspan="5">
						<c:if test="${enquadramento != null}">
							&nbsp;${enquadramento.idEnquadramento} - ${enquadramento.descricao}
						</c:if>
						<c:forEach var="enquadramento" items="${enquadramentosSelecionados}">
							&nbsp;${enquadramento.idEnquadramento} - ${enquadramento.descricao}<br>
						</c:forEach>
					</td>
				</tr>
				
	
				<tr>
					<td colspan="5">&nbsp;</td>
				</tr>
	
				<tr>
					<td colspan="5" align="right">
					<table>
						<tr>
							<td class="valor_campo" style="text-align: right;">
								<button onclick="history.back(); return false;">&lt;&lt; Voltar</button>
								<c:if test="${enquadramentosSelecionados != null}">
									<button onclick="frm_cadastrar_inibicao.submit()">Confirmar</button>
								</c:if>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<input type="hidden" name="nome_inibicao" value="${nome_inibicao}">
			<input type="hidden" name="classe_veiculo" value="${cod_classe}">
			<input type="hidden" name="serie_equipamento" value="${serie_equipamento}">
			<input type="hidden" name="pista" value="${pista}">
			<input type="hidden" name="data_ini" value="${data_ini}">
			<input type="hidden" name="hora_ini" value="${hora_ini}">
			<input type="hidden" name="data_fim" value="${data_fim}">
			<input type="hidden" name="hora_fim" value="${hora_fim}">
			<c:forEach var="enquadramento" items="${enquadramentosSelecionados}">
				<input type="hidden" name="enquadramentos_selecionados" value="${enquadramento.idEnquadramento}">
			</c:forEach>
			<input type="hidden" name="confirmado" value="1">
			</form>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
