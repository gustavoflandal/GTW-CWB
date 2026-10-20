# Entrega — Plano 27: Score de OCR, Revisão Automática e Múltiplas Placas

> **Data:** 2026-10-16
> **Status:** 🟡 Implementado — aguardando teste no navegador (build e testes automatizados aprovados; migração executada no DEV em 2026-10-16, conferida em 2026-10-20).
> **Fase:** 5 de 11 da correção da auditoria de 2026-10-16.

## Escopo

Score de OCR por placa guardado de forma permanente, limiar parametrizável (global e por equipamento), encaminhamento automático para a fila de revisão, N placas por passagem com origem (embarcada, central, manual), correção manual com motivo e justificativa obrigatórios nos três caminhos de correção, e fila com filtros por equipamento, período, score, tipo de intervenção e operador.

## Origem

- Plano: [`27-score-ocr-revisao-placas.md`](../planos-salvador/27-score-ocr-revisao-placas.md)
- **Errata aplicada:** [`00c-errata-correcoes.md`](../planos-salvador/00c-errata-correcoes.md) §1/§2/§3 (sessão, acesso e estrutura dos servlets novos e do `CorrecaoPlacaServlet`), §12/§13 (PK `uniqueidentifier` das passagens), §14 (job no `Agendador`)

## Onde o score chega hoje

Em lugar nenhum: a procedure que grava as passagens (`dbo.spu_veiculo_tempo_real`) não recebe score, `ia.veiculo_caracteristica` não tem coluna de score e o `dado_bruto` está vazio no DEV. Por isso a origem do score é parametrizável (`TABELA_LEITURA`, `DADO_BRUTO:<campo>` ou `AUSENTE`, o padrão) e, enquanto o Captura não enviar o score (Fase 11), **a fila automática fica vazia**: nada passa do limiar porque não há score. O encaminhamento de passagens sem placa lida existe, mas fica desligado (`OCR_REVISAR_SEM_LEITURA = 0`), porque no DEV são 23% das passagens.

## O que mudou

- **`muralha.vtr_placa_leitura`** (5.7.1.4, 5.7.4.4, 5.7.5.1): uma linha por placa e origem (EMBARCADA, CENTRAL, MANUAL), com tipo (tração, frontal, reboque, semirreboque) e score. A migração copia `placa`/`placa_frontal` dos últimos 30 dias (~850 mil linhas no DEV); o job grava as passagens novas.
- **Regra e job** (5.7.4.2-3, 5.2.2.5): `JobOcrRevisao` a cada 5 minutos avalia cada passagem uma vez com o limiar do equipamento (`muralha.equipamento_ocr_config`) ou o global (`OCR_SCORE_LIMIAR`, padrão 85) e põe em `muralha.vtr_revisao_placa` as de score baixo, as com divergência embarcada × central e, se configurado, as sem leitura ou sem score.
- **Correção com justificativa** (5.7.5.2): `CorrecaoPlacaService` é o único caminho de gravação, usado pela fila, pela correção em lote e pelo modal da consulta de veículos. Exige motivo (lista em `muralha.motivo_correcao_placa`; "Outro" e "frontal diferente" exigem texto) e grava `veiculo_tempo_real_correcao`, `vtr_correcao_justificativa`, leituras MANUAL, a situação na fila e o log de auditoria. O modal da consulta antes nem gravava em `veiculo_tempo_real_correcao`.
- **Segurança da correção em lote:** o `CorrecaoPlacaServlet` não verificava sessão nem permissão e gravava o `idUsuario` enviado pelo navegador; agora verifica as duas e usa o usuário da sessão.
- **Fila** (5.7.5.4): tela Consulta de Veículos → Revisão de Placas com filtros de período, equipamento, situação, motivo, faixa de score, tipo de intervenção (fila, lote, consulta) e operador; revisão com imagens, todas as placas lidas, placas adicionais, histórico de correções; aba "Score e limiar" (grupo 42) com origem e limiar padrão e por equipamento, registrados em `parametro_historico`.
- **Consulta de veículos:** o modal de detalhe mostra todas as placas lidas (com score e origem) e pede motivo/justificativa ao alterar a placa. A correção em lote mostra a placa frontal e pede a justificativa uma vez por lote.

## Arquivos

| Tipo | Arquivos |
|---|---|
| Regras | `src/main/java/muralha/digital/ocr/LeituraPlaca.java`, `OrigemScore.java`, `RegraRevisao.java`, `ValidacaoCorrecao.java`, `FiltroRevisao.java`, `ParametrosOcr.java`, `PassagemPendente.java` |
| Processamento | `ProcessadorOcr.java`, `RepositorioOcr.java`, `JdbcRepositorioOcr.java`, `JobOcrRevisao.java`; registro em `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` |
| Correção | `CorrecaoPlacaService.java`; `src/main/java/muralha/digital/veiculo/VeiculoServlet.java` (`AlterarPlaca`), `src/main/java/muralha/digital/correlacaoplaca/CorrecaoPlacaServlet.java` (reescrito), `VeiculoRegistro.java` (removido `ProcessarCorrecaoPlacas`) |
| Servlets | `RevisaoPlacaServlet.java` (`/MuralhaDigital/RevisaoPlaca`), `OcrConfigServlet.java` (`/MuralhaDigital/OcrConfig`) |
| Telas | `src/main/webapp/muralha-digital/pages/processamento/revisao-placas/` (novo), `assets/js/justificativa-correcao-placa.js` (novo), `pages/consulta-veiculo/modal-detalhe-veiculo.jsp` e `js/modal-detalhe-veiculo.js`, `pages/correcao-placas-lote/correcao-placas-lote.jsp` e `assets/js/correcao-placas-lote.js` |
| Testes | `src/test/java/muralha/digital/ocr/RegraRevisaoTest.java`, `ProcessadorOcrTest.java`, `CorrecaoEFiltroRevisaoTest.java` |
| Banco | `docs/banco-de-dados/migracoes/20261016_ocr_score_revisao_placas.sql` |

## Migrações

| Script | Situação |
|---|---|
| `20261016_ocr_score_revisao_placas.sql` | **Não executado — necessário** para a fila, a justificativa em tabela e as N placas. Sem ele, a correção continua funcionando (motivo único "Outro", justificativa só no log de auditoria) e o job só registra aviso. Sintaxe conferida com `SET PARSEONLY ON` |

## Parâmetros (`muralha.config_chave_valor`)

| Chave | Padrão | Uso |
|---|---|---|
| `OCR_SCORE_ORIGEM` | `AUSENTE` | `TABELA_LEITURA`, `DADO_BRUTO:<campo>` ou `AUSENTE` |
| `OCR_SCORE_LIMIAR` | `85` | Limiar global (0-100); exceção por equipamento em `equipamento_ocr_config` |
| `OCR_SCORE_ESCALA` | `100` | `100` = score 0-100; `1` = score 0-1 |
| `OCR_REVISAR_SEM_SCORE` | `0` | 1 = passagem sem score vai para revisão |
| `OCR_REVISAR_SEM_LEITURA` | `0` | 1 = passagem sem placa lida vai para revisão |
| `OCR_JANELA_HORAS` | `24` | Só avalia passagens das últimas N horas |
| `OCR_LOTE` | `500` | Passagens por execução |
| `OCR_ESPERA_SCORE_MIN` | `10` | Minutos esperando o score antes de avaliar sem ele |

## Critérios de aceite

- [x] 1. `..\.setup-gtw\build.ps1` compila sem erro (Windows, 2026-10-16).
- [x] 2. Testes TestNG (Windows, 2026-10-16): `RegraRevisaoTest` 9, `ProcessadorOcrTest` 6, `CorrecaoEFiltroRevisaoTest` 17. Total 32/32; com as fases anteriores, 160/160. Cobrem limiar global e por equipamento, encaminhamento por score, sem leitura (só se configurado) e divergência, espera do score, leituras da recepção, justificativa obrigatória, formato de placa, placas adicionais e filtros da fila.
- [ ] 3. Migração executada (com autorização) e roteiro [`docs/testes/27-score-ocr-revisao-placas.md`](../testes/27-score-ocr-revisao-placas.md) no navegador. *Migração executada no DEV em 2026-10-16 (6 tabelas, ~847 mil leituras em `vtr_placa_leitura`, menus 389–391; ver [`migracoes/README.md`](../banco-de-dados/migracoes/README.md)); falta o roteiro no navegador.*
- [x] 4. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Situação dos itens do TR

| Item | Antes | Depois | Por quê |
|---|---|---|---|
| 5.7.1.4 | ❌ | ⚠️ | N placas por passagem (manual na revisão; tração e frontal do equipamento); o equipamento ainda não envia reboques |
| 5.7.4.1 | ⚠️ | ⚠️ | Score guardado quando houver origem; nenhum equipamento envia hoje |
| 5.7.4.2 | ❌ | ⚠️ | Limiar global e por equipamento, com tela; aguardando teste |
| 5.7.4.3 | ⚠️ | ⚠️ | Encaminhamento automático pronto; sem score, a fila não recebe nada |
| 5.7.4.4 | ❌ | ⚠️ | Score como metadado em `vtr_placa_leitura`; migração executada, aguardando teste |
| 5.2.2.5 | ⚠️ | ⚠️ | Regra de divergência embarcada × central pronta; não há OCR central |
| 5.7.5.2 | ⚠️ | ⚠️ | Justificativa obrigatória nos três caminhos; aguardando teste |
| 5.7.5.4 | ⚠️ | ⚠️ | Fila com os cinco filtros; aguardando teste |

## Limitações conhecidas

- Sem score vindo do equipamento, o fluxo automático não tem o que encaminhar (Fase 11).
- A correção de placa frontal na fila atualiza `placa_frontal`; a do modal da consulta só corrige a placa principal, como antes.
- Correção em lote: um único motivo/justificativa para todas as placas alteradas do lote.
- A cópia inicial só cobre 30 dias (`@dias` no script); passagens mais antigas mostram as placas pelas colunas da própria passagem.
