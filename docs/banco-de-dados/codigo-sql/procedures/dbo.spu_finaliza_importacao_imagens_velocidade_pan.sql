
--- Finalizar imagens de equipamento Velsis com Velocidade com Panorâmica 

CREATE PROCEDURE [dbo].[spu_finaliza_importacao_imagens_velocidade_pan]
AS
BEGIN

BEGIN TRY
BEGIN TRAN

DECLARE @total_inf INT = 0
DECLARE @sequencia TABLE (id_veiculo_unic BIGINT, id_infracao INT, id_veiculo INT, id_imagem_local INT, id_imagem INT, indice_imagem INT, imagem IMAGE, id_tipo_imagem INT, formato VARCHAR(3))

INSERT INTO @sequencia
SELECT vi.id_veiculo_unic, i.id_infracao, v.id_veiculo, img.id_imagem_local, img.id_imagem, img.indice_imagem, img.imagem, tim.id_tipo_imagem, img.formato 
FROM 
veiculo_importacao vi (NOLOCK)
JOIN infracao_importacao ii (NOLOCK) 
	ON vi.id_veiculo_unic = ii.id_veiculo_unic
JOIN imagem_importacao img (NOLOCK)
	ON vi.id_veiculo_unic = img.id_veiculo_unic AND img.indice_imagem = 1
JOIN tipo_imagem tim	(NOLOCK) 					
	ON tim.nome = img.nome AND tim.numero = img.numero
LEFT JOIN veiculo_importacao_estatico vie (NOLOCK)
	ON vi.id_veiculo_unic = vie.id_veiculo_unic 
JOIN veiculo v (NOLOCK) 
	ON vi.id_veiculo_local = v.id_veiculo_local
	AND vi.id_local = v.id_local
	AND vi.data = v.data 
JOIN infracao i (NOLOCK)
	ON v.id_veiculo = i.id_veiculo
	AND i.id_enquadramento = ii.id_enquadramento 
JOIN enquadramento e (NOLOCK) 
	ON e.id_enquadramento = i.id_enquadramento 
LEFT JOIN veiculo_imagem vei (NOLOCK)
	ON vei.id_imagem = img.id_imagem 
	--AND vei.id_imagem_local = img.id_imagem_local
	AND vei.id_veiculo = v.id_veiculo
WHERE 
e.infracao_metrologia = 1
AND vei.id_veiculo IS NULL 
--OPTION (MAXDOP 1)
--ORDER BY vi.data 

SET @total_inf = @@ROWCOUNT 
PRINT 'Imagens Panorâmicas encontradas: ' + CAST(@total_inf AS VARCHAR)

IF @total_inf > 0  
BEGIN

INSERT INTO imagem_info WITH(ROWLOCK) (id_imagem, id_tipo_imagem, formato)
	SELECT 
		id_imagem, id_tipo_imagem, formato
	FROM
		@sequencia
							
SET @total_inf = @@ROWCOUNT 
PRINT 'imagem_info: ' + CAST(@total_inf AS VARCHAR)
										
INSERT INTO imagem WITH(ROWLOCK) (id_imagem, imagem, indice_imagem, ds_caminho)
	SELECT 
		id_imagem, imagem, indice_imagem, null AS ds_caminho 
	FROM
		@sequencia

SET @total_inf = @@ROWCOUNT 
PRINT 'imagem: ' + CAST(@total_inf AS VARCHAR)

INSERT INTO veiculo_imagem WITH(ROWLOCK) (id_veiculo, id_imagem, id_imagem_local)
	SELECT 
		id_veiculo, id_imagem, id_imagem_local 		
	FROM
		@sequencia

SET @total_inf = @@ROWCOUNT 
PRINT 'veiculo_imagem: ' + CAST(@total_inf AS VARCHAR)

DELETE imagem_importacao	WITH(ROWLOCK)
FROM imagem_importacao ii	WITH(ROWLOCK)
JOIN @sequencia vpi ON vpi.id_veiculo_unic = ii.id_veiculo_unic 				

DELETE infracao_importacao	WITH(ROWLOCK)
FROM infracao_importacao ii WITH(ROWLOCK)
JOIN @sequencia vpi ON vpi.id_veiculo_unic = ii.id_veiculo_unic 				
		
DELETE veiculo_importacao_estatico 
FROM veiculo_importacao_estatico vie WITH(ROWLOCK)
JOIN @sequencia vpi ON vpi.id_veiculo_unic = vie.id_veiculo_unic 				

DELETE veiculo_importacao	WITH(ROWLOCK)
FROM veiculo_importacao vi	WITH(ROWLOCK)
JOIN @sequencia vpi ON vpi.id_veiculo_unic = vi.id_veiculo_unic 		

END

COMMIT
--ROLLBACK

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


--EXEC spu_finaliza_importacao_imagens_velocidade_pan
