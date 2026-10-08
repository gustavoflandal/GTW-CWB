
CREATE VIEW [dbo].[relatorio_aproveitamento_semanal]
AS
SELECT
	id_local as Local, 
    i.pista as Pista, 
    datepart( YEAR , data ) as Ano, 
    datepart( WEEK , data ) as NumSemana,
	CAST(DATEADD(wk, DATEDIFF(WK, 6, data), 6) AS DATE) as Data,
    sum(case when p.ativo = 1 AND p.id_processo_proximo IS NULL and i.id_inconsistencia = 0 then 1 else 0 end) as Validas, 
    sum(case when r.razao_tecnica in (1) and i.id_inconsistencia > 0 then 1 else 0 end) as PTL, 
    sum(case when r.razao_tecnica in (0) and i.id_inconsistencia > 0 then 1 else 0 end) as PNT, 
    sum(case when r.razao_tecnica in (2) and i.id_inconsistencia > 0 then 1 else 0 end) as PTG, 
    count(*) as Total, 
    cast(i.id_local as varchar(4)) + ' - P' + cast(i.pista as varchar(1)) as LocalEPista        
FROM infracao i (nolock) 
    LEFT JOIN inconsistencia r (nolock) 
		on r.id_inconsistencia = i.id_inconsistencia 
    LEFT JOIN processo p (nolock) 
		on i.id_processo = p.id_processo 
GROUP BY 
	id_local, 
	i.pista, 
    datepart( YEAR, data), 
    datepart( WEEK, data),
    CAST(DATEADD(wk, DATEDIFF(WK, 6, data), 6) AS DATE)


