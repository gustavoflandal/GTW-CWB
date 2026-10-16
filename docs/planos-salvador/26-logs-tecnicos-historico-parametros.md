# Repositório de Logs Técnicos e Histórico de Parâmetros — Plano 26 (Fase 4 da auditoria de 2026-10-16)

> **Status:** Executado (2026-10-16): ver [Entrega 26](../entregas/26-logs-tecnicos-historico-parametros.md).
> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) (§1, §2 e §3 aplicadas no servlet novo).
> **Depende de:** [Plano 18](18-disponibilidade.md) (disponibilidade), [Plano 07](07-sla-latencia.md) e [Plano 10](10-sla-preprocessamento.md) (alertas de SLA), [Plano 01](01-log-auditoria.md) (log de auditoria).

**Objetivo:** expor num único lugar os logs técnicos que já existem no legado, normalizados e tipificados, com consulta, linha do tempo, correlação, exportação e relatório por equipamento; e guardar o valor anterior e o novo de cada parâmetro alterado.

## Referência ao Edital (TR TRANSALVADOR)

Texto literal de [`TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md), seção 5.4.7 e itens 5.2.7.2-3:

> 5.4.7.1. A Funcionalidade de Inteligência de Dados deverá atuar como repositório central para recebimento, armazenamento, organização, consolidação e consulta dos logs técnicos e operacionais gerados pelos equipamentos de fiscalização eletrônica e pelos sistemas integrantes da solução.
> 5.4.7.2. O sistema deverá receber e armazenar automaticamente os logs operacionais e técnicos relacionados:
> - a) ao funcionamento dos equipamentos;
> - b) aos eventos operacionais;
> - c) às falhas e degradações de desempenho;
> - d) às alterações de configuração;
> - e) às ações realizadas por usuários;
> - f) às ocorrências sistêmicas relevantes.
> 5.4.7.3. O repositório deverá permitir consulta e visualização de logs relacionados, no mínimo:
> - a) à alimentação elétrica;
> - b) à comunicação de dados;
> - c) ao sincronismo temporal;
> - d) às falhas de hardware;
> - e) às falhas de software;
> - f) aos sensores e módulos operacionais;
> - g) às alterações de parâmetros;
> - h) às intervenções técnicas;
> - i) às atividades de processamento e formação de lotes;
> - j) aos eventos operacionais relevantes.
> 5.4.7.4. Todos os registros de log deverão possuir:
> - a) data e horário;
> - b) identificação do equipamento;
> - c) identificação do usuário responsável, quando aplicável;
> - d) descrição do evento;
> - e) rastreabilidade integral da ocorrência.
> 5.4.7.5. O sistema deverá permitir:
> - a) filtros por período, equipamento, localidade, categoria e severidade;
> - b) correlação temporal entre eventos;
> - c) exportação dos registros;
> - d) emissão de relatórios técnicos;
> - e) auditoria operacional dos eventos registrados.
> 5.4.7.6. O sistema deverá manter histórico das alterações realizadas nos parâmetros operacionais dos equipamentos e funcionalidades da solução, contendo:
> - a) data e horário da alteração;
> - b) parâmetro alterado;
> - c) valor anterior;
> - d) valor atualizado;
> - e) identificação do responsável pela alteração.
> 5.4.7.7. Os registros de logs deverão permanecer íntegros, rastreáveis e disponíveis durante toda a vigência contratual e pelo prazo adicional definido pela política de retenção de dados da CONTRATANTE.
>
> 5.2.7.2. O sistema deverá identificar automaticamente falhas de comunicação, indisponibilidade operacional, falhas de captura, interrupções elétricas e inconsistências técnicas dos equipamentos.
>
> 5.2.7.3. Todos os eventos operacionais deverão ser registrados em histórico auditável.

## Situação encontrada (investigação de 2026-10-16, consultas só de leitura no DEV)

| Fonte | Linhas | O que tem |
|---|---|---|
| `dbo.eventos_csx` (+ domínios `eventos_csx_desc_*`) | 504 mil, vivos | Eventos do Captura e do GTW: aplicação, configuração, hardware, rede, detecção, sincronismo de configuração. Energia (eventos 6, 7, 8), conexão (9, 10), NTP (14, 38), manutenção aberta/fechada (15, 16), parâmetros metrológicos (33, 34), login (40-42). Nível 0-4 = INFO, ALERTA, ERRO, CRÍTICO, DEBUG. `id_proprietario` → `proprietario` = série do equipamento (ou login/servidor) |
| `dbo.status_conexao` / `dbo.status_energia` | 11,3 mil / 467 | Histórico de transições: 1 = online/com energia, 2 = offline/sem energia (`LocalStatus.StatusConexao/StatusEnergia`) |
| `dbo.log_alerta` + `dbo.log_tipo` | 83 | Estados do equipamento (sem energia, sensores, câmera, semáforo, conexão) e eventos de mobilidade |
| `dbo.log_processos` | 1,36 milhão | Uma linha por minuto de cada rotina de importação; sem índice em `data_inicio` |
| `dbo.manutencao*` | 0 | Cadastro de manutenção do GTW clássico (vazio no DEV) |
| `muralha.equipamento_disponibilidade` | 0 | Plano 18 (vazio no DEV) |
| `muralha.alerta_sla` / `alerta_sla_preproc` | 992 / 113 | Janelas de latência e verificações de pré-processamento |
| `dbo.sis_log_auditoria` | 71 | Ações dos usuários (Plano 01) |
| `muralha.config_chave_valor_hist` | 68 | Texto "chave: anterior -> novo" gravado por telas antigas de configuração |

Valor anterior/novo de parâmetros de equipamento não existe em lugar nenhum; o ConfigEquip só guarda a versão vigente.

## Decisões

- **Serviço Java, não view.** Cada fonte é uma classe (`FontesLogTecnico`) com a própria consulta e a normalização para `data_hora, id_local, origem, categoria, severidade, descrição, usuário, id_referencia`, testável sem banco. A categoria dos eventos do Captura e de `log_tipo` vem de tabelas de classificação no código (`ClassificacaoCsx`, `ClassificacaoLogTipo`) sobre domínios fixos do banco; o filtro por categoria vira predicado SQL com inteiros desses domínios.
- **Categorias:** energia, comunicação, sincronismo, hardware, software, sensor, parâmetro, intervenção técnica, processamento, usuário. **Severidades:** DEBUG, INFO, ALERTA, ERRO, CRÍTICO.
- **Início/fim (5.2.7.2):** eventos marcados como início ou fim de um grupo (ENERGIA, COMUNICACAO, CONEXAO_SERVIDOR, MANUTENCAO, CONFIGURACAO, AFERICAO, SEMAFORO, DIV, DISPONIBILIDADE, FALHA, FORA_DE_OPERACAO) viram períodos com duração (`CorrelacionadorIntervalos`), por equipamento + fonte + grupo.
- **Paginação:** busca as N primeiras linhas de cada fonte e intercala; profundidade máxima de 10 mil registros (além disso, CSV). `log_processos` entra só como "não finalizado" por padrão (parâmetro), para não inundar a lista.
- **Parâmetros:** período máximo, modo e atraso de `log_processos`, limite do CSV e janela de correlação em `config_chave_valor` (`LOG_TECNICO_*`).
- **Histórico de parâmetros:** tabela `muralha.parametro_historico` e helper único `HistoricoParametros` (comparação + gravação que nunca impede a alteração). Ligado a: `ConfiguracaoEquipamento.incluirConfigEquip` (ConfigEquipWS e importação de XML; compara o XML da versão vigente com o novo, campo a campo), KPIs, retenção, origem do hash de integridade (padrão e por equipamento) e `ConfigChaveValor.gravar` com contexto. As telas antigas que já gravam `config_chave_valor_hist` continuam como estão e aparecem no repositório como fonte `CONFIG_HIST`.
- **Menu 201** ("Relatório Alt. ConfigEquip", GTW clássico): desativado por decisão do dono do projeto em 2026-10-16 (sem grupos com direito; substituído pelo histórico de parâmetros). Ver [Entrega 24](../entregas/24-correcoes-pontuais-auditoria.md).

## Tarefas

- [x] 1. Modelo e regras: `CategoriaLog`, `SeveridadeLog`, `RegistroLogTecnico`, `FiltroLogTecnico`, `ClassificacaoCsx`, `ClassificacaoLogTipo`, `ParametrosLogTecnico`.
- [x] 2. Fontes: `FontesLogTecnico` (12 fontes) e `ServicoLogTecnico` (contagem, intercalação, localidade → equipamentos, fontes ausentes).
- [x] 3. Períodos e relatório: `CorrelacionadorIntervalos`, `RelatorioTecnico`.
- [x] 4. Histórico de parâmetros: `HistoricoParametros`, `AlteracaoParametro`, `ContextoAlteracao`, `HistoricoConfigEquip`; ganchos em `ConfiguracaoEquipamento`, `KpiServlet`, `RetencaoServlet`, `IntegridadeConfigServlet`, `ConfigChaveValor`.
- [x] 5. Servlet `/MuralhaDigital/LogTecnico` (opcoes, listar, exportarCsv, linhaTempo, correlacionar, relatorioEquipamento, historicoParametros).
- [x] 6. Tela `pages/monitoramento/logs-tecnicos/` (Eventos, Linha do tempo, Relatório técnico, Histórico de parâmetros).
- [x] 7. Migração `20261016_logs_tecnicos_parametro_historico.sql` e desativação do menu 201 (`20261016_desativa_menu_201.sql`).
- [x] 8. Testes TestNG da normalização de cada fonte, classificação, períodos, histórico e filtro.

## Critérios de aceite

1. `..\.setup-gtw\build.ps1` compila sem erro.
2. Testes: normalização de cada uma das 12 fontes; classificação e predicado do Captura; períodos início/fim; comparação de parâmetros (mapa e XML do ConfigEquip); filtro e relatório.
3. Consulta real no DEV (somente leitura) devolve eventos das fontes existentes, com tempos aceitáveis.
4. Migração executada (com autorização) e roteiro [`docs/testes/26-logs-tecnicos-historico-parametros.md`](../testes/26-logs-tecnicos-historico-parametros.md) executado no navegador.
5. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Fora do escopo

- Integridade e disponibilidade dos logs por toda a vigência e pela política de retenção (5.4.7.7) e "rastreabilidade integral" (5.4.7.4 e): as tabelas de origem são do legado, alimentadas pelo Captura; não há trilha imutável nova nem política de retenção específica para logs. Ficam pendentes na matriz.
- Migrar as telas antigas que gravam `config_chave_valor_hist` para o helper novo (comportamento do legado).
- Índice em `dbo.log_processos.data_inicio` (tabela legada; o modo padrão "não finalizados" limita o volume exibido, mas a consulta varre a tabela).
