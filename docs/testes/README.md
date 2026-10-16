# Testes — PoC TRANSALVADOR

Registro de testes manuais executados a cada plano implementado.

## Ambiente de teste
- Servidor: `http://localhost:8080/`
- Banco: `GTW_MURALHA_DEV` (10.0.0.200)
- Build: `..\.setup-gtw\build.ps1 -ProjectName 'GTW-CWB'`

## Índice

A numeração segue a do plano: o Teste 20 corresponde ao Plano 20, cuja entrega é a 21. Não há Teste 21 (o Plano 21 foi validado dentro do Teste 20 e da Entrega 21).

| Plano | Documento | Status |
|---|---|---|
| 01 — Log de Auditoria | [01-log-auditoria.md](01-log-auditoria.md) | ⏳ Parcial: 1 caso aprovado, demais pendentes de execução manual |
| 02 — SHA-256 Integridade | [02-sha256-integridade.md](02-sha256-integridade.md) | ⏳ Parcial: 1 caso aprovado, demais pendentes de execução manual |
| 03 — Política de Senhas | [03-politica-senhas.md](03-politica-senhas.md) | ⏳ Pendente |
| 04 — MFA/TOTP | [04-mfa-totp.md](04-mfa-totp.md) | ⏳ Pendente |
| 05 — Dupla Análise | [05-dupla-analise.md](05-dupla-analise.md) | ⏳ Pendente |
| 06 — Obliteração de Imagens | [06-obliteracao-imagens.md](06-obliteracao-imagens.md) | ⏳ Pendente |
| 07 — SLA Latência | [07-sla-latencia.md](07-sla-latencia.md) | ⏳ Pendente |
| 08 — Painel Assertividade | [08-painel-assertividade.md](08-painel-assertividade.md) | ⏳ Pendente |
| 09 — CPF/Matrícula | [09-cpf-matricula.md](09-cpf-matricula.md) | ⏳ Pendente |
| 10 — SLA Pré-processamento | [10-sla-preprocessamento.md](10-sla-preprocessamento.md) | ⏳ Pendente |
| 11 — Exportação Padronizada | [11-exportacao-padronizada.md](11-exportacao-padronizada.md) | ⏳ Pendente |
| 12 — Retenção de Dados | [12-retencao-dados.md](12-retencao-dados.md) | ⏳ Pendente |
| 13 — Anonimização de Placas | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) | ⏳ Pendente |
| 14 — Gestão de Lotes | [14-gestao-lotes.md](14-gestao-lotes.md) | ⏳ Pendente |
| 15 — KPIs Configuráveis | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) | ⏳ Pendente |
| 16 — Exportação GIS | [16-exportacao-gis.md](16-exportacao-gis.md) | ⏳ Pendente |
| 17 — Time-lapse | [17-timelapse.md](17-timelapse.md) | ⏳ Pendente |
| 18 — Disponibilidade | [18-disponibilidade.md](18-disponibilidade.md) | ⏳ Pendente |
| 19 — Sensores Externos | [19-sensores-externos.md](19-sensores-externos.md) | ⏳ Pendente |
| 20 — Dashboard Builder (entrega 21; inclui a revisão do Plano 21) | [20-dashboard-builder.md](20-dashboard-builder.md) | ⏳ Pendente (testes TestNG do módulo registrados na Entrega 21; roteiro no navegador pendente) |
| 22 — Sistema de Chamados | [22-sistema-chamados.md](22-sistema-chamados.md) | ⏳ Parcial: teste integrado de 2026-10-15 com 10 de 10 casos aprovados; navegador e perfil Gestor pendentes |
| 23 — Correção da documentação de aderência | [23-correcao-documentacao-aderencia.md](23-correcao-documentacao-aderencia.md) | ⏳ Pendente (script de validação executado na nuvem; roteiro a repetir no Windows) |
| 24 — Correções pontuais da auditoria | [24-correcoes-pontuais-auditoria.md](24-correcoes-pontuais-auditoria.md) | ⏳ Parcial: 15 testes TestNG aprovados na nuvem e no Windows; roteiro manual pendente |
| 25 — Integridade SHA-256 na recepção | [25-integridade-sha256-recepcao.md](25-integridade-sha256-recepcao.md) | ⏳ Parcial: 31 testes TestNG aprovados no Windows; migração e roteiro no navegador pendentes |
| 26 — Logs técnicos e histórico de parâmetros | [26-logs-tecnicos-historico-parametros.md](26-logs-tecnicos-historico-parametros.md) | ⏳ Parcial: 42 testes TestNG e consulta real somente leitura aprovados no Windows; migração e roteiro no navegador pendentes |
| 27 — Score de OCR e revisão de placas | [27-score-ocr-revisao-placas.md](27-score-ocr-revisao-placas.md) | ⏳ Parcial: 32 testes TestNG aprovados no Windows; migração e roteiro no navegador pendentes |

Revisado em 2026-10-16 a partir dos próprios roteiros ([Plano 23](../planos-salvador/23-correcao-documentacao-aderencia.md)).
