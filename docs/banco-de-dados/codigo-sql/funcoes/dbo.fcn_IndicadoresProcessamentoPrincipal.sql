
CREATE FUNCTION [dbo].[fcn_IndicadoresProcessamentoPrincipal]
(	
)
RETURNS TABLE 
AS
RETURN 
(
	SELECT 
		info_atual_processamento.dias_atraso_atual,
		ind_at.imagem_dia_atraso as total_atraso,
		ind_ant.imagem_dia_atraso as total_atraso_ant,
		ind_at.imagem_dia_erro as total_erro,
		ind_ant.imagem_dia_erro as total_erro_ant,
		info_cad_isento.dias_ultima_importacao as dias_atraso_cad_isento
 FROM fcn_IndicadoresProcessamentoAgrupado(DATEADD(MONTH,-1,CAST(CAST(DATEADD(DAY,(DATEPART(DAY,GETDATE())-1)*-1,GETDATE()) AS DATE) AS DATETIME)), CAST(CAST(DATEADD(DAY,(DATEPART(DAY,GETDATE())-1)*-1,GETDATE()) AS DATE) AS DATETIME), 12) AS ind_ant,
	fcn_IndicadoresProcessamentoAgrupado(CAST(CAST(DATEADD(DAY,(DATEPART(DAY,GETDATE())-1)*-1,GETDATE()) AS DATE) AS DATETIME), GETDATE(), 12) AS ind_at,
		(
			SELECT 
				DATEDIFF(day, MIN(i.data), 
				getDate()) as dias_atraso_atual 
			FROM infracao i (nolock)
				LEFT JOIN solicitacao_auditoria_infracao sai (nolock) 
					ON sai.id_infracao = i.id_infracao
			WHERE	i.id_enquadramento > 1 
				AND	i.id_processo IN (NULL, 1, 2, 3, 11, 20, 21, 23, 24) 
				AND	sai.id_infracao IS NULL
		) as info_atual_processamento,
		(
			SELECT 
				MAX(sub1.dui) AS dias_ultima_importacao 
			FROM (	SELECT id_enquadramento, MIN(DATEDIFF(DAY, data_hora, GETDATE())) AS dui 
						FROM cad_isento_arquivo (nolock)
						GROUP BY id_enquadramento
				) AS sub1
		) AS info_cad_isento
)


