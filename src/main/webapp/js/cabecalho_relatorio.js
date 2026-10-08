function verificar_sistema() 
{
  const params = new URLSearchParams(window.location.search);
  const tipo_sistema = params.get('tipo_sistema');
  
  if (tipo_sistema == "mobilidade") 
  {
    document.getElementById("BARRA_MENU_GTW").style.display = 'none'; 
	document.getElementById("img_cabecalho_orig").src = "/images/cabecalho_relatorio.png";
	document.getElementById("BOTAO_HOME").style.display = "block";
  }
}		

function redireciona_sistemas()
{	
	window.location.href = "/login/abertura-sistemas.jsp";
}


verificar_sistema();