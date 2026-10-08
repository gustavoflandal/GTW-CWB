<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>
<%@ include file="/includes/cabecalho_sem_acesso.jsp" %>
<%
	String sMensagem = request.getParameter("m");
	String sUrlVolta = request.getParameter("p");
%>
<script language="javascript">
    function mostraMensagem() {
        alert("<%=sMensagem%>");
        document.location.href = "<%=sUrlVolta%>";
    }
    mostraMensagem();
</script>
<%@ include file="/includes/rodape.jsp" %>
