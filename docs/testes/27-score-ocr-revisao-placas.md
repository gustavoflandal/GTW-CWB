# Testes — Plano 27: Score de OCR, Revisão Automática e Múltiplas Placas

Roteiro para a [Entrega 27](../entregas/27-score-ocr-revisao-placas.md). Executar a partir de `D:\GTW-CWB`.

## Pré-requisitos

- Build: `..\.setup-gtw\build.ps1`; servidor: `..\.setup-gtw\run.ps1`.
- Migração `docs/banco-de-dados/migracoes/20261016_ocr_score_revisao_placas.sql` executada com autorização (`sqlcmd -b -f 65001 -i …`).
- Usuário do grupo 42 (configuração) e do grupo 41 (fila sem configuração).
- O DEV não recebe passagens desde 2026-10-01: para o job avaliar, `UPDATE muralha.config_chave_valor SET valor = '720' WHERE chave = 'OCR_JANELA_HORAS'` (voltar para `24` no fim).

## T01 — Testes automatizados

```powershell
..\.setup-gtw\gtw.ps1 -ModulePath D:\GTW-CWB -Goals test -AllowInsecure `
  -MavenArgs '-Dexec.skip=true','-Dtest=RegraRevisaoTest,ProcessadorOcrTest,CorrecaoEFiltroRevisaoTest'
```

**Esperado:** 32 testes, 0 falhas. No Windows (2026-10-16): ✅ 32/32.

## T02 — Migração

```sql
SELECT origem, tipo, COUNT(*) FROM muralha.vtr_placa_leitura GROUP BY origem, tipo;
SELECT chave, valor FROM muralha.config_chave_valor WHERE chave LIKE 'OCR[_]%';
SELECT id, descricao, exige_texto, confirmacao FROM muralha.motivo_correcao_placa;
```

**Esperado:** leituras EMBARCADA/TRACAO dos últimos 30 dias (~850 mil no DEV), oito chaves `OCR_*`, sete motivos. Rodar de novo só imprime "Ja existe" e copia 0 placas. ⏳ Pendente.

## T03 — Score abaixo do limiar vai para a fila

1. Escolher uma passagem recente com `id_captura` e gravar `{"ocr":{"score":[60]}}` em `ia.veiculo_caracteristica.dado_bruto` dessa captura (só no DEV).
2. Na aba "Score e limiar", definir o equipamento com origem `DADO_BRUTO`, campo `ocr.score`, limiar `85`.
3. Aguardar o job (5 min).

**Esperado:** a passagem aparece na fila (situação Pendente, motivo "Score abaixo do limiar", score 60, limiar 85); `vtr_placa_leitura` tem a leitura com score 60; `parametro_historico` tem `ocr.origem_score` e `ocr.limiar` do equipamento. Com score 90, a passagem é avaliada e não entra na fila. ⏳ Pendente.

## T04 — Revisão com justificativa e N placas

Na fila, Revisar a passagem do T03:
1. Salvar correção sem motivo. **Esperado:** aviso "Selecione o motivo".
2. Motivo "Outro (descrever)" com texto curto. **Esperado:** aviso de mínimo de 10 caracteres.
3. Corrigir a placa, adicionar um reboque e salvar com motivo "Inclusão de placa de reboque ou semirreboque". **Esperado:** situação Corrigida, operador = usuário logado, intervenção Fila; `veiculo_tempo_real.placa` alterada; `veiculo_tempo_real_correcao` + `vtr_correcao_justificativa` gravados; `vtr_placa_leitura` com MANUAL ordem 1 e 3; `sis_log_auditoria` com `corrigir-placa`.
4. Em outra passagem, Conferir. **Esperado:** situação Conferida, sem mudança de placa.

⏳ Pendente.

## T05 — Filtros da fila

Filtrar por equipamento, faixa de score (ex.: 0 a 70), tipo de intervenção (Fila/Lote/Consulta) e operador. **Esperado:** só as linhas que atendem; período acima de 92 dias ou score fora de 0-100 recusados. ⏳ Pendente.

## T06 — Consulta de veículos

Abrir o detalhe de uma passagem, clicar ALTERAR e salvar outra placa.

**Esperado:** abre o pedido de motivo e justificativa; ao confirmar, a placa muda, aparece em "Placas lidas" a leitura MANUAL; a passagem passa a constar na fila como intervenção "Consulta". Cancelar o pedido não altera nada. ⏳ Pendente.

## T07 — Correção em lote

Gerar um lote, alterar duas placas e gravar.

**Esperado:** pedido único de motivo/justificativa; duas correções e as demais conferidas; o `idUsuario` gravado é o da sessão (não o enviado pelo navegador); `sis_log_auditoria` com `gravar-lote`. Sem sessão, `POST /MuralhaDigital/CorrecaoPlaca?acao=atualizarPlacas` → 401. ⏳ Pendente.

## T08 — Sem a migração

Antes do script: corrigir pela consulta. **Esperado:** o pedido só oferece "Outro (descrever)", a correção grava normalmente e a justificativa vai para `sis_log_auditoria`; a tela da fila mostra o aviso da migração. ⏳ Pendente.
