
CREATE    PROCEDURE [dbo].[spu_cria_Relatorios_Diario_Trafego_Pista]
	@Data datetime,
	@HoraInicio datetime,
	@HoraFinal  datetime,
	@Usuario char(30)
AS
	declare @DataStart DateTime

	Set @DataStart = GetDate()

	SET @Data = convert(char(10),@Data,120)	
	SET @HoraInicio = convert(char(5),@HoraInicio,108)
	SET @HoraFinal = convert(char(5),@HoraFinal,108)

	DECLARE @ID_Gerado int

	INSERT INTO Relatorios_Gerados with (rowlock)
		(Data,ID_Relatorio,DataInicio,DataFinal,HoraInicio,HoraFinal,Gerador)
	VALUES 
		(GetDate(), 1, @Data, @Data,@HoraInicio,@HoraFinal,@Usuario)
	
	SELECT 
		@ID_Gerado = Max(ID_Gerado) 
	FROM 
		Relatorios_Gerados (nolock)

	INSERT INTO Relatorios_Trafego_Pista with (rowlock)
		(ID_Gerado, Local, Pista, v00_40, v41_48, v49_67, v68_79, v80_97, v98_999, v68_77, v78_91, v92_113, v114_999, qt_Infratores, qt_RejeitadoPT, qt_RejeitadoPNT, qt_InvalidadoPT, qt_InvalidadoPNT, qt_Veic_Oficiais, q1.qt_Validos,qt_Infr_Red)
	SELECT 
		@ID_Gerado, Lc.id_local, LL.id_pista as Pista, Q1.v00_40, Q1.v41_48, Q1.v49_67, Q1.v68_79, v80_97, Q1.v98_999, Q1.v68_77, Q1.v78_91, v92_113, Q1.v114_999, Q1.qt_Infratores,Q1.qt_RejeitadoPT,qt_RejeitadoPNT,q1.qt_InvalidadoPT,q1.qt_InvalidadoPNT,q1.qt_Veic_Oficiais, q1.qt_Validos,qt_Infr_Red  
	FROM local_vigente lc (nolock) 
		INNER JOIN configuracao_equipamento_pista LL (nolock) 
			ON LL.id_configuracao_equipamento = Lc.id_configuracao_equipamento 
		LEFT JOIN (	SELECT  
						v.id_local, v.pista,
	    				SUM(CASE WHEN v.velocidade >= 0 and v.velocidade < 41 THEN 1 ELSE 0 END) as v00_40,
	      				SUM(CASE WHEN v.velocidade >= 41 and v.velocidade < 49 THEN 1 ELSE 0 END) as v41_48,
	      				SUM(CASE WHEN v.velocidade >= 49 and v.velocidade < 68 THEN 1 ELSE 0 END) as v49_67,
	      				SUM(CASE WHEN i.velocidade_limite = 60 and v.velocidade >= 68 and v.velocidade < 80 and I.id_infracao IS NOT NULL THEN 1 ELSE 0 END) as v68_79,
	      				SUM(CASE WHEN i.velocidade_limite = 60 and v.velocidade >= 80 and v.velocidade < 98 and I.id_infracao IS NOT NULL THEN 1 ELSE 0 END) as v80_97,
	      				SUM(CASE WHEN i.velocidade_limite = 60 and v.velocidade >= 98 and I.id_infracao IS NOT NULL THEN 1 ELSE 0 END) as v98_999,
	      				SUM(CASE WHEN i.velocidade_limite = 70 and v.velocidade >= 68 and v.velocidade < 78 THEN 1 ELSE 0 END) as v68_77,
	      				SUM(CASE WHEN i.velocidade_limite = 70 and v.velocidade >= 78 and v.velocidade < 92  and I.id_infracao IS NOT NULL THEN 1 ELSE 0 END) as v78_91,
	      				SUM(CASE WHEN i.velocidade_limite = 70 and v.velocidade >= 92 and v.velocidade < 114  and I.id_infracao IS NOT NULL THEN 1 ELSE 0 END) as v92_113,
	      				SUM(CASE WHEN i.velocidade_limite = 70 and v.velocidade >= 114 and I.id_infracao IS NOT NULL THEN 1 ELSE 0 END) as v114_999,
	      				SUM(CASE WHEN i.velocidade_limite <= v.velocidade THEN 1 ELSE 0 END) as qt_infratores,
	      				SUM(CASE WHEN i.aproveitavel = 0 and inc.razao_tecnica > 0 THEN 1 ELSE 0 END) as qt_rejeitadoPT,
	      				SUM(CASE WHEN i.aproveitavel = 0 and inc.razao_tecnica = 0 THEN 1 ELSE 0 END) as qt_rejeitadoPNT,
	      				SUM(CASE WHEN i.valida = 0 and inc.razao_tecnica > 0 THEN 1 ELSE 0 END) as qt_invalidadoPT,
	      				SUM(CASE WHEN i.valida = 0 and inc.razao_tecnica = 0 THEN 1 ELSE 0 END) as qt_invalidadoPNT,
	      				SUM(CASE WHEN I.id_inconsistencia = 3 THEN 1 ELSE 0 END) as qt_Veic_Oficiais,
	      				SUM(CASE WHEN i.valida = 1 THEN 1 ELSE 0 END) as qt_validos,
						SUM(CASE WHEN v.segundos > 0 then 1 else 0 end) as qt_Infr_RED
					FROM veiculo v (nolock)
							INNER JOIN local_vigente L (nolock) 
								ON L.id_local = v.id_local
							LEFT JOIN infracao_completa I (nolock) 
								ON I.id_veiculo = v.id_veiculo
							LEFT JOIN inconsistencia inc (nolock) 
								ON inc.id_inconsistencia = I.id_inconsistencia
							--Left Join configuracao_equipamento_regra_infracao ceri (nolock) 
							--	on ceri.id_configuracao_equipamento = L.id_configuracao_equipamento
							--Left Join Invalidacao Inv (nolock) 
							--	on Inv.Invalidacao = I.Invalidacao			
					WHERE	v.Data >= @Data and v.Data < @Data + 1
						and (convert(char(5),v.data,14) >= @HoraInicio and convert(char(5),v.data,14) <= @HoraFinal  )
					GROUP BY 
						v.id_local, 
						v.pista

					) as q1 
					ON q1.id_local = lc.id_local 
					AND q1.Pista = ll.id_pista

	UPDATE Relatorios_Gerados with (rowlock)
	SET TempoGeracao = GetDate() - @DataStart 
	WHERE ID_Gerado = @ID_Gerado

	RETURN @ID_Gerado



