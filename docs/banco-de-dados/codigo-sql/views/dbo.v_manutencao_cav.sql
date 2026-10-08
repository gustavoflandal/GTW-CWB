
CREATE VIEW [dbo].[v_manutencao_cav]
AS

SELECT m.id_manutencao, cep.cod_pista, cep.cod_pista_alternativo, COALESCE(es.codigo_prodam, cep.cod_pista_prodam) cod_pista_prodam, 
CASE WHEN em.descricao_apait = 'VELOCIDADE' THEN NULL ELSE em.id_enquadramento END AS id_enquadramento, 
em.descricao_apait AS tipo_grupo_autuador, CAST(m.data_inicio AS DATE) AS data_inicio, m.descricao, CAST(COALESCE(m.data_conclusao, m.data_previsto) AS DATE) AS data_fim, 
CASE WHEN m.data_conclusao IS NOT NULL THEN 'TÉRMINO' ELSE 'PREVISÃO' END AS estado FROM manutencao m (NOLOCK) 
LEFT JOIN equipamento_estatico es (NOLOCK) ON m.serie_equipamento = es.serie_equipamento
JOIN local_vigente lv (NOLOCK) ON m.id_local = lv.id_local
JOIN configuracao_equipamento_pista cep (NOLOCK) ON lv.id_configuracao_equipamento = cep.id_configuracao_equipamento AND (m.id_pista IS NULL OR m.id_pista = cep.id_pista)
JOIN v_enquadramentos_ativos em (NOLOCK) ON lv.id_local = em.id_local AND m.data_ocorrencia > em.data_inicio AND m.data_ocorrencia < COALESCE(em.data_fim,GETDATE()) --AND lv.sequencia_local = em.sequencia_local 
AND cep.id_pista = em.id_pista AND (m.tipo_grupo_autuador IS NULL OR m.tipo_grupo_autuador = em.descricao_apait)
WHERE m.encaminhar = 1
GROUP BY m.id_manutencao, cep.cod_pista, cep.cod_pista_alternativo, COALESCE(es.codigo_prodam, cep.cod_pista_prodam),
CASE WHEN em.descricao_apait = 'VELOCIDADE' THEN NULL ELSE em.id_enquadramento END, em.descricao_apait, CAST(m.data_inicio AS DATE),
m.descricao, CAST(COALESCE(m.data_conclusao, m.data_previsto) AS DATE), CASE WHEN m.data_conclusao IS NOT NULL THEN 'TÉRMINO' ELSE 'PREVISÃO' END

--SELECT m.id_manutencao, cep.cod_pista, cep.cod_pista_alternativo, COALESCE(es.codigo_prodam, cep.cod_pista_prodam) cod_pista_prodam, em.id_enquadramento, 
--em.descricao_apait AS tipo_grupo_autuador, CAST(m.data_inicio AS DATE) AS data_inicio, m.descricao, CAST(COALESCE(m.data_conclusao, m.data_previsto) AS DATE) AS data_fim, 
--CASE WHEN m.data_conclusao IS NOT NULL THEN 'TÉRMINO' ELSE 'PREVISÃO' END AS estado FROM manutencao m (NOLOCK) 
--LEFT JOIN equipamento_estatico es (NOLOCK) ON m.serie_equipamento = es.serie_equipamento
--JOIN local_vigente lv (NOLOCK) ON m.id_local = lv.id_local
--JOIN configuracao_equipamento_pista cep (NOLOCK) ON lv.id_configuracao_equipamento = cep.id_configuracao_equipamento
--JOIN v_enquadramentos_manutencao em (NOLOCK) ON (m.tipo_grupo_autuador IS NULL OR m.tipo_grupo_autuador = em.descricao_apait)
--AND (m.id_pista IS NULL OR m.id_pista = cep.id_pista)

