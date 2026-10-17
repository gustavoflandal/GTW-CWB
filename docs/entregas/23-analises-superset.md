# Entrega 23 — Plataforma de Analises com Apache Superset

## Origem

[Plano 23 — Analises com Apache Superset](../planos-salvador/23-analises-superset.md)

## Status

**Em andamento** — SUP-1 a SUP-10 concluidas.

## Sessoes concluidas

| Sessao | Data | Resumo |
|---|---|---|
| SUP-1 | 2026-10-16 | Fundacao: estrutura Docker, SsoAnaliseServlet, migracao de schema, documentacao |
| SUP-2 | 2026-10-17 | Camada de dados: 2 tabelas materializadas, 8 views, 2 stored procedures |
| SUP-3 | 2026-10-17 | Datasets e semantica: 11 datasets declarativos (YAML), 42 metricas, 24 certificadas, provisioner idempotente |
| SUP-4 | 2026-10-17 | Paineis operacionais: 9 dashboards declarativos (YAML), 60 graficos, 8 tipos de visualizacao, provisioner estendido |
| SUP-5 | 2026-10-17 | Governanca: 3 papeis customizados, 7 tags, RLS por regiao, Jinja context |
| SUP-6 | 2026-10-17 | Auditoria: log estruturado, StatsD opcional, action log com retencao 90 dias |
| SUP-7 | 2026-10-17 | Alertas: 4 alertas + 3 relatorios agendados, celery-worker e celery-beat no Compose |
| SUP-8 | 2026-10-17 | Mapas: 2 datasets geolocalizados, migracao SQL, centro Salvador, tiles OpenStreetMap |
| SUP-9 | 2026-10-17 | Embedding: GuestTokenServlet (JWT), embedding.jsp com iframe, CORS configurado |
| SUP-10 | 2026-10-17 | Manual do usuario (8 secoes) e guia do administrador (Docker, backup, troubleshooting) |

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
| `analise/superset/provisionar.py` | Provisionamento declarativo: conexao, datasets, dashboards, tags, RLS, papeis, alertas |
| `analise/superset/datasets/*.yaml` | 13 definicoes de datasets (11 base + 2 geolocalizados) |
| `analise/superset/dashboards/*.yaml` | 9 dashboards operacionais com graficos, native filters e conditional formatting |
| `analise/superset/governanca/*.yaml` | 3 papeis, 7 tags, 2 regras RLS declarativas |
| `analise/superset/alertas/*.yaml` | 4 alertas SQL + 3 relatorios agendados (screenshot de dashboard) |
| `muralha.digital.analise.GuestTokenServlet` | Servlet que gera JWT guest token para embedding de dashboards |
| `muralha-digital/pages/analise/embedding.jsp` | Tela de embedding com seletor de dashboards e iframe |
| `docs/analise/manual-usuario.md` | Manual do usuario final (paineis, filtros, alertas, FAQ) |
| `docs/analise/guia-administrador.md` | Guia do administrador (Docker, provisioner, backup) |
| `analise/nginx/default.conf` | Configuracao nginx com auth_request para SSO |
| `analise/.env.exemplo` | Template de variaveis de ambiente |

### Migracoes SQL

| Script | Objetos |
|---|---|
| `20261016_analise_fundacao.sql` | Schema `analise`, tabela `analise.carga_log`, chave `SUPERSET_URL`, menus 395/56 |
| `20261017_analise_camada_dados.sql` | `mat_passagens_hora`, `mat_infracoes_dia`, 8 views analiticas, 2 procedures de carga |
| `20261017_analise_mapas_geo.sql` | `v_infracoes_geolocalizado`, `v_passagens_geolocalizado`, `v_usuario_area_monitorada` |

### Testes

| Classe | Testes |
|---|---|
| `SsoAnaliseServletTest` | 5 testes (instanciacao, acoes, status HTTP, cabecalhos) |

## Pendencias

- [x] Executar migracao `20261016_analise_fundacao.sql` no banco DEV
- [x] Testar `docker compose up` com `.env` preenchido
- [x] Executar migracao `20261017_analise_camada_dados.sql` no banco DEV
- [ ] Testar login SSO no navegador (usuario GTW -> Superset sem senha)
- [ ] Testar provisionamento de datasets (`python /app/provisionar.py`)
- [ ] Validar metricas certificadas contra SQL de referencia
- [ ] Testar provisionamento de dashboards (`python /app/provisionar.py --dashboards-only`)
- [ ] Validar layout dos dashboards no navegador
- [ ] Testar papeis e RLS com usuarios distintos
- [ ] Testar alertas e relatorios (`python /app/provisionar.py --alertas-only`)
- [ ] Validar envio de e-mail (requer SMTP configurado)
- [ ] Executar migracao `20261017_analise_mapas_geo.sql` no banco DEV
- [ ] Testar embedding de dashboard no GTW (iframe + guest token)
- [ ] Sessoes SUP-11 a SUP-14
