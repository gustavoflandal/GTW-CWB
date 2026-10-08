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
  <div class="card shadow-sm p-4" style="width: 100%; max-width: 400px;">
    <h4 class="mb-3 text-center">Recuperar Senha</h4>
    <p class="text-muted text-center mb-4">Informe seu nome de usuário para recuperar o acesso</p>
      <div class="mb-3">
        <label for="usuario" class="form-label">Usuário</label>
        <input type="text" class="form-control" id="usuario" placeholder="Digite seu usuário">
      </div>
      <div class="d-grid">
        <button id="enviar" class="btn btn-primary">Enviar</button>
        <a style="justify-self: flex-end;" href="/login/login.jsp">retornar à tela anterior</a>
      </div>
  </div>
</div>
  <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
  <script src="assets/js/esqueci_senha.js"></script>