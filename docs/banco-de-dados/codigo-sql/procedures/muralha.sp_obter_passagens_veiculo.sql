CREATE PROCEDURE [muralha].[sp_obter_passagens_veiculo]
    @placa NVARCHAR(255),
    @data_inicio DATETIME,
    @data_final DATETIME
AS
BEGIN
    SET NOCOUNT ON;

    WITH PassagensFiltradas AS (
        SELECT
            am.id AS area_id,
            am.nome AS area_nome,
            eam.id_equipamento,
            vtr.placa,
            vtr.data
        FROM muralha.area_monitorada am
        JOIN muralha.equipamentos_area_monitorada eam ON eam.id_area_monitorada = am.id
        JOIN muralha.veiculo_tempo_real vtr ON vtr.id_local = eam.id_equipamento
        WHERE vtr.placa = @placa
            AND vtr.data >= @data_inicio
            AND vtr.data < @data_final
    ),
    TotalPassagens AS (
        SELECT COUNT(*) AS total_geral FROM PassagensFiltradas
    ),
    PassagensAreaEquip AS (
        SELECT
            area_id,
            area_nome,
            id_equipamento,
            COUNT(*) AS total_passagens_equip
        FROM PassagensFiltradas
        GROUP BY area_id, area_nome, id_equipamento
    ),
    PassagensArea AS (
        SELECT
            area_id,
            area_nome,
            SUM(total_passagens_equip) AS total_passagens_area
        FROM PassagensAreaEquip
        GROUP BY area_id, area_nome
    ),
    PassagensDatas AS (
        SELECT
            area_id,
            area_nome,
            placa,
            MIN(data) AS primeira_passagem,
            MAX(data) AS ultima_passagem
        FROM PassagensFiltradas
        GROUP BY area_id, area_nome, placa
    ),
    PassagensComDiferenca AS (
        SELECT
            area_id,
            DATEDIFF(MINUTE, LAG(data) OVER (PARTITION BY area_id ORDER BY data), data) AS diff_minutos
        FROM PassagensFiltradas
    ),
    MediasTempo AS (
        SELECT
            area_id,
            AVG(CAST(diff_minutos AS FLOAT)) AS media_tempo_entre_passagens_minutos
        FROM PassagensComDiferenca
        GROUP BY area_id
        HAVING AVG(CAST(diff_minutos AS FLOAT)) IS NOT NULL
    )
    SELECT
        pa.area_id AS area_id,
        pa.area_nome AS area_nome,
        pd.placa AS placa,
        pa.total_passagens_area AS total_passagens,
        CAST(pa.total_passagens_area * 100.0 / tp.total_geral AS DECIMAL(5,1)) AS percentual,
        pd.primeira_passagem AS primeira_passagem,
        pd.ultima_passagem AS ultima_passagem,
        ISNULL(mt.media_tempo_entre_passagens_minutos, 0) AS media_passagens_minutos
    FROM PassagensArea pa
    JOIN PassagensDatas pd ON pa.area_id = pd.area_id AND pa.area_nome = pd.area_nome
    CROSS JOIN TotalPassagens tp
    LEFT JOIN MediasTempo mt ON pa.area_id = mt.area_id
    ORDER BY pd.primeira_passagem;
END
