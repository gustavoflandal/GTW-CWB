<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%
    List<Enquadramento> enquadramentos = Enquadramento.buscaTodosEnquadramentos();

%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="java.util.List"%><script type="text/javascript" src="/js/calendario.js"></script>
<c:set var="enquadramentos" value="<%=enquadramentos%>" />
<script type="text/javascript">
    function consultar() {
        var frame_listar_isento = document.getElementById("frame_listar_isento");
        var txt_placa = document.getElementById("txt_placa");
        var sel_enquadramento = document.getElementById("sel_enquadramento");
        frame_listar_isento.src ="/ferramenta/listar_cadastro_isento_action.jsp?placa="+txt_placa.value+"&id_enquadramento="+sel_enquadramento.value;
    }   
    function verifDigitUpper(e,txt) { 
        txt.value = txt.value.toUpperCase();
    }
    function verifTecla(e) {
        if (e.keyCode == 13)
            consultar();
    }
</script>
<table class="tabela_branca" width="100%" >
	<tr>
		<td align="center">
			<table class="tabela_branca" width="100%" >
				<tr>
					<th class="head_tabela" width="100%" colspan="3">Consulta Cadastro de Isentos</th>
				</tr>
				<tr>
					<td class="label_campo" width="15%">Placa:</td>
                    <td class="label_campo" width="75%">Enquadramento:</td>
                    <td class="label_campo" width="10%">&nbsp;</td>
				</tr>
				<tr>
                    <td class="valor_campo"><input id="txt_placa" name="placa" type="text" class="campo_texto" maxlength="7" onkeyup="verifDigitUpper(event,this)" onkeypress="verifTecla(event)"/></td>
                    <td class="valor_campo">
                    	<select id="sel_enquadramento" name="id_enquadramento">
	                        <option value="0" selected="selected">--enquadramento--</option>
	                        <c:forEach var="enquadramento" items="${enquadramentos}">
	                            <option value="${enquadramento.idEnquadramento}">${enquadramento.descricao}</option>
	                        </c:forEach>
	                    </select>
                    </td>
                    <td class="valor_campo"><button onclick="consultar()">Consultar</button></td>
				</tr>
				<tr>
					<td class="valor_campo" colspan="3" width="100%" >
					   <iframe width="100%" height="300" id="frame_listar_isento"></iframe>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
    var w = (screen.width-window.outerHeight)/2;
    var h = (screen.height-window.outerWidth)/2;

    window.moveTo(w, h);
    document.getElementById("txt_placa").focus();
</script>
<%@ include file="/includes/rodape.jsp" %>  
