CREATE PROCEDURE [dbo].[spu_muralha_info_mobile_boletim]  
 @id_atendimento int,
 @id_atendimento_guarnicao int
AS  
  
 SELECT  
  a.id as id_atendimento,  
  ag.observacao as atendimento_observacao,  
  a.id_boletim,  
  b.detalhamento as boletim_detalhamento,  
  b.data_criacao,  
  bl.id_cidade,  
  bl.bairro,  
  bl.rua,  
  bl.numero,  
  bl.complemento  
 FROM muralha.atendimento a  
 INNER JOIN muralha.atendimento_guarnicao ag  
  ON ag.id_atendimento = a.id  
 INNER JOIN muralha.boletim b  
  ON b.id = a.id_boletim  
 INNER JOIN muralha.boletim_local bl  
  ON bl.id = b.id_local  
 WHERE a.id = @id_atendimento  AND ag.id = @id_atendimento_guarnicao 
  