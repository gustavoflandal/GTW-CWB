-- =============================================
-- Procedure: sp_MonitoramentoAreaMonitorada
-- Versão: 1.0 - Estrutura Base
-- =============================================

CREATE   PROCEDURE muralha.spu_MonitoramentoAreaMonitorada_V1
    @area_monitorada INT = NULL,
    @placa CHAR(7) = NULL,
    @data_ini DATETIME,
    @data_fim DATETIME
AS
BEGIN
    SET NOCOUNT ON;
    IF @area_monitorada = '' SET @area_monitorada = null
	IF @placa = '' SET @placa = null
    
    -- Validação de parâmetros
    IF @data_ini IS NULL OR @data_fim IS NULL
    BEGIN
        RAISERROR('Os parâmetros @data_ini e @data_fim são obrigatórios', 16, 1);
        RETURN;
    END
    
    IF @data_fim < @data_ini
    BEGIN
        RAISERROR('A data final não pode ser menor que a data inicial', 16, 1);
        RETURN;
    END
    
    -- Tabela temporária para armazenar todos os registros relevantes
    -- Classificados por área monitorada
    IF OBJECT_ID('tempdb..#RegistrosVeiculos') IS NOT NULL
        DROP TABLE #RegistrosVeiculos;
    
    CREATE TABLE #RegistrosVeiculos (
        placa CHAR(7),
        data_passagem DATETIME,
        id_local INT,
        id_area_monitorada INT NULL,
        seq_registro INT,
        PRIMARY KEY (placa, seq_registro)
    );
    
    -- Buscar todos os registros de veículos no período
    -- Identificando se a passagem foi em equipamento de área monitorada
    INSERT INTO #RegistrosVeiculos (placa, data_passagem, id_local, id_area_monitorada, seq_registro)
    SELECT 
        vtr.placa,
        vtr.data AS data_passagem,
        vtr.id_local,
        eam.id_area_monitorada,
        ROW_NUMBER() OVER (PARTITION BY vtr.placa ORDER BY vtr.data) AS seq_registro
    FROM muralha.veiculo_tempo_real vtr
    LEFT JOIN muralha.equipamentos_area_monitorada eam 
        ON vtr.id_local = eam.id_equipamento
    LEFT JOIN muralha.area_monitorada am 
        ON eam.id_area_monitorada = am.id
    WHERE vtr.data >= @data_ini 
        AND vtr.data < DATEADD(DAY, 1, @data_fim)
        AND (@placa IS NULL OR vtr.placa = @placa)
        AND (@area_monitorada IS NULL OR eam.id_area_monitorada = CAST(@area_monitorada AS INT))
		AND am.deletado = 0
    ORDER BY vtr.placa, vtr.data;
    
    -- Retornar registros para análise inicial
    SELECT 
        placa,
        data_passagem,
        id_local,
        id_area_monitorada,
        CASE 
            WHEN id_area_monitorada IS NOT NULL THEN 'Equipamento de Área Monitorada'
            ELSE 'Equipamento Comum'
        END AS tipo_equipamento,
        seq_registro
    FROM #RegistrosVeiculos
    ORDER BY placa, seq_registro;
    
    -- Limpeza
    DROP TABLE #RegistrosVeiculos;
    
END;
