
CREATE PROCEDURE [dbo].[spu_ObterAmostraRemessa] @id_remessa INT
AS

DECLARE @tab_amostra TABLE 
(
	id_remessa INT, 
	id_infracao INT, 
	inconsistente TINYINT, 
	escolha FLOAT
) 

DECLARE @total_infracao INT 
DECLARE @total_consistente INT
DECLARE @total_inconsistente INT 
DECLARE @tamanho_amostra INT 

DECLARE @amostra_consistente	INT
DECLARE @amostra_inconsistente	INT

DECLARE @per_consist	FLOAT = 0.0 
DECLARE @per_inconsist	FLOAT = 0.0 

IF NOT EXISTS(	
			SELECT 
				1 
			FROM 
				remessa_amostragem (nolock) 
			WHERE 
				id_remessa = @id_remessa	)
BEGIN

SELECT 
	@total_infracao =	 COUNT(i.id_infracao),
	@total_consistente = SUM(CASE WHEN i.id_inconsistencia = 0 THEN 1 ELSE 0 END),
	@total_inconsistente = SUM(CASE WHEN i.id_inconsistencia > 0 THEN 1 ELSE 0 END)
FROM infracao_remessa ir (nolock) 
	JOIN infracao i (nolock) 
		ON ir.id_infracao = i.id_infracao
WHERE 
	ir.id_remessa = @id_remessa
	AND i.id_processo = 3

SELECT @tamanho_amostra = tamanho_amostra 
	FROM chave_valor cv_nivel (nolock) 
	INNER JOIN chave_valor cv_nqa (nolock) 
		ON cv_nqa.chave = 'nqa' 
	INNER JOIN amostragem_nivel amn (nolock) 
		ON amn.nivel = cv_nivel.valor
	INNER JOIN amostragem am (nolock) 
		ON	am.id_nivel = amn.id_nivel 
		AND @total_infracao BETWEEN am.tamanho_inicial AND am.tamanho_final 
	INNER JOIN amostragem_tamanho amt (nolock) 
		ON amt.nqa = cv_nqa.valor 
		AND amt.codigo = am.codigo 
	WHERE cv_nivel.chave = 'nivel_inspecao'

IF  @total_infracao      IS NOT NULL 
AND @total_consistente   IS NOT NULL 
AND @total_inconsistente IS NOT NULL
BEGIN

SET @per_consist			= ROUND(CONVERT(FLOAT, @total_consistente)		/ CONVERT(FLOAT, @total_infracao) , 3 )
SET @per_inconsist			= ROUND(CONVERT(FLOAT, @total_inconsistente)	/ CONVERT(FLOAT, @total_infracao) , 3 )
SET @amostra_consistente	= CONVERT(INT, @per_consist * @tamanho_amostra)
SET @amostra_inconsistente	= CONVERT(INT, @per_inconsist * @tamanho_amostra)

IF	@amostra_consistente	= 0
	SET @amostra_consistente= @tamanho_amostra - @amostra_inconsistente

IF	@amostra_inconsistente	= 0 
	SET @amostra_inconsistente	= @tamanho_amostra - @amostra_consistente

IF  (@amostra_consistente + @amostra_inconsistente) < @tamanho_amostra 
	SET @amostra_consistente	= @tamanho_amostra - @amostra_inconsistente

INSERT INTO @tab_amostra 
-- DECLARE @id_remessa INT = 51
SELECT 
	sub1.id_remessa, 
	sub1.id_infracao, 
	sub1.inconsistente, 
	RAND(sub1.semente) * 1e18 AS escolha 
FROM (	-- DECLARE @id_remessa INT = 51
		SELECT 
			ir.id_remessa, 
			ir.id_infracao, 
			CASE 
				WHEN i.id_inconsistencia > 0 
					THEN 1 
				ELSE 
					0 
			END AS inconsistente,
			isnull(CONVERT(INT, i.data),2)/3 * isnull(i.id_infracao,3) + isnull(i.id_imagem_local,2) - isnull(i.id_enquadramento,3)
				+ isnull(i.id_inconsistencia,3) -isnull( COALESCE(i.id_processo, 0),3) + isnull(COALESCE(i.id_processo_concluido, 0),3)
				- isnull(COALESCE(i.sequencia_local, 0),3) + isnull(COALESCE(i.id_usuario_atual, 0),3) + isnull(COALESCE(i.id_usuario_final, 0),2)
				+ isnull(CONVERT(INT, i.data_entrada),3) - isnull(CONVERT(INT, i.data_afericao),3) + isnull(CONVERT(INT, GETDATE()),3)
			AS semente 
		FROM infracao i (nolock) 
			INNER JOIN infracao_remessa ir (nolock) 
				ON i.id_infracao = ir.id_infracao
		WHERE	ir.id_remessa = @id_remessa 
			AND i.id_processo = 3
		) AS sub1
ORDER BY escolha 

INSERT INTO remessa_amostragem with (rowlock)
SELECT TOP(@amostra_consistente)	
	id_remessa, 
	id_infracao 
FROM @tab_amostra 
WHERE inconsistente = 0
ORDER BY escolha 

INSERT INTO remessa_amostragem with (rowlock)
SELECT TOP(@amostra_inconsistente)	
	id_remessa, 
	id_infracao 
FROM @tab_amostra 
WHERE inconsistente = 1 
ORDER BY escolha 

-- Verificar se existem infracoes de velocidade 100% acima da velocidade regulamentada
INSERT INTO remessa_amostragem with (rowlock)
SELECT 
	ir.id_remessa, 
	ir.id_infracao 
FROM infracao i (NOLOCK) 
	INNER JOIN enquadramento e (nolock) 
		ON i.id_enquadramento = e.id_enquadramento 
	INNER JOIN infracao_remessa ir (nolock) 
		ON i.id_infracao = ir.id_infracao 
WHERE	ir.id_remessa = @id_remessa 
	AND i.id_processo = 3 
	AND e.infracao_metrologia = 1 
	AND i.velocidade_considerada >= (i.velocidade_limite * 2) 

END

END

PRINT @@ROWCOUNT

