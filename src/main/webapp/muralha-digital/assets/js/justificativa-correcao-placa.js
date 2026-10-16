/*
 * Pedido de motivo e justificativa para correção manual de placa (TR 5.7.5.2).
 * Usado pela consulta de veículos (modal de detalhe), pela correção em lote e pela fila de revisão.
 * Depende só de jQuery e Bootstrap 5 (carregados pelo cabeçalho).
 *
 *   JustificativaCorrecaoPlaca.pedir({ titulo: '...', resumo: '...' }, function (dados) {
 *       // dados = { idMotivo: 1, justificativa: '...' }
 *   });
 *   JustificativaCorrecaoPlaca.aviso('mensagem', 'sucesso' | 'erro' | 'atencao');
 */
var JustificativaCorrecaoPlaca = (function () {
    var URL = '/MuralhaDigital/CorrecaoPlaca';
    var motivos = null, minimo = 10, modal = null, modalAviso = null, aoConfirmar = null;

    function montar() {
        if (document.getElementById('modalJustificativaPlaca')) return;
        $('body').append(
            '<div class="modal fade" id="modalJustificativaPlaca" tabindex="-1" aria-labelledby="tituloJustificativaPlaca" aria-hidden="true">' +
            ' <div class="modal-dialog modal-dialog-centered"><div class="modal-content">' +
            '  <div class="modal-header"><h5 class="modal-title" id="tituloJustificativaPlaca"></h5>' +
            '   <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button></div>' +
            '  <div class="modal-body">' +
            '   <p class="small" id="resumoJustificativaPlaca"></p>' +
            '   <div class="mb-2"><label for="motivoJustificativaPlaca" class="form-label">Motivo <span class="text-danger">*</span></label>' +
            '    <select id="motivoJustificativaPlaca" class="form-select form-select-sm"></select></div>' +
            '   <div class="mb-2"><label for="textoJustificativaPlaca" class="form-label">Justificativa <span id="obrigJustificativaPlaca" class="text-danger d-none">*</span></label>' +
            '    <textarea id="textoJustificativaPlaca" class="form-control form-control-sm" rows="3" maxlength="500"></textarea>' +
            '    <div class="form-text" id="ajudaJustificativaPlaca"></div></div>' +
            '   <div class="alert alert-danger py-1 small d-none" id="erroJustificativaPlaca" role="alert"></div>' +
            '  </div>' +
            '  <div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>' +
            '   <button type="button" class="btn btn-primary" id="confirmarJustificativaPlaca"><i class="bi bi-check-lg"></i> Confirmar</button></div>' +
            ' </div></div></div>' +
            '<div class="modal fade" id="modalAvisoPlaca" tabindex="-1" aria-hidden="true">' +
            ' <div class="modal-dialog modal-dialog-centered modal-sm"><div class="modal-content">' +
            '  <div class="modal-body" id="textoAvisoPlaca"></div>' +
            '  <div class="modal-footer"><button type="button" class="btn btn-primary btn-sm" data-bs-dismiss="modal">OK</button></div>' +
            ' </div></div></div>');
        modal = new bootstrap.Modal(document.getElementById('modalJustificativaPlaca'), { backdrop: 'static' });
        modalAviso = new bootstrap.Modal(document.getElementById('modalAvisoPlaca'));
        $('#motivoJustificativaPlaca').on('change', atualizarObrigatoriedade);
        $('#confirmarJustificativaPlaca').on('click', confirmar);
    }

    function motivoSelecionado() {
        var id = $('#motivoJustificativaPlaca').val();
        return (motivos || []).filter(function (m) { return String(m.id) === id; })[0];
    }

    function atualizarObrigatoriedade() {
        var m = motivoSelecionado();
        var exige = m && m.exige_texto;
        $('#obrigJustificativaPlaca').toggleClass('d-none', !exige);
        $('#ajudaJustificativaPlaca').text(exige ? 'Obrigatória para este motivo (mínimo ' + minimo + ' caracteres).' : 'Opcional para este motivo.');
    }

    function confirmar() {
        var m = motivoSelecionado();
        var texto = $.trim($('#textoJustificativaPlaca').val());
        var $erro = $('#erroJustificativaPlaca');
        if (!m) { $erro.removeClass('d-none').text('Selecione o motivo.'); return; }
        if (m.exige_texto && texto.length < minimo) {
            $erro.removeClass('d-none').text('Descreva a justificativa (mínimo ' + minimo + ' caracteres).');
            return;
        }
        $erro.addClass('d-none');
        modal.hide();
        if (aoConfirmar) aoConfirmar({ idMotivo: m.id, justificativa: texto });
    }

    function carregarMotivos(depois) {
        if (motivos) { depois(); return; }
        $.ajax({
            type: 'GET', url: URL, dataType: 'json', data: { acao: 'motivos' },
            success: function (d) { motivos = d.motivos; minimo = d.justificativa_min || minimo; depois(); },
            error: function (jqXHR) {
                console.error('Erro ao carregar motivos de correção de placa', jqXHR.status);
                aviso('Não foi possível carregar os motivos de correção.', 'erro');
            }
        });
    }

    function pedir(opcoes, callback) {
        montar();
        carregarMotivos(function () {
            aoConfirmar = callback;
            $('#tituloJustificativaPlaca').text((opcoes && opcoes.titulo) || 'Justificativa da correção');
            $('#resumoJustificativaPlaca').text((opcoes && opcoes.resumo) || '');
            var $sel = $('#motivoJustificativaPlaca').empty().append($('<option value="">').text('-- Selecione --'));
            motivos.filter(function (m) { return !m.confirmacao || (opcoes && opcoes.permitirConferencia); })
                .forEach(function (m) { $sel.append($('<option>').val(m.id).text(m.descricao)); });
            $('#textoJustificativaPlaca').val('');
            $('#erroJustificativaPlaca').addClass('d-none');
            atualizarObrigatoriedade();
            modal.show();
        });
    }

    function aviso(texto, tipo) {
        montar();
        var icone = tipo === 'sucesso' ? 'bi-check-circle text-success' : tipo === 'erro' ? 'bi-x-circle text-danger' : 'bi-exclamation-triangle text-warning';
        $('#textoAvisoPlaca').empty().append($('<i class="bi me-2">').addClass(icone)).append(document.createTextNode(texto));
        modalAviso.show();
    }

    return { pedir: pedir, aviso: aviso };
})();
