CREATE VIEW [dbo].[veiculo_pesquisa_classe] AS

SELECT 
v.id_local,v.pista, v.data,
CASE WHEN v.comprimento >  1.0 AND v.comprimento <  3 THEN	'A' ELSE 
CASE WHEN v.comprimento >= 3.0 AND v.comprimento <  6 THEN	'B' ELSE 
CASE WHEN v.comprimento >= 6.0 AND v.comprimento < 15 THEN	'C' ELSE 
CASE WHEN v.comprimento >= 15                         THEN	'D' ELSE 
															'O'
END END END END 
AS
classe
FROM veiculo v (NOLOCK)

UNION ALL 

SELECT 
v.id_local,v.pista, v.data,
CASE WHEN v.comprimento >  1.0 AND v.comprimento <  3 THEN	'A' ELSE 
CASE WHEN v.comprimento >= 3.0 AND v.comprimento <  6 THEN	'B' ELSE 
CASE WHEN v.comprimento >= 6.0 AND v.comprimento < 15 THEN	'C' ELSE 
CASE WHEN v.comprimento >= 15                         THEN	'D' ELSE 
															'O'
END END END END 
AS
classe
FROM veiculo_estatistica v (NOLOCK)
