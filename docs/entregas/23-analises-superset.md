# Entrega 23 — Plataforma de Analises com Apache Superset

## Origem

[Plano 23 — Analises com Apache Superset](../planos-salvador/23-analises-superset.md)

## Status

**Em andamento** — SUP-1 (Fundacao) implementada.

## Sessoes concluidas

| Sessao | Data | Resumo |
|---|---|---|
| SUP-1 | 2026-10-16 | Fundacao: estrutura Docker, SsoAnaliseServlet, migracao de schema, documentacao |

## Artefatos

### Java (GTW)

| Arquivo | Descricao |
|---|---|
| `muralha.digital.analise.SsoAnaliseServlet` | Servlet SSO para validacao de sessao (`acao=validar`) e redirecionamento (`acao=abrir`) |

### Infraestrutura (analise/)

| Arquivo | Descricao |
|---|---|
| `analise/Dockerfile` | Imagem customizada baseada em `apache/superset:6.0.0` |
| `analise/docker-compose.yml` | Compose de desenvolvimento (Superset + Postgres + Redis + nginx) |
| `analise/docker-compose.prod.yml` | Override de producao (restart, limites, healthchecks) |
| `analise/superset/superset_config.py` | Configuracao do Superset (cache, auth, formatos, idioma) |
| `analise/superset/gtw_security.py` | Security manager customizado para SSO via cabecalhos GTW |
| `analise/superset/provisionar.py` | Script de provisionamento inicial (conexao com banco GTW) |
| `analise/nginx/default.conf` | Configuracao nginx com auth_request para SSO |
| `analise/.env.exemplo` | Template de variaveis de ambiente |

### Migracoes SQL

| Script | Objetos |
|---|---|
| `20261016_analise_fundacao.sql` | Schema `analise`, tabela `analise.carga_log`, chave `SUPERSET_URL`, menus 390-391 |

### Testes

| Classe | Testes |
|---|---|
| `SsoAnaliseServletTest` | 5 testes (instanciacao, acoes, status HTTP, cabecalhos) |

## Pendencias

- [ ] Executar migracao `20261016_analise_fundacao.sql` no banco DEV
- [ ] Testar `docker compose up` com `.env` preenchido
- [ ] Testar login SSO no navegador (usuario GTW -> Superset sem senha)
- [ ] Sessoes SUP-2 a SUP-14
