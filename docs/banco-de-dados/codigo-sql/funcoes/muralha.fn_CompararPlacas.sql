CREATE FUNCTION muralha.fn_CompararPlacas
(
    @Placa1 VARCHAR(7),
    @Placa2 VARCHAR(7)
)
RETURNS TABLE
AS
RETURN
(
	--DECLARE @Placa1 VARCHAR(7) = 'ABC1234', @Placa2 VARCHAR(7) = 'ADC7234'
    SELECT  
        @Placa1 AS placa1,
        @Placa2 AS placa2,
		CASE WHEN @Placa1 <> @Placa2 THEN 1 ELSE 0 END AS semelhante,
        -- Conta diferenças
        (SELECT COUNT(*) 
         FROM   (
					SELECT TOP (CASE WHEN LEN(@Placa1) >= LEN(@Placa2) THEN LEN(@Placa1) ELSE LEN(@Placa2) END) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
					FROM   sys.all_objects
				) nums
		 WHERE  SUBSTRING(UPPER(REPLACE(@Placa1,'-','')), n, 1) <> SUBSTRING(UPPER(REPLACE(@Placa2,'-','')), n, 1)) AS diferencas,
        -- Lista diferenças
        STUFF((
				SELECT '; ' + 'Pos ' + CAST(n AS VARCHAR) + ': ' + SUBSTRING(@Placa1, n, 1) + ' vs ' + SUBSTRING(@Placa2, n, 1)
				FROM   (
							SELECT TOP (10) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n FROM sys.columns
					   ) nums
				WHERE  SUBSTRING(@Placa1, n, 1) <> SUBSTRING(@Placa2, n, 1)
				FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS caracteres_diferentes
);