
CREATE VIEW [dbo].[status_processo] 
AS
SELECT 0 as status_processo, 'Processado' as descricao_status 
UNION
SELECT 1 as status_processo, 'Pendente' as descricao_status 
UNION
SELECT 2 as status_processo, 'Cancelado' as descricao_status


