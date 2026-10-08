<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%!private static Logger logger = Logger.getLogger(VeiculoCompletoLista.class);%>
<%
	String sIdVeiculoIni = request.getParameter("id_veiculo_ini") != null ? request
			.getParameter("id_veiculo_ini").trim() : null;
	sIdVeiculoIni = sIdVeiculoIni != null
			&& sIdVeiculoIni.length() == 0 ? null : sIdVeiculoIni;
	String sIdVeiculoFim = request.getParameter("id_veiculo_fim") != null ? request
			.getParameter("id_veiculo_fim").trim() : null;
	sIdVeiculoFim = sIdVeiculoFim != null
			&& sIdVeiculoFim.length() == 0 ? null : sIdVeiculoFim;
	String sDataVeiculoIni = request.getParameter("data_veiculo_ini") != null ? request
			.getParameter("data_veiculo_ini").trim() : null;
	sDataVeiculoIni = sDataVeiculoIni != null
			&& sDataVeiculoIni.length() == 0 ? null : sDataVeiculoIni;
	String sHoraVeiculoIni = request.getParameter("hora_veiculo_ini") != null ? request
			.getParameter("hora_veiculo_ini").trim() : null;
	sHoraVeiculoIni = sHoraVeiculoIni != null
			&& sHoraVeiculoIni.length() == 0 ? null : sHoraVeiculoIni;
	String sDataVeiculoFim = request.getParameter("data_veiculo_fim") != null ? request
			.getParameter("data_veiculo_fim").trim() : null;
	sDataVeiculoFim = sDataVeiculoFim != null
			&& sDataVeiculoFim.length() == 0 ? null : sDataVeiculoFim;
	String sHoraVeiculoFim = request.getParameter("hora_veiculo_fim") != null ? request
			.getParameter("hora_veiculo_fim").trim() : null;
	sHoraVeiculoFim = sHoraVeiculoFim != null
			&& sHoraVeiculoFim.length() == 0 ? null : sHoraVeiculoFim;
	String sIdVeiculoLocalIni = request
			.getParameter("id_veiculo_local_ini") != null ? request
			.getParameter("id_veiculo_local_ini").trim() : null;
	sIdVeiculoLocalIni = sIdVeiculoLocalIni != null
			&& sIdVeiculoLocalIni.length() == 0 ? null
			: sIdVeiculoLocalIni;
	String sIdVeiculoLocalFim = request
			.getParameter("id_veiculo_local_fim") != null ? request
			.getParameter("id_veiculo_local_fim").trim() : null;
	sIdVeiculoLocalFim = sIdVeiculoLocalFim != null
			&& sIdVeiculoLocalFim.length() == 0 ? null
			: sIdVeiculoLocalFim;
	String sLocal = request.getParameter("local");
	String sClasse = request.getParameter("id_classe");
	String sImgTeste = request.getParameter("img_teste");
	String sImgIrregular = request.getParameter("img_irregular");
	String sImgInfrator = request.getParameter("img_infrator");
	String sPlaca = request.getParameter("placa") != null ? request
			.getParameter("placa").trim() : null;
	sPlaca = sPlaca != null && sPlaca.length() == 0 ? null : sPlaca;

	// XXX: FELIPE: Alterado para multiplas pistas
	String[] sPista = request.getParameterValues("pista");
	ArrayList<String> list_sPista = new ArrayList<String>(
			Arrays.asList(sPista));
	ArrayList<Integer> list_iPista = new ArrayList<Integer>();
	if (!list_sPista.contains("0")) {
		for (String item_pista : list_sPista) {
			list_iPista.add(Integer.parseInt(item_pista));
			logger.info("sPista = " + item_pista);
		}
	}
// 	Integer[] array_iPista = new Integer[list_iPista.size()];
// 	for(int t=0; t<list_iPista.size(); t++)
// 		array_iPista[t] = list_iPista.get(t);

	if (sIdVeiculoIni != null
			&& !ExpValida.LONGO.validar(sIdVeiculoIni)) {
		new MensagemJS(response)
				.showErro("Identificador de veículo inicial enviado inválido!");
		return;
	}
	if (sIdVeiculoFim != null
			&& !ExpValida.LONGO.validar(sIdVeiculoFim)) {
		new MensagemJS(response)
				.showErro("Identificador de veículo final enviado inválido!");
		return;
	}
	if ((sIdVeiculoIni != null && sIdVeiculoFim == null)
			|| (sIdVeiculoFim != null && sIdVeiculoIni == null)) {
		new MensagemJS(response)
				.showErro("Limites de indentificador de veículo incompletos!");
		return;
	}
	if (sDataVeiculoIni != null
			&& !Pattern
					.matches(
							"([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",
							sDataVeiculoIni)) {
		new MensagemJS(response)
				.showErro("Data inicial do veículo enviada inválida!");
		return;
	}
	if (sDataVeiculoFim != null
			&& !Pattern
					.matches(
							"([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",
							sDataVeiculoFim)) {
		new MensagemJS(response)
				.showErro("Data inicial do veículo enviada inválida!");
		return;
	}
	if (sHoraVeiculoIni != null
			&& !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])",
					sHoraVeiculoIni)) {
		new MensagemJS(response)
				.showErro("Hora inicial do veículo enviada inválida!");
		return;
	}
	if (sHoraVeiculoFim != null
			&& !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])",
					sHoraVeiculoFim)) {
		new MensagemJS(response)
				.showErro("Hora final do veículo enviada inválida!");
		return;
	}
	if ((sDataVeiculoIni != null || sHoraVeiculoIni != null
			|| sDataVeiculoFim != null || sHoraVeiculoFim != null)
			&& (sDataVeiculoIni == null || sHoraVeiculoIni == null
					|| sDataVeiculoFim == null || sHoraVeiculoFim == null)) {
		new MensagemJS(response)
				.showErro("Período de infração incompleto!");
		return;
	}
	if (sIdVeiculoLocalIni != null
			&& !Pattern.matches("[1-9][0-9]{0,7}", sIdVeiculoLocalIni)) {
		new MensagemJS(response)
				.showErro("Identificador de imagem inicial enviado inválido!");
		return;
	}
	if (sIdVeiculoLocalFim != null
			&& !Pattern.matches("[1-9][0-9]{0,7}", sIdVeiculoLocalFim)) {
		new MensagemJS(response)
				.showErro("Identificador de imagem final enviado inválido!");
		return;
	}
	if ((sIdVeiculoLocalIni != null && sIdVeiculoLocalFim == null)
			|| (sIdVeiculoLocalFim != null && sIdVeiculoLocalIni == null)) {
		new MensagemJS(response)
				.showErro("Limites de indentificador de imagem incompletos!");
		return;
	}
	if (sLocal == null || !Pattern.matches("[0-9]{1,8}", sLocal)) {
		new MensagemJS(response)
				.showErro("Local selecionado inválido!");
		return;
	}
// 	if (sPista == null || !Pattern.matches("[0-9]{1,8}", sPista)) {
// 		new MensagemJS(response)
// 				.showErro("Pista selecionada inválida!");
// 		return;
// 	}
	if (sClasse == null || sClasse.length() != 1) {
		new MensagemJS(response)
				.showErro("Classe selecionada inválida!");
		return;
	}
	if (sPlaca != null && !ExpValida.PLACA.validar(sPlaca) && !ExpValida.PLACA_MERCOSUL.validar(sPlaca)) {
		new MensagemJS(response).showErro("Placa enviada inválida!");
		return;
	}
	if (sImgTeste != null && !sImgTeste.equals("1")) {
		new MensagemJS(response)
				.showErro("Tipo de imagem selecionado inválido!");
		return;
	}
	if (sImgIrregular != null && !sImgIrregular.equals("1")) {
		new MensagemJS(response)
				.showErro("Tipo de imagem selecionado inválido!");
		return;
	}
	if (sImgInfrator != null && !sImgInfrator.equals("1")) {
		new MensagemJS(response)
				.showErro("Tipo de imagem selecionado inválido!");
		return;
	}

	if (sIdVeiculoIni == null && sDataVeiculoIni == null
			&& sIdVeiculoFim == null && sIdVeiculoLocalIni == null
			&& sPlaca == null) {
		new MensagemJS(response)
				.showErro("Pesquisa muito abregente, selecione um filtro!");
		return;
	}

	Map<String, Object> mFiltro = new HashMap<String, Object>();

	if (sIdVeiculoIni != null) {
		mFiltro.put("id_veiculo_ini", Long.parseLong(sIdVeiculoIni));
		mFiltro.put("id_veiculo_fim", Long.parseLong(sIdVeiculoFim));
	}
	if (sDataVeiculoIni != null) {
		mFiltro.put(
				"data_veiculo_ini",
				new Timestamp(new SimpleDateFormat(
						"dd/MM/yyyy HH:mm:ss")
						.parse(sDataVeiculoIni + " " + sHoraVeiculoIni
								+ ":00").getTime()));
		mFiltro.put(
				"data_veiculo_fim",
				new Timestamp(new SimpleDateFormat(
						"dd/MM/yyyy HH:mm:ss")
						.parse(sDataVeiculoFim + " " + sHoraVeiculoFim
								+ ":59").getTime()));
	}
	if (sIdVeiculoLocalIni != null) {
		mFiltro.put("id_veiculo_local_ini",
				Integer.parseInt(sIdVeiculoLocalIni));
		mFiltro.put("id_veiculo_local_fim",
				Integer.parseInt(sIdVeiculoLocalFim));
	}
	if (sPlaca != null)
		mFiltro.put("placa", sPlaca);
	if (Integer.parseInt(sLocal) > 0)
		mFiltro.put("id_local", Integer.parseInt(sLocal));
	if (list_iPista.size() > 0)
		mFiltro.put("pista", list_iPista);
	if (sClasse.length() == 1 && !sClasse.equals(" "))
		mFiltro.put("id_classe", sClasse);

	if (sImgTeste != null && Integer.parseInt(sImgTeste) == 1)
		mFiltro.put("img_teste", null);
	if (sImgIrregular != null && Integer.parseInt(sImgIrregular) == 1)
		mFiltro.put("img_irregular", null);
	
	boolean infrator = false;
	
	if (sImgInfrator != null && Integer.parseInt(sImgInfrator) == 1)
	{
		mFiltro.put("img_infrator", null);
		infrator = true;
	}
	
	List<VeiculoCompletoLista> veiculos = null;
	try {
		veiculos = VeiculoCompletoLista.buscaVeiculoCompletoPor(mFiltro, infrator);
	} catch (Exception err) {
		logger.error("Erro ao pesquisar veículo: " + err.getMessage(), err);
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
<%@page import="com.consilux.model.VeiculoCompletoLista"%>
<%@page import="com.consilux.infra.ExpValida"%>

<%@page import="org.apache.log4j.Logger"%><c:set var="veiculos"
	value="<%=veiculos%>" />
<script type="text/javascript">
	function encadeiaVeiculoImagem(idVeiculoAtual, idVeiculoAnterior) {
		var link_imagem_anterior = document.getElementById("link_imagem_"
				+ idVeiculoAnterior);
		var link_imagem_atual = document.getElementById("link_imagem_"
				+ idVeiculoAtual);

		if (link_imagem_anterior) {
			link_imagem_anterior.idVeiculoProximo = idVeiculoAtual;
			link_imagem_atual.idVeiculoAnterior = idVeiculoAnterior;
		}
		link_imagem_atual.idVeiculo = idVeiculoAtual;
	}
	function mostraImagem(idVeiculo) {
		window.open("/ferramenta/veiculo_info.jsp?id_veiculo=" + idVeiculo
				+ "&encadeado", "Informações do Veículo",
				"width=750, height=700");
	}
	function getIdVeiculoAnterior(idVeiculo) {
		var ret = 0;
		var link_imagem = document.getElementById("link_imagem_" + idVeiculo);

		if (link_imagem) {
			ret = link_imagem.idVeiculoAnterior;
		}
		return ret;
	}
	function getIdVeiculoProximo(idVeiculo) {
		var ret = 0;
		var link_imagem = document.getElementById("link_imagem_" + idVeiculo);

		if (link_imagem) {
			ret = link_imagem.idVeiculoProximo;
		}
		return ret;
	}
</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="100%">
				<tr>
					<th class="head_tabela" width="6%">Nº Veículo</th>
					<th class="head_tabela" width="7%">Nº Veículo Local</th>
					<th class="head_tabela" width="2%">Pista</th>
					<th class="head_tabela" width="4%">Cod. Local.</th>
					<th class="head_tabela" width="23%">Nome Local</th>
					<th class="head_tabela" width="9%">Data Captura</th>
					<th class="head_tabela" width="7%">Placa</th>
					<th class="head_tabela" width="4%">Vel.</th>
					<th class="head_tabela" width="4%">Compr.</th>
					<th class="head_tabela" width="3%">Flag</th>
					<th class="head_tabela" width="9%">Classe</th>
					<th class="head_tabela" width="5%">Ocupação</th>
					<th class="head_tabela" width="4%">PBT</th>
					<th class="head_tabela" width="4%">Nº Eixos</th>
					<th class="head_tabela" width="4%">Classificação</th>
					<th class="head_tabela" width="5%">Ação</th>
				</tr>
				<c:set var="contaRegistros" value="0" />
				<c:set var="idVeiculoAnterior" value="" />
				<c:forEach var="veiculo" varStatus="linhaInfo" items="${veiculos}">
					<tr>
						<c:set var="css_td"
							value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						<td class="${css_td}" align="center">${veiculo.id}</td>
						<td class="${css_td}" align="center">${veiculo.idVeiculoLocal}</td>
						<td class="${css_td}" align="center">${veiculo.pista}</td>
						<td class="${css_td}" align="center">${veiculo.idLocal}</td>
						<td class="${css_td}" align="center">${veiculo.nomeLocal}</td>
						<td class="${css_td}" align="center"><fmt:formatDate value="${veiculo.data}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td class="${css_td}" align="center">${veiculo.placa}</td>
						<td class="${css_td}" align="center">${veiculo.velocidade}</td>
						<td class="${css_td}" align="center">${veiculo.comprimento}</td>
						<td class="${css_td}" align="center">${veiculo.strFlag}</td>
						<td class="${css_td}" align="center">${veiculo.nomeClasse}</td>
						<td class="${css_td}" align="center">${veiculo.ocupacao}</td>
						<td class="${css_td}" align="center">${veiculo.pbt != null && veiculo.pbt > 0 ? veiculo.pbt : ''}</td>
						<td class="${css_td}" align="center">${veiculo.numeroEixos != null && veiculo.numeroEixos > 0 ? veiculo.numeroEixos.toString() : ''}</td>
						<td class="${css_td}" align="center">${veiculo.classificacao != null ? veiculo.classificacao : ''}</td>
						<td class="${css_td}" align="center"><c:if
								test="${veiculo.id > 0}">
								<a id="link_imagem_${veiculo.id}" class='link_td'
									href="javascript:mostraImagem(${veiculo.id})">[+&nbsp;info]</a>
								<script type="text/javascript">
									encadeiaVeiculoImagem('${veiculo.id}',
											'${idVeiculoAnterior}')
								</script>
							</c:if></td>
						<c:set var="contaRegistros" value="${linhaInfo.count}" />
						<c:set var="idVeiculoAnterior" value="${veiculo.id}" />
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
	var div_conta_registros = parent.document
			.getElementById("div_conta_registros");
	if (div_conta_registros)
		div_conta_registros.innerHTML = '${contaRegistros}';
</script>
<%@ include file="/includes/rodape.jsp"%>
