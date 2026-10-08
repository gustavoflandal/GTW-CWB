/*
===============================================================================
Nome da Função: [muralha].[fcn_perfil_comportamental_estadia_por_manchas]

Descrição:
    Retorna a quantidade de transições e o tempo total de permanência
    entre áreas monitoradas (manchas), com base nas rotas entre locais (PCLs).
    A função agrupa os dados por par de manchas (entrada e saída), permitindo
    análise de movimentação entre regiões monitoradas.

Parâmetros:
    @placa           - Placa do veículo a ser consultado (NVARCHAR(7))
    @dataInicio      - Data inicial para o filtro do período (DATETIME)
    @dataFim         - Data final para o filtro do período (DATETIME)

Retorno: Tabela
Autor: Thiago Guilsotti
Data de Criação: 05/08/2025

Observações:
    - Utiliza a função base [fcn_perfil_comportamental_base_rotas_entre_pcls]
    para obter os dados de movimentação entre locais.
    - Somente rotas entre locais associados a manchas (áreas monitoradas) são consideradas.
    - Ideal para análises de permanência em regiões monitoradas e fluxo entre áreas.

Exemplo de uso:
	SELECT * FROM muralha.fcn_perfil_comportamental_estadia_por_manchas(
		'HAR5B39',
		'2025-01-01',
		'2025-12-31'
	)
	ORDER BY id_area_monitorada_entrada;
===============================================================================
*/
CREATE   FUNCTION muralha.fcn_perfil_comportamental_estadia_por_manchas
(
    @placa NVARCHAR(7),
    @dataInicio DATETIME,
    @dataFim DATETIME
)
RETURNS TABLE
AS
RETURN
(
    --DECLARE @placa NVARCHAR(7) = 'HAR5B39';
    --DECLARE @dataInicio DATETIME = '2025-01-01';
    --DECLARE @dataFim DATETIME = '2025-12-31';
    SELECT 
        PCL_ENTRADA.id_area_monitorada AS id_area_monitorada_entrada,
        MIN(PCL_ENTRADA.nome_area_monitorada) AS nome_area_monitorada_entrada,
        COUNT(*) AS qtd_transicoes_entre_manchas,
        SUM(RE.tempo_total_estadia_min) AS tempo_total_estadia_min
    FROM muralha.fcn_perfil_comportamental_base_rotas_entre_pcls(@placa, @dataInicio, @dataFim) RE
    LEFT JOIN muralha.fcn_perfil_comportamental_base_locais_com_mancha() PCL_ENTRADA
        ON RE.id_entrada = PCL_ENTRADA.id_local
    GROUP BY 
        PCL_ENTRADA.id_area_monitorada
);