<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.consilux.model.Enquadramento"%>

<%@page import="com.consilux.model.Processo"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.Enquadramento"%>
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
		div_conta_registros.innerHTML = 0;
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


    function mostraImagem(idInfracao) {
        window.open("/infracao/infracao_imagem.jsp?id_infracao="+idInfracao+"&encadeado", "Imagem","width=750, height=660");
	}

    function encadeiaInfracaoImagem(idInfracaoAtual, idInfracaoAnterior) {
		var link_imagem_anterior = document.getElementById("link_imagem_"+idInfracaoAnterior);
        var link_imagem_atual = document.getElementById("link_imagem_"+idInfracaoAtual);

        if (link_imagem_anterior) { 
            link_imagem_anterior.idInfracaoProximo = idInfracaoAtual;
            link_imagem_atual.idInfracaoAnterior = idInfracaoAnterior;
        }
        link_imagem_atual.idInfracao = idInfracaoAtual;
	}
    
</script>
<body onunload="infracoesPreSelecionadas()">
<br />
<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
			<form name="frm_cadastrar_filtro" action="/ferramenta/CadastrarFiltroServlet" method="get">
	
			<table class="tabela_branca" width="1000">
				<tr>
					<th class="head_tabela" width="100%" colspan="6">Paramentros
					para filtrar a entrada da imagem:</t>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 1ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" colspan="4" width="50%">&nbsp;Nome do Filtro</td>
					<td class="label_campo" width="25%">&nbsp;Enquadramento</td>
					<td class="label_campo" width="25%">&nbsp;Processo</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 2ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
	
	
	
				<tr>
					<td colspan="4" class="valor_campo">
						<input ${disabled} id="txt_nome_filtro"	name="txt_nome_filtro" value="${txt_nome_filtro}" type="text" class="campo_texto" maxlength="50"/></td>
	
					<td class="valor_campo" colspan="1">
						<select	${disabled}	id="filtro_enquadramento" name="filtro_enquadramento">
						<option value="-1" selected="selected">--Todos--</option>
						<c:forEach var="enquadramento" items="${enquadramentos}">
	
							<c:if
								test="${filtro_enquadramento == enquadramento.idEnquadramento}">
								<option selected="selected"
									value="${enquadramento.idEnquadramento}">${enquadramento.idEnquadramento}
								- ${enquadramento.descricao}</option>
							</c:if>
							<c:if
								test="${filtro_enquadramento != enquadramento.idEnquadramento}">
								<option value="${enquadramento.idEnquadramento}">${enquadramento.idEnquadramento}
								- ${enquadramento.descricao}</option>
							</c:if>
	
						</c:forEach>
					</select></td>
					<td class="valor_campo">
						<select ${disabled}	id="filtro_processo" name="filtro_processo">
						<option value="-1" selected="selected">--Todos--</option>
						<c:forEach var="processo" items="${processosDe}">
	
							<c:if
								test="${filtro_processo == processo.idProcesso}">
								<option selected="selected"
									value="${processo.idProcesso}">${processo.idProcesso}
								- ${processo.nome}</option>
							</c:if>
							<c:if
								test="${filtro_processo != processo.idProcesso}">
								<option value="${processo.idProcesso}">${processo.idProcesso}
								- ${processo.nome}</option>
							</c:if>
	
						</c:forEach>
					</select></td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 3ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" colspan="3" width="40%">&nbsp;Local</td>
					<td class="label_campo" width="10%">&nbsp;Pista</td>
					<td class="label_campo">&nbsp;Classe</td>
					<td class="label_campo">&nbsp;</td>
				</tr>
	
	
				<%-- ***************************************************************** --%>
				<%-- 4ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
				<tr>
	
	
	
					<td class="valor_campo" colspan="3">
					<table class="tabela_branca" width="100%">
						<tr>
							<td class="valor_campo" width="20%"><input id="txt_local"
								${disabled}
									type="text" name="txt_local"
								class="campo_texto" maxlength="4" onblur="ajustaLocal(true)"
								value="${txt_local}" /></td>
							<td class="valor_campo" width="80%">
								<select id="sel_local" ${disabled} name="sel_local"	onchange="ajustaLocal(false)">
									<option value="0" selected="selected">--Todos--</option>
									<c:forEach var="local" items="${locais}">
										<c:if test="${txt_local == local.idLocal}">
											<option selected="selected" value="${local.idLocal}">${local.nome}</option>
										</c:if>
										<c:if test="${txt_local != local.idLocal}">
											<option value="${local.idLocal}">${local.nome}</option>
										</c:if>
									</c:forEach>
							</select></td>
						</tr>
					</table>
					</td>
					<td class="valor_campo">
						<select ${disabled}	id="sel_pista" name="pista">
							<option value="-1" ${pista == '-1' ? 'selected' : ''}>--Todas--</option>
							<option value="1" ${pista == '1' ? 'selected' : ''}>1</option>
							<option value="2" ${pista == '2' ? 'selected' : ''}>2</option>
							<option value="3" ${pista == '3' ? 'selected' : ''}>3</option>
							<option value="4" ${pista == '4' ? 'selected' : ''}>4</option>
						</select>
					</td>
					<td>
						<select id="filtro_tipo_veiculo" ${disabled} name="filtro_tipo_veiculo">
							<option value="null" selected="selected">--Todas--</option>
							<c:forEach var="classe" items="${classeVeiculo}">
								<c:if test="${filtro_tipo_veiculo == classe.idClasse}">
									<option selected="selected" value="${classe.idClasse}">${classe.descricao}</option>
								</c:if>
								<c:if test="${filtro_tipo_veiculo != classe.idClasse}">
									<option value="${classe.idClasse}">${classe.descricao}</option>
								</c:if>
							</c:forEach>
						</select>
					</td>
					<td>&nbsp;</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 5ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" colspan="4">&nbsp;Data das Imagens:</td>
					<td class="label_campo" colspan="2"></td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 4ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
	
	
	
	
				<tr>
					<td class="valor_campo" colspan="4">A partir: <input
						${disabled}
							value="${txt_data_infracao_ini}"
						id="txt_data_infracao_ini" type="text" name="txt_data_infracao_ini"
						maxlength="10" style="width: 80px"
						onblur="preenchePeriodoInfracao();"> <c:if
						test="${disabled == null}">
						<img src="/images/calendario/calendario.png" align="top"
							onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_ini'), 'dd/mm/yyyy')">
					</c:if> <input ${disabled}
							value="${txt_hora_infracao_ini}"
						id="txt_hora_infracao_ini" type="text" name="txt_hora_infracao_ini"
						class="campo_texto" maxlength="5" style="width: 40px"> <c:if
						test="${disabled == null}">
						<img src="/images/calendario/relogio.png" align="top"
							onclick="mostraHorario(this, document.getElementById('txt_hora_infracao_ini'), 'hh:mm')">
					</c:if> Até <input ${disabled}
							value="${txt_data_infracao_fim}"
						id="txt_data_infracao_fim" type="text" name="txt_data_infracao_fim"
						maxlength="10" style="width: 80px"> <c:if
						test="${disabled == null}">
						<img src="/images/calendario/calendario.png" align="top"
							onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_fim'), 'dd/mm/yyyy')">
					</c:if> <input ${disabled}
							value="${txt_hora_infracao_fim}"
						id="txt_hora_infracao_fim" type="text" name="txt_hora_infracao_fim"
						class="campo_texto" maxlength="5" style="width: 40px"> <c:if
						test="${disabled == null}">
						<img src="/images/calendario/relogio.png" align="top"
							onclick="mostraHorario(this, document.getElementById('txt_hora_infracao_fim'), 'hh:mm')">
					</c:if></td>
	
					<td class="valor_campo" colspan="2">&nbsp;</td>
				</tr>
	
	
				<tr>
					<td colspan="9">&nbsp;</td>
				</tr>
	
				<tr>
					<th class="head_tabela" width="100%" colspan="6">Parametros
					para aplicar na imagem:</th>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 6ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" width="20%">&nbsp;Válido Até:</td>
					<td class="label_campo" colspan="3">&nbsp;Inconsistência</td>
					<td class="label_campo" colspan="2">&nbsp;Colocar na espera?</td>
				</tr>
	
	
	
	
				<%-- ***************************************************************** --%>
				<%-- 7ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="valor_campo"><input id="validade_data"
						${disabled}
							value="${validade_data}" type="text"
						name="validade_data" maxlength="10" style="width: 80px"
						onblur="preenchePeriodoInfracao();"> <c:if
						test="${disabled == null}">
						<img src="/images/calendario/calendario.png" align="top"
							onclick="mostraCalendario(this, document.getElementById('validade_data'), 'dd/mm/yyyy')">
					</c:if> <input ${disabled}
							value="${validade_hora}"
						id="validade_hora" type="text" name="validade_hora"
						class="campo_texto" maxlength="5" style="width: 40px"> <c:if
						test="${disabled == null}">
						<img src="/images/calendario/relogio.png" align="top"
							onclick="mostraHorario(this, document.getElementById('validade_hora'), 'hh:mm')">
					</c:if></td>
	
	
					<td class="valor_campo" colspan="3"><select
						${disabled} 
							id="set_inconsistencia"
						name="set_inconsistencia">
						<option value="" selected="selected">--justificativa--</option>
						<c:forEach var="inconsistencia" items="${inconsistencias}">
	
							<c:if
								test="${set_inconsistencia == inconsistencia.idInconsistencia}">
								<option selected="selected"
									value="${inconsistencia.idInconsistencia}">${inconsistencia.idInconsistencia}
								- ${inconsistencia.descricao}</option>
							</c:if>
							<c:if
								test="${set_inconsistencia != inconsistencia.idInconsistencia}">
								<option value="${inconsistencia.idInconsistencia}">${inconsistencia.idInconsistencia}
								- ${inconsistencia.descricao}</option>
							</c:if>
	
						</c:forEach>
					</select></td>
					<td class="valor_campo" colspan="2">&nbsp;<input type="checkbox" value="checked" name="espera" id="cbEspera"/>&nbsp;Sim</td>
				</tr>
	
	
				<tr>
					<td colspan="6">&nbsp;</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 7,2ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" colspan="6"><c:if
						test="${desenvolvedor}">
		                    		Digite a query do filtro "... AND ( $query )", utilizando como tabela base: 'infracao_completa as i'.
		            </c:if></td>
				</tr>
	
	
	
				<%-- ***************************************************************** --%>
				<%-- 7,5ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="valor_campo" colspan="6"><c:if
						test="${desenvolvedor}">
						<textarea ${disabled} name="sql_criterio" cols="40" rows="6" />${sql_criterio}</textarea>
					</c:if></td>
				</tr>
	
	
	
	
				<%-- ***************************************************************** --%>
				<%-- 8ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
	
				<tr>
					<td colspan="10" align="right">
					<table>
						<tr>
							<td class="valor_campo" style="text-align: right;">
								
									<button onclick="frm_cadastrar_filtro.submit()">Próximo >></button>
							
							</form>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<c:if test="${desabilitar != null}">
				<table class="tabela_lista" width="800">
					<tr>
						<th class="head_tabela" width="10%">Cód. Local</th>
						<th class="head_tabela" width="40%">Local</th>
						<th class="head_tabela" width="15%">Data Infração</th>
						<th class="head_tabela" width="10%">Cód. Enquadramento</th>
						<th class="head_tabela" width="10%">Cód. Infração</th>
						<th class="head_tabela" width="12%">Imagem</th>
					</tr>
					<c:forEach var="infracao" varStatus="linhaInfo"	items="${infracoes}">
						<tr>
							<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
							<td class="${css_td}" align="center">${infracao.idLocal}</td>
							<td class="${css_td}" align="left">${infracao.local}</td>
							<td class="${css_td}" align="center"><fmt:formatDate value="${infracao.data}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
							<td class="${css_td}" align="center">${infracao.idEnquadramento}</td>
							<td class="${css_td}" align="center">${infracao.id}</td>
							<td class="${css_td}" align="center"><a	id="link_imagem_${infracao.id}" class='link_td' href="#" onclick="mostraImagem(${infracao.id})">[ver&nbsp;imagem]</a>
								<script	type="text/javascript">encadeiaInfracaoImagem('${infracao.id}', '${idInfracaoAnterior}')</script>
							</td>
						</tr>
						<c:set var="idInfracaoAnterior" value="${infracao.id}" />
					</c:forEach>
				</table>
			</c:if>
		</td>
	</tr>
</table>


<script type="text/javascript">
    document.getElementById("txt_id_infracao_ini").focus();
</script>
<%@ include file="/includes/rodape.jsp"%>