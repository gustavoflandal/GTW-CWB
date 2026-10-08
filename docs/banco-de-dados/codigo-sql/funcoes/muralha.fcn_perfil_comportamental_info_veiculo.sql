/*
===============================================================================
Nome da Função: [muralha].[fcn_perfil_comportamental_info_veiculo]

Descrição:
    Retorna as informações básicas do veículo (placa, marca, modelo, cor, ano de fabricação)
    juntamente com a imagem mais recente registrada no período informado.

Parâmetros:
    @placa       - Placa do veículo a ser consultado (NVARCHAR(7))
    @dataInicio  - Data/hora inicial do intervalo de busca (DATETIME)
    @dataFim     - Data/hora final do intervalo de busca (DATETIME)

Retorno: Tabela
Autor: Thiago Guislotti
Data de Criação: 31/07/2025
Versão: 1.1 - Atualização da tabela de muralha.veiculo_cad para dbo.cadastro_veiculo

Observações:
    - Retorna apenas uma linha (imagem mais recente) com base na data mais próxima do fim do período.
    - Utiliza a função [muralha].[fcn_ObterInfoVeiculo] para complementar os dados cadastrais.
    - Imagens são obtidas a partir da tabela [muralha].[veiculo_tempo_real_imagem].

Exemplo de uso:
    SELECT * FROM muralha.fcn_perfil_comportamental_info_veiculo(
        'HAR5B39',
        '2025-01-01',
        '2025-12-31'
    );
===============================================================================
*/
CREATE   FUNCTION muralha.fcn_perfil_comportamental_info_veiculo
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
    SELECT TOP 1
        MVTR.id AS id_veiculo,
        MVTR.placa,
        LTRIM(RTRIM(IV.marca_cet)) AS marca,
        LTRIM(RTRIM(IV.marca)) AS modelo,
        LTRIM(RTRIM(IV.cor)) AS cor,
        IV.ano AS ano_fabricacao,
        MVTRI.imagem
    FROM  muralha.veiculo_tempo_real MVTR
        INNER JOIN muralha.veiculo_tempo_real_imagem MVTRI
            ON  MVTR.id = MVTRI.id_veiculo_tempo_real
        LEFT JOIN dbo.cadastro_veiculo IV
            ON  MVTR.placa = IV.placa
    WHERE  MVTR.placa = @placa
        AND MVTR.data BETWEEN @dataInicio AND @dataFim
        AND MVTRI.indice_imagem = 0
    ORDER BY
        MVTR.data DESC
);