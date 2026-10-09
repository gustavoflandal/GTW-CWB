# Entrega — Plano 14: Gestão de Lotes de Infrações

**Data:** 2026-10-08  
**Status:** ✅ Entregue

## Escopo

Agrupamento de infrações pré-aprovadas em lotes para encaminhamento ao DETRAN/DENATRAN, com ciclo de vida RASCUNHO → ENVIADO → CONFIRMADO/CANCELADO.

## Origem

- Plano [`14-gestao-lotes.md`](../planos-salvador/14-gestao-lotes.md) — Gestão de Lotes de Infrações

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `docs/banco-de-dados/migracoes/20261008_lotes_kpis.sql` | Migração combinada (Planos 14+15): CREATE TABLE `muralha.lote_infracao` e `muralha.lote_infracao_item` |
| `src/main/java/muralha/digital/lote/LoteServlet.java` | GET: listar lotes e itens. POST: criar, adicionar infrações, enviar, cancelar. Auditoria via AuditoriaService |
| `src/main/webapp/muralha-digital/pages/processamento/lotes/index.jsp` | Painel com tabela de lotes, modal de detalhes, botões criar/enviar/cancelar |

## Arquitetura

- **Tabelas complementares**: `muralha.lote_infracao` (ciclo de vida) + `muralha.lote_infracao_item` (vínculo com `veiculo_tempo_real.id` UNIQUEIDENTIFIER)
- **Constraint UNIQUE em `id_infracao`**: impede uma infração em dois lotes
- **Código de lote**: gerado automaticamente (`LOTE-yyyyMMddHHmmss`)
- **SQL parametrizado** em todas as operações
- **Auditoria**: criação e envio registrados via `AuditoriaService`

## Critérios de aceite

- [x] Build compila sem erros
- [ ] Criar lote com descrição opcional
- [ ] Listar lotes com contagem de infrações
- [ ] Visualizar itens de um lote (placa, local, pista, data)
- [ ] Enviar lote (status RASCUNHO → ENVIADO)
- [ ] Cancelar lote rascunho
- [ ] Lote enviado não pode ser alterado

## Pendências

- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/14-gestao-lotes.md`)
