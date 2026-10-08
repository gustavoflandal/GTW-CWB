/*
===============================================================================
Nome da Função: [muralha].[fcn_perfil_comportamental_rotas_entre_pcls_com_mancha]

Descrição:
    Retorna as rotas entre locais consecutivos (PCLs), incluindo nomes dos locais
    de entrada e saída, além das áreas monitoradas associadas. Baseia-se na função
    [fcn_perfil_comportamental_base_rotas_entre_pcls] para os dados principais.

Parâmetros:
    @placa           - Placa do veículo a ser consultado (NVARCHAR(7))
    @dataInicio      - Data inicial para o filtro do período (DATETIME)
    @dataFim         - Data final para o filtro do período (DATETIME)

Retorno: Tabela
Autor: Thiago Guilsotti
Data de Criação: 28/07/2025

Observações:
    - Evita explosões cartesianas usando subconsulta base.
    - Deve ser usada somente quando necessário enriquecer visualizações ou relatórios.

Exemplo de uso:
    SELECT * FROM muralha.fcn_perfil_comportamental_rotas_entre_pcls_com_mancha(
        'HAR5B39',
        '2025-01-01',
        '2025-12-31'
    );
===============================================================================
*/
CREATE   FUNCTION muralha.fcn_perfil_comportamental_rotas_entre_pcls_com_mancha
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
        RE.id_entrada,
        RE.id_saida,
        RE.dia_semana,
        RE.hora,
        RE.qtd_rotas_entre_pcls,
        RE.tempo_total_estadia_min,
        PCL_ENTRADA.nome_local AS nome_local_entrada,
        PCL_SAIDA.nome_local AS nome_local_saida,
        PCL_ENTRADA.id_area_monitorada AS id_area_monitorada_entrada,
        PCL_SAIDA.id_area_monitorada AS id_area_monitorada_saida,
        PCL_ENTRADA.nome_area_monitorada AS nome_area_monitorada_entrada,
        PCL_SAIDA.nome_area_monitorada AS nome_area_monitorada_saida
    FROM fcn_perfil_comportamental_base_rotas_entre_pcls(@placa, @dataInicio, @dataFim) RE
    JOIN muralha.fcn_perfil_comportamental_base_locais_com_mancha() PCL_ENTRADA
        ON RE.id_entrada = PCL_ENTRADA.id_local
    JOIN muralha.fcn_perfil_comportamental_base_locais_com_mancha() PCL_SAIDA
        ON RE.id_saida = PCL_SAIDA.id_local
);