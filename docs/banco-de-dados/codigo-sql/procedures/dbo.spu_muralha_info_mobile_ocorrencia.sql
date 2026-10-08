CREATE PROCEDURE [dbo].[spu_muralha_info_mobile_ocorrencia]    
 @id_atendimento int,  
 @id_atendimento_guarnicao int  
AS    
    
 SELECT    
  TOP(1)    
  a.id as id_atendimento,    
  a.id_ocorrencia,    
  o.data,    
  tao.tipo,    
  vtri.id_veiculo_tempo_real,    
  vtri.imagem,    
  ag.observacao    
 FROM muralha.atendimento a    
 INNER JOIN muralha.atendimento_guarnicao ag    
  ON ag.id_atendimento = a.id    
 INNER JOIN muralha.ocorrencia o    
  ON o.id = a.id_ocorrencia    
 INNER JOIN muralha.tipo_alerta_ocorrencia as tao    
  ON tao.id = o.id_tipo_alerta_ocorrencia    
 INNER JOIN muralha.alerta al    
  ON al.id = o.id_alerta    
 INNER JOIN muralha.alerta_veiculo av    
  ON av.id_alerta = al.id    
 INNER JOIN muralha.veiculo_tempo_real vtr    
  ON vtr.id = av.id_veiculo_tempo_real    
 INNER JOIN muralha.veiculo_tempo_real_imagem vtri     
  ON vtri.id_veiculo_tempo_real = vtr.id    
 WHERE a.id = @id_atendimento  AND ag.id = @id_atendimento_guarnicao   
    