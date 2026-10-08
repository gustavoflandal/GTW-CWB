CREATE VIEW [dbo].[v_municipios_equipamentos]
AS  
	SELECT r.id_localidade,
		   r.nome,
		   r.uf
	FROM   (
				SELECT cl.id_localidade,
   					   dbo.InitCap(cl.nome) AS nome,
   					   cl.uf
				FROM   muralha.fcn_LocalidadeContrato() l
   					   INNER JOIN cad_localidade cl
   	  						ON  cl.id_localidade = l.id_localidade
				GROUP BY
   					   cl.id_localidade,
   					   cl.nome,
   					   cl.uf

				UNION

				SELECT cl.id_localidade,
   					   dbo.InitCap(cl.nome) AS nome,
   					   cl.uf
				FROM   local_vigente lv
   					   INNER JOIN cad_localidade cl
   	  						ON  cl.id_localidade = lv.id_localidade
				WHERE  lv.desativado = 0
				GROUP BY
   					   cl.id_localidade,
   					   cl.nome,
   					   cl.uf
		   ) r
	--ORDER BY
	--	   r.nome
