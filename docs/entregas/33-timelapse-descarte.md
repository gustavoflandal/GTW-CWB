# Entrega 33 — Descarte Automático de Time-lapse

## Origem

[Plano 33](../planos-salvador/33-timelapse-descarte.md) — Frente 9 da auditoria de 2026-10-16.

## Escopo

Implementação de descarte automático parametrizado de dados de sessões de time-lapse, com retenção configurável (padrão 72 horas), atendendo §5.7.7.5-6 do TR TRANSALVADOR.

## O que foi implementado

### Back-end

| Artefato | Descrição |
|---|---|
| `JobTimelapseDescarte.java` | Job Quartz que roda a cada 60 min: lê `TIMELAPSE_RETENCAO_HORAS` (padrão 72h) e remove registros mais antigos de `timelapse_cache` |
| `TimelapseServlet.java` (modificado) | Agora registra cada sessão de consulta de frames na tabela `timelapse_cache` (equipamento, período, total de frames, usuário) |
| Registro no Agendador | Constantes `GRUPO_TIMELAPSE`, `JOB_TIMELAPSE_DESCARTE`; método `reajustaJobTimelapseDescarte()` |

### Banco de dados

| Migração | Status |
|---|---|
| `20261016_timelapse_descarte.sql` | ✅ Executada no DEV (objetos e chaves presentes; conferido em 2026-10-20) |

- Tabela `muralha.timelapse_cache` — registra sessões de timelapse (id_local, dt_inicio, dt_fim, total_frames, dt_geracao, id_usuario)
- Índice `IX_timelapse_cache_dt` para limpeza eficiente por data
- 2 parâmetros em `config_chave_valor`: `TIMELAPSE_RETENCAO_HORAS` (72), `TIMELAPSE_DESCARTE_HABILITADO` (true)

### Fluxo

1. Usuário acessa a tela de time-lapse e carrega frames de um equipamento/período
2. `TimelapseServlet` registra a sessão na tabela `timelapse_cache`
3. `JobTimelapseDescarte` roda a cada 60 min
4. Remove registros de `timelapse_cache` com `dt_geracao` mais antiga que o período de retenção configurado
5. Período de retenção parametrizável via `TIMELAPSE_RETENCAO_HORAS`

## Critérios de aceite

- [x] Build compila sem erros
- [x] Migração executada no banco DEV (conferido em 2026-10-20)
- [ ] Sessões de timelapse são registradas no cache ao carregar frames
- [ ] Job de descarte remove sessões expiradas
- [ ] Período de retenção configurável via `config_chave_valor`
