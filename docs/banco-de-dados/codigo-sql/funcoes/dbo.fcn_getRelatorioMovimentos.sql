CREATE FUNCTION [dbo].[fcn_getRelatorioMovimentos](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
( 
	--DECLARE @dataInicio DATE = '2018-06-01', @dataFim DATE = '2018-06-05'
	SELECT relatorio.data,
		   relatorio.id_remessa,
		   relatorio.tipo,
		   relatorio.codigo_externo,
		   relatorio.nome_arquivo
	FROM   (
				SELECT CAST(sub1.data AS DATE) data,
					   sub1.id_remessa,
					   sub1.tipo,
					   sub1.codigo_externo,
					   sub1.nome_arquivo
				FROM   (
							SELECT data,
								   tipo,
								   id_remessa,
								   codigo_externo,
								   CASE WHEN id_enquadramento = 99999 THEN 'XM' ELSE 'LM' END + 
								   RTRIM(LTRIM(tipo)) + RIGHT(REPLICATE('0',6) + LTRIM(RTRIM(codigo_externo)), 6) + CONVERT(VARCHAR, data, 112) + '.TXT' AS nome_arquivo
							FROM   remessa (NOLOCK) 
							WHERE  CAST(data AS DATE) BETWEEN @dataInicio AND @dataFim 
					   ) AS sub1 
					   LEFT JOIN  arquivos_cai_para_cav ai (NOLOCK)
							ON  sub1.nome_arquivo = ai.nome_arquivo 
				WHERE  ai.nome_arquivo IS NULL
	
				UNION 
	
				SELECT CAST(sub1.data AS DATE) data,
					   sub1.id_remessa,
					   sub1.tipo,
					   sub1.codigo_externo,
					   sub1.nome_arquivo
				FROM   (
							SELECT r.data,
								   r.tipo,
								   r.id_remessa,
								   r.codigo_externo,
								   'TX' + RTRIM(LTRIM(r.tipo)) + RIGHT(REPLICATE('0',6) + LTRIM(RTRIM(r.codigo_externo)), 6) + CONVERT(VARCHAR, r.data, 112) +
								   RIGHT(REPLICATE('0',4) + CONVERT(VARCHAR, ir.sequencia), 4) + CONVERT(VARCHAR, img.indice_imagem) + '.TXT' AS nome_arquivo
							FROM   remessa r (NOLOCK) 
								   JOIN infracao_remessa ir (NOLOCK)
										ON  r.id_remessa = ir.id_remessa 
								   JOIN infracao i (NOLOCK)
										ON  ir.id_infracao = i.id_infracao 
								   JOIN veiculo_imagem vi (NOLOCK) 
										ON  i.id_veiculo = vi.id_veiculo 
								   JOIN imagem img (NOLOCK) 
										ON  vi.id_imagem = img.id_imagem 
							WHERE  CAST(r.data AS DATE) BETWEEN @dataInicio AND @dataFim 
					   ) AS sub1 
					   LEFT JOIN arquivos_cai_para_cav ai (NOLOCK) 
							ON  sub1.nome_arquivo = ai.nome_arquivo
				WHERE  ai.nome_arquivo IS NULL

				UNION 
	
				SELECT CAST(sub1.data AS DATE) data,
					   sub1.id_remessa,
					   sub1.tipo,
					   sub1.codigo_externo,
					   sub1.nome_arquivo
				FROM   (
							SELECT r.data,
								   r.tipo,
								   r.id_remessa,
								   r.codigo_externo,
								   'IM' + RTRIM(LTRIM(r.tipo)) + RIGHT(REPLICATE('0',6) + LTRIM(RTRIM(r.codigo_externo)), 6) + CONVERT(VARCHAR, r.data, 112) + 
								   RIGHT(REPLICATE('0',4) + CONVERT(VARCHAR, ir.sequencia), 4) + CONVERT(VARCHAR, img.indice_imagem) + '.JPG' AS nome_arquivo
							FROM   remessa r (NOLOCK) 
								   JOIN infracao_remessa ir (NOLOCK) 
										ON  r.id_remessa = ir.id_remessa 
								   JOIN infracao i (NOLOCK) 
										ON  ir.id_infracao = i.id_infracao 
								   JOIN veiculo_imagem vi (NOLOCK) 
										ON  i.id_veiculo = vi.id_veiculo 
								   JOIN imagem img (NOLOCK) 
										ON  vi.id_imagem = img.id_imagem 
							WHERE  CAST(r.data AS DATE) BETWEEN @dataInicio AND @dataFim 
					   ) AS sub1 
					   LEFT JOIN arquivos_cai_para_cav ai (NOLOCK) 
							ON  sub1.nome_arquivo = ai.nome_arquivo
				WHERE  ai.nome_arquivo IS NULL
	       ) AS relatorio
		   LEFT JOIN movimentos_erro erro (NOLOCK)
				ON  erro.id_remessa = relatorio.id_remessa
	WHERE  erro.id_remessa IS NULL

)
