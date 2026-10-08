
CREATE PROCEDURE [dbo].[spu_finaliza_remessas_validadas_cai]  
AS

BEGIN

	UPDATE remessa WITH(ROWLOCK) 
	SET
	remessa.data_confirmacao = sub1.data_confirmacao,
	remessa.data_validacao = sub1.data_validacao,
	remessa.id_movimento_arquivo = sub1.id_movimento_arquivo  
	FROM 
	(
	SELECT r.id_remessa, COALESCE(ma.data_arquivo, ma.data_validacao) [data_confirmacao], ma.data_validacao, ma.id_movimento_arquivo FROM remessa r (NOLOCK) 
	JOIN movimento_arquivo ma (NOLOCK) 
	ON r.tipo = SUBSTRING(ma.nome_arquivo,3,2) AND r.codigo_externo = ma.id_movimento 
	WHERE r.id_movimento_arquivo IS NULL 
	) AS sub1 
	WHERE 
	remessa.id_remessa = sub1.id_remessa 

	PRINT @@ROWCOUNT 

	UPDATE lote_reprovado SET data_atualizacao = GETDATE(), ativo = 0 
	FROM remessa r WITH(NOLOCK) 
	WHERE 
	lote_reprovado.id_remessa = r.id_remessa 
	AND ativo = 1 
	AND r.data_confirmacao IS NOT NULL

END
