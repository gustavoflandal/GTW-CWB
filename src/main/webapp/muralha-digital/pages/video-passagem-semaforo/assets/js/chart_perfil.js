 
var chartPerfil;
var array_label 	= [];
var array_pontos1 	= [];
var array_pontos2 	= [];

function processa_perfil(perfil1, perfil2)
{
	var qtde = 0
	var pontos_1 = perfil1.split(',');
	var pontos_2 = perfil2.split(',');	
	
	if (pontos_1.length < pontos_2.length){
		qtde = pontos_1.length; 
	}else{
		qtde = pontos_2.length;
	}
	
	array_label 	= [];
	array_pontos1 	= [];
	array_pontos2 	= [];
	
	for (i = 0; i < qtde; i++)
	{
		array_label.push('');
		array_pontos1.push(pontos_1[i]);
		array_pontos2.push(pontos_2[i]);	
	}
}

function cria_perfil(perfil1, perfil2)
{
	processa_perfil(perfil1, perfil2);
	
	if (chartPerfil) {
		 chartPerfil.destroy();
	}	

	const ctx = document.getElementById('canvas_perfil_magnetico');
	chartPerfil = new Chart(ctx, {
								    type: 'line',
								    data: 
									{
										labels: array_label,
									    datasets: 
										[
											{
										        label: 'Perfil 1',
												data: array_pontos1,
										        borderWidth: 2,
												cubicInterpolationMode: 'monotone',
										      	tension: 0.4
										     }, 
											 {
										        label: 'Perfil 2',
												data: array_pontos2,
										        borderWidth: 2,
												cubicInterpolationMode: 'monotone',
										      	tension: 0.4
										      }
									]
								    },
								    options: {
										responsive: true,
								    	plugins: {
								      		title: {
								        			display: true,
								        			text: 'Perfil Magnético do Veículo'
								      			}, 
										},	
								      	scales: {
								        	y: {
								          		beginAtZero: true
								        	}
								      	}
								    }
								  });	
}
  