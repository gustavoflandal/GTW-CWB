# Plano 23 — Plataforma de Analises com Apache Superset

> **Status:** Executado (2026-10-17): sessões SUP-1 a SUP-14 — ver [Entrega 23 — Análises](../entregas/23-analises-superset.md). Testes no navegador e homologação pendentes.

## Objetivo

Implantar o Apache Superset 6.0 como plataforma de criacao de dashboards e exploracao self-service do GTW-CWB / Muralha Digital, substituindo o Dashboard Builder proprio (Planos 20-21) por uma solucao madura, de codigo aberto e auto-hospedada.

## Justificativa

O Superset entrega pronto o que o builder ainda precisaria de ~13 sessoes para alcancar: catalogo semantico, construtor sem SQL, colecoes/tags, filtros nativos, cross-filter, drill, alertas e relatorios agendados. O motor de consulta do builder (`MontadorSql`, pre-agregacao de `veiculo_tempo_real`) e preservado como views e tabelas materializadas no SQL Server, consumidas pelo Superset.

## Arquitetura

```
  Navegador (Bootstrap 5.3 / GTW)
       |
       v
  nginx (proxy reverso)
       |--- auth_request --> SsoAnaliseServlet (GTW/Tomcat)
       |
       v
  Apache Superset 6.0 (Docker)
       |--- Redis (cache)
       |--- Postgres (metadados)
       |--- SQL Server 10.0.0.200 (somente leitura)
```

- **Superset** roda como servico separado (Docker Compose), fora do WAR do GTW
- **Login unico**: nginx com `auth_request` para `SsoAnaliseServlet`; Superset le `X-GTW-Usuario` via `GtwSecurityManager`
- **Permissoes**: fonte da verdade e `dbo.sis_grupo` / `dbo.sis_usuario_grupo` do GTW; mapeamento grupo -> papeis do Superset via `GTW_GRUPOS_PAPEIS`
- **Banco GTW**: somente leitura, `allow_dml = False`, sem CTAS/CVAS/upload; objetos de apoio no schema `analise`
- **Desempenho**: datasets analiticos sobre tabelas materializadas (`analise.mat_*`), cache Redis 60s, ROW_LIMIT 10.000

## Decisoes tecnicas

| Tema | Decisao |
|---|---|
| Versao | `apache/superset:6.0.0` (fixada, nunca `latest`) |
| Driver SQL Server | `pymssql==2.3.0` |
| Autenticacao | SSO via `auth_request` + cabecalhos `X-GTW-*` |
| LGPD | Dados pessoais (placa, CPF) fora dos datasets; uso de views anonimizadas (Plano 13) |
| Mapas | deck.gl + tiles OpenStreetMap (sem Mapbox) |
| SQL Lab | Apenas Admin |
| Alertas | `ALERT_REPORTS` do Superset (Celery + Redis) |
| Builder antigo | Desligado na SUP-13 apos migracao |

## Mapa de sessoes

| Sessao | Escopo |
|---|---|
| SUP-1 | Fundacao: plano, ambiente e PoC promovida a base |
| SUP-2 | Camada de dados analitica (views e materializacoes) |
| SUP-3 | Datasets, metricas e semantica no Superset |
| SUP-4 | Paineis operacionais padrao |
| SUP-5 | Permissoes, colecoes e governanca |
| SUP-6 | Auditoria e observabilidade |
| SUP-7 | Alertas e relatorios agendados |
| SUP-8 | Mapas e analise espacial |
| SUP-9 | Embedding em telas do GTW |
| SUP-10 | Manual do usuario e capacitacao |
| SUP-11 | Producao: implantacao, backup e operacao |
| SUP-12 | Desempenho e carga |
| SUP-13 | Desligamento do Dashboard Builder proprio |
| SUP-14 | Homologacao e PoC do edital |

## Itens do TR atendidos

- 5.2.8.4 — Dashboards e indicadores configuráveis
- 5.4.2.1-6 — Relatorios gerenciais e analiticos
- 5.4.3.5-7 — Alertas analiticos e relatorios agendados
- 5.4.4.1-3 — Analise espacial com mapas
- 5.4.5.1-2 — Exportacao de dados (CSV, PDF, XLS)
- 5.4.6.1-3 — Integracao de fontes de dados
- 5.5.3.1-4 — Indicadores de pre-processamento

## Dependencias

- Planos 05, 07, 08, 10, 12, 13, 15, 18, 19 (tabelas de origem dos datasets)
- Docker e Docker Compose no servidor de producao
- Certificado TLS para o nginx (producao)
