
--DROP TABLE log_finaliza_resumo

--GO

--CREATE TABLE log_finaliza_resumo (
--dia DATE NOT NULL,
--infracao INT,
--remessa  INT,
--imagens  INT,
--video    INT
--);

--GO

-- SELECT * FROM log_finaliza_resumo

CREATE PROCEDURE [dbo].[spu_atualiza_log_finaliza_resumo] AS

 -- PASSO 1 : Obter o primeiro FINALIZA MOVIMENTO da noite
DECLARE @tab_id_log		AS TABLE (id_log INT, primeiro BIT)
DECLARE @tab_id_log_dia AS TABLE (id_log INT)
DECLARE @tab_log_dia	AS TABLE (id_log_detalhe INT, texto_log VARCHAR(500), quantidade INT)
DECLARE @tab_log_dia_or	AS TABLE (ordem INT, id_log_detalhe INT, texto_log VARCHAR(500), quantidade INT)
DECLARE @id_log_inicio	INT

INSERT INTO @tab_id_log
 SELECT lf.id_log,
 CASE WHEN EXISTS(SELECT 1 FROM log_finaliza (NOLOCK) WHERE id_log = lf.id_log - 1 AND finaliza_movimento = 0) THEN 1 ELSE 0 END AS primeiro
 FROM log_finaliza lf 
 WHERE lf.finaliza_movimento = 1

 SELECT TOP(1) @id_log_inicio = id_log FROM @tab_id_log
 WHERE primeiro = 1
 ORDER BY id_log DESC 

-- PASSO 2 : Obter as execuções do FINALIZA do dia
 INSERT INTO @tab_id_log_dia
 SELECT id_log FROM @tab_id_log WHERE id_log >= @id_log_inicio

-- PASSO 3 : Obter resumo da execução do FINALIZA destas
INSERT INTO @tab_log_dia
SELECT MAX(lfd.id_log_detalhe) AS id_log_detalhe, lfd.texto_log, SUM(lfd.quantidade) AS quantidade FROM log_finaliza lf 
JOIN log_finaliza_detalhe lfd ON lf.id_log = lfd.id_log
WHERE lf.id_log IN (SELECT id_log FROM @tab_id_log_dia)
AND lfd.nome_procedure = 'spu_finaliza_importacao_movimento_lote'
GROUP BY lfd.texto_log

INSERT INTO @tab_log_dia_or
SELECT * FROM 
(
SELECT ROW_NUMBER() OVER (ORDER BY id_log_detalhe) AS ordem, * FROM @tab_log_dia
) AS sub1

DECLARE @infracao INT
DECLARE @remessa  INT
DECLARE @imagens  INT
DECLARE @video    INT
DECLARE @dia	  DATE

SELECT @dia      = (SELECT CAST(MIN(hora_inicio) AS DATE) FROM log_finaliza (NOLOCK) WHERE id_log IN (SELECT id_log FROM @tab_id_log_dia))

SELECT @infracao = quantidade FROM @tab_log_dia_or WHERE ordem = 3
SELECT @remessa  = quantidade FROM @tab_log_dia_or WHERE ordem = 4
SELECT @imagens  = quantidade FROM @tab_log_dia_or WHERE ordem = 6
SELECT @video    = quantidade FROM @tab_log_dia_or WHERE ordem = 10

IF EXISTS(SELECT 1 FROM log_finaliza_resumo WHERE dia = @dia) 
	UPDATE log_finaliza_resumo SET infracao = @infracao, remessa = @remessa, imagens = @imagens, video = @video WHERE dia = @dia
ELSE
	INSERT INTO log_finaliza_resumo VALUES (@dia, @infracao, @remessa, @imagens, @video)
