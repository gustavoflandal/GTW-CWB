
CREATE VIEW [dbo].[gerencia_contrato_painel]
AS
select 
	lv.serie_equipamento
	,CAST(gca.id_pista AS TINYINT) as id_pista
	,CAST(cep.cod_pista_alternativo AS TINYINT) as faixa
	,COALESCE( cep.nome_pista, lv.nome) as descr_local
	,gcca.nome
	,gca.valor
	,CAST(CASE WHEN gca.alerta = 1 THEN 1 ELSE 0 END AS INT) AS alerta
from local_vigente lv (nolock) 
	left join gerencia_contrato_alerta gca (nolock)
		on gca.serie_equipamento = lv.serie_equipamento 
	left join configuracao_equipamento_pista cep (nolock)
		on	cep.id_configuracao_equipamento = lv.id_configuracao_equipamento 
		and cep.id_pista = gca.id_pista
	left join gerencia_contrato_cad_alerta gcca (nolock)
		on gcca.id_alerta = gca.id_alerta			
WHERE alerta = 1


