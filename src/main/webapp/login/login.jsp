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

<style>
	.profile-pic {
		width: 120px;
		height: 120px;
		object-fit: cover;
		border-radius: 50%;
		margin-top: -60px;
		border: 5px solid #fff;
		box-shadow: 0 2px 8px rgba(0,0,0,0.1);
	}
</style>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<br>
<br>
<br>

<div class="row">
	<div class="col-md-4"></div>
	<div class="col-md-4">

		<!-- Material form login -->
		<div class="card border-primary mx-4 my-4">
		
			<h5 style="background-color: #0d75bf;" class="white-text text-center py-4 " >
				<strong>ACESSO AO SISTEMA</strong>
			</h5>
		
			<br></br>
			
			<!--Card content-->
			<div class="card-body px-lg-5 pt-0">
			
				<!-- Form -->
				<form class="text-center" novalidate style="color: #757575;" id="frm_login" action="/login/login_action.jsp" method="post" name="frm_login" >
			
					<!-- Usuário -->
					<div class="md-form row text-left">
						  <div class="col-auto me-2 d-flex align-items-center">
							<i class="fas fa-user prefix grey-text"></i>
						</div>										
						<div class="col">
							<label for="txt_login" class="ms-3 active">Usuário</label>							
							<input type="text" id="txt_login" name="login" class="form-control" maxlength="25" onkeypress="return enter(event)"/>						
						</div>
					</div>
			
					<!-- Password -->
					<div class="md-form row text-left">
						<div class="col-auto me-2 d-flex align-items-center">
							<i class="fas fa-lock prefix grey-text"></i>
						</div>
						<div class="col">						
							<input type="password" id="txt_senha" name="senha" class="form-control" maxlength="20" onkeypress="return enter(event)"/>			
							<label for="txt_senha" class="ms-3 active">Senha</label>
						</div>								
					</div>
						
						
					<!-- IP -->
					<div class="hide" style="display:none;">
						<input type="text" readonly="readonly" id="txt_ip" name="ip" class="form-control" maxlength="25" onkeypress="return enter(event)" placeholder="" />
					</div>
					
					<!-- 					
					<div class="text-center" style="margin-bottom: 8px;">
				        <input type="checkbox" id="chk_manter_conectado" name="manterConectado" class="form-check-input" value="true" onkeypress="return enter(event)" />
				        <label for="chk_manter_conectado" class="form-check-label ms-2 mb-0">
				            Manter conectado
				        </label>
					</div> 
					-->
						
					<!-- Sign in button -->
					<button class="btn btn-primary text-dark waves-effect waves-light" style="width: 230px;" name="btn_entrar" type="submit">Entrar</button>
					
					<input type="hidden" name="retUrl" value="<%=retUrl%>"/>
				
				</form>
				<!-- Form -->
				
				<div class="text-center">
					<button class="btn btn-warning  text-dark waves-effect waves-light" style="width: 230px;" onclick="logar_com_google()">
						Login com o google&nbsp;&nbsp;&nbsp;<i class="fa fa-google"></i>
					</button>
				</div>
			
			</div>
			<!--Card content-->
			<div class="card-footer text-center py-3">
				<p class="text-muted mb-0">Esqueceu a senha? <a href="/login/esqueci_senha.jsp" class="text-primary">Clique aqui</a></p>
			</div>
		</div>
		<!-- Material form login -->
	</div>

	<div class="col-md-4"></div>
</div>

<script type="text/javascript">
	//Script para validação do formulário
	(() => {
		'use strict';

		// Fetch all the forms we want to apply custom Bootstrap validation styles to
		const forms = document.querySelectorAll('.needs-validation');

		// Loop over them and prevent submission
		Array.prototype.slice.call(forms).forEach((form) => {
			form.addEventListener('submit', (event) => {
				if (!form.checkValidity()) {
					event.preventDefault();
					event.stopPropagation();
				}
				form.classList.add('was-validated');
			}, false);
		});
	})();
</script>

<script type="text/javascript">
	var txt_login = document.getElementById("txt_login");
	var txt_senha = document.getElementById("txt_senha");

	nome_browser = navigator.userAgent.toLowerCase();

	if (nome_browser.indexOf("firefox") == -1) {
		//alert("O GTW pode não ser compatível com este browser.")
	}

	function enter(e) {
		if (e.keyCode == 13) {
			if (txt_login.value == "")
				txt_login.focus();
			else if (txt_senha.value == "")
				txt_senha.focus();
			else
				document.getElementById("frm_login").submit();
			return false;
		}
	}
	
	$(document).ready(()=>{
		$.getJSON("https://api.ipify.org?format=json",
		function (data) {
			$("#txt_ip").html(data.ip);
			//$("#txt_ip").attr("placeholder", data.ip);
			$("#txt_ip").attr("value", data.ip)
			console.log(document.getElementById("txt_ip"));
			console.log("IP: " + document.getElementById("txt_ip").value);
		})
	});
</script>

<script src="/login/assets/js/login.js"></script>

<%@ include file="/includes/rodape.jsp" %>
