# Relatorio DASH-2 — Dataset Editor (Wizard multi-passo)

## Origem

Plano: [DASH-2-editor-dataset.md](../sessoes/DASH-2-editor-dataset.md)

Data: 2026-10-13

## Resumo

Editor de datasets com wizard de 6 passos: selecionar tabela base, relacoes, campos,
campos calculados (com gramatica de expressao validada), preview e publicacao.
Inclui validador de expressoes tanto no backend (Java) quanto no frontend (JS).

## Matriz Requisito → Arquivo

| Requisito | Arquivo | Status |
|---|---|---|
| Tabelas complementares (dataset_tabela, dataset_filtro_fixo) | `docs/banco-de-dados/migracoes/20261013_dash2_dataset_editor.sql` | Criado |
| Gramatica de expressao (whitelist SQL Server 2016) | `src/.../dashboards/GramaticaCampoCalculado.java` | Criado |
| Servico CRUD de datasets | `src/.../dashboards/DatasetService.java` | Criado |
| Servlet REST /api/dash/datasets | `src/.../dashboards/DatasetServlet.java` | Criado |
| Pagina lista de datasets | `src/.../analises/datasets/index.jsp` | Criado |
| Wizard editor 6 passos | `src/.../analises/datasets/editor.jsp` | Criado |
| Logica JS do editor | `src/.../static/js/dashboards/dataset-editor.js` | Criado |
| Validador JS client-side | `src/.../static/js/dashboards/gramatica-validador.js` | Criado |
| Testes gramatica (14 cenarios) | `src/test/.../dashboards/GramaticaCampoCalculadoTest.java` | Criado |
| Testes DatasetService (3 cenarios) | `src/test/.../dashboards/DatasetServiceTest.java` | Criado |

## Gramatica de Expressoes

### Funcoes permitidas (SQL Server 2016+)
YEAR, MONTH, DAY, DATEPART, DATEDIFF, CAST, COALESCE, NULLIF, IIF,
LEN, LEFT, RIGHT, SUBSTRING, UPPER, LOWER, ROUND, ABS, CEILING, FLOOR

### Funcoes bloqueadas (SQL Server 2017+)
STRING_AGG, TRIM, CONCAT_WS, TRANSLATE

### Seguranca
- Deteccao de SQL injection: `;`, `--`, `/*`, `xp_`, `exec`, DDL keywords
- Bloqueio de subqueries: SELECT, FROM, WHERE
- Validacao de parenteses e CASE/END balanceados
- Whitelist de tokens: so aceita identificadores, numeros, strings, operadores e funcoes permitidas

## Build

```
java version "13.0.1" 2019-10-15
Compiling 1007 source files (main) + 8 test files
BUILD SUCCESS (30.638s)
```

## Correcoes aplicadas

1. **Testes migrados para TestNG**: Projeto usa TestNG 6.8.21 (nao JUnit).
   Imports corrigidos de `org.junit.Test` → `org.testng.annotations.Test`
   e `org.junit.Assert` → `org.testng.Assert`.

## Endpoints REST

| Metodo | Acao | Descricao |
|---|---|---|
| GET | `listar` | Datasets do usuario + publicos |
| GET | `obter` | Dataset completo (tabelas, campos, filtros) |
| GET | `relacoes` | Tabelas relacionadas a uma tabela base |
| GET | `colunas` | Colunas de uma tabela do catalogo |
| GET | `tabelasCatalogo` | Lista tabelas do catalogo |
| POST | `salvar` | Criar/atualizar dataset com tabelas+campos+filtros |
| POST | `publicar` | Validar e publicar dataset |
| POST | `excluir` | Soft-delete (status='arquivado') |
| POST | `validarExpressao` | Validar expressao calculada |

## Wizard (6 passos)

1. **Tabela Base**: Nome, descricao, selecao de tabela, visibilidade
2. **Relacoes**: Toggle de tabelas relacionadas (JOIN)
3. **Campos**: Selecao de colunas com tipo (dimensao/medida/tempo) e agregacao
4. **Calculados**: Expressoes com validacao em tempo real (client + server)
5. **Preview**: Consulta via Cube proxy (50 linhas)
6. **Publicar**: Resumo + botoes Publicar/Salvar Rascunho

## Lacunas

- [LACUNA: Migracao SQL] Script `20261013_dash2_dataset_editor.sql` nao executado no banco.
- [LACUNA: Menu] Paginas de datasets nao tem entrada no menu da navbar (requer INSERT no banco).
- [LACUNA: Cube] Preview depende do Cube Core rodando (Docker).

## Debitos

- [DEBITO: Testes integracao] Testes unitarios cobrem apenas gramatica; CRUD depende de conexao real.
- [DEBITO: Paginacao] Lista de datasets nao tem paginacao (OK para volume inicial).
- [DEBITO: Filtros fixos] UI para filtros fixos (dataset_filtro_fixo) adiada para DASH-3.

## Decisoes

1. **Gramatica whitelist**: Abordagem conservadora — so permite tokens conhecidos.
2. **Validacao dupla**: Client-side para UX imediata + server-side para seguranca.
3. **TestNG**: Mantido framework existente do projeto (6.8.21), sem adicionar JUnit.
4. **Soft-delete**: Datasets arquivados (nao deletados) para rastreabilidade.
