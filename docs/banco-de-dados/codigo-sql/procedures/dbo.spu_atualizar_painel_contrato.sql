
CREATE PROCEDURE [dbo].[spu_atualizar_painel_contrato]
AS

SET NOCOUNT ON
-------------------------------------------------------------------------------------------------------
--> Passo 01 - AtualizaPainel
--> Insere os locais que não existem na tabela

INSERT INTO painel_contrato with (ROWLOCK) 
(
	dataGeracao,
	numeroSerie,
	idLocal,
	idPista,
	codigoFaixa,
	nomeFaixa
)
SELECT
	GETDATE(),
	lv.serie_equipamento AS numeroSerie,
	lv.id_local,
	cep.id_pista AS [idPista],
	CASE WHEN (cep.cod_pista_alternativo = 0) THEN cep.id_pista ELSE cep.cod_pista_alternativo END AS [codigoFaixa],
	RTRIM(cep.nome_pista) AS [nomeFaixa]
FROM local_vigente lv (NOLOCK)
	INNER JOIN configuracao_equipamento_pista cep (NOLOCK)
		ON cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
	LEFT JOIN painel_contrato pc with (ROWLOCK)
		ON pc.numeroSerie = lv.serie_equipamento AND pc.idPista = cep.id_pista
WHERE	pc.numeroSerie IS NULL
	AND lv.data_inicio < GETDATE()
	AND lv.desativado = 0

-------------------------------------------------------------------------------------------------------
--> Passo 02 - AtualizaPainel
--> Atualizar ultima infracao

DECLARE @ultimaInfracao TABLE 
(
	serieEquipamento INT, 
	idPista INT, 
	ultima_data DATETIME
)

-- copia locais e pistas da tabela painel_contrato 
INSERT INTO @ultimaInfracao (
	serieEquipamento, 
	idPista, 
	ultima_data)
SELECT 
	pc.numeroSerie, 
	idPista, 
	NULL
FROM painel_contrato pc (NOLOCK)

-- atualiza os dados da última infração na tabela temporária
UPDATE @ultimaInfracao 
SET ultima_data = ui.data_ultima_infracao
FROM painel_ultima_infracao ui (NOLOCK)
WHERE	serieEquipamento = ui.serie_equipamento
	AND idPista = ui.id_pista 

-- atualiza tabela painel_contrato		
UPDATE painel_contrato with (ROWLOCK)
SET dataUltimaInfracao = ui.ultima_data
FROM @ultimaInfracao ui
WHERE 	painel_contrato.numeroSerie = ui.serieEquipamento
	AND painel_contrato.idPista = ui.idPista 

-------------------------------------------------------------------------------------------------------
--> Passo 03 - AtualizaPainel
--> 

UPDATE painel_contrato WITH (ROWLOCK) 
SET	dataUltimaDesconexao = NULL

UPDATE painel_contrato with (ROWLOCK)
SET dataUltimaDesconexao = ld.data_ultima_desconexao
FROM painel_local_desconectado ld (NOLOCK)
WHERE painel_contrato.idLocal = ld.id_local

-------------------------------------------------------------------------------------------------------
--> Passo 04 - AtualizaPainel
--> Atualizar ult. arquivo

UPDATE painel_contrato with (ROWLOCK)
SET dataUltimoArquivo = NULL

UPDATE painel_contrato with (ROWLOCK)
SET dataUltimoArquivo = ultArq.data_ultimo_arquivo
FROM painel_ultimo_arquivo ultArq (NOLOCK)
WHERE painel_contrato.idLocal = ultArq.id_local


