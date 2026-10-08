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
        var frame_listar_cadastro = document.getElementById("frame_listar_cadastro");
        var txt_placa = document.getElementById("txt_placa");
        var txt_marca = document.getElementById("txt_marca");
        var txt_localidade = document.getElementById("txt_localidade");
        var txt_categora = document.getElementById("txt_categora");
        var txt_especie = document.getElementById("txt_especie");
        var txt_tipo = document.getElementById("txt_tipo");
        var sel_enquadramento = document.getElementById("sel_enquadramento");
        frame_listar_cadastro.src ="/ferramenta/listar_cadastro_veiculo_action.jsp?placa="+txt_placa.value+"&marca="+txt_marca.value+"&id_enquadramento="+sel_enquadramento.value+"&categoria="+txt_categora.value+"&localidade="+txt_localidade.value+"&especie="+txt_especie.value+"&tipo="+txt_tipo.value;
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
					<th class="head_tabela" width="100%" colspan="5">Consulta Cadastro de Veículos</th>
				</tr>
				<tr>
					<td class="label_campo" width="15%">Placa:</td>
                    <td class="label_campo" width="85%">Marca:</td>
				</tr>
				<tr>
                    <td class="valor_campo" width="10%"><input id="txt_placa" name="placa" type="text" class="campo_texto" maxlength="15" onkeyup="verifDigitUpper(event,this)" onkeypress="verifTecla(event)"/></td>
                    <td class="valor_campo" width="90%"><input id="txt_marca" name="marca" type="text" class="campo_texto" maxlength="33" onkeypress="verifTecla(event)"/></td>
				</tr>

				<tr>
					<td colspan="2">
						<table class="tabela_branca" width="100%" >
							<tr>					
			                    <td class="label_campo" width="30%">Localidade:</td>
			                    <td class="label_campo" width="20%">Categoria:</td>
			                    <td class="label_campo" width="20%">Espécie:</td>
			                    <td class="label_campo" width="20%">Tipo:</td>
			                    <td class="label_campo" width="10%">&nbsp;</td>
		                    </tr>
							<tr>
			                    <td class="valor_campo" width="20%"><input id="txt_localidade" name="localidade" type="text" class="campo_texto" maxlength="30" onkeyup="verifDigitUpper(event,this)" onkeypress="verifTecla(event)"/></td>
			                    <td class="valor_campo" width="30%"><input id="txt_categora" name="categoria" type="text" class="campo_texto" maxlength="30" onkeyup="verifDigitUpper(event,this)" onkeypress="verifTecla(event)"/></td>
			                    <td class="valor_campo" width="20%"><input id="txt_especie" name="especie" type="text" class="campo_texto" maxlength="30" onkeyup="verifDigitUpper(event,this)" onkeypress="verifTecla(event)"/></td>
			                    <td class="valor_campo" width="20%"><input id="txt_tipo" name="tipo" type="text" class="campo_texto" maxlength="30" onkeyup="verifDigitUpper(event,this)" onkeypress="verifTecla(event)"/></td>
			                    <td class="valor_campo" width="10%"><button onclick="consultar()">Consultar</button></td>
							</tr>
						</table>
					</td>
	                    
				</tr>
 
 
				<tr>
					<td class="valor_campo" colspan="5" width="100%" >
					   <iframe width="100%" height="200" id="frame_listar_cadastro"></iframe>
					</td>
				</tr>
				
				
				
                <tr>
                    <td class="label_campo" width="10%">Isentos para:</td>
                    <td class="valor_campo" width="90%" colspan="4">
	                    <select id="sel_enquadramento" name="id_enquadramento">
	                        <option value="0" selected="selected">--enquadramento--</option>
	                        <c:forEach var="enquadramento" items="${enquadramentos}">
	                            <option value="${enquadramento.idEnquadramento}">${enquadramento.descricao}</option>
	                        </c:forEach>
	                    </select>
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
