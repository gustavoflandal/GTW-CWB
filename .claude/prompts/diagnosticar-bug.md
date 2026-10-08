# Prompt: diagnosticar bug

Papel: **Agente Explorador** (+ QA). Leia `AGENTS.md`, `agente-explorador.md`, `agente-qa-testes.md`.

## Entrada
Sintoma, tela/URL, usuário/perfil, horário, mensagem de erro, trecho de log (sem segredos).

## Faça
1. Reproduza o caminho: tela → JS (`assets/js`) → URL/`acao` → servlet → classe → SQL/procedure (`codigo-sql/`).
2. Verifique em ordem: permissão/sessão (`sis_menu`), parâmetros e nomes de campo JSON, SQL e dados (SELECT somente leitura com filtro), pool de conexões (`maxWait 2 s`, vazamento), WebSocket/threads, configuração (`.example` vs local), diferenças DEV×produção.
3. Formule hipóteses ranqueadas com evidência `arquivo:linha`; indique o teste mínimo que confirma cada uma.
4. Proponha correção mínima (não aplique, a menos que solicitado) e riscos de regressão. Relatório `AGENTS.md` §5.