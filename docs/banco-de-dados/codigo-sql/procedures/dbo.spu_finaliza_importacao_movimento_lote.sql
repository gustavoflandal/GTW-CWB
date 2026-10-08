CREATE PROCEDURE [dbo].[spu_finaliza_importacao_movimento_lote]
AS
BEGIN

	SET NOCOUNT ON

	DECLARE @finaliza_ini TIME = '05:00:00', @finaliza_fim TIME = '19:00:00'

	IF EXISTS(SELECT 1 FROM chave_valor (NOLOCK) WHERE chave = 'finaliza_ini')
		SELECT @finaliza_ini = CAST(valor AS TIME) FROM chave_valor (NOLOCK) WHERE chave = 'finaliza_ini'
	ELSE 
		INSERT INTO chave_valor WITH(ROWLOCK) VALUES ('finaliza_ini', CAST(@finaliza_ini AS VARCHAR(8)))

	IF EXISTS(SELECT 1 FROM chave_valor (NOLOCK) WHERE chave = 'finaliza_fim')
		SELECT @finaliza_fim = CAST(valor AS TIME) FROM chave_valor (NOLOCK) WHERE chave = 'finaliza_fim'
	ELSE 
		INSERT INTO chave_valor WITH(ROWLOCK) VALUES ('finaliza_fim', CAST(@finaliza_fim AS VARCHAR(8)))

	IF CAST(GETDATE() AS TIME) BETWEEN @finaliza_ini AND @finaliza_fim
		PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' PULANDO FINALIZA MOVIMENTO LOTE POR CAUSA DO HORARIO ( ' + CONVERT(VARCHAR, @finaliza_ini, 108) + ' - ' + CONVERT(VARCHAR, @finaliza_fim, 108) + ' )'
	ELSE
BEGIN

	PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' MOVER REGISTROS DUPLICADOS (SET id_local = 9999) - VEICULO_IMPORTACAO: '

	UPDATE veiculo_importacao
	SET    id_local = 9999
	WHERE  id_veiculo IN (
				SELECT sub1.id_veiculo
				FROM   (
							SELECT id_veiculo, vi.id_veiculo_local, vi.id_local, vi.data
							FROM   veiculo_importacao vi (NOLOCK)
								   JOIN (
											SELECT id_veiculo_local, id_local, data
											FROM   veiculo_importacao (NOLOCK)
											GROUP BY id_veiculo_local, id_local, data
											HAVING COUNT(id_veiculo) > 1
										) AS sub1
											ON sub1.id_veiculo_local = vi.id_veiculo_local  AND sub1.id_local = vi.id_local AND sub1.data = vi.data

						) AS sub1
					    LEFT JOIN (
									SELECT MAX(id_veiculo) id_veiculo, id_veiculo_local, id_local, data
									FROM   veiculo_importacao (NOLOCK)
									GROUP BY id_veiculo_local, id_local, data
									HAVING COUNT(id_veiculo) > 1
						) AS sub2 ON sub1.id_veiculo = sub2.id_veiculo
				WHERE sub2.id_veiculo IS NULL
	) AND id_local <> 9999 

	PRINT @@ROWCOUNT 


   BEGIN TRY -- rollback select @@trancount 
   BEGIN TRANSACTION 
   
PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' INSERIR VEICULO'

 INSERT INTO veiculo WITH(ROWLOCK) (id_veiculo_local, data, placa, velocidade, 
 comprimento, pista, flag, segundos, id_veiculo_unic, id_classe, 
 id_local, sequencia_local, ocupacao, id_arquivo)--, entre_faixa)  
 SELECT 
  mi.id_veiculo_local,
  mi.data_hora AS [data],
  COALESCE(vi.placa, mi.placa) AS [placa],
  mi.velocidade_constatada AS [velocidade],
  COALESCE(vi.comprimento, 0) AS [comprimento],
  mi.pista,
  0 AS flag,
  COALESCE(vi.segundos, mt.tempo_decor_verm, 0) AS [segundos],
  COALESCE(vi.id_veiculo_unic, CAST((RAND(mi.id_veiculo_local + mi.id_enquadramento) * 1e18) AS BIGINT)) AS id_veiculo_unic,
  COALESCE(vi.id_classe, 
	CASE cv.id_tipo 
	WHEN 14 THEN 'C'
	WHEN 13 THEN 'T'
	WHEN 4  THEN 'M'
	WHEN 8  THEN 'O'
	WHEN 6  THEN 'P'
	ELSE ' '
	END) AS [id_classe],
  mi.id_local,
  null AS sequencia_local, -- XXX!
  COALESCE(vi.ocupacao, 0) AS [ocupacao],
  ma.id_movimento_arquivo AS [id_arquivo]--,
  --vi.entre_faixa
 FROM movimento_arquivo ma (NOLOCK) 
 JOIN enquadramento_regra_infracao eri (NOLOCK) 
  ON eri.tipo_apait = SUBSTRING(ma.nome_arquivo,3,2) 
 JOIN movimento_importacao mi (NOLOCK) 
  ON ma.id_movimento_arquivo = mi.id_movimento_arquivo 
  AND ma.id_movimento = mi.id_movimento
  AND ma.data_movimento = mi.data_movimento 
  AND mi.id_enquadramento = eri.id_enquadramento
 JOIN movimento_tarja mt (NOLOCK) 
  ON mi.data_hora = mt.data_infracao 
  AND mi.id_veiculo_local = mt.id_veiculo_local 
  AND mi.id_enquadramento = mt.id_enquadramento 
 JOIN movimento_arquivo mta (NOLOCK) 
  ON mt.id_movimento_arquivo = mta.id_movimento_arquivo
  AND mta.indice_imagem = 0
  --AND mta.id_movimento = mi.id_movimento       (custo alto)
  --AND mta.data_movimento = mi.data_movimento   
 LEFT JOIN veiculo_importacao vi (NOLOCK) 
  ON mi.id_veiculo_local = vi.id_veiculo_local
  AND mi.id_local = vi.id_local 
  AND mi.data_hora = vi.data
 LEFT JOIN remessa r (NOLOCK) 
  ON mi.id_movimento = r.codigo_externo
  AND mi.id_enquadramento = r.id_enquadramento
  AND mi.data_movimento = r.data 
 LEFT JOIN infracao_remessa ir (NOLOCK)
  ON r.id_remessa = ir.id_remessa  
  AND ir.sequencia = mi.sequencia 
 LEFT JOIN cad_veiculo cv (NOLOCK) 
  ON mi.placa = cv.placa 
 WHERE r.id_remessa IS NULL OR ir.id_infracao IS NULL 
 
 PRINT @@ROWCOUNT 

 PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' INSERIR INFRACAO'
 
 INSERT INTO infracao WITH(ROWLOCK) (id_imagem_local, placa, id_enquadramento, id_inconsistencia, id_processo_concluido,
 id_processo, id_veiculo, id_local, sequencia_local, pista, data, id_usuario_atual, id_usuario_final, velocidade_limite,
 velocidade_considerada, segundos_tolerancia, tempo_vermelho_detec, data_afericao)
 SELECT 
 mi.id_veiculo_local AS [id_imagem_local],
 mi.placa,
 mi.id_enquadramento,
 mi.id_inconsistencia,
 4 AS [id_processo_concluido], --XXX!
 3 AS [id_processo], --XXX!
 v.id_veiculo,
 v.id_local,
 null AS sequencia_local,
 v.pista,
 v.data AS [data],
 null AS id_usuario_atual,
 null AS id_usuario_final,
 mi.velocidade_regulamentada AS [velocidade_limite],
 mi.velocidade_considerada,
 mt.tempo_perm AS [segundos_tolerancia],
 mt.tempo_decor_verm AS [tempo_vermelho_detec],
 mt.data_afericao AS [data_afericao]
 --SELECT v.id_veiculo, mi.id_enquadramento
 FROM movimento_arquivo ma (NOLOCK) 
 JOIN enquadramento_regra_infracao eri (NOLOCK) 
  ON eri.tipo_apait = SUBSTRING(ma.nome_arquivo,3,2) 
 JOIN veiculo v (NOLOCK)
  ON ma.id_movimento_arquivo = v.id_arquivo 
 LEFT JOIN infracao inf (NOLOCK) 
  ON v.id_veiculo = inf.id_veiculo 
 JOIN movimento_importacao mi (NOLOCK)
  ON mi.id_movimento_arquivo = v.id_arquivo 
  AND mi.id_veiculo_local = v.id_veiculo_local 
  AND mi.data_hora = v.data
  AND mi.id_local = v.id_local
  AND ma.id_movimento = mi.id_movimento
  AND ma.data_movimento = mi.data_movimento 
  AND mi.id_enquadramento = eri.id_enquadramento
 JOIN movimento_tarja mt (NOLOCK) 
  ON v.data = mt.data_infracao 
  AND v.id_veiculo_local = mt.id_veiculo_local 
  AND mi.id_enquadramento = mt.id_enquadramento 
 JOIN movimento_arquivo mta (NOLOCK) 
  ON mt.id_movimento_arquivo = mta.id_movimento_arquivo
  AND mta.indice_imagem = 0
 WHERE inf.id_infracao IS NULL 
 
 PRINT @@ROWCOUNT 

 PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' INSERIR REMESSA'
 
 INSERT INTO remessa WITH(ROWLOCK) (codigo_externo, data, id_processo, data_inicial, data_final, total_infracao, tipo, revisao, data_exportacao, id_usuario, id_enquadramento, id_movimento_arquivo)
 SELECT 
 ma.id_movimento AS [codigo_externo],
 CAST(ma.data_movimento AS DATETIME) AS [data],
 4 AS [id_processo],
 MIN(mi.data_hora) AS [data_inicial],
 MAX(mi.data_hora) AS [data_final],
 ma.numero_registros AS [total_infracao],
 SUBSTRING(ma.nome_arquivo, 3, 2) AS [tipo],
 ma.revisao,
 CAST(ma.data_movimento AS DATETIME) AS [data_exportacao],
 MAX(mi.cod_operador) AS [id_usuario],
 mi.id_enquadramento,
 mi.id_movimento_arquivo  
 FROM movimento_arquivo ma (NOLOCK) 
 JOIN enquadramento_regra_infracao eri (NOLOCK) 
  ON eri.tipo_apait = SUBSTRING(ma.nome_arquivo,3,2) 
 JOIN movimento_importacao mi (NOLOCK)
  ON ma.id_movimento_arquivo = mi.id_movimento_arquivo 
  AND ma.id_movimento = mi.id_movimento
  AND ma.data_movimento = mi.data_movimento 
  AND mi.id_enquadramento = eri.id_enquadramento
 LEFT JOIN remessa r (NOLOCK) 
  ON mi.id_movimento = r.codigo_externo 
  AND mi.id_enquadramento = r.id_enquadramento
 WHERE ma.id_tipo = 5 AND r.id_movimento_arquivo IS NULL 
 GROUP BY 
 ma.id_movimento, ma.data_movimento, ma.numero_registros,
 ma.nome_arquivo, ma.revisao, mi.id_enquadramento,
 mi.id_movimento_arquivo 
 
 PRINT @@ROWCOUNT 

 PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' INSERIR INFRACAO_REMESSA'
 
 INSERT INTO infracao_remessa WITH(ROWLOCK) (id_infracao, id_remessa, sequencia) 
 SELECT 
 i.id_infracao,
 r.id_remessa,
 mi.sequencia   
 FROM movimento_arquivo ma (NOLOCK)
 JOIN veiculo v (NOLOCK) 
  ON ma.id_movimento_arquivo = v.id_arquivo 
 JOIN infracao i (NOLOCK) 
  ON v.id_veiculo = i.id_veiculo
 JOIN movimento_importacao mi (NOLOCK) 
  ON ma.id_movimento_arquivo = mi.id_movimento_arquivo 
  AND mi.id_local = v.id_local
  AND mi.id_veiculo_local = v.id_veiculo_local 
  AND mi.data_hora = v.data -- XX
  AND mi.id_enquadramento = i.id_enquadramento 
 JOIN remessa r (NOLOCK) 
  ON ma.id_movimento = r.codigo_externo
  AND i.id_enquadramento = r.id_enquadramento
 LEFT JOIN infracao_remessa ir (NOLOCK) 
  ON i.id_infracao = ir.id_infracao
 WHERE ir.id_infracao IS NULL 
  
  PRINT @@ROWCOUNT 

-- IMAGEM
  
PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' BUSCANDO IMAGENS'

DECLARE @sequencia TABLE (id_veiculo INT, id_imagem_local INT, id_imagem INT, indice_imagem INT, ds_caminho VARCHAR(255), id_tipo_imagem INT, formato VARCHAR(3))

INSERT INTO @sequencia (id_veiculo, id_imagem_local, indice_imagem, ds_caminho, id_tipo_imagem, formato) 
SELECT DISTINCT 
v.id_veiculo,
v.id_veiculo_local,
mai.indice_imagem,
mai.ds_caminho,
mai.indice_imagem + 1 AS [id_tipo_imagem], --XXX!
mta.extensao AS [formato] 
FROM movimento_arquivo ma (NOLOCK) 
JOIN veiculo v (NOLOCK)
 ON  ma.id_movimento_arquivo = v.id_arquivo
JOIN infracao i (NOLOCK)
 ON i.id_veiculo = v.id_veiculo 
JOIN infracao_remessa ir (NOLOCK)
 ON i.id_infracao = ir.id_infracao 
JOIN remessa r (NOLOCK)
 ON ir.id_remessa = r.id_remessa 
JOIN enquadramento_regra_infracao eri (NOLOCK) 
 ON eri.id_enquadramento = i.id_enquadramento 
JOIN movimento_arquivo mai (NOLOCK)
 ON r.codigo_externo = mai.id_movimento
 AND ir.sequencia = mai.sequencia
 AND mai.id_tipo = 1 
 AND mai.data_movimento = ma.data_movimento 
JOIN movimento_tipo_arquivo mta (NOLOCK) 
 ON mai.id_tipo = mta.id_tipo 
LEFT JOIN imagem img (NOLOCK) 
 ON mai.ds_caminho = img.ds_caminho
WHERE img.id_imagem IS NULL AND SUBSTRING(mai.nome_arquivo,3,2) = eri.tipo_apait

PRINT @@ROWCOUNT 

DECLARE @seq_max INT = (SELECT COALESCE(MAX(id_imagem), 0) + 1 FROM imagem_info (NOLOCK))
DECLARE @t_ds_caminho VARCHAR(255)

WHILE EXISTS(SELECT 1 FROM @sequencia WHERE id_imagem IS NULL)
BEGIN
SELECT TOP 1 @t_ds_caminho = ds_caminho FROM @sequencia WHERE id_imagem IS NULL 
UPDATE @sequencia SET id_imagem = @seq_max WHERE ds_caminho = @t_ds_caminho 
SET @seq_max = @seq_max + 1
END

PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' INSERIR IMAGEM_INFO'

INSERT INTO imagem_info WITH(ROWLOCK) (id_imagem, id_tipo_imagem, formato)
SELECT seq.id_imagem, seq.id_tipo_imagem, seq.formato FROM @sequencia seq 

PRINT @@ROWCOUNT 

PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' INSERIR IMAGEM'

INSERT INTO imagem WITH(ROWLOCK) (id_imagem, indice_imagem, ds_caminho) 
SELECT seq.id_imagem, seq.indice_imagem, seq.ds_caminho FROM @sequencia seq

PRINT @@ROWCOUNT 

PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' INSERIR VEICULO_IMAGEM'

INSERT INTO veiculo_imagem WITH(ROWLOCK) (id_imagem, id_veiculo, id_imagem_local) 
SELECT seq.id_imagem, seq.id_veiculo, seq.id_imagem_local  FROM @sequencia seq

PRINT @@ROWCOUNT 

PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' INSERIR VIDEO E VEICULO_VIDEO'

DECLARE @video_imp TABLE (id_veiculo INT, id_video INT, ds_caminho VARCHAR(255), formato CHAR(5))

INSERT INTO @video_imp (id_veiculo, ds_caminho, formato)
SELECT DISTINCT 
inf.id_veiculo,
mav.ds_caminho,
mta.extensao AS [formato]
FROM movimento_arquivo ma (NOLOCK) 
JOIN movimento_importacao mi (NOLOCK)
 ON ma.id_movimento_arquivo = mi.id_movimento_arquivo
JOIN infracao inf (NOLOCK)
 ON mi.id_veiculo_local = inf.id_imagem_local 
 AND mi.data_hora = inf.data
 AND mi.id_enquadramento = inf.id_enquadramento 
JOIN enquadramento_regra_infracao eri (NOLOCK)
 ON eri.id_enquadramento = inf.id_enquadramento 
JOIN movimento_arquivo mav (NOLOCK)
 ON mi.id_movimento = mav.id_movimento
 AND mi.sequencia = mav.sequencia
 AND mav.id_tipo = 3
 AND mav.data_movimento = ma.data_movimento 
JOIN movimento_tipo_arquivo mta (NOLOCK) 
 ON mav.id_tipo = mta.id_tipo 
LEFT JOIN video v (NOLOCK)
 ON mav.ds_caminho = v.ds_caminho
WHERE v.id_video IS NULL AND SUBSTRING(mav.nome_arquivo,3,2) = eri.tipo_apait

DECLARE @seq_max_v INT = (SELECT COALESCE(MAX(id_video), 0) + 1 FROM video (NOLOCK))
DECLARE @t_ds_caminho_v VARCHAR(255)

WHILE EXISTS(SELECT 1 FROM @video_imp WHERE id_video IS NULL)
BEGIN
SELECT TOP 1 @t_ds_caminho_v = ds_caminho FROM @video_imp WHERE id_video IS NULL 
UPDATE @video_imp SET id_video = @seq_max_v WHERE ds_caminho = @t_ds_caminho_v 
SET @seq_max_v = @seq_max_v + 1
END

INSERT INTO video_info WITH(ROWLOCK) (id_video, id_tipo_video, formato)
SELECT id_video, 1 AS id_tipo_video, formato FROM @video_imp
GROUP BY id_video, formato

PRINT @@ROWCOUNT 

INSERT INTO video WITH(ROWLOCK) (id_video, ds_caminho)
SELECT id_video, ds_caminho FROM @video_imp 
GROUP BY id_video, ds_caminho

PRINT @@ROWCOUNT 

INSERT INTO veiculo_video WITH(ROWLOCK) (id_video, id_veiculo) 
SELECT id_video, id_veiculo FROM @video_imp 
GROUP BY id_video, id_veiculo

PRINT @@ROWCOUNT 

	COMMIT

   END TRY
   BEGIN CATCH

   PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' ERRO ENCONTRADO: Linha [' + CONVERT(VARCHAR, ERROR_LINE()) + '] Mensagem [' + ERROR_MESSAGE() + ']'
   PRINT @@TRANCOUNT
   IF @@TRANCOUNT > 0
   BEGIN
		PRINT CONVERT(VARCHAR, GETDATE(), 108) + ' FAZENDO ROLLBACK'
		ROLLBACK 
   END

   END CATCH

END
END 
