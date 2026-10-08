

--
-- Cria a function para o relatório de login dos auditores
--
CREATE FUNCTION [dbo].[fcn_getRelatorioLogin](@dataInicio DATE, @dataFim DATE) RETURNS TABLE AS RETURN 
(
    SELECT
        l.data_logon AS [DATA_LOGIN],
        l.data_logoff AS [DATA_LOGOFF],
        u.nome AS [NOME],
        CASE 
			WHEN(u.cod_agente IS NOT NULL) 
				THEN 'SIM' 
			ELSE 
				'NÃO' 
		END AS [AGENTE]
    FROM sis_logon_logoff_usuario l (nolock)
        INNER JOIN sis_usuario u (nolock)
            ON u.id_usuario = l.id_usuario
    WHERE	l.data_logon >= @dataInicio
        AND CAST(l.data_logon AS DATE) BETWEEN @dataInicio AND @dataFim 
)






