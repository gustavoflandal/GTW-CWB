CREATE FUNCTION [dbo].[fcn_periodos_falha_horario]
  ( @id_local int,
    @data_ini datetime,
    @data_fim datetime )
RETURNS @ret TABLE (
  [proprietario] varchar(50) COLLATE Latin1_General_CI_AS NULL,
  [inicio_imagens] datetime NULL,
  [registro] datetime NULL,
  [corrigido] datetime NULL,
  [final_imagens] datetime NULL
)
AS

BEGIN

	DECLARE @evento_captura_iniciado INT = 2
	DECLARE @evento_horario_ok INT = 38
	DECLARE @evento_horario_defasado INT = 51
	DECLARE @max_defasagem_ok INT = 600
	
	INSERT INTO @ret
	SELECT 
		ep.proprietario, 
		e2.data_hora as inicio_imagens, /*e2.mensagem,*/
		e.data_hora-(CAST(REPLACE(SUBSTRING(e.mensagem,31,LEN(RTRIM(e.mensagem))-32),',','.') AS NUMERIC(15,1)))/(24*60*60) as registro,
		e.data_hora as corrigido,
		dbo.MAX_DATE(e.data_hora-(CAST(REPLACE(SUBSTRING(e.mensagem,31,LEN(RTRIM(e.mensagem))-32),',','.') AS NUMERIC(15,1)))/(24*60*60), e.data_hora) as final_imagens/*,
		e.mensagem, 
		CAST(REPLACE(SUBSTRING(e.mensagem,31,LEN(RTRIM(e.mensagem))-32),',','.') AS NUMERIC(15,1)) as defasagem,
		datediff(hour,e2.data_hora,dbo.MAX_DATE(e.data_hora-(CAST(REPLACE(SUBSTRING(e.mensagem,31,LEN(RTRIM(e.mensagem))-32),',','.') AS NUMERIC(15,1)))/(24*60*60), e.data_hora))*/
	FROM eventos_csx e (nolock) 
		JOIN eventos_csx_desc_proprietario ep (nolock) 
			ON ep.id_proprietario = e.id_proprietario
		LEFT JOIN eventos_csx e2 (nolock) 
			ON e2.id_proprietario = e.id_proprietario 
			and e2.id_evento = @evento_horario_ok 
			and e2.data_hora = (SELECT MAX(_e2.data_hora) 
									FROM eventos_csx _e2 
									WHERE	_e2.id_proprietario = e.id_proprietario 
										AND	_e2.id_evento = @evento_horario_ok 
										AND	_e2.data_hora < e.data_hora
										/*ABS(CAST(REPLACE(SUBSTRING(_e2.mensagem,65,LEN(RTRIM(_e2.mensagem))-66),',','.') AS NUMERIC(15,1))) < @max_defasagem_ok AND*/
				
							)
	WHERE	e.id_evento = @evento_horario_defasado 
		AND (@id_local IS NULL OR ep.proprietario <> '990'+RTRIM(@id_local)) 
		AND	e.data_hora BETWEEN @data_ini AND @data_fim
	ORDER BY e.data_hora

	RETURN

END




