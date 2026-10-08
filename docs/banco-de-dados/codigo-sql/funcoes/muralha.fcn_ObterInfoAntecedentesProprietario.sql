CREATE FUNCTION [muralha].[fcn_ObterInfoAntecedentesProprietario](@id_proprietario INT)
RETURNS TABLE
AS
RETURN
(
	--DECLARE @id_proprietario INT = 3
    SELECT AC.tipo_crime,
		   AC.data_ocorrencia,
		   AC.local_ocorrencia,
		   AC.descricao,
		   AC.sentenca
    FROM   muralha.antecedentes_criminais AC
    WHERE  AC.id_proprietario = @id_proprietario
)
