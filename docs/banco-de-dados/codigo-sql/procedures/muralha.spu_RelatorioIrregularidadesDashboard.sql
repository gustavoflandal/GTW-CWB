
-- ==================================================================================================
-- Procedure: Relatório de Irregularidade detalhado.
-- Descrição: Cria um relatório com dados das irregularidades para o Dashboard.
-- Autor: Gustavo F. Landal (Revisado por Gemini)
-- Data: 2025-10-14
-- Otimização V2: Lógica de contagem zero implementada no Recordset 6.
-- ===================================================================================================

CREATE     PROCEDURE [muralha].[spu_RelatorioIrregularidadesDashboard]
    @data_ini DATE,
    @data_fim DATE
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;

    -- 1. MATERIALIZAÇÃO DOS DADOS NA TABELA TEMPORÁRIA
    -- Essa tabela será a base para todos os recordsets de resumo.

    SELECT
        ma.data,
        mtao.tipo AS tipo,
        -- Concatena serie_equipamento e o nome (parte antes do hifen)
        CONVERT(VARCHAR(10), lv.serie_equipamento) + ' - ' +
        SUBSTRING(
            lv.nome,
            1,
            CASE
                WHEN CHARINDEX('-', lv.nome) > 0
                THEN CHARINDEX('-', lv.nome) - 1
                ELSE LEN(lv.nome)
            END
        ) AS equipamento,
        mcvm.placa AS placa_cad,
        mvtr.placa AS placa_cap,
        CASE
            WHEN mcvm.placa <> mvtr.placa
            THEN 'DIVERGENTE'
            ELSE 'CAPTURA OK'
        END AS leitura,
        mcvm.data_inicio AS inicio,
        CONVERT(DATE, mcvm.data_inativacao) AS fim,
        msa.descricao AS situacao
    INTO
        #irregularidades -- <-- Cria a Tabela Temporária
    FROM
        muralha.alerta_veiculo mav
    INNER JOIN
        muralha.alerta ma ON mav.id_alerta = ma.id
    INNER JOIN
        muralha.veiculo_tempo_real mvtr ON mvtr.id = mav.id_veiculo_tempo_real
    INNER JOIN
        muralha.tipo_alerta_ocorrencia mtao ON ma.id_tipo_alerta_ocorrencia = mtao.id
    INNER JOIN
        muralha.status_alerta msa ON msa.id = ma.id_status_alerta
    INNER JOIN
        local_vigente lv ON lv.id_local = mvtr.id_local
    INNER JOIN
        muralha.cad_veiculo_monitorado AS mcvm ON ma.id_cad_veiculo_monitorado = mcvm.id

    -- FILTRO DE DATAS: Inclui o dia final (Data Fim) completo.
    WHERE
        ma.data >= @data_ini
    AND
        ma.data < DATEADD(DAY, 1, @data_fim);


    -- =================================================================================
    -- 2. PRIMEIRO RECORDSET: DETALHES DAS IRREGULARIDADES (Grid de Dados)
    -- =================================================================================
    SELECT
        *
    FROM
        #irregularidades
    ORDER BY
        data DESC;

    -- =================================================================================
    -- 3. SEGUNDO RECORDSET: ESTATÍSTICAS POR TIPO (Gráfico de Tipos)
    -- =================================================================================
    SELECT
        tipo,
        COUNT(*) AS Total
    FROM
        #irregularidades
    GROUP BY
        tipo
    ORDER BY
        Total DESC;

    -- =================================================================================
    -- 4. TERCEIRO RECORDSET: ESTATÍSTICAS POR LEITURA (Gráfico Placa OK vs Divergente)
    -- =================================================================================
    SELECT
        leitura,
        COUNT(*) AS Total
    FROM
        #irregularidades
    GROUP BY
        leitura
    ORDER BY
        Total DESC;

    -- =================================================================================
    -- 5. QUARTO RECORDSET: ESTATÍSTICAS POR SITUAÇÃO (Gráfico de Status)
    -- =================================================================================
    SELECT
        situacao,
        COUNT(*) AS Total
    FROM
        #irregularidades
    GROUP BY
        situacao
    ORDER BY
        Total DESC;

    -- =================================================================================
    -- 6. QUINTO RECORDSET: ESTATÍSTICAS DE FINALIZAÇÃO (Contagem de Finalizados vs Abertos)
    --    CORREÇÃO APLICADA: Usa CTEs e LEFT JOIN para garantir linhas zero.
    -- =================================================================================
    WITH StatusBase AS (
        -- Cria uma tabela base com os dois status obrigatórios
        SELECT 'FINALIZADO' AS StatusFinalizacao
        UNION ALL
        SELECT 'EM ABERTO' AS StatusFinalizacao
    ),
    ContagemStatus AS (
        -- Contagem agrupada dos dados existentes
        SELECT
            CASE
                WHEN fim IS NOT NULL THEN 'FINALIZADO'
                ELSE 'EM ABERTO'
            END AS StatusFinalizacao,
            COUNT(*) AS Total
        FROM
            #irregularidades
        GROUP BY
            CASE
                WHEN fim IS NOT NULL THEN 'FINALIZADO'
                ELSE 'EM ABERTO'
            END
    )
    -- Combina a tabela base (StatusBase) com a contagem (ContagemStatus)
    SELECT
        SB.StatusFinalizacao,
        COALESCE(CS.Total, 0) AS Total -- Se a contagem for NULL (não encontrou), retorna 0
    FROM
        StatusBase SB
    LEFT JOIN
        ContagemStatus CS ON SB.StatusFinalizacao = CS.StatusFinalizacao
    ORDER BY
        Total DESC;

    -- =================================================================================
    -- 7. LIMPEZA
    -- =================================================================================
    IF OBJECT_ID('tempdb..#irregularidades') IS NOT NULL
    BEGIN
        DROP TABLE #irregularidades;
    END

END
