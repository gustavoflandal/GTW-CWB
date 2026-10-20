# Entrega 32 — Retry de Notificações em Falha de Comunicação

## Origem

[Plano 32](../planos-salvador/32-retry-notificacao.md) — Fase 10 da auditoria de 2026-10-16.

## Escopo

Implementação de mecanismo de reenvio automático para notificações (e-mail e SMS) que falharam na primeira tentativa de envio, atendendo §5.3.5.3 do TR TRANSALVADOR.

## O que foi implementado

### Back-end

| Artefato | Descrição |
|---|---|
| `JobRetryNotificacao.java` | Job Quartz que roda a cada 5 min: identifica notificações com status `NAO_ENVIADO`, verifica se o número de tentativas não excedeu o máximo configurado, e reseta o status para `PENDENTE` para que os jobs de envio existentes reprocessem |
| Registro no Agendador | Constantes `GRUPO_RETRY_NOTIFICACAO`, `JOB_RETRY_NOTIFICACAO`; método `reajustaJobRetryNotificacao()` condicionado a `EMAIL_ATIVO_MURALHA || SMS_ATIVO_MURALHA` |

### Banco de dados

| Migração | Status |
|---|---|
| `20261016_retry_notificacao.sql` | ✅ Executada no DEV (objetos e chaves presentes; conferido em 2026-10-20) |

- Tabela `muralha.notificacao_retry` — rastreia cada tentativa com (id_notificacao, tipo_registro, tentativa, dt_tentativa, erro, sucesso)
- Índice `IX_notificacao_retry_notif` para busca rápida por notificação
- 3 parâmetros em `config_chave_valor`: `RETRY_NOTIFICACAO_MAX` (3), `RETRY_NOTIFICACAO_INTERVALO_MIN` (5), `RETRY_NOTIFICACAO_HABILITADO` (true)

### Fluxo

1. Jobs existentes (`JobEnviaEmailAlerta`, `JobEnviaSmsAlerta` etc.) marcam notificações falhadas como `NAO_ENVIADO`
2. `JobRetryNotificacao` identifica essas notificações
3. Para cada uma, consulta `notificacao_retry` para contar tentativas anteriores
4. Se tentativas < `RETRY_NOTIFICACAO_MAX`, registra nova tentativa e reseta status para `PENDENTE`
5. Na próxima execução dos jobs de envio, a notificação é reprocessada
6. Se exceder o máximo, a notificação permanece como `NAO_ENVIADO` (falha definitiva)

## Critérios de aceite

- [x] Build compila sem erros
- [x] Migração executada no banco DEV (conferido em 2026-10-20)
- [ ] Job registrado no Agendador e executa a cada 5 min
- [ ] Notificações com `NAO_ENVIADO` são reagendadas para `PENDENTE`
- [ ] Após N tentativas (configurável), para de tentar
- [ ] Log registra cada tentativa de retry
