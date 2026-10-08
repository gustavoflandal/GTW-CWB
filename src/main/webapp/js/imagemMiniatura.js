function buscaImagemMiniatura(IdInfracao){
	var params = {};
	
	params['Action'] = 'GetImagemMiniatura';
	params['IdInfracao'] = IdInfracao;

	$.ajax({url: '/ajax/ImagemMiniaturaController', data: params, success: function(xml) {

		if($("IdInfracao", xml).text())
		{
			var idImagemPrincipal = $("IdImagemPrincipal", xml).text();
			var idImagemMiniatura = $("IdImagemMiniatura", xml).text();
			var idPosicao = $("IdPosicao", xml).text();
			var idTamanho = $("IdTamanho", xml).text();
			
			setImagemPrincipal(idImagemPrincipal);
			setImagemMiniatura(idImagemMiniatura);
			setPosicaoMiniatura(idPosicao);
			setTamanhoMiniatura(idTamanho);
			
			carregaImagemPrincipalMiniatura(idImagemPrincipal, "I", null, idImagemPrincipal, idImagemMiniatura, idPosicao, idTamanho)
		}
	
    }, async: false});
}

function enviaImagemMiniatura(idInfracao){
    var idImagemPrincipal = obterImagemPrincipal();
    var idImagemMiniatura = obterImagemMiniatura();
    var posicao = obterPosicaoMiniatura();
    var tamanho = obterTamanhoMiniatura();
    
    if (idImagemPrincipal != null && idImagemMiniatura != null && posicao != null && tamanho != null) {
    
	    var params = {
	    	Action    : 'SetImagemMiniatura',
	    	id_infracao: idInfracao,
	    	id_imagem_principal: idImagemPrincipal,
	    	id_imagem_miniatura: idImagemMiniatura,
	    	posicao_miniatura: posicao,
	    	tamanho_miniatura: tamanho
	    };
	
		$.ajax({url: '/ajax/ImagemMiniaturaController', data: params, success: function(xml) {
	    }, async: false});
	
    }
}
