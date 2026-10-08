CREATE FUNCTION [muralha].[fcn_ObterOcorrencias]()    
RETURNS TABLE    
AS    
 RETURN    
 (    
  SELECT o.id,    
      tp.id AS id_tipo_alerta_ocorrencia,    
      RTRIM(tp.tipo) AS tipo_alerta_ocorrencia,    
      cvm.id AS id_cad_veiculo_monitorado,    
      cvm.privado AS cvm_privado,    
      cvm.supervisionado AS cvm_supervisionado,    
      s.id AS id_status,    
      RTRIM(s.descricao) AS status,    
      o.data,    
      cvm.placa,    
      md.id AS id_motivo_descarte,    
      RTRIM(md.descricao) AS motivo_descarte,    
      su.id_usuario,    
      RTRIM(su.usuario) AS usuario,    
      reg.id AS id_tipo_registro,    
      RTRIM(reg.descricao) AS tipo_registro,    
      a.id AS id_alerta,    
       a.assinado,    
      STUFF((SELECT '-' + RTRIM(RIGHT('0000'+CAST(vtr.id_local AS VARCHAR(4)), 4)) AS [text()]    
       FROM   muralha.alerta_veiculo av    
        INNER JOIN muralha.veiculo_tempo_real vtr    
         ON  vtr.id = av.id_veiculo_tempo_real    
       WHERE  av.id_alerta = a.id    
       FOR XML PATH('')    
      ), 1, 1, '' ) AS [equipamentos],    
      CASE WHEN cvm.id IS NOT NULL THEN 1 ELSE 0 END AS possui_cad_monitorado,    
      CASE WHEN cvm.id IS NULL THEN 0 WHEN cvm.data_fim IS NOT NULL AND cvm.data_fim <= CAST(GETDATE() AS DATE) AND cvm.data_inativacao IS NOT NULL THEN 0 ELSE 1 END AS cad_monitorado_ativo,
	  cvm.id_registro_fato
  --SELECT *    
  FROM   muralha.ocorrencia o    
      INNER JOIN muralha.alerta a    
     ON  a.id = o.id_alerta    
      INNER JOIN muralha.tipo_alerta_ocorrencia tp    
     ON  tp.id = o.id_tipo_alerta_ocorrencia    
      INNER JOIN muralha.status_ocorrencia s    
     ON  s.id = o.id_status_ocorrencia    
      INNER JOIN muralha.tipo_registro reg    
     ON  reg.id = '5511CEF5-C1A0-450B-99B3-6FCA8668D243'    
      LEFT JOIN muralha.cad_veiculo_monitorado cvm    
     ON  cvm.id = a.id_cad_veiculo_monitorado    
      LEFT JOIN muralha.motivo_descarte md    
     ON  md.id = a.id_motivo_descarte    
      LEFT JOIN sis_usuario su    
     ON  su.id_usuario = cvm.id_usuario    
  --WHERE  a.enviado_cliente = 1    
 )    
    
    