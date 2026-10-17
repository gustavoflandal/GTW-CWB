# Guia do Administrador — Análises (Superset)

## 1. Arquitetura

```
Navegador → nginx (8088) → auth_request → SsoAnaliseServlet (GTW/Tomcat)
                         → proxy_pass  → Superset (Docker)
                                          ├── Redis (cache + Celery broker)
                                          ├── PostgreSQL (metadados)
                                          └── SQL Server (somente leitura)
```

## 2. Comandos Docker

### Iniciar o stack
```bash
cd analise/
docker compose up -d
```

### Parar
```bash
docker compose down
```

### Produção (com restart, limites, healthchecks)
```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### Logs
```bash
docker compose logs -f superset
docker compose logs -f celery-worker
docker compose logs -f celery-beat
```

## 3. Provisionamento

O provisionamento cria datasets, dashboards, papéis, tags, RLS e alertas a partir dos YAMLs:

```bash
# Tudo de uma vez
docker compose exec superset python /app/provisionar.py

# Apenas datasets
docker compose exec superset python /app/provisionar.py --datasets-only

# Apenas dashboards
docker compose exec superset python /app/provisionar.py --dashboards-only

# Apenas alertas e relatórios
docker compose exec superset python /app/provisionar.py --alertas-only

# Simulação (não grava)
docker compose exec superset python /app/provisionar.py --dry-run

# Limpar action log (>90 dias)
docker compose exec superset python /app/provisionar.py --cleanup-log
```

## 4. Variáveis de ambiente (.env)

| Variável | Descrição |
|---|---|
| `SUPERSET_SECRET_KEY` | Chave secreta (obrigatória, única) |
| `SUPERSET_ADMIN_*` | Credenciais do admin inicial |
| `GTW_DB_*` | Conexão com SQL Server (somente leitura) |
| `POSTGRES_*` | Banco de metadados do Superset |
| `REDIS_URL` | Cache e broker Celery |
| `GTW_GRUPOS_PAPEIS` | JSON: mapeamento grupo GTW → papel Superset |
| `GTW_SSO_HMAC_SECRET` | Segredo HMAC compartilhado com SsoAnaliseServlet |
| `GTW_BASE_URL` | URL base do GTW para redirecionamento |
| `SMTP_*` | Servidor de e-mail para alertas e relatórios |
| `SUPERSET_LOG_LEVEL` | Nível de log (INFO, DEBUG, WARNING) |
| `STATSD_HOST` | Host do StatsD (vazio = desabilitado) |

## 5. Migrações SQL

Executar no banco `GTW_MURALHA_DEV` via SSMS ou sqlcmd:

| Script | Objetos |
|---|---|
| `20261016_analise_fundacao.sql` | Schema, carga_log, config, menus |
| `20261017_analise_camada_dados.sql` | Tabelas materializadas, views, procedures |
| `20261017_analise_mapas_geo.sql` | Views geolocalizadas, vínculo usuário-área |

## 6. Papéis e permissões

### Papéis customizados

| Papel | Base | Diferença |
|---|---|---|
| Operador | Gamma | Sem criação de dashboards/gráficos |
| Gestor | Alpha | Sem SQL Lab |
| Auditor | Gamma | + acesso ao log de ações |

### Mapeamento de grupos

Configurado no `.env`:
```json
GTW_GRUPOS_PAPEIS={"11":"Admin","41":"Alpha","42":"Gamma","50":"Operador","51":"Gestor"}
```

### Row Level Security

Operadores veem apenas dados da sua região. A cláusula RLS filtra por `area_monitorada_id` usando o username do GTW.

## 7. Alertas

### Verificar status dos alertas
No Superset: **Settings > Alert & Report** (apenas Admin).

### Verificar o Celery
```bash
docker compose exec celery-worker celery -A superset.tasks.celery_app:app inspect active
docker compose exec celery-worker celery -A superset.tasks.celery_app:app inspect scheduled
```

### Teste de e-mail
Configure o SMTP no `.env` e verifique com:
```bash
docker compose exec superset python -c "
import smtplib, os
s = smtplib.SMTP(os.environ['SMTP_HOST'], int(os.environ.get('SMTP_PORT', 587)))
s.starttls()
s.login(os.environ['SMTP_USER'], os.environ['SMTP_PASSWORD'])
s.sendmail(os.environ['SMTP_MAIL_FROM'], 'teste@local', 'Subject: Teste\n\nOK')
s.quit()
print('E-mail enviado com sucesso')
"
```

## 8. Backup

### Metadados (PostgreSQL)
```bash
docker compose exec postgres pg_dump -U superset superset > backup_superset_$(date +%Y%m%d).sql
```

### Restaurar
```bash
docker compose exec -T postgres psql -U superset superset < backup_superset_20261017.sql
```

## 9. Troubleshooting

| Problema | Solução |
|---|---|
| Dashboard não carrega | Verificar `docker compose ps` — todos os serviços devem estar "Up" |
| "No data" nos gráficos | Verificar se as views existem no SQL Server e têm dados |
| Alertas não disparam | Verificar celery-worker e celery-beat nos logs |
| Login redireciona em loop | Verificar auth_request do nginx e SsoAnaliseServlet |
| Mapa em branco | Verificar coordenadas na tabela `dbo.local` e conexão internet |
