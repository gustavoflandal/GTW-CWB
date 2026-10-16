# Score de OCR, Revisão Automática e Múltiplas Placas — Plano 27 (Fase 5 da auditoria de 2026-10-16)

> **Status:** Executado (2026-10-16): ver [Entrega 27](../entregas/27-score-ocr-revisao-placas.md).
> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) (§1, §2, §3, §12, §13 e §14 aplicadas).
> **Depende de:** [Plano 26](26-logs-tecnicos-historico-parametros.md) (histórico de parâmetros usado na configuração do limiar).

**Objetivo:** guardar o score de OCR de cada placa, encaminhar automaticamente para revisão as leituras de baixa confiabilidade, registrar N placas por passagem e exigir justificativa em toda correção manual.

## Referência ao Edital (TR TRANSALVADOR)

Texto literal de [`TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md):

> 5.2.2.5. Divergências identificadas entre leituras automáticas deverão ser encaminhadas automaticamente para revisão operacional.
>
> 5.7.1.4. Registro individual de cada placa em composições veiculares com múltiplas placas visíveis.
>
> 5.7.4.1. Indicador de confiança (score) atribuído automaticamente a cada leitura.
>
> 5.7.4.2. Limiar parametrizável pela CONTRATANTE.
>
> 5.7.4.3. Score inferior ao limiar → encaminhamento automático para revisão operacional.
>
> 5.7.4.4. Score armazenado como metadado permanente do registro.
>
> 5.7.5.1. Distinção obrigatória entre leitura embarcada, leitura central e leitura corrigida manualmente.
>
> 5.7.5.2. Log automático para toda correção manual.
>
> 5.7.5.4. Consulta por equipamento, período, score, tipo de intervenção e operador.

## Situação encontrada (investigação de 2026-10-16, consultas só de leitura no DEV)

- **Onde o score chega hoje: em lugar nenhum.** `dbo.spu_veiculo_tempo_real` (chamada pelo sistema externo que grava as passagens) recebe placa, `placa_frontal`, `info_adicional` (`varchar(7)`), perfis e classificação, sem score. `ia.veiculo_caracteristica` não tem coluna de score e o `dado_bruto` está vazio nas 8,2 milhões de linhas. Nenhuma coluna `%score%`/`%confian%` no banco. Os eventos do Captura só citam arquivos `*_ocr.log`.
- `muralha.veiculo_tempo_real`: 8,2 milhões de passagens, 797 mil nos últimos 30 dias; 1,1 milhão com `placa_frontal`; **1,9 milhão sem placa lida**.
- Correção de placa existia em três caminhos, nenhum com justificativa: modal da consulta de veículos (`Veiculo?acao=alterarPlaca`, só atualiza `veiculo_tempo_real`, não grava em `veiculo_tempo_real_correcao`), correção em lote (`CorrecaoPlacaServlet`: sem verificação de sessão e com o `idUsuario` vindo do navegador) e nenhuma fila por score.

## Decisões

- **Origem do score parametrizável por equipamento** (como a Fase 3): `TABELA_LEITURA` (gravado pela recepção em `muralha.vtr_placa_leitura`, quando o Captura enviar — Fase 11), `DADO_BRUTO:<campo>` (número ou lista, um por placa) ou `AUSENTE` (padrão). Escala 0-100 ou 0-1 (`OCR_SCORE_ESCALA`).
- **`muralha.vtr_placa_leitura`**: N placas por passagem (ordem 1 tração, 2 frontal, 3+ reboques), com tipo, score e origem (EMBARCADA/CENTRAL/MANUAL), atendendo também 5.7.5.1. Os últimos 30 dias de `placa`/`placa_frontal` são copiados pela migração; as colunas originais continuam.
- **Regra de revisão** (`RegraRevisao`), por prioridade: sem placa lida → divergência entre leitura embarcada e central da mesma placa → score abaixo do limiar → (se `OCR_REVISAR_SEM_SCORE = 1`) score ausente. Limiar global `OCR_SCORE_LIMIAR` (padrão 85, o índice mínimo de 5.7.2.1 para placas antigas) com exceção por equipamento.
- **Job `JobOcrRevisao`** (5 em 5 min, janela `OCR_JANELA_HORAS`) avalia cada passagem uma vez (`muralha.vtr_ocr_avaliacao`) e põe as de baixa confiabilidade em `muralha.vtr_revisao_placa`.
- **Correção única** (`CorrecaoPlacaService`) para fila, lote e consulta: motivo obrigatório de `muralha.motivo_correcao_placa` (parametrizável; alguns motivos exigem texto), grava `veiculo_tempo_real_correcao` + `vtr_correcao_justificativa` + leituras MANUAL + situação na fila + `sis_log_auditoria`. Correções feitas fora da fila entram na fila como intervenção `MANUAL`, para a consulta por tipo de intervenção e operador (5.7.5.4). Sem a migração, a correção continua possível, com a justificativa só no log de auditoria.
- **Lote:** placas alteradas exigem motivo/justificativa (uma para o lote); as mantidas viram "conferidas".

## Tarefas

- [x] 1. Regras: `LeituraPlaca`, `OrigemScore`, `RegraRevisao`, `ValidacaoCorrecao`, `FiltroRevisao`, `ParametrosOcr`.
- [x] 2. Processamento: `ProcessadorOcr`, `RepositorioOcr`, `JdbcRepositorioOcr`, `JobOcrRevisao` (registrado no `Agendador`).
- [x] 3. Correção: `CorrecaoPlacaService`; `VeiculoServlet.AlterarPlaca` e `CorrecaoPlacaServlet` passam a usá-lo (este último ganhou sessão/permissão e usa o usuário da sessão); `VeiculoRegistro.ProcessarCorrecaoPlacas` removido.
- [x] 4. Servlets `RevisaoPlacaServlet` (fila) e `OcrConfigServlet` (origem e limiar, com `parametro_historico`).
- [x] 5. Telas: `pages/processamento/revisao-placas/` (fila + configuração); justificativa na consulta de veículos e na correção em lote (`assets/js/justificativa-correcao-placa.js`); placas lidas no modal de detalhe e placa frontal no lote.
- [x] 6. Migração `20261016_ocr_score_revisao_placas.sql`.
- [x] 7. Testes TestNG: limiar, encaminhamento, justificativa obrigatória, N placas, filtros.

## Critérios de aceite

1. `..\.setup-gtw\build.ps1` compila sem erro.
2. Testes: limiar (global e por equipamento), encaminhamento por score/sem leitura/divergência, espera do score, justificativa obrigatória, placas adicionais, filtros da fila.
3. Migração executada (com autorização) e roteiro [`docs/testes/27-score-ocr-revisao-placas.md`](../testes/27-score-ocr-revisao-placas.md) no navegador.
4. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Fora do escopo

- Receber o score e as placas adicionais do equipamento (Fase 11: protocolo do Captura). Sem isso, a fila só recebe passagens **sem placa lida** (e divergências, quando houver leitura central).
- OCR central: não existe no GTW; a regra de divergência fica pronta para quando houver leituras `CENTRAL`.
- Índices de acerto e relatório mensal (5.7.2): outra fase.
