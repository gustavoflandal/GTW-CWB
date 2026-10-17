# Testes — Entrega 32: Retry de Notificações

## Testes unitários (TestNG)

| # | Classe | Método | Verificação |
|---|---|---|---|
| 1 | `RetryNotificacaoTest` | `testRetryAlertaNaoEnviado` | Notificação com status NAO_ENVIADO é resetada para PENDENTE |
| 2 | `RetryNotificacaoTest` | `testMaxTentativasRespeitado` | Após N tentativas, notificação permanece NAO_ENVIADO |
| 3 | `RetryNotificacaoTest` | `testRegistroTentativaInserido` | Cada retry insere registro em `notificacao_retry` |
| 4 | `RetryNotificacaoTest` | `testContadorTentativasIncrementa` | Tentativa incrementa corretamente (1, 2, 3...) |
| 5 | `RetryNotificacaoTest` | `testDesabilitadoViaConfig` | Com `RETRY_NOTIFICACAO_HABILITADO=false`, nenhum retry é feito |
| 6 | `RetryNotificacaoTest` | `testOcorrenciaTambemRetentada` | Retry funciona para `ocorrencia_notificacao` além de `alerta_notificacao` |
| 7 | `RetryNotificacaoTest` | `testNotificacaoEnviadaNaoRetentada` | Notificações com status ENVIADO não são tocadas |
| 8 | `RetryNotificacaoTest` | `testResetaDataProcessado` | Ao resetar para PENDENTE, `data_processado` é anulado |

**Total: 8 testes**

## Teste manual no navegador

| # | Passo | Esperado |
|---|---|---|
| 1 | Verificar logs do Tomcat para `[Muralha] Job Retry de Notificações` | Job executa a cada 5 min |
| 2 | Inserir manualmente uma notificação com status NAO_ENVIADO | Retry identifica e reagenda |
| 3 | Verificar tabela `notificacao_retry` | Registro da tentativa com número sequencial |
| 4 | Alterar `RETRY_NOTIFICACAO_MAX` para 1 e verificar | Após 1 tentativa, para de reagendar |
