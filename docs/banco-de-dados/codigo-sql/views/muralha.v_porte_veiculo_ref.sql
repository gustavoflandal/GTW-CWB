CREATE VIEW [muralha].[v_porte_veiculo_ref]  
AS  
	SELECT CASE WHEN cv.id_classe = '' THEN 2  
				WHEN cv.id_classe = 'M' THEN 1  
				WHEN cv.id_classe = 'P' THEN 2  
				WHEN cv.id_classe IN ('T', 'C','O') THEN 3  
				--WHEN cv.id_classe IN ('C','O') THEN 3  
		   END AS id,  
		   CASE WHEN cv.id_classe = '' THEN 'Pequeno'  
				WHEN cv.id_classe = 'M' THEN 'Moto'  
				WHEN cv.id_classe = 'P' THEN 'Pequeno'  
				WHEN cv.id_classe IN ('T', 'C','O') THEN 'Medio'  
				--WHEN cv.id_classe IN ('C','O') THEN 'Grande'  
		   END AS porte,  
		   id_classe,
		   descricao
	FROM   classe_veiculo cv
	UNION
	SELECT 4 AS id,  
		   'Grande' AS porte,  
		   NULL AS id_classe,
		   NULL AS descricao
	FROM   classe_veiculo cv
