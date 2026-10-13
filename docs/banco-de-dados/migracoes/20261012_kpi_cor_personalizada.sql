-- Adiciona coluna cor_personalizada à tabela kpi_config
-- Permite ao usuário definir uma cor fixa para o KPI no dashboard
-- Quando NULL, o sistema usa a cor automática baseada nos limites (Normal/Atenção/Crítico)
-- Executar em: GTW_MURALHA_DEV (10.0.0.200)

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'muralha' AND TABLE_NAME = 'kpi_config'
      AND COLUMN_NAME = 'cor_personalizada'
)
BEGIN
    ALTER TABLE muralha.kpi_config
    ADD cor_personalizada VARCHAR(7) NULL;

    PRINT 'Coluna cor_personalizada adicionada a muralha.kpi_config';
END
ELSE
    PRINT 'Coluna cor_personalizada ja existe';
