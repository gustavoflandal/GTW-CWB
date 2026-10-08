<%@page import="com.consilux.model.manutencao.AlertaManutencao"%>
<%@page import="com.consilux.model.relatorio.RelatorioAlertasFaixa"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.relatorio.RelatorioInfracoesAuditoria"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
	List<RelatorioAlertasFaixa> lia = RelatorioAlertasFaixa.buscaRelatorio();
	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("grupo",((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
	List<LocalVigente> equipamentos = LocalVigente.buscaLocalVigentePor(mFiltro,2);
%>
<c:set var="equipamentos" value="<%=equipamentos%>" />
<c:set var="lia" value="<%=lia%>" />
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript">
	function ajustaEquipamento(porCodigo) {
		var txt_equipamento = document.getElementById("txt_equipamento");
		var sel_equipamento = document.getElementById("sel_equipamento");
		if (porCodigo) {
			sel_equipamento.selectedIndex = 0;

			for (var i=0;i<sel_equipamento.options.length;i++) {
				if (sel_equipamento.options[i].value == txt_equipamento.value)
					sel_equipamento.selectedIndex = i;
			}
		}
		else
			txt_equipamento.value = sel_equipamento.value;
	}
	
	function preSelecionaPista(idEquipamento, idPista) {
		var txt_equipamento = document.getElementById("txt_equipamento");
		var sel_pista = document.getElementById("sel_pista");
		var txt_alerta = document.getElementById("txt_alerta");
		txt_equipamento.value = idEquipamento;
		sel_pista.value = idPista;
		ajustaEquipamento(true);
		txt_alerta.select();
	}
	function ajustaTextoFalsoPositivo() {
		var txt_alerta = document.getElementById("txt_alerta");
		txt_alerta.value = '[Falso Positivo]';
	}
	function desativarAlerta(idEquipamento, idPista) {
	   if (confirm("Deseja realmente desativar o alerta para o equipamento '"+idEquipamento+"', pista: '"+idPista+"'?")) {
		   $.get('/manutencao/AlterarAlertaManutencao', {
			      serie_equipamento: idEquipamento,
		          pista: idPista,
		          acao: <%=AlertaManutencao.Acao.DESATIVAR.ordinal()%>
		          }, function(xml) {
		        	  alert('Alerta desabilitado.');
		        	  window.location.reload();
		        });
	   }
	}
	function desativarAlertasInexistentes() {
	   if ((sInfo = prompt("Deseja realmente desativar TODOS os alertas que já foram resolvidos e não têm descrição?\nSe SIM, digite o motivo:")) != null) {
		   $.get('/manutencao/AjustarAlertasManutencao', {
			   	  info_adic: sInfo,
		          acao: <%=AlertaManutencao.Acao.DESATIVAR_INEXISTENTES.ordinal()%>
		          }, function(xml) {
		        	  alert('Alertas desabilitados.');
		        	  window.location.reload();
		        });
	   }
	}
</script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_alerta" action="/manutencao/AlterarAlertaManutencao" method="post" accept-charset="ISO-8859-1">
				<table class="tabela_lista" width="600">
					<tr>
						<th class="head_tabela" width="100%" colspan="4">Informação específica</th>
					</tr>
                    <tr>
                        <td class="label_campo" width="20%">Equipamento:</td>
                        <td class="valor_campo" width="10%">
                        	<input id="txt_equipamento" type="text" class="campo_texto" maxlength="7" onblur="ajustaEquipamento(true)"/>
                       	</td>
                        <td class="valor_campo" width="55%">
                            <select id="sel_equipamento" name="serie_equipamento" onchange="ajustaEquipamento(false)">
                                <option value="0" selected="selected">--equipamento--</option>
                                <c:forEach var="equip" items="${equipamentos}">
                                    <option value="${equip.serieEquipamento}">${equip.nome}</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td class="valor_campo" width="15%">
                            <select id="sel_pista" name="pista">
                                <option value="-1" selected="selected">--pista--</option>
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="0">--todas--</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Informação Adicional:</td>
                        <td class="label_campo" colspan="3">
                        	<input id="txt_alerta" type="text" name="info_adic" class="campo_texto" />
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Alerta inválido:</td>
                        <td class="label_campo" colspan="3">
                        	<input type="checkbox" name="causa_falso_positivo" value="1" onclick="ajustaTextoFalsoPositivo()"/>Falso Positivo
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Causa externa:</td>
                        <td class="label_campo" colspan="3">
                        	<input type="checkbox" name="causa_ext_energia" value="1"/>Energia
                        	<input type="checkbox" name="causa_ext_vandalismo" value="1"/>Vandalismo
                        	<input type="checkbox" name="causa_ext_pavimento" value="1"/>Pavimento
                        </td>
                    </tr>
					<tr>
						<td class="box_botoes" colspan="4">
							<button onclick="document.getElementById('frm_alerta').submit()">Alterar</button><br>
						</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
		<table class="tabela_lista" width="1100">
			<tr>
				<th class="head_tabela" width="100%" colspan="9">Alertas Ativos</th>
			</tr>
			<tr align="center" >
				<th class="head_tabela" align="center" colspan="3" width="35%">Faixa</th>
				<th class="head_tabela" align="center" colspan="1" rowspan="2" width="40%">Informação Adicional</th>
				<th class="head_tabela" align="center" colspan="4" width="15%">Causas Externas</th>
				<th class="head_tabela" align="center" colspan="1" rowspan="2" width="10%">Ação</th>
			</tr>
			<tr align="center" >
				<th class="head_tabela" align="center" width="5%">Nº Série</th>
				<th class="head_tabela" align="center" width="20%">Nome Pista</th>
				<th class="head_tabela" align="center" width="5%">Pista</th>
				<th class="head_tabela" align="center" width="5%">Energ.</th>
				<th class="head_tabela" align="center" width="5%">Vand.</th>
				<th class="head_tabela" align="center" width="5%">Pav.</th>
				<th class="head_tabela" align="center" width="5%">Falso</th>
			</tr>
			<c:forEach var="alerta" varStatus="linhaInfo" items="${lia}">
				<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
				<tr align="center" >
					<td class="${css_td}" align="center">${alerta.serieEquipamento}</td>
					<td class="${css_td}" align="center">${alerta.nomePista}</td>
					<td class="${css_td}" align="center">${alerta.idPista}</td>
					
					<td class="${css_td}" align="center">
						<c:if test="${alerta.informacaoAdicional == null}"><a href="javascript:preSelecionaPista(${alerta.serieEquipamento}, ${alerta.idPista})">[ajustar]</a></c:if>
						${alerta.informacaoAdicional}</td>
					<td class="${css_td}" align="center">${alerta.energia ? 'S' : 'N'}</td>
					<td class="${css_td}" align="center">${alerta.vandalismo ? 'S' : 'N'}</td>
					<td class="${css_td}" align="center">${alerta.pavimento ? 'S' : 'N'}</td>
					<td class="${css_td}" align="center">${alerta.falsoPositivo ? 'S' : 'N'}</td>
					<td class="${css_td}" align="center"><button onclick="desativarAlerta(${alerta.serieEquipamento},${alerta.idPista});">Encerrar</button>
				</tr>
			</c:forEach>
		</table>
		</td>
	</tr>
</table>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center"><button onclick="desativarAlertasInexistentes();">Desativar Inexistentes</button></td>
	</tr>
</table>

<%@ include file="/includes/rodape.jsp" %>
