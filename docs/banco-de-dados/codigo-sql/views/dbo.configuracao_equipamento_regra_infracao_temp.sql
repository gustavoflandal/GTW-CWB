
CREATE VIEW [dbo].[configuracao_equipamento_regra_infracao_temp]
AS
	SELECT lv.id_configuracao_equipamento,
		   lv.id_local,
		   lv.serie_equipamento,
		   lv.id_pista_regra,
		   eri.tipo,
		   eri.descricao_apait
	FROM   local_pista_vigente_temp lv
		   JOIN configuracao_equipamento_regra_infracao ceri
				ON  ceri.id_configuracao_equipamento = lv.id_configuracao_equipamento
					AND (ceri.id_pista IS NULL OR ceri.id_pista = lv.id_pista)
		   JOIN enquadramento_regra_infracao eri (NOLOCK)
				ON  eri.tipo = ceri.tipo
	WHERE  lv.desativado = 0
		   AND eri.id_enquadramento > 1
		   --AND lv.id_local = 6
	GROUP BY
		   lv.id_configuracao_equipamento,
		   lv.id_local,
		   lv.serie_equipamento,
		   lv.id_pista_regra,
		   eri.tipo,
		   eri.descricao_apait
