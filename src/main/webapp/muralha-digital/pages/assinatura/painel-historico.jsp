<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- Painel de histórico de operações -->
<div class="card mt-4" id="resultadosCard" style="display: none;">
    <div class="card-header d-flex justify-content-between align-items-center">
        <div>
            <h5 class="mb-0">📊 Histórico das Operações</h5>
            <small class="text-muted">Últimas 100 operações</small>
        </div>
        <div class="d-flex gap-2 align-items-center">
            <button type="button" class="btn btn-outline-warning btn-sm fw-bold" onclick="limparResultados()">
                <i class="bi bi-arrow-clockwise"></i> Limpar
            </button>
        </div>
    </div>
    <div class="card-body p-0">
        <!-- Tabela de histórico -->
        <div class="table-responsive">
            <table class="table table-striped table-hover mb-0" id="tabelaResultados">
                <thead class="table-secondary">
                    <tr>
                        <th width="12%">Data/Hora</th>
                        <th width="10%">Operação</th>
                        <th width="8%">Status</th>
                        <th width="10%">Solicitante</th>
                        <th width="25%">Arquivo</th>
                        <th width="35%">Hash SHA-256</th>
                    </tr>
                </thead>
                <tbody id="corpoTabelaResultados">
                    <!-- Resultados inseridos dinamicamente via JavaScript -->
                </tbody>
            </table>
        </div>
    </div>
</div>