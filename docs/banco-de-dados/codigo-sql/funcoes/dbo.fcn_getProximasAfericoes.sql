
CREATE FUNCTION [dbo].[fcn_getProximasAfericoes]()
RETURNS TABLE
AS
RETURN
(
	SELECT
		lv.id_local as id_local,
		lv.nome as nome_local,
		a.id_pista as id_pista,
		p.nome_pista as nome_pista,
		a.data as data_ultima_afericao,
		a.data_validade as data_validade
	FROM local_vigente lv (nolock)
		JOIN configuracao_equipamento_afericao a (nolock) 
			ON a.id_configuracao_equipamento = lv.id_configuracao_equipamento
		JOIN configuracao_equipamento_pista p (nolock) 
			ON	p.id_configuracao_equipamento = lv.id_configuracao_equipamento 
			AND p.id_pista = a.id_pista
	WHERE 
		data_validade BETWEEN getdate() and getdate() + 45
)



