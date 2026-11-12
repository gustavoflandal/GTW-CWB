<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
<script src="/muralha-digital/assets/js/jspdf.umd.min.js"></script>
<script src="/muralha-digital/assets/js/jspdf.plugin.autotable.min.js"></script>
<style>
    .table th { white-space: nowrap; }
    .table td { font-size: .85rem; }
</style>

<div class="container-fluid py-3">
    <h4 class="mb-3"><i class="bi bi-shield-lock me-2"></i>Log de Auditoria</h4>

    <div class="card mb-3 shadow-sm">
        <div class="card-body">
            <div class="row g-2">
                <div class="col-md-2">
                    <label class="form-label form-label-sm">Login</label>
                    <select id="fLogin" class="form-select form-select-sm">
                        <option value="">Todos</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label form-label-sm">Perfil</label>
                    <select id="fPerfil" class="form-select form-select-sm">
                        <option value="">Todos</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label form-label-sm">Nível</label>
                    <select id="fNivel" class="form-select form-select-sm">
                        <option value="">Todos</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label form-label-sm">Tipo de captura</label>
                    <select id="fTipo" class="form-select form-select-sm">
                        <option value="">Todos</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label form-label-sm">De</label>
                    <input id="fDtIni" type="date" class="form-control form-control-sm">
                </div>
                <div class="col-md-2">
                    <label class="form-label form-label-sm">Até</label>
                    <input id="fDtFim" type="date" class="form-control form-control-sm">
                </div>
            </div>
            <div class="row g-2 mt-2">
                <div class="col-12 d-flex gap-1 flex-wrap">
                    <button class="btn btn-primary btn-sm" onclick="buscar(1)">
                        <i class="bi bi-search"></i> Buscar
                    </button>
                    <button class="btn btn-outline-secondary btn-sm" onclick="exportarCSV()">
                        <i class="bi bi-filetype-csv"></i> CSV
                    </button>
                    <button class="btn btn-outline-secondary btn-sm" onclick="exportarXLS()">
                        <i class="bi bi-file-earmark-excel"></i> XLS
                    </button>
                    <button class="btn btn-outline-danger btn-sm" onclick="exportarPDF()">
                        <i class="bi bi-filetype-pdf"></i> PDF
                    </button>
                    <button class="btn btn-outline-success btn-sm" onclick="verificarIntegridade()" title="Verifica a cadeia de hash e os selos diários">
                        <i class="bi bi-shield-check"></i> Integridade
                    </button>
                </div>
            </div>
        </div>
    </div>

    <div class="table-responsive">
        <table class="table table-sm table-hover table-bordered" id="tblLog">
            <thead style="background-color:#0d75bf; color:#fff;">
                <tr>
                    <th>Data/Hora</th>
                    <th>Login</th>
                    <th>IP</th>
                    <th>Funcionalidade</th>
                    <th>Operação</th>
                    <th>Nível</th>
                    <th>Tipo</th>
                    <th>Método</th>
                    <th>Status</th>
                    <th>ID Registro</th>
                    <th>Descrição</th>
                    <th title="Prefixo do hash de integridade">Hash</th>
                </tr>
            </thead>
            <tbody id="tbodyLog">
                <tr><td colspan="12" class="text-center text-muted">Use os filtros acima e clique em Buscar.</td></tr>
            </tbody>
        </table>
    </div>

    <div id="paginacao" class="d-flex gap-2 mt-2"></div>
</div>

<!-- Modal de detalhe do log (antes/depois para operações POST) -->
<div class="modal fade" id="modalDetalhe" tabindex="-1">
  <div class="modal-dialog modal-lg modal-dialog-scrollable">
    <div class="modal-content">
      <div class="modal-header" style="background-color:#0d75bf; color:#fff;">
        <h5 class="modal-title"><i class="bi bi-card-list me-2"></i>Detalhe do log</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body" id="detalheCorpo"></div>
      <div class="modal-footer">
        <button class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Fechar</button>
      </div>
    </div>
  </div>
</div>

<script src="/muralha-digital/assets/js/auditoria/consulta-log.js?v=9"></script>
