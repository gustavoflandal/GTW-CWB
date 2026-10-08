CREATE PROCEDURE [dbo].[spu_gerencia_contrato_atualiza_imagens_defeituosas]
AS
DECLARE @temp TABLE 
(
	id_alerta INT, 
	id_local INT, 
	id_pista INT, 
	valor INT, 
	alerta BIT DEFAULT 0
)

-- % Imagens Diurnas Defeituosas                     
INSERT INTO @temp (
	id_alerta, 
	id_local, 
	id_pista, valor)           
SELECT 
	1,
	imgDefDia.id_local,
	imgDefDia.id_pista,
	CAST((
		CASE WHEN 
			imgDefDia.trafegoImagemCapturada > 0 
		THEN 
			(imgDefDia.trafegoImagemDef * 100.0) / (imgDefDia.trafegoImagemCapturada) 
		ELSE 
			0 
		END) AS INT) 
FROM 
	fcn_LocalImagensDefeituosa( GETDATE() - 1, GETDATE() , '07:00', '17:00') imgDefDia 
		
-- % Imagens Noturnas Defeituosas
INSERT INTO @temp (
	id_alerta, 
	id_local, 
	id_pista, valor)           
SELECT 
	2,
    imgDefNoite.id_local,
    imgDefNoite.id_pista,
    CAST((
		CASE WHEN 
			imgDefNoite.trafegoImagemCapturada > 0 
		THEN 
			(imgDefNoite.trafegoImagemDef * 100.0) / (imgDefNoite.trafegoImagemCapturada) 
		ELSE 
			0 
		END) AS INT)
FROM 
	fcn_LocalImagensDefeituosa( GETDATE() - 1, GETDATE() , '17:00', '07:00') imgDefNoite
		
-- % Imagens Diurnas com Qualidade Ruim
INSERT INTO @temp (
	id_alerta, 
	id_local, 
	id_pista, valor)           
SELECT 
	10,
    imgDefDia.id_local,
    imgDefDia.id_pista,
	CAST((
		CASE WHEN 
			imgDefDia.trafegoOCRprocessado > 0 
		THEN 
			(imgDefDia.sem_placa_reconhecida * 100.0) / (imgDefDia.trafegoOCRprocessado) 
		ELSE 0 END) AS INT)
FROM 
	fcn_LocalImagensDefeituosa( GETDATE() - 1, GETDATE() , '07:00', '17:00') imgDefDia
		
-- % Imagens Noturnas Qualidade Ruim
INSERT INTO @temp (
	id_alerta, 
	id_local, 
	id_pista, valor)           
SELECT 
	11,
    imgDefNoite.id_local,
    imgDefNoite.id_pista,
	CAST((
		CASE WHEN 
			imgDefNoite.trafegoOCRprocessado > 0 
		THEN 
			(imgDefNoite.sem_placa_reconhecida * 100.0) / (imgDefNoite.trafegoOCRprocessado) 
		ELSE 0 END) AS INT)
FROM fcn_LocalImagensDefeituosa( GETDATE() - 1, GETDATE() , '17:00', '07:00') imgDefNoite
	
-- ajusta os alertas
UPDATE @temp 
SET alerta = 1 
WHERE	id_alerta = 1 
	AND valor > 15

UPDATE @temp 
SET alerta = 1 
WHERE	id_alerta = 2 
	AND valor > 40

UPDATE @temp 
SET alerta = 1 
WHERE	id_alerta = 10 
	AND valor > 80

UPDATE @temp 
SET alerta = 1 
WHERE	id_alerta = 11 
	AND valor > 95

INSERT INTO gerencia_contrato_alerta with (rowlock)
    (id_alerta,
	serie_equipamento,
	id_pista,
    valor,
    alerta)
SELECT
	t.id_alerta,
	lv.serie_equipamento,
	t.id_pista,
	STR(t.valor),
	t.alerta
FROM @temp t
	INNER JOIN local_vigente lv (nolock)
		ON lv.id_local = t.id_local



