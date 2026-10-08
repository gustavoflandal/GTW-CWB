
IP_API_MONITORAMENTO_TRECHO = "189.112.204.89:8003"

//Configurações de centralização dos mapas
var LatLngPadraoCuritiba = {
	lat: -25.432947,
	lng: -49.270591
};

var LatLngPadraoEunapolis = {
	lat: -16.363826,
	lng: -39.576247
};

var LatLngPadraoBeloHorizonte = {
	lat: -19.923927,
	lng: -43.958581
};

var LatLngPadraoAraxa = {
	lat: -19.589789,
	lng: -46.938783
};

var LatLngPadraoSeteLagoas = {
	lat: -19.453456,
	lng: -44.243670
};

var LatLngPadraoSaoSebastiaoParaiso = {
	lat: -20.915668,
	lng: -46.983374
};

var LatLngPadraoMossoroRN = {
	lat: -5.192671114435731,
	lng: -37.34507415265819
};

var LatLngPadraoOlindaPE = {
	lat: -7.991286185377773,
	lng: -34.86303303613399
};

var LatLngPadraoBetim = {
	lat: -19.952367358553754,
	lng: -44.19713343402478
};

var LatLngPadraoVitoriaDerES = {
	lat: -20.292316276159376,
	lng: -40.343336886648025
};

var LatLngPadraoCataguases = {
	lat: -21.387658,
	lng: -42.695612
};

var LatLngPadraoIpatinga = {
	lat: -19.47206400842364,
	lng: -42.55245406292123
};

var LatLngPadraoSaoPaulo = {
	lat: -23.570340,
	lng: -46.670918
};

//Configurações de zoom de centralização dos mapas
var ZOOM_PADRAO_CURITIBA 				= 11;
var ZOOM_PADRAO_EUNAPOLIS 				= 13;
var ZOOM_PADRAO_BELO_HORIZONTE			= 11;
var ZOOM_PADRAO_ARAXA					= 13;
var ZOOM_PADRAO_SETE_LAGOAS				= 13;
var ZOOM_PADRAO_SAO_SEBASTIAO_PARAISO	= 13;
var ZOOM_PADRAO_MOSSORO_RN				= 13;
var ZOOM_PADRAO_OLINDA_PE				= 13;
var ZOOM_PADRAO_BETIM					= 13;
var ZOOM_PADRAO_VITORIA_DER_ES			= 10;
var ZOOM_PADRAO_CATAGUASES			    = 14;
var ZOOM_PADRAO_IPATINGA			    = 14;
var ZOOM_PADRAO_SAO_PAULO			    = 13;

//Configurações de códigos de municipios
var ID_MUNICIPIO_BELO_HORIZONTE 		= 4123;
var ID_MUNICIPIO_CURITIBA 				= 7535;
var ID_MUNICIPIO_EUNAPOLIS 				= 3117;
var ID_MUNICIPIO_ARAXA 					= 4079;
var ID_MUNICIPIO_SETE_LAGOAS 			= 5343;
var ID_MUNICIPIO_SAO_SEBASTIAO_PARAISO 	= 5293;
var ID_MUNICIPIO_MOSSORO_RN			 	= 1759;
var ID_MUNICIPIO_OLINDA_PE			 	= 2491;
var ID_MUNICIPIO_BETIM		 			= 4133;
var ID_MUNICIPIO_VITORIA_DER_ES			= 5705;
var ID_MUNICIPIO_CATAGUASES				= 4305;
var ID_MUNICIPIO_IPATINGA				= 4625;
var ID_MUNICIPIO_SAOPAULO				= 35;



//Configurações iniciais para mapas e municpio
var LatLngPadrao = LatLngPadraoIpatinga;
var ZOOM_PADRAO = ZOOM_PADRAO_IPATINGA;
var ID_MUNICIPIO_PADRAO = ID_MUNICIPIO_IPATINGA;


function AtualizarLocalizacaoMapas()
{
	console.log("AtualizarLocalizacaoMapas");
	
	var municipio = document.getElementById("selectMunMapa").value;
	
	var latLng = null;
	var zoom = null;
	var mapa = null;
	
	console.log("Municipio: " + municipio);
	
	if (municipio == ID_MUNICIPIO_BELO_HORIZONTE || (ID_MUNICIPIO_BELO_HORIZONTE == ID_MUNICIPIO_PADRAO && municipio == 0)) //BELO HORIZONTE
	{
		latLng = LatLngPadraoBeloHorizonte;
		zoom = ZOOM_PADRAO_BELO_HORIZONTE;
	}
	else if (municipio == ID_MUNICIPIO_CURITIBA || (ID_MUNICIPIO_CURITIBA == ID_MUNICIPIO_PADRAO && municipio == 0)) //CURITIBA
	{
		latLng = LatLngPadraoCuritiba;
		zoom = ZOOM_PADRAO_CURITIBA;
	}
	else if (municipio == ID_MUNICIPIO_EUNAPOLIS || (ID_MUNICIPIO_EUNAPOLIS == ID_MUNICIPIO_PADRAO && municipio == 0)) //EUNAPOLIS
	{
		latLng = LatLngPadraoEunapolis;
		zoom = ZOOM_PADRAO_EUNAPOLIS;
	}
	else if (municipio == ID_MUNICIPIO_ARAXA || (ID_MUNICIPIO_ARAXA == ID_MUNICIPIO_PADRAO && municipio == 0)) //ARAXÁ
	{
		latLng = LatLngPadraoAraxa;
		zoom = ZOOM_PADRAO_ARAXA;
	}
	else if (municipio == ID_MUNICIPIO_SETE_LAGOAS || (ID_MUNICIPIO_SETE_LAGOAS == ID_MUNICIPIO_PADRAO && municipio == 0)) //SETE LAGOAS
	{
		latLng = LatLngPadraoSeteLagoas;
		zoom = ZOOM_PADRAO_SETE_LAGOAS;
	}
	else if (municipio == ID_MUNICIPIO_SAO_SEBASTIAO_PARAISO || (ID_MUNICIPIO_SAO_SEBASTIAO_PARAISO == ID_MUNICIPIO_PADRAO && municipio == 0)) //SÃO SEBASTIÃO DO PARAÍSO
	{
		latLng = LatLngPadraoSaoSebastiaoParaiso;
		zoom = ZOOM_PADRAO_SAO_SEBASTIAO_PARAISO;
	}
	else if (municipio == ID_MUNICIPIO_MOSSORO_RN || (ID_MUNICIPIO_MOSSORO_RN == ID_MUNICIPIO_PADRAO && municipio == 0)) //MOSSORÓ-RN
	{
		latLng = LatLngPadraoMossoroRN;
		zoom = ZOOM_PADRAO_MOSSORO_RN;
	}
	else if (municipio == ID_MUNICIPIO_OLINDA_PE || (ID_MUNICIPIO_OLINDA_PE == ID_MUNICIPIO_PADRAO && municipio == 0)) //OLINDA-PE
	{
		latLng = LatLngPadraoOlindaPE;
		zoom = ZOOM_PADRAO_OLINDA_PE;
	}
	else if (municipio == ID_MUNICIPIO_BETIM || (ID_MUNICIPIO_BETIM == ID_MUNICIPIO_PADRAO && municipio == 0)) //BETIM-MG
	{
		latLng = LatLngPadraoBetim;
		zoom = ZOOM_PADRAO_BETIM;
	}
	else if (municipio == ID_MUNICIPIO_VITORIA_DER_ES || (ID_MUNICIPIO_VITORIA_DER_ES == ID_MUNICIPIO_PADRAO && municipio == 0)) //VITORIA-ES (DER-ES)
	{
		latLng = LatLngPadraoVitoriaDerES;
		zoom = ZOOM_PADRAO_VITORIA_DER_ES;
	}
	else if (municipio == ID_MUNICIPIO_CATAGUASES || (ID_MUNICIPIO_CATAGUASES == ID_MUNICIPIO_PADRAO && municipio == 0)) //CATAGUASES-MG
	{
		latLng = LatLngPadraoCataguases;
		zoom = ZOOM_PADRAO_CATAGUASES;
	}
	else if (municipio == ID_MUNICIPIO_IPATINGA || (ID_MUNICIPIO_IPATINGA == ID_MUNICIPIO_PADRAO && municipio == 0)) //IPATINGA-MG
	{
		latLng = LatLngPadraoIpatinga;
		zoom = ZOOM_PADRAO_IPATINGA;
	}
	else if (municipio == ID_MUNICIPIO_SAOPAULO || (ID_MUNICIPIO_SAOPAULO == ID_MUNICIPIO_PADRAO && municipio == 0)) //SÃO PAULO-SP
	{
		latLng = LatLngPadraoSaoPaulo;
		zoom = ZOOM_PADRAO_SAO_PAULO;
	}
	
	console.log("latLng: " + latLng);
	console.log("zoom: " + zoom);
	
	
	//Mapa 3D
	AtualizarLocalizacao(mapa_google_3d, latLng, zoom);
	
	//Mapa de concentração
	AtualizarLocalizacao(mapa_concentracao, latLng, zoom);
	
}

function AtualizarLocalizacao(mapa, latLng, zoom)
{
	if (mapa != null && latLng != null)
		mapa.setCenter(latLng);
	if (mapa != null && zoom != null)
		mapa.setZoom(zoom);
}
