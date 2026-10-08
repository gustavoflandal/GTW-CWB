  
CREATE VIEW [dbo].[v_infracao_enquadramento_inconsistencia] AS  
  
 SELECT i.id_enquadramento,   
        e.descricao,   
		i.id_inconsistencia,
		inc.descricao AS descricao_inconsistencia,
        --CASE WHEN e.infracao_metrologia = 1 AND v.velocidade >= (2 * i.velocidade_limite) THEN 99 ELSE inc.id_inconsistencia END AS id_inconsistencia,   
        --CASE WHEN e.infracao_metrologia = 1 AND v.velocidade >= (2 * i.velocidade_limite) THEN 'Vel. 100% Acima da Vel. Regul.' ELSE inc.descricao END AS descricao_inconsistencia,   
   CAST(i.data AS DATE) AS Data,   
   COUNT(*) AS total   
 FROM   infracao i  (NOLOCK)  
  INNER JOIN veiculo v (NOLOCK)  
      ON  i.id_veiculo = v.id_veiculo    
   LEFT JOIN infracao_remessa ir (NOLOCK)   
         ON  ir.id_infracao = i.id_infracao   
        INNER JOIN enquadramento e (NOLOCK)   
         ON  e.id_enquadramento = i.id_enquadramento   
        INNER JOIN inconsistencia inc (NOLOCK)   
         ON  inc.id_inconsistencia = i.id_inconsistencia   
 WHERE  ir.id_infracao IS NULL   
        --AND ((inc.id_inconsistencia > 0) OR ( v.velocidade >= (2 * i.velocidade_limite) ))  
        AND i.id_processo = 4   
 GROUP BY   
        i.id_enquadramento,   
        e.descricao,   
		i.id_inconsistencia,
		inc.descricao,
        --CASE WHEN e.infracao_metrologia = 1 AND v.velocidade >= (2 * i.velocidade_limite) THEN 99 ELSE inc.id_inconsistencia END,   
        --CASE WHEN e.infracao_metrologia = 1 AND v.velocidade >= (2 * i.velocidade_limite) THEN 'Vel. 100% Acima da Vel. Regul.' ELSE inc.descricao END,   
        CAST(i.data AS DATE)   
 --ORDER BY   
 --       id_inconsistencia,   
 --       i.id_enquadramento,   
 --       CAST(i.data AS DATE)   
  
  
