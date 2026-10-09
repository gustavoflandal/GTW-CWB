<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2 flex-wrap">
    <h4 class="mb-0"><i class="bi bi-person-badge me-2"></i>CPF e Matrícula dos Usuários</h4>
    <button class="btn btn-sm btn-primary ms-auto" onclick="carregar()">
      <i class="bi bi-arrow-clockwise"></i>
    </button>
  </div>

  <div class="card">
    <div class="card-body p-0">
      <table class="table table-sm table-striped table-hover mb-0" id="tblUsuarios">
        <thead>
          <tr>
            <th>Usuário</th>
            <th>Nome</th>
            <th>CPF</th>
            <th>Matrícula</th>
            <th style="width:100px">Ação</th>
          </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </div>
</div>

<div class="modal fade" id="modalEditar" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title"><i class="bi bi-pencil-square me-2"></i>Editar CPF / Matrícula</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <input type="hidden" id="edIdUsuario">
        <div class="mb-3">
          <label class="form-label fw-bold">Usuário</label>
          <p id="edNomeUsuario" class="form-control-plaintext"></p>
        </div>
        <div class="mb-3">
          <label for="edCpf" class="form-label">CPF</label>
          <input type="text" class="form-control" id="edCpf" maxlength="14"
                 placeholder="000.000.000-00">
          <div class="form-text">Apenas números; pontuação é opcional.</div>
        </div>
        <div class="mb-3">
          <label for="edMatricula" class="form-label">Matrícula</label>
          <input type="text" class="form-control" id="edMatricula" maxlength="20">
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="button" class="btn btn-primary" onclick="salvar()">
          <i class="bi bi-check-lg me-1"></i>Salvar
        </button>
      </div>
    </div>
  </div>
</div>

<script>
var modal;
document.addEventListener('DOMContentLoaded', function() {
  modal = new bootstrap.Modal(document.getElementById('modalEditar'));
  carregar();
});

function formatarCpf(cpf) {
  if (!cpf || cpf.length !== 11) return cpf || '';
  return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

async function carregar() {
  var r = await fetch('<%= request.getContextPath() %>/MuralhaDigital/UsuarioCpfMatricula');
  var d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

  var tbody = document.querySelector('#tblUsuarios tbody');
  tbody.innerHTML = '';
  d.usuarios.forEach(function(u) {
    tbody.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td>' + u.usuario + '</td>' +
        '<td>' + u.nome + '</td>' +
        '<td>' + formatarCpf(u.cpf) + '</td>' +
        '<td>' + u.matricula + '</td>' +
        '<td><button class="btn btn-sm btn-outline-primary" ' +
          'onclick="editar(' + u.idUsuario + ',\'' +
            u.nome.replace(/'/g, "\\'") + '\',\'' +
            u.cpf + '\',\'' +
            u.matricula + '\')">' +
          '<i class="bi bi-pencil"></i></button></td>' +
      '</tr>');
  });
}

function editar(id, nome, cpf, matricula) {
  document.getElementById('edIdUsuario').value = id;
  document.getElementById('edNomeUsuario').textContent = nome;
  document.getElementById('edCpf').value = formatarCpf(cpf);
  document.getElementById('edMatricula').value = matricula;
  modal.show();
}

async function salvar() {
  var params = new URLSearchParams();
  params.append('idUsuario', document.getElementById('edIdUsuario').value);
  params.append('cpf', document.getElementById('edCpf').value);
  params.append('matricula', document.getElementById('edMatricula').value);

  var r = await fetch('<%= request.getContextPath() %>/MuralhaDigital/UsuarioCpfMatricula', {
    method: 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: params.toString()
  });
  var d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

  modal.hide();
  Swal.fire({icon:'success', title:'Salvo', timer:1500, showConfirmButton:false});
  carregar();
}
</script>
