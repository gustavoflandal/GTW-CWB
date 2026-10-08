<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%
	String sIdLocal = request.getParameter("id_local");
	
	if (sIdLocal != null && !Pattern.matches("[1-9][0-9]{0,7}",sIdLocal)) {
	    new Mensagem(response).showErro("Identificador de local enviado inválido!");
	    return;
	}
	
    Integer idLocal = 0;
	if (sIdLocal != null)
	    idLocal = Integer.valueOf(sIdLocal);
	
	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("grupo",((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
	List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,2);
%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.LocalVigente"%>

<%@page import="com.consilux.model.Enquadramento"%><c:set var="id_local" value="<%=idLocal%>" />
<c:set var="locais" value="<%=locais%>" />
<br />
<br />
<br />
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">
	function ajustaLocal(porCodigo) {
		var txt_local = document.getElementById("txt_local");
		var sel_local = document.getElementById("sel_local");
		if (porCodigo) {
			sel_local.selectedIndex = 0;

			for (var i=0;i<sel_local.options.length;i++) {
				if (sel_local.options[i].value == txt_local.value)
					sel_local.selectedIndex = i;
			}
		}
		else
			txt_local.value = sel_local.value;
	}
	function ajustaDatas() {
		var txt_dataini = document.getElementById("txt_dataini");
		var txt_horaini = document.getElementById("txt_horaini");
		var txt_datafim = document.getElementById("txt_datafim");
		var txt_horafim = document.getElementById("txt_horafim");
		var dtAgora = new Date();
		
		if (txt_dataini.value == "") {
			txt_dataini.value = adicZeroEsquerda(dtAgora.getDate(),2)+"/"+adicZeroEsquerda(dtAgora.getMonth()+1,2)+"/"+dtAgora.getFullYear();
		}
		if (txt_horaini.value == "") {
			txt_horaini.value = "00:00";
		}
		if (txt_datafim.value == "") {
			txt_datafim.value = txt_dataini.value;
		}
		if (txt_horafim.value == "") {
			txt_horafim.value = "23:59";
		}
	}
</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_veiculo_qualquer" action="/ferramentas/ListarVeiculosQualquer" method="get" target="_blank">
                <table class="tabela_branca" width="500">
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">Busca de Veículos</th>
                    </tr>
                    <tr>
                        <td class="label_campo" width="25%">Local:</td>
                        <td class="valor_campo" width="10%"><input id="txt_local" type="text" name="id_local" class="campo_texto" maxlength="4" onblur="ajustaLocal(true)" value="${id_local}"/></td>
                        <td class="valor_campo" width="45%">
                            <select id="sel_local" name="local" onchange="ajustaLocal(false)">
                                <option value="0" selected="selected">--local--</option>
                                <c:forEach var="local" items="${locais}">
                                    <option value="${local.idLocal}">${local.nome}</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td class="valor_campo" width="7%">Pista:</td>
                        <td class="valor_campo" width="13%">
                            <select name="pista">
                                <option value="0" selected="selected">Todas</option>
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Data inicial:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_dataini" type="text" name="data_veiculo_ini" class="campo_texto" maxlength="10" style="width: 110px">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataini'), 'dd/mm/yyyy')">
                            <input id="txt_horaini" type="text" name="hora_veiculo_ini" class="campo_texto" maxlength="8" style="width: 70px">
                            <img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_horaini'), 'hh:mm')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Data final:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_datafim" type="text" name="data_veiculo_fim" class="campo_texto" maxlength="10" style="width: 110px">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_datafim'), 'dd/mm/yyyy')">
                            <input id="txt_horafim" type="text" name="hora_veiculo_fim" class="campo_texto" maxlength="5" style="width: 70px">
                            <img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_horafim'), 'hh:mm')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Placa:</td>
                        <td class="valor_campo" colspan="4"><input id="txt_placa" type="text" name="placa" class="campo_texto" maxlength="7" style="width: 110px" /></td>
                    </tr>
                    <tr>
						<td class="label_campo" align="left" > 
							Tipo de consulta:
						</td>
						<td class="valor_campo" colspan="4">
                            <input name="tipo_consulta" type="radio" value="all" checked="yes" style="vertical-align: middle;"/>Todos veículos
                            <input name="tipo_consulta" type="radio" value="mon" style="vertical-align: middle;"/>Veículos monitorados
                        </td>
                    </tr>
                    <tr>
						<td class="label_campo" align="left" > 
							Formado:
						</td>
						<td class="valor_campo" colspan="4">
                            <input name="formato" type="radio" value="pdf" checked="yes" style="vertical-align: middle;"/>pdf
                            <input name="formato" type="radio" value="xls" style="vertical-align: middle;"/>Excel (xls)
                        </td>
                    </tr>

					<tr>
						<td class="box_botoes" colspan="5" width="100%">
							<button id="btEnvio" onclick="document.getElementById('frm_veiculo_qualquer').submit(),'div_mens');">Visualizar</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="5" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
	ajustaLocal(true);
	ajustaDatas();
    document.getElementById("btEnvio").disabled = false;
</script>
<%@ include file="/includes/rodape.jsp" %>
