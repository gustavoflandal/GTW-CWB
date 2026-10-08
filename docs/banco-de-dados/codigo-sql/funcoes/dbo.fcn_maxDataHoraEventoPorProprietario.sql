
/****** Object:  UserDefinedFunction [dbo].[fcn_maxDataHoraEventoPorProprietario]    Script Date: 02/04/2011 11:31:30 ******/
CREATE FUNCTION [dbo].[fcn_maxDataHoraEventoPorProprietario]( @idEvento int )
RETURNS TABLE
AS
RETURN
(

	SELECT
		ep.proprietario,
		MAX(e.data_hora) as [ultima_data]
	FROM eventos_csx e (nolock)
		JOIN eventos_csx_desc_proprietario ep (nolock)
			ON ep.id_proprietario = e.id_proprietario
	WHERE
		e.id_evento = @idEvento
	GROUP BY
		ep.proprietario
		
)



