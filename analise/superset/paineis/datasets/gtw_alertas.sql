-- Alertas de veículos de interesse gerados pela Muralha Digital
-- Fonte: analise.v_alertas (muralha.alerta)
SELECT
    a.dt_alerta,
    RIGHT('0' + CAST(DATEPART(HOUR, a.dt_alerta) AS varchar(2)), 2) + 'h' AS hora_dia,
    CASE DATEDIFF(DAY, '19000107', CAST(a.dt_alerta AS date)) % 7
        WHEN 0 THEN N'1-Dom' WHEN 1 THEN N'2-Seg' WHEN 2 THEN N'3-Ter' WHEN 3 THEN N'4-Qua'
        WHEN 4 THEN N'5-Qui' WHEN 5 THEN N'6-Sex' ELSE N'7-Sáb' END AS dia_semana,
    a.tipo AS tipo_alerta,
    CASE WHEN a.critico = 1 THEN N'Crítico' ELSE N'Não crítico' END AS criticidade,
    ISNULL(a.status, N'Sem status') AS status,
    ISNULL(a.origem, N'Não informada') AS origem,
    a.descartado,
    a.tratado,
    ISNULL(NULLIF(RTRIM(a.usuario_responsavel), ''), N'Não atribuído') AS responsavel,
    ISNULL(a.motivo_descarte, N'-') AS motivo_descarte
FROM analise.v_alertas a
