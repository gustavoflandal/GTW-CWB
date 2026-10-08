CREATE FUNCTION [dbo].[fcn_getDadosPendentesImportacao]()
RETURNS TABLE
AS
RETURN
	(
		SELECT ROW_NUMBER() OVER(ORDER BY import.data) AS ordem,
			   data AS dia,
			   SUM(import.qtde_veiculo) AS qtde_veiculo,
			   SUM(import.qtde_infracao) AS qtde_infracao,
			   SUM(import.qtde_perfil) AS qtde_perfil,
			   SUM(import.qtde_imagem) AS qtde_imagem
		FROM   (
					SELECT CAST(data AS DATE) AS data,
						   COUNT(*) AS qtde_veiculo,
						   0 AS qtde_infracao,
						   0 AS qtde_perfil,
						   0 AS qtde_imagem
					FROM   veiculo_importacao (NOLOCK)
					WHERE  importar = 1
					GROUP BY
						   CAST(data AS DATE)

					UNION

					SELECT CAST(data AS DATE) AS data,
						   0 AS qtde_veiculo,
						   COUNT(*) AS qtde_infracao,
						   0 AS qtde_perfil,
						   0 AS qtde_imagem
					FROM   infracao_importacao ii (NOLOCK)
						   INNER JOIN veiculo_importacao vi (NOLOCK)
								ON vi.id_veiculo_unic = ii.id_veiculo_unic
					WHERE  vi.importar = 1
					GROUP BY
						   CAST(data AS DATE)

					UNION

					SELECT CAST(data AS DATE) AS data,
						   0 AS qtde_veiculo,
						   0 AS qtde_infracao,
						   COUNT(*) AS qtde_perfil,
						   0 AS qtde_imagem
					FROM   perfil_importacao pimp (NOLOCK)
						   INNER JOIN veiculo_importacao vi (NOLOCK)
								ON vi.id_veiculo_unic = pimp.id_veiculo_unic
					WHERE  vi.importar = 1
					GROUP BY
						   CAST(data AS DATE)

					UNION

					SELECT CAST(data AS DATE) AS data,
						   0 AS qtde_veiculo,
						   0 AS qtde_infracao,
						   0 AS qtde_perfil,
						   COUNT(*) AS qtde_imagem
					FROM   imagem_importacao ii (NOLOCK)
						   INNER JOIN veiculo_importacao vi (NOLOCK)
								ON vi.id_veiculo_unic = ii.id_veiculo_unic
									AND ii.indice_imagem = 0
					WHERE  vi.importar = 1
					GROUP BY
						   CAST(data AS DATE)
		)import
		GROUP BY
			   import.data

	)
