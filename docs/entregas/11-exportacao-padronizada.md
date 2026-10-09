# Entrega — Plano 11: Exportação Padronizada

**Data:** 2026-10-08  
**Status:** ✅ Entregue²

## Escopo

Exportação de passagens em CSV, XLS e PDF com filtros por período, local e status. CSV server-side com hash SHA-256 de integridade; XLS e PDF client-side via SheetJS e jsPDF (já existentes no projeto).

## Origem

- Plano [`11-exportacao-padronizada.md`](../planos-salvador/11-exportacao-padronizada.md) — Exportação Padronizada (PDF/CSV/XLS)

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `src/main/java/muralha/digital/exportacao/ExportacaoServlet.java` | Servlet GET com formato `csv` (streaming com BOM, SHA-256) e `json` (para client-side XLS/PDF). Queries parametrizadas, LEFT JOIN em `vtr_status_analise` |
| `src/main/webapp/muralha-digital/pages/relatorios/exportacao-passagens/index.jsp` | Tela com filtros, tabela de preview, botões CSV/XLS/PDF. Usa `export-table.js` existente |

## Arquitetura

- **CSV server-side**: streaming direto no response com BOM UTF-8, cabeçalho institucional, hash SHA-256 do conteúdo no rodapé
- **XLS/PDF client-side**: consulta JSON, tabela renderizada no DOM, funções `GerarRelatorioXLSX()` e `GerarRelatorioPDF()` do `export-table.js` existente
- **SQL parametrizado**: filtros via PreparedStatement (sem concatenação), TOP 10000, LEFT JOIN em `vtr_status_analise` com COALESCE

## Critérios de aceite

- [x] Build compila sem erros
- [ ] Consulta retorna dados na tabela com filtros
- [ ] CSV baixa com BOM, cabeçalho, hash SHA-256
- [ ] XLS baixa via SheetJS
- [ ] PDF baixa via jsPDF autoTable
- [ ] Filtros de status e local funcionam
- [ ] Período obrigatório validado no cliente

## Pendências

- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/11-exportacao-padronizada.md`)
