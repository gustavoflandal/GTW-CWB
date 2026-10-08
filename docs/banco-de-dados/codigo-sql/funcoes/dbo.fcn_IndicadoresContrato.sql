CREATE FUNCTION [dbo].[fcn_IndicadoresContrato]
  ( @data_ini datetime,
    @data_fim datetime )
RETURNS TABLE
AS
RETURN
(
	SELECT 
		SUM(indicadores_fx_dia.total_fx_dia) AS total_fx_dia, 
		SUM(indicadores_fx_dia.total_fx_soh_metro_dia) AS total_fx_soh_metro_dia, 
		SUM(indicadores_fx_dia.total_fx_soh_metro_ok_dia) AS total_fx_soh_metro_ok_dia,
		SUM(indicadores_fx_dia.total_fx_soh_nmetro_dia) AS total_fx_soh_nmetro_dia,
		SUM(indicadores_fx_dia.total_fx_soh_nmetro_ok_dia) AS total_fx_soh_nmetro_ok_dia,
		SUM(indicadores_fx_dia.total_fx_metro_nmetro_dia) AS total_fx_metro_nmetro_dia, 
		SUM(indicadores_fx_dia.total_fx_metro_nmetro_ok_dia) AS total_fx_metro_nmetro_ok_dia,
		SUM(indicadores_fx_dia.total_fx_excl_dia) AS total_fx_excl_dia, 
		SUM(indicadores_fx_dia.total_fx_cext_dia) AS total_fx_cext_dia,
		SUM(indicadores_pr_dia.imagem_dia_atraso) AS imagem_dia_atraso,
		SUM(indicadores_pr_dia.imagem_dia_erro) AS imagem_dia_erro
		--indicadores_fx_dia.data
	FROM (	SELECT 
				COUNT(ifx.serie_equipamento) as total_fx_dia, 
				SUM(CASE WHEN ifx.eh_metro = 1 AND ifx.eh_nmetro = 0 THEN 1 END) as total_fx_soh_metro_dia, 
				SUM(CASE WHEN ifx.eh_metro = 1 AND ifx.eh_nmetro = 0 THEN ifx.metro_ok  END) as total_fx_soh_metro_ok_dia,
				SUM(CASE WHEN ifx.eh_nmetro = 1 AND ifx.eh_metro = 0  THEN 1 END) as total_fx_soh_nmetro_dia,
				SUM(CASE WHEN ifx.eh_nmetro = 1 AND ifx.eh_metro = 0 THEN ifx.nmetro_ok END) as total_fx_soh_nmetro_ok_dia,
				SUM(CASE WHEN ifx.eh_metro = 1 AND ifx.eh_nmetro = 1 THEN 1 END) as total_fx_metro_nmetro_dia, 
				SUM(CASE WHEN ifx.eh_metro = 1 AND ifx.eh_nmetro = 1 THEN ifx.metro_ok & ifx.nmetro_ok  END) as total_fx_metro_nmetro_ok_dia,
				SUM(CASE WHEN ifx.eh_excl = 1 THEN 1 END) as total_fx_excl_dia, 
				SUM(COALESCE(ifx.motivo_ext_energia,0) | COALESCE(ifx.motivo_ext_vandalismo,0) | COALESCE(ifx.motivo_ext_pavimento,0)) as total_fx_cext_dia,
				ifx.data
			FROM 
				fcn_IndicadoresFaixa(@data_ini, @data_fim) ifx 
			GROUP BY 
				ifx.data
		) as indicadores_fx_dia
		INNER JOIN fcn_IndicadoresProcessamento(@data_ini, @data_fim, 12) indicadores_pr_dia 
			ON indicadores_pr_dia.data = indicadores_fx_dia.data
)




