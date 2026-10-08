CREATE FUNCTION [dbo].[fcn_SplitString] 
( 
    @string NVARCHAR, 
    @delimiter CHAR(1),
	@part INT 
) 
RETURNS VARCHAR  
AS 
BEGIN 
    DECLARE @start INT, @end INT, @count INT = 1 
	DECLARE @ret VARCHAR 
    SELECT @start = 1, @end = CHARINDEX(@delimiter, @string) 
    WHILE @start < LEN(@string) + 1 BEGIN 
        IF @end = 0  
            SET @end = LEN(@string) + 1
       
        IF @count = @part
			SET @ret = SUBSTRING(@string, @start, @end - @start)

        SET @count = @count + 1
		SET @start = @end + 1 
        SET @end = CHARINDEX(@delimiter, @string, @start)
        
    END 
    RETURN CAST(@count AS VARCHAR)--@ret
END
