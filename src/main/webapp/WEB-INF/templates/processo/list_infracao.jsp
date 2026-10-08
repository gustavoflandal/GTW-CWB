<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<br />
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
	}
	function selTudo(cbTudo) {
		var aChecks = document.getElementsByName("sel_infracao");
		for (i=0;i<aChecks.length;i++) {
			aChecks[i].checked = cbTudo.checked;
		}
	}

	function selPerc(cbPerc) {
		var aChecks = document.getElementsByName("sel_infracao");
		var txt_percSelecao = document.getElementById("txt_percSelecao");

		var fatSelecao = 1.0 / (100.0 / parseInt(txt_percSelecao.value)); 
		
		for (i=0;i<aChecks.length;i++) {
			if (cbPerc.checked && Math.random() < fatSelecao) {
				aChecks[i].checked = true;
				incSelecao = 0;
			}
			else
				aChecks[i].checked = false;
		}
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
	function mostraImagem(idInfracao) {
        window.open("/infracao/infracao_imagem.jsp?id_infracao="+idInfracao+"&encadeado", "Imagem","width=750, height=700");
	}
	function getIdInfracaoAnterior(idInfracao) {
		var ret = 0;
        var link_imagem = document.getElementById("link_imagem_"+idInfracao);
        
        if (link_imagem) {
        	ret = link_imagem.idInfracaoAnterior; 
        } 
        return ret;
	}
    function getIdInfracaoProximo(idInfracao) {
        var ret = 0;
        var link_imagem = document.getElementById("link_imagem_"+idInfracao);
        
        if (link_imagem) {
            ret = link_imagem.idInfracaoProximo; 
        } 
        return ret;
    }
    
    function processa() {
    	document.getElementById('frm_list_infracao').submit();
    }	

    function processaDireto() {
    	document.getElementById('frm_list_infracao').submit();
    }	

	function fitrarConsultaCompleta() {
        w = window.open("/infracao/listar_infracao_completa_popup.jsp?id_processo=${id_processo}", "Consulta Completa","width=1150, height=520");
        w.infracoesPreSelecionadas = function() {
        	var infracoes_pre_selecionadas = document.getElementById("infracoes_pre_selecionadas");
        	var frm_list = document.getElementById("frm_list");
        	infracoes_pre_selecionadas.value = 1;
        	frm_list.submit();
        }
	}
	function limparConsultaCompleta() {
    	var infracoes_pre_selecionadas = document.getElementById("infracoes_pre_selecionadas");
    	var frm_list = document.getElementById("frm_list");
    	infracoes_pre_selecionadas.value = 0;
    	frm_list.submit();
	}
    
    function contaConsultaCompleta() {
    	var infracoes_pre_selecionadas = document.getElementById("infracoes_pre_selecionadas");
    	var div_conta_consulta_completa = document.getElementById("div_conta_consulta_completa");

    	if (infracoes_pre_selecionadas.value > 0) {
	    	div_conta_consulta_completa.innerHTML = infracoes_pre_selecionadas.value;
    	}
    	else
	    	div_conta_consulta_completa.innerHTML = 0;
    }
    function verifTecla(e) {
		var frm_list = document.getElementById("frm_list");
        if (e.keyCode == 13)
        	frm_list.submit();
    }
</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
				<table class="tabela_branca" width="950">
					<tr>
						<td class="dado_lista_tabela_claro" align="left" width="100%" colspan="5">Mostrando os primeiros ${infracoes_size} de ${total_infracoes} disponíveis.</td>
					</tr>
					<tr>
						<td class="dado_lista_tabela_claro" colspan="5">&nbsp;</td>
					</tr>
					
					<form id="frm_list" action="" method="get">
					
					
					<!-- Se existir uma lista de processos, então mostre a combo para que se possa escolher -->
					<c:if test="${processos!=null}">
					<tr>
						<td class="dado_lista_tabela_claro" align="left" width="250"> 
							Processo de Origem:
							<select id="sel_processo" name="id_processo" style="width: 250px" onchange="submit();">
		                    	<option value="0">-- escolher --</option>
				                <c:forEach var="processo" items="${processos}">
									<c:set var="selecionado" value="${id_processo == processo.idProcesso ? 'selected' : ''}" />
				                    	<option value="${processo.idProcesso}" ${selecionado}>${processo.nome}</option>
				             	</c:forEach>
		                     </select>
						</td>
						<td colspan="4">
						&nbsp;
						</td>
					</tr>
					</c:if>
					
					<tr>
						<td class="dado_lista_tabela_claro" align="left" width="250"> 
							Enquadramento:
							<select id="sel_enquadramento" name="id_enquadramento" style="width: 250px"
								onchange="submit();">
	                            <option value="0">TODOS</option>
		                        <c:forEach var="enquadramento" items="${enquadramentos}">
									<c:set var="selecionado" value="${id_enquadramento == enquadramento.idEnquadramento ? 'selected' : ''}" />
		                            <option value="${enquadramento.idEnquadramento}" ${selecionado}>${enquadramento.descricao}</option>
		                        </c:forEach>
	                        </select>
						</td>
						<td class="dado_lista_tabela_claro" align="left" width="80"> 
							Número de registros inicial:
							<select id="sel_num_reg" name="num_reg" style="width: 160px"
								onchange="submit();">
	                            <option value="10" ${num_reg == 10 ? 'selected' : ''}>10</option>
	                            <option value="100" ${num_reg == 100 ? 'selected' : ''}>100</option>
	                            <option value="1000" ${num_reg == 1000 ? 'selected' : ''}>1000</option>
	                            <option value="0" ${num_reg == null ? 'selected' : ''}>TODOS</option>
	                        </select>
						</td>
						<td class="dado_lista_tabela_claro" align="left" width="300"> 
							Consistentes/Inconsistentes:<br>
                            <input name="consistencia" type="radio" value="1" onchange="submit();" ${consistencia == 1 ? 'checked' : ''}/>Consistentes
                            <input name="consistencia" type="radio" value="2" onchange="submit();" ${consistencia == 2 ? 'checked' : ''}/>Inconsistentes
                            <input name="consistencia" type="radio" value="0" onchange="submit();" ${consistencia == 0 ? 'checked' : ''}/>Todas
                        </td>
						<td class="label_campo" align="right" colspan="2">
							<table class="tabela_branca" style="border: #dadada 1px solid;">
								<tr>
									<td colspan="4">
										<a  class='link' href="javascript:fitrarConsultaCompleta()">Filtro a partir da consulta completa...</a>
									</td>
								</tr>
								<tr>
									<td width="100">
										Seleção atual:
									</td>
									<td>
										<div id="div_conta_consulta_completa"></div>
									</td>
									<td>
										<a class='link' href="javascript:limparConsultaCompleta()">Limpar</a>
									</td>
								</tr>
                        	</table>
                        </td>
					</tr>
					<tr>
						<td class="dado_lista_tabela_claro" align="left" colspan="2"> 
							Período:
							<table class="tabela_branca" width="90%">
								<tr>
				                    <td class="valor_campo" width="48%">
			                            <input id="txt_data_infracao_ini" type="text" name="data_infracao_ini" maxlength="10" style="width: 80px" onkeypress="verifTecla(event)" onblur="preenchePeriodo();" value="${data_infracao_ini}"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_ini'), 'dd/mm/yyyy')"><input id="txt_hora_infracao_ini" type="text" name="hora_infracao_ini" class="campo_texto" maxlength="5" style="width: 40px" onkeypress="verifTecla(event)" value="${hora_infracao_ini}"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_infracao_ini'), 'hh:mm')">
			                        </td>
				                    <td class="valor_campo" width="4%" style="text-align: center;">Até</td>
			                        <td class="valor_campo" width="48%">
			                            <input id="txt_data_infracao_fim" type="text" name="data_infracao_fim" maxlength="10" style="width: 80px" onkeypress="verifTecla(event)" value="${data_infracao_fim}"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_fim'), 'dd/mm/yyyy')"><input id="txt_hora_infracao_fim" type="text" name="hora_infracao_fim" class="campo_texto" maxlength="5" style="width: 40px" onkeypress="verifTecla(event)" value="${hora_infracao_fim}"><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_infracao_fim'), 'hh:mm')">
			                        </td>
								</tr>
							</table>
						</td>
						<td style="vertical-align: bottom;">
							<table class="tabela_branca" width="100%">
								<tr>
									<td width="3%"><input type="checkbox" value="1" name="espera" id="cbEspera" onclick="submit();" ${espera ? 'checked' : ''}/></td>
									<td class="dado_lista_tabela_claro" align="left" width="97%">Somente infrações em espera</td>
								</tr>
							</table>
						</td>
						</td>
						<td class="dado_lista_tabela_claro" align="left" width="15%"> 
							<!-- Se existir uma lista de processos, então mostre a combo para que se possa escolher -->
							<c:if test="${img_teste != null}">
								Nº Img. Local/Pista:
								<select id="sel_num_imagens" name="num_imagens" onchange="submit();">
			                    	<option value="0">-- todas --</option>
			                    	<option value="2" ${num_imagens == 2 ? 'selected' : ''}>2</option>
			                    	<option value="3" ${num_imagens == 3 ? 'selected' : ''}>3</option>
			                    	<option value="4" ${num_imagens == 4 ? 'selected' : ''}>4</option>
			                    	<option value="6" ${num_imagens == 6 ? 'selected' : ''}>6</option>
			                     </select>
							</c:if>
						</td>
						<input type="hidden" id="infracoes_pre_selecionadas" name="infracoes_pre_selecionadas" value="${infracoes_pre_selecionadas}"></input>
					</form>
						<td class="dado_lista_tabela_claro" align="right" style="vertical-align: bottom;">
							<button onclick="document.getElementById('frm_list_infracao').submit();">${btn_processo_caption}</button>
						</td>
					</tr>
				</table>
				<br />
			<form id="frm_list_infracao" action="${list_infracao_action}" method="post">
				<table class="tabela_lista" width="950">
					<tr>
						<th class="head_tabela" width="3%">&nbsp;</th>
						
						<th class="head_tabela" width="3%">Nº</th>
						
						<th class="head_tabela" width="10%">Cód. Local</th>
						<th class="head_tabela" width="37%">Local</th>
						<th class="head_tabela" width="15%">Data Infração</th>
						<th class="head_tabela" width="10%">Cód. Enquadramento</th>
						<th class="head_tabela" width="10%">Cód. Infração</th>
						<th class="head_tabela" width="12%">Imagem</th>
					</tr>
					<% int contador = 1; %>
					<c:forEach var="infracao" varStatus="linhaInfo" items="${infracoes}">
						<tr>
							<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
							<td class="${css_td}" align="center"><input type="checkbox" id="infracao_${infracao.id}" name="sel_infracao" value="${infracao.id}" /></td>
							
							<td class="${css_td}" align="center"><%= contador++ %></td>
							
							<td class="${css_td}" align="center">${infracao.idLocal}</td>
							<td class="${css_td}" align="left">${infracao.local}</td>
							<td class="${css_td}" align="center"><fmt:formatDate value="${infracao.data}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
							<td class="${css_td}" align="center">${infracao.idEnquadramento}</td>
							<td class="${css_td}" align="center">${infracao.id}</td>
	                        <td class="${css_td}" align="center">
								<a id="link_imagem_${infracao.id}" class='link_td' href="javascript:mostraImagem(${infracao.id})">[ver&nbsp;imagem]</a>
	                            <script type="text/javascript">encadeiaInfracaoImagem('${infracao.id}', '${idInfracaoAnterior}')</script>
	                        </td>
						</tr>
	                    <c:set var="idInfracaoAnterior" value="${infracao.id}" />
					</c:forEach>
				</table>
				<table class="tabela_branca" width="950">
					<tr>
						<td width="3%"><input type="checkbox" id="cbTudo" onclick="selTudo(this);"></td>
						<td class="dado_lista_tabela_claro" align="left" width="97%">Selecionar as primeiras ${infracoes_size} infrações.</td>
					</tr>
					<tr>
						<td width="3%"><input type="checkbox" id="cbPerc" onclick="selPerc(this);"></td>
						<td class="dado_lista_tabela_claro" align="left">
							<input type="text" id="txt_percSelecao" style="width: 20px;" value="50">
							% Seleção aproximada com sorteio aleatório.
						</td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="3">
							<button onclick="processa()">${btn_processo_caption}</button>
	                        <c:if test="${processa_direto}">
								<button name="processa_direto" onclick="processaDireto()" value="1">Agendar Processamento</button>
							</c:if>
							<br>
						</td>
					</tr>
				</table>
				<input type="hidden" name="id_processo" value="${id_processo}" />
				<input type="hidden" id="id_enquadramento" name="id_enquadramento" value="${id_enquadramento}" />
				<input type="hidden" id="consistencia" name="consistencia" value="${consistencia}" />
				<input type="hidden" id="espera" name="espera" value="${espera}" />
				<input type="hidden" id="infracoes_pre_selecionadas" name="infracoes_pre_selecionadas" value="${infracoes_pre_selecionadas}" />
				<input type="hidden" id="data_infracao_ini" name="data_infracao_ini" value="${data_infracao_ini}" />
				<input type="hidden" id="data_infracao_fim" name="data_infracao_fim" value="${data_infracao_fim}" />
				<input type="hidden" id="hora_infracao_ini" name="hora_infracao_ini" value="${hora_infracao_ini}" />
				<input type="hidden" id="hora_infracao_fim" name="hora_infracao_fim" value="${hora_infracao_fim}" />
				<input type="hidden" id="num_imagens" name="num_imagens" value="${num_imagens}" />
			</form>
		</td>
	</tr>
</table>
<br />
<br />
<script type="text/javascript">
	contaConsultaCompleta();
</script>
<%@ include file="/includes/rodape.jsp" %>
