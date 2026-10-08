
CREATE PROCEDURE [dbo].[spu_cria_Relatorios_Diario_Volume_Hora_PMG]
	@Data datetime,
	@HoraInicio datetime,
	@HoraFinal  datetime,
	@Usuario char(30)
AS	
	
	DECLARE @DataStart DateTime
	DECLARE @ID_Relatorio INT
	
	SET @Id_Relatorio = 2 -- Número do ID do relatório PMG

	SET @DataStart = GETDATE()

	SET @Data = CONVERT(char(10),@Data,120)
	SET @HoraInicio = CONVERT(char(5),@HoraInicio,108)
	SET @HoraFinal = CONVERT(char(5),@HoraInicio,108)
	--CONVERT(char(5),@HoraFinal,108)

	DECLARE @ID_Gerado INT

	INSERT INTO Relatorios_Gerados with (rowlock)
		(Data,ID_Relatorio,DataInicio,DataFinal,HoraInicio,HoraFinal,Gerador)
	VALUES 
		(GETDATE(), @Id_Relatorio, @Data, @Data,@HoraInicio,@HoraFinal,@Usuario)
	
	SELECT 
		@ID_Gerado = Max(ID_Gerado) 
	FROM 
		Relatorios_Gerados (nolock)

	INSERT INTO Relatorios_Volume_Hora_PMG with (rowlock)
		([ID_Gerado],[Local],[pista], [ClassePorTamanho],[h0],[h1],[h2],[h3],[h4],[h5],[h6],[h7],[h8],[h9],
		[h10],[h11],[h12],[h13],[h14],[h15],[h16],[h17],[h18],[h19],[h20],[h21],[h22],[h23],[Total])
	SELECT 	
		@ID_Gerado, L1.[id_local],L1.[pista], L1.[id_classe],[h0],[h1],[h2],[h3],[h4],[h5],[h6],[h7],[h8],[h9],
		[h10],[h11],[h12],[h13],[h14],[h15],[h16],[h17],[h18],[h19],[h20],[h21],[h22],[h23],[Total]
	FROM (	SELECT 	
				L.id_local, 
				LL.id_pista as [pista], 
				CL.id_classe
	     	FROM local_vigente L (nolock) , 
				classe_veiculo CL (nolock), 
				configuracao_equipamento_pista LL (nolock)
	     	WHERE	LL.id_configuracao_equipamento = L.id_configuracao_equipamento
	    	) AS L1 -- Garante que todos os locais, pistas, classificações de tamanho sejam incluidos no relatório, mesmo se não houve eventos
			LEFT JOIN (	SELECT 	
							C.id_local ,
							C.Pista, 
							C.id_classe,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 0 THEN 1 ELSE 0 END) AS h0,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 1 THEN 1 ELSE 0 END) AS h1,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 2 THEN 1 ELSE 0 END) AS h2,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 3 THEN 1 ELSE 0 END) AS h3,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 4 THEN 1 ELSE 0 END) AS h4,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 5 THEN 1 ELSE 0 END) AS h5,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 6 THEN 1 ELSE 0 END) AS h6,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 7 THEN 1 ELSE 0 END) AS h7,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 8 THEN 1 ELSE 0 END) AS h8,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 9 THEN 1 ELSE 0 END) AS h9,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 10 THEN 1 ELSE 0 END) AS h10,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 11 THEN 1 ELSE 0 END) AS h11,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 12 THEN 1 ELSE 0 END) AS h12,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 13 THEN 1 ELSE 0 END) AS h13,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 14 THEN 1 ELSE 0 END) AS h14,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 15 THEN 1 ELSE 0 END) AS h15,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 16 THEN 1 ELSE 0 END) AS h16,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 17 THEN 1 ELSE 0 END) AS h17,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 18 THEN 1 ELSE 0 END) AS h18,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 19 THEN 1 ELSE 0 END) AS h19,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 20 THEN 1 ELSE 0 END) AS h20,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 21 THEN 1 ELSE 0 END) AS h21,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 22 THEN 1 ELSE 0 END) AS h22,
							SUM(CASE CONVERT(CHAR(2),C.data,14) WHEN 23 THEN 1 ELSE 0 END) AS h23,
							Count(*) AS Total
						FROM Veiculo c (nolock)
							RIGHT JOIN (SELECT
											l.id_local,
											MAX(l.id_configuracao_equipamento) as id_configuracao_equipamento,
											ce.id_pista
										FROM local l (nolock)
											INNER JOIN configuracao_equipamento_captura_veiculo ce (nolock)
												ON ce.id_configuracao_equipamento = l.id_configuracao_equipamento
										WHERE	l.data_atualizacao <= @Data
											and ce.num_lacos > 1
										GROUP BY 
											l.id_local, 
											ce.id_pista
										) AS atuais
								ON	atuais.id_local = c.id_local 
								AND atuais.id_pista = c.pista
						WHERE	C.data >= @Data  
							AND C.data <= @Data + 1	
						GROUP BY 
							C.id_local, 
							C.pista, 
							C.id_classe
								--@data @horaInicio @HoraFinal
						) AS Q1 
				ON Q1.id_local = L1.id_local AND q1.Pista = L1.Pista and L1.id_classe = Q1.id_classe
		
	--WHERE L1.id_local in (SELECT top 1 id_configuracao_equipamento FROM local where id_local = c.id data_atualizacao <= '2009-09-01' order by data_atualizacao desc)   -- periodo valido do local	ORDER BY L1.local, L1.Pista, L1.ClassePorTamanho 
	--WHERE L1.id_local in (SELECT id_local FROM local where data_atualizacao <= @Data order by data_atualizacao  desc) 

	UPDATE Relatorios_Gerados with (rowlock)
	SET TempoGeracao = GETDATE() - @DataStart 
	WHERE ID_Gerado = @ID_Gerado

	RETURN @ID_Gerado



