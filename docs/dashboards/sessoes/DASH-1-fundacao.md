# SESSAO DASH-1 — Fundacao do construtor de dashboards (ponta a ponta)

## Antes de qualquer codigo
1. Leia CLAUDE.md (se existir) e docs/dashboards/construtor-dashboards-arquitetura.md.
2. Confirme a stack lendo pom.xml/build.gradle, web.xml e a estrutura de webapp:
   Java 13.0.1, JSP + Bootstrap 5.3, SQL Server 2016. Identifique o framework web
   (Servlet puro, Spring MVC, outro) e o mecanismo de migration existente.
   Se encontrar algo diferente, PARE e me reporte.
3. Liste em 10 linhas o que vai criar e onde. Aguarde meu OK.

## Regras de stack (inviolaveis)
- Somente componentes open source ou 100% gratuitos. Nenhuma edicao paga, trial
  ou licenca comercial (AG Grid Enterprise, jOOQ comercial, Cube Cloud: proibidos).
- Codigo compila com --release 13, sem --enable-preview. Proibido: record, text
  blocks, switch expressions, pattern matching de instanceof, sealed classes.
- Nenhuma dependencia que exija Java 17+ (Spring Boot 3, Spring 6, Hibernate 6...).
- SQL compativel com SQL Server 2016: proibido STRING_AGG, TRIM, CONCAT_WS, TRANSLATE.
- Frontend sem React, sem jQuery novo, sem etapa de build: JSP + Bootstrap 5.3 +
  ES modules. Bibliotecas em webapp/static/vendor/ com versao fixada, sem CDN.

## Objetivo
Um widget de barras, salvo no banco, renderiza dado real do SQL Server passando por:
painel.jsp (JS) -> Java (/api/dash/consultas) -> Cube (/cubejs-api/v1/load) -> SQL Server.
Nenhum outro tipo de widget nesta sessao.

## Escopo
### Infraestrutura
- Servico `cube` (imagem oficial cubejs/cube, versao estavel fixada, nunca `latest`),
  CUBEJS_DB_TYPE=mssql, so rede interna, sem porta publicada para fora do servidor.
- Login SQL Server `cube_leitura` com SELECT apenas nas tabelas do catalogo,
  sem acesso ao schema dash.
- CUBEJS_API_SECRET compartilhado com a aplicacao Java; ausente = falha na
  inicializacao dos dois (fail-fast, sem valor padrao).
- CUBEJS_CONCURRENCY=4.
- cube.js com checkAuth, contextToAppId (por tenantId), queryRewrite (filtro de
  tenant), repositoryFactory + schemaVersion (GET /internal/cube/modelos).

### Backend — pacote `dashboards`
- Scripts de criacao do schema dash: catalogo_tabela, catalogo_coluna,
  catalogo_relacao, dataset, dataset_campo, dashboard, widget. Colunas JSON em
  NVARCHAR(MAX) com CHECK (ISJSON(...) = 1). Auditoria de criacao/alteracao.
- CatalogoService: CRUD + importar (INFORMATION_SCHEMA + sys.foreign_keys,
  devolve sugestao; nao grava sem confirmacao).
- GeradorModeloCube (StringBuilder): catalogo + datasets publicados -> YAML.
  Saida deterministica (mesma entrada, mesmo YAML, mesmo hash). Sem SnakeYAML — usa concatenacao segura.
- /internal/cube/modelos e /internal/cube/versao: so rede interna, segredo de servico.
- POST /api/dash/consultas: valida membros contra o dataset e as permissoes,
  assina JWT de 60 s (javax.crypto HMAC nativo) com {tenantId, usuarioId, perfis}, chama o
  Cube com java.net.http.HttpClient, trata "Continue wait", limite de linhas e
  timeout, audita. Usa a sessao e o CSRF ja existentes na aplicacao.
- Fronteira por pacote; sem fachada nem ArchUnit (zero deps novas).
- Permissoes DASHBOARD_VER, DASHBOARD_EDITAR, DATASET_EDITAR, CATALOGO_ADMIN em
  toda rota nova.

### Frontend
- painel.jsp em /analises/paineis?id=..., layout Bootstrap 5.3.
- static/js/dashboards/api.js (fetch + CSRF) e widgets/barras.js (ECharts),
  interface render(elemento, especificacao, dados); estados carregando, vazio, erro.
- Botao "ver como tabela" no widget.

### Dados de exemplo
- Seed com 2 tenants, 1 tabela de catalogo com 1 relacao, 1 dataset publicado,
  1 painel com 1 widget de barras por tenant.

## Testes obrigatorios (integracao contra SQL Server e Cube reais, sem mock)
1. Usuario do tenant A recebe so linhas do tenant A (afirme NAO vazio antes de comparar).
2. Usuario do tenant A pedindo membro de dataset do tenant B: 403.
3. Chamada ao Cube sem JWT ou com JWT expirado: rejeitada.
4. Publicar dataset muda o hash de /internal/cube/versao; republicar sem mudanca mantem.
5. GeradorModeloCube: snapshot do YAML do seed (regressao).
6. Consulta acima do limite e truncada e sinalizada.
7. Fronteira de pacote: nenhum servlet fora de `dashboards` importa classes internas.
8. widgets/barras.js: renderiza dados, estado vazio e estado de erro.

## Proibicoes
- SQL concatenado com entrada do usuario, em qualquer camada.
- Cube exposto fora do servidor ou conectado com login de escrita/sysadmin.
- Valor padrao para segredo ausente.
- Teste ignorado (@Disabled/@Ignore); comparar dois resultados possivelmente vazios.
- Remover, mover ou renomear arquivo fora do escopo sem me perguntar.
- Implementar itens das sessoes DASH-2 em diante.

## Marcadores
[LACUNA: ...] informacao faltando — pare naquele ponto, siga o resto, reporte.
[DEBITO: ... -> sessao-alvo] dificuldade adiada (nao pode ser item do DoD).

## Definition of Done (cole a saida real de cada comando no relatorio)
- Build da aplicacao com JDK 13 (saida mostrando a versao do javac).
- Cube saudavel e respondendo a /readyz.
- Testes unitarios + integracao, duas execucoes seguidas, zero ignorados.
- Abrir painel.jsp do seed e ver o grafico com dado real (print no relatorio).
- docs/relatorios/SESSAO-DASH-1-relatorio.md: matriz requisito -> arquivo -> teste,
  saida dos comandos, lacunas, debitos, decisoes.
- commit + push incluindo docs/PROMPT-SESSAO-DASH-1.md.

## Arquivos criados (resultado real)

| Acao | Caminho |
|---|---|
| Criar | `docker-compose.cube.yml` |
| Criar | `cube/cube.js` |
| Criar | `docs/banco-de-dados/migracoes/20261013_dash_fundacao.sql` |
| Criar | `src/main/java/muralha/digital/dashboards/DashCatalogoServlet.java` |
| Criar | `src/main/java/muralha/digital/dashboards/DashConsultaServlet.java` |
| Criar | `src/main/java/muralha/digital/dashboards/DashInternoServlet.java` |
| Criar | `src/main/java/muralha/digital/dashboards/DashPainelServlet.java` |
| Criar | `src/main/java/muralha/digital/dashboards/GeradorModeloCube.java` |
| Criar | `src/main/java/muralha/digital/dashboards/JwtService.java` |
| Criar | `src/main/webapp/muralha-digital/pages/analises/paineis/index.jsp` |
| Criar | `src/main/webapp/static/js/dashboards/api.js` |
| Criar | `src/main/webapp/static/js/dashboards/estado.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/barras.js` |
| Criar | `src/main/webapp/static/vendor/echarts/echarts.min.js` |
| Criar | `docs/dashboards/relatorios/SESSAO-DASH-1-relatorio.md` |

## Dependencias novas

Nenhuma dependencia nova foi adicionada ao pom.xml. JWT via `javax.crypto` (HMAC nativo do JDK),
JSON via Gson 2.8.8 (ja presente), modelo Cube gerado via StringBuilder (sem SnakeYAML).
