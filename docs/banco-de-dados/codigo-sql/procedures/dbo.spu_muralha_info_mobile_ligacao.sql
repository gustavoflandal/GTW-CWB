CREATE PROCEDURE [dbo].[spu_muralha_info_mobile_ligacao]      
 @id_atendimento int,  
 @id_atendimento_guarnicao int  
AS      
      
 SELECT      
  a.id as id_atendimento,      
  a.id_ocorrencia as id_ocorrencia_ligacao,     
  ag.observacao,    
  f.data_hora_evento,      
  rfit.descricao as tipo_solicitante,      
  rfi.nome AS nome_solicitante,      
  rfi.cpf AS cpf_solicitante,      
  rf.id_tipo AS id_tipo_ocorrencia,      
  rft.tipo_desc as tipo_ocorrencia,      
  c.nome as cidade,      
  rfe.bairro,      
  rfe.rua,      
  rfe.numero,      
  rfe.complemento,      
 CASE 
    WHEN rfit.id = 2 THEN rfi.nome 
    ELSE 'Vítima não informada' 
END AS nome_vitima,     
  f.detalhamento,      
  f.existe_arma_envolvida      
 FROM muralha.atendimento a      
 INNER JOIN muralha.registro_fato rf      
  ON rf.id = a.id_registro_fato
INNER JOIN muralha.fato f 
  ON f.id_registro_fato = rf.id 
INNER JOIN muralha.registro_fato_individuo rfi
 ON rfi.id_registro_fato = rf.id
INNER JOIN muralha.registro_fato_individuo_tipo rfit
 on rfit.id = rfi.id_tipo_envolvimento
 INNER JOIN muralha.atendimento_guarnicao ag    
 ON ag.id_atendimento = a.id
INNER JOIN muralha.registro_fato_tipo rft
 ON rft.id = rf.id_tipo
INNER JOIN muralha.registro_fato_endereco rfe
on rfe.id_registro_fato = rf.id     
 INNER JOIN muralha.cidade c      
  ON c.id = rfe.id_cidade      
 WHERE a.id = @id_atendimento AND ag.id = @id_atendimento_guarnicao 


 