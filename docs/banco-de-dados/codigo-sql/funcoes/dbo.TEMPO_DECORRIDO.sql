
CREATE FUNCTION [dbo].[TEMPO_DECORRIDO]
(
	@data datetime 
)
RETURNS varchar(max)
AS

BEGIN

	RETURN (CAST( DATEDIFF( minute ,  @data , GETDATE()) / 60 / 24 AS VARCHAR(MAX) ) + 'd ' + 
			CAST( (DATEDIFF( mi ,  @data , GETDATE()) / 60) % 24 AS VARCHAR(MAX) ) + 'h ' + 
			CAST( (DATEDIFF( mi ,  @data , GETDATE()) % 60) AS VARCHAR(MAX) ) + 'min. ' 	
		)

END	






