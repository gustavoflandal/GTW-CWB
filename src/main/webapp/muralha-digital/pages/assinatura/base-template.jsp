<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html lang="pt-br">
<head>
    <!-- Template base para páginas de assinatura e validação -->
    <!-- Meta tags básicas -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title><c:out value="${param.pageTitle}" escapeXml="true"/></title>

    <!-- Otimização de recursos -->
    <link rel="icon" href="data:," type="image/x-icon">
    <link rel="preconnect" href="https://cdn.jsdelivr.net" crossorigin>
    
    <!-- Bootstrap já carregado pelo cabeçalho principal 
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
    -->
    <link rel="stylesheet" href="css/validacao-imagem.css" />

    <%@ include file="/muralha-digital/utils/notificacao/notificacao.jsp" %>
    
    <!-- Bootstrap JavaScript já carregado pelo cabeçalho principal 
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    -->
</head>
<body>

<!-- Overlay de carregamento -->
<div class="loading-overlay" id="loadingOverlay">
    <div class="loading-content">
        <div class="spinner-border text-primary loading-spinner" role="status">
            <span class="visually-hidden">Carregando...</span>
        </div>
        <h5 id="loadingText">Processando...</h5>
        <p class="text-muted" id="loadingSubtext">Aguarde while processamos sua solicitação</p>
    </div>
</div>

<!-- Alertas de erro -->
<c:if test="${not empty erro}">
    <div class="alert alert-danger alert-dismissible fade show mt-4" role="alert">
        <i class="bi bi-exclamation-triangle" aria-hidden="true"></i> 
        <strong>Erro:</strong> <c:out value="${erro}" escapeXml="true"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
    </div>
</c:if>

<!-- Alertas de status da operação -->
<c:if test="${not empty acao && not empty status}">
    <div class="alert ${ok ? 'alert-success' : 'alert-danger'} alert-dismissible fade show mt-4" role="alert">
        <c:out value="${status}" escapeXml="true"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
    </div>
</c:if>

<!-- Container para alertas dinâmicos do JavaScript -->
<div id="alertContainer" role="alert" aria-live="polite"></div>

<!-- Dados temporários do servlet (oculto) com escape seguro -->
<div id="dadosServlet" style="display: none;" data-component="servlet-data">
    <c:if test="${not empty acao}">
        <span data-acao="<c:out value='${acao}'/>" 
            data-status="<c:out value='${status}'/>" 
            data-nome-arquivo="<c:out value='${nomeArquivo}'/>" 
            data-sha256="<c:out value='${sha256}'/>" 
            data-caminho-arquivo="<c:out value='${caminhoArquivo}'/>" 
            data-tipo-solicitante="<c:out value='${tipoSolicitante}'/>" 
            data-imagem-assinada="<c:out value='${imagemAssinada}'/>" 
            data-ok="<c:out value='${ok}'/>"></span>
    </c:if>
</div>

<!-- Conteúdo principal da página -->
<div class="container py-4">
    <div class="card mb-4">
        <div class="card-header">
            <div class="d-flex justify-content-between align-items-center">
                <h4 class="mb-0"><c:out value="${param.headerTitle}" escapeXml="true"/></h4>
                <c:if test="${not empty param.alternatePageUrl}">
                    <a href="<c:out value='${param.alternatePageUrl}'/>" 
                        class="btn btn-outline-secondary btn-sm">
                        <i class="bi <c:out value='${param.alternatePageIcon}'/>" aria-hidden="true"></i> 
                        <c:out value="${param.alternatePageText}" escapeXml="true"/>
                    </a>
                </c:if>
            </div>
        </div>
        
        <!-- Inclusão do conteúdo específico da página -->
        <c:if test="${not empty param.contentPage}">
            <jsp:include page="${param.contentPage}" />
        </c:if>
        
    </div>
</div>

<!-- Scripts JavaScript -->
<script src="js/utils.js"></script>
<script src="js/client.js"></script>
<script src="js/download.js"></script>
<script src="js/loading.js"></script>
<script src="js/resultados.js"></script>
<script src="js/controller.js"></script>

<!-- Inicialização do controlador -->
<script>
    document.addEventListener('DOMContentLoaded', () => {
        window.controller = new AssinaturaImagemController();
    });
</script>

</body>
</html>