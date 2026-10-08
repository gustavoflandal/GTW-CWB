
CREATE FUNCTION [dbo].[fcn_InfracoesSemEscala] 
(	
)
RETURNS TABLE 
AS
RETURN 
(
	--SELECT GETDATE() data, 333 id_local, 'teste123' descricao_local, 42 numero_imagens
	SELECT CAST(i.data AS DATE) data, i.id_local, lv.nome AS descricao_local, COUNT(*) AS numero_imagens FROM infracao i (NOLOCK) 
	JOIN local_vigente lv (NOLOCK) ON i.id_local = lv.id_local 
	JOIN veiculo v (NOLOCK) ON i.id_veiculo = v.id_veiculo 
	WHERE 
	i.id_processo = 20 
	AND espera = 1 
	AND i.id_enquadramento > 1 
	AND v.codigo_prodam IS NOT NULL 
	AND NOT EXISTS(SELECT 1 FROM agenda_estatico ae (NOLOCK) JOIN agenda_estatico_item aei (NOLOCK) ON ae.id_agenda_estatico = aei.id_agenda_estatico WHERE aei.status = 1 AND ae.id_local = i.id_local AND CAST(i.data AS DATE) = aei.data_operacao AND CAST(i.data AS TIME) BETWEEN aei.hora_inicio AND aei.hora_fim)
	GROUP BY CAST(i.data AS DATE), i.id_local, lv.nome
)
