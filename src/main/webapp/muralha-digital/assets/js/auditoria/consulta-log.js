let paginaAtual = 1;
let ultimaResposta = [];
let modalDetalhe;

$(function() {
    modalDetalhe = new bootstrap.Modal(document.getElementById('modalDetalhe'));
    carregarFiltros();
});

function escaparHtml(v) { var d = document.createElement('div'); d.textContent = v == null ? '' : String(v); return d.innerHTML.replace(/"/g, '&quot;').replace(/'/g, '&#39;'); }

function badgeNivel(n) {
    var map = { 'ACESSO': 'bg-info text-dark', 'VISUALIZACAO': 'bg-secondary', 'LEITURA': 'bg-secondary',
                'CADASTRO': 'bg-success', 'ALTERACAO': 'bg-warning text-dark', 'EXCLUSAO': 'bg-danger',
                'EXPORTACAO': 'bg-primary', 'IMPRESSAO': 'bg-primary','ERRO': 'bg-danger', 'SISTEMA': 'bg-dark' };
    var cls = map[n] || 'bg-light text-dark';
    return '<span class="badge ' + cls + '">' + escaparHtml(n || '-') + '</span>';
}

function carregarFiltros() {
    $.getJSON('/MuralhaDigital/Auditoria', { acao: 'listarFiltros' })
     .done(function(data) {
        if (!data || !data.ok) { console.warn('listarFiltros: resposta inválida', data); return; }
        preencherSelect('#fLogin', data.logins);
        preencherSelect('#fNivel', data.niveis);
        preencherSelect('#fTipo',  data.tipos);
        preencherSelectObj('#fPerfil', data.perfis, 'id', 'descricao');
     })
     .fail(function(jqXHR, status, err) {
        console.error('Erro ao carregar filtros:', status, err);
     });
}

function preencherSelect(seletor, valores) {
    var sel = $(seletor);
    if (!sel.length) return;
    var textoDefault = sel.find('option:first').text();
    sel.empty().append('<option value="">' + textoDefault + '</option>');
    if (valores) {
        valores.forEach(function(v) {
            sel.append('<option value="' + escaparHtml(v) + '">' + escaparHtml(v) + '</option>');
        });
    }
}

function preencherSelectObj(seletor, lista, campoVal, campoLabel) {
    var sel = $(seletor);
    var textoDefault = sel.find('option:first').text();
    sel.empty().append('<option value="">' + textoDefault + '</option>');
    if (lista) {
        lista.forEach(function(item) {
            sel.append('<option value="' + escaparHtml(item[campoVal]) + '">' + escaparHtml(item[campoLabel]) + '</option>');
        });
    }
}

function buscar(pag) {
    paginaAtual = pag || 1;
    $.get('/MuralhaDigital/Auditoria', {
        acao:          'consultar',
        login:          $('#fLogin').val(),
        nivel:          $('#fNivel').val(),
        tipo:           $('#fTipo').val(),
        perfil:         $('#fPerfil').val(),
        dtInicio:       $('#fDtIni').val(),
        dtFim:          $('#fDtFim').val(),
        pagina:         paginaAtual
    }, function(data) {
        if (!data.ok) {
            Swal.fire('Erro', 'Erro ao consultar: ' + data.erro, 'error');
            return;
        }
        ultimaResposta = data.registros;
        const tbody = $('#tbodyLog').empty();

        if (data.registros.length === 0) {
            tbody.append('<tr><td colspan="12" class="text-center text-muted">Nenhum registro encontrado.</td></tr>');
        } else {
            data.registros.forEach(function(r, i) {
                tbody.append(
                    '<tr style="cursor:pointer" onclick="verDetalhe(' + i + ')" title="Clique para ver o detalhe (antes/depois)">' +
                    '<td>' + escaparHtml(r.dtOperacao) + '</td>' +
                    '<td>' + escaparHtml(r.login) + '</td>' +
                    '<td>' + escaparHtml(r.ip) + '</td>' +
                    '<td>' + escaparHtml(r.funcional) + '</td>' +
                    '<td>' + escaparHtml(r.operacao) + '</td>' +
                    '<td>' + badgeNivel(r.nivel) + '</td>' +
                    '<td>' + escaparHtml(r.tipo) + '</td>' +
                    '<td>' + escaparHtml(r.metodo) + '</td>' +
                    '<td>' + escaparHtml(r.status) + '</td>' +
                    '<td>' + escaparHtml(r.idRegistro) + '</td>' +
                    '<td>' + escaparHtml(r.descricao) + '</td>' +
                    '<td><code title="prefixo SHA-256">' + escaparHtml(r.hash) + '</code></td>' +
                    '</tr>'
                );
            });
        }

        const pag = $('#paginacao').empty();
        if (paginaAtual > 1) {
            pag.append('<button class="btn btn-sm btn-outline-secondary" onclick="buscar(' + (paginaAtual - 1) + ')">← Anterior</button>');
        }
        if (data.registros.length === 50) {
            pag.append('<button class="btn btn-sm btn-outline-secondary" onclick="buscar(' + (paginaAtual + 1) + ')">Próxima →</button>');
        }
    });
}

function verDetalhe(i) {
    var r = ultimaResposta[i];
    if (!r) return;
    var ehEscrita = (r.metodo && r.metodo.toUpperCase() !== 'GET')
                 || ['CADASTRO', 'ALTERACAO', 'EXCLUSAO'].indexOf(r.nivel) >= 0;

    var html = '<table class="table table-sm"><tbody>' +
        linhaDet('Data/Hora', r.dtOperacao) + linhaDet('Usuário', r.login) + linhaDet('IP', r.ip) +
        linhaDet('Funcionalidade', r.funcional) + linhaDet('Operação', r.operacao) +
        linhaDet('Nível', r.nivel) + linhaDet('Tipo de captura', r.tipo) + linhaDet('Método', r.metodo) +
        linhaDet('Status HTTP', r.status) + linhaDet('Rota', r.rota) + linhaDet('ID Registro', r.idRegistro) +
        linhaDet('Descrição', r.descricao) +
        '</tbody></table>';

    if (r.dadosExtra) {
        try {
            var di = JSON.parse(r.dadosExtra);
            if (di && typeof di === 'object' &&
                (di.formato !== undefined || di.documento !== undefined || di.telaOrigem !== undefined)) {
                html += '<h6 class="mt-3"><i class="bi bi-printer me-1"></i>Impressão</h6>' +
                        '<table class="table table-sm"><tbody>' +
                        linhaDet('Formato', di.formato) + linhaDet('Documento', di.documento) +
                        linhaDet('Tela de origem', di.telaOrigem) +
                        '</tbody></table>';
            }
        } catch (e) { /* dadosExtra não-JSON: ignora */ }
    }

    if (ehEscrita) {
        var antes = null, depois = null;
        if (r.dadosExtra) {
            try {
                var d = JSON.parse(r.dadosExtra);
                if (d && (d.antes !== undefined || d.depois !== undefined)) { antes = d.antes; depois = d.depois; }
                else { depois = d; }
            } catch (e) { /* dadosExtra não-JSON: ignora */ }
        }
        html += '<h6 class="mt-3"><i class="bi bi-arrow-left-right me-1"></i>Antes e depois</h6>' +
                '<div class="row g-2">' +
                '<div class="col-md-6"><div class="card h-100"><div class="card-header py-1 bg-light"><b>Antes</b></div>' +
                '<div class="card-body p-2">' + renderObjeto(antes) + '</div></div></div>' +
                '<div class="col-md-6"><div class="card h-100"><div class="card-header py-1 bg-light"><b>Depois</b></div>' +
                '<div class="card-body p-2">' + renderObjeto(depois) + '</div></div></div>' +
                '</div>';
    }

    $('#detalheCorpo').html(html);
    modalDetalhe.show();
}

function linhaDet(k, v) {
    return '<tr><th style="width:170px">' + escaparHtml(k) + '</th><td>' + escaparHtml(v) + '</td></tr>';
}

function renderObjeto(o) {
    if (o == null) return '<span class="text-muted small">— não disponível —</span>';
    if (typeof o !== 'object') return escaparHtml(o);
    var keys = Object.keys(o);
    if (!keys.length) return '<span class="text-muted small">— vazio —</span>';
    var h = '<table class="table table-sm mb-0"><tbody>';
    keys.forEach(function(k) {
        h += '<tr><th style="width:45%">' + escaparHtml(k) + '</th><td>' + escaparHtml(o[k]) + '</td></tr>';
    });
    return h + '</tbody></table>';
}

function verificarIntegridade() {
    Swal.fire({ title: 'Verificando integridade...', didOpen: function() { Swal.showLoading(); },
                allowOutsideClick: false });
    $.get('/MuralhaDigital/Auditoria', {
        acao: 'verificarIntegridade',
        dtInicio: $('#fDtIni').val(),
        dtFim: $('#fDtFim').val()
    }, function(data) {
        if (!data.ok) { Swal.fire('Erro', data.erro || 'Falha na verificação', 'error'); return; }
        var integro = data.integro;
        var html =
            '<div class="text-start">' +
            '<p><b>Eventos verificados:</b> ' + data.eventosVerificados + ' | íntegros: ' + data.eventosIntegros + '</p>' +
            '<p><b>Selos diários:</b> ' + data.selosVerificados + ' | íntegros: ' + data.selosIntegros + '</p>';
        if (!integro) {
            html += '<p class="text-danger"><b>Anomalias encontradas:</b></p><ul>';
            (data.anomalias || []).forEach(function(a) {
                html += '<li>Evento #' + a.id + ' — ' + a.tipo + ' (' + a.dt + ')</li>';
            });
            (data.selosAnomalias || []).forEach(function(a) {
                html += '<li>Selo ' + a.dia + ' — ' + a.tipo + '</li>';
            });
            html += '</ul>';
        }
        html += '</div>';
        Swal.fire({
            icon: integro ? 'success' : 'error',
            title: integro ? 'Trilha íntegra' : 'Integridade comprometida',
            html: html,
            width: 600
        });
    }).fail(function() { Swal.fire('Erro', 'Falha ao verificar integridade.', 'error'); });
}

var COLS_EXP = ['dtOperacao','login','ip','funcional','operacao','nivel','tipo','metodo','status','idRegistro','descricao'];
var CAB_EXP  = ['Data/Hora','Login','IP','Funcionalidade','Operação','Nível','Tipo','Método','Status','ID Registro','Descrição'];

function exportarCSV() {
    if (!ultimaResposta.length) { Swal.fire('Atenção', 'Busque antes de exportar.', 'warning'); return; }
    const header = CAB_EXP.join(';') + '\n';
    const rows = ultimaResposta.map(function(r) {
        return COLS_EXP.map(function(c) { return '"' + (r[c] == null ? '' : r[c]) + '"'; }).join(';');
    }).join('\n');
    const blob = new Blob(['﻿' + header + rows], { type: 'text/csv;charset=utf-8' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'log_auditoria.csv';
    a.click();
}

function exportarXLS() {
    if (!ultimaResposta.length) { Swal.fire('Atenção', 'Busque antes de exportar.', 'warning'); return; }
    const wsData = [CAB_EXP];
    ultimaResposta.forEach(function(r) {
        wsData.push(COLS_EXP.map(function(c) { return r[c] == null ? '' : r[c]; }));
    });
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(wsData), 'Log');
    XLSX.writeFile(wb, 'log_auditoria.xlsx');
}

function exportarPDF() {
    if (!ultimaResposta.length) { Swal.fire('Atenção', 'Busque antes de exportar.', 'warning'); return; }
    var jsPDF = window.jspdf.jsPDF;
    var doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });
    doc.setFontSize(14);
    doc.text('Log de Auditoria — Muralha Digital', 14, 15);
    doc.setFontSize(8);
    doc.text('Gerado em: ' + new Date().toLocaleString('pt-BR'), 14, 21);

    var body = ultimaResposta.map(function(r) {
        return COLS_EXP.map(function(c) { return r[c] == null ? '' : String(r[c]); });
    });

    doc.autoTable({
        head: [CAB_EXP],
        body: body,
        startY: 25,
        styles: { fontSize: 6.5, cellPadding: 1 },
        headStyles: { fillColor: [13, 117, 191] },
        columnStyles: { 10: { cellWidth: 60 } }
    });
    doc.save('log_auditoria.pdf');
}
