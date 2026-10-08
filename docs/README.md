# Documentação do GTW-CWB

Sistema legado de gestão de trânsito e monitoramento (GTW clássico + Muralha Digital), Consilux Tecnologia. Esta pasta é a **base de conhecimento oficial** para qualquer implementação e para orientar subagents. Gerada em 2026-10-08 a partir do código e do banco `GTW_MURALHA_DEV`.

> **Regras de ouro:** (1) a stack em `.setup-gtw` é imutável; (2) siga o padrão visual (`05`); (3) sem segredos no repositório; (4) banco somente leitura sem autorização; (5) todo código novo verifica sessão e permissão.

## Por onde começar
| Você é… | Leia |
|---|---|
| Humano novo no projeto | `00` → `01` → `02` → `04` |
| Vai criar/alterar uma tela | `05` → `06` §4–5 → `10` §1–2 → exemplos `pages/guarnicao`, `pages/consulta-veiculo` |
| Vai criar/alterar back-end | `02` → `06` §1–3 → `10` §3 → `referencia/modulos-backend-muralha.md` |
| Vai mexer em banco | `banco-de-dados/README.md` → `agentes/agente-banco-de-dados.md` |
| Subagent | **`agentes/AGENTS.md`** e o guia do seu papel |

## Índice
| Documento | Conteúdo |
|---|---|
| [00-visao-geral.md](00-visao-geral.md) | O que é, gerações, domínio, números, regras imutáveis |
| [01-stack-e-ambiente.md](01-stack-e-ambiente.md) | Stack (`.setup-gtw`), versões, build/run/debug |
| [02-arquitetura.md](02-arquitetura.md) | Camadas, requisição, sessão, dados, WebSocket, jobs, SOAP |
| [03-estrutura-do-projeto.md](03-estrutura-do-projeto.md) | Árvore e onde colocar cada coisa |
| [04-modulos-funcionais.md](04-modulos-funcionais.md) | Catálogo funcional (menus, telas, APIs, tabelas) |
| [05-padrao-visual.md](05-padrao-visual.md) | **Design system**: cores, fontes, ícones, componentes, layout |
| [06-padroes-de-codigo.md](06-padroes-de-codigo.md) | Padrões Java/JSP/JS/SQL e anti-padrões |
| [07-configuracao-e-segredos.md](07-configuracao-e-segredos.md) | Arquivos de configuração, variáveis de ambiente |
| [08-integracoes.md](08-integracoes.md) | SMS, e-mail, Google, câmeras, SOAP, WebSocket |
| [09-seguranca-e-permissoes.md](09-seguranca-e-permissoes.md) | Autenticação, autorização, modelo de menus/direitos |
| [10-guia-de-implementacao.md](10-guia-de-implementacao.md) | Receitas passo a passo e Definição de Pronto |
| [11-glossario.md](11-glossario.md) | Termos e siglas |
| [12-riscos-e-debitos-tecnicos.md](12-riscos-e-debitos-tecnicos.md) | Riscos de segurança, desempenho e manutenção |
| [banco-de-dados/](banco-de-dados/README.md) | Catálogo do banco: 746 tabelas, 821 objetos programáveis, código SQL, domínios |
| [referencia/](referencia/) | Inventários gerados: servlets, módulos, telas, pacotes, menus/permissões |
| [agentes/AGENTS.md](agentes/AGENTS.md) | **Guia para subagentes**: mapa de docs por tarefa, padrões de código, índice dos planos Salvador |

## Manutenção
Regenerar inventários: `referencia/scripts/gerar-referencia.ps1`; catálogo do banco: `banco-de-dados/scripts/extrair-catalogo.ps1` (precisa de `GTW_DB_PASSWORD`). Procedimento: `agentes/prompts/regenerar-referencia.md`.

## Limitações conhecidas desta documentação
- Inventários em `referencia/` usam heurísticas (regex); confirme no código antes de decisões críticas.
- Fontes GWT/GXT do GTW clássico não estão no repositório; só a saída compilada foi observada.
- O banco documentado é o **DEV**; produção pode divergir (dados, procedures, configurações).
- Descrições de colunas (`MS_Description`) praticamente não existem no banco; significados vêm do nome, do código e das procedures.
