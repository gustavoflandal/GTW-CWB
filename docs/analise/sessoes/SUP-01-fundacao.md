# SUP-1 — Fundacao: plano, ambiente e PoC promovida a base

## Escopo

1. Criar plano `docs/planos-salvador/23-analises-superset.md` com arquitetura, decisoes e mapa de sessoes
2. Criar entrega `docs/entregas/23-analises-superset.md` (esqueleto com `## Origem`)
3. Estrutura `analise/` na raiz do repositorio: Dockerfile, docker-compose (dev e prod), superset_config.py, gtw_security.py, nginx, .env.exemplo, provisionar.py, README
4. `SsoAnaliseServlet.java` em `muralha.digital.analise` para validacao SSO (auth_request do nginx)
5. Migracao `20261016_analise_fundacao.sql`: schema `analise`, tabela `carga_log`, chave `SUPERSET_URL`, menus
6. Testes TestNG do contrato SSO
7. Documentacao: `docs/analise/README.md`, este documento, relatorio

## Tarefas

- [x] Criar branch `feat/sup-01-fundacao`
- [x] Criar plano 23
- [x] Criar entrega 23 (esqueleto)
- [x] Criar `analise/` com toda a infraestrutura Docker
- [x] Criar `SsoAnaliseServlet.java`
- [x] Criar migracao de fundacao
- [x] Criar `SsoAnaliseServletTest.java`
- [x] Criar documentacao da sessao
- [x] Build verde
- [ ] Testar `docker compose up` (requer Docker instalado)
- [ ] Testar SSO no navegador

## Criterios de aceite

| Criterio | Status |
|---|---|
| `docker compose up` sobe limpo | Pendente — requer Docker |
| Usuario GTW entra no Superset sem senha nova | Pendente — requer Docker + Tomcat |
| Testes TestNG verdes | Implementado |
| Build do GTW verde | Implementado |
