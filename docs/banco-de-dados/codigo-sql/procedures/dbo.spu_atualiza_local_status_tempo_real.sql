CREATE PROCEDURE [dbo].[spu_atualiza_local_status_tempo_real]
	@serie_equipamento  AS INT,
	@versao AS NCHAR(30),
	@tempo_executando AS BIGINT,
	@status_copia AS NCHAR(250),
	@ultima_deteccao DATETIME
AS
BEGIN

	DECLARE @registro_igual AS INT

	SELECT 
		@registro_igual = COUNT( serie_equipamento ) 
	FROM 
		status_tempo_real (nolock) 
	WHERE	serie_equipamento = @serie_equipamento 
		AND versao = @versao
		AND tempo_executando = @tempo_executando
		AND status_copia = @status_copia
		AND ultima_deteccao = @ultima_deteccao

	IF( @registro_igual = 0 )

		BEGIN
		
			DELETE
			FROM status_tempo_real with (rowlock)
			WHERE serie_equipamento = @serie_equipamento 
		
			INSERT INTO status_tempo_real with (rowlock)
				( serie_equipamento , versao , tempo_executando , status_copia , ultima_deteccao )
			VALUES
				( @serie_equipamento, @versao, @tempo_executando, @status_copia, @ultima_deteccao )
			
		END

END



