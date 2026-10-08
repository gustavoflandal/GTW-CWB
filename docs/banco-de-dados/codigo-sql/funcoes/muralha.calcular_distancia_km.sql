CREATE FUNCTION muralha.calcular_distancia_km(
    @lat1 DECIMAL(20,10),
    @lon1 DECIMAL(20,10),
    @lat2 DECIMAL(20,10), 
    @lon2 DECIMAL(20,10)
)
RETURNS DECIMAL(10,3)
AS
BEGIN
    DECLARE @R DECIMAL(10,6) = 6371;
    DECLARE @dLat DECIMAL(20,10) = RADIANS(@lat2 - @lat1);
    DECLARE @dLon DECIMAL(20,10) = RADIANS(@lon2 - @lon1);
    
    DECLARE @a DECIMAL(20,10) = 
        SIN(@dLat/2) * SIN(@dLat/2) +
        COS(RADIANS(@lat1)) * COS(RADIANS(@lat2)) * 
        SIN(@dLon/2) * SIN(@dLon/2);
        
    DECLARE @c DECIMAL(20,10) = 2 * ATN2(SQRT(@a), SQRT(1-@a));
    
    RETURN @R * @c;
END;