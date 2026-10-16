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
                    <label class="form-label form-label-sm">Funcionalidade</label>
                    <select id="fFunc" class="form-select form-select-sm">
                        <option value="">Todas</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label form-label-sm">Operação</label>
                    <select id="fOper" class="form-select form-select-sm">
                        <option value="">Todas</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label form-label-sm">Perfil</label>
                    <select id="fPerfil" class="form-select form-select-sm">
                        <option value="">Todos</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label form-label-sm">Equipamento</label>
                    <select id="fEquip" class="form-select form-select-sm">
                        <option value="">Todos</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label form-label-sm">De</label>
                    <input id="fDtIni" type="date" class="form-control form-control-sm">
                </div>
            </div>
            <div class="row g-2 mt-1">
                <div class="col-md-2">
                    <label class="form-label form-label-sm">Até</label>
                    <input id="fDtFim" type="date" class="form-control form-control-sm">
                </div>
                <div class="col-md-10 d-flex align-items-end gap-1">
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
                    <th>ID Registro</th>
                    <th>Descrição</th>
                </tr>
            </thead>
            <tbody id="tbodyLog">
                <tr><td colspan="7" class="text-center text-muted">Use os filtros acima e clique em Buscar.</td></tr>
            </tbody>
        </table>
    </div>

    <div id="paginacao" class="d-flex gap-2 mt-2"></div>
</div>

<script src="/muralha-digital/assets/js/auditoria/consulta-log.js?v=3"></script>
