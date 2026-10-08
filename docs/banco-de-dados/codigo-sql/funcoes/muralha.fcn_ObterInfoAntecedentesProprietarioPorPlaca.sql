CREATE FUNCTION [muralha].[fcn_ObterInfoAntecedentesProprietarioPorPlaca](@placa VARCHAR(7))
RETURNS TABLE
AS
RETURN
(
	--DECLARE @placa VARCHAR(7) = 'HAR5B39'
    SELECT AC.tipo_crime,
		   AC.data_ocorrencia,
		   AC.local_ocorrencia,
		   AC.descricao,
		   AC.sentenca
    FROM   muralha.antecedentes_criminais AC
    WHERE  AC.id_proprietario IN (SELECT id FROM muralha.fcn_ObterInfoProprietarioVeiculo(@placa))
)