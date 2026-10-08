CREATE PROCEDURE muralha.spu_obter_alertas_blitz_pendentes
AS
BEGIN
    SELECT DISTINCT
        a.id,
        bd.id as id_blitz_digital,
        bd.nome_blitz,
        bd.titulo_notificacao,
        bd.descricao,
        cadv.placa,
        tao.tipo as tipo_alerta,
        l.nome as nome_local
    FROM muralha.alerta a
    INNER JOIN muralha.alerta_veiculo av ON a.id = av.id_alerta
    INNER JOIN muralha.veiculo_tempo_real vtr ON av.id_veiculo_tempo_real = vtr.id
    INNER JOIN muralha.cad_veiculo_monitorado cadv ON a.id_cad_veiculo_monitorado = cadv.id
    INNER JOIN muralha.tipo_alerta_ocorrencia tao ON a.id_tipo_alerta_ocorrencia = tao.id
    INNER JOIN muralha.blitz_local bl ON vtr.id_local = bl.id_local
    INNER JOIN muralha.blitz_digital bd ON bl.id_blitz_digital = bd.id
    INNER JOIN dbo.[local] l ON vtr.id_local = l.id_local
    LEFT JOIN muralha.blitz_tipo_alerta bta ON bd.id = bta.id_blitz_digital 
        AND a.id_tipo_alerta_ocorrencia = bta.id_tipo_alerta_ocorrencia
        AND bta.ativo = 1
    WHERE a.enviado_blitz_mobile is null
      AND bd.ativo = 1
      AND bd.data_inicio IS NOT NULL
      AND GETDATE() >= bd.data_inicio
      AND (bd.data_fim IS NULL OR GETDATE() <= bd.data_fim)
      AND a.data >= bd.data_inicio
      AND (bd.data_fim IS NULL OR a.data <= bd.data_fim)
      AND a.data >= bd.data_criacao
      AND a.data <= GETDATE()
      AND (bta.id IS NOT NULL OR NOT EXISTS (
          SELECT 1 FROM muralha.blitz_tipo_alerta bta2 
          WHERE bta2.id_blitz_digital = bd.id AND bta2.ativo = 1
      ))
    GROUP BY
        a.id,
        bd.id,
        bd.nome_blitz,
        bd.titulo_notificacao,
        bd.descricao,
        cadv.placa,
        tao.tipo,
        l.nome
    ORDER BY a.id, bd.id;
END