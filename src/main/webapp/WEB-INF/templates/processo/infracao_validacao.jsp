<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<br/>

<c:if test="${existeProximoLote == true}">
	<table class="tabela_branca" style="width:100%;">
		<tr>
			<td align="center">
				<table class="tabela_branca" style="width:950px;">
					<tr>
						<td class="dado_lista_tabela_claro" colspan="5">&nbsp;</td>
					</tr>
						<form id="frm_list" action="" method="get">
					<tr>
					<tr>
						<td class="dado_lista_tabela_claro" colspan="5">&nbsp;</td>
					</tr>
					
					
					<tr>
					<td class="dado_lista_tabela_claro" colspan="2">
					<big>${remessa_at.descricaoApait}</big>
					</td>
					</tr>
					
					<tr>
					<td class="dado_lista_tabela_claro" align="left" width="250" valign="top">
						<c:if test="${remessa_at != null}">
							Primeira Auditoria: ${remessa_at.primeiroUsuarioValidacao} <br />
							Revisão: ${remessa_at.revisao} <br />
							Tamanho da Amostra: <label id="tamanho_amostra">${remessa_at.tamanhoAmostraReal > 0 ? remessa_at.tamanhoAmostraReal : 'N/D'}</label> <br />
							Infrações no Lote: ${remessa_at.totalInfracao} <br />
							Infrações Validáveis: ${remessa_at.totalInfracoesValidaveis} <br />
						</c:if>
					</td>
					<td class="dado_lista_tabela_claro" align="left" width="300" valign="top">
						<c:if test="${remessa_at != null}">
							Infrações Validadas: ${remessa_at.totalInfracao - remessa_at.totalInfracoesValidaveis} <br />
							Erros encontrados na Amostra: <label id="num_erros_processamento">${remessa_at.errosProcessamento}</label> <br />
							Erros encontrados no Lote: ${remessa_at.errosProcessamentoRelatorio} <br />
							Erros Aceitáveis na Amostra: <label id="num_erros_aceitaveis">${remessa_at.ac}</label> <br />
							Erros para Reprovação: ${remessa_at.re}
						</c:if>
					</td>
					<c:if test="${remessa_at != null && remessa_at.integridade == false}">
					<td class="dado_lista_tabela_vermelho" align="left" width="300" valign="top">
							${remessa_at.integridadeDescricao} <br/>
							${remessa_at.integridadeDescricaoExt}	
					</td>
					</c:if>
					<c:if test="${remessa_at != null && remessa_at.integridade == true}">
					<td class="dado_lista_tabela_claro" align="left" width="300" valign="top">
							${remessa_at.integridadeDescricao}	
					</td>
					</c:if>
					</tr>
					<c:if test="${remessa_at != null && remessa_at.integridade == true}">			  
					<tr>
						<td colspan="1" style="vertical-align: bottom;">
							<table class="tabela_branca" style="width=100%">
								<tr>
									<td width="3%"><input type="checkbox" value="1" name="espera" id="cbEspera" onclick="submit();" ${espera ? 'checked' : ''}/></td>
									<td class="dado_lista_tabela_claro" align="left" width="42%">Somente Infrações em Espera</td>
									
									<td width="3%"><input type="checkbox" value="1" name="revisao" id="cbRevisao" onclick="submit();" ${revisao ? 'checked' : ''}/></td>
									<td class="dado_lista_tabela_claro" align="left" width="22%">Revisar Validadas</td>
								</tr>
							</table>
						</td>
	
						<input type="hidden" id="cbAmostra" name="amostra" value="${amostra}"></input>
						<input type="hidden" id="infracoes_pre_selecionadas" name="infracoes_pre_selecionadas" value="${infracoes_pre_selecionadas}"></input>
						</form>
						<td class="dado_lista_tabela_claro" align="left" style="vertical-align: bottom;">
							<button onclick="iniciarValidacaoAmostra();">${btn_processo_amostra_caption}</button>
						</td>
						<c:if test="${remessa_at.amostra == false}">
							<td class="dado_lista_tabela_claro" align="left" style="vertical-align: bottom;">
								<button onclick="iniciarValidacao();">${btn_processo_caption}</button>
							</td>
						</c:if>
						<c:if test="${remessa_at.amostra == true}">
							<td class="dado_lista_tabela_claro" align="left" style="vertical-align: bottom;">
								<button onclick="limparAmostra(${remessa_at.idRemessa});">${btn_limpar_amostra_caption}</button>
							</td>
						</c:if>
					</tr>
					</c:if>
				</table>
				
				<br />
				<form id="frm_list_infracao" action="${list_infracao_action}" method="post">
					<c:if test="${revisao == true || espera == true}">
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
						</table>
					</c:if>	
					<input type="hidden" name="id_processo" value="${id_processo}" />
					<input type="hidden" id="id_enquadramento" name="id_enquadramento" value="${id_enquadramento}" />
					<input type="hidden" id="id_remessa" name="id_remessa" value="${remessa_at.idRemessa}" />
					<input type="hidden" id="consistencia" name="consistencia" value="${consistencia}" />
					<input type="hidden" id="espera" name="espera" value="${espera}" />
					<input type="hidden" id="revisao" name="revisao" value="${revisao}"/>
					<input type="hidden" id="amostra" name="amostra" value="${amostra}"/>
					<input type="hidden" id="infracoes_pre_selecionadas" name="infracoes_pre_selecionadas" value="${infracoes_pre_selecionadas}" />
					<input type="hidden" id="num_imagens" name="num_imagens" value="${num_imagens}" />
					<input type="hidden" id="mostrarTodos" name="mostrarTodos" value="${mostrarTodos}" />
					<input type="hidden" id="limparAmostra" name="limparAmostra" />
				</form>
			</td>
		</tr>
	</table>
</c:if>

<c:if test="${existeProximoLote == false}">
	<table class="tabela_branca" style="width:100%;">
		<tr>
			<td align="center">
				<table class="tabela_branca" style="width:950px;">
					<tr>
						<td class="dado_lista_tabela_claro" align="center" colspan="5">
							<big>Não há lotes pendetes de validação!</big>
						</td>
					</tr>
					<tr>
						<td class="dado_lista_tabela_claro" align="center" colspan="5">
							<a href="/login/gtw_principal.jsp"><big><button>Sair</button></big></a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</c:if>

<br/>
<br/>
<%@ include file="/includes/rodape.jsp" %>
<script type="text/javascript">
	function selTudo(cbTudo) {
		var aChecks = document.getElementsByName("sel_infracao");
		for (i=0;i<aChecks.length;i++) {
			aChecks[i].checked = cbTudo.checked;
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
	function limparAmostra(idRemessa) {
		var limparAmostra = document.getElementById("limparAmostra");
		limparAmostra.value = "true";
		
		if (confirm("Deseja limpar a amostra?")) {
			window.open("/processo/LiberarMovimentosLote?mov_limpar=1&sel_movimento="+idRemessa,"Limpar Amostra","width=700, height=180");
// 			document.getElementById('frm_list').submit();
		} else {
			return false;
		}
	}
	function iniciarValidacao() {
		var amostra = document.getElementById("amostra");
		var limparAmostra = document.getElementById("limparAmostra");
		
		amostra.value = "false";
		limparAmostra.value = "false";
		
		document.getElementById('frm_list_infracao').submit();
	}
	function iniciarValidacaoAmostra() {
		var frm_list = document.getElementById("frm_list");
		var div_erros = document.getElementById("num_erros_processamento");
		var div_erros_aceitaveis = document.getElementById("num_erros_aceitaveis");
		var tamanho_amostra = document.getElementById("tamanho_amostra").innerHTML;
		var amostra = document.getElementById("amostra");
		var limparAmostra = document.getElementById("limparAmostra");
		var erros = 0;
		var erros_aceitaveis = 0;
		amostra.value = "true";
		limparAmostra.value = "false";
		
		if (div_erros)
			erros = parseInt(div_erros.innerHTML);
		if (div_erros_aceitaveis)
			erros_aceitaveis = parseInt(div_erros_aceitaveis.innerHTML);
		
		if (erros > erros_aceitaveis && tamanho_amostra == 'N/D') {
			var ret = confirm("Lote será reprovado se for iniciada validação por Amostra. Deseja continuar?");
			if(ret) {
				document.getElementById('frm_list_infracao').submit();
			}
		} else {
			document.getElementById('frm_list_infracao').submit();
		}
		
	}
    function relatorioFormacaoLote(idRemessa) {
    	window.open("/relatorio/RelatorioFormacaoLote?id_remessa=" + idRemessa);
    }
    function relatorioValidacao(idRemessa) {
    	window.open("/relatorio/RelatorioValidacao?id_remessa=" + idRemessa);
    }
    function exportarLoteValidado(idRemessa) {
    	window.open("/remessa/ExportarRemessa?id_remessa=" + idRemessa + "&tipo=lv");
    }
</script>