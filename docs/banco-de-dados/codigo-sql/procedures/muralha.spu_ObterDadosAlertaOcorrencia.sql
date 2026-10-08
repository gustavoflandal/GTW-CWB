CREATE PROCEDURE [muralha].[spu_ObterDadosAlertaOcorrencia] @idAlerta UNIQUEIDENTIFIER      
AS      
 --DECLARE @idAlerta UNIQUEIDENTIFIER = '5D6B707B-E21C-4BF1-92BD-791BD0CAE5AF'      
 DECLARE @status_ocorrencia_finalizacao AS TABLE (id_status UNIQUEIDENTIFIER, descricao VARCHAR(50), id_tipo_ocorrencia UNIQUEIDENTIFIER)      
 DECLARE @ocorrencia_notificacao AS TABLE (id_ocorrencia UNIQUEIDENTIFIER)      
      
 DECLARE @alerta AS TABLE (id UNIQUEIDENTIFIER, id_tipo_alerta_ocorrencia UNIQUEIDENTIFIER, tipo_alerta_ocorrencia VARCHAR(40), id_cad_veiculo_monitorado UNIQUEIDENTIFIER, status_alerta VARCHAR(20), data DATETIME,      
        observacao VARCHAR(200), id_motivo_descarte UNIQUEIDENTIFIER, id_ponto_interesse UNIQUEIDENTIFIER, alerta_vinculado BIT, id_alerta_vinculado UNIQUEIDENTIFIER, id_usuario INT, com_semelhanca BIT,  
  com_semelhanca_erros INT, com_semelhanca_desc VARCHAR(130), assinado BIT)      
      
 DECLARE @veiculo_tempo_real AS TABLE (id_alerta UNIQUEIDENTIFIER, id_veiculo_tempo_real UNIQUEIDENTIFIER, placa VARCHAR(7), data DATETIME, id_local INT, serie_equipamento INT, equipamento VARCHAR(130),      
           id_pista TINYINT, faixa TINYINT, velocidade SMALLINT, posicao_lat DECIMAL(19,17), posicao_lon DECIMAL(19,17))      
      
 SET NOCOUNT ON;      
      
 INSERT INTO @status_ocorrencia_finalizacao      
 SELECT s.id_status,      
     s.descricao,      
     t.id_tipo_ocorrencia      
 FROM   muralha.v_status_ocorrencia_finalizacao s      
     JOIN muralha.tipo_ocorrencia_status t      
    ON  t.id_status_ocorrencia = s.id_status      
      
 INSERT INTO @ocorrencia_notificacao      
 --DECLARE @idAlerta UNIQUEIDENTIFIER = '61508652-BBAA-4105-A550-195E7CF9CC48'      
 SELECT id_ocorrencia      
 FROM   muralha.ocorrencia_notificacao      
 WHERE  id_ocorrencia IN (SELECT sub.id FROM muralha.ocorrencia sub WHERE sub.id_alerta = @idAlerta)      
 GROUP BY      
     id_ocorrencia      
      
 INSERT INTO @alerta      
 --DECLARE @idAlerta UNIQUEIDENTIFIER = '61508652-BBAA-4105-A550-195E7CF9CC48'      
 SELECT a.id,      
     a.id_tipo_alerta_ocorrencia,      
     tao.tipo AS tipo_alerta_ocorrencia,      
     a.id_cad_veiculo_monitorado,      
     sa.descricao AS status_alerta,      
     a.data,      
     a.observacao,      
     a.id_motivo_descarte,      
     a.id_ponto_interesse,      
     a.alerta_vinculado,      
     a.id_alerta_vinculado,      
     a.id_usuario,  
  a.com_semelhanca,  
  a.com_semelhanca_erros,  
  a.com_semelhanca_desc,
  a.assinado
 FROM   muralha.alerta a (NOLOCK)       
     INNER JOIN muralha.tipo_alerta_ocorrencia tao (NOLOCK)       
      ON  tao.id = a.id_tipo_alerta_ocorrencia       
     INNER JOIN muralha.status_alerta sa (NOLOCK)       
      ON  sa.id = a.id_status_alerta      
 WHERE  a.id = @idAlerta      
      
 INSERT INTO @veiculo_tempo_real      
 --DECLARE @idAlerta UNIQUEIDENTIFIER = '61508652-BBAA-4105-A550-195E7CF9CC48'      
 SELECT av.id_alerta,      
     av.id_veiculo_tempo_real,      
     vtr.placa,      
     vtr.data,      
     lv.id_local,      
     lv.serie_equipamento,      
     CASE WHEN lv.codigo_equipamento IS NOT NULL AND lv.codigo_equipamento != '' THEN RTRIM(lv.codigo_equipamento) + ' - ' ELSE '' END + RTRIM(CAST(lv.serie_equipamento AS VARCHAR(20))) + ' - ' + RTRIM(lv.nome) AS equipamento,      
     vtr.id_pista,      
     lv.cod_pista_alternativo AS faixa,      
     vtr.velocidade,      
     lv.posicao_lat,      
     lv.posicao_lon      
 --DECLARE @idAlerta UNIQUEIDENTIFIER = '61508652-BBAA-4105-A550-195E7CF9CC48' SELECT *      
 FROM   muralha.alerta_veiculo av       
     INNER JOIN muralha.veiculo_tempo_real vtr       
      ON  vtr.id = av.id_veiculo_tempo_real       
     INNER JOIN local_pista_vigente lv       
      ON  lv.id_local = vtr.id_local       
     AND lv.id_pista = vtr.id_pista      
 WHERE  av.id_alerta = @idAlerta      
      
 SET NOCOUNT OFF;      
        
 SELECT a.id,      
     a.id_tipo_alerta_ocorrencia,      
     a.tipo_alerta_ocorrencia,      
     a.id_cad_veiculo_monitorado,      
     cvm.placa AS placa_cadastro,      
     a.status_alerta,      
     a.data AS data_alerta,      
     vtr.id_veiculo_tempo_real,      
     vtr.placa AS placa_veiculo,      
     vtr.data AS data_veiculo,      
     vtr.id_local,      
     vtr.serie_equipamento,      
     vtr.equipamento,      
     vtr.id_pista,      
     vtr.faixa,      
   vtr.velocidade,      
     vtr.posicao_lat AS latitude,      
     vtr.posicao_lon AS longitude,      
     md.id AS id_motivo_descarte,      
     md.descricao AS motivo_descarte,      
     a.observacao,      
     CASE WHEN a.id_motivo_descarte IS NOT NULL THEN 1 ELSE 0 END AS descartado,      
     o.id AS id_ocorrencia,      
     CASE WHEN o.id IS NOT NULL THEN 1 ELSE 0 END AS ocorrencia_gerada,      
     CASE WHEN ocn.id_ocorrencia IS NOT NULL THEN 1 ELSE 0 END AS ocorrencia_com_notificacao,      
     CASE WHEN o.id IS NOT NULL THEN '5511CEF5-C1A0-450B-99B3-6FCA8668D243' ELSE 'E7D115B9-E6B3-4E86-9083-F347A1917045' END AS id_tipo_registro,      
     RTRIM(CASE WHEN o.id IS NOT NULL THEN 'IRREGULARIDADE' ELSE 'ALERTA' END) AS tipo_registro,      
     o.id_status_ocorrencia,      
     os.descricao AS status_ocorrencia,      
     CASE WHEN sfo.id_status IS NOT NULL THEN 1 ELSE 0 END AS ocorrencia_finalizada,      
     o.observacao AS obs_finalizar_ocorrencia,      
     a.id_ponto_interesse,      
     pt.nome AS nome_ponto_interesse,      
     a.alerta_vinculado,      
     a.id_alerta_vinculado,      
     o.permite_atendimento,      
     CASE WHEN atend.id IS NOT NULL THEN 0 ELSE 1 END AS permite_alterar_atendimento,      
     atend.id AS id_atendimento,  
     cvm.supervisionado,  
     a.assinado,
   a.com_semelhanca,  
  a.com_semelhanca_erros,  
  a.com_semelhanca_desc,
  (
	select * from [muralha].[fn_ObterSom](a.id)
  ) as som
 FROM   @alerta a      
     INNER JOIN @veiculo_tempo_real vtr      
      ON  vtr.id_alerta = a.id       
     LEFT JOIN muralha.cad_veiculo_monitorado cvm (NOLOCK)       
      ON  cvm.id = a.id_cad_veiculo_monitorado       
     LEFT JOIN muralha.motivo_descarte md       
    ON  md.id = a.id_motivo_descarte       
     LEFT JOIN sis_usuario su       
      ON  su.id_usuario = a.id_usuario       
     LEFT JOIN muralha.ocorrencia o       
    ON  o.id_alerta = a.id       
     LEFT JOIN muralha.status_ocorrencia os      
    ON  os.id = o.id_status_ocorrencia      
     LEFT JOIN @status_ocorrencia_finalizacao sfo      
    ON  sfo.id_tipo_ocorrencia = o.id_tipo_alerta_ocorrencia      
     AND sfo.id_status = o.id_status_ocorrencia      
     LEFT JOIN @ocorrencia_notificacao ocn      
    ON  ocn.id_ocorrencia = o.id      
     LEFT JOIN muralha.ponto_interesse pt      
    ON  pt.id = a.id_ponto_interesse      
     LEFT JOIN muralha.atendimento atend      
    ON  atend.id_ocorrencia = o.id      
 WHERE  a.id = @idAlerta      
 ORDER BY      
     vtr.data