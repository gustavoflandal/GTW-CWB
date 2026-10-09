# Relatórios de Entrega — PoC TRANSALVADOR

Registro formal de cada fase entregue: escopo, arquivos produzidos, critérios de aceite e pendências.

## Entregas

| # | Plano | Data | Status | Relatório |
|---|---|---|---|---|
| 01 | Log de Auditoria Centralizado | 2026-10-08 | ✅ Entregue | [01-log-auditoria.md](01-log-auditoria.md) |
| 02 | Hash SHA-256 de Integridade | 2026-10-08 | ✅ Entregue | [02-sha256-integridade.md](02-sha256-integridade.md) |
| 03 | Política de Senhas | 2026-10-08 | ✅ Entregue¹ | [03-politica-senhas.md](03-politica-senhas.md) |
| 04 | MFA/TOTP | 2026-10-08 | ✅ Entregue¹ | [04-mfa-totp.md](04-mfa-totp.md) |
| 05 | Dupla Análise | 2026-10-08 | ✅ Entregue² | [05-dupla-analise.md](05-dupla-analise.md) |
| 06 | Obliteração de Imagens | 2026-10-08 | ✅ Entregue² | [06-obliteracao-imagens.md](06-obliteracao-imagens.md) |
| 07 | SLA de Latência | 2026-10-08 | ✅ Entregue² | [07-sla-latencia.md](07-sla-latencia.md) |
| 08 | Painel de Assertividade | 2026-10-08 | ✅ Entregue² | [08-painel-assertividade.md](08-painel-assertividade.md) |
| 09 | CPF/Matrícula | 2026-10-08 | ✅ Entregue² | [09-cpf-matricula.md](09-cpf-matricula.md) |
| 10 | SLA Pré-processamento | 2026-10-08 | ✅ Entregue² | [10-sla-preprocessamento.md](10-sla-preprocessamento.md) |
| 11 | Exportação Padronizada | 2026-10-08 | ✅ Entregue² | [11-exportacao-padronizada.md](11-exportacao-padronizada.md) |
| 12 | Retenção de Dados | 2026-10-08 | ✅ Entregue³ | [12-retencao-dados.md](12-retencao-dados.md) |
| 13 | Anonimização de Placas (LGPD) | 2026-10-08 | ✅ Entregue³ | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 14 | Gestão de Lotes | 2026-10-08 | ✅ Entregue⁴ | [14-gestao-lotes.md](14-gestao-lotes.md) |
| 15 | KPIs Configuráveis | 2026-10-08 | ✅ Entregue⁴ | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 16 | Exportação GIS | 2026-10-08 | ✅ Entregue | [16-exportacao-gis.md](16-exportacao-gis.md) |
| 17 | Time-lapse de Passagens | 2026-10-08 | ✅ Entregue | [17-timelapse.md](17-timelapse.md) |
| 18 | Disponibilidade dos Equipamentos | 2026-10-08 | ✅ Entregue⁵ | [18-disponibilidade.md](18-disponibilidade.md) |
| 19 | Sensores Externos (Waze/SAMU) | 2026-10-08 | ✅ Entregue⁵ | [19-sensores-externos.md](19-sensores-externos.md) |

¹ Migração SQL do Plano 03 pendente de execução manual no banco.  
² Migração `20261008_complementar.sql` pendente de execução manual (tabelas complementares — sem ALTER TABLE).  
³ Migração `20261008_retencao_anonimizacao.sql` pendente de execução manual (expurgo_log + view anonimizada).  
⁴ Migração `20261008_lotes_kpis.sql` pendente de execução manual (lote_infracao + kpi_config).  
⁵ Migração `20261008_disponibilidade_incidentes.sql` pendente de execução manual (disponibilidade + incidentes).

## Referências cruzadas

- Testes manuais: [`docs/testes/`](../testes/README.md)
- Planos de implementação: [`docs/planos-salvador/`](../planos-salvador/)
- Migrações de banco: [`docs/banco-de-dados/migracoes/`](../banco-de-dados/migracoes/)
