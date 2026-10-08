document.addEventListener("DOMContentLoaded", function () {
    obterTiposEvento();
	obterCidades();
});

function obterCidades() {
	const url = "/MuralhaDigital/RegistroDeFato/Cidade";

	$.ajax({
		type: "GET",
		url: url,
		dataType: "xml",
		success: function (data, textStatus, jqXHR) {
			const xmlDoc = new DOMParser().parseFromString(jqXHR.responseText, "application/xml");
			const cidades = xmlDoc.getElementsByTagName("Cidade");
			const select = document.getElementById("idCidadeComBoletim");

			select.innerHTML = '<option value="">Selecione</option>';

			for (let i = 0; i < cidades.length; i++) {
				const cidade = cidades[i];
				const id = cidade.getElementsByTagName("id")[0].textContent;
				const nome = cidade.getElementsByTagName("nome")[0].textContent;

				const option = document.createElement("option");
				option.value = id;
				option.textContent = nome;
				select.appendChild(option);
			}
		},
		error: function () {
			ErrorNotification("Erro ao carregar as cidades.", "");
		}
	});
}

function obterTiposEvento() {
    const url = "/MuralhaDigital/RegistroDeFato/EnderecoEvento";

    $.ajax({
        type: "GET",
        url: url,
        dataType: "xml",
        success: function (data, textStatus, jqXHR) {
            const xmlDoc = new DOMParser().parseFromString(jqXHR.responseText, "application/xml");
            const tiposEvento = xmlDoc.getElementsByTagName("RegistroDeFatoEnderecoEvento");
            const select = document.getElementById("idTipoEventoComBoletim");

            select.innerHTML = '<option value="">Selecione</option>';

            for (let i = 0; i < tiposEvento.length; i++) {
                const tipoEvento = tiposEvento[i];
                const id = tipoEvento.getElementsByTagName("id")[0].textContent;
                const nome = tipoEvento.getElementsByTagName("descricao")[0].textContent;

                const option = document.createElement("option");
                option.value = id;
                option.textContent = nome;

                if (id === '1') {
                    option.selected = true;
                }

                select.appendChild(option);
            }
        },
        error: function () {
            ErrorNotification("Erro ao carregar os tipos de evento.", "");
        }
    });
}

