
CREATE   FUNCTION [muralha].[fcn_ObterInfoRegistroFatoAlertaPorPlaca](@placa VARCHAR(7))
RETURNS TABLE
AS
RETURN
(
	--DECLARE @placa VARCHAR(7) = 'SEU7J11'
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
        RFS.descricao AS statusRegistroFato,
        OC.id_alerta AS alertaId
    FROM  muralha.registro_fato RF
        INNER JOIN muralha.atendimento ATD 
            ON RF.id = ATD.id_registro_fato
        INNER JOIN muralha.ocorrencia OC
            ON ATD.id_ocorrencia = OC.id
        INNER JOIN muralha.registro_fato_tipo OT 
            ON RF.id_tipo = OT.id
        INNER JOIN muralha.registro_fato_status RFS
            ON RFS.id = RF.id_status
        LEFT JOIN muralha.boletim BO 
            ON BO.id_registro_fato = RF.id
        LEFT JOIN muralha.boletim_situacao BS 
            ON BS.id = BO.id_situacao
    WHERE OC.id_alerta IN (SELECT alertaId FROM muralha.fcn_ObterInfoAlerta(@placa))
)
