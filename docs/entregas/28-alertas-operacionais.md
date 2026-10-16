# Entrega — Plano 28: Alertas Operacionais

> **Data:** 2026-10-16
> **Status:** 🟡 Implementado — migração executada, aguardando teste no navegador.
> **Fase:** 6 de 11 da correção da auditoria de 2026-10-16.

## Escopo

Tipos de alerta operacionais (estacionamento irregular, congestionamento/retenção, bloqueio de via, falha de equipamento), criticidade e cor/ícone configuráveis pela CONTRATANTE, tratamentos parametrizáveis com execução automática, detecção de retenção/congestionamento por regra sobre dados de tráfego horários, filtros avançados no histórico de alertas (localidade, usuário responsável, resultado do tratamento) e exportação CSV.

## Origem

- Plano: [`28-alertas-operacionais.md`](../planos-salvador/28-alertas-operacionais.md)
- **Errata aplicada:** [`00c-errata-correcoes.md`](../planos-salvador/00c-errata-correcoes.md) §1/§2 (sessão e acesso), §7 (jobs Quartz), §10 (`AuditoriaService`), §13 (PKs uniqueidentifier), §14 (Agendador), §15 (`config_chave_valor`)

## O que mudou

- **Tipos operacionais** (5.3.2.1): 4 novos tipos em `muralha.tipo_alerta_ocorrencia` — Estacionamento Irregular, Congestionamento/Retenção, Bloqueio de Via e Falha de Equipamento. Cada um com UUID fixo e prioridade. O tipo Congestionamento é detectado automaticamente pelo job.
- **Criticidade e destaque visual** (5.3.3.3-4, 5.3.7.8): tabela complementar `muralha.tipo_alerta_config` com `critico` (bit), `cor` (nome CSS) e `icone` (classe Bootstrap Icons). O `notificacao.js` carrega do banco via AJAX ao inicializar, com fallback para o mapa fixo anterior. Tela de configuração (`TipoAlertaConfigServlet`) para o grupo 42.
- **Tratamentos** (5.3.1.3): tabela `muralha.tipo_alerta_tratamento` com 7 tipos possíveis (CAMPO, ANALISE_OPERACIONAL, REGISTRO_ESTATISTICO, INTEGRACAO_CORPORATIVA, GERAR_OCORRENCIA, PRIORIZAR_AUDITORIA, OUTRO). Os marcados como automáticos são executados ao gerar o alerta: `REGISTRO_ESTATISTICO` e `PRIORIZAR_AUDITORIA` gravam no log de auditoria, `GERAR_OCORRENCIA` muda o status do alerta para Ocorrência.
- **Detecção de retenção** (5.3.7.4 d-e): `RegraRetencao` avalia horas consecutivas em `dbo.veiculo_sumarizado` onde velocidade média < limiar AND ocupação >= limiar AND tráfego > 0. Severidade ALTA (>= 4h), MEDIA (>= 2h), BAIXA (1h). O `ProcessadorRetencao` processa em lote e grava em `muralha.alerta` + `muralha.alerta_retencao`. O `JobAlertaRetencaoTrafego` roda a cada 10 minutos via Quartz.
- **Filtros avançados no histórico** (5.3.6): `consulta.jsp` ganha filtros de localidade (selectpicker), usuário responsável (selectpicker) e resultado do tratamento (PENDENTE, OCORRENCIA, DESCARTADO, VINCULADO). `FiltroHistorico` constrói os fragmentos SQL com validação de entrada.
- **Exportação CSV** (5.3.6.4): botão CSV adicionado à consulta de alertas, ao lado do botão Excel.
- **Parâmetros de retenção** (5.3.7.6): 6 chaves em `muralha.config_chave_valor` (`RETENCAO_OCUPACAO_MINIMA`, `RETENCAO_VELOCIDADE_MAXIMA`, `RETENCAO_DURACAO_HORAS`, `RETENCAO_JANELA_HORAS`, `RETENCAO_LOTE`, `RETENCAO_ESPERA_REINCIDENCIA_MIN`), todos com padrão documentado.

## Arquivos

| Tipo | Arquivos |
|---|---|
| Enum/POJO | `src/main/java/muralha/digital/alerta/operacional/TratamentoAlerta.java`, `ConfigTipoAlerta.java` |
| Regra | `RegraRetencao.java`, `ParametrosRetencao.java`, `FiltroHistorico.java` |
| Repositório | `RepositorioRetencao.java` (interface), `JdbcRepositorioRetencao.java` |
| Processamento | `ProcessadorRetencao.java`, `JobAlertaRetencao.java`, `ExecutorTratamento.java`; registro em `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` |
| Servlet | `TipoAlertaConfigServlet.java` (`/MuralhaDigital/TipoAlertaConfig`) |
| Frontend | `src/main/webapp/muralha-digital/utils/notificacao/js/notificacao.js` (cores do banco), `src/main/webapp/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp` (filtros avançados + CSV) |
| Testes | `src/test/java/muralha/digital/alerta/operacional/RegraRetencaoTest.java`, `ProcessadorRetencaoTest.java`, `ConfigETratamentoTest.java` |
| Banco | `docs/banco-de-dados/migracoes/20261016_alertas_operacionais.sql` |

## Migrações

| Script | Situação |
|---|---|
| `20261016_alertas_operacionais.sql` | **Executado (2026-10-16)** — 3 tabelas, 4 tipos operacionais, 13 configs de cor, 6 tratamentos seed, 6 parâmetros `RETENCAO_*`, menu criado (id_menu=392, id_infos=53). |

## Parâmetros (`muralha.config_chave_valor`)

| Chave | Padrão | Uso |
|---|---|---|
| `RETENCAO_OCUPACAO_MINIMA` | `500` | Ocupação mínima (valor bruto) para considerar retenção |
| `RETENCAO_VELOCIDADE_MAXIMA` | `15` | Velocidade média máxima (km/h) para considerar retenção |
| `RETENCAO_DURACAO_HORAS` | `1` | Mínimo de horas consecutivas para gerar alerta |
| `RETENCAO_JANELA_HORAS` | `3` | Janela de dados analisada pelo job (últimas N horas) |
| `RETENCAO_LOTE` | `100` | Equipamentos avaliados por execução |
| `RETENCAO_ESPERA_REINCIDENCIA_MIN` | `60` | Minutos antes de gerar outro alerta para o mesmo equipamento |

## Critérios de aceite

- [x] 1. `..\.setup-gtw\build.ps1` compila sem erro (Windows, 2026-10-16).
- [x] 2. Testes TestNG (Windows, 2026-10-16): `RegraRetencaoTest` 7, `ProcessadorRetencaoTest` 4, `ConfigETratamentoTest` 9. Total 20/20; com as fases anteriores, 369/369 (5 skips de infra). Cobrem consecutividade de horas, severidade, tráfego zero, geração de alerta, duplicata, falha parcial, enum de tratamento, validação de cor, filtro histórico e parâmetros padrão.
- [ ] 3. Migração executada (com autorização) e roteiro [`docs/testes/28-alertas-operacionais.md`](../testes/28-alertas-operacionais.md) no navegador.
- [x] 4. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Situação dos itens do TR

| Item | Antes | Depois | Por quê |
|---|---|---|---|
| 5.3.1.3 | ⚠️ | ⚠️ | Tratamentos parametrizáveis com execução automática; tela de configuração pronta; aguardando migração e teste |
| 5.3.2.1 | ⚠️ | ⚠️ | 4 novos tipos operacionais inseridos pelo script; aguardando migração |
| 5.3.3.3 | ⚠️ | ⚠️ | Criticidade por tipo via `tipo_alerta_config`; aguardando migração e teste |
| 5.3.3.4 | ⚠️ | ⚠️ | Cor e ícone configuráveis pelo banco, `notificacao.js` carrega via AJAX; aguardando migração |
| 5.3.6.2 | ⚠️ | ⚠️ | Filtros de localidade, responsável e resultado adicionados; aguardando migração e teste |
| 5.3.6.4 | ⚠️ | ⚠️ | CSV adicionado; aguardando teste no navegador |
| 5.3.7.4 | ⚠️ | ⚠️ | Retenção detectada por regra sobre `veiculo_sumarizado`; aguardando migração e teste |
| 5.3.7.6 | ⚠️ | ⚠️ | Limiares de retenção e configuração de tipos via interface; aguardando migração |
| 5.3.7.8 | ⚠️ | ⚠️ | Prioridade + cor/ícone parametrizáveis; aguardando migração e teste |

## Limitações conhecidas

- A detecção de retenção usa `dbo.veiculo_sumarizado` (granularidade horária): blocos menores que 1 hora não são detectáveis.
- O campo `ocupacao` de `veiculo_sumarizado` aparenta ser um valor bruto, não percentual; o limiar padrão 500 foi escolhido por observação dos dados do DEV e deve ser ajustado em produção.
- Alertas sem veículo monitorado (congestionamento, falha de equipamento) não aparecem no popup de notificação em tempo real porque `spu_ObterNovosAlertasV2` faz `INNER JOIN cad_veiculo_monitorado`. A refatoração dessa procedure fica para a Fase 9.
- A correlação espacial (5.3.4.2-3) usa o `id_local` dos equipamentos existentes; agrupamento por proximidade geográfica ou manchas de calor depende de dados de coordenada que o sistema já tem em `local_vigente`, mas a visualização fica para a Fase 8.
