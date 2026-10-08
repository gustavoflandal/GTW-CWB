$(document).ready(function() {
	obterTiposRegistrosDeFatosComBoletim();
	obterSituacaoComBoletim();
});

function obterTiposRegistrosDeFatosComBoletim() {
	var urlPesquisa = "/MuralhaDigital/RegistroDeFato/Tipo";

	return $.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				const parser = new DOMParser();
				const xmlDoc = parser.parseFromString(jqXHR.responseText, "application/xml");
				const tipos = xmlDoc.getElementsByTagName('RegistroDeFatoTipo');
				const select = document.getElementById('id_tipoComBoletim');

				// Limpa opções antigas mantendo a primeira ("Selecione")
				select.innerHTML = '<option value="0" selected>Selecione</option>';

				// Adiciona cada tipo na ordem recebida
				for (let i = 0; i < tipos.length; i++) {
					const tipo = tipos[i];
					const id = tipo.getElementsByTagName('id')[0].textContent;
					const descricao = tipo.getElementsByTagName('descricao')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
				}
			} else {
				ErrorNotification(msgResposta, "");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading");
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}

function obterSituacaoComBoletim() {
	var urlPesquisa = "/MuralhaDigital/RegistroDeFato/Situacao";

	return $.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				const parser = new DOMParser();
				const listaSituacoes = xmlDoc.getElementsByTagName('ListaSituacoes')[0];
				const situacoes = listaSituacoes.getElementsByTagName('Situacao');
				const select = document.getElementById('id_statusComBoletim');

				// Limpa opções antigas mantendo a padrão
				select.innerHTML = '<option value="0" selected>Selecione</option>';

				// Adiciona cada situação como uma opção
				for (let i = 0; i < situacoes.length; i++) {
					const situacao = situacoes[i];
					const id = situacao.getElementsByTagName('id')[0].textContent;
					const descricao = situacao.getElementsByTagName('descricao')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
				}
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading");
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}

/**
 * Busca as naturezas de um tipo e, opcionalmente, seleciona e bloqueia o campo.
 * @param {string} idTipo - O ID do tipo selecionado.
 * @param {string|null} idParaSelecionar - (Opcional) O ID da natureza a ser selecionada.
 * @param {boolean} bloquearAposCarregar - (Opcional) Se true, desabilita o select após o carregamento.
 */
function obterNaturezasPorTipo(idTipo,idParaSelecionar = null, bloquearAposCarregar = false) {
    // A URL do seu novo servlet, passando o idTipo como parâmetro
    var urlPesquisa = "/MuralhaDigital/RegistroDeFato/Natureza?idTipo=" + idTipo;
    const selectNatureza = document.getElementById('id_naturezaTipo');

    // Limpa o select e mostra uma mensagem de "Carregando"
    selectNatureza.innerHTML = '<option value="0">Carregando...</option>';
    selectNatureza.disabled = true;

    return $.ajax({
        type: "GET",
        url: urlPesquisa,
        dataType: "xml",
        success: function(data, textStatus, jqXHR) {
            var event = jqXHR.responseText;
            var xmlDoc = $.parseXML(event);
            var $xml = $(xmlDoc);

            // AJUSTE 1: O caminho para encontrar as naturezas mudou.
            // Agora procuramos por RegistroDeFatoNatureza DENTRO de RegistroDeFatoNaturezaLista.
            var naturezas = $xml.find('RegistroDeFatoNaturezaLista RegistroDeFatoNatureza');

            if (naturezas.length > 0) {
                // Limpa as opções e adiciona a padrão "Selecione"
                selectNatureza.innerHTML = '<option value="0" selected>Selecione</option>';

                // Itera sobre cada tag <RegistroDeFatoNatureza> no XML
                naturezas.each(function() {
                    const id = $(this).find('id').text();
                    
                    // AJUSTE 2: O nome da tag de descrição mudou para 'naturezaDesc'.
                    const descricao = $(this).find('naturezaDesc').text();

                    const option = document.createElement('option');
                    option.value = id;
                    option.textContent = descricao;
                    selectNatureza.appendChild(option);
                });

				selectNatureza.disabled = bloquearAposCarregar;

				if (idParaSelecionar) {
				    selectNatureza.value = idParaSelecionar;
				}

            } else {
                 // Se não encontrou naturezas, verifica se foi uma resposta de erro padrão
                var sucesso = $xml.find('sucesso').text();
                if (sucesso === 'false') {
                    var msgResposta = $xml.find('msgResposta').text();
                    ErrorNotification(msgResposta, "");
                    selectNatureza.innerHTML = '<option value="0">Erro ao carregar</option>';
                } else {
                    // Não há erro, mas a lista veio vazia
                    selectNatureza.innerHTML = '<option value="0">Nenhuma natureza encontrada</option>';
                    // Mantém desabilitado se não houver opções
                    selectNatureza.disabled = true;
                }
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $("body").removeClass("loading");
            selectNatureza.innerHTML = '<option value="0">Erro na requisição</option>';
            ErrorNotification('Erro ao processar requisição ao servidor!!', "");
        },
        beforeSend: function(jqXHR, settings) { },
        complete: function(jqXHR, textStatus) { }
    });
}