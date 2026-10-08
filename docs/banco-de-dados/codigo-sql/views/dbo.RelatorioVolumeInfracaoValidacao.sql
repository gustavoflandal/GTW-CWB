
CREATE VIEW [dbo].[RelatorioVolumeInfracaoValidacao]
AS
SELECT
	R.ID_Gerado as id_relatorio,
	R.Local as id_local, 
	L.Nome as desc_local,
	sum(v00_40) as v00_40,
	sum(v41_48) as v41_48,
	sum(v49_67) as v49_67,
	sum(v68_79) as v68_79,
	sum(v80_97) as v80_97,
	sum(v98_999) as v98_999,
	sum(v68_77) as v68_77,
	sum(v78_91) as v78_91,
	sum(v92_113) as v92_113,
	sum(v114_999) as v114_999,
	(sum(v00_40)+sum(v41_48)+sum(v49_67)+sum(v68_79)+sum(v80_97)+sum(v98_999)+sum(v68_77)+sum(v78_91)+sum(v92_113)+sum(v114_999)) as trafegoTotal,
	sum(qt_Infratores) as infratores,
	sum(qt_RejeitadoPT) as rejeitadoPT,
	sum(qt_RejeitadoPNT) as rejeitadoPNT,
	(sum(qt_Infratores)-(sum(qt_rejeitadoPT)+sum(qt_rejeitadoPNT))) as totalPValidacao,
	( cast(( sum(qt_Infratores)-sum(qt_rejeitadoPT))as decimal(10,1)) / NULLIF(sum(qt_infratores),0)) as aprovRejeicoes ,
	sum(qt_InvalidadoPT) as invalidadoPT,
	sum(qt_InvalidadoPNT) as invalidadoPNT,
--	sum(qt_Veic_Oficiais) as veiculosOficiais,
	sum(qt_Validos) as valido,
	(cast(sum(qt_Validos)as decimal(10,1)) / NULLIF(sum(qt_infratores),0)) as aprovBruto,
	( cast( (sum(qt_Validos)+ sum(qt_InvalidadoPNT)+ sum(qt_RejeitadoPNT))as decimal(10,1))/NULLIF(sum(qt_infratores),0) ) as indiceOficial,
--	sum(qt_Infr_Red) as infracaoAvancoSinal
	vlc.velocidade_limite as vl_local
FROM Relatorios_Trafego_Pista R (nolock)
	LEFT JOIN local_vigente L (nolock) 
		ON L.id_local = R.Local
	LEFT JOIN (	select top 1
					ceri.id_configuracao_equipamento as id_configuracao_equipamento,
					ceri.velocidade_limite as velocidade_limite
				from configuracao_equipamento_regra_infracao ceri (nolock) 
				where ceri.ativo = 1 
					and tipo = 'VL'
				order by ceri.velocidade_limite desc
				) as vlc 
		ON vlc.id_configuracao_equipamento = L.id_configuracao_equipamento
	--LEFT JOIN Local_Enquadramentos E 
	--	ON E.Local = L.Local
GROUP BY R.ID_Gerado, R.Local, L.Nome, vlc.velocidade_limite


