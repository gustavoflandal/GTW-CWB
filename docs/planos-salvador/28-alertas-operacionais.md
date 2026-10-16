# Alertas Operacionais — Plano 28 (Fase 6 da auditoria de 2026-10-16)

> **Status:** Executado (2026-10-16): ver [Entrega 28](../entregas/28-alertas-operacionais.md).
> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) (§1, §2, §7, §10, §13, §14 e §15 aplicadas).
> **Depende de:** [Plano 27](27-score-ocr-revisao-placas.md) (fila de revisão e estrutura de alertas).

**Objetivo:** ampliar o sistema de alertas com tipos operacionais (não apenas veiculares), criticidade e cores configuráveis por tipo, tratamentos parametrizáveis (com execução automática), detecção de retenção/congestionamento por regra sobre `veiculo_sumarizado`, e filtros avançados no histórico de alertas (localidade, responsável, resultado do tratamento, exportação CSV).

## Referência ao Edital (TR TRANSALVADOR)

Texto literal de [`TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md):

> 5.3.1.3. Funcionalidades de registro, acompanhamento e encerramento de tratamentos associados a alertas e ocorrências gerados pelo sistema.
>
> 5.3.2.1. Tipos de alertas parametrizáveis, incluindo: alertas de estacionamento irregular, alertas de congestionamento/retenção, alertas de bloqueio de via e alertas de falha de equipamento.
>
> 5.3.3.3. Classificação de criticidade (baixa, média, alta, crítica) associada a cada tipo de alerta, parametrizável pela CONTRATANTE.
>
> 5.3.3.4. Destaque visual diferenciado para alertas críticos, incluindo cores e ícones configuráveis pela CONTRATANTE.
>
> 5.3.4.2. Correlação espacial entre alertas e equipamentos de fiscalização por geolocalização.
>
> 5.3.4.3. Agrupamento de alertas por proximidade geográfica ou área de monitoramento.
>
> 5.3.6. Histórico e consulta de alertas: [...] filtros por localidade, usuário responsável e resultado do tratamento; exportação em CSV.
>
> 5.3.7.4. A funcionalidade deverá suportar recursos de análise volumétrica de tráfego baseados em videodetecção e laço virtual, permitindo: [...] d) identificação de retenções ou congestionamentos; e) geração de alertas operacionais parametrizáveis.
>
> 5.3.7.6. Parametrização de limiares e regras via interface administrativa.
>
> 5.3.7.8. Priorização de alertas por tipo, com destaque visual parametrizável.

## Situação encontrada (investigação de 2026-10-16, consultas só de leitura no DEV)

- **Tipos de alerta:** 9 tipos, todos veiculares (Roubado, Furtado, Clandestino, Roubo Banco, Sequestro, Clonado, Licenciamento, Monitorado, Comboio). Nenhum tipo operacional (estacionamento, congestionamento, bloqueio, falha de equipamento).
- **Criticidade:** não existe classificação de criticidade nem parametrização de cor/ícone; `notificacao.js` tem um mapa fixo de cor por tipo (`obterCorPorTipoAlerta`).
- **Tratamentos:** não há funcionalidade de tratamento de alertas.
- **Histórico:** `consulta-alerta-ocorrencia` filtra por período, equipamento, tipo, status e placa; faltam localidade, usuário responsável e resultado do tratamento. Exporta PDF e XLS, sem CSV.
- **Retenção/congestionamento:** `dbo.veiculo_sumarizado` agrega tráfego por hora por equipamento (`data`, `hora`, `id_local`, `media_velocidade`, `ocupacao`, `trafego`); campo `ocupacao` parece ser valor bruto (842 com 1 veículo), não percentual. Não há regra que avalie esses dados para gerar alertas. O `JOB_RETENCAO` existente é de retenção de **dados** (exclusão), não de tráfego.
- **WebSocket:** `ClienteSessoes.EnviaTextoClientes("ALERTA-NOTIFICACAO", xml)` transmite alertas em tempo real para os navegadores conectados.

## Decisões

- **Tabela complementar `tipo_alerta_config`** (regra "nunca ALTER TABLE"): FK 1:1 para `tipo_alerta_ocorrencia`, com `critico`, `cor`, `icone` e timestamp. Carregada pelo `notificacao.js` via AJAX ao inicializar.
- **Tabela `tipo_alerta_tratamento`**: cada tipo pode ter N tratamentos (campo enum), com flag `automatico` e `parametros_json`. Tratamentos automáticos (`REGISTRO_ESTATISTICO`, `PRIORIZAR_AUDITORIA`, `GERAR_OCORRENCIA`) são executados pelo `ExecutorTratamento` ao gerar o alerta.
- **Ocupação como valor bruto**: limiar default 500 (não percentual); configurável via `RETENCAO_OCUPACAO_MINIMA`.
- **Regra de retenção** (`RegraRetencao`): avalia horas consecutivas onde velocidade < limiar AND ocupação >= limiar AND tráfego > 0. Severidade ALTA (>= 4h), MEDIA (>= 2h), BAIXA (1h). Grava em `muralha.alerta` + `muralha.alerta_retencao`.
- **Job `JobAlertaRetencaoTrafego`** a cada 10 minutos, grupo MONITORAMENTO. Não confundir com `JOB_RETENCAO` (retenção de dados).
- **Filtros avançados** na consulta de alertas: localidade (via `local_vigente`), usuário responsável, resultado do tratamento (PENDENTE, OCORRENCIA, DESCARTADO, VINCULADO). Exportação CSV adicionada.
- **Servlet `TipoAlertaConfigServlet`** para gerenciar cores, criticidade e tratamentos via interface web (grupo 42, menu sob Alertas).

## Tarefas

- [x] 1. Enum `TratamentoAlerta` e POJO `ConfigTipoAlerta` com validação de cor.
- [x] 2. Regra: `RegraRetencao` (avaliação de horas consecutivas), `ParametrosRetencao` (6 chaves em `config_chave_valor`).
- [x] 3. Repositório: `RepositorioRetencao` (interface) + `JdbcRepositorioRetencao` (JDBC sobre `veiculo_sumarizado` e `alerta`).
- [x] 4. Processamento: `ProcessadorRetencao` (lote), `JobAlertaRetencaoTrafego` (Quartz), `ExecutorTratamento` (automáticos).
- [x] 5. Servlet `TipoAlertaConfigServlet` (CRUD de config, tratamentos, tipos novos).
- [x] 6. Filtros avançados: `FiltroHistorico` (localidade, usuário, resultado) + alterações em `consulta.jsp` + CSV.
- [x] 7. Frontend: `notificacao.js` carrega cores do banco em vez de mapa fixo.
- [x] 8. Agendador: registro do job no `Agendador.verificarJobsGTW()`.
- [x] 9. Migração `20261016_alertas_operacionais.sql`.
- [x] 10. Testes TestNG: regra de retenção, processador, config e tratamentos.

## Critérios de aceite

1. `..\.setup-gtw\build.ps1` compila sem erro.
2. Testes: regra de retenção (consecutividade, severidade, tráfego zero), processador (geração, duplicata, falha parcial), config/tratamentos (enum, cor, filtro, parâmetros).
3. Migração executada (com autorização) e roteiro [`docs/testes/28-alertas-operacionais.md`](../testes/28-alertas-operacionais.md) no navegador.
4. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Fora do escopo

- Videodetecção e laço virtual (5.3.7.4 a–c): os dados de contagem, fluxo e ocupação já são fornecidos pelos equipamentos; a detecção por vídeo não faz parte do GTW.
- Mapa de calor de congestionamento em tempo real: a visualização no mapa fica para a Fase 8 (painéis).
- Retry de alertas em falha de comunicação (5.3.5.3): gap vermelho pendente de definição.
- Alertas de integridade no popup: o popup atual exige `INNER JOIN cad_veiculo_monitorado`; sem refatorar a procedure `spu_ObterNovosAlertasV2`, alertas sem veículo monitorado (integridade, congestionamento) não aparecem. Essa refatoração fica para a Fase 9 (acabamentos).
