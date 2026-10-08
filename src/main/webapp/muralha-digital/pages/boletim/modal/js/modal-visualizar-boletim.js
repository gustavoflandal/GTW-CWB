async function buscarInformacaoBoletimId(id) {
	const url = "/MuralhaDigital/Boletim";
	const params = new URLSearchParams({ acao: "obterPorId", id });
	$("body").addClass("loading");

	try {
		const response = await $.ajax({
			type: "GET",
			url,
			data: params.toString(),
			dataType: "xml",
		});

		const $xml = $(response);
		const sucesso = $xml.find("sucesso").text();
		const msgResposta = $xml.find("msgResposta").text();

		const isSucesso = sucesso === "true" || sucesso === "";

		if (!isSucesso) {
			throw new Error(msgResposta || "Erro desconhecido na resposta XML.");
		}

		processaDadosVisualizar(new XMLSerializer().serializeToString(response));
	} catch (error) {
		ErrorNotificationBoletimModal(error.message || "Erro ao processar requisição ao servidor!", "");
		throw error;
	} finally {
		$("body").removeClass("loading");
	}
}

function processaDadosVisualizar(xmlString) {
	configuraModalVisualizar();
	const parser = new DOMParser();
	const xmlDoc = parser.parseFromString(xmlString, "application/xml");
	polularTabBoletim(xmlDoc);
	popularTabDocumento(xmlDoc);
	popularTabIndividuos(xmlDoc);
	popularTabVeiculos(xmlDoc);
	popularTabApreensoes(xmlDoc);
}

function polularTabBoletim(xmlDoc) {
	const boletim = xmlDoc.querySelector("Boletim");
	if (!boletim) return;

	const getText = (tag) => boletim.querySelector(tag)?.textContent?.trim() || "";

	document.querySelector("#idDetalhamentoBoletim").value = getText("detalhamento");
	// atualiza contador tab Boletim
	atualizarContador();
	document.querySelector("#idRuaBoletim").value = getText("local > rua");
	document.querySelector("#idNumeroBoletim").value = getText("local > numero");
	document.querySelector("#idBairroBoletim").value = getText("local > bairro");
	document.querySelector("#complementoBoletim").value = getText("local > complemento");

	document.querySelector("#selTipoBoletim").value = getText("idTipo");
	document.querySelector("#selSituacaoBoletim").value = getText("idSituacao");

	// ✅ Cidade usando id_cidade
	const idCidade = getText("local > id_cidade");
	const cidadeSelect = document.querySelector("#selCidadeBoletim");
	if (cidadeSelect) cidadeSelect.value = idCidade;

	// ✅ Checkbox permitir atendimento
	const permite = getText("permite_atendimento") === "1";
	document.querySelector("#permitirAtendimento").checked = permite;
}

function popularTabDocumento(xmlDoc) {
	const listaDocumentos = document.getElementById("listaDocumentos");
	listaDocumentos.innerHTML = ""; // limpa antes

	const boletins = xmlDoc.getElementsByTagName("Boletim");

	if (!boletins.length) return;

	const documentos = boletins[0].getElementsByTagName("documentos");

	Array.from(documentos).forEach((doc, index) => {
		const id = doc.getElementsByTagName("id")[0]?.textContent || "";
		const tipo = doc.getElementsByTagName("tipo")[0]?.textContent || "";
		const caminho = doc.getElementsByTagName("dirArquivo")[0]?.textContent || "";
		const detalhamento = doc.getElementsByTagName("detalhamento")[0]?.textContent || "";

		const nomeArquivo = caminho.split("\\").pop();

		const tr = document.createElement("tr");
		tr.innerHTML = `
			<td>${index + 1}</td>
			<td>${tipo}</td>
			<td title="${caminho}">${nomeArquivo}</td>
			<td>${detalhamento}</td>
			<td>
				<button class="btn btn-sm btn-danger" disabled>
					<i class="fa fa-trash"></i>
				</button>
				<button class="btn btn-sm btn-primary align-items-center gap-1" title='Baixar' onclick="baixarDocumentoPorId(${id})"><i class="fas fa-download"></i></button>
			</td>
		`;

		listaDocumentos.appendChild(tr);
	});
}

function popularTabIndividuos(xmlDoc) {
	const lista = document.getElementById("listIndividuos");
	lista.innerHTML = ""; // limpa antes

	const boletins = xmlDoc.getElementsByTagName("Boletim");
	if (!boletins.length) return;

	const envolvidos = boletins[0].getElementsByTagName("individuos");

	Array.from(envolvidos).forEach((ind) => {
		const nome = ind.getElementsByTagName("nome")[0]?.textContent || "";
		const cpf = ind.getElementsByTagName("cpf")[0]?.textContent || "";
		const detalhe = ind.getElementsByTagName("detalheEnvolvimento")[0]?.textContent || "";
		const tipo = ind.getElementsByTagName("tipoEnvolvimento")[0]?.getElementsByTagName("descricao")[0]?.textContent || "";

		const tr = document.createElement("tr");
		tr.innerHTML = `
			<td>${tipo}</td>
			<td>${nome}</td>
			<td>${formatarCPF(cpf)}</td>
			<td>${detalhe}</td>
			<td>
				<button class="btn btn-sm btn-danger" disabled>
					<i class="fa fa-trash"></i>
				</button>
			</td>
		`;
		lista.appendChild(tr);
	});
}

function popularTabVeiculos(xmlDoc) {
	const lista = document.getElementById("listVeiculos");
	lista.innerHTML = ""; // limpa antes

	const boletins = xmlDoc.getElementsByTagName("Boletim");
	if (!boletins.length) return;

	const veiculos = boletins[0].getElementsByTagName("veiculos");

	Array.from(veiculos).forEach((v) => {
		const placa = v.getElementsByTagName("placa")[0]?.textContent || "";
		const cor = v.getElementsByTagName("cor")[0]?.textContent || "";
		const marca = v.getElementsByTagName("marca")[0]?.textContent || "";
		const modelo = v.getElementsByTagName("modelo")[0]?.textContent || "";

		const tr = document.createElement("tr");
		tr.innerHTML = `
			<td>${placa}</td>
			<td>${cor}</td>
			<td>${marca}</td>
			<td>${modelo}</td>
			<td class="text-center">
				<button class="btn btn-sm btn-danger" disabled>
					<i class="fa fa-trash"></i>
				</button>
			</td>
		`;
		lista.appendChild(tr);
	});
}

function popularTabApreensoes(xmlDoc) {
	const lista = document.getElementById("listApreensoes");
	lista.innerHTML = ""; // limpa antes

	const boletins = xmlDoc.getElementsByTagName("Boletim");
	if (!boletins.length) return;

	const apreensoes = boletins[0].getElementsByTagName("apreensoes");

	Array.from(apreensoes).forEach((a) => {
		const tipo = a.getElementsByTagName("tipo")[0]?.textContent || "";
		const descricao = a.getElementsByTagName("descricao")[0]?.textContent || "";

		const tr = document.createElement("tr");
		tr.innerHTML = `
			<td>${tipo}</td>
			<td>${descricao}</td>
			<td class="text-center">
				<button class="btn btn-sm btn-danger" disabled>
					<i class="fa fa-trash"></i>
				</button>
			</td>
		`;
		lista.appendChild(tr);
	});
}

function formatarCPF(cpf) {
	const numeros = cpf.replace(/\D/g, "").padStart(11, "0").slice(0, 11);

	if (numeros.length !== 11) return cpf;

	return numeros.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
}


function configuraModalVisualizar() {
	document.getElementById("idBoletimModalSalvar").disabled = true;
	// Chamada de cada arquivo .js das tab
	bloquearCamposBoletim();
	bloquearCamposDocumentos();
	bloquearCamposDocumentos();
	bloquearCamposIndividuos();
	bloquearCamposVeiculos();
	bloquearCamposApreensoes();
}