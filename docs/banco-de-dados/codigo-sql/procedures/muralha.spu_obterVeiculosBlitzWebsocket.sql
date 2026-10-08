CREATE PROCEDURE muralha.spu_obterVeiculosBlitzWebsocket
    @data_referencia DATETIME
AS
BEGIN
    SET NOCOUNT ON;

    CREATE TABLE #blitzes_ativas (
        id_local INT NOT NULL,
        id_blitz_digital INT NOT NULL,
        PRIMARY KEY (id_local, id_blitz_digital)
    );

    INSERT INTO #blitzes_ativas (id_local, id_blitz_digital)
    SELECT DISTINCT 
        bl.id_local,
        bl.id_blitz_digital
    FROM muralha.blitz_local bl
    INNER JOIN muralha.blitz_digital bd
        ON bd.id = bl.id_blitz_digital
    WHERE bd.ativo = 1
      AND bd.data_inicio <= @data_referencia
      AND (bd.data_fim IS NULL OR bd.data_fim >= @data_referencia);

    SELECT
        vtr.id AS idVeiculoTempoReal,
        vtr.placa,
        vtr.data,
        vtr.id_local,
        vtr.id_pista,
        vtr.velocidade,
        vtr.classificacao,

        -- dados do alerta (podem ser NULL)
        a.id AS id_alerta,
        tao.tipo AS tipo_alerta,
        a.observacao AS observacao_alerta,

        STUFF((
            SELECT ',' + CAST(ba2.id_blitz_digital AS VARCHAR(10))
            FROM #blitzes_ativas ba2
            WHERE ba2.id_local = vtr.id_local
            FOR XML PATH(''), TYPE
        ).value('.', 'VARCHAR(MAX)'), 1, 1, '') AS blitzes

    FROM muralha.veiculo_tempo_real vtr

    LEFT JOIN muralha.alerta_veiculo av
        ON av.id_veiculo_tempo_real = vtr.id

    LEFT JOIN muralha.alerta a
        ON a.id = av.id_alerta

    LEFT JOIN muralha.tipo_alerta_ocorrencia tao
        ON tao.id = a.id_tipo_alerta_ocorrencia

    WHERE vtr.data > @data_referencia
      AND EXISTS (
            SELECT 1
            FROM #blitzes_ativas ba
            WHERE ba.id_local = vtr.id_local
        )

    ORDER BY vtr.data;

    DROP TABLE #blitzes_ativas;
END