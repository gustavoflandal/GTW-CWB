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
| [SUP-8](sessoes/SUP-08-mapas-analise-espacial.md) | Mapas e analise espacial | Implementada |
| [SUP-9](sessoes/SUP-09-embedding.md) | Embedding em telas do GTW | Implementada |
| [SUP-10](sessoes/SUP-10-manual-usuario.md) | Manual do usuario e capacitacao | Implementada |
| [SUP-11](sessoes/SUP-11-producao.md) | Producao: implantacao, backup e operacao | Implementada |
| [SUP-12](sessoes/SUP-12-desempenho.md) | Desempenho e carga | Implementada |
| [SUP-13](sessoes/SUP-13-coexistencia-builder.md) | Coexistencia: Dashboard Builder e Superset | Implementada |
| [SUP-14](sessoes/SUP-14-homologacao.md) | Homologacao e PoC do edital | Implementada |

## Relatorios

- [SUP-1](relatorios/SESSAO-SUP-01-relatorio.md)
- [SUP-2](relatorios/SESSAO-SUP-02-relatorio.md)
- [SUP-3](relatorios/SESSAO-SUP-03-relatorio.md)
- [SUP-4](relatorios/SESSAO-SUP-04-relatorio.md)
- [SUP-5](relatorios/SESSAO-SUP-05-relatorio.md)
- [SUP-6](relatorios/SESSAO-SUP-06-relatorio.md)
- [SUP-7](relatorios/SESSAO-SUP-07-relatorio.md)
- [SUP-8](relatorios/SESSAO-SUP-08-relatorio.md)
- [SUP-9](relatorios/SESSAO-SUP-09-relatorio.md)
- [SUP-10](relatorios/SESSAO-SUP-10-relatorio.md)
- [SUP-11](relatorios/SESSAO-SUP-11-relatorio.md)
- [SUP-12](relatorios/SESSAO-SUP-12-relatorio.md)
- [SUP-13](relatorios/SESSAO-SUP-13-relatorio.md)
- [SUP-14](relatorios/SESSAO-SUP-14-relatorio.md)

## Documentacao

- [Manual do Usuario](manual-usuario.md) — acesso, paineis, filtros, exportacao, alertas
- [Guia do Administrador](guia-administrador.md) — Docker, provisioner, backup, troubleshooting
- [Camada de dados](camada-dados-views-datasets.md) — views, datasets e tabelas de origem

## Infraestrutura

Toda a infraestrutura Docker fica em `analise/` na raiz do repositorio. Consulte o [README da infraestrutura](../../analise/README.md) para instrucoes de uso.
