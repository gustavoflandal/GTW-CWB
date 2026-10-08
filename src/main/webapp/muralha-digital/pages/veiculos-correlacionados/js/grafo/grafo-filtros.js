/**
 * Obtém os filtros aplicados pelo usuário a partir dos campos do formulário.
 * @returns {object|null} Filtros ou null se estiver inválido.
 */
async function obterFiltrosAsync() {
    const dataIni = document.getElementById("txt_data_inicio")?.value || "";
    const horaIni = document.getElementById("txt_hora_inicio")?.value || "";
    const dataFim = document.getElementById("txt_data_fim")?.value || "";
    const horaFim = document.getElementById("txt_hora_fim")?.value || "";

    const dataHoraInicio = (dataIni && horaIni) ? `${dataIni}T${horaIni}` : "";
    const dataHoraFim = (dataFim && horaFim) ? `${dataFim}T${horaFim}` : "";

    const placa1 = document.getElementById("txt_placa_1")?.value.trim().toUpperCase() || "";
    const placa2 = document.getElementById("txt_placa_2")?.value.trim().toUpperCase() || "";
    const placa3 = document.getElementById("txt_placa_3")?.value.trim().toUpperCase() || "";
    
    const placas = [placa1, placa2, placa3].filter(p => p);
    const numMinPassagensCorrelacionadasRaw = parseInt(document.getElementById("txt_min_correlacoes")?.value, 10);
    const numMinPassagensCorrelacionadas = (isNaN(numMinPassagensCorrelacionadasRaw) || numMinPassagensCorrelacionadasRaw < 1) ? "1" : String(numMinPassagensCorrelacionadasRaw);

    if (!dataHoraInicio || !dataHoraFim) {
        mostrarToastAsync("Preencha data para realizar a análise.", "erro");
        return null;
    }

    var dtInicio = criarDataLocal(dataIni);
    var dtFim = criarDataLocal(dataFim);
    const minCorrNum = parseInt(numMinPassagensCorrelacionadas, 10) || 0;
    const condA = minCorrNum === 1;
    const condB = minCorrNum === 2;
    const numPlacas = placas.length;
    const condC = minCorrNum === 3;

    if (condA) {
        const maxDias = numPlacas >= 3 ? 7 : numPlacas === 2 ? 14 : 21;
        const limiteCondA = new Date(dtInicio);
        limiteCondA.setDate(limiteCondA.getDate() + maxDias);
        if (dtFim > limiteCondA) {
            const labelDias = numPlacas >= 3 ? '1 semana' : numPlacas === 2 ? '2 semanas' : '3 semanas';
            mostrarToastAsync(`Com mínimo de 1 correlação, o período máximo é de ${labelDias} para ${numPlacas} placa(s). Ajuste as datas.`, "erro", 7000);
            return null;
        }
    } else if (condB) {
        const maxMeses = numPlacas >= 3 ? 1 : numPlacas === 2 ? 2 : 3;
        const limiteCondB = somarMesesCalendario(dtInicio, maxMeses);
        if (dtFim > limiteCondB) {
            const labelMeses = numPlacas >= 3 ? '1 mês' : numPlacas === 2 ? '2 meses' : '3 meses';
            mostrarToastAsync(`Com ${numPlacas} placa(s) e mínimo de 2 correlações, o período máximo é de ${labelMeses}. Ajuste as datas.`, "erro", 7000);
            return null;
        }
    } else if (condC) {
        const maxMeses = numPlacas >= 3 ? 3 : numPlacas === 2 ? 6 : 12;
        const limiteCondC = somarMesesCalendario(dtInicio, maxMeses);
        if (dtFim > limiteCondC) {
            const labelMeses = numPlacas >= 3 ? '3 meses' : numPlacas === 2 ? '6 meses' : '12 meses';
            mostrarToastAsync(`Com ${numPlacas} placa(s) e mínimo de 3 passagens correlacionadas, o período máximo é de ${labelMeses}. Ajuste as datas.`, "erro", 7000);
            return null;
        }
    } else {
        var limiteMaximo = somarMesesCalendario(dtInicio, 12);
        if (dtFim > limiteMaximo) {
            mostrarToastAsync("O intervalo entre as datas deve ser de no máximo 12 meses.", "erro", 5000);
            return null;
        }
    }

    if (placas.length === 0) {
        mostrarToastAsync("Preencha ao menos uma placa para realizar a análise.", "erro");
        return null;
    }

    return {
        dataHoraInicio,
        dataHoraFim,
        placas,
        numMinPassagensCorrelacionadas,
    };
}

/**
 * Mapeia o texto do nível de correlação para a sigla retornada pela SPU.
 * @param {string} texto - Texto do nível de correlação ('Alta', 'Média', 'Baixa').
 * @returns {string|null} Sigla correspondente ('A', 'M', 'F') ou null.
 */
function mapTextoParaSiglaIncidencia(texto) {
    const valor = texto?.toLowerCase();
    return valor === 'alta' ? 'A' :
            (valor === 'média' || valor === 'media') ? 'M' :
             valor === 'baixa' ? 'F' : null;
}

/**
 * Aplica TODOS os filtros (avançados e do cabeçalho) de uma só vez.
 * Esta função é a ÚNICA fonte de verdade para a visibilidade dos nós e arestas.
 */
async function aplicarTodosOsFiltrosAsync() {
    if (!cy) return;

    const alertaOcorrenciaValues = $('#filtro-alerta-ocorrencia').val() || [];
    const qtdPassagens = parseInt(document.getElementById('filtro-qtd-passagens').value, 10) || 0;
    const tipoVeiculoValues = $('#filtro-tipo-veiculo').val() || [];
    const nivelCorrelacaoValues = $('#filtro-nivel-correlacao').val() || [];
    const manchaValues = $('#filtro-mancha').val() || [];
    const pclManchaValues = $('#filtro-pcl-mancha').val() || [];
    const registroFatoValues = $('#filtro-registro-fato').val() || [];
    const chkOcultarIsoladosEl = document.getElementById("chk-ocultar-isolados");
    const ocultarIsolados = chkOcultarIsoladosEl ? chkOcultarIsoladosEl.checked : false;
    const tempoPermanenciaInput = document.getElementById('filtro-tempo-permanencia').value;
    const tempoPermanencia = parseInt(tempoPermanenciaInput.replace(/\D/g, ''), 10) || 0;
    
    const periodoPredominante = document.getElementById('filtro-periodo-predominante').value;
    
    const chkCorrelacaoComumEl = document.getElementById('chk-correlacao-comum');
    const mostrarApenasEmComum = chkCorrelacaoComumEl ? chkCorrelacaoComumEl.checked : false;
    const placasSelecionadasValues = $('#filtro-placas-selecionadas').val() || [];

    cy.edges().style("display", "none");
    if (nivelCorrelacaoValues.length > 0) {
        nivelCorrelacaoValues.forEach(nivel => {
            const siglaIncidencia = mapTextoParaSiglaIncidencia(nivel);
            if (siglaIncidencia) {
                cy.edges(`[incidencia = "${siglaIncidencia}"]`).style("display", "element");
            }
        });
    } else {
        cy.edges().style("display", "element");
    }

    /**
     * Valida se o nó atende ao filtro de Registro de Fato ativo.
     * @param {object} dadosNode Dados associados ao nó no grafo.
     * @returns {boolean} Indica se o nó deve permanecer visível conforme o filtro de Registro de Fato.
     */
    const validarRegistroFato = (dadosNode) => {
        if (registroFatoValues.length === 0) {
            return true;
        }

        const valoresSelecionados = registroFatoValues
            .map(valor => (valor || '').trim())
            .filter(valor => valor);

        const registrosDoVeiculo = dadosNode.registro_fato
            ? dadosNode.registro_fato.split(',').map(registro => registro.trim()).filter(registro => registro)
            : [];

        const registrosNormalizados = registrosDoVeiculo
            .filter(registro => registro.toLowerCase() !== 'sem registro' && registro.toLowerCase() !== 'nenhum');

        const requerRegistroDeFato = valoresSelecionados
            .some(valor => valor === 'Produto/Objeto' || valor === 'Recuperação');

        if (requerRegistroDeFato && registrosNormalizados.length === 0) {
            return false;
        }

        return valoresSelecionados.some(valorSelecionado => registrosDoVeiculo
            .some(registroDoVeiculo => registroDoVeiculo.toLowerCase().includes(valorSelecionado.toLowerCase())));
    };

    cy.nodes().forEach(node => {
        const dadosNode = node.data();
        let mostrar = true;
        const registroFatoValido = validarRegistroFato(dadosNode);

        if (dadosNode.basePlaca === "true") { return; } // tratado separadamente após o loop
        
        if (mostrar && alertaOcorrenciaValues.length > 0) {
            const temAlertaSelecionado = alertaOcorrenciaValues.some(alerta => 
                dadosNode.alertas_array && dadosNode.alertas_array.includes(alerta)
            );
            if (!temAlertaSelecionado) mostrar = false;
        }
        
        if (qtdPassagens > 0 && dadosNode.passagens < qtdPassagens) mostrar = false;
        
        if (mostrar && tipoVeiculoValues.length > 0) {
            if (!tipoVeiculoValues.includes(dadosNode.tipo_veiculo)) {
                mostrar = false;
            }
        }
        
        if (mostrar && nivelCorrelacaoValues.length > 0) {
            const edgesDoNode = node.connectedEdges();
            const incidenciasDoNode = edgesDoNode.map(edge => edge.data('incidencia')).filter(inc => inc);
            const semCorrelacao = incidenciasDoNode.length === 0;
            
            const temCorrelacaoSelecionada = nivelCorrelacaoValues.some(nivel => {
                if (nivel === 'Sem correlacionamento') {
                    return semCorrelacao;
                }
                if (nivel === 'Sem OCR') {
                    return dadosNode.semPlaca === 'true';
                }
                if (nivel === 'Sem OCR Registro de fato') {
                    return dadosNode.sem_ocr_registro_fato === 'true';
                }
                const siglaFiltro = mapTextoParaSiglaIncidencia(nivel);
                return incidenciasDoNode.includes(siglaFiltro);
            });
            if (!temCorrelacaoSelecionada) {
                mostrar = false;
            }
        }
        
        if (mostrar && manchaValues.length > 0) {
            const temManchaSelecionada = manchaValues.some(mancha => 
                dadosNode.manchas_array && dadosNode.manchas_array.includes(mancha)
            );
            if (!temManchaSelecionada) mostrar = false;
        }
        
        if (mostrar && pclManchaValues.length > 0) {
            const temPCLSelecionado = pclManchaValues.some(pcl => 
                dadosNode.pcls_array && dadosNode.pcls_array.includes(pcl)
            );
            if (!temPCLSelecionado) mostrar = false;
        }
        
        if (mostrar && !registroFatoValido) {
            mostrar = false;
        }

        if (mostrar && tempoPermanencia > 0) {
            if (dadosNode.tempo_permanencia < tempoPermanencia) {
                mostrar = false;
            }
        }
        
        if (mostrar && periodoPredominante && dadosNode.periodo_predominante !== periodoPredominante) {
            mostrar = false;
        }

        if (mostrar && placasSelecionadasValues.length > 0) {
            if (!placasSelecionadasValues.includes(dadosNode.id)) {
                mostrar = false;
            }
        }

        if (mostrar && mostrarApenasEmComum) {
            if (dadosNode.basePlaca !== "true" && dadosNode.veiculoComum !== "true") {
                mostrar = false;
            }
        }

        if (mostrar && ocultarIsolados) {
            const conexoesVisiveis = node.connectedEdges().filter(e => e.style('display') !== 'none').length;
            if (conexoesVisiveis === 0) {
                mostrar = false;
            }
        }

        node.style('display', mostrar ? 'element' : 'none');
    });

    // Processa nós basePlaca separadamente, após a visibilidade de arestas e demais nós já estar definida
    const algumFiltroAtivo = alertaOcorrenciaValues.length > 0 || qtdPassagens > 0 ||
        tipoVeiculoValues.length > 0 || nivelCorrelacaoValues.length > 0 ||
        manchaValues.length > 0 || pclManchaValues.length > 0 ||
        registroFatoValues.length > 0 || tempoPermanencia > 0 ||
        !!periodoPredominante || placasSelecionadasValues.length > 0 ||
        mostrarApenasEmComum || ocultarIsolados;

    let algumBasePlacaSoPorCorrelacao = false;

    cy.nodes('[basePlaca = "true"]').forEach(node => {
        const dadosNode = node.data();
        const registroFatoValido = validarRegistroFato(dadosNode);

        if (!algumFiltroAtivo) {
            node.style('display', registroFatoValido ? 'element' : 'none');
            return;
        }

        let mostrarPorFiltro = registroFatoValido;

        if (mostrarPorFiltro && alertaOcorrenciaValues.length > 0) {
            if (!alertaOcorrenciaValues.some(a => dadosNode.alertas_array && dadosNode.alertas_array.includes(a))) mostrarPorFiltro = false;
        }
        if (mostrarPorFiltro && qtdPassagens > 0 && dadosNode.passagens < qtdPassagens) mostrarPorFiltro = false;
        if (mostrarPorFiltro && tipoVeiculoValues.length > 0 && !tipoVeiculoValues.includes(dadosNode.tipo_veiculo)) mostrarPorFiltro = false;
        if (mostrarPorFiltro && nivelCorrelacaoValues.length > 0) {
            const edgesDoNode = node.connectedEdges();
            const incidenciasDoNode = edgesDoNode.map(e => e.data('incidencia')).filter(Boolean);
            const semCorrelacao = incidenciasDoNode.length === 0;
            const temCorrSelecionada = nivelCorrelacaoValues.some(nivel => {
                if (nivel === 'Sem correlacionamento') { return semCorrelacao; }
                if (nivel === 'Sem OCR') { return dadosNode.semPlaca === 'true'; }
                if (nivel === 'Sem OCR Registro de fato') { return dadosNode.sem_ocr_registro_fato === 'true'; }
                return incidenciasDoNode.includes(mapTextoParaSiglaIncidencia(nivel));
            });
            if (!temCorrSelecionada) mostrarPorFiltro = false;
        }
        if (mostrarPorFiltro && manchaValues.length > 0) {
            if (!manchaValues.some(m => dadosNode.manchas_array && dadosNode.manchas_array.includes(m))) mostrarPorFiltro = false;
        }
        if (mostrarPorFiltro && pclManchaValues.length > 0) {
            if (!pclManchaValues.some(p => dadosNode.pcls_array && dadosNode.pcls_array.includes(p))) mostrarPorFiltro = false;
        }
        if (mostrarPorFiltro && tempoPermanencia > 0 && dadosNode.tempo_permanencia < tempoPermanencia) mostrarPorFiltro = false;
        if (mostrarPorFiltro && periodoPredominante && dadosNode.periodo_predominante !== periodoPredominante) mostrarPorFiltro = false;
        if (mostrarPorFiltro && placasSelecionadasValues.length > 0 && !placasSelecionadasValues.includes(dadosNode.id)) mostrarPorFiltro = false;

        if (mostrarPorFiltro) {
            node.style('display', 'element');
        } else {
            const temCorrelacaoVisivel = node.connectedEdges().some(e => {
                if (e.style('display') === 'none') { return false; }
                const outroNo = e.source().id() === node.id() ? e.target() : e.source();
                return outroNo.style('display') !== 'none';
            });
            if (temCorrelacaoVisivel) {
                node.style('display', 'element');
                algumBasePlacaSoPorCorrelacao = true;
            } else {
                node.style('display', 'none');
            }
        }
    });

    const avisoAlvoEl = document.getElementById('aviso-placa-alvo-correlacao');
    if (avisoAlvoEl) { avisoAlvoEl.style.display = algumBasePlacaSoPorCorrelacao ? '' : 'none'; }
    
    atualizarContadorPlacasCorrelacionadas();
    resetViewAsync();
}

/**
 * Atualiza o contador de placas correlacionadas visíveis no cabeçalho.
 * Separa a contagem em placas com correlação (com arestas) e sem correlação (abaixo do mínimo).
 */
function atualizarContadorPlacasCorrelacionadas() {
    if (!cy) return;
    
    const nodosVisiveis = cy.nodes().filter(node => {
        const display = node.style('display');
        const basePlaca = node.data('basePlaca');
        return display !== 'none' && basePlaca !== 'true';
    });

    var comCorrelacao = 0;
    var semCorrelacao = 0;

    nodosVisiveis.forEach(function(node) {
        var temAresta = node.connectedEdges().some(function(edge) {
            return edge.style('display') !== 'none';
        });
        if (temAresta) {
            comCorrelacao++;
        } else {
            semCorrelacao++;
        }
    });

    var contadorEl = document.getElementById('contador-placas-correlacionadas');
    if (contadorEl) {
        contadorEl.textContent = comCorrelacao;
    }

    var semCorrelacaoValor = document.getElementById('contador-placas-sem-correlacao-valor');
    if (semCorrelacaoValor) {
        semCorrelacaoValor.textContent = semCorrelacao;
    }
}