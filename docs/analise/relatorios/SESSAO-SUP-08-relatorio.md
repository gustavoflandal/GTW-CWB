# Relatorio — SUP-8: Mapas e analise espacial

**Data:** 2026-10-17
**Branch:** `feat/sup-04-paineis-operacionais`

## Resumo

Ampliacao da camada georreferenciada com 2 datasets geolocalizados,
migracao SQL para views com coordenadas e configuracao de tiles
OpenStreetMap centrado em Salvador/BA.

## Artefatos

| Arquivo | Descricao |
|---|---|
| `analise/superset/datasets/infracoes_geolocalizado.yaml` | Infracoes com lat/lon |
| `analise/superset/datasets/passagens_geolocalizado.yaml` | Passagens com lat/lon |
| `docs/banco-de-dados/migracoes/20261017_analise_mapas_geo.sql` | 3 views geolocalizadas |
| `analise/superset/superset_config.py` | Centro Salvador, zoom 12 |
