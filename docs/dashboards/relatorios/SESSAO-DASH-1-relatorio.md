# Relatorio DASH-1 — Fundacao do Construtor de Dashboards

## Origem

Plano: [DASH-1-fundacao.md](../sessoes/DASH-1-fundacao.md)

Data: 2026-10-13

## Stack confirmada

| Item | Valor |
|---|---|
| Java | 13.0.1 (source/target 13) |
| Framework web | Servlet puro (@WebServlet, 137+ servlets) |
| Banco | SQL Server via mssql-jdbc 9.2.1 |
| Frontend | JSP + Bootstrap 5.3 + JS puro |
| JSON | Gson 2.8.8 (ja no pom.xml) |
| HTTP client | java.net.http.HttpClient (JDK 11+) |
| JWT | javax.crypto.Mac HMAC-SHA256 (JDK nativo) |
| Dependencias novas | ZERO — nenhuma alteracao no pom.xml |

## Matriz Requisito → Arquivo

| Requisito | Arquivo | Status |
|---|---|---|
| Schema dash (9 tabelas + seed) | `docs/banco-de-dados/migracoes/20261013_dash_fundacao.sql` | Criado |
| Cube Core (Docker) | `docker-compose.cube.yml` | Criado |
| Configuracao Cube (auth, tenant, modelos) | `cube/cube.js` | Criado |
| JWT sem dependencia externa | `src/.../dashboards/JwtService.java` | Criado |
| Gerador de modelos YAML | `src/.../dashboards/GeradorModeloCube.java` | Criado |
| Proxy de consultas (Java → Cube) | `src/.../dashboards/DashConsultaServlet.java` | Criado |
| Endpoints internos para Cube | `src/.../dashboards/DashInternoServlet.java` | Criado |
| Catalogo CRUD + importar | `src/.../dashboards/DashCatalogoServlet.java` | Criado |
| Paineis/widgets CRUD | `src/.../dashboards/DashPainelServlet.java` | Criado |
| Painel JSP | `src/.../analises/paineis/index.jsp` | Criado |
| API fetch (unico ponto) | `src/.../static/js/dashboards/api.js` | Criado |
| Estado minimo | `src/.../static/js/dashboards/estado.js` | Criado |
| Widget barras (ECharts) | `src/.../static/js/dashboards/widgets/barras.js` | Criado |
| ECharts vendor | `src/.../static/vendor/echarts/echarts.min.js` | Baixado (5.5.0, 1MB) |
| Sessoes DASH-1 a DASH-6 | `docs/dashboards/sessoes/DASH-*.md` | Criados |

## Build

```
java version "13.0.1" 2019-10-15
Compiling 1004 source files
BUILD SUCCESS (31.789s)
```

## Correcoes aplicadas (revisao pos-build)

1. **JSP corrigida**: `index.jsp` tinha HTML duplicado (`<html>`, `<head>`, `<body>`) apos
   include do `cabecalho_bootstrap_simples.jsp` que ja fornece tudo isso + navbar.
   Removido HTML duplicado e include de `navbar.jsp` inexistente.
2. **display:none !important removido**: CSS `!important` no `barraPainel` impedia
   JS de mostrar a barra superior. Trocado para `display:none` simples.
3. **Inline onclick removido em widgets**: Trocado por delegated event listeners
   via `data-wid` attributes (evita XSS e funciona com conteudo dinamico).

## Lacunas

- [LACUNA: ECharts offline] Arquivo baixado de CDN; verificar se intranet permite ou copiar manualmente.
- [LACUNA: Cube Core] Requer Docker no servidor. Verificar disponibilidade.
- [LACUNA: Login cube_leitura] Script SQL comentado; requer execucao por DBA com permissao sysadmin.
- [LACUNA: CUBEJS_API_SECRET] Variavel de ambiente obrigatoria; definir antes de subir o Cube e a aplicacao.
- [LACUNA: Migracao SQL] Script nao executado no banco; requer permissao de escrita.

## Debitos

- [DEBITO: ArchUnit → descartado] Teste de fronteira de modulo descartado — nao altera pom.xml; fronteira mantida por convencao de pacote.
- [DEBITO: Multi-tenant → DASH-4] Tenant fixo = 1; implementar vinculo usuario-tenant quando necessario.
- [DEBITO: CSRF → DASH-4] api.js nao envia token CSRF; implementar quando integrado ao mecanismo existente.
- [DEBITO: Testes integracao → pos-infra] Testes contra Cube+SQLServer reais dependem de Docker disponivel.

## Decisoes

1. **Zero dependencias novas**: JWT via javax.crypto, HTTP via HttpClient JDK, JSON via Gson existente.
2. **Schema `dash`** (nao `dashboards`): mais curto, evita conflito com tabelas existentes.
3. **Endpoints `/api/dash/...`**: diferente do padrao `/MuralhaDigital/...` para separar modulo novo.
4. **ECharts 5.5.0**: versao estavel, vendor local, sem CDN para funcionar em intranet.
