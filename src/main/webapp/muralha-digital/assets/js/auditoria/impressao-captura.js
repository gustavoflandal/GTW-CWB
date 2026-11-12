/**
 * Captura de impressao de tela pelo navegador (Ctrl+P / menu).
 * Envia um registro ao motor de logs (POST /MuralhaDigital/Impressao).
 * Blindado: nunca lanca excecao nem atrapalha a impressao do usuario.
 */
(function () {
    'use strict';
    var aguardando = false;

    function registrar() {
        try {
            if (aguardando) { return; }
            aguardando = true;
            setTimeout(function () { aguardando = false; }, 1000);

            var raiz = (typeof urlRoot !== 'undefined' && urlRoot) ? urlRoot : '/';
            var url = raiz + 'MuralhaDigital/Impressao';
            var params = new URLSearchParams();
            params.set('telaOrigem', location.pathname);
            params.set('formato', 'Impressao de tela');

            if (navigator.sendBeacon) {
                navigator.sendBeacon(url, params);
            } else if (window.fetch) {
                fetch(url, {
                    method: 'POST',
                    keepalive: true,
                    credentials: 'same-origin',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: params.toString()
                }).catch(function () { /* ignora */ });
            }
        } catch (e) { /* nunca interferir na impressao */ }
    }

    try {
        window.addEventListener('beforeprint', registrar);
    } catch (e) { /* ignora */ }
})();
