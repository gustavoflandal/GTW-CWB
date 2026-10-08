window.idRegistroFatoComBoletim = null;
window.temBoletimMarcado = true;
window.anotacoes = window.anotacoes || [];

document.addEventListener("DOMContentLoaded", function() {
	const dataEventoInput = document.getElementById('dataEvento');
	const agora = new Date();
	agora.setMinutes(agora.getMinutes() - agora.getTimezoneOffset());
	dataEventoInput.max = agora.toISOString().slice(0, 16);
});

function alternarVisibilidadeCamposFato(modo) {
	const containerDetalhamento = document.getElementById('containerDetalhamentoFato');
	const containerArma = document.getElementById('containerEnvolvimentoArma');

	if (containerDetalhamento && containerArma) {
		if (modo === "cadastro" && window.temBoletimMarcado) {
			containerDetalhamento.classList.add('d-none');
			containerArma.classList.add('d-none');
		} else {
			containerDetalhamento.classList.remove('d-none');
			containerArma.classList.remove('d-none');
		}
	}
}

function getRegistroFatoComBoletim(modo) {
	const tipo = document.getElementById("id_tipoComBoletim");
	const status = document.getElementById("id_statusComBoletim");
	const naturezaTipo = document.getElementById("id_naturezaTipo");
	const privado = document.getElementById("privadoComBoletim");
	const temBoletimCheckbox = document.getElementById("tem_boletimComBoletim");
	const permitirAtendimento = document.getElementById("permitirAtendimento");
	const dataEvento = document.getElementById("dataEvento");
	const detalhamento = document.getElementById("detalhamentoFato");
	const envolvimentoArma = document.getElementById("envolvimentoArma");

	let valido = true;

	// Ajusta segundos para o padrão do backend
	let dataEventoValue = dataEvento.value;
	if (dataEventoValue && dataEventoValue.length === 16) {
		dataEventoValue += ':00';
	}

	// --------------------------
	//  Cadastro
	// --------------------------
	if (modo === "cadastro") {

		const camposObrigatorios = [tipo, naturezaTipo, status, dataEvento];
		camposObrigatorios.forEach(e => e.classList.remove("is-invalid"));

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
				text: "Por favor, preencha todos os campos obrigatórios na aba Registro de Fato."
			});
			return null;
		}

		// coleta anotações (retorna array com status)
		const anotacoes = coletarAnotacoesParaPayload(window.idRegistroFatoComBoletim || null);

		return {
			idTipo: parseInt(tipo.value),
			idStatus: parseInt(status.value),
			idNaturezaTipo: parseInt(naturezaTipo.value),
			temBoletim: temBoletimCheckbox.checked ? 1 : 0,
			privado: privado.checked ? 1 : 0,
			dataEvento: dataEventoValue,
			permitirAtendimento: permitirAtendimento.checked ? 1 : 0,
			detalhamento: detalhamento.value,
			envolvimentoArma: envolvimentoArma.checked ? 1 : 0,
			anotacoes: anotacoes
		};
	}

	// --------------------------
	//  Edição
	// --------------------------
	if (modo === "edicao") {

		const camposObrigatoriosEdicao = [naturezaTipo, status, dataEvento];
		let validoEdicao = true;

		camposObrigatoriosEdicao.forEach(el => el.classList.remove("is-invalid"));
		camposObrigatoriosEdicao.forEach(el => {
			if (!el.value.trim() || el.value.trim() === "0") {
				el.classList.add("is-invalid");
				validoEdicao = false;
			}
		});

		if (!validoEdicao) {
			Swal.fire({
				icon: "error",
				title: "Campos obrigatórios",
				text: "Por favor, preencha a situação e a data do evento."
			});
			return null;
		}

		// coleta anotações vinculadas ao registro em edição
		const anotacoes = coletarAnotacoesParaPayload(window.idRegistroFatoComBoletim);

		return {
			id: window.idRegistroFatoComBoletim,
			idTipo: parseInt(tipo.value),
			idNaturezaTipo: parseInt(naturezaTipo.value),
			idStatus: parseInt(status.value),
			temBoletim: temBoletimCheckbox.checked ? 1 : 0,
			privado: privado.checked ? 1 : 0,
			dataEvento: dataEventoValue,
			permitirAtendimento: permitirAtendimento.checked ? 1 : 0,
			detalhamento: detalhamento.value,
			envolvimentoArma: envolvimentoArma.checked ? 1 : 0,
			anotacoes: anotacoes
		};
	}

	return null;
}

function normalizarStatus(a) {
    if (!a || typeof a !== "object") return null;

    if (typeof a.status === "string" && a.status.trim() !== "") {
        return a.status.trim().toLowerCase();
    }

    const idNum = Number(a.id);
    if (!isNaN(idNum) && idNum > 0) return "existente";

    return "novo";
}

function coletarAnotacoesParaPayload() {
    const result = [];

    if (!Array.isArray(window.anotacoes)) return result;

    window.anotacoes.forEach(a => {
        if (!a || typeof a !== "object") return;

        const status = normalizarStatus(a);

        // ID sempre numérico ou zero
        let idNum = 0;
        if (typeof a.id === "number") idNum = a.id;
        else if (typeof a.id === "string" && a.id.trim() !== "") {
            const parsed = Number(a.id);
            idNum = !isNaN(parsed) ? parsed : 0;
        }

        // dataIso → usa dataCriacao se existir
        const dataIso = (a.dataCriacao && typeof a.dataCriacao === "string")
            ? a.dataCriacao.trim()
            : null;

        // nomeUsuario → usuario
        const usuario = a.nomeUsuario?.trim?.() || a.usuario?.trim?.() || null;

        result.push({
            id: idNum,
            texto: a.texto ?? "",
            usuario: usuario,
            dataIso: dataIso,
            status: status // "novo" | "existente" | "removido"
        });
    });

    return result;
}

function limparCamposRegistroFatoComBoletim() {
	const form = document.getElementById('formRegistroFatoComBoletim');
	if (!form) return;

	// Limpa selects para a opção padrão
	const selects = form.querySelectorAll('select');
	selects.forEach(select => select.value = '0');

	// Reseta especificamente o campo de Natureza para o estado inicial
	const naturezaSelect = document.getElementById('id_naturezaTipo');
	if (naturezaSelect) {
		naturezaSelect.innerHTML = '<option value="0">Selecione o Tipo primeiro</option>';
		naturezaSelect.disabled = true;
	}

	// Limpa inputs de texto, data, número, etc.
	const outrosInputs = form.querySelectorAll('input:not([type=checkbox]):not([type=radio]), textarea');
	outrosInputs.forEach(input => {
		input.value = ''; // Define como vazio, que é o correto para esses campos
	});

	// Limpa checkboxes (exceto o de boletim que é fixo)
	const checkboxes = form.querySelectorAll('input[type="checkbox"]:not(#tem_boletimComBoletim)');
	checkboxes.forEach(cb => cb.checked = false);

	// Remove classes de validação de todos os campos
	form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));

	// Garante que o checkbox de "tem boletim" continue marcado e desabilitado
	const checkboxBoletim = document.getElementById('tem_boletimComBoletim');
	if (checkboxBoletim) {
		checkboxBoletim.checked = true;
		checkboxBoletim.disabled = true;
	}
	
	window.anotacoes || [];
}

function preencherTabRegistroDeFatoComBoletim(xmlString) {
	const parser = new DOMParser();
	const xml = parser.parseFromString(xmlString, "text/xml");
	const registro = xml.querySelector("RegistroDeFato");

	if (!registro) return;

	// Pega ID corretamente
	const id = parseInt(registro.querySelector("id")?.textContent.trim(), 10) || null;
	window.idRegistroFatoComBoletim = id;

	const idTipo = registro.querySelector("idTipo")?.textContent.trim() || "0";
	const idNaturezaTipo = registro.querySelector("idNaturezaTipo")?.textContent.trim();
	const privado = registro.querySelector("privado")?.textContent.trim() === "1";
	// Conforme sua nova instrução, idStatus e dataEvento agora são sempre do 'RegistroDeFato'.
	const idStatus = registro.querySelector("idStatus")?.textContent.trim() || "0";
	const dataEventoStr = registro.querySelector("dataEvento")?.textContent.trim();
	let permitirAtendimento = false;
	let detalhamento = "";
	let envolvimentoArma = false;
	const fato = registro.querySelector("fatos");
	const boletim = registro.querySelector("boletins boletim");
	
	preencherTabAnotacoes(xmlString);

	if (fato) {
		permitirAtendimento = fato.querySelector("permiteAtendimento")?.textContent.trim() === "1";
		detalhamento = fato.querySelector("detalhamento")?.textContent.trim() || "";
		envolvimentoArma = fato.querySelector("existeArmaEnvolvida")?.textContent.trim() === "1";
	} else if (boletim) {
		permitirAtendimento = boletim.querySelector("permiteAtendimento")?.textContent.trim() === "1";
	}

	const dataEventoInput = document.getElementById("dataEvento");
	if (dataEventoInput) { // NOVO
		dataEventoInput.value = formatarParaInputDateTimeLocal(dataEventoStr);
	}

	if (idStatus === '2') {
		modoVisualizarTabRegistroDeFato();
	}

	// Preencher campos do formulário
	const tipoSelect = document.getElementById("id_tipoComBoletim");
	if (tipoSelect) {
		tipoSelect.value = idTipo;
		if (idTipo && idTipo !== '0') {
			obterNaturezasPorTipo(idTipo, idNaturezaTipo, true);
		}
	}

	const status = document.getElementById("id_statusComBoletim");
	if (status) status.value = idStatus;

	const checkboxPrivado = document.getElementById("privadoComBoletim");
	if (checkboxPrivado) checkboxPrivado.checked = privado;

	const checkboxPermitirAtendimento = document.getElementById("permitirAtendimento");
	if (checkboxPermitirAtendimento) checkboxPermitirAtendimento.checked = permitirAtendimento;

	const textareaDetalhamento = document.getElementById("detalhamentoFato");
	if (textareaDetalhamento) textareaDetalhamento.value = detalhamento;

	const checkboxEnvolvimentoArma = document.getElementById("envolvimentoArma");
	if (checkboxEnvolvimentoArma) checkboxEnvolvimentoArma.checked = envolvimentoArma;

	// Verifica se existem boletins
	const boletins = registro.querySelectorAll("boletins boletim");
	const checkboxTemBoletim = document.getElementById("tem_boletimComBoletim");
	const temBoletim = parseInt(registro.querySelector("temBoletim").textContent);
	document.getElementById('detalhamentoFato').disabled = temBoletim;
	document.getElementById('envolvimentoArma').disabled = temBoletim;
	if (checkboxTemBoletim) checkboxTemBoletim.checked = temBoletim;

	if (temBoletim == false) {
		prepararModalSemBoletim();
	}

	// 1. Extrai as datas do XML como texto
	const dataCriacaoStr = registro.querySelector("dataCriacao")?.textContent.trim();
	const dataEncerramentoStr = registro.querySelector("dataEncerramento")?.textContent.trim();

	// 2. Preenche os campos de Data de Criação e Encerramento
	document.getElementById("infoDataCriacao").textContent = formatarData(dataCriacaoStr);
	document.getElementById("infoDataEncerramento").textContent = formatarData(dataEncerramentoStr);

	// 3. Calcula e preenche o Prazo para Encerramento
	const prazoElement = document.getElementById("infoPrazoEncerramento");
	if (dataEncerramentoStr && dataCriacaoStr) {
		// Se ambas as datas existem, calcula a diferença.
		const dataInicio = new Date(dataCriacaoStr);
		const dataFim = new Date(dataEncerramentoStr);

		// Calcula a diferença em milissegundos
		const diffEmMs = dataFim.getTime() - dataInicio.getTime();

		// Converte milissegundos para horas (1 hora = 3600000 ms)
		const diffEmHoras = Math.round(diffEmMs / 3600000);

		// Define o texto com o total de horas
		if (diffEmHoras < 1) {
			prazoElement.textContent = "Encerrado em menos de uma hora";
		} else if (diffEmHoras === 1) {
			prazoElement.textContent = "Encerrado em 1 hora"; // Trata o singular
		} else {
			prazoElement.textContent = `Encerrado em ${diffEmHoras} horas`; // Trata o plural
		}

		prazoElement.classList.remove('text-danger');
		prazoElement.classList.add('text-success');

	} else if (dataEncerramentoStr) {
		// Fallback: se tiver data de encerramento mas não a de criação, mostra o texto antigo
		prazoElement.textContent = "Encerrado";
		prazoElement.classList.remove('text-danger');
		prazoElement.classList.add('text-success');

	} else if (dataCriacaoStr) {
		// Se não tem data de encerramento, calcula o prazo de 7 dias
		const dataCriacao = new Date(dataCriacaoStr);
		dataCriacao.setDate(dataCriacao.getDate() + 7);
		prazoElement.textContent = `Até ${formatarData(dataCriacao.toISOString())}`;
		prazoElement.classList.add('text-danger');
		prazoElement.classList.remove('text-success');

	} else {
		// Caso não tenha nem data de criação
		prazoElement.textContent = "Não aplicável";
	}
	checkboxTemBoletim.addEventListener("change", function() {
		window.temBoletimMarcado = this.checked;
	});
}

function bloquearCamposRegistroDeFatoComBoletim() {
	document
		.querySelectorAll('#formRegistroFatoComBoletim input, #formRegistroFatoComBoletim select, #formRegistroFatoComBoletim textarea, #formRegistroFatoComBoletim button')
		.forEach(el => el.disabled = true);

	// Reabilita apenas o campo de status
	document.getElementById('id_statusComBoletim').disabled = false;
	document.getElementById('dataEvento').disabled = false;
	document.getElementById('privadoComBoletim').disabled = false;
	document.getElementById('permitirAtendimento').disabled = false;
	document.getElementById('envolvimentoArma').disabled = false;
	document.getElementById('detalhamentoFato').disabled = false;
	document.getElementById('btnAddAnotacao').disabled = false;
	document.getElementById('txtAnotacao').disabled = false;
}

function habilitarCamposRegistroDeFatoComBoletim() {
	document
		.querySelectorAll('#formRegistroFatoComBoletim input, #formRegistroFatoComBoletim select, #formRegistroFatoComBoletim textarea, #formRegistroFatoComBoletim button')
		.forEach(el => el.disabled = false);

	// Mantém o "Possui Boletim" sempre bloqueado
	document.getElementById('tem_boletimComBoletim').disabled = true;
	document.getElementById('privadoComBoletim').disabled = false;
	document.getElementById('permitirAtendimento').disabled = false;
}

function gerenciarVisibilidadeDatasTabRegistro(modo) {
	const container = document.getElementById('containerInfoDatas');
	if (!container) return;

	if (modo === 'cadastro') {
		// Adiciona a classe d-none para ocultar
		container.classList.add('d-none');
	} else {
		// Remove a classe d-none para mostrar
		container.classList.remove('d-none');
	}
}

/**
 * Formata uma data (string ISO) para o padrão de exibição DD/MM/AAAA.
 * Usada nos campos de visualização.
 */
function formatarData(isoString) {
	if (!isoString || isoString.trim() === "") {
		return "Não informado";
	}
	const data = new Date(isoString);
	if (isNaN(data.getTime())) {
		return "Data inválida";
	}
	const dia = String(data.getDate()).padStart(2, '0');
	const mes = String(data.getMonth() + 1).padStart(2, '0'); // Mês é base 0, então +1
	const ano = data.getFullYear();
	return `${dia}/${mes}/${ano}`;
}

/**
 * Formata uma data para o padrão AAAA-MM-DDTHH:mm exigido pelo input [type=datetime-local].
 * Usada no campo de edição "Data e Hora do Evento".
 */
function formatarParaInputDateTimeLocal(dateInput) {
	if (!dateInput) return "";

	const data = (typeof dateInput === 'string') ? new Date(dateInput) : dateInput;
	if (isNaN(data.getTime())) return "";

	const ano = data.getFullYear();
	const mes = String(data.getMonth() + 1).padStart(2, '0');
	const dia = String(data.getDate()).padStart(2, '0');
	const hora = String(data.getHours()).padStart(2, '0');
	const minuto = String(data.getMinutes()).padStart(2, '0');

	return `${ano}-${mes}-${dia}T${hora}:${minuto}`;
}

function modoVisualizarTabRegistroDeFato() {
	document
		.querySelectorAll('#formRegistroFatoComBoletim input, #formRegistroFatoComBoletim select, #formRegistroFatoComBoletim textarea, #formRegistroFatoComBoletim button')
		.forEach(el => el.disabled = true);
}

/**
 * Função chamada pelo 'onchange' do dropdown de Tipo.
 * Ela decide se deve buscar as naturezas ou resetar o campo.
 * @param {string} tipoId - O valor do tipo selecionado, vindo do 'this.value'.
 */
function consultaTipoNatureza(tipoId) {
	const naturezaSelect = document.getElementById('id_naturezaTipo');

	// Se um tipo válido foi selecionado, chama a função de consulta
	if (tipoId && tipoId !== '0') {
		// A função obterNaturezasPorTipo() continua no seu arquivo consulta.js
		obterNaturezasPorTipo(tipoId);
	} else {
		// Se o usuário voltou para "Selecione", reseta o dropdown de natureza
		naturezaSelect.innerHTML = '<option value="0">Selecione o Tipo primeiro</option>';
		naturezaSelect.disabled = true;
	}
}

/* ---------- Adicionar ---------- */
function adicionarAnotacao() {
    const textarea = document.getElementById("txtAnotacao");
    if (!textarea) return;
    const texto = textarea.value.trim();
    if (!texto) {
        alert("Por favor, digite a anotação.");
        return;
    }

    // usuário atual (fallback simples)
    const usuario = (document.querySelector("[data-usuario-nome]") && document.querySelector("[data-usuario-nome]").textContent.trim())
        || (document.getElementById("nomeUsuario") && document.getElementById("nomeUsuario").textContent.trim())
        || "-";

    window.anotacoes.push({
        id: null, // novo
        texto,
        nomeUsuario: usuario,
        dataCriacao: isoComOffsetLocal(new Date()),
        status: "novo"
    });

    atualizarTabelaAnotacoes();
    // limpa form
    textarea.value = "";
    atualizarContadorAnotacao();
}

function isoComOffsetLocal(date = new Date()) {
    const pad = (n, z = 2) => String(n).padStart(z, '0');
    const ano = date.getFullYear();
    const mes = pad(date.getMonth() + 1);
    const dia = pad(date.getDate());
    const hh = pad(date.getHours());
    const mm = pad(date.getMinutes());
    const ss = pad(date.getSeconds());
    const ms = pad(date.getMilliseconds(), 3);

    // offset em minutos: diferença UTC - local; queremos sinal invertido
    const offsetMin = -date.getTimezoneOffset(); // ex: +180 para -03:00
    const sign = offsetMin >= 0 ? '+' : '-';
    const absOff = Math.abs(offsetMin);
    const offH = pad(Math.floor(absOff / 60));
    const offM = pad(absOff % 60);

    return `${ano}-${mes}-${dia}T${hh}:${mm}:${ss}.${ms}${sign}${offH}:${offM}`;
}

/* ---------- Remover ---------- */
function removerAnotacao(index) {
    const anot = window.anotacoes[index];
    if (!anot) return;

    if (anot.status === "novo") {
        // remove imediatamente se ainda não foi persistida
        window.anotacoes.splice(index, 1);
    } else {
        // marca como removida para posterior processamento no backend
        anot.status = "removido";
    }

    atualizarTabelaAnotacoes();
}

/* ---------- Atualizar tabela ---------- */
function atualizarTabelaAnotacoes() {
    const tbody = document.getElementById("tableAnotacoes")?.querySelector("tbody") || document.getElementById("tableAnotacoes");
    if (!tbody) return;

    tbody.innerHTML = "";

    if (!window.anotacoes || window.anotacoes.length === 0) {
        const tr = document.createElement("tr");
        tr.innerHTML = `<td colspan="5" class="text-center small text-muted">Nenhuma anotação</td>`;
        tbody.appendChild(tr);
        return;
    }

    window.anotacoes.forEach((a, idx) => {
        let textoDisplay = escapeHtml(a.texto);
        let usuarioDisplay = escapeHtml(a.nomeUsuario || "-");
		let dataDisplay = formatarIsoParaExibir(a.dataCriacao || "");

        if (a.status === "removido") {
            textoDisplay = `<del>${textoDisplay}</del>`;
            usuarioDisplay = `<del>${usuarioDisplay}</del>`;
            dataDisplay = `<del>${dataDisplay}</del>`;
        } else if (a.status === "novo") {
            textoDisplay += ' <span class="badge bg-success">Novo</span>';
        }

        const btnRemover = a.status === "removido"
            ? ""
            : `<button class="btn btn-sm btn-danger" title="Remover" onclick="removerAnotacao(${idx})">
                    <i class="fa fa-trash"></i>
               </button>`;

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${idx + 1}</td>
            <td class="texto-anotacao text-start">${textoDisplay}</td>
            <td class="text-start">${usuarioDisplay}</td>
            <td class="text-start">${dataDisplay}</td>
            <td class="text-center">${btnRemover}</td>
        `;
        tbody.appendChild(tr);
    });
}

// Formata ISO (aceita Z ou offset) para "DD/MM/AAAA HH:mm:ss"
function formatarIsoParaExibir(iso) {
    if (!iso) return "";
    // usa Date para interpretar ISO com offset corretamente
    const d = new Date(iso);
    if (isNaN(d.getTime())) return iso; // fallback: retorna string original
    const pad = (n) => String(n).padStart(2, '0');
    const dia = pad(d.getDate());
    const mes = pad(d.getMonth() + 1);
    const ano = d.getFullYear();
    const hora = pad(d.getHours());
    const minuto = pad(d.getMinutes());
    const segundo = pad(d.getSeconds());
    return `${dia}/${mes}/${ano} ${hora}:${minuto}:${segundo}`;
}

/* ---------- Helpers ---------- */
function escapeHtml(s) {
    if (s == null) return "";
    return String(s)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

function getAnotacoes() {
    return window.anotacoes;
}

function limparAnotacoes() {
    window.anotacoes = [];
    atualizarTabelaAnotacoes();
    const textarea = document.getElementById("txtAnotacao");
    if (textarea) textarea.value = "";
    atualizarContadorAnotacao();
}

/* ---------- Preencher a partir do XML ---------- */
function preencherTabAnotacoes(xmlString) {
    if (!xmlString || typeof xmlString !== "string") return;
    // remove BOM e aspas acidentais
    xmlString = xmlString.replace(/^\uFEFF/, "").trim();
    if ((xmlString.startsWith('"') && xmlString.endsWith('"')) || (xmlString.startsWith("'") && xmlString.endsWith("'"))) {
        xmlString = xmlString.slice(1, -1).trim();
    }

    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlString, "application/xml");
    if (xmlDoc.querySelector("parsererror")) {
        // XML inválido: limpa e sai
        window.anotacoes = [];
        atualizarTabelaAnotacoes();
        return;
    }

    window.anotacoes = [];

    // procura por RegistroDeFato > anotacoes > anotacao (ou qualquer <anotacao>)
    const nodes = xmlDoc.querySelectorAll("RegistroDeFato > anotacoes > anotacao");
    const fallback = xmlDoc.getElementsByTagName("anotacao");

    if (nodes && nodes.length > 0) {
        nodes.forEach(n => {
            const id = n.querySelector("id")?.textContent?.trim() || null;
            const texto = n.querySelector("texto")?.textContent?.trim() || n.textContent.trim() || "";
            const nomeUsuario = n.querySelector("nomeUsuario")?.textContent?.trim() || "";
            const dataCriacao = n.querySelector("dataCriacao")?.textContent?.trim() || "";

            if (texto) {
                window.anotacoes.push({
                    id: id ? parseInt(id, 10) : null,
                    texto,
                    nomeUsuario,
                    dataCriacao,
                    status: "existente"
                });
            }
        });
    } else if (fallback && fallback.length > 0) {
        Array.from(fallback).forEach(n => {
            const id = n.querySelector("id")?.textContent?.trim() || null;
            const texto = n.querySelector("texto")?.textContent?.trim() || n.textContent.trim() || "";
            const nomeUsuario = n.querySelector("nomeUsuario")?.textContent?.trim() || "";
            const dataCriacao = n.querySelector("dataCriacao")?.textContent?.trim() || "";

            if (texto) {
                window.anotacoes.push({
                    id: id ? parseInt(id, 10) : null,
                    texto,
                    nomeUsuario,
                    dataCriacao,
                    status: "existente"
                });
            }
        });
    }

    atualizarTabelaAnotacoes();
}

/* ---------- Contador de caracteres / Inicialização do botão ---------- */
function atualizarContadorAnotacao() {
    const txt = document.getElementById("txtAnotacao");
    const contador = document.getElementById("contadorAnotacao");
    if (!txt || !contador) return;
    const max = parseInt(txt.getAttribute("maxlength") || "300", 10);
    contador.textContent = Math.max(0, max - txt.value.length);
}

// adiciona listener no botão (se existir)
(function attachAnotacaoButton() {
    const btn = document.getElementById("btnAddAnotacao");
    const txt = document.getElementById("txtAnotacao");
    if (btn) btn.addEventListener("click", adicionarAnotacao);
    if (txt) txt.addEventListener("input", atualizarContadorAnotacao);
    // inicializa contador ao carregar
    document.addEventListener("DOMContentLoaded", atualizarContadorAnotacao);
})();