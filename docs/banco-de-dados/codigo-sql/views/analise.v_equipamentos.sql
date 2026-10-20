
-- 3. Equipamentos ----------------------------------------------------------------
CREATE   VIEW analise.v_equipamentos AS
SELECT
    lv.id_local,
    lv.nome,
    lv.lat,
    lv.lon,
    lv.tipo,
    CASE WHEN EXISTS (
        SELECT 1 FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK)
        WHERE vtr.id_local = lv.id_local AND vtr.data >= DATEADD(HOUR, -24, SYSDATETIME())
    ) THEN 1 ELSE 0 END             AS ativo_24h,
    am.nome                         AS area_monitorada
FROM analise.v_local_vigente lv
LEFT JOIN muralha.equipamentos_area_monitorada eam
    ON eam.id_equipamento = lv.id_local
LEFT JOIN muralha.area_monitorada am
    ON am.id = eam.id_area_monitorada AND am.deletado = 0;
