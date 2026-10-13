-- Corrige encoding duplo no nome do KPI "Pré-aprovadas (7d)"
-- O INSERT original usou literal varchar sem prefixo N'...', causando
-- double-encoding UTF-8 dependendo da ferramenta de execução.
-- Executar em: GTW_MURALHA_DEV (10.0.0.200)

UPDATE muralha.kpi_config
SET nome = N'Pré-aprovadas (7d)'
WHERE id = 3;

-- Corrige também qualquer outro campo que possa ter o mesmo problema
UPDATE muralha.kpi_config
SET nome = N'Pendentes de Análise'
WHERE id = 2;
