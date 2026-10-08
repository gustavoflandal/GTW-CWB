<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_mdb_sem_menu.jsp"%>	

<br />
<br />
<br />

<div  class="row">
	<div class="col-md-4"></div>
	<div class="col-md-4">

		<!-- Material form login -->
		<div class="card border-primary mx-4 my-4">
		
			  <h5 style="background-color: #0d75bf;" class="white-text text-center py-4 " >
			    <strong>ALTERAR SENHA</strong>
			  </h5>
		
			  <br></br>
			
			  <!--Card content-->
			  <div class="card-body px-lg-5 pt-0">
			
			    <!-- Form -->
			    <form class="text-center needs-validation" novalidate style="color: #757575;" id="frm_login" action="/login/login_change_action.jsp" method="post" name="frm_login" >
			
					<!-- Senha atual -->
					<div class="md-form row text-left">
			     	 	<i class="fas fa-lock prefix grey-text"></i>
			        	<input type="password" id="txt_senha_ant" name="senha_ant" class="form-control" maxlength="10" onkeypress="return enter(event)" required />
			        	<label for="txt_senha_ant" class="ms-4">Senha atual</label>
			        	<div class="invalid-feedback">Por favor, informe a senha atual.</div>
			      	</div>
			
			      	<!-- Nova senha -->
			      	<div class="md-form row text-left">
			      		<i class="fas fa-lock prefix grey-text"></i>
			      		<input type="password" id="txt_senha_nova" name="senha_nova" class="form-control"  maxlength="10" onkeypress="return enter(event)" required />
			        	<label for="txt_senha_nova" class="ms-4">Nova senha</label>
			        	<div class="invalid-feedback">Por favor, informe a nova senha.</div>
			      	</div>
			      	
			      	<!-- Confirmar nova senha -->
			      	<div class="md-form row text-left">
			      		<i class="fas fa-lock prefix grey-text"></i>
			      		<input type="password" id="txt_senha_confirma" name="senha_confirma" class="form-control"  maxlength="10" onkeypress="return enter(event)" required />
			        	<label for="txt_senha_confirma" class="ms-4">Confirmar nova senha</label>
			        	<div class="invalid-feedback">Por favor, informe a confirmação da nova senha.</div>
			      	</div>
			
			      	<!-- Sign in button -->
			      	<button class="btn btn-warning text-dark waves-effect waves-light" name="btn_alterar" type="submit">Alterar</button>
			    
			    </form>
			    <!-- Form -->
			
			  </div>
			  <!--Card content-->
		
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
	var txt_senha_ant = document.getElementById("txt_senha_ant");
	var txt_senha_nova = document.getElementById("txt_senha_nova");
	var txt_senha_confirma = document.getElementById("txt_senha_confirma");
	
	function enter(e) {
		if (e.keyCode == 13) {
			if (txt_senha_ant.value == "")
				txt_senha_ant.focus();
			else if (txt_senha_nova.value == "")
				txt_senha_nova.focus();
			else if (txt_senha_confirma.value == "")
				txt_senha_confirma.focus();
			else	
				document.getElementById("frm_login").submit();
			return false;
		}
	}

	txt_senha_ant.focus();	
</script>
<%@ include file="/includes/rodape.jsp" %>	
