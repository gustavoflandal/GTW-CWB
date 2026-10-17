# Documentação do GTW-CWB

Sistema legado de gestão de trânsito e monitoramento (GTW clássico + Muralha Digital), Consilux Tecnologia. Esta pasta é a **base de conhecimento oficial** para qualquer implementação e para orientar subagents. Gerada em 2026-10-08 a partir do código e do banco `GTW_MURALHA_DEV`; índice atualizado em 2026-10-16.

> **Regras de ouro:** (1) a stack em `.setup-gtw` é imutável; (2) siga o padrão visual (`05`); (3) sem segredos no repositório; (4) banco somente leitura sem autorização; (5) todo código novo verifica sessão e permissão.

## Por onde começar
| Você é… | Leia |
|---|---|
| Humano novo no projeto | `00` → `01` → `02` → `04` |
| Vai criar/alterar uma tela | `05` → `06` §4–5 → `10` §1–2 → exemplos `pages/guarnicao`, `pages/consulta-veiculo` |
| Vai criar/alterar back-end | `02` → `06` §1–3 → `10` §3 → `referencia/modulos-backend-muralha.md` |
| Vai mexer em banco | `banco-de-dados/README.md` → `agentes/AGENTS.md` |
| Subagent | **`agentes/AGENTS.md`** e o guia do seu papel |

## Diagrama de Arquitetura

Visão completa do sistema em diagramas Mermaid (renderizados automaticamente pelo GitHub):

➡️ **[`arquitetura-sistema.md`](diagrama-arquitetura/arquitetura-sistema.md)** — camadas, módulos PoC, jobs Quartz, schema do banco e módulos legados.

## Índice
| Documento | Conteúdo |
|---|---|
| [00-visao-geral.md](requisitos/00-visao-geral.md) | O que é, gerações, domínio, números, regras imutáveis |
| [01-stack-e-ambiente.md](requisitos/01-stack-e-ambiente.md) | Stack (`.setup-gtw`), versões, build/run/debug |
| [02-arquitetura.md](requisitos/02-arquitetura.md) | Camadas, requisição, sessão, dados, WebSocket, jobs, SOAP |
| [03-estrutura-do-projeto.md](requisitos/03-estrutura-do-projeto.md) | Árvore e onde colocar cada coisa |
| [04-modulos-funcionais.md](requisitos/04-modulos-funcionais.md) | Catálogo funcional: módulos originais e planos PoC Salvador (menus, telas, APIs, tabelas) |
| [05-padrao-visual.md](requisitos/05-padrao-visual.md) | **Design system**: cores, fontes, ícones, componentes, layout |
| [06-padroes-de-codigo.md](requisitos/06-padroes-de-codigo.md) | Padrões Java/JSP/JS/SQL e anti-padrões |
| [07-configuracao-e-segredos.md](requisitos/07-configuracao-e-segredos.md) | Arquivos de configuração, variáveis de ambiente |
| [08-integracoes.md](requisitos/08-integracoes.md) | SMS, e-mail, Google, câmeras, SOAP, WebSocket |
| [09-seguranca-e-permissoes.md](requisitos/09-seguranca-e-permissoes.md) | Autenticação, autorização, modelo de menus/direitos |
| [10-guia-de-implementacao.md](requisitos/10-guia-de-implementacao.md) | Receitas passo a passo e Definição de Pronto |
| [11-glossario.md](requisitos/11-glossario.md) | Termos e siglas |
| [12-riscos-e-debitos-tecnicos.md](requisitos/12-riscos-e-debitos-tecnicos.md) | Riscos de segurança, desempenho e manutenção |
| [banco-de-dados/](banco-de-dados/README.md) | Catálogo do banco: 788 tabelas, 826 objetos programáveis, código SQL, domínios |
| [referencia/](referencia/) | Inventários gerados: servlets, módulos, telas, pacotes, menus/permissões |
| [dashboards/](dashboards/) | Construtor de Dashboards (Plano 20): arquitetura, sessões DASH-1 a DASH-6, relatórios |
| [chamados/](chamados/) | Sistema de Chamados (Plano 22): arquitetura, manual do usuário, sessões CH-1 a CH-3, relatórios |
| [planos-salvador/](planos-salvador/) | 34 planos PoC Salvador (01–34) mais `00`, `00b` (tabelas complementares) e `00c` (errata) |
| [entregas/](entregas/README.md) | Relatórios de entrega 01–34 e a matriz canônica de aderência ao TR ([`itens-edital-x-entregas.md`](entregas/itens-edital-x-entregas.md)) |
| [testes/](testes/README.md) | Roteiros de teste manual por plano |
| [agentes/AGENTS.md](agentes/AGENTS.md) | **Guia para subagentes**: mapa de docs por tarefa, padrões de código, índice dos planos Salvador |

## Manutenção
Regenerar inventários: `referencia/scripts/gerar-referencia.ps1`; catálogo do banco: `banco-de-dados/scripts/extrair-catalogo.ps1` (precisa de `GTW_DB_PASSWORD`).

## Limitações conhecidas desta documentação
- Inventários em `referencia/` usam heurísticas (regex); confirme no código antes de decisões críticas.
- Fontes GWT/GXT do GTW clássico não estão no repositório; só a saída compilada foi observada.
- O banco documentado é o **DEV**; produção pode divergir (dados, procedures, configurações).
- Descrições de colunas (`MS_Description`) praticamente não existem no banco; significados vêm do nome, do código e das procedures.
