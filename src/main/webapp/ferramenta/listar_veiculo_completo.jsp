<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%
    List<ClasseVeiculo> classes = ClasseVeiculo.buscaTodasClassesVeiculo();

    Map<String,Object> mFiltro = new HashMap<String,Object>();
    mFiltro.put("1","1");
    List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,2);
    
%>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.consilux.model.Enquadramento"%>

<%@page import="com.consilux.model.Processo"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="com.consilux.model.ClasseVeiculo"%>
<c:set var="classes" value="<%=classes%>" />
<c:set var="locais" value="<%=locais%>" />
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">
    function verifDigitPlaca(e,txt) {
        txt.value = txt.value.toUpperCase();
    }
    function verifTecla(e) {
        if (e.keyCode == 13)
            consultar();
    }
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
    function preenchePeriodo(e) {
        var txt_data_veiculo_ini = document.getElementById('txt_data_veiculo_ini');
        var txt_hora_veiculo_ini = document.getElementById('txt_hora_veiculo_ini');
        var txt_data_veiculo_fim = document.getElementById('txt_data_veiculo_fim');
        var txt_hora_veiculo_fim = document.getElementById('txt_hora_veiculo_fim');

        if (txt_data_veiculo_ini.value != "") {
            if (txt_hora_veiculo_ini.value == "")
            	txt_hora_veiculo_ini.value = "00:00";
            if (txt_data_veiculo_fim.value == "")
                txt_data_veiculo_fim.value = txt_data_veiculo_ini.value;
            if (txt_hora_veiculo_fim.value == "")
            	txt_hora_veiculo_fim.value = "23:59";
        }
        else {
            txt_hora_veiculo_ini.value = "";
            txt_data_veiculo_fim.value = "";
            txt_hora_veiculo_fim.value = "";
        }
    }
    function preencheDeAte(txt_ini, txt_fim) {
        if (txt_ini.value != "") {
            if (txt_fim.value == "")
                txt_fim.value = txt_ini.value;
        }
        else
            txt_fim.value = "";
    }
</script>
<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
            <form name="frm_listar_veiculo_completa" action="/ferramenta/listar_veiculo_completo_action.jsp" target="listar_veiculo" method="post">
	            <table class="tabela_branca" width="800">
					<tr>
						<th class="head_tabela" width="100%" colspan="8">Consulta de Veículos</th>
					</tr>
					<tr>
						<td class="label_campo" width="25%" colspan="3">Nº do Veículo</td>
	                    <td class="label_campo" width="1%" colspan=>&nbsp;</td>
	                    <td class="label_campo" width="58%" colspan="3">Data da Captura</td>
	                    <td class="label_campo" width="12%" colspan="1">&nbsp;</td>
					</tr>
					<tr>
	                    <td class="valor_campo" width="10%"><input id="txt_id_veiculo_ini" name="id_veiculo_ini" type="text" class="campo_texto" maxlength="10" onkeypress="verifTecla(event)" onblur="preencheDeAte(document.getElementById('txt_id_veiculo_ini'), document.getElementById('txt_id_veiculo_fim'))"/></td>
	                    <td class="valor_campo" width="5%" style="text-align: center;">Até</td>
	                    <td class="valor_campo" width="10%"><input id="txt_id_veiculo_fim" name="id_veiculo_fim" type="text" class="campo_texto" maxlength="10" onkeypress="verifTecla(event)"/></td>
	                    <td class="valor_campo" width="1%">&nbsp;</td>
	                    <td class="valor_campo" width="27%">
                            <input id="txt_data_veiculo_ini" type="text" name="data_veiculo_ini" maxlength="10" style="width: 80px" onblur="preenchePeriodo();"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_veiculo_ini'), 'dd/mm/yyyy')"><input id="txt_hora_veiculo_ini" type="text" name="hora_veiculo_ini" class="campo_texto" maxlength="5" style="width: 40px"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_veiculo_ini'), 'hh:mm')">
                        </td>
	                    <td class="valor_campo" width="4%" style="text-align: center;">Até</td>
                        <td class="valor_campo" width="27%">
                            <input id="txt_data_veiculo_fim" type="text" name="data_veiculo_fim" maxlength="10" style="width: 80px"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_veiculo_fim'), 'dd/mm/yyyy')"><input id="txt_hora_veiculo_fim" type="text" name="hora_veiculo_fim" class="campo_texto" maxlength="5" style="width: 40px"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_veiculo_fim'), 'hh:mm')">
                        </td>
	                    <td class="valor_campo">&nbsp;</td>
					</tr>
	                <tr>
	                    <td class="label_campo" colspan="3">Nº do Veículo Local</td>
	                    <td class="valor_campo">&nbsp;</td>
                        <td class="label_campo" colspan="3">Local</td>
                        <td class="valor_campo">&nbsp;</td>
	                </tr>
	                <tr>
	                    <td class="valor_campo"><input id="txt_id_veiculo_local_ini" name="id_veiculo_local_ini" type="text" class="campo_texto" maxlength="8" onkeypress="verifTecla(event)" onblur="preencheDeAte(document.getElementById('txt_id_veiculo_local_ini'), document.getElementById('txt_id_veiculo_local_fim'))"/></td>
	                    <td class="valor_campo" style="text-align: center;">Até</td>
	                    <td class="valor_campo"><input id="txt_id_veiculo_local_fim" name="id_veiculo_local_fim" type="text" class="campo_texto" maxlength="8" onkeypress="verifTecla(event)"/></td>
	                    <td class="valor_campo">&nbsp;</td>
                        <td class="valor_campo" colspan="3">
	                        <table class="tabela_branca" width="100%">
		                        <tr>
			                        <td class="valor_campo" width="15%"><input id="txt_local" type="text" name="id_local" class="campo_texto" maxlength="4" onblur="ajustaLocal(true)" value="${id_local}"/></td>
			                        <td class="valor_campo" width="55%">
			                            <select id="sel_local" name="local" onchange="ajustaLocal(false)">
			                                <option value="0" selected="selected">--local--</option>
			                                <c:forEach var="local" items="${locais}">
			                                    <option value="${local.idLocal}">${local.nome}</option>
			                                </c:forEach>
			                            </select>
			                        </td>
			                        <td class="valor_campo" width="12%">Pista:</td>
			                        <td class="valor_campo" width="18%">
			                            <select name="pista" multiple>
			                                <option value="0" selected>Todas</option>
			                                <option value="1">1</option>
			                                <option value="2">2</option>
			                                <option value="3">3</option>
			                                <option value="4">4</option>
			                            </select>
			                        </td>
		                        </tr>
	                        </table>
                        </td>
                        <td class="valor_campo">&nbsp;</td>
                    </tr>
                    <tr>
	                    <td class="label_campo" colspan="3">Classe</td>
	                    <td class="valor_campo">&nbsp;</td>
                        <td class="label_campo" >Placa</td>
                        <td class="label_campo" colspan="2">Tipo Imagem</td>
                        <td class="label_campo">&nbsp;</td>
                    </tr>
                    <tr>
	                    <td class="valor_campo" colspan="3">
	                        <select id="sel_classe" name="id_classe">
	                            <option value=" " selected="selected">--classe--</option>
		                        <c:forEach var="classe" items="${classes}">
		                            <option value="${classe.codigo}">${classe.descricao}</option>
		                        </c:forEach>
	                        </select>
	                    </td>
	                    <td class="valor_campo">&nbsp;</td>
                        <td class="valor_campo"><input name="placa" type="text" class="campo_texto" maxlength="7" onkeyup="verifDigitPlaca(event,this)" onkeypress="verifTecla(event)"  style="width: 70px"/></td>
                        <td class="valor_campo" colspan="3">
                        	<table class="tabela_branca" width="100%">
	                        	<tr>
	                        		<td>
			                            <input name="img_teste" type="checkbox" value="1"/>Teste
			                            <input name="img_irregular" type="checkbox" value="1"/>Irregular
			                            <input name="img_infrator" type="checkbox" value="1">Com Imagem
			                        </td>
		                        	<td class="valor_campo" style="text-align: right;"><button onclick="frm_listar_veiculo_completa.submit()">Consultar</button></td>
	                        	</tr>
                        	</table>
                        </td>
	                </tr>
                </table>
            </form>
            <table class="tabela_lista" width="95%" style="height: 70%">
				<tr style="height: 100%">
					<td class="valor_campo" colspan="3">
					   <iframe name="listar_veiculo" width="100%" id="frame_listar_veiculo" style="height: 100%"></iframe>
					</td>
				</tr>
                <tr>
                    <td class="label_campo" width="20%">
                        Registros encontrados:
                    </td>
                    <td class="valor_campo" width="60%">
                        <div id="div_conta_registros">0</div>
                    </td>
                    <td class="label_campo" width="20%">
                        (Limitado em 1000 registros)
                    </td>
                </tr>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
    document.getElementById("txt_id_veiculo_ini").focus();
</script>
<%@ include file="/includes/rodape.jsp" %>