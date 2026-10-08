<%@page import="com.consilux.model.InfracaoProcessoObservacao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%
	String sIdInfracao = request.getParameter("id_infracao") != null ? request.getParameter("id_infracao").trim() : null;
	sIdInfracao = sIdInfracao != null && sIdInfracao.length() == 0 ? null : sIdInfracao;

	if (sIdInfracao == null || !Pattern.matches("[1-9][0-9]{0,7}",sIdInfracao)) {
        new MensagemJS(response).showErro("Identificador de infração enviado inválido!");
        return;
    }

    InfracaoCompleta infracao = InfracaoCompleta.buscaInfracaoPorId(Integer.parseInt(sIdInfracao));
    InfoEspecificaInfracao infoEspec = InfoEspecificaInfracao.buscaPorInfracao(infracao.getId());
    Cadastro cadastro = CadastroBD.buscaCadastroPorPlaca(infracao.getPlaca());

    Map<String,Object> mFiltro = new HashMap<String,Object>();
    mFiltro.put("id_infracao", infracao.getId());
    List<InfracaoProcesso> infracoesProcesso = InfracaoProcesso.buscaInfracaoProcessoPor(mFiltro);
    
    InfracaoRemessa remessaInfracao = InfracaoRemessa.buscarItemRemessaPorInfracao(infracao.getId());
    
    Remessa remessa = null;
    if (remessaInfracao != null) {
    	remessa = Remessa.buscarRemessaPorId(remessaInfracao.getIdRemessa());
    }
    
    List<InfracaoProcessoObservacao> observacoes = InfracaoProcessoObservacao.ObterInfracaoProcessoObservacao(infracao.getId());
    Integer nobservacoes = observacoes.size();
%>
<%@page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.consilux.model.Enquadramento"%>

<%@page import="com.consilux.model.MensagemJS"%>

<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.InfracaoCompleta"%>

<%@page import="com.consilux.model.InfoEspecificaInfracao"%>
<%@page import="com.consilux.model.Cadastro"%>
<%@page import="com.consilux.model.CadastroBD"%>
<%@page import="com.consilux.model.InfracaoProcesso"%>

<%@page import="com.consilux.model.InfracaoRemessa"%>
<%@page import="com.consilux.model.Remessa"%>
<c:set var="infracao"          value="<%=infracao%>" />
<c:set var="infoEspec"         value="<%=infoEspec%>" />
<c:set var="cadastro"          value="<%=cadastro%>" />
<c:set var="infracoesProcesso" value="<%=infracoesProcesso%>" />
<c:set var="remessa"           value="<%=remessa%>" />
<c:set var="remessaInfracao"   value="<%=remessaInfracao%>" />
<c:set var="observacoes"       value="<%=observacoes%>" />
<c:set var="nobservacoes"      value="<%=nobservacoes%>" />
<script type="text/javascript">
	function mostraImagem(idInfracao) {
	    window.open("/infracao/infracao_imagem.jsp?id_infracao="+idInfracao, "Imagem","width=750, height=700");
	}
</script>
<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
		<table class="tabela_branca" width="100%">
			<tr>
				<th class="head_tabela" width="100%" colspan="6">DADOS DA
				INFRAÇÃO</th>
			</tr>
			<tr>
				<td class="label_campo" width="10%">Nº da Infracão</td>
				<td class="label_campo" width="10%">Nº Sequencial de Registro</td>
				<td class="label_campo" width="25%">Local</td>
				<td class="label_campo" width="5%">Pista</td>
				<td class="label_campo" width="15%">Data</td>
				<td class="label_campo" width="35%">Tipo</td>
			</tr>
			<tr>
				<td class="visualiza_campo">
				<div id="div_infracao">${infracao.id}</div>
				</td>
				<td class="visualiza_campo">
				<div id="div_imagem">${infracao.idImagemLocal}&nbsp;<a
					class='link_td' href="javascript:mostraImagem(${infracao.id})">[...]</a></div>
				</td>
				<td class="visualiza_campo">
				<div id="div_local">${infracao.nomeLocal}</div>
				</td>
				<td class="visualiza_campo">
				<div id="div_local">${infracao.pista}</div>
				</td>
				<td class="visualiza_campo">
				<div id="div_data"><fmt:formatDate
					value="${infracao.dataVeiculo}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></div>
				</td>
				<td class="visualiza_campo">
				<div id="div_tipo">${infoEspec.descricao}</div>
				</td>
			</tr>
			<tr>
				<td class="label_campo" colspan="5">Dados específicos</td>
			</tr>
			<tr>
				<td align="center" colspan="5">
				<table class="tabela_branca" width="100%">
					<tr>
						<td class="visualiza_campo" width="20%">
						<div id="div_tit_info_espec">${infoEspec.tituloEspecifica}</div>
						</td>
						<td class="visualiza_campo" width="20%">
						<div id="div_info_espec_1"></div>
						${infoEspec.descricaoEspecifica[0]}</td>
						<td class="visualiza_campo" width="20%">
						<div id="div_info_espec_2"></div>
						${infoEspec.descricaoEspecifica[1]}</td>
						<td class="visualiza_campo" width="20%">
						<div id="div_info_espec_3"></div>
						${infoEspec.descricaoEspecifica[2]}</td>
						<td width="20%"></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<th class="head_tabela" width="100%" colspan="6">ESTADO ATUAL</th>
			</tr>
			<tr>
				<td align="center" colspan="5">
				<table class="tabela_branca" width="100%">
					<tr>
						<td class="label_campo" width="40%">Usuário</td>
						<td class="label_campo" width="40%">Processo</td>
						<td class="label_campo" width="40%">Espera</td>
					</tr>
					<tr>
						<td class="visualiza_campo" width="40%">${infracao.usuarioAtual}</td>
						<td class="visualiza_campo" width="60%">${infracao.nomeProcesso}</td>
						<td class="visualiza_campo" width="60%">${infracao.espera ?
						'Sim' : 'Não'}</td>
						<td width="20%"></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		<table class="tabela_branca" width="100%">
			<tr>
				<th class="head_tabela" width="100%" colspan="6">CADASTRO DO
				VEÍCULO</th>
			</tr>
			<tr>
				<td class="label_campo" width="10%">Placa</td>
				<td class="label_campo" width="30%">Marca</td>
				<td class="label_campo" width="10%">Cor</td>
				<td class="label_campo" width="10%">Ano</td>
				<td class="label_campo" colspan="2" width="40%">Espécie</td>
			</tr>
			<tr>
				<td class="visualiza_campo">
				<div id="div_placa">${cadastro.placa}</div>
				</td>
				<td class="visualiza_campo">
				<div id="div_marca">${cadastro.marcaDisponivel}</div>
				</td>
				<td class="visualiza_campo">
				<div id="div_cor">${cadastro.cor}</div>
				</td>
				<td class="visualiza_campo">
				<div id="div_ano">${cadastro.ano}</div>
				</td>
				<td class="visualiza_campo" colspan="2">
				<div id="div_especie">${cadastro.especie}</div>
				</td>
			</tr>
			<tr>
				<td class="label_campo">Tipo</td>
				<td class="label_campo">Categoria</td>
				<td class="label_campo" colspan="2">Situação</td>
				<td class="label_campo">Localidade</td>
				<td class="label_campo">Atualização</td>
			</tr>
			<tr>
				<td class="visualiza_campo">
				<div id="div_tipo">${cadastro.tipo}</div>
				</td>
				<td class="visualiza_campo">
				<div id="div_categoria">${cadastro.categoria}</div>
				</td>
				<td class="visualiza_campo" colspan="2">
				<div id="div_situacao">${cadastro.situacao}</div>
				</td>
				<td class="visualiza_campo">
				<div id="div_localidade">${cadastro.localidade}</div>
				</td>
				<td class="visualiza_campo">
				<div id="div_atualizacao"><fmt:formatDate
					value="${cadastro.dataAtualizacao}" type="date" pattern="dd/MM/yyyy" /></div>
				</td>
			</tr>
		</table>
		<table class="tabela_branca" width="100%">
			<tr>
				<th class="head_tabela" width="100%" colspan="6">REMESSA</th>
			</tr>
			<tr>
				<td class="label_campo" width="15%">Remessa</td>
				<td class="label_campo" width="30%">Data de Geração</td>
				<td class="label_campo" width="30%">Data de Confirmação</td>
				<td class="label_campo" width="10%">Série</td>
				<td class="label_campo" width="15%">Auto</td>
			</tr>
			<tr>
				<td class="visualiza_campo">${remessa.idRemessa}</td>
				<td class="visualiza_campo"><fmt:formatDate
					value="${remessa.data}" type="date" pattern="dd/MM/yyyy" /></td>
				<td class="visualiza_campo"><fmt:formatDate
					value="${remessa.dataConfirmacao}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
				<td class="visualiza_campo">${remessaInfracao.serie}</td>
				<td class="visualiza_campo">${remessaInfracao.auto}</td>
			</tr>
		</table>
		
		<c:if test="${nobservacoes > 0}">
		
		<table class="tabela_branca" width="100%">
		<thead>
		<tr>
		<th>OBSERVAÇÕES</th>
		</tr>
		</thead>
		<tbody>
		<tr> <td align="center">
		
		<table class="tabela_lista" width="100%">
					<tr>
						<th class="head_tabela" width="15%">Processo</th>
						<th class="head_tabela" width="10%">Operador</th>
						<th class="head_tabela" width="10%">Data Processo</th>
						<th class="head_tabela" width="65%">Observacao</th>
					</tr>
					<c:forEach var="obs" varStatus="linhaInfo" items="${observacoes}">
							<tr>
							<c:set var="css_td"
								value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
							<td class="${css_td}" align="center">${obs.processo}</td>
							<td class="${css_td}" align="center">${obs.operador}</td>
							<td class="${css_td}" align="center"><fmt:formatDate
								value="${obs.dataProcesso}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
							<td class="${css_td}" align="left">${obs.observacao}</td>
							</tr>
					</c:forEach>
		</table>
		
		</td></tr>
		</tbody>
		</table>
		
		</c:if>
		
		<table class="tabela_branca" width="100%">
			<tr>
				<th class="head_tabela" width="100%">HISTÓRICO DO PROCESSO</th>
			</tr>
			<tr>
				<td align="center">
				<table class="tabela_lista" width="100%">
					<tr>
						<th class="head_tabela" width="15%">Processo</th>
						<th class="head_tabela" width="10%">Operador</th>
						<th class="head_tabela" width="10%">Data Processo</th>
						<th class="head_tabela" width="5%">Tempo</th>
						<th class="head_tabela" width="25%">Inconsistência</th>
						<th class="head_tabela" width="8%">Placa</th>
						<th class="head_tabela" width="10%">Marca</th>
						<th class="head_tabela" width="10%">Tipo</th>
						<th class="head_tabela" width="7%">Status</th>
					</tr>
					<c:forEach var="infracaoProcesso" varStatus="linhaInfo"
						items="${infracoesProcesso}">
						<tr>
							<c:set var="css_td"
								value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
							<td class="${css_td}" align="center">${infracaoProcesso.descricaoProcesso}</td>
							<td class="${css_td}" align="center">${infracaoProcesso.usuario}</td>
							<td class="${css_td}" align="center"><fmt:formatDate
								value="${infracaoProcesso.data}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
							<td class="${css_td}" align="center">${infracaoProcesso.tempo}</td>
							<td class="${css_td}" align="center">${infracaoProcesso.descricaoInconsistencia}</td>
							<td class="${css_td}" align="center">${infracaoProcesso.placa}</td>
							<td class="${css_td}" align="center">${infracaoProcesso.marcaDisponivel}</td>
							<td class="${css_td}" align="center">${infracaoProcesso.tipo}</td>
							<td class="${css_td}" align="center">${infracaoProcesso.descricaoStatus}</td>
						</tr>
					</c:forEach>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp"%>