$(document).ready(async function() {
	try {
		await carregarDadosDeSuporte();

	} catch (error) {
		// Se a busca inicial por usuários/grupos falhar, avise o usuário.
		Swal.fire({
			icon: 'error',
			title: 'Falha na Inicialização',
			text: 'Não foi possível carregar os dados de suporte da página. Algumas informações podem não ser exibidas corretamente.',
		});
	}
});

async function abrirModalHistorico(idRegistro) {
	// 1. Prepara o modal para uma nova exibição
	$('#modalHistoricoLabel').text(`Histórico de Alterações - Registro #${idRegistro}`);

	// Limpa o conteúdo anterior e mostra o spinner de carregamento
	$('#historicoConteudo').empty().hide();
	$('#historicoVazio').hide();
	$('#historicoLoading').show(); // Spinner aparece aqui

	// Abre o modal
	$('#modalHistorico').modal('show'); // Usando a versão jQuery que é mais robusta

	try {
		const listaDeHistoricos = await obterHistoricoPorIdRegistro(idRegistro);
		popularModalHistorico(listaDeHistoricos);

	} catch (error) {
		console.error("Falha ao obter histórico:", error);

		// --- CORREÇÃO AQUI ---
		// 1. Esconde o spinner que ficou visível.
		$('#historicoLoading').hide();

		// 2. Esconde o modal.
		$('#modalHistorico').modal('hide');

		// 3. Exibe o alerta de erro.
		exibirErroModalHistorico(error.message);
	}
}

function popularModalHistorico(listaHistoricos) {
	$('#historicoLoading').hide();
	const container = $('#historicoConteudo');

	if (listaHistoricos.length === 0) {
		$('#historicoVazio').show();
		return;
	}

	listaHistoricos.forEach((item, index) => {
		const itemHtml = criarItemAcordeao(item, index);
		container.append(itemHtml);
	});

	container.show();
}


/**
 * Exibe uma mensagem de erro usando SweetAlert2.
 * Esta função foi COMPLETAMENTE SUBSTITUÍDA.
 * @param {string} mensagem A mensagem de erro a ser exibida.
 */
function exibirErroModalHistorico(mensagem) {
	Swal.fire({
		icon: 'error',
		title: 'Ocorreu um Erro!',
		text: mensagem,
		confirmButtonColor: '#3085d6',
		confirmButtonText: 'Entendi'
	});
}

/**
 * Helper: Cria o HTML para um único item do acordeão.
 * @param {object} item O objeto do histórico.
 * @param {number} index O índice do item, para IDs únicos.
 * @returns {string} O HTML do item do acordeão.
 */
function criarItemAcordeao(item, index) {
	const dataFormatada = new Date(item.dataAlteracao).toLocaleString('pt-BR');
	const badgeClass = item.tipoOperacao === 'INSERT' ? 'bg-success' : item.tipoOperacao === 'UPDATE' ? 'bg-warning text-dark' : 'bg-danger';

	let htmlAlteracoes = '';
	try {
		// Para UPDATE, mostramos apenas o que mudou (dadosNovos).
		// Para INSERT, mostramos o registro completo que foi criado (dadosNovos).
		// Para DELETE, mostramos o que foi removido (dadosAnteriores).
		if (item.tipoOperacao === 'DELETE') {
			const dadosAnterioresObj = JSON.parse(item.dadosAnteriores || '{}');
			htmlAlteracoes = formatarAlteracoes(dadosAnterioresObj, item.tipoOperacao);
		} else {
			const dadosNovosObj = JSON.parse(item.dadosNovos || '{}');
			htmlAlteracoes = formatarAlteracoes(dadosNovosObj, item.tipoOperacao);
		}
	} catch (e) {
		console.error("Erro ao parsear JSON do histórico:", e);
		htmlAlteracoes = '<div class="alert alert-warning">Não foi possível exibir os detalhes da alteração.</div>';
	}

	return `
    <div class="accordion-item">
        <h2 class="accordion-header" id="heading${index}">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">
                <span class="badge ${badgeClass} me-2">${item.tipoOperacao}</span>
                <span class="fw-bold me-2">Usuário : ${item.nomeUsuario}</span>
                <span class="text-muted small">em ${dataFormatada}</span>
            </button>
        </h2>
        <div id="collapse${index}" class="accordion-collapse collapse" aria-labelledby="heading${index}" data-bs-parent="#historicoConteudo">
            <div class="accordion-body">
                <h6 class="border-bottom pb-2 mb-3">Detalhes da Alteração</h6>
                ${htmlAlteracoes}
            </div>
        </div>
    </div>
    `;
}

/**
 * Traduz um objeto JSON de alterações em um HTML legível para humanos.
 * @param {object} dadosJson O objeto JSON com as alterações.
 * @param {string} tipoOperacao O tipo de operação (INSERT, UPDATE, DELETE).
 * @returns {string} Uma string HTML formatada com as alterações.
 */
function formatarAlteracoes(dadosJson, tipoOperacao) {
    if (!dadosJson || Object.keys(dadosJson).length === 0) {
        return tipoOperacao === 'DELETE'
            ? '<div class="text-muted">Registro foi removido.</div>'
            : '<div class="text-muted">Nenhuma alteração detalhada.</div>';
    }

    const linhasHtml = [];
    const verbo = tipoOperacao === 'INSERT' ? 'Definido' : 'Alterado';

    const dicionario = {
        detalhamento: "Detalhamento",
        envolvimentoArmas: "Envolvimento com Armas",
        permiteAtendimento: "Permite Atendimento",
        complemento: "Complemento",
        rua: "Rua",
        numero: "Número",
        bairro: "Bairro",
        cep: "CEP"
        // adicione mais traduções se precisar
    };

    const chavesGerais = [
        'idTipo', 'idStatus', 'temBoletim', 'dataCriacao',
        'tipoDescricao', 'statusDescricao', 'nomeUsuario',
        'privado', 'id', 'idUsuario', 'observacaoAlteracao',
        'idNaturezaTipo', 'naturezaTipoDescricao', 'dataEvento'
    ];

    // Exibe observacaoAlteracao em destaque se existir
    if (dadosJson.hasOwnProperty('observacaoAlteracao')) {
        linhasHtml.push(`
            <div class="alert alert-primary d-flex align-items-center" role="alert">
                <i class="bi bi-info-circle-fill me-2"></i>
                <div><strong>${dadosJson.observacaoAlteracao}</strong></div>
            </div>`);
    }

    // Bloco geral (tipo, data, criado por, etc.)
    const blocoGeralHtml = formatarDadosGerais(dadosJson);
    if (blocoGeralHtml) linhasHtml.push(blocoGeralHtml);

    // helpers locais
    function kindOf(key, val) {
        if (val && typeof val === 'object' && !Array.isArray(val) && (val.hasOwnProperty('adicionados') || val.hasOwnProperty('removidos'))) {
            return 'adRem';
        }
        if (Array.isArray(val)) return 'array';
        if (val && typeof val === 'object') {
            if (key === 'boletim') return 'boletim';
            return 'object';
        }
        return 'primitive';
    }

    function renderAdRem(key, val) {
        const nomeCampo = dicionario[key] || key.charAt(0).toUpperCase() + key.slice(1);
        let html = `<strong>${nomeCampo}:</strong><ul class="list-unstyled ps-3 mt-1">`;
        if (val.adicionados && val.adicionados.length > 0) {
            val.adicionados.forEach(item => {
                html += `<li><span class="text-success fw-bold">ADICIONADO:</span> ${gerarDescricaoItem(key, item)}</li>`;
            });
        }
        if (val.removidos && val.removidos.length > 0) {
            val.removidos.forEach(item => {
                html += `<li><span class="text-danger fw-bold">REMOVIDO:</span> ${gerarDescricaoItem(key, item)}</li>`;
            });
        }
        html += '</ul>';
        return html;
    }

    function renderArray(key, arr) {
        const nomeCampo = dicionario[key] || key.charAt(0).toUpperCase() + key.slice(1);
        let html = `<strong>${nomeCampo}:</strong><ul class="list-unstyled ps-3 mt-1">`;
        arr.forEach(item => {
            html += `<li><span class="text-info fw-bold">REGISTRADO:</span> ${gerarDescricaoItem(key, item)}</li>`;
        });
        html += '</ul>';
        return html;
    }

    function renderBoletim(val) {
        const nomeCampo = 'Boletim';
        const verboBoletim = tipoOperacao === 'INSERT' ? '' : 'Alterado para';
        let desc = `<strong>${nomeCampo}:</strong><div class="ps-3">`;

        if (val.detalhamento) {
            desc += `<div><strong>Detalhes:</strong> ${verboBoletim}: ${val.detalhamento}</div>`;
        }
        if (val.situacao?.descricao) {
            desc += `<div><strong>Situação:</strong> ${verboBoletim}: ${val.situacao.descricao}</div>`;
        }
        if (val.hasOwnProperty('permitirAtendimento')) {
            const permite = val.permitirAtendimento == 1 ? 'Sim' : 'Não';
            desc += `<div><strong>Permite Atendimento:</strong> ${verboBoletim}: ${permite}</div>`;
        }

        if (val.apreensoes && (val.apreensoes.adicionados || val.apreensoes.removidos)) {
            desc += '<div class="mt-2"><strong>Apreensões:</strong><ul class="list-unstyled ps-3">';
            if (val.apreensoes.adicionados && val.apreensoes.adicionados.length > 0) {
                val.apreensoes.adicionados.forEach(ap => {
                    desc += `<li><span class="text-success fw-bold">ADICIONADO:</span> Apreensão ${ap.tipo}: ${ap.descricao}</li>`;
                });
            }
            if (val.apreensoes.removidos && val.apreensoes.removidos.length > 0) {
                val.apreensoes.removidos.forEach(ap => {
                    desc += `<li><span class="text-danger fw-bold">REMOVIDO:</span> Apreensão ${ap.tipo}: ${ap.descricao}</li>`;
                });
            }
            desc += '</ul></div>';
        }

        desc += '</div>';
        return desc;
    }

    function renderObjectGeneric(key, obj) {
        const nomeCampo = dicionario[key] || key.charAt(0).toUpperCase() + key.slice(1);
        let sub = `<strong>${nomeCampo}:</strong><ul class="list-unstyled ps-3 mt-1">`;
        for (const subChave in obj) {
            if (['id', 'nomeCidade', 'idCidade', 'idTipoEvento'].includes(subChave)) continue;
            const subValor = obj[subChave];
            const nomeSubCampo = dicionario[subChave] || subChave.charAt(0).toUpperCase() + subChave.slice(1);
            sub += `<li><strong>${nomeSubCampo}:</strong> ${verbo} para: <strong>${subValor}</strong></li>`;
        }
        sub += '</ul>';
        return sub;
    }

    function renderPrimitive(key, val) {
        const nomeCampo = dicionario[key] || key.charAt(0).toUpperCase() + key.slice(1);
        return `<strong>${nomeCampo}:</strong><div class="ps-3">${verbo} para: <strong>${val}</strong></div>`;
    }

    // Loop principal usando switch por tipo de "kind"
    for (const chave in dadosJson) {
        if (chavesGerais.includes(chave)) continue;
        const valor = dadosJson[chave];
        const k = kindOf(chave, valor);

        let html = '<div class="mb-3">';
        switch (k) {
            case 'adRem':
                html += renderAdRem(chave, valor);
                break;
            case 'array':
                html += renderArray(chave, valor);
                break;
            case 'boletim':
                html += renderBoletim(valor);
                break;
            case 'object':
                html += renderObjectGeneric(chave, valor);
                break;
            default:
                html += renderPrimitive(chave, valor);
        }
        html += '</div>';
        linhasHtml.push(html);
    }

    return linhasHtml.join('');
}

/**
 * Cria um bloco HTML formatado para os dados gerais do registro de fato.
 * @param {object} dadosJson O objeto JSON com os dados.
 * @returns {string} Uma string HTML com o bloco de dados gerais, ou uma string vazia.
 */
function formatarDadosGerais(dadosJson) {
    if (!dadosJson || Object.keys(dadosJson).length === 0) return "";

    // Mapa: chaveNoJson -> { label, opcional: formatter(value) }
    const campos = {
        tipoDescricao: { label: "Tipo" },
        naturezaTipoDescricao: { label: "Natureza do Tipo" },
        statusDescricao: { label: "Situação" },
        dataEvento: { label: "Data do Evento", formatter: v => formatarDataValor(v) },
        nomeUsuario: { label: "Criado Por", formatter: v => (typeof v === 'string' ? v.trim() : v) },
        temBoletim: { label: "Possui Boletim Vinculado", formatter: v => (Number(v) === 1 ? "Sim" : "Não") },
        privado: { label: "Visibilidade", formatter: v => (Number(v) === 1 ? "Privado" : "Público") },
        dataCriacao: { label: "Data de Criação", formatter: v => formatarDataValor(v) }
    };

    const itens = [];

    for (const chave in campos) {
        if (!Object.prototype.hasOwnProperty.call(dadosJson, chave)) continue;
        const meta = campos[chave];
        const raw = dadosJson[chave];
        const valor = typeof meta.formatter === "function" ? meta.formatter(raw) : raw;
        if (valor == null || (typeof valor === "string" && valor.trim() === "")) continue;
        itens.push(`<li><strong>${meta.label}:</strong> ${valor}</li>`);
    }

    if (itens.length === 0) return "";

    return `
        <div class="mb-2">
            <strong>Dados Gerais do Registro:</strong>
            <ul class="list-unstyled ps-3 mt-1">
                ${itens.join("")}
            </ul>
        </div>
    `;
}

/**
 * Gera uma string de descrição para um item de uma lista (indivíduo, veículo, etc.).
 * @param {string} chave A chave da lista (ex: 'individuos', 'veiculos').
 * @param {object} item O objeto do item.
 * @returns {string} A descrição formatada.
 */
function gerarDescricaoItem(chave, item) {
    let itemDesc = "Item"; // padrão

    switch (chave) {

        // -----------------------------------------------------
        case "individuos":
            itemDesc = `${item.nome} (${item.tipoEnvolvimento?.descricao || "Envolvimento não especificado"})`;
            break;

        // -----------------------------------------------------
        case "veiculos":
            itemDesc = `Veículo ${item.placa} (${item.marca || ""} ${item.modelo || ""})`.trim();
            break;

        // -----------------------------------------------------
        case "objetos":
            itemDesc = `Objeto ${item.tipo || ""}: ${item.descricao || ""}`.trim();
            break;

        // -----------------------------------------------------
        case "documentos": {
            let nomeArquivo = "Nome não encontrado";

            if (item.dirArquivo) {
                const ultimoSeparador = Math.max(
                    item.dirArquivo.lastIndexOf("\\"),
                    item.dirArquivo.lastIndexOf("/")
                );
                nomeArquivo = item.dirArquivo.substring(ultimoSeparador + 1);
            }

            itemDesc = `Documento: ${item.tipo || ""} (${nomeArquivo})`;
            break;
        }

        // -----------------------------------------------------
        case "links":
            itemDesc = `Link: ${item.detalhamento || item.url || ""}`;
            break;

        // -----------------------------------------------------
        case "usuarioGrupos":
            if (item.idUsuario) {
                const usuario = window.usuariosDisponiveis?.find(u => u.id == item.idUsuario);
                itemDesc = `Vínculo com Usuário: ${usuario ? usuario.nome : `ID ${item.idUsuario}`}`;
            } else if (item.idGrupo) {
                const grupo = window.gruposDisponiveis?.find(g => g.id == item.idGrupo);
                itemDesc = `Vínculo com Grupo: ${grupo ? grupo.nome : `ID ${item.idGrupo}`}`;
            } else {
                itemDesc = "Vínculo desconhecido";
            }
            break;

        // -----------------------------------------------------
        case "passagensVeiculo":
            itemDesc = `Passagem de Veículo registrada em ${formatarDataValor(item.dataHora)}`;
            break;

        // -----------------------------------------------------
        case "anotacoes": {
            const texto = (item.texto || "").trim();
            const usuario = (item.nomeUsuario || item.usuario || "").trim();
            const data = formatarDataValor(item.dataCriacao ?? item.dataIso ?? item.data);
            const resumo = texto.length > 120 ? texto.substring(0, 120) + "..." : texto;

            itemDesc = `${resumo} <small class="text-muted">— por ${usuario || "—"} em ${data || "—"}</small>`;
            break;
        }

        // -----------------------------------------------------
        default:
            itemDesc = "Item";
    }

    return itemDesc;
}

function formatarDataValor(value) {
    if (value == null || value === "") return "";
    try {
        // se for número (epoch ms)
        if (typeof value === "number" || (typeof value === "string" && /^\d+$/.test(value))) {
            const ms = Number(value);
            if (!isNaN(ms)) {
                return new Date(ms).toLocaleString('pt-BR');
            }
        }
        // senão tenta como string ISO/Date
        const d = new Date(value);
        if (!isNaN(d.getTime())) {
            return d.toLocaleString('pt-BR');
        }
    } catch (e) {
        // swallow
    }
    return String(value);
}
