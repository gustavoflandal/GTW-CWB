# Entrega — Plano 16: Exportação GIS (GeoJSON/KML)

**Data:** 2026-10-08  
**Status:** ✅ Entregue

## Escopo

Exportação da localização dos equipamentos de monitoramento em GeoJSON e KML, com mapa interativo via Leaflet.js para visualização inline.

## Origem

- Plano [`16-exportacao-gis.md`](../planos-salvador/16-exportacao-gis.md) — Exportação GIS (GeoJSON/KML)
- **Requisitos TR:** §5.4.4.1–4 (análise espacial e exportação GIS), §5.3.7.7 (mapas, gráficos, indicadores visuais)
- **Análise de aderência:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §3 e §4

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `src/main/java/muralha/digital/gis/GisServlet.java` | GET com formato `geojson` (RFC 7946) ou `kml`. Usa tabela `local_vigente` existente (sem nova tabela) |
| `src/main/webapp/muralha-digital/pages/monitoramento/mapa/index.jsp` | Mapa Leaflet com pontos dos equipamentos, popup com dados, botões de download GeoJSON/KML |

## Arquitetura

- **Sem nova tabela**: usa `local_vigente` (já possui `posicao_lat`, `posicao_lon`, `id_local`, `nome`)
- **LEFT JOIN com `veiculo_tempo_real`**: conta total de passagens por local para enriquecer o GeoJSON
- **GeoJSON RFC 7946**: FeatureCollection com Point, coordenadas [lon, lat]
- **KML 2.2**: Placemarks com coordenadas, para Google Earth
- **Leaflet.js de cdnjs**: mapa com CircleMarkers coloridos por volume (azul < 100, amarelo < 1000, vermelho > 1000)
- **fitBounds**: centraliza mapa nos pontos existentes

## Critérios de aceite

- [x] Build compila sem erros
- [ ] Mapa exibe pontos dos equipamentos com coordenadas
- [ ] Popup mostra nome, ID Local e total de passagens
- [ ] Download GeoJSON gera arquivo válido
- [ ] Download KML gera arquivo válido
- [ ] Mapa centraliza nos pontos existentes

## Pendências

- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/16-exportacao-gis.md`)
