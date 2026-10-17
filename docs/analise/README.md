# Analises — Apache Superset para GTW-CWB

Plataforma de criacao de dashboards e exploracao self-service baseada no [Apache Superset 6.0](https://superset.apache.org/), integrada ao GTW-CWB / Muralha Digital.

## Plano e entregas

- [Plano 23](../planos-salvador/23-analises-superset.md) — arquitetura, decisoes e mapa de sessoes
- [Entrega 23](../entregas/23-analises-superset.md) — status e artefatos

## Sessoes

| Sessao | Escopo | Status |
|---|---|---|
| [SUP-1](sessoes/SUP-01-fundacao.md) | Fundacao: plano, ambiente, SSO | Implementada |
| [SUP-2](sessoes/SUP-02-camada-dados.md) | Camada de dados analitica | Concluida |
| [SUP-3](sessoes/SUP-03-datasets-semantica.md) | Datasets, metricas e semantica | Implementada |
| [SUP-4](sessoes/SUP-04-paineis-operacionais.md) | Paineis operacionais padrao | Implementada |
| [SUP-5](sessoes/SUP-05-permissoes-governanca.md) | Permissoes, colecoes e governanca | Implementada |
| [SUP-6](sessoes/SUP-06-auditoria-observabilidade.md) | Auditoria e observabilidade | Implementada |
| [SUP-7](sessoes/SUP-07-alertas-relatorios.md) | Alertas e relatorios agendados | Implementada |
| SUP-8 | Mapas e analise espacial | Pendente |
| SUP-9 | Embedding em telas do GTW | Pendente |
| SUP-10 | Manual do usuario e capacitacao | Pendente |
| SUP-11 | Producao: implantacao, backup e operacao | Pendente |
| SUP-12 | Desempenho e carga | Pendente |
| SUP-13 | Desligamento do Dashboard Builder | Pendente |
| SUP-14 | Homologacao e PoC do edital | Pendente |

## Relatorios

- [SUP-1](relatorios/SESSAO-SUP-01-relatorio.md)
- [SUP-2](relatorios/SESSAO-SUP-02-relatorio.md)
- [SUP-3](relatorios/SESSAO-SUP-03-relatorio.md)
- [SUP-4](relatorios/SESSAO-SUP-04-relatorio.md)
- [SUP-5](relatorios/SESSAO-SUP-05-relatorio.md)
- [SUP-6](relatorios/SESSAO-SUP-06-relatorio.md)
- [SUP-7](relatorios/SESSAO-SUP-07-relatorio.md)

## Infraestrutura

Toda a infraestrutura Docker fica em `analise/` na raiz do repositorio. Consulte o [README da infraestrutura](../../analise/README.md) para instrucoes de uso.
