
CREATE VIEW muralha.v_dashboard_alertas_resumo AS
SELECT
    CAST(a.data AS DATE)      AS data,
    tao.tipo                  AS tipo_alerta,
    sa.descricao              AS status_alerta,
    COUNT(*)                  AS total_alertas
FROM muralha.alerta a (NOLOCK)
INNER JOIN muralha.tipo_alerta_ocorrencia tao (NOLOCK)
    ON tao.id = a.id_tipo_alerta_ocorrencia
INNER JOIN muralha.status_alerta sa (NOLOCK)
    ON sa.id = a.id_status_alerta
GROUP BY CAST(a.data AS DATE), tao.tipo, sa.descricao
