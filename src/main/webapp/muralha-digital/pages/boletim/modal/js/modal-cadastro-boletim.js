function salvarBoletim() {
	const documentos = getDocumentosBoletim() || [];
	const individuos = getIndividuosBoletim() || [];
	const veiculos = getVeiculosBoletim() || [];
	const apreensoes = getApreensoesBoletim() || [];
	const boletim = getBoletim();
	if (boletim == null) {
		return;
	}
	// Aqui você pode enviar via AJAX ou manipular conforme necessário
	console.log("Dados do boletim:", boletim);
	const dados = {
		boletim: boletim,
		documentos: documentos,
		individuos: individuos,
		veiculos: veiculos,
		apreensoes: apreensoes
	};
	cadastrarBoletim(dados);
}

function cadastrarBoletim(dados) {
	const url = "/MuralhaDigital/Boletim";
	const salvarBtn = document.getElementById("idBoletimModalSalvar");

	const mostrarLoading = () => $("body").addClass("loading");
	const esconderLoading = () => $("body").removeClass("loading");

	const desabilitarBotao = () => salvarBtn.disabled = true;
	const habilitarBotao = () => salvarBtn.disabled = false;

	const extrairIdBoletim = (mensagem) => {
		const match = mensagem.match(/ID do boletim:\s*(\d+)/);
		return match ? parseInt(match[1], 10) : null;
	};

	mostrarLoading();
	desabilitarBotao();

	$.ajax({
		type: "POST",
		url,
		contentType: "application/json; charset=UTF-8",
		accepts: { xml: "application/xml" },
		data: JSON.stringify(dados),
		dataType: "xml",

		success: (responseXml) => {
			esconderLoading();

			const mensagem = $(responseXml).find("msgResposta").text();
			const boletimId = extrairIdBoletim(mensagem);

			if (boletimId !== null) {
				SuccessNotificationBoletimModal("Boletim criado com sucesso!", "");
				eventoRetornoId(boletimId);
			} else {
				habilitarBotao();
				ErrorNotificationBoletimModal("Boletim criado, mas ID não encontrado na resposta.", "");
			}

			fecharModal();
		},

		error: (jqXHR, textStatus, errorThrown) => {
			esconderLoading();
			habilitarBotao();
			console.error("Erro ao criar boletim:", errorThrown);
			ErrorNotificationBoletimModal("Erro ao criar boletim.", "");
		}
	});
}

function configuraModalCadastrar()
{
	document.getElementById("idBoletimModalSalvar").disabled = false;
	// Chamada de cada arquivo .js das tab
	desbloquearCamposBoletim();	
	desbloquearCamposDocumentos();
	desbloquearCamposDocumentos();
	desbloquearCamposIndividuos();
	desbloquearCamposVeiculos();
	desbloquearCamposApreensoes();
}

function eventoRetornoId(boletimId)
{
	//Dispara evento com o ID do boletim
	const evento = new CustomEvent('boletimSalvoCadastro', {
		detail: { boletimId },
	});

	document.dispatchEvent(evento);
}
