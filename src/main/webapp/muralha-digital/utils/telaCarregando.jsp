<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html lang="en">

	<head>	
	  	<meta 		name="viewport" content="width=device-width, initial-scale=1">    	
	  	
	  	<style>
			#loading 
			{
			  display: block;
			  position: absolute;
			  top: 0;
			  left: 0;
			  z-index: 100;
			  width: 100vw;
			  height: 100vh;
			  background-color: rgba(192, 192, 192, 0.5);
			  background-image: url("images/gifs-Load/processando2.gif"),
			  					url("../images/gifs-Load/processando2.gif"), 
			  					url("../../images/gifs-Load/processando2.gif"),
			  					url("../../../images/gifs-Load/processando2.gif");
			  background-repeat: no-repeat;
			  background-position: center;
			}
		</style>
	</head>

	<body class="homepage">
		
		<div class="page"></div>
		<div id="loading"></div>
	
	</body>
	
	<script>
	
		//Esta variavel pode ser modificado por outro JS
		var tempoAguardando = 0;

		$(document).ready(function() 
		{
			
			if ( tempoAguardando == 0 ) 
				tempoAguardando = 10000;
			
			DesbloqueiaTelaAguarde();
	
		});
		
		/////////////////////////////////
		//Funcões a serem chamadas
		/////////////////////////////////
		function DesbloqueiaTelaAguardeTempo(tempo) { setTimeout(LoadingInativo, tempo); }
		function DesbloqueiaTelaAguarde() 			{ setTimeout(LoadingInativo, tempoAguardando); }		
		function BloqueiaTelaAguarde() 				{ LoadingAtivo(); }

		///////////////////////////////////////////////////////////////////////////////////////
		// Funções para não serem usadas
		///////////////////////////////////////////////////////////////////////////////////////
		function LoadingAtivo() 	{ TelaCarregando(false, true);	}	
		function LoadingInativo() 	{ TelaCarregando(true, false); }
		
		function TelaCarregando(paginaVisivel, gifLoading)
		{
			setVisible('.page', paginaVisivel);
			setVisible('#loading', gifLoading);
		}
	
		function setVisible(selector, visible) 
		{
			document.querySelector(selector).style.display = visible ? 'block' : 'none';
		}	
		///////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////
	
	</script>

</html>