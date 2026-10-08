

CREATE   FUNCTION [muralha].[fcn_ObterInfoBoletimOcorrencia](@placa VARCHAR(7))
RETURNS TABLE
AS
RETURN
(
    --DECLARE @placa VARCHAR(7) = 'OWQ3465'
    SELECT BO.id AS boletimId,
        RF.id AS registroFatoId,
        CONVERT(VARCHAR(10), BO.data_criacao, 120) AS data,
        OT.tipo_desc AS tipoOcorrencia,
        BO.detalhamento AS descricao,
        BS.descricao AS situacaoAtual
    FROM   muralha.boletim BO
        INNER JOIN muralha.registro_fato RF
            ON  RF.id = BO.id_registro_fato
        INNER JOIN muralha.registro_fato_veiculo BV
            ON  BV.id_registro_fato = RF.id
        INNER JOIN muralha.boletim_situacao BS
            ON  BS.id = BO.id_situacao
        INNER JOIN muralha.registro_fato_tipo OT
            ON  OT.id = RF.id_tipo
    WHERE  BV.placa = @placa
)
