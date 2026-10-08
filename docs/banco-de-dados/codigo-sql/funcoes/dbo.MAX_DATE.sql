CREATE FUNCTION [dbo].[MAX_DATE]
(
	@date_a datetime,
    @date_b datetime 
)
RETURNS datetime
AS

BEGIN

	IF ( @date_a IS NULL )
		RETURN @date_b 
	ELSE IF ( @date_b IS NULL )
		RETURN @date_a 
	ELSE IF (@date_a >= @date_b)
		RETURN @date_a
	ELSE IF (@date_a < @date_b)
		RETURN @date_b
	ELSE
		RETURN NULL
		
	RETURN NULL -- deve ter, se não não compila

END




