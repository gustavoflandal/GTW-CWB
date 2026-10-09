<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%
    // Redireciona para login se não há sessão MFA pendente
    if (session.getAttribute("mfa_pendente_usuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String erro = request.getParameter("erro");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Verificação MFA — GTW</title>
  <link rel="stylesheet"
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
        integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM"
        crossorigin="anonymous">
  <style>
    :root { --bs-body-bg: #f0f4f8; }
    body  { background: var(--bs-body-bg); }
    .card { border-radius: .75rem; }
  </style>
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100">
  <div class="card shadow-sm p-4" style="width:340px">
    <div class="text-center mb-3">
      <span style="font-size:2.5rem">🔐</span>
      <h5 class="mt-2 mb-0">Verificação em dois fatores</h5>
      <small class="text-muted">Digite o código de 6 dígitos do seu app autenticador.</small>
    </div>

    <% if (erro != null && !erro.isEmpty()) { %>
      <div class="alert alert-danger py-2 small"><%= java.net.URLDecoder.decode(erro, "UTF-8") %></div>
    <% } %>

    <form method="POST" action="mfa_verificar.jsp">
      <input name="codigo" class="form-control form-control-lg text-center mb-3"
             maxlength="6" placeholder="000000" autocomplete="one-time-code"
             inputmode="numeric" autofocus required>
      <button class="btn btn-primary w-100">Verificar</button>
    </form>

    <div class="text-center mt-3">
      <a href="login.jsp" class="text-muted small">← Voltar ao login</a>
    </div>
  </div>
</body>
</html>
