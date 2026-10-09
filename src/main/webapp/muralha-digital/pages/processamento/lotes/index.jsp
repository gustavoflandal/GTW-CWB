<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2">
    <h4 class="mb-0"><i class="bi bi-collection me-2"></i>Gestão de Lotes</h4>
    <button class="btn btn-sm btn-primary ms-auto" onclick="criarLote()">
      <i class="bi bi-plus-circle me-1"></i>Novo Lote
    </button>
    <button class="btn btn-sm btn-outline-secondary" onclick="carregar()">
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
        <button class="btn btn-danger" id="btnCancelar" onclick="cancelarLote()" style="display:none">
          <i class="bi bi-x-circle me-1"></i>Cancelar Lote
        </button>
        <button class="btn btn-success" id="btnEnviar" onclick="enviarLote()" style="display:none">
          <i class="bi bi-send me-1"></i>Enviar para DETRAN
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

async function carregar() {
  var r = await fetch(ctx + '/MuralhaDigital/Lote?acao=listar');
  var d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

  var tbody = document.getElementById('tblLotes');
  tbody.innerHTML = '';
  (d.lotes || []).forEach(function(l) {
    var cor = BADGE_COR[l.status] || 'secondary';
    tbody.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td><strong>' + l.codigo + '</strong></td>' +
        '<td>' + (l.descricao || '') + '</td>' +
        '<td><span class="badge bg-' + cor + '">' + l.status + '</span></td>' +
        '<td class="small">' + (l.dtCriacao || '') + '</td>' +
        '<td class="small">' + (l.dtEnvio || '—') + '</td>' +
        '<td>' + l.qtd + '</td>' +
        '<td><button class="btn btn-xs btn-outline-info" ' +
          'onclick="verItens(' + l.id + ',\'' + l.codigo + '\',\'' + l.status + '\')">' +
          '<i class="bi bi-eye"></i></button></td>' +
      '</tr>');
  });
}

async function criarLote() {
  var res = await Swal.fire({
    title: 'Novo Lote', input: 'text',
    inputLabel: 'Descrição (opcional)',
    showCancelButton: true, confirmButtonText: 'Criar'
  });
  if (!res.isConfirmed) return;
  var r = await fetch(ctx + '/MuralhaDigital/Lote', {
    method: 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: 'acao=criar&descricao=' + encodeURIComponent(res.value || '')
  });
  var d = await r.json();
  if (d.ok) {
    Swal.fire({icon: 'success', title: 'Lote criado: ' + d.codigo, timer: 2000, showConfirmButton: false});
    carregar();
  } else {
    Swal.fire('Erro', d.erro, 'error');
  }
}

async function verItens(idLote, codigo, status) {
  loteAtual = { id: idLote, status: status };
  document.getElementById('modalLoteTitulo').textContent = codigo;
  document.getElementById('btnEnviar').style.display = status === 'RASCUNHO' ? '' : 'none';
  document.getElementById('btnCancelar').style.display = status === 'RASCUNHO' ? '' : 'none';

  var r = await fetch(ctx + '/MuralhaDigital/Lote?acao=itens&idLote=' + idLote);
  var d = await r.json();
  var tbody = document.getElementById('tblItens');
  tbody.innerHTML = '';
  (d.itens || []).forEach(function(item) {
    tbody.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td class="small">' + item.id + '</td>' +
        '<td><code>' + (item.placa || '—') + '</code></td>' +
        '<td>' + item.idLocal + '</td>' +
        '<td>' + item.idPista + '</td>' +
        '<td class="small">' + (item.dtCaptura || '') + '</td>' +
      '</tr>');
  });
  new bootstrap.Modal(document.getElementById('modalLote')).show();
}

async function enviarLote() {
  if (!loteAtual) return;
  var conf = await Swal.fire({
    title: 'Confirmar envio?',
    text: 'O lote será marcado como ENVIADO e não poderá mais ser alterado.',
    icon: 'question', showCancelButton: true, confirmButtonText: 'Enviar'
  });
  if (!conf.isConfirmed) return;
  var r = await fetch(ctx + '/MuralhaDigital/Lote', {
    method: 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: 'acao=enviar&idLote=' + loteAtual.id
  });
  var d = await r.json();
  if (d.ok) {
    bootstrap.Modal.getInstance(document.getElementById('modalLote')).hide();
    Swal.fire({icon: 'success', title: 'Lote enviado!', timer: 1500, showConfirmButton: false});
    carregar();
  } else {
    Swal.fire('Erro', d.erro, 'error');
  }
}

async function cancelarLote() {
  if (!loteAtual) return;
  var conf = await Swal.fire({
    title: 'Cancelar lote?', icon: 'warning',
    showCancelButton: true, confirmButtonText: 'Sim, cancelar'
  });
  if (!conf.isConfirmed) return;
  var r = await fetch(ctx + '/MuralhaDigital/Lote', {
    method: 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: 'acao=cancelar&idLote=' + loteAtual.id
  });
  var d = await r.json();
  if (d.ok) {
    bootstrap.Modal.getInstance(document.getElementById('modalLote')).hide();
    Swal.fire({icon: 'info', title: 'Lote cancelado', timer: 1500, showConfirmButton: false});
    carregar();
  } else {
    Swal.fire('Erro', d.erro, 'error');
  }
}

carregar();
</script>
