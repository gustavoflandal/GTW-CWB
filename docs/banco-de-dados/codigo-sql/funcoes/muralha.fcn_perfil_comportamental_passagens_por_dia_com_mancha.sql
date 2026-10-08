
/*
===============================================================================
Nome da Função: [muralha].[fcn_perfil_comportamental_passagens_por_dia_com_mancha]

Descrição:
    Retorna a quantidade total de passagens por dia da semana, agrupadas
    por local (PCL), incluindo informações do local e da área monitorada.

Parâmetros:
    @placa NVARCHAR(7)
    @dataInicio DATETIME
    @dataFim DATETIME

Retorno: Tabela
Autor: Thiago Guislotti
Data de Criação: 31/07/2025

Observações:
    - Ideal para gerar padrões comportamentais semanais por local.

Exemplo de uso:
    SELECT * FROM muralha.fcn_perfil_comportamental_passagens_por_dia_com_mancha(
        'HAR5B39',
        '2025-01-01',
        '2025-12-31'
    );
===============================================================================
*/
CREATE FUNCTION [muralha].[fcn_perfil_comportamental_passagens_por_dia_com_mancha]
(
    @placa NVARCHAR(7),
    @dataInicio DATETIME,
    @dataFim DATETIME
)
RETURNS TABLE
AS
RETURN
(
    --DECLARE @placa NVARCHAR(7) = 'HAR5B39', @dataInicio DATETIME = '2025-01-01', @dataFim DATETIME = '2025-12-31';
    SELECT PCL.id_local,
		   PCL.nome_local,
		   PCL.id_area_monitorada,
		   PCL.nome_area_monitorada,
		   PDH.dia_semana,
		   SUM(PDH.total_passagens) AS total_passagens
    FROM   muralha.fcn_perfil_comportamental_base_passagens_por_dia_hora(@placa, @dataInicio, @dataFim) PDH
		   JOIN muralha.fcn_perfil_comportamental_base_locais_com_mancha() PCL
				ON  PDH.id_local = PCL.id_local
    GROUP BY
		   PCL.id_local, 
		   PCL.nome_local, 
		   PCL.id_area_monitorada, 
		   PCL.nome_area_monitorada,
		   PDH.dia_semana
);