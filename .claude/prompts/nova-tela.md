# Prompt: criar nova tela Muralha

Papel: **Agente Frontend Muralha** (+ Backend se faltar endpoint). Leia `docs/agentes/AGENTS.md`, `docs/agentes/agente-frontend-muralha.md`, `docs/05-padrao-visual.md`, `docs/06-padroes-de-codigo.md`, `docs/10-guia-de-implementacao.md` §1–2.

## Preencher
- **Nome da tela / diretório (kebab-case):** `<...>`
- **Objetivo e usuário-alvo (grupo):** `<...>`
- **Tipo:** listagem+CRUD | consulta com filtros | relatório | dashboard | mapa
- **Dados/endpoint:** existente `/MuralhaDigital/<X>?acao=<Y>` | criar novo (descrever campos)
- **Tabelas/procedures envolvidas:** `<...>`
- **Filtros, colunas, ações, exportações:** `<...>`
- **Critérios de aceite:** `<...>`
- **Tela de referência a imitar:** `pages/guarnicao/` | `pages/consulta-veiculo/` | `pages/relatorios/alertas-detalhado.jsp`

## Faça
1. Explore a tela de referência e o módulo (`docs/referencia/telas-muralha-digital.md`).
2. Crie `pages/<dir>/<tela>.jsp`, `modal-*.jsp`, `assets/js/<tela>.js`, `assets/css/<tela>.css` no padrão visual (inclua `cabecalho_bootstrap_simples.jsp`).
3. Se faltar API, acione o fluxo de `novo-endpoint.md`.
4. Gere (não aplique) o script de `sis_menu`/`sis_menu_direitos`/`sis_menu_infos` em `docs/banco-de-dados/migracoes/`.
5. Valide com `docs/agentes/agente-qa-testes.md` (roteiro) e a checklist `05` §9.
6. Atualize `docs/04-modulos-funcionais.md`. Entregue o relatório de `AGENTS.md` §5.

## Não faça
Não nova lib/ícone/fonte; não altere cabeçalhos/utils compartilhados; não grave no banco.