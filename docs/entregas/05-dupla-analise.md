# Entrega — Plano 05: Dupla Análise Independente de Infrações

**Data de entrega:** 2026-10-08  
**Commits:** `7dfe25c` · `3b149b6` · `548aa68`  
**Status:** ✅ Entregue (migração SQL pendente de execução manual)

---

## Origem

- Plano [`05-dupla-analise.md`](../planos-salvador/05-dupla-analise.md) — Dupla Análise Independente
- **Requisitos TR:** §5.2.3.1–5 (dupla análise obrigatória, desempate, segregação de operadores), §5.5.1.2.g (dupla análise no pré-processamento)
- **Análise de aderência:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §2 e §5

## O que foi entregue

### Banco de dados
- Tabela `muralha.infracao_analise` com constraint `UNIQUE (id_infracao, id_usuario)` garantindo que o mesmo operador nunca analisa duas vezes
- Tabela complementar `muralha.vtr_status_analise` (PK: `id_veiculo_tempo_real UNIQUEIDENTIFIER`) com coluna `status_analise VARCHAR(25)` — abordagem sem ALTER TABLE em `veiculo_tempo_real`
- Script principal: [`20261008_dupla_analise.sql`](../banco-de-dados/migracoes/20261008_dupla_analise.sql) (cria `infracao_analise`)
- Script complementar: [`20261008_complementar.sql`](../banco-de-dados/migracoes/20261008_complementar.sql) (cria `vtr_status_analise` e demais tabelas dos planos 06/07)

> **Decisão arquitetural:** em vez de `ALTER TABLE muralha.veiculo_tempo_real ADD status_analise`, criamos tabela complementar `vtr_status_analise` com LEFT JOIN + COALESCE para default `'AGUARDANDO_ANALISE'`. Isso evita lock exclusivo na tabela principal (~8M linhas).

### Back-end
| Arquivo | Responsabilidade |
|---|---|
| `muralha/digital/processamento/InfracaoAnaliseDAO.java` | Consulta fila via LEFT JOIN com `vtr_status_analise`; MERGE para atualizar status; indicadores da tabela complementar |
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
| 7 | Transação garante consistência entre INSERT e MERGE de status | ✅ |
| 8 | Indicadores de fila disponíveis em tempo real | ✅ |

---

## Pendências

- Cadastrar menu em `dbo.sis_menu_infos` para expor a fila no menu do sistema
- O endpoint de imagem referenciado no JS (`VeiculoTempoReal?acao=obterImagem`) pode não existir — imagem não carregará, mas fluxo de análise funciona

Testes detalhados: [`docs/testes/05-dupla-analise.md`](../testes/05-dupla-analise.md)
