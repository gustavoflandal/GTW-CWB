# SESSAO DASH-2 — Editor de Dataset

## Pre-requisito
- DASH-1 concluida e funcional (Cube respondendo, proxy de consultas operacional, widget de barras renderizando).

## Antes de qualquer codigo
1. Leia CLAUDE.md e docs/dashboards/construtor-dashboards-arquitetura.md.
2. Verifique que o ambiente DASH-1 esta funcional: build OK, Cube /readyz, painel.jsp renderiza.
3. Liste em 10 linhas o que vai criar/modificar e onde. Aguarde meu OK.

## Regras de stack (inviolaveis)
- Mesmas regras de DASH-1: Java 13, SQL Server 2016, JSP + Bootstrap 5.3, ES modules.
- Codigo compila com --release 13, sem --enable-preview.
- SQL compativel com SQL Server 2016: proibido STRING_AGG, TRIM, CONCAT_WS, TRANSLATE.
- Frontend sem React, sem jQuery novo, sem etapa de build.

## Objetivo
Usuario avancado cria um dataset selecionando tabela base do catalogo, adicionando
tabelas relacionadas pela arvore de relacoes, escolhendo campos, definindo medidas
(SUM, COUNT, COUNT_DISTINCT, AVG, MIN, MAX) e campos calculados com gramatica fechada.
Pode pre-visualizar 50 linhas e publicar, gerando novo modelo Cube automaticamente.

## Escopo

### Backend
- **DatasetService**: CRUD completo de datasets com versionamento.
  - `salvar(dataset)` — INSERT/UPDATE com status `rascunho`.
  - `publicar(datasetId)` — muda status para `publicado`, incrementa versao,
    regenera modelo Cube (GeradorModeloCube da DASH-1), atualiza hash de versao.
  - `preVisualizar(datasetId)` — executa consulta com LIMIT 50 via proxy Cube.
  - `obterMeta(datasetId)` — retorna medidas e dimensoes disponiveis, filtradas
    por permissao do usuario.
  - Validacao: campos escolhidos devem existir no catalogo; relacoes devem estar
    declaradas; expressoes calculadas passam pela gramatica.
- **GramaticaCampoCalculado**: parser de expressoes seguro, compativel com SQL Server 2016.
  - Funcoes permitidas: `YEAR()`, `MONTH()`, `DAY()`, `DATEPART()`, `DATEDIFF()`,
    `CAST()`, `CASE WHEN...THEN...ELSE...END`, `COALESCE()`, `NULLIF()`,
    `IIF()`, `LEN()`, `LEFT()`, `RIGHT()`, `SUBSTRING()`, `UPPER()`, `LOWER()`,
    `ROUND()`, `ABS()`, `CEILING()`, `FLOOR()`.
  - Funcoes proibidas (SQL Server 2017+): `STRING_AGG`, `TRIM`, `CONCAT_WS`, `TRANSLATE`.
  - Operadores: `+`, `-`, `*`, `/`, `%`, `||` (traduzido para `+` no T-SQL).
  - Nunca gera SQL concatenado: a expressao validada vira YAML com `sql` no Cube.
- **DatasetServlet**: endpoints REST sob `/api/dash/datasets`.
  - `GET /` — lista datasets do usuario + publicos.
  - `GET /{id}` — detalhes com campos e relacoes.
  - `GET /{id}/meta` — medidas e dimensoes filtradas por permissao.
  - `POST /` — criar dataset (rascunho).
  - `PUT /{id}` — atualizar dataset.
  - `POST /{id}/publicar` — publicar e regenerar modelo.
  - `POST /{id}/pre-visualizar` — 50 primeiras linhas.
  - `DELETE /{id}` — soft delete (so dono ou admin).

### Frontend
- **dataset-editor.jsp** em `/analises/datasets/editor?id=...`
  - **Passo 1**: selecionar tabela base (dropdown do catalogo).
  - **Passo 2**: arvore de relacoes — list-group Bootstrap mostrando tabelas
    relacionadas (vindas de `catalogo_relacao`); clicar para incluir/excluir.
  - **Passo 3**: selecionar campos das tabelas incluidas (checkboxes).
    Para cada campo, opcao de definir como dimensao ou medida (com agregacao).
  - **Passo 4**: campos calculados — formulario com editor de expressao,
    validacao client-side basica e validacao server-side completa.
  - **Passo 5**: pre-visualizacao — tabela AG Grid Community (ou Bootstrap Table)
    com 50 linhas do dataset.
  - **Passo 6**: publicar — confirmar e publicar, atualizando o modelo Cube.
- **dataset-lista.jsp** em `/analises/datasets/`
  - Tabela Bootstrap com datasets do usuario + publicos.
  - Status (rascunho/publicado), dono, data, versao.
  - Acoes: editar, publicar, duplicar, excluir.
- **static/js/dashboards/dataset-editor.js** — logica do editor (wizard multi-step).
- **static/js/dashboards/gramatica-validador.js** — validacao client-side da expressao.

### Migracao SQL
- Novas colunas ou tabelas necessarias (se DASH-1 nao cobriu tudo):
  - `dataset_tabela` (tabelas relacionadas incluidas no dataset).
  - `dataset_filtro_fixo` (condicoes aplicadas sempre ao dataset).
  - Verificar se DASH-1 ja criou; se sim, apenas seed adicional.

## Testes obrigatorios
1. Criar dataset com 2 tabelas relacionadas; publicar; modelo YAML gerado contem joins corretos.
2. Campo calculado com `CASE WHEN x > 10 THEN 'alto' ELSE 'baixo' END` — aceito.
3. Campo calculado com `STRING_AGG(...)` — rejeitado (funcao do SQL Server 2017).
4. Campo calculado com `; DROP TABLE` — rejeitado (SQL injection).
5. Pre-visualizar retorna exatamente 50 linhas (ou menos se a tabela for menor).
6. Dataset do tenant A nao aparece na lista do tenant B.
7. Publicar dataset atualiza hash em `/internal/cube/versao`.
8. Despublicar e republicar incrementa versao.

## Proibicoes
- SQL concatenado com entrada do usuario.
- Funcoes SQL fora da gramatica permitida.
- Dataset sem tabela base ou sem campos.
- Acesso a dataset de outro tenant sem ser publico.
- Implementar itens de DASH-3 em diante.

## Marcadores
[LACUNA: ...] informacao faltando — pare naquele ponto, siga o resto, reporte.
[DEBITO: ... -> sessao-alvo] dificuldade adiada.

## Definition of Done
- Build com JDK 13 OK.
- Cube reflete novo modelo apos publicacao de dataset.
- Testes unitarios + integracao, zero ignorados.
- Criar dataset, publicar, e ver dados na pre-visualizacao (print no relatorio).
- docs/relatorios/SESSAO-DASH-2-relatorio.md com matriz, saidas, lacunas, debitos.

## Arquivos esperados

| Acao | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/dashboards/DatasetService.java` |
| Criar | `src/main/java/muralha/digital/dashboards/DatasetServlet.java` |
| Criar | `src/main/java/muralha/digital/dashboards/GramaticaCampoCalculado.java` |
| Criar | `src/main/webapp/muralha-digital/pages/analises/datasets/editor.jsp` |
| Criar | `src/main/webapp/muralha-digital/pages/analises/datasets/index.jsp` |
| Criar | `src/main/webapp/static/js/dashboards/dataset-editor.js` |
| Criar | `src/main/webapp/static/js/dashboards/gramatica-validador.js` |
| Modificar | `src/main/java/muralha/digital/dashboards/GeradorModeloCube.java` |
| Criar | `src/test/java/muralha/digital/dashboards/GramaticaCampoCalculadoTest.java` |
| Criar | `src/test/java/muralha/digital/dashboards/DatasetServiceTest.java` |
| Criar | `docs/relatorios/SESSAO-DASH-2-relatorio.md` |
