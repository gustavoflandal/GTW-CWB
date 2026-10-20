
-- 3. Disponibilidade diária por equipamento e local --------------------------------
-- Agrega primeiro por dia/equipamento/versão (4 milhões de linhas) e só depois resolve o nome;
-- num dia com duas versões do mesmo local, vale o maior número de horas.
CREATE   VIEW analise.v_disponibilidade_dia AS
WITH horas_versao AS (
    SELECT s.data AS dia, s.id_local, s.sequencia_local, COUNT(DISTINCT s.hora) AS horas_captura
    FROM dbo.veiculo_sumarizado s
    GROUP BY s.data, s.id_local, s.sequencia_local
),
horas AS (
    SELECT
        hv.dia,
        hv.id_local,
        ISNULL(lv.nome, 'Equipamento ' + CAST(hv.id_local AS varchar(10))) AS local_nome,
        MAX(hv.horas_captura) AS horas_captura
    FROM horas_versao hv
    LEFT JOIN analise.v_local_versao lv
        ON lv.id_local = hv.id_local AND lv.sequencia_local = hv.sequencia_local
    GROUP BY hv.dia, hv.id_local, ISNULL(lv.nome, 'Equipamento ' + CAST(hv.id_local AS varchar(10)))
),
dias_sistema AS (
    SELECT DISTINCT dia FROM horas
),
faixa AS (
    SELECT id_local, local_nome, MIN(dia) AS d0, MAX(dia) AS d1
    FROM horas GROUP BY id_local, local_nome
)
SELECT
    d.dia,
    f.id_local,
    f.local_nome,
    24                                                            AS verificacoes,
    ISNULL(h.horas_captura, 0)                                    AS verificacoes_ok,
    CAST(100.0 * ISNULL(h.horas_captura, 0) / 24 AS DECIMAL(5,2)) AS pct_disponibilidade,
    (24 - ISNULL(h.horas_captura, 0)) * 60                        AS max_minutos_offline
FROM faixa f
JOIN dias_sistema d ON d.dia BETWEEN f.d0 AND f.d1
LEFT JOIN horas h ON h.id_local = f.id_local AND h.local_nome = f.local_nome AND h.dia = d.dia;
