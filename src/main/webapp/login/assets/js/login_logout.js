$(document).ready(function()
{
	var url = '../' + document.URL.match(/[^\/]+$/);
	
	$.ajaxSetup({headers: { 'x-gtw-custom-header': url }});
	
	$("#btnLogout").click(function(e)
	{
	    var dataString = "acao=out";
	    var end = "/MuralhaDigital/Usuarios";
		
	    $.ajax({
	        type: 		"POST",
	        url: 		end,
	        data: 		dataString,
	        dataType:	"json",
	       
	        //if received a response from the server
	        success: 	function( data, textStatus, jqXHR) {chamaPaginaLogin();},
	        beforeSend: function(jqXHR, settings){},
	        complete: 	function(jqXHR, textStatus){},
	        error: 		function(jqXHR, textStatus, errorThrown)
	        {
	        	alert("Erro ao processar requisição ao servidor!!");
	        }
	    });
	});
});

function chamaPaginaLogin()
{
	var end = "/login/login.jsp";

	window.location.href = end;
	return false;
}
