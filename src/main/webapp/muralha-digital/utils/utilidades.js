var MOBILE = /Mobi/i.test(window.navigator.userAgent);
//alert("MOBILE: " + MOBILE);

const NAVAGEADOR_FIREFOX = typeof InstallTrigger !== 'undefined';

if (NAVAGEADOR_FIREFOX)
	console.log('O navegador é o Firefox');

var host = location.origin.replace(/^http/, 'ws') + "/ClientesWebSocket";

function ObterCookie(name) 
{
	match = document.cookie.match(new RegExp(name + '=([^;]+)'));
	if (match) return match[1];
}



///////////////////////////////////////////////////////////////
/////////////////WEB SOCKET de ALERTAS
//////////////////////////////////////////////////////////////
var socketAlertas;

function iniciaWebSocketAlertas()
{
	try 
	{
		console.log("Host de webSocket Alertas: " + host);
		
		socketAlertas = new WebSocket(host);
		socketAlertas.binaryType = 'arraybuffer';
	
		socketAlertas.onerror = function(event) {
			onErrorSockAlertas(event); 
		};
	
		socketAlertas.onopen = function(event) {
			onOpenSockAlertas(event);
		};
	
		socketAlertas.onmessage = function(event) {
			onRecebeDadosSockAlertas(event);
		}; 
		
		socketAlertas.onclose = function (event) {
			onCloseSocketAlertas(event);
    	};
	} 
	catch(e){
		console.log(e);
	}
}   

///////////////////////////////////////////////////////////////
/////////////////WEB SOCKET de VEICULOS TEMPO REAL
//////////////////////////////////////////////////////////////
var socketVeicsTempoReal;

function IniciaWebSocketVeicTempoReal()
{
	try 
	{
		console.log("Host de webSocket Veiculos Tempo Real: " + host);
		
		socketVeicsTempoReal = new WebSocket(host);
		socketVeicsTempoReal.binaryType = 'arraybuffer';
	
		socketVeicsTempoReal.onerror = function(event) {
			onErrorSockVeicsTempoReal(event); 
		};
	
		socketVeicsTempoReal.onopen = function(event) {
			onOpenSockVeicsTempoReal(event);
		};
	
		socketVeicsTempoReal.onmessage = function(event) {
			onRecebeDadosSockVeicsTempoReal(event);
		}; 
	} 
	catch(e){
		console.log(e);
	}
}   

///////////////////////////////////////////////////////////////
/////////////////WEB SOCKET de VIDEO PASSAGEM TEMPO REAL
//////////////////////////////////////////////////////////////
var socketVideoPassagemTempoReal;

function IniciaWebSocketVideoPassagemTempoReal()
{
	try 
	{
		console.log("Host de webSocket Video e Passagem em Tempo Real: " + host);
		
		socketVideoPassagemTempoReal = new WebSocket(host);
		socketVideoPassagemTempoReal.binaryType = 'arraybuffer';
	
		socketVideoPassagemTempoReal.onerror = function(event) {
			onErrorSockVideoPassagemTempoReal(event); 
		};
	
		socketVideoPassagemTempoReal.onopen = function(event) {
			onOpenSockVideoPassagemTempoReal(event);
		};
	
		socketVideoPassagemTempoReal.onmessage = function(event) {
			onRecebeDadosSockVideoPassagemTempoReal(event);
		}; 
	} 
	catch(e){
		console.log(e);
	}
}
	
///////////////////////////////////////////////////////////////
/////////////////WEB SOCKET de BLITZ ELETRONICA
//////////////////////////////////////////////////////////////
var socketBlitzEletronica;

function IniciaWebSocketBlitzEletronica()
{
	try 
	{
		console.log("Host de webSocket Blitz Eletronica: " + host);
		
		socketBlitzEletronica = new WebSocket(host);
		socketBlitzEletronica.binaryType = 'arraybuffer';
	
		socketBlitzEletronica.onerror = function(event) {
			onErrorSockBlitzEletronica(event); 
		};
	
		socketBlitzEletronica.onopen = function(event) {
			onOpenSockBlitzEletronica(event);
		};
	
		socketBlitzEletronica.onmessage = function(event) {
			onRecebeDadosSockBlitzEletronica(event);
		}; 
	} 
	catch(e){
		console.log(e);
	}
}  