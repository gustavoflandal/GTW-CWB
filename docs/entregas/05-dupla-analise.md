# Entrega — Plano 05: Dupla Análise Independente de Infrações

**Data de entrega:** 2026-10-08  
**Commits:** `7dfe25c` · `3b149b6` · `548aa68`  
**Status:** ✅ Entregue (migração SQL pendente de execução manual)

---

## O que foi entregue

### Banco de dados
- Tabela `muralha.infracao_analise` com constraint `UNIQUE (id_infracao, id_usuario)` garantindo que o mesmo operador nunca analisa duas vezes
- Coluna `status_analise VARCHAR(25)` em `muralha.veiculo_tempo_real` com valor default `'AGUARDANDO_ANALISE'`
- Script: [`docs/banco-de-dados/migracoes/20261008_dupla_analise.sql`](../banco-de-dados/migracoes/20261008_dupla_analise.sql)

> **Correção em relação ao plano original:** `id_infracao` é `UNIQUEIDENTIFIER` (não `BIGINT`), pois `muralha.veiculo_tempo_real.id` é `uniqueidentifier`. Colunas de consulta também corrigidas: `data` (não `dt_passagem`), `id_local` (não `id_equipamento`), `id_pista` (não `faixa`).

### Back-end
| Arquivo | Responsabilidade |
|---|---|
| `muralha/digital/processamento/InfracaoAnaliseDAO.java` | Consulta fila (excluindo infrações já analisadas pelo usuário), registra análise em transação, determina novo status, retorna indicadores |
| `muralha/digital/processamento/InfracaoAnaliseServlet.java` | `GET ?acao=proximaFila\|indicadores` e `POST` para registrar análise; auditoria integrada |

### Front-end
| Arquivo | Responsabilidade |
|---|---|
| `webapp/.../dupla-analise/fila.jsp` | Exibe indicadores de status por fila e botão "Próxima Infração" |
| `webapp/.../dupla-analise/analisar.jsp` | Tela de análise: imagem com brilho/contraste, classificação, justificativa |
| `webapp/.../js/processamento/dupla-analise.js` | Lógica completa de fila, preenchimento de tela, filtros de imagem, submissão com SweetAlert2 |

---

## Fluxo de status

```
AGUARDANDO_ANALISE → (1ª análise) → PRIMEIRA_ANALISE
PRIMEIRA_ANALISE   → (2ª análise igual) → PRE_APROVADA
PRIMEIRA_ANALISE   → (2ª análise diferente) → DESEMPATE
DESEMPATE          → (3ª análise) → PRE_APROVADA ou REPROVADA
```

---

## Critérios de aceite

| # | Critério | Status |
|---|---|---|
| 1 | Mesmo operador não analisa a mesma infração duas vezes (constraint UNIQUE + exclusão na query) | ✅ |
| 2 | Fila exclui registros já analisados pelo usuário atual | ✅ |
| 3 | Segunda análise igual à primeira → PRE_APROVADA | ✅ |
| 4 | Segunda análise diferente da primeira → DESEMPATE | ✅ |
| 5 | Terceira análise determina resultado final | ✅ |
| 6 | Todo ato de análise gera registro em `sis_log_auditoria` | ✅ |
| 7 | Transação garante consistência entre INSERT e UPDATE de status | ✅ |
| 8 | Indicadores de fila disponíveis em tempo real | ✅ |

---

## Pendências

- Cadastrar menu em `dbo.sis_menu_infos` para expor a fila no menu do sistema
- O endpoint de imagem referenciado no JS (`VeiculoTempoReal?acao=obterImagem`) pode não existir — imagem não carregará, mas fluxo de análise funciona

Testes detalhados: [`docs/testes/05-dupla-analise.md`](../testes/05-dupla-analise.md)
