
CREATE VIEW analise.v_alertas AS
SELECT
    a.data                          AS dt_alerta,
    tao.tipo                        AS tipo,
    tao.descricao                   AS tipo_descricao,
    ISNULL(tac.critico, 0)          AS critico,
    COALESCE(tao.prioridade, 50)    AS prioridade,
    sa.descricao                    AS status,
    a.origem,
    CASE WHEN a.id_motivo_descarte IS NOT NULL THEN 1 ELSE 0 END AS descartado,
    md.descricao                    AS motivo_descarte,
    a.data_descarte,
    CASE WHEN a.id_usuario IS NOT NULL THEN 1 ELSE 0 END AS tratado,
    u.usuario                       AS usuario_responsavel,
    a.observacao
FROM muralha.alerta a
JOIN muralha.tipo_alerta_ocorrencia tao
    ON tao.id = a.id_tipo_alerta_ocorrencia
LEFT JOIN muralha.status_alerta sa
    ON sa.id = a.id_status_alerta
LEFT JOIN muralha.motivo_descarte md
    ON md.id = a.id_motivo_descarte
LEFT JOIN muralha.tipo_alerta_config tac
    ON tac.id_tipo_alerta_ocorrencia = tao.id
LEFT JOIN dbo.sis_usuario u
    ON u.id_usuario = a.id_usuario;
