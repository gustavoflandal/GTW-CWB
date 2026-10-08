CREATE FUNCTION [muralha].[fcn_ObterInfoAlerta](@placa VARCHAR(7))
RETURNS TABLE
AS
RETURN
(
	--DECLARE @placa VARCHAR(7) = 'RMP0E18'
    SELECT MAL.data AS data,
		   MAL.observacao,
		   MAL.origem,
		   TAO.tipo,
		   MSA.descricao AS status,
		   MAL.id AS alertaId
    FROM   muralha.cad_veiculo_monitorado CVM
		   INNER JOIN muralha.alerta MAL
				ON  CVM.id = MAL.id_cad_veiculo_monitorado
		   INNER JOIN muralha.status_alerta MSA
				ON  MAL.id_status_alerta = MSA.id
		   INNER JOIN muralha.tipo_alerta_ocorrencia TAO
				ON  TAO.id = MAL.id_tipo_alerta_ocorrencia
    WHERE CVM.placa = @placa
)