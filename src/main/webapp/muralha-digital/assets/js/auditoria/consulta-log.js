let paginaAtual = 1;
let ultimaResposta = [];

function buscar(pag) {
    paginaAtual = pag || 1;
    $.get('/MuralhaDigital/Auditoria', {
        acao:          'consultar',
        login:          $('#fLogin').val(),
        funcionalidade: $('#fFunc').val(),
        operacao:       $('#fOper').val(),
        dtInicio:       $('#fDtIni').val(),
        dtFim:          $('#fDtFim').val(),
        pagina:         paginaAtual
    }, function(data) {
        if (!data.ok) {
            alert('Erro ao consultar: ' + data.erro);
            return;
        }
        ultimaResposta = data.registros;
        const tbody = $('#tbodyLog').empty();

        if (data.registros.length === 0) {
            tbody.append('<tr><td colspan="7" class="text-center text-muted">Nenhum registro encontrado.</td></tr>');
        } else {
            data.registros.forEach(function(r) {
                tbody.append(
                    '<tr>' +
                    '<td>' + (r.dtOperacao || '') + '</td>' +
                    '<td>' + (r.login || '') + '</td>' +
                    '<td>' + (r.ip || '') + '</td>' +
                    '<td>' + (r.funcional || '') + '</td>' +
                    '<td>' + (r.operacao || '') + '</td>' +
                    '<td>' + (r.idRegistro || '') + '</td>' +
                    '<td>' + (r.descricao || '') + '</td>' +
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

function exportarCSV() {
    if (!ultimaResposta.length) { alert('Busque antes de exportar.'); return; }
    const cols = ['dtOperacao', 'login', 'ip', 'funcional', 'operacao', 'idRegistro', 'descricao'];
    const header = 'Data/Hora,Login,IP,Funcionalidade,Operação,ID Registro,Descrição\n';
    const rows = ultimaResposta.map(function(r) {
        return cols.map(function(c) { return '"' + (r[c] || '') + '"'; }).join(',');
    }).join('\n');
    const blob = new Blob(['﻿' + header + rows], { type: 'text/csv;charset=utf-8' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'log_auditoria.csv';
    a.click();
}

function exportarXLS() {
    if (!ultimaResposta.length) { alert('Busque antes de exportar.'); return; }
    const wsData = [['Data/Hora', 'Login', 'IP', 'Funcionalidade', 'Operação', 'ID Registro', 'Descrição']];
    ultimaResposta.forEach(function(r) {
        wsData.push([r.dtOperacao, r.login, r.ip, r.funcional, r.operacao, r.idRegistro, r.descricao]);
    });
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(wsData), 'Log');
    XLSX.writeFile(wb, 'log_auditoria.xlsx');
}
