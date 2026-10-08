CREATE PROCEDURE [dbo].[spu_atualizar_cad_isento_antigo]
AS

	DECLARE @data_fim DATETIME = GETDATE()-20
	
	INSERT INTO [cad_isento_ant] with (rowlock)
           ([placa],
           [id_enquadramento],
           [area],
           [id_localidade],
           [modalidade],
           [data_inicio],
           [data_fim],
           [horario_inicio],
           [horario_fim],
           [data_atualizacao],
           [id_arquivo])
		   SELECT ci.placa,
			  ci.id_enquadramento,
			  ci.area,
			  ci.id_localidade,
			  ci.modalidade,
			  ci.data_inicio,
			  ci.data_fim,
			  ci.horario_inicio,
			  ci.horario_fim,
			  ci.data_atualizacao,
			  ci.id_arquivo
		  FROM [cad_isento] ci (nolock)
			LEFT JOIN cad_isento_ant cia (nolock)
				ON cia.id_arquivo = ci.id_arquivo 
				AND cia.placa = ci.placa 
				AND cia.id_enquadramento = ci.id_enquadramento 
				AND cia.area = ci.area
		  WHERE ci.placa in (	SELECT 
									i.placa 
								FROM 
									infracao i (nolock) 
								WHERE 
									i.id_enquadramento = ci.id_enquadramento
							)
			AND ci.data_atualizacao < @data_fim
			AND cia.placa IS NULL



