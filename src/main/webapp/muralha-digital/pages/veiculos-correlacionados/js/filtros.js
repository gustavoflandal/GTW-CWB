const REGEX_PLACA_ANTIGA = /^[A-Z]{3}[0-9]{4}$/;
const REGEX_PLACA_MERCOSUL = /^[A-Z]{3}[0-9][A-Z][0-9]{2}$/;
const IDS_CAMPOS_TEMPORAIS = ['txt_data_inicio', 'txt_hora_inicio', 'txt_data_fim', 'txt_hora_fim'];

/**
 * Converte uma data ISO yyyy-MM-dd em Date local sem deslocamento de fuso.
 * @param {string} dataIso - Data no formato yyyy-MM-dd.
 * @returns {Date} Data local correspondente.
 */
function criarDataLocal(dataIso) {
    const partes = (dataIso || '').split('-').map(parte => parseInt(parte, 10));
    return new Date(partes[0], partes[1] - 1, partes[2]);
}

/**
 * Soma meses preservando o conceito de mês de calendário e limitando ao último dia válido.
 * @param {Date} dataBase - Data inicial.
 * @param {number} quantidadeMeses - Quantidade de meses a somar.
 * @returns {Date} Nova data ajustada.
 */
function somarMesesCalendario(dataBase, quantidadeMeses) {
    const ano = dataBase.getFullYear();
    const mes = dataBase.getMonth();
    const dia = dataBase.getDate();
    const ultimoDiaMesDestino = new Date(ano, mes + quantidadeMeses + 1, 0).getDate();

    return new Date(ano, mes + quantidadeMeses, Math.min(dia, ultimoDiaMesDestino));
}

/**
 * Formata uma data local para o valor esperado pelo input date.
 * @param {Date} data - Data a ser formatada.
 * @returns {string} Data formatada em yyyy-MM-dd.
 */
function formatarDataParaInput(data) {
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');

    return `${ano}-${mes}-${dia}`;
}

/**
 * Atualiza o estado visual do campo temporal conforme a validade nativa do input.
 * @param {HTMLInputElement} input - Campo date/time a ser atualizado.
 */
function atualizarEstadoVisualCampoTemporal(input) {
    if (!input) {
        return false;
    }

    const possuiConteudo = !!input.value || input.validity.badInput;
    const valorValido = !input.validity.badInput && (!input.value || input.validity.valid);
    const invalida = possuiConteudo && !valorValido;

    input.classList.toggle('is-invalid', invalida);
    input.classList.toggle('text-danger', invalida);
    input.setAttribute('aria-invalid', invalida ? 'true' : 'false');

    return !invalida;
}

/**
 * Atualiza o estado visual dos campos temporais do formulário principal.
 * @param {string[]} idsCampos - Identificadores dos campos a validar.
 */
function atualizarEstadoVisualCamposTemporais(idsCampos) {
    idsCampos.forEach(idCampo => {
        const campo = document.getElementById(idCampo);
        if (campo) {
            atualizarEstadoVisualCampoTemporal(campo);
        }
    });
}

/**
 * Atualiza a validação visual do campo temporal durante a digitação.
 * @param {HTMLInputElement} txt - Campo date/time informado.
 */
async function verificarCampoTemporalAsync(txt) {
    atualizarEstadoVisualCampoTemporal(txt);
    atualizarAvisoLimitePeriodo();
}

/**
 * Normaliza o valor digitado da placa para validar os formatos aceitos.
 * @param {string} placa - Texto informado no campo.
 * @returns {string} Texto normalizado sem separadores e em maiúsculas.
 */
function normalizarPlacaParaValidacao(placa) {
    return (placa || '').toUpperCase().replace(/[\s-]/g, '');
}

/**
 * Atualiza o estado visual do campo de placa conforme a validade do valor digitado.
 * @param {HTMLInputElement} input - Campo de placa a ser atualizado.
 */
function atualizarEstadoVisualPlaca(input) {
    if (!input) {
        return;
    }

    const placaNormalizada = normalizarPlacaParaValidacao(input.value);
    const possuiConteudo = placaNormalizada.length > 0;
    const placaCompletaValida = REGEX_PLACA_ANTIGA.test(placaNormalizada) || REGEX_PLACA_MERCOSUL.test(placaNormalizada);
    const invalida = possuiConteudo && !placaCompletaValida;

    input.classList.toggle('is-invalid', invalida);
    input.classList.toggle('text-danger', invalida);
    input.setAttribute('aria-invalid', invalida ? 'true' : 'false');
}

/**
 * Atualiza o estado visual de todos os campos de placa do formulário principal.
 */
function atualizarEstadoVisualCamposPlaca() {
    ['txt_placa_1', 'txt_placa_2', 'txt_placa_3'].forEach(idCampo => {
        const campo = document.getElementById(idCampo);
        if (campo) {
            atualizarEstadoVisualPlaca(campo);
        }
    });
}

/**
 * Converte o texto do campo de entrada da placa para letras maiúsculas e destaca valores inválidos.
 * @param {HTMLInputElement} txt - O campo de entrada de texto da placa.
 */
async function verificaDigitoPlacaAsync(txt) {
    txt.value = txt.value.toUpperCase();
    atualizarEstadoVisualPlaca(txt);
    atualizarAvisoLimitePeriodo();
}

/**
 * Verifica se a tecla pressionada é Enter (código 13) e chama a função `executarAnalise()` se for o caso.
 * @param {Event} e - O evento de tecla pressionada.
 */
async function verifacaTeclaAsync(e) {
    if (e.keyCode == 13)
        executarAnalise();
}

/**
 * Preenche os campos de data e hora final com valores adequados com base nos valores dos campos de data e hora inicial.
 */
async function ajustarPeriodoDataHoraAsync() {
    const txt_data_inicio = document.getElementById('txt_data_inicio');
    const txt_hora_inicio = document.getElementById('txt_hora_inicio');
    const txt_data_fim = document.getElementById('txt_data_fim');
    const txt_hora_fim = document.getElementById('txt_hora_fim');

    atualizarEstadoVisualCamposTemporais(IDS_CAMPOS_TEMPORAIS);

    if (!txt_data_inicio.value) {
        return;
    }

    if (txt_hora_inicio.value === "") {
        txt_hora_inicio.value = "00:00";
    }
    if (txt_data_fim.value === "") {
        txt_data_fim.value = txt_data_inicio.value;
    }
    if (txt_hora_fim.value === "") {
        txt_hora_fim.value = "23:59";
    }

    // Atualiza limites de data
    txt_data_fim.min = txt_data_inicio.value;
    txt_data_inicio.max = txt_data_fim.value;
    atualizarEstadoVisualCamposTemporais(IDS_CAMPOS_TEMPORAIS);

    const dataHoraInicio = new Date(`${txt_data_inicio.value}T${txt_hora_inicio.value}`);
    const dataHoraFim = new Date(`${txt_data_fim.value}T${txt_hora_fim.value}`);

    if (dataHoraFim < dataHoraInicio) {
        txt_hora_fim.value = txt_hora_inicio.value;
        atualizarEstadoVisualCamposTemporais(IDS_CAMPOS_TEMPORAIS);
        mostrarToastAsync("A Data/Hora Final deve ser maior que a Data/Hora Inicial.", "erro", 5000);
        return;
    }

    var dtInicioSoData = criarDataLocal(txt_data_inicio.value);
    var dtFimSoData = criarDataLocal(txt_data_fim.value);
    var limiteMaximo = somarMesesCalendario(dtInicioSoData, 12);
    if (dtFimSoData > limiteMaximo) {
        mostrarToastAsync("O intervalo entre as datas deve ser de no máximo 12 meses.", "erro", 5000);
    }

    atualizarAvisoLimitePeriodo();
}

/**
 * Preenche os campos de data e hora final com combase em um intervalo.
 */
async function aplicarIntervaloRapidoAsync(intervalo) {
    if (!intervalo) {
        return;
    }

    const hoje = new Date();
    const dataFim = new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate());
    const dataInicio = new Date(dataFim);

    if (intervalo === '30' || intervalo === '60' || intervalo === '90' || intervalo === '365') {
        const meses = intervalo === '30' ? 1 : intervalo === '60' ? 2 : intervalo === '90' ? 3 : 12;
        const dataBase = new Date(dataFim);
        dataInicio.setTime(somarMesesCalendario(dataBase, -meses).getTime());
    } else {
        dataInicio.setDate(dataFim.getDate() - parseInt(intervalo, 10));
    }

    document.getElementById('txt_data_inicio').value = formatarDataParaInput(dataInicio);
    document.getElementById('txt_data_fim').value = formatarDataParaInput(dataFim);
    document.getElementById('txt_hora_inicio').value = "00:00";
    document.getElementById('txt_hora_fim').value = "23:59";

    atualizarEstadoVisualCamposTemporais(IDS_CAMPOS_TEMPORAIS);

    await ajustarPeriodoDataHoraAsync();
}

/**
 * Inicializa na tela de correlação as faixas configuradas no módulo de configuração.
 * @returns {Promise<void>} Promessa resolvida após aplicar a configuração carregada.
 */
async function inicializarConfiguracaoCorrelacaoAsync() {
    const configuracaoCorrelacao = await carregarConfiguracaoCorrelacaoAsync();
    aplicarConfiguracaoCorrelacaoNaTela(configuracaoCorrelacao);
}

/**
 * Aplica na tela apenas a informação visual das faixas de correlação configuradas.
 * @param {{qtd_pas_correlacao_baixa:number, qtd_pas_correlacao_media:number, qtd_pas_correlacao_alta:number}} configuracaoCorrelacao Configuração vigente para a tela.
 */
function aplicarConfiguracaoCorrelacaoNaTela(configuracaoCorrelacao) {
    const conteudoModalEl = document.getElementById('conteudo-modal-regra-correlacionamento');
    const qtdPasSemCorrelacaoMax = configuracaoCorrelacao.qtd_pas_correlacao_baixa - 1;

    if (conteudoModalEl) {
        conteudoModalEl.innerHTML = montarConteudoModalRegraCorrelacionamento(configuracaoCorrelacao, qtdPasSemCorrelacaoMax);
    }
}

/**
 * Monta o HTML exibido no modal com a regra atual de correlacionamento.
 * @param {{qtd_pas_correlacao_baixa:number, qtd_pas_correlacao_media:number, qtd_pas_correlacao_alta:number}} configuracaoCorrelacao Configuração vigente para a tela.
 * @param {number} qtdPasSemCorrelacaoMax Quantidade máxima de passagens para classificação sem correlação.
 * @returns {string} HTML formatado para o corpo do modal.
 */
function montarConteudoModalRegraCorrelacionamento(configuracaoCorrelacao, qtdPasSemCorrelacaoMax) {
    const descricaoSemCorrelacao = qtdPasSemCorrelacaoMax >= 1
        ? `${qtdPasSemCorrelacaoMax === 1 ? '1 passagem' : `1 a ${qtdPasSemCorrelacaoMax} passagens`}`
        : 'Desabilitada';
    const nivelBaixo = '<strong style="color:#212529;">Baixo</strong>';
    const nivelMedio = '<strong style="color:#ffc107;">Médio</strong>';
    const nivelAlto = '<strong style="color:#dc3545;">Alto</strong>';
    const descricaoBaixa = `${configuracaoCorrelacao.qtd_pas_correlacao_baixa} ${configuracaoCorrelacao.qtd_pas_correlacao_baixa === 1 ? 'passagem' : 'passagens'} para nível ${nivelBaixo}.`;
    const descricaoMedia = `${configuracaoCorrelacao.qtd_pas_correlacao_media} ${configuracaoCorrelacao.qtd_pas_correlacao_media === 1 ? 'passagem' : 'passagens'} para nível ${nivelMedio}.`;
    const descricaoAlta = `${configuracaoCorrelacao.qtd_pas_correlacao_alta} ou mais passagens para nível ${nivelAlto}.`;

    return `
        <div class="alert alert-primary mb-3" role="alert">
            <strong>Regra:</strong> a quantidade de passagens da correlação alta deve ser maior que a da média, e a da correlação média deve ser maior que a da baixa.
        </div>
        <ul class="mb-2">
            <li>${descricaoBaixa}</li>
            <li>${descricaoMedia}</li>
            <li>${descricaoAlta}</li>
        </ul>
        <p class="text-muted mb-0">Sem correlação: ${descricaoSemCorrelacao}.</p>
        <div class="bg-light border rounded px-3 py-2 mt-3 small text-muted">
            <strong class="d-block mb-1">Placas de Exemplo para o correlacionamento:</strong>
            <span>PLACA ALVO ANS3537 - SEU7J11, SEG5G44, AEL908</span>
        </div>
    `;
}

/**
 * Valida o campo de mínimo de correlações para aceitar apenas valores positivos.
 * @param {HTMLInputElement} input - O campo de input a ser validado.
 */
function validarMinCorrelacoes(input) {
    const valor = parseInt(input.value, 10);
    if (isNaN(valor) || valor < 1) {
        input.value = 1;
    }
    atualizarAvisoLimitePeriodo();
}

/**
 * Chamado no oninput do campo nº mínimo: atualiza o aviso sem forçar o valor,
 * permitindo que o usuário apague o campo para redigitar livremente.
 */
function onInputMinCorrelacoes() {
    atualizarAvisoLimitePeriodo();
}

/**
 * Atualiza o aviso de limite de período exibido no formulário principal.
 * Acionado ao alterar placas ou mínimo de correlações.
 */
function atualizarAvisoLimitePeriodo() {
    const avisoEl = document.getElementById('aviso-limite-periodo');
    if (!avisoEl) { return; }

    const placa1 = document.getElementById('txt_placa_1')?.value.trim() || '';
    const placa2 = document.getElementById('txt_placa_2')?.value.trim() || '';
    const placa3 = document.getElementById('txt_placa_3')?.value.trim() || '';
    const numPlacas = [placa1, placa2, placa3].filter(p => p).length;

    if (numPlacas === 0) {
        avisoEl.style.display = 'none';
        return;
    }

    const minCorr = parseInt(document.getElementById('txt_min_correlacoes')?.value, 10) || 3;
    const condA = minCorr === 1;
    const condB = minCorr === 2;
    const condC = minCorr === 3;

    const dtInicioVal = document.getElementById('txt_data_inicio')?.value;
    const dtFimVal = document.getElementById('txt_data_fim')?.value;
    const temDatas = !!(dtInicioVal && dtFimVal);

    if (condA) {
        const maxDias = numPlacas >= 3 ? 7 : numPlacas === 2 ? 14 : 21;
        const labelLimite = numPlacas >= 3 ? '1 semana' : numPlacas === 2 ? '2 semanas' : '3 semanas';
        let dentro = false;
        if (temDatas) {
            const dtInicio = criarDataLocal(dtInicioVal);
            const dtFim = criarDataLocal(dtFimVal);
            const limite = new Date(dtInicio);
            limite.setDate(limite.getDate() + maxDias);
            dentro = dtFim <= limite;
        }
        if (dentro) {
            avisoEl.className = 'alert alert-success mt-2 py-2 small';
            avisoEl.innerHTML = `<i class="bi bi-check-circle-fill me-1"></i> <strong>OK:</strong> Período dentro do limite de <strong>${labelLimite}</strong> para ${numPlacas} placa(s).`;
        } else {
            avisoEl.className = 'alert alert-warning mt-2 py-2 small';
            avisoEl.innerHTML = `<i class="bi bi-exclamation-triangle-fill me-1"></i> <strong>Atenção:</strong> com mínimo de 1 correlação, o período máximo permitido é <strong>${labelLimite}</strong> para ${numPlacas} placa(s).`;
        }
        avisoEl.style.display = '';
    } else if (condB) {
        const maxMeses = numPlacas >= 3 ? 1 : numPlacas === 2 ? 2 : 3;
        const labelLimite = numPlacas >= 3 ? '1 mês' : numPlacas === 2 ? '2 meses' : '3 meses';
        let dentro = false;
        if (temDatas) {
            const dtInicio = criarDataLocal(dtInicioVal);
            const dtFim = criarDataLocal(dtFimVal);
            const limite = somarMesesCalendario(dtInicio, maxMeses);
            dentro = dtFim <= limite;
        }
        if (dentro) {
            avisoEl.className = 'alert alert-success mt-2 py-2 small';
            avisoEl.innerHTML = `<i class="bi bi-check-circle-fill me-1"></i> <strong>OK:</strong> Período dentro do limite de <strong>${labelLimite}</strong> para ${numPlacas} placa(s).`;
        } else {
            avisoEl.className = 'alert alert-warning mt-2 py-2 small';
            avisoEl.innerHTML = `<i class="bi bi-exclamation-triangle-fill me-1"></i> <strong>Atenção:</strong> com mínimo de 2 correlações, o período máximo recomendado é <strong>${labelLimite}</strong> para ${numPlacas} placa(s).`;
        }
        avisoEl.style.display = '';
    } else if (condC) {
        const maxMeses = numPlacas >= 3 ? 3 : numPlacas === 2 ? 6 : 12;
        const labelLimite = numPlacas >= 3 ? '3 meses' : numPlacas === 2 ? '6 meses' : '12 meses';
        let dentro = false;
        if (temDatas) {
            const dtInicio = criarDataLocal(dtInicioVal);
            const dtFim = criarDataLocal(dtFimVal);
            const limite = somarMesesCalendario(dtInicio, maxMeses);
            dentro = dtFim <= limite;
        }
        if (dentro) {
            avisoEl.className = 'alert alert-success mt-2 py-2 small';
            avisoEl.innerHTML = `<i class="bi bi-check-circle-fill me-1"></i> <strong>OK:</strong> Período dentro do limite de <strong>${labelLimite}</strong> para ${numPlacas} placa(s).`;
        } else {
            avisoEl.className = 'alert alert-warning mt-2 py-2 small';
            avisoEl.innerHTML = `<i class="bi bi-exclamation-triangle-fill me-1"></i> <strong>Atenção:</strong> com ${numPlacas} placa(s) e mínimo de 3 passagens correlacionadas, o período máximo permitido é <strong>${labelLimite}</strong>.`;
        }
        avisoEl.style.display = '';
    } else {
        avisoEl.style.display = 'none';
    }
}

/**
 * Função para limpar o formulário, o grafo e restaurar os filtros.
 */
async function limparFormularioPrincipal() {
    document.getElementById('txt_data_inicio').value = '';
    document.getElementById('txt_hora_inicio').value = '';
    document.getElementById('txt_data_fim').value = '';
    document.getElementById('txt_hora_fim').value = '';
    document.getElementById('txt_placa_1').value = '';
    document.getElementById('txt_placa_2').value = '';
    document.getElementById('txt_placa_3').value = '';
    document.getElementById('txt_min_correlacoes').value = '3';
    atualizarEstadoVisualCamposTemporais(IDS_CAMPOS_TEMPORAIS);
    atualizarEstadoVisualCamposPlaca();
    $('#intervalo').selectpicker('val', '');
    
    document.getElementById('filtro-qtd-passagens').value = '';
    document.getElementById('filtro-periodo-predominante').value = '';
    
    $('#filtro-tipo-veiculo').selectpicker('deselectAll');
    $('#filtro-registro-fato').selectpicker('deselectAll');
    $('#filtro-alerta-ocorrencia').selectpicker('deselectAll');
    $('#filtro-mancha').selectpicker('deselectAll');
    $('#filtro-pcl-mancha').selectpicker('deselectAll');
    $('#filtro-nivel-correlacao').selectpicker('deselectAll');
    $('#filtro-placas-selecionadas').selectpicker('deselectAll');
    document.getElementById('filtro-tempo-permanencia').value = '';
    atualizarAvisoLimitePeriodo();
    const chkOcultarIsoladosEl = document.getElementById('chk-ocultar-isolados');
    if (chkOcultarIsoladosEl) {
        chkOcultarIsoladosEl.checked = false;
    }
    const chkCorrelacaoComumEl = document.getElementById('chk-correlacao-comum');
    if (chkCorrelacaoComumEl) {
        chkCorrelacaoComumEl.checked = false;
    }

    document.getElementById('containerDeFiltros').style.display = 'none';

    if (cy) {
        cy.destroy();
    }
    await criarEConfigurarGrafoAsync([]);
}


/**
 * Limpa todos os filtros avançados sem afetar o formulário principal.
 */
function limparFiltrosAvancados() {
    document.getElementById('filtro-qtd-passagens').value = '';
    document.getElementById('filtro-tempo-permanencia').value = '';
    document.getElementById('filtro-periodo-predominante').value = '';

    $('#filtro-tipo-veiculo').selectpicker('deselectAll');
    $('#filtro-registro-fato').selectpicker('deselectAll');
    $('#filtro-alerta-ocorrencia').selectpicker('deselectAll');
    $('#filtro-mancha').selectpicker('deselectAll');
    $('#filtro-pcl-mancha').selectpicker('deselectAll');
    $('#filtro-nivel-correlacao').selectpicker('deselectAll');
    $('#filtro-placas-selecionadas').selectpicker('deselectAll');

    const chkOcultarIsoladosEl = document.getElementById('chk-ocultar-isolados');
    if (chkOcultarIsoladosEl) {
        chkOcultarIsoladosEl.checked = false;
    }

    const chkCorrelacaoComumEl = document.getElementById('chk-correlacao-comum');
    if (chkCorrelacaoComumEl) {
        chkCorrelacaoComumEl.checked = false;
    }

    aplicarTodosOsFiltrosAsync();
}

/**
 * Atualiza o estado (habilitado/desabilitado) do checkbox "Apenas placas em comum".
 * Desabilita se não existem veículos em comum no grafo atual.
 */
function atualizarEstadoBotaoPlacasEmComum() {
    const chkCorrelacaoComumEl = document.getElementById('chk-correlacao-comum');
    if (!chkCorrelacaoComumEl || !cy) {
        return;
    }

    const existeVeiculoComum = cy.nodes().some(node => node.data('veiculoComum') === 'true');
    chkCorrelacaoComumEl.disabled = !existeVeiculoComum;

    if (!existeVeiculoComum) {
        chkCorrelacaoComumEl.checked = false;
    }
}

/**
 * Função para executar a análise, popular os filtros e exibir o resultado.
 */
async function executarAnalise() {
    limparFiltrosAvancados();

    const resultado = await recarregarGrafo();

    const containerFiltros = document.getElementById('containerDeFiltros');
    if (resultado && resultado.elementos && resultado.elementos.length > 0) {
        containerFiltros.style.display = 'block';
        const dadosDosFiltros = gerarOpcoesDeFiltroDinamicamente(resultado.elementos);
        await popularFiltrosAvancadosAsync(dadosDosFiltros);
        $(containerFiltros).find('.selectpicker').each(function() {
            const $select = $(this);
            if (!$select.data('selectpicker')) {
                $select.selectpicker();
            }
            $select.selectpicker('refresh');
            $select.selectpicker('render');
        });
    } else {
        containerFiltros.style.display = 'none';
    }

    atualizarEstadoBotaoPlacasEmComum();
}

/**
 * Separa o texto de Registro de Fato em itens, usando vírgula como separador.
 * @param {string|null|undefined} registroFato - Texto vindo do backend (ex.: "Furto, Produto/Objeto").
 * @returns {string[]} Lista de itens normalizados (trimados) e não vazios.
 */
function separarTiposRegistroFato(registroFato) {
    if (!registroFato || typeof registroFato !== 'string') {
        return [];
    }

    return registroFato
        .split(',')
        .map(item => item.trim())
        .filter(item => item);
}

/**
 * Gera as opções para os filtros dinamicamente a partir dos dados do grafo.
 * @param {Array} elementos - Os elementos do grafo (nós e arestas).
 * @returns {Object} Um objeto com as listas de opções para cada filtro.
 */
function gerarOpcoesDeFiltroDinamicamente(elementos) {
    const nos = elementos.filter(el => el.data.id);

    const removerNaoInformado = (arr) => arr.filter(item => item && item !== 'Não informado');

    const tiposAlertaOcorrencia = [...new Set(nos.flatMap(no => no.data.alertas_array || []))];

    const placas = nos
        .map(no => no.data.label)
        .filter(placa => placa && placa !== 'null' && placa !== 'Sem Placa')
        .sort();

    return {
        tiposVeiculo: removerNaoInformado([...new Set(nos.map(no => no.data.tipo_veiculo))]).sort(),
        manchas: removerNaoInformado([...new Set(nos.flatMap(no => no.data.manchas_array || []))]).sort(),
        pclsMancha: removerNaoInformado([...new Set(nos.flatMap(no => no.data.pcls_array || []))]).sort(),
        tiposRegistroFato: removerNaoInformado([...new Set(nos.flatMap(no => separarTiposRegistroFato(no.data.registro_fato)))]).sort(),
        tiposAlertaOcorrencia: tiposAlertaOcorrencia.sort(),
        placas: placas
    };
}

/**
 * Popula os seletores de filtros avançados e configura os eventos de mudança.
 * @param {object} dadosFiltros - Objeto contendo as listas de opções para cada filtro.
 */
async function popularFiltrosAvancadosAsync(dadosFiltros = {}) {
    const popularSelectPicker = (selectId, opcoes) => {
        const $select = $(`#${selectId}`);
        $select.empty();
        opcoes?.forEach(opcao => {
            $select.append(new Option(opcao, opcao));
        });
        if (!$select.data('selectpicker')) {
            $select.selectpicker();
        }
        $select.selectpicker('refresh');
        $select.selectpicker('render');
    };

    popularSelectPicker('filtro-alerta-ocorrencia', dadosFiltros.tiposAlertaOcorrencia);
    popularSelectPicker('filtro-tipo-veiculo', dadosFiltros.tiposVeiculo);
    popularSelectPicker('filtro-mancha', dadosFiltros.manchas);
    popularSelectPicker('filtro-pcl-mancha', dadosFiltros.pclsMancha);
    popularSelectPicker('filtro-registro-fato', dadosFiltros.tiposRegistroFato);
    popularSelectPicker('filtro-placas-selecionadas', dadosFiltros.placas);

    const idsFiltrosParaMonitorar = [
        'filtro-qtd-passagens', 'filtro-tipo-veiculo', 'filtro-nivel-correlacao',
        'filtro-mancha', 'filtro-pcl-mancha', 'filtro-tempo-permanencia',
        'filtro-registro-fato', 'filtro-alerta-ocorrencia', 'chk-ocultar-isolados',
        'filtro-periodo-predominante',
        'chk-correlacao-comum', 'filtro-placas-selecionadas'
    ];

    idsFiltrosParaMonitorar.forEach(id => {
        const filtroEl = document.getElementById(id);
        if (filtroEl) {
            filtroEl.onchange = () => {
                aplicarTodosOsFiltrosAsync();
            };
        }
    });
    
    const selectpickersIds = ['filtro-tipo-veiculo', 'filtro-registro-fato', 'filtro-alerta-ocorrencia', 'filtro-mancha', 'filtro-pcl-mancha', 'filtro-nivel-correlacao', 'filtro-placas-selecionadas'];
    selectpickersIds.forEach(id => {
        $(`#${id}`).off('changed.bs.select').on('changed.bs.select', function() {
            aplicarTodosOsFiltrosAsync();
        });
    });
}