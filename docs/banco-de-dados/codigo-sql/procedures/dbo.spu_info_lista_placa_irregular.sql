  
CREATE  PROCEDURE [dbo].[spu_info_lista_placa_irregular]    
AS    
 SELECT     
  p.placa AS placa,     
  cs.descricao AS situacao,     
  p.descricao AS descricao,    
  NULL AS modelo,     
  NULL AS cor,     
  NULL AS categoria,     
  NULL AS especie    
 FROM dbo.cad_veiculo_monitorado p (nolock)   
 --JOIN muralha.tipo_alerta_ocorrencia t (NOLOCK) ON p.id_tipo_alerta_ocorrencia = t.id   
 LEFT JOIN cadastro_veiculo cv (nolock)     
  ON cv.placa = p.placa    
 LEFT JOIN situacao_placa_irregular ps (nolock)    
  ON ps.id_situacao_placa_irregular = p.id_situacao 
 INNER JOIN cad_situacao cs 
  ON cs.id_situacao = p.id_situacao
 WHERE (data_exclusao >= getdate()     
  OR data_exclusao IS NULL  )  
  AND p.placa IS NOT NULL  
 ORDER BY p.placa    
