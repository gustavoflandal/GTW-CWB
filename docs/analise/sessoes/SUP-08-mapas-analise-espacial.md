# SUP-8 — Mapas e análise espacial

## Escopo

Ampliar a camada de dados georreferenciados com views dedicadas para
deck.gl, configurar tiles OpenStreetMap e coordenadas padrão para
Salvador/BA.

## Artefatos

### Datasets geolocalizados

| Dataset | Descrição | Métricas |
|---|---|---|
| v_infracoes_geolocalizado | Infrações com lat/lon do equipamento | total_infracoes_geo, excesso_medio_geo |
| v_passagens_geolocalizado | Passagens com lat/lon do equipamento | total_passagens_geo, velocidade_media_geo |

### Migração SQL

`20261017_analise_mapas_geo.sql`:
- `analise.v_infracoes_geolocalizado` — infrações com coordenadas (JOIN dbo.local)
- `analise.v_passagens_geolocalizado` — passagens com coordenadas (JOIN dbo.local)
- `analise.v_usuario_area_monitorada` — vínculo usuário↔área para RLS

### Configuração superset_config.py

| Configuração | Valor |
|---|---|
| DEFAULT_MAP_CENTER | [-12.9714, -38.5124] (Salvador/BA) |
| DEFAULT_MAP_ZOOM | 12 |
| DECK_GL_DOT_SIZE | 300 |
| MAPBOX_API_KEY | "" (OpenStreetMap) |

## Tarefas

- [x] Criar 2 datasets geolocalizados em YAML
- [x] Criar migração SQL com views georreferenciadas
- [x] Configurar centro e zoom padrão para Salvador
- [x] Build verde
- [ ] Executar migração no banco DEV
- [ ] Testar mapas no navegador com Docker
