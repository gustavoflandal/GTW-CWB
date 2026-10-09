# Testes — Plano 16: Exportação GIS (GeoJSON/KML)

Data: 2026-10-08

## Pré-requisitos

- Tabela `local_vigente` com registros contendo `posicao_lat` e `posicao_lon` preenchidos
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado

---

## T01 — Mapa carrega no browser

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/monitoramento/mapa/index.jsp`

**Resultado esperado:** Mapa Leaflet com tiles OpenStreetMap. Pontos circulares nos locais com coordenadas. Botões GeoJSON e KML visíveis.

**Status:** ⏳ Aguarda teste manual

---

## T02 — Popup de equipamento

**Procedimento:**
1. Clicar em um ponto no mapa

**Resultado esperado:** Popup com nome do local, ID Local e total de passagens.

**Status:** ⏳ Aguarda teste manual

---

## T03 — Cores por volume

**Procedimento:**
1. Observar cores dos pontos no mapa

**Resultado esperado:** Azul (< 100 passagens), amarelo (100-1000), vermelho (> 1000).

**Status:** ⏳ Aguarda teste manual

---

## T04 — Download GeoJSON

**Procedimento:**
1. Clicar botão "GeoJSON"

**Resultado esperado:** Arquivo `.geojson` baixado. Abrir em editor de texto mostra FeatureCollection válida com coordenadas [lon, lat].

**Status:** ⏳ Aguarda teste manual

---

## T05 — Download KML

**Procedimento:**
1. Clicar botão "KML"

**Resultado esperado:** Arquivo `.kml` baixado. Abrir no Google Earth (ou geojson.io) mostra Placemarks nos locais corretos.

**Status:** ⏳ Aguarda teste manual

---

## T06 — Sem coordenadas cadastradas

**Procedimento:**
1. Acessar mapa em ambiente sem locais com coordenadas

**Resultado esperado:** SweetAlert informando "Nenhum equipamento com coordenadas cadastradas".

**Status:** ⏳ Aguarda teste manual

---

## T07 — Acesso restrito (sem sessão)

**Procedimento:**
1. Abrir aba anônima
2. Acessar `http://localhost:8080/MuralhaDigital/Gis?formato=geojson`

**Resultado esperado:** Redirecionamento para login ou resposta de acesso negado.

**Status:** ⏳ Aguarda teste manual
