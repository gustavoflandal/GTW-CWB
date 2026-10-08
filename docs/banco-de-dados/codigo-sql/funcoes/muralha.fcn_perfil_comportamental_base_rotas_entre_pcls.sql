/*
===============================================================================
Nome da Função: [muralha].[fcn_perfil_comportamental_base_rotas_entre_pcls]

Descrição:
    Retorna rotas entre locais consecutivos (PCLs) com base nas passagens
    do veículo informado, incluindo o tempo de estadia entre uma passagem
    e outra.

Parâmetros:
    @placa           - Placa do veículo a ser consultado (NVARCHAR(7))
    @dataInicio      - Data inicial para o filtro do período (DATETIME)
    @dataFim         - Data final para o filtro do período (DATETIME)

Retorno: Tabela
Autor: Thiago Guilsotti
Data de Criação: 28/07/2025

Observações:
    - A lógica usa LEAD para identificar o próximo local visitado por placa.
    - Útil para análises de trajetos e padrões de movimentação.
	
Exemplo de uso:
    SELECT * FROM muralha.fcn_perfil_comportamental_base_rotas_entre_pcls(
        'HAR5B39',
        '2025-01-01',
        '2025-12-31'
    );
===============================================================================
*/
CREATE   FUNCTION muralha.fcn_perfil_comportamental_base_rotas_entre_pcls
(
    @placa NVARCHAR(7),
    @dataInicio DATETIME,
    @dataFim DATETIME
)
RETURNS TABLE
AS
RETURN
(
    --DECLARE @placa NVARCHAR(7) = 'HAR5B39', @dataInicio DATETIME = '2025-01-01',@dataFim DATETIME = '2025-12-31';
    WITH rotas_entre_pcls AS (
        SELECT 
            MVTR.id_local AS id_entrada,
            LEAD(MVTR.id_local) OVER (PARTITION BY MVTR.placa ORDER BY MVTR.data) AS id_saida,
            DATEDIFF(MINUTE, MVTR.data, LEAD(MVTR.data) OVER (PARTITION BY MVTR.placa ORDER BY MVTR.data)) AS tempo_estadia_minutos
        FROM muralha.veiculo_tempo_real MVTR
        WHERE MVTR.placa = @placa
            AND MVTR.data BETWEEN @dataInicio AND @dataFim
    )
    SELECT 
        id_entrada,
        id_saida,
        COUNT(*) AS qtd_rotas_entre_pcls,
        SUM(tempo_estadia_minutos) AS tempo_total_estadia_min
    FROM rotas_entre_pcls
    GROUP BY 
        id_entrada, 
        id_saida
);