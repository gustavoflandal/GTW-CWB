// Array para armazenar as apreensões temporariamente
window.apreensoesComBoletim = window.apreensoesComBoletim || [];
window.idBoletimComBoletim = null;
// Função para adicionar apreensão na lista
function adicionarApreensaoComBoletim() {
	const tipoInput = document.getElementById("tipoApreensaoComBoletim");
	const descricaoInput = document.getElementById("descricaoApreensaoComBoletim");

	const tipo = tipoInput.value.trim();
	const descricao = descricaoInput.value.trim();

	if (!tipo) {
		alert("Por favor, informe o tipo da apreensão.");
		return;
	}
	if (!descricao) {
		alert("Por favor, informe a descrição da apreensão.");
		return;
	}

	// Adiciona no array como novo
	window.apreensoesComBoletim.push({
		id: null,
		tipo,
		descricao,
		status: "novo"
	});

	atualizarTabelaApreensoesComBoletim();

	// Limpar campos
	tipoInput.value = "";
	descricaoInput.value = "";
}

// Função para remover apreensão pelo índice
function removerApreensaoComBoletim(index) {
	const apreensao = window.apreensoesComBoletim[index];
	if (apreensao.status === "novo") {
		// Remove diretamente, pois ainda não existe no backend
		window.apreensoesComBoletim.splice(index, 1);
	} else {
		// Marca como removido para envio ao backend
		apreensao.status = "removido";
	}
	atualizarTabelaApreensoesComBoletim();
}

// Atualiza a tabela de apreensões
function atualizarTabelaApreensoesComBoletim() {
	const tbody = document.getElementById("listApreensoesComBoletim");
	tbody.innerHTML = "";

	window.apreensoesComBoletim.forEach((apreensao, index) => {
		const tr = document.createElement("tr");

		let tipoTexto = apreensao.tipo;
		let descricaoTexto = apreensao.descricao;

		// Adiciona badge visual
		if (apreensao.status === "novo") {
			tipoTexto += ' <span class="badge bg-success">Novo</span>';
		} else if (apreensao.status === "removido") {
			tipoTexto = `<del>${tipoTexto}</del>`;
			descricaoTexto = `<del>${descricaoTexto}</del>`;
		}

		// Botão de remoção apenas altera status para "removido"
		const btnRemover = apreensao.status === "removido"
			? ''
			: `<button class="btn btn-sm btn-danger" title="Remover" onclick="removerApreensaoComBoletim(${index})">
                   <i class="fa fa-trash"></i>
               </button>`;

		tr.innerHTML = `
            <td>${tipoTexto}</td>
            <td>${descricaoTexto}</td>
            <td class="text-center">${btnRemover}</td>
        `;

		tbody.appendChild(tr);
	});
}

// Retorna os dados do boletim (situação única)
function getDadosBoletim(modo) {
			
    // Se temBoletim não estiver marcado, descarta boletim
    if (!window.temBoletimMarcado) {
        return null;
    }
    
	if(TEM_BOLETIM === 0){
		return;
	}

    const situacaoSelect = document.getElementById("idSituacaoComBoletim");
    const detalhamentoTextarea = document.getElementById("detalhamentoSituacaoComBoletim");
    const permiteCheckbox = false;

    let valido = true;

    if (modo === "cadastro") {
        const camposObrigatorios = [situacaoSelect, detalhamentoTextarea];
        camposObrigatorios.forEach(el => el.classList.remove("is-invalid"));

        camposObrigatorios.forEach(el => {
            if (!el.value.trim() || el.value.trim() === "0") {
                el.classList.add("is-invalid");
                valido = false;
            }
        });

        if (!valido) {
            Swal.fire({
                icon: "error",
                title: "Campos obrigatórios",
                text: "Por favor, preencha os campos obrigatórios."
            });
            return null;
        }

        return {
            idSituacao: parseInt(situacaoSelect.value, 10),
            detalhamento: detalhamentoTextarea.value.trim(),
            permiteAtendimento: permiteCheckbox.checked ? 1 : 0,
            apreensoes: window.apreensoesComBoletim
        };

    } else if (modo === "edicao") {
        if (!situacaoSelect.value.trim() || situacaoSelect.value.trim() === "0") {
            situacaoSelect.classList.add("is-invalid");
            Swal.fire({
                icon: "error",
                title: "Situação obrigatória",
                text: "Por favor, selecione a situação."
            });
            return null;
        }

        return {
            id: window.idBoletimComBoletim,
            idSituacao: parseInt(situacaoSelect.value, 10),
            permiteAtendimento: permiteCheckbox.checked ? 1 : 0,
			detalhamento: detalhamentoTextarea.value.trim(),
            apreensoes: window.apreensoesComBoletim
                .filter(a => a.status === "novo" || a.status === "removido")
                .map(a => ({
                    id: a.id,
                    tipo: a.tipo,
                    descricao: a.descricao,
                    status: a.status
                }))
        };
    }

    return null;
}

// Retorna array de apreensões adicionadas
function getApreensoesBoletim() {
	return window.apreensoesComBoletim;
}

// Limpa formulário do boletim e tabela de apreensões
function limparCamposBoletim() {
    // Reseta os formulários
    document.getElementById("formSituacaoComBoletim").reset();
    document.getElementById("formApreensaoComBoletim").reset();

    // Remove classes de erro manualmente
    const invalidElements = document.querySelectorAll("#formSituacaoComBoletim .is-invalid, #formApreensaoComBoletim .is-invalid");
    invalidElements.forEach(el => el.classList.remove("is-invalid"));

    // Limpa lista de apreensões e tabela
    window.apreensoesComBoletim = [];
    atualizarTabelaApreensoesComBoletim();
}

/**
 * Bloqueia os campos Situação e Detalhamento
 */
function bloquearCamposComBoletim() {
	document.getElementById("detalhamentoSituacaoComBoletim").disabled = true;
}

/**
 * Desbloqueia os campos Situação e Detalhamento
 */
function habilitarCamposComBoletim() {
	const situacao = document.getElementById("idSituacaoComBoletim");
	const detalhamento = document.getElementById("detalhamentoSituacaoComBoletim");
	if (situacao) situacao.removeAttribute("disabled");
	if (detalhamento) detalhamento.removeAttribute("disabled");
}

function preencherTabBoletimComBoletim(xmlString) {
    const parser = new DOMParser();
    const xml = parser.parseFromString(xmlString, "text/xml");
    
    const temBoletim = xml.querySelector("temBoletim");
    
    if(temBoletim === 0){
		return;
	}

    const boletim = xml.querySelector("boletins > boletim");

    if (!boletim) {
        console.info("Nenhum boletim encontrado → modo CADASTRO");
        window.idBoletimComBoletim = null;
        limparCamposBoletim();
        prepararModalSemBoletim();
		habilitarCamposComBoletim();
        return;
    }else{
		prepararModalComBoletim();	
	}    

    // Existe boletim → modo EDIÇÃO
    let idBoletim = boletim.querySelector(":scope > id")?.textContent.trim();
    window.idBoletimComBoletim = idBoletim ? parseInt(idBoletim, 10) : null;

    // Situação
    const situacao = boletim.querySelector("idSituacao")?.textContent.trim() || "0";
    const situacaoSelect = document.getElementById("idSituacaoComBoletim");
    if (situacaoSelect) situacaoSelect.value = situacao;

    // Detalhamento
    const detalhamento = boletim.querySelector("detalhamento")?.textContent.trim() || "";
    document.getElementById("detalhamentoSituacaoComBoletim").value = detalhamento;

    // Limpa lista de apreensões
    window.apreensoesComBoletim = [];

    // Carrega apreensões
    boletim.querySelectorAll("apreensoes").forEach(apreensaoNode => {
        const id = parseInt(apreensaoNode.querySelector("id")?.textContent.trim(), 10) || null;
        const tipo = apreensaoNode.querySelector("tipo")?.textContent.trim() || "";
        const descricao = apreensaoNode.querySelector("descricao")?.textContent.trim() || "";

        window.apreensoesComBoletim.push({
            id,
            tipo,
            descricao,
            status: "existente"
        });
    });

    atualizarTabelaApreensoesComBoletim();
}

function prepararModalSemBoletim() {
	
	const checkboxTemBoletim = document.getElementById("tem_boletimComBoletim");

    if (checkboxTemBoletim) {
        // Não existe boletim → desmarca e bloqueia
        checkboxTemBoletim.checked = false;
        checkboxTemBoletim.disabled = true;
    }
    
    window.temBoletimMarcado = false;
	
	let abaBoletim = document.getElementById('boletim-comBoletim-tab');
	abaBoletim.style.display = "none";
	
	let abaDocumentos = document.getElementById('documento-comBoletim-tab');
	abaDocumentos.style.display = "none";
	
	let abaLinks = document.getElementById('link-comBoletim-tab');
	abaLinks.style.display = "none";
}

function prepararModalComBoletim() {

	let abaBoletim = document.getElementById('boletim-comBoletim-tab');
	abaBoletim.style.display = "block";
	
	let abaDocumentos = document.getElementById('documento-comBoletim-tab');
	abaDocumentos.style.display = "block";
	
	let abaLinks = document.getElementById('link-comBoletim-tab');
	abaLinks.style.display = "block";
}