
CREATE   FUNCTION [muralha].[fcn_ObterInfoRegistroFatoPorPlaca](@placa VARCHAR(7))
RETURNS TABLE
AS
RETURN
(
	--DECLARE @placa VARCHAR(7) = 'ANS3537'
    SELECT RF.id AS registroFatoId,
        RF.tem_boletim AS temBoletim,
        BO.id AS boletimId,
        CONVERT(VARCHAR(10), RF.data_criacao, 120) AS data,
        OT.tipo_desc AS tipoOcorrencia,
        CASE 
            WHEN BO.id IS NOT NULL THEN BO.detalhamento 
            ELSE 'Sem boletim associado'
        END AS descricao,
        CASE 
            WHEN BO.id IS NOT NULL THEN BS.descricao 
            ELSE 'N/A'
        END AS situacaoAtual,
        RFS.descricao AS statusRegistroFato
    FROM   muralha.registro_fato RF
        INNER JOIN muralha.registro_fato_veiculo RFV
            ON  RFV.id_registro_fato = RF.id
        INNER JOIN muralha.registro_fato_tipo OT
            ON  OT.id = RF.id_tipo
        INNER JOIN muralha.registro_fato_status RFS
            ON  RFS.id = RF.id_status
        LEFT JOIN muralha.boletim BO
            ON  BO.id_registro_fato = RF.id
        LEFT JOIN muralha.boletim_situacao BS
            ON  BS.id = BO.id_situacao
    WHERE  RFV.placa = @placa
)
