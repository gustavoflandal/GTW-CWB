
CREATE      
 FUNCTION [dbo].[fcn_buscaLocalStatus] ()      
 RETURNS TABLE AS RETURN      
      
 SELECT sub1.*,      
 CASE WHEN sub1.metrologico = 1 THEN sub1.data_afericao_met ELSE rnm.data END AS data_afericao,      
 CASE WHEN sub1.metrologico = 1 THEN sub1.data_validade_met ELSE rnm.data_valido END AS data_validade      
 FROM      
 (      
 SELECT l.id_local,      
   c.serie_equipamento,      
   l.nome,      
   ls.data_atualizacao,      
   ls.status_conexao,      
   ls.status_DIV,      
   ls.status_energia,      
   lsc.ip,      
   c.id_grupo_equipamento,      
   sub1.id_arquivo, sub1.data_video, sub1.data_valida,      
   sub4.id_arquivo AS id_arquivo_imgs, sub4.data_imagens,  
   MAX(sub2.id_arquivo) AS id_arquivo_laudo,      
   MAX(cea.data) data_afericao_met,      
   --MAX(cea.data_validade) data_validade_met,      
   DATEADD(YEAR, 1, MAX(cea.data)) AS data_validade_met,      
   MAX(CASE WHEN ceri.tipo = 'VL' THEN 1 ELSE 0 END) metrologico,    
   COALESCE(sub3.equipamentos,'') AS equipamentos       
  FROM      
   local_vigente l (NOLOCK)      
    LEFT JOIN local_status ls (NOLOCK) ON ls.id_local = l.id_local      
    LEFT JOIN local_status_conexao lsc (NOLOCK) ON lsc.id_local = l.id_local      
   INNER JOIN configuracao_equipamento c (NOLOCK) ON c.id_configuracao_equipamento = l.id_configuracao_equipamento      
   INNER JOIN configuracao_equipamento_afericao cea (NOLOCK) ON l.id_configuracao_equipamento = cea.id_configuracao_equipamento      
   INNER JOIN configuracao_equipamento_regra_infracao ceri (NOLOCK) ON l.id_configuracao_equipamento = ceri.id_configuracao_equipamento      
   LEFT JOIN (  
   SELECT id_local,id_arquivo,vf.data_video,data_valida FROM videos_fis vf (NOLOCK)      
   WHERE vf.id_arquivo IN (SELECT MAX(id_arquivo) FROM videos_fis (NOLOCK) GROUP BY id_local)) AS sub1      
   ON l.id_local = sub1.id_local      
   LEFT JOIN (  
   SELECT la.id_local, la.data_afericao, la.id_arquivo FROM laudo_afericao la (NOLOCK)       
   WHERE id_arquivo IN (SELECT MAX(id_arquivo) FROM laudo_afericao la (NOLOCK) WHERE la.nome_arquivo LIKE '%Cert%' GROUP BY la.id_local, la.data_afericao)      
   --ORDER BY la.id_local      
   ) AS sub2 ON l.id_local = sub2.id_local AND cea.data = sub2.data_afericao      
   LEFT JOIN (  
   SELECT id_configuracao_equipamento, COALESCE([1],'')+COALESCE([2],'')+COALESCE([3],'')+COALESCE([4],'')+COALESCE([5],'') AS equipamentos FROM    
   (SELECT id_configuracao_equipamento, id_pista, sentido FROM configuracao_equipamento_pista     
   ) AS sub1 PIVOT (MAX(sub1.sentido) FOR id_pista IN ([1],[2],[3],[4],[5])) AS pvt    
   ) AS sub3 ON l.id_configuracao_equipamento = sub3.id_configuracao_equipamento    
   LEFT JOIN (  
   SELECT ims.id_local,MIN(ims.id_arquivo) AS id_arquivo,ims.data_imagens FROM imagens_sinalizacao ims (NOLOCK)   
   JOIN (SELECT id_local, MAX(data_imagens) data_imagens FROM imagens_sinalizacao (NOLOCK) GROUP BY id_local) AS subx  
   ON ims.id_local = subx.id_local AND ims.data_imagens = subx.data_imagens  
   GROUP BY ims.id_local, ims.data_imagens  
   ) AS sub4 ON l.id_local = sub4.id_local  
   GROUP BY   
   l.id_local,      
   c.serie_equipamento,      
   l.nome,      
   ls.data_atualizacao,      
   ls.status_conexao,      
   ls.status_DIV,      
   ls.status_energia,      
   lsc.ip,      
   c.id_grupo_equipamento,      
   sub1.id_arquivo, sub1.data_video, sub1.data_valida,      
   sub4.id_arquivo, sub4.data_imagens  
   --,sub2.id_arquivo      
   ,COALESCE(sub3.equipamentos,'')    
  ) AS sub1      
  JOIN remessa_nao_metrologico rnm ON 1 = 1      
      
