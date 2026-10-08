<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<jsp:include page="base-template.jsp">
    <jsp:param name="pageTitle" value="Validação de Imagens Assinadas" />
    <jsp:param name="headerTitle" value="Validação de Imagens Assinadas" />
    <jsp:param name="alternatePageUrl" value="assinar-imagem.jsp" />
    <jsp:param name="alternatePageIcon" value="bi-shield-lock" />
    <jsp:param name="alternatePageText" value="Assinar Imagem" />
    <jsp:param name="contentPage" value="validacao-content.jsp" />
</jsp:include>