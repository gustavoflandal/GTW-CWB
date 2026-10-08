CREATE PROCEDURE [dbo].[spu_insere_evento_csx] 
   @proprietario varchar(50),
   @data_hora datetime,
   @id_categoria int,
   @id_evento int,
   @descricao varchar(100),
   @mensagem varchar(512),
   @id_prioridade int,
   @id_nivel int,
   @usuario varchar(25) = NULL
   
AS
	DECLARE @id_proprietario INT

	SET @id_proprietario = (SELECT TOP 1 id_proprietario
							FROM   eventos_csx_desc_proprietario (NOLOCK)
							WHERE  proprietario = @proprietario)

	IF (@id_proprietario IS NULL)
	BEGIN
		INSERT INTO eventos_csx_desc_proprietario with (rowlock) (proprietario) VALUES (@proprietario)
		SET @id_proprietario = SCOPE_IDENTITY()
	END

	--DECLARE @usuario varchar(25) = 'thiago.surgik'
	IF NOT EXISTS (SELECT 1 FROM eventos_csx_usuarios WHERE usuario = @usuario)
	BEGIN
		INSERT INTO eventos_csx_usuarios VALUES (@usuario)
	END

	INSERT INTO eventos_csx with (rowlock) (
	   id_proprietario,
	   data_hora,
	   id_categoria,
	   id_evento,   
	   mensagem,
	   id_prioridade,
	   id_nivel,
	   usuario)
	VALUES (
	   @id_proprietario,
	   @data_hora,
	   @id_categoria,
	   @id_evento,   
	   @mensagem,
	   @id_prioridade,
	   @id_nivel,
	   @usuario)

	RETURN @@ROWCOUNT
