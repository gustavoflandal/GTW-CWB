CREATE FUNCTION [dbo].[fcn_IndicadoresProcessamentoAgrupado]
(
	@data_ini           DATETIME
   ,@data_fim           DATETIME
   ,@max_dia_atraso     INT
)
RETURNS TABLE
AS

RETURN
(
	-- DECLARE @data_ini DATETIME = '2016-09-01', @data_fim DATETIME = '2016-09-30', @max_dia_atraso INT = 8
    SELECT ISNULL(info_atraso.imagem_dia_atraso, 0) AS imagem_dia_atraso
          ,ISNULL(info_erro.imagem_dia_erro, 0) AS imagem_dia_erro
    FROM   (
			   -- DECLARE @data_ini DATETIME = '2016-09-01', @data_fim DATETIME = '2016-09-30', @max_dia_atraso INT = 8
               SELECT SUM(DATEDIFF(DAY, DATEADD(DAY, @max_dia_atraso, inf.data), r.data)) AS imagem_dia_atraso
               FROM   infracao inf (NOLOCK)
                      JOIN infracao_remessa ir (NOLOCK)
                           ON  inf.id_infracao = ir.id_infracao
                      JOIN remessa r (NOLOCK)
                           ON  r.id_remessa = ir.id_remessa
               WHERE  inf.data BETWEEN @data_ini AND @data_fim
                      AND DATEDIFF(DAY, inf.data, r.data) > @max_dia_atraso
           )  AS info_atraso
          ,(
		  -- DECLARE @data_ini DATETIME = '2016-09-01', @data_fim DATETIME = '2016-09-30', @max_dia_atraso INT = 8
               SELECT COUNT(DISTINCT i.id_infracao) AS imagem_dia_erro
               FROM   infracao i (NOLOCK)
                      INNER JOIN infracao_remessa ir (NOLOCK)
                           ON  i.id_infracao = ir.id_infracao
                      INNER JOIN remessa r (NOLOCK)
                           ON  r.id_remessa = ir.id_remessa
                      INNER JOIN movimento_importacao mi (NOLOCK)
                           ON  r.id_movimento_arquivo = mi.id_movimento_arquivo
                               AND r.id_enquadramento = mi.id_enquadramento
                               AND ir.sequencia = mi.sequencia
                      LEFT JOIN infracao_processo ipc_l (NOLOCK)
                           ON  i.id_infracao = ipc_l.id_infracao
                               AND ipc_l.id_processo = 2
                               AND id_infracao_processo = (
                                       SELECT MAX(id_infracao_processo) AS id_infracao_processo
                                       FROM   infracao_processo ip (NOLOCK)
                                       WHERE  ip.id_infracao = ipc_l.id_infracao
                                   )
                      LEFT JOIN infracao_processo_digitacao ipd (NOLOCK)
                           ON  ipc_l.id_infracao_processo = ipd.id_infracao_processo
					  LEFT JOIN infracao_erro_desconsiderar infracao_desconsiderar (NOLOCK)
						   ON  infracao_desconsiderar.id_infracao = i.id_infracao
               WHERE  CAST(i.data AS DATE) BETWEEN CAST(@data_ini AS DATE) AND CAST(@data_fim AS DATE)
                      AND (
                              (
                                  (i.id_inconsistencia = 0 AND mi.id_inconsistencia <> 0)
                                  OR (mi.id_inconsistencia = 0 AND i.id_inconsistencia <> 0)
                              )
                              OR (
                                     mi.placa <> REPLICATE(' ', 7)
                                     AND i.placa IS NOT NULL 
                                     AND mi.placa <> i.placa
                                 )
                              OR (
                                     ipd.id_marca_cet IS NOT NULL
                                     AND mi.id_marca_cet <> ipd.id_marca_cet
                                 )
                          )
					   AND infracao_desconsiderar.id_infracao IS NULL
           )  AS info_erro
           
           
	/*
	* --> QUERY ORIGINAL
	* 
	SELECT 
		ISNULL(info_atraso.imagem_dia_atraso, 0) AS imagem_dia_atraso,
		ISNULL(info_erro.imagem_dia_erro, 0) AS imagem_dia_erro
	FROM (	SELECT 
				SUM(CASE WHEN DATEDIFF(day,dateadd(day,@max_dia_atraso,sa.data_imagens),
				sa.data_geracao) > 0 THEN (DATEDIFF(day,dateadd(day,@max_dia_atraso,sa.data_imagens),
				sa.data_geracao)*sa.total_imagens) ELSE 0 END) AS imagem_dia_atraso
			FROM 
				solicitacao_auditoria sa (nolock)
			WHERE
				sa.data_geracao BETWEEN @data_ini AND @data_fim
		) AS info_atraso, 
		(	SELECT 
				COUNT(i.id_infracao) AS imagem_dia_erro
			FROM infracao i  (NOLOCK)
				INNER JOIN infracao_processo_concluido ipc_lib  (NOLOCK) 
					ON ipc_lib.id_infracao = i.id_infracao AND ipc_lib.id_processo = 11
				INNER JOIN infracao_processo_concluido ipc_val  (NOLOCK) 
					ON ipc_val.id_infracao = i.id_infracao AND ipc_val.id_processo = 3
			WHERE	ipc_val.data_conclusao BETWEEN @data_ini AND @data_fim 
				AND	ipc_lib.id_inconsistencia <> ipc_val.id_inconsistencia
		) AS info_erro 
	*/
	
	--SELECT * FROM fcn_IndicadoresProcessamentoPrincipal_alt (8)

)

