CREATE PROCEDURE [dbo].[spu_criar_processo_medicao]
	@mes SMALLINT,
	@ano SMALLINT
AS 

	DECLARE @id_processo_medicao INT
	DECLARE @data_inicio DATETIME
	DECLARE @data_final DATETIME

	SET NOCOUNT ON 

	SELECT @data_inicio = CONVERT(DATETIME,STR(@ano)+'-'+STR(@mes)+'-01 00:00:00',120)
	SELECT @data_final = DATEADD(SECOND,-1,DATEADD(month,1,@data_inicio))

	INSERT INTO processo_medicao with (rowlock)
		(mes, ano, data_criacao, periodo_ini, periodo_fim)
	VALUES 
		(@mes, @ano, GETDATE(), @data_inicio, @data_final)

	SELECT @id_processo_medicao = SCOPE_IDENTITY()

	RETURN @id_processo_medicao
	



