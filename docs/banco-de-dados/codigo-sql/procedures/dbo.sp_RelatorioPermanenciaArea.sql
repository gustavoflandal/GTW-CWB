
 CREATE PROCEDURE sp_RelatorioPermanenciaArea
     @area_monitorada VARCHAR(6) = NULL,
     @placa VARCHAR(8) = NULL,
     @data_ini DATE,
     @data_fim DATE
 AS
 BEGIN
     SET NOCOUNT ON;

     DECLARE @data_fim_ajustada DATETIME = DATEADD(day, 1, @data_fim);

     -- CTE 1: Mapeia todos os registros de passagem para sua respectiva área monitorada (se houver)
     WITH PassagensComArea AS (
         SELECT
             vtr.placa,
             vtr.data,
             vtr.id_local,
             eam.id_area_monitorada
         FROM
             muralha.veiculo_tempo_real vtr
         LEFT JOIN
             muralha.equipamentos_area_monitorada eam ON vtr.id_local = eam.id_equipamento
         LEFT JOIN
             muralha.area_monitorada am ON eam.id_area_monitorada = am.id AND am.deletado = 0
         WHERE
             vtr.data >= @data_ini AND vtr.data < @data_fim_ajustada
             AND (@placa IS NULL OR vtr.placa = @placa)
     ),
     -- CTE 2: Identifica apenas as "Entradas Reais", ou seja, a primeira passagem em uma área
     -- quando o veículo não estava em nenhuma área antes.
     EntradasReais AS (
         SELECT
             placa,
             data,
             id_local,
             id_area_monitorada
         FROM (
             SELECT
                 placa,
                 data,
                 id_local,
                 id_area_monitorada,
                 LAG(id_area_monitorada, 1) OVER (PARTITION BY placa ORDER BY data) as id_area_anterior
             FROM
                 PassagensComArea
         ) AS sub
         WHERE
             id_area_monitorada IS NOT NULL
             AND id_area_anterior IS NULL
     )
     -- Seleção Final: Para cada "Entrada Real", busca a primeira "Saída Válida"
     SELECT
         e.placa,
         'Entrada' AS Ocorrencia_Entrada,
         e.data AS data_entrada_area_monitorada,
         e.id_area_monitorada,
         e.id_local AS id_equipamento_entrada,
         'Saída' AS Ocorrencia_Saida,
         saida.data_saida AS data_saida_area_monitorada,
         saida.id_equipamento_saida,
         CASE
             WHEN saida.data_saida IS NOT NULL THEN
                 CONVERT(VARCHAR, DATEDIFF(day, e.data, saida.data_saida)) + 'd ' +
                 FORMAT(DATEADD(second, DATEDIFF(second, e.data, saida.data_saida), 0), 'HH\h mm\m ss\s')
             ELSE -- Se não encontrou saída válida, calcula permanência até o momento atual
                 CONVERT(VARCHAR, DATEDIFF(day, e.data, GETDATE())) + 'd ' +
                 FORMAT(DATEADD(second, DATEDIFF(second, e.data, GETDATE()), 0), 'HH\h mm\m ss\s')
         END AS tempo_permanencia
     FROM
         EntradasReais e
     -- OUTER APPLY para encontrar a primeira passagem futura que satisfaça a nova regra de saída
     OUTER APPLY (
         SELECT TOP 1
             p.data as data_saida,
             p.id_local as id_equipamento_saida
         FROM
             PassagensComArea p
         WHERE
             p.placa = e.placa
             AND p.data > e.data
             -- A regra de saída: a área da passagem de saída não pode ser a mesma da entrada
             AND (p.id_area_monitorada IS NULL OR p.id_area_monitorada <> e.id_area_monitorada)
         ORDER BY
             p.data ASC
     ) AS saida
     WHERE
         -- Filtro opcional por área monitorada, aplicado no resultado final
         (@area_monitorada IS NULL OR e.id_area_monitorada = CAST(@area_monitorada AS INT))
     ORDER BY
         e.placa, e.data;

 END
