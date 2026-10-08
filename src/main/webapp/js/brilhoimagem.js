function buscaImagemAjuste(idImagem){
	var params = {};
	
	params['Action'] = 'GetImagemAjuste';
	params['IdImagem'] = idImagem;

	$.ajax({url: '/ajax/ImagemAjusteController', data: params, success: function(xml) {

		if($("IdImagem", xml).text())
		{
            var brilho = document.getElementById("brilho");
            var contraste = document.getElementById("contraste");
			brilho.value = $("Brilho", xml).text();
			contraste.value = $("Contraste", xml).text();
		}
	
    }, async: false});
}

function enviaImagemAjuste(vlBrilho, vlContraste){
    var params = {
    	Action    : 'SetImagemAjuste',
		IdImagem  : getIdImagemSel(),
		Brilho    : vlBrilho,
		Contraste : vlContraste
    };

	$.ajax({url: '/ajax/ImagemAjusteController', data: params, success: function(xml) {
    }, async: false});
}