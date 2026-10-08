
CREATE PROCEDURE [dbo].[spu_obterRemessasApagarHomologacao] 
AS 

	DECLARE @data_apagar DATE, @data_maior_inf DATE

	SET @data_maior_inf= (SELECT CAST(MAX(r_aux.data_inicial) AS DATE) AS data_inicial
						  FROM   tmp_arquivos_ftp_hom t_aux (NOLOCK)
								 INNER JOIN remessa r_aux (NOLOCK)
									  ON  r_aux.tipo = t_aux.tipo
										  AND r_aux.codigo_externo = t_aux.codigo_externo)

	SET @data_apagar = DATEADD(DAY, -10, @data_maior_inf)

	SELECT r.tipo, r.codigo_externo, t.diretorio, dir.tamanho
	FROM   tmp_arquivos_ftp_hom t (NOLOCK)
		   INNER JOIN remessa r (NOLOCK)
				ON  r.tipo = t.tipo AND r.codigo_externo = t.codigo_externo
		   INNER JOIN tmp_diretorios_ftp_hom dir (NOLOCK)
				ON  r.tipo = dir.tipo AND r.codigo_externo = dir.codigo_externo AND dir.nivel = 5
	WHERE  CAST(r.data_inicial AS DATE) < @data_apagar
	--GROUP BY r.tipo, r.codigo_externo, t.diretorio
	ORDER BY 
		   r.tipo, r.codigo_externo, t.diretorio

-- 75.568
