
CREATE   FUNCTION [muralha].[fcn_ObterInfoBoletimOcorrenciaAlertaPorPlaca](@placa VARCHAR(7))
RETURNS TABLE
AS
RETURN
(
	--DECLARE @placa VARCHAR(7) = 'SEU7J11'
	SELECT BO.id AS boletimId,
        RF.id AS registroFatoId,
        CONVERT(VARCHAR(10), BO.data_criacao, 120) AS data,
        OT.tipo_desc AS tipoOcorrencia,
        BO.detalhamento AS descricao,
        BS.descricao AS situacaoAtual,
        OC.id_alerta AS alertaId
    FROM   muralha.ocorrencia OC
        INNER JOIN muralha.atendimento ATD 
            ON ATD.id_ocorrencia = OC.id
        INNER JOIN muralha.boletim BO 
            ON BO.id_registro_fato = ATD.id_registro_fato
        INNER JOIN muralha.boletim_situacao BS 
            ON BS.id = BO.id_situacao
        INNER JOIN muralha.registro_fato RF 
            ON RF.id = BO.id_registro_fato
        INNER JOIN muralha.registro_fato_tipo OT 
            ON RF.id_tipo = OT.id
    WHERE OC.id_alerta IN (SELECT alertaId FROM muralha.fcn_ObterInfoAlerta(@placa))
)
