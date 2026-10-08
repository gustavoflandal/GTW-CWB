CREATE FUNCTION [dbo].[fcn_getLimiteCargaGrupoQFV](@id_classificacao char(3), @grupo CHAR(3))
RETURNS TABLE
AS
RETURN
(
	--DECLARE @id_classificacao CHAR(3) = '3C', @grupo CHAR(3) = NULL
	SELECT grupo_desc.id_classificacao,
		   grupo_desc.grupo,
		   grupo_carga.carga,
		   (grupo_carga.carga * 1000) AS carga_kg,
		   grupo_carga.carga_tolerancia,
		   (grupo_carga.carga_tolerancia * 1000) AS carga_tolerancia_kg,
		   grupo_desc.descricao
	FROM   (
				SELECT id_classificacao,
					   grupo,
					   descricao
				FROM   (
							SELECT id_classificacao,
								   grupo1_desc AS G1,
								   grupo2_desc AS G2,
								   grupo3_desc AS G3,
								   grupo4_desc AS G4,
								   grupo5_desc AS G5,
								   grupo6_desc AS G6,
								   grupo7_desc AS G7
							--SELECT *
							FROM   v_ppv_qfv
							WHERE  id_classificacao = @id_classificacao
					   ) t
				UNPIVOT
					   (
							descricao FOR grupo IN (G1,G2,G3,G4,G5,G6,G7)
					   ) AS t2
		   ) AS grupo_desc
		   INNER JOIN (
							SELECT id_classificacao,
								   grupo,
								   carga,
								   carga_tolerancia
							FROM   (
										SELECT id_classificacao,
											   grupo1_carga AS G1,
											   grupo2_carga AS G2,
											   grupo3_carga AS G3,
											   grupo4_carga AS G4,
											   grupo5_carga AS G5,
											   grupo6_carga AS G6,
											   grupo7_carga AS G7,
											   grupo1_carga_tolerancia AS G1_t,
											   grupo2_carga_tolerancia AS G2_t,
											   grupo3_carga_tolerancia AS G3_t,
											   grupo4_carga_tolerancia AS G4_t,
											   grupo5_carga_tolerancia AS G5_t,
											   grupo6_carga_tolerancia AS G6_t,
											   grupo7_carga_tolerancia AS G7_t
										FROM   v_ppv_qfv
										WHERE  id_classificacao = @id_classificacao
								   ) t
							UNPIVOT
								   (
										carga FOR grupo IN (G1,G2,G3,G4,G5,G6,G7)
								   ) AS t2
							UNPIVOT
								   (
										carga_tolerancia FOR grupo2 IN (G1_t,G2_t,G3_t,G4_t,G5_t,G6_t,G7_t)
								   ) AS t3
							WHERE  grupo = SUBSTRING(grupo2, 1, CASE WHEN (CHARINDEX('_', grupo2) - 1) = 0 THEN 2 ELSE (CHARINDEX('_', grupo2) - 1) END)
		   ) AS grupo_carga
				ON  grupo_carga.id_classificacao = grupo_desc.id_classificacao
					AND grupo_carga.grupo = grupo_desc.grupo
		  -- LEFT JOIN (
				--			SELECT id_classificacao,
				--				   grupo,
				--				   carga_tolerancia
				--			FROM   (
				--						SELECT id_classificacao,
				--							   grupo1_carga_tolerancia AS G1,
				--							   grupo2_carga_tolerancia AS G2,
				--							   grupo3_carga_tolerancia AS G3,
				--							   grupo4_carga_tolerancia AS G4,
				--							   grupo5_carga_tolerancia AS G5,
				--							   grupo6_carga_tolerancia AS G6,
				--							   grupo7_carga_tolerancia AS G7
				--						FROM   v_ppv_qfv
				--						WHERE  id_classificacao = @id_classificacao
				--				   ) t
				--			UNPIVOT
				--				   (
				--						carga_tolerancia FOR grupo IN (G1,G2,G3,G4,G5,G6,G7)
				--				   ) AS t2
		  -- ) AS grupo_carga_tolerancia
				--ON  grupo_carga_tolerancia.id_classificacao = grupo_desc.id_classificacao
				--	AND grupo_carga_tolerancia.grupo = grupo_desc.grupo
	WHERE  (@grupo IS NULL OR grupo_desc.grupo = @grupo)
	GROUP BY
		   grupo_desc.id_classificacao,
		   grupo_desc.grupo,
		   grupo_carga.carga,
		   grupo_carga.carga_tolerancia,
		   grupo_desc.descricao
)
