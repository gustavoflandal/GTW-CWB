# Entrega 31 — Acabamentos de baixo custo

**Status:** 🟡 Build OK — aguardando teste em navegador
**Data:** 2026-10-16
**Branch:** `fix/auditoria-fase-09-acabamentos`

## Origem

[Plano 31 — Acabamentos de baixo custo](../planos-salvador/31-acabamentos-baixo-custo.md)

## Itens do edital atendidos

| Item | Antes | Depois | O que mudou |
|------|-------|--------|-------------|
| 5.1.10 | ⚠️ Parcial | ✅ | Filtros por perfil e equipamento + exportação PDF no log de auditoria |
| 5.4.4.4 | ⚠️ Parcial | ✅ | GIS exporta 3 camadas: equipamentos, alertas e infrações (GeoJSON + KML) |
| 5.5.4.3 | ⚠️ Parcial | ✅ | Botão "Inverter" (`invert(1)`) em dupla-análise, tratar-alerta e consulta-veículo |

## Detalhes técnicos

### 5.1.10 — Log de auditoria aprimorado

- `AuditoriaServlet`: novos filtros `perfil` (JOIN com `sis_grupo`) e `equipamento` (LIKE na descrição).
- `consulta-log.jsp`: selects de Perfil e Equipamento + botão PDF.
- `consulta-log.js`: `preencherSelectObj()` para filtros com id/label, `exportarPDF()` via jsPDF/AutoTable.
- CSV agora usa `;` como separador e `alert()` substituído por `Swal.fire()`.

### 5.4.4.4 — Camadas GIS

- `GisServlet`: parâmetro `camada=equipamentos|alertas|infracoes`.
  - `?camada=alertas&formato=geojson` — alertas dos últimos 30 dias com coordenadas via `local_vigente`.
  - `?camada=infracoes&formato=kml` — infrações dos últimos 30 dias idem.
- Backward-compatible: sem parâmetro `camada`, mantém comportamento original (equipamentos).

### 5.5.4.3 — Inversão de cores

- 3 JS modificados: `dupla-analise.js`, `tratar-alerta.js`, `modal-detalhe-veiculo.js`.
- 3 JSPs modificadas: botão `#btnInverter` com ícone `bi-circle-half`.
- `toggleInverter()` aplica/remove `invert(1)` no CSS filter; reset limpa o estado.

## Migração

Nenhuma migração necessária nesta entrega (sem alterações de banco).

## Testes automatizados

- `GisServletTest`: 6 testes (`escJson` e `escXml` — nulo, aspas/barra, ampersand/tags).

## Verificação

- [ ] Build compila sem erros
- [ ] 6 testes GisServletTest passam
- [ ] Botão "Inverter" funciona nas 3 telas de análise
- [ ] GIS exporta 3 camadas (URLs com `?camada=alertas`, `?camada=infracoes`)
- [ ] Filtros Perfil/Equipamento populam e filtram no log de auditoria
- [ ] Exportação PDF gera arquivo válido
