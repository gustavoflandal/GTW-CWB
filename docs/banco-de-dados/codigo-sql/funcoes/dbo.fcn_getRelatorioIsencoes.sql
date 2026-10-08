
CREATE FUNCTION [dbo].[fcn_getRelatorioIsencoes](@dataInicio date, @dataFim date)
RETURNS TABLE
AS
RETURN
(
	SELECT cast(id_local as nvarchar(4)) as [id_local], ctb_56811, ctb_57030, ctb_57461, ctb_57462, ctb_57463, TOTAL
		FROM (
			SELECT 
				id_local,
				sum( case when id_enquadramento = 56811 then 1 else 0 end) as ctb_56811,
				sum( case when id_enquadramento = 57030 then 1 else 0 end) as ctb_57030,
				sum( case when id_enquadramento = 57461 then 1 else 0 end) as ctb_57461,
				sum( case when id_enquadramento = 57462 then 1 else 0 end) as ctb_57462,
				sum( case when id_enquadramento = 57463 then 1 else 0 end) as ctb_57463,
				COUNT(*) as TOTAL
			FROM 
				infracao i
			WHERE 
				i.id_inconsistencia in (6,13,23)
				and CAST( i.data as DATE ) >= @dataInicio and CAST( i.data as DATE )<= @dataFim
			GROUP BY id_local ) as T
	    
	UNION
	  
	SELECT id_local, ctb_56811, ctb_57030, ctb_57461, ctb_57462, ctb_57463, TOTAL
		FROM (
			SELECT 
				'Todos' as id_Local,
				sum( case when id_enquadramento = 56811 then 1 else 0 end) as ctb_56811,
				sum( case when id_enquadramento = 57030 then 1 else 0 end) as ctb_57030,
				sum( case when id_enquadramento = 57461 then 1 else 0 end) as ctb_57461,
				sum( case when id_enquadramento = 57462 then 1 else 0 end) as ctb_57462,
				sum( case when id_enquadramento = 57463 then 1 else 0 end) as ctb_57463,
				COUNT(*) as TOTAL
			FROM 
				infracao i
			WHERE 
				i.id_inconsistencia in (6,13,23)
				and CAST( i.data as DATE ) >= @dataInicio and CAST( i.data as DATE )<= @dataFim
			) as T
);



