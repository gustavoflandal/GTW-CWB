<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- Descrição da funcionalidade -->
<div class="card-body">
    <p class="card-text">
        Esta ferramenta permite assinar digitalmente imagens para garantir sua integridade 
        e autenticidade. A assinatura é embutida na própria imagem JPEG.
    </p>
</div>

<div class="row g-4">
    <div class="col-lg-6">
        <!-- Formulário de assinatura -->
        <div class="card h-100">
            <div class="card-header">
                <h5 class="mb-0">🔐 Assinar Imagem</h5>
            </div>
            <div class="card-body">
                <form method="post" action="/muralha-digital/assinatura/assinar" enctype="multipart/form-data">
                    <div class="upload-area mb-3">
                        <div class="mb-3">
                            <label for="imgAssinar" class="form-label fw-bold">Selecionar Imagem</label>
                            <input type="file" class="form-control form-control-lg" id="imgAssinar" name="imagem" accept=".jpeg,.JPEG,.jpg,.JPG,image/jpeg" required>
                            <div class="form-text">Formatos aceitos: JPEG, JPG</div>
                        </div>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-secondary btn-lg" id="btnAssinar" disabled>
                            <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                            <i class="bi bi-shield-lock"></i> Assinar Imagem
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="col-lg-6">
        <!-- Painel de resultado da operação -->
        <%@ include file="painel-resultado.jsp" %>
    </div>
</div>

<!-- Painel de histórico de operações -->
<%@ include file="painel-historico.jsp" %>