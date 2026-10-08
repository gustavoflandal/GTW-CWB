CREATE FUNCTION [muralha].[fcn_ObterAlertas]()      
RETURNS TABLE      
AS      
 RETURN      
 (      
  SELECT a.id,      
      tp.id AS id_tipo_alerta_ocorrencia,      
      RTRIM(tp.tipo) AS tipo_alerta_ocorrencia,      
      cvm.id AS id_cad_veiculo_monitorado,      
      cvm.privado AS cvm_privado,      
      cvm.supervisionado AS cvm_supervisionado,      
      s.id AS id_status,      
      RTRIM(s.descricao) AS status,      
      a.data,      
      cvm.placa,      
      md.id AS id_motivo_descarte,      
      RTRIM(md.descricao) AS motivo_descarte,      
      su.id_usuario,
	  a.id_usuario AS id_usuario_alerta,
      RTRIM(su.usuario) AS usuario,      
      reg.id AS id_tipo_registro,      
      RTRIM(reg.descricao) AS tipo_registro,      
      a.id AS id_alerta,      
      a.lembrete_visualizado,      
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
      --vtr.id_local      
  FROM   muralha.alerta a      
      INNER JOIN muralha.tipo_alerta_ocorrencia tp      
     ON  tp.id = a.id_tipo_alerta_ocorrencia      
      INNER JOIN muralha.status_alerta s      
     ON  s.id = a.id_status_alerta      
      INNER JOIN muralha.tipo_registro reg      
     ON  reg.id = 'E7D115B9-E6B3-4E86-9083-F347A1917045'      
      LEFT JOIN muralha.cad_veiculo_monitorado cvm      
     ON  cvm.id = a.id_cad_veiculo_monitorado      
      LEFT JOIN muralha.motivo_descarte md      
     ON  md.id = a.id_motivo_descarte      
      LEFT JOIN sis_usuario su      
     ON  su.id_usuario = cvm.id_usuario      
  --WHERE  a.enviado_cliente = 1      
  --WHERE  a.id = 'DB33F263-4A45-4871-AF13-006E0FE66E8A'      
 ) 