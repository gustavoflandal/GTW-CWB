
CREATE VIEW [dbo].[sis_documento_classificador] 
AS
SELECT 1 as id_classificador,'RAIZ' as descricao, 'ROOT' as sigla, NULL as id_classficador_pai 
UNION
SELECT 2 as id_classificador,'Processo Medição' as descricao, 'PMED' as sigla, 1 as id_classficador_pai 
UNION
SELECT 3 as id_classificador,'Protocolo CD Comprovação' as descricao, 'PCDC' as sigla, 2 as id_classficador_pai 
UNION
SELECT 4 as id_classificador,'Planilha CD Comprovação' as descricao, 'PLCDC' as sigla, 2 as id_classficador_pai 
UNION
SELECT 5 as id_classificador,'Protocolo CD Complemento Comprovação' as descricao, 'PCDCC' as sigla, 2 as id_classficador_pai 
UNION
SELECT 6 as id_classificador,'Planilha Quantitativos' as descricao, 'PLQTD' as sigla, 2 as id_classficador_pai


