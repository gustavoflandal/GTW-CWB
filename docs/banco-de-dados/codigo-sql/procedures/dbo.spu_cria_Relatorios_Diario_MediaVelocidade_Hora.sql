CREATE      PROCEDURE [dbo].[spu_cria_Relatorios_Diario_MediaVelocidade_Hora]
	@Data datetime,
	@HoraInicio datetime,
	@HoraFinal  datetime,
	@Usuario char(30)
AS
	declare @DataStart DateTime
	declare @ID_Relatorio INT
	
	SET @Id_Relatorio = 4 -- Número do ID do relatório PMG

	SET @DataStart = GETDATE()

	SET @Data = CONVERT(char(10),@Data,120)	
	SET @HoraInicio = CONVERT(char(5),@HoraInicio,108)
	SET @HoraFinal = CONVERT(char(5),@HoraFinal,108)

	DECLARE @ID_Gerado INT

	INSERT INTO Relatorios_Gerados with (rowlock)
		(Data,ID_Relatorio,DataInicio,DataFinal,HoraInicio,HoraFinal,Gerador)
	VALUES 
		(GETDATE(), @Id_Relatorio, @Data, @Data,@HoraInicio,@HoraFinal,@Usuario)
	
	SELECT 
		@ID_Gerado = Max(ID_Gerado) 
	FROM 
		Relatorios_Gerados (nolock)

	INSERT INTO dbo.Relatorios_MediaVelocidade_Hora with (rowlock)
		([ID_Gerado],[Local],[h0],[h1],[h2],[h3],[h4],[h5],[h6],[h7],[h8],[h9],[h10],[h11],
		[h12],[h13],[h14],[h15],[h16],[h17],[h18],[h19],[h20],[h21],[h22],[h23],[Total])
	SELECT 	
		@ID_Gerado, 
		L1.[id_local],[h0],[h1],[h2],[h3],[h4],[h5],[h6],[h7],[h8],[h9],[h10],[h11],[h12],
		[h13],[h14],[h15],[h16],[h17],[h18],[h19],[h20],[h21],[h22],[h23],[Total]
	FROM (	SELECT 	
				L.id_local
	     	FROM
				local_vigente L (nolock)
	    ) AS L1 -- Garante que todos os locais sejam incluidos no relatório, mesmo se não houve eventos
		LEFT JOIN (	SELECT
						C.id_local ,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 0 THEN c.velocidade END) AS h0,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 1 THEN c.velocidade END) AS h1,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 2 THEN c.velocidade END) AS h2,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 3 THEN c.velocidade END) AS h3,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 4 THEN c.velocidade END) AS h4,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 5 THEN c.velocidade END) AS h5,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 6 THEN c.velocidade END) AS h6,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 7 THEN c.velocidade END) AS h7,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 8 THEN c.velocidade END) AS h8,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 9 THEN c.velocidade END) AS h9,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 10 THEN c.velocidade END) AS h10,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 11 THEN c.velocidade END) AS h11,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 12 THEN c.velocidade END) AS h12,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 13 THEN c.velocidade END) AS h13,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 14 THEN c.velocidade END) AS h14,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 15 THEN c.velocidade END) AS h15,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 16 THEN c.velocidade END) AS h16,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 17 THEN c.velocidade END) AS h17,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 18 THEN c.velocidade END) AS h18,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 19 THEN c.velocidade END) AS h19,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 20 THEN c.velocidade END) AS h20,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 21 THEN c.velocidade END) AS h21,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 22 THEN c.velocidade END) AS h22,
						AVG(CASE CONVERT(CHAR(2),c.data,14) WHEN 23 THEN c.velocidade END) AS h23,
						AVG(c.velocidade) AS Total
					FROM veiculo C (nolock)
						--LEFT JOIN classe_veiculo CL (nolock) 
						--	ON CL.id_classe = C.id_classe
						INNER JOIN local_vigente L (nolock) 
							ON L.id_local = C.id_local
						--JOIN configuracao_equipamento CE (nolock)
						--	ON CE.id_configuracao_equipamento = L.id_configuracao_equipamento
					WHERE	C.data >= @Data
		    			AND c.data <= @Data + 1
		    			--	AND CONVERT(CHAR(5),c.data,14) BETWEEN @HoraInicio AND @HoraFinal
					GROUP BY	
						C.id_local
					) AS Q1 
					ON Q1.id_local = L1.id_local 
	--WHERE
		--L1.id_local in (SELECT top 1 id_local FROM local_vigente lv WHERE  '2009-09-20' >= lv.data_atualizacao and lv.id_local = L1.id_local order by lv.data_atualizacao desc )   	ORDER BY L1.id_local

	UPDATE Relatorios_Gerados with (rowlock)
	SET TempoGeracao = GETDATE() - @DataStart 
	WHERE ID_Gerado = @ID_Gerado


	Return @ID_Gerado



