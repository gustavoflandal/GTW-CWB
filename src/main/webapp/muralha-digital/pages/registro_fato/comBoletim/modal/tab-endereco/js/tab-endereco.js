window.enderecoEditandoId = null;
// Máscara para CEP

document.addEventListener("DOMContentLoaded", function() {
	const cepInput = document.getElementById("cepComBoletim");
	if (cepInput) {
		cepInput.addEventListener("input", async function(e) {
			// Formata CEP
			e.target.value = formatarCEP(e.target.value);

			// Remove tudo que não for número
			const somenteNumeros = e.target.value.replace(/\D/g, "");

			// Chama a função buscarEnderecoPorCEP quando tiver 8 dígitos completos
			if (somenteNumeros.length === 8) {
				await buscarEnderecoPorCEP();
			}
		});
	}
});

function formatarCEP(value) {
	value = value.replace(/\D/g, '');
	if (value.length > 5) {
		value = value.replace(/^(\d{5})(\d)/, '$1-$2');
	}
	return value;
}

// Clique no mapa para preencher endereço
function configurarCliqueMapa(map) {
	if (!map || map.cliqueConfigurado) return;

	map.on('click', async function(e) {
		const { lat, lng } = e.latlng;
		atualizarMarkerComBoletim(lat, lng);
		atualizarLatLngInputs(lat, lng);
		const address = await buscarEnderecoPorLatLng(lat, lng);
		preencherCamposEndereco(address);
	});
	map.cliqueConfigurado = true;
	const lat = document.getElementById("latitudeComBoletim").value;
	const lng = document.getElementById("longitudeComBoletim").value;
	if (lat && lng) {
		atualizarMarkerComBoletim(lat, lng);
	}
}

async function buscarEnderecoPorLatLng(lat, lng) {
	try {
		const res = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&addressdetails=1`);
		const data = await res.json();
		return data.address;
	} catch (err) {
		console.error('Erro ao buscar endereço:', err);
		return null;
	}
}

function atualizarLatLngInputs(lat, lng) {
	document.getElementById("latitudeComBoletim").value = lat.toFixed(6);;
	document.getElementById("longitudeComBoletim").value = lng.toFixed(6);
}

function preencherCamposEndereco(address) {
	if (!address) return;

	const rua = document.getElementById('ruaComBoletim');
	const bairro = document.getElementById('bairroComBoletim');
	const cidadeSelect = document.getElementById('idCidadeComBoletim'); // select
	const cep = document.getElementById('cepComBoletim');

	if (rua) rua.value = address.road || '';
	if (bairro) bairro.value = address.suburb || address.neighbourhood || '';
	if (cep) cep.value = address.postcode || '';

	// Determina a cidade com prioridade: city > town > municipality > county > state
	const cidadeNome = address.city || address.town || address.municipality || address.county || address.state || '';

	if (cidadeSelect && cidadeNome) {
		selecionarCidadePorNome(cidadeSelect, cidadeNome);
	}
}

// Validação do formulário (todos obrigatórios, exceto complemento)
function validarEnderecoComBoletim() {
	const camposObrigatorios = [
		'cepComBoletim',
		'logradouroComBoletim',
		'bairroComBoletim',
		'cidadeComBoletim',
		'estadoComBoletim'
	];

	for (let id of camposObrigatorios) {
		const campo = document.getElementById(id);
		if (!campo.value.trim()) {
			Swal.fire({
				icon: 'warning',
				title: 'Campo obrigatório',
				text: `Preencha o campo: ${campo.previousElementSibling.innerText}`
			});
			campo.focus();
			return false;
		}
	}
	return true;
}

function atualizarMarkerComBoletim(lat, lng) {
	if (!window.meuMapaComBoletim) return;

	const latLng = [lat, lng];

	if (window.marcadorEnderecoComBoletim) {
		window.marcadorEnderecoComBoletim.setLatLng(latLng);
	} else {
		window.marcadorEnderecoComBoletim = L.marker(latLng, { draggable: true }).addTo(window.meuMapaComBoletim);

		window.marcadorEnderecoComBoletim.on('dragend', async function(ev) {
			const pos = ev.target.getLatLng();
			atualizarLatLngInputs(pos.lat, pos.lng);
			const address = await buscarEnderecoPorLatLng(pos.lat, pos.lng);
			preencherCamposEndereco(address);
		});
	}

	window.meuMapaComBoletim.setView(latLng, 16);
}

function limparCamposEnderecoComBoletim() {
	const form = document.getElementById("formEnderecoComBoletim");
	if (form) form.reset();

	// Zerar selects manualmente
	const selects = ["idCidadeComBoletim"]; 
	selects.forEach(id => {
		const el = document.getElementById(id);
		if (el) el.selectedIndex = 0;
	});

	// Limpar inputs individuais
	const inputs = [
		"cepComBoletim",
		"bairroComBoletim",
		"complementoComBoletim",
		"ruaComBoletim",
		"numeroComBoletim",
		"latitudeComBoletim",
		"longitudeComBoletim"
	];
	inputs.forEach(id => {
		const el = document.getElementById(id);
		if (el) el.value = "";
	});

	// Limpar marker do mapa
	if (window.marcadorEnderecoComBoletim && window.meuMapaComBoletim) {
		window.meuMapaComBoletim.removeLayer(window.marcadorEnderecoComBoletim);
		window.marcadorEnderecoComBoletim = null;
	}

	// Opcional: centralizar o mapa na posição inicial
	if (window.meuMapaComBoletim) {
		window.meuMapaComBoletim.setView([-23.55052, -46.633308], 13);
	}
	
	// Limpar tabela de endereços adicionais
	const tabela = document.getElementById("tabelaEnderecosAdicionaisComBoletim");
	if (tabela) {
	    const tbody = tabela.querySelector("tbody");
	    if (tbody) tbody.innerHTML = "";
	}
}

function preencherTabEnderecosComBoletim(xmlString) {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlString, "application/xml");
    const enderecos = xmlDoc.getElementsByTagName("endereco");
    if (!enderecos || enderecos.length === 0) return;

    // 🔹 1º endereço → formulário
    const endereco = enderecos[0];
	window.enderecoEditandoId = parseInt(endereco.querySelector(":scope > id")?.textContent || "0");

    const latitude = parseFloat(endereco.getElementsByTagName("latitude")[0]?.textContent || "0");
    const longitude = parseFloat(endereco.getElementsByTagName("longitude")[0]?.textContent || "0");

    document.getElementById("cepComBoletim").value = formatarCEP(endereco.getElementsByTagName("cep")[0]?.textContent || "");
    document.getElementById("ruaComBoletim").value = endereco.getElementsByTagName("rua")[0]?.textContent || "";
    document.getElementById("bairroComBoletim").value = endereco.getElementsByTagName("bairro")[0]?.textContent || "";
    document.getElementById("numeroComBoletim").value = endereco.getElementsByTagName("numero")[0]?.textContent || "";
    document.getElementById("complementoComBoletim").value = endereco.getElementsByTagName("complemento")[0]?.textContent || "";

    const cidadeId = endereco.getElementsByTagName("idCidade")[0]?.textContent || "";
    const tipoEventoId = endereco.getElementsByTagName("idTipoEvento")[0]?.textContent || "";

    const selectCidade = document.getElementById("idCidadeComBoletim");
    if (selectCidade && cidadeId) selectCidade.value = cidadeId;

    const selectTipoEvento = document.getElementById("idTipoEventoComBoletim");
    if (selectTipoEvento && tipoEventoId) selectTipoEvento.value = tipoEventoId;

    document.getElementById("latitudeComBoletim").value = latitude;
    document.getElementById("longitudeComBoletim").value = longitude;

    if (latitude && longitude) {
        atualizarMarkerComBoletim(latitude, longitude);
    }

    // 🔹 Endereços adicionais → tabela
    const tabelaBody = document.querySelector("#tabelaEnderecosAdicionaisComBoletim tbody");
    tabelaBody.innerHTML = ""; // limpa antes de preencher

	for (let i = 1; i < enderecos.length; i++) {
	    const e = enderecos[i];
	    const tipoEvento = e.getElementsByTagName("evento")[0]
	        ?.getElementsByTagName("descricao")[0]?.textContent || "";
	    const rua = e.getElementsByTagName("rua")[0]?.textContent || "";
	    const numero = e.getElementsByTagName("numero")[0]?.textContent || "";
	    const bairro = e.getElementsByTagName("bairro")[0]?.textContent || "";
	    const cidade = e.getElementsByTagName("cidade")[0]?.getElementsByTagName("nome")[0]?.textContent || "";

	    const enderecoFormatado = `${rua}, ${numero} - ${bairro} - ${cidade}`;

	    const tr = document.createElement("tr");
	    tr.innerHTML = `
	        <td>${tipoEvento}</td>
	        <td>${enderecoFormatado}</td>
	    `;
	    tabelaBody.appendChild(tr);
	}
}

function getEnderecosComBoletim() {
	// Pega os elementos do formulário
	const tipoEventoSelect = document.getElementById("idTipoEventoComBoletim");
	const cidadeSelect = document.getElementById("idCidadeComBoletim");
	const cepInput = document.getElementById("cepComBoletim");
	const bairroInput = document.getElementById("bairroComBoletim");
	const ruaInput = document.getElementById("ruaComBoletim");
	const numeroInput = document.getElementById("numeroComBoletim");
	const complementoInput = document.getElementById("complementoComBoletim");
	const latitudeInput = document.getElementById("latitudeComBoletim");
	const longitudeInput = document.getElementById("longitudeComBoletim");

	// Lista de campos obrigatórios (complemento não entra)
	const camposObrigatorios = [
		tipoEventoSelect,
		cidadeSelect,
		cepInput,
		bairroInput,
		ruaInput,
		numeroInput,
		latitudeInput,
		longitudeInput
	];

	// Limpa erros anteriores
	camposObrigatorios.forEach(el => el.classList.remove("is-invalid"));

	let valido = true;

	// Valida campos obrigatórios
	camposObrigatorios.forEach(el => {
		if (!el.value || el.value.trim() === "" || el.value === "0") {
			el.classList.add("is-invalid");
			valido = false;
		}
	});

	if (!valido) {
		Swal.fire({
			icon: 'warning',
			title: 'Campos obrigatórios',
			text: 'Por favor, preencha todos os campos obrigatórios do endereço.',
			confirmButtonText: 'OK'
		});
		return null;
	}

	// Cria o objeto de endereço
	const endereco = {
		id: window.enderecoEditandoId,
		idTipoEvento: parseInt(tipoEventoSelect.value, 10),
		idCidade: parseInt(cidadeSelect.value, 10),
		bairro: bairroInput.value.trim(),
		cep: cepInput.value.replace(/\D/g, '').trim(),
		rua: ruaInput.value.trim(),
		numero: numeroInput.value.trim(),
		complemento: complementoInput.value.trim(),
		latitude: latitudeInput.value.trim(),
		longitude: longitudeInput.value.trim()
	};

	return [endereco];
}

async function buscarEnderecoPorCEP() {
	const cepInput = document.getElementById("cepComBoletim");
	let cep = cepInput?.value.replace(/\D/g, "");

	if (!cep || cep.length !== 8) {
		Swal.fire({
			icon: "warning",
			title: "CEP inválido",
			text: "Informe um CEP válido com 8 números."
		});
		return;
	}

	try {
		// Consulta ViaCEP
		const viaCepRes = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
		const viaCepData = await viaCepRes.json();

		if (viaCepData.erro) {
			Swal.fire({
				icon: "error",
				title: "CEP não encontrado",
				text: "Não foi possível localizar o endereço para o CEP informado."
			});
			return;
		}

		// Preenche campos de texto
		document.getElementById("ruaComBoletim").value = viaCepData.logradouro || "";
		document.getElementById("bairroComBoletim").value = viaCepData.bairro || "";
		document.getElementById("complementoComBoletim").value = viaCepData.complemento || "";

		// Seleciona cidade usando a função separada
		const cidadeSelect = document.getElementById("idCidadeComBoletim");
		selecionarCidadePorNome(cidadeSelect, viaCepData.localidade);

		// Consulta Nominatim para latitude e longitude
		const estado = viaCepData.uf || "";
		const logradouro = viaCepData.logradouro || "";

		const nominatimRes = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(`${logradouro}, ${viaCepData.localidade}, ${estado}, Brasil`)}`);
		const nominatimData = await nominatimRes.json();

		if (nominatimData && nominatimData.length > 0) {
			const { lat, lon } = nominatimData[0];
			document.getElementById("latitudeComBoletim").value = lat;
			document.getElementById("longitudeComBoletim").value = lon;

			if (!isNaN(lat) && !isNaN(lon)) {
				atualizarMarkerComBoletim(parseFloat(lat), parseFloat(lon));
			}
		}

	} catch (error) {
		console.error("Erro ao buscar endereço por CEP:", error);
		Swal.fire({
			icon: "error",
			title: "Erro",
			text: "Não foi possível buscar o endereço. Tente novamente."
		});
	}
}

/**
 * Seleciona uma opção de <select> de cidade pelo nome,
 * ignorando acentos, cedilhas, espaços e maiúsculas/minúsculas.
 * @param {HTMLSelectElement} select - O select de cidades
 * @param {string} nomeCidade - Nome da cidade a selecionar
 */
function selecionarCidadePorNome(select, nomeCidade) {
	if (!select || !nomeCidade) return;

	const normalize = str => str
		.normalize("NFD")                  // separa acentos
		.replace(/[\u0300-\u036f]/g, "")  // remove acentos
		.replace(/\s+/g, "")               // remove espaços
		.toLowerCase();

	const nomeCidadeNormalized = normalize(nomeCidade);

	const option = Array.from(select.options).find(opt => normalize(opt.textContent) === nomeCidadeNormalized);

	if (option) {
		select.value = option.value;
	} else {
		Swal.fire({
			icon: "info",
			title: "Cidade não encontrada",
			text: `O CEP retornou a cidade "${nomeCidade}", mas ela não está disponível na lista.`
		});
	}
}

function bloquearCamposEnderecoComBoletim(modo) {
    exibirTabelaEnderecosComBoletim(modo);

    const camposParaBloquear = [
        "latitudeComBoletim",
        "longitudeComBoletim",
        "bairroComBoletim",
        "ruaComBoletim",
        "idTipoEventoComBoletim"
    ];

    camposParaBloquear.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            if (el.tagName === "SELECT") {
                el.disabled = true; // bloqueia o select
            } else {
                el.readOnly = true; // bloqueia input
            }
            el.classList.add("bg-light"); // opcional: deixa visualmente desabilitado
        }
    });
}

function exibirTabelaEnderecosComBoletim(modo) {
    const containerTabela = document.getElementById("containerTabelaEnderecosComBoletim");
    if (!containerTabela) return;

    if (modo === "cadastro") {
        containerTabela.style.display = "none";  // Esconde no cadastro
    } else if (modo === "edicao") {
        containerTabela.style.display = "block"; // Mostra na edição
    }
}
