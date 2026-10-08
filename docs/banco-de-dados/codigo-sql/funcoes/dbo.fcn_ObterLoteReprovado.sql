
CREATE FUNCTION [dbo].[fcn_ObterLoteReprovado]()
RETURNS TABLE
AS
RETURN
	(

		SELECT lr.id_remessa
			  ,lr.id_remessa_cav
			  ,lr.tipo
			  ,lr.codigo_externo
			  ,lr.data_inicial
			  ,lr.revisao
			  ,lr.data_processo
			  ,lr.data_atualizacao
			  ,COALESCE(lr.mensagem,'N/D') mensagem
			  ,lr.ativo
			  ,DATEDIFF(DAY, CAST(lr.data_inicial AS DATE), CAST(GETDATE() AS DATE)) atraso
			  ,r.total_infracao
			  ,CASE WHEN lr.data_processo IS NULL THEN 0 ELSE DATEDIFF(DAY, CAST(lr.data_processo AS DATE), CAST(GETDATE() AS DATE)) END AS atraso_processo
			  ,CASE WHEN processo.id_processo IN (3,4) AND (lr.mensagem IS NULL OR detalhe.id_remessa IS NOT NULL) THEN 1 ELSE 0 END AS reposicionar
			  ,CASE WHEN detalhe.id_remessa IS NULL THEN 0 ELSE 1 END AS possui_detalhe			  
		FROM   lote_reprovado lr (NOLOCK)
 			   INNER JOIN remessa r (NOLOCK)
		 			ON  r.id_remessa = lr.id_remessa
			   INNER JOIN (
		 			   SELECT r_aux.id_remessa
		 	   				 ,MAX(i_aux.id_processo) AS id_processo
		  			   FROM   remessa r_aux (NOLOCK)
		 	   				  INNER JOIN infracao_remessa ir_aux (NOLOCK)
		 	   					   ON  ir_aux.id_remessa = r_aux.id_remessa
		 	   				  INNER JOIN infracao i_aux (NOLOCK)
		 	   					   ON  i_aux.id_infracao = ir_aux.id_infracao
		 			   GROUP BY
		 	   				  r_aux.id_remessa
			   ) AS processo
		 			ON  processo.id_remessa = r.id_remessa
			   LEFT JOIN (
						SELECT id_remessa FROM lote_reprovado_detalhe (NOLOCK) GROUP BY id_remessa
				) AS detalhe ON lr.id_remessa = detalhe.id_remessa
		WHERE  lr.ativo = 1 AND CAST(lr.data_atualizacao AS DATE) = CAST(GETDATE() AS DATE)
 			   --AND processo.id_processo = 3

	)



