var divStatusProgresso;
var funcaoFinal;

function disparaProgresso(url, divSP, funcFinal) {
	divStatusProgresso = divSP;
	funcaoFinal = funcFinal;
	
	$.ajax({url: url, success: function(xml) {
		id_processo = $("ID_PROGRESSO",xml).text();
		verificaProgresso(id_processo);
	}, async: false});
	
}

function verificaProgresso(id_progresso) {
	var progresso = setInterval(
			function () {
				$.get('/ajax/InfoProgresso', {id_progresso: id_progresso}, function(xml) {
					divStatusProgresso.innerHTML = $("STATUS",xml).text()
					if ($("TERMINADO",xml).text() == "true") {
						clearInterval(progresso);
						funcaoFinal();
					}
				})
			}
			, 1000
	); 				
}