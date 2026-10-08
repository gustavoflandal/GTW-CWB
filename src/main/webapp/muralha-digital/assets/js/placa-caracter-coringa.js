$().ready(function () 
{
	$('.placa-caracter-coringa-2').on('keypress', function (e) 
	{
        var input = $(this);
        var value = input.val();
        var key = e.originalEvent.key;
        
        ValidarPlacaCaracterEspecial(e, value, key, 2);
	});
	
	$('.placa-caracter-coringa-4').on('keypress', function (e) 
	{
        var input = $(this);
        var value = input.val();
        var key = e.originalEvent.key;
        
        ValidarPlacaCaracterEspecial(e, value, key, 4);
	});
});

function ValidarPlacaCaracterEspecial(e, value, key, qtdeMaxCaracterEspecial)
{
	// Não permitir inserir caracter, a menos que seja no final da string 
    if (e.target.selectionStart < value.length)
    {
		e.preventDefault();
		
		// Reposicionar o cursor para o final da string
    	e.target.setSelectionRange(value.length, value.length);
	}
        
    value += key;
    var placa = value;
        
    try 
    {
		var regExp = new RegExp("[*]", "gi");
	  	var count = (placa.match(regExp) || []).length;
	  	var okQtdeCoringa = count <= qtdeMaxCaracterEspecial;
	  	
		if (!okQtdeCoringa) e.preventDefault();
	  	placa = TratarCaracterEspecialPlaca(placa);
    		
		var pattern = /^(([A-Z]{0,3}))([0-9]{1}[A-Z0-9]{1})?([0-9]{0,2})$/i;
		var ok = pattern.test(placa);
		
		if (!ok) e.preventDefault();
	} 
	catch (e) 
	{
//		HandleErrorMessages(e);
	}
}

function TratarCaracterEspecialPlaca(placa)
{
	for(var i = 0; i < placa.length; i++)
	    if (placa[i] === "*") placa = SubstituirCaracterEspecialPlaca(placa, i);
	
	return placa;
}

function SubstituirCaracterEspecialPlaca(placa, posicao)
{
	switch (posicao)
	{
		case 0:
		case 1:
		case 2:
	    	placa = placa.replaceAt(posicao, 'A');
	    	break;
	  	case 3:
	  	case 4:
	  	case 5:
	  	case 6:
	    	placa = placa.replaceAt(posicao, '1');
	    	break;
	}
	
	return placa;
}

String.prototype.replaceAt = function(index, replacement) {
    if (index >= this.length) {
        return this.valueOf();
    }
 
    return this.substring(0, index) + replacement + this.substring(index + 1);
}