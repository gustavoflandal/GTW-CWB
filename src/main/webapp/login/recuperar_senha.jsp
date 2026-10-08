<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%
	//Finalizando uma possível sessão ativa:
	session.invalidate();

	String retUrl = request.getParameter("p") != null ? request.getParameter("p") : "";
	try {
		retUrl = URLEncoder.encode(retUrl, "UTF-8");
	}
	catch(Exception e) { }

%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_mdb_sem_menu.jsp"%>	
<div class="row" style="justify-content: center; margin-top: 15%;">
  <div id="conteudo-recuperacao" class="card shadow-sm p-4" style="width: 100%; max-width: 400px;">
    <p class="text-center">Carregando...</p>
  </div>
</div>
  <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
  <script src="assets/js/recupera_senha.js"></script>