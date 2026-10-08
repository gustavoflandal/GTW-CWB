
CREATE PROCEDURE [dbo].[spu_verificar_filtros]( @id_infracao INT , @id_processo INT, @id_filtro_aplicado INT OUTPUT , @id_inconsistencia INT OUTPUT , @espera BIT OUTPUT )
AS

BEGIN

	DECLARE @id_filtro INT
	DECLARE @id_enquadramento INT
	DECLARE @id_local INT
	DECLARE @id_pista INT
	DECLARE @id_classe CHAR(1)
	DECLARE @data_ini DATETIME
	DECLARE @data_fim DATETIME
	DECLARE @sql_criterio varchar(1000)
	DECLARE @set_id_inconsistencia INT
	DECLARE @set_espera BIT

	DECLARE @SQLString nvarchar(MAX)
	
	DECLARE @filtro_aplicavel INT
	
	SET @id_filtro_aplicado = NULL
	SET @id_inconsistencia = NULL
	
	DECLARE @id_enquadramento_inf INT = NULL
	DECLARE @id_local_inf INT = NULL
	DECLARE @id_pista_inf INT = NULL
	
	SELECT 
		@id_enquadramento_inf = i.id_enquadramento, 
		@id_local_inf = i.id_local, 
		@id_pista_inf = i.pista 
	FROM 
		infracao i (nolock) 
	WHERE 
		i.id_infracao = @id_infracao

	DECLARE cursor_filtros CURSOR 
	FOR 
	SELECT 
		id_filtro,
		id_enquadramento,
		id_local,
		id_pista,
		id_classe,
		data_ini,
		data_fim,
		sql_criterio,
		set_id_inconsistencia,
		set_espera
	FROM filtro (nolock)
	WHERE	(data_validade >= GETDATE() OR data_validade IS NULL) 
		AND	(id_processo = @id_processo OR id_processo IS NULL OR @id_processo IS NULL) 
		AND	(id_enquadramento = @id_enquadramento_inf OR id_enquadramento IS NULL) 
		AND	(id_local = @id_local_inf OR id_local IS NULL) 
		AND	(id_pista = @id_pista_inf OR id_pista IS NULL)
	ORDER BY 
		prioridade DESC, 
		id_filtro ASC

	OPEN cursor_filtros

	
	FETCH NEXT FROM cursor_filtros
	INTO 	 
		@id_filtro,
		@id_enquadramento,
		@id_local,
		@id_pista,
		@id_classe,
		@data_ini,
		@data_fim,
		@sql_criterio,
		@set_id_inconsistencia,
		@set_espera

	WHILE @@FETCH_STATUS = 0

		BEGIN 
		
			SET @SQLString = dbo.fcn_getSqlVerificaFiltros( 
								@id_infracao,
								@id_enquadramento,
								@id_processo,
								@id_local,
								@id_pista,
								@id_classe,
								@data_ini,
								@data_fim,
								@sql_criterio,
								1
								)
			
			EXECUTE sp_executesql
				@SQLString,
				N'@countOUT int OUTPUT', 
				@countOUT=@filtro_aplicavel OUTPUT
		
			IF @filtro_aplicavel > 0

				BEGIN

					SET @id_filtro_aplicado = @id_filtro
					SET @id_inconsistencia = @set_id_inconsistencia
					SET @espera = @set_espera

					BREAK

				END
		
			FETCH NEXT FROM cursor_filtros
			INTO 	 
				@id_filtro,
				@id_enquadramento,
				@id_local,
				@id_pista,
				@id_classe,
				@data_ini,
				@data_fim,
				@sql_criterio,
				@set_id_inconsistencia,
				@set_espera

		END
	
	CLOSE cursor_filtros
	DEALLOCATE cursor_filtros

END





