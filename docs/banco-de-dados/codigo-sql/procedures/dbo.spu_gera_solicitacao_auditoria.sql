CREATE PROCEDURE [dbo].[spu_gera_solicitacao_auditoria]
	@data_imagens DATE,
	@id_processo INT,
	@id_usuario INT
AS

BEGIN TRY 

	DECLARE @total_consistentes INT
	DECLARE @total_inconsistentes INT
	DECLARE @id_solicitacao_auditoria INT
	DECLARE @lista_infracoes TABLE 
	(
		id_infracao INT, 
		consistente BIT
	)
	
	INSERT INTO @lista_infracoes
	SELECT 
		id_infracao,
		(CASE 
			WHEN id_inconsistencia > 0 
				THEN 0 
			ELSE 
				1 
		END) 
	FROM 
		infracao i (nolock)
	WHERE	id_processo = @id_processo 
		AND	CAST(data AS DATE) = @data_imagens 
		AND	id_infracao NOT IN (SELECT 
									id_infracao 
								FROM 
									solicitacao_auditoria_infracao (nolock)
								WHERE 
									id_infracao = i.id_infracao)
	
	IF (@@ROWCOUNT = 0)

		BEGIN

			PRINT 'NÃO EXISTEM IMAGENS PARA O DIA SOLICITADO!'
			RAISERROR('NÃO EXISTEM IMAGENS PARA O DIA SOLICITADO!', 16, 1)

		END

	BEGIN TRANSACTION
		
		INSERT INTO solicitacao_auditoria with (rowlock) 
			(data_imagens, id_usuario)
		VALUES 
			(@data_imagens,@id_usuario)
		
		SET @id_solicitacao_auditoria = @@IDENTITY
		
		--Iserindo as consistentes
		INSERT INTO solicitacao_auditoria_infracao with (rowlock) 
			(id_solicitacao_auditoria, id_infracao)
		SELECT
			@id_solicitacao_auditoria, 
			id_infracao 
		FROM
			@lista_infracoes li
		WHERE
			consistente = 1
			
		SET @total_consistentes = @@ROWCOUNT
		
		--Iserindo as inconsistentes
		INSERT INTO solicitacao_auditoria_infracao with (rowlock) 
			(id_solicitacao_auditoria, id_infracao)
		SELECT
			@id_solicitacao_auditoria, 
			id_infracao 
		FROM
			@lista_infracoes li
		WHERE
			consistente = 0
			
		SET @total_inconsistentes = @@ROWCOUNT

		--Atualizando totalizadores
		UPDATE solicitacao_auditoria 
		SET	total_consistentes = @total_consistentes,
			total_inconsistentes = @total_inconsistentes,
			total_imagens = @total_consistentes + @total_inconsistentes
		WHERE
			id_solicitacao_auditoria = @id_solicitacao_auditoria
		
	COMMIT	

	RETURN @id_solicitacao_auditoria

END TRY 

BEGIN CATCH 

	IF (@@TRANCOUNT > 0) 
		ROLLBACK 
		 
	EXEC spu_replica_erro 
	 
	RETURN -1
	 
END CATCH


