<%@page import="com.consilux.model.OcorrenciaLista"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@include file="/includes/cabecalho_vazio.jsp"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%!private static Logger logger = Logger.getLogger(OcorrenciaLista.class);%>
<%
	String sDataOcorrenciaIni = request.getParameter("data_ocorrencia_ini") != null ? request.getParameter("data_ocorrencia_ini").trim() : null;
	sDataOcorrenciaIni = sDataOcorrenciaIni != null && sDataOcorrenciaIni.length() == 0 ? null : sDataOcorrenciaIni;
	
	String sHoraOcorrenciaIni = request.getParameter("hora_ocorrencia_ini") != null ? request.getParameter("hora_ocorrencia_ini").trim() : null;
	sHoraOcorrenciaIni = sHoraOcorrenciaIni != null && sHoraOcorrenciaIni.length() == 0 ? null : sHoraOcorrenciaIni;
	
	String sDataOcorrenciaFim = request.getParameter("data_ocorrencia_fim") != null ? request.getParameter("data_ocorrencia_fim").trim() : null;
	sDataOcorrenciaFim = sDataOcorrenciaFim != null && sDataOcorrenciaFim.length() == 0 ? null : sDataOcorrenciaFim;
	
	String sHoraOcorrenciaFim = request.getParameter("hora_ocorrencia_fim") != null ? request.getParameter("hora_ocorrencia_fim").trim() : null;
	sHoraOcorrenciaFim = sHoraOcorrenciaFim != null && sHoraOcorrenciaFim.length() == 0 ? null : sHoraOcorrenciaFim;
	
	String sIdLocal = request.getParameter("id_local") != null ? request.getParameter("id_local").trim() : null;
	sIdLocal = sIdLocal != null && sIdLocal.length() == 0 ? null : sIdLocal;
	Integer idLocal = sIdLocal != null ? Integer.parseInt(sIdLocal) : 0;
	
	String sEquipamento = request.getParameter("equipamento") != null ? request.getParameter("equipamento").trim() : null;
	sEquipamento = sEquipamento != null && sEquipamento.length() == 0 ? null : sEquipamento;
	
	if (sDataOcorrenciaIni != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataOcorrenciaIni)) {
		new MensagemJS(response).showErro("Data inicial da ocorrência enviada inválida!");
		return;
	}
	if (sDataOcorrenciaFim != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataOcorrenciaFim)) {
		new MensagemJS(response).showErro("Data inicial da ocorrência enviada inválida!");
		return;
	}
	if (sHoraOcorrenciaIni != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])", sHoraOcorrenciaIni)) {
		new MensagemJS(response).showErro("Hora inicial da ocorrência enviada inválida!");
		return;
	}
	if (sHoraOcorrenciaFim != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])", sHoraOcorrenciaFim)) {
		new MensagemJS(response).showErro("Hora final da ocorrência enviada inválida!");
		return;
	}
	if ((sDataOcorrenciaIni != null || sHoraOcorrenciaIni != null || sDataOcorrenciaFim != null || sHoraOcorrenciaFim != null) &&
			(sDataOcorrenciaIni == null || sHoraOcorrenciaIni == null || sDataOcorrenciaFim == null || sHoraOcorrenciaFim == null)) {
		new MensagemJS(response).showErro("Período de ocorrência incompleto!");
		return;
	}
	if (sIdLocal != null) {
		if (!Pattern.matches("[0-9]{1,8}", sIdLocal)) {
			new MensagemJS(response).showErro("Local selecionado inválido!");
			return;
		}
	}
	
// 	if (sDataOcorrenciaIni == null && sIdLocal == null) {
// 		new MensagemJS(response).showErro("Pesquisa muito abregente, selecione um filtro!");
// 		return;
// 	}	

	Map<String, Object> mFiltro = new HashMap<String, Object>();

	if (sDataOcorrenciaIni != null) {
		mFiltro.put("data_ocorrencia_ini", new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataOcorrenciaIni + " " + sHoraOcorrenciaIni + ":00").getTime()));
	}
	if (sDataOcorrenciaFim != null) {
		mFiltro.put("data_ocorrencia_fim", new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataOcorrenciaFim + " " + sHoraOcorrenciaFim + ":59").getTime()));
	}
	if (idLocal > 0)
		mFiltro.put("id_local", Integer.parseInt(sIdLocal));

	List<OcorrenciaLista> ocorrencias = null;
	try {
		ocorrencias = OcorrenciaLista.buscaOcorrenciaPor(mFiltro);
	} catch (Exception err) {
		logger.error("Erro ao pesquisar ocorrências: " + err.getMessage(), err);
		new MensagemJS(response).showErro("Não foi possível realizar a busca no banco de dados!");
		return;
	}
%>

<%@page import="com.consilux.model.Cadastro"%>
<%@page import="com.consilux.model.CadastroBD"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.consilux.model.OcorrenciaLista"%>
<%@page import="com.consilux.infra.ExpValida"%>
<%@page import="org.apache.log4j.Logger"%>

<script type="text/javascript">

function setIdOcorrencia(idOcorrencia) {
	document.getElementById('idOcorrenciaHidden').value = idOcorrencia;
}

function submit() {
	document.getElementById('frm_dados_ocorrencia').submit();
}

</script>

<c:set var="ocorrencias" value="<%=ocorrencias%>" />

<form id="frm_dados_ocorrencia" action="/relatorio/RelatorioOcorrencia" method="get" >
	<table class="tabela_branca" width="100%">
		<tr>
			<td align="center">
				<table class="tabela_lista" width="100%">
					<tr>
						<th class="head_tabela" width="9%">Ofício</th>
						<th class="head_tabela" width="9%">Série Equipamento</th>
						<th class="head_tabela" width="8%">Cod. Local</th>
						<th class="head_tabela" width="12%">Data Ocorrência</th>
						<th class="head_tabela" width="25%">Motivo Resumido</th>
						<th class="head_tabela" width="15%">Status</th>
						<th class="head_tabela" width="12%">Data Cadastro</th>
						<th class="head_tabela" width="10%">Ação</th>
					</tr>
					<c:set var="contaRegistros" value="0" />
					<c:forEach var="ocorrencia" varStatus="linhaInfo" items="${ocorrencias}">
						<tr>
							<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
							<td class="${css_td}" align="center">${ocorrencia.numeroOficio}/${ocorrencia.anoOficio}</td>
							<td class="${css_td}" align="center">${ocorrencia.serieEquipamento}</td>
							<td class="${css_td}" align="center">${ocorrencia.codPista}</td>
							<td class="${css_td}" align="center"><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${ocorrencia.dataHora}" type="both" /></td>
							<td class="${css_td}" align="center">${ocorrencia.motivoResumido}</td>
							<td class="${css_td}" align="center">${ocorrencia.estado}</td>
							<td class="${css_td}" align="center"><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${ocorrencia.dataCadastro}" type="both" /></td>
							<td class="${css_td}" align="center">
								<c:if test="${ocorrencia.idOcorrencia > 0}">
									<a id="link_arquivo_${ocorrencia.idOcorrencia}" onclick="setIdOcorrencia(${ocorrencia.idOcorrencia}); submit();" class='link_td'>[Abrir&nbsp;arquivo]</a>
								</c:if>
							</td>
							<c:set var="contaRegistros" value="${linhaInfo.count}" />
							<c:set var="idOcorrencia" value="${ocorrencia.idOcorrencia}" />
						</tr>
					</c:forEach>
				</table>
			</td>
			<td class="valor_campo" colspan="1" hidden="true">
            	<input id="idOcorrenciaHidden" name="idOcorrenciaHidden" type="hidden" /> Id
			</td>
		</tr>
	</table>
</form>
<script type="text/javascript">
	var div_conta_registros = parent.document.getElementById("div_conta_registros");
	if (div_conta_registros)
		div_conta_registros.innerHTML = '${contaRegistros}';
		
</script>
<%@ include file="/includes/rodape.jsp"%>
