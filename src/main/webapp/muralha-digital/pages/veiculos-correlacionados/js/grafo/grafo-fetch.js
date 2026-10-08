/**
 * Determina a incidência mais forte entre duas correlações.
 * Ordem de prioridade: A (Alta) > M (Média) > F (Fraca)
 *
 * @param {string|null} incidencia1 - Primeira incidência (A, M ou F)
 * @param {string|null} incidencia2 - Segunda incidência (A, M ou F)
 * @returns {string} A incidência mais forte
 */
function obterIncidenciaMaisForte(incidencia1, incidencia2) {
    const prioridades = { 'A': 3, 'M': 2, 'F': 1 };
    
    const inc1 = (incidencia1 || 'F').toUpperCase();
    const inc2 = (incidencia2 || 'F').toUpperCase();
    
    const peso1 = prioridades[inc1] || 0;
    const peso2 = prioridades[inc2] || 0;
    
    return peso1 >= peso2 ? inc1 : inc2;
}

/**
 * Busca os dados de correlação e prepara os elementos para o grafo e os dados para os filtros.
 *
 * @param {object} filtros - Filtros com placas e data.
 * @returns {Promise<Object|null>} Um objeto com { elementos, dadosFiltros } ou null se não houver dados.
 */
async function fetchVeiculosAsync(filtros) {
    const dataHoraInicio = filtros.dataHoraInicio;
    const dataHoraFim = filtros.dataHoraFim;
    const placas = filtros.placas || [];
    const numMinPassagensCorrelacionadas = filtros.numMinPassagensCorrelacionadas;

    if (placas.length === 0) {
        return null;
    }

    atualizarProgressoLoading(placas.length);

    const resposta = await obterVeiculosCorrelacionadosAsync(dataHoraInicio, dataHoraFim, placas, numMinPassagensCorrelacionadas);

    if (!resposta || !resposta.veiculos || resposta.veiculos.length === 0) {
        return null;
    }

    const { veiculos, correlacoes, dadosFiltros } = resposta;
    const nos = [];
    const arestas = [];
    const veiculosMap = new Map();

    veiculos.forEach(veiculo => {
        veiculosMap.set(veiculo.placa || 'null', veiculo);
    });

    const nosMap = new Map();

    /**
     * Cria um nó a partir dos dados do veículo.
     * @param {Object} veiculo - Dados do veículo
     * @param {boolean} ehPlacaAlvo - Se é uma placa alvo buscada
     * @returns {Object} Nó formatado para o grafo
     */
    function criarNo(veiculo, ehPlacaAlvo) {
        const tipoVeiculoLimpo = veiculo.tipo_veiculo ? veiculo.tipo_veiculo.trim() : 'Não informado';

        let todasAsManchas = [];
        let todosOsPcls = [];
        let tempoTotalPermanencia = 0;

        try {
            if (veiculo.manchas && veiculo.manchas.length > 2) {
                const manchasArray = JSON.parse(veiculo.manchas);
                if (manchasArray && manchasArray.length > 0) {
                    todasAsManchas = manchasArray.map(m => (m.nome_area || '').trim()).filter(nome => nome);
                    todosOsPcls = manchasArray.flatMap(mancha =>
                        (mancha.locais || []).map(local => (local.nome_local || '').trim())
                    ).filter(nome => nome);
                    tempoTotalPermanencia = manchasArray.reduce((total, mancha) => total + (mancha.tempo_estadia_minutos || 0), 0);
                }
            }
        } catch (e) {
            console.error(`[grafo-fetch.js] Erro ao decodificar JSON de manchas para a placa ${veiculo.placa}:`, e);
        }

        const temAgravante = veiculo.alerta || veiculo.antecedentes || veiculo.boletim || veiculo.monitorado;
        const registroDeFato = veiculo.tipos_registro_fato || 'Sem registro';
        const tiposAlertaString = veiculo.tipos_alerta;
        let alertasArray = [];
        if (tiposAlertaString) {
            alertasArray = tiposAlertaString.split(',').map(alerta => alerta.trim()).filter(alerta => alerta);
        }
        if (alertasArray.length === 0) {
            alertasArray.push('Sem alerta');
        }

        const nodeId = veiculo.placa || 'null';

        return {
            data: {
                id: nodeId,
                label: !veiculo.placa ? 'Sem Placa' : veiculo.placa,
                basePlaca: ehPlacaAlvo ? "true" : "false",
                semPlaca: !veiculo.placa ? "true" : "false",
                has_agravante: temAgravante ? "true" : "false",
                tipo_veiculo: tipoVeiculoLimpo,
                manchas_array: todasAsManchas,
                pcls_array: [...new Set(todosOsPcls)],
                registro_fato: registroDeFato,
                alertas_array: alertasArray,
                tempo_permanencia: tempoTotalPermanencia,
                periodo_predominante: veiculo.periodo_predominante,
                sem_ocr_registro_fato: veiculo.sem_ocr_registro_fato ? "true" : "false"
            }
        };
    }

    placas.forEach(placaAlvo => {
        const veiculo = veiculosMap.get(placaAlvo);
        if (veiculo && !nosMap.has(placaAlvo)) {
            const node = criarNo(veiculo, true);
            node.data.correlacao = '';
            nosMap.set(placaAlvo, node);
            nos.push(node);
        }
    });

    Object.entries(correlacoes).forEach(([placaPrincipal, listaCorrelacoes]) => {
        listaCorrelacoes.forEach(correlacao => {
            const placaKey = correlacao.placa || 'null';
            const veiculo = veiculosMap.get(placaKey);

            if (!veiculo) {
                console.warn(`[grafo-fetch.js] Veículo não encontrado para placa: ${placaKey}`);
                return;
            }

            const incidenciaAtual = correlacao.incidencia || '';
            
            if (!nosMap.has(placaKey)) {
                const ehPlacaAlvo = placas.includes(placaKey);
                const node = criarNo(veiculo, ehPlacaAlvo);
                node.data.correlacao = incidenciaAtual;
                nosMap.set(placaKey, node);
                nos.push(node);
            } else {
                const nodeExistente = nosMap.get(placaKey);
                if (nodeExistente.data.correlacao !== '' && incidenciaAtual) {
                    nodeExistente.data.correlacao = obterIncidenciaMaisForte(
                        nodeExistente.data.correlacao, 
                        incidenciaAtual
                    );
                } else if (incidenciaAtual) {
                    nodeExistente.data.correlacao = incidenciaAtual;
                }
            }

            const nodeId = placaKey;
            
            if (nodeId !== placaPrincipal && correlacao.incidencia) {
                arestas.push({
                    data: {
                        source: placaPrincipal,
                        target: nodeId,
                        incidencia: correlacao.incidencia,
                        passagens: correlacao.passagens
                    }
                });
            }
        });
    });

    const placasAlvoIds = placas.filter(p => nosMap.has(p));
    
    placasAlvoIds.forEach(placa1 => {
        placasAlvoIds.forEach(placa2 => {
            if (placa1 < placa2) {
                const aresta1 = arestas.find(a => a.data.source === placa1 && a.data.target === placa2);
                const aresta2 = arestas.find(a => a.data.source === placa2 && a.data.target === placa1);
                
                if (aresta1 || aresta2) {
                    const incidenciaMaisForte = obterIncidenciaMaisForte(
                        aresta1 ? aresta1.data.incidencia : null, 
                        aresta2 ? aresta2.data.incidencia : null
                    );
                    
                    if (aresta1) {
                        aresta1.data.incidencia = incidenciaMaisForte;
                    }
                    if (aresta2) {
                        aresta2.data.incidencia = incidenciaMaisForte;
                    }
                }
            }
        });
    });

    placasAlvoIds.forEach(placa1 => {
        placasAlvoIds.forEach(placa2 => {
            if (placa1 < placa2) {
                const veiculos1 = new Set(arestas.filter(a => a.data.source === placa1).map(a => a.data.target));
                const veiculos2 = new Set(arestas.filter(a => a.data.source === placa2).map(a => a.data.target));
                
                const veiculosComuns = [...veiculos1].filter(v => veiculos2.has(v));
                
                veiculosComuns.forEach(veiculoComum => {
                    if (!placasAlvoIds.includes(veiculoComum)) {
                        const nodeComum = nosMap.get(veiculoComum);
                        if (nodeComum && nodeComum.data) {
                            nodeComum.data.veiculoComum = "true";
                        }
                    }
                });
            }
        });
    });

    const nosPrioritarios = nos.filter(n => {
        const temCorrelacao = arestas.some(a => a.data.target === n.data.id || a.data.source === n.data.id);
        return temCorrelacao || n.data.basePlaca === "true";
    });

    const nosSecundarios = nos.filter(n => {
        const temCorrelacao = arestas.some(a => a.data.target === n.data.id || a.data.source === n.data.id);
        return !temCorrelacao && n.data.basePlaca !== "true";
    });

    const elementosPrioritarios = [...nosPrioritarios, ...arestas];
    const elementosSecundarios = nosSecundarios;
    const elementos = [...nos, ...arestas];

    return { elementos, elementosPrioritarios, elementosSecundarios, dadosFiltros };
}