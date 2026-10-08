window.documentosComBoletim = window.documentosComBoletim || [];

function atualizarNomeArquivoComBoletim() {
	const input = document.getElementById("arquivoDocumentoComBoletim");
	const tipoInput = document.getElementById("tipoDocumentoComBoletim");
	const grupoTipo = document.getElementById("grupoTipoDocumentoComBoletim");

	if (input.files.length > 0) {
		const nomeArquivo = input.files[0].name.toLowerCase();
		const extensao = nomeArquivo.split('.').pop();

		const mapaExtensaoParaTipo = {
			'pdf': 'PDF',
			'jpg': 'Imagem JPG',
			'jpeg': 'Imagem JPG',
			'png': 'Imagem PNG'
		};

		if (mapaExtensaoParaTipo[extensao]) {
			tipoInput.value = mapaExtensaoParaTipo[extensao];
			grupoTipo.classList.remove("d-none");
		} else {
			alert("Extensão de arquivo inválida. Somente arquivos PDF, JPG ou PNG são permitidos.");
			input.value = '';
			tipoInput.value = '';
			grupoTipo.classList.add("d-none");
		}
	}
}

async function adicionarDocumentoComBoletim() {
	const arquivoInput = document.getElementById("arquivoDocumentoComBoletim");
	const tipoInput = document.getElementById("tipoDocumentoComBoletim");
	const grupoTipo = document.getElementById("grupoTipoDocumentoComBoletim");
	const detalhamentoInput = document.getElementById("detalhamentoDocumentoComBoletim");

	if (arquivoInput.files.length === 0) {
		alert("Por favor, selecione um arquivo.");
		return;
	}

	const arquivo = arquivoInput.files[0];
	const tamanhoMaximoBytes = 10 * 1024 * 1024;

	if (arquivo.size > tamanhoMaximoBytes) {
		alert("Arquivo muito grande! O tamanho máximo permitido é 10 MB.");
		arquivoInput.value = '';
		return;
	}

	const nomeArquivo = arquivo.name;
	const extensao = nomeArquivo.split('.').pop().toLowerCase();

	const mapaExtensaoParaTipo = {
		pdf: 'PDF',
		jpg: 'Imagem JPG',
		jpeg: 'Imagem JPG',
		png: 'Imagem PNG'
	};

	const tipo = mapaExtensaoParaTipo[extensao];

	if (!tipo) {
		alert("Extensão de arquivo inválida. Somente PDF, JPG ou PNG são permitidos.");
		arquivoInput.value = '';
		return;
	}

	if (!detalhamentoInput.value.trim()) {
		alert("Por favor, preencha o detalhamento.");
		return;
	}

	let arquivoBase64 = "";
	try {
		arquivoBase64 = await arquivoParaBase64ComBoletim(arquivo);
	} catch (error) {
		alert("Erro ao ler o arquivo.");
		return;
	}

	tipoInput.value = tipo;
	tipoInput.required = true;
	grupoTipo.classList.remove("d-none");

	window.documentosComBoletim.push({
		tipo,
		nome: nomeArquivo,
		detalhamento: detalhamentoInput.value.trim(),
		conteudoBase64: arquivoBase64,
		status: "novo", // indica que é um documento recém-adicionado
		id: null
	});

	atualizarTabelaDocumentosComBoletim();

	document.getElementById("formDocumentosComBoletim").reset();
	grupoTipo.classList.add("d-none");
	tipoInput.required = false;
}

function removerDocumentoComBoletim(index) {
	const doc = window.documentosComBoletim[index];
	if (doc.id) {
		doc.status = "removido"; // só marca removido
	} else {
		// remove da lista se nunca foi salvo
		window.documentosComBoletim.splice(index, 1);
	}
	atualizarTabelaDocumentosComBoletim();
}

function atualizarTabelaDocumentosComBoletim() {
    const tbody = document.getElementById("listaDocumentosComBoletim");
    tbody.innerHTML = "";

    window.documentosComBoletim.forEach((doc, index) => {
        let nomeDisplay = doc.nome;
        let detalhamentoDisplay = doc.detalhamento || "-";

        if (doc.status === "removido") {
            nomeDisplay = `<del>${nomeDisplay}</del>`;
            detalhamentoDisplay = `<del>${detalhamentoDisplay}</del>`;
        } else if (doc.status === "novo") {
            nomeDisplay += ' <span class="badge bg-success">Novo</span>';
        }

        const btnRemover = doc.status === "removido"
            ? ""
            : `<button class="btn btn-sm btn-danger" title="Remover" onclick="removerDocumentoComBoletim(${index})">
                   <i class="fa fa-trash"></i>
               </button>`;

        const btnBaixar = (doc.id && doc.status !== "removido")
            ? `<button class="btn btn-sm btn-primary ms-1" title="Baixar" onclick="baixarDocumentoPorId(${doc.id})">
                   <i class="fa fa-download"></i>
               </button>`
            : "";

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${doc.tipo}</td>
            <td class="text-start">${nomeDisplay}</td>
            <td class="text-start">${detalhamentoDisplay}</td>
            <td class="text-center">${btnRemover}${btnBaixar}</td>
        `;

        tbody.appendChild(tr);
    });
}

function getDocumentosComBoletim() {
	return window.documentosComBoletim;
}

function clearTabDocumentosComBoletim() {
	window.documentosComBoletim = [];
	const tbody = document.getElementById("listaDocumentosComBoletim");
	tbody.innerHTML = "";
	document.getElementById("formDocumentosComBoletim").reset();
}

function arquivoParaBase64ComBoletim(file) {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.onload = () => {
			const binary = new Uint8Array(reader.result);
			const base64 = btoa([...binary].map(b => String.fromCharCode(b)).join(""));
			resolve(base64);
		};
		reader.onerror = reject;
		reader.readAsArrayBuffer(file);
	});
}

function limparCamposDocumentosComBoletim() {
    const form = document.getElementById('formDocumentosComBoletim');
    if (form) form.reset();

    const tipoDocumento = document.getElementById('tipoDocumentoComBoletim');
    if (tipoDocumento) tipoDocumento.value = '';

    const detalhamento = document.getElementById('detalhamentoDocumentoComBoletim');
    if (detalhamento) detalhamento.value = '';

    const arquivo = document.getElementById('arquivoDocumentoComBoletim');
    if (arquivo) arquivo.value = '';

    const tbody = document.getElementById('listaDocumentosComBoletim');
    if (tbody) tbody.innerHTML = '';

    window.documentosComBoletim = [];
}

function bloquearCamposDocumentosComBoletim() {
	const form = document.getElementById('formDocumentosComBoletim');
	if (!form) return;

	const elements = form.querySelectorAll('input, textarea, button');
	elements.forEach(el => el.disabled = true);
}

function desbloquearCamposDocumentosComBoletim() {
	const form = document.getElementById('formDocumentosComBoletim');
	if (!form) return;

	const elements = form.querySelectorAll('input, textarea, button');
	elements.forEach(el => el.disabled = false);
}

function preencherTabDocumentosComBoletim(xmlString) {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlString, "application/xml");

    window.documentosComBoletim = [];

    const documentos = xmlDoc.querySelectorAll("RegistroDeFato > documentos > documento");

    documentos.forEach(docNode => {
        const id = parseInt(docNode.querySelector("id")?.textContent?.trim()) || null;
        const tipo = docNode.querySelector("tipo")?.textContent?.trim() || "";
        const caminhoArquivo = docNode.querySelector("dirArquivo")?.textContent?.trim() || "";
        const nome = extrairNomeArquivo(caminhoArquivo);
        const detalhamento = docNode.querySelector("detalhamento")?.textContent?.trim() || "";

		window.documentosComBoletim.push({
			id,
			tipo,
			nome,
			detalhamento,
			conteudoBase64: null,
			status: "existente"
		});
    });

    atualizarTabelaDocumentosComBoletim();
}

function extrairNomeArquivo(caminhoCompleto) {
  if (!caminhoCompleto) return "";
  const partes = caminhoCompleto.split(/[/\\]/); // separa tanto barras normais quanto invertidas
  return partes[partes.length - 1];
}

