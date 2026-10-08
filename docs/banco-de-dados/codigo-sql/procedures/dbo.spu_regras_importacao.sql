
CREATE PROCEDURE [dbo].[spu_regras_importacao] AS

DECLARE @id_regra INT
DECLARE @dias_atraso INT
DECLARE @veiculos_atraso TABLE (id_veiculo_unic BIGINT)
DECLARE @num_veiculos INT

SET NOCOUNT ON

-- Verifica se ha regra para o dia (feriados, pontes, etc)
SELECT @id_regra = id_regra FROM regras_importacao 
WHERE 
data = CAST(GETDATE() AS DATE)

-- Verifica se ha regra para o dia da semana e horario (caso nao haja regra para o dia)
SELECT @id_regra = COALESCE(@id_regra, id_regra) FROM regras_importacao 
WHERE 
(dia_semana = DATEPART(WEEKDAY,GETDATE())
AND
((hora_ini IS NULL AND hora_fim IS NULL)
OR
(CAST(GETDATE() AS TIME) BETWEEN hora_ini AND hora_fim)))

-- Obtem os dias de atraso baseado na regra encontrada
SELECT @dias_atraso = atraso FROM regras_importacao WHERE id_regra = @id_regra

PRINT 'REGRA: ' + CAST(@id_regra AS VARCHAR)
PRINT 'ATRASO: ' + CAST(@dias_atraso AS VARCHAR)

-- Obtem todos os veiculos atrasados
INSERT INTO @veiculos_atraso
SELECT vi.id_veiculo_unic AS atraso 
FROM veiculo_importacao vi (NOLOCK)
JOIN infracao_importacao ii (NOLOCK) ON vi.id_veiculo_unic = ii.id_veiculo_unic
WHERE 
DATEDIFF(DAY,vi.data,GETDATE()) > @dias_atraso -- Atraso maior que X dias
AND 
ii.id_enquadramento > 1 -- Enquadramento de Infracao
AND 
vi.importar = 1 -- Veiculos configurados para importar
GROUP BY vi.id_veiculo_unic

SET @num_veiculos = @@rowcount

PRINT 'VEICULOS ATRASO: ' + CAST(@num_veiculos AS VARCHAR)

IF @num_veiculos > 0  
BEGIN

BEGIN TRY
BEGIN TRAN

UPDATE veiculo_importacao SET importar = 0 WHERE id_veiculo_unic IN 
(SELECT id_veiculo_unic FROM @veiculos_atraso)

PRINT 'VEICULOS ATUALIZADOS: ' + CAST(@@rowcount AS VARCHAR)

INSERT INTO regra_importacao_veiculo (id_regra, id_veiculo_unic)
SELECT @id_regra AS id_regra, va.id_veiculo_unic FROM @veiculos_atraso va
LEFT JOIN regra_importacao_veiculo riv ON va.id_veiculo_unic = riv.id_veiculo_unic
WHERE riv.id_veiculo_unic IS NULL

PRINT 'VEICULOS REGISTRADOS: ' + CAST(@@rowcount AS VARCHAR)

COMMIT
END TRY
BEGIN CATCH

PRINT 'ERRO ENCONTRADO'

IF @@TRANCOUNT > 0
ROLLBACK

END CATCH
END

