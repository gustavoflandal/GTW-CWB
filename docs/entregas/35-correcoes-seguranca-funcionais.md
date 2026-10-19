# Entrega 35 — Correções de segurança e funcionais (fases 1 e 2)

## Origem

- [Plano 35](../planos-salvador/35-correcoes-seguranca-funcionais.md) — correções de segurança e funcionais das fases 1 e 2.
- [Guia de correções — Análise de Dados, KPIs, Gestão de Lotes e acessos](../referencia/guia-correcoes-analise-kpis-lotes.md) — texto de cada item (`SEG-*` e `FUN-*`).

## Escopo

Correção dos itens de segurança e funcionais das fases 1 e 2 do guia, nas telas novas do Muralha Digital (servlets de `muralha/digital/*`). O comportamento global de `Acesso` e as telas legadas não mudaram.

## O que mudou por item

| Item | O que mudou |
|---|---|
| **SEG-01** (verificação fail-open) | Nova classe `ControleAcesso` (`muralha.digital.acessos`) com verificação **estrita**: nega quando a tela não tem direito cadastrado, responde 401 (sessão expirada) ou 403 (sem permissão) em JSON e registra a negação na auditoria. Os 30 servlets dos módulos novos passaram a chamá-la no lugar de `Acesso.verificaAcesso`. `SsoAnaliseServlet.podeAbrirSuperset` agora usa a mesma verificação. |
| **SEG-03** (sem permissão por ação) | `LoteServlet` e `KpiServlet` exigem o direito registrado para o servlet em `sis_menu` (Lotes: menu 370; painel de KPIs: menu 371). Gravar ou listar a configuração de KPIs exige o direito do menu 372 (`/muralha-digital/pages/admin/kpis/index.jsp`). Quem não tem o direito recebe 403. A separação leitura/escrita da Gestão de Lotes não foi feita: todas as ações exigem o direito do menu 370. |
| **SEG-04** (SQL livre nos KPIs) | Novo `ValidadorSqlKpi`: aceita só uma consulta `SELECT`, recusa `;`, `INTO`, `EXEC`, `INSERT`, `UPDATE`, `DELETE`, `MERGE`, `DROP`, `ALTER`, `CREATE`, `GRANT`, `xp_` e `sp_`, e ignora o conteúdo de textos e identificadores delimitados (`'...'`, `[...]`, `"..."`). Usado ao salvar a configuração, no painel e no `KpiSnapshotJob`. A execução tem timeout de 30 s e a falha de um KPI é registrada no log. |
| **FUN-04** (KPIs sem snapshot) | Migração `20261019_kpis_corrige_consultas.sql` corrige o `query_sql` dos KPIs 5, 7, 8, 9 e 11, que usavam colunas inexistentes. É idempotente e grava o SQL anterior em `muralha.parametro_historico` (rollback). Registrada em [`migracoes/README.md`](../banco-de-dados/migracoes/README.md). |
| **FUN-03** (Gestão de Lotes) | Novo modal "Adicionar infrações" (lista as infrações disponíveis, com filtro e seleção) que chama `adicionarInfracoes`; a inclusão é transacional, só vale para lote em RASCUNHO e informa adicionados e ignorados. `enviar` recusa lote sem itens. `cancelar` e `enviar` só auditam quando alteram alguma linha. O botão "Enviar para DETRAN" passou a se chamar **"Marcar como enviado"**, e a confirmação avisa que a integração com o DETRAN ainda não está disponível. Regras em `LoteRegras`. |
| **SEG-10** (Tipos de Alerta) | `TipoAlertaConfigServlet` ganhou a ação `aparencia`, que só exige sessão e devolve apenas os campos do popup de notificação. As demais ações, inclusive `listar`, exigem o direito da tela. `notificacao.js` passou a chamar `acao=aparencia`. |
| **SEG-05** (XSS armazenado) | Todo valor vindo do servidor passou por `escaparHtml()` (ou `Number()`, ou lista de valores permitidos) nas telas de KPIs (painel e configuração), Relatórios Gerenciais e Log de Auditoria (`consulta-log.js`). A cor personalizada do KPI só vale se for um hexadecimal válido, e o histórico do KPI não usa mais `onclick` com o nome. |
| **SEG-09** (hash de senha) | `UsuarioServlet` não registra mais `senhaMD5` no log de debug nem a devolve na resposta do login. Nenhuma tela fora de `GtwWidgets/` usava o campo. |
| **FUN-01** (Relatório Gerencial) | `RelatorioGerencialJob` passou a consultar `dbo.sis_log_auditoria` (coluna `dt_operacao`) e a usar `desativado = 0` na disponibilidade. As consultas do job foram executadas no DEV sem erro. |
| **FUN-02** (Relatório mensal de pré-processamento) | `RelatorioPreProcMensalJob` foi reescrito sobre os dados existentes (`infracao_analise` e `vtr_status_analise`), sem colunas novas. Os volumes recebidos/processados/disponibilizados/descartados continuam ancorados na data da passagem. |
| **FUN-05** (cargas da camada `analise`) | Novo `CargaAnaliseJob` (Quartz), agendado em `Agendador` a cada 30 minutos, que chama as procedures `spu_carga_*`. A [seção "Recarga de tabelas materializadas"](../analise/guia-administrador.md) do guia do administrador descreve o job, a recarga manual e o log em `analise.carga_log`. |

## Como verificar

### Já verificado

- Compilação (`test-compile`) e testes TestNG das classes novas e alteradas (`ControleAcesso`, `ValidadorSqlKpi`, `LoteRegras`, `TipoAlertaAparencia`, `RelatorioPreProcMensalJob` e os servlets dos módulos novos): sem falhas.
- Migração `20261019_kpis_corrige_consultas.sql` executada no DEV; a segunda execução não altera nada. As consultas dos KPIs 5, 7, 8, 9 e 11 executam sem erro.
- Consultas de `RelatorioGerencialJob` e `RelatorioPreProcMensalJob` executadas no DEV em modo de leitura.

### Pendente de execução manual

1. **XSS no navegador (SEG-05).** Na Configuração de KPIs, criar um KPI de teste com o nome `<img src=x onerror=alert(1)>`. Abrir o painel de KPIs, a Configuração de KPIs e o histórico do KPI: o nome deve aparecer como texto, sem exibir alerta. Depois, **desativar** o KPI de teste.
2. **Roteamento de acesso dos Tipos de Alerta (SEG-10).** Com um usuário que **não** tem o direito da tela de Tipos de Alerta:
   - `GET /MuralhaDigital/TipoAlertaConfig?acao=listar` deve devolver **403**;
   - `GET /MuralhaDigital/TipoAlertaConfig?acao=aparencia` deve devolver **200**, e o popup de notificação deve continuar funcionando.
3. **Fluxo da Gestão de Lotes no navegador (FUN-03).** Em `/muralha-digital/pages/processamento/lotes/index.jsp`: criar um lote; abrir o lote em RASCUNHO; usar "Adicionar infrações" (filtrar, selecionar, confirmar) e conferir os itens; tentar "Marcar como enviado" em um lote vazio (deve ser recusado); marcar um lote com itens como enviado; cancelar um lote em RASCUNHO. Usuário sem o direito do menu 370 deve receber 403 nas ações.
4. **Acesso por usuário (SEG-01/SEG-03).** Um usuário logado sem o grupo da tela recebe 403 em `/MuralhaDigital/Lote?acao=listar` e no `POST` de configuração de KPI; um usuário do grupo 42 continua acessando.
5. **Relatórios e KPIs com dados.** Executar o `RelatorioGerencialJob` no DEV e conferir que existe uma linha em `muralha.relatorio_periodico` exibida na tela; conferir que o `KpiSnapshotJob` grava snapshot dos KPIs 5, 7, 8, 9 e 11.

## O que ficou de fora

- **Fases 3 e 4 do guia:** SEG-06 (RLS do Superset), FUN-06 a FUN-11, DES-01 (materialização), IMP-01 (tela de grupos e direitos) e os demais itens listados no §5 do guia.
- **Integração com o DETRAN:** o botão apenas marca o lote como enviado; não há envio real nem gravação de `CONFIRMADO`. Depende do layout e do canal definidos pelo contratante.
- **Chave estrangeira em `lote_infracao_item.id_infracao`:** a existência da infração é validada no código; não há migração com FK.
- **Separação de leitura e escrita na Gestão de Lotes** e login de banco somente leitura para os KPIs (pedido ao DBA).
- **Cron de recarga da camada `analise`:** se o agendamento externo continuar ativo, haverá carga duplicada com o job do GTW (ver o guia do administrador).

## Critérios de aceite

- [x] Build compila sem erros
- [x] Testes TestNG das classes alteradas passam
- [x] Migração `20261019_kpis_corrige_consultas.sql` executada no DEV
- [ ] Teste de XSS no navegador
- [ ] Teste de roteamento de acesso dos Tipos de Alerta
- [ ] Fluxo da Gestão de Lotes no navegador
