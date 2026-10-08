$().ready(function () 
{
	$('.mes').mask('NN', {'translation': {
	    'N': {pattern: /[0-9]/}
	  }
	});
	$('.ano').mask('ABBB', {'translation': {
	    'A': {pattern: /[1-2]/},
	    'B': {pattern: /[0-9]/}
	  }
	});
	
	obterEquipamentos();
});
