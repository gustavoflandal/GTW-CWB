<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<jsp:include page="base-template.jsp">
    <jsp:param name="pageTitle" value="Assinatura Digital de Imagens" />
    <jsp:param name="headerTitle" value="Assinatura Digital de Imagens" />
    <jsp:param name="alternatePageUrl" value="validacao-imagem.jsp" />
    <jsp:param name="alternatePageIcon" value="bi-check-circle" />
    <jsp:param name="alternatePageText" value="Validar Imagem" />
    <jsp:param name="contentPage" value="assinar-content.jsp" />
</jsp:include>