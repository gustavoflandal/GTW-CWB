CREATE PROCEDURE [dbo].[spu_finaliza_trata_registro_duplicado]
AS

SET NOCOUNT ON
 
DECLARE	@Amostra				int			= 5000,
		@Data_Fim				datetime,
		@Registros_Proc			int			= 0,
		@Registros_Proc_Total	int			= 0

BEGIN

	BEGIN TRY

		--DECLARE @Amostra INT = 5000
		DECLARE @temp_01 AS TABLE (id_veiculo_local INT, data DATETIME, id_local INT, id_enquadramento INT, quantidade INT)
		DECLARE @temp_02 AS TABLE (id_veiculo_local INT, data DATETIME, id_local INT, id_enquadramento INT, id_veiculo_unic BIGINT)
		DECLARE @temp_03 AS TABLE (id_veiculo_local INT, data DATETIME, id_local INT, id_enquadramento INT, id_veiculo_unic BIGINT)

		--1. Quais infrações foram afetadas?
		INSERT INTO @temp_01
		--DECLARE @Amostra INT = 5000
		SELECT TOP (@Amostra)
			   vi.id_veiculo_local
			  ,vi.data
			  ,vi.id_local
			  ,ii.id_enquadramento
			  ,COUNT(*) cnt
		FROM   veiculo_importacao vi (NOLOCK)
			   INNER JOIN infracao_importacao ii (NOLOCK)
					ON  ii.id_veiculo_unic = vi.id_veiculo_unic
			   INNER JOIN imagem_importacao img_imp (NOLOCK)
					ON  img_imp.id_veiculo_unic = vi.id_veiculo_unic
		WHERE  vi.importar = 1 
			   AND img_imp.indice_imagem = 0
		GROUP BY
			   vi.id_veiculo_local
			  ,vi.data
			  ,vi.id_local
			  ,ii.id_enquadramento
		HAVING COUNT(*) > 1

		INSERT INTO @temp_02
		SELECT infracao.id_veiculo_local
			  ,infracao.data
			  ,infracao.id_local
			  ,infracao.id_enquadramento
			  ,infracao.id_veiculo_unic
		FROM   @temp_01 tmp001
			   JOIN (
						SELECT vi.id_veiculo_local
							  ,vi.data
							  ,vi.id_local
							  ,ii.id_enquadramento
							  ,ii.id_veiculo_unic
						FROM   veiculo_importacao vi (NOLOCK)
							   INNER JOIN infracao_importacao ii (NOLOCK)
									ON  ii.id_veiculo_unic = vi.id_veiculo_unic
			   ) infracao
					ON  infracao.id_veiculo_local = tmp001.id_veiculo_local
						AND infracao.data = tmp001.data
						AND infracao.id_local = tmp001.id_local
						AND infracao.id_enquadramento = tmp001.id_enquadramento
		--SELECT * FROM @temp_02

		--2. Das infrações que foram afetadas, obter todas exceto o último registro, em ordem de entrada no sistema (mesma ordem csx5)
		INSERT INTO @temp_03
		SELECT id_veiculo_local
			  ,data
			  ,id_local
			  ,id_enquadramento
			  ,id_veiculo_unic
		FROM   @temp_02
		WHERE  id_veiculo_unic NOT IN (
										SELECT MAX(id_veiculo_unic) id_veiculo_unic
										FROM   @temp_02
										GROUP BY
											   id_veiculo_local
											  ,data
											  ,id_local
											  ,id_enquadramento
								)
		--SELECT * FROM @temp_03

		--3. Move estas infrações para o processo Arquivo Morto
		BEGIN TRAN 
		UPDATE veiculo_importacao WITH(ROWLOCK)
		SET    importar = 0
		WHERE  id_veiculo_unic IN (SELECT id_veiculo_unic FROM @temp_03) 

		SET @Registros_Proc	= @@ROWCOUNT
		SET @Registros_Proc_Total= @Registros_Proc_Total + @Registros_Proc

		COMMIT;
		
	END TRY 

	BEGIN CATCH

		PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'

		IF @@TRANCOUNT > 0 
			ROLLBACK;

	END CATCH

END

PRINT ' - [Registro Duplicado] - registros tratados....: ' + dbo.fcn_FormataNumero(@Registros_Proc_Total)	+ ' - '
