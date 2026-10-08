CREATE PROCEDURE [muralha].[spu_ObterNovosAlertasTeste] @lembrete_visualizado BIT      
AS      
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
  supervisionado VARCHAR(50),    
  Semelhante VARCHAR(50),    
  diferencas VARCHAR(20),    
  caracteres_diferentes    VARCHAR(150)    
      
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
  CASE     
  WHEN cadv.supervisionado IS NULL THEN -1   
  WHEN cadv.supervisionado = 1 THEN 1    
  ELSE 0    
 END AS supervisionado,    
 CASE WHEN cadv.placa <> vtr.placa THEN 1 ELSE 0 END AS 'utilizou_semelhanca_placa',    
 fp.diferencas,    
    fp. caracteres_diferentes      
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
  INNER JOIN muralha.config_alarme_tipo cat    
 ON cat.id_tipo_alerta = tpa.id  AND cat.habilitado = 1    
  INNER JOIN muralha.config_alarme ca    
 ON ca.id_tipo = cat.id    
 CROSS APPLY muralha.fn_CompararPlacas(cadv.placa, vtr.placa) fp    
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
  supervisionado ,    
  Semelhante,    
  Diferencas ,    
  caracteres_diferentes     
 FROM   @temp_alerta      
 ORDER BY      
     data_alerta