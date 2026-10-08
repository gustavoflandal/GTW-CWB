var ATIVO = true;
var EDITAR = false;
var ID = null;
var POSSUI_ALERTA = false;
var ID_TIPO_REGISTRO = 'E7D115B9-E6B3-4E86-9083-F347A1917045';
var ID_TIPO_ALERTA_EDIT = '';
var IDS_GRUPOS_EDIT = [];
var USUARIO_ID = '';
var diasSemanaEdit = [
	{ valor: '1', texto: 'Domingo' },
	{ valor: '2', texto: 'Segunda-feira' },
	{ valor: '3', texto: 'Terça-feira' },
	{ valor: '4', texto: 'Quarta-feira' },
	{ valor: '5', texto: 'Quinta-feira' },
	{ valor: '6', texto: 'Sexta-feira' },
	{ valor: '7', texto: 'Sábado' }
];

$().ready(function() {	
	
		
	$('.date').mask('00/00/0000');

	$('.nomemonitorado').on('keypress', function(e) {
		var input = $(this);
		var value = input.val();
		var key = e.originalEvent.key;

		value += key;
		

		try {
			var pattern = /^(([A-Za-z0-9 _()-.@\[\]]{0,85}))$/i;
			var ok = pattern.test(value);

			if (!ok) e.preventDefault();
		}
		catch (e) {
			//HandleErrorMessages(e); 
		}
	});
});

async function AbrirEditarMonitorado(id, editar, possuiAlerta) {
	EDITAR = editar;
	ID = id;
	POSSUI_ALERTA = possuiAlerta;
	$("body").addClass("loading");

	var checkboxTempo = document.getElementById("checkboxTempoEdit");
	var intervaloTempoInicio = document.getElementById("intervaloTempoInicioEdit");
	var intervaloTempoFim = document.getElementById("intervaloTempoFimEdit");
	//var botaoSalvarModal = document.getElementById("btnSalvarModalEditar");
	var avisoIntervaloTempo = document.getElementById("dateTimeAdvise");

	try {
		ObterListaUsuariosEdit();
		irParaPrimeiraAbaEditarMonitoradoEdit();
		limparTodosCamposModalEditar();
		DefinirTituloModal();
		await gerenciarPermissaoSupervisionadoEdit();
		await obterEquipamentosGenericoEdit();
		await obterTiposAlertasOcorrenciasEdit();
		await carregarDadosEdit();
		await obterVeiculoMonitoradoPorIdEdit();
		await onGerenciarAbaSupervisionadoEdit();
	} catch (erro) {
		$("body").removeClass("loading");
		console.error("Falha ao carregar grupos:", erro);
		alert("Erro ao carregar informações da tela");
	}

	try {
		$("body").removeClass("loading");
		$("#modalEditarMonitorado").modal("show");
	} catch (erro) {
		$("body").removeClass("loading");
		console.error("Falha ao carregar grupos:", erro);
		alert("Erro ao carregar informações auxiliares da tela");
	}
	DesabilitarControlesModalEditar();
	configuraTabelaHorariosPermitidosEdit(EDITAR);

	/* 
		Aqui é feito o reset das variáveis do Intervalo de Tempo para que o checkbox inicie desmarcado,
		os selects de início e fim sendo desabilitados, o botão de salvar habilitado e o aviso de erro escondido.
		Foi tentado ativar isso em uma função abaixo, porém algo não estava permitindo puxar com valor alterado
		então a solução foi iniciar assim. */
	checkboxTempo.checked = false;
	intervaloTempoInicio.disabled = true;
	intervaloTempoFim.disabled = true;
	//botaoSalvarModal.disabled = false;
	avisoIntervaloTempo.setAttribute("hidden", true);
}

function irParaPrimeiraAbaEditarMonitoradoEdit() {
	const primeiraAba = document.querySelector('#tabMonitoradoEdit button[data-bs-target="#tab-dados-edit"]');
	const tab = new bootstrap.Tab(primeiraAba);
	tab.show();
}

async function verificarPermissaoSupervisorEdit() {
	try {
		const xml = await $.ajax({
			type: "GET",
			url: "/MuralhaDigital/PermissoesFuncionalidade",
			data: {
				acao: "verificarPermissaoGrupo"
			},
			dataType: "xml"
		});

		const permitido = $(xml).find("permitido").text().toLowerCase() === "true";
		return permitido;

	} catch (error) {
		console.error("Erro ao verificar permissão de grupo!", error);
		return false; // segurança: nega se erro
	}
}

async function gerenciarPermissaoSupervisionadoEdit() {
	// 1. Verifica a permissão do usuário.
	const permitido = await verificarPermissaoSupervisorEdit();

	const $checkbox = $("#checkboxSupervisionadoEdit");

	if (permitido) {
		$checkbox.prop('disabled', false);
	} else {
		$checkbox.prop('disabled', true);
	}
}

function DefinirTituloModal() {
	var tituloModalEditarMonitorado = document.getElementById("tituloModalEditarMonitorado");

	if (EDITAR)
		tituloModalEditarMonitorado.innerHTML = "Editar Monitorado";
	else
		tituloModalEditarMonitorado.innerHTML = "Detalhar Monitorado";

}

function PopulaModalDetalheVeiculoMonitorado(
	idTipoAlertaOcorrencia,
	placa,
	dataInicioFormatada,
	dataFimFormatada,
	descricao,
	nome,
	id_usuario_responsavel,
	monitorar_somente_este,
	idClasse,
	idCor,
	idMarca,
	idModelo,
	textoAdesivo
) {
	var idNomeEdit = document.getElementById("idNomeEdit");
	var chkAtivoEdit = document.getElementById("cadastroAtivoEdit");
	var idPlacaEdit = document.getElementById("idPlacaEdit");
	var selTipoAlertaOcorrenciaEdit = document.getElementById("selTipoAlertaOcorrenciaEdit");
	var dataInicioEdit = document.getElementById("dataInicioEdit");
	var dataFimEdit = document.getElementById("dataFimEdit");
	var descricaoEdit = document.getElementById("descricaoEdit");

	// Dados do veículo
	var selClasseVeiculoEdit = document.getElementById("selClasseVeiculoEdit");
	var selCorEdit = document.getElementById("selCorEdit");
	var selMarcaEdit = document.getElementById("selMarcaEdit");
	var selModeloEdit = document.getElementById("selModeloEdit");
	var textoAdesivoEdit = document.getElementById("textoAdesivoEdit");

	$("#selUsuarioResponsavelEdit").val(id_usuario_responsavel);

	console.log(id_usuario_responsavel);
	console.log(`\nmonitorar_somente_este = ${monitorar_somente_este}\n`);

	$("#checkboxMonitorarSomenteEsteEdit").prop("checked", monitorar_somente_este);

	if (idNomeEdit)
		idNomeEdit.value = nome;

	if (chkAtivoEdit)
		chkAtivoEdit.checked = ATIVO;

	if (idPlacaEdit)
		idPlacaEdit.value = placa;

	if (selTipoAlertaOcorrenciaEdit)
		selTipoAlertaOcorrenciaEdit.value = idTipoAlertaOcorrencia;

	if (dataInicioEdit)
		dataInicioEdit.value = dataInicioFormatada;

	if (dataFimEdit)
		dataFimEdit.value = dataFimFormatada;

	if (descricaoEdit)
		descricaoEdit.value = descricao;

	// Dados do veículo
	if (selClasseVeiculoEdit)
		selClasseVeiculoEdit.value = idClasse || "0";

	if (selCorEdit)
		selCorEdit.value = idCor || "0";

	if (selMarcaEdit)
		selMarcaEdit.value = idMarca || "0";

	if (selModeloEdit)
		selModeloEdit.value = idModelo || "0";

	if (textoAdesivoEdit)
		textoAdesivoEdit.value = textoAdesivo || "";

	// Marca e Modelo utilizam bootstrap-select
	$("#selMarcaEdit").selectpicker("refresh");
	$("#selModeloEdit").selectpicker("refresh");
}

function limparTodosCamposModalEditar() {
	var nome = document.getElementById("idNomeEdit");
	var chkCadastroAtivo = document.getElementById("cadastroAtivoEdit");
	var tipoAlertaOcorrencia = document.getElementById("selTipoAlertaOcorrenciaEdit");
	var placa = document.getElementById("idPlacaEdit");
	var dataIni = document.getElementById("dataInicioEdit");
	var dataFim = document.getElementById("dataFimEdit");
	var descricao = document.getElementById("descricaoEdit");
	
	// Dados do veículo
	var selClasseVeiculoEdit = document.getElementById("selClasseVeiculoEdit");
	var selCorEdit = document.getElementById("selCorEdit");
	var textoAdesivoEdit = document.getElementById("textoAdesivoEdit");

	if (selClasseVeiculoEdit)
		selClasseVeiculoEdit.value = "";

	if (selCorEdit)
		selCorEdit.value = "";

	if (textoAdesivoEdit)
		textoAdesivoEdit.value = "";

	// Marca
	$("#selMarcaEdit").val("");
	$("#selMarcaEdit").selectpicker("refresh");

	// Modelo
	$("#selModeloEdit").val("");
	$("#selModeloEdit").selectpicker("refresh");

	if (tipoAlertaOcorrencia)
		tipoAlertaOcorrencia.value = 0;

	if (nome)
		nome.value = "";

	if (chkCadastroAtivo)
		chkCadastroAtivo.checked = true;

	if (placa)
		placa.value = "";

	if (dataIni)
		dataIni.value = "";

	if (dataFim)
		dataFim.value = "";

	if (descricao)
		descricao.value = "";
}

async function limparCamposEdit() {
	var dataFim = document.getElementById("dataFimEdit");
	var descricao = document.getElementById("descricaoEdit");

	if (dataFim)
		dataFim.value = "";

	if (descricao)
		descricao.value = "";

	var chkPrivado = document.getElementById("checkboxPrivadoEdit");
	var chkSuperv = document.getElementById("checkboxSupervisionadoEdit");
	var nivelPlaca = document.getElementById("nivelSemelhancaPlacaEdit");
	var intervaloTempoInicio = document.getElementById("intervaloTempoInicioEdit");
	var intervaloTempoFim = document.getElementById("intervaloTempoFimEdit");
	var chkMonitorarSomenteEste = document.getElementById("checkboxMonitorarSomenteEsteEdit");

	if (chkMonitorarSomenteEste) chkMonitorarSomenteEste.checked = false;

	if (chkPrivado)
		chkPrivado.checked = false;

	if (chkSuperv)
		chkSuperv.checked = false;

	if (nivelPlaca)
		nivelPlaca.value = "0";
	
	// Dados do veículo
	var selClasseVeiculoEdit = document.getElementById("selClasseVeiculoEdit");
	var selCorEdit = document.getElementById("selCorEdit");
	var textoAdesivoEdit = document.getElementById("textoAdesivoEdit");

	if (selClasseVeiculoEdit)
		selClasseVeiculoEdit.value = "0";

	if (selCorEdit)
		selCorEdit.value = "0";

	if (textoAdesivoEdit)
		textoAdesivoEdit.value = "";

	// Marca
	$("#selMarcaEdit").val("0");
	$("#selMarcaEdit").selectpicker("refresh");

	// Modelo
	$("#selModeloEdit").val("0");
	$("#selModeloEdit").selectpicker("refresh");

	/*
		Aqui abaixo é feito um if para, quando o usuário clicar em limpar campos, o valor dos selects pego pelo ID acima,
		será resetado para o padrão que é '00:00' para a hora de início e '00:59' para a hora de fim.
	*/
	if (intervaloTempoInicio)
		intervaloTempoInicio.value = "00";

	if (intervaloTempoFim)
		intervaloTempoFim.value = "00";

	irParaPrimeiraAbaEditarMonitoradoEdit();
	// Oculta aba de grupos (caso queira esconder o conteúdo da aba)
	const liTabGrupos = document.getElementById('li-tab-grupos-edit');
	liTabGrupos.classList.add('d-none');

	if(await verificarPermissaoGrupoUsuarioEdit())
	{
		$('#selEquipamentoEdit option').prop('selected', false);
		$('#selEquipamentoEdit').selectpicker('refresh');
		$('#selAcaoGrupoPopupEdit option').prop('selected', false);
		$('#selAcaoGrupoPopupEdit').selectpicker('refresh');
		// Limpa linhas da tabela de horários
		$("#tabelaHorariosPermitidosEdit tbody").empty();
		
		IDS_GRUPOS_EDIT = [];
	}
}

/*
	Aqui abaixo é feito uma validação para verificar o checkbox do Intervalo de Tempo, ele começa desmarcado no front, então é pego os valores
	escolhido pelo usuário, caso o checkbox seja marcado e a hora de início seja maior ou igual a hora de fim, o botão de salvar ficará
	desabilitado e um aviso de erro ficará desabilitado, caso a hora de início seja menor que a hora de fim, tudo ficará ativo normalmente,
	por fim, caso o checkbox esteja desmarcado, os selects ficarão desabilitados e o botão de salvar ficará ativo normalmente.
*/
function verificacaoIntervaloTempoEdit() {
	var checkboxTempo = document.getElementById("checkboxTempoEdit");
	var intervaloTempoInicio = document.getElementById("intervaloTempoInicioEdit");
	var intervaloTempoFim = document.getElementById("intervaloTempoFimEdit");
	var botaoSalvarModal = document.getElementById("btnSalvarModalEditar");
	var avisoIntervaloTempo = document.getElementById("dateTimeAdvise");

	if(checkboxTempo.checked == true) {
		intervaloTempoInicio.disabled = false;
		intervaloTempoFim.disabled = false;
		
		if(parseInt(intervaloTempoInicio.value) >= parseInt(intervaloTempoFim.value)) {
			botaoSalvarModal.disabled = true;
			avisoIntervaloTempo.removeAttribute("hidden");
		}
		else {
			botaoSalvarModal.disabled = false;
			avisoIntervaloTempo.setAttribute("hidden", true);
		}
	}
	else {
		intervaloTempoInicio.disabled = true;
		intervaloTempoFim.disabled = true;
		botaoSalvarModal.disabled = false;
		avisoIntervaloTempo.setAttribute("hidden", true);
	}
}

function DesabilitarControlesModalEditar() {
	// Desabilita controles dos modais
	document.getElementById("idPlacaEdit").disabled = true;
	document.getElementById("selTipoAlertaOcorrenciaEdit").disabled = true;
	document.getElementById("dataInicioEdit").disabled = true;
	document.getElementById("checkboxMonitorarSomenteEsteEdit").disabled = !EDITAR;

	if (!POSSUI_ALERTA) {
		document.getElementById("btnVerAlertasModalEditar").disabled = false;
	}

	if (EDITAR) {
		document.getElementById("idNomeEdit").disabled = false;
		document.getElementById("cadastroAtivoEdit").disabled = (ATIVO ? false : true);
		document.getElementById("dataFimEdit").disabled = false;
		document.getElementById("descricaoEdit").disabled = false;
		document.getElementById("btnLimparCamposModalEditar").disabled = false;
		document.getElementById("btnSalvarModalEditar").disabled = false;

		gerenciarPermissaoPrivadoEdit();

		document.getElementById("nivelSemelhancaPlacaEdit").disabled = false;
		document.getElementById("intervaloTempoInicioEdit").disabled = false;
		document.getElementById("intervaloTempoFimEdit").disabled = false;
		document.querySelector("#selUsuarioResponsavelEdit").disabled = false;
		document.getElementById("btLinhaHorarioEdit").disabled = false;

		// Dados do veículo
		document.getElementById("selClasseVeiculoEdit").disabled = false;
		document.getElementById("selCorEdit").disabled = false;
		document.getElementById("textoAdesivoEdit").disabled = false;

		// Marca e Modelo utilizam selectpicker
		const $selMarcaEdit = $("#selMarcaEdit");
		$selMarcaEdit.prop("disabled", false);
		$selMarcaEdit.selectpicker("refresh").selectpicker("render");

		const $selModeloEdit = $("#selModeloEdit");
		$selModeloEdit.prop("disabled", false);
		$selModeloEdit.selectpicker("refresh").selectpicker("render");

		const $selEquipamentoEdit = $("#selEquipamentoEdit");
		$selEquipamentoEdit.prop("disabled", !EDITAR);
		$selEquipamentoEdit.selectpicker("refresh").selectpicker("render");

		const $selAcaoGrupoPopupEdit = $("#selAcaoGrupoPopupEdit");
		$selAcaoGrupoPopupEdit.prop("disabled", !EDITAR);
		$selAcaoGrupoPopupEdit.selectpicker("refresh").selectpicker("render");
	}
	else {
		document.getElementById("idNomeEdit").disabled = true;
		document.getElementById("cadastroAtivoEdit").disabled = true;
		document.getElementById("dataFimEdit").disabled = true;
		document.getElementById("descricaoEdit").disabled = true;
		document.getElementById("btnLimparCamposModalEditar").disabled = true;
		document.getElementById("btnSalvarModalEditar").disabled = true;
		document.getElementById("checkboxPrivadoEdit").disabled = true;
		document.getElementById("checkboxSupervisionadoEdit").disabled = true;
		document.getElementById("nivelSemelhancaPlacaEdit").disabled = true;
		document.getElementById("intervaloTempoInicioEdit").disabled = true;
		document.getElementById("intervaloTempoFimEdit").disabled = true;

		// Dados do veículo
		document.getElementById("selClasseVeiculoEdit").disabled = true;
		document.getElementById("selCorEdit").disabled = true;
		document.getElementById("textoAdesivoEdit").disabled = true;

		// Marca e Modelo utilizam selectpicker
		const $selMarcaEdit = $("#selMarcaEdit");
		$selMarcaEdit.prop("disabled", true);
		$selMarcaEdit.selectpicker("refresh").selectpicker("render");

		const $selModeloEdit = $("#selModeloEdit");
		$selModeloEdit.prop("disabled", true);
		$selModeloEdit.selectpicker("refresh").selectpicker("render");

		document.getElementById("selAcaoGrupoPopupEdit").disabled = true;
		document.getElementById("selEquipamentoEdit").disabled = true;

		document.getElementById("btLinhaHorarioEdit").disabled = true;
		document.querySelector("#selUsuarioResponsavelEdit").disabled = true;
	}

	document.querySelectorAll("#selUsuarioResponsavelEdit").forEach(function(el) {
		el.disabled = !EDITAR;
	});
}

function  AbrirListaAlertasCadMonitorado(){
	if (ID != null) {window.open(
            "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?cad-veiculo-monitorado=" + ID,
            "_blank" // abre em nova aba
      );
      /*  window.location.href = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?cad-veiculo-monitorado=" + ID;*/
   } else {
      alert("ID do veículo monitorado não encontrado.");
	}
}

function gerenciarPermissaoPrivadoEdit() {
	if (!window.usuarioID) {
		console.warn("ID de usuário não definido!");
		return;
	}

	if (USUARIO_ID === window.usuarioID) {
		document.getElementById("checkboxPrivadoEdit").disabled = false;
	} else {
		document.getElementById("checkboxPrivadoEdit").disabled = true;
	}
}

function HabilitarControlesModalEditar() {
	document.getElementById("idPlacaEdit").disabled = false;
	document.getElementById("cadastroAtivoEdit").disabled = false;
	document.getElementById("selTipoAlertaOcorrenciaEdit").disabled = false;
	document.getElementById("dataInicioEdit").disabled = false;
	document.getElementById("dataFimEdit").disabled = false;
	document.getElementById("descricaoEdit").disabled = false;
	document.getElementById("btnLimparCamposModalEditar").disabled = false;
	document.getElementById("btnSalvarModalEditar").disabled = false;
	document.getElementById("btnVerAlertasModalEditar").disabled = false;

	document.getElementById("checkboxPrivadoEdit").disabled = false;

	document.getElementById("nivelSemelhancaPlacaEdit").disabled = false;
	document.getElementById("intervaloTempoInicioEdit").disabled = false;
	document.getElementById("intervaloTempoFimEdit").disabled = false;

	document.getElementById("selAcaoGrupoPopupEdit").disabled = false;
	document.getElementById("selEquipamentoEdit").disabled = false;
}

async function editarMonitorado() {
	var dataFim = document.getElementById("dataFimEdit").value;
	var descricao = document.getElementById("descricaoEdit").value;
	var nome = document.getElementById("idNomeEdit").value;
	var cadAtivo = document.getElementById("cadastroAtivoEdit").checked;

	var privadoCheckbox = document.getElementById("checkboxPrivadoEdit");
	var supervisionadoCheckbox = document.getElementById("checkboxSupervisionadoEdit");
	var errosPermitidosSelect = document.getElementById("nivelSemelhancaPlacaEdit");
	var usuario_responsavel = document.getElementById("selUsuarioResponsavelEdit").value;
	var intervaloTempoInicioSelect = document.getElementById("intervaloTempoInicioEdit").value;
	var intervaloTempoFimSelect = document.getElementById("intervaloTempoFimEdit").value;
	
	var selectTipoOcorrencia = document.getElementById("selTipoAlertaOcorrenciaEdit");
	//var tipoAlertaOcorrencia = selectTipoOcorrencia.value;
	var tipoAlertaOcorrenciatitulo = selectTipoOcorrencia.options[selectTipoOcorrencia.selectedIndex].text;
	var placa = document.getElementById("idPlacaEdit").value;
	
	// Dados do veículo
	var idClasse = document.getElementById("selClasseVeiculoEdit").value;
	var cor = document.getElementById("selCorEdit").value;
	var marca = document.getElementById("selMarcaEdit").value;
	var modelo = document.getElementById("selModeloEdit").value;
	var textoAdesivo = document.getElementById("textoAdesivoEdit").value;

	var selectUsuario_responsavel = document.getElementById("selUsuarioResponsavelEdit")
	var usuario_responsavel = selectUsuario_responsavel.value;
	var usuario_responsavel_nome = selectUsuario_responsavel.options[selectUsuario_responsavel.selectedIndex].text;

	var privado = privadoCheckbox ? privadoCheckbox.checked : false;
	var supervisionado = supervisionadoCheckbox ? supervisionadoCheckbox.checked : false;
	var errosPermitidos = errosPermitidosSelect ? parseInt(errosPermitidosSelect.value) : null;

	/*
		Aqui abaixo é feito uma verificação para o Intervalo de Tempo, como esse é o valor que enviará para o back,
		caso o select esteja com valor vazio, será gravado null, caso contrário gravará o valor, sendo o de início
		com ':00' no final e o de fim com ':59' no final.
	*/
	var intervaloTempoInicio = intervaloTempoInicioSelect ? (intervaloTempoInicioSelect + ":00") : null;
	var intervaloTempoFim = intervaloTempoFimSelect ? (intervaloTempoFimSelect + ":59") : null;

	var checkboxMonitorarSomenteEste = document.getElementById("checkboxMonitorarSomenteEsteEdit").checked;

	// Início da string de envio
	let dataStringPesquisa =
		"id=" + ID +
		"&nome=" + encodeURIComponent(nome) +
		"&dataFim=" + encodeURIComponent(dataFim) +
		"&descricao=" + encodeURIComponent(descricao) +
		"&cadAtivo=" + cadAtivo +
		"&privado=" + privado +
		"&supervisionado=" + supervisionado +
		"&monitorar_somente_este=" + (checkboxMonitorarSomenteEste ? "1" : "0") +
		"&usuario_responsavel=" + encodeURIComponent(usuario_responsavel) +
		"&tipoAlertaOcorrenciatitulo=" + encodeURIComponent(tipoAlertaOcorrenciatitulo) +
		"&usuario_responsavel_nome=" + encodeURIComponent(usuario_responsavel_nome) +
		"&placa=" + encodeURIComponent(placa) +
		"&idClasse=" + encodeURIComponent(idClasse) +
		"&cor=" + encodeURIComponent(cor) +
		"&marca=" + encodeURIComponent(marca) +
		"&modelo=" + encodeURIComponent(modelo) +
		"&textoAdesivo=" + encodeURIComponent(textoAdesivo) +
		"&erros_permitidos_placa=" + (errosPermitidos !== null ? encodeURIComponent(errosPermitidos) : "") +
		"&erros_permitido_ini=" + (intervaloTempoInicio !== null ? encodeURIComponent(intervaloTempoInicio) : "") +
		"&erros_permitido_fim=" + (intervaloTempoFim !== null ? encodeURIComponent(intervaloTempoFim) : "");
		console.log("xuxu ", dataStringPesquisa )
		

	// Sempre tenta capturar os dados da aba, mesmo que ela esteja oculta
	const gruposPopup = $('#selAcaoGrupoPopupEdit').val(); // array
	const equipamentosSelecionados = $('#selEquipamentoEdit').val(); // array

	let horarios = [];
	try {
		horarios = obterHorariosPermitidosEdit(); // pode lançar erro
	} catch (e) {
		Swal.fire({
			icon: 'error',
			title: 'Erro de Horário!',
			text: e.message,
		});
		return;
	}

	if (gruposPopup) {
		dataStringPesquisa += "&gruposPopup=" + encodeURIComponent(gruposPopup.join(","));
	}
	dataStringPesquisa += "&equipamentosLocais=" + encodeURIComponent(
		equipamentosSelecionados ? equipamentosSelecionados.join(",") : ""
	);
	dataStringPesquisa += "&horariosPermitidos=" + encodeURIComponent(JSON.stringify(horarios));

	dataStringPesquisa += "&acao=atualizar";

	// Requisição AJAX
	showLoadingOverlay();
	$.ajax({
		type: "POST",
		url: "/MuralhaDigital/Monitorado",
		data: dataStringPesquisa,
		dataType: "xml",

		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				hideLoadingOverlay();
				limparTodosCamposModalEditar();
				fecharModalEdit();
				executaPesquisa();
			} else {
				hideLoadingOverlay();
				WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, "#error_container_modal_editar_veiculo_mon");
			}
		},

		error: function(jqXHR, textStatus, errorThrown) {
			hideLoadingOverlay();
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', "#error_container_modal_editar_veiculo_mon");
		}
	});
}

async function verificarPermissaoGrupoUsuarioEdit() {
	try {
		const xml = await $.ajax({
			type: "GET",
			url: "/MuralhaDigital/PermissoesFuncionalidade",
			data: {
				acao: "verificarPermissaoGrupo"
			},
			dataType: "xml"
		});

		const permitido = $(xml).find("permitido").text().toLowerCase() === "true";
		return permitido;

	} catch (error) {
		console.error("Erro ao verificar permissão de grupo!", error);
		return false; // segurança: nega se erro
	}
}

function obterHorariosPermitidosEdit() {
	const linhas = document.querySelectorAll("#tabelaHorariosPermitidosEdit tbody tr");
	const horarios = [];

	for (const linha of linhas) {
		const dia = linha.querySelector("select").value;
		const horaInicio = linha.querySelectorAll("input[type='time']")[0].value;
		const horaFim = linha.querySelectorAll("input[type='time']")[1].value;

		// Se somente o dia estiver preenchido, descarta silenciosamente
		if (dia && !horaInicio && !horaFim) {
			continue;
		}

		if (!dia || !horaInicio || !horaFim) {
			throw new Error("Todos os campos de horário devem estar preenchidos.");
		}

		if (horaFim <= horaInicio) {
			throw new Error(`Hora final deve ser maior que a inicial no dia ${dia}.`);
		}

		horarios.push({ dia, horaInicio, horaFim });
	}

	// Verifica sobreposição
	const agrupadoPorDia = {};

	for (const h of horarios) {
		if (!agrupadoPorDia[h.dia]) agrupadoPorDia[h.dia] = [];
		agrupadoPorDia[h.dia].push(h);
	}
	for (const [dia, entradas] of Object.entries(agrupadoPorDia)) {
		entradas.sort((a, b) => a.horaInicio.localeCompare(b.horaInicio));

		for (let i = 1; i < entradas.length; i++) {
			const anterior = entradas[i - 1];
			const atual = entradas[i];

			if (atual.horaInicio < anterior.horaFim) {
				const nomeDia = diasSemanaCad.find(d => d.valor === dia)?.texto || `Dia ${dia}`;
				throw new Error(`Conflito de horário em ${nomeDia}: ${atual.horaInicio} sobrepõe ${anterior.horaFim}.`);
			}
		}
	}

	return horarios;
}

async function obterVeiculoMonitoradoPorIdEdit() {
	var dataString = "acao=obterPorId" +
		"&id=" + ID;

	var urlPesquisa = "/MuralhaDigital/Monitorado";

	//	console.log('ObterAlerta():: URL: ' + url + ' --> ' + dataString);

	return await $.ajax(
	{
		type: "GET",
		url: urlPesquisa,
		data: dataString,
		dataType: "xml",

		success: function(xml, textStatus, jqXHR) {
			console.log("esse? ",jqXHR)
			TratarRetornoVeicMonitorado(jqXHR);
		},

		error: function(e, b, error) {
			console.log("Erro ao obter Veículo Monitorado por Id." + e.respone);
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao obter Veículo Monitorado!!', "#error_container_modal_editar_veiculo_mon");
		},
		async: false
	});
}

async function TratarRetornoVeicMonitorado(jqXHR) {
	var xmlDoc = $.parseXML(jqXHR.responseText);
	var $xml = $(xmlDoc);

	var monitorado = $xml.find('VeiculoMonitorado');

	var idTipoAlertaOcorrencia = monitorado.find('idTipoAlertaOcorrencia').text();
	var placa = monitorado.find('placa').text();
	var dataInicioFormatada = monitorado.find('dataInicioFormatada').text() + " " + monitorado.find('horaInicioFormatada').text();
	var dataFimFormatada = monitorado.find('dataFimFormatada').text() + " " + monitorado.find('horaFimFormatada').text();
	var descricao = monitorado.find('descricao').text();
	var nome = monitorado.find('nome').text();
	var id_usuario_responsavel = monitorado.find('id_usuario_responsavel').text();
	var monitorar_somente_este = monitorado.find("monitorarSomenteEste").text() === "1";
	
	// Dados do veículo
	var idClasseElement = monitorado.find('idClasse');
	var idClasse = idClasseElement.length > 0
		? idClasseElement.text()
		: null;
	var idCor = monitorado.find('idCor').text();
	var idMarca = monitorado.find('idMarca').text();
	var idModelo = monitorado.find('idModelo').text();
	var textoAdesivo = monitorado.find('textoAdesivo').text();
	
	ATIVO = monitorado.find('ativo').text() === 'true';
	USUARIO_ID = $(monitorado).find('> idUsuario').text();
	// Privado
	const privado = $xml.find("privado").text() === "true";
	$("#checkboxPrivadoEdit").prop("checked", privado);

	// Supervisionado
	const supervisionado = $xml.find("supervisionado").text() === "true";
	$("#checkboxSupervisionadoEdit").prop("checked", supervisionado);

	extrairGruposDoMonitorado(jqXHR.responseText);
	extrairHorariosPermitidosDoMonitorado(jqXHR.responseText);
	extrairEquipamentosDoMonitorado(jqXHR.responseText);

	const errosPermitidos = $xml.find("errosPermitidosPlaca").text();
	$("#nivelSemelhancaPlacaEdit").val(errosPermitidos || "0");

	/*
		Aqui abaixo é feito a chamada do Intervalo de Tempo, pegando os valores do XML e formatando para texto,
		então ele pega os 2 primeiros caracteres (de 00 até 23) e popula no select correspondente.
	*/
	var intervaloTempoInicio = $xml.find("errosPermitidosIni").text();
	var intervaloTempoFim = $xml.find("errosPermitidosFim").text();

	if(intervaloTempoInicio) {
		intervaloTempoInicio = intervaloTempoInicio.substring(0, 2);
	}
	if(intervaloTempoFim) {
		intervaloTempoFim = intervaloTempoFim.substring(0, 2);
	}

	/*
		Aqui abaixo é feito a população dos selects de Intervalo de Tempo, que será o valor extraído do XML ou,
		quando não houver valor nenhum, será populado '00' como padrão tanto para início quanto para fim.
	*/
	$("#intervaloTempoInicioEdit").val(intervaloTempoInicio || '00');
	$("#intervaloTempoFimEdit").val(intervaloTempoFim || '00');

	PopulaModalDetalheVeiculoMonitorado(
		idTipoAlertaOcorrencia,
		placa,
		dataInicioFormatada,
		dataFimFormatada,
		descricao,
		nome,
		id_usuario_responsavel,
		monitorar_somente_este,
		idClasse,
		idCor,
		idMarca,
		idModelo,
		textoAdesivo
	);
}

function extrairGruposDoMonitorado(xmlString) {
	var xmlDoc = $.parseXML(xmlString);
	var $xml = $(xmlDoc);

	IDS_GRUPOS_EDIT = []; // zera antes

	$xml.find('grupos').each(function() {
		var idGrupo = parseInt($(this).text());
		IDS_GRUPOS_EDIT.push(idGrupo);
	});
}

function extrairEquipamentosDoMonitorado(xmlString) {
	const xmlDoc = $.parseXML(xmlString);
	const $xml = $(xmlDoc);

	const idsEquipamentos = [];

	$xml.find('equipamentosEntidade').each(function () {
		const idLocal = $(this).find('idLocal').text();
		if (idLocal) {
			idsEquipamentos.push(idLocal);
		}
	});

	// Marca apenas os IDs que já existem no combo
	const $select = $('#selEquipamentoEdit');
	const optionsExistentes = $select.find('option').map(function () {
		return this.value;
	}).get();

	const idsValidos = idsEquipamentos.filter(id => optionsExistentes.includes(id));

	$select.val(idsValidos);
	$select.selectpicker('refresh');
}

function extrairHorariosPermitidosDoMonitorado(xmlString) {
	var xmlDoc = $.parseXML(xmlString);
	var $xml = $(xmlDoc);

	$('#tabelaHorariosPermitidosEdit tbody').empty(); // limpa antes

	const horarios = $xml.find('horariosEntidade');

	if (horarios.length === 0 && EDITAR == true) {
		adicionarLinhaHorarioEdit(); // adiciona linha vazia
	} else {
		horarios.each(function() {
			const id = $(this).find('id').text(); // <-- extrai o ID
			const diaSemana = parseInt($(this).find('diaSemana').text());
			const horaInicio = $(this).find('horaInicio').text();
			const horaFim = $(this).find('horaFim').text();

			adicionarLinhaHorarioEdit(diaSemana, horaInicio, horaFim, id); // <-- passa o ID
		});
	}
}


function carregaComboTiposAlertasOcorrenciasModalEdit(event) {
	var xmlDoc = $.parseXML(event);
	var $xml = $(xmlDoc);

	$("#selTipoAlertaOcorrenciaEdit").empty();

	$xml.find('TipoAlertaOcorrencia').each(function() {

		var $item = $(this);

		var id = $item.find('id').text();
		var tipo = $item.find('tipo').text();

		$('#selTipoAlertaOcorrenciaEdit').append($('<option>', {
			value: id,
			text: tipo
		}));
	});
}

function AtivarInativarEdit() {
	var cadastroAtivoEdit = document.getElementById("cadastroAtivoEdit");
	var dataFimEdit = document.getElementById("dataFimEdit");

	if (cadastroAtivoEdit && dataFimEdit) {
		if (cadastroAtivoEdit.checked) {
			dataFimEdit.value = "";
			dataFimEdit.disabled = false;
		}
		else {
			const dataFim = new Date();
			dataFimEdit.value = dataFim.toLocaleDateString("pt-BR");
			dataFimEdit.disabled = true;
		}
	}
}

function fecharModalEdit() {
	$('#modalEditarMonitorado').modal('hide');
}

function toggleSupervisionadoExtrasEdit(checkbox) {
	const extrasDiv = document.getElementById('supervisionadoExtrasEdit');
	extrasDiv.classList.toggle('d-none', !checkbox.checked);
	onGerenciarAbaSupervisionadoEdit();
}

async function onGerenciarAbaSupervisionadoEdit() {
	const checkboxSupervisionado = document.getElementById("checkboxSupervisionadoEdit");
	const liTabGrupos = document.getElementById('li-tab-grupos-edit');
	const select = document.getElementById('selTipoAlertaOcorrenciaEdit');
	const valor = select.value;
	const permi = await verificarPermissaoSupervisorEdit();

	if (!checkboxSupervisionado.checked || !permi) {
		liTabGrupos.classList.add('d-none');
		return;
	}

	if ((valor !== "0" && valor !== "")) {

		liTabGrupos.classList.remove('d-none');
		ID_TIPO_ALERTA_EDIT = valor;
		ObterGruposPopupMoniEdit();
	} else {
		liTabGrupos.classList.add('d-none');

		// Volta para a aba principal se a de grupos estiver ativa
		const tabDados = new bootstrap.Tab(document.querySelector('#tab-dados-tab-edit'));
		tabDados.show();

		// Limpa os selects
		const popupSelect = document.getElementById('selAcaoGrupoPopupEdit');
		const equipamentoSelect = document.getElementById('selEquipamentoEdit');
		if (popupSelect) {
			popupSelect.innerHTML = ""; // remove opções
			$(popupSelect).selectpicker('refresh');
		}

		if (equipamentoSelect) {
			equipamentoSelect.innerHTML = ""; // remove opções
			$(equipamentoSelect).selectpicker('refresh');
		}
	}
}
function ObterGruposPopupMoniEdit() {
	var dataString = "acao=obterGruposPopup" +
		"&tipoRegistro=" + ID_TIPO_REGISTRO +
		"&tipoAlertaOcorrencia=" + ID_TIPO_ALERTA_EDIT;

	var url = urlRoot + "MuralhaDigital/GrupoNotificacao";

	$.ajax({
		type: "GET",
		url: url,
		data: dataString,
		dataType: "xml",

		//if received a response from the server
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				carregaComboGruposPopupEdit(jqXHR.responseText);
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
		},

		error: function(jqXHR, textStatus, errorThrown) {
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}

function carregaComboGruposPopupEdit(event) {
	var xmlDoc = $.parseXML(event);
	var $xml = $(xmlDoc);

	var $combo = $('#selAcaoGrupoPopupEdit');
	$combo.empty();

	$xml.find('GrupoNotificacao').each(function() {
		var $item = $(this);

		var idGrupo = parseInt($item.find('idGrupo').text());
		var grupo = $item.find('grupo').text();

		var $option = $('<option>', {
			value: idGrupo,
			text: grupo
		});

		if (IDS_GRUPOS_EDIT.includes(idGrupo)) {
			$option.prop('selected', true);
		}

		$combo.append($option);
	});

	$combo.selectpicker('refresh');
}

function adicionarLinhaHorarioEdit(diaSemana = '', horaInicio = '', horaFim = '', id = '') {
	const tbody = document.querySelector("#tabelaHorariosPermitidosEdit tbody");

	// Monta o select com dias da semana
	let selectDias = `<select class="form-select form-select-sm">`;
	diasSemanaEdit.forEach(dia => {
		const selected = String(dia.valor) === String(diaSemana) ? 'selected' : '';
		selectDias += `<option value="${dia.valor}" ${selected}>${dia.texto}</option>`;
	});
	selectDias += `</select>`;

	// Cria a linha da tabela
	const linha = document.createElement("tr");
	linha.innerHTML = `
		<td>${selectDias}</td>
		<td><input type="time" class="form-control form-control-sm" value="${horaInicio}" /></td>
		<td><input type="time" class="form-control form-control-sm" value="${horaFim}" /></td>
		<td class="text-center">
			<input type="hidden" class="idHorarioBanco" value="${id}" />
			<button title="Remover" class="btn btn-sm btn-outline-danger" onclick="removerConfirmLinhaTempoEdit(this)">
				<i class="fa fa-trash"></i>
			</button>
		</td>
	`;

	tbody.appendChild(linha);
}

function carregarHorariosPermitidosEdit(listaHorarios) {
	const tbody = document.querySelector("#tabelaHorariosPermitidosEdit tbody");
	tbody.innerHTML = '';

	if (listaHorarios && listaHorarios.length > 0) {
		listaHorarios.forEach(item => adicionarLinhaHorarioEdit(item));
	} else {
		// Adiciona uma linha vazia por padrão
		adicionarLinhaHorarioEdit();
	}
}

// Inicializa com uma linha vazia quando o modal é aberto
document.addEventListener('DOMContentLoaded', function() {
	adicionarLinhaHorarioEdit();
});

function removerConfirmLinhaTempoEdit(botao) {
	const tr = botao.closest('tr');
	const id = tr.querySelector('.idHorarioBanco')?.value;

	let textoConfirmacao;
	let textoRemocao;

	if (id) {
		// Horário veio do banco
		textoConfirmacao = "Este horário será marcado para exclusão e será removido permanentemente apenas ao clicar em Salvar.";
		textoRemocao = "O horário será excluído definitivamente ao salvar as alterações.";
	} else {
		// Horário novo (não salvo ainda)
		textoConfirmacao = "Este horário será removido imediatamente e não poderá ser desfeito.";
		textoRemocao = "Horário removido.";
	}

	Swal.fire({
		title: "Deseja realmente remover?",
		text: textoConfirmacao,
		icon: "warning",
		showCancelButton: true,
		confirmButtonColor: "#3085d6",
		cancelButtonColor: "#d33",
		confirmButtonText: "Sim, remover",
		cancelButtonText: "Cancelar"
	}).then((result) => {
		if (result.isConfirmed) {
			removerLinhaHorarioEdit(botao);
			Swal.fire({
				title: "Removido",
				text: textoRemocao,
				icon: "info",
				timer: 3000,
				showConfirmButton: false
			});
		}
	});
}

function removerLinhaHorarioEdit(botao) {
	botao.closest("tr").remove();
}

function configuraTabelaHorariosPermitidosEdit(habilitar) {
	const tabela = document.getElementById("tabelaHorariosPermitidosEdit");
	const campos = tabela.querySelectorAll("select, input, button");

	campos.forEach(campo => {
		campo.disabled = !habilitar;
	});
}

async function obterTiposAlertasOcorrenciasEdit() {
	
	const select = $("#selTipoAlertaOcorrenciaEdit");

	if (select.children().length > 1) {
		return;
	}

	var urlPesquisa = "/MuralhaDigital/AlertaOcorrencia/Tipo";

	return $.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",

		//if received a response from the server
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				carregaComboTiposAlertasOcorrenciasModalEdit(jqXHR.responseText);
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
		},

		error: function(jqXHR, textStatus, errorThrown) {
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
			$("body").removeClass("loading");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }

	});
}

async function obterEquipamentosGenericoEdit() {
	const urlPesquisa = urlRoot + "MuralhaDigital/Equipamento";
	const dataStringPesquisa = "acao=obterListaEquipamentos";

	return $.ajax({
		type: "GET",
		url: urlPesquisa,
		data: dataStringPesquisa,
		dataType: "xml",
		success: function (data, textStatus, jqXHR) {
			const sucesso = $(jqXHR.responseText).find('sucesso').text();
			const msgResposta = $(jqXHR.responseText).find('msgResposta').text();

			if (sucesso === 'true' || sucesso === '') {
				carregaComboEquipamentosGenericoEdit(jqXHR.responseText);
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
		},
		error: function () {
			AlertCsx_E_TimeOut_8000ms('Erro ao carregar equipamentos!');
		}
	});
}

function carregaComboEquipamentosGenericoEdit(event) {
	const xmlDoc = $.parseXML(event);
	const $xml = $(xmlDoc);
	const $select = $("#selEquipamentoEdit");

	$select.empty();
	$select.selectpicker();

	$xml.find('Equipamento').each(function () {
		const $item = $(this);

		const idLocal = $item.find('idLocal').text();
		const serieEquipamento = $item.find('serieEquipamento').text();
		const nome = $item.find('nome').text();
		const codigoEquipamento = $item.find('codigoEquipamento').text() || "N/D";

		const textoCombo = `${codigoEquipamento} - ${serieEquipamento} - ${nome}`;

		$select.append($('<option>', {
			value: idLocal,
			text: textoCombo
		}));
	});

	$select.selectpicker('refresh');
}

function showLoadingOverlay() {
	$('#loading-overlay').show();
}

function hideLoadingOverlay() {
	$('#loading-overlay').hide();
}

function ObterListaUsuariosEdit() {
	var dataString = "acao=obterListaUsuariosAtivos";
	var url = urlRoot + "MuralhaDigital/Usuarios";

	$.ajax({
		type: "GET",
		url: url,
		data: dataString,
		dataType: "xml",
		success: function(data) {
			console.log('Resposta do Servlet:', data);
			var $xml = $(data);
			var $usuarios = $xml.find('Usuario');

			// Seleciona o <select> pelo ID para limpá-lo
			var $select = $('#selUsuarioResponsavelEdit');
			$select.empty(); // Limpa todas as opções existentes

			// Adiciona a primeira opção padrão
			$select.append($('<option>', {
					value: 0,
					text: '-- Selecione --'
			}));

			// Preenche as outras opções com os dados retornados
			$usuarios.each(function() {
					var $item = $(this);
					var id = $item.find('id').text();
					var nome = $item.find('nome').text();

					$select.append($('<option>', {
						value: id,
						text: nome
					}));
			});
		},
		error: function(jqXHR, textStatus, errorThrown) {
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
		}
	});
}

function onMonitorarSomenteEsteChangeEdit(checkbox) {
	if (checkbox.checked) {
		exibirAlertaMonitorarSomenteEsteEdit();
	}
}

function exibirAlertaMonitorarSomenteEsteEdit() {
	Swal.fire({
		title: 'Monitoramento Personalizado',
		html: 'Agora você receberá apenas alertas deste veículo e de outros supervisionados.',
		icon: 'info',
		confirmButtonText: 'OK',
		confirmButtonColor: '#3085d6',
		allowOutsideClick: false,
		allowEscapeKey: false
	});
}

async function obterCoresEdit() {

    var urlPesquisa = "/MuralhaDigital/VeiculoAuxiliar?acao=buscarCores";

    try {

        const data = await $.ajax({
            type: "GET",
            url: urlPesquisa,
            dataType: "xml"
        });

        const cores = data.getElementsByTagName("cores");

        return cores;

    } catch (error) {

        $("body").removeClass("loading");

        ErrorNotification(
            "Erro ao buscar as cores dos veículos!",
            ""
        );

        throw error;
    }
}

async function obterMarcasEdit() {

    var urlPesquisa = "/MuralhaDigital/VeiculoAuxiliar?acao=buscarMarcas";

    try {

        const data = await $.ajax({
            type: "GET",
            url: urlPesquisa,
            dataType: "xml"
        });

        const marcas = data.getElementsByTagName("marcas");

        return marcas;

    } catch (error) {

        $("body").removeClass("loading");

        ErrorNotification(
            "Erro ao buscar as marcas dos veículos!",
            ""
        );

        throw error;
    }
}

async function obterModelosEdit() {

    var urlPesquisa = "/MuralhaDigital/VeiculoAuxiliar?acao=buscarModelos";

    try {

        const data = await $.ajax({
            type: "GET",
            url: urlPesquisa,
            dataType: "xml"
        });

        const modelos = data.getElementsByTagName("modelos");

        return modelos;

    } catch (error) {

        $("body").removeClass("loading");

        ErrorNotification(
            "Erro ao buscar os modelos dos veículos!",
            ""
        );

        throw error;
    }
}

async function obterClassesVeiculoEdit() {

	var urlPesquisa =
		"/MuralhaDigital/VeiculoAuxiliar?acao=buscarClassesVeiculo";

	return await $.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml"
	}).then(function(data) {

		return data.getElementsByTagName("classesVeiculo");

	});
}

async function carregarDadosEdit() {

	try {

		var resultados = await Promise.all([
			obterCoresEdit(),
			obterMarcasEdit(),
			obterModelosEdit(),
			obterClassesVeiculoEdit()
		]);

		var cores = resultados[0];
		var marcas = resultados[1];
		var modelos = resultados[2];
		var classesVeiculo = resultados[3];

		preencherSelectVeiculoEdit(
			"#selCorEdit",
			cores,
			"-- Selecione a Cor --",
			"id"
		);

		preencherSelectVeiculoEdit(
			"#selMarcaEdit",
			marcas,
			"-- Selecione a Marca --",
			"id"
		);

		preencherSelectVeiculoEdit(
			"#selModeloEdit",
			modelos,
			"-- Selecione o Modelo --",
			"id"
		);

		preencherSelectVeiculoEdit(
			"#selClasseVeiculoEdit",
			classesVeiculo,
			"-- Selecione a Classe --",
			"idClasse"
		);

	} catch (erro) {

		console.error(
			"Erro ao carregar dados auxiliares do veículo:",
			erro
		);

		throw erro;
	}
}
function preencherSelectVeiculoEdit(
	seletor,
	lista,
	textoPadrao,
	campoId
) {

	var select = $(seletor);

	select.empty();

	select.append(
		$("<option>", {
			value: "0",
			text: textoPadrao
		})
	);

	$(lista).each(function() {

		var id = $(this).find(campoId).text();
		var descricao = $(this).find("descricao").text();

		select.append(
			$("<option>", {
				value: id,
				text: descricao
			})
		);

	});

	select.val("0");

	// Atualiza o Bootstrap Select quando o campo for selectpicker
	if (select.hasClass("selectpicker")) {
		select.selectpicker("refresh");
	}
}