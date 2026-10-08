
CREATE VIEW [dbo].[RelatorioVolumeInfracaoValidacaoPista]
AS
-- Busca Relatorio Trafego por Pista *****************************
SELECT 
	R.ID_Gerado as idGerado,
	R.Local as codigoLocal, 
	L.Nome as descrLocal,
	pista as pista,
	v00_40,
	v41_48,
	v49_67,
	v68_79,
	v80_97,
	v98_999,
	v68_77,
	v78_91,
	v92_113,
	v114_999,
	(v00_40+v41_48+v49_67+v68_79+v80_97+v98_999+v68_77+v78_91+v92_113+v114_999) as trafegoTotal,
	qt_Infratores as infratores,
	qt_RejeitadoPT as rejeitadoPT,
	qt_RejeitadoPNT as rejeitadoPNT,
	(qt_Infratores-(qt_rejeitadoPT+qt_rejeitadoPNT)) as totalPValidacao,
	( cast(( qt_Infratores - qt_rejeitadoPT)as decimal(10,1)) / NULLIF(qt_infratores,0)) as aprovRejeicoes ,
	qt_InvalidadoPT as invalidadoPT,
	qt_InvalidadoPNT as invalidadoPNT,
--	qt_Veic_Oficiais,
	qt_Validos as valido,
	(cast((qt_Validos)as decimal(10,1)) / NULLIF((qt_infratores),0)) as aprovBruto,
	( cast( (qt_Validos + qt_InvalidadoPNT + qt_RejeitadoPNT) as decimal( 10 , 1 ) )/NULLIF( qt_infratores ,0 ))  as indiceOficial,
--	qt_Infr_Red as infracaoAvancoSinal
	vlc.velocidade_limite as velLimLocal
FROM Relatorios_Trafego_Pista R (nolock)
	LEFT JOIN local_vigente L 
		ON L.id_local = R.Local
	LEFT JOIN (	select top 1
					ceri.id_configuracao_equipamento as id_configuracao_equipamento,
					ceri.velocidade_limite as velocidade_limite
				from configuracao_equipamento_regra_infracao ceri 
				where	ceri.ativo = 1 
					and tipo = 'VL'
				order by ceri.velocidade_limite desc
				) as vlc 
		ON vlc.id_configuracao_equipamento = L.id_configuracao_equipamento
	--LEFT JOIN Local_Enquadramentos E 
	--	ON E.Local = L.Local
--GROUP BY
	--R.ID_Gerado,R.Local, L.Nome, vlc.velocidade_limite, pista


