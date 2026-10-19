let paginaAtual = 1;
let ultimaResposta = [];

$(function() {
    carregarFiltros();
});

function escaparHtml(v) { var d = document.createElement('div'); d.textContent = v == null ? '' : String(v); return d.innerHTML.replace(/"/g, '&quot;').replace(/'/g, '&#39;'); }

function carregarFiltros() {
    $.getJSON('/MuralhaDigital/Auditoria', { acao: 'listarFiltros' })
     .done(function(data) {
        if (!data || !data.ok) { console.warn('listarFiltros: resposta inválida', data); return; }
        preencherSelect('#fLogin', data.logins);
        preencherSelect('#fFunc',  data.funcionalidades);
        preencherSelect('#fOper',  data.operacoes);
        preencherSelectObj('#fPerfil', data.perfis, 'id', 'descricao');
        preencherSelectObj('#fEquip',  data.equipamentos, 'id', 'nome');
     })
     .fail(function(jqXHR, status, err) {
        console.error('Erro ao carregar filtros:', status, err);
     });
}

function preencherSelect(seletor, valores) {
    var sel = $(seletor);
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
        funcionalidade: $('#fFunc').val(),
        operacao:       $('#fOper').val(),
        perfil:         $('#fPerfil').val(),
        equipamento:    $('#fEquip option:selected').text() !== 'Todos' ? $('#fEquip option:selected').text() : '',
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
            tbody.append('<tr><td colspan="7" class="text-center text-muted">Nenhum registro encontrado.</td></tr>');
        } else {
            data.registros.forEach(function(r) {
                tbody.append(
                    '<tr>' +
                    '<td>' + escaparHtml(r.dtOperacao) + '</td>' +
                    '<td>' + escaparHtml(r.login) + '</td>' +
                    '<td>' + escaparHtml(r.ip) + '</td>' +
                    '<td>' + escaparHtml(r.funcional) + '</td>' +
                    '<td>' + escaparHtml(r.operacao) + '</td>' +
                    '<td>' + escaparHtml(r.idRegistro) + '</td>' +
                    '<td>' + escaparHtml(r.descricao) + '</td>' +
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
    if (!ultimaResposta.length) { Swal.fire('Atenção', 'Busque antes de exportar.', 'warning'); return; }
    const cols = ['dtOperacao', 'login', 'ip', 'funcional', 'operacao', 'idRegistro', 'descricao'];
    const header = 'Data/Hora;Login;IP;Funcionalidade;Operação;ID Registro;Descrição\n';
    const rows = ultimaResposta.map(function(r) {
        return cols.map(function(c) { return '"' + (r[c] || '') + '"'; }).join(';');
    }).join('\n');
    const blob = new Blob(['﻿' + header + rows], { type: 'text/csv;charset=utf-8' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'log_auditoria.csv';
    a.click();
}

function exportarXLS() {
    if (!ultimaResposta.length) { Swal.fire('Atenção', 'Busque antes de exportar.', 'warning'); return; }
    const wsData = [['Data/Hora', 'Login', 'IP', 'Funcionalidade', 'Operação', 'ID Registro', 'Descrição']];
    ultimaResposta.forEach(function(r) {
        wsData.push([r.dtOperacao, r.login, r.ip, r.funcional, r.operacao, r.idRegistro, r.descricao]);
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

    var head = [['Data/Hora', 'Login', 'IP', 'Funcionalidade', 'Operação', 'ID Registro', 'Descrição']];
    var body = ultimaResposta.map(function(r) {
        return [r.dtOperacao || '', r.login || '', r.ip || '', r.funcional || '',
                r.operacao || '', r.idRegistro || '', r.descricao || ''];
    });

    doc.autoTable({
        head: head,
        body: body,
        startY: 25,
        styles: { fontSize: 7, cellPadding: 1.5 },
        headStyles: { fillColor: [13, 117, 191] },
        columnStyles: { 6: { cellWidth: 80 } }
    });
    doc.save('log_auditoria.pdf');
}
