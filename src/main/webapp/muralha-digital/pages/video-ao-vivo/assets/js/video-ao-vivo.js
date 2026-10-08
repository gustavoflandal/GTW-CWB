$(document).ready(function () {
    setInterval(obterImg, 150);
});

function obterImg() {
    $.ajax({
        type: "GET",
        url: "http://10.0.2.112:8003/last-frame",
        dataType: "json",
        success: function (data) {
            let base64 = data.imagem.startsWith('data:')
                ? data.imagem
                : `data:image/jpeg;base64,${data.imagem}`;

            $('#iframe_tuc').attr('src', base64);
        },
        error: function () {
            console.log('Erro ao obter imagem');
        }
    });
}