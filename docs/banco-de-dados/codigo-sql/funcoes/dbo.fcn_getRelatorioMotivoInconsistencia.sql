CREATE FUNCTION [dbo].[fcn_getRelatorioMotivoInconsistencia] (@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS

RETURN
( 
	--> OBTER COLUNAS PROBLEMA NÃO TÉCNICO
	--SELECT ',SUM(CASE WHEN i.id_inconsistencia = ' + CAST(id_inconsistencia AS VARCHAR(2)) + ' THEN 1 ELSE 0 END) AS [' + LTRIM(RTRIM(descricao)) + ']' AS coluna FROM inconsistencia WHERE id_inconsistencia > 0 AND razao_tecnica < 2

	--> OBTER COLUNAS PROBLEMA TÉCNICO
	--SELECT ',SUM(CASE WHEN i.id_inconsistencia = ' + CAST(id_inconsistencia AS VARCHAR(2)) + ' THEN 1 ELSE 0 END) AS [' + LTRIM(RTRIM(descricao)) + ']' AS coluna FROM inconsistencia WHERE id_inconsistencia > 0 AND razao_tecnica = 2


	--DECLARE @dataInicio DATE = '2017-10-27', @dataFim DATE = '2017-10-27'
	SELECT lv.serie_equipamento AS [N/S]--,
		   --lv.id_local AS [COD. LOCAL]
		   ,cep.cod_pista AS [COD. LOCAL]
		   ,RTRIM(cep.nome_pista) AS [LOCAL]
		   ,CASE WHEN cast(i.data as time) BETWEEN '06:00:00' AND '18:00:00' THEN 'DIURNO' ELSE 'NOTURNO' END AS [PERIODO]
		   ,SUM(CASE WHEN i.id_inconsistencia = 2 THEN 1 ELSE 0 END) AS [Imagem sem nitidez - diversos (chuva, neblina, insolação, etc)]
		   ,SUM(CASE WHEN i.id_inconsistencia = 3 THEN 1 ELSE 0 END) AS [Placa ilegível - má conservação ou posição no veículo]
		   ,SUM(CASE WHEN i.id_inconsistencia = 4 THEN 1 ELSE 0 END) AS [Veículo sem placa]
		   ,SUM(CASE WHEN i.id_inconsistencia = 5 THEN 1 ELSE 0 END) AS [Divergência de cadastro]
		   ,SUM(CASE WHEN i.id_inconsistencia = 6 THEN 1 ELSE 0 END) AS [Placa fora do padrão ( 3 letras / 4 números)]
		   ,SUM(CASE WHEN i.id_inconsistencia = 7 THEN 1 ELSE 0 END) AS [Veículo de emergência]
		   ,SUM(CASE WHEN i.id_inconsistencia = 8 THEN 1 ELSE 0 END) AS [Veículo de teste - invalidado por solicitação do Órgão de Trânsito]
		   ,SUM(CASE WHEN i.id_inconsistencia = 18 THEN 1 ELSE 0 END) AS [Veículo longo (maior que ônibus normal - 13m)]
		   ,SUM(CASE WHEN i.id_inconsistencia = 29 THEN 1 ELSE 0 END) AS [Agente de Trânsito na via]
		   ,SUM(CASE WHEN i.id_inconsistencia = 30 THEN 1 ELSE 0 END) AS [Obras / interdição na via]
		   ,SUM(CASE WHEN i.id_inconsistencia = 31 THEN 1 ELSE 0 END) AS [Falta de sinalização vertical / horizontal]
		   ,SUM(CASE WHEN i.id_inconsistencia = 32 THEN 1 ELSE 0 END) AS [Semáforo encoberto (árvore, veículo, etc)]
		   ,SUM(CASE WHEN i.id_inconsistencia = 33 THEN 1 ELSE 0 END) AS [Dúvida no semáforo]
		   ,SUM(CASE WHEN i.id_inconsistencia = 34 THEN 1 ELSE 0 END) AS [Semáforo apagado]
		   ,SUM(CASE WHEN i.id_inconsistencia = 43 THEN 1 ELSE 0 END) AS [Panorâmica sem nitidez - diversos ( chuva, neblina, insolação, etc)]
		   ,SUM(CASE WHEN i.id_inconsistencia = 53 THEN 1 ELSE 0 END) AS [Veículo isento do rodízio - Visual]
		   ,SUM(CASE WHEN i.id_inconsistencia = 54 THEN 1 ELSE 0 END) AS [Captura de imagem frontal - Moto]
		   ,SUM(CASE WHEN i.id_inconsistencia = 55 THEN 1 ELSE 0 END) AS [Veículo é táxi de outro município ou sem cadastro]
		   ,SUM(CASE WHEN i.id_inconsistencia = 56 THEN 1 ELSE 0 END) AS [Situação de dúvida em veículos captados nas entrefaixas]
		   ,SUM(CASE WHEN i.id_inconsistencia = 57 THEN 1 ELSE 0 END) AS [Invalidado por solicitação do órgão de trânsito]
		   --------------------------
		   ,SUM(CASE WHEN i.id_inconsistencia > 0 AND inc.razao_tecnica < 2 THEN 1 ELSE 0 END) AS [TOTAL NT]
		   --------------------------

		   ,SUM(CASE WHEN i.id_inconsistencia = 1 THEN 1 ELSE 0 END) AS [Imagem sem nitidez/iluminação - Equipamento]
		   ,SUM(CASE WHEN i.id_inconsistencia = 9 THEN 1 ELSE 0 END) AS [Dúvida quanto ao infrator (dois ou mais veículos na mesma imagem)]
		   ,SUM(CASE WHEN i.id_inconsistencia = 10 THEN 1 ELSE 0 END) AS [Veículo entre faixas  ( com velocidades regulamentadas diferentes)]
		   ,SUM(CASE WHEN i.id_inconsistencia = 11 THEN 1 ELSE 0 END) AS [Imagem sem veículo]
		   ,SUM(CASE WHEN i.id_inconsistencia = 12 THEN 1 ELSE 0 END) AS [Veículo fora do campo visível]
		   ,SUM(CASE WHEN i.id_inconsistencia = 13 THEN 1 ELSE 0 END) AS [Enquadramento (limites) da imagem incorreto]
		   ,SUM(CASE WHEN i.id_inconsistencia = 14 THEN 1 ELSE 0 END) AS [Erro de captura - veículo registrado não é infrator]
		   ,SUM(CASE WHEN i.id_inconsistencia = 15 THEN 1 ELSE 0 END) AS [Imagem repetida]
		   ,SUM(CASE WHEN i.id_inconsistencia = 16 THEN 1 ELSE 0 END) AS [Infração já registrada no período]
		   ,SUM(CASE WHEN i.id_inconsistencia = 17 THEN 1 ELSE 0 END) AS [Bicicleta/ carrinho/ carroça]
		   ,SUM(CASE WHEN i.id_inconsistencia = 19 THEN 1 ELSE 0 END) AS [Veículo não é caminhão]
		   ,SUM(CASE WHEN i.id_inconsistencia = 20 THEN 1 ELSE 0 END) AS [Veículo não é caminhão / ônibus]
		   ,SUM(CASE WHEN i.id_inconsistencia = 21 THEN 1 ELSE 0 END) AS [Veículo não e ônibus / microônibus]
		   ,SUM(CASE WHEN i.id_inconsistencia = 22 THEN 1 ELSE 0 END) AS [Veículo não permaneceu na faixa exclusiva]
		   ,SUM(CASE WHEN i.id_inconsistencia = 23 THEN 1 ELSE 0 END) AS [Veículo isento do rodízio]
		   ,SUM(CASE WHEN i.id_inconsistencia = 24 THEN 1 ELSE 0 END) AS [Veículo isento na ZMRC]
		   ,SUM(CASE WHEN i.id_inconsistencia = 25 THEN 1 ELSE 0 END) AS [Veículo isento na ZMRF]
		   ,SUM(CASE WHEN i.id_inconsistencia = 26 THEN 1 ELSE 0 END) AS [Veículo isento na Faixa não Destinada]
		   ,SUM(CASE WHEN i.id_inconsistencia = 27 THEN 1 ELSE 0 END) AS [Veículo é ônibus]
		   ,SUM(CASE WHEN i.id_inconsistencia = 28 THEN 1 ELSE 0 END) AS [Veículo é taxi]
		   ,SUM(CASE WHEN i.id_inconsistencia = 35 THEN 1 ELSE 0 END) AS [Tarja incorreta]
		   ,SUM(CASE WHEN i.id_inconsistencia = 36 THEN 1 ELSE 0 END) AS [Código do enquadramento incorreto]
		   ,SUM(CASE WHEN i.id_inconsistencia = 37 THEN 1 ELSE 0 END) AS [Velocidade incoerente]
		   ,SUM(CASE WHEN i.id_inconsistencia = 38 THEN 1 ELSE 0 END) AS [Equipamento não publicado]
		   ,SUM(CASE WHEN i.id_inconsistencia = 39 THEN 1 ELSE 0 END) AS [Equipamento não aferido]
		   ,SUM(CASE WHEN i.id_inconsistencia = 40 THEN 1 ELSE 0 END) AS [Imagem panorâmica sem veículo]
		   ,SUM(CASE WHEN i.id_inconsistencia = 41 THEN 1 ELSE 0 END) AS [Enquadramento (limites) da imagem panorâmica incorreto]
		   ,SUM(CASE WHEN i.id_inconsistencia = 42 THEN 1 ELSE 0 END) AS [Panorâmica sem nitidez / iluminação - Equipamento]
		   ,SUM(CASE WHEN i.id_inconsistencia = 44 THEN 1 ELSE 0 END) AS [Ausência de panorâmica]
		   ,SUM(CASE WHEN i.id_inconsistencia = 45 THEN 1 ELSE 0 END) AS [Divergência entre imagens panorâmica / pontual]
		   ,SUM(CASE WHEN i.id_inconsistencia = 46 THEN 1 ELSE 0 END) AS [Infração não registrada no filme]
		   ,SUM(CASE WHEN i.id_inconsistencia = 47 THEN 1 ELSE 0 END) AS [Filme com excesso de luminosidade]
		   ,SUM(CASE WHEN i.id_inconsistencia = 48 THEN 1 ELSE 0 END) AS [Filme com falta de luminosidade]
		   ,SUM(CASE WHEN i.id_inconsistencia = 49 THEN 1 ELSE 0 END) AS [Filme curto]
		   ,SUM(CASE WHEN i.id_inconsistencia = 50 THEN 1 ELSE 0 END) AS [Operador não é funcionário]
		   ,SUM(CASE WHEN i.id_inconsistencia = 51 THEN 1 ELSE 0 END) AS [Infração registrada nos dois sensores]
		   ,SUM(CASE WHEN i.id_inconsistencia = 91 THEN 1 ELSE 0 END) AS [Placa saturada (reflexiva)]
		   ,SUM(CASE WHEN i.id_inconsistencia = 92 THEN 1 ELSE 0 END) AS [Imagem desfocada/borrada/embaçada]
		   ,SUM(CASE WHEN i.id_inconsistencia = 93 THEN 1 ELSE 0 END) AS [Imagem com iluminação insuficiente]
		   ,SUM(CASE WHEN i.id_inconsistencia = 94 THEN 1 ELSE 0 END) AS [Parada sobre faixa - veículo não é infrator]
		   ,SUM(CASE WHEN i.id_inconsistencia = 95 THEN 1 ELSE 0 END) AS [Rodízio - veículo não é infrator]
		   ,SUM(CASE WHEN i.id_inconsistencia = 96 THEN 1 ELSE 0 END) AS [Conversão à direita/esquerda - veículo não é infrator]
		   ,SUM(CASE WHEN i.id_inconsistencia = 97 THEN 1 ELSE 0 END) AS [Avanço de semáforo - veículo não é infrator]
		   ,SUM(CASE WHEN i.id_inconsistencia = 98 THEN 1 ELSE 0 END) AS [Veiculo fora da faixa fiscalizada - veículo não é infrator]
		   --------------------------------------------------------------------------------------
		   ,SUM(CASE WHEN inc.razao_tecnica = 2 THEN 1 ELSE 0 END) AS [TOTAL PT]
		   ---------------------------------------------------------------------

		   ,COUNT(i.id_infracao) AS [Imagens registradas]
		   ,SUM(CASE WHEN inc.razao_tecnica = 2 THEN 1 ELSE 0 END) AS [Imagens rejeitadas por problemas técnicos]
		   ,SUM(CASE WHEN i.id_inconsistencia > 0 AND inc.razao_tecnica < 2 THEN 1 ELSE 0 END) AS [Imagens rejeitadas por problemas não-técnicos]
		   ,SUM(CASE WHEN i.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS [Imagens válidas]
		   ,STR(CAST((1-(CAST(SUM(CASE WHEN inc.razao_tecnica = 2 THEN 1 ELSE 0 END) AS NUMERIC(15,3))/COUNT(i.id_infracao)))*100 AS INT)) + '%' AS [Aproveitamento líquido]
		   ,CAST(@dataInicio AS DATETIME) AS [INICIO PERIODO]
		   ,CAST(@dataFim AS DATETIME) AS [FIM PERIODO]
		   ,(SELECT valor FROM CHAVE_VALOR WHERE chave = 'identificacao_cliente') AS [CONTRATO]
	FROM   infracao i WITH (NOLOCK)
		   JOIN inconsistencia inc WITH (NOLOCK)
				ON  inc.id_inconsistencia = i.id_inconsistencia
		   JOIN local_vigente lv WITH (NOLOCK)
				ON  lv.id_local = i.id_local
		   JOIN configuracao_equipamento_pista cep WITH (NOLOCK)
				ON  cep.id_configuracao_equipamento = lv.id_configuracao_equipamento and cep.id_pista = i.pista
		   JOIN enquadramento enq WITH (NOLOCK)
				ON  enq.id_enquadramento = i.id_enquadramento
		   LEFT JOIN infracao_remessa ir (NOLOCK)
				ON  ir.id_infracao = i.id_infracao
		   LEFT JOIN remessa r (NOLOCK)
				ON  r.id_remessa = ir.id_remessa
	WHERE  i.id_enquadramento > 1
		   AND CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim

	
		  --Alterado O.S. 101 - Auditoria CET
		  --Thiago Surgik - 22/07/2015
		  --AND CAST(i.data AS DATE) >= CAST(DATEADD(DAY, -45, GETDATE()) AS DATE)
		  --AND r.data_validacao IS NULL

	GROUP BY
		   lv.serie_equipamento
		  --,lv.id_local
		  ,cep.cod_pista
		  ,RTRIM(cep.nome_pista)
		  ,CASE WHEN cast(i.data as time) BETWEEN '06:00:00' AND '18:00:00' THEN 'DIURNO' ELSE 'NOTURNO' END
	--ORDER BY
	--	   1
	--	  ,2
)
