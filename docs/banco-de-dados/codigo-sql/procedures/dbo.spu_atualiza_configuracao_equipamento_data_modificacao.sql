CREATE PROCEDURE [dbo].[spu_atualiza_configuracao_equipamento_data_modificacao]
AS

DELETE FROM configuracao_equipamento_data_modificacao

INSERT INTO configuracao_equipamento_data_modificacao
SELECT l.id_local, l.sequencia_local, CAST(CONVERT(CHAR(19),ce.data_modificacao,120) AS DATETIME) AS data_modificacao
FROM configuracao_equipamento ce (NOLOCK)
    JOIN [local] l (NOLOCK)
        ON l.id_configuracao_equipamento = ce.id_configuracao_equipamento

