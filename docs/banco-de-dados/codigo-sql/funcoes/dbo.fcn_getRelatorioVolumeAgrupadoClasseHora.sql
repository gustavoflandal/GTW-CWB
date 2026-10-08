
--
-- Recria a function para o relatório de VOLUME
--
CREATE FUNCTION [dbo].[fcn_getRelatorioVolumeAgrupadoClasseHora](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(-- Cálculo Baseado na tabela 'veiculo sumarizado'
	SELECT
		lv.serie_equipamento [N/S],
		cep.cod_pista_alternativo AS [FX],
		cep.nome_pista [NOME],
		cv.descricao AS [CLASSE],
		sub2.[HORA],
		sub2.VOLUME
	FROM (	SELECT
				sub1.id_local,
				sub1.pista,
				sub1.id_classe,
				sub1.hora,
				SUM (sub1.trafego) AS [VOLUME]
			FROM (	SELECT
						vs.id_local,
						vs.pista,
						CASE 
							WHEN vs.id_classe = '' 
								THEN 'P' 
							ELSE 
								vs.id_classe 
						END AS id_classe,
						vs.hora,
						SUM (vs.trafego) AS [trafego]
					FROM
						veiculo_sumarizado vs (nolock)
					WHERE
						vs.data BETWEEN @dataInicio AND @dataFim
					GROUP BY
						vs.id_local, 
						vs.pista, 
						vs.id_classe, 
						vs.hora
					) AS sub1
			GROUP BY
				sub1.id_local, 
				sub1.pista, 
				sub1.id_classe, 
				sub1.hora		
		) AS sub2
			JOIN local_vigente lv (nolock)
				ON lv.id_local = sub2.id_local
			JOIN configuracao_equipamento_pista cep (nolock)
				ON cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
				AND cep.id_pista = sub2.pista
			JOIN classe_veiculo cv (nolock)
				ON cv.id_classe = sub2.id_classe
		WHERE CAST(lv.data_inicio AS DATE) < @dataInicio
			AND (lv.data_fim IS NULL OR CAST(lv.data_fim AS DATE) >= @dataFim)
)




