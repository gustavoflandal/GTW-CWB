# Entrega — Plano 25: Integridade SHA-256 na Recepção

> **Data:** 2026-10-16
> **Status:** 🟡 Implementado — aguardando migração e teste (build e testes automatizados aprovados no Windows; migração não executada; roteiro no navegador pendente).
> **Fase:** 3 de 11 da correção da auditoria de 2026-10-16.

## Escopo

Conferência real de integridade das imagens recebidas (TR 5.2.1.2) e registro de origem, captura, recepção e contadores de recebidos, íntegros e rejeitados com motivo (TR 5.2.1.3 a-f). Substitui o `'OK'` gravado sem comparação pelo `JobSha256Imagem` da [Entrega 02](02-sha256-integridade.md).

## Origem

- Plano: [`25-integridade-sha256-recepcao.md`](../planos-salvador/25-integridade-sha256-recepcao.md)
- **Errata aplicada:** [`00c-errata-correcoes.md`](../planos-salvador/00c-errata-correcoes.md) §1/§2 (sessão e acesso nos servlets), §12/§13 (PK `uniqueidentifier` e tipo `image`), §14 (job no `Agendador`, registro existente mantido)

## O que mudou

- **Conferência:** para cada imagem das passagens das últimas `INTEGRIDADE_JANELA_HORAS` horas ainda não conferidas, o job obtém o hash de origem conforme o equipamento (`PAYLOAD_SOAP`, `DADO_BRUTO:<campo>` ou `AUSENTE`), compara com o hash calculado (SHA-256, ou SHA-384/512 se a origem mandar um deles) e grava `INTEGRO`, `DIVERGENTE`, `SEM_CONFERENCIA` ou `ERRO_LEITURA`, com motivo e data da verificação. O `'OK'` não é mais gravado.
- **Divergência:** um alerta por passagem em `muralha.alerta` (tipo `INTEGRIDADE`, `origem = 'INTEGRIDADE_SHA256'`, ligado em `alerta_veiculo`) e um registro por imagem em `sis_log_auditoria` (login `sistema`).
- **Contadores (5.2.1.3 d-f):** view `muralha.v_integridade_recepcao` por equipamento e dia; rejeitados = divergentes + erro de leitura.
- **Tela** Análise de Dados → Integridade das Imagens: filtros de período (até 92 dias), equipamentos e status; cartões de totais; tabela por equipamento e dia; imagens paginadas no servidor; CSV completo com SHA-256 no rodapé (auditado), XLS e PDF da tabela da tela. Aba Configuração (só grupo 42) para a origem padrão e por equipamento; cada alteração vai para `sis_log_auditoria` com valor anterior e novo.

## Arquivos

| Tipo | Arquivos |
|---|---|
| Regras (sem banco) | `src/main/java/muralha/digital/integridade/OrigemHash.java`, `StatusIntegridade.java`, `ConferenciaIntegridade.java`, `ResultadoConferencia.java`, `FiltroIntegridade.java`, `ImagemPendente.java`, `ParametrosIntegridade.java` |
| Processamento | `ProcessadorIntegridade.java`, `RepositorioIntegridade.java`, `JdbcRepositorioIntegridade.java`, `JobSha256Imagem.java` (reescrito) |
| Servlets | `IntegridadeRecepcaoServlet.java` (`/MuralhaDigital/IntegridadeRecepcao`), `IntegridadeConfigServlet.java` (`/MuralhaDigital/IntegridadeConfig`) |
| Utilitários | `src/main/java/muralha/digital/util/HashUtil.java` (`hex` com algoritmo), `ConfigChaveValor.java` (novo); `src/main/java/muralha/digital/auditoria/AuditoriaService.java` (`registrarSistema`) |
| Tela | `src/main/webapp/muralha-digital/pages/monitoramento/integridade/index.jsp`, `assets/js/integridade.js`, `assets/css/integridade.css` |
| Testes | `src/test/java/muralha/digital/integridade/ConferenciaIntegridadeTest.java`, `OrigemHashTest.java`, `ProcessadorIntegridadeTest.java`, `FiltroIntegridadeTest.java` |
| Banco | `docs/banco-de-dados/migracoes/20261016_integridade_sha256_recepcao.sql` |

## Migrações

| Script | Situação |
|---|---|
| `20261016_integridade_sha256_recepcao.sql` | **Não executado — necessário.** Sintaxe conferida no DEV com `SET PARSEONLY ON` (nada executado). Enquanto não rodar, o `JobSha256Imagem` registra um aviso e não confere nenhuma imagem (antes ele gravava `'OK'`). Converte 196,8 mil linhas `'OK'` em lotes de 20 mil |

## Parâmetros (`muralha.config_chave_valor`)

| Chave | Padrão | Uso |
|---|---|---|
| `INTEGRIDADE_ORIGEM_HASH` | `AUSENTE` | Origem do hash para equipamentos sem configuração própria |
| `INTEGRIDADE_LOTE` | `200` | Imagens por execução (job a cada 5 min) |
| `INTEGRIDADE_JANELA_HORAS` | `24` | Só confere passagens capturadas nas últimas N horas |
| `INTEGRIDADE_ESPERA_HASH_MIN` | `10` | Minutos esperando o hash de origem antes de concluir "não recebido" |
| `INTEGRIDADE_ALERTA_HABILITADO` | `1` | Divergência gera alerta em `muralha.alerta` |

## Critérios de aceite

- [x] 1. `..\.setup-gtw\build.ps1` compila sem erro (Windows, 2026-10-16: BUILD SUCCESS).
- [x] 2. Testes TestNG (Windows, 2026-10-16): `ConferenciaIntegridadeTest` 10, `OrigemHashTest` 6, `ProcessadorIntegridadeTest` 7, `FiltroIntegridadeTest` 8. Total 31/31, junto com os testes das fases anteriores: 90/90. Cobrem hash igual → `INTEGRO`; diferente → `DIVERGENTE` + alerta (um por passagem) + auditoria; origem ausente → `SEM_CONFERENCIA`; imagem vazia → `ERRO_LEITURA`.
- [ ] 3. Migração executada no DEV, sem `'OK'` restante. Depende de autorização.
- [ ] 4. Roteiro [`docs/testes/25-integridade-sha256-recepcao.md`](../testes/25-integridade-sha256-recepcao.md) executado no navegador. Não executado: subir o Tomcat liga os jobs, que gravam no DEV.
- [x] 5. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Situação dos itens do TR

| Item | Antes | Depois | Por quê |
|---|---|---|---|
| 5.2.1.2 | ⚠️ hash a posteriori, `'OK'` sem comparação | ⚠️ conferência implementada | O mecanismo compara e sinaliza, mas nenhum equipamento envia hash hoje (no DEV o `dado_bruto` está vazio); até o Captura enviar (Fase 11), as imagens ficam `SEM_CONFERENCIA` |
| 5.2.1.3 | ⚠️ dados espalhados | ⚠️ a-f registrados por imagem e por equipamento/dia | Falta executar a migração e o roteiro |

## Limitações conhecidas

- **Sem hash de origem, não há detecção de adulteração.** Só a Fase 11 (envio pelo Captura com hash) ou a configuração `DADO_BRUTO` com um campo real tornam a conferência efetiva.
- **Alertas de integridade não aparecem no popup operacional:** `spu_ObterNovosAlertasV2` exige veículo monitorado (`INNER JOIN cad_veiculo_monitorado`). A consulta de alertas (`spu_ObterDadosAlertaOcorrencia`) usa `LEFT JOIN` e deve listá-los sem placa, o que não foi testado. A exibição adequada (tipo, cor, ícone, tratativa) é escopo da Fase 6. A trigger `TRG_alerta_semelhanca_placa_after_insert` não age sem veículo monitorado.
- A imagem é conferida depois de gravada pela `spu_veiculo_tempo_real_imagem` (sistema externo); não há recusa na recepção.
- Imagens de passagens mais antigas que a janela não são conferidas (o legado convertido fica `SEM_CONFERENCIA`).
- `vtr_imagem_complemento.status_integridade` mantém `DEFAULT 'OK'` (mudar exige `ALTER TABLE`); nenhum código grava sem informar o status.
