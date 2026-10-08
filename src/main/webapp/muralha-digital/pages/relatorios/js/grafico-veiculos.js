$(document).ready(function() 
{
	CarregaComponenteDataHora();
	obterEquipamentos();
	CriarGraficoBarra('Gráfico de Veículos por Período')
});
function ValidarObterDadosGraficoVeiculos()
{
  	var tipoPeriodo = document.getElementById("selTipo");
  	var idTipoPeriodo = "1";
  	if (tipoPeriodo)
  		idTipoPeriodo = tipoPeriodo.value;
  		
  	if (idTipoPeriodo == 1)
	ValidarObterDadosGrafico('veiculosPorPeriodo',120,'2 Horas');
	else if (idTipoPeriodo == 2)
	ValidarObterDadosGrafico('veiculosPorPeriodo',720,'12 Horas');
	else if (idTipoPeriodo == 3)
	ValidarObterDadosGrafico('veiculosPorPeriodo',17280,'12 Dias');
}