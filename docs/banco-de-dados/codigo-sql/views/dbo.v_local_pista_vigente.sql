CREATE VIEW [dbo].[v_local_pista_vigente]        
AS        
 SELECT l.id_local,        
     l.sequencia_local,        
     cep.nome_pista AS nome,        
     cep.id_pista,        
     cep.cod_pista_alternativo,        
     l.id_configuracao_equipamento,        
     ce.serie_equipamento,        
             
  CASE WHEN LEN(cod_GIT_Faixa) >= 9  
    THEN cep.cod_GIT_Faixa  
    ELSE CAST(LTRIM(RTRIM(CAST(ce.cod_GIT_Contrato AS VARCHAR(4)))) +        
     LTRIM(RTRIM(RIGHT('000'+CAST(ce.cod_GIT_Ponto AS VARCHAR(6)),3))) +  
     LTRIM(RTRIM(CAST(cep.cod_GIT_Logradouro AS VARCHAR(2)))) +        
     LTRIM(RTRIM(CAST(cep.cod_GIT_Pista AS VARCHAR(2)))) +        
     LTRIM(RTRIM(CAST(cep.cod_GIT_Sentido AS VARCHAR(2)))) +        
     LTRIM(RTRIM(CAST(cep.cod_GIT_Faixa AS VARCHAR(2)))) AS INT)  
  END AS codigo_GIT,  
  
  CASE WHEN LEN(cod_GIT_Faixa) >= 9  
    THEN CAST(SUBSTRING(CAST(cep.cod_GIT_Faixa AS VARCHAR), 1, (LEN(cep.cod_GIT_Faixa) - 1)) AS INT)  
    ELSE CAST(LTRIM(RTRIM(CAST(ce.cod_GIT_Contrato AS VARCHAR(4)))) +        
      LTRIM(RTRIM(RIGHT('000'+CAST(ce.cod_GIT_Ponto AS VARCHAR(6)),3))) +        
      LTRIM(RTRIM(CAST(cep.cod_GIT_Logradouro AS VARCHAR(2)))) +        
      LTRIM(RTRIM(CAST(cep.cod_GIT_Pista AS VARCHAR(2)))) +        
      LTRIM(RTRIM(CAST(cep.cod_GIT_Sentido AS VARCHAR(2)))) AS INT)  
  END AS codigo_GIT_local,  
  
  su.usuario,        
     ce.em_operacao,        
     l.posicao_lat,        
     l.posicao_lon,        
     ce.data_inicio,        
     CASE WHEN ( CAST(ISNULL(cep.faixa_exclusiva_direita,0) AS INT) + CAST(ISNULL(cep.faixa_exclusiva_esquerda,0) AS INT) ) > 0 THEN 1 ELSE 0 END AS faixa_exclusiva,        
     (CASE WHEN ce.flag_opcao & 1 = 0 THEN 1 ELSE 0 END) AS desativado,      
  MAX(a.data) AS data_afericao,
  L.localidade_desc,
  cep.sentido
  FROM   local AS l (NOLOCK)         
     INNER JOIN dbo.configuracao_equipamento AS ce (NOLOCK)        
    ON  ce.id_configuracao_equipamento = l.id_configuracao_equipamento        
     AND ce.ativo = 1        
     INNER JOIN dbo.sis_usuario su (NOLOCK)        
    ON  ce.id_usuario = su.id_usuario        
     INNER JOIN configuracao_equipamento_pista cep (NOLOCK)        
    ON  cep.id_configuracao_equipamento = ce.id_configuracao_equipamento        
  LEFT OUTER JOIN dbo.configuracao_equipamento_afericao AS a (nolock)       
 ON  a.id_configuracao_equipamento = ce.id_configuracao_equipamento      
 AND (a.id_pista IS NULL OR a.id_pista = cep.id_pista)    
 WHERE  L.id_local NOT IN (82, 83, 84, 85, 86)    
  AND cep.pista_1_transversal = 0 AND cep.pista_2_transversal = 0 AND cep.pista_3_transversal = 0 AND cep.pista_4_transversal = 0        
     AND cep.pista_5_transversal = 0 AND cep.pista_6_transversal = 0 AND cep.pista_7_transversal = 0 AND cep.pista_8_transversal = 0        
 GROUP BY        
     l.id_local,        
     l.sequencia_local,        
     cep.nome_pista,        
     l.id_configuracao_equipamento,        
     ce.serie_equipamento,        
     su.usuario,        
     ce.em_operacao,        
     l.posicao_lat,        
     l.posicao_lon,        
     ce.data_inicio,        
     cep.id_pista,        
     cep.cod_pista_alternativo,        
     ce.cod_GIT_Ponto, ce.cod_GIT_Contrato, cep.cod_GIT_Logradouro, cep.cod_GIT_Sentido, cep.cod_GIT_Pista, cep.cod_GIT_Faixa,        
     cep.faixa_exclusiva_direita,        
     cep.faixa_exclusiva_esquerda,        
     ce.flag_opcao,
  L.localidade_desc,
  cep.sentido--,      
  --a.data      
 --ORDER BY        
 --    l.id_local,        
 --    cep.id_pista   
