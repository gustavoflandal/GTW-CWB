async function BuscarInformacoes(valor, idTipoConsulta) {
    const url = "/MuralhaDigital/PesquisaRapida";
    const params = {
        valor: valor,
        idTipoConsulta: idTipoConsulta
    };

    $("body").addClass("loading");

    return await $.ajax({
        type: "GET",
        url: url,
        data: params,
        dataType: "text",
        success: function (responseText) {
            const $xml = $($.parseXML(responseText));

            const sucesso = $xml.find("sucesso").text();
            const msg = $xml.find("msgResposta").text();

            if (sucesso === "true" || sucesso === "") {
                return responseText;
            } else {
                Swal.fire({
                    icon: "error",
                    title: "Erro",
                    text: msg || "Erro na resposta da consulta."
                });
                return null;
            }
        },
        error: function () {
            Swal.fire({
                icon: "error",
                title: "Erro",
                text: "Erro ao processar requisição ao servidor!"
            });
            return null;
        },
        complete: function () {
            $("body").removeClass("loading");
        }
    });
}

/**
 * Busca sugestões de autocomplete no backend, com tratamento de erro via Swal.
 * @param {string} termo O texto a ser pesquisado.
 * @param {string} tipo O tipo de busca (ex: "nome").
 * @returns {Promise<string[]>} Retorna um array com as sugestões, ou um array vazio em caso de falha.
 */
async function BuscarSugestoesAutocomplete(termo, tipo) {
    const url = new URL('/MuralhaDigital/PesquisaRapida/Autocomplete', window.location.origin);
    url.searchParams.append('termo', termo);
    url.searchParams.append('tipo', tipo);

    try {
        const response = await fetch(url);

        if (!response.ok) {
            console.error(`Erro de rede ao buscar sugestões: ${response.statusText}`);
            return []; // Em erros de rede, falhamos silenciosamente para não interromper o usuário.
        }

        const xmlString = await response.text();
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, "application/xml");

        const sucessoNode = xmlDoc.querySelector("sucesso");

        // Caso de Sucesso: a tag <sucesso> existe e seu conteúdo é 'true'.
        if (sucessoNode && sucessoNode.textContent === 'true') {
            const suggestionNodes = xmlDoc.querySelectorAll("sugestoes item");
            const suggestions = Array.from(suggestionNodes).map(node => node.textContent);
            return suggestions;
        } 
        // Caso de Falha: o backend respondeu, mas indicou um erro.
        else {
            // Tenta encontrar uma mensagem de erro específica na resposta XML.
            const msgNode = xmlDoc.querySelector("msgResposta");
            const msg = msgNode ? msgNode.textContent : "Não foi possível carregar as sugestões.";

            // Mantendo o Swal.fire conforme solicitado.
            Swal.fire({
                icon: "error",
                title: "Erro",
                text: msg
            });

            console.error("A resposta do autocomplete não indicou sucesso. Mensagem: " + msg);
            return []; // Retorna um array vazio após exibir o erro.
        }

    } catch (error) {
        console.error("Erro crítico ao processar a requisição de autocomplete:", error);
        // Em erros de parsing ou outros, também falhamos silenciosamente.
        return [];
    }
}


