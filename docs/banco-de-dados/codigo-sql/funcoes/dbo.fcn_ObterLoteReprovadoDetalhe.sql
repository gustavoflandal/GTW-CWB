
-- SELECT * FROM lote_reprovado

CREATE FUNCTION [dbo].[fcn_ObterLoteReprovadoDetalhe](@id_remessa INT) 
RETURNS TABLE 
AS
RETURN 
(
	SELECT 

	ir.id_infracao, 
	lrd.placa_cai, lrd.placa_cav, 
	STR(lrd.id_inconsistencia_cai) + ' - ' + inc_cai.descricao AS inconsistencia_cai,
	STR(lrd.id_inconsistencia_cav) + ' - ' + inc_cav.descricao AS inconsistencia_cav,
	COALESCE(marca_cai.descricao, 'N/D') marca_cai, COALESCE(marca_cav.descricao, 'N/D') marca_cav,
	CASE WHEN lrd.erro_obliteracao = 0 THEN 'Não' ELSE 'Sim' END AS erro_obliteracao, 
	STR(lrd.cod_agente) + ' - ' + COALESCE(su.nome, 'N/D') AS nome_agente

	FROM lote_reprovado lr (NOLOCK)
	JOIN lote_reprovado_detalhe lrd (NOLOCK) ON lr.id_remessa = lrd.id_remessa
	JOIN infracao_remessa ir (NOLOCK) ON lr.id_remessa = ir.id_remessa AND lrd.sequencia = ir.sequencia
	LEFT JOIN sis_usuario su (NOLOCK) ON lrd.cod_agente = su.cod_agente
	JOIN inconsistencia inc_cai (NOLOCK) ON lrd.id_inconsistencia_cai = inc_cai.id_inconsistencia
	JOIN inconsistencia inc_cav (NOLOCK) ON lrd.id_inconsistencia_cav = inc_cav.id_inconsistencia
	LEFT JOIN cad_marca_cet marca_cai (NOLOCK) ON lrd.id_marca_cet_cai = marca_cai.id_marca_cet
	LEFT JOIN cad_marca_cet marca_cav (NOLOCK) ON lrd.id_marca_cet_cav = marca_cav.id_marca_cet

	WHERE lr.id_remessa = @id_remessa 
)
