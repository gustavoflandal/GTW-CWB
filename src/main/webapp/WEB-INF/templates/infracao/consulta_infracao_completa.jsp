<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="com.consilux.model.EnquadramentoRegraInfracao"%>
<%@page import="com.consilux.model.Processo"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.beans.UsuarioBean"%>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<%@page import="com.consilux.model.Grupo"%>
<%@page import="com.consilux.model.Veiculo"%>
<%@page import="com.consilux.model.VeiculoCompletoLista"%>
<%@page import="com.consilux.model.Inconsistencia"%>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">
	var conta_infracoes = 0;

	function limparContadorRegistros() {
		var div_conta_registros = document.getElementById("div_conta_registros");
		div_conta_registros.innerHTML = 0;;
	}
		
	function adicInfracao(id_infracao) {
		var div_conta_registros = document.getElementById("div_conta_registros");
		div_conta_registros.innerHTML = ++conta_infracoes;
	}

    function verifDigitPlaca(e,txt) {
        txt.value = txt.value.toUpperCase();
    }
    function verifTecla(e) {
		var frm_listar_infracao_completa = document.getElementsByName("frm_listar_infracao_completa")[0];
        if (e.keyCode == 13)
        	frm_listar_infracao_completa.submit();
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
    function preenchePeriodo(e, txt_data_ini, txt_hora_ini, txt_data_fim, txt_hora_fim) {
        if (txt_data_ini.value != "") {
            if (txt_hora_ini.value == "")
            	txt_hora_ini.value = "00:00";
            if (txt_data_fim.value == "")
                txt_data_fim.value = txt_data_ini.value;
            if (txt_hora_fim.value == "")
            	txt_hora_fim.value = "23:59";
        }
        else {
            txt_hora_ini.value = "";
            txt_data_fim.value = "";
            txt_hora_fim.value = "";
        }
    }

    function preenchePeriodoInfracao(e) {
        var txt_data_ini = document.getElementById('txt_data_infracao_ini');
        var txt_hora_ini = document.getElementById('txt_hora_infracao_ini');
        var txt_data_fim = document.getElementById('txt_data_infracao_fim');
        var txt_hora_fim = document.getElementById('txt_hora_infracao_fim');
        
        preenchePeriodo(e, txt_data_ini, txt_hora_ini, txt_data_fim, txt_hora_fim);
    }
    
    function preenchePeriodoValidavel(e) {
        var txt_data_validavel_ini = document.getElementById('txt_data_validavel_ini');
        var txt_hora_validavel_ini = document.getElementById('txt_hora_validavel_ini');
        var txt_data_validavel_fim = document.getElementById('txt_data_validavel_fim');
        var txt_hora_validavel_fim = document.getElementById('txt_hora_validavel_fim');
        
        preenchePeriodo(e, txt_data_validavel_ini, txt_hora_validavel_ini, txt_data_validavel_fim, txt_hora_validavel_fim);
    }

    function preenchePeriodoValida(e) {
        var txt_data_valida_ini = document.getElementById('txt_data_valida_ini');
        var txt_hora_valida_ini = document.getElementById('txt_hora_valida_ini');
        var txt_data_valida_fim = document.getElementById('txt_data_valida_fim');
        var txt_hora_valida_fim = document.getElementById('txt_hora_valida_fim');
        
        preenchePeriodo(e, txt_data_valida_ini, txt_hora_valida_ini, txt_data_valida_fim, txt_hora_valida_fim);
    }
    
    function preencheDeAte(txt_ini, txt_fim) {
        if (txt_ini.value != "") {
            if (txt_fim.value == "")
                txt_fim.value = txt_ini.value;
        }
        else
            txt_fim.value = "";
    }
    function ajustaVisibilidadeJustificativaAproveitaveis() {
    	var sel_aproveitaveis = document.getElementById('sel_aproveitaveis');
		var sel_justificativa_aproveitaveis = document.getElementById('sel_justificativa_aproveitaveis');
		
    	if (sel_aproveitaveis && sel_justificativa_aproveitaveis) {
    		sel_justificativa_aproveitaveis.value = -1;
        	if (sel_aproveitaveis.value == 0) {
        		sel_justificativa_aproveitaveis.disabled = false;
        	} else {
        		sel_justificativa_aproveitaveis.disabled = true;
        	}
    	}
    }

    function ajustaVisibilidadeJustificativaValidaveis() {
    	var sel_validaveis = document.getElementById('sel_validaveis');
		var sel_justificativa_validaveis = document.getElementById('sel_justificativa_validaveis');
		
    	if (sel_validaveis && sel_justificativa_validaveis) {
    		sel_justificativa_validaveis.value = -1;
        	if (sel_validaveis.value == 0) {
        		sel_justificativa_validaveis.disabled = false;
        	} else {
        		sel_justificativa_validaveis.disabled = true;
        	}
    	}
    }

    function ajustaVisibilidadeJustificativaValidas() {
    	var sel_validas = document.getElementById('sel_validas');
		var sel_justificativa_validas = document.getElementById('sel_justificativa_validas');
		
    	if (sel_validas && sel_justificativa_validas) {
    		sel_justificativa_validas.value = -1;
        	if (sel_validas.value == 0) {
        		sel_justificativa_validas.disabled = false;
        	} else {
        		sel_justificativa_validas.disabled = true;
        	}
    	}
    }
    function aoFechar() {
    	infracoesPreSelecionadas()
    }
</script>
<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
            <form name="frm_listar_infracao_completa" action="/infracao/listar_infracao_completa_action.jsp" target="listar_infracao" onsubmit="conta_infracoes=0" method="post">
	            <table class="tabela_branca" width="1000">
					<tr>
						<th class="head_tabela" width="100%" colspan="10">Consulta de Infrações</th>
					</tr>
					
	                <%-- ***************************************************************** --%>
	                <%-- 1ª Linha (labels) --%>
	                <%-- ***************************************************************** --%>
					<tr>
						<td class="label_campo" width="25%" colspan="3">Nº da Infracão</td>
	                    <td class="label_campo" width="1%" colspan=>&nbsp;</td>
	                    <td class="label_campo" width="48%" colspan="3">Data da Infracão</td>
	                    <td class="label_campo" width="1%" colspan=>&nbsp;</td>
	                    <td class="label_campo" width="25%">Enquadramento</td>
					</tr>
					
	                <%-- ***************************************************************** --%>
	                <%-- 2ª Linha (campos) --%>
	                <%-- ***************************************************************** --%>
					<tr>
	                    <td class="valor_campo" width="10%"><input id="txt_id_infracao_ini" name="id_infracao_ini" type="text" class="campo_texto" maxlength="8" onkeypress="verifTecla(event)" onblur="preencheDeAte(document.getElementById('txt_id_infracao_ini'), document.getElementById('txt_id_infracao_fim'))"/></td>
	                    <td class="valor_campo" width="5%" style="text-align: center;">Até</td>
	                    <td class="valor_campo" width="10%"><input id="txt_id_infracao_fim" name="id_infracao_fim" type="text" class="campo_texto" maxlength="8" onkeypress="verifTecla(event)"/></td>
	                    <td class="valor_campo" width="1%">&nbsp;</td>
	                    <td class="valor_campo" width="22%">
                            <input id="txt_data_infracao_ini" type="text" name="data_infracao_ini" maxlength="10" style="width: 80px" onblur="preenchePeriodoInfracao();"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_ini'), 'dd/mm/yyyy')"><input id="txt_hora_infracao_ini" type="text" name="hora_infracao_ini" class="campo_texto" maxlength="5" style="width: 40px"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_infracao_ini'), 'hh:mm')">
                        </td>
	                    <td class="valor_campo" width="4%" style="text-align: center;">Até</td>
                        <td class="valor_campo" width="22%">
                            <input id="txt_data_infracao_fim" type="text" name="data_infracao_fim" maxlength="10" style="width: 80px"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_fim'), 'dd/mm/yyyy')"><input id="txt_hora_infracao_fim" type="text" name="hora_infracao_fim" class="campo_texto" maxlength="5" style="width: 40px"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_infracao_fim'), 'hh:mm')">
                        </td>
	                    <td class="valor_campo" width="1%">&nbsp;</td>
	                    <td class="valor_campo" width="25%">
	                        <select id="sel_enquadramento" name="id_enquadramento">
	                            <option value="0" selected="selected">--enquadramento--</option>
		                        <c:forEach var="enquadramento" items="${enquadramentos}">
		                            <option value="${enquadramento.idEnquadramento}">${enquadramento.descricao}</option>
		                        </c:forEach>
	                        </select>
	                    </td>
					</tr>
					
	                <%-- ***************************************************************** --%>
	                <%-- 3ª Linha (labels) --%>
	                <%-- ***************************************************************** --%>					
	                <tr>
	                    <td class="label_campo" colspan="3">Nº Sequencial de Registro</td>
	                    <td class="valor_campo">&nbsp;</td>
                        <td class="label_campo" colspan="3">Cód. Equip./Tipo Equip./Local</td>
                        <td class="valor_campo">&nbsp;</td>
                        <td class="label_campo" >Grupo Atuação</td>
	                </tr>

	                <%-- ***************************************************************** --%>
	                <%-- 4ª Linha (campos) --%>
	                <%-- ***************************************************************** --%>	                
	                <tr>
	                    <td class="valor_campo"><input id="txt_id_imagem_ini" name="id_imagem_ini" type="text" class="campo_texto" maxlength="8" onkeypress="verifTecla(event)" onblur="preencheDeAte(document.getElementById('txt_id_imagem_ini'), document.getElementById('txt_id_imagem_fim'))"/></td>
	                    <td class="valor_campo" style="text-align: center;">Até</td>
	                    <td class="valor_campo"><input id="txt_id_imagem_fim" name="id_imagem_fim" type="text" class="campo_texto" maxlength="8" onkeypress="verifTecla(event)"/></td>
	                    <td class="valor_campo">&nbsp;</td>
                        <td class="valor_campo" colspan="3">
	                        <table class="tabela_branca" width="100%">
		                        <tr>
		                            <td class="valor_campo" width="15%"><input id="txt_equip" type="text" name="id_equip" class="campo_texto" maxlength="4" value="${id_equip}"/></td>
		                            <td class="valor_campo" width="5%"> <input id="txt_tipo"  type="text" name="id_tipo"  class="campo_texto" maxlength="1" value="${id_tipo}"/></td>
			                        <td class="valor_campo" width="15%"><input id="txt_local" type="text" name="id_local" class="campo_texto" maxlength="4" onblur="ajustaLocal(true)" value="${id_local}"/></td>
			                        <td class="valor_campo" width="35%">
			                            <select id="sel_local" name="local" onchange="ajustaLocal(false)">
			                                <option value="0" selected="selected">--local--</option>
			                                <c:forEach var="local" items="${locais}">
			                                    <option value="${local.idLocal}">${local.nome}</option>
			                                </c:forEach>
			                            </select>
			                        </td>
			                        <td class="valor_campo" width="12%">Pista</td>
			                        <td class="valor_campo" width="18%">
			                            <select id="sel_pista" name="id_pista" multiple>
			                                <option value="0" selected="selected">Todas</option>
			                                <option value="1">1</option>
			                                <option value="2">2</option>
			                                <option value="3">3</option>
			                                <option value="4">4</option>
			                                <option value="5">5</option>
			                                <option value="6">6</option>
			                                <option value="7">7</option>
			                                <option value="8">8</option>
			                            </select>
			                        </td>
		                        </tr>
	                        </table>
                        </td>
                        <td class="valor_campo">&nbsp;</td>

                        <td class="valor_campo">
                            <select id="sel_enquadRegra" name="id_enquadRegra">
                            <option value="0" selected="selected">Todos</option>
                            <c:forEach var="enquadRegra" items="${enquadRegraInfracao}">
								<c:set var="selecionado" value="${descApait == enquadRegra.descApait ? 'selected' : ''}" />
                                <option value="${enquadRegra.descApait}" ${selecionado}>${enquadRegra.descApait}</option>
                            </c:forEach>
                            </select>
                        </td>
                    </tr>
                    
	                <%-- ***************************************************************** --%>
	                <%-- 5ª Linha (labels) --%>
	                <%-- ***************************************************************** --%>                
                    <tr>
	                    <td class="label_campo" colspan="3">Nº Movimento de Lote</td>
	                    <td class="valor_campo">&nbsp;</td>
                        <td class="label_campo" colspan="1">Placa</td>
                        <td class="label_campo" colspan="2">Tipo de problema</td>
                        <td></td>
		 				<td class="label_campo" colspan="4">Tipo de Veículo</td>
                    </tr>
                    
	                <%-- ***************************************************************** --%>
	                <%-- 6ª Linha (campos) --%>
	                <%-- ***************************************************************** --%>                    
                    <tr>
                    
                     <%--
	                    <td class="valor_campo"><input id="txt_auto_ini" name="auto_ini" type="text" class="campo_texto" maxlength="8" onkeypress="verifTecla(event)" onblur="preencheDeAte(document.getElementById('txt_auto_ini'), document.getElementById('txt_auto_fim'))"/></td>
	                    <td class="valor_campo"><input id="txt_auto_fim" name="auto_fim" type="text" class="campo_texto" maxlength="8" onkeypress="verifTecla(event)"/></td>
	                 --%>
	                 <td colspan="1" class="valor_campo"><input id="txt_movimentoLote" name="movimentoLote" type="text" class="campo_texto" maxlength="8" onkeypress="verifTecla(event)"/></td>
					 <td colspan="1" class="valor_campo"></td>
	                 <td colspan="1" class="valor_campo"></td>
	                    
	                    <td class="valor_campo">&nbsp;</td>
                        <td class="valor_campo" colspan="1"><input id="txt_placa" name="placa" type="text" class="campo_texto" maxlength="7" onkeyup="verifDigitPlaca(event,this)" onkeypress="verifTecla(event)"/></td>
                        <td class="valor_campo" colspan="2">
							<select id="sel_imagem_inconsistencia" name="imagemInconsistencia">
                               <option value="2" selected="selected">Todas</option>
                               <option value="0">Problemas Não Tecnicos</option>
							   <option value="1">Problemas Tecnicos</option>
                           </select>                        
						</td>
						<td></td>
						<td colspan="1">
							<select id="sel_tipo_veiculo" name="tipoVeiculo">
								<option value="" selected="selected">Todos</option>
<!--                                <option value="P">Veíc. Passeio</option> -->
<!--                                <option value="O">Ônibus</option> -->
<!--                                <option value="C">Caminhão</option> -->
<!--                                <option value="M">Motocicleta</option> -->
<!--                                <option value="T">Camionete</option> -->
								<c:forEach var="classeVeiculo" items="${classesVeiculos}">
									<c:set var="selecionado" value="${descricao == classeVeiculo.descricao ? 'selected' : ''}" />
                                	<option value="${classeVeiculo.idClasse}" ${selecionado}>${classeVeiculo.descricao}</option>
                            	</c:forEach>
                           </select>
						</td>
	                </tr>
	                <%-- ***************************************************************** --%>
	                <%-- 7ª Linha (labels) --%>
	                <%-- ***************************************************************** --%>
	                <tr>
						<td class="label_campo" colspan="3" hidden>Consistentes</td>
						<td hidden></td>
						<td class="label_campo" colspan="1" hidden>Justif. da Inconsistência</td>
						<td class="label_campo" colspan="2" hidden>RG do Téc. Administ.</td>
						<td hidden></td>
						<td class="label_campo" hidden>Espera?</td>
	                </tr>
	                
	                <%-- ***************************************************************** --%>
	                <%-- 8ª Linha (campos) --%>
	                <%-- ***************************************************************** --%>
					<tr>
                        <td class="valor_campo" colspan="3" hidden>
							 <select id="sel_aproveitaveis" name="aproveitaveis" onchange="ajustaVisibilidadeJustificativaAproveitaveis()">
                                <option value="3" selected="selected">--aproveitáveis--</option>
                                <option value="2">Não Processadas</option>
                                <option value="1">Verdadeiro</option>
                                <option value="0">Falso</option>
                            </select>                        
                        </td>
                        <td hidden></td>
                        <td class="valor_campo" colspan="1" hidden>
	                        <select id="sel_justificativa_aproveitaveis" name="justificativa_aproveitaveis" disabled="disabled">
	                            <option value="-1" selected="selected">--justificativa--</option>
		                        <c:forEach var="inconsistencia" items="${inconsistencias}">
		                            <option value="${inconsistencia.idInconsistencia}">${inconsistencia.descricao}</option>
		                        </c:forEach>
	                        </select>                        
                        </td>                           
                        <td class="valor_campo" colspan="2" hidden>
							<input id="txt_rg_operador_aproveitavel" name="rg_operador_aproveitavel" type="text" class="campo_texto" maxlength="10" onkeypress="verifTecla(event)"/>
                        </td>
                        <td class="valor_campo" hidden></td>
                        <td class="valor_campo" hidden><input type="checkbox" value="checked" name="espera" id="cbEspera"/>&nbsp;Sim</td>
					</tr>	                
	                
	                <%-- ***************************************************************** --%>
	                <%-- 9ª Linha (labels) --%>
	                <%-- ***************************************************************** --%>
	                <tr>
						<td class="label_campo" colspan="3" hidden>Validáveis</td>
						<td hidden></td>
						<td class="label_campo" colspan="1" hidden>Justif. da Não-Validação.</td>
						<td class="label_campo" colspan="2" hidden>De (Data/Hora)</td>
						<td hidden></td>
						<td class="label_campo" colspan="1" hidden>Até (Data/Hora)</td>
	                </tr>
	                
	                <%-- ***************************************************************** --%>
	                <%-- 10ª Linha (campos) --%>
	                <%-- ***************************************************************** --%>
					<tr>
                        <td class="valor_campo" colspan="3" hidden>
                            <select id="sel_validaveis" name="validaveis" onchange="ajustaVisibilidadeJustificativaValidaveis()">
                                <option value="3" selected="selected">--validáveis--</option>
                                <option value="2">Não Processadas</option>
                                <option value="1">Verdadeiro</option>
                                <option value="0">Falso</option>
                            </select>                        
                        </td>
                        <td hidden></td>
                        <td class="valor_campo" colspan="1" hidden>
	                        <select id="sel_justificativa_validaveis" name="justificativa_validaveis" disabled="disabled">
	                            <option value="-1" selected="selected">--justificativa--</option>
		                        <c:forEach var="inconsistencia" items="${inconsistencias}">
		                            <option value="${inconsistencia.idInconsistencia}">${inconsistencia.descricao}</option>
		                        </c:forEach>
	                        </select>
                        </td>
	                    <td class="valor_campo" width="22%" colspan="2" hidden>
                            <input id="txt_data_validavel_ini" type="text" name="data_validavel_ini" maxlength="10" style="width: 80px" onblur="preenchePeriodoValidavel();"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_validavel_ini'), 'dd/mm/yyyy')"><input id="txt_hora_validavel_ini" type="text" name="hora_validavel_ini" class="campo_texto" maxlength="5" style="width: 40px"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_validavel_ini'), 'hh:mm')">
                        </td>
                        <td hidden></td>
                        <td class="valor_campo" width="22%" hidden>
                            <input id="txt_data_validavel_fim" type="text" name="data_validavel_fim" maxlength="10" style="width: 80px"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_validavel_fim'), 'dd/mm/yyyy')"><input id="txt_hora_validavel_fim" type="text" name="hora_validavel_fim" class="campo_texto" maxlength="5" style="width: 40px"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_validavel_fim'), 'hh:mm')">
                        </td>
					</tr>		                
	                
	                <%-- ***************************************************************** --%>
	                <%-- 11ª Linha (labels) --%>
	                <%-- ***************************************************************** --%>
	                <tr>
						<td class="label_campo" colspan="3" hidden>Válidas</td>
						<td hidden></td>
						<td class="label_campo" colspan="1" hidden>Justif. da Não-Validação.</td>
						<td class="label_campo" colspan="2" hidden>De (Data/Hora)</td>
						<td hidden></td>
						<td class="label_campo" colspan="1" hidden>Até (Data/Hora)</td>
	                </tr>
	                
	                <%-- ***************************************************************** --%>
	                <%-- 12ª Linha (campos) --%>
	                <%-- ***************************************************************** --%>
	                <tr>
                        <td class="valor_campo" colspan="3" hidden>
                            <select id="sel_validas" name="validas" onchange="ajustaVisibilidadeJustificativaValidas()">
                                <option value="3" selected="selected">--válidas--</option>
                                <option value="2">Não Processadas</option>
                                <option value="1">Verdadeiro</option>
                                <option value="0">Falso</option>
                            </select>                      
                        </td>
                        <td hidden></td>
                        <td class="valor_campo" colspan="1" hidden>
	                        <select id="sel_justificativa_validas" name="justificativa_validas" disabled="disabled">
	                            <option value="-1" selected="selected">--justificativa--</option>
		                        <c:forEach var="inconsistencia" items="${inconsistencias}">
		                            <option value="${inconsistencia.idInconsistencia}">${inconsistencia.descricao}</option>
		                        </c:forEach>
	                        </select>                        
                        </td>
	                    <td class="valor_campo" width="22%" colspan="2" hidden>
                            <input id="txt_data_valida_ini" type="text" name="data_valida_ini" maxlength="10" style="width: 80px" onblur="preenchePeriodoValida();"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_valida_ini'), 'dd/mm/yyyy')"><input id="txt_hora_valida_ini" type="text" name="hora_valida_ini" class="campo_texto" maxlength="5" style="width: 40px"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_valida_ini'), 'hh:mm')">
                        </td>
                        <td hidden></td>
                        <td class="valor_campo" width="22%" hidden>
                            <input id="txt_data_valida_fim" type="text" name="data_valida_fim" maxlength="10" style="width: 80px"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_valida_fim'), 'dd/mm/yyyy')"><input id="txt_hora_valida_fim" type="text" name="hora_valida_fim" class="campo_texto" maxlength="5" style="width: 40px"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_valida_fim'), 'hh:mm')">
                        </td>
	                </tr>
	                
	                <%-- ***************************************************************** --%>
	                <%-- 13ª Linha (labels) --%>
	                <%-- ***************************************************************** --%>	                
	                <tr>
	                	<td class="label_campo" colspan="3">Cod. do Agente de Trâns.</td>
	                	<td></td>
	                	<td colspan="3">
	                		<table width="100%">
	                			<tr>
		    		            	<td class="label_campo" width="48%">Vel. Mínima (km/h)</td>
		    		            	<td width="4%"></td>
				                	<td class="label_campo" width="48%">Vel. Máxima (km/h)</td>
			                	</tr>
	                		</table>
	                	</td>
	                	<td></td>
						<td class="label_campo" colspan="2">Filtrar resultados</td>	                	
	                </tr>
	                
	                <%-- ***************************************************************** --%>
	                <%-- 14ª Linha (campos) --%>
	                <%-- ***************************************************************** --%>	       	                
					<tr>
						<td colspan="3">
							<input id="txt_rg_operador_validas" name="rg_operador_validas" type="text" class="campo_texto" maxlength="10" onkeypress="verifTecla(event)"/>
						</td>
						<td></td>
	                	<td colspan="3">
	                		<table width="100%">
	                			<tr>
		    		            	<td width="48%">
										<input id="txt_vel_min" name="vel_min" type="text" class="campo_texto" maxlength="3" onkeypress="verifTecla(event)" style="width: 30px;"/>
		    		            	</td>
		    		            	<td width="4%"></td>
				                	<td width="48%">
										<input id="txt_vel_max" name="vel_max" type="text" class="campo_texto" maxlength="3" onkeypress="verifTecla(event)" style="width: 30px;"/>
				                	</td>
			                	</tr>
	                		</table>
	                	</td>
						<td></td>
						<td class="valor_campo" colspan="2">
							<select id="sel_filtrar_resultados" name="filtrar_resultados">
                               <option value="0" selected="selected">Todos</option>							
                               <option value="1">Somente Remessas</option>
                               <option value="2">Com peso</option>
                           </select>						
						</td>
					</tr>
					
	                <%-- ***************************************************************** --%>
	                <%-- 15ª Linha (botões) --%>
	                <%-- ***************************************************************** --%>	      	
	                <tr>
                        <td colspan="4">
	                		<table width="100%">
	                			<tr>
		    		            	<td class="label_campo" width="20%">Processo</td>
		    		            	<td class="valor_campo" width="70%">
			                            <select id="sel_processo" name="id_processo">
			                            <option value="0" selected="selected">Todos</option>
			                            <c:forEach var="processo" items="${processos}">
											<c:set var="selecionado" value="${id_processo == processo.idProcesso ? 'selected' : ''}" />
			                                <option value="${processo.idProcesso}" ${selecionado}>${processo.nome}</option>
			                            </c:forEach>
			                            </select>
			                        </td>	                	
			                	</tr>
	                		</table>
	                	</td>

                        <td colspan="4">
	                		<table width="100%">
	                			<tr>
		    		            	<td class="label_campo" width="17%">Tipo Equip.</td>
		    		            	<td class="valor_campo" width="63%">
			                            <select id="sel_produto" name="id_produto">
			                            <option value="0" selected="selected">Todos</option>
			                            <c:forEach var="produto" items="${produtos}">
											<c:set var="selecionado" value="${id_produto == produto.idProduto ? 'selected' : ''}" />
			                                <option value="${produto.idProduto}" ${selecionado}>${produto.descricao}</option>
			                            </c:forEach>
			                            </select>
			                        </td>
			                        <td width="20%"></td>
			                	</tr>
	                		</table>
	                	</td>
                        
						<td colspan="2" >
							<table width="100%">
								<tr>
									<td width="20%"></td>
									<td class="valor_campo" style="text-align: right;" width="20%">
										<button onclick="frm_listar_infracao_completa.submit()">Consultar</button>
										</form>
									</td>
									<td class="valor_campo" style="text-align: right;" width="60%">
										<form name="FormExportaInfracoes" action="/relatorio/ExportarPlanilhaInfracoes" method="get">
											<button>Exportar Planilha</button>
										</form>
									</td>
								</tr>
							</table>
						</td>
	                </tr>
                </table>
            </form>
         </td>
     </tr>
     <tr style="height: 100%">
		<td align="center" valign="top" style="height: 100%">
            <table class="tabela_lista" width="95%" style="height: 100%">
				<tr>
					<td class="valor_campo" colspan="2" style="height: 100%">
					   <iframe name="listar_infracao" width="100%" id="frame_listar_infracao" style="height: 100%"></iframe>
					</td>
				</tr>
                <tr>
                    <td class="label_campo" width="20%">
                        Registros encontrados:
                    </td>
                    <td class="valor_campo" width="80%">
                        <div id="div_conta_registros">0</div>
                    </td>
                </tr>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
    document.getElementById("txt_id_infracao_ini").focus();
</script>
<%@ include file="/includes/rodape.jsp" %>