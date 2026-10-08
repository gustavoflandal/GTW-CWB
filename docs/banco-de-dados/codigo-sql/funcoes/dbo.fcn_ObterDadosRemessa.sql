CREATE FUNCTION [dbo].[fcn_ObterDadosRemessa] 
(	
	@id_remessa INT
)
RETURNS TABLE 
AS
RETURN 
(

/*

Atualizado em 17/09/2015 com Resolução CONTRAN nº 396/2011 (O.S 0120)

7	MICROONIBUS                   
8	ONIBUS                        
10	REBOQUE                       
11	SEMI-REBOQUE                  
14	CAMINHAO                      
17	CAMINHAO TRATOR               
18	TRATOR RODAS                  
20	TRATOR MISTO                  
22	CHASSI-PLATAFORMA             
26	MOTOR-CASA                    

*/

	 --DECLARE @id_remessa INT = 15938
	SELECT 
		ir.sequencia, 
		ir.id_infracao, 
		i.id_imagem_local, 
		COALESCE(icav.id_inconsistencia_cav, i.id_inconsistencia) AS id_inconsistencia, 
		-1 AS inconsistencia_validacao, 
		ir.serie, 
		ir.auto, 
		COALESCE(i.placa, v.placa, REPLICATE(' ', 7)) AS placa, 
		0 AS pais, 
		COALESCE(cv.id_marca_cet, cmcetp.id_marca_cet, cvv.id_marca_cet, 0) as id_marca, 
		COALESCE(cv.id_especie, ep.id_especie, 19) AS id_especie, 
		i.id_enquadramento, 
	
		CASE WHEN p.cod_local_prodam_auxiliar > 0 AND e.infracao_metrologia = 1 AND 
			((cv.placa IS NULL AND v.id_classe IN ('C','O')) OR (cv.placa IS NOT NULL AND cv.id_tipo IN (7,8,10,11,14,17,18,20,22,26))
				OR (i.id_inconsistencia > 0 AND cv.placa IS NULL AND (cvv.placa IS NOT NULL AND cvv.id_tipo IN (7,8,10,11,14,17,18,20,22,26)))) 
		THEN p.cod_local_prodam_auxiliar
		ELSE p.cod_pista END	AS cod_pista,

		p.nome_pista as nome_local, 
		COALESCE(v.codigo_prodam, p.cod_pista_prodam) AS cod_pista_prodam,
		i.data, 
		v.velocidade, 
		i.velocidade_considerada,
		i.velocidade_limite,

		CASE WHEN v.entre_faixa IS NOT NULL THEN p.cod_pista_tarja ELSE p.cod_pista_alternativo END AS pista, 

		COALESCE(ip_l.id_usuario, r.id_usuario, 0) AS cod_operador, 
		0 AS cod_agente,
		COALESCE(ip_l.data, ipc_l.data_conclusao) AS data_analise,
		CAST(null AS DATETIME) AS data_validacao,
		CASE WHEN obl.id_imagem IS NULL THEN 0 ELSE 1 END AS com_obliteracao,
		obl.x,  
		obl.y,  
		obl.altura,  
		obl.largura, 
		vim.id_imagem, 
		CASE WHEN vv.id_veiculo IS NULL THEN 0 ELSE 1 END AS com_video, 
		CASE WHEN img.imagem IS NOT NULL THEN CAST(1 AS BIT) ELSE CAST (0 AS BIT) END AS com_imagem, 
		img.imagem, img.indice_imagem 
	FROM  
		infracao_remessa ir WITH (NOLOCK) 
	JOIN remessa r (NOLOCK)
		ON ir.id_remessa = r.id_remessa 
	JOIN infracao i WITH (NOLOCK) 
		ON i.id_infracao = ir.id_infracao 
	JOIN enquadramento e (NOLOCK)
		ON i.id_enquadramento = e.id_enquadramento 
	JOIN veiculo v WITH (NOLOCK) 
		ON v.id_veiculo = i.id_veiculo 
	LEFT JOIN local l WITH (NOLOCK) 
		ON l.id_local = i.id_local 
		AND l.sequencia_local = i.sequencia_local 
	LEFT JOIN cad_veiculo cv WITH (NOLOCK) 
		ON cv.placa = i.placa
	LEFT JOIN cad_veiculo cvv (NOLOCK) 
		ON cvv.placa = v.placa  
	LEFT JOIN cad_marca_cet_processo cmcetp WITH (NOLOCK) 
		ON cmcetp.placa = i.placa 
	LEFT JOIN cad_especie_processo ep WITH (NOLOCK) 
		ON ep.placa = i.placa 
	LEFT JOIN configuracao_equipamento_pista p WITH (NOLOCK) 
		ON p.id_configuracao_equipamento = l.id_configuracao_equipamento AND p.id_pista = i.pista 
	JOIN veiculo_imagem vim WITH (NOLOCK) 
		ON vim.id_veiculo = i.id_veiculo 
	LEFT JOIN infracao_obliteracao obl WITH (NOLOCK) 
	    ON obl.id_infracao = ir.id_infracao AND obl.id_imagem = vim.id_imagem 
		AND obl.sequencia_obliteracao = 1
	LEFT JOIN infracao_processo ip_l (NOLOCK)
		ON i.id_infracao = ip_l.id_infracao 
		AND ip_l.id_processo IN (1, 2, 24)  
	LEFT JOIN (	SELECT 
						MAX(id_infracao_processo) AS id_infracao_processo, 
						id_infracao
					FROM 
						infracao_processo ip (nolock) 
					WHERE ip.id_processo IN (1, 2, 24) AND ip.status_processo = 0
					GROUP BY 
						id_infracao
				) AS sub2 
				ON	ir.id_infracao = sub2.id_infracao 
				AND ip_l.id_infracao_processo = sub2.id_infracao_processo
	JOIN infracao_processo_concluido ipc_l (NOLOCK)
		ON i.id_infracao = ipc_l.id_infracao
		AND ipc_l.id_processo = 11 
 	JOIN imagem img WITH (NOLOCK) 
		ON img.id_imagem = vim.id_imagem 
	LEFT JOIN veiculo_video vv (NOLOCK) 
		ON vv.id_veiculo = v.id_veiculo
	LEFT JOIN inconsistencia_cav icav (NOLOCK)
		ON icav.id_inconsistencia_cai = i.id_inconsistencia
		AND (icav.data_fim >= CAST(GETDATE() AS DATE) OR icav.data_fim IS NULL)
		AND icav.data_inicio <= CAST(GETDATE() AS DATE)
	WHERE 
		ir.id_remessa = @id_remessa 
		AND (ip_l.id_infracao_processo IS NULL OR sub2.id_infracao_processo IS NOT NULL) 
	--ORDER BY sequencia
)
