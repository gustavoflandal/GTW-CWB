
CREATE VIEW [dbo].[v_locais_pmesp]   
AS  
  
  SELECT lv.id_local, cep.nome_pista, lv.serie_equipamento, cep.id_pista, cep.cod_pista_alternativo, cep.cod_pista_tarja, cep.entre_faixa,  
  CASE WHEN cep.entre_faixa = 1 THEN RTRIM(LTRIM(STR(cep.cod_pista_tarja) + 'E')) ELSE RTRIM(LTRIM(STR(cep.cod_pista_tarja))) END AS cod_pista_str,  
  cep.cod_pista, cep.cod_pista_prodam   
  , CASE WHEN cepa.parametros_adicionais LIKE '%ENVIAR_VEICULOS_PM=1%' THEN 1 ELSE 0 END AS ENVIAR_VEICULOS_PM  
  FROM local_vigente lv (NOLOCK)   
  JOIN configuracao_equipamento_pista cep (NOLOCK) ON lv.id_configuracao_equipamento = cep.id_configuracao_equipamento  
  JOIN configuracao_equipamento_parametros_adicionais cepa (NOLOCK) ON lv.id_configuracao_equipamento = cepa.id_configuracao_equipamento  
  WHERE cepa.parametros_adicionais LIKE '%ENVIAR_VEICULOS_PM=1%'  

