// src/main/webapp/muralha-digital/assets/js/chamados/chamados-comum.js
var Chamados = (function () {
  var ctx = window.CTX || '';

  var ROTULO_STATUS = {
    ABERTO: ['Aberto', 'secondary'], EM_ATENDIMENTO: ['Em atendimento', 'primary'],
    AGUARDANDO_SOLICITANTE: ['Aguardando solicitante', 'warning'], RESOLVIDO: ['Resolvido', 'info'],
    FECHADO: ['Fechado', 'success'], CANCELADO: ['Cancelado', 'dark']
  };
  var ROTULO_SLA = {
    NO_PRAZO: ['No prazo', 'success'], EM_RISCO: ['Em risco', 'warning'], VENCIDO: ['Vencido', 'danger'],
    CUMPRIDO: ['Cumprido', 'secondary'], VIOLADO: ['Violado', 'danger']
  };

  function esc(v) {
    return String(v == null ? '' : v).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }

  function badge(mapa, chave) {
    var r = mapa[chave] || [chave, 'secondary'];
    return '<span class="badge text-bg-' + r[1] + '">' + esc(r[0]) + '</span>';
  }

  function badgePrioridade(nome, cor) {
    var c = /^#[0-9a-fA-F]{6}$/.test(cor || '') ? cor : '#6c757d';
    return '<span class="badge" style="background-color:' + c + '">' + esc(nome) + '</span>';
  }

  function dataHora(iso) {
    if (!iso) return '—';
    var d = new Date(iso);
    return d.toLocaleDateString('pt-BR') + ' ' + d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  }

  async function tratar(r) {
    if (r.status === 401) { location.href = ctx + '/login/login.jsp'; return { ok: false }; }
    var d;
    try { d = await r.json(); } catch (e) { d = { ok: false, erro: 'Falha de comunicação (HTTP ' + r.status + ').' }; }
    if (!d.ok) Swal.fire({ title: 'Atenção', text: d.erro || 'Falha na operação.', icon: 'warning' });
    return d;
  }

  async function get(servlet, params) {
    var r = await fetch(ctx + '/MuralhaDigital/' + servlet + '?' + new URLSearchParams(params || {}));
    return tratar(r);
  }

  async function post(servlet, formData) {
    var r = await fetch(ctx + '/MuralhaDigital/' + servlet, { method: 'POST', body: formData });
    return tratar(r);
  }

  function baixar(servlet, params) {
    window.location.href = ctx + '/MuralhaDigital/' + servlet + '?' + new URLSearchParams(params);
  }

  /** PDF no navegador com os dados completos do servidor e o SHA-256 no rodape (spec D13). */
  async function exportarPdf(params) {
    var p = Object.assign({}, params, { acao: 'exportar', formato: 'json' });
    var d = await get('ChamadoRelatorio', p);
    if (!d.ok) return;
    var doc = new window.jspdf.jsPDF('l', 'pt');
    doc.setFontSize(12);
    doc.text(d.titulo, 40, 30);
    doc.setFontSize(8);
    doc.text('Filtros: ' + d.filtros + '  |  Usuário: ' + d.usuario + '  |  Gerado em: ' + new Date().toLocaleString('pt-BR'), 40, 44);
    doc.autoTable({ head: [d.rotulos], body: d.linhas, startY: 54, styles: { fontSize: 7 } });
    var y = doc.lastAutoTable.finalY + 14;
    doc.text('Total: ' + d.linhas.length + '  |  SHA-256 dos dados: ' + d.sha256, 40, y);
    doc.save(d.arquivo);
  }

  return {
    esc: esc, ROTULO_STATUS: ROTULO_STATUS,
    badgeStatus: function (s) { return badge(ROTULO_STATUS, s); },
    badgeSla: function (s) { return badge(ROTULO_SLA, s); },
    badgePrioridade: badgePrioridade, dataHora: dataHora,
    get: get, post: post, baixar: baixar, exportarPdf: exportarPdf
  };
})();
