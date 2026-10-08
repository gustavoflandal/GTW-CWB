window.documentos = window.documentos || [];
function atualizarNomeArquivo() {
	const input = document.getElementById("arquivoDocumento");
	const tipoInput = document.getElementById("tipoDocumento");
	const grupoTipo = document.getElementById("grupoTipoDocumento");

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
			grupoTipo.classList.remove("d-none"); // mostrar campo
		} else {
			alert("Extensão de arquivo inválida. Somente arquivos PDF, JPG ou PNG são permitidos.");
			input.value = ''; // limpa arquivo
			tipoInput.value = '';
			grupoTipo.classList.add("d-none"); // esconder campo
		}
	}
}

async function adicionarDocumento() {
	const arquivoInput = document.getElementById("arquivoDocumento");
	const tipoInput = document.getElementById("tipoDocumento");
	const grupoTipo = document.getElementById("grupoTipoDocumento");
	const detalhamentoInput = document.getElementById("detalhamentoDocumento");

	if (arquivoInput.files.length === 0) {
		alert("Por favor, selecione um arquivo.");
		return;
	}

	const arquivo = arquivoInput.files[0];

	// Verifica tamanho máximo 10 MB
	const tamanhoMaximoBytes = 10 * 1024 * 1024; // 10 MB
	if (arquivo.size > tamanhoMaximoBytes) {
		alert("Arquivo muito grande! O tamanho máximo permitido é 10 MB.");
		arquivoInput.value = ''; // limpa seleção do arquivo
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

	// Converter arquivo para base64
	let arquivoBase64 = "";
	try {
		arquivoBase64 = await arquivoParaBase64(arquivo);
	} catch (error) {
		alert("Erro ao ler o arquivo.");
		return;
	}

	tipoInput.value = tipo;
	tipoInput.required = true;
	grupoTipo.classList.remove("d-none");

	window.documentos.push({
		tipo,
		nome: nomeArquivo,
		detalhamento: detalhamentoInput.value.trim(),
		conteudoBase64: arquivoBase64
	});
	atualizarTabelaDocumentos();

	document.getElementById("formDocumentos").reset();
	grupoTipo.classList.add("d-none");
	tipoInput.required = false;
}

function removerDocumento(index) {
	window.documentos.splice(index, 1);
	atualizarTabelaDocumentos();
}

function atualizarTabelaDocumentos() {
	const tbody = document.getElementById("listaDocumentos");
	tbody.innerHTML = "";

	window.documentos.forEach((doc, index) => {
		const tr = document.createElement("tr");
		tr.innerHTML = `
		            <td>${index + 1}</td>
		            <td>${doc.tipo}</td>
		            <td class="text-start">${doc.nome}</td>
		            <td class="text-start">${doc.detalhamento}</td> <!-- novo campo -->
		            <td>
		                <button class="btn btn-sm btn-danger" title='Remover' onclick="removerDocumento(${index})"><i class="fa fa-trash"></i></button>
		            </td>
		        `;
		tbody.appendChild(tr);
	});
}

function getDocumentosBoletim() {
	return window.documentos;
}
function clearTabDocumentos() {
	window.documentos = [];
	const tbody = document.getElementById("listaDocumentos");
	tbody.innerHTML = "";
	document.getElementById("formDocumentos").reset();
}

function arquivoParaBase64(file) {
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

function limparCamposEListaDocumentos() {
	const form = document.getElementById('formDocumentos');
	if (!form) return;

	form.reset();

	const tipoDocumento = document.getElementById('tipoDocumento');
	if (tipoDocumento) tipoDocumento.value = '';

	const detalhamento = document.getElementById('detalhamentoDocumento');
	if (detalhamento) detalhamento.value = '';

	const arquivo = document.getElementById('arquivoDocumento');
	if (arquivo) arquivo.value = '';

	const tbody = document.getElementById('listaDocumentos');
	if (tbody) tbody.innerHTML = '';
}

function bloquearCamposDocumentos() {
	const form = document.getElementById('formDocumentos');
	if (!form) return;

	const elements = form.querySelectorAll('input, textarea, button');
	elements.forEach(el => el.disabled = true);
}

function desbloquearCamposDocumentos() {
	const form = document.getElementById('formDocumentos');
	if (!form) return;

	const elements = form.querySelectorAll('input, textarea, button');
	elements.forEach(el => el.disabled = false);
}