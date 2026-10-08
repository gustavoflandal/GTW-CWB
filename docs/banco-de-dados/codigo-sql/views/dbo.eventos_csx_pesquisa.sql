
CREATE VIEW [dbo].[eventos_csx_pesquisa]
AS
SELECT
	ec.id,
	dpro.proprietario,
	ec.data_hora,
	ec.id_categoria,
	ec.id_evento, 
    ec.mensagem,
    ec.id_prioridade,
    ec.id_nivel,
    ca.categoria, 
    dp.prioridade,
    de.evento,
    dn.nivel,
    ec.usuario
FROM eventos_csx ec (nolock)
	LEFT JOIN eventos_csx_desc_categoria ca (nolock)
		ON ec.id_categoria = ca.id_categoria
	LEFT JOIN eventos_csx_desc_evento de (nolock)
		ON ec.id_evento = de.id_evento
	LEFT JOIN eventos_csx_desc_nivel dn (nolock)
		ON ec.id_nivel = dn.id_nivel
	LEFT JOIN eventos_csx_desc_prioridade dp (nolock)
		ON ec.id_prioridade = dp.id_prioridade
	LEFT JOIN eventos_csx_desc_proprietario dpro (nolock)
		ON ec.id_proprietario = dpro.id_proprietario
--WHERE ec.id_evento IN 
--(11, 13, 14, 18, 23, 27, 32, 42, 47, 48, 55, 56, 62, 15, 16, 37, 9, 10, 8, 7, 10, 9, 14, 38, 45, 2001, 2002, 5, 21, 1004, 1021, 15, 16)
--UNION
--SELECT
--	el.id,
--	el.proprietario,
--	el.data_hora,
--	el.id_categoria,
--	el.id_evento, 
--    el.mensagem,
--    el.id_prioridade,
--    el.id_nivel,
--    ca.categoria, 
--    dp.prioridade,
--    de.evento,
--    dn.nivel,
--    el.usuario
--FROM eventos_csx_legacy el (nolock)
--	LEFT JOIN eventos_csx_desc_categoria ca (nolock)
--		ON el.id_categoria = ca.id_categoria
--	LEFT JOIN eventos_csx_desc_evento de (nolock)
--		ON el.id_evento = de.id_evento
--	LEFT JOIN eventos_csx_desc_nivel dn (nolock)
--		ON el.id_nivel = dn.id_nivel
--	LEFT JOIN eventos_csx_desc_prioridade dp (nolock)
--		ON el.id_prioridade = dp.id_prioridade




