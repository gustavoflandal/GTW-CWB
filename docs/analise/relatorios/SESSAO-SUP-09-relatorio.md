# Relatorio — SUP-9: Embedding em telas do GTW

**Data:** 2026-10-17
**Branch:** `feat/sup-04-paineis-operacionais`

## Resumo

Embedding de dashboards do Superset no GTW via iframe com guest token JWT.
Criado GuestTokenServlet, tela JSP com seletor e configuracao CORS.

## Artefatos

| Arquivo | Descricao |
|---|---|
| `muralha.digital.analise.GuestTokenServlet` | Gera JWT guest token |
| `muralha-digital/pages/analise/embedding.jsp` | Tela com iframe |
| `muralha-digital/pages/analise/assets/js/embedding.js` | Logica de carregamento |
| `analise/superset/superset_config.py` | CORS, X-Frame-Options, Talisman |
