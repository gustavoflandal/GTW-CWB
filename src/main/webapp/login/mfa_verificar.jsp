<%@page import="com.consilux.infra.SessaoConstantes"%>
<%@page import="com.consilux.infra.SessaoFinalizaAdapter"%>
<%@page import="com.consilux.infra.SessaoFinalizaManager"%>
<%@page import="com.consilux.model.LogonLogoff"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="muralha.digital.acesso.TotpService"%>
<%@page import="muralha.digital.acessos.ConfiguracaoInatividadeResult"%>
<%@page import="muralha.digital.acessos.ConfiguracaoInatividadeServlet"%>
<%@page import="muralha.digital.auditoria.AuditoriaService"%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" session="false" %>
<%
    HttpSession sessAtual = request.getSession(false);
    if (sessAtual == null || sessAtual.getAttribute("mfa_pendente_usuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Usuario usuarioPendente = (Usuario) sessAtual.getAttribute("mfa_pendente_usuario");
    String  segredoPendente = (String)  sessAtual.getAttribute("mfa_pendente_segredo");
    String  sipPendente     = (String)  sessAtual.getAttribute("mfa_pendente_sip");
    String  retUrlPendente  = (String)  sessAtual.getAttribute("mfa_pendente_retUrl");
    String  codigo          = request.getParameter("codigo");

    if (!TotpService.validar(segredoPendente, codigo)) {
        String erro = java.net.URLEncoder.encode("Código inválido. Tente novamente.", "UTF-8");
        response.sendRedirect("mfa_codigo.jsp?erro=" + erro);
        return;
    }

    // Código correto — completar a sessão definitiva
    SessaoFinalizaManager.removeSessaoFinaliza(sessAtual);
    sessAtual.invalidate();
    HttpSession sessaoNova = request.getSession(true);

    ConfiguracaoInatividadeResult configs = ConfiguracaoInatividadeServlet.buscarConfigsInatividade();
    if (configs.LoginNuncaBloqueia.getValor() == 1) {
        sessaoNova.setMaxInactiveInterval(-1);
        sessaoNova.setAttribute("manterConectado", true);
    } else {
        sessaoNova.setMaxInactiveInterval(configs.LoginTempoInatividade.getValor());
    }

    if (request.isSecure()) {
        Cookie cookie = new Cookie("JSESSIONID", sessaoNova.getId());
        cookie.setPath(request.getContextPath());
        cookie.setSecure(true);
        response.addCookie(cookie);
    } else {
        Cookie cookie = new Cookie("JSESSIONID_HTTP", sessaoNova.getId());
        cookie.setPath(request.getContextPath());
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    sessaoNova.setAttribute(SessaoConstantes.SESSAO_USUARIO, usuarioPendente);

    LogonLogoff ll = LogonLogoff.inserirLogonLogoff(
        usuarioPendente.getId(), request.getRemoteAddr(), sipPendente != null ? sipPendente : "");
    if (ll == null) {
        new Mensagem(response).showErroMuralha("Host não reconhecido!");
        return;
    }
    SessaoFinalizaManager.adicSessaoFinaliza(sessaoNova, ll);

    AuditoriaService.registrar(request, "Acesso", "login-mfa-ok",
        String.valueOf(usuarioPendente.getId()), "Login com MFA verificado");

    String destino = (retUrlPendente != null && !retUrlPendente.isEmpty())
        ? retUrlPendente
        : "/login/abertura-sistemas.jsp";
    response.sendRedirect(destino);
%>
