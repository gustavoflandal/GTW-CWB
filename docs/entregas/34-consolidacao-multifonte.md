# Entrega 34 — Consolidação de Dados Multifonte

## Origem

[Plano 34](../planos-salvador/34-consolidacao-multifonte.md) — Gap remanescente §5.4.1.1 da auditoria de 2026-10-16.

## Escopo

Implementação de pipeline de consolidação de dados provenientes de múltiplas fontes em view unificada, com servlet de consulta e tela administrativa, atendendo §5.4.1.1 do TR TRANSALVADOR.

## O que foi implementado

### Back-end

| Artefato | Descrição |
|---|---|
| `ConsolidacaoServlet.java` | Servlet em `/MuralhaDigital/Consolidacao` com ações: `resumo` (totais por fonte com datas), `consultar` (filtros por fonte/período/limite), `fontes` (lista de fontes disponíveis) |

### Front-end

| Artefato | Descrição |
|---|---|
| `pages/inteligencia/consolidacao/index.jsp` | Tela com cards de resumo por fonte (coloridos), filtros dinâmicos e tabela de resultados com badges por tipo de fonte |

### Banco de dados

| Migração | Status |
|---|---|
| `20261016_consolidacao_multifonte.sql` | ⏳ Pendente |

- View `muralha.v_dados_consolidados` — UNION ALL de 4 fontes com schema normalizado:
  - **PASSAGEM**: `muralha.veiculo_tempo_real` + `dbo.local` (8,2M registros)
  - **ALERTA**: `muralha.alerta` + `tipo_alerta_ocorrencia`
  - **INCIDENTE_WAZE**: `muralha.incidente` (importados via Waze API)
  - **SUMARIZADO**: `dbo.veiculo_sumarizado` + `dbo.local` (4,15M registros)
- 1 parâmetro em `config_chave_valor`: `CONSOLIDACAO_FONTES_HABILITADO` (true)

### Campos da view

| Campo | Tipo | Descrição |
|---|---|---|
| fonte | VARCHAR | PASSAGEM, ALERTA, INCIDENTE_WAZE, SUMARIZADO |
| id_registro | VARCHAR(36) | Identificador único do registro na fonte |
| id_local | INT | ID do equipamento (quando aplicável) |
| equipamento | VARCHAR | Nome do equipamento |
| dt_evento | DATETIME | Data/hora do evento |
| placa | VARCHAR(15) | Placa do veículo (quando aplicável) |
| tipo_evento | VARCHAR(50) | Tipo do evento (tipo de alerta, VOLUMETRIA etc.) |
| latitude | DECIMAL(10,6) | Coordenada (quando aplicável) |
| longitude | DECIMAL(10,6) | Coordenada (quando aplicável) |
| descricao | VARCHAR(200) | Descrição adicional |

## Critérios de aceite

- [x] Build compila sem erros
- [ ] Migração executada no banco DEV
- [ ] Ação `resumo` retorna totais por fonte
- [ ] Ação `consultar` filtra por fonte, período e limite
- [ ] Tela exibe cards por fonte e tabela com badges coloridos
- [ ] View consolida dados de 4 fontes distintas
