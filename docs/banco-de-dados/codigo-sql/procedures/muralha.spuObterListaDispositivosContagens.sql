CREATE PROCEDURE [muralha].[spuObterListaDispositivosContagens] @equipamentos ListaEquipamentos READONLY
AS
	--DECLARE @equipamentos ListaEquipamentos
	SET NOCOUNT ON

	DECLARE @contagem_veiculos AS TABLE (id_local INT INDEX IX1, qtde_ultimos_10_dias INT, qtde_ultima_hora INT, qtde_ultimos_15_min INT, vel_media_ultimos_15_min INT)
	DECLARE @infracoes_registradas AS TABLE (id_local INT INDEX IX1, infracoes_registradas INT)
	DECLARE @status_equipamento AS TABLE (id_local INT INDEX IX1, conectado BIT)
	DECLARE @status_trafego AS TABLE (id_local INT INDEX IX1, dados_trafego VARCHAR(240), status_trafego TINYINT)
	DECLARE @locais AS TABLE (id_local INT INDEX IX1)

	DECLARE @possui_filtro BIT = (SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM @equipamentos)
	
	--CREATE TABLE #locais (id_local INT)
	INSERT INTO @locais
	SELECT id_local
	FROM   local_vigente
	WHERE  desativado = 0
		   AND (
					(@possui_filtro = 1 AND id_local IN (SELECT id_local FROM @equipamentos))
					OR
					(@possui_filtro = 0)
			   )

	INSERT INTO @contagem_veiculos
	SELECT id_local,
		   COUNT(*) AS qtde_ultimos_10_dias,
		   SUM(CASE WHEN vtr.data > DATEADD(HOUR, -1, GETDATE()) THEN 1 ELSE 0 END) AS qtde_ultima_hora,
		   SUM(CASE WHEN vtr.data > DATEADD(MINUTE, -15, GETDATE()) THEN 1 ELSE 0 END) AS qtde_ultimos_15_min,
		   AVG(CASE WHEN vtr.data > DATEADD(MINUTE, -15, GETDATE()) AND vtr.velocidade BETWEEN 5 AND 200 THEN vtr.velocidade ELSE NULL END) AS vel_media_ultimos_15_min
	FROM   muralha.veiculo_tempo_real vtr (NOLOCK)
	WHERE  vtr.data BETWEEN DATEADD(DAY, -10, GETDATE()) AND GETDATE()
	GROUP BY
		   id_local

	--SELECT * FROM @contagem_veiculos

	INSERT INTO @infracoes_registradas
	SELECT id_local,
		   COUNT(*) AS infracoes_registradas
	FROM   infracao
	--WHERE  id_enquadramento IN (74550,74630,74710)
	WHERE  id_enquadramento > 1
	GROUP BY
		   id_local

	--SELECT * FROM @infracoes_registradas

	INSERT INTO @status_trafego
	EXEC prd_getEstatisticaTodosCompleto

	UPDATE @status_trafego SET status_trafego = 2 WHERE status_trafego = 3
	UPDATE @status_trafego SET status_trafego = 3 WHERE status_trafego = 4

	--SELECT * FROM @status_trafego

	INSERT INTO @status_equipamento
	SELECT id_local,
		   CASE WHEN status = 1 THEN 1 ELSE 0 END AS conectado
	FROM   local_status_conexao

	SELECT l.id_local,
		   l.sequencia_local,
		   l.id_configuracao_equipamento,
		   l.serie_equipamento,
		   l.nome,
		   l.codigos_equipamentos,
		   l.em_operacao,
		   l.posicao_lat,
		   l.posicao_lon,
		   ISNULL(se.conectado,0) AS conectado,
		   ISNULL(st.status_trafego, 0) AS status_trafego,
		   ISNULL(ct.qtde_ultimos_10_dias, 0) AS qtde_ultimos_10_dias,
		   ISNULL(ct.qtde_ultima_hora, 0) AS qtde_ultima_hora,
		   ISNULL(ct.qtde_ultimos_15_min, 0) AS qtde_ultimos_15_min,
		   ISNULL(ct.vel_media_ultimos_15_min, 0) AS vel_media_ultimos_15_min,
		   ISNULL(ir.infracoes_registradas, 0) AS infracoes_registradas,
		   muralha.fcn_getDispositivosContagensFluxoDiarioJson(l.id_local) AS contagens_fluxo_diario
	FROM   local_vigente l (NOLOCK)
		   JOIN @locais lf
				ON  lf.id_local = l.id_local
		   LEFT JOIN @contagem_veiculos ct
				ON  ct.id_local = l.id_local
		   LEFT JOIN @infracoes_registradas ir
				ON  ir.id_local = l.id_local
		   LEFT JOIN @status_equipamento se
				ON  se.id_local = l.id_local
		   LEFT JOIN @status_trafego st
				ON  st.id_local = l.id_local
	WHERE  l.desativado = 0
