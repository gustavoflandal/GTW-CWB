
CREATE PROCEDURE [dbo].[spu_atualiza_painel_principal]
AS

	DECLARE @data_ini DATETIME = GETDATE()

	CREATE TABLE #painel_principal_at (
		dias_atraso_atual INT NULL,
		dias_atraso_reprovado INT NULL,
		dias_atraso_remessa INT NULL,
		dias_atraso_validacao INT NULL,
		total_atraso INT NULL,
		total_atraso_ant INT NULL,
		total_erro INT NULL,
		total_erro_ant INT NULL,
		dias_atraso_cad_isento INT NULL,
		arquivos_verificados INT NULL,
		data_arquivos_cav DATETIME NULL,
		erros_remessa_automatico INT NULL
		)

	INSERT INTO #painel_principal_at 
	SELECT TOP(1) dias_atraso_atual, dias_atraso_reprovado ,dias_atraso_remessa ,dias_atraso_validacao ,total_atraso ,
	total_atraso_ant ,total_erro ,total_erro_ant ,dias_atraso_cad_isento ,arquivos_verificados ,data_arquivos_cav,
	erros_remessa_automatico   
	FROM painel_principal (NOLOCK) ORDER BY data_adicionado DESC

	UPDATE #painel_principal_at SET dias_atraso_validacao = sub1.dias_atraso_validacao FROM
	(
		SELECT DATEDIFF(DAY ,MIN(i.data) ,GETDATE()) AS dias_atraso_validacao
		FROM   infracao i(NOLOCK)
				JOIN infracao_remessa ir(NOLOCK)
					ON  i.id_infracao = ir.id_infracao
				JOIN remessa r(NOLOCK)
					ON  r.id_remessa = ir.id_remessa
				LEFT JOIN movimentos_erro me (NOLOCK) 
					ON me.id_remessa = r.id_remessa 
		WHERE  r.data_validacao IS NULL 
		AND    me.id_movimento IS NULL 
	) AS sub1

	UPDATE #painel_principal_at SET dias_atraso_remessa = sub1.dias_atraso_remessa FROM 
	(
		SELECT COALESCE(DATEDIFF(DAY ,MIN(inf.data) ,GETDATE()),0) AS dias_atraso_remessa
		FROM   infracao inf(NOLOCK)
				LEFT JOIN infracao_remessa ir(NOLOCK)
					ON  inf.id_infracao = ir.id_infracao
		WHERE  inf.id_processo = 4 
			AND ir.id_infracao IS NULL
	) AS sub1

	UPDATE #painel_principal_at SET dias_atraso_atual = sub1.dias_atraso_atual FROM 
	(
		SELECT ISNULL(DATEDIFF(DAY ,MIN(infracao.data) ,GETDATE()),0) AS dias_atraso_atual
		FROM   infracao(NOLOCK)
				LEFT JOIN infracao_remessa ir (NOLOCK)
					ON infracao.id_infracao = ir.id_infracao
		WHERE  infracao.id_enquadramento > 1
			AND ir.id_infracao IS NULL
			AND infracao.id_processo IN (NULL, 1, 2, 11, 20, 21, 23, 24)
	) AS sub1

	UPDATE #painel_principal_at SET dias_atraso_reprovado = sub1.dias_atraso_reprovado FROM 
	(
		SELECT ISNULL(DATEDIFF(DAY ,MIN(infracao.data) ,GETDATE()),0) AS dias_atraso_reprovado
		-- SELECT * 
		FROM   infracao(NOLOCK)
				JOIN infracao_remessa ir (NOLOCK)
					ON infracao.id_infracao = ir.id_infracao
				JOIN remessa r (NOLOCK)
					ON ir.id_remessa = r.id_remessa
		WHERE r.data_validacao IS NULL AND infracao.id_processo IN (1, 2, 11, 20, 21, 23, 24)
	) AS sub1

	UPDATE #painel_principal_at SET dias_atraso_cad_isento = sub1.dias_atraso_cad_isento FROM 
	(
		SELECT MAX(sub1.dui) AS dias_atraso_cad_isento
		FROM   (
					SELECT MIN(DATEDIFF(DAY, data_hora, GETDATE())) AS dui
					FROM   cad_isento_arquivo(NOLOCK)
					GROUP BY
							id_enquadramento
				) AS sub1
	) AS sub1 

	UPDATE #painel_principal_at SET arquivos_verificados = sub1.arquivos_verificados, data_arquivos_cav = sub1.data_arquivos_cav FROM
	(SELECT SUM(verif_arquivo) arquivos_verificados, MAX(data_arquivo_cav) data_arquivos_cav 
	FROM fcn_getRelatorioIsentos ()) AS sub1

	UPDATE #painel_principal_at SET erros_remessa_automatico = sub1.cnt FROM 
	(SELECT COUNT(*) AS cnt FROM dbo.fcn_getRelatorioErrosExportaAutomatico(NULL,NULL)) AS sub1

	IF NOT CAST(GETDATE() AS TIME) BETWEEN '02:00:00' AND '23:00:00'
	BEGIN
	UPDATE #painel_principal_at SET total_atraso_ant = sub1.imagem_dia_atraso, total_erro_ant = sub1.imagem_dia_erro FROM 
			fcn_IndicadoresProcessamentoAgrupado(
               DATEADD(MONTH, -1, CAST(CAST(DATEADD(DAY, (DATEPART(DAY, GETDATE()) -1) * -1, GETDATE())AS DATE) AS DATETIME))
              --,CAST(CAST(DATEADD(DAY, (DATEPART(DAY, GETDATE()) -1) * -1, GETDATE())AS DATE) AS DATETIME)
			  ,CAST(CAST(DATEADD(DAY, - DATEPART(DAY, GETDATE()), GETDATE()) AS DATE) AS DATETIME)
              ,8) AS sub1

	UPDATE #painel_principal_at SET total_atraso = sub1.imagem_dia_atraso, total_erro = sub1.imagem_dia_erro FROM 
          fcn_IndicadoresProcessamentoAgrupado(
               CAST(CAST(DATEADD(DAY, (DATEPART(DAY, GETDATE()) -1) * -1, GETDATE()) AS DATE) AS DATETIME)
              ,GETDATE()
              ,8) AS sub1

	END

	INSERT INTO painel_principal (dias_atraso_atual, dias_atraso_remessa, dias_atraso_validacao, 
	total_atraso, total_atraso_ant, total_erro, total_erro_ant, dias_atraso_cad_isento, dias_atraso_reprovado,
	tempo_consulta, arquivos_verificados ,data_arquivos_cav, erros_remessa_automatico)
    SELECT dias_atraso_atual, dias_atraso_remessa, dias_atraso_validacao, 
	total_atraso, total_atraso_ant, total_erro, total_erro_ant, dias_atraso_cad_isento, dias_atraso_reprovado,
	DATEDIFF(SECOND,@data_ini,GETDATE()) tempo_consulta, arquivos_verificados ,data_arquivos_cav, 
	erros_remessa_automatico 
	FROM #painel_principal_at

	DROP TABLE #painel_principal_at
