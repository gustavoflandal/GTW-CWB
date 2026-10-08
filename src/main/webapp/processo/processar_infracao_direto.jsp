<%@page import="com.consilux.model.Inconsistencia"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
	List<Inconsistencia> inconsistencias = Inconsistencia.buscaTodasInconsistencias(false, true);
%>
<br />
<br />
<br />
<script type="text/javascript">
	function esconde_inconsistencia() {
		var sel_inconsistencia = document.getElementById("sel_inconsistencia");
		sel_inconsistencia.style.display = "none";
	}
	function ver_inconsistencia() {
		var sel_inconsistencia = document.getElementById("sel_inconsistencia");
		sel_inconsistencia.style.display = "";
	}
</script>
<c:set var="inconsistencias" value="<%=inconsistencias%>" scope="request"/>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_processar_infracao_direto" action="/processo/status_processa_direto.jsp" method="get">
			<table class="tabela_lista" width="400">
				<tr>
					<th class="head_tabela" width="100%" colspan="2">Processamento automático</th>
				</tr>
				<tr>
					<td class="label_campo" width="100%" colspan="2">
							<br />
                            <input id="rad_anteriores" name="tipo" type="radio" onclick="esconde_inconsistencia();" value="1"/>Confirmar a análise da empresa.
					</td>
				</tr>
				<tr>
					<td class="label_campo" width="100%" colspan="2">
                            <input id="rad_selec" name="tipo" type="radio" onclick="ver_inconsistencia();" value="2"/>Inconsistir com o código especifico.
                            <br />
                            <br />
							<select id="sel_inconsistencia" name="id_inconsistencia"
								<c:forEach var="inc" items="${inconsistencias}">
									<option value="${inc.idInconsistencia}">${inc.idInconsistencia} - ${inc.descricao}</option>
								</c:forEach>
							></select>
					</td>
				</tr>
				<tr>
					<td class="box_botoes" width="50%" style="text-align: right;">
						<button id="btCancelar" onclick="window.back(); return false;">Cancelar</button>
					</td>
					<td class="box_botoes" width="50" style="text-align: left;">
						<button id="btOk" onclick="submit();">OK</button>
					</td>
				</tr>
			</table>
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
	document.getElementById("rad_anteriores").checked = true;
	esconde_inconsistencia();
	document.getElementById("btCancelar").focus();
</script>
<%@ include file="/includes/rodape.jsp" %>