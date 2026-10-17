<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<% String ctx = request.getContextPath(); %>

<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-database-gear me-2"></i>Consolidação de Dados Multifonte</h4>

  <!-- Resumo por fonte -->
  <div class="row mb-3" id="painelResumo"></div>

  <!-- Filtros -->
  <div class="card mb-3">
    <div class="card-body">
      <div class="row g-2 align-items-end">
        <div class="col-md-3">
          <label class="form-label">Fonte</label>
          <select id="selFonte" class="form-select">
            <option value="">Todas</option>
          </select>
        </div>
        <div class="col-md-3">
          <label class="form-label">Início</label>
          <input type="datetime-local" id="dtInicio" class="form-control">
        </div>
        <div class="col-md-3">
          <label class="form-label">Fim</label>
          <input type="datetime-local" id="dtFim" class="form-control">
        </div>
        <div class="col-md-2">
          <label class="form-label">Limite</label>
          <input type="number" id="limite" class="form-control" value="200" min="1" max="1000">
        </div>
        <div class="col-md-1">
          <button class="btn btn-primary w-100" onclick="consultar()">
            <i class="bi bi-search"></i>
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Tabela de resultados -->
  <div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
      <span>Registros consolidados</span>
      <span class="badge bg-secondary" id="badgeTotal">0</span>
    </div>
    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-sm table-striped table-hover mb-0">
          <thead class="table-light">
            <tr>
              <th>Fonte</th>
              <th>Data/Hora</th>
              <th>Equipamento</th>
              <th>Placa</th>
              <th>Tipo</th>
              <th>Descrição</th>
            </tr>
          </thead>
          <tbody id="tbBody"></tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<script>
const API = '<%= ctx %>/MuralhaDigital/Consolidacao';

const CORES_FONTE = {
  'PASSAGEM': 'primary',
  'ALERTA': 'danger',
  'INCIDENTE_WAZE': 'warning',
  'SUMARIZADO': 'info'
};

async function carregarResumo() {
  const r = await fetch(API + '?acao=resumo');
  const d = await r.json();
  if (!d.ok) return;
  const painel = document.getElementById('painelResumo');
  painel.innerHTML = '';
  d.fontes.forEach(f => {
    const cor = CORES_FONTE[f.fonte] || 'secondary';
    painel.insertAdjacentHTML('beforeend',
      '<div class="col-md-3">' +
        '<div class="card border-' + cor + '">' +
          '<div class="card-body text-center">' +
            '<h6 class="text-' + cor + '">' + f.fonte + '</h6>' +
            '<h3>' + Number(f.total).toLocaleString('pt-BR') + '</h3>' +
            '<small class="text-muted">' + (f.primeiro||'—') + ' a ' + (f.ultimo||'—') + '</small>' +
          '</div>' +
        '</div>' +
      '</div>');
  });
  // popular select
  const sel = document.getElementById('selFonte');
  sel.innerHTML = '<option value="">Todas</option>';
  d.fontes.forEach(f => {
    sel.insertAdjacentHTML('beforeend', '<option>' + f.fonte + '</option>');
  });
}

async function consultar() {
  const params = new URLSearchParams({ acao: 'consultar' });
  const fonte = document.getElementById('selFonte').value;
  const ini   = document.getElementById('dtInicio').value;
  const fim   = document.getElementById('dtFim').value;
  const lim   = document.getElementById('limite').value;
  if (fonte) params.set('fonte', fonte);
  if (ini)   params.set('dtInicio', ini.replace('T', ' '));
  if (fim)   params.set('dtFim', fim.replace('T', ' '));
  params.set('limite', lim);

  const r = await fetch(API + '?' + params);
  const d = await r.json();
  document.getElementById('badgeTotal').textContent = d.total || 0;
  const tb = document.getElementById('tbBody');
  tb.innerHTML = '';
  if (!d.ok || !d.registros || !d.registros.length) {
    tb.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-3">Nenhum registro encontrado.</td></tr>';
    return;
  }
  d.registros.forEach(r => {
    const cor = CORES_FONTE[r.fonte] || 'secondary';
    tb.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td><span class="badge bg-' + cor + '">' + r.fonte + '</span></td>' +
        '<td>' + (r.dtEvento || '—') + '</td>' +
        '<td>' + (r.equipamento || '—') + '</td>' +
        '<td>' + (r.placa || '—') + '</td>' +
        '<td>' + (r.tipoEvento || '—') + '</td>' +
        '<td>' + (r.descricao || '—') + '</td>' +
      '</tr>');
  });
}

carregarResumo();
</script>
