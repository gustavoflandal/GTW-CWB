<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%
	String retUrl = request.getParameter("p") != null ? request.getParameter("p") : "";
	try {
		retUrl = URLEncoder.encode(retUrl, "UTF-8");
	}
	catch(Exception e) { }

	String user = request.getHeader("X-USER"); //username
	String secretKey = request.getHeader("X-USER-SECRET"); //Texto Criptografado
	String urlService = request.getHeader("X-URL-SERVICE"); //Url de redirecionamento
	String nonce = request.getHeader("X-NONCE"); // Nonce
	String tag = request.getHeader("X-TAG"); //Tag

	session.invalidate();
%>
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
			    <strong>ACESSO AO SISTEMA</strong>
			  </h5>
		
			  <br></br>
			
			  <!--Card content-->
			  <div class="card-body px-lg-5 pt-0">
			
			    <!-- Form -->
			    <form class="text-center needs-validation" novalidate style="color: #757575;" id="frm_login" action="/login/login_action_app.jsp" method="post" name="frm_login" >
								
					<input type="hidden" name=user value="<%= user != null ? user : ""%>"/>
					<input type="hidden" name=secretKey value="<%= secretKey != null ? secretKey : ""%>"/>
					<input type="hidden" name="urlService" value="<%= urlService != null ? urlService : "" %>"/>
					<input type="hidden" name="nonce" value="<%= nonce != null ? nonce : "" %>"/>
					<input type="hidden" name="tag" value="<%= tag != null ? tag : "" %>"/>    
			    	<input type="hidden" name="retUrl" value="<%=retUrl%>"/>
			    
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

document.addEventListener("DOMContentLoaded", function () {
    login_action_app();
});  

    function login_action_app() {
        document.getElementById('frm_login').submit();
    }

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

	nome_browser = navigator.userAgent.toLowerCase();

	if (nome_browser.indexOf("firefox") == -1) {
		//alert("O GTW pode não ser compatível com este browser.")
	}

	function enter(e) {
		document.getElementById("frm_login").submit();
		}
	
	
	$(document).ready(()=>{
        $.getJSON("https://api.ipify.org?format=json",
        function (data) {
			$("#txt_ip").html(data.ip);
			//$("#txt_ip").attr("placeholder", data.ip);
			$("#txt_ip").attr("value", data.ip)
        })
    });	
</script>
<%@ include file="/includes/rodape.jsp" %>	
