# SUP-14 — Homologacao e PoC do edital

## Objetivo

Validar a plataforma de Analises com Superset contra os requisitos do TR TRANSALVADOR, criar roteiro de testes e checklist de homologacao.

## Itens do TR atendidos

| Item TR | Descricao | Cobertura Superset |
|---|---|---|
| 5.2.8.4 | Dashboards e indicadores configuraveis | 9 dashboards, 60 graficos, filtros nativos |
| 5.4.2.1-6 | Relatorios gerenciais e analiticos | Exportacao CSV/PDF/XLS, relatorios agendados |
| 5.4.3.5-7 | Alertas analiticos e relatorios agendados | 4 alertas SQL + 3 relatorios (Celery) |
| 5.4.4.1-3 | Analise espacial com mapas | deck.gl scatter/screengrid/polygon, OpenStreetMap |
| 5.4.5.1-2 | Exportacao de dados | CSV, PDF, screenshot de dashboard |
| 5.4.6.1-3 | Integracao de fontes de dados | SQL Server via pymssql, views analiticas |
| 5.5.3.1-4 | Indicadores de pre-processamento | Datasets com metricas certificadas |

## Testes manuais

Adicionados ao roteiro consolidado em `docs/entregas/roteiro-testes-manuais.md` como **Fase 6 — Analises com Superset (Plano 23)**, contendo 28 testes.

## Checklist de homologacao

- [ ] Docker Compose funcional em servidor de homologacao
- [ ] .env configurado com credenciais de homologacao
- [ ] Migracoes SQL executadas (fundacao, camada dados, mapas geo)
- [ ] Provisionamento executado sem erros
- [ ] SSO funcionando (login GTW → Superset sem senha)
- [ ] 9 dashboards visiveis e com dados
- [ ] Filtros nativos operacionais em todos os dashboards
- [ ] Mapas com pontos geolocalizados em Salvador
- [ ] Embedding de dashboard no GTW via iframe
- [ ] Papeis (Operador, Gestor, Auditor) com permissoes corretas
- [ ] RLS filtrando por regiao para Operador
- [ ] Alertas de e-mail disparando (requer SMTP)
- [ ] Backup e restore funcionais
- [ ] Health check retornando todos servicos saudaveis
- [ ] Manual do usuario acessivel
- [ ] Dashboard Builder antigo ainda funcional (coexistencia)
