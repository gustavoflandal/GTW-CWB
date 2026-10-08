--Text
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

CREATE PROCEDURE [dbo].[spu_reposiciona_infracoes_sem_escala] 
	@data DATE, 
	@id_local INT
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @id_inconsistencia INT = 14 -- Veiculo não é infrator 
	DECLARE @tab_infracao TABLE (id_infracao INT NOT NULL) 
	DECLARE @id_usuario INT = (SELECT id_usuario FROM sis_usuario WHERE nome = 'sistema')
	DECLARE @id_processo INT = (SELECT id_processo FROM processo WHERE nome = 'Filtro Triagem')
	DECLARE @id_processo_n INT = (SELECT id_processo FROM processo WHERE nome = 'Triagem')
	DECLARE @linhas INT = 0

	-- Lista de infrações a alterar
	INSERT INTO @tab_infracao 
	SELECT id_infracao FROM infracao i (NOLOCK) 
	WHERE i.espera = 1 
	AND (@id_local IS NULL OR id_local = @id_local)
	AND CAST(i.data as date) = @data

	SET @linhas = @@ROWCOUNT

	BEGIN TRY
		BEGIN TRAN 
		-- Cancela processos (filtro triagem) anteriores
		UPDATE infracao_processo 
		SET status_processo = 2 
		--SELECT * FROM infracao_processo
		WHERE 
		id_infracao IN (SELECT id_infracao FROM @tab_infracao) AND 
		id_processo = @id_processo 

		-- Insere novos processos com a inconsistencia correta 
		INSERT INTO infracao_processo (data, tempo, id_infracao, id_processo, id_inconsistencia, id_usuario, status_processo, tempo_cliente) 
		SELECT GETDATE() data, 0 tempo, id_infracao, @id_processo id_processo, @id_inconsistencia id_inconsistencia, @id_usuario id_usuario, 0 status_processo, 0 tempo_cliente FROM
		@tab_infracao

		-- Atualiza tabela infracao com nova inconsistencia, tira da espera
		UPDATE infracao SET id_processo = @id_processo_n, id_processo_concluido = @id_processo, id_inconsistencia = @id_inconsistencia, espera = NULL  
		WHERE id_infracao IN (SELECT id_infracao FROM @tab_infracao) 

		COMMIT;

	END TRY

	BEGIN CATCH

		ROLLBACK;
		THROW;
	
	END CATCH
	--ROLLBACK

	RETURN @linhas
END

