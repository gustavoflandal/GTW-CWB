CREATE FUNCTION [dbo].[fcn_espelho_processamento](
	@id_processo int, @data_base datetime
)
RETURNS TABLE
AS
RETURN (
	select 
		cast(i.data as date) as data,
		count(ipa1.id_infracao) as consistente,
		count(ipa2.id_infracao) as inconsistente 
	from infracao i (nolock)
		INNER JOIN infracao_processo_concluido ipa (nolock) 
			on ipa.id_infracao = i.id_infracao 
			and ipa.id_processo IN (select id_processo_origem 
										from processo_ligacao (nolock) 
										where id_processo_destino = @id_processo)
		LEFT JOIN infracao_processo_concluido ipa1 (nolock) 
			on ipa1.id_infracao_processo_concluido = ipa.id_infracao_processo_concluido 
			and ipa1.id_inconsistencia = 0
		LEFT JOIN infracao_processo_concluido ipa2 (nolock)
			on ipa2.id_infracao_processo_concluido = ipa.id_infracao_processo_concluido 
			and ipa2.id_inconsistencia > 0
		LEFT JOIN infracao_processo_concluido ipb (nolock) 
			on ipb.id_infracao = i.id_infracao 
			and ipb.id_processo = @id_processo 
	where	ipa.data_conclusao <= @data_base 
		and (ipb.data_conclusao > @data_base /* or ipb.id_infracao is null*/)
	group by cast(i.data as date)
)


