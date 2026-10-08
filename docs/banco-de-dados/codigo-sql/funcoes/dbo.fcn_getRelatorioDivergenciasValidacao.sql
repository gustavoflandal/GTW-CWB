CREATE FUNCTION [dbo].[fcn_getRelatorioDivergenciasValidacao]
(
	@dataInicio     DATE
   ,@dataFim        DATE
)
RETURNS TABLE
AS
	RETURN 
	(
		SELECT TOP 100 PERCENT
			   COALESCE(i.placa, v.placa, mi.placa, 'N/D') AS [Placa]
			  ,i.data AS [Data]
			  ,CONVERT(VARCHAR(8), i.data, 114) AS [Horário]
			  ,e.descricao AS [Enquadramento]
			  ,inc_p.descricao AS [Análise CAI]
			  ,inc_v.descricao AS [Análise CAV]
			  ,'' as [Confirma erro?]
		FROM   infracao i (NOLOCK)
			   INNER JOIN local l (NOLOCK)
					ON  i.id_local = l.id_local
						AND i.sequencia_local = l.sequencia_local
			   INNER JOIN configuracao_equipamento ce (NOLOCK)
					ON  l.id_configuracao_equipamento = ce.id_configuracao_equipamento
			   INNER JOIN veiculo v (NOLOCK)
					ON  i.id_veiculo = v.id_veiculo
			   INNER JOIN infracao_remessa ir (NOLOCK)
					ON  i.id_infracao = ir.id_infracao
			   INNER JOIN remessa r (NOLOCK)
					ON  ir.id_remessa = r.id_remessa
			   INNER JOIN movimento_importacao mi (NOLOCK)
					ON  r.codigo_externo = mi.id_movimento
						AND ir.sequencia = mi.sequencia
						AND i.id_enquadramento = mi.id_enquadramento
			   INNER JOIN inconsistencia inc_p (NOLOCK)
					ON  i.id_inconsistencia = inc_p.id_inconsistencia
			   INNER JOIN inconsistencia inc_v (NOLOCK)
					ON  mi.id_inconsistencia = inc_v.id_inconsistencia
			   INNER JOIN enquadramento e (NOLOCK)
					ON  e.id_enquadramento = i.id_enquadramento
			   LEFT JOIN sis_usuario su (NOLOCK)
					ON  su.cod_agente = mi.codigo_agente
		WHERE  CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim 
			   AND (
						(i.id_inconsistencia > 0 AND mi.validacao = 1)
					OR
						(i.id_inconsistencia = 0 AND mi.validacao = 0)
					OR
						(i.placa IS NOT NULL AND i.placa <> mi.placa)
				)

		GROUP BY
			   COALESCE(i.placa, v.placa, mi.placa, 'N/D')
			  ,i.data
			  ,CONVERT(VARCHAR(8), i.data, 114)
			  ,e.descricao
			  ,inc_p.descricao
			  ,inc_v.descricao

	)
