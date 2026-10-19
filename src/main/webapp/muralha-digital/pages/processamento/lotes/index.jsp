<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2">
    <h4 class="mb-0"><i class="bi bi-collection me-2"></i>Gestão de Lotes</h4>
    <button class="btn btn-sm btn-primary ms-auto" id="btnNovoLote">
      <i class="bi bi-plus-circle me-1"></i>Novo Lote
    </button>
    <button class="btn btn-sm btn-outline-secondary" id="btnRecarregar" title="Recarregar">
      <i class="bi bi-arrow-clockwise"></i>
    </button>
  </div>

  <div class="card">
    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-sm table-hover mb-0">
          <thead>
            <tr>
              <th>Código</th><th>Descrição</th><th>Status</th>
              <th>Criação</th><th>Envio</th><th>Infrações</th><th></th>
            </tr>
          </thead>
          <tbody id="tblLotes"></tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="modalLote" tabindex="-1">
  <div class="modal-dialog modal-xl">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="modalLoteTitulo">Lote</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="table-responsive">
          <table class="table table-sm table-striped">
            <thead><tr><th>ID</th><th>Placa</th><th>Local</th><th>Pista</th><th>Data Captura</th></tr></thead>
            <tbody id="tblItens"></tbody>
          </table>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-outline-primary me-auto" id="btnAdicionar" style="display:none">
          <i class="bi bi-plus-square me-1"></i>Adicionar infrações
        </button>
        <button class="btn btn-danger" id="btnCancelar" style="display:none">
          <i class="bi bi-x-circle me-1"></i>Cancelar Lote
        </button>
        <button class="btn btn-success" id="btnEnviar" style="display:none">
          <i class="bi bi-check2-square me-1"></i>Marcar como enviado
        </button>
        <button class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="modalAdicionar" tabindex="-1">
  <div class="modal-dialog modal-xl">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title"><i class="bi bi-plus-square me-2"></i>Adicionar infrações ao lote</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="row g-2 mb-2 align-items-center">
          <div class="col-md-6">
            <input type="text" class="form-control form-control-sm" id="filtroDisp"
                   placeholder="Filtrar por placa ou ID">
          </div>
          <div class="col-md-6 text-md-end small text-muted">
            Infrações pré-aprovadas que ainda não estão em nenhum lote.
            <span id="contadorSel" class="ms-2 fw-semibold"></span>
          </div>
        </div>
        <div class="table-responsive" style="max-height:55vh; overflow-y:auto;">
          <table class="table table-sm table-striped table-hover mb-0">
            <thead class="sticky-top bg-white">
              <tr>
                <th style="width:40px"><input type="checkbox" class="form-check-input" id="chkTodos" title="Selecionar todos"></th>
                <th>ID</th><th>Placa</th><th>Local</th><th>Data Captura</th>
              </tr>
            </thead>
            <tbody id="tblDisp"></tbody>
          </table>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-primary" id="btnConfirmarAdicao">
          <i class="bi bi-plus-square me-1"></i>Adicionar selecionadas
        </button>
        <button class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
      </div>
    </div>
  </div>
</div>

<script>
var ctx = '<%= request.getContextPath() %>';
var loteAtual = null;

var BADGE_COR = {
  RASCUNHO: 'secondary', ENVIADO: 'primary',
  CONFIRMADO: 'success', CANCELADO: 'danger'
};

function escaparHtml(v) {
  var d = document.createElement('div');
  d.textContent = v == null ? '' : String(v);
  return d.innerHTML.replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

function vazio(v) {
  return v == null || v === '' ? '—' : escaparHtml(v);
}

function mostrarErro(d) {
  Swal.fire('Erro', (d && d.erro) || 'Não foi possível concluir a operação.', 'error');
}

async function chamar(url, opcoes) {
  try {
    var r = await fetch(url, opcoes);
    return await r.json();
  } catch (e) {
    return { ok: false, erro: 'Falha na comunicação com o servidor.' };
  }
}

function post(corpo) {
  return chamar(ctx + '/MuralhaDigital/Lote', {
    method: 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: corpo
  });
}

async function carregar() {
  var d = await chamar(ctx + '/MuralhaDigital/Lote?acao=listar');
  if (!d.ok) { mostrarErro(d); return; }

  var tbody = document.getElementById('tblLotes');
  tbody.innerHTML = '';
  (d.lotes || []).forEach(function(l) {
    var cor = BADGE_COR[l.status] || 'secondary';
    tbody.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td><strong>' + escaparHtml(l.codigo) + '</strong></td>' +
        '<td>' + escaparHtml(l.descricao) + '</td>' +
        '<td><span class="badge bg-' + cor + '">' + escaparHtml(l.status) + '</span></td>' +
        '<td class="small">' + escaparHtml(l.dtCriacao) + '</td>' +
        '<td class="small">' + vazio(l.dtEnvio) + '</td>' +
        '<td>' + escaparHtml(l.qtd) + '</td>' +
        '<td><button class="btn btn-sm btn-outline-info btn-ver" title="Ver infrações" ' +
          'data-id="' + escaparHtml(l.id) + '" data-codigo="' + escaparHtml(l.codigo) + '" ' +
          'data-status="' + escaparHtml(l.status) + '">' +
          '<i class="bi bi-eye"></i></button></td>' +
      '</tr>');
  });
  tbody.querySelectorAll('.btn-ver').forEach(function(b) {
    b.addEventListener('click', function() {
      verItens(b.getAttribute('data-id'), b.getAttribute('data-codigo'), b.getAttribute('data-status'));
    });
  });
}

async function criarLote() {
  var res = await Swal.fire({
    title: 'Novo Lote', input: 'text',
    inputLabel: 'Descrição (opcional)',
    inputAttributes: { maxlength: 200 },
    showCancelButton: true, confirmButtonText: 'Criar'
  });
  if (!res.isConfirmed) return;
  var d = await post('acao=criar&descricao=' + encodeURIComponent(res.value || ''));
  if (d.ok) {
    Swal.fire({icon: 'success', title: 'Lote criado: ' + d.codigo, timer: 2000, showConfirmButton: false});
    carregar();
  } else {
    mostrarErro(d);
  }
}

async function verItens(idLote, codigo, status) {
  loteAtual = { id: idLote, codigo: codigo, status: status };
  document.getElementById('modalLoteTitulo').textContent = codigo;
  var rascunho = status === 'RASCUNHO';
  document.getElementById('btnEnviar').style.display = rascunho ? '' : 'none';
  document.getElementById('btnCancelar').style.display = rascunho ? '' : 'none';
  document.getElementById('btnAdicionar').style.display = rascunho ? '' : 'none';

  var d = await chamar(ctx + '/MuralhaDigital/Lote?acao=itens&idLote=' + encodeURIComponent(idLote));
  if (!d.ok) { mostrarErro(d); return; }
  var tbody = document.getElementById('tblItens');
  tbody.innerHTML = '';
  (d.itens || []).forEach(function(item) {
    tbody.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td class="small">' + escaparHtml(item.id) + '</td>' +
        '<td><code>' + vazio(item.placa) + '</code></td>' +
        '<td>' + vazio(item.idLocal) + '</td>' +
        '<td>' + vazio(item.idPista) + '</td>' +
        '<td class="small">' + vazio(item.dtCaptura) + '</td>' +
      '</tr>');
  });
  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalLote')).show();
}

async function enviarLote() {
  if (!loteAtual) return;
  var conf = await Swal.fire({
    title: 'Marcar lote como enviado?',
    text: 'O lote será marcado como enviado. A integração com o DETRAN ainda não está disponível.',
    icon: 'question', showCancelButton: true, confirmButtonText: 'Marcar como enviado'
  });
  if (!conf.isConfirmed) return;
  var d = await post('acao=enviar&idLote=' + encodeURIComponent(loteAtual.id));
  if (d.ok) {
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalLote')).hide();
    Swal.fire({icon: 'success', title: 'Lote marcado como enviado', timer: 1500, showConfirmButton: false});
    carregar();
  } else {
    mostrarErro(d);
  }
}

async function cancelarLote() {
  if (!loteAtual) return;
  var conf = await Swal.fire({
    title: 'Cancelar lote?', icon: 'warning',
    showCancelButton: true, confirmButtonText: 'Sim, cancelar'
  });
  if (!conf.isConfirmed) return;
  var d = await post('acao=cancelar&idLote=' + encodeURIComponent(loteAtual.id));
  if (d.ok) {
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalLote')).hide();
    Swal.fire({icon: 'info', title: 'Lote cancelado', timer: 1500, showConfirmButton: false});
    carregar();
  } else {
    mostrarErro(d);
  }
}

/* ---- Adicionar infrações ---- */
var disponiveis = [];
var selecionadas = {};

function atualizarContador() {
  var n = Object.keys(selecionadas).length;
  document.getElementById('contadorSel').textContent = n ? n + ' selecionada(s)' : '';
}

function desenharDisponiveis() {
  var filtro = document.getElementById('filtroDisp').value.trim().toLowerCase();
  var tbody = document.getElementById('tblDisp');
  tbody.innerHTML = '';
  var visiveis = disponiveis.filter(function(i) {
    return !filtro ||
      (i.placa || '').toLowerCase().indexOf(filtro) >= 0 ||
      (i.id || '').toLowerCase().indexOf(filtro) >= 0;
  });
  if (!visiveis.length) {
    tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-3">Nenhuma infração disponível.</td></tr>';
  }
  visiveis.forEach(function(i) {
    tbody.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td><input type="checkbox" class="form-check-input chk-disp" data-id="' + escaparHtml(i.id) + '"' +
          (selecionadas[i.id] ? ' checked' : '') + '></td>' +
        '<td class="small">' + escaparHtml(i.id) + '</td>' +
        '<td><code>' + vazio(i.placa) + '</code></td>' +
        '<td>' + vazio(i.idLocal) + '</td>' +
        '<td class="small">' + vazio(i.dtCaptura) + '</td>' +
      '</tr>');
  });
  tbody.querySelectorAll('.chk-disp').forEach(function(c) {
    c.addEventListener('change', function() {
      var id = c.getAttribute('data-id');
      if (c.checked) selecionadas[id] = true; else delete selecionadas[id];
      atualizarContador();
    });
  });
  document.getElementById('chkTodos').checked = false;
  atualizarContador();
}

async function abrirAdicionar() {
  if (!loteAtual) return;
  var d = await chamar(ctx + '/MuralhaDigital/Lote?acao=disponiveis');
  if (!d.ok) { mostrarErro(d); return; }
  disponiveis = d.itens || [];
  selecionadas = {};
  document.getElementById('filtroDisp').value = '';
  desenharDisponiveis();
  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalLote')).hide();
  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalAdicionar')).show();
}

function selecionarTodos(marcado) {
  document.querySelectorAll('#tblDisp .chk-disp').forEach(function(c) {
    c.checked = marcado;
    var id = c.getAttribute('data-id');
    if (marcado) selecionadas[id] = true; else delete selecionadas[id];
  });
  atualizarContador();
}

async function confirmarAdicao() {
  var ids = Object.keys(selecionadas);
  if (!ids.length) {
    Swal.fire('Atenção', 'Selecione ao menos uma infração.', 'warning');
    return;
  }
  var d = await post('acao=adicionarInfracoes&idLote=' + encodeURIComponent(loteAtual.id) +
                     '&ids=' + encodeURIComponent(ids.join(',')));
  if (!d.ok) { mostrarErro(d); return; }
  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalAdicionar')).hide();
  await Swal.fire({
    icon: d.adicionados > 0 ? 'success' : 'info',
    title: 'Inclusão concluída',
    text: 'Adicionadas: ' + d.adicionados + ' · Ignoradas: ' + d.ignorados
  });
  carregar();
  verItens(loteAtual.id, loteAtual.codigo, loteAtual.status);
}

document.getElementById('btnNovoLote').addEventListener('click', criarLote);
document.getElementById('btnRecarregar').addEventListener('click', carregar);
document.getElementById('btnEnviar').addEventListener('click', enviarLote);
document.getElementById('btnCancelar').addEventListener('click', cancelarLote);
document.getElementById('btnAdicionar').addEventListener('click', abrirAdicionar);
document.getElementById('btnConfirmarAdicao').addEventListener('click', confirmarAdicao);
document.getElementById('filtroDisp').addEventListener('input', desenharDisponiveis);
document.getElementById('chkTodos').addEventListener('change', function() { selecionarTodos(this.checked); });

carregar();
</script>
