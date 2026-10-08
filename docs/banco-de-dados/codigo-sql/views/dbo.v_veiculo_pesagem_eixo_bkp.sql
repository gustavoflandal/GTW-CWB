CREATE VIEW [dbo].[v_veiculo_pesagem_eixo_bkp]
AS
	SELECT id_veiculo_unic,
		   (SELECT COUNT(c) FROM (VALUES(p.E1),(p.E2),(p.E3),(p.E4),(p.E5),(p.E6),(p.E7),(p.E8),(p.E9)) T (c)) AS qtde_eixos,
		   E1,E2,E3,E4,E5,E6,E7,E8,E9,
		   (SELECT SUM(c) FROM (VALUES(E1),(E2),(E3),(E4),(E5),(E6),(E7),(E8),(E9)) T (c)) AS pbtc
	FROM   (
				SELECT vpi.id_veiculo_unic,
					   vpi.temperatura_pavimento,
					   vpi.velocidade_piezo,
					   vpi.pbt,
					   'E' + CAST(vpei.eixo AS CHAR(1)) AS eixo,
					   ROUND(vpei.peso,0) AS peso
				FROM   bkp_veiculo_pesagem vpi
					   JOIN bkp_veiculo_pesagem_eixo vpei
							ON  vpei.id_veiculo_unic = vpi.id_veiculo_unic
				WHERE  vpi.pesagem_valida = 1
		   ) e
	PIVOT  (
				SUM(peso)
				FOR eixo IN ([E1],[E2],[E3],[E4],[E5],[E6],[E7],[E8],[E9])
		   ) p
