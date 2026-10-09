# Testes — Plano 18: Auditoria de Disponibilidade

## Pré-requisitos

- Servidor rodando (`..\.setup-gtw\run.ps1`)
- Migracao `20261008_disponibilidade_incidentes.sql` executada
- Usuario autenticado com permissao

## Casos de teste

### T01 — Job Quartz registrado

1. Iniciar servidor
2. Verificar log: "Job Disponibilidade Equipamentos agendada a cada 10 minutos"

**Resultado esperado:** Log confirma agendamento no grupo "Monitoramento"

### T02 — Verificacao de disponibilidade

1. Aguardar 10 minutos apos inicio do servidor
2. Consultar: `SELECT TOP 10 * FROM muralha.equipamento_disponibilidade ORDER BY id DESC`

**Resultado esperado:** Registros inseridos com `id_local`, flag `disponivel` e `ultima_passagem`

### T03 — Painel de status

1. Acessar `/muralha-digital/pages/monitoramento/disponibilidade/index.jsp`
2. Verificar cards de contagem

**Resultado esperado:** Cards "Online agora", "Offline agora", "Uptime medio", "Total monitorados" com valores numericos

### T04 — Tabela de status atual

1. No painel, verificar tabela "Status atual"
2. Verificar que cada equipamento exibe nome, badge de status, ultima passagem e tempo offline

**Resultado esperado:** Equipamentos offline destacados em vermelho, online sem destaque

### T05 — Grafico de uptime 24h

1. No painel, verificar grafico horizontal
2. Verificar cores das barras (verde >= 95%, amarelo >= 80%, vermelho < 80%)

**Resultado esperado:** Grafico renderiza com barras horizontais e escala ate 100%

### T06 — Auto-refresh

1. Manter painel aberto por mais de 60 segundos
2. Verificar que dados atualizam automaticamente

**Resultado esperado:** Tabela e grafico atualizam sem recarregar a pagina

### T07 — Threshold configuravel

1. Alterar valor: `UPDATE muralha.config_chave_valor SET valor='5' WHERE chave='disponibilidade_threshold_min'`
2. Aguardar proxima execucao do job
3. Verificar que equipamentos com mais de 5 min sem passagem aparecem como offline

**Resultado esperado:** Threshold de 5 minutos aplicado na proxima verificacao

### T08 — Sem dados iniciais

1. Acessar painel antes da primeira execucao do job (tabela vazia)

**Resultado esperado:** Cards exibem 0/0/0, tabela e grafico vazios (sem erro JS)
