/**
 * Cliente responsável por realizar chamadas HTTP para o servlet
 * /MuralhaDigital/PerfilComportamental, que fornece dados sobre o
 * perfil comportamental de veículos (passagens, rotas, mapas de calor etc.).
 */
class ApiClient {

    /**
     * Define a rota base para o servlet do perfil comportamental.
     */
    constructor() {
        this.baseUrl = '/MuralhaDigital/PerfilComportamental';
    }

    /**
     * Executa uma chamada GET genérica ao servlet, enviando todos os parâmetros necessários.
     * @param {string} placa - Placa do veículo consultado.
     * @param {string} dataInicio - Data inicial no formato yyyy-MM-dd.
     * @param {string} dataFim - Data final no formato yyyy-MM-dd.
     * @param {string} tipoConsulta - Tipo da consulta: 'pordia', 'porhora', 'porpcl' ou 'rotas'.
     * @param {number|null} idMancha - (Opcional) ID da mancha para filtrar a consulta.
     * @returns {Promise<Array<Object>>} Lista de objetos retornados pelo servlet em formato JSON.
     * @throws {Error} Em caso de falha HTTP (status diferente de 200).
     */
    async fetchPerfilComportamental(placa, dataInicio, dataFim, tipoConsulta, idMancha = null, horaInicio = '00:00', horaFim = '23:59') {
        const params = new URLSearchParams({ placa, dataInicio, dataFim, horaInicio, horaFim, tipoConsulta });
        if (idMancha) { params.append('idMancha', idMancha); }
        const response = await fetch(`${this.baseUrl}?${params}`);
        if (!response.ok) { throw new Error(`Erro na requisição: ${response.status}`); }
        return await response.json();
    }

    //Busca a lista de passagens individuais (sem coordenadas)
    async fetchPassagensIndividuais(placa, dataInicio, dataFim, horaInicio, horaFim) {
        return this.fetchPerfilComportamental(placa, dataInicio, dataFim, 'passagensindividuais', null, horaInicio, horaFim);
    }

    // Funções antigas que continuam a ser usadas
    async fetchInformacoesVeiculo(placa, dataInicio, dataFim, horaInicio, horaFim) {
        return this.fetchPerfilComportamental(placa, dataInicio, dataFim, 'info', null, horaInicio, horaFim);
    }

    /**
     * Consulta agrupada por dia da semana, com dados das passagens por local.
     * PODE SER FILTRADA POR MANCHA.
     */
    async fetchPassagensPorDia(placa, dataInicio, dataFim, horaInicio, horaFim, idMancha = null) {
        return this.fetchPerfilComportamental(placa, dataInicio, dataFim, 'pordia', idMancha, horaInicio, horaFim);
    }

    /**
     * Consulta agrupada por dia da semana e hora, útil para análise temporal mais granular.
     */
    async fetchPassagensPorDiaHora(placa, dataInicio, dataFim, horaInicio, horaFim) {
        return this.fetchPerfilComportamental(placa, dataInicio, dataFim, 'porhora', null, horaInicio, horaFim);
    }

    /**
     * Consulta de total de passagens por PCL (local de leitura), com informações de área monitorada.
     */
    async fetchPassagensPorPcl(placa, dataInicio, dataFim, horaInicio, horaFim) {
        return this.fetchPerfilComportamental(placa, dataInicio, dataFim, 'porpcl', null, horaInicio, horaFim);
    }

    /**
     * Consulta o tempo total de estadia do veículo entre áreas monitoradas (manchas),
     * agrupando por mancha de entrada e mancha de saída.
     */
    async fetchEstadiaPorManchas(placa, dataInicio, dataFim, horaInicio, horaFim) {
        return this.fetchPerfilComportamental(placa, dataInicio, dataFim, 'permanencia', null, horaInicio, horaFim);
    }
}

// Cria a instância global que será usada pelo script.js
const apiClient = new ApiClient();