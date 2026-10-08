CREATE   FUNCTION [muralha].[fcn_ObterInfoProprietarioVeiculo](@placa VARCHAR(7))
RETURNS TABLE
AS
RETURN
(
	--DECLARE @placa VARCHAR(7) = 'HAR5B39'
    SELECT PP.nome,
		PP.sobrenome,
		PP.cpf,
		CONVERT(VARCHAR(10), PP.data_nascimento, 120) AS dataNascimento,
		PP.endereco,
		PP.telefone,
		PP.email,
		PP.id
    FROM muralha.proprietario PP
		INNER JOIN muralha.proprietario_veiculo PV
			ON  PP.id = PV.id_proprietario
		INNER JOIN dbo.cadastro_veiculo VEI
			ON  VEI.placa = PV.placa
    WHERE VEI.placa = @placa
)