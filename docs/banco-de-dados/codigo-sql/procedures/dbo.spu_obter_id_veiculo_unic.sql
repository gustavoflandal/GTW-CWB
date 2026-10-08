
CREATE PROCEDURE [dbo].[spu_obter_id_veiculo_unic] 
	@quantidade INT 
AS
BEGIN
	SET NOCOUNT ON;

	-- DROP TABLE #veiculo_unic; DECLARE @quantidade INT = 1000
	CREATE TABLE #veiculo_unic (id_veiculo_unic BIGINT PRIMARY KEY NOT NULL)
	DECLARE @count INT = 0 

	WHILE @count < @quantidade 
	BEGIN 
	INSERT INTO #veiculo_unic
	SELECT new.id_veiculo_unic FROM
	(SELECT CAST(RAND() * 1e18 AS BIGINT) as id_veiculo_unic) AS new
	LEFT JOIN #veiculo_unic vu ON vu.id_veiculo_unic = new.id_veiculo_unic
	WHERE vu.id_veiculo_unic IS NULL

	SET @count = @count + 1
	END

	INSERT INTO #veiculo_unic 
	SELECT id_veiculo_unic * -1 FROM #veiculo_unic

	SELECT vu.id_veiculo_unic
	FROM #veiculo_unic AS vu
	LEFT JOIN veiculo v (NOLOCK) ON vu.id_veiculo_unic = v.id_veiculo_unic
	--LEFT JOIN veiculo_estatistica ve (NOLOCK) ON vu.id_veiculo_unic = ve.id_veiculo_unic
	LEFT JOIN veiculo_importacao vi (NOLOCK) ON vu.id_veiculo_unic = vi.id_veiculo_unic
	WHERE	 v.id_veiculo_unic IS NULL 
	--AND		ve.id_veiculo_unic IS NULL 
	AND		vi.id_veiculo_unic IS NULL 
END
