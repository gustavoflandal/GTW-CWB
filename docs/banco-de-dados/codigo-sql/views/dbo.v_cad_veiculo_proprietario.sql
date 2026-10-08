

CREATE VIEW [dbo].[v_cad_veiculo_proprietario] AS

SELECT placa, 
COALESCE(marca, 'N/D') AS marca, 
COALESCE(tipo, 'N/D') AS tipo, 
COALESCE(proprietario, 'N/D') AS proprietario, 
COALESCE(observacao, 'N/D') AS observacao, 
data_atualizado FROM cad_veiculo_proprietario (NOLOCK)

