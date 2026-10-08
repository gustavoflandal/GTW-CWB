CREATE PROCEDURE [dbo].[spu_gerencia_contrato_atualiza_datas]
AS
	DECLARE @temp TABLE (id_alerta INT, id_local INT, id_pista INT, valor DATETIME NULL, alerta BIT DEFAULT 0)

	--7	Sem Arquivos                                      
	INSERT INTO @temp 
		(id_alerta, id_local, id_pista, valor)           
	SELECT
		7,
		id_local,
		NULL,
		ultArq.data_ultimo_arquivo
	FROM 
		painel_ultimo_arquivo ultArq (nolock)

	--8	Sem Imagens                                       
	INSERT INTO @temp 
		(id_alerta, id_local, id_pista, valor)           
	SELECT
		8,
		id_local,
		id_pista,
		ui.data_ultima_infracao
	FROM 
		painel_ultima_infracao ui (nolock)		

	--9	Desconectado                                      	            
	INSERT INTO @temp (id_alerta, id_local, id_pista, valor)           
	SELECT
		9,
		id_local,
		NULL,
		ld.data_ultima_desconexao
	FROM painel_local_desconectado ld (nolock)
	
	--12 Sem carregar agenda
	INSERT INTO @temp (id_alerta, id_local, id_pista, valor)           
	SELECT
		12,
		id_local,
		NULL,
		ultAgenda.data_ultima_agenda
	FROM local_vigente lv (nolock)
		INNER JOIN painel_ultima_camera_carregada AS ultAgenda (nolock)
			ON ultAgenda.proprietario = CAST(lv.serie_equipamento AS CHAR(7))

	--6 Sem Sincronizar Relógio                           
	INSERT INTO @temp (id_alerta, id_local, id_pista, valor)           
	SELECT
		6,
		id_local,
		NULL,
		ultSincHorario.ultima_data
	FROM local_vigente lv (nolock)
		INNER JOIN fcn_maxDataHoraEventoPorProprietario(38) AS ultSincHorario
			ON ultSincHorario.proprietario = CAST(lv.serie_equipamento AS CHAR(7))

	--13 Sem atualizar BD
	INSERT INTO @temp (id_alerta, id_local, id_pista, valor)           
	SELECT
		13,
		id_local,
		NULL,
		ultUpdateDB.ultima_data
	FROM local_vigente lv (nolock)
		INNER JOIN fcn_maxDataHoraEventoPorProprietario(54) AS ultUpdateDB
			ON ultUpdateDB.proprietario = CAST(lv.serie_equipamento AS CHAR(7))
	WHERE ultUpdateDB.proprietario NOT IN ( -- somente os equip. que fazem busca no banco de dados 
											SELECT 
												CAST(serie_equipamento AS CHAR(7)) 
											FROM 
												local_parametro_adicional_data_search (nolock)
											WHERE 
												database_search_ativo = 0)



	-- ultimo evento geral
	--5 Sem Log
	INSERT INTO @temp (id_alerta, id_local, id_pista, valor)           
	SELECT
		5,
		id_local,
		NULL,
		ultEvento.ultima_data
	FROM local_vigente lv 
		INNER JOIN (SELECT
						ep.proprietario
						,MAX(e.data_hora) as [ultima_data]
					FROM eventos_csx e (nolock)
						INNER JOIN eventos_csx_desc_proprietario ep (nolock)
							ON ep.id_proprietario = e.id_proprietario
					WHERE
						data_hora < GETDATE()					
					GROUP BY
						ep.proprietario		
				) AS ultEvento 
			ON ultEvento.proprietario = CAST(lv.serie_equipamento AS CHAR(7))
			
	-- ajusta os alertas
	UPDATE @temp 
	SET alerta = 1 
	WHERE	id_alerta = 7 
		AND DATEADD(HOUR,12, valor) < GETDATE()
	
	UPDATE @temp 
	SET alerta = 1 
	WHERE	id_alerta = 8 
		AND DATEADD(HOUR,12, valor) < GETDATE()

	UPDATE @temp 
	SET alerta = 1 
	WHERE	id_alerta = 9 
		AND DATEADD(HOUR,12, valor) < GETDATE()

	UPDATE @temp 
	SET alerta = 1 
	WHERE	id_alerta = 12 
		AND DATEADD(DAY,1, valor) < GETDATE()

	UPDATE @temp 
	SET alerta = 1 
	WHERE	id_alerta = 6 
		AND DATEADD(DAY,1, valor) < GETDATE()

	UPDATE @temp 
	SET alerta = 1 
	WHERE	id_alerta = 13 
		and DATEADD(DAY,7, valor) < GETDATE()

	UPDATE @temp 
	SET alerta = 1 
	WHERE	id_alerta = 5 
		and DATEADD(DAY,1, valor) < GETDATE()

	INSERT INTO gerencia_contrato_alerta with (rowlock) (
        id_alerta,
		serie_equipamento,
		id_pista,
        valor,
        alerta)
	SELECT
		t.id_alerta,
		lv.serie_equipamento,
		t.id_pista,
		LTRIM(RTRIM(STR(DATEDIFF(DAY, t.valor, GETDATE())))) + 'd ' +  
				LTRIM(RTRIM(STR(DATEDIFF(HOUR, t.valor, GETDATE()) % 24))) + 'h',
		t.alerta
	FROM @temp t
		INNER JOIN local_vigente lv (nolock)
			ON lv.id_local = t.id_local



