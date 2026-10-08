  CREATE   FUNCTION dbo.fn_FormatarSegundosEmDHM (
      @total_segundos BIGINT
  )
  RETURNS VARCHAR(100)
  AS
  BEGIN
      -- Retorna NULL se a entrada for inválida ou negativa
      IF @total_segundos IS NULL OR @total_segundos < 0
          RETURN NULL;

      -- Se for zero, retorna o formato zerado para consistência
      IF @total_segundos = 0
          RETURN '00d 00h 00m 00s';

      DECLARE @dias BIGINT, @horas BIGINT, @minutos BIGINT, @segundos BIGINT;

      -- Calcula os componentes de tempo
      SET @dias = @total_segundos / 86400;
      SET @total_segundos = @total_segundos % 86400;
      SET @horas = @total_segundos / 3600;
      SET @total_segundos = @total_segundos % 3600;
      SET @minutos = @total_segundos / 60;
      SET @segundos = @total_segundos % 60;

      -- Concatena e formata a string de saída com padding de zero
      RETURN CONCAT(
          FORMAT(@dias, '00'), 'd ',
          FORMAT(@horas, '00'), 'h ',
          FORMAT(@minutos, '00'), 'm ',
          FORMAT(@segundos, '00'), 's'
      );
  END;
