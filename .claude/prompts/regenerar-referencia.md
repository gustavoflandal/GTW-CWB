# Prompt: regenerar documentação de referência

Papel: **Agente de Documentação**. Leia `AGENTS.md`, `agente-documentacao.md`.

## Passos
1. Inventários de código (somente leitura do código):
   `powershell -ExecutionPolicy Bypass -File docs\referencia\scripts\gerar-referencia.ps1`
   → atualiza `servlets-e-endpoints.md`, `modulos-backend-muralha.md`, `telas-muralha-digital.md`, `inventario-pacotes-java.md`.
2. Catálogo do banco (somente SELECT; peça ao humano para definir `$env:GTW_DB_PASSWORD`):
   `powershell -ExecutionPolicy Bypass -File docs\banco-de-dados\scripts\extrair-catalogo.ps1`
   → atualiza `tabelas/` e `codigo-sql/`.
3. **Manual / consulta específica** (o script não gera ainda): `objetos-programaveis.md` (use `sys.objects`, `sys.parameters`, `sys.sql_expression_dependencies`), `dominios.md` (SELECT TOP 40 de tabelas pequenas `*tipo*`, `*status*`, `*motivo*`, …), `referencia/menus-e-permissoes.md` (`sis_grupo`, `sis_menu`, `sis_menu_infos`, `sis_menu_relatorio_infos`, `sis_menu_graficos_infos`, `sis_menu_direitos`). Reaproveite o formato dos arquivos atuais.
4. Atualize os números do `docs/00-visao-geral.md` §2 e `docs/banco-de-dados/README.md` §2.
5. Confira: nenhum segredo/dado pessoal nos arquivos gerados (`Grep` por `password|senha|token|secret|AKIA|SG\.`), links relativos válidos, `git status` mostra só `docs/`.