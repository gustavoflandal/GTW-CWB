SISTEMAS_URL = []

$(document).ready(function() 
{
	document.getElementById("gtw").style.display = "none";
	document.getElementById("muralha_digital").style.display = "none";
	document.getElementById("jari").style.display = "none";
	document.getElementById("mobilidade_urbana").style.display = "none";
	
	document.getElementById("txt_gtw").style.display = "none";
	document.getElementById("txt_muralha_digital").style.display = "none";
	document.getElementById("txt_jari").style.display = "none";
	document.getElementById("txt_mobilidade_urbana").style.display = "none";

	
	ObterSistemasConsilux();
}); 

function ObterSistemasConsilux()
{

	var urlReqMapa = "/Abertura/SistemasConsilux"; 
	
	
	console.log(urlReqMapa);

	$.ajax(
    {
        type: 		"GET",
        url: 		urlReqMapa,
        dataType:	"xml",
       
        success: 	function( data, textStatus, jqXHR) 
        {
         	
        	console.log("ObterSistemasConsilux()::Chegou dados");
			console.log(jqXHR.responseText );
			
			var xmlDoc = $.parseXML( jqXHR.responseText );
			var $xml = $(xmlDoc);
			
			var count = 0;
			var descricao = '';
			var mostrarPainel;
			var urlExterna = "";
			
			$xml.find('ListaSistemas').find('item').each(function()
			{
				count++;
				
				var $sistema = $(this);
				
				descricao 		= $sistema.find('descricao').text();
				mostrarPainel 	= $sistema.find('mostrarPainel').text();		
				urlExterna 		= $sistema.find('urlExterna').text();	
				
				if(mostrarPainel == 'true')
				{
					console.log('Mostrar sistema:: ' + descricao);
					
					var campo = '';
					if(descricao == 'Muralha Digital')		campo = 'muralha_digital';					
					if(descricao == 'GTW') 					campo = 'gtw';
					if(descricao == 'J.A.R.I.') 			campo = 'jari'; 
					if(descricao == 'Mobilidade Urbana') 	campo = 'mobilidade_urbana'; 
					
					document.getElementById("txt_" + campo).style.display = "block";
					document.getElementById(campo).style.display = "block";
					
					item = [];
					item.push(campo);
					item.push(urlExterna);
					SISTEMAS_URL.push(item);					
				}										
			});
			
			console.log('Quantidade de sistemas carregado:: ' + count);
			
			//Caso não haja sistema configurado na base de dados
			if(count > 0) document.getElementById("txt_sem_softwares").style.display = "none";
	    				
			console.log(SISTEMAS_URL);
    		return false;
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log("(ObterSistemasConsilux) Erro ao processar requisição ao servidor)!!");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
    });    			
}

function obter_url(modulo, url_fixo)
{
	ret = "";
	for (let i = 0; i < SISTEMAS_URL.length; i++) 
	{
	    if (modulo == SISTEMAS_URL[i][0])
		{
			url_banco = SISTEMAS_URL[i][1];
			
			if (url_banco != "") 
				ret = url_banco;
			else
				ret = url_fixo;
			break;		
		}
	}
	return ret;
}


function chamaPaginaPrincipal() 
{
	var end =  "${root}login/principal.jsp";

	window.location.href = end;
	return false;
}

function abrirGTWAntigo() 
{	
	window.location.href = obter_url("gtw", "/login/gtw_principal.jsp");
	return false;
}	

function abrirMuralhaDigital() 
{	
	window.location.href = obter_url("muralha_digital", "/login/muralha_principal.jsp");
	return false;
}	

function abrirMobilidadeUrbana()
{
	window.location.href = obter_url("mobilidade_urbana", "http://127.0.0.1:8000");
	return false;
}


function abrirJARI() 
{	
	
	alert("Esta função ainda não está desenvolvida");
	return false;
}	