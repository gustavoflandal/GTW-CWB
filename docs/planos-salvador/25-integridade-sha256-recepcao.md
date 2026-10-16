# Integridade SHA-256 na Recepção — Plano 25 (Fase 3 da auditoria de 2026-10-16)

> **Status:** Executado (2026-10-16): ver [Entrega 25](../entregas/25-integridade-sha256-recepcao.md).
> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) (§1, §2, §12, §13 e §14 aplicadas).
> **Depende de:** [Plano 02](02-sha256-integridade.md) (hash a posteriori que este plano corrige) e [Plano 24](24-correcoes-pontuais-auditoria.md) (script de `vtr_imagem_complemento`).

**Objetivo:** trocar o `'OK'` gravado sem comparação por uma conferência real: comparar o SHA-256 da imagem gravada com o hash enviado pela origem, configurável por equipamento, e registrar o resultado, os contadores e os motivos de rejeição.

## Referência ao Edital (TR TRANSALVADOR)

| Ref. TR | Texto do TR ([`TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md)) |
|---|---|
| 5.2.1.2 | "A recepção dos registros deverá contemplar verificação automática de integridade dos arquivos mediante utilização de função hash **SHA-256** ou superior, assegurando a detecção de corrupção, adulteração ou inconsistência dos dados transmitidos." |
| 5.2.1.3 | "O sistema deverá registrar automaticamente: a) identificação do equipamento de origem; b) data e horário da captura; c) data e horário da recepção; d) quantidade de registros recebidos; e) quantidade de registros íntegros; f) quantidade de registros rejeitados e respectivos motivos." |

## Situação encontrada (investigação de 2026-10-16)

- As imagens são gravadas por `dbo.spu_veiculo_tempo_real_imagem`, chamada por sistema externo; não há ponto Java na gravação. `CSXEventsWS` recebe só eventos, não imagens.
- Nenhuma fonte de hash de origem existe hoje: no DEV, `ia.veiculo_caracteristica.dado_bruto` está vazio nas 8,2 milhões de linhas e não há coluna de hash nas tabelas de passagem. As 196,8 mil linhas de `muralha.vtr_imagem_complemento` estavam todas com `'OK'`.
- `veiculo_tempo_real` tem `id_captura` (índice), ligado a `ia.veiculo_caracteristica.id_captura` (índice único), e `data_importado` (hora da recepção). A última passagem no DEV é de 2026-10-01.
- `muralha.spu_ObterNovosAlertasV2` faz `INNER JOIN` com `cad_veiculo_monitorado`: um alerta sem veículo monitorado não chega ao popup operacional. `spu_ObterDadosAlertaOcorrencia` (consulta de alertas) usa `LEFT JOIN`, e a trigger `TRG_alerta_semelhanca_placa_after_insert` ignora alerta sem veículo monitorado.

## Decisões

- **Origem do hash parametrizável** (decisão já tomada no prompt da auditoria): `PAYLOAD_SOAP` (hash enviado pelo Captura e gravado pela recepção em `muralha.vtr_imagem_hash_origem`), `DADO_BRUTO:<campo>` (caminho no JSON de `ia.veiculo_caracteristica.dado_bruto`; lista = um hash por índice de imagem) ou `AUSENTE`. Padrão global em `INTEGRIDADE_ORIGEM_HASH` = `AUSENTE`; exceções em `muralha.equipamento_integridade_config`.
- **Resultado em tabela nova** `muralha.vtr_imagem_integridade` (uma linha por imagem, com `id_local`, captura e recepção copiados da passagem para não varrer as tabelas de 10 milhões de linhas). `vtr_imagem_complemento.status_integridade` continua sincronizado. Sem `ALTER TABLE`.
- **"SHA-256 ou superior"**: aceita hash de origem SHA-256, SHA-384 ou SHA-512 (pelo tamanho); grava sempre o SHA-256 calculado.
- **Hash que ainda pode chegar**: com origem configurada e sem hash, o job espera `INTEGRIDADE_ESPERA_HASH_MIN` minutos (padrão 10) antes de gravar `SEM_CONFERENCIA` com motivo "não recebido".
- **Rejeitados** (5.2.1.3 f) = `DIVERGENTE` + `ERRO_LEITURA`, com o motivo em cada linha. A recusa da imagem no momento da recepção depende do Captura (Fase 11); aqui a imagem é sinalizada, não descartada.
- **Alerta**: um por passagem, tipo novo `INTEGRIDADE` em `muralha.alerta` + `alerta_veiculo`, com `origem = 'INTEGRIDADE_SHA256'`. Exibição no popup operacional fica para a Fase 6 (alertas sem veículo monitorado). Cada imagem divergente também vai para `sis_log_auditoria` (login `sistema`).

## Tarefas

- [x] 1. Regras puras: `OrigemHash`, `StatusIntegridade`, `ConferenciaIntegridade` (comparação e leitura do dado bruto), `FiltroIntegridade` (filtros da consulta).
- [x] 2. `ProcessadorIntegridade` com `RepositorioIntegridade` (interface) e `JdbcRepositorioIntegridade`; `JobSha256Imagem` reescrito sobre eles (`@DisallowConcurrentExecution`; não roda sem a migração).
- [x] 3. `AuditoriaService.registrarSistema` para registros feitos por job; `HashUtil.hex` para SHA-384/512; `ConfigChaveValor` (leitura e gravação de `config_chave_valor`).
- [x] 4. Migração `20261016_integridade_sha256_recepcao.sql`: três tabelas, chaves `INTEGRIDADE_*`, tipo de alerta, conversão do legado `'OK'` → `SEM_CONFERENCIA`, view `muralha.v_integridade_recepcao`, menus.
- [x] 5. Servlets `IntegridadeRecepcaoServlet` (resumo, listar paginado, CSV com SHA-256) e `IntegridadeConfigServlet` (origem padrão e por equipamento, com auditoria de valor anterior e novo).
- [x] 6. Tela `pages/monitoramento/integridade/` (abas Recepção e Configuração; XLS/PDF no cliente como no Plano 11).
- [x] 7. Testes TestNG: `ConferenciaIntegridadeTest`, `OrigemHashTest`, `ProcessadorIntegridadeTest`, `FiltroIntegridadeTest`.

## Critérios de aceite

1. `..\.setup-gtw\build.ps1` compila sem erro.
2. Testes: hash igual → `INTEGRO`; diferente → `DIVERGENTE` + um alerta por passagem + auditoria; origem ausente → `SEM_CONFERENCIA`; imagem vazia → `ERRO_LEITURA`.
3. Migração executada no DEV (com autorização): nenhuma linha `'OK'` restante em `vtr_imagem_complemento`.
4. Roteiro [`docs/testes/25-integridade-sha256-recepcao.md`](../testes/25-integridade-sha256-recepcao.md) executado no navegador.
5. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Fora do escopo

- Enviar o hash pelo Captura e gravá-lo em `vtr_imagem_hash_origem` na recepção: Fase 11 (ADR do envio por socket).
- Mostrar os alertas de integridade no popup e na consulta de alertas: Fase 6.
- Histórico de alteração de parâmetros em tabela própria (`parametro_historico`): Fase 4. Aqui a alteração vai para `sis_log_auditoria` com valor anterior e novo.
