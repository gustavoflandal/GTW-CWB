# Entrega — Plano 08: Painel de Assertividade

**Data:** 2026-10-08  
**Status:** ✅ Entregue (migração SQL pendente de execução manual)

---

## Origem

- Plano [`08-painel-assertividade.md`](../planos-salvador/08-painel-assertividade.md) — Painel de Assertividade da Pré-classificação
- **Requisitos TR:** §5.2.2.3 (aderência mínima 80% da pré-classificação), §5.2.2.6 (painel de controle com assertividade em tempo real)
- **Análise de aderência:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §2

## Escopo

Painel de controle que exibe a taxa de assertividade entre a pré-classificação automática e a validação final dos operadores. Inclui gráfico temporal (Chart.js), taxa percentual por período e alertas quando a aderência cai abaixo de 80%.

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `src/main/java/muralha/digital/assertividade/AssertividadeServlet.java` | GET com `acao=consultar`: calcula taxa de assertividade por período |
| `src/main/webapp/muralha-digital/pages/monitoramento/assertividade/index.jsp` | Painel com gráfico de assertividade e indicadores |

## Critérios de aceite

- [x] Build compila sem erros
- [ ] Painel exibe taxa de assertividade com gráfico temporal
- [ ] Alerta visual quando assertividade < 80%
- [ ] Filtros por período e equipamento

## Pendências

- Migração SQL pendente de execução manual
- Teste manual completo (ver `docs/testes/08-painel-assertividade.md`)
