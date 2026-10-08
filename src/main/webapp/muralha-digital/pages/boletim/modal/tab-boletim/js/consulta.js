$(document).ready(function() {
	obterTiposModalBoletins();
	obterModalSituacao();
	obterModalCidade();
});

function obterTiposModalBoletins() {
	var urlPesquisa = "/MuralhaDigital/Boletim/Tipo";

	$.ajax({
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
				const tipos = xmlDoc.getElementsByTagName('OcorrenciaTipo');
				const select = document.getElementById('selTipoBoletim');
				select.innerHTML = "";
				const option = document.createElement('option');
				option.value = 0;
				option.textContent = "Selecione";
				select.appendChild(option);
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
				ErrorNotification(msgResposta,"");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			ErrorNotification('Erro ao processar requisição ao servidor!!',"");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}

function obterModalSituacao() {
	var urlPesquisa = "/MuralhaDigital/Boletim/Situacao";

	$.ajax({
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
				const select = document.getElementById('selSituacaoBoletim');
				select.innerHTML = "";
				const option = document.createElement('option');
				option.value = 0;
				option.textContent = "Selecione";
				select.appendChild(option);
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
				ErrorNotification(msgResposta,"");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			ErrorNotification('Erro ao processar requisição ao servidor!!',"");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}



function obterModalCidade() {
	var urlPesquisa = "/MuralhaDigital/Boletim/Cidade";

	$.ajax({
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
				const listaSituacoes = xmlDoc.getElementsByTagName('ListaCidades')[0];
				const situacoes = listaSituacoes.getElementsByTagName('Cidade');
				const select = document.getElementById('selCidadeBoletim');
				select.innerHTML = "";
				const option = document.createElement('option');
				option.value = 0;
				option.textContent = "Selecione";
				select.appendChild(option);
				// Adiciona cada situação como uma opção
				for (let i = 0; i < situacoes.length; i++) {
					const situacao = situacoes[i];
					const id = situacao.getElementsByTagName('id')[0].textContent;
					const descricao = situacao.getElementsByTagName('nome')[0].textContent;

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
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}