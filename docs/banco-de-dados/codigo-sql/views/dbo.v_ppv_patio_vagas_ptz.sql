CREATE VIEW [dbo].[v_ppv_patio_vagas_ptz]
AS 
SELECT id_registro,
	   id_registro_patio_simulado,
	   id_vaga,
	   data_entrada,
	   data_saida,
	   COALESCE(placa_digitada, placa_lida) AS placa,
	   placa_lida,
	   placa_digitada,
	   imagem,
	   imagem_placa,
	   id_veiculo
FROM   ppv_patio_vagas_ptz (NOLOCK)
