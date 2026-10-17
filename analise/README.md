# Análises — Apache Superset 6.0 (GTW-CWB / Muralha Digital)

Plataforma de dashboards e exploração self-service do GTW-CWB,
baseada no **Apache Superset 6.0** auto-hospedado.

O Superset roda como serviço separado (Docker Compose), **fora** do WAR do GTW.
Nenhuma dependência é adicionada ao `pom.xml`.

## Pré-requisitos

- [Docker](https://docs.docker.com/get-docker/) 24+
- [Docker Compose](https://docs.docker.com/compose/install/) v2+
- Acesso à rede do banco de dados GTW (`10.0.0.200:1433`)
- GTW rodando (para SSO via `auth_request`)

## Início rápido

```bash
# 1. Copiar o arquivo de exemplo e preencher os valores
cp .env.exemplo .env
# Editar .env com as credenciais e segredos reais
# SUPERSET_SECRET_KEY: gerar com  openssl rand -base64 42

# 2. Subir os serviços (desenvolvimento)
docker compose up -d

# 3. Aguardar a inicialização (migração do banco e criação do admin)
docker compose logs -f superset-init

# 4. Provisionar a conexão com o banco do GTW
docker compose exec superset python /app/provisionar.py

# 5. Acessar via navegador
# http://localhost:8088  (nginx → Superset, com SSO pelo GTW)
```

## Arquitetura

```
┌──────────────────────────────────────────────────────────────────┐
│  Host / Rede Docker (analise_net)                                │
│                                                                  │
│  ┌──────────┐     ┌───────────┐     ┌──────────────────────┐    │
│  │  Nginx   │────▶│  Superset │────▶│  PostgreSQL 16       │    │
│  │  :80     │     │  :8088    │     │  (metadados)         │    │
│  │ (publ.)  │     │ (interno) │     └──────────────────────┘    │
│  └────┬─────┘     └─────┬─────┘                                 │
│       │                 │           ┌──────────────────────┐    │
│       │                 ├──────────▶│  Redis 7             │    │
│       │                 │           │  (cache + Celery)    │    │
│       │                 │           └──────────────────────┘    │
│  auth_request           │                                       │
│       │                 │           ┌──────────────────────┐    │
│       ▼                 └──────────▶│  SQL Server          │    │
│  ┌──────────┐                       │  10.0.0.200:1433     │    │
│  │  GTW     │                       │  (somente leitura)   │    │
│  │  :8080   │                       └──────────────────────┘    │
│  │ (Tomcat) │                                                   │
│  └──────────┘                                                   │
└──────────────────────────────────────────────────────────────────┘
```

**Fluxo de autenticação (SSO):**

1. Usuário acessa `http://localhost:8088` (nginx)
2. Nginx faz `auth_request` ao endpoint `/MuralhaDigital/SsoAnalise` do GTW
3. GTW valida a sessão (cookie JSESSIONID) e retorna cabeçalhos `X-GTW-*`
4. Nginx **sobrescreve** quaisquer cabeçalhos `X-GTW-*` enviados pelo cliente
5. `GtwSecurityManager` (Superset) lê os cabeçalhos, cria/atualiza o usuário
6. Papéis são sincronizados conforme `GTW_GRUPOS_PAPEIS` do `.env`
7. Sem sessão GTW válida → redirect ao login do GTW

## Estrutura de arquivos

```
analise/
├── .env.exemplo              # Template de variáveis de ambiente
├── .gitignore                # Ignora .env, volumes, __pycache__
├── Dockerfile                # Imagem customizada (pymssql, config)
├── docker-compose.yml        # Compose de desenvolvimento
├── docker-compose.prod.yml   # Override de produção
├── README.md                 # Este arquivo
├── nginx/
│   └── default.conf          # Proxy reverso com auth_request SSO
└── superset/
    ├── superset_config.py    # Configuração principal do Superset
    ├── gtw_security.py       # Gerenciador de segurança (SSO + papéis)
    └── provisionar.py        # Criação idempotente da conexão GTW
```

## Desenvolvimento vs. Produção

| Aspecto | Desenvolvimento | Produção |
|---|---|---|
| Comando | `docker compose up -d` | `docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d` |
| Reinício | Manual | `restart: always` |
| Memória | Sem limite | Superset 2G, Postgres 512M, Redis 256M, Nginx 128M |
| Healthchecks | Não | Sim, em todos os serviços |
| Logs | Console (padrão Docker) | JSON com rotação (10MB, 3 arquivos) |

## Variáveis de ambiente

Consulte `.env.exemplo` para a lista completa com comentários.
As principais:

| Variável | Descrição |
|---|---|
| `SUPERSET_SECRET_KEY` | Chave secreta (gerar com `openssl rand -base64 42`) |
| `SUPERSET_DB_URI` | URI do PostgreSQL de metadados |
| `GTW_DB_HOST` / `GTW_DB_PASSWORD` | Conexão com o SQL Server do GTW |
| `GTW_GRUPOS_PAPEIS` | JSON mapeando grupo GTW → papel Superset |
| `GTW_SSO_HMAC_SECRET` | Segredo HMAC compartilhado para SSO |

## Documentação relacionada

- Plano do projeto: `docs/planos-salvador/23-analises-superset.md`
- Entrega: `docs/entregas/23-analises-superset.md`
- Sessões: `docs/analise/sessoes/`
- Relatórios: `docs/analise/relatorios/`
- Manual do usuário: `docs/analise/manual-analises.md`
