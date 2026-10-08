--ALTER TABLE filtro ADD id_pista INT NULL

CREATE FUNCTION [dbo].[fcn_getSqlVerificaFiltros] 
(
	@id_infracao int, 
    @id_enquadramento int, 
    @id_processo int,
    @id_local int, 
    @id_pista int, 
    @id_classe char(1), 
    @data_ini datetime, 
    @data_fim datetime, 
    @sql_criterio varchar(1000), 
    @select_count bit 
) 
RETURNS nvarchar(max) 
AS 

BEGIN 
 
	DECLARE @SQLString nvarchar(MAX) 
 
	IF @select_count = 1 
		SET @SQLString = N' SELECT @countOUT = count(id_infracao) ' 
	ELSE 
		SET @SQLString = N' SELECT id_infracao ' 
 
	SET @SQLString = @SQLString + N' FROM infracao_completa i WITH (NOLOCK) WHERE 1 = 1 ' 
				 
	IF @id_infracao IS NOT NULL 
		SET @SQLString = @SQLString + N' AND id_infracao = '+ CAST( @id_infracao AS VARCHAR(20) ) 
 
	IF @id_enquadramento IS NOT NULL 
		SET @SQLString = @SQLString + N' AND id_enquadramento = ' + CAST( @id_enquadramento AS VARCHAR(20) ) 

	IF @id_processo IS NOT NULL
		SET @SQLString = @SQLString + N' AND id_processo = ' + CAST( @id_processo AS VARCHAR(20) )
		 
	IF @id_local IS NOT NULL 
		SET @SQLString = @SQLString + N' AND id_local = ' + CAST( @id_local AS VARCHAR(20) ) 
		 
	IF @id_pista IS NOT NULL 
		SET @SQLString = @SQLString + N' AND pista = ' + CAST( @id_pista AS VARCHAR(20) ) 
		 
	IF @id_classe IS NOT NULL 
		SET @SQLString = @SQLString + N' AND id_classe = ''' + @id_classe + '''' 
		 
	IF @data_ini IS NOT NULL 
		SET @SQLString = @SQLString + N' AND data_veiculo >= ''' + CONVERT(CHAR(19), @data_ini, 120) + '''' 
 
	IF @data_fim IS NOT NULL 
		SET @SQLString = @SQLString + N' AND data_veiculo <= ''' + CONVERT(CHAR(19), @data_fim, 120) + '''' 
 
	IF (@sql_criterio IS NOT NULL) AND ( @sql_criterio <> '' ) 
		SET @SQLString = @SQLString + ' AND ( ' + @sql_criterio + ' )' 	 

	RETURN @SQLString 
	 
END



