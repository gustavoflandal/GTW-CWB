CREATE VIEW muralha.v_integridade_recepcao
AS
SELECT g.id_local,
       CAST(g.dt_captura AS DATE)                                                AS dia,
       COUNT(*)                                                                  AS recebidos,
       SUM(CASE WHEN g.status_integridade = 'INTEGRO'         THEN 1 ELSE 0 END) AS integros,
       SUM(CASE WHEN g.status_integridade = 'DIVERGENTE'      THEN 1 ELSE 0 END) AS divergentes,
       SUM(CASE WHEN g.status_integridade = 'SEM_CONFERENCIA' THEN 1 ELSE 0 END) AS sem_conferencia,
       SUM(CASE WHEN g.status_integridade = 'ERRO_LEITURA'    THEN 1 ELSE 0 END) AS erro_leitura,
       SUM(CASE WHEN g.status_integridade IN ('DIVERGENTE', 'ERRO_LEITURA') THEN 1 ELSE 0 END) AS rejeitados,
       MIN(g.dt_captura)                                                         AS primeira_captura,
       MAX(g.dt_captura)                                                         AS ultima_captura,
       MAX(g.dt_verificacao)                                                     AS ultima_verificacao
FROM muralha.vtr_imagem_integridade g
GROUP BY g.id_local, CAST(g.dt_captura AS DATE);
