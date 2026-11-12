let modalRegra;

$(function() {
    modalRegra = new bootstrap.Modal(document.getElementById('modalRegra'));
    carregarTipos();
    listar();
});

function carregarTipos() {
    $.getJSON('/MuralhaDigital/RegraLog', { acao: 'listarTipos' })
     .done(function(data) {
        const box = $('#tiposEvento').empty();
        if (!data.ok) { box.append('<div class="text-danger small">' + escaparHtml(data.erro) + '</div>'); return; }
        data.tipos.forEach(function(t) {
            box.append(
                '<div class="col-md-4 col-lg-3">' +
                '<div class="form-check form-switch">' +
                '<input class="form-check-input tipo-chk" type="checkbox" value="' + escaparHtml(t.codigo) + '" id="tipo_' + escaparHtml(t.codigo) + '"' + (t.capturar ? ' checked' : '') + '>' +
                '<label class="form-check-label" for="tipo_' + escaparHtml(t.codigo) + '"><b>' + escaparHtml(t.codigo) + '</b><br><span class="small text-muted">' + escaparHtml(t.descricao) + '</span></label>' +
                '</div></div>'
            );
        });
     })
     .fail(function() { $('#tiposEvento').html('<div class="text-danger small">Falha ao carregar tipos.</div>'); });
}

function salvarTipos() {
    var ativos = [];
    $('.tipo-chk:checked').each(function() { ativos.push($(this).val()); });
    $.post('/MuralhaDigital/RegraLog', { acao: 'salvarTipos', ativos: ativos.join(',') }, function(data) {
        if (!data.ok) { Swal.fire('Erro', data.erro || 'Falha', 'error'); return; }
        Swal.fire({ icon: 'success', title: 'Tipos de evento salvos', timer: 1200, showConfirmButton: false });
    }, 'json').fail(function() { Swal.fire('Erro', 'Falha ao salvar tipos.', 'error'); });
}

function escaparHtml(v) { var d = document.createElement('div'); d.textContent = v == null ? '' : String(v); return d.innerHTML.replace(/"/g, '&quot;').replace(/'/g, '&#39;'); }
function simNao(b, cor) { return b ? '<span class="badge ' + (cor || 'bg-success') + '">Sim</span>' : '<span class="badge bg-light text-dark">Não</span>'; }

function listar() {
    $.getJSON('/MuralhaDigital/RegraLog', { acao: 'listar' })
     .done(function(data) {
        const tb = $('#tbodyRegras').empty();
        if (!data.ok) { tb.append('<tr><td colspan="11" class="text-danger">' + escaparHtml(data.erro) + '</td></tr>'); return; }
        if (!data.regras.length) { tb.append('<tr><td colspan="11" class="text-muted text-center">Nenhuma regra.</td></tr>'); return; }
        data.regras.forEach(function(r) {
            tb.append(
                '<tr>' +
                '<td>' + r.ordem + '</td>' +
                '<td>' + escaparHtml(r.descricao) + '</td>' +
                '<td><code>' + escaparHtml(r.padraoRota) + '</code></td>' +
                '<td>' + escaparHtml(r.metodo || '*') + '</td>' +
                '<td>' + simNao(r.capturar, 'bg-primary') + '</td>' +
                '<td>' + escaparHtml(r.nivel) + '</td>' +
                '<td>' + simNao(r.registrarParametros) + '</td>' +
                '<td><small>' + escaparHtml(r.mascararCampos || '-') + '</small></td>' +
                '<td>' + simNao(r.garantido, 'bg-warning text-dark') + '</td>' +
                '<td>' + simNao(r.ativo) + '</td>' +
                '<td class="text-nowrap">' +
                  '<button class="btn btn-sm btn-outline-primary me-1" onclick="editar(' + r.id + ')"><i class="bi bi-pencil"></i></button>' +
                  '<button class="btn btn-sm btn-outline-danger" onclick="excluir(' + r.id + ')"><i class="bi bi-trash"></i></button>' +
                '</td>' +
                '</tr>'
            );
            $('#tbodyRegras tr:last').data('regra', r);
        });
     })
     .fail(function() { Swal.fire('Erro', 'Falha ao listar regras.', 'error'); });
}

function novaRegra() {
    $('#rId').val(''); $('#rOrdem').val(100); $('#rDescricao').val(''); $('#rPadrao').val('');
    $('#rMetodo').val(''); $('#rCapturar').val('1'); $('#rNivel').val('AUTO'); $('#rFuncionalidade').val('');
    $('#rMascarar').val(''); $('#rParametros').prop('checked', false); $('#rGarantido').prop('checked', false);
    $('#rAtivo').prop('checked', true);
    $('#modalTitulo').text('Nova regra de captura');
    modalRegra.show();
}

function editar(id) {
    var r = null;
    $('#tbodyRegras tr').each(function() { var d = $(this).data('regra'); if (d && d.id === id) r = d; });
    if (!r) return;
    $('#rId').val(r.id); $('#rOrdem').val(r.ordem); $('#rDescricao').val(r.descricao); $('#rPadrao').val(r.padraoRota);
    $('#rMetodo').val(r.metodo || ''); $('#rCapturar').val(r.capturar ? '1' : '0'); $('#rNivel').val(r.nivel || 'AUTO');
    $('#rFuncionalidade').val(r.funcionalidade || ''); $('#rMascarar').val(r.mascararCampos || '');
    $('#rParametros').prop('checked', !!r.registrarParametros); $('#rGarantido').prop('checked', !!r.garantido);
    $('#rAtivo').prop('checked', !!r.ativo);
    $('#modalTitulo').text('Editar regra #' + r.id);
    modalRegra.show();
}

function salvarRegra() {
    var padrao = $('#rPadrao').val().trim();
    if (!padrao) { Swal.fire('Atenção', 'Informe o padrão de rota.', 'warning'); return; }
    $.post('/MuralhaDigital/RegraLog', {
        acao: 'salvar',
        id: $('#rId').val(),
        ordem: $('#rOrdem').val(),
        descricao: $('#rDescricao').val(),
        padraoRota: padrao,
        metodo: $('#rMetodo').val(),
        capturar: $('#rCapturar').val(),
        nivel: $('#rNivel').val(),
        funcionalidade: $('#rFuncionalidade').val(),
        mascararCampos: $('#rMascarar').val(),
        registrarParametros: $('#rParametros').is(':checked') ? '1' : '0',
        garantido: $('#rGarantido').is(':checked') ? '1' : '0',
        ativo: $('#rAtivo').is(':checked') ? '1' : '0'
    }, function(data) {
        if (!data.ok) { Swal.fire('Erro', data.erro || 'Falha ao salvar', 'error'); return; }
        modalRegra.hide();
        Swal.fire({ icon: 'success', title: 'Regra salva', timer: 1200, showConfirmButton: false });
        listar();
    }, 'json').fail(function() { Swal.fire('Erro', 'Falha ao salvar.', 'error'); });
}

function excluir(id) {
    Swal.fire({ title: 'Excluir regra?', icon: 'warning', showCancelButton: true,
                confirmButtonText: 'Excluir', cancelButtonText: 'Cancelar', confirmButtonColor: '#dc3545' })
    .then(function(res) {
        if (!res.isConfirmed) return;
        $.post('/MuralhaDigital/RegraLog', { acao: 'excluir', id: id }, function(data) {
            if (!data.ok) { Swal.fire('Erro', data.erro || 'Falha', 'error'); return; }
            Swal.fire({ icon: 'success', title: 'Excluída', timer: 1000, showConfirmButton: false });
            listar();
        }, 'json');
    });
}

function recarregarCache() {
    $.post('/MuralhaDigital/RegraLog', { acao: 'recarregar' }, function(data) {
        Swal.fire({ icon: 'success', title: 'Cache recarregado', timer: 1000, showConfirmButton: false });
    }, 'json');
}
