CREATE FUNCTION [dbo].[fcn_getInfracaoHorarioValida] (@id_infracao INT)
RETURNS INT
AS
BEGIN

    DECLARE @status BIT

    SET @status = 0

    --DECLARE temp_cur CURSOR FOR
    --DECLARE @id_infracao INT = 106639, @status BIT
    SELECT @status = CASE WHEN EXISTS(
    SELECT 1
    FROM   infracao i (NOLOCK)
           INNER JOIN local l (NOLOCK)
                ON  l.id_local = i.id_local
                    AND l.sequencia_local = i.sequencia_local
           INNER JOIN configuracao_equipamento_regra_infracao ceri (NOLOCK)
                ON  ceri.id_configuracao_equipamento = l.id_configuracao_equipamento
           INNER JOIN enquadramento_regra_infracao eri (NOLOCK)
                ON  eri.id_enquadramento = i.id_enquadramento
                    AND eri.tipo = ceri.tipo
    WHERE  i.id_infracao = @id_infracao
           AND ceri.ativo = 1
           AND CAST(i.data AS TIME) BETWEEN CAST(ceri.hora_ini AS TIME) AND CAST(ceri.hora_fim AS TIME)) THEN 1 ELSE 0 END

    --SELECT @status

    RETURN @status

END
