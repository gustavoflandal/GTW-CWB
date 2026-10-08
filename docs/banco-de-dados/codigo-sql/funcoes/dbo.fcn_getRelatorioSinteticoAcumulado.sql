
CREATE FUNCTION [dbo].[fcn_getRelatorioSinteticoAcumulado](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	SELECT TOP 100 PERCENT
		@dataInicio AS [INICIO],
		@dataFim AS [FIM],
		sub1.serie_equipamento AS [NS],
		sub1.nome AS [LOCAL],
		sub1.DATA_INICIO AS [INICIO_OPERACAO],
		COALESCE(sub2.TRAFEGO, sub1.TRAFEGO) AS [TRAFEGO],
		COALESCE(sub3.[56732], sub1.[56732]) AS [56732],
		COALESCE(sub3.[56900], sub1.[56900]) AS [56900],
		COALESCE(sub3.[57030], sub1.[57030]) AS [57030],
		COALESCE(sub3.[57461], sub1.[57461]) AS [57461],
		COALESCE(sub3.[57462], sub1.[57462]) AS [57462],
		COALESCE(sub3.[57463], sub1.[57463]) AS [57463],
		COALESCE(sub3.[59910], sub1.[59910]) AS [59910],
		COALESCE(sub3.[60411], sub1.[60411]) AS [60411],
		COALESCE(sub3.[60412], sub1.[60412]) AS [60412],
		COALESCE(sub3.[60503], sub1.[60503]) AS [60503],
		COALESCE(sub3.[74550], sub1.[74550]) AS [74550],
		COALESCE(sub3.[74630], sub1.[74630]) AS [74630],
		COALESCE(sub3.[74710], sub1.[74710]) AS [74710],
		COALESCE(sub3.[TOT_INFRACAO], sub1.[TOT_INFRACAO]) AS [TOT_INFRACAO],
		COALESCE(sub4.[PT_TRI]+sub4.[PT_DIG],  sub1.[PT_ANALISE])    AS [PT_ANALISE],
		COALESCE(sub4.[PNT_TRI]+sub4.[PNT_DIG],sub1.[PNT_ANALISE])   AS [PNT_ANALISE],
		COALESCE(sub4.[REJEICOES],sub1.[REJEICOES]) AS [REJEICOES],
		COALESCE(sub4.[PT_VAL], sub1.[PT_VALIDACAO])  AS [PT_VAL],
		COALESCE(sub4.[PNT_VAL],sub1.[PNT_VALIDACAO]) AS [PNT_VAL],
		COALESCE(sub4.[INVALIDACOES],sub1.[INVALIDACOES]) AS [INVALIDACOES],
		COALESCE(sub6.[56732], sub1.[56732]) AS [AUTUACOES 56732],
		COALESCE(sub6.[56900], sub1.[56900]) AS [AUTUACOES 56900],
		COALESCE(sub6.[57030], sub1.[57030]) AS [AUTUACOES 57030],
		COALESCE(sub6.[57461], sub1.[57461]) AS [AUTUACOES 57461],
		COALESCE(sub6.[57462], sub1.[57462]) AS [AUTUACOES 57462],
		COALESCE(sub6.[57463], sub1.[57463]) AS [AUTUACOES 57463],
		COALESCE(sub6.[59910], sub1.[59910]) AS [AUTUACOES 59910],
		COALESCE(sub6.[60411], sub1.[60411]) AS [AUTUACOES 60411],
		COALESCE(sub6.[60412], sub1.[60412]) AS [AUTUACOES 60412],
		COALESCE(sub6.[60503], sub1.[60503]) AS [AUTUACOES 60503],
		COALESCE(sub6.[74550], sub1.[74550]) AS [AUTUACOES 74550],
		COALESCE(sub6.[74630], sub1.[74630]) AS [AUTUACOES 74630],
		COALESCE(sub6.[74710], sub1.[74710]) AS [AUTUACOES 74710],
		COALESCE(sub6.[AUTUACOES], sub1.[AUTUACOES]) AS [AUTUACOES TOTAL]
	FROM (	SELECT
				lv.id_local,
				lv.serie_equipamento,
				CAST(lv.data_inicio AS DATE) AS [DATA_INICIO],
				lv.nome,
				0 AS [TRAFEGO],
				0 AS [56732],
				0 AS [56900],
				0 AS [57030],
				0 AS [57461],
				0 AS [57462],
				0 AS [57463],
				0 AS [59910],
				0 AS [60411],
				0 AS [60412],
				0 AS [60503],
				0 AS [74550],
				0 AS [74630],
				0 AS [74710],
				0 AS [TOT_INFRACAO],
				0 AS [PT_ANALISE],
				0 AS [PNT_ANALISE],
				0 AS [REJEICOES],
				0 AS [PNT_VALIDACAO],
				0 AS [PT_VALIDACAO],
				0 AS [INVALIDACOES],
				0 AS [AUTUACOES]
			FROM local_vigente lv (nolock)
			WHERE	@dataFim >= CAST(lv.data_inicio AS DATE)
				AND (lv.data_fim IS NULL OR @dataFim <= CAST(lv.data_fim AS DATE))
		) AS sub1
		LEFT JOIN (	SELECT
						vs.id_local,
						SUM(vs.trafego) AS [TRAFEGO]
					FROM
						veiculo_sumarizado vs (nolock)
					WHERE	CAST(vs.data AS DATE) >= @dataInicio
						AND CAST(vs.data AS DATE) <= @dataFim
					GROUP BY
						vs.id_local
				) AS sub2
			ON sub1.id_local = sub2.id_local
		LEFT JOIN (SELECT
					   inf.id_local,
					   SUM(CASE WHEN (inf.id_enquadramento = 56732) THEN 1 ELSE 0 END) AS [56732],
					   SUM(CASE WHEN (inf.id_enquadramento = 56900) THEN 1 ELSE 0 END) AS [56900],
					   SUM(CASE WHEN (inf.id_enquadramento = 57030) THEN 1 ELSE 0 END) AS [57030],
					   SUM(CASE WHEN (inf.id_enquadramento = 57461) THEN 1 ELSE 0 END) AS [57461],
					   SUM(CASE WHEN (inf.id_enquadramento = 57462) THEN 1 ELSE 0 END) AS [57462],
					   SUM(CASE WHEN (inf.id_enquadramento = 57463) THEN 1 ELSE 0 END) AS [57463],
					   SUM(CASE WHEN (inf.id_enquadramento = 59910) THEN 1 ELSE 0 END) AS [59910],
					   SUM(CASE WHEN (inf.id_enquadramento = 60411) THEN 1 ELSE 0 END) AS [60411],
					   SUM(CASE WHEN (inf.id_enquadramento = 60412) THEN 1 ELSE 0 END) AS [60412],
					   SUM(CASE WHEN (inf.id_enquadramento = 60503) THEN 1 ELSE 0 END) AS [60503],
					   SUM(CASE WHEN (inf.id_enquadramento = 74550) THEN 1 ELSE 0 END) AS [74550],
					   SUM(CASE WHEN (inf.id_enquadramento = 74630) THEN 1 ELSE 0 END) AS [74630],
					   SUM(CASE WHEN (inf.id_enquadramento = 74710) THEN 1 ELSE 0 END) AS [74710],
					   COUNT(*) AS [TOT_INFRACAO]
				   FROM
						infracao inf (nolock)
				   WHERE	inf.id_enquadramento > 1
					   AND CAST(inf.data AS DATE) >= @dataInicio
					   AND CAST(inf.data AS DATE) <= @dataFim
				   GROUP BY
						inf.id_local
				) AS sub3
		ON  sub1.id_local = sub3.id_local
	LEFT JOIN (	SELECT
					inf.id_local,
					SUM(CASE WHEN ((inc_dig.razao_tecnica IS NULL AND inc_tri.razao_tecnica IS NULL AND inc_fdig.razao_tecnica IS NULL AND inc_ftri.razao_tecnica IS NULL) -- inconsistente somente na validacao ou no filtro da validacao por PNT
             						AND (inc_val.razao_tecnica <= 1 OR inc_fval.razao_tecnica <= 1)) THEN 1 ELSE 0 END) AS [PNT_VAL],
					SUM(CASE WHEN ((inc_dig.razao_tecnica IS NULL AND inc_tri.razao_tecnica IS NULL AND inc_fdig.razao_tecnica IS NULL AND inc_ftri.razao_tecnica IS NULL) -- inconsistente somente na validacao ou no filtro da validacao por PT
								AND (inc_val.razao_tecnica = 2 OR inc_fval.razao_tecnica = 2)) THEN 1 ELSE 0 END) AS [PT_VAL], 
					SUM(CASE WHEN ((inc_dig.razao_tecnica <= 1)OR(inc_fdig.razao_tecnica <= 1)) THEN 1 ELSE 0 END) AS [PNT_DIG], -- inconsistente somente na digitacao ou no filtro da digitacao por PNT
					SUM(CASE WHEN ((inc_dig.razao_tecnica = 2)OR(inc_fdig.razao_tecnica = 2)) THEN 1 ELSE 0 END) AS [PT_DIG], -- inconsistente somente na digitacao ou no filtro da digitacaolidacao por PT
					SUM(CASE WHEN ((inc_tri.razao_tecnica <= 1)OR(inc_ftri.razao_tecnica <= 1)) THEN 1 ELSE 0 END) AS [PNT_TRI], -- inconsistente somente na triagem ou no filtro da triagem por PNT
					SUM(CASE WHEN ((inc_tri.razao_tecnica = 2)OR(inc_tri.razao_tecnica = 2)) THEN 1 ELSE 0 END) AS [PT_TRI], -- inconsistente somente na triagem ou no filtro da triagem por PT
					COUNT(inc_tri.id_inconsistencia)+COUNT(inc_ftri.id_inconsistencia)+COUNT(inc_dig.id_inconsistencia)+COUNT(inc_fdig.id_inconsistencia) AS [REJEICOES], -- inconsistente na triagem e digitação e seus filtros
					COUNT(CASE WHEN ((inc_dig.razao_tecnica IS NULL AND inc_tri.razao_tecnica IS NULL AND inc_fdig.razao_tecnica IS NULL AND inc_ftri.razao_tecnica IS NULL) -- inconsistente na validação
             						AND (inc_val.razao_tecnica <= 2 OR inc_fval.razao_tecnica <= 2)) THEN 1 ELSE NULL END) AS [INVALIDACOES]
				FROM infracao inf (nolock)
					LEFT JOIN infracao_processo_concluido ic_val (nolock) 
						ON ic_val.id_infracao = inf.id_infracao 
						AND ic_val.id_processo = 3                
					LEFT JOIN infracao_processo_concluido ic_dig (nolock) 
						ON ic_dig.id_infracao = inf.id_infracao 
						AND ic_dig.id_processo = 2                
					LEFT JOIN infracao_processo_concluido ic_tri (nolock) 
						ON ic_tri.id_infracao = inf.id_infracao 
						AND ic_tri.id_processo = 1                
					LEFT JOIN infracao_processo_concluido ic_fval (nolock) 
						ON ic_fval.id_infracao = inf.id_infracao 
						AND ic_fval.id_processo = 22              
					LEFT JOIN infracao_processo_concluido ic_fdig (nolock) 
						ON ic_fdig.id_infracao = inf.id_infracao
						AND ic_fdig.id_processo = 21              
					LEFT JOIN infracao_processo_concluido ic_ftri (nolock) 
						ON ic_ftri.id_infracao = inf.id_infracao 
						AND ic_ftri.id_processo = 20              
					LEFT JOIN inconsistencia inc_val (nolock) 
						ON inc_val.id_inconsistencia = ic_val.id_inconsistencia 
						and inc_val.id_inconsistencia > 0
					LEFT JOIN inconsistencia inc_dig (nolock) 
						ON inc_dig.id_inconsistencia = ic_dig.id_inconsistencia 
						and inc_dig.id_inconsistencia > 0
					LEFT JOIN inconsistencia inc_tri (nolock) 
						ON inc_tri.id_inconsistencia = ic_tri.id_inconsistencia 
						and inc_tri.id_inconsistencia > 0
					LEFT JOIN inconsistencia inc_fval (nolock) 
						ON inc_fval.id_inconsistencia = ic_fval.id_inconsistencia 
						and inc_fval.id_inconsistencia > 0
					LEFT JOIN inconsistencia inc_fdig (nolock) 
						ON inc_fdig.id_inconsistencia = ic_fdig.id_inconsistencia 
						and inc_fdig.id_inconsistencia > 0
					LEFT JOIN inconsistencia inc_ftri (nolock) 
						ON inc_ftri.id_inconsistencia = ic_ftri.id_inconsistencia 
						and inc_ftri.id_inconsistencia > 0
				WHERE inf.id_enquadramento > 1 
					AND inf.id_inconsistencia > 0 
					AND inf.id_processo IN (select id_processo 
												from processo (nolock) 
												where id_processo_proximo IS NULL)
					AND CAST(inf.data AS DATE) >= @dataInicio 
					AND CAST(inf.data AS DATE) <= @dataFim
				GROUP BY
					inf.id_local           
				) AS sub4
		ON sub1.id_local = sub4.id_local
	LEFT JOIN (	SELECT
					inf.id_local,
					SUM(CASE WHEN (inf.id_enquadramento = 56732) THEN 1 ELSE 0 END) AS [56732],
					SUM(CASE WHEN (inf.id_enquadramento = 56900) THEN 1 ELSE 0 END) AS [56900],
					SUM(CASE WHEN (inf.id_enquadramento = 57030) THEN 1 ELSE 0 END) AS [57030],
					SUM(CASE WHEN (inf.id_enquadramento = 57461) THEN 1 ELSE 0 END) AS [57461],
					SUM(CASE WHEN (inf.id_enquadramento = 57462) THEN 1 ELSE 0 END) AS [57462],
					SUM(CASE WHEN (inf.id_enquadramento = 57463) THEN 1 ELSE 0 END) AS [57463],
					SUM(CASE WHEN (inf.id_enquadramento = 59910) THEN 1 ELSE 0 END) AS [59910],
					SUM(CASE WHEN (inf.id_enquadramento = 60411) THEN 1 ELSE 0 END) AS [60411],
					SUM(CASE WHEN (inf.id_enquadramento = 60412) THEN 1 ELSE 0 END) AS [60412],
					SUM(CASE WHEN (inf.id_enquadramento = 60503) THEN 1 ELSE 0 END) AS [60503],
					SUM(CASE WHEN (inf.id_enquadramento = 74550) THEN 1 ELSE 0 END) AS [74550],
					SUM(CASE WHEN (inf.id_enquadramento = 74630) THEN 1 ELSE 0 END) AS [74630],
					SUM(CASE WHEN (inf.id_enquadramento = 74710) THEN 1 ELSE 0 END) AS [74710],
					COUNT(*) AS [AUTUACOES]
				FROM infracao inf (nolock)
					INNER JOIN infracao_remessa ir (nolock)
						ON inf.id_infracao = ir.id_infracao
				WHERE	CAST(inf.data AS DATE) >= @dataInicio 
					AND CAST(inf.data AS DATE) <= @dataFim
				GROUP BY
					inf.id_local
				) AS sub6
		ON sub1.id_local = sub6.id_local
	ORDER BY
		sub1.serie_equipamento ASC
)






