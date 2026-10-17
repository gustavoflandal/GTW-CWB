# Retry de Notificações em Falha de Comunicação — Plano de Implementação

> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) antes de usar o código deste plano.

**Goal:** Implementar reenvio automático de notificações (e-mail e SMS) que falharam, com rastreamento de tentativas e limites configuráveis, atendendo §5.3.5.3 do TR TRANSALVADOR.

**Architecture:** Job Quartz (`JobRetryNotificacao`) roda a cada 5 min, identifica notificações com status `NAO_ENVIADO` nas tabelas `alerta_notificacao` e `ocorrencia_notificacao`, verifica se o limite de tentativas não foi excedido consultando `notificacao_retry`, e reseta o status para `PENDENTE` para que os jobs existentes de envio (`JobEnviaEmailAlerta`, `JobEnviaSmsAlerta` etc.) reprocessem.

**Tech Stack:** Java Servlet · Quartz · SQL Server

## Referência ao Edital (TR TRANSALVADOR)

| Ref. TR | Requisito |
|---|---|
| §5.3.5.3 | Reenvio automático em caso de falha de comunicação |

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/notificacao/JobRetryNotificacao.java` |
| Criar | `docs/banco-de-dados/migracoes/20261016_retry_notificacao.sql` |
| Modificar | `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` |

## Parâmetros de configuração (`config_chave_valor`)

| Chave | Padrão | Descrição |
|---|---|---|
| `RETRY_NOTIFICACAO_MAX` | 3 | Máximo de tentativas de reenvio por notificação |
| `RETRY_NOTIFICACAO_INTERVALO_MIN` | 5 | Intervalo em minutos entre verificações |
| `RETRY_NOTIFICACAO_HABILITADO` | true | Habilita/desabilita o mecanismo de retry |

## Migração

Script: `docs/banco-de-dados/migracoes/20261016_retry_notificacao.sql`
- Cria tabela `muralha.notificacao_retry`
- Insere 3 parâmetros em `config_chave_valor`
