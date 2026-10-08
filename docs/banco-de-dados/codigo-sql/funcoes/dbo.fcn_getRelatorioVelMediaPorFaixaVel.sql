CREATE FUNCTION [dbo].[fcn_getRelatorioVelMediaPorFaixaVel](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
	(
		--DECLARE @dataInicio DATETIME = '2018-05-01', @dataFim DATETIME = '2018-05-31'
		SELECT ROW_NUMBER() OVER(ORDER BY datas.Data) AS ordem,
			   CONVERT(VARCHAR(10), datas.Data, 103) AS data,
			   ISNULL([51-55],0) AS [51-55],ISNULL([56-60], 0) AS [56-60],ISNULL([61-65], 0) AS [61-65],ISNULL([66-70], 0) AS [66-70],
			   ISNULL([71-75], 0) AS [71-75],ISNULL([76-80], 0) AS [76-80],ISNULL([81-85], 0) AS [81-85],ISNULL([86-90], 0) AS [86-90],
			   ISNULL([91-95], 0) AS [91-95],ISNULL([96-100], 0) AS [96-100],ISNULL([101-105], 0) AS [101-105],ISNULL([106-110], 0) AS [106-110],
			   ISNULL([111-115], 0) AS [111-115],ISNULL([116-120], 0) AS [116-120],ISNULL([121-125], 0) AS [121-125],ISNULL([126-130], 0) AS [126-130],
			   ISNULL([131-135], 0) AS [131-135],ISNULL([136-140], 0) AS [136-140],ISNULL([141-145], 0) AS [141-145],ISNULL([146-150], 0) AS [146-150],
			   ISNULL([+ 150], 0) AS [+ 150]
		FROM   dbo.fcn_ObterDatasPeriodo(@dataInicio, @dataFim) AS datas
			   LEFT JOIN (
							SELECT *
							FROM   (
										SELECT CAST(i.data AS DATE) AS data,
											   i.id_infracao,
											   CASE WHEN v.velocidade_media BETWEEN 51 AND 55 THEN '51-55'
													WHEN v.velocidade_media BETWEEN 56 AND 60 THEN '56-60'
													WHEN v.velocidade_media BETWEEN 61 AND 65 THEN '61-65'
													WHEN v.velocidade_media BETWEEN 66 AND 70 THEN '66-70'
													WHEN v.velocidade_media BETWEEN 71 AND 75 THEN '71-75'
													WHEN v.velocidade_media BETWEEN 76 AND 80 THEN '76-80'
													WHEN v.velocidade_media BETWEEN 81 AND 85 THEN '81-85'
													WHEN v.velocidade_media BETWEEN 86 AND 90 THEN '86-90'
													WHEN v.velocidade_media BETWEEN 91 AND 95 THEN '91-95'
													WHEN v.velocidade_media BETWEEN 96 AND 100 THEN '96-100'
													WHEN v.velocidade_media BETWEEN 101 AND 105 THEN '101-105'
													WHEN v.velocidade_media BETWEEN 106 AND 110 THEN '106-110'
													WHEN v.velocidade_media BETWEEN 111 AND 115 THEN '111-115'
													WHEN v.velocidade_media BETWEEN 116 AND 120 THEN '116-120'
													WHEN v.velocidade_media BETWEEN 121 AND 125 THEN '121-125'
													WHEN v.velocidade_media BETWEEN 126 AND 130 THEN '126-130'
													WHEN v.velocidade_media BETWEEN 131 AND 135 THEN '131-135'
													WHEN v.velocidade_media BETWEEN 136 AND 140 THEN '136-140'
													WHEN v.velocidade_media BETWEEN 141 AND 145 THEN '141-145'
													WHEN v.velocidade_media BETWEEN 146 AND 150 THEN '146-150'
													WHEN v.velocidade_media > 150 THEN '+ 150'
											   END faixa
										FROM   infracao i (NOLOCK)
											   INNER JOIN veiculo v (NOLOCK)
													ON  v.id_veiculo = i.id_veiculo
											   INNER JOIN infracao_remessa ir (NOLOCK)
													ON  ir.id_infracao = i.id_infracao
										WHERE  CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim
											   AND i.id_enquadramento = 99999
							) AS vm
							PIVOT
									(
										COUNT(vm.id_infracao)
										FOR faixa IN ([51-55],[56-60],[61-65],[66-70],[71-75],[76-80],[81-85],[86-90],[91-95],[96-100],[101-105],[106-110],[111-115],[116-120],[121-125],[126-130],[131-135],[136-140],[141-145],[146-150],[+ 150])
									) AS cont_vm
			   ) AS inf
					ON  inf.data = datas.Data
	)
