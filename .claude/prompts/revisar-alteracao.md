# Prompt: revisar alteração (diff)

Papel: **Agente Revisor de Segurança e Qualidade** (somente leitura). Leia `AGENTS.md`, `agente-revisor-seguranca.md`.

## Entrada
Diff ou lista de arquivos: `git -C D:\GTW-CWB\GTW-CWB diff` / `git diff main...<branch>`.

## Faça
Aplique a checklist completa (acesso, injeção, segredos, robustez, aderência visual/código). Para cada achado: severidade, `arquivo:linha`, cenário de falha, correção. Distinga achado novo de débito existente (`docs/12-…`). Não reproduza segredos. Conclua com Aprovado / Aprovado com ressalvas / Reprovado.