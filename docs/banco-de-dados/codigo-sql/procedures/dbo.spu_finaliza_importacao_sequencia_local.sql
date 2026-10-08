CREATE PROCEDURE [dbo].[spu_finaliza_importacao_sequencia_local]
AS
BEGIN

SET NOCOUNT ON

	DECLARE @Registros_Proc int = 1,
			@Amostra		int = 100000,
			@Total			int,
			@id				int,
			@Tempo_Processamento VARCHAR(8)	= '00:10:00',
			@Data_Fim DATETIME,
			@Registros_Proc_Total int = 0,
			@Rowcount int = 0

	SET @Data_Fim = GETDATE() + @Tempo_Processamento

	SELECT @Total = COUNT(*)
	FROM   veiculo_importacao vi (NOLOCK)
		   LEFT JOIN configuracao_equipamento_pendente_importacao_novo cepi (NOLOCK)
				ON  vi.id_veiculo_unic = cepi.id_veiculo_unic
	WHERE  vi.sequencia_local IS NULL
		   AND cepi.id_veiculo_unic IS NULL
		   AND vi.importar = 1

	EXEC @id = spu_log_inicia_processo 'finaliza_importacao_sequencia_local', @Total

	--DECLARE @Amostra INT = 100000
	DECLARE @tmp_veiculos_sequencia AS TABLE (id_veiculo_unic BIGINT, sequencia_local TINYINT)

	WHILE @Total > 0 AND @Registros_Proc > 0 AND GETDATE() < @Data_Fim
	BEGIN

	INSERT INTO @tmp_veiculos_sequencia (id_veiculo_unic, sequencia_local)
	--DECLARE @Amostra INT = 100000
	SELECT TOP (@Amostra) vi.id_veiculo_unic, lv.sequencia_local --INTO #tmp_veiculos_sequencia
	FROM   local_vigente lv (NOLOCK)
			INNER JOIN veiculo_importacao vi (NOLOCK)
				ON  vi.id_local = lv.id_local
					AND vi.sequencia_local IS NULL
			LEFT JOIN configuracao_equipamento_pendente_importacao_novo cepi (NOLOCK)
				ON  vi.id_veiculo_unic = cepi.id_veiculo_unic
		  --  INNER JOIN imagem_importacao ii (NOLOCK)
				--ON  ii.id_veiculo_unic = vi.id_veiculo_unic
	WHERE cepi.id_veiculo_unic IS NULL 
	--ORDER BY vi.data

	UPDATE veiculo_importacao WITH(ROWLOCK)
    SET    sequencia_local = sub1.sequencia_local
	FROM   @tmp_veiculos_sequencia AS sub1
	WHERE  veiculo_importacao.id_veiculo_unic = sub1.id_veiculo_unic 
    
	SET @Rowcount = @@ROWCOUNT
	SET @Registros_Proc = @Rowcount
	SET @Registros_Proc_Total = @Registros_Proc_Total + @Rowcount
	EXEC spu_log_atualiza_processo @id, @Registros_Proc
        
	DELETE @tmp_veiculos_sequencia

	IF NOT (@Registros_Proc > 0)
	BEGIN
		BREAK;
	END

	END

	PRINT ' - Registros Processados....: ' + dbo.fcn_FormataNumero(@Registros_Proc_Total)	+ ' - '	
	EXEC spu_log_finaliza_processo @id
END
