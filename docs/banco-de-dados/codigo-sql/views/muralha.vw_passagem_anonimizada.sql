
    CREATE VIEW muralha.vw_passagem_anonimizada AS
    SELECT
        vtr.id,
        CASE
            WHEN vtr.placa IS NULL THEN NULL
            WHEN LEN(vtr.placa) >= 4
                THEN LEFT(vtr.placa, LEN(vtr.placa) - 3) + '***'
            ELSE '***'
        END AS placa,
        vtr.data,
        vtr.data_importado,
        vtr.id_local,
        vtr.id_pista,
        COALESCE(vsa.status_analise, 'AGUARDANDO_ANALISE') AS status_analise
    FROM muralha.veiculo_tempo_real vtr
    LEFT JOIN muralha.vtr_status_analise vsa
        ON vsa.id_veiculo_tempo_real = vtr.id
    