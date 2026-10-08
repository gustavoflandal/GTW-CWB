<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Relatório de Ações de Alarmes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        .passagem-foto { max-width: 150px; cursor: pointer; border-radius: 0.25rem; }
    </style>
</head>
<body>
    <div id="toast-container" class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 1100;"></div>

    <%-- LARGURA AUMENTADA AQUI --%>
    <div class="container mt-4" style="max-width: 1320px;">
        <div class="card shadow-sm">
            <div class="card-header bg-light"><h4 class="mb-0">Relatório de Ações de Alarmes</h4></div>
            <div class="card-body">
                <div class="row g-3 align-items-end">
                    <div class="col-md-5">
                        <label for="operador" class="form-label">Operador</label>
                        <select id="operador" class="form-select">
                            <option value="">Carregando...</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label for="data-inicial" class="form-label">Data Início</label>
                        <input type="date" id="data-inicial" class="form-control">
                    </div>
                    <div class="col-md-3">
                        <label for="data-final" class="form-label">Data Fim</label>
                        <input type="date" id="data-final" class="form-control">
                    </div>
                </div>
                <div class="mt-4 pt-3 border-top">
                    <button id="gerar-relatorio" class="btn btn-primary"><i class="bi bi-search me-2"></i>Consultar</button>
                    <button id="limpar-pesquisa" class="btn btn-secondary"><i class="bi bi-eraser me-2"></i>Limpar</button>
                </div>
            </div>
        </div>

        <div class="card mt-4 shadow-sm">
             <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="mb-0 d-inline-block">Resultados da Consulta</h5>
                    <span id="record-count" class="badge bg-secondary ms-2">0 registros</span>
                </div>
                <div>
                    <button id="exportar-pdf" class="btn btn-outline-danger btn-sm"><i class="bi bi-file-earmark-pdf me-2"></i>Exportar PDF</button>
                    <button id="exportar-excel" class="btn btn-outline-success btn-sm"><i class="bi bi-file-earmark-excel me-2"></i>Exportar Excel</button>
                </div>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>Passagem</th>
                                <th>Fato Registrado</th>
                                <th>Ação Tomada (Anotação)</th>
                            </tr>
                        </thead>
                        <tbody id="report-table-body">
                            <tr><td colspan="3" class="text-center text-muted">Nenhum dado gerado.</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    
    <div class="modal fade" id="fotoModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Visualizar Imagem</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body text-center">
            <img id="imagemAmpliada" src="" class="img-fluid" alt="Imagem da Passagem">
          </div>
        </div>
      </div>
    </div>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.23/jspdf.plugin.autotable.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
    <script>
        const servletURL = '${pageContext.request.contextPath}/MuralhaDigital/Relatorios/AcoesAlarmes';
    </script>
    <script src="acoes-alarmes.js"></script> 
</body>
</html>