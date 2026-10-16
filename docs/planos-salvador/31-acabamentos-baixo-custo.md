# Plano 31 — Acabamentos de baixo custo

**Fase da auditoria:** 9 — Acabamentos de baixo custo
**Itens do edital:** 5.1.10, 5.4.4.4, 5.5.4.3
**Branch:** `fix/auditoria-fase-09-acabamentos`

## Contexto

A auditoria identificou três itens parciais que podem ser resolvidos com ajustes pontuais, sem impacto estrutural:

1. **5.1.10** — Log de auditoria: faltam filtros por perfil de usuário e por equipamento, e exportação PDF.
2. **5.4.4.4** — Exportação GIS: apenas equipamentos são exportados; faltam camadas de alertas e infrações.
3. **5.5.4.3** — Análise de imagens: brilho e contraste existem, mas não há inversão de cores.

## Escopo

### 5.1.10 — Filtros e PDF no log de auditoria

- **Back-end** (`AuditoriaServlet.java`):
  - Novo método `buscarPerfis()` — JOIN `sis_log_auditoria → sis_usuario_grupo → sis_grupo` para listar perfis com registros.
  - Novo método `buscarEquipamentos()` — lista `local_vigente` ativos.
  - Consulta aceita parâmetros `perfil` (id_grupo) e `equipamento` (nome do local, busca por LIKE na descrição).
- **Front-end** (`consulta-log.jsp`, `consulta-log.js`):
  - Dois novos selects: Perfil e Equipamento, populados via `listarFiltros`.
  - Botão PDF usando jsPDF + AutoTable (já disponível no projeto).
  - Substituição de `alert()` por `Swal.fire()`.
  - CSV agora usa `;` como separador (padrão PT-BR).

### 5.4.4.4 — Camadas GIS

- **`GisServlet.java`**: parâmetro `camada` com valores `equipamentos` (padrão), `alertas`, `infracoes`.
  - **alertas**: JOIN `muralha.alerta → alerta_veiculo → veiculo_tempo_real → local_vigente`, últimos 30 dias.
  - **infrações**: JOIN `dbo.infracao → local_vigente`, últimos 30 dias.
  - Suporte a GeoJSON e KML para as 3 camadas.

### 5.5.4.3 — Inversão de cores

- **3 arquivos JS** modificados: `dupla-analise.js`, `tratar-alerta.js`, `modal-detalhe-veiculo.js`.
  - Variável `invertido` + função `toggleInverter()` que aplica/remove `invert(1)` no CSS filter.
  - Reset limpa o estado de inversão.
- **3 JSPs** modificadas: botão "Inverter" com ícone `bi-circle-half` ao lado dos controles existentes.

## Arquivos alterados

| Arquivo | Tipo |
|---------|------|
| `src/main/java/muralha/digital/auditoria/AuditoriaServlet.java` | Modificado |
| `src/main/java/muralha/digital/gis/GisServlet.java` | Modificado |
| `src/main/webapp/muralha-digital/assets/js/processamento/dupla-analise.js` | Modificado |
| `src/main/webapp/muralha-digital/pages/alerta-tratativa/js/tratar-alerta.js` | Modificado |
| `src/main/webapp/muralha-digital/pages/consulta-veiculo/js/modal-detalhe-veiculo.js` | Modificado |
| `src/main/webapp/muralha-digital/pages/auditoria/consulta-log.jsp` | Modificado |
| `src/main/webapp/muralha-digital/assets/js/auditoria/consulta-log.js` | Modificado |
| `src/main/webapp/muralha-digital/pages/processamento/dupla-analise/analisar.jsp` | Modificado |
| `src/main/webapp/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp` | Modificado |
| `src/main/webapp/muralha-digital/pages/consulta-veiculo/modal-detalhe-veiculo.jsp` | Modificado |
| `src/test/java/muralha/digital/gis/GisServletTest.java` | Novo |

## Não incluso

- Camada de "áreas de interesse" no GIS (requer tabela `ponto_interesse` com polígonos, não pontos).
- Dashboard Builder v1 removal (verificação pendente de uso — adiado para fase posterior se necessário).
