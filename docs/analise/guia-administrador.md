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

### Acesso em desenvolvimento

No `docker-compose.yml` de desenvolvimento, o nginx usa `nginx/default.dev.conf` (porta 8088). Esse arquivo **desabilita o SSO** (`auth_request` comentado) e injeta uma identidade fixa de administrador: `X-GTW-Usuario: administrador`, `X-GTW-UserId: 1`, `X-GTW-Grupos: 11` (grupo 11 = Admin). Qualquer pessoa que alcance a porta 8088 entra como administrador, portanto não exponha esse stack fora da máquina de desenvolvimento. A produção usa `nginx/default.conf`, com `auth_request` ao `SsoAnaliseServlet`, montado pelo `docker-compose.prod.yml`.

Para entrar pela tela de login do próprio Superset, use `SUPERSET_ADMIN_USERNAME` e `SUPERSET_ADMIN_PASSWORD` definidos no `.env` local. Nunca registre essas senhas em documentação ou no git.

## 3. Provisionamento

O provisionamento tem duas partes:

- **`provisionar.py`**: conexão, datasets da camada `analise`, papéis, tags, RLS e alertas/relatórios, a partir dos YAMLs de `superset/datasets/`, `superset/governanca/` e `superset/alertas/`.
- **`paineis/operacionais.py` e `paineis/demonstracao.py`**: datasets dos painéis (`gtw_*`, SQL em `superset/paineis/datasets/`), gráficos e dashboards. Ao final, cada script executa a consulta de cada gráfico e falha se algum vier sem dados.

Numa instalação nova, a ordem é esta, porque os relatórios agendados apontam para os painéis pelo slug (o `scripts/deploy.sh` já segue essa ordem):

```bash
docker compose exec superset python /app/provisionar.py
docker compose exec superset python /app/paineis/operacionais.py
docker compose exec superset python /app/paineis/demonstracao.py
docker compose exec superset python /app/provisionar.py --alertas-only

# Só revalidar os painéis publicados, sem regravar
docker compose exec superset python /app/paineis/operacionais.py --validar

# Apenas datasets dos YAMLs
docker compose exec superset python /app/provisionar.py --datasets-only

# Simulação (não grava)
docker compose exec superset python /app/provisionar.py --dry-run

# Limpar action log (>90 dias)
docker compose exec superset python /app/provisionar.py --cleanup-log
```

## 4. Variáveis de ambiente (.env)

| Variável | Descrição |
|---|---|
| `SUPERSET_SECRET_KEY` | Chave secreta (obrigatória, única) |
| `SUPERSET_ADMIN_*` | Credenciais do admin inicial (`USERNAME`, `PASSWORD`, `FIRST_NAME`, `LAST_NAME`, `EMAIL`) |
| `GTW_DB_*` | Conexão com SQL Server (somente leitura) |
| `POSTGRES_*` | Banco de metadados do Superset (`POSTGRES_USER`, `POSTGRES_PASSWORD`) |
| `SUPERSET_DB_URI` | URI SQLAlchemy do banco de metadados (PostgreSQL) |
| `REDIS_URL` | Cache e broker Celery |
| `GTW_GRUPOS_PAPEIS` | JSON: mapeamento grupo GTW → papel Superset |
| `GTW_SSO_HMAC_SECRET` | Segredo HMAC compartilhado com SsoAnaliseServlet |
| `GTW_BASE_URL` | URL base do GTW para redirecionamento |
| `SMTP_*` | Servidor de e-mail para alertas e relatórios (`SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASSWORD`, `SMTP_MAIL_FROM`, `SMTP_STARTTLS`, `SMTP_SSL`) |
| `SUPERSET_LOG_LEVEL` | Nível de log (INFO, DEBUG, WARNING) |
| `STATSD_HOST` | Host do StatsD (vazio = desabilitado) |
| `STATSD_PORT` | Porta do StatsD (padrão 8125) |
| `SUPERSET_WEBSERVER_TIMEOUT` | Timeout do webserver Gunicorn, em segundos (padrão 60) |

## 5. Migrações SQL

Executar no banco `GTW_MURALHA_DEV` somente com autorização, via `sqlcmd` (`-b -I -f 65001` nos scripts que o pedirem) ou SSMS. A situação de cada script está em [`docs/banco-de-dados/migracoes/README.md`](../banco-de-dados/migracoes/README.md); confira-o antes de executar.

| Ordem | Script | Objetos | Situação no DEV |
|---|---|---|---|
| 1 | `20261016_analise_fundacao.sql` | Schema `analise`, carga_log, config, menus | Executado |
| 2 | `20261017_analise_camada_dados.sql` | Tabelas materializadas, views, procedures | Executado |
| 3 | `20261017_analise_correcao_camada.sql` | Correção da camada: `v_local_vigente`, cargas `spu_carga_*` parametrizáveis, `v_equipamentos`, `v_disponibilidade_dia`, `v_preprocessamento`, áreas monitoradas e views geolocalizadas | Executado (2026-10-17) |
| 4 | `20261017_analise_versao_equipamento.sql` | `v_local_versao`; cargas e views passam a usar a versão vigente do equipamento | Executado (2026-10-17) |
| 5 | `20261017_fix_icone_superset.sql` | Ícone do card Análises (395), direitos dos grupos 41 e 42, chave `SUPERSET_BASE_URL` | Executado (2026-10-17) |
| 6 | `20261019_analise_permissao_abrir_superset.sql` | Funcionalidade 396 "Analises - Abrir no Superset" (direito do grupo 11) e direito do grupo 11 à tela de Análises (395) | Executado (2026-10-19). Na mesma data, `20261019_registro_modulos_novos.sql` corrigiu a `acao` do 395 para `/MuralhaDigital/SsoAnalise` (a tela passou a exigir o direito) e registrou o guest token (438) |
| — | `20261017_analise_mapas_geo.sql` | Views geolocalizadas e vínculo usuário-área (versão antiga) | **Obsoleto — não executar** (colunas e tabela inexistentes; substituído pela correção da camada, ordem 3) |

## 6. Papéis e permissões

### Papéis customizados

| Papel | Base | Diferença |
|---|---|---|
| Operador | Gamma | Sem criação de dashboards/gráficos |
| Gestor | Alpha | Sem SQL Lab |
| Auditor | Gamma | + acesso ao log de ações |

### Mapeamento de grupos

Configurado no `.env` (valor do `.env.exemplo`). A chave é o id do grupo em `dbo.sis_grupo`; o valor é o nome do papel no Superset:
```json
GTW_GRUPOS_PAPEIS={"11":"Admin","41":"Alpha","42":"Gamma"}
```

Neste exemplo, 11 = Administradores, 41 = Anel de Segurança e 42 = Anel de Segurança - Supervisores.

Para usar os papéis customizados **Operador**, **Gestor** e **Auditor**, escolha o grupo do GTW que representa cada perfil, consulte o id dele em `dbo.sis_grupo` e acrescente a entrada ao JSON, por exemplo:
```json
GTW_GRUPOS_PAPEIS={"11":"Admin","41":"Alpha","42":"Gamma","<id_grupo>":"Operador"}
```
Use um `<id_grupo>` por entrada (o mesmo papel pode ter vários grupos). Não presuma ids: confira sempre no banco, pois grupos como 50 e 51 pertencem a outras funções (App Alerta Roubado e App Alerta Sequestro Relâmpago). Depois de alterar o `.env`, recrie o container: `docker compose up -d superset`.

### Botão "Abrir no Superset" na tela de Análises

O botão só é gerado para quem tem direito à funcionalidade **"Analises - Abrir no Superset"** (`dbo.sis_menu`, `id_menu = 396`, ação `/MuralhaDigital/SsoAnalise/abrir-superset`). O `SsoAnaliseServlet` verifica o direito com `dbo.fcn_VerificaAcesso` e **nega** se a funcionalidade não estiver cadastrada. Por padrão, só o grupo 11 (Administradores) tem o direito. Para conceder a outro grupo ou usuário:

```sql
INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu) VALUES (<id_grupo>, NULL, 396);   -- por grupo
INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu) VALUES (NULL, <id_usuario>, 396); -- por usuário
```

Esconder o botão não bloqueia o endereço do Superset: quem acessar a porta do Superset diretamente continua sujeito ao login e aos papéis do próprio Superset.

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

## 8. Backup e restauração

### Backup automatizado (recomendado)
```bash
cd analise/
./scripts/backup.sh                    # salva em ./backups/
./scripts/backup.sh /mnt/nfs/backups   # destino customizado
```

O script exporta PostgreSQL (formato custom compactado), YAMLs declarativos e configs. Rotação automática de 30 dias.

### Agenda de backup (crontab)
```
0 2 * * * cd /opt/analise && ./scripts/backup.sh /mnt/backups >> /var/log/superset-backup.log 2>&1
```

### Restaurar
```bash
./scripts/restore.sh ./backups/superset_20261017_020000.tar.gz
```

### Backup manual (alternativa)
```bash
docker compose exec postgres pg_dump -U superset superset > backup_superset_$(date +%Y%m%d).sql
```

## 9. Deploy em produção

### Primeiro deploy
```bash
cd analise/
cp .env.exemplo .env
# Editar .env com valores reais
./scripts/deploy.sh
```

### Atualização
```bash
cd analise/
git pull
./scripts/deploy.sh --rebuild
```

### TLS (HTTPS)
1. Copiar `nginx/ssl.conf.exemplo` para `nginx/ssl.conf`
2. Colocar certificado em `nginx/ssl/cert.pem` e chave em `nginx/ssl/key.pem`
3. Descomentar volumes no `docker-compose.prod.yml` (seção nginx)
4. `docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d nginx`

### Health check
```bash
./scripts/healthcheck.sh         # saída legível
./scripts/healthcheck.sh --json  # para monitoramento
```

### Recarga de tabelas materializadas
```bash
./scripts/refresh-materialized.sh
```

Agenda sugerida (crontab):
```
*/30 * * * * cd /opt/analise && ./scripts/refresh-materialized.sh >> /var/log/superset-refresh.log 2>&1
```

## 10. Troubleshooting

| Problema | Solução |
|---|---|
| Dashboard não carrega | Verificar `docker compose ps` — todos os serviços devem estar "Up" |
| "No data" nos gráficos | Verificar se as views existem no SQL Server e têm dados |
| Alertas não disparam | Verificar celery-worker e celery-beat nos logs |
| Login redireciona em loop | Verificar auth_request do nginx e SsoAnaliseServlet |
| Mapa em branco | Verificar coordenadas na tabela `dbo.local` e conexão internet |
| Numa instalação nova, `superset`, `celery-worker` e `celery-beat` encerram com `sqlalchemy.exc.SQLAlchemyError` | Eles sobem junto com `superset-init`, antes de o banco de metadados ser migrado. Aguardar o `superset-init` terminar (`docker compose logs -f superset-init` até "Inicialização concluída") e rodar `docker compose up -d superset celery-worker celery-beat` |
| nginx responde 502 depois de recriar o container `superset` | O nginx guardou o IP antigo. Executar `docker compose restart nginx` |
| Relatórios agendados avisam "Dashboard ... não encontrado" | Os painéis não foram publicados. Rodar `paineis/operacionais.py`, `paineis/demonstracao.py` e depois `provisionar.py --alertas-only` (ordem do §3) |
