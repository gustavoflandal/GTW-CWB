var VOLTAR_MODAL = false;
var MODAL_ORIGEM = '';
var ID_TIPO_REGISTRO = 'E7D115B9-E6B3-4E86-9083-F347A1917045';
var ID_TIPO_ALERTA = '';
var diasSemanaCad = diasSemanaCad || [
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

async function AbrirModalCadMonitorador() {
	try {
		ObterListaUsuarios();
		SetarDataInicio();
		irParaPrimeiraAbaMonitorado();
		$("body").addClass("loading");
		await GerenciarPermissaoSupervisionadoCad();
		await carregarDados();
		carregarHorariosPermitidosCad();
		obterEquipamentosGenerico("selEquipamentoCad");
		$("body").removeClass("loading");
		$(".selUsuarioResponsavel").prop("disabled", false);
		$("#modalCadMonitorado").modal("show");

	} catch (erro) {
		$("body").removeClass("loading");
		console.error(
			"Falha ao carregar dados do cadastro:",
			erro
		);
		alert(
			"Erro ao carregar os dados do cadastro. Verifique sua conexão ou permissões."
		);
	}
}

function irParaPrimeiraAbaMonitorado() {
	const primeiraAba = document.querySelector('#tabMonitorado button[data-bs-target="#tab-dados"]');
	const tab = new bootstrap.Tab(primeiraAba);
	tab.show();
}

async function GerenciarPermissaoSupervisionadoCad() {
	// 1. Verifica a permissão do usuário.
	const permitido = await VerificarPermissaoSupervisorCad();

	const $checkbox = $("#checkboxSupervisionado");

	if (permitido) {
		$checkbox.prop('disabled', false);
	} else {
		$checkbox.prop('checked', false);
		$checkbox.prop('disabled', true);
	}
	if (typeof toggleSupervisionadoExtrasCad === 'function') {
		toggleSupervisionadoExtrasCad($checkbox[0]);
	}
}

async function VerificarPermissaoSupervisorCad() {
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

async function cadastrarMonitorado() {
    // --- 1. Coleta de Dados do Formulário ---
	const nome = document.getElementById("idNomeMonitorado").value;
	const placa = document.getElementById("idPlacaCad").value;
	const dataIni = document.getElementById("dataInicioCad").value;
	const dataFim = document.getElementById("dataFimCad").value;
	const descricao = document.getElementById("descricaoCad").value;
	
	const selectTipoOcorrencia = document.getElementById("selTipoAlertaOcorrenciaCad");
	const tipoAlertaOcorrencia = selectTipoOcorrencia.value;
	const tipoAlertaOcorrenciatitulo = selectTipoOcorrencia.options[selectTipoOcorrencia.selectedIndex].text;

	const selectUsuarioResponsavel = document.getElementById("selUsuarioResponsavel");
	const usuarioResponsavel = selectUsuarioResponsavel.value;
	const usuarioResponsavelNome = selectUsuarioResponsavel.options[selectUsuarioResponsavel.selectedIndex].text;

	const checkboxSupervisionado = document.getElementById("checkboxSupervisionado").checked;
   	const checkboxPrivado = document.getElementById("checkboxPrivado").checked;
	const checkboxMonitorarSomenteEste = document.getElementById("checkboxMonitorarSomenteEste").checked;

	const idClasse = document.getElementById("selClasseVeiculoCad").value;
	const cor = document.getElementById("selCorCad").value;
	const marca = document.getElementById("selMarcaCad").value;
	const modelo = document.getElementById("selModeloCad").value;
	const textoAdesivo = document.getElementById("textoAdesivoCad").value;

	// --- 2. Validação de Permissão (saída antecipada se não tiver) ---
	if (checkboxSupervisionado && !(await VerificarPermissaoSupervisorCad())) {
		Swal.fire({ icon: 'error', title: 'Erro Permissão!', text: "Tentativa de cadastro supervisionado sem permissão." });
		return; 
	}

	// --- 3. Construção dos Parâmetros da Requisição ---
	const params = new URLSearchParams();

	// Parâmetros que são enviados em TODOS os casos
	params.append("nome", nome);
	params.append("placa", placa);
	params.append("dataIni", dataIni);
	params.append("dataFim", dataFim);
	params.append("descricao", descricao);
	params.append("tipoAlertaOcorrencia", tipoAlertaOcorrencia);
	params.append("tipoAlertaOcorrenciatitulo", tipoAlertaOcorrenciatitulo);
	params.append("usuario_responsavel", usuarioResponsavel);
	params.append("usuario_responsavel_nome", usuarioResponsavelNome);
	params.append("privado", checkboxPrivado);
	params.append("supervisionado", checkboxSupervisionado);
	params.append("monitorar_somente_este", (checkboxMonitorarSomenteEste ? "1" : "0"));
	params.append("acao", "inserir");
	params.append("idClasse", idClasse);
	params.append("cor", cor);
	params.append("marca", marca);
	params.append("modelo", modelo);
	params.append("textoAdesivo", textoAdesivo);
	
	// --- 2. Validações dos campos obrigatórios ---

	if (!nome || nome.trim() === "") {
		Swal.fire({
			icon: 'warning',
			title: 'Campo obrigatório!',
			text: 'Informe o nome do monitorado.'
		});

		document.getElementById("idNomeMonitorado").focus();
		return;
	}

	if (!placa || placa.trim() === "") {
		Swal.fire({
			icon: 'warning',
			title: 'Campo obrigatório!',
			text: 'Informe a placa do veículo.'
		});

		document.getElementById("idPlacaCad").focus();
		return;
	}

	if (!tipoAlertaOcorrencia || tipoAlertaOcorrencia === "0") {
		Swal.fire({
			icon: 'warning',
			title: 'Campo obrigatório!',
			text: 'Selecione o Tipo Alerta/Ocorrência.'
		});

		document.getElementById("selTipoAlertaOcorrenciaCad").focus();
		return;
	}

	if (dataIni && dataFim) {

		const partesDataIni = dataIni.split(" ");
		const partesDataFim = dataFim.split(" ");

		const dataHoraIni = converterDataHoraCadastro(partesDataIni);
		const dataHoraFim = converterDataHoraCadastro(partesDataFim);

		if (dataHoraFim < dataHoraIni) {
			Swal.fire({
				icon: 'warning',
				title: 'Período inválido!',
				text: 'A Data Fim não pode ser anterior à Data Início.'
			});

			document.getElementById("dataFimCad").focus();
			return;
		}
	}

	// Parâmetros que podem ou não ser enviados
	const equipamentosSelecionados = $('#selEquipamentoCad').val();
	params.append("equipamentosLocais", equipamentosSelecionados ? equipamentosSelecionados.join(",") : "");

	try {
		const horarios = obterHorariosPermitidos();
		params.append("horariosPermitidos", JSON.stringify(horarios));
	} catch (e) {
		Swal.fire({ icon: 'error', title: 'Erro de Horário!', text: e.message });
		return;
	}
	
	const nivelSemelhancaPlacaEl = document.getElementById("nivelSemelhancaPlaca");
	const nivelSemelhancaPlaca = nivelSemelhancaPlacaEl ? parseInt(nivelSemelhancaPlacaEl.value) : null;

	/*
		Aqui abaixo é pego os valores dos selects de intervalo de tempo (início e fim), após isso é verificado se possui algum valor ou se é nulo.
		Após isso fará um if para verificar se o valor é '00' (no caso o valor padrão) e grava nulo no banco, caso contrário, grava o valor que o
		usuário informou, sendo '00' para os minuto de início e '59' para os minutos de fim.
	*/
	const intervaloTempoInicioEl = document.getElementById("intervaloTempoInicioCadastro");
	var intervaloTempoInicio = intervaloTempoInicioEl ? intervaloTempoInicioEl.value : null;

	const intervaloTempoFimEl = document.getElementById("intervaloTempoFimCadastro");
	var intervaloTempoFim = intervaloTempoFimEl ? intervaloTempoFimEl.value : null;
	
	params.append("erros_permitidos_placa", nivelSemelhancaPlaca ?? '');

	if(intervaloTempoInicio == '00' || intervaloTempoFim == '00') {
		params.append("erros_permitido_ini", null);
		params.append("erros_permitido_fim", null);
	} else {
		params.append("erros_permitido_ini", intervaloTempoInicio + ':00' ?? '');
		params.append("erros_permitido_fim", intervaloTempoFim + ':59' ?? '');
	}

    // ⭐ Lógica Principal: Adiciona parâmetros EXCLUSIVOS do modo Supervisionado ⭐
	if (checkboxSupervisionado) {
		const gruposEmail = $('#selAcaoGrupoPopupCad').val();
		params.append("gruposPopup", gruposEmail ? gruposEmail.join(",") : "");
	}
	
	// --- 4. Envio da Requisição AJAX ---
	showLoadingOverlay();
	$.ajax({
		type: "POST",
		url: "/MuralhaDigital/Monitorado",
		data: params.toString(),
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			const event = jqXHR.responseText;
			const xmlDoc = $.parseXML(event);
			const $xml = $(xmlDoc);
			let sucesso = $xml.find('sucesso').text();
			const msgResposta = $xml.find('msgResposta').text();
			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				limparCampos();
				hideLoadingOverlay();
				fecharModalCad();
				if (!VOLTAR_MODAL) executaPesquisa(true);
			} else {
				hideLoadingOverlay();
				WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, "#error_container_modal_cad_veiculo_mon");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			hideLoadingOverlay();
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', "#error_container_modal_cad_veiculo_mon");
		}
	});
}

/*
	Aqui abaixo é feito uma validação para verificar o checkbox do Intervalo de Tempo, ele começa desmarcado no front, então é pego os valores
	escolhido pelo usuário, caso o checkbox seja marcado e a hora de início seja maior ou igual a hora de fim, o botão de salvar ficará
	desabilitado e um aviso de erro ficará desabilitado, caso a hora de início seja menor que a hora de fim, tudo ficará ativo normalmente,
	por fim, caso o checkbox esteja desmarcado, os selects ficarão desabilitados e o botão de salvar ficará ativo normalmente.
*/
function verificacaoIntervaloTempo() {
	var checkboxTempo = document.getElementById("checkboxTempoCadastro");
	var intervaloTempoInicio = document.getElementById("intervaloTempoInicioCadastro");
	var intervaloTempoFim = document.getElementById("intervaloTempoFimCadastro");
	var botaoSalvarModal = document.getElementById("btnSalvarModalCadastro");
	var avisoIntervaloTempo = document.getElementById("avisoIntervaloTempoCadastro");

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

function obterHorariosPermitidos() {
	const linhas = document.querySelectorAll("#tabelaHorariosPermitidos tbody tr");
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

function fecharModalCad() {
	$('#modalCadMonitorado').modal('hide');

	if (VOLTAR_MODAL) {
		VOLTAR_MODAL = false;
		limparCampos();
		OcultarModalCadMonitorado();
		MostrarModalOrigem();
	}
}

function limparCampos() {
	var tipoAlertaOcorrencia = document.getElementById("selTipoAlertaOcorrenciaCad");
	var placa = document.getElementById("idPlacaCad");
	var dataIni = document.getElementById("dataInicioCad");
	var dataFim = document.getElementById("dataFimCad");
	var descricao = document.getElementById("descricaoCad");
	var nome = document.getElementById("idNomeMonitorado");

	var checkboxPrivado = document.getElementById("checkboxPrivado");
	var checkboxSupervisionado = document.getElementById("checkboxSupervisionado");
	var nivelSemelhancaPlaca = document.getElementById("nivelSemelhancaPlaca");
	var intervaloTempoInicio = document.getElementById("intervaloTempoInicioCadastro");
	var intervaloTempoFim = document.getElementById("intervaloTempoFimCadastro");
	var supervisionadoExtras = document.getElementById("supervisionadoExtras");
	var checkboxMonitorarSomenteEste = document.getElementById("checkboxMonitorarSomenteEste");
	
	// Dados do veículo
	var selClasseVeiculoCad = document.getElementById("selClasseVeiculoCad");
	var selCorCad = document.getElementById("selCorCad");
	var textoAdesivoCad = document.getElementById("textoAdesivoCad");

	if (selClasseVeiculoCad)
		selClasseVeiculoCad.value = "0";

	if (selCorCad)
		selCorCad.value = "0";

	if (textoAdesivoCad)
		textoAdesivoCad.value = "";

	// Marca
	$("#selMarcaCad").val("0");
	$("#selMarcaCad").selectpicker("refresh");

	// Modelo
	$("#selModeloCad").val("0");
	$("#selModeloCad").selectpicker("refresh");

	if (tipoAlertaOcorrencia)
		tipoAlertaOcorrencia.value = 0;

	if (nome)
		nome.value = "";

	if (placa)
		placa.value = "";

	if (dataIni)
		dataIni.value = "";

	if (dataFim)
		dataFim.value = "";

	if (descricao)
		descricao.value = "";

	if (checkboxPrivado) checkboxPrivado.checked = false;
	if (checkboxSupervisionado) checkboxSupervisionado.checked = false;
	if (nivelSemelhancaPlaca) nivelSemelhancaPlaca.value = "0";

	/*
		Aqui abaixo é feito um if para, quando o usuário clicar em limpar campos, o valor dos selects pego pelo ID acima,
		será resetado para o padrão que é '00:00' para a hora de início e '00:59' para a hora de fim.
	*/
	if (intervaloTempoInicio) intervaloTempoInicio.value = "00";
	if (intervaloTempoFim) intervaloTempoFim.value = "00";
	if (checkboxMonitorarSomenteEste) checkboxMonitorarSomenteEste.checked = false;

	if (supervisionadoExtras) supervisionadoExtras.classList.add("d-none");
	// 🧹 Limpa linhas da tabela de horários
	const tbody = document.querySelector("#tabelaHorariosPermitidos tbody");
	if (tbody) tbody.innerHTML = "";

	// 🔄 Limpa selects múltiplos (usando Bootstrap Select)
	$('#selAcaoGrupoPopupCad').val([]).selectpicker('refresh');
	$('#selEquipamentoCad').val([]).selectpicker('refresh');

	irParaPrimeiraAbaMonitorado();
	const liTabGrupos = document.getElementById('li-tab-grupos');
	liTabGrupos.classList.add('d-none');
}

function ObterTiposAlertasOcorrencias() {
	var urlPesquisa = "/MuralhaDigital/AlertaOcorrencia/Tipo";

	$.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				CarregaComboTiposAlertasOcorrenciasModalCad(jqXHR.responseText);
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

function CarregaComboTiposAlertasOcorrenciasModalCad(event) {
	var xmlDoc = $.parseXML(event);
	var $xml = $(xmlDoc);

	$("#selTipoAlertaOcorrenciaCad").empty();

	$xml.find('TipoAlertaOcorrencia').each(function() {
		var $item = $(this);

		var id = $item.find('id').text();
		var tipo = $item.find('tipo').text();

		$('#selTipoAlertaOcorrenciaCad').append($('<option>', {
			value: id,
			text: tipo
		}));
	});
}

function SetarDataInicio() {
	var dataInicioCad = document.getElementById("dataInicioCad");
	if (dataInicioCad) {
		const dataInicio = new Date();
		dataInicioCad.value = dataInicio.toLocaleDateString("pt-BR");
	}
}


function CadMonitoradoExterno(nomeModalOrigem, placa) {
	VOLTAR_MODAL = true;
	MODAL_ORIGEM = nomeModalOrigem;
	OcultarModalOrigem();
	MostrarModalCadMonitorado();
	CarregarModalCadMonitoradoExterno(placa);
}

function CarregarModalCadMonitoradoExterno(placa) {
	var placaCad = document.getElementById("idPlacaCad");
	if (placaCad)
		placaCad.value = placa;
	ObterTiposAlertasOcorrencias();
	SetarDataInicio();
}

function OcultarModalOrigem() {
	if (MODAL_ORIGEM != '')
		$(MODAL_ORIGEM).modal('hide');
}

function MostrarModalOrigem() {
	if (MODAL_ORIGEM != '')
		$(MODAL_ORIGEM).modal('show');
}

function OcultarModalCadMonitorado() {
	$("#modalCadMonitorado").modal('hide');
}

function MostrarModalCadMonitorado() {
	$("#modalCadMonitorado").modal('show');
}

function toggleSupervisionadoExtrasCad(checkbox) {
	const extrasDiv = document.getElementById('supervisionadoExtras');
	extrasDiv.classList.toggle('d-none', !checkbox.checked);
	onGerenciarAbaSupervisionadoCad();
}

function onGerenciarAbaSupervisionadoCad() {
	const checkboxSupervisionado = document.getElementById("checkboxSupervisionado");
	const liTabGrupos = document.getElementById('li-tab-grupos');
	const select = document.getElementById('selTipoAlertaOcorrenciaCad');
	const valor = select.value;

	if (!checkboxSupervisionado.checked) {
		liTabGrupos.classList.add('d-none');
		return;
	}

	if (checkboxSupervisionado) {
		liTabGrupos.classList.remove('d-none');

		if (valor !== "0" && valor !== "") {
			// Atualiza os grupos
			ID_TIPO_ALERTA = valor;
			ObterGruposPopupMoniCad();
		}
	} else {
		liTabGrupos.classList.add('d-none');

		// Volta para a aba principal se a de grupos estiver ativa
		const tabDados = new bootstrap.Tab(document.querySelector('#tab-dados-tab'));
		tabDados.show();

		// Limpa os selects
		const emailSelect = document.getElementById('selAcaoGrupoPopupCad');
		const smsSelect = document.getElementById('selAcaoGrupoSmsCad');

		if (emailSelect) {
			emailSelect.innerHTML = ""; // remove opções
			$(emailSelect).selectpicker('refresh');
		}

		if (smsSelect) {
			smsSelect.innerHTML = ""; // remove opções
			$(smsSelect).selectpicker('refresh');
		}
	}
}

function ObterGruposPopupMoniCad() {
	var dataString = "acao=obterGruposPopup" +
		"&tipoRegistro=" + ID_TIPO_REGISTRO +
		"&tipoAlertaOcorrencia=" + ID_TIPO_ALERTA;

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
				carregaComboGruposPopup(jqXHR.responseText);
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

function carregaComboGruposPopup(event) {
	var xmlDoc = $.parseXML(event);
	var $xml = $(xmlDoc);

	$('#selAcaoGrupoPopupCad').empty();

	$xml.find('GrupoNotificacao').each(function() {
		var $item = $(this);

		var idGrupo = $item.find('idGrupo').text();
		var grupo = $item.find('grupo').text();

		$('#selAcaoGrupoPopupCad').append($('<option>', {
			value: idGrupo,
			text: grupo
		}));
	});

	$('#selAcaoGrupoPopupCad').selectpicker('refresh');
}

function adicionarLinhaHorarioCad() {
	const tbody = document.querySelector("#tabelaHorariosPermitidos tbody");

	// Monta o select com dias da semana
	let selectDias = `<select class="form-select form-select-sm">`;
	diasSemanaCad.forEach(dia => {
		selectDias += `<option value="${dia.valor}">${dia.texto}</option>`;
	});
	selectDias += `</select>`;

	// Cria a linha da tabela
	const linha = document.createElement("tr");
	linha.innerHTML = `
	<td>${selectDias}</td>
	<td><input type="time" class="form-control form-control-sm" /></td>
	<td><input type="time" class="form-control form-control-sm" /></td>
	<td class="text-center">
      <button title="Remover" class="btn btn-sm btn-outline-danger" onclick="removerConfirmLinhaTempoCad(this)">
			<i class="fa fa-trash"></i>
      </button>
	</td>
	`;

	tbody.appendChild(linha);
}

function carregarHorariosPermitidosCad(listaHorarios) {
	const tbody = document.querySelector("#tabelaHorariosPermitidos tbody");
	tbody.innerHTML = '';

	if (listaHorarios && listaHorarios.length > 0) {
		listaHorarios.forEach(item => adicionarLinhaHorarioCad(item));
	} else {
		// Adiciona uma linha vazia por padrão
		adicionarLinhaHorarioCad();
	}
}

function removerConfirmLinhaTempoCad(botao) {
	Swal.fire({
		title: "Tem certeza?",
		text: "Essa ação não pode ser desfeita!",
		icon: "warning",
		showCancelButton: true,
		confirmButtonColor: "#3085d6",
		cancelButtonColor: "#d33",
		confirmButtonText: "Sim, remover",
		cancelButtonText: "Cancelar"
	}).then((result) => {
		if (result.isConfirmed) {
			removerLinhaHorarioCad(botao);
			Swal.fire({
				title: "Removido!",
				text: "O horário foi excluído com sucesso.",
				icon: "success",
				timer: 1500,
				showConfirmButton: false
			});
		}
	});
}

function removerLinhaHorarioCad(botao) {
	botao.closest("tr").remove();
}

function ObterListaUsuarios() {
	var dataString = "acao=obterListaUsuariosAtivos";
	var url = urlRoot + "MuralhaDigital/Usuarios";

	$.ajax({
		type: "GET",
		url: url,
		data: dataString,
		dataType: "xml",

		//if received a response from the server
		success: function(data, ) {
			console.log('Resposta do Servlet:', data);
			var $xml = $(data);
			var $usuarios = $xml.find('Usuario');

            $('.selUsuarioResponsavel').empty(); // Limpa o select

      
            // Adiciona opção padrão
            $('.selUsuarioResponsavel').append($('<option>', {
               value: 0,
               text: 'Selecione'
            }));

			// Preenche as opções com os dados retornados
			$usuarios.each(function () {
				var $item = $(this);
				var id = $item.find('id').text();
				var nome = $item.find('nome').text();

				$('.selUsuarioResponsavel').append($('<option>', {
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

function onMonitorarSomenteEsteChange(checkbox) {
    if (checkbox.checked) {
        exibirAlertaMonitorarSomenteEste();
    }
}

function exibirAlertaMonitorarSomenteEste() {
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

async function obterCores() {

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

async function obterMarcas() {

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

async function obterModelos() {

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

async function obterClassesVeiculo() {

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

function preencherSelectVeiculo(
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

async function carregarDados() {

	try {

		var resultados = await Promise.all([
			obterCores(),
			obterMarcas(),
			obterModelos(),
			obterClassesVeiculo()
		]);

		var cores = resultados[0];
		var marcas = resultados[1];
		var modelos = resultados[2];
		var classesVeiculo = resultados[3];

		preencherSelectVeiculo(
			"#selCorCad",
			cores,
			"-- Selecione a Cor --",
			"id"
		);

		preencherSelectVeiculo(
			"#selMarcaCad",
			marcas,
			"-- Selecione a Marca --",
			"id"
		);

		preencherSelectVeiculo(
			"#selModeloCad",
			modelos,
			"-- Selecione o Modelo --",
			"id"
		);

		preencherSelectVeiculo(
			"#selClasseVeiculoCad",
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