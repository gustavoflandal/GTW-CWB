
--CREATE 
CREATE 
FUNCTION [dbo].[fcn_getRelatorioVeiculosInfratores](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	SELECT 
		sub1.pista AS						[Faixa de Rolamento],
		sub1.nome AS						[Local/Sentido],
		CONVERT(varchar, sub1.data, 103) AS [Data],
		CASE DATEPART(DW, sub1.data)
			WHEN 1 THEN 'DOMINGO'
			WHEN 2 THEN 'SEGUNDA'
			WHEN 3 THEN 'TERÇA'
			WHEN 4 THEN 'QUARTA'
			WHEN 5 THEN 'QUINTA'
			WHEN 6 THEN 'SEXTA'
			WHEN 7 THEN 'SÁBADO'
		END AS			[Dia de Semana],
		CONVERT(varchar, sub1.data, 8) AS	[Horário],
		CONVERT(INT,sub1.velocidade) AS		[Velocidade], 
		sub1.id_classe AS					[Classificação],
		sub1.id_enquadramento AS			[Enquadramento] 
	FROM	(SELECT 
				i.pista,
				lv.nome,
				i.data,
				v.velocidade,
				v.id_classe, 
				i.id_enquadramento 
			FROM infracao i (nolock)
				INNER JOIN veiculo v (nolock) 
					ON i.id_veiculo = v.id_veiculo  
				INNER JOIN local_vigente lv (nolock) 
					ON lv.id_local = i.id_local 
			WHERE 
				CONVERT(DATE,i.data) BETWEEN @dataInicio AND @dataFim 
			) AS sub1 
)



