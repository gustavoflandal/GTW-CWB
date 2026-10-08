
CREATE FUNCTION [dbo].[fcn_getRelatorioIsencoesHTML]()
RETURNS NVARCHAR(MAX)
WITH EXECUTE AS CALLER
AS

BEGIN

	DECLARE @numero_dias int = 7
	DECLARE @inc int = 0
	DECLARE @tableHTML NVARCHAR(MAX)
	
	SET @tableHTML =	N'<b>Relatório de isenções acumuladas nos últimos '+STR(@numero_dias)+' dias</b>' +
						N'<table border="1">' +
						N'<tr><th>ID Local</th><th>CTB 56811</th></th><th>CTB 57030</th><th>CTB 57461</th><th>CTB 57462</th><th>CTB 57463</th><th>TOTAL</th>' +
						CAST ( (SELECT 
									td=[id_local], 
									'',
									td=[ctb_56811], 
									'',
									td=[ctb_57030], 
									'',
									td=[ctb_57461], 
									'',
									td=[ctb_57462], 
									'',
									td=[ctb_57463], 
									'',
									td=[Total], 
									''
								FROM 
									fcn_getRelatorioIsencoes(getdate()-(@numero_dias+1), getdate())
								ORDER BY 
									Total ASC
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX) 
							) + N'</table>'
	
	WHILE (@inc < @numero_dias)

		BEGIN

			SET @tableHTML = @tableHTML + '<br><br>'+
							N'<b>Relatório de isenções acumuladas no dia '+(select CONVERT(CHAR(10),getdate()-@inc, 103))+'</b>' +
							N'<table border="1">' +
							N'<tr><th>ID Local</th><th>CTB 56811</th></th><th>CTB 57030</th><th>CTB 57461</th><th>CTB 57462</th><th>CTB 57463</th><th>TOTAL</th>' +
							CAST ( (SELECT 
										td=[id_local], 
										'',
										td=[ctb_56811], 
										'',
										td=[ctb_57030], 
										'',
										td=[ctb_57461], 
										'',
										td=[ctb_57462], 
										'',
										td=[ctb_57463], 
										'',
										td=[Total], 
										''
									FROM 
										fcn_getRelatorioIsencoes(getdate()-@inc, getdate()-@inc)
									ORDER BY 
										Total ASC
									FOR XML PATH('tr'), TYPE 
									) AS NVARCHAR(MAX) 
								) +	N'</table>' 

			SET @inc = @inc + 1

		END
	
	RETURN @tableHTML

END




