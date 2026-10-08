CREATE PROCEDURE [dbo].[prd_getEstatisticaTodosCompleto]
AS
	SET NOCOUNT ON;  

	DECLARE @dataFim    DATETIME = GETDATE()  
	DECLARE @dataInicio DATETIME = GETDATE() - '01:00:00'  
  
	DECLARE @velocidade_padrao INT = 50
	DECLARE @vel_regulamentada TABLE (id_local INT NOT NULL, id_pista INT NOT NULL, velocidade INT NULL);  
	DECLARE @parte0 TABLE (id_local INT NOT NULL, id_pista INT NOT NULL, classificacao CHAR(1) NOT NULL, veiculos INT NOT NULL);  
	DECLARE @parte1 TABLE (id_local INT NOT NULL, id_pista INT NOT NULL, classificacao CHAR(1) NOT NULL, veiculos INT NOT NULL);  
	DECLARE @parte2 TABLE (id_local INT NOT NULL, id_pista INT NOT NULL, C INT NOT NULL, M INT NOT NULL, O INT NOT NULL, P INT NOT NULL);  
	DECLARE @parte3 TABLE (id_local INT NOT NULL, id_pista INT NOT NULL, velocidade INT NOT NULL);  
	DECLARE @parte4 TABLE (id_local INT NOT NULL, id_pista INT NOT NULL, velocidade INT NOT NULL, mph FLOAT NOT NULL);  
	DECLARE @parte5 TABLE (id_local INT NOT NULL, id_pista INT NOT NULL, situacao INT NOT NULL);  
	DECLARE @parte6 TABLE (id_local INT NOT NULL, pista_dupla BIT NOT NULL, densidade FLOAT NULL, ats FLOAT NULL);  
	DECLARE @parte7 TABLE (id_local INT NOT NULL, volume_c INT NOT NULL, volume_d INT NOT NULL, velocidade_via INT NOT NULL);  
	DECLARE @parte8 TABLE (id_local INT NOT NULL, ats_c FLOAT NULL, ats_d FLOAT NULL);  
	DECLARE @parte9 TABLE (id_local INT NOT NULL, id_pista INT NOT NULL, C INT NOT NULL, M INT NOT NULL, O INT NOT NULL, P INT NOT NULL, T INT NOT NULL, V INT NOT NULL, S INT NOT NULL);  
	DECLARE @parte10 TABLE (id_local INT NOT NULL, dados_trafego VARCHAR(240), S INT NOT NULL);  
  

	INSERT INTO @vel_regulamentada
	SELECT lpv.id_local, lpv.id_pista, COALESCE(ceri.velocidade_limite, @velocidade_padrao) AS velocidade
	FROM   local_pista_vigente lpv
		   LEFT JOIN configuracao_equipamento_regra_infracao ceri
				ON  ceri.id_configuracao_equipamento = lpv.id_configuracao_equipamento
					AND (ceri.id_pista IS NULL OR ceri.id_pista = lpv.id_pista)
					AND ceri.tipo = 'VL'
	GROUP BY
		   lpv.id_local, lpv.id_pista, ceri.velocidade_limite

	--SELECT * FROM @vel_regulamentada

	INSERT INTO @parte0  
	-- DECLARE @dataFim DATETIME = GETDATE(), @dataInicio DATETIME = GETDATE() - '01:00:00'  
	SELECT etr.id_local,
		   etr.id_pista,
		   etr.classificacao,
		   COUNT(*) AS veiculos  
	FROM   muralha.veiculo_tempo_real etr (NOLOCK)  
	WHERE  etr.data BETWEEN @dataInicio AND @dataFim  
	GROUP BY
		   etr.id_local,
		   etr.id_pista,
		   etr.classificacao  
  
	--SELECT * FROM @parte0  
  
	INSERT INTO @parte1  
	SELECT lv.id_local,
		   lv.id_pista,
		   cv.id_classe_tr AS classificacao,
		   COALESCE(SUM(etr.veiculos),0) AS veiculos  
	--SELECT *  
	FROM   local_pista_vigente lv (NOLOCK)  
		   CROSS JOIN classe_veiculo cv (NOLOCK)  
		   LEFT JOIN @parte0 etr
				ON  lv.id_local = etr.id_local
					AND lv.id_pista = etr.id_pista
					AND cv.id_classe = etr.classificacao  
	GROUP BY
		   lv.id_local,
		   lv.id_pista,
		   cv.id_classe_tr  
  
	--SELECT * FROM @parte1  
  
	INSERT INTO @parte2  
	SELECT p.id_local,
		   p.id_pista,
		   p.C,
		   p.M,
		   p.O,
		   p.P  
	FROM   @parte1 AS sub1  
	PIVOT  (
				SUM(sub1.veiculos)
				FOR sub1.classificacao IN ([C],[M],[O],[P])
		   ) AS p  
  
	--SELECT * FROM @parte2
  
	INSERT INTO @parte3  
	-- DECLARE @dataInicio DATETIME, @dataFim DATETIME  
	SELECT id_local,
		   id_pista,
		   AVG(velocidade) AS velocidade  
	FROM   muralha.veiculo_tempo_real etr (NOLOCK)  
	WHERE  etr.data BETWEEN @dataInicio AND @dataFim
		   AND etr.velocidade BETWEEN 5 AND 200  
	GROUP BY
		   id_local,
		   id_pista  
  
	--SELECT * FROM @parte3  
  
	INSERT INTO @parte4  
	SELECT lv.id_local,
		   lv.id_pista,
		   COALESCE(sub1.velocidade,0) AS velocidade,
		   CONVERT(FLOAT, COALESCE(sub1.velocidade,0)) * 0.621371 AS mph  
	FROM   local_pista_vigente lv (NOLOCK)  
		   LEFT JOIN @parte3 AS sub1
				ON  lv.id_local = sub1.id_local
					AND lv.id_pista = sub1.id_pista  
  
	--SELECT * FROM @parte4  
  
	INSERT INTO @parte6  
	SELECT lv.id_local,
		   tp.pista_dupla,
		   NULL,
		   NULL  
	FROM   local_vigente lv (NOLOCK)  
		   JOIN v_local_tipo_pista tp
				ON  lv.id_local = tp.id_local  
  
	UPDATE @parte6
	SET    densidade = sub1.densidade  
	FROM   (  
			   SELECT sub1.id_local,
					  CASE WHEN sub2.mph > 0 THEN CONVERT(FLOAT, (sub1.P + sub1.C + sub1.O + sub1.M)) / sub2.mph
						   ELSE 0 END AS densidade  
			   FROM   (
							SELECT p2.id_local,
								   SUM(p2.P) AS P,
								   SUM(p2.C) AS C,
								   SUM(p2.O) AS O,
								   SUM(p2.M) AS M
							FROM   @parte2 p2
							GROUP BY
								   p2.id_local
					   ) AS sub1  
					   JOIN (
								SELECT p4.id_local,
									   AVG(p4.mph) AS mph
								FROM   @parte4 p4
								GROUP BY
									   p4.id_local
					   ) AS sub2
							ON  sub1.id_local = sub2.id_local  
		   ) AS sub1  
	WHERE  [@parte6].id_local = sub1.id_local  

	--SELECT * FROM @parte6  
  
	INSERT INTO @parte7  
	SELECT p.id_local,
		   COALESCE(p.C,0),
		   COALESCE(p.D,0),
		   p.velocidade_via
	FROM   (  
			   SELECT v.id_local,
					  t.tipo,
					  v.veiculos,
					  vel.velocidade AS velocidade_via
			   FROM   @parte0 v  
					  JOIN v_local_tipo_id t (NOLOCK)
							ON  v.id_local = t.id_local
								AND v.id_pista = t.id_pista
					  JOIN @vel_regulamentada vel
							ON  vel.id_local = v.id_local
								AND vel.id_pista = v.id_pista
			   WHERE  t.pista_dupla = 0
		   ) AS sub1  
	PIVOT  (
				SUM(sub1.veiculos)
				FOR sub1.tipo IN ([C],[D])
		   ) AS p  
  
	--SELECT * FROM @parte7  
  
	INSERT INTO @parte8  
	SELECT v.id_local,
		   COALESCE(velocidade_via,@velocidade_padrao) - (0.00776 * (v.volume_c + v.volume_d)) - dbo.fcn_ObterVariavelFluxo(v.volume_d) AS ats_c,  
		   COALESCE(velocidade_via,@velocidade_padrao) - (0.00776 * (v.volume_d + v.volume_c)) - dbo.fcn_ObterVariavelFluxo(v.volume_c) AS ats_d  
	FROM   @parte7 v  
  
	--SELECT * FROM @parte8  
  
	UPDATE @parte6
	SET    ats = (v.ats_c + v.ats_d) / 2.0  
	FROM   @parte8 v  
	WHERE  [@parte6].id_local = v.id_local  
  
	--SELECT * FROM @parte6  
  
	-- DECLARE @parte9 TABLE (id_local  C  M  O  P T V S );  
	INSERT INTO @parte9  
	SELECT sub1.id_local,
		   sub1.id_pista,
		   sub1.C,
		   sub1.M,
		   sub1.O,
		   sub1.P,
		   sub1.P + sub1.C + sub1.O + sub1.M AS T,
		   sub2.velocidade AS V,
		   --sub3.densidade,
		   --sub3.ats,
		   --sub3.pista_dupla,
		   --sub3.pista_dupla, sub3.densidade, sub3.ats,
		   CASE WHEN sub3.pista_dupla = 1
				THEN CASE WHEN sub3.densidade > 0 AND sub3.densidade < 18
						  THEN 1
						  ELSE CASE WHEN sub3.densidade >= 18 AND sub3.densidade < 35
									THEN 2
									ELSE CASE WHEN sub3.densidade >= 35 AND sub3.densidade < 45
											  THEN 3
											  ELSE CASE WHEN sub3.densidade >= 45
														THEN 4
														ELSE 0 END END END END
				ELSE CASE WHEN sub3.ats > 0 AND sub3.ats < 40
						  THEN 4
						  ELSE CASE WHEN sub3.ats >= 40 AND sub3.ats < 45
									THEN 3
									ELSE CASE WHEN sub3.ats >= 45 AND sub3.ats < 50
											  THEN 2
											  ELSE CASE WHEN sub3.ats >= 50
														THEN 1
														ELSE 0 END END END END
		   END AS S  
	FROM   @parte2 AS sub1  
		   JOIN @parte4 AS sub2
				ON  sub1.id_local = sub2.id_local  
					AND sub1.id_pista = sub2.id_pista  
		   JOIN @parte6 AS sub3  
				ON  sub1.id_local = sub3.id_local  
		   LEFT JOIN status_traffic st (NOLOCK)  
				ON  sub1.id_local = st.id_local  

	--SELECT * FROM @parte9


	INSERT INTO status_traffic  
	SELECT lv.id_local,
		   0  
	FROM   local_vigente lv (NOLOCK)  
		   LEFT JOIN status_traffic st (NOLOCK)  
				ON  lv.id_local = st.id_local  
	WHERE  st.id_local IS NULL  
  
	--SELECT id_local, MAX(S) AS S FROM @parte9 GROUP BY id_local ORDER BY id_local  
  
	UPDATE status_traffic
	SET    status_traffic.traffic = v.S  
	--FROM @parte9 v  
	FROM   (
				SELECT id_local,
					   MAX(S) AS S
				FROM   @parte9
				GROUP BY
					   id_local
		   ) v
	WHERE  status_traffic.id_local = v.id_local  
	 
	--SELECT * FROM @parte9 ORDER BY id_local
	 
	DECLARE @id_local INT, @pista CHAR(2), @C INT, @M INT, @O INT, @P INT, @T INT, @V INT, @S INT,  
				@id_local_ant INT, @S_ant INT, @dados_trafego VARCHAR(240)  
	 
	DECLARE dados_trafego_cursor CURSOR FOR
	SELECT v.id_local,
		   l.pista,
		   v.P,
		   v.C,
		   v.O,
		   v.M,
		   v.T,
		   v.V,
		   v.S
	FROM   @parte9 v
		   JOIN v_local_tipo_id l
				ON  l.id_local = v.id_local
					AND l.id_pista = v.id_pista
	ORDER BY
		   l.id_local,
		   l.id_pista;
	 
	OPEN dados_trafego_cursor
	 
	FETCH NEXT FROM dados_trafego_cursor
	INTO @id_local, @pista, @C, @M, @O, @P, @T, @V, @S
	 
	SET @id_local_ant = @id_local
	SET @S_ant = @S
	SET @dados_trafego = ''
  
	WHILE @@FETCH_STATUS = 0
	BEGIN
		IF (@id_local != @id_local_ant)  
		BEGIN  
			INSERT INTO @parte10 (id_local, dados_trafego, S)
			VALUES (@id_local_ant, @dados_trafego, @S_ant)

			SET @id_local_ant = @id_local
			SET @dados_trafego = ''
			SET @S_ant = @S
	 END  
	  
	 SET @dados_trafego = @dados_trafego + CASE WHEN @dados_trafego = '' THEN '' ELSE '|' END +  
			LTRIM(RTRIM(@pista)) + ' - ' +
			LTRIM(RTRIM(TRY_CAST(@C AS CHAR(5)))) + ' / ' +
			LTRIM(RTRIM(TRY_CAST(@M AS CHAR(5)))) + ' / ' +
			LTRIM(RTRIM(TRY_CAST(@O AS CHAR(5)))) + ' / ' +
			LTRIM(RTRIM(TRY_CAST(@P AS CHAR(5)))) + ' / ' +
			LTRIM(RTRIM(TRY_CAST(@T AS CHAR(5)))) + ' / ' +
			LTRIM(RTRIM(TRY_CAST(@V AS CHAR(5)))) + ' km/h   '  
	 
	 SET @id_local_ant = @id_local
	 SET @S_ant = @S
	 FETCH NEXT FROM dados_trafego_cursor
	 INTO @id_local, @pista, @C, @M, @O, @P, @T, @V, @S
	 
	END
	CLOSE dados_trafego_cursor;
	DEALLOCATE dados_trafego_cursor;
	
	INSERT INTO @parte10 (id_local, dados_trafego, S)
	VALUES (@id_local, @dados_trafego, @S)
	 
	--SELECT v.id_local, l.pista, v.P, v.C, v.O, v.M, v.T, v.V, v.S  
	--FROM @parte9 v  
	--JOIN v_local_tipo_id l ON l.id_local = v.id_local AND l.id_pista = v.id_pista  
	--ORDER BY id_local, id_pista  
	 
	SELECT * FROM @parte10 ORDER BY id_local  
  
RETURN 
