<%@page import="com.consilux.model.TipoRemessaC006_C008"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="com.consilux.model.RecursosC006_C008"%>
<%@page import="java.util.Collection"%><br />
<%@include file="/includes/cabecalho.jsp" %>
<%
Collection<TipoRemessaC006_C008> tiposRemessa = TipoRemessaC006_C008.buscarTiposRemessa();
%>

<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="/js/tableToExcel.js"></script>

<script type="text/javascript">
    function consultar() {
        var frame_listar_recursos = document.getElementById("frame_listar_recursos");
        var txt_tipo_remessa = document.getElementById("sel_id_tipo_remessa").value;
        var txt_serie = document.getElementById("txt_serie");
        var txt_autos = document.getElementById("txt_autos");
        var txt_placa = document.getElementById("txt_placa");
        
        frame_listar_recursos.src ="/relatorio/frm_rel_recursos_c006_c008_action.jsp?tipoRemessa=" + txt_tipo_remessa + "&serie="+txt_serie.value + "&autos="+txt_autos.value + "&placa="+txt_placa.value;
    }
    
    function verifDigitPlaca(e,txt) {
        txt.value = txt.value.toUpperCase();
    }
    
    function verifTecla(e) {
		if (e.keyCode == 13)
        	consultar();
    }
</script>

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_rel_recursos_c006_c008">
                <table class="tabela_branca" width="400">
					<tr><td>&nbsp;</td> </tr>
                    <tr>
                    	<th class="head_tabela" width="100%" colspan="5">Recursos dos Contratos C006 e C008</th>
                    </tr>
                    <tr><td>&nbsp;</td> </tr>
                    
                    <tr>
						<td class="label_campo" colspan="2">Tipo Remesssa:</td>
                        <td class="valor_campo" width="50%">
                            <select id="sel_id_tipo_remessa" name="tipo_remessa">
                            <c:forEach var="tipoRemessa" items="<%=tiposRemessa%>">
                                <option value="${tipoRemessa.tipo}">${tipoRemessa.tipo}</option>
                            </c:forEach>
                            </select>
                        </td>
					</tr>
					
					<tr>
						<td class="label_campo" colspan="2">Série:</td>
						<td class="valor_campo" width="50%"><input id="txt_serie" type="text" name="serie" class="campo_texto" maxlength="2" onkeyup="verifDigitPlaca(event,this)"/></td>
					</tr>
					
					<tr>
						<td class="label_campo" colspan="2">Lista de autos de infração (separados por ','):</td>
						<td class="valor_campo" width="50%">
						<textarea rows="5" id="txt_autos" name="autos"></textarea>
						</td>
					</tr>
					
					<tr>
                        <td class="label_campo" colspan="2">Placa:</td>
						<td class="valor_campo" width="50%"><input id="txt_placa" type="text" name="placa" class="campo_texto" maxlength="7" onkeyup="verifDigitPlaca(event,this)" onkeypress="verifTecla(event)"/></td>
                    </tr>
                    
				</table>
			</form>
		</td>
	</tr>
	
	<tr style="height: 100%;">
		<td align="center" valign="top" style="height: 100%">
	            <table class="tabela_lista" width="600" style="height: 100%; border: none;">
   					<tr>
	            		<td class="box_botoes" colspan="2" style="text-align: right;">
							<button onclick="consultar()">Consultar</button>
						</td>
						<td class="box_botoes" width="50%" style="text-align: left;">
							<input type="button" id="btnExport" onclick="tableToExcel(document.getElementById('frame_listar_recursos').contentWindow.document.getElementById('dvData'), 'Recursos', 'Relatório de Recursos dos Contratos C006 e C008.xls')" value="Exportar para Excel" />
						</td>
					</tr>
					<tr>
						<td class="valor_campo" colspan="3" width="100%">
						   <iframe width="100%" height="180" id="frame_listar_recursos"></iframe>
						</td>
					</tr>
			</table>
		</td>
	</tr>
</table>

<script type="text/javascript">
	document.getElementById("sel_contrato").focus();
</script>

<%@ include file="/includes/rodape.jsp" %>