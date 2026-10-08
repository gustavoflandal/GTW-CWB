$(document).ready(function() {
	obterTipoEnvolvimento();
});

function obterTipoEnvolvimento() {
	var urlPesquisa = "/MuralhaDigital/Boletim/TipoIndividuo";

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
				const listaTipos = xmlDoc.getElementsByTagName('ListaTiposIndividuo')[0];
				const tipos = listaTipos.getElementsByTagName('IndividuoTipo');

				const select = document.getElementById('tipoEnvolvimento');
				select.innerHTML = "";

				const option = document.createElement('option');
				option.value = "";
				option.textContent = "Selecione";
				select.appendChild(option);

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
			ErrorNotification('Erro ao buscar tipos de envolvimento!',"");
		}
	});
}
