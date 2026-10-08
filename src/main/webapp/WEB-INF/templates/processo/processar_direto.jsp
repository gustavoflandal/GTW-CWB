<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%
    List<Enquadramento> enquadramentos = Enquadramento.buscaTodosEnquadramentos();
%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.Enquadramento"%>
<c:set var="enquadramentos" value="<%=enquadramentos%>" />
<br />
<br />
<br />
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">
	function preenchePeriodo(e) {
	    var txt_data_infracao_ini = document.getElementById('txt_data_infracao_ini');
	    var txt_hora_infracao_ini = document.getElementById('txt_hora_infracao_ini');
	    var txt_data_infracao_fim = document.getElementById('txt_data_infracao_fim');
	    var txt_hora_infracao_fim = document.getElementById('txt_hora_infracao_fim');
	
	    if (txt_data_infracao_ini.value != "") {
	        if (txt_hora_infracao_ini.value == "")
	        	txt_hora_infracao_ini.value = "00:00";
	        if (txt_data_infracao_fim.value == "")
	            txt_data_infracao_fim.value = txt_data_infracao_ini.value;
	        if (txt_hora_infracao_fim.value == "")
	        	txt_hora_infracao_fim.value = "23:59";
	    }
	    else {
	        txt_hora_infracao_ini.value = "";
	        txt_data_infracao_fim.value = "";
	        txt_hora_infracao_fim.value = "";
	    }
	    escondeIniciar();
	}
	function calcular() {
		var div_mens = document.getElementById("div_mens");
		var sel_processo = ${id_processo};
		var sel_enquadramento = document.getElementById("sel_enquadramento");
		var txt_data_infracao_ini = document.getElementById("txt_data_infracao_ini");
		var txt_hora_infracao_ini = document.getElementById("txt_hora_infracao_ini");
		var txt_data_infracao_fim = document.getElementById("txt_data_infracao_fim");
		var txt_hora_infracao_fim = document.getElementById("txt_hora_infracao_fim");
		var rd_consistente = document.getElementById("rd_consistente");
		var rd_inconsistente = document.getElementById("rd_inconsistente");
		
		var consistencia = rd_consistente.checked ? 1 : (rd_inconsistente.checked ? 0 : "");  
		
		div_mens.innerHTML = "<B>Calculando...</B>";
		$.get('/ajax/ContaInfracaoDisponivelUsuario', { 
	          id_processo: ${id_processo},
	          id_enquadramento: sel_enquadramento.value,
	          consistencia: consistencia,
	          data_infracao_ini: txt_data_infracao_ini.value,
	          hora_infracao_ini: txt_hora_infracao_ini.value,
	          data_infracao_fim: txt_data_infracao_fim.value,
	          hora_infracao_fim: txt_hora_infracao_fim.value
			  }, function(xml) {
				var total = $("TOTAL",xml).text();
				div_mens.innerHTML = "<B>Total: "+total+" imagens.</B>";
				liberaIniciar();
			});
	}
	function escondeIniciar() {
		var linha_botao = document.getElementById("linha_botao");
		linha_botao.style.display = "none";
	}
	function liberaIniciar() {
		var linha_botao = document.getElementById("linha_botao");
		linha_botao.style.display = "";
	}

</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_grafic" action="/processo/IniciarProcesso" method="post">
                <table class="tabela_branca" width="500">
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">Processamento por Lote</th>
                    </tr>
					<c:if test="${processos!=null}">
					</c:if>
					<tr>
                        <td class="label_campo">Enquadramento:</td>
                        <td class="valor_campo" colspan="4">
                            <select id="sel_enquadramento" name="id_enquadramento" onchange="escondeIniciar()">
                                <option value="0" selected="selected">--enquadramento--</option>
                                <c:forEach var="enquadramento" items="${enquadramentos}">
                                    <option value="${enquadramento.idEnquadramento}">${enquadramento.idEnquadramento} - ${enquadramento.descricao}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Data inicial:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_data_infracao_ini" type="text" name="data_infracao_ini" class="campo_texto" maxlength="10" style="width: 110px" onblur="preenchePeriodo();" onchange="escondeIniciar()">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_ini'), 'dd/mm/yyyy')">
                            <input id="txt_hora_infracao_ini" type="text" name="hora_infracao_ini" class="campo_texto" maxlength="8" style="width: 70px" onblur="preenchePeriodo();" onchange="escondeIniciar()">
                            <img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_infracao_ini'), 'hh:mm')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Data final:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_data_infracao_fim" type="text" name="data_infracao_fim" class="campo_texto" maxlength="10" style="width: 110px" onblur="preenchePeriodo();" onchange="escondeIniciar()">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_fim'), 'dd/mm/yyyy')">
                            <input id="txt_hora_infracao_fim" type="text" name="hora_infracao_fim" class="campo_texto" maxlength="5" style="width: 70px" onblur="preenchePeriodo();" onchange="escondeIniciar()">
                            <img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_infracao_fim'), 'hh:mm')">
                        </td>
                    </tr>
                    <tr>
						<td class="label_campo" align="left" > 
							Classificação:
						</td>
						<td class="valor_campo" colspan="4">
                            <input id="rd_consistente" name="consistencia" type="radio" value="1" onchange="escondeIniciar()" />Consistentes
                            <input id="rd_inconsistente" name="consistencia" type="radio" value="2" onchange="escondeIniciar()" />Inconsistentes
                            <input id="rd_todas" name="consistencia" type="radio" value="0" onchange="escondeIniciar()" />Todas
                        </td>
                    </tr>
					<tr>
						<td class="box_botoes" colspan="5" width="100%">
							<button id="btEnvio" onclick="calcular(); return false;">Calcular...</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="5" align="left"><div id="div_mens"></div></td>
					</tr>
					<tr id="linha_botao" style="display: none;">
						<td class="box_botoes" colspan="5" width="100%">
							<button id="btEnvio" onclick="enviar('btEnvio','frm_grafic','div_mens');">Iniciar</button>
						</td>
					</tr>
				</table>
				<input type="hidden" name="id_processo" value="${id_processo}" /> 
				<input type="hidden" name="processa_direto" value="1" /> 
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
    document.getElementById("btEnvio").disabled = false;
</script>
