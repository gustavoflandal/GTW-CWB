# Entrega — Plano 26: Repositório de Logs Técnicos e Histórico de Parâmetros

> **Data:** 2026-10-16
> **Status:** 🟡 Implementado — aguardando teste no navegador (build, testes, consulta real no DEV e migrações concluídos em 2026-10-16).
> **Fase:** 4 de 11 da correção da auditoria de 2026-10-16.

## Escopo

Tela única de logs técnicos sobre as tabelas que já existem no legado e na PoC, normalizadas em categoria e severidade, com períodos de início/fim, correlação temporal, exportação e relatório técnico por equipamento (TR 5.4.7.1-5, 5.2.7.2); histórico de parâmetros com valor anterior e novo (TR 5.4.7.6). Inclui a desativação do menu 201, decidida pelo dono do projeto.

## Origem

- Plano: [`26-logs-tecnicos-historico-parametros.md`](../planos-salvador/26-logs-tecnicos-historico-parametros.md)
- **Errata aplicada:** [`00c-errata-correcoes.md`](../planos-salvador/00c-errata-correcoes.md) §1, §2 e §3 (sessão, acesso e estrutura do servlet `LogTecnicoServlet`; usuário da sessão em `ContextoAlteracao`)

## O que mudou

- **Repositório (5.4.7.1-4):** `ServicoLogTecnico` lê 12 fontes (`eventos_csx`, `status_conexao`, `status_energia`, `manutencao`, `log_alerta`, `equipamento_disponibilidade`, `log_processos`, `alerta_sla`, `alerta_sla_preproc`, `sis_log_auditoria`, `config_chave_valor_hist`, `parametro_historico`) e devolve registros com data/hora, equipamento, origem, categoria, severidade, descrição, usuário e referência à linha de origem. Fonte cuja tabela não existe é pulada e avisada na tela.
- **Classificação:** eventos do Captura por categoria/evento do domínio `eventos_csx_desc_*` (`ClassificacaoCsx`); `log_alerta` por `log_tipo` (`ClassificacaoLogTipo`, sem os tipos de mobilidade). Severidade do Captura pelo nível (0 INFO, 1 ALERTA, 2 ERRO, 3 CRÍTICO, 4 DEBUG).
- **Períodos (5.2.7.2):** queda/retorno de energia, desconexão/reconexão, offline/online, indisponibilidade, manutenção, configuração aberta/fechada, aferição, semáforo e DIV viram períodos com início, fim e duração.
- **Tela** Análise de Dados → Logs Técnicos (5.4.7.5): aba Eventos (filtros período, equipamento, localidade, categoria, severidade; paginação no servidor; CSV com SHA-256 e XLS/PDF), Linha do tempo (faixas por categoria, barras de período e pontos de evento; clique abre a correlação: eventos de todos os equipamentos em ± `LOG_TECNICO_JANELA_CORRELACAO_MIN`), Relatório técnico (contagem por categoria e severidade, períodos com duração, parâmetros alterados, erros e críticos; PDF) e Histórico de parâmetros. Exportação CSV e relatório vão para `sis_log_auditoria`.
- **Histórico de parâmetros (5.4.7.6):** `muralha.parametro_historico` e helper único `HistoricoParametros`. Gravam: `ConfiguracaoEquipamento.incluirConfigEquip` (web service `ConfigEquipWS` e importação de XML; compara o XML da versão vigente com o novo, campo a campo, ignorando `dataHoraConfiguracao`), Configuração de KPIs, Retenção de dados e origem do hash de integridade (padrão e por equipamento). O histórico nunca impede a alteração: sem a tabela, só registra aviso no log.
- **Menu 201:** relatório revertido, servlet só avisa a desativação, script `20261016_desativa_menu_201.sql` (ver [Entrega 24](24-correcoes-pontuais-auditoria.md)).

## Arquivos

| Tipo | Arquivos |
|---|---|
| Modelo e regras | `src/main/java/muralha/digital/logtecnico/CategoriaLog.java`, `SeveridadeLog.java`, `RegistroLogTecnico.java`, `FiltroLogTecnico.java`, `ParametrosLogTecnico.java`, `ClassificacaoCsx.java`, `ClassificacaoLogTipo.java` |
| Fontes e serviço | `FonteLog.java`, `FontesLogTecnico.java`, `ServicoLogTecnico.java`, `CorrelacionadorIntervalos.java`, `RelatorioTecnico.java`, `LogTecnicoServlet.java` (`/MuralhaDigital/LogTecnico`) |
| Histórico de parâmetros | `HistoricoParametros.java`, `AlteracaoParametro.java`, `ContextoAlteracao.java`, `HistoricoConfigEquip.java`; ganchos em `src/main/java/com/consilux/model/ConfiguracaoEquipamento.java` (5 linhas), `muralha/digital/kpi/KpiServlet.java`, `muralha/digital/retencao/RetencaoServlet.java`, `muralha/digital/integridade/IntegridadeConfigServlet.java`, `muralha/digital/util/ConfigChaveValor.java` |
| Tela | `src/main/webapp/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp`, `assets/js/logs-tecnicos.js`, `assets/css/logs-tecnicos.css` |
| Menu 201 | `src/main/java/com/consilux/servlet/relatorio/RelatorioAlteracaoConfigEquip.java`; removidos `src/main/webapp/relatorio/res_alteracoes_configequip.jsp` e `RelatorioAlteracaoConfigEquipTest.java` |
| Testes | `src/test/java/muralha/digital/logtecnico/FontesLogTecnicoTest.java`, `ClassificacaoCsxTest.java`, `CorrelacionadorIntervalosTest.java`, `HistoricoParametrosTest.java`, `FiltroERelatorioLogTecnicoTest.java` |
| Banco | `docs/banco-de-dados/migracoes/20261016_logs_tecnicos_parametro_historico.sql`, `20261016_desativa_menu_201.sql` |

## Migrações

| Script | Situação |
|---|---|
| `20261016_logs_tecnicos_parametro_historico.sql` | Executado (2026-10-16): tabela, 6 chaves e menus 387-388 |
| `20261016_desativa_menu_201.sql` | Executado (2026-10-16): menu 201 com `Ativo = 0` |

## Parâmetros (`muralha.config_chave_valor`)

| Chave | Padrão | Uso |
|---|---|---|
| `LOG_TECNICO_PERIODO_MAX_DIAS` | `31` | Período máximo das consultas |
| `LOG_TECNICO_PROCESSOS` | `NAO_FINALIZADOS` | `dbo.log_processos`: `NENHUM`, `NAO_FINALIZADOS` ou `TODOS` (uma linha por minuto) |
| `LOG_TECNICO_PROCESSO_ATRASO_MIN` | `30` | Minutos sem atualização para um processo contar como não finalizado |
| `LOG_TECNICO_EXPORTACAO_MAX` | `50000` | Linhas máximas do CSV |
| `LOG_TECNICO_JANELA_CORRELACAO_MIN` | `10` | Janela (± minutos) da correlação temporal |
| `PARAMETRO_HIST_IGNORAR_CONFIGEQUIP` | `dataHoraConfiguracao` | Campos do ConfigEquip fora do histórico |

## Critérios de aceite

- [x] 1. `..\.setup-gtw\build.ps1` compila sem erro (Windows, 2026-10-16: BUILD SUCCESS).
- [x] 2. Testes TestNG (Windows, 2026-10-16): `FontesLogTecnicoTest` 14 (normalização e consulta de cada fonte), `ClassificacaoCsxTest` 8, `CorrelacionadorIntervalosTest` 6, `HistoricoParametrosTest` 6, `FiltroERelatorioLogTecnicoTest` 8. Total 42/42; com os das fases anteriores, 128/128.
- [x] 3. Consulta real no DEV, só leitura (jshell com o `ServicoLogTecnico` e conexão `setReadOnly`, 2026-10-16): 7 dias, todas as fontes → 7 568 eventos em 2,7 s; equipamento 1, energia/comunicação, ALERTA ou mais grave, 30 dias → 210 eventos em 1,1 s; linha do tempo de 2 dias do equipamento 1 → 2 124 eventos e 11 períodos (CONEXAO_SERVIDOR do Captura e COMUNICACAO de `status_conexao`); filtro por localidade funcionando; `PARAMETRO_HISTORICO` corretamente indicada como ausente. Comparação de ConfigEquip conferida com um `TConfigEquip` serializado pelo `SerializadorXML` (119 campos planificados).
- [x] 4a. Migrações executadas no DEV (2026-10-16, com autorização): `parametro_historico` criada, 6 chaves, menus 387-388, menu 201 inativo.
- [ ] 4b. Roteiro [`docs/testes/26-logs-tecnicos-historico-parametros.md`](../testes/26-logs-tecnicos-historico-parametros.md) executado no navegador.
- [x] 5. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro (planilha atualizada só na aba GTW-Muralha, células D114 e D153-D158).

## Situação dos itens do TR

| Item | Antes | Depois | Por quê |
|---|---|---|---|
| 5.4.7.1-5 | ⚠️ só log de usuários | ⚠️ repositório técnico implementado | Falta migração/teste; os logs continuam nas tabelas do legado |
| 5.4.7.6 | ❌ | ⚠️ | Implementado para ConfigEquip, KPIs, retenção e integridade; telas antigas de configuração só gravam texto em `config_chave_valor_hist`; falta migração/teste |
| 5.4.7.7 | ⚠️ | ⚠️ | Sem trilha imutável nem retenção própria dos logs técnicos (fora do escopo) |

## Limitações conhecidas

- **Eventos do ConfigEquip feitos fora do GTW** (alteração local no Captura) só aparecem como eventos do Captura (categoria 2/1002), sem valor anterior.
- **`dbo.log_processos` não tem índice em `data_inicio`:** a consulta varre a tabela (1,36 milhão de linhas no DEV). Criar o índice é DDL em tabela legada e não foi feito.
- **Paginação:** até 10 mil registros por filtro (página × tamanho); além disso, usar o CSV.
- **Categoria "formação de lotes" (5.4.7.3 i):** não há fonte de log de lote; a gestão de lotes (Plano 14) não grava eventos.
- Os rótulos dos eventos 72 e 73 do Captura não existem em `eventos_csx_desc_evento`; aparecem como "Evento 5/72" com a mensagem.
