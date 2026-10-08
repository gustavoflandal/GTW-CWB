CREATE VIEW [dbo].[v_enquadramentos_ativos] AS

SELECT l.id_local, l.sequencia_local, 
CAST(l.data_atualizacao  AS DATE) AS data_inicio, 
CAST(ln.data_atualizacao AS DATE) AS data_fim, 
eri.id_enquadramento,eri.descricao_apait,MAX(CAST(ceri.ativo AS INT)) AS ativo,cep.id_pista,cep.cod_pista_alternativo,cep.cod_pista,cep.cod_pista_prodam
FROM local l (NOLOCK) 
LEFT JOIN local ln (NOLOCK) ON l.id_local = ln.id_local AND ln.sequencia_local = l.sequencia_local + 1
JOIN configuracao_equipamento_regra_infracao ceri (NOLOCK) 
	ON l.id_configuracao_equipamento = ceri.id_configuracao_equipamento 
JOIN enquadramento_regra_infracao eri (NOLOCK) 
	ON ceri.tipo = eri.tipo 
JOIN configuracao_equipamento_pista cep (NOLOCK) 
	ON cep.id_configuracao_equipamento = l.id_configuracao_equipamento 
	AND (cep.id_pista = ceri.id_pista OR ceri.id_pista IS NULL)
WHERE eri.id_enquadramento > 1
GROUP BY 
l.id_local, l.sequencia_local, 
CAST(l.data_atualizacao  AS DATE), 
CAST(ln.data_atualizacao AS DATE), 
eri.id_enquadramento,eri.descricao_apait,cep.id_pista,cep.cod_pista_alternativo,cep.cod_pista,cep.cod_pista_prodam

