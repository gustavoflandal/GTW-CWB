CREATE PROCEDURE [dbo].[spu_atualiza_processo_medicao]
	@id_processo_medicao INT
AS

	DECLARE @id_classificador_proto_cd_comprovacao INT = NULL
	DECLARE @id_classificador_planilha_comprovacao INT = NULL
	DECLARE @id_classificador_proto_cd_complemento INT = NULL
	DECLARE @id_classificador_planilha_quantitativos INT = NULL
	DECLARE @data_protocolo_exportacao DATETIME = NULL
	DECLARE @data_retorno_exportacao DATETIME = NULL

	SELECT 
		@id_classificador_proto_cd_comprovacao = id_classificador
	FROM 
		sis_documento_classificador (nolock) 
	WHERE 
		sigla = 'PCDC'

	SELECT 
		@id_classificador_planilha_comprovacao = id_classificador
	FROM 
		sis_documento_classificador (nolock) 
	WHERE 
		sigla = 'PLCDC'

	SELECT 
		@id_classificador_proto_cd_complemento = id_classificador
	FROM 
		sis_documento_classificador (nolock) 
	WHERE 
		sigla = 'PCDCC'

	SELECT 
		@id_classificador_planilha_quantitativos = id_classificador
	FROM 
		sis_documento_classificador (nolock) 
	WHERE 
		sigla = 'PLQTD'

	SELECT 
		@data_protocolo_exportacao = data_protocolo_exportacao
	FROM 
		processo_medicao (nolock) 
	WHERE 
		id_processo_medicao = @id_processo_medicao
	
	-- PROTOCOLO CD COMPROVAÇÃO
	
	IF EXISTS (	SELECT 
					id_documento 
				FROM 
					sis_documento (nolock)  
				WHERE	id_classificador = @id_classificador_proto_cd_comprovacao 
					AND	identificador_externo = @id_processo_medicao
				)
	--já foi anexado, então atualizaremos a data...

		BEGIN

			UPDATE processo_medicao with (rowlock) 
				SET data_protocolo_exportacao = GETDATE()
				WHERE	id_processo_medicao = @id_processo_medicao 
					AND data_protocolo_exportacao IS NULL

		END

-- PLANILHA RETORNO

	IF EXISTS (	SELECT 
					id_documento 
				FROM 
					sis_documento (nolock)  
				WHERE	id_classificador = @id_classificador_planilha_comprovacao 
					AND	identificador_externo = @id_processo_medicao
				)

		--já foi anexado, então atualizaremos a data...
		BEGIN

			UPDATE processo_medicao with (rowlock)
				SET data_retorno_exportacao = GETDATE()
				WHERE	id_processo_medicao = @id_processo_medicao 
					AND data_retorno_exportacao IS NULL
		
		END

-- PROTOCOLO CD COMPLEMENTO

	IF EXISTS (	SELECT 
					id_documento 
				FROM 
					sis_documento (nolock)  
				WHERE	id_classificador = @id_classificador_proto_cd_complemento 
					AND	identificador_externo = @id_processo_medicao
				)

		--já foi anexado, então atualizaremos a data...
		BEGIN

			UPDATE processo_medicao with (rowlock) 
				SET data_protocolo_complemento = GETDATE()
				WHERE	id_processo_medicao = @id_processo_medicao 
					AND data_protocolo_complemento IS NULL

		END

-- PLANILHA QUANTITATIVOS

	IF EXISTS (	SELECT 
					id_documento 
				FROM 
					sis_documento (nolock)  
				WHERE	id_classificador = @id_classificador_planilha_quantitativos 
					AND	identificador_externo = @id_processo_medicao
				)
		--já foi anexado, então atualizaremos a data...
		BEGIN

			UPDATE processo_medicao with (rowlock)
				SET data_planilha_quantitativos = GETDATE()
				WHERE	id_processo_medicao = @id_processo_medicao 
					AND data_planilha_quantitativos IS NULL

		END



