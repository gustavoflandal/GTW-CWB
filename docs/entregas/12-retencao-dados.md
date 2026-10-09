# Entrega — Plano 12: Política de Retenção de Dados

**Data:** 2026-10-08  
**Status:** ✅ Entregue

## Escopo

Política configurável de retenção de dados com job Quartz para verificação periódica, painel administrativo e log de execuções.

## Origem

- Plano [`12-retencao-dados.md`](../planos-salvador/12-retencao-dados.md) — Política de Retenção de Dados
- **Requisitos TR:** §5.1.8 (retenção de logs durante vigência), §5.5.4.6 (retenção por 5 anos pós-contrato), §5.7.5.3 (retenção do histórico LAP)
- **Análise de aderência:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §1, §5 e §7

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `docs/banco-de-dados/migracoes/20261008_retencao_anonimizacao.sql` | Migração combinada (Planos 12+13): CREATE TABLE `muralha.expurgo_log`, config `retencao_anos` em `config_chave_valor` |
| `src/main/java/muralha/digital/retencao/RetencaoJob.java` | Job Quartz (24h): conta registros elegíveis (PRE_APROVADA/REPROVADA além do período), registra em `expurgo_log` |
| `src/main/java/muralha/digital/retencao/RetencaoServlet.java` | GET: config atual + estimativa + logs. POST: atualiza `retencao_anos` |
| `src/main/webapp/muralha-digital/pages/admin/retencao/index.jsp` | Painel com cards (retenção atual, estimativa), formulário de config, tabela de histórico |
| `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` | Registro do `RetencaoJob` (grupo RETENCAO, intervalo 1440 min) |

## Arquitetura

- **Job read-only (PoC)**: conta registros elegíveis via COUNT + PreparedStatement, não executa DELETE real
- **Tabela complementar**: `muralha.expurgo_log` (nova, sem ALTER TABLE)
- **Config via `config_chave_valor`**: chave `retencao_anos`, padrão 5 anos
- **Critério de elegibilidade**: `vtr.data < DATEADD(YEAR, -N, GETDATE())` com status PRE_APROVADA ou REPROVADA

## Critérios de aceite

- [x] Build compila sem erros
- [ ] GET retorna config, estimativa e logs em JSON
- [ ] POST atualiza retenção (validação 1-20 anos)
- [ ] Painel exibe cards e histórico
- [ ] Job registrado no Agendador

## Pendências

- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/12-retencao-dados.md`)
