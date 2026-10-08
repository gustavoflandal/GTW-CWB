CREATE PROCEDURE [muralha].[spu_ObterNovosAlertas] @lembrete_visualizado BIT        
AS      
--UPDATE muralha.alerta set enviado_cliente = 0, data_enviado = NULL --WHERE id = '5D6B707B-E21C-4BF1-92BD-791BD0CAE5AF'      
--DECLARE @lembrete_visualizado BIT = 0        
 DECLARE @temp_alerta AS TABLE        
 (        
  id UNIQUEIDENTIFIER,        
  id_tipo_alerta_ocorrencia UNIQUEIDENTIFIER,        
  tipo_alerta_ocorrencia VARCHAR(40),        
  id_cad_veiculo_monitorado UNIQUEIDENTIFIER,        
  placa CHAR(7),        
  veiculo_monitorado_datacad DATETIME,        
  id_status_alerta UNIQUEIDENTIFIER,        
  status_alerta_desc VARCHAR(20),        
  data_alerta DATETIME,        
  enviado_cliente BIT,        
  data_enviado DATETIME,        
  equipamento VARCHAR(120),      
  supervisionado BIT,      
  com_semelhanca BIT,      
  com_semelhanca_erros INT,      
  com_semelhanca_desc VARCHAR(130),    
  som VARCHAR(100),  
  id_usuario_responsavel INT,
  requer_e_possui VARCHAR(10),
  data_passagem DATETIME
 )        
        
 SET NOCOUNT ON;        
        
 INSERT INTO @temp_alerta        
 SELECT alert.id,        
     alert.id_tipo_alerta_ocorrencia,        
     tpa.tipo AS 'tipo_alerta_ocorrencia',        
     alert.id_cad_veiculo_monitorado,        
     cadv.placa,        
     cadv.data_cadastro AS 'veiculo_monitorado_datacad',        
     alert.id_status_alerta,        
     sa.descricao AS 'status_alerta_desc',        
     alert.data AS 'data_alerta',        
     alert.enviado_cliente,        
     alert.data_enviado,        
     (RTRIM(CAST(lv.serie_equipamento AS VARCHAR(10))) + ' - ' + RTRIM(lv.nome)) AS 'equipamento',      
  cadv.supervisionado,      
  alert.com_semelhanca,      
  alert.com_semelhanca_erros,      
  alert.com_semelhanca_desc,    
  (    
 select * from [muralha].[fn_ObterSom](alert.id)    
  ) as som,  
  cadv.id_usuario_responsavel,
  (
  SELECT * FROM [muralha].[fn_RegistroFatoRequerEPossuiBO](cadv.id_registro_fato)
  ) AS requer_e_possui,
  vtr.data AS 'data_passagem'
 FROM   muralha.alerta alert(NOLOCK)        
     INNER JOIN muralha.tipo_alerta_ocorrencia tpa(NOLOCK)        
    ON  tpa.id = alert.id_tipo_alerta_ocorrencia        
     INNER JOIN muralha.cad_veiculo_monitorado cadv(NOLOCK)        
    ON  cadv.id = alert.id_cad_veiculo_monitorado        
     INNER JOIN muralha.status_alerta sa(NOLOCK)        
    ON  sa.id = alert.id_status_alerta         
     INNER JOIN muralha.alerta_veiculo alv        
    ON  alv.id_alerta = alert.id        
     AND alv.id_veiculo_tempo_real = (        
        SELECT TOP 1 id_veiculo_tempo_real        
        FROM muralha.alerta_veiculo        
        WHERE id_alerta = alert.id        
     )        
     INNER JOIN muralha.veiculo_tempo_real vtr        
    ON  vtr.id = alv.id_veiculo_tempo_real        
     INNER JOIN local_vigente lv        
    ON  lv.id_local = vtr.id_local        
 WHERE  alert.enviado_cliente = 0        
         
 UPDATE muralha.alerta        
 SET    enviado_cliente = 1,        
     data_enviado = GETDATE(),        
     lembrete_visualizado = @lembrete_visualizado        
 WHERE  id IN (SELECT id FROM @temp_alerta);        
        
 SET NOCOUNT OFF;        
        
 SELECT id,        
     id_tipo_alerta_ocorrencia,        
     tipo_alerta_ocorrencia,        
     id_cad_veiculo_monitorado,        
     placa,        
     veiculo_monitorado_datacad,        
     id_status_alerta,        
     status_alerta_desc,        
     data_alerta,        
     enviado_cliente,        
     data_enviado,        
     equipamento,      
  supervisionado,      
  com_semelhanca,      
  com_semelhanca_erros,      
  com_semelhanca_desc,    
  som,  
  id_usuario_responsavel,
  requer_e_possui,
  data_passagem
 FROM   @temp_alerta        
 ORDER BY        
     data_alerta;