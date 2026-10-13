-- Adiciona coluna tipo_visual à tabela kpi_config
-- Permite ao usuário escolher como exibir cada KPI no dashboard
-- Executar em: GTW_MURALHA_DEV (10.0.0.200)

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'muralha' AND TABLE_NAME = 'kpi_config'
      AND COLUMN_NAME = 'tipo_visual'
)
BEGIN
    ALTER TABLE muralha.kpi_config
    ADD tipo_visual VARCHAR(20) NOT NULL DEFAULT 'card';

    PRINT 'Coluna tipo_visual adicionada a muralha.kpi_config';
END
ELSE
    PRINT 'Coluna tipo_visual ja existe';

-- Valores aceitos:
--   card      = Card com número grande (padrão)
--   compacto  = Indicador compacto em linha
--   medidor   = Gauge semicircular
--   progresso = Barra de progresso
