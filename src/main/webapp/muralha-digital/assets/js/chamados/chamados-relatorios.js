// src/main/webapp/muralha-digital/assets/js/chamados/chamados-relatorios.js
(function () {
  var esc = Chamados.esc;
  var grafico = null;

  function filtros() {
    var p = {};
    new FormData(document.getElementById('formRel')).forEach(function (v, k) { if (v) p[k] = v; });
    return p;
  }

  function fmt(v, sufixo) { return v == null ? '—' : String(v).replace('.', ',') + (sufixo || ''); }

  async function iniciar() {
    var hoje = new Date();
    document.getElementById('rInicio').value = new Date(hoje.getFullYear(), hoje.getMonth() - 5, 1).toISOString().substring(0, 10);
    document.getElementById('rFim').value = hoje.toISOString().substring(0, 10);
    var o = await Chamados.get('Chamado', { acao: 'opcoes' });
    if (!o.ok) return;
    o.categorias.filter(function (c) { return !c.idPai; }).forEach(function (c) { adicionar('rCategoria', c.id, c.nome); });
    o.equipamentos.forEach(function (e) { adicionar('rEquipamento', e.idLocal, e.nome); });
    o.prioridades.forEach(function (p) { adicionar('rPrioridade', p.id, p.nome); });
    gerar();
  }

  function adicionar(id, valor, rotulo) {
    var op = document.createElement('option');
    op.value = valor; op.textContent = rotulo;
    document.getElementById(id).appendChild(op);
  }

  async function gerar() {
    var d = await Chamados.get('ChamadoRelatorio', Object.assign(filtros(), { acao: 'indicadores' }));
    if (!d.ok) return;
    var l = d.linhas;
    var tot = l.reduce(function (a, x) { return a + x.total; }, 0);
    var res = l.reduce(function (a, x) { return a + x.resolvidos; }, 0);
    var canc = l.reduce(function (a, x) { return a + x.cancelados; }, 0);
    document.getElementById('cards').innerHTML = [
      ['Chamados', tot, 'bi-inbox'], ['Resolvidos', res, 'bi-check2-circle'], ['Cancelados', canc, 'bi-x-circle']
    ].map(function (c) {
      return '<div class="col-md-4"><div class="card text-center"><div class="card-body"><h6 class="text-muted"><i class="bi ' +
        c[2] + ' me-1"></i>' + c[0] + '</h6><h2 class="fw-bold mb-0">' + c[1] + '</h2></div></div></div>';
    }).join('');
    document.getElementById('tblRel').innerHTML = l.map(function (x) {
      return '<tr><td>' + esc(x.grupo) + '</td><td>' + x.total + '</td><td>' + x.resolvidos + '</td><td>' + x.cancelados +
        '</td><td>' + fmt(x.pctSlaAtendimento, '%') + '</td><td>' + fmt(x.pctSlaResolucao, '%') + '</td><td>' + fmt(x.tmaMin) +
        '</td><td>' + fmt(x.tmrMin) + '</td><td>' + fmt(x.notaMedia) + '</td><td>' + fmt(x.pctAvaliados, '%') + '</td></tr>';
    }).join('') || '<tr><td colspan="10" class="text-center text-muted py-3">Sem dados no período.</td></tr>';

    if (grafico) grafico.destroy();
    grafico = new Chart(document.getElementById('grafico'), {
      data: {
        labels: l.map(function (x) { return x.grupo; }),
        datasets: [
          { type: 'bar', label: 'Chamados', data: l.map(function (x) { return x.total; }), backgroundColor: '#0d75bf', yAxisID: 'y' },
          { type: 'line', label: '% SLA resolução', data: l.map(function (x) { return x.pctSlaResolucao; }), borderColor: '#198754', yAxisID: 'y1' }
        ]
      },
      options: { scales: { y: { beginAtZero: true }, y1: { position: 'right', min: 0, max: 100, grid: { drawOnChartArea: false } } } }
    });
  }

  document.getElementById('formRel').addEventListener('submit', function (e) { e.preventDefault(); gerar(); });
  document.getElementById('btnCsv').addEventListener('click', function () {
    Chamados.baixar('ChamadoRelatorio', Object.assign(filtros(), { acao: 'exportar', formato: 'csv', origem: 'relatorio' }));
  });
  document.getElementById('btnXls').addEventListener('click', function () {
    Chamados.baixar('ChamadoRelatorio', Object.assign(filtros(), { acao: 'exportar', formato: 'xlsx', origem: 'relatorio' }));
  });
  document.getElementById('btnPdf').addEventListener('click', function () {
    Chamados.exportarPdf(Object.assign(filtros(), { origem: 'relatorio' }));
  });
  iniciar();
})();
