let modalPol;

$(function() {
    modalPol = new bootstrap.Modal(document.getElementById('modalPol'));
    listar();
    listarExpurgos();
});

function escaparHtml(v) { var d = document.createElement('div'); d.textContent = v == null ? '' : String(v); return d.innerHTML.replace(/"/g, '&quot;').replace(/'/g, '&#39;'); }
function simNao(b, cor) { return b ? '<span class="badge ' + (cor || 'bg-success') + '">Sim</span>' : '<span class="badge bg-light text-dark">Não</span>'; }

function listar() {
    $.getJSON('/MuralhaDigital/RetencaoLog', { acao: 'listar' })
     .done(function(data) {
        const tb = $('#tbodyPol').empty();
        if (!data.ok) { tb.append('<tr><td colspan="7" class="text-danger">' + escaparHtml(data.erro) + '</td></tr>'); return; }
        $('#dirArquivo').text(data.diretorioArquivo || '-');
        if (!data.politicas.length) { tb.append('<tr><td colspan="7" class="text-muted text-center">Nenhuma política.</td></tr>'); return; }
        data.politicas.forEach(function(p) {
            tb.append(
                '<tr>' +
                '<td>' + escaparHtml(p.nivel || '(padrão)') + '</td>' +
                '<td>' + p.diasRetencao + ' dias</td>' +
                '<td>' + simNao(p.arquivarAntes) + '</td>' +
                '<td>' + simNao(p.holdLegal, 'bg-danger') + '</td>' +
                '<td>' + simNao(p.ativo) + '</td>' +
                '<td><small>' + escaparHtml(p.descricao || '') + '</small></td>' +
                '<td class="text-nowrap">' +
                  '<button class="btn btn-sm btn-outline-primary me-1" onclick="editar(' + p.id + ')"><i class="bi bi-pencil"></i></button>' +
                  '<button class="btn btn-sm btn-outline-danger" onclick="excluir(' + p.id + ')"><i class="bi bi-trash"></i></button>' +
                '</td>' +
                '</tr>'
            );
            $('#tbodyPol tr:last').data('pol', p);
        });
     })
     .fail(function() { Swal.fire('Erro', 'Falha ao listar políticas.', 'error'); });
}

function listarExpurgos() {
    $.getJSON('/MuralhaDigital/RetencaoLog', { acao: 'listarExpurgos' })
     .done(function(data) {
        const tb = $('#tbodyExp').empty();
        if (!data.ok || !data.expurgos.length) { tb.append('<tr><td colspan="6" class="text-muted text-center">Nenhum expurgo registrado.</td></tr>'); return; }
        data.expurgos.forEach(function(e) {
            tb.append(
                '<tr>' +
                '<td>' + escaparHtml(e.dtExecucao) + '</td>' +
                '<td>' + escaparHtml(e.nivel || '(padrão)') + '</td>' +
                '<td>' + e.qtdRemovida + '</td>' +
                '<td><small>' + escaparHtml(e.arquivo || '-') + '</small></td>' +
                '<td>' + escaparHtml(e.login || '-') + '</td>' +
                '<td><small>' + escaparHtml(e.observacao || '') + '</small></td>' +
                '</tr>'
            );
        });
     });
}

function novaPolitica() {
    $('#pId').val(''); $('#pNivel').val(''); $('#pDias').val(730); $('#pDescricao').val('');
    $('#pArquivar').prop('checked', true); $('#pHold').prop('checked', false); $('#pAtivo').prop('checked', true);
    $('#modalTituloPol').text('Nova política de retenção');
    modalPol.show();
}

function editar(id) {
    var p = null;
    $('#tbodyPol tr').each(function() { var d = $(this).data('pol'); if (d && d.id === id) p = d; });
    if (!p) return;
    $('#pId').val(p.id); $('#pNivel').val(p.nivel || ''); $('#pDias').val(p.diasRetencao); $('#pDescricao').val(p.descricao || '');
    $('#pArquivar').prop('checked', !!p.arquivarAntes); $('#pHold').prop('checked', !!p.holdLegal); $('#pAtivo').prop('checked', !!p.ativo);
    $('#modalTituloPol').text('Editar política #' + p.id);
    modalPol.show();
}

function salvarPolitica() {
    $.post('/MuralhaDigital/RetencaoLog', {
        acao: 'salvar',
        id: $('#pId').val(),
        nivel: $('#pNivel').val(),
        diasRetencao: $('#pDias').val(),
        descricao: $('#pDescricao').val(),
        arquivarAntes: $('#pArquivar').is(':checked') ? '1' : '0',
        holdLegal: $('#pHold').is(':checked') ? '1' : '0',
        ativo: $('#pAtivo').is(':checked') ? '1' : '0'
    }, function(data) {
        if (!data.ok) { Swal.fire('Erro', data.erro || 'Falha', 'error'); return; }
        modalPol.hide();
        Swal.fire({ icon: 'success', title: 'Política salva', timer: 1200, showConfirmButton: false });
        listar();
    }, 'json');
}

function excluir(id) {
    Swal.fire({ title: 'Excluir política?', icon: 'warning', showCancelButton: true,
                confirmButtonText: 'Excluir', cancelButtonText: 'Cancelar', confirmButtonColor: '#dc3545' })
    .then(function(res) {
        if (!res.isConfirmed) return;
        $.post('/MuralhaDigital/RetencaoLog', { acao: 'excluir', id: id }, function(data) {
            if (!data.ok) { Swal.fire('Erro', data.erro || 'Falha', 'error'); return; }
            listar();
        }, 'json');
    });
}

function gerarSelo() {
    $.post('/MuralhaDigital/RetencaoLog', { acao: 'gerarSelo' }, function(data) {
        if (!data.ok) { Swal.fire('Erro', data.erro || 'Falha', 'error'); return; }
        Swal.fire({ icon: 'success', title: 'Selos gerados: ' + data.selosGerados });
    }, 'json');
}

function simular() {
    Swal.fire({ title: 'Simulando...', didOpen: function() { Swal.showLoading(); }, allowOutsideClick: false });
    $.getJSON('/MuralhaDigital/RetencaoLog', { acao: 'simular' }, function(data) {
        mostrarResultado(data, true);
    }).fail(function() { Swal.fire('Erro', 'Falha na simulação.', 'error'); });
}

function executar() {
    Swal.fire({
        title: 'Executar expurgo?',
        html: 'Esta ação remove definitivamente os eventos selados fora do prazo de retenção.<br>Os registros com <b>Arquivar antes</b> serão exportados antes.',
        icon: 'warning', showCancelButton: true, confirmButtonText: 'Executar', cancelButtonText: 'Cancelar',
        confirmButtonColor: '#dc3545'
    }).then(function(res) {
        if (!res.isConfirmed) return;
        Swal.fire({ title: 'Expurgando...', didOpen: function() { Swal.showLoading(); }, allowOutsideClick: false });
        $.post('/MuralhaDigital/RetencaoLog', { acao: 'executarExpurgo' }, function(data) {
            mostrarResultado(data, false);
            listar(); listarExpurgos();
        }, 'json').fail(function() { Swal.fire('Erro', 'Falha no expurgo.', 'error'); });
    });
}

function mostrarResultado(data, simulacao) {
    if (!data.ok) { Swal.fire('Erro', data.erro || 'Falha', 'error'); return; }
    var html = '<div class="text-start"><p><b>' + (simulacao ? 'Elegíveis para expurgo' : 'Total removido') + ':</b> ' +
               (simulacao ? somaElegiveis(data) : data.totalRemovido) + '</p><ul>';
    (data.escopos || []).forEach(function(e) {
        html += '<li><b>' + escaparHtml(e.nivel) + '</b> (' + e.diasRetencao + 'd): ' +
                (simulacao ? (e.elegiveis + ' elegíveis') : (e.removidos + ' removidos')) +
                ' — ' + escaparHtml(e.status) + (e.arquivo ? '<br><small>' + escaparHtml(e.arquivo) + '</small>' : '') + '</li>';
    });
    html += '</ul></div>';
    Swal.fire({ icon: 'info', title: simulacao ? 'Simulação' : 'Expurgo concluído', html: html, width: 600 });
}

function somaElegiveis(data) {
    var t = 0; (data.escopos || []).forEach(function(e) { t += (e.elegiveis || 0); }); return t;
}
