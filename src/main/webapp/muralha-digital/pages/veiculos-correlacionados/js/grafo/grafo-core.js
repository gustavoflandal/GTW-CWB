/**
 * Objeto principal do grafo gerenciado pelo Cytoscape.js
 */
let cy;

/**
 * Função para mostrar o overlay de loading global.
 * Reseta o texto de progresso ao exibir.
 */
function showLoadingOverlay() {
    const overlay = document.getElementById('loading-overlay');
    if (overlay) {
        overlay.style.display = 'flex';
    }
    const progressText = document.getElementById('loading-progress-text');
    if (progressText) {
        progressText.textContent = '';
    }
}

/**
 * Função para esconder o overlay de loading global.
 * Limpa o texto de progresso ao ocultar.
 */
function hideLoadingOverlay() {
    const overlay = document.getElementById('loading-overlay');
    if (overlay) {
        overlay.style.display = 'none';
    }
    const progressText = document.getElementById('loading-progress-text');
    if (progressText) {
        progressText.textContent = '';
    }
}

/**
 * Atualiza o texto de progresso no overlay de carregamento.
 *
 * @param {number} totalPlacas - Quantidade total de placas sendo processadas.
 */
function atualizarProgressoLoading(totalPlacas) {
    const progressText = document.getElementById('loading-progress-text');
    if (!progressText) {
        return;
    }
    if (totalPlacas > 1) {
        progressText.textContent = 'Processando ' + totalPlacas + ' placas em paralelo...';
    } else {
        progressText.textContent = 'Processando placa...';
    }
}


/**
 * Controla todo o fluxo de recarregamento do grafo.
 * Prioriza o carregamento dos nós com correlação antes dos isolados.
 * @returns {Promise<Object|null>} Retorna um objeto com { elementos, dadosFiltros } ou null.
 */
async function recarregarGrafo() {
    const filtros = await obterFiltrosAsync();
    if (!filtros) {
        return null;
    }

    showLoadingOverlay();

    try {
        if (cy) {
            cy.destroy();
        }

        const resultado = await fetchVeiculosAsync(filtros);

        if (!resultado || !resultado.elementos || resultado.elementos.length === 0) {
            await criarEConfigurarGrafoAsync([], [], 0);
            return null;
        }

        const elementosPrioritarios = resultado.elementosPrioritarios || [];
        const elementosSecundarios = resultado.elementosSecundarios || [];
        const totalElementos = Array.isArray(resultado.elementos) ? resultado.elementos.length : (elementosPrioritarios.length + elementosSecundarios.length);

        await criarEConfigurarGrafoAsync(elementosPrioritarios, elementosSecundarios, totalElementos);

        return resultado;

    } catch (error) {
        console.error("Ocorreu um erro ao recarregar o grafo:", error);
        mostrarToastAsync("Erro ao processar a análise. Verifique o console.", "erro");
        return null;
    } finally {
        hideLoadingOverlay();
    }
}


/**
 * Cria e configura a instância do grafo com carregamento priorizado e assíncrono.
 * @param {Array} elementosPrioritarios - Elementos com correlação (nós + arestas).
 * @param {Array} elementosSecundarios - Elementos sem correlação.
 * @param {number} totalElementos - Quantidade total retornada pelo backend.
 */
async function criarEConfigurarGrafoAsync(elementosPrioritarios = [], elementosSecundarios = [], totalElementos = 0) {
    const ZOOM_MIN = 0.2;
    const ZOOM_MAX = 5.0;
    const volumeConfig = obterConfiguracaoVolume(totalElementos);

    cy = cytoscape({
        container: document.getElementById("grafo"),
        elements: [],
        style: [
            {
                selector: "node",
                style: {
                    label: "data(label)",
                    "background-color": "#343a40",
                    color: "#ffffff",
                    "text-valign": "center",
                    "text-halign": "center",
                    "font-size": "12px",
                    width: 60,
                    height: 60,
                    "border-width": 2,
                    "border-color": "#343a40"
                },
            },
            {
                selector: 'node[basePlaca = "true"]',
                style: { 'background-color': '#0d6efd', 'border-color': '#0d6efd', 'border-width': 3, 'font-weight': 'bold' }
            },
            {
                selector: 'node[semPlaca = "true"]',
                style: {
                    'background-color': '#7a7a7a',
                    'border-color': '#7a7a7a',
                    'color': '#ffffff',
                    'font-size': '14px',
                    'text-wrap': 'wrap',
                    'text-max-width': '55px'
                }
            },
            {
                selector: 'node[has_agravante = "true"]',
                style: { 'border-color': '#dc3545', 'border-width': 4 }
            },
            { selector: 'edge[incidencia = "A"]', style: { "width": 5, "line-color": "#dc3545" } },
            { selector: 'edge[incidencia = "M"]', style: { "width": 3, "line-color": "#ffc107" } },
            { selector: 'edge[incidencia = "F"]', style: { "width": 2, "line-color": "#6c757d" } },
        ],
        layout: { name: 'preset' },
        minZoom: ZOOM_MIN,
        maxZoom: ZOOM_MAX,
        wheelSensitivity: 0.2,
        textureOnViewport: volumeConfig.textureOnViewport,
        hideEdgesOnViewport: volumeConfig.hideEdgesOnViewport,
        pixelRatio: volumeConfig.pixelRatio
    });

    configurarEventosGrafo();

    const podeAdicionarElemento = criarValidadorElemento();
    const prioridade = prepararElementosPrioritarios(elementosPrioritarios);

    if (prioridade.noBase.length > 0) {
        const baseNode = prioridade.noBase[0];
        if (podeAdicionarElemento(baseNode)) {
            cy.add(baseNode);
        }
    }

    const tamanhoLotePrioritario = volumeConfig.lotePrioritario;
    await adicionarElementosEmLotesAsync(prioridade.alta, tamanhoLotePrioritario, podeAdicionarElemento);
    await adicionarElementosEmLotesAsync(prioridade.media, tamanhoLotePrioritario, podeAdicionarElemento);
    await adicionarElementosEmLotesAsync(prioridade.baixa, tamanhoLotePrioritario, podeAdicionarElemento);

    await executarLayoutAsync(volumeConfig.layoutInicial);

    agendarCarregamentoSecundario(elementosSecundarios, volumeConfig, podeAdicionarElemento);

    if (typeof resetViewAsync !== 'undefined') {
        resetViewAsync();
    }
    
    if (typeof atualizarContadorPlacasCorrelacionadas !== 'undefined') {
        atualizarContadorPlacasCorrelacionadas();
    }
}

/**
 * Configura handlers de interação do grafo.
 * @returns {void}
 */
function configurarEventosGrafo() {
    cy.on('tap', (event) => {
        if (event.target === cy) {
            if (typeof removerIconesFlutuantesAsync !== 'undefined') {
                removerIconesFlutuantesAsync();
            }
        } else if (event.target.isNode && event.target.isNode()) {
            const node = event.target;
            const info = {
                id: node.id(),
                label: node.data('label'),
                basePlaca: node.data('basePlaca'),
                semPlaca: node.data('semPlaca'),
                correlacao: node.data('correlacao'),
                has_agravante: node.data('has_agravante'),
                passagens: node.data('passagens'),
                tipo_veiculo: node.data('tipo_veiculo'),
                manchas_array: node.data('manchas_array'),
                pcls_array: node.data('pcls_array'),
                registro_fato: node.data('registro_fato'),
                alertas_array: node.data('alertas_array'),
                tempo_permanencia: node.data('tempo_permanencia'),
                periodo_predominante: node.data('periodo_predominante')
            };
            if (typeof showModalInfoAsync !== 'undefined') {
                showModalInfoAsync(info);
            }
        }
    });

    cy.on('position', 'node', (e) => {
        if (e.target._customIcons && typeof atualizarPosicaoIconesAsync !== 'undefined') {
            atualizarPosicaoIconesAsync(e.target);
        }
    });

    cy.on('render', () => {
        cy.nodes().forEach(node => {
            if (node._customIcons && typeof atualizarPosicaoIconesAsync !== 'undefined') {
                atualizarPosicaoIconesAsync(node);
            }
        });
    });
}

/**
 * Verifica se o elemento informado representa uma aresta.
 * @param {Object} elemento - Elemento do grafo.
 * @returns {boolean} True quando o elemento é uma aresta.
 */
function ehAresta(elemento) {
    return Boolean(elemento && elemento.data && typeof elemento.data.source !== 'undefined' && typeof elemento.data.target !== 'undefined');
}

/**
 * Separa os elementos prioritários por nível (alta, média e baixa correlação).
 * @param {Array} elementos - Array com nós e arestas prioritários.
 * @returns {Object} Estrutura organizada por prioridade.
 */
function prepararElementosPrioritarios(elementos = []) {
    const nosMap = new Map();
    const arestasPorNivel = { A: [], M: [], F: [] };

    elementos.forEach(elemento => {
        if (ehAresta(elemento)) {
            const incidencia = elemento.data?.incidencia || '';
            if (incidencia === 'A' || incidencia === 'M' || incidencia === 'F') {
                arestasPorNivel[incidencia].push(elemento);
            }
            return;
        }

        if (elemento?.data?.id && !nosMap.has(elemento.data.id)) {
            nosMap.set(elemento.data.id, elemento);
        }
    });

    const noBase = [...nosMap.values()].find(n => n.data.basePlaca === 'true');
    const idsConsumidos = new Set(noBase ? [noBase.data.id] : []);

    function montarSequencia(sigla) {
        const arestas = arestasPorNivel[sigla] || [];
        if (arestas.length === 0) {
            return [];
        }

        const novosNos = [];
        arestas.forEach(aresta => {
            const sourceNode = nosMap.get(aresta.data.source);
            if (sourceNode && !idsConsumidos.has(sourceNode.data.id)) {
                novosNos.push(sourceNode);
                idsConsumidos.add(sourceNode.data.id);
            }

            const targetNode = nosMap.get(aresta.data.target);
            if (targetNode && !idsConsumidos.has(targetNode.data.id)) {
                novosNos.push(targetNode);
                idsConsumidos.add(targetNode.data.id);
            }
        });

        return [...novosNos, ...arestas];
    }

    return {
        noBase: noBase ? [noBase] : [],
        alta: montarSequencia('A'),
        media: montarSequencia('M'),
        baixa: montarSequencia('F')
    };
}

/**
 * Cria validador que evita adição duplicada de nós e arestas.
 * @returns {Function} Função que retorna true quando o elemento pode ser inserido.
 */
function criarValidadorElemento() {
    const idsRegistrados = new Set();
    return (elemento) => {
        if (!elemento || !elemento.data) {
            return false;
        }

        const chave = ehAresta(elemento)
            ? (elemento.data.id || `${elemento.data.source}_${elemento.data.target}_${elemento.data.incidencia || ''}`)
            : elemento.data.id;

        if (!chave) {
            return true;
        }

        if (idsRegistrados.has(chave)) {
            return false;
        }

        idsRegistrados.add(chave);
        return true;
    };
}

/**
 * Adiciona elementos em lotes usando requestAnimationFrame para evitar travamentos.
 * @param {Array} elementos - Elementos que serão inseridos.
 * @param {number} tamanhoLote - Quantidade de elementos por lote.
 * @param {Function} validarElemento - Função que decide se o elemento pode ser inserido.
 * @returns {Promise<void>} Promise resolvida após processar todos os elementos.
 */
async function adicionarElementosEmLotesAsync(elementos = [], tamanhoLote = 50, validarElemento) {
    if (!Array.isArray(elementos) || elementos.length === 0) {
        return;
    }

    await new Promise((resolve) => {
        let indice = 0;

        function processarLote() {
            if (indice >= elementos.length) {
                resolve();
                return;
            }

            const lote = elementos.slice(indice, indice + tamanhoLote);

            cy.batch(() => {
                lote.forEach(elemento => {
                    try {
                        if (!validarElemento || validarElemento(elemento)) {
                            cy.add(elemento);
                        }
                    } catch (erro) {
                        console.warn('[grafo-core] Falha ao adicionar elemento:', erro);
                    }
                });
            });

            indice += tamanhoLote;
            requestAnimationFrame(processarLote);
        }

        requestAnimationFrame(processarLote);
    });
}

/**
 * Define parâmetros específicos conforme o volume total de elementos.
 * @param {number} totalElementos - Quantidade total retornada pelo backend.
 * @returns {Object} Configuração de volume.
 */
function obterConfiguracaoVolume(totalElementos) {
    const EXTREMO = totalElementos >= 1800;
    const GRANDE = totalElementos >= 900;

    return {
        lotePrioritario: EXTREMO ? 30 : GRANDE ? 45 : 70,
        loteSecundario: EXTREMO ? 60 : GRANDE ? 90 : 120,
        delaySecundario: EXTREMO ? 700 : 400,
        layoutInicial: {
            name: 'cose',
            fit: true,
            padding: 40,
            animate: !EXTREMO,
            animationDuration: EXTREMO ? 200 : 400,
            animationEasing: 'ease-out',
            nodeRepulsion: EXTREMO ? 5000 : 9000,
            idealEdgeLength: EXTREMO ? 70 : 110,
            edgeElasticity: EXTREMO ? 60 : 100,
            numIter: EXTREMO ? 320 : 700,
            gravity: 1.2
        },
        layoutIncremental: {
            name: 'cose',
            fit: false,
            animate: false,
            padding: 20,
            nodeRepulsion: 4000,
            idealEdgeLength: 90,
            edgeElasticity: 80,
            numIter: 200,
            gravity: 1
        },
        textureOnViewport: GRANDE,
        hideEdgesOnViewport: false,
        pixelRatio: GRANDE ? 1 : 'auto'
    };
}

/**
 * Executa o layout informado aguardando o término para prosseguir.
 * @param {Object} layoutConfig - Configuração de layout do Cytoscape.
 * @returns {Promise<void>} Promise resolvida ao finalizar o layout.
 */
async function executarLayoutAsync(layoutConfig) {
    if (!layoutConfig || !cy) {
        return;
    }

    await new Promise((resolve) => {
        const layout = cy.layout(layoutConfig);
        layout.one('layoutstop', resolve);
        layout.run();
    });
}

/**
 * Agenda o carregamento assíncrono dos elementos sem correlação.
 * @param {Array} elementosSecundarios - Nós sem correlação direta.
 * @param {Object} volumeConfig - Configurações de volume.
 * @param {Function} validarElemento - Função para evitar duplicidade.
 * @returns {void}
 */
function agendarCarregamentoSecundario(elementosSecundarios = [], volumeConfig, validarElemento) {
    if (!Array.isArray(elementosSecundarios) || elementosSecundarios.length === 0) {
        return;
    }

    const delay = volumeConfig?.delaySecundario || 300;
    setTimeout(() => {
        adicionarElementosEmLotesAsync(elementosSecundarios, volumeConfig.loteSecundario, validarElemento)
            .then(() => executarLayoutAsync(volumeConfig.layoutIncremental))
            .then(() => {
                if (typeof atualizarContadorPlacasCorrelacionadas !== 'undefined') {
                    atualizarContadorPlacasCorrelacionadas();
                }
            })
            .catch(erro => console.error('[grafo-core] Erro ao carregar elementos secundários:', erro));
    }, delay);
}