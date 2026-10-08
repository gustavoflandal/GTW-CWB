
CREATE PROCEDURE [dbo].[spu_finaliza_movimento_rejeitado] 
AS

-- Finaliza Lote Rejeitado

SET NOCOUNT ON 
SET ANSI_WARNINGS OFF 

BEGIN TRY
BEGIN TRAN

DECLARE @codigo_externo INT

PRINT 'STEP 1: Verificar se existe lote rejeitado' 
-- Verificar se existe um lote rejeitado
SELECT TOP(1) @codigo_externo = r.id_remessa 
--SELECT r.id_remessa  
FROM remessa r (NOLOCK) 
LEFT JOIN movimento_importacao mi (NOLOCK)
 ON r.id_movimento_arquivo = mi.id_movimento_arquivo  
GROUP BY r.id_remessa 
HAVING COUNT(mi.sequencia) = 0

PRINT @codigo_externo

IF @codigo_externo IS NOT NULL
BEGIN

DECLARE @id_remessa INT
DECLARE @id_movimento_arquivo INT
DECLARE @id_movimento_arquivo_alt INT
DECLARE @id_infracao INT

DECLARE @cont INT = 1 

WHILE @cont > 0
BEGIN

SELECT 
TOP(1) 
@id_remessa = r.id_remessa, 
@id_movimento_arquivo = r.id_movimento_arquivo, 
@id_movimento_arquivo_alt = sub1.id_movimento_arquivo,
@id_infracao = ir.id_infracao  
FROM remessa r (NOLOCK) 
JOIN infracao_remessa ir (NOLOCK) ON ir.id_remessa = r.id_remessa 
JOIN (
 SELECT id_movimento, MAX(id_movimento_arquivo) AS id_movimento_arquivo,
 enquadramento_regra_infracao.id_enquadramento   
 FROM movimento_arquivo (NOLOCK) 
 LEFT JOIN enquadramento_regra_infracao (NOLOCK) ON tipo_apait = SUBSTRING(nome_arquivo, 3, 2)
 WHERE id_tipo = 5 
 GROUP BY id_movimento, nome_arquivo, enquadramento_regra_infracao.id_enquadramento
) AS sub1 
ON sub1.id_movimento = r.codigo_externo
AND sub1.id_enquadramento = r.id_enquadramento 
JOIN infracao i (NOLOCK) ON ir.id_infracao = i.id_infracao 
WHERE r.id_movimento_arquivo <> sub1.id_movimento_arquivo
AND i.id_processo = 25

--SELECT @id_remessa, @id_movimento_arquivo, @id_movimento_arquivo_alt, @id_infracao

SET @cont = @@ROWCOUNT

IF @cont > 0
BEGIN
PRINT 'STEP 2: Reposicionando infracao ' + CAST(@id_infracao AS VARCHAR)
EXEC spu_reposiciona_infracao_processo @id_infracao, 3 
END

END

UPDATE remessa WITH(ROWLOCK) SET remessa.id_movimento_arquivo = sub1.id_movimento_arquivo 
FROM remessa r (ROWLOCK) 
JOIN (
 SELECT id_movimento, MAX(id_movimento_arquivo) AS id_movimento_arquivo,
 enquadramento_regra_infracao.id_enquadramento   
 FROM movimento_arquivo (NOLOCK) 
 LEFT JOIN enquadramento_regra_infracao (NOLOCK) ON tipo_apait = SUBSTRING(nome_arquivo, 3, 2)
 WHERE id_tipo = 5 
 GROUP BY id_movimento, nome_arquivo, enquadramento_regra_infracao.id_enquadramento
) AS sub1 
ON sub1.id_movimento = r.codigo_externo
AND sub1.id_enquadramento = r.id_enquadramento 
WHERE r.id_movimento_arquivo <> sub1.id_movimento_arquivo 

END

COMMIT

END TRY
BEGIN CATCH

IF @@TRANCOUNT > 0 
BEGIN
	PRINT 'ERRO, FAZENDO ROLLBACK' 
	ROLLBACK
END

END CATCH

