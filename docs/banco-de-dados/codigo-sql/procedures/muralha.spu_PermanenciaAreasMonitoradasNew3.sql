CREATE   PROCEDURE muralha.spu_PermanenciaAreasMonitoradasNew3
      @area INT,
      @placa VARCHAR(8),
      @data_inicio DATE,
      @data_final DATE
  AS
  BEGIN
      SET NOCOUNT ON;

      -- 1. TRATAMENTO DOS PARÂMETROS DE ENTRADA
      IF LTRIM(RTRIM(ISNULL(CAST(@area AS VARCHAR(10)), ''))) = '' SET @area = NULL;
      IF LTRIM(RTRIM(ISNULL(@placa, ''))) = '' SET @placa = NULL;

      DECLARE @dt_final_ajustada DATETIME = DATEADD(day, 1, @data_final);


      -- 2. CTE PARA SEQUENCIAR AS PASSAGENS (COM IDs DE EQUIPAMENTO)
      WITH PassagensSequenciadas AS (
          SELECT
              vtr.placa,
              vtr.data,
              vtr.id_local AS id_equipamento_entrada, -- Captura o ID do equipamento de entrada
              eam.id_area_monitorada,
              LEAD(vtr.data, 1) OVER(PARTITION BY vtr.placa ORDER BY vtr.data) AS data_proxima_passagem,
              LEAD(vtr.id_local, 1) OVER(PARTITION BY vtr.placa ORDER BY vtr.data) AS id_equipamento_saida, -- Captura o ID do equipamento da próxima passagem (saída)
              LEAD(eam.id_area_monitorada, 1) OVER(PARTITION BY vtr.placa ORDER BY vtr.data) AS id_area_proxima_passagem
          FROM
              muralha.veiculo_tempo_real vtr
          LEFT JOIN
              muralha.equipamentos_area_monitorada eam ON vtr.id_local = eam.id_equipamento
          WHERE
              vtr.data >= @data_inicio AND vtr.data < @dt_final_ajustada
              AND (@placa IS NULL OR vtr.placa = @placa)
      ),
      -- 3. CTE PARA DEFINIR AS PERMANÊNCIAS (PASSANDO OS IDs)
      Permanencias AS (
          SELECT
              ps.placa,
              ps.id_area_monitorada,
              am.nome AS nome_area,
              ps.id_equipamento_entrada, -- Passa o ID de entrada adiante
              ps.data AS data_entrada,
              ps.id_equipamento_saida, -- Passa o ID de saída adiante
              ps.data_proxima_passagem AS data_saida,
              DATEDIFF(SECOND, ps.data, ps.data_proxima_passagem) AS segundos_permanencia
          FROM
              PassagensSequenciadas ps
          JOIN
              muralha.area_monitorada am ON ps.id_area_monitorada = am.id
          WHERE
              ps.id_area_monitorada IS NOT NULL
              AND ps.data_proxima_passagem IS NOT NULL
              AND (ps.id_area_proxima_passagem IS NULL OR ps.id_area_proxima_passagem <> ps.id_area_monitorada)
              AND (@area IS NULL OR ps.id_area_monitorada = @area)
      )
      SELECT *
      INTO #PermanenciasDetalhes
      FROM Permanencias;

      -- Tabela temporária para calcular o total de passagens
      SELECT vtr.placa, eam.id_area_monitorada
      INTO #PassagensEmArea
      FROM muralha.veiculo_tempo_real vtr
      JOIN muralha.equipamentos_area_monitorada eam ON vtr.id_local = eam.id_equipamento
      WHERE
          vtr.data >= @data_inicio AND vtr.data < @dt_final_ajustada
          AND (@placa IS NULL OR vtr.placa = @placa)
          AND (@area IS NULL OR eam.id_area_monitorada = @area);


      -- 4. GERAÇÃO DOS RECORDSETS DE SAÍDA

      -- Recordset 1: Detalhes das Permanências (Top 100) - ATUALIZADO
      SELECT TOP 100
          placa AS Placa,
          nome_area AS AreaMonitorada,
          id_equipamento_entrada AS ID_EquipamentoEntrada, -- NOVO CAMPO
          data_entrada AS DataHora_Entrada,
          id_equipamento_saida AS ID_EquipamentoSaida,   -- NOVO CAMPO
          data_saida AS DataHora_Saida,
          dbo.fn_FormatarSegundosEmDHM(segundos_permanencia) AS TempoPermanencia
      FROM
          #PermanenciasDetalhes
      ORDER BY
          segundos_permanencia DESC;


      -- Recordset 2: Permanência por Veículo (Top 50) - Sem alterações
      SELECT TOP 50
          p.placa AS Placa,
          (SELECT COUNT(1) FROM #PassagensEmArea pa WHERE pa.placa = p.placa) AS TotalPassagens,
          dbo.fn_FormatarSegundosEmDHM(SUM(p.segundos_permanencia)) AS TempoTotalPermanencia,
          dbo.fn_FormatarSegundosEmDHM(AVG(CAST(p.segundos_permanencia AS BIGINT))) AS TempoMedioPermanencia
      FROM
          #PermanenciasDetalhes p
      GROUP BY
          p.placa
      ORDER BY
          SUM(p.segundos_permanencia) DESC;


      -- Recordset 3: Permanência por Área - Sem alterações
      SELECT
          p.nome_area AS AreaMonitorada,
          (SELECT COUNT(1) FROM #PassagensEmArea pa WHERE pa.id_area_monitorada = p.id_area_monitorada) AS TotalPassagens,
          COUNT(DISTINCT p.placa) AS VeiculosUnicos,
          dbo.fn_FormatarSegundosEmDHM(SUM(p.segundos_permanencia)) AS TempoTotalPermanencia,
          dbo.fn_FormatarSegundosEmDHM(AVG(CAST(p.segundos_permanencia AS BIGINT))) AS TempoMedioPermanencia
      FROM
          #PermanenciasDetalhes p
      GROUP BY
          p.id_area_monitorada, p.nome_area
      ORDER BY
          p.nome_area;


      -- Recordset 4: Resumo Geral - Sem alterações
      SELECT
          (SELECT COUNT(1) FROM #PassagensEmArea) AS TotalGeralPassagens,
          COUNT(DISTINCT placa) AS TotalVeiculosUnicos,
          COUNT(DISTINCT id_area_monitorada) AS TotalAreasMonitoradas,
          dbo.fn_FormatarSegundosEmDHM(AVG(CAST(segundos_permanencia AS BIGINT))) AS TempoMedioGeralPermanencia
      FROM
          #PermanenciasDetalhes;


      -- Limpeza
      DROP TABLE #PermanenciasDetalhes;
      DROP TABLE #PassagensEmArea;

  END;
