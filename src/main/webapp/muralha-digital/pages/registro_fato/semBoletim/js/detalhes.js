$(document).ready(function() {
	
	const urlParams = new URLSearchParams(window.location.search);
    const idRegistro = urlParams.get('id');
	buscarPorId(idRegistro);
});

function buscarPorId(idRegistro) {
    const urlPesquisa = `/MuralhaDigital/RegistroDeFato?acao=buscarFatoSemBoletimPorId&idRegistroFato=${idRegistro}`;

    return $.ajax({
        type: "GET",
        url: urlPesquisa,
        dataType: "xml",
        success: function(data) {
            const $xml = $(data);

            // Pegando dados do registro_fato
            const registroFato = {
                id: $xml.find('registro_fato > id').text(),
                idTipo: $xml.find('registro_fato > idTipo').text(),
                idStatus: $xml.find('registro_fato > idStatus').text(),
                temBoletim: $xml.find('registro_fato > temBoletim').text(),
                idUsuario: $xml.find('registro_fato > idUsuario').text(),
                dataCriacao: $xml.find('registro_fato > dataCriacao').text(),
                privado: $xml.find('registro_fato > privado').text()
            };

            // Pegando objeto (apenas um no exemplo, pode haver vários)
            const objetos = [];
            $xml.find('objetos').each(function () {
                objetos.push({
                    idRegistroFato: $(this).find('id_registro_fato').text(),
                    tipo: $(this).find('tipo').text(),
                    descricao: $(this).find('descricao').text()
                });
            });

            // Pegando localização
            const localizacao = {
                cidadeId: $xml.find('localizacao > cidadeId').text(),
                tipoEnderecoEvento: $xml.find('localizacao > tipoEnderecoEvento').text(),
                cep: $xml.find('localizacao > cep').text(),
                bairro: $xml.find('localizacao > bairro').text(),
                rua: $xml.find('localizacao > rua').text(),
                numero: $xml.find('localizacao > numero').text(),
                complemento: $xml.find('localizacao > complemento').text(),
                latitude: $xml.find('localizacao > latitude').text(),
                longitude: $xml.find('localizacao > longitude').text()
            };

            console.log("Registro:", registroFato);
            console.log("Objetos:", objetos);
            console.log("Localização:", localizacao);

        },
        error: function() {
            $("body").removeClass("loading");
            alert('Erro ao processar requisição ao servidor!');
        }
    });
}