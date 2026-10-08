var registroDeFatoComBoletim = [];
var registroDeFatoSemBoletim = [];
var alertasIdsPesquisaRapida = [];
var veiculosPesquisaRapida = [];
var veiculoMonitoradoIdsPesquisaRapida = [];
var idTipoConsultaPesquisaRapida = null;
var cpfPesquisaRapida = null;
async function abrirModalPesquisaRapida(valor, idTipoConsulta) {
	try {
		const responseText = await BuscarInformacoes(valor, idTipoConsulta);

		if (responseText) {
			// Verifica se veio algum resultado antes de carregar ou abrir modal
			const parser = new DOMParser();
			const xmlDoc = parser.parseFromString(responseText, "application/xml");
			const totalRegistros = parseInt(xmlDoc.querySelector("totalRegistros")?.textContent ?? "0");

			if (totalRegistros === 0) {
				Swal.fire({
					icon: "info",
					title: "Nenhum dado encontrado",
					text: "Não foram encontradas informações para os dados informados."
				});
				return;
			}
			idTipoConsultaPesquisaRapida = idTipoConsulta;
			CarregarInformacoesTela(responseText);

			const modalElement = document.getElementById('modalPesquisaRapida');
			const bootstrapModal = new bootstrap.Modal(modalElement);
			bootstrapModal.show();
		} else {
			Swal.fire({
				icon: "info",
				title: "Nenhum dado encontrado",
				text: "Não foram encontradas informações para os dados informados."
			});
			return;
		}
	} catch (e) {
		console.error("Erro inesperado ao buscar informações:", e);
		Swal.fire({
			icon: "error",
			title: "Erro inesperado",
			text: "Algo deu errado ao tentar abrir a pesquisa rápida."
		});
	}
}


function CarregarInformacoesTela(responseText) {
	resetGlobals();
	const xmlDoc = parseXml(responseText);

	const nome = getTextContent(xmlDoc, "nome", "N/A");
	const cpf = getTextContent(xmlDoc, "cpf", "N/A");

	// Todos os registros
	const todosRegistros = xmlDoc.querySelectorAll("registrosFato > registroFato") || [];

	// Filtra por temBoletim
	const registrosComBoletim = [];
	const registrosSemBoletim = [];
	
	// 🔹 contadores totais
	let totalAbordagensComBoletim = 0;
	let totalAbordagensSemBoletim = 0;

	todosRegistros.forEach(registro => {
		const temBoletim = registro.querySelector("temBoletim")?.textContent === "true";
		const abordagens = parseInt(registro.querySelector("totalDeAbordagens")?.textContent || "0", 10);

		if (temBoletim) {
			registrosComBoletim.push(registro);
			totalAbordagensComBoletim += abordagens;
		} else {
			registrosSemBoletim.push(registro);
			totalAbordagensSemBoletim += abordagens;
		}
	});
	preencherTotaisAbordagens(totalAbordagensSemBoletim, totalAbordagensComBoletim);

	const alertas = xmlDoc.querySelectorAll("alertas > alerta") || [];
	const veiculosMonitorados = xmlDoc.querySelectorAll("veiculosMonitorados > veiculo") || [];

	// Atualiza os contadores de registro de fato
	preencherDadosBasicos(nome, cpf, registrosComBoletim.length, registrosSemBoletim.length);

	// Monta mapa de veículos
	const mapaVeiculos = montarMapaVeiculos(alertas, registrosComBoletim, registrosSemBoletim, veiculosMonitorados);

	montarBadgesVeiculos(mapaVeiculos);
	preencherVeiculosMonitorados(veiculosMonitorados);

	const { totalSupervisionado, totalSimples } = contarTiposAlertas(alertas);
	preencherContadoresAlertas(totalSupervisionado, totalSimples);

	const imagens = montarImagensAlertas(alertas);
	montarGaleriaOuLimpar(imagens);

	mostrarBotoesDetalhes();
}

function preencherTotaisAbordagens(totalSemBoletim, totalComBoletim) {
  const textoSem = totalSemBoletim === 1
    ? "1 abordagem"
    : totalSemBoletim + " abordagens";

  const textoCom = totalComBoletim === 1
    ? "1 abordagem"
    : totalComBoletim + " abordagens";

  document.getElementById("abordagensSemBoletins").textContent = textoSem;
  document.getElementById("abordagensComBoletins").textContent = textoCom;
}

function preencherVeiculosMonitorados(veiculosMonitorados) {
	const total = veiculosMonitorados.length;
	document.getElementById("veiculosMonitoradoPesquisaRapida").textContent = `${total} Registros(s)`;

	veiculoMonitoradoIdsPesquisaRapida = Array.from(veiculosMonitorados)
		.map(v => v.querySelector("id")?.textContent)
		.filter(id => !!id);
}

function parseXml(xmlString) {
	const parser = new DOMParser();
	return parser.parseFromString(xmlString, "application/xml");
}

function getTextContent(xmlDoc, selector, defaultValue = "") {
	return xmlDoc.querySelector(selector)?.textContent ?? defaultValue;
}

function preencherDadosBasicos(nome, cpf, totalComBoletim = 0, totalSemBoletim = 0) {
	cpfPesquisaRapida = formatarCPF(cpf) || null;

	setTextContent("infoNome", nome || "-");
	setTextContent("infoCPF", cpfPesquisaRapida || "N/A");
	setTextContent("registroDeFatoComboletins", `${totalComBoletim} ocorrência(s)`);
	setTextContent("registroDeFatoSemboletins", `${totalSemBoletim} ocorrência(s)`);

	clearContainer("infoVeiculos");
}

function setTextContent(elementId, text) {
	const el = document.getElementById(elementId);
	if (el) el.textContent = text;
}

function clearContainer(elementId) {
	const el = document.getElementById(elementId);
	if (el) el.innerHTML = '';
}

function formatarCPF(cpf) {
	if (!cpf) return null;

	const numeros = cpf.replace(/\D/g, '');

	if (numeros.length !== 11) return cpf; // Retorna original se não for CPF válido

	return numeros.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
}

function resetGlobals() {
	alertasIdsPesquisaRapida = [];
	registroDeFatoComBoletim = [];
	registroDeFatoSemBoletim = [];
	veiculosPesquisaRapida = [];
	cpfPesquisaRapida = null;
	veiculoMonitoradoIdsPesquisaRapida = [];
}

function montarMapaVeiculos(alertas, registrosComBoletim, registrosSemBoletim, veiculosMonitorados) {
	const mapaVeiculos = new Map();

	// [alertas]
	alertas.forEach(alerta => {
		const idAlerta = alerta.querySelector("id")?.textContent;
		const placa = alerta.querySelector("placa")?.textContent?.toUpperCase();
		const supervisionado = alerta.querySelector("supervisionado")?.textContent === "true";

		if (idAlerta) alertasIdsPesquisaRapida.push(idAlerta);
		if (!placa) return;

		let info = mapaVeiculos.get(placa);
		if (!info) {
			info = { alertaIds: [], registroFatoComBoletimIds: [], registroFatoSemBoletimIds: [], supervisionado: false, monitorado: false };
			mapaVeiculos.set(placa, info);
		}

		info.alertaIds.push(idAlerta);
		if (supervisionado) info.supervisionado = true;
	});

	// [registros com boletim]
	registrosComBoletim.forEach(registro => {
		const registroId = registro.querySelector("id")?.textContent;
		if (!registroId) return;

		registroDeFatoComBoletim.push(registroId);

		const veiculos = registro.querySelectorAll("veiculos > veiculo") || [];
		veiculos.forEach(veiculo => {
			const placa = veiculo.querySelector("placa")?.textContent?.toUpperCase();
			if (!placa) return;

			let info = mapaVeiculos.get(placa);
			if (!info) {
				info = { alertaIds: [], registroFatoComBoletimIds: [], registroFatoSemBoletimIds: [], supervisionado: false, monitorado: false };
				mapaVeiculos.set(placa, info);
			}

			info.registroFatoComBoletimIds.push(registroId);
		});
	});

	// [registros sem boletim]
	registrosSemBoletim.forEach(registro => {
		const registroId = registro.querySelector("id")?.textContent;
		if (!registroId) return;

		registroDeFatoSemBoletim.push(registroId);

		const veiculos = registro.querySelectorAll("veiculos > veiculo") || [];
		veiculos.forEach(veiculo => {
			const placa = veiculo.querySelector("placa")?.textContent?.toUpperCase();
			if (!placa) return;

			let info = mapaVeiculos.get(placa);
			if (!info) {
				info = { alertaIds: [], registroFatoComBoletimIds: [], registroFatoSemBoletimIds: [], supervisionado: false, monitorado: false };
				mapaVeiculos.set(placa, info);
			}

			info.registroFatoSemBoletimIds.push(registroId);
		});
	});

	// [veiculos monitorados]
	veiculosMonitorados.forEach(veiculo => {
		const placa = veiculo.querySelector("placa")?.textContent?.toUpperCase();
		const supervisionado = veiculo.querySelector("supervisionado")?.textContent === "true";
		if (!placa) return;

		let info = mapaVeiculos.get(placa);
		if (!info) {
			info = { alertaIds: [], registroFatoComBoletimIds: [], registroFatoSemBoletimIds: [], supervisionado: false, monitorado: true };
			mapaVeiculos.set(placa, info);
		}

		info.monitorado = true;
		if (supervisionado) info.supervisionado = true;
	});

	veiculosPesquisaRapida = Array.from(mapaVeiculos.keys());

	return mapaVeiculos;
}

function montarBadgesVeiculos(mapaVeiculos) {
	const containerVeiculos = document.getElementById("infoVeiculos");
	containerVeiculos.innerHTML = ''; // Limpa antes de preencher
	const placasSet = new Set();

	mapaVeiculos.forEach((info, placa) => {
		if (placasSet.has(placa)) return;
		placasSet.add(placa);

		const span = document.createElement('span');
		span.className = 'badge bg-light text-dark rounded-pill px-2 py-1 d-inline-flex align-items-center me-1 mb-1';

		const alertaIds = info.alertaIds?.join(',') ?? '';
		const registroComBoletimIds = info.registroFatoComBoletimIds?.join(',') ?? '';
		const registroSemBoletimIds = info.registroFatoSemBoletimIds?.join(',') ?? '';

		span.innerHTML = `
            <span class="me-2 fw-bold text-uppercase">${placa}</span>
			
			${registroSemBoletimIds ? `
			    <button type="button" class="btn btn-sm btn-outline-primary me-1" 
			            title="Registro(s) de Fato sem Boletim da placa ${placa}" 
			            onclick="mostrarInfoVeiculo('${placa}', 'registroSemBoletim')">
			        <i class="fa fa-file"></i>
			    </button>` : ''}
			
			${registroComBoletimIds ? `
			    <button type="button" class="btn btn-sm btn-outline-info me-1" 
			            title="Registro(s) de Fato com Boletim da placa ${placa}" 
			            onclick="mostrarInfoVeiculo('${placa}', 'registroComBoletim')">
			        <i class="fa fa-file-alt"></i>
			    </button>` : ''}

            ${info.monitorado ? `
                <button type="button" class="btn btn-sm btn-outline-warning me-1" 
                        title="Mais informações do Veículo Monitorado (${placa})" 
                        onclick="mostrarInfoVeiculo('${placa}', 'veiculoMonitorado')">
                    <i class="fa fa-eye"></i>
                </button>` : ''}

            ${alertaIds ? `
                <button type="button" class="btn btn-sm btn-outline-danger me-1" 
                        title="Alerta(s) da placa ${placa}"
                        onclick="mostrarInfoVeiculo('${placa}', 'alerta')">
                    <i class="fa fa-bell"></i>
                </button>` : ''}
        `;

		containerVeiculos.appendChild(span);
	});
}

function contarTiposAlertas(alertas) {
	let totalSupervisionado = 0;
	let totalSimples = 0;

	alertas.forEach(alerta => {
		const supervisionado = alerta.querySelector("supervisionado")?.textContent === "true";
		if (supervisionado) totalSupervisionado++;
		else totalSimples++;
	});

	return { totalSupervisionado, totalSimples };
}

function preencherContadoresAlertas(totalSupervisionado, totalSimples) {
	document.getElementById("alarmesSupervisionado").textContent = `SUPERVISIONADO: ${totalSupervisionado}`;
	document.getElementById("alarmesSimples").textContent = `SIMPLES: ${totalSimples}`;
}

function montarImagensAlertas(alertas) {
	const imagens = [];
	alertas.forEach(alerta => {
		const idAlerta = alerta.querySelector("id")?.textContent;
		const placa = alerta.querySelector("placa")?.textContent?.trim() || "N/A";

		if (idAlerta) {
			const baseUrl = `/MuralhaDigital/Veiculo/Imagem?acao=ImagemByIdAlerta&idAlerta=${encodeURIComponent(idAlerta)}`;
			imagens.push({
				placa,
				full: baseUrl,
				thumb: baseUrl
			});
		}
	});
	return imagens;
}

function montarGaleriaOuLimpar(imagens) {
	if (imagens.length > 0) {
		montarCarouselGaleria(imagens);
	} else {
		document.getElementById("galeriaImagensCarouselContainer").innerHTML = 'Sem Informação';
	}
}

function mostrarBotoesDetalhes() {
	document.getElementById('btnRegistroDeFatoComBoletins').style.display =
		registroDeFatoComBoletim.length > 0 ? 'inline-block' : 'none';

	document.getElementById('btnRegistroDeFatoSemBoletins').style.display =
		registroDeFatoSemBoletim.length > 0 ? 'inline-block' : 'none';

	document.getElementById('btnDetalhesAlertas').style.display =
		alertasIdsPesquisaRapida.length > 0 ? 'inline-block' : 'none';

	document.getElementById('btnDetalhesVeiculoMonitorado').style.display =
		veiculoMonitoradoIdsPesquisaRapida.length > 0 ? 'inline-block' : 'none';
}

function montarCarouselGaleria(imagens) {
	const container = document.getElementById('galeriaImagensCarouselContainer');
	const carouselId = "carouselGaleriaImagens";

	if (!imagens || imagens.length === 0) {
		container.innerHTML = "<p>Nenhuma imagem disponível.</p>";
		return;
	}

	let html = `
	<div id="${carouselId}" class="carousel slide" data-bs-ride="carousel">
	  <div class="carousel-inner">
	`;

	for (let i = 0; i < imagens.length; i += 3) {
		const grupo = imagens.slice(i, i + 3);
		html += `
		<div class="carousel-item ${i === 0 ? "active" : ""}">
		  <div class="row d-flex justify-content-center">
		`;

		grupo.forEach((imagem, index) => {
			html += `
			<div class="col-12 col-sm-6 col-md-4 d-flex justify-content-center mb-3">
			  <div class="position-relative text-center">
				<div class="bg-dark text-white py-1 px-2 mb-1 rounded placa-overlay small">
				  Placa: ${imagem.placa}
				</div>
				<a href="${imagem.full}" data-lightbox="galeria-imagens" data-title="Imagem ${i + index + 1}">
				  <img src="${imagem.thumb}" class="img-thumbnail" style="height: 140px; width: 100%; object-fit: cover;">
				</a>
				<a href="javascript:void(0);"
				   class="info-overlay-btn d-block text-center mt-1"
				   title="Mais informações"
				   onclick="abrirMaisInformacoesImagem('${encodeURIComponent(imagem.full)}')">
				   <i class="fas fa-info-circle me-1"></i> Mais informações
				</a>
			  </div>
			</div>
			`;
		});

		html += `
		  </div>
		</div>
		`;
	}

	html += `
	  </div>
	  <button class="carousel-control-prev" type="button" data-bs-target="#${carouselId}" data-bs-slide="prev">
		<span class="carousel-control-prev-icon"></span>
		<span class="visually-hidden">Anterior</span>
	  </button>
	  <button class="carousel-control-next" type="button" data-bs-target="#${carouselId}" data-bs-slide="next">
		<span class="carousel-control-next-icon"></span>
		<span class="visually-hidden">Próximo</span>
	  </button>
	</div>
	`;

	container.innerHTML = html;
}


function abrirMaisInformacoesImagem(urlImagem) {
	try {
		const urlDecodificada = decodeURIComponent(urlImagem);
		const url = new URL(urlDecodificada, window.location.origin);
		const idAlerta = url.searchParams.get("idAlerta");

		if (idAlerta) {
			const urlNova = `/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=${encodeURIComponent(idAlerta)}`;
			window.open(urlNova, '_blank');
		} else {
			console.warn("ID do alerta não encontrado na URL:", urlDecodificada);
		}
	} catch (e) {
		console.error("Erro ao processar a URL da imagem:", urlImagem, e);
	}
}

function fecharModalPesquisaRapida() {
	const modalElement = document.getElementById('modalPesquisaRapida');
	const bootstrapModal = new bootstrap.Modal(modalElement);
	bootstrapModal.hide();
}

function abrirTodosRegistroDeFatoBoletins(tipo) {
    let ids = [];
    let urlBase = "";

    if (tipo === "ComBoletim") {
        ids = registroDeFatoComBoletim || [];
        urlBase = "/muralha-digital/pages/registro_fato/comBoletim/";
    } else if (tipo === "SemBoletim") {
        ids = registroDeFatoSemBoletim || [];
        urlBase = "/muralha-digital/pages/registro_fato/comBoletim/";
    }

    if (!ids || ids.length === 0) {
        alert(`Nenhum registro de fato ${tipo === "ComBoletim" ? "com boletim" : "sem boletim"} encontrado.`);
        return;
    }

    let url = "";

	if (idTipoConsultaPesquisaRapida === 1 && veiculosPesquisaRapida.length > 0) {
	    // Consulta por veículo (placas, separadas por vírgula)
	    const placas = veiculosPesquisaRapida.join(",");
	    url = urlBase + "consulta.jsp?placa=" + encodeURIComponent(placas);

    } else if ((idTipoConsultaPesquisaRapida === 2 || idTipoConsultaPesquisaRapida === 3) && cpfPesquisaRapida && cpfPesquisaRapida.length > 0) {
        // Consulta por pessoa (CPF)
        url = urlBase + "consulta.jsp?cpf=" + encodeURIComponent(cpfPesquisaRapida);

    } else {
        // Caso padrão: abrir com lista de registros
        url = urlBase + "consulta.jsp?registroId=" + ids.join(',');
    }

    window.open(url, '_blank');
}

function abrirTodosAlertas() {
	if (!alertasIdsPesquisaRapida || alertasIdsPesquisaRapida.length === 0) {
		alert("Nenhum Alerta encontrado.");
		return;
	}

	// 🚗 Consulta por veículo (idTipoConsultaPesquisaRapida == 1)
	if (veiculosPesquisaRapida.length === 1) {
		// Apenas 1 placa → abre direto
		const placa = veiculosPesquisaRapida[0];
		const url = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?placa=" + encodeURIComponent(placa);
		window.open(url, '_blank');
		return;
	} else if (veiculosPesquisaRapida.length > 1) {
		// Várias placas → abre com todas separadas por vírgula
		const placasParam = veiculosPesquisaRapida.map(p => p.trim().toUpperCase()).join(",");
		const url = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?placa=" + placasParam;
		window.open(url, '_blank');
		return;
	}

	// 🔔 Caso padrão: abrir com lista de alertas
	const url = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?alertaId=" + alertasIdsPesquisaRapida.join(',');
	window.open(url, '_blank');
}

function abrirTodosVeiculosMonitorado() {
    if (!veiculoMonitoradoIdsPesquisaRapida || veiculoMonitoradoIdsPesquisaRapida.length === 0) {
        alert("Nenhum Veículo Monitorado encontrado.");
        return;
    }

    // Tipo consulta 1 (Veículo) e apenas uma placa
    if (veiculosPesquisaRapida.length === 1) {
        const placa = veiculosPesquisaRapida[0];
        const url = "/muralha-digital/pages/monitorado/consulta.jsp?placa=" + encodeURIComponent(placa);
        window.open(url, '_blank');
        return;
    }else if (veiculosPesquisaRapida.length > 1) {
        const placasParam = veiculosPesquisaRapida.map(p => encodeURIComponent(p.trim().toUpperCase())).join(',');
        const url = "/muralha-digital/pages/monitorado/consulta.jsp?placa=" + placasParam;
        window.open(url, '_blank');
        return;
    }

    // Caso padrão: abrir por IDs dos veículos monitorados
    const url = "/muralha-digital/pages/monitorado/consulta.jsp?veiculoMonitoradoId=" + veiculoMonitoradoIdsPesquisaRapida.join(',');
    window.open(url, '_blank');
}

function mostrarInfoVeiculo(placa, tela) {
	let url = "";

	switch (tela) {
		case "veiculoMonitorado":
			url = "/muralha-digital/pages/monitorado/consulta.jsp?placa=" + placa;
			break;
		case "boletim":
			url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?placa=" + placa;
			break;
		case "alerta":
			url = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?placa=" + placa;
			break;
		case "registroComBoletim":
			url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?placa=" + placa;
			break;
		case "registroSemBoletim":
			url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?placa=" + placa;
			break;
		default:
			console.warn("Tela não reconhecida:", tela);
			return;
	}

	window.open(url, '_blank', 'noopener,noreferrer');
}

function cadastrarRegistroDeFato(tipo) {
	let url = "";
    if (tipo === "ComBoletim") {
        url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp";
    } else if (tipo === "SemBoletim") {
        url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp";
    } else {
        console.error("Tipo de cadastro inválido:", tipo);
    }
	window.open(url, '_blank', 'noopener,noreferrer');
}