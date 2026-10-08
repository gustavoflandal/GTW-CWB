CREATE FUNCTION [dbo].[fcn_IndicadoresProcessamentoPrincipal_alt]
(
	@ATRASO INTEGER
)
RETURNS TABLE
AS

RETURN 
(
    -- DECLARE @ATRASO INTEGER = 8
    SELECT TOP(1)
	dias_atraso_atual
		  ,dias_atraso_reprovado
          ,dias_atraso_remessa
          ,dias_atraso_validacao
          ,total_atraso
          ,total_atraso_ant
          ,total_erro
          ,total_erro_ant
          ,dias_atraso_cad_isento
		  ,arquivos_verificados 
		  ,data_arquivos_cav
		  ,erros_remessa_automatico
		  ,erros_proc.erros_mes_atual
		  ,erros_proc.erros_mes_anterior
    FROM   painel_principal 
		   CROSS JOIN (
				SELECT TOP 1 * FROM painel_principal_erros ORDER BY data DESC
		   ) erros_proc
	ORDER BY data_adicionado DESC 
)
