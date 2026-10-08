# Prompt: criar endpoint/ação

Papel: **Agente Backend Java**. Leia `AGENTS.md`, `agente-backend-java.md`, `docs/06-padroes-de-codigo.md` §1–3, `docs/banco-de-dados/README.md`.

## Preencher
- **Módulo (`muralha.digital.<pkg>`) e servlet:** `<...>` (reutilize o servlet da entidade se existir)
- **Ação (`acao`), método HTTP:** `<listarX | obterXPorId | cadastrarX ...>` GET|POST
- **Parâmetros (nome, tipo, obrigatório):** `<...>`
- **Resposta JSON (campos em snake_case):** `<...>`
- **Dados:** tabelas/procedures existentes a reutilizar `<...>`; precisa de DDL? sim/não (se sim, só script)
- **Permissão/grupos:** `<...>`
- **Critérios de aceite:** `<...>`

## Faça
1. `Grep` pelo padrão no módulo; copie o estilo do servlet vizinho.
2. Implemente verificação de sessão+permissão, validação, JDBC parametrizado, fechamento de recursos, JSON via Gson, erros sem detalhes internos.
3. Não mude contratos existentes; acrescente campos se necessário.
4. Compile (`..\.setup-gtw\build.ps1`) se possível; descreva testes manuais (200/400/401/403/404).
5. Atualize `docs/04-modulos-funcionais.md` e regenere `docs/referencia/*` (`regenerar-referencia.md`). Relatório `AGENTS.md` §5.