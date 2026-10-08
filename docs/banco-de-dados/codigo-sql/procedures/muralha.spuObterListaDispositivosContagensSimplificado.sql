CREATE PROCEDURE [muralha].[spuObterListaDispositivosContagensSimplificado] @equipamentos ListaEquipamentos READONLY
AS
	--DECLARE @equipamentos ListaEquipamentos
	SET NOCOUNT ON

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
		   ISNULL(st.status_trafego, 0) AS status_trafego
	FROM   local_vigente l (NOLOCK)
		   JOIN @locais lf
				ON  lf.id_local = l.id_local
		   LEFT JOIN @status_equipamento se
				ON  se.id_local = l.id_local
		   LEFT JOIN @status_trafego st
				ON  st.id_local = l.id_local
	WHERE  l.desativado = 0
		   --AND l.id_local IN (1,2,3,4,5,6,7,8,9,10,15,20,28)
