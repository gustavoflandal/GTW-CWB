<script>
    
    /// Pagina com tipos de Notificações
    ///	https://medium.com/nerd-for-tech/best-notification-libraries-and-plugins-for-javascript-and-jquery-c457e50eeddd
    ///	Escolhas interessantes:
    ///	Alertfy e Toastr e izimodal
    
  	$().ready(function () 
	{
  		desktopPermissaoNotificacao();
	});
  
    
	function desktopPermissaoNotificacao() 
	{
		
		document.getElementById("status_notificacoes").innerHTML = "";
		
	
		Notification.requestPermission(function(p) {
			
			var status_notificacoes = document.getElementById("status_notificacoes");
			
			if (status_notificacoes) {
				status_notificacoes.innerHTML = ""
			  /*if (p == 'denied') {
				  status_notificacoes.innerHTML = "Nofiticações estão <strong>Desabilitadas</strong>."
			  } else if (p == 'granted') {
				  status_notificacoes.innerHTML = "Nofiticações estão <strong>Habilitadas</strong>."
			  }*/
			}
		});	
		
	}


	function mostraNovoAlerta()
 	{
		ChamaNotificacaoLigacao();		
 	}	
	
	
	function ChamaNotificacaoLigacao() 
	{
		var iicon = "/muralha-digital/images/call_less_notification.png";
		
		var notify;
			
		if (Notification.permission != 'default') 
		{
			
			notify = new Notification('Novo Alerta', 
			{
			  	body: 'Muralha Digital - Alerta!',
			  	icon: iicon,
			  	tag: Math.ceil(Math.random()*1000).toString()
			});
			
		}

	}	
	

</script>