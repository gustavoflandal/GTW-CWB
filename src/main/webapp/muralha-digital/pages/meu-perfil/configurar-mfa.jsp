<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@page import="com.consilux.infra.SessaoConstantes"%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="muralha.digital.acesso.TotpService"%>
<%
    Usuario usu = (Usuario) session.getAttribute(SessaoConstantes.SESSAO_USUARIO);
    if (usu == null) { response.sendRedirect("/login/login.jsp"); return; }

    String segredo = TotpService.gerarSegredo();
    String uri     = TotpService.gerarOtpAuthUri(segredo, usu.getUsuario(), "GTW-Muralha");
    session.setAttribute("totp_setup_segredo", segredo);
%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container py-4" style="max-width:520px">
  <h5 class="mb-3"><i class="bi bi-shield-check me-2"></i>Configurar Autenticação em Dois Fatores</h5>
  <p class="text-muted small">
    Escaneie o QR Code com <strong>Google Authenticator</strong>, <strong>Microsoft Authenticator</strong> ou <strong>Authy</strong>.
    Após escanear, digite o código de 6 dígitos para confirmar.
  </p>

  <div class="card p-3 mb-3 text-center">
    <div id="qrcode" class="d-flex justify-content-center mb-2"></div>
    <small class="text-muted">
      Chave manual: <code class="user-select-all"><%= segredo %></code>
    </small>
  </div>

  <div class="mb-3">
    <label class="form-label fw-semibold">Código do app</label>
    <input id="totp" class="form-control form-control-lg text-center"
           maxlength="6" placeholder="000000" autocomplete="off" inputmode="numeric">
  </div>

  <button class="btn btn-primary w-100" onclick="confirmarMFA()">
    <i class="bi bi-check-circle me-1"></i> Ativar autenticação em dois fatores
  </button>
  <div id="msgMFA" class="mt-2"></div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"
        integrity="sha512-CNgIRecGo7nphbeZ04Sc13ka07paqdeTu0WR1IM4kNcpmBAUSHSi2jPBrKGrfG4yksNt2yCT4eMsCRHBMNJqA=="
        crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script>
  new QRCode(document.getElementById("qrcode"), {
    text: "<%= uri.replace("\"", "\\\"") %>",
    width: 200, height: 200
  });

  function confirmarMFA() {
    var codigo = document.getElementById("totp").value.trim();
    if (codigo.length !== 6) {
      document.getElementById("msgMFA").innerHTML =
        '<div class="alert alert-warning">Digite os 6 dígitos do app.</div>';
      return;
    }
    $.post('/MuralhaDigital/MfaConfig', { acao: 'ativar', codigo: codigo }, function(r) {
      if (r.ok) {
        document.getElementById("msgMFA").innerHTML =
          '<div class="alert alert-success">MFA ativado com sucesso!</div>';
        setTimeout(function() { location.href = '/login/abertura-sistemas.jsp'; }, 1500);
      } else {
        document.getElementById("msgMFA").innerHTML =
          '<div class="alert alert-danger">' + (r.erro || 'Erro desconhecido') + '</div>';
      }
    });
  }

  document.getElementById("totp").focus();
  document.getElementById("totp").addEventListener("keydown", function(e) {
    if (e.key === "Enter") confirmarMFA();
  });
</script>

<%@ include file="/includes/rodape.jsp" %>
