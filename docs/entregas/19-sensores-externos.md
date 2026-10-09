# Entrega — Plano 19: Integracao com Sensores Externos (Waze/SAMU)

**Data:** 2026-10-08  
**Status:** ✅ Entregue

## Escopo

Importacao periodica de incidentes de trafego de fontes externas (Waze for Cities API, CSV manual) e exibicao no mapa Leaflet como camada sobreposta aos equipamentos.

## Origem

- Plano [`19-sensores-externos.md`](../planos-salvador/19-sensores-externos.md) — Integração com Sensores Externos (Waze/SAMU)
- **Requisitos TR:** §5.3.7.1–3 (integração com sensores externos e Waze)
- **Análise de aderência:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §3

## Arquivos produzidos

| Arquivo | Descricao |
|---|---|
| `docs/banco-de-dados/migracoes/20261008_disponibilidade_incidentes.sql` | Migracao combinada (Planos 18+19): CREATE TABLE `muralha.incidente_externo` com constraint de unicidade |
| `src/main/java/muralha/digital/sensores/IncidenteImportJob.java` | Job Quartz (30 min): importa alertas da API Waze for Cities via HttpURLConnection |
| `src/main/java/muralha/digital/sensores/IncidenteServlet.java` | GET com `acao=geojson` (incidentes 24h como GeoJSON) e `acao=listar` (ultimos 200 registros) |
| `src/main/webapp/muralha-digital/pages/monitoramento/mapa/index.jsp` | Modificado: camada de incidentes com CircleMarkers coloridos por tipo, toggle via switch |

## Arquitetura

- **Tabela `incidente_externo`**: fonte (WAZE/CSV/SAMU), tipo (ACCIDENT/JAM/HAZARD), coordenadas, severidade, constraint UNIQUE para evitar duplicatas
- **Job Quartz a cada 30 minutos**: busca alertas da API Waze, graceful skip se `WAZE_API_KEY` nao estiver configurada
- **API key via variavel de ambiente**: `WAZE_API_KEY` — sem segredo no codigo ou configuracao
- **Configuracao via `config_chave_valor`**: URL da API e bounding box de Salvador
- **GeoJSON de incidentes**: Feature Collection com Points, cores por tipo (vermelho=acidente, laranja=congestionamento, amarelo=perigo, azul=alerta)
- **Toggle no mapa**: switch "Incidentes" para ativar/desativar camada, auto-refresh a cada 5 minutos
- **Popup com detalhes**: tipo, descricao, severidade, data/hora

## Criterios de aceite

- [x] Build compila sem erros
- [ ] Job registrado no Quartz (30 min)
- [ ] Sem WAZE_API_KEY: job loga mensagem e nao gera erro
- [ ] GeoJSON retorna incidentes das ultimas 24 horas
- [ ] Mapa exibe circulos coloridos por tipo de incidente
- [ ] Toggle liga/desliga camada de incidentes
- [ ] Popup mostra detalhes do incidente

## Pendencias

- Migracao `20261008_disponibilidade_incidentes.sql` pendente de execucao manual
- Obter credenciais da API Waze for Cities junto a TRANSALVADOR
- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/19-sensores-externos.md`)
