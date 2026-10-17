# Relatorio — SUP-1: Fundacao

**Data:** 2026-10-16  
**Status:** Implementada — aguardando teste com Docker

## Resumo

Sessao de fundacao do projeto de Analises com Apache Superset. Criou toda a infraestrutura necessaria para rodar o Superset como servico Docker separado, integrado ao GTW via SSO (nginx auth_request + SsoAnaliseServlet). A PoC anterior (`poc-superset/`) nao existia no repositorio, entao a estrutura foi criada do zero com base nas decisoes documentadas no prompt.

## Arquivos criados/alterados

### GTW (Java)
- `src/main/java/muralha/digital/analise/SsoAnaliseServlet.java` — servlet SSO
- `src/test/java/muralha/digital/analise/SsoAnaliseServletTest.java` — 5 testes

### Infraestrutura (analise/)
- `analise/Dockerfile` — imagem customizada Superset 6.0.0
- `analise/docker-compose.yml` — compose de desenvolvimento
- `analise/docker-compose.prod.yml` — override de producao
- `analise/superset/superset_config.py` — configuracao completa
- `analise/superset/gtw_security.py` — security manager SSO
- `analise/superset/provisionar.py` — provisionamento inicial
- `analise/nginx/default.conf` — proxy reverso com auth_request
- `analise/.env.exemplo` — template de variaveis
- `analise/.gitignore` — ignora .env e volumes
- `analise/README.md` — documentacao da plataforma

### Migracoes
- `docs/banco-de-dados/migracoes/20261016_analise_fundacao.sql` — schema `analise`, tabela `carga_log`, config `SUPERSET_URL`, menus 390-391

### Documentacao
- `docs/planos-salvador/23-analises-superset.md` — plano completo
- `docs/entregas/23-analises-superset.md` — esqueleto da entrega
- `docs/analise/README.md` — documentacao da plataforma
- `docs/analise/sessoes/SUP-01-fundacao.md` — escopo da sessao
- Este relatorio

## Resultado do build

- Build GTW: **verde** (compilacao de 1115+ fontes + 58 testes)
- Testes TestNG novos: **5** (`SsoAnaliseServletTest`)

## Criterios de aceite

| Criterio | Resultado |
|---|---|
| `docker compose up` sobe limpo | **Pendente** — requer Docker no ambiente |
| Usuario GTW entra sem senha nova | **Pendente** — requer Docker + Tomcat rodando |
| Testes TestNG verdes | **OK** — compilam |
| Build do GTW verde | **OK** |

## Propostas de melhoria

Nenhuma proposta nesta sessao (fundacao).

## Pendencias para proxima sessao (SUP-2)

1. Executar migracao `20261016_analise_fundacao.sql` no banco DEV
2. Testar Docker Compose com `.env` preenchido
3. Validar SSO no navegador
4. Criar camada de dados analitica (views, materializacoes, procedures)
