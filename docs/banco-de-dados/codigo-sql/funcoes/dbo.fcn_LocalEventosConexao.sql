
CREATE FUNCTION [dbo].[fcn_LocalEventosConexao](@dataInicio DATETIME ,@dataFim DATETIME)
RETURNS @result TABLE 
(
	serie_equipamento INT NOT NULL,
	id_local INT NOT NULL,
	data_atualizacao DATETIME NOT NULL,
	[status] INT NULL,
	tempo_evento_segundos INT NOT NULL
)
AS
BEGIN
   WITH EventosConexao(id_local, data_evento, [status], tempo_evento_segundos) AS
    (
		SELECT 
			id_local, data_atualizacao as data_evento, [status], 
			DATEDIFF(
				SECOND, 
				data_atualizacao, 
				(
					SELECT 
						COALESCE( MIN(data_atualizacao), @dataFim ) 
					FROM status_conexao c (nolock) 
					WHERE 
						data_atualizacao >= @dataInicio AND data_atualizacao <= @dataFim 
						AND c.id_status_conexao > sc.id_status_conexao 
						AND c.id_local = sc.id_local
				)  ) as tempo_evento_segundos
		FROM status_conexao sc (nolock)
		WHERE 
			data_atualizacao >= @dataInicio AND data_atualizacao <= @dataFim 
		UNION
		SELECT
			lv.id_local ,
			@dataInicio as data_evento, 
			(
				SELECT TOP 1[status]
				FROM status_conexao c (nolock) 
				WHERE data_atualizacao < @dataInicio and id_local = lv.id_local 
				ORDER BY id_status_conexao DESC
			),
			DATEDIFF(
				SECOND, 
				@dataInicio, 
				COALESCE(
					(
						SELECT TOP 1 data_atualizacao 
						FROM status_conexao c (nolock) 
						WHERE 
							data_atualizacao >= @dataInicio AND data_atualizacao <= @dataFim 
							and id_local = lv.id_local 
						ORDER BY id_status_conexao 
					),
					@dataFim ) )
		FROM local_vigente lv (nolock)
	)
   -- copy the required columns to the result of the function 

   INSERT @result
	   SELECT lv.serie_equipamento, ec.id_local, ec.data_evento, ec.[status], tempo_evento_segundos
		 FROM 
			EventosConexao ec (nolock)
			JOIN local_vigente lv (nolock)
				ON lv.id_local = ec.id_local
	   ORDER BY serie_equipamento

   RETURN
END



