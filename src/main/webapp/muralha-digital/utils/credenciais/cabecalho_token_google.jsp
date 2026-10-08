<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="en" data-bs-theme="light">
	<head>
	  <title>GTW</title>
	
	  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  <meta http-equiv="x-ua-compatible" content="ie=edge">
	  
	  	<script type="text/javascript">
			var urlRoot = "${root}";
		</script>
	
	  	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
	  
	  	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
  		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery-3.6.0.min.js"></script>
	  	<script type="text/javascript" src="/muralha-digital/utils/credenciais/assets/js/cabecalho.js"></script>
	</head>

	<body>
	</body>
	
	<script type="text/javascript">
	    
		function getQueryParam(param) 
		{
	        const urlParams = new URLSearchParams(window.location.search);
	        return urlParams.get(param);
	    }
	    
		(function() {
	        const tokenGoogle = getQueryParam('token_google');
	        if (tokenGoogle) {
	            console.log(tokenGoogle);
				login_com_google_token(tokenGoogle);
	        }
	    })();
		
		
		function login_com_google_token(tokenGoogle)
		{
			var end = "/MuralhaDigital/Usuarios";

			$.ajax({
			    type: 		"POST",
			    url: 		end,
				dataType: 	"xml",
				data: {
				    acao: "login_google_token",
				    google_token: tokenGoogle
				},
			   
			    //if received a response from the server
			    success: 	function( data, textStatus, jqXHR) {
									
					window.location.href = "/login/muralha_principal.jsp";
					
				},
			    beforeSend: function(jqXHR, settings){},
			    complete: 	function(jqXHR, textStatus){},
			    error: 		function(jqXHR, textStatus, errorThrown)
			    {
			    	alert("Erro ao processar requisição ao servidor!!");
					window.location.href = "/login/login.jsp";
			    }
			});		
		}	
		
	</script>	

</html>
