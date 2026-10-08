modo = "";
ID_REGISTRO_FATO = '';
veiculos = [];
TEM_BOLETIM = 2; //2 para não ser nem com boletim nem sem boletim

async function abrirModalConfirmacao() {
	const result = await Swal.fire({
		title: 'Gostaria de cadastrar um fato com ou sem boletim?',
		text: '',
		icon: 'question',
		showCancelButton: true,
		confirmButtonText: 'Com boletim',
		cancelButtonText: 'Sem boletim',
		reverseButtons: true,
		allowOutsideClick: false
	});
	modo = "cadastro";
	habilitarCamposModalComBoletim();
	atualizarBotoesModalComBoletim();
	limpaCamposModalComBoletim();
	gerenciarVisibilidadeDatasTabRegistro(modo);
	if (result.isConfirmed) {
		TEM_BOLETIM = 1;
		window.temBoletimMarcado = true;
		prepararModalComBoletim();
		exibirModalComBoletim();
	} else {
		TEM_BOLETIM = 0;
		prepararModalSemBoletim();
		exibirModalComBoletim();
		window.temBoletimMarcado = false;
	}
	alternarVisibilidadeCamposFato(modo);
	await configuraModalCadastrarComBoletim();
}

async function abrirModalEditarRegistroDeFatoAbos(id) {
	modo = "edicao";
	$("body").addClass("loading");
	limpaCamposModalComBoletim();
	bloqueiaCamposModalComBoletim();
	await configuraModalVisualizarComBoletim();
	await buscarDadosModalVisualizarAbosTiposRegistro(id);
	exibirModalComBoletim();
	atualizarBotoesModalComBoletim(id);
	gerenciarVisibilidadeDatasTabRegistro(modo);
	$("body").removeClass("loading");
}

async function abrirModalEditarRegistroAtendimento(id) {
	modo = "edicao";
	$("body").addClass("loading");
	limpaCamposModalComBoletim();
	bloqueiaCamposModalComBoletim();
	atualizarBotoesModalComBoletim();
	gerenciarVisibilidadeDatasTabRegistro(modo);
	await configuraModalVisualizarComBoletim();
	await buscarDadosModalVisualizarAbosTiposRegistro(id);
	exibirModalComBoletimAtendimento();
	$("body").removeClass("loading");
}

function atualizarBotoesModalComBoletim(id) {
	consultarVeiculosPorRegistroFato(id);
	const btnSalvar = document.getElementById("idRegistroComBoletimModalSalvar");

	if (modo === "cadastro") {
		btnSalvar.textContent = "Cadastrar";
		btnSalvar.style.display = "inline-block";
		btnSalvar.onclick = salvarRegistroDeFatoComBoletim;
	}
	else if (modo === "edicao") {
		btnSalvar.textContent = "Salvar alterações";
		btnSalvar.style.display = "inline-block";
		btnSalvar.onclick = atualizarRegistroDeFatoComBoletim; // nova função para editar
	}
	else if (modo === "visualizar") {
		btnSalvar.style.display = "none";
	}
}

function habilitarCamposModalComBoletim() {
	habilitarBotaoSavlar()
	habilitarCamposRegistroDeFatoComBoletim();
	habilitarCamposComBoletim();
	bloquearCamposEnderecoComBoletim(modo);
}

function bloqueiaCamposModalComBoletim() {
	bloquearCamposRegistroDeFatoComBoletim();
	bloquearCamposComBoletim();
	bloquearCamposEnderecoComBoletim(modo);
}

function habilitarBotaoSavlar() {
	const btnSalvar = $("#idRegistroComBoletimModalSalvar");
	btnSalvar.prop("disabled", false);
}

function bloquearBotaoSavlar() {
	const btnSalvar = $("#idRegistroComBoletimModalSalvar");
	btnSalvar.prop("disabled", true);
}

async function buscarDadosModalVisualizarComBoletim(id) {
	if (!id) {
		Swal.fire("Atenção", "ID inválido para buscar o registro.", "warning");
		return;
	}

	const params = new URLSearchParams({
		acao: 'buscarFatoComBoletimPorId',
		idRegistroFato: id,
	});

	return $.ajax({
		url: '/MuralhaDigital/RegistroDeFato',
		method: 'GET',
		dataType: 'xml',
		data: params.toString(),
		success: (data, textStatus, jqXHR) => {
			try {
				popularVisualizarComBoletim(jqXHR.responseText);
				$("body").removeClass("loading");
			} catch (e) {
				console.error(e);
				Swal.fire("Erro", "Erro ao interpretar a resposta do servidor.", "error");
			}
		},
		error: function(xhr, status, error) {
			$("body").removeClass("loading");
			console.error("Erro ao buscar:", error);
			Swal.fire("Erro", "Falha ao se comunicar com o servidor. Tente novamente.", "error");
		}
	});
}

async function buscarDadosModalVisualizarAbosTiposRegistro(id) {
	if (!id) {
		Swal.fire("Atenção", "ID inválido para buscar o registro.", "warning");
		return;
	}
	ID_REGISTRO_FATO = id;

	const params = new URLSearchParams({
		acao: 'buscarFatoAmbosPorId',
		idRegistroFato: id,
	});

	return $.ajax({
		url: '/MuralhaDigital/RegistroDeFato',
		method: 'GET',
		dataType: 'xml',
		data: params.toString(),
		success: (data, textStatus, jqXHR) => {
			try {
				const parser = new DOMParser();
				const xml = parser.parseFromString(jqXHR.responseText, "text/xml");
				const registro = xml.querySelector("RegistroDeFato");

				// 2. Defina a função helper aqui também para garantir que ela exista no escopo
				const getText = (element, selector) => element?.querySelector(selector)?.textContent.trim() || '';

				// 3. Verifique se o registro foi encontrado antes de prosseguir
				if (registro) {
					const temBoletim = getText(registro, "temBoletim") === '1';

					if (temBoletim) {
						TEM_BOLETIM = 1;
						window.temBoletimMarcado = true;
					} else {
						TEM_BOLETIM = 0;
						window.temBoletimMarcado = false;
					}
				}
				alternarVisibilidadeCamposFato(modo);
				popularVisualizarAbos(jqXHR.responseText);
				verificarStatusEBloquearBotao(jqXHR.responseText);
				$("body").removeClass("loading");
			} catch (e) {
				console.error(e);
				Swal.fire("Erro", "Erro ao interpretar a resposta do servidor.", "error");
			}
		},
		error: function(xhr, status, error) {
			$("body").removeClass("loading");
			console.error("Erro ao buscar:", error);
			Swal.fire("Erro", "Falha ao se comunicar com o servidor. Tente novamente.", "error");
		}
	});
}


function popularVisualizarAbos(xml) {

	preencherTabRegistroDeFatoComBoletim(xml);
	preencherTabBoletimComBoletim(xml);
	preencherTabEnvolvidosComBoletim(xml);
	preencherTabObjetosComBoletim(xml);
	preencherTabEnderecosComBoletim(xml);
	preencherTabVeiculosComBoletim(xml);
	preencherTabDocumentosComBoletim(xml);
	preencherTabLinksComBoletim(xml);
	preencherTabGruposComBoletim(xml);
	preencherTabelaPassagensComBoletim(xml);
}


async function configuraModalCadastrarComBoletim() {
  // helpers locais
  const $ = id => document.getElementById(id);
  const setHidden = (el, hidden) => {
    if (!el) return;
    el.classList.toggle('d-none', !!hidden);
  };
  const removeShowActive = el => {
    if (!el) return;
    el.classList.remove('show', 'active');
  };
  const setModalTitle = title => {
    const modalTitle = $('modalPadraoLabel');
    if (modalTitle) modalTitle.innerHTML = title;
  };

  //Define o valor padrão para o Tipo de Evento, APENAS SE o campo existir
  const selectTipoEvento = $('idTipoEventoComBoletim');
  if (selectTipoEvento) selectTipoEvento.value = '1';

  //Função externa que depende do modo (variável global)
  try {
    gerenciarVisibilidadeDatasTabRegistro(modo);
  } catch (err) {
    console.warn('Erro em gerenciarVisibilidadeDatasTabRegistro:', err);
  }

  //Atualiza título do modal
	if (TEM_BOLETIM == 1) {
		setModalTitle('Cadastro de Fato C/ Boletim');
	}
	else {
		setModalTitle('Cadastro de Fato S/ Boletim');
	}


  //Verifica permissão (com tratamento de erro)
  const temPermissaoGrupo = await verificarPermissaoGrupoUsuarioConsulta().catch(err => {
    console.error('Erro ao verificar permissão de grupo:', err);
    return false; // assume sem permissão em caso de erro
  });

  //Cache dos elementos de abas/panes
  const abaGrupoTab = $('abaGrupoTab');
  const grupoPane = $('grupo-comBoletim-tab-pane');
  const abaPassagensTab = $('abaPassagensTab');
  const passagensPane = $('passagens-comBoletim-tab-pane');

  //Ajusta acesso de permissão do grupo conforme permissão
  habilitarGrupoCamposBoletim(temPermissaoGrupo);

  // No modo de cadastro, sempre oculta Passagens
  setHidden(abaPassagensTab, true);
  removeShowActive(passagensPane);
  setHidden(passagensPane, true);
}

async function configuraModalVisualizarComBoletim() {
  const $ = id => document.getElementById(id);
  const setHidden = (el, hidden) => {
    if (!el) return;
    if (hidden) {
      el.classList.add('d-none');
    } else {
      el.classList.remove('d-none');
    }
  };
  const removeShowActive = el => {
    if (!el) return;
    el.classList.remove('show', 'active');
  };

  // chamada externa que depende do modo
  try {
    gerenciarVisibilidadeDatasTabRegistro(modo);
  } catch (e) {
    console.warn('gerenciarVisibilidadeDatasTabRegistro falhou:', e);
  }

  const modalTitle = $('modalPadraoLabel');
  const abaGrupoTab = $('abaGrupoTab');
  const grupoPane = $('grupo-comBoletim-tab-pane');
  const abaPassagensTab = $('abaPassagensTab');
  const passagensPane = $('passagens-comBoletim-tab-pane');

  // atualiza título do modal, se existir
	if (TEM_BOLETIM == 1) {

	} else {
		if (modalTitle) modalTitle.innerHTML = 'Edição de Fato C/ Boletim';
	}


  // verifica permissão e ajusta visibilidade de "Grupo"
  const temPermissaoGrupo = await verificarPermissaoGrupoUsuarioConsulta().catch(err => {
    console.error('Erro ao verificar permissão:', err);
    return false; // em caso de erro, assume sem permissão
  });

  //Ajusta acesso de permissão do grupo conforme permissão
  habilitarGrupoCamposBoletim(temPermissaoGrupo);

  // No modo de visualização, sempre mostra a aba Passagens (mantive comportamento original)
  setHidden(abaPassagensTab, false);
  setHidden(passagensPane, false);
}

function limpaCamposModalComBoletim() {
	limparCamposEnderecoComBoletim();
	limparCamposRegistroFatoComBoletim();
	limparCamposBoletim();
	limparCamposIndividuoComBoletim();
	limparCamposVeiculoComBoletim();
	limparCamposDocumentosComBoletim();
	limparLinksComBoletim();
	limparGrupoComBoletim();
	limparObjetosComBoletim()
}

function exibirModalComBoletim() {
	const modalElement = document.getElementById('modalRegistroDeFatoComBoletim');
	const modal = new bootstrap.Modal(modalElement);
	// Abre o modal
	modal.show();

	if (document.querySelector('#enderecos-comBoletim-tab').classList.contains('active')) {
		setTimeout(inicializaOuAtualizaMapa, 100);
	}

	// Escuta evento de mudança de tab para "Endereços"
	document.querySelector('button#enderecos-comBoletim-tab').addEventListener('shown.bs.tab', function() {
		inicializaOuAtualizaMapa();
	}, { once: false }); // escuta sempre que usuário clicar na tab
}


function inicializaOuAtualizaMapa() {
	if (!window.meuMapaComBoletim) {
		window.meuMapaComBoletim = L.map('mapComBoletim').setView([-23.55052, -46.633308], 13);
		L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
			attribution: '&copy; OpenStreetMap contributors'
		}).addTo(window.meuMapaComBoletim);

		configurarCliqueMapa(window.meuMapaComBoletim);
	}
}

function exibirModalComBoletimAtendimento() {
	const modalElement = document.getElementById('modalRegistroDeFatoComBoletim');
	const modal = new bootstrap.Modal(modalElement);
	const checkboxTemBoletim = document.getElementById("tem_boletimComBoletim");
	checkboxTemBoletim.checked = window.temBoletimMarcado = true;
	TEM_BOLETIM = 1;
	window.temBoletimMarcado = true;
	document.getElementById('detalhamentoFato').disabled = true;
	document.getElementById('envolvimentoArma').disabled = true;
	document.getElementById('permitirAtendimento').disabled = true;
	prepararModalComBoletim();
	// Abre o modal
	modal.show();

	if (document.querySelector('#enderecos-comBoletim-tab').classList.contains('active')) {
		setTimeout(inicializaOuAtualizaMapa, 100);
	}

	// Escuta evento de mudança de tab para "Endereços"
	document.querySelector('button#enderecos-comBoletim-tab').addEventListener('shown.bs.tab', function() {
		inicializaOuAtualizaMapa();
	}, { once: false }); // escuta sempre que usuário clicar na tab
}


function inicializaOuAtualizaMapa() {
	if (!window.meuMapaComBoletim) {
		window.meuMapaComBoletim = L.map('mapComBoletim').setView([-23.55052, -46.633308], 13);
		L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
			attribution: '&copy; OpenStreetMap contributors'
		}).addTo(window.meuMapaComBoletim);

		configurarCliqueMapa(window.meuMapaComBoletim);
	}
}

function fecharModal() {
	const modalElement = document.getElementById("modalRegistroDeFatoComBoletim");
	const modalInstance = bootstrap.Modal.getInstance(modalElement);
	if (modalInstance) {
		modalInstance.hide();
	}
}

function salvarRegistroDeFatoComBoletim() {

	const extrairIdRegistroDeFato = (mensagem) => {
		const match = mensagem.match(/ID do Registro de Fato:\s*(\d+)/);
		return match ? parseInt(match[1], 10) : null;
	};

	modo = "cadastro";

	const registro = getRegistroFatoComBoletim(modo);
	const boletim = getDadosBoletim(modo);
	const documento = getDocumentosComBoletim(modo);
	const link = getLinksComBoletim(modo);
	const individuo = getIndividuosComBoletim(modo);
	const endereco = getEnderecosComBoletim(modo);
	const veiculo = getVeiculosComBoletim(modo);

	const grupo = getGruposUsuariosComBoletim(modo);
	const objeto = getObjetoComBoletim(modo);

	if (TEM_BOLETIM === 1) {
		if (registro == null || boletim == null || endereco == null) {
			Swal.fire("Atenção", "É obrigatório cadastrar todos os campos de registro de fato, boletim e endereço", "warning");
			return;
		}
	} else {
		if (registro == null || endereco == null) {
			Swal.fire("Atenção", "É obrigatório cadastrar todos os campos de registro de fato e endereço", "warning");
			return;
		}
	}

	if (!individuo || individuo.length === 0) {
		Swal.fire("Atenção", "É obrigatório cadastrar pelo menos um indivíduo.", "warning");
		return;
	}

	let payload = {}

	if (TEM_BOLETIM === 1) {
		payload = {
			registro: registro,
			boletim: boletim,
			individuos: individuo,
			enderecos: endereco,
			veiculos: veiculo,
			documentos: documento,
			links: link,
			grupos: grupo,
			objetos: objeto
		}
	} else {
		payload = {
			registro: registro,
			individuos: individuo,
			enderecos: endereco,
			veiculos: veiculo,
			grupos: grupo,
			objetos: objeto
		}
	}

	const btnSalvar = $("#idRegistroComBoletimModalSalvar");
	btnSalvar.prop("disabled", true);
	$.ajax({
		url: `/MuralhaDigital/RegistroDeFato?acao=cadastrarComBoletim&comBoletim=${TEM_BOLETIM}`,
		method: 'POST',
		contentType: 'application/json',
		dataType: 'xml',
		data: JSON.stringify(payload),
		success: function(xml) {
			try {
				const sucesso = $(xml).find("sucesso").text().trim() === "true";
				const mensagem = $(xml).find("mensagem").text() || "Registro salvo com sucesso.";

				if (sucesso) {
					Swal.fire("Sucesso", mensagem, "success");
					const registroDeFatoId = extrairIdRegistroDeFato(mensagem);
					eventoRetornoId(registroDeFatoId);
					fecharModal();
					verificarETratarMonitorados(veiculo);
				} else {
					Swal.fire("Erro", mensagem, "error");
				}

			} catch (e) {
				Swal.fire("Erro", "Erro ao interpretar a resposta do servidor.", "error");
			} finally {
				btnSalvar.prop("disabled", false);
			}
		},
		error: function(xhr, status, error) {
			console.error("Erro ao salvar:", error);
			Swal.fire("Erro", "Falha ao se comunicar com o servidor. Tente novamente.", "error");
			btnSalvar.prop("disabled", false);
		}
	});
}

/**
 * Verifica uma lista de veículos, identifica os que foram marcados para monitoramento E possuem status 'novo'
 * e pergunta ao usuário se deseja redirecionar para a tela de supervisionados.
 * @param {Array} listaDeVeiculos - O array de veículos vindo do formulário.
 * @returns {boolean} - Retorna true se o redirecionamento ocorrer, false caso contrário.
 */
function verificarETratarMonitorados(listaDeVeiculos) {
	const placas = [];

	if (Array.isArray(listaDeVeiculos)) {
		listaDeVeiculos.forEach(veiculo => {
			if (veiculo.monitorado && veiculo.status === 'novo') {
				placas.push(veiculo.placa);
			}
		});
	}

	if (placas.length > 0) {
		const redirecionar = confirm("Gostaria de cadastrar algum desses veículos como supervisionado?\n\nPlacas: " + placas.join(", "));

		if (redirecionar) {
			const placasParam = encodeURIComponent(placas.join(","));
			window.location.href = "../../monitorado/consulta.jsp?placas=" + placasParam;
			return true;
		}
	}

	return false;
}

async function atualizarRegistroDeFatoComBoletim() {
    const extrairIdRegistroDeFato = (mensagem) => {
        const match = mensagem.match(/ID do Registro de Fato:\s*(\d+)/);
        return match ? parseInt(match[1], 10) : null;
    };

	const registro = getRegistroFatoComBoletim(modo);
	let boletim = getDadosBoletim(modo);
	const individuo = getIndividuosComBoletim(modo);
	const objeto = getObjetoComBoletim(modo);
	const link = getLinksComBoletim(modo);
	const documento = getDocumentosComBoletim(modo);
	const veiculo = getVeiculosComBoletim(modo);
	const grupo = getGruposUsuariosComBoletim(modo);
	const endereco = getEnderecosComBoletim(modo);

	// Regra: só valida boletim se temBoletim estiver marcado
	if (registro.temBoletim === 1) {
		//precisa ter dados do boletim
		if (!boletim || Object.keys(boletim).length === 0) {
			Swal.fire("Aviso", "É necessário informar os dados do boletim.", "warning");
			return;
		}

		// 🔒 precisa ter pelo menos 1 indivíduo que não esteja removido
		const individuosAtivos = individuo.filter(ind => ind.status !== "removido");
		if (individuosAtivos.length === 0) {
			Swal.fire("Aviso", "Inclua pelo menos um indivíduo no boletim.", "warning");
			return;
		}
	} else {
		// temBoletim = 0 → descarta qualquer dado de boletim enviado
		boletim = null;
	}

	if (endereco === null) {
		Swal.fire("Aviso", "Inclua pelo menos um endereço no registro ou termine de preencher os campos obrigatórios.", "warning");
		return;
	}

	const payload = {
	    registro: registro,
	    boletim: boletim,
	    individuos: individuo,
	    objetos: objeto,
	    links: link,
	    documentos: documento,
	    veiculos: veiculo,
	    grupos: grupo,
	    enderecos: endereco
	};

    if (veiculo.length > 0 && registro.idStatus === 2) {
        const listaPlacasHtml = veiculo.map(v => `<li>${v.placa}</li>`).join("");

        const swalWithBootstrapButtons = Swal.mixin({
            customClass: {
                confirmButton: "btn btn-success",
                cancelButton: "btn btn-danger"
            },
            buttonsStyling: false
        });

        const result = await swalWithBootstrapButtons.fire({
            title: "Existem veículos monitorados",
            html: `
                <p style="margin-bottom: 8px;">Deseja encerrar o monitoramento?</p>
                <p><strong>Placas monitoradas:</strong></p>
                <ul style="padding-left: 20px; text-align: left; list-style-type: disc;">
                    ${listaPlacasHtml}
                </ul>
                <style>.swal2-actions{gap: 12px !important;}</style>
            `,
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sim, encerrar",
            cancelButtonText: "Não, cancelar",
            reverseButtons: true
        });

        if (result.isConfirmed) {
            const placas = veiculo.map(v => v.placa);
            encerrarMonitoramentoDasPlacas(placas);
            await swalWithBootstrapButtons.fire({
                title: "Encerrado!",
                text: "O monitoramento foi encerrado com sucesso.",
                icon: "success"
            });
        }
    }

    if (registro.temBoletim === 1) {
        if (!boletim || Object.keys(boletim).length === 0) {
            await Swal.fire("Aviso", "É necessário informar os dados do boletim.", "warning");
            return;
        }

        const individuosAtivos = individuo.filter(ind => ind.status !== "removido");
        if (individuosAtivos.length === 0) {
            await Swal.fire("Aviso", "Inclua pelo menos um indivíduo no boletim.", "warning");
            return;
        }
    } else {
        boletim = null;
    }

    if (endereco === null) {
        await Swal.fire("Aviso", "Inclua pelo menos um endereço no registro ou termine de preencher os campos obrigatórios.", "warning");
        return;
    }

    const btnSalvar = $("#idRegistroComBoletimModalSalvar");
    btnSalvar.prop("disabled", true);

    $.ajax({
        url: '/MuralhaDigital/RegistroDeFato?acao=edicaoComBoletim',
        method: 'POST',
        contentType: 'application/json',
        dataType: 'xml',
        data: JSON.stringify(payload),
        success: function(xml) {
            try {
                const sucesso = $(xml).find("sucesso").text().trim() === "true";
                const mensagem = $(xml).find("mensagem").text() || "Registro salvo com sucesso.";

                if (sucesso) {
                    Swal.fire("Sucesso", mensagem, "success");
                    const registroDeFatoId = extrairIdRegistroDeFato(mensagem);
                    eventoRetornoId(registroDeFatoId);
                    fecharModal();
                    verificarETratarMonitorados(veiculo);
                } else {
                    Swal.fire("Erro", mensagem, "error");
                }
            } catch (e) {
                Swal.fire("Erro", "Erro ao interpretar a resposta do servidor.", "error");
            } finally {
                btnSalvar.prop("disabled", false);
            }
        },
        error: function(xhr, status, error) {
            console.error("Erro ao salvar:", error);
            Swal.fire("Erro", "Falha ao se comunicar com o servidor. Tente novamente.", "error");
            btnSalvar.prop("disabled", false);
        }
    });
}


function eventoRetornoId(registroDeRatoId) {
	//Dispara evento com o ID do boletim
	const evento = new CustomEvent('RegistroDeFatoSalvoCadastro', {
		detail: { registroDeRatoId },
	});

	document.dispatchEvent(evento);
}

async function verificarPermissaoGrupoUsuarioConsulta() {
	try {
		const xml = await $.ajax({
			type: "GET",
			url: "/MuralhaDigital/PermissoesFuncionalidade",
			data: {
				acao: "verificarPermissaoGrupo"
			},
			dataType: "xml"
		});
		const parser = new DOMParser();
		const xmlDoc = parser.parseFromString(xml, "application/xml");
		const permitido = $(xml).find("permitido").text().toLowerCase() === "true";
		return permitido;

	} catch (error) {
		console.error("Erro ao verificar permissão de grupo!", error);
		return false; // segurança: nega se erro
	}
}

function AbrirListaAlertasCadMonitorado() {
	const id = ID_REGISTRO_FATO
	console.log("id ", id)
	if (id != null) {
		window.open(
			"/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?registro-fato=" + id,
			"_blank" // abre em nova aba
		);
		/*  window.location.href = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?cad-veiculo-monitorado=" + ID;*/
	} else {
		alert("ID do veículo monitorado não encontrado.");
	}
}

/**
 * Abre o modal de edição de Fato/Boletim diretamente na aba "Boletim".
 * Usado especificamente pela tela de Veículos Correlacionados.
 * @param {number} id - O ID do Registro de Fato a ser exibido.
 */
window.preencherEExibirModalBoletim = async function(id) {
	modo = "visualizar"; // Garante que o modal abra em modo de visualização (sem botão de salvar)
	$("body").addClass("loading");
	limpaCamposModalComBoletim();
	bloqueiaCamposModalComBoletim();
	await configuraModalVisualizarComBoletim();
	await buscarDadosModalVisualizarAbosTiposRegistro(id); // Busca os dados do B.O.

	//Ativa a aba "Boletim" como padrão antes de exibir
	const tabBoletim = document.getElementById('boletim-comBoletim-tab');
	if (tabBoletim) {
		const tab = new bootstrap.Tab(tabBoletim);
		tab.show();
	}

	exibirModalComBoletim(); // Exibe o modal já preenchido
	atualizarBotoesModalComBoletim(); // Esconde o botão "Salvar"
	$("body").removeClass("loading");
}

/**
 * Analisa o XML e decide se o botão Salvar deve ser bloqueado ou habilitado,
 * seguindo a nova regra de prioridade baseada no status 'Encerrado' (2).
 * @param {string} xmlString A resposta XML do servidor.
 */
function verificarStatusEBloquearBotao(xmlString) {
	const parser = new DOMParser();
	const xmlDoc = parser.parseFromString(xmlString, "text/xml");
	const registro = xmlDoc.querySelector("RegistroDeFato");

	if (!registro) {
		return; // Sai se o XML for inválido
	}

	const idStatus = registro.querySelector("idStatus")?.textContent.trim();
	if (idStatus === '2') {
		bloquearBotaoSavlar();
	} else {
		habilitarBotaoSavlar();
	}
}

function consultarVeiculosPorRegistroFato(idRegistroFato) {
	veiculos = [];
    $.ajax({
        url: '/MuralhaDigital/RegistroDeFato',
        method: 'GET',
        dataType: 'xml',
        data: {
            acao: 'consultarVeiculosMonitorados',
            idRegistroFato: idRegistroFato
        },
        success: function(xml) {
            try {
                $(xml).find("veiculo").each(function() {
                    const v = {
                        id: $(this).find("id").text().trim(),
                        placa: $(this).find("placa").text().trim(),
                        nome: $(this).find("nome").text().trim(),
                        tipoAlerta: $(this).find("tipoAlertaOcorrencia").text().trim(),
                        descricao: $(this).find("descricao").text().trim(),
                        dataInicio: $(this).find("dataInicio").text().trim(),
                        dataFim: $(this).find("dataFim").text().trim(),
                        dataCadastro: $(this).find("dataCadastro").text().trim(),
                        nomeUsuario: $(this).find("nomeUsuario").text().trim()
                    };
                    veiculos.push(v);
                });

                console.log("Veículos monitorados:", veiculos);

            } catch (e) {
                Swal.fire("Erro", "Erro ao interpretar os dados dos veículos.", "error");
            }
        },
        error: function(xhr, status, error) {
            console.error("Erro ao consultar veículos:", error);
            Swal.fire("Erro", "Falha ao se comunicar com o servidor. Tente novamente.", "error");
        }
    });
}

function encerrarMonitoramentoDasPlacas(placas) {
  if (!Array.isArray(placas) || placas.length === 0) {
    console.warn("Nenhuma placa foi informada.");
    return;
  }

  $.ajax({
    url: "/MuralhaDigital/RegistroDeFato?acao=EncerrarMonitoramento",
    method: "POST",
    contentType: "application/json",
    dataType: "xml",
    data: JSON.stringify(placas),
    success: function(xml) {
      const sucesso = $(xml).find("sucesso").text().trim() === "true";
      const mensagem = $(xml).find("mensagem").text().trim();

      if (sucesso) {
        console.log("Monitoramento encerrado com sucesso:", mensagem);
      } else {
        console.error("Erro ao encerrar monitoramento:", mensagem);
      }
    },
    error: function(xhr, status, error) {
      console.error("Falha na requisição:", error);
    }
  });
}

