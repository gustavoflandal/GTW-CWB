CREATE FUNCTION [dbo].[fcn_getRelPesagemPorEixoDetalhado](@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE      
AS      
RETURN      
(      
	--DECLARE @dataInicio DATE = '2025-09-30', @dataFim DATE = '2025-09-30';
	WITH dados_pesagem AS  
	(
		SELECT vp.id_veiculo_unic,
			   vp.data,
			   vp.id_local,
			   vp.pista,
			   vp.placa,
			   vp.velocidade,
			   vp.comprimento,
			   vp.id_classe AS id_classe_original,
			   CASE WHEN vp.id_classe = 'O' AND vpe.qtde_eixos > 3 THEN 'C'
					WHEN vp.id_classe = 'O' AND vpe.qtde_eixos = 3 AND vpde.distancia_E2E3 > 2.4 THEN 'C'
					WHEN vp.id_classe = 'O' AND vpe.qtde_eixos = 2 AND vpde.distancia_E1E2 < 3.5 THEN 'T'
					WHEN vp.id_classe = 'T' AND vpe.qtde_eixos > 2 AND vpp.pbt > 2000 THEN 'C'
					WHEN vp.id_classe IN ('C', 'Q') AND vpe.qtde_eixos < 3 AND vpp.pbt <= 2000 THEN 'T'
					ELSE vp.id_classe
			   END AS id_classe,
			   vpp.pbt,
			   vpe.qtde_eixos,
			   vpde.distancia_E1E2, vpde.distancia_E2E3, vpde.distancia_E3E4, vpde.distancia_E4E5, vpde.distancia_E5E6, vpde.distancia_E6E7, vpde.distancia_E7E8, vpde.distancia_E8E9,
			   E1,E2,E3,E4,E5,E6,E7,E8,E9,
			   vpp.temperatura_pavimento
		FROM   veiculo_pesquisa vp (NOLOCK)
			   JOIN v_veiculo_pesagem vpp (NOLOCK)
					ON  vpp.id_veiculo_unic = vp.id_veiculo_unic  
			   JOIN v_veiculo_pesagem_eixo vpe (NOLOCK)
					ON  vpe.id_veiculo_unic = vp.id_veiculo_unic  
			   JOIN v_veiculo_pesagem_distancia_eixos vpde (NOLOCK)
					ON  vpde.id_veiculo_unic = vp.id_veiculo_unic  
		WHERE  CAST(vp.data AS DATE) BETWEEN @dataInicio AND @dataFim		   
	)  
 
	SELECT ROW_NUMBER() OVER(ORDER BY p.data) AS [#],
		   --p.id_veiculo_unic,
		   lv.serie_equipamento AS [Nº Serie Equip.],
		   p.pista AS [Faixa],
		   RTRIM(lv.nome) AS [Local],
		   p.data AS [Data Veiculo],
		   p.placa AS [Placa],
		   p.velocidade AS [Velocidade],
		   p.comprimento AS [Comprimento],
		   --id_classe_original,
		   --p.id_classe,
		   CASE WHEN p.qtde_eixos > 0
				THEN dbo.fcn_getClassificacaoPNCT_v2 (
						p.id_classe, p.qtde_eixos, p.comprimento, p.pbt,
						p.distancia_E1E2, p.distancia_E2E3, p.distancia_E3E4, p.distancia_E4E5, p.distancia_E5E6, p.distancia_E6E7, p.distancia_E7E8, p.distancia_E8E9)
				ELSE NULL
		   END AS [PNCT],
		   p.pbt AS [PBT],
		   p.qtde_eixos AS [Qtde. Eixos],
		   p.E1,p.E2,p.E3,p.E4,p.E5,p.E6,p.E7,p.E8,p.E9,
		   --p.distancia_E1E2, p.distancia_E2E3, p.distancia_E3E4, p.distancia_E4E5, p.distancia_E5E6, p.distancia_E6E7, p.distancia_E7E8, p.distancia_E8E9,
		   p.temperatura_pavimento AS [Temperatura Pavimento]  
	FROM   dados_pesagem p
		   JOIN local_vigente lv (NOLOCK)
				ON  lv.id_local = p.id_local
	WHERE  ((p.id_classe IN ('P', 'M', 'T', '', 'V', 'F', 'B') AND p.velocidade < 170) OR (p.id_classe IN ('C', 'O', 'Q') AND p.velocidade < 140))
)