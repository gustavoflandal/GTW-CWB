/**
 * Captura de impressao/exportacao disparada no NAVEGADOR (cliente), em TODAS as telas.
 *  - Ctrl+P / menu de impressao (beforeprint)   -> IMPRESSAO ("Impressao de tela")
 *  - PDF no cliente (jsPDF)                      -> IMPRESSAO (formato "PDF")
 *  - Planilha/CSV no cliente (SheetJS / Blob)    -> EXPORTACAO (formato "XLSX"/"CSV")
 *
 * Intercepta em varios pontos (beforeprint, URL.createObjectURL por tipo de blob,
 * XLSX.writeFile, jsPDF.save, <a download>) e deduplica por nivel|formato, para
 * cobrir qualquer mecanismo de download sem gravar o mesmo evento duas vezes.
 * Envia POST /MuralhaDigital/Impressao. Blindado: nunca lanca nem atrapalha o usuario.
 */
(function () {
    'use strict';

    var recentes = {}; // chave (nivel|formato) -> timestamp

    function enviar(nivel, formato) {
        try {
            var n = nivel || 'IMPRESSAO';
            var f = formato || 'Impressao de tela';
            var agora = Date.now();
            var k = n + '|' + f;
            if (recentes[k] && (agora - recentes[k]) < 1500) { return; } // dedup da mesma acao
            recentes[k] = agora;

            var raiz = (typeof urlRoot !== 'undefined' && urlRoot) ? urlRoot : '/';
            var url = raiz + 'MuralhaDigital/Impressao';
            var params = new URLSearchParams();
            params.set('telaOrigem', location.pathname);
            params.set('formato', f);
            params.set('nivel', n);

            var ok = false;
            try { if (navigator.sendBeacon) { ok = navigator.sendBeacon(url, params); } } catch (e) { ok = false; }
            if (!ok && window.fetch) {
                fetch(url, {
                    method: 'POST', keepalive: true, credentials: 'same-origin',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: params.toString()
                }).catch(function () { /* ignora */ });
            }
        } catch (e) { /* nunca interferir */ }
    }

    // Classifica pelo tipo MIME do blob.
    function porTipo(mime) {
        var t = (mime || '').toLowerCase();
        if (t.indexOf('application/pdf') === 0) { return { nivel: 'IMPRESSAO',  formato: 'PDF' }; }
        if (t.indexOf('text/csv') === 0 || t.indexOf('application/csv') === 0) { return { nivel: 'EXPORTACAO', formato: 'CSV' }; }
        if (t.indexOf('spreadsheet') >= 0 || t.indexOf('ms-excel') >= 0 || t.indexOf('excel') >= 0) { return { nivel: 'EXPORTACAO', formato: 'XLSX' }; }
        return null;
    }

    // Classifica pela extensao do arquivo.
    function porArquivo(nome) {
        var s = (nome || '').toLowerCase();
        if (s.slice(-4) === '.pdf')  { return { nivel: 'IMPRESSAO',  formato: 'PDF' }; }
        if (s.slice(-4) === '.csv')  { return { nivel: 'EXPORTACAO', formato: 'CSV' }; }
        if (s.slice(-5) === '.xlsx' || s.slice(-4) === '.xls') { return { nivel: 'EXPORTACAO', formato: 'XLSX' }; }
        return null;
    }

    // 1) Impressao nativa (Ctrl+P / menu do navegador).
    try {
        window.addEventListener('beforeprint', function () { enviar('IMPRESSAO', 'Impressao de tela'); });
    } catch (e) { /* ignora */ }

    // 2) URL.createObjectURL: ponto comum a praticamente todo download de arquivo no cliente
    //    (jsPDF.save, SheetJS, Blob+ancora). Classifica pelo tipo do blob.
    try {
        if (typeof URL !== 'undefined' && URL.createObjectURL && !URL.__logWrap) {
            var createOrig = URL.createObjectURL;
            URL.createObjectURL = function (obj) {
                try {
                    if (obj && obj.type) { var c = porTipo(obj.type); if (c) { enviar(c.nivel, c.formato); } }
                } catch (e) { /* ignora */ }
                return createOrig.apply(this, arguments);
            };
            URL.__logWrap = true;
        }
    } catch (e) { /* ignora */ }

    // 3) Download via <a download="..."> (reforco para libs que nao usam createObjectURL).
    try {
        var clickOrig = HTMLAnchorElement.prototype.click;
        HTMLAnchorElement.prototype.click = function () {
            try {
                var nome = this.getAttribute ? this.getAttribute('download') : null;
                if (nome) { var c = porArquivo(nome); if (c) { enviar(c.nivel, c.formato); } }
            } catch (e) { /* ignora */ }
            return clickOrig.apply(this, arguments);
        };
    } catch (e) { /* ignora */ }

    // 4) Wrappers deterministicos das libs (reforco; dedup evita duplicar com (2)/(3)).
    function instalar() {
        try {
            var J = window.jspdf && window.jspdf.jsPDF;
            if (J && J.prototype && !J.prototype.__logSaveWrap) {
                var saveOrig = J.prototype.save;
                J.prototype.save = function (nome) {
                    try { enviar('IMPRESSAO', 'PDF'); } catch (e) { /* ignora */ }
                    return saveOrig.apply(this, arguments);
                };
                J.prototype.__logSaveWrap = true;
            }
        } catch (e) { /* ignora */ }
        try {
            if (window.XLSX && typeof window.XLSX.writeFile === 'function' && !window.XLSX.__logWriteWrap) {
                var wfOrig = window.XLSX.writeFile;
                window.XLSX.writeFile = function (wb, nome) {
                    try { var c = porArquivo(nome) || { nivel: 'EXPORTACAO', formato: 'XLSX' }; enviar(c.nivel, c.formato); } catch (e) { /* ignora */ }
                    return wfOrig.apply(this, arguments);
                };
                window.XLSX.__logWriteWrap = true;
            }
        } catch (e) { /* ignora */ }
    }
    try { instalar(); } catch (e) { /* ignora */ }
    try { window.addEventListener('DOMContentLoaded', instalar); } catch (e) { /* ignora */ }
    try { window.addEventListener('load', instalar); } catch (e) { /* ignora */ }
})();
