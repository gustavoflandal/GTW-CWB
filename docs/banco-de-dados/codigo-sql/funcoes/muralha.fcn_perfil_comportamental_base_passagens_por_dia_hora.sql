/*
===============================================================================
Nome da Função: [muralha].[fcn_perfil_comportamental_base_passagens_por_dia_hora]

Descrição:
    Retorna as passagens do veículo especificado, agrupadas por local (PCL),
    dia da semana e hora do dia. A função pode ser utilizada para gerar
    análises comportamentais e mapas de calor preditivos de presença.

Parâmetros:
    @placa           - Placa do veículo a ser consultado (NVARCHAR(7))
    @dataInicio      - Data inicial para o filtro do período (DATETIME)
    @dataFim         - Data final para o filtro do período (DATETIME)

Retorno: Tabela
Autor: Thiago Guilsotti
Data de Criação: 28/07/2025

Observações:
    - Ideal para análises de padrão de comportamento por hora e dia da semana.
    - Dados podem ser cruzados com áreas monitoradas para visualização em mapa.
	
Exemplo de uso:
    SELECT * FROM muralha.fcn_perfil_comportamental_base_passagens_por_dia_hora(
        'HAR5B39',
        '2025-01-01',
        '2025-12-31'
    );
===============================================================================
*/
CREATE FUNCTION [muralha].[fcn_perfil_comportamental_base_passagens_por_dia_hora]
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
    SELECT MVTR.id_local,
		   DATENAME(WEEKDAY, MVTR.data) AS dia_semana,
		   DATEPART(HOUR, MVTR.data) AS hora,
		   COUNT(*) AS total_passagens
    FROM   muralha.veiculo_tempo_real MVTR
    WHERE  MVTR.placa = @placa
		   AND MVTR.data BETWEEN @dataInicio AND @dataFim
    GROUP BY
		   MVTR.id_local,
		   DATENAME(WEEKDAY, MVTR.data),
		   DATEPART(HOUR, MVTR.data)
);