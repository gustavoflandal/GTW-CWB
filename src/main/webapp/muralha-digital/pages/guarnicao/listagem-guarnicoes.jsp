<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Listagem de Guarnições</title>

    <link rel="stylesheet" href="assets/css/listagem-guarnicoes.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    
    <!-- Bootstrap CSS -->
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"><!-- Bootstrap Select -->
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/css/bootstrap-select.min.css">
	<link rel="stylesheet" href="assets/css/nova-guarnicao.css">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	
	<!-- Bootstrap Select CSS -->
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/css/bootstrap-select.min.css">
	
	<!-- Bootstrap Select JS -->
	<script src="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/js/bootstrap-select.min.js"></script>
	</head>
<body>
    <div class="container-fluid mt-4">
        <h2 class="text-center mb-4">Listagem de Guarnições</h2>

        <div class="calledopen d-flex justify-content-end px-4">
            <button class="btn btn-sm btn-success" id="btnNovaGuarnicao" title="Adicionar">Nova guarnição</button>
        </div>

        <div class="mt-4">
            <table class="table table-striped align-middle" id="tabela-guarnicoes">
                <thead>
                    <tr>
                        <th>Guarnição</th>
                        <th>Responsável</th>
                        <th>Data de Criação</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td colspan="4" class="text-center">Carregando...</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    
<div class="modal fade" id="modalCriarGuarnicao" tabindex="-1" aria-labelledby="modalCriarGuarnicaoLabel" aria-hidden="true">
 <div class="modal-dialog modal-lg modal-dialog-centered">  
    <div class="modal-content">
		<%@ include file="nova-guarnicao.jsp" %>
    </div>
  </div>
</div>

    <div class="modal fade" id="modalTelefone" tabindex="-1" aria-labelledby="modalTelefoneLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title">Adicionar Telefone</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
        </div>
        <div class="modal-body">
            <p>Adicionar telefone para <strong id="nomeUsuarioTelefone"></strong>:</p>
            <input type="text" id="inputTelefoneUsuario" class="form-control" placeholder="Digite o telefone">
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" id="btnFecharTelefone">Fechar</button>
            <button id="salvarTelefone" type="button" class="btn btn-primary">Salvar</button>
        </div>
        </div>
    </div>
    </div>

    <div class="modal fade" id="modalVisualizaGuarnicao" tabindex="-1" aria-labelledby="modalVisualizaGuarnicaoLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <%@ include file="modal-visualiza-guarnicao.jsp" %>
            </div>
        </div>
    </div>
    
        <div class="modal fade" id="modalAtualizaGuarnicao" tabindex="-1" aria-labelledby="modalVisualizaGuarnicaoLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <%@ include file="modal-atualiza-guarnicao.jsp" %>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalEditarGuarnicao" tabindex="-1" aria-labelledby="modalEditarGuarnicaoLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <%@ include file="modal-edita-guarnicao.jsp" %>
            </div>
        </div>
    </div>

    <script src="assets/js/listagem-guarnicoes.js"></script>
</body>
</html>