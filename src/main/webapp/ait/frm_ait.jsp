<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
	Boolean comObliteracao = ConfiguracaoProvider.getInstance().getComObliteracao();
	Collection<TipoRemessa> tiposRemessa = TipoRemessa.buscarTiposRemessa();
%>

<%@page import="com.consilux.model.Processo"%>
<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<%@page import="com.consilux.model.TipoRemessa"%>
<%@page import="java.util.Collection"%><br />
<c:set var="com_obliteracao" value="<%=comObliteracao%>" scope="request"/>
<script language="JavaScript">
	function verificaOpcaoObliteracao() {
		var linha_opcao_obliteracao = document.getElementById("linha_opcao_obliteracao");
		
		if ("${com_obliteracao}" != "true") {//com_obliteracao == true
			linha_opcao_obliteracao.style.display = "none";
		}
	}
</script>
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_2a_via" action="/ait/AITPDF" method="get" target="_blank">
				<table class="tabela_branca" width="500">
					<tr>
						<th class="head_tabela" width="100%" colspan="2">Emissão de AIT</th>
					</tr>
					<tr>
						<td class="label_campo" width="50%">Tipo Remesssa:</td>
                        <td class="valor_campo" width="50%">
                            <select id="sel_id_tipo_remessa" name="tipo_remessa">
                            <c:forEach var="tipoRemessa" items="<%=tiposRemessa%>">
                                <option value="${tipoRemessa.codigo}">${tipoRemessa.descricao}</option>
                            </c:forEach>
                            </select>
                        </td>
					</tr>
					<tr>
						<td class="label_campo">Série:</td>
						<td class="valor_campo"><input id="txt_auto" type="text" name="serie" class="campo_texto" maxlength="2"/></td>
					</tr>
					<tr>
						<td class="label_campo">Lista de autos de infração (separados por ',' ou ENTER):</td>
						<td class="valor_campo">
						<textarea rows="5" name="ids_auto"></textarea>
						</td>
					</tr>
					<tr id="linha_opcao_obliteracao">
                        <td class="valor_campo" colspan="3">
                        	<br>
							<input type="checkbox" name="obliteracao" value="true" style="vertical-align: middle;" checked>Com obliteração
                        </td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="2">
							<button onclick="document.getElementById('frm_2a_via').submit()">Visualizar</button><br>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="2">(necessário Acrobat Reader)</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<script language="JavaScript">
	verificaOpcaoObliteracao();
</script>

<%@ include file="/includes/rodape.jsp" %>
