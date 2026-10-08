CREATE VIEW muralha.vw_registro_fato_origem AS
SELECT 
    rf.id AS id_registro_fato,
    f.id,
    h.data_alteracao AS data_ultimo_historico,
    CASE WHEN f.id IS NOT NULL THEN 1 ELSE 2 END AS origem,
    CASE WHEN f.id IS NOT NULL THEN 'ORIGEM_BOLETIM_ATENDIMENTO'
         ELSE 'ORIGEM_BOLETIM_DIRETO' END AS origem_descricao
FROM muralha.registro_fato rf
LEFT JOIN muralha.fato f
    ON f.id_registro_fato = rf.id
OUTER APPLY (
    SELECT TOP 1 rfh.data_alteracao
    FROM muralha.registro_fato_historico rfh
    WHERE rfh.id_registro = rf.id
    ORDER BY rfh.data_alteracao DESC
) h;