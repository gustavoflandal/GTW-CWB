
CREATE VIEW muralha.v_dashboard_passagens_diarias AS
SELECT
    CAST(vtr.data AS DATE)   AS data,
    vtr.id_local,
    RTRIM(lpv.nome)          AS equipamento,
    lpv.posicao_lat          AS latitude,
    lpv.posicao_lon          AS longitude,
    COUNT(*)                 AS total_passagens
FROM muralha.veiculo_tempo_real vtr (NOLOCK)
INNER JOIN (
    SELECT id_local, MIN(id_pista) AS id_pista
    FROM local_pista_vigente (NOLOCK)
    GROUP BY id_local
) pista_ref ON pista_ref.id_local = vtr.id_local
INNER JOIN local_pista_vigente lpv (NOLOCK)
    ON lpv.id_local = pista_ref.id_local
    AND lpv.id_pista = pista_ref.id_pista
GROUP BY CAST(vtr.data AS DATE), vtr.id_local, RTRIM(lpv.nome), lpv.posicao_lat, lpv.posicao_lon
