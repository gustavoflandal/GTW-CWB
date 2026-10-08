
CREATE FUNCTION [muralha].[fcn_ObterImagensObjAlertaVinculado](@id_alerta UNIQUEIDENTIFIER)
RETURNS TABLE
AS
	RETURN
	(
		--DECLARE @id_alerta UNIQUEIDENTIFIER = '94b93990-5fbc-4cb2-bb21-a329a3e0b61e'
		SELECT id_alerta,
			   obj1,
			   obj2
		FROM   (
					SELECT id_alerta,
						   'obj' + CAST(item AS CHAR(1)) AS num_imagem,
						   id_imagem_tempo_real
					FROM   (
								SELECT ROW_NUMBER() OVER(ORDER BY vtr.data) AS item,
									   av.id_alerta,
									   av.id_veiculo_tempo_real,
									   vtri.id AS id_imagem_tempo_real,
									   vtri.indice_imagem
								FROM   muralha.alerta_veiculo av
									   INNER JOIN muralha.veiculo_tempo_real vtr
											ON  vtr.id = av.id_veiculo_tempo_real
									   INNER JOIN muralha.veiculo_tempo_real_imagem vtri
											ON  vtri.id_veiculo_tempo_real = av.id_veiculo_tempo_real
								WHERE  av.id_alerta = @id_alerta
									   AND vtri.indice_imagem = 0
						   ) r
					WHERE  item <= 2
			   ) img
		PIVOT  (
					MAX(id_imagem_tempo_real)
					FOR num_imagem IN ([obj1],[obj2])
			   ) cnt
	)
