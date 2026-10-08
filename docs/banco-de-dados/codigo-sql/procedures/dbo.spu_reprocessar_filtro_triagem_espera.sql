CREATE PROCEDURE [dbo].[spu_reprocessar_filtro_triagem_espera] 
AS
BEGIN
	--BEGIN TRAN 
	UPDATE infracao WITH(ROWLOCK) SET espera = NULL FROM 
	(
	SELECT i.id_infracao FROM infracao i (ROWLOCK)
	JOIN veiculo v (NOLOCK) ON i.id_veiculo = v.id_veiculo 
	WHERE 
	i.id_processo = 20 
	AND i.espera = 1 
	AND i.id_enquadramento > 1 
	AND v.codigo_prodam IS NOT NULL 
	AND EXISTS(
				SELECT 1
				FROM   agenda_estatico ae (NOLOCK)
					   JOIN agenda_estatico_item aei (NOLOCK)
							ON  ae.id_agenda_estatico = aei.id_agenda_estatico
				WHERE  aei.status = 1
					   AND ae.id_local = i.id_local
					   AND CAST(i.data AS DATE) = aei.data_operacao
					   AND CAST(i.data AS TIME) BETWEEN aei.hora_inicio AND aei.hora_fim
			  )
	) AS sub1
	WHERE infracao.id_infracao = sub1.id_infracao 
	--ROLLBACK 
END

