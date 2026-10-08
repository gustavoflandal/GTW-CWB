
CREATE PROCEDURE [dbo].[spu_integridade_infracoes]
AS

DECLARE	@data_ini date
DECLARE	@data_fim date

DECLARE @idLocal INT
DECLARE @idImagemLocal INT
DECLARE @data DATETIME

DECLARE @id_veiculo_local BIGINT

DECLARE @ano INT
DECLARE @mes INT
DECLARE @dia INT

DECLARE @currLocal INT
DECLARE @currImagem INT
DECLARE @currData DATETIME

SET @data_ini = CAST(DATEADD(dd, -12, GetDate()) AS DATE)
SET @data_fim = CAST(DATEADD(dd, -1, GetDate()) AS DATE)

SET @currLocal = 0
SET @currImagem = 0
SET @id_veiculo_local = 0
SET @currData = GETDATE()

DELETE 
FROM falha_sequencia_imagem with (rowlock)
WHERE	CAST(data_imagem_antes AS DATE) >= CAST (@data_ini AS DATE)
	OR	(ano = YEAR(data_imagem_antes)
		AND mes = MONTH(data_imagem_antes)
  		AND id_imagem_local_antes = 0)

DECLARE local_cursor CURSOR LOCAL FAST_FORWARD 
FOR (
SELECT * 
FROM (	SELECT
			inf.id_local, 
			img.id_imagem_local, 
			inf.data,
			vei.id_veiculo_local, 
			YEAR(inf.data) AS ano,
			MONTH(inf.data) AS mes, 
			DAY(inf.data) AS dia
		FROM infracao inf (nolock)
			INNER JOIN veiculo vei (nolock)
				ON inf.id_veiculo = vei.id_veiculo
			INNER JOIN veiculo_imagem img (nolock)
				ON vei.id_veiculo = img.id_veiculo
			INNER JOIN imagem_info ii (nolock)
				ON ii.id_imagem = img.id_imagem
		WHERE CAST(inf.data AS DATE) BETWEEN @data_ini AND @data_fim
				-- HACK: Evita as imagens teste de Curitiba, que estão
				-- vindo "bugadas" do importador, com id_imagem_local = 0
			AND NOT (inf.id_enquadramento = 1 AND img.id_imagem_local = 0)
	
		UNION
	
		SELECT
			lvg.id_local, 
			0, 
			CAST( LTRIM(STR(YEAR(@data_fim))) + '-' + LTRIM(STR(MONTH(@data_fim))) + '-01' AS DATETIME), 
			0,
			YEAR(@data_fim), 
			MONTH(@data_fim), 1
		FROM 
			local_vigente lvg (nolock)
		WHERE
			-- Apenas locais "ativos".
			lvg.data_inicio <= GetDate()
		) AS sub
	) ORDER BY sub.id_local, sub.ano, sub.mes, sub.dia, sub.id_veiculo_local
    
 OPEN local_cursor
  
 FETCH NEXT FROM local_cursor 
 INTO 
	@idLocal, 
	@idImagemLocal, 
	@data, 
	@id_veiculo_local, 
	@ano, 
	@mes, 
	@dia

    -- Laço
 WHILE @@FETCH_STATUS = 0

	BEGIN
  
		IF (@currLocal != @idLocal)

			-- Trocou o local
			BEGIN

				PRINT 'Novo Local:' + CAST(@idLocal AS CHAR(4))

  				SET @currLocal = @idLocal
				SET @currImagem = @idImagemLocal
				SET @currData = @data
	  
				FETCH NEXT FROM local_cursor 
				INTO 
					@idLocal, 
					@idImagemLocal, 
					@data, 
					@id_veiculo_local, 
					@ano, 
					@mes, 
					@dia

				CONTINUE

			END
    
		IF (MONTH(@currData) != MONTH(@data))

			-- Trocou o mês
			BEGIN

  				SET @currLocal = @idLocal
				SET @currImagem = @idImagemLocal
				SET @currData = @data

				FETCH NEXT FROM local_cursor 
				INTO 
					@idLocal, 
					@idImagemLocal, 
					@data, 
					@id_veiculo_local, 
					@ano, 
					@mes, 
					@dia

				CONTINUE

			END    
    
		IF (@currImagem + 1 != @idImagemLocal)

			BEGIN

				INSERT INTO falha_sequencia_imagem with (rowlock) (
					ano, 
					mes, 
					id_local, 
					id_imagem_local_antes,
					id_imagem_local_depois, 
					data_imagem_antes, 
					data_imagem_depois) 
				VALUES ( 
					YEAR(@currData), 
					MONTH(@currData), 
					@currLocal, 
					@currImagem, 
					@idImagemLocal, 
					@currData, 
					@data)

			END
    
		SET @currLocal = @idLocal;
		SET @currImagem = @idImagemLocal;
		SET @currData = @data;  
    
		FETCH NEXT FROM local_cursor 
		INTO 
			@idLocal, 
			@idImagemLocal, 
			@data, 
			@id_veiculo_local, 
			@ano, 
			@mes, 
			@dia

	END

CLOSE local_cursor
DEALLOCATE local_cursor



