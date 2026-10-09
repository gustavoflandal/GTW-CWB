# Entrega — Plano 13: Anonimização de Placas (LGPD)

**Data:** 2026-10-08  
**Status:** ✅ Entregue

## Escopo

View SQL para anonimização de placas veiculares e tela de consulta com toggle de anonimização, em conformidade com a LGPD.

## Requisito do TR atendido

- §5.2.11 — Anonimização de dados pessoais (LGPD): mascaramento de placas veiculares em consultas, view dedicada com lógica de ofuscação, controle de exibição por configuração.

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `docs/banco-de-dados/migracoes/20261008_retencao_anonimizacao.sql` | Migração combinada (Planos 12+13): CREATE VIEW `muralha.vw_passagem_anonimizada`, config `anonimizar_placa_padrao` |
| `src/main/java/muralha/digital/anonimizacao/ConsultaAnonimizadaServlet.java` | GET com filtros (período, local, toggle anonimização). Usa view ou tabela real conforme parâmetro |
| `src/main/webapp/muralha-digital/pages/consulta/passagens-anonimizadas/index.jsp` | Tela de consulta com filtros, toggle anonimização, badge de status, tabela de resultados |

## Arquitetura

- **View `vw_passagem_anonimizada`**: mascara placa com `LEFT(placa, LEN-3) + '***'`, LEFT JOIN em `vtr_status_analise`
- **Toggle de anonimização**: parâmetro `anonimizar` (0/1); sem parâmetro usa config `anonimizar_placa_padrao`
- **Sem ALTER TABLE**: view sobre tabelas existentes, config via `config_chave_valor`
- **SQL parametrizado**: filtros via PreparedStatement, TOP 5000

## Critérios de aceite

- [x] Build compila sem erros
- [ ] Consulta com anonimização ativa mostra placas mascaradas (ex: ABC\*\*\*\*)
- [ ] Consulta sem anonimização mostra placas completas
- [ ] Badge indica modo ativo (Anonimizado / Dados completos)
- [ ] Filtros por período e local funcionam

## Pendências

- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/13-anonimizacao-placas.md`)
