document.addEventListener('DOMContentLoaded', () => {
	obterTiposIndividuoComBoletim()
});

function obterTiposIndividuoComBoletim() {
    const url = "/MuralhaDigital/RegistroDeFato/IndividuoTipo";
    const params = new URLSearchParams({
        acao: 'obterLista',
    });

    $.ajax({
        type: "GET",
        url: url,
        data: params.toString(),
        dataType: "xml",
        success: function (data, textStatus, jqXHR) {
            try {
                const xmlDoc = new DOMParser().parseFromString(jqXHR.responseText, "application/xml");

                const registros = xmlDoc.getElementsByTagName("RegistroDeFato");
                const select = document.getElementById("tipoEnvolvimentoComBoletim");

                // Limpa e adiciona a opção padrão
                select.innerHTML = '<option value="" selected>Selecione</option>';

                const idsAdicionados = new Set(); // Para evitar duplicatas

                for (let i = 0; i < registros.length; i++) {
                    const registro = registros[i];

                    const idNode = registro.getElementsByTagName("id")[0];
                    const descricaoNode = registro.getElementsByTagName("descricao")[0];

                    if (idNode && descricaoNode) {
                        const id = idNode.textContent.trim();
                        const descricao = descricaoNode.textContent.trim();

                        if (!idsAdicionados.has(id)) {
                            const option = document.createElement("option");
                            option.value = id;
                            option.textContent = descricao;
                            select.appendChild(option);

                            idsAdicionados.add(id);
                        }
                    }
                }

                if (idsAdicionados.size === 0) {
                    Swal.fire("Atenção", "Nenhum tipo de indivíduo encontrado.", "warning");
                }
            } catch (e) {
                Swal.fire("Erro", "Erro ao processar dados recebidos.", "error");
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire("Erro", "Erro ao processar requisição ao servidor!", "error");
        }
    });
}



