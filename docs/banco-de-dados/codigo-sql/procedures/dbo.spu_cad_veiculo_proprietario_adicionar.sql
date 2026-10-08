
CREATE PROCEDURE [dbo].[spu_cad_veiculo_proprietario_adicionar]
(
@placa CHAR(7),
@id_marca INT,
@marca VARCHAR(35),
@id_tipo INT,
@tipo VARCHAR(35),
@proprietario VARCHAR(100),
@observacao VARCHAR(100),
@imagem IMAGE
)
AS

IF EXISTS(SELECT 1 FROM cad_veiculo_proprietario (NOLOCK) WHERE placa = @placa) 

UPDATE cad_veiculo_proprietario SET id_marca = @id_marca, marca = @marca, id_tipo = @id_tipo, tipo = @tipo, proprietario = @proprietario, observacao = @observacao, imagem = @imagem, data_atualizado = GETDATE()
WHERE placa = @placa

ELSE

INSERT INTO cad_veiculo_proprietario VALUES (@placa, @id_marca, @marca, @id_tipo, @tipo, @proprietario, @observacao, @imagem, GETDATE())


