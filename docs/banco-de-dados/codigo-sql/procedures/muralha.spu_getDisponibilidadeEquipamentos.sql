
CREATE   PROCEDURE [muralha].[spu_getDisponibilidadeEquipamentos]
    @Data_Ini DATETIME,
    @Data_Fim DATETIME,
    @Id_Local INT = NULL,
    @Id_Municipio INT = NULL,
    @Id_Regiao TINYINT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- EDITAL 5.4.2.3 - ITEM E
    -- Disponibilidade da Solucao

    IF @Data_Ini IS NULL
       OR @Data_Fim IS NULL
       OR @Data_Fim < @Data_Ini
    BEGIN
        THROW 50001, 'Periodo de consulta invalido.', 1;
    END;

    ---------------------------------------------------------
    -- EQUIPAMENTOS ATIVOS QUE ATENDEM AOS FILTROS
    -- Recupera o ultimo estado de conexao registrado
    ---------------------------------------------------------

    SELECT
        l.id_local,
        RTRIM(l.nome) AS equipamento,
        l.serie_equipamento,
        sc.status AS status_conexao,
        sc.data_atualizacao
    INTO #Equipamentos
    FROM local_vigente l
    OUTER APPLY
    (
        SELECT TOP (1)
            s.status,
            s.data_atualizacao
        FROM local_status_conexao s
        WHERE s.id_local = l.id_local
        ORDER BY
            s.data_atualizacao DESC
    ) sc
    WHERE l.desativado = 0
      AND (
          @Id_Local IS NULL
          OR l.id_local = @Id_Local
      )
      AND
      (
          (
              @Id_Municipio IS NULL
              AND @Id_Regiao IS NULL
          )
          OR EXISTS
          (
              SELECT 1
              FROM local_pista_vigente lp
              WHERE lp.id_local = l.id_local
                AND lp.desativado = 0
                AND (
                    @Id_Municipio IS NULL
                    OR lp.id_localidade = @Id_Municipio
                )
                AND (
                    @Id_Regiao IS NULL
                    OR lp.id_regiao = @Id_Regiao
                )
          )
      );

    CREATE UNIQUE CLUSTERED INDEX IX_Equipamentos
        ON #Equipamentos (id_local);

    ---------------------------------------------------------
    -- EVENTOS DO PERIODO
    -- Somente equipamentos selecionados pelos filtros
    ---------------------------------------------------------

    SELECT
        e.id_local,
        la.id,
        la.data,
        la.id_tipo,
        lt.descricao
    INTO #Eventos
    FROM #Equipamentos e
    INNER JOIN log_alerta la
        ON la.serial = CONVERT(
            VARCHAR(15),
            e.serie_equipamento
        )
    INNER JOIN log_tipo lt
        ON lt.id = la.id_tipo
    WHERE la.data >= @Data_Ini
      AND la.data <= @Data_Fim
      AND la.id_tipo IN
      (
          21,  -- Com energia
          22,  -- Sem energia (bateria)
          23,  -- Falha nos sensores
          24,  -- Tensao da bateria
          25,  -- Temperatura interna
          26,  -- Falha na comunicacao com camera
          27,  -- Falha na sequencia semaforica
          28,  -- Sequencia semaforica correta
          112, -- Conectado ao servidor
          113  -- Desconectado do servidor
      );

    CREATE CLUSTERED INDEX IX_Eventos
        ON #Eventos (id_local, data);

    ---------------------------------------------------------
    -- RESULTADO 1: TOTALIZADORES
    -- Ultimo estado de conexao registrado
    ---------------------------------------------------------

    SELECT
        COUNT(*) AS total_equipamentos,

        COALESCE(SUM(
            CASE
                WHEN status_conexao = 1 THEN 1
                ELSE 0
            END
        ), 0) AS conectados,

        COALESCE(SUM(
            CASE
                WHEN status_conexao = 2 THEN 1
                ELSE 0
            END
        ), 0) AS desconectados,

        COALESCE(SUM(
            CASE
                WHEN status_conexao IS NULL
                  OR status_conexao NOT IN (1, 2)
                THEN 1
                ELSE 0
            END
        ), 0) AS sem_informacao

    FROM #Equipamentos;

    ---------------------------------------------------------
    -- RESULTADO 2: DETALHAMENTO POR EQUIPAMENTO
    -- Inclui a data da ultima atualizacao do status
    ---------------------------------------------------------

    SELECT
        e.id_local,
        e.equipamento,
        e.serie_equipamento,
        e.status_conexao,
        e.data_atualizacao,

        COALESCE(SUM(
            CASE
                WHEN ev.id_tipo = 112 THEN 1
                ELSE 0
            END
        ), 0) AS conexoes,

        COALESCE(SUM(
            CASE
                WHEN ev.id_tipo = 113 THEN 1
                ELSE 0
            END
        ), 0) AS desconexoes,

        COALESCE(SUM(
            CASE
                WHEN ev.id_tipo = 21 THEN 1
                ELSE 0
            END
        ), 0) AS com_energia,

        COALESCE(SUM(
            CASE
                WHEN ev.id_tipo = 22 THEN 1
                ELSE 0
            END
        ), 0) AS em_bateria,

        COALESCE(SUM(
            CASE
                WHEN ev.id_tipo IN (23, 26, 27)
                THEN 1
                ELSE 0
            END
        ), 0) AS falhas

    FROM #Equipamentos e
    LEFT JOIN #Eventos ev
        ON ev.id_local = e.id_local

    GROUP BY
        e.id_local,
        e.equipamento,
        e.serie_equipamento,
        e.status_conexao,
        e.data_atualizacao

    ORDER BY
        e.equipamento;

    ---------------------------------------------------------
    -- RESULTADO 3: HISTORICO DOS EVENTOS
    -- Eventos dentro do periodo selecionado
    ---------------------------------------------------------

    SELECT
        ev.id_local,
        e.equipamento,
        e.serie_equipamento,
        ev.data,
        ev.id_tipo,
        ev.descricao

    FROM #Eventos ev
    INNER JOIN #Equipamentos e
        ON e.id_local = ev.id_local

    ORDER BY
        ev.data DESC,
        ev.id DESC;

END;
