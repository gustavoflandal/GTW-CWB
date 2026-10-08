/**
 * Estado da paginação para cada tipo de dado.
 */
const paginacaoState = {
    alertas: { atuais: [], paginaAtual: 1, porPagina: 9 },
    boletim: { atuais: [], paginaAtual: 1, porPagina: 9 },
};

/**
 * URL base da aplicação.
 */
const baseUrl = window.location.origin;

/**
 * Cache para os detalhes do veículo.
 */
const detalhesVeiculoCache = new Map();

/**
 * Exibe os ícones de ação ou abre o modal diretamente.
 * @param {Object} data - Dados básicos do nó do grafo.
 */
async function showModalInfoAsync(data) {
    const modalElement = document.getElementById('modalInfoVeiculo');
    const modalAberto = modalElement && modalElement.classList.contains('show');
    if (modalAberto) {
        return;
    }
    
    removerIconesFlutuantesAsync();
    const node = cy.getElementById(data.id);
    if (!node) return;

    showLoadingOverlay();
    try {
        const detalhes = await obterDetalhesVeiculoComCacheAsync(data.id);

    if (!detalhes) {
        mostrarToastAsync(`Não há detalhes disponíveis para a placa ${data.id}.`, "alerta");
        return;
    }
    
    const temAlertas = detalhes.alertas && detalhes.alertas.length > 0;
    const temProprietario = detalhes.proprietario && detalhes.proprietario.nome;
    const temQualquerBoletim = (detalhes.boletimOcorrencia && detalhes.boletimOcorrencia.length > 0) ||
                               (detalhes.alertas || []).some(a => a.boletimOcorrencia && a.boletimOcorrencia.length > 0);
    const iconesDisponiveis = [
        { id: 'alertas-tab', label: 'Alerta', icon: '🚨', condicao: temAlertas },
        { id: 'proprietario-tab', label: 'Proprietário', icon: '🚗', condicao: temProprietario },
        { id: 'boletimOcorrencia-tab', label: 'Registro de Fato', icon: '📄', condicao: temQualquerBoletim },
        { id: 'passagens-tab', label: 'Passagens', icon: '📍', condicao: true },
    ];

    const iconesParaMostrar = iconesDisponiveis.filter(item => item.condicao);
    const temOutrasAbas = temAlertas || temProprietario || temQualquerBoletim;

    if (!temOutrasAbas) {
        await abrirModalComAbaAsync(data, 'passagens-tab', detalhes);
        return;
    }

    node._customIcons = [];
    const container = document.body;
    iconesParaMostrar.forEach((item) => {
        const el = document.createElement('div');
        el.className = 'grafo-action-icon';
        el.innerHTML = `<div style="font-size: 32px;">${item.icon}</div><div>${item.label}</div>`;
        el.onclick = () => {
            removerIconesFlutuantesAsync();
            abrirModalComAbaAsync(data, item.id, detalhes);
        };
        container.appendChild(el);
        node._customIcons.push({ element: el });
    });

    atualizarPosicaoIconesAsync(node);
    } catch (error) {
        console.error('[grafo-modal.js] Erro ao buscar detalhes:', error);
        mostrarToastAsync('Erro ao carregar detalhes do veículo', 'erro');
    } finally {
        hideLoadingOverlay();
    }
}

/**
 * Abre o modal e ativa a aba especificada.
 * @param {Object} data - Dados do veículo (vindo do nó do grafo, contém o ID/placa).
 * @param {string | null} abaId - ID da aba a ser ativada.
 * @param {Object | null} detalhesJaBuscados - Objeto de detalhes já buscado.
 */
async function abrirModalComAbaAsync(data, abaId, detalhesJaBuscados = null) {
    removerIconesFlutuantesAsync();
    const detalhes = detalhesJaBuscados || await obterDetalhesVeiculoComCacheAsync(data.id);
    if (!detalhes) {
        mostrarToastAsync(`Não foram encontrados detalhes para a placa ${data.id}.`, "alerta");
        return;
    }

    if (!detalhes.informacoes) detalhes.informacoes = {};
    if (!detalhes.informacoes.placa && data.id !== 'null') {
        detalhes.informacoes.placa = data.id;
    }
    if (data.correlacao) detalhes.informacoes.correlacao = data.correlacao;
    if (data.passagens !== undefined) detalhes.informacoes.passagens = data.passagens;
    if (cy) {
        const node = cy.getElementById(data.id);
        if (node && node.length > 0) {
            detalhes.informacoes.registro_fato = node.data('registro_fato') || '';
            detalhes.informacoes.sem_ocr_registro_fato = node.data('sem_ocr_registro_fato') || 'false';
        }
    }

    await preencherModalComDetalhesAsync(detalhes, data.basePlaca !== "true", abaId);

    const modalElement = document.getElementById('modalInfoVeiculo');
    const modalInstance = bootstrap.Modal.getOrCreateInstance(modalElement);
    modalInstance.show();
}

/**
 * Obtém os detalhes do veículo, utilizando o cache se disponível.
 * @param {string} placa - Placa do veículo.
 * @returns {Promise<Object|null>}
 */
async function obterDetalhesVeiculoComCacheAsync(placa) {
    if (placa === 'null') {
        return {
            informacoes: {
                placa: null,
                marca: null,
                modelo: null,
                cor: null,
                anoFabricacao: null
            },
            proprietario: {},
            alertas: [],
            boletimOcorrencia: []
        };
    }
    
    if (detalhesVeiculoCache.has(placa)) {
        return detalhesVeiculoCache.get(placa);
    }
    const filtros = await obterFiltrosAsync();
    const dataHoraInicio = filtros?.dataHoraInicio || '';
    const dataHoraFim = filtros?.dataHoraFim || '';
    const detalhes = await obterDetalhesVeiculoAsync(dataHoraInicio, dataHoraFim, placa);
    if (detalhes) {
        detalhesVeiculoCache.set(placa, detalhes);
    }
    return detalhes;
}

/**
 * Limpa todo o conteúdo dinâmico do modal.
 */
async function limparConteudoModalAsync() {
    const limparSeExistir = (id) => {
        const elemento = document.getElementById(id);
        if (elemento) elemento.innerHTML = '';
    };
    limparSeExistir('tabela-alertas-body');
    limparSeExistir('tabela-antecedentes-body');
    limparSeExistir('tabela-boletim-body');
    limparSeExistir('tabela-correlacoes-placa-body');
    limparSeExistir('tabela-passagens-body');

    const camposTexto = ['modal-nome-proprietario', 'modal-cpf-proprietario', 'modal-data-nascimento-proprietario', 'modal-endereco-proprietario', 'modal-telefone-proprietario', 'modal-email-proprietario', 'modal-placa', 'modal-marca', 'modal-modelo', 'modal-cor', 'modal-ano'];
    camposTexto.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.textContent = '-';
    });
    document.getElementById('modal-sobrenome-proprietario').textContent = '';

    const imgElement = document.getElementById('modal-imagem-veiculo');
    if (imgElement) {
        imgElement.src = "";
        imgElement.style.display = 'none';
    }
    
    const imgPlaceholder = document.getElementById('modal-imagem-placeholder');
    if (imgPlaceholder) {
        imgPlaceholder.style.display = 'none';
    }
    
    const correlacoesPlaceholder = document.getElementById('modal-correlacoes-placeholder');
    if (correlacoesPlaceholder) {
        correlacoesPlaceholder.style.display = 'none';
    }

    const passagensPlaceholder = document.getElementById('modal-passagens-placeholder');
    if (passagensPlaceholder) {
        passagensPlaceholder.style.display = 'none';
    }

    const badgeTotalPassagens = document.getElementById('badge-total-passagens');
    if (badgeTotalPassagens) {
        badgeTotalPassagens.textContent = '0 passagens';
    }

    const badgesRegistroFato = document.getElementById('badges-registro-fato');
    if (badgesRegistroFato) {
        badgesRegistroFato.innerHTML = '';
        badgesRegistroFato.style.display = 'none';
    }

    const chkCorrelacionadas = document.getElementById('chk-apenas-correlacionadas');
    if (chkCorrelacionadas) {
        chkCorrelacionadas.checked = false;
    }

    passagensCache.clear();

    document.getElementById('modal-empty-message').style.display = 'none';
    document.querySelector('#modalInfoVeiculo .nav-tabs').style.display = '';
    document.querySelector('#modalInfoVeiculo .tab-content').style.display = '';
}

/**
 * Preenche todas as abas do modal com os detalhes do veículo.
 * @param {Object} detalhes - Detalhes do veículo.
 * @param {boolean} mostrarBotaoRemover - Controla a exibição do botão de remover.
 * @param {string | null} abaIdParaAtivar - ID da aba a ser ativada.
 */
async function preencherModalComDetalhesAsync(detalhes, mostrarBotaoRemover, abaIdParaAtivar = null) {
    await limparConteudoModalAsync();
    configurarBotoesFooterModal();

    const { informacoes, alertas, proprietario, boletimOcorrencia } = detalhes;

    const temAlertas = alertas && alertas.length > 0;
    const temProprietario = proprietario && proprietario.nome;
    const temAntecedentes = temProprietario && proprietario.AntecedentesCriminais && proprietario.AntecedentesCriminais.length > 0;
    const todosOsBoletinsUnicos = boletimOcorrencia || [];
    const temQualquerBoletim = todosOsBoletinsUnicos.length > 0;
    const mostrarAbaProprietario = temProprietario || temAntecedentes;
    const temAlgoParaMostrar = temAlertas || mostrarAbaProprietario || temQualquerBoletim || true;

    const abas = {
        'passagens-tab': true,
        'alertas-tab': temAlertas,
        'boletimOcorrencia-tab': temQualquerBoletim,
        'proprietario-tab': mostrarAbaProprietario,
    };

    await preencherInformacoesFixasAsync(informacoes);

    if (temAlgoParaMostrar) {
        document.querySelector('#modalInfoVeiculo .nav-tabs').style.display = '';
        document.querySelector('#modalInfoVeiculo .tab-content').style.display = '';
        document.getElementById('modal-empty-message').style.display = 'none';

        let primeiraAbaVisivel = null;
        for (const [id, condicao] of Object.entries(abas)) {
            const tabElement = document.getElementById(id);
            if (tabElement) {
                const parentLi = tabElement.closest('.nav-item');
                parentLi.style.display = condicao ? '' : 'none';
                if (condicao && !primeiraAbaVisivel) {
                    primeiraAbaVisivel = tabElement;
                }
            }
        }

        await preencherAbaPassagensAsync(informacoes);

        if (mostrarAbaProprietario) await preencherAbaProprietarioAsync(proprietario);
        if (temAlertas) await inicializarPaginacaoAsync('alertas', alertas, renderizarTabelaAlertasAsync);
        if (temQualquerBoletim) await inicializarPaginacaoAsync('boletim', todosOsBoletinsUnicos, renderizarTabelaBoletimAsync);
        let abaParaMostrar = null;
        if (abaIdParaAtivar) {
            const abaRequisitada = document.getElementById(abaIdParaAtivar);
            if (abaRequisitada && abaRequisitada.closest('.nav-item').style.display !== 'none') {
                abaParaMostrar = abaRequisitada;
            }
        }
        
        if (!abaParaMostrar) {
            abaParaMostrar = primeiraAbaVisivel;
        }

        if (abaParaMostrar) {
            bootstrap.Tab.getOrCreateInstance(abaParaMostrar).show();
        }
    } else {
        document.querySelector('#modalInfoVeiculo .nav-tabs').style.display = 'none';
        document.querySelector('#modalInfoVeiculo .tab-content').style.display = 'none';
        document.getElementById('modal-empty-message').textContent = "Não foram encontrados agravantes para esta placa no sistema.";
        document.getElementById('modal-empty-message').style.display = 'block';
    }

    const btnRemover = document.getElementById('btn-remover-veiculo');
    btnRemover.setAttribute('data-placa', informacoes.placa);
    btnRemover.style.display = mostrarBotaoRemover ? 'inline-block' : 'none';
}

async function preencherInformacoesFixasAsync(info = {}) {
    const semPlaca = info.placa === null;
    document.getElementById('modal-placa').textContent = semPlaca ? 'SEM PLACA' : (info.placa || '-');
    
    const btnTodosBoletins = document.getElementById('btn-todos-boletins');
    const btnPerfilComportamental = document.getElementById('btn-perfil-comportamental');
    const btnConsultarVeiculo = document.getElementById('btn-consultar-veiculo');
    const btnRemoverVeiculo = document.getElementById('btn-remover-veiculo');
    
    if (semPlaca) {
        btnTodosBoletins.disabled = true;
        btnTodosBoletins.title = 'Não disponível para passagens sem placa';
        btnPerfilComportamental.disabled = true;
        btnPerfilComportamental.title = 'Não disponível para passagens sem placa';
        btnConsultarVeiculo.disabled = true;
        btnConsultarVeiculo.title = 'Não disponível para passagens sem placa';
        btnRemoverVeiculo.disabled = true;
        btnRemoverVeiculo.title = 'Não disponível para passagens sem placa';
    } else {
        btnTodosBoletins.disabled = false;
        btnTodosBoletins.title = '';
        btnPerfilComportamental.disabled = false;
        btnPerfilComportamental.title = '';
        btnConsultarVeiculo.disabled = false;
        btnConsultarVeiculo.title = '';
        btnRemoverVeiculo.disabled = false;
        btnRemoverVeiculo.title = '';
    }
    document.getElementById('modal-marca').textContent = info.marca || '-';
    document.getElementById('modal-modelo').textContent = info.modelo || '-';
    document.getElementById('modal-cor').textContent = info.cor || '-';
    document.getElementById('modal-ano').textContent = info.anoFabricacao || '-';
    
    const explicacao = document.getElementById('modal-explicacao-sem-placa');
    if (explicacao) {
        explicacao.style.display = semPlaca ? 'block' : 'none';
    }

    const secaoSemOcrRegistroFato = document.getElementById('modal-secao-sem-ocr-registro-fato');
    if (secaoSemOcrRegistroFato) {
        const temRegistroFatoSemOcr = info.sem_ocr_registro_fato === 'true' || info.sem_ocr_registro_fato === true;
        secaoSemOcrRegistroFato.style.display = temRegistroFatoSemOcr ? 'block' : 'none';
    }

    // Imagem do veículo
    const imgElement = document.getElementById('modal-imagem-veiculo');
    const imgPlaceholder = document.getElementById('modal-imagem-placeholder');
    
    if (imgElement && imgPlaceholder) {
        if (info.imagem_base64) {
            imgElement.src = info.imagem_base64;
            imgElement.style.display = 'block';
            imgPlaceholder.style.display = 'none';
        } else {
            imgElement.style.display = 'none';
            imgPlaceholder.style.display = 'block';
        }
    }

    // Correlações por placa alvo
    await preencherCorrelacoesPorPlacaAsync(info.placa);
}

/**
 * Preenche a aba Passagens com a lista paginada de passagens.
 * @param {Object} info - Informações do veículo.
 */
async function preencherAbaPassagensAsync(info = {}) {
    await carregarPassagensPaginadasAsync(info.placa, 1);
}

/**
 * Preenche a tabela de correlações por placa alvo buscando as arestas do grafo.
 * @param {string} placa - Placa do veículo selecionado.
 */
async function preencherCorrelacoesPorPlacaAsync(placa) {
    const corpo = document.getElementById('tabela-correlacoes-placa-body');
    const placeholder = document.getElementById('modal-correlacoes-placeholder');
    
    if (!corpo || !cy) return;
    
    corpo.innerHTML = '';
    placeholder.style.display = 'none';
    
    const correlacoesPorPlaca = [];
    
    const node = cy.getElementById(placa);
    if (node && node.length > 0) {
        const arestas = node.connectedEdges();
        
        arestas.forEach(aresta => {
            const source = aresta.data('source');
            const target = aresta.data('target');
            const incidencia = aresta.data('incidencia');
            const passagens = aresta.data('passagens');
            
            const placaAlvo = source === placa ? target : source;
            
            const nodeAlvo = cy.getElementById(placaAlvo);
            const ehPlacaAlvo = nodeAlvo && nodeAlvo.data('basePlaca') === 'true';
            
            // Evita duplicatas
            const jaExiste = correlacoesPorPlaca.some(c => c.placaAlvo === placaAlvo);
            
            if (ehPlacaAlvo && !jaExiste) {
                correlacoesPorPlaca.push({
                    placaAlvo,
                    passagens: passagens || 0,
                    incidencia: incidencia || '-'
                });
            }
        });
    }
    
    correlacoesPorPlaca.sort((a, b) => b.passagens - a.passagens);
    
    mapaCorPlacaAlvo.clear();
    correlacoesPorPlaca.forEach((item, idx) => {
        mapaCorPlacaAlvo.set(item.placaAlvo, idx);
    });
    
    const linhasFixas = 3;
    
    for (let i = 0; i < linhasFixas; i++) {
        if (i < correlacoesPorPlaca.length) {
            const item = correlacoesPorPlaca[i];
            const corIdx = mapaCorPlacaAlvo.get(item.placaAlvo) ?? i;
            const incidenciaLabel = traduzirIncidencia(item.incidencia);
            const incidenciaClass = obterClasseIncidencia(item.incidencia);
            
            corpo.innerHTML += `
                <div class="mb-1">
                    <strong>Placa:</strong> <span class="badge badge-placa-${corIdx}">${item.placaAlvo}</span> 
                    <strong class="ms-2">Passagens:</strong> ${item.passagens} 
                    <strong class="ms-2">Correlação:</strong> <span class="badge ${incidenciaClass}">${incidenciaLabel}</span>
                </div>`;
        } else {
            corpo.innerHTML += `
                <div class="mb-1 text-muted">
                    <strong>Placa:</strong> - 
                    <strong class="ms-2">Passagens:</strong> - 
                    <strong class="ms-2">Correlação:</strong> -
                </div>`;
        }
    }
}

/**
 * Traduz a sigla de incidência para texto legível.
 * @param {string} sigla - A/M/F ou texto completo.
 * @returns {string} Texto traduzido.
 */
function traduzirIncidencia(sigla) {
    const mapa = {
        'A': 'Alta',
        'M': 'Média',
        'F': 'Baixa',
        'alta': 'Alta',
        'media': 'Média',
        'baixa': 'Baixa'
    };
    return mapa[sigla] || sigla || '-';
}

/**
 * Retorna a classe CSS para o badge de incidência.
 * @param {string} sigla - A/M/F ou texto completo.
 * @returns {string} Classe CSS do badge.
 */
function obterClasseIncidencia(sigla) {
    const mapa = {
        'A': 'bg-danger',
        'M': 'bg-warning text-dark',
        'F': 'bg-secondary',
        'alta': 'bg-danger',
        'media': 'bg-warning text-dark',
        'baixa': 'bg-secondary'
    };
    return mapa[sigla] || 'bg-secondary';
}

/**
 * Mapa de placa alvo → índice de cor (0, 1, 2).
 * Populado em preencherCorrelacoesPorPlacaAsync para uso na renderização de passagens.
 * @type {Map<string, number>}
 */
const mapaCorPlacaAlvo = new Map();

/** Variáveis de estado para paginação de passagens */
let passagensPlacaAtual = null;
let passagensPaginaAtual = 1;
let passagensTotalPaginas = 1;
let passagensTotalRegistros = 0;

/**
 * Cache de passagens para evitar requisições repetidas ao alternar filtro.
 * Chave: "placa|pagina|apenasCorrelacionadas" → Valor: dados JSON do backend.
 */
const passagensCache = new Map();

/**
 * Carrega as passagens paginadas do backend.
 * @param {string} placa - Placa do veículo.
 * @param {number} pagina - Número da página (1-indexed).
 */
async function carregarPassagensPaginadasAsync(placa, pagina = 1) {
    const corpo = document.getElementById('tabela-passagens-body');
    const placeholder = document.getElementById('modal-passagens-placeholder');
    const badge = document.getElementById('badge-total-passagens');
    const containerPaginacao = document.getElementById('passagens-paginacao-container');
    
    if (!corpo) return;
    
    const placaRequisicao = placa || 'SEM_PLACA';
    passagensPlacaAtual = placaRequisicao;
    passagensPaginaAtual = pagina;
    
    try {
        corpo.innerHTML = '<tr><td colspan="4" class="text-center"><i class="bi bi-hourglass-split"></i> Carregando...</td></tr>';
        
        const toggleCorrelacionadas = document.getElementById('chk-apenas-correlacionadas');
        const apenasCorrelacionadas = toggleCorrelacionadas ? toggleCorrelacionadas.checked : false;
        
        const cacheKey = `${placaRequisicao}|${pagina}|${apenasCorrelacionadas}`;
        let dados;
        
        if (passagensCache.has(cacheKey)) {
            dados = passagensCache.get(cacheKey);
        } else {
            const dataIni = document.getElementById('txt_data_inicio')?.value || '';
            const horaIni = document.getElementById('txt_hora_inicio')?.value || '';
            const dataFim = document.getElementById('txt_data_fim')?.value || '';
            const horaFim = document.getElementById('txt_hora_fim')?.value || '';
            const dataHoraInicio = (dataIni && horaIni) ? `${dataIni}T${horaIni}` : '';
            const dataHoraFim = (dataFim && horaFim) ? `${dataFim}T${horaFim}` : '';
            
            let placasAlvoStr = '';
            if (cy) {
                const placasAlvo = cy.nodes().filter(n => n.data('basePlaca') === 'true').map(n => n.data('id'));
                placasAlvoStr = placasAlvo.join(',');
            }
            
            const params = new URLSearchParams({
                acao: 'passagens',
                placa: placaRequisicao,
                dataInicio: dataHoraInicio,
                dataFim: dataHoraFim,
                pagina: pagina,
                tamanhoPagina: 10
            });
            if (placasAlvoStr) {
                params.append('placasAlvo', placasAlvoStr);
            }
            if (apenasCorrelacionadas) {
                params.append('apenasCorrelacionadas', 'true');
            }
            
            const response = await fetch(`/MuralhaDigital/VeiculosCorrelacionados?${params}`);
            
            if (!response.ok) {
                throw new Error('Erro ao buscar passagens');
            }
            
            dados = await response.json();
            passagensCache.set(cacheKey, dados);
        }
        
        corpo.innerHTML = '';
        
        let passagens = dados.passagens || [];
        const totalRegistros = dados.totalRegistros || 0;
        passagensTotalPaginas = dados.totalPaginas || 1;
        passagensTotalRegistros = totalRegistros;
        
        badge.textContent = `${totalRegistros} passagen${totalRegistros !== 1 ? 's' : ''}`;
        
        if (passagens.length === 0) {
            placeholder.style.display = 'block';
            renderizarPaginacaoPassagens();
            return;
        }
        
        placeholder.style.display = 'none';
        
        const tamanhoPagina = 10;
        const offset = (pagina - 1) * tamanhoPagina;
        const itensRestantes = totalRegistros - offset;
        
        if (passagens.length > itensRestantes) {
            passagens = passagens.slice(0, itensRestantes);
        }
        
        passagens.forEach((pass, idx) => {
            const indice = offset + idx + 1;
            const dataHora = pass.dataHora ? formatarDataHora(pass.dataHora) : '-';
            const idLocal = pass.idLocal || '';
            const nomeLocal = pass.nomeLocal || 'Local desconhecido';
            const localExibicao = idLocal ? `${idLocal} - ${nomeLocal}` : nomeLocal;
            const ehCorrelacionada = pass.correlacionada === true;
            let badgeCorrelacao = '';
            if (ehCorrelacionada && pass.placasCorrelacionadas) {
                const placas = pass.placasCorrelacionadas.split(',');
                badgeCorrelacao = ' ' + placas.map(p => {
                    const corIdx = mapaCorPlacaAlvo.get(p.trim()) ?? 0;
                    return `<span class="dot-correlacao cor-placa-${corIdx}" title="${p.trim()}">⬤</span>`;
                }).join(' ');
            }
            
            const placaAtualPassagem = (passagensPlacaAtual === 'SEM_PLACA' || !passagensPlacaAtual) ? '' : passagensPlacaAtual;
            corpo.innerHTML += `
                <tr data-passagem-id="${pass.passagemId || ''}">
                    <td class="text-center">${indice}${badgeCorrelacao}</td>
                    <td>${localExibicao}</td>
                    <td class="text-center">${dataHora}</td>
                    <td class="text-center"><button class="btn-corrigir-placa btn btn-sm btn-info" data-passagem-id="${pass.passagemId || ''}" data-placa-atual="${placaAtualPassagem}" title="Editar placa deste registro">Editar</button></td>
                </tr>`;
        });
        
        const infoPaginacao = document.getElementById('passagens-info-paginacao');
        if (infoPaginacao) {
            const inicio = passagens.length > 0 ? offset + 1 : 0;
            const fim = offset + passagens.length;
            infoPaginacao.textContent = `Exibindo ${inicio}-${fim} de ${totalRegistros} itens`;
        }
        
        renderizarPaginacaoPassagens();
        
    } catch (error) {
        console.error('Erro ao carregar passagens:', error);
        corpo.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Erro ao carregar passagens</td></tr>';
    }
}

/**
 * Renderiza os botões de paginação no formato: << < Página X de Y > >>
 */
function renderizarPaginacaoPassagens() {
    const container = document.getElementById('passagens-paginacao-container');
    if (!container) return;
    
    const totalPaginas = passagensTotalPaginas;
    const paginaAtual = passagensPaginaAtual;
    
    if (totalPaginas <= 1) {
        container.innerHTML = '<span class="text-muted small">Página 1 de 1</span>';
        return;
    }
    
    let html = '';
    
    html += `<button class="btn btn-outline-secondary btn-sm" ${paginaAtual === 1 ? 'disabled' : ''} onclick="navegarPaginaPassagens(1)" title="Primeira página">&laquo;&laquo;</button> `;
    html += `<button class="btn btn-outline-secondary btn-sm" ${paginaAtual === 1 ? 'disabled' : ''} onclick="navegarPaginaPassagens(${paginaAtual - 1})" title="Página anterior">&laquo;</button> `;
    
    html += `<span class="fw-bold mx-2">Página ${paginaAtual} de ${totalPaginas}</span>`;
    
    html += ` <button class="btn btn-outline-secondary btn-sm" ${paginaAtual === totalPaginas ? 'disabled' : ''} onclick="navegarPaginaPassagens(${paginaAtual + 1})" title="Próxima página">&raquo;</button>`;
    html += ` <button class="btn btn-outline-secondary btn-sm" ${paginaAtual === totalPaginas ? 'disabled' : ''} onclick="navegarPaginaPassagens(${totalPaginas})" title="Última página">&raquo;&raquo;</button>`;
    
    container.innerHTML = html;
}

/**
 * Navega para uma página específica de passagens.
 * @param {number} pagina - Número da página.
 */
function navegarPaginaPassagens(pagina) {
    if (pagina < 1 || pagina > passagensTotalPaginas) return;
    if (pagina === passagensPaginaAtual) return;
    if (!passagensPlacaAtual) return;
    
    carregarPassagensPaginadasAsync(passagensPlacaAtual, pagina);
}

/**
 * Formata data/hora ISO para exibição.
 * @param {string} dataHoraISO - Data/hora em formato ISO.
 * @returns {string} Data/hora formatada com segundos.
 */
function formatarDataHora(dataHoraISO) {
    if (!dataHoraISO) return '-';
    try {
        const dt = new Date(dataHoraISO);
        const dia = String(dt.getDate()).padStart(2, '0');
        const mes = String(dt.getMonth() + 1).padStart(2, '0');
        const ano = dt.getFullYear();
        const hora = String(dt.getHours()).padStart(2, '0');
        const min = String(dt.getMinutes()).padStart(2, '0');
        const seg = String(dt.getSeconds()).padStart(2, '0');
        return `${dia}/${mes}/${ano} ${hora}:${min}:${seg}`;
    } catch (e) {
        return dataHoraISO;
    }
}

async function preencherAbaProprietarioAsync(info = {}) {
    document.getElementById('modal-nome-proprietario').textContent = info.nome || '-';
    document.getElementById('modal-sobrenome-proprietario').textContent = info.sobrenome || '';
    document.getElementById('modal-cpf-proprietario').textContent = info.cpf || '-';
    document.getElementById('modal-data-nascimento-proprietario').textContent = info.dataNascimento || '-';
    document.getElementById('modal-endereco-proprietario').textContent = info.endereco || '-';
    document.getElementById('modal-telefone-proprietario').textContent = info.telefone || '-';
    document.getElementById('modal-email-proprietario').textContent = info.email || '-';
    await preencherAntecedentesCriminaisAsync(info.AntecedentesCriminais);
}

async function preencherAntecedentesCriminaisAsync(antecedentes = []) {
    const corpo = document.getElementById('tabela-antecedentes-body');
    if (!corpo) return;
    corpo.innerHTML = '';
    if (!antecedentes || antecedentes.length === 0) {
        corpo.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Nenhum antecedente registrado.</td></tr>`;
        return;
    }
    antecedentes.forEach(item => {
        corpo.innerHTML += `
            <tr>
                <td>${item.TipoCrime || '-'}</td>
                <td>${item.DataOcorrencia || '-'}</td>
                <td>${item.LocalOcorrencia || '-'}</td>
                <td>${item.Descricao || '-'}</td>
                <td>${item.Sentenca || '-'}</td>
            </tr>`;
    });
}

async function inicializarPaginacaoAsync(tipo, dados, renderFunction) {
    const state = paginacaoState[tipo];
    state.atuais = dados || [];
    state.paginaAtual = 1;
    await configurarPaginacaoAsync(tipo, renderFunction);
    await renderFunction();
}

/**
 * Configura os botões de paginação.
 * @param {string} tipo - 'alertas', 'boletim', etc.
 * @param {Function} renderFunction - A função que renderiza a tabela.
 */
async function configurarPaginacaoAsync(tipo, renderFunction) {
    const state = paginacaoState[tipo];
    let prefixo;
    switch (tipo) {
        case 'boletim':
            prefixo = 'boletim-pagina-';
            break;
        case 'alertas':
        default:
            prefixo = 'pagina-';
            break;
    }
    
    const btnAnterior = document.getElementById(`${prefixo}anterior`);
    const btnProxima = document.getElementById(`${prefixo}proxima`);
    
    if (btnAnterior) {
        btnAnterior.onclick = () => {
            if (state.paginaAtual > 1) {
                state.paginaAtual--;
                renderFunction();
            }
        };
    }
    
    if (btnProxima) {
        btnProxima.onclick = () => {
            const totalPaginas = Math.ceil((state.atuais || []).length / state.porPagina);
            if (state.paginaAtual < totalPaginas) {
                state.paginaAtual++;
                renderFunction();
            }
        };
    }
}

/**
 * Renderiza a tabela de alertas.
 */
async function renderizarTabelaAlertasAsync() {
    const state = paginacaoState.alertas;
    const corpoTabela = document.getElementById('tabela-alertas-body');
    const getRowHtml = (alerta) => {
        const link = `${baseUrl}/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=${alerta.alertaId}`;
        return `
            <td>${alerta.data || '-'}</td>
            <td>${alerta.tipo || '-'}</td>
            <td>${alerta.status || '-'}</td>
            <td>${alerta.origem || '-'}</td>
            <td><button type="button" class="btn btn-sm btn-primary btn-open-link-modal" data-url="${link}">Ver mais</button></td>
        `;
    };
    await renderizarTabelaGenericaAsync(corpoTabela, state, getRowHtml, 5, 'Nenhum alerta disponível');
    await atualizarContadoresPaginacaoAsync('alertas');
}

/**
 * Renderiza a tabela de boletins de ocorrência.
 */
async function renderizarTabelaBoletimAsync() {
    const state = paginacaoState.boletim;
    const corpoTabela = document.getElementById('tabela-boletim-body');
    
    atualizarBadgesRegistroFato();
    
    const temAlertas = paginacaoState.alertas.atuais && paginacaoState.alertas.atuais.length > 0;

    const getRowHtml = (boletim) => {
        const placa = document.getElementById('modal-placa').textContent;
        let botaoVerAlerta = '';

        const buttonText = boletim.temBoletim === 1 ? 'Ver B.O.' : 'Ver Registro';

        if (temAlertas && placa) {
            botaoVerAlerta = `<button type="button" class="btn btn-sm btn-info btn-open-link-modal" data-url="${baseUrl}/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?placa=${placa}">Ver Alerta</button>`;
        }

        return `
            <td>${boletim.data || '-'}</td>
            <td>${boletim.tipoOcorrencia || '-'}</td>
            <td>${boletim.situacaoAtual || '-'}</td>
            <td>${boletim.descricao || '-'}</td>
            <td class="text-center">
                <button type="button" class="btn btn-sm btn-primary" onclick="abrirRegistroComLoading(this, ${boletim.registroFatoId})">${buttonText}</button>
            </td>
            <td class="text-center">${botaoVerAlerta}</td>
        `;
    };
    
    await renderizarTabelaGenericaAsync(corpoTabela, state, getRowHtml, 6, 'Nenhum boletim de ocorrência encontrado.');
    await atualizarContadoresPaginacaoAsync('boletim');
}

async function renderizarTabelaGenericaAsync(corpoTabela, state, getRowHtml, colSpan, noDataMessage) {
    if (!corpoTabela) return;
    corpoTabela.innerHTML = '';
    if (!state.atuais || state.atuais.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="${colSpan}" class="text-center text-muted">${noDataMessage}</td></tr>`;
        return;
    }
    const inicio = (state.paginaAtual - 1) * state.porPagina;
    const fim = inicio + state.porPagina;
    const pagina = state.atuais.slice(inicio, fim);
    pagina.forEach(item => {
        const linha = document.createElement('tr');
        linha.innerHTML = getRowHtml(item);
        corpoTabela.appendChild(linha);
    });
}

async function atualizarContadoresPaginacaoAsync(tipo) {
    const state = paginacaoState[tipo];
    let paginaAtualEl, totalPaginasEl, btnAnterior, btnProxima;

    if (tipo === 'boletim') {
        paginaAtualEl = document.getElementById('boletim-pagina-atual');
        totalPaginasEl = document.getElementById('boletim-total-paginas');
        btnAnterior = document.getElementById('boletim-pagina-anterior');
        btnProxima = document.getElementById('boletim-pagina-proxima');
    } else if (tipo === 'alertas') {
        paginaAtualEl = document.getElementById('pagina-atual');
        totalPaginasEl = document.getElementById('total-paginas');
        btnAnterior = document.getElementById('pagina-anterior');
        btnProxima = document.getElementById('pagina-proxima');
    } 
    else {
        return;
    }
    
    const totalPaginas = Math.ceil((state.atuais?.length || 0) / state.porPagina) || 1;

    if (paginaAtualEl) paginaAtualEl.textContent = state.paginaAtual;
    if (totalPaginasEl) totalPaginasEl.textContent = totalPaginas;
    if (btnAnterior) btnAnterior.disabled = state.paginaAtual === 1;
    if (btnProxima) btnProxima.disabled = state.paginaAtual >= totalPaginas;
}

/**
 * Atualiza a posição dos ícones flutuantes.
 * @param {Object} node - O nó do grafo.
 */
async function atualizarPosicaoIconesAsync(node) {
    if (!node._customIcons) return;
    const pos = node.renderedPosition();
    const boundingRect = document.getElementById('grafo').getBoundingClientRect();
    const centerX = boundingRect.left + pos.x;
    const centerY = boundingRect.top + pos.y;
    const radius = 70;
    const total = node._customIcons.length;
    node._customIcons.forEach(({ element }, index) => {
        const angle = (2 * Math.PI / total) * index - (Math.PI / 2);
        const offsetX = radius * Math.cos(angle);
        const offsetY = radius * Math.sin(angle);
        element.style.position = 'fixed';
        element.style.left = `${centerX + offsetX}px`;
        element.style.top = `${centerY + offsetY}px`;
        element.style.zIndex = 1055;
    });
}

/**
 * Remove todos os ícones flutuantes.
 */
async function removerIconesFlutuantesAsync() {
    document.querySelectorAll('.grafo-action-icon').forEach(e => e.remove());
    if (cy) {
        cy.nodes().forEach(n => delete n._customIcons);
    }
}

/**
 * Flag que indica se o modal está em estado de loading.
 */
let modalLoadingAtivo = false;

/**
 * Converte data (yyyy-MM-dd) e hora (HH:mm) nativos para o formato do consulta-veiculo (dd/MM/yyyy HH:mm).
 * @param {string} dataValue - Valor no formato yyyy-MM-dd.
 * @param {string} horaValue - Valor no formato HH:mm.
 * @returns {string|null} Data formatada ou null se vazio.
 */
function converterDataParaConsulta(dataValue, horaValue) {
    if (!dataValue) { return null; }
    const partes = dataValue.split('-');
    const hora = horaValue || '00:00';
    return `${partes[2]}/${partes[1]}/${partes[0]} ${hora}`;
}

/**
 * Envia a correção de placa para o backend e recarrega o grafo.
 * @param {string} passagemId - ID do registro veiculo_tempo_real.
 * @param {string} placaAtual - Placa atual (pode ser vazia para SEM_PLACA).
 * @param {string} novaPlaca - Nova placa digitada pelo usuário.
 */
async function corrigirPlacaPassagem(passagemId, placaAtual, novaPlaca) {
    if (!passagemId) {
        alert('ID da passagem não encontrado.');
        return;
    }
    try {
        const idUsuario = (typeof usuarioID !== 'undefined') ? usuarioID : '';
        const payload = {
            loteCompleto: [{
                placaAtual: placaAtual || '',
                novaPlaca: novaPlaca,
                idVeiculo: passagemId,
                idUsuario: String(idUsuario)
            }]
        };
        const resp = await fetch('/MuralhaDigital/CorrecaoPlaca?acao=atualizarPlacas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const resultado = await resp.json();
        if (!resp.ok || resultado.status === 'erro') {
            throw new Error(resultado.mensagem || 'Erro ao salvar');
        }
        passagensCache.clear();
        detalhesVeiculoCache.clear();
        mostrarToastAsync(`Placa corrigida para ${novaPlaca} com sucesso.`, 'sucesso');
        if (typeof recarregarGrafo === 'function') { recarregarGrafo(); }
        if (passagensPlacaAtual) {
            await carregarPassagensPaginadasAsync(passagensPlacaAtual, passagensPaginaAtual);
        }
    } catch (err) {
        console.error('Erro ao corrigir placa:', err);
        alert('Erro ao corrigir placa: ' + err.message);
        if (passagensPlacaAtual) {
            carregarPassagensPaginadasAsync(passagensPlacaAtual, passagensPaginaAtual);
        }
    }
}

/**
 * Converte data ISO (yyyy-MM-ddTHH:mm:ss) para o formato do consulta-veiculo (dd/MM/yyyy HH:mm).
 * @param {string} dataHoraISO - Data/hora em formato ISO.
 * @returns {string|null} Data formatada ou null se inválida.
 */
function converterISOParaConsulta(dataHoraISO) {
    if (!dataHoraISO) { return null; }
    try {
        const dt = new Date(dataHoraISO);
        const dia = String(dt.getDate()).padStart(2, '0');
        const mes = String(dt.getMonth() + 1).padStart(2, '0');
        const ano = dt.getFullYear();
        const hora = String(dt.getHours()).padStart(2, '0');
        const min = String(dt.getMinutes()).padStart(2, '0');
        return `${dia}/${mes}/${ano} ${hora}:${min}`;
    } catch (e) {
        return null;
    }
}

/**
 * Aguarda o iframe concluir a pesquisa monitorando REQUEST_DB do iframe.
 * REQUEST_DB é definido quando o AJAX de pesquisa inicia e volta a null no complete.
 * Isso evita falso-positivo do body.loading que é removido pela inicialização da página (~1100ms)
 * independentemente do AJAX de pesquisa ter terminado.
 * @param {Window} iframeWin - Referência ao window do iframe.
 */
function aguardarFimPesquisaIframe(iframeWin) {
    var fase = 0; // 0: aguarda REQUEST_DB ser atribuído; 1: aguarda REQUEST_DB voltar a null
    var tentativas = 0;
    var maxTentativasFase0 = 15;  // 3s para o AJAX iniciar
    var maxTentativasFase1 = 200; // 40s para o AJAX concluir
    setTimeout(function() {
        var intervalo = setInterval(function() {
            tentativas++;
            try {
                var requestAtivo = iframeWin.REQUEST_DB;
                if (fase === 0) {
                    if (requestAtivo !== null && requestAtivo !== undefined) {
                        fase = 1;
                        tentativas = 0;
                    } else if (tentativas >= maxTentativasFase0) {
                        clearInterval(intervalo);
                        hideModalLoading();
                    }
                } else {
                    if (requestAtivo === null || requestAtivo === undefined) {
                        clearInterval(intervalo);
                        hideModalLoading();
                    } else if (tentativas >= maxTentativasFase1) {
                        clearInterval(intervalo);
                        hideModalLoading();
                    }
                }
            } catch (e) {
                clearInterval(intervalo);
                hideModalLoading();
            }
        }, 200);
    }, 300);
}

/**
 * Exibe o overlay de loading sobre o modal, ofuscando o conteúdo.
 * Bloqueia o fechamento do modal (backdrop, botão X e ESC).
 */
function showModalLoading() {
    modalLoadingAtivo = true;
    const overlay = document.getElementById('modal-loading-overlay');
    if (overlay) { overlay.style.display = 'flex'; }
}

/**
 * Esconde o overlay de loading do modal.
 * Libera o fechamento do modal novamente.
 */
function hideModalLoading() {
    modalLoadingAtivo = false;
    const overlay = document.getElementById('modal-loading-overlay');
    if (overlay) { overlay.style.display = 'none'; }
}

function configurarBotoesFooterModal() {
    const btnPerfil = document.getElementById('btn-perfil-comportamental');
    if (btnPerfil) {
        btnPerfil.onclick = () => {
            const placa = document.getElementById('modal-placa').textContent;
            const intervalo = document.getElementById('intervalo')?.value || '30';
            if (placa && placa.trim() !== '-') {
                showModalLoading();
                window.open(`/muralha-digital/pages/perfil-comportamental/perfil-comportamental.jsp?placa=${encodeURIComponent(placa)}&intervalo=${intervalo}`, '_blank');
                setTimeout(hideModalLoading, 1500);
            } else {
                mostrarToastAsync('Placa não disponível para consulta.', 'alerta');
            }
        };
    }

    const btnConsultar = document.getElementById('btn-consultar-veiculo');
    if (btnConsultar) {
        btnConsultar.onclick = () => {
            const placa = document.getElementById('modal-placa').textContent?.trim() || '';
            const dataIni = document.getElementById('txt_data_inicio').value;
            const horaIni = document.getElementById('txt_hora_inicio').value;
            const dataFim = document.getElementById('txt_data_fim').value;
            const horaFim = document.getElementById('txt_hora_fim').value;
            const dataInicioConsulta = converterDataParaConsulta(dataIni, horaIni);
            const dataFimConsulta = converterDataParaConsulta(dataFim, horaFim);
            const placaValida = placa && placa !== '-' && placa !== 'SEM_PLACA' && !placa.toLowerCase().startsWith('sem');
            showModalLoading();
            const novaAba = window.open('/muralha-digital/pages/consulta-veiculo/consulta.jsp', '_blank');
            var tentativas = 0;
            var check = setInterval(function() {
                tentativas++;
                try {
                    // Aguarda o $().ready() da nova aba terminar completamente:
                    // o ready remove body.loading após sleep(300)+setTimeout(800) ≈ 1100ms.
                    // Verificamos que: jQuery carregou, a função existe E o body não tem mais
                    // a classe 'loading' (indica que o ready + TempusDominus já inicializaram).
                    var bodyPronto = novaAba.$
                        && typeof novaAba.executaPesquisa === 'function'
                        && !novaAba.$('body').hasClass('loading');
                    if (bodyPronto) {
                        clearInterval(check);
                        novaAba.$('body').addClass('loading');
                        if (placaValida) {
                            var placaInput = novaAba.document.getElementById('placaConsulta');
                            if (placaInput) { placaInput.value = placa; }
                        }
                        if (dataInicioConsulta) {
                            var elDI = novaAba.document.getElementById('dataInicioInput');
                            if (elDI) { elDI.value = dataInicioConsulta; }
                        }
                        if (dataFimConsulta) {
                            var elDF = novaAba.document.getElementById('dataFimInput');
                            if (elDF) { elDF.value = dataFimConsulta; }
                        }
                        novaAba.executaPesquisa(true);
                        hideModalLoading();
                    } else if (tentativas >= 75) {
                        clearInterval(check);
                        hideModalLoading();
                    }
                } catch (e) {
                    clearInterval(check);
                    hideModalLoading();
                }
            }, 200);
        };
    }

    const btnTodosBoletins = document.getElementById('btn-todos-boletins');
    if(btnTodosBoletins) {
        btnTodosBoletins.onclick = () => {
            const placa = document.getElementById('modal-placa').textContent;
            if (placa && placa.trim() !== '-') {
                showModalLoading();
                window.open(`/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?placa=${encodeURIComponent(placa)}`, '_blank');
                setTimeout(hideModalLoading, 1500);
            }
        };
    }
}

/**
 * Abre o modal de Registro de Fato com loading visual no botão.
 * @param {HTMLElement} btn - Botão que disparou a ação.
 * @param {number} registroFatoId - ID do registro de fato.
 */
async function abrirRegistroComLoading(btn, registroFatoId) {
    showModalLoading();
    try {
        await window.preencherEExibirModalBoletim(registroFatoId);
    } catch (e) {
        console.error('[grafo-modal.js] Erro ao abrir registro de fato:', e);
        mostrarToastAsync('Erro ao abrir o registro de fato.', 'erro');
    } finally {
        hideModalLoading();
    }
}

document.getElementById('btn-remover-veiculo')?.addEventListener('click', async () => {
    const btnRemover = document.getElementById('btn-remover-veiculo');
    const placa = btnRemover.getAttribute('data-placa');
    showModalLoading();
    try {
        if (placa && cy) {
            const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalInfoVeiculo'));
            if (modalInstance) { modalInstance.hide(); }
            cy.getElementById(placa).remove();
        }
        await removerIconesFlutuantesAsync();
        await aplicarTodosOsFiltrosAsync();
    } finally {
        hideModalLoading();
    }
});

/**
 * Recarrega as passagens do backend ao alternar o filtro de correlacionadas.
 */
function filtrarPassagensCorrelacionadas() {
    if (passagensPlacaAtual) {
        carregarPassagensPaginadasAsync(passagensPlacaAtual, 1);
    }
}

/**
 * Atualiza os badges de Produto/Objeto e Recuperação na aba de Registro de Fato.
 * Lê o campo registro_fato do nó do grafo correspondente à placa atual.
 */
function atualizarBadgesRegistroFato() {
    const container = document.getElementById('badges-registro-fato');
    if (!container) return;

    const placa = document.getElementById('modal-placa')?.textContent;
    if (!placa || placa === '-' || placa === 'SEM PLACA' || !cy) {
        container.style.display = 'none';
        container.innerHTML = '';
        return;
    }

    const node = cy.getElementById(placa);
    const registroFato = node && node.length > 0 ? node.data('registro_fato') || '' : '';

    if (!registroFato || registroFato === 'Sem registro') {
        container.style.display = 'none';
        container.innerHTML = '';
        return;
    }

    const tipos = registroFato.split(',').map(t => t.trim());
    const temProdutoObjeto = tipos.some(t => t.toLowerCase().includes('produto') || t.toLowerCase().includes('objeto'));
    const temRecuperacao = tipos.some(t => t.toLowerCase().includes('recupera'));

    if (!temProdutoObjeto && !temRecuperacao) {
        container.style.display = 'none';
        container.innerHTML = '';
        return;
    }

    let html = '';
    if (temProdutoObjeto) {
        html += '<span class="badge bg-warning text-dark me-1"><i class="bi bi-exclamation-triangle-fill"></i> Produto/Objeto</span>';
    }
    if (temRecuperacao) {
        html += '<span class="badge bg-success me-1"><i class="bi bi-shield-fill-check"></i> Recuperação</span>';
    }

    container.innerHTML = html;
    container.style.display = 'block';
}

/**
 * Lógica para o modal de links.
 */
document.addEventListener('DOMContentLoaded', () => {
    const linkModal = document.getElementById('link-modal');
    const linkModalClose = document.getElementById('link-modal-close');
    const linkModalIframe = document.getElementById('link-modal-iframe');
    /**
     * Abre o modal de link (iframe) e exibe loading no botão clicado.
     * @param {string} url - URL a ser carregada no iframe.
     * @param {HTMLElement|null} btn - Botão que disparou a ação.
     */
    const openLinkModal = (url) => {
        if (url) {
            showModalLoading();
            linkModalIframe.onload = () => {
                hideModalLoading();
            };
            linkModalIframe.src = url;
            linkModal.style.display = 'flex';
        }
    };
    const closeLinkModal = () => {
        linkModal.style.display = 'none';
        linkModalIframe.onload = null;
        modalLoadingAtivo = false;
        hideModalLoading();
        setTimeout(() => { linkModalIframe.src = 'about:blank'; }, 0);
    };
    document.body.addEventListener('click', (event) => {
        const btn = event.target.closest('.btn-open-link-modal');
        if (btn) {
            openLinkModal(btn.getAttribute('data-url'));
        }
    });
    linkModalClose?.addEventListener('click', closeLinkModal);
    linkModal?.addEventListener('click', (event) => {
        if (event.target === linkModal) {
            closeLinkModal();
        }
    });
    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && linkModal.style.display !== 'none') {
            closeLinkModal();
        }
    });

    const tabelaPassagensBody = document.getElementById('tabela-passagens-body');
    if (tabelaPassagensBody) {
        tabelaPassagensBody.addEventListener('click', (event) => {
            const btnCorrigir = event.target.closest('.btn-corrigir-placa');
            if (!btnCorrigir) return;

            const passagemId = btnCorrigir.dataset.passagemId;
            const placaAtual = btnCorrigir.dataset.placaAtual || '';

            const labelAtual = document.getElementById('corrigir-placa-atual-label');
            const inputNova = document.getElementById('corrigir-nova-placa-input');
            const erroDiv = document.getElementById('corrigir-placa-erro');
            const btnConfirmar = document.getElementById('btn-confirmar-correcao-placa');

            if (labelAtual) { labelAtual.textContent = placaAtual || 'SEM PLACA'; }
            if (inputNova) { inputNova.value = placaAtual; }
            if (erroDiv) { erroDiv.style.display = 'none'; }

            btnConfirmar.onclick = null;
            btnConfirmar.onclick = () => {
                const novaPlaca = inputNova ? inputNova.value.trim().toUpperCase() : '';
                if (novaPlaca.length > 0 && novaPlaca.length < 7) {
                    if (erroDiv) { erroDiv.style.display = 'block'; }
                    inputNova?.focus();
                    return;
                }
                if (erroDiv) { erroDiv.style.display = 'none'; }
                const bsModal = bootstrap.Modal.getInstance(document.getElementById('modalCorrigirPlaca'));
                if (bsModal) { bsModal.hide(); }
                if (novaPlaca === placaAtual.toUpperCase()) { return; }
                corrigirPlacaPassagem(passagemId, placaAtual, novaPlaca);
            };

            const modalEl = document.getElementById('modalCorrigirPlaca');
            const bsModal = new bootstrap.Modal(modalEl);
            const modalPai = document.getElementById('modalInfoVeiculo');
            modalEl.addEventListener('show.bs.modal', () => { if (modalPai) { modalPai.classList.add('modal-dimmed'); } }, { once: true });
            modalEl.addEventListener('hidden.bs.modal', () => { if (modalPai) { modalPai.classList.remove('modal-dimmed'); } }, { once: true });
            bsModal.show();
            modalEl.addEventListener('shown.bs.modal', () => { inputNova?.select(); }, { once: true });
        });
    }

    const chkCorrelacionadas = document.getElementById('chk-apenas-correlacionadas');
    if (chkCorrelacionadas) {
        chkCorrelacionadas.addEventListener('change', filtrarPassagensCorrelacionadas);
    }

    /**
     * Recebe evento de placa alterada via consulta-veiculo (iframe).
     * Invalida caches, fecha modais e recarrega o grafo.
     */
    window.addEventListener('message', (event) => {
        if (event.origin !== window.location.origin) { return; }
        if (event.data && event.data.type === 'placaAlterada') {
            const { placaAntiga } = event.data;
            detalhesVeiculoCache.delete(placaAntiga);
            for (const chave of passagensCache.keys()) {
                if (chave.startsWith(placaAntiga + '|')) {
                    passagensCache.delete(chave);
                }
            }
            const linkModal = document.getElementById('link-modal');
            const linkModalIframe = document.getElementById('link-modal-iframe');
            if (linkModal) { linkModal.style.display = 'none'; }
            if (linkModalIframe) { linkModalIframe.src = 'about:blank'; }
            const modalEl = document.getElementById('modalInfoVeiculo');
            if (modalEl) {
                const bsModal = bootstrap.Modal.getInstance(modalEl);
                if (bsModal) { bsModal.hide(); }
            }
            if (typeof recarregarGrafo === 'function') { recarregarGrafo(); }
            mostrarToastAsync(`Placa alterada de ${placaAntiga} para ${event.data.placaNova}.`, 'sucesso');
        }
    });

    /**
     * Bloqueia o fechamento do modal enquanto o loading estiver ativo.
     */
    const modalInfoVeiculo = document.getElementById('modalInfoVeiculo');
    if (modalInfoVeiculo) {
        modalInfoVeiculo.addEventListener('hide.bs.modal', (event) => {
            if (modalLoadingAtivo) {
                event.preventDefault();
                event.stopImmediatePropagation();
            }
        });
    }

    /**
     * Garante remoção de backdrops órfãos ao fechar qualquer modal Bootstrap.
     */
    document.addEventListener('hidden.bs.modal', () => {
        const modaisAbertos = document.querySelectorAll('.modal.show');
        if (modaisAbertos.length === 0) {
            document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
            document.body.classList.remove('modal-open');
            document.body.style.removeProperty('overflow');
            document.body.style.removeProperty('padding-right');
        }
    });
});