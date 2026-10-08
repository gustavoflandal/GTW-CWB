# AGENTS.md — Regras Gerais para Agentes (subagents) no GTW-CWB

> Todo agente que trabalhar neste repositório DEVE ler este arquivo e o guia do seu papel (`agentes/agente-*.md`) antes de agir. Sistema **legado, de produção**: prefira mudanças pequenas, localizadas e reversíveis.

## 1. Leitura obrigatória (nesta ordem, só o necessário para a tarefa)
1. `docs/00-visao-geral.md` — o que é o sistema (2 min).
2. O guia do seu papel em `docs/agentes/`.
3. Documentos temáticos conforme a tarefa:

| Tarefa envolve… | Leia |
|---|---|
| Qualquer Java/JSP/JS | `06-padroes-de-codigo.md`, `02-arquitetura.md` |
| Qualquer tela | `05-padrao-visual.md` + um exemplo canônico (`pages/guarnicao/`, `pages/consulta-veiculo/`) |
| Banco | `banco-de-dados/README.md`, `tabelas/<schema>/<grupo>.md`, `objetos-programaveis.md` |
| Módulo existente | `04-modulos-funcionais.md`, `referencia/modulos-backend-muralha.md`, `referencia/telas-muralha-digital.md` |
| Acesso/menus | `09-seguranca-e-permissoes.md`, `referencia/menus-e-permissoes.md` |
| Config/integrações | `07-configuracao-e-segredos.md`, `08-integracoes.md` |
| Build/ambiente | `01-stack-e-ambiente.md`, `D:\GTW-CWB\.setup-gtw\STACK.md` |

## 2. Limites inegociáveis
1. **Não alterar `D:\GTW-CWB\.setup-gtw\`** (JDK 13.0.1, Maven, Tomcat 9, scripts, settings) nem o `pom.xml` (versões, plugins, dependências) sem aprovação humana explícita.
2. **Não introduzir** frameworks, bibliotecas, CDNs, fontes ou ícones novos; não migrar versões; não adicionar build de front-end.
3. **Seguir o padrão visual** (`05`) — paleta, fontes, ícones, componentes.
4. **Banco: somente leitura** (SELECT / `sys.*`). Qualquer DDL/DML/EXEC com efeito colateral exige pedido explícito do humano; gere o script em `docs/banco-de-dados/migracoes/` e pare.
5. **Segredos**: nunca ler para repetir, escrever, logar ou commitar credenciais (`confGTW.xml`, `muralha-digital-config.xml`, variáveis de ambiente). Se encontrar um segredo no código, reporte o arquivo/linha **sem reproduzir o valor**.
6. **Não executar** `git push`, `git reset --hard`, `rm -rf`, deploy ou publicar artefatos sem ordem direta. Commits só se solicitado.
7. **Dados pessoais**: não copiar placas, CPFs, imagens ou coordenadas reais para respostas, testes ou documentação.
8. Código novo **sempre** com verificação de sessão/permissão (`06` §1.1), SQL parametrizado e recursos fechados.
9. UTF-8, português do Brasil em mensagens, comentários e commits.
10. Não "melhorar" código fora do escopo (refatorações oportunistas, reformatação em massa). Se achar um problema, **registre** em `docs/12-riscos-e-debitos-tecnicos.md` (ou no relatório) em vez de corrigir.

## 3. Como trabalhar
1. **Entenda antes de mudar**: localize o módulo (`referencia/`), leia a classe/JSP vizinha e copie o *padrão local*; use `Grep`/`Glob` — o projeto tem ~960 `.java` e ~2.000 arquivos web.
2. **Planeje curto**: liste arquivos que vai tocar e riscos (WebSocket, procedures compartilhadas, JSON consumido por JS).
3. **Altere o mínimo**; preserve nomes de campos JSON, parâmetros de `acao`, ids de elementos HTML e classes CSS existentes.
4. **Valide**: compile com `..\.setup-gtw\build.ps1` quando tocar Java (exige o ambiente; informe se não foi possível), revise o diff, rode o roteiro manual descrito no guia do papel.
5. **Documente**: atualize `04-modulos-funcionais.md`/`referencia/` se criou tela/endpoint/tabela; registre dívidas.
6. **Reporte** no formato da §5.

## 4. Mapa de decisão rápida
| Se… | Então… |
|---|---|
| A tela é nova | `agentes/prompts/nova-tela.md` |
| É um endpoint/ação nova | `agentes/prompts/novo-endpoint.md` |
| Precisa de tabela/coluna/procedure | `agente-banco-de-dados.md` (gerar script, não aplicar) |
| Precisa de permissão/menu | `10-guia-de-implementacao.md` §4 (gerar script de carga) |
| Encontrou segredo/vulnerabilidade | Não corrija silenciosamente: reporte (`agente-revisor-seguranca.md`) |
| Dúvida de regra de negócio | Procure na procedure (`codigo-sql/`) e no código; se ainda ambígua, **pergunte ao humano** |
| Algo exigiria mexer na stack | **Pare** e reporte |

## 5. Formato de relatório final (todo agente)
```
## Resumo (2-3 linhas)
## Arquivos alterados/criados (caminho — motivo)
## Como validar (passos e resultado; o que NÃO foi possível validar)
## Riscos / impactos (WebSocket, procedures, JSON, permissões, desempenho)
## Pendências e dívidas encontradas (sem valores de segredos)
## Documentação atualizada (sim/não e quais)
```

## 6. Papéis disponíveis
| Papel | Arquivo | Pode escrever em |
|---|---|---|
| Explorador (somente leitura) | `agente-explorador.md` | nada (relatório) |
| Backend Java | `agente-backend-java.md` | `src/main/java/**` |
| Frontend Muralha | `agente-frontend-muralha.md` | `src/main/webapp/muralha-digital/**`, JSPs novas |
| Banco de Dados | `agente-banco-de-dados.md` | `docs/banco-de-dados/migracoes/**` (scripts) |
| Revisor de Segurança | `agente-revisor-seguranca.md` | nada (relatório) |
| QA / Testes | `agente-qa-testes.md` | `src/test/**`, roteiros em `docs/qa/` |
| Documentação | `agente-documentacao.md` | `docs/**` |

Prompts prontos em `agentes/prompts/`: `nova-tela.md`, `novo-endpoint.md`, `revisar-alteracao.md`, `regenerar-referencia.md`, `diagnosticar-bug.md`.

## 7. Orquestração (quando o humano pedir uma feature completa)
Ordem sugerida: **Explorador** (mapeia impacto) → **Banco** (script, se houver) → **Backend** → **Frontend** → **Revisor de Segurança** → **QA** → **Documentação**. Passe a cada subagent: objetivo, arquivos/módulos relevantes, links dos docs a ler, critérios de aceite e os limites da §2. Subagents não compartilham memória: inclua tudo no prompt.
