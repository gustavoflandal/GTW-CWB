
CREATE VIEW [dbo].[infracao_processo_finalizada]
AS

SELECT 
	i.id_infracao,
	i.id_imagem_local,
	i.placa,
	i.id_enquadramento,
	i.id_inconsistencia,
	i.id_processo_concluido,
	i.id_processo,
	i.id_veiculo,
	i.id_local,
	i.sequencia_local,
	i.data
FROM  infracao i (nolock)
	LEFT JOIN processo p (nolock) 
		ON i.id_processo = p.id_processo
WHERE	p.ativo = 1 
	and p.id_processo_proximo IS NULL


