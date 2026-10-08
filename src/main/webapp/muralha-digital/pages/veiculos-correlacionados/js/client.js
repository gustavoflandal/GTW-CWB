const CONFIGURACAO_CORRELACAO_PADRAO = Object.freeze({
    qtd_pas_correlacao_baixa: 3,
    qtd_pas_correlacao_media: 4,
    qtd_pas_correlacao_alta: 5
});

/**
 * Carrega do backend a configuração de faixas de passagens usada pela tela de correlação.
 * @returns {Promise<{qtd_pas_correlacao_baixa:number, qtd_pas_correlacao_media:number, qtd_pas_correlacao_alta:number}>} Configuração válida para uso no front.
 */
async function carregarConfiguracaoCorrelacaoAsync() {
    try {
        const params = new URLSearchParams({ acao: "obterConfigs" });
        const resp = await fetch(`/MuralhaDigital/ConfigurarEquipamento?${params.toString()}`);

        if (!resp.ok) {
            throw new Error(`Erro ao obter configurações de correlação: ${resp.status} ${resp.statusText}`);
        }

        const xmlTexto = await resp.text();
        const xmlDoc = $.parseXML(xmlTexto);
        const $xml = $(xmlDoc);
        const sucesso = $xml.find("sucesso").text();

        if (sucesso && sucesso !== "true") {
            throw new Error($xml.find("msgResposta").text() || "Falha ao carregar configurações de correlação.");
        }

        const qtdPasCorrelacaoBaixa = parseInt($xml.find("qtd_pas_correlacao_baixa").text() || $xml.find("passagens_correlacao_baixa").text(), 10);
        const qtdPasCorrelacaoMedia = parseInt($xml.find("qtd_pas_correlacao_media").text() || $xml.find("passagens_correlacao_media").text(), 10);
        const qtdPasCorrelacaoAlta = parseInt($xml.find("qtd_pas_correlacao_alta").text() || $xml.find("passagens_correlacao_alta").text(), 10);

        if (isNaN(qtdPasCorrelacaoBaixa) || qtdPasCorrelacaoBaixa < 1 ||
            isNaN(qtdPasCorrelacaoMedia) || qtdPasCorrelacaoMedia <= qtdPasCorrelacaoBaixa ||
            isNaN(qtdPasCorrelacaoAlta) || qtdPasCorrelacaoAlta <= qtdPasCorrelacaoMedia) {
            return obterConfiguracaoCorrelacaoPadrao();
        }

        return {
            qtd_pas_correlacao_baixa: qtdPasCorrelacaoBaixa,
            qtd_pas_correlacao_media: qtdPasCorrelacaoMedia,
            qtd_pas_correlacao_alta: qtdPasCorrelacaoAlta
        };
    } catch (erro) {
        console.warn("Nao foi possivel carregar a configuracao de correlacao. Aplicando valores padrao.", erro);
        return obterConfiguracaoCorrelacaoPadrao();
    }
}

/**
 * Gera uma cópia dos valores padrão de correlação.
 * @returns {{qtd_pas_correlacao_baixa:number, qtd_pas_correlacao_media:number, qtd_pas_correlacao_alta:number}} Configuração padrão.
 */
function obterConfiguracaoCorrelacaoPadrao() {
    return {
        qtd_pas_correlacao_baixa: CONFIGURACAO_CORRELACAO_PADRAO.qtd_pas_correlacao_baixa,
        qtd_pas_correlacao_media: CONFIGURACAO_CORRELACAO_PADRAO.qtd_pas_correlacao_media,
        qtd_pas_correlacao_alta: CONFIGURACAO_CORRELACAO_PADRAO.qtd_pas_correlacao_alta
    };
}

/**
 * Consulta os veículos correlacionados via servlet com base nos parâmetros informados.
 *
 * @param {string} dataHoraInicio - Data e hora inicial (formato ISO ou compatível)
 * @param {string} dataHoraFim - Data e hora final (formato ISO ou compatível)
 * @param {string[]} placas - Array de placas base para análise (com ou sem traço)
 * @param {string} numMinPassagensCorrelacionadas - O número mínimo de passagens correlacionadas para o filtro
 * @returns {Promise<Object|null>} Um objeto com 'veiculos', 'correlacoes' e 'dadosFiltros', ou null em caso de falha.
 */
async function obterVeiculosCorrelacionadosAsync(dataHoraInicio, dataHoraFim, placas, numMinPassagensCorrelacionadas) {
    const placasNormalizadas = placas.map(p => (p ?? "").replace(/-/g, "").toUpperCase()).filter(p => p);

    if (placasNormalizadas.length === 0) {
        mostrarToastAsync("Nenhuma placa v\u00e1lida informada.", "erro");
        return null;
    }

    const params = new URLSearchParams({
        acao: "correlacionados",
        placas: JSON.stringify(placasNormalizadas),
        dataInicio: dataHoraInicio,
        dataFim: dataHoraFim,
        numMinPassagensCorrelacionadas: (parseInt(numMinPassagensCorrelacionadas) >= 1 ? numMinPassagensCorrelacionadas : "1")
    });

    try {
        const url = `/MuralhaDigital/VeiculosCorrelacionados?${params.toString()}`;
        const resp = await fetch(url);
        
        if (!resp.ok) {
            const errorText = await resp.text();
            throw new Error(`Erro na requisição: ${resp.status} ${resp.statusText} - ${errorText}`);
        }

        const dadosRecebidos = await resp.json();

        if (!dadosRecebidos || !dadosRecebidos.veiculos || dadosRecebidos.veiculos.length === 0) {
            mostrarToastAsync("Nenhum dado encontrado para os filtros informados.", "alerta");
            return null;
        }
        
        if (!dadosRecebidos.correlacoes || Object.keys(dadosRecebidos.correlacoes).length === 0) {
            mostrarToastAsync("Nenhuma correla\u00e7\u00e3o encontrada para os filtros informados.", "alerta");
            return null;
        }

        return {
            veiculos: dadosRecebidos.veiculos,
            correlacoes: dadosRecebidos.correlacoes,
            dadosFiltros: {}
        };

    } catch (e) {
        console.error("Erro na chamada de correla\u00e7\u00e3o:", e);
        mostrarToastAsync("Erro ao consultar os dados de correla\u00e7\u00e3o. Verifique o console.", "erro");
        return null;
    }
}


/**
 * Consulta os detalhes de um veículo via servlet.
 *
 * @param {string} dataHoraInicio - Data e hora inicial (formato ISO ou compatível)
 * @param {string} dataHoraFim - Data e hora final (formato ISO ou compatível)
 * @param {string} placa - Placa para análise (com ou sem traço)
 * @returns {Promise<Object|null>} Um único objeto com os detalhes do veículo ou null.
 */
async function obterDetalhesVeiculoAsync(dataHoraInicio, dataHoraFim, placa) {
    const placaNormalizada = (placa ?? "").replace(/-/g, "").toUpperCase();

    const params = new URLSearchParams({
        acao: "detalhes",
        placa: placaNormalizada,
        dataInicio: dataHoraInicio,
        dataFim: dataHoraFim
    });

	try {
        const resp = await fetch(`/MuralhaDigital/VeiculosCorrelacionados?${params.toString()}`);
        if (!resp.ok) {
             const errorText = await resp.text();
             throw new Error(`Erro na requisição de detalhes: ${resp.status} ${resp.statusText} - ${errorText}`);
        }

        const dados = await resp.json();

        if (!dados || dados.length === 0) {
            mostrarToastAsync("Nenhum detalhe encontrado para o veículo.", "alerta");
            return null;
        }

        return dados[0];

	    } catch (e) {
	        console.error("Erro na chamada de detalhes:", e);
	        mostrarToastAsync("Erro ao consultar os detalhes do veículo.", "erro");
	        return null;
	    }
}