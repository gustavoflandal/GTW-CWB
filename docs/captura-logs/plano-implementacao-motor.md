# Motor de Captura de Logs — implementação sobre a base atual

## Origem
- Análise comparativa do módulo atual × especificação do motor: [`motor-captura-logs.md`](motor-captura-logs.md) e [`prompt-execucao-motor.md`](prompt-execucao-motor.md) (nesta pasta).
- Módulo pré-existente evoluído: `muralha.digital.auditoria` (Plano 01) + `sis_log_auditoria` (migração `20261008_log_auditoria.sql`).

## Decisão (reaproveitar e evoluir)
A especificação do motor descreve uma arquitetura *greenfield* poliglota (PostgreSQL/ClickHouse, Go, React, Kafka/Debezium) **incompatível** com as regras imutáveis do GTW (Java 13/JSP, SQL Server, sem novos frameworks). Em vez de construir do zero ou descartar o atual, **evoluímos o módulo de Auditoria** para a "Fase 1" do próprio motor, portada para o stack do GTW. O módulo de Logs Técnicos (`muralha.digital.logtecnico`) permanece como camada complementar de leitura.

### Escopo escolhido (decisões do solicitante — 2026-11-05)
1. **Abrangência:** todas as telas — Muralha Digital **e** legado GTW clássico (filtro em `/*`).
2. **Captura padrão:** tudo, inclusive leituras/aberturas de tela; ajustável por configuração. Ruído (estáticos, polling, websocket) vem excluído por regra.
3. **Integridade:** cadeia de hash SHA-256 encadeada (em tempo real) + selo diário.
4. **Limpeza:** expurgo configurável por política, **compatível** com a integridade (ver abaixo).

## Como integridade e limpeza coexistem
- A cadeia de hash impede **alteração/remoção sorrateira** de um registro no meio da base (quebra a verificação).
- O **expurgo** é limpeza autorizada, em lote, por política. Só remove eventos **já selados** (`selado=1`), isto é, de dias fechados. Como o selo é diário e a retenção é de muitos dias, nunca se apaga registro recente nem se quebra a cadeia viva.
- A **cadeia de selos** (diária, nunca apagada) preserva a verificabilidade do histórico mesmo após o expurgo. Quando a política pede `arquivar_antes`, os registros são exportados (CSV + arquivo `.selo` com o SHA-256 do CSV) antes de apagar.
- O próprio expurgo é auditado (`sis_log_expurgo` + evento SISTEMA na trilha).

## Arquitetura (o que foi construído)

### Banco (migração `20261105_motor_captura_logs.sql`)
- `dbo.sis_log_auditoria` evoluída: `metodo, rota, status_http, duracao_ms, tipo_captura, nivel, id_regra, dados_extra, hash_evento, hash_anterior, selado`.
- `dbo.sis_log_regra` — regras de captura configuráveis (+ 25 regras semente).
- `dbo.sis_log_selo` — selo diário de integridade.
- `dbo.sis_log_retencao` — políticas de retenção (+ 6 políticas semente).
- `dbo.sis_log_expurgo` — histórico/auditoria dos expurgos.
- Menus/cards/direitos: `20261105_menu_motor_captura_logs.sql` (telas 441–444, grupo 11).

### Java (`muralha.digital.auditoria`)
| Classe | Papel |
|---|---|
| `CapturaLogFilter` (`@WebFilter("/*")`) | Captura automática de toda requisição (todas as telas). Assíncrona; blindada por try/finally — nunca afeta a resposta. |
| `RespostaComStatus` | Wrapper para capturar o status HTTP (o servlet-api de compilação é anterior ao 3.0). |
| `AuditoriaService` | Núcleo de gravação. Fila de **thread única** → ordenação + cadeia de hash em tempo real. API manual preservada (`registrar`/`registrarSistema`) + `capturarAutomatico`. Eventos "garantidos" são síncronos. |
| `MotorRegras` | Cache das regras (recarregável sem redeploy); casa rota/método (curinga `%`). |
| `Mascarador` | Mascara CPF, placa e campos nomeados (senha/token…) + os definidos na regra. |
| `SeloService` | Gera selos diários e verifica a integridade (recalcula hashes e confere encadeamento). |
| `ExpurgoService` | Expurgo por política (só selados), arquivamento selado, registro em `sis_log_expurgo`. |
| `MotorLogListener` (`@WebListener`) | Aquece o cache e agenda o selo diário. Expurgo automático só com `-Dgtw.logs.expurgo.auto=true`. |
| `AuditoriaServlet` | Consulta evoluída (novos campos/filtros) + `acao=verificarIntegridade`. |
| `RegraLogServlet` / `RetencaoLogServlet` | Administração (grupo 11) de regras e de retenção/expurgo. |

### Telas (Bootstrap 5.3, padrão do projeto)
- `pages/auditoria/consulta-log.jsp` — evoluída: filtros Nível/Tipo, colunas (método, status, nível, tipo, hash), botão **Integridade**.
- `pages/admin/logs-regras/index.jsp` — CRUD das regras de captura (card em Cadastro e Configuração).
- `pages/admin/logs-retencao/index.jsp` — políticas de retenção, gerar selo, simular/executar expurgo, histórico.

## Eficiência
- Captura assíncrona (fila única, inserts sub-ms); só eventos garantidos aguardam.
- Ruído excluído por regra (estáticos/polling), evitando volume inútil mesmo capturando "tudo".
- Retenção/expurgo em lote (`DELETE TOP (5000)`), arquivamento em streaming.

## Legado GTW clássico (ressalva conhecida)
O filtro cobre o legado (rota + usuário/IP garantidos). As chamadas GWT-RPC aparecem com a rota genérica (`/GtwWidgets/...`) sem o nome fino da operação; a decodificação do payload GWT-RPC fica para fase posterior, se desejado.

## Status
- [x] Migrações aplicadas no `GTW_MURALHA_LOCAL` (schema + seeds + menus).
- [x] Código compila e empacota (`clean install` — BUILD SUCCESS).
- [x] **Validação em runtime (2026-11-05)**:
  - Captura automática em todas as telas (Muralha + legado), eventos AUTO com `hash_evento` encadeado.
  - Regras aplicadas: CSS/JS/polling excluídos; `/login/` = ACESSO (garantido); GET Muralha = LEITURA.
  - Integridade: `verificarIntegridade` → `integro:true` (eventos recalculados conferem).
  - Eventos autenticados resolvem `login`/`id_usuario`.
  - Telas (consulta, regras, retenção) renderizam (HTTP 200); CRUD de regras e simulação de expurgo via sessão admin (grupo 11) OK.
- Correção aplicada durante a validação: `MotorRegras` tolera falha de leitura no startup sem envenenar o cache (lazy-load na 1ª requisição, quando a conexão já está configurada); carga prematura removida do `MotorLogListener`.

### Tipos de evento semânticos (configuráveis por toda a aplicação)
- O motor classifica cada operação em: **ACESSO** (abrir tela), **VISUALIZACAO** (consulta/leitura), **CADASTRO** (inclusão), **ALTERACAO** (edição), **EXCLUSAO** (exclusão/inativação), **EXPORTACAO** e **ERRO**. A derivação usa o método HTTP + a `acao` (`AuditoriaService.derivarNivel`).
- Tabela `dbo.sis_log_tipo_evento` (migração `20261105_tipos_evento_log.sql`) permite ligar/desligar a captura de cada tipo **globalmente, para todas as telas**; cache em `TiposEvento`. As regras gerais passaram a nível `AUTO` para a classificação agir.
- A tela *Captura de Logs — Regras* ganhou um painel no topo com os tipos de evento (liga/desliga) + a tabela de regras avançadas por rota abaixo.
- Validado: `inserir`→CADASTRO, `atualizar`→ALTERACAO, `encerrar`→EXCLUSAO, `obterLista`→VISUALIZACAO, abrir tela→ACESSO; desligar um tipo interrompe a captura dele em todo o sistema.

### Detalhe do log (antes/depois) — parcial
- Na tela *Log de Auditoria*, clicar numa linha abre um modal de detalhe. Para operações de escrita (CADASTRO/ALTERACAO/EXCLUSAO) o motor captura o **"depois"** (dados enviados, mascarados) em `dados_extra` no formato `{"depois":{...}}`, e o modal mostra o layout **Antes | Depois**.
- **Pendente (adiado a pedido do usuário):** a captura do **"antes"** (valor anterior do registro). O filtro é genérico e não conhece a tabela/registro de cada tela. Opções avaliadas para retomar: (A) snapshot por regra — admin informa tabela/PK por tela e o motor lê o registro antes/depois (recomendado, exato, vinculado ao usuário/evento); (B) triggers no banco (automático, porém correlação frágil com pool); (C) instrumentação por módulo. Por ora, o "antes" aparece como "não disponível".

### Regra absoluta: banco somente em localhost
`com.consilux.lib.Conexao.validarHostLocal` recusa (lança `ConfiguracaoException`) qualquer host que não seja loopback, mesmo que configurado no `confGTW.xml`. O sistema nunca conecta a banco remoto.

### Acesso a telas (requisito: registrar o acesso mesmo sem ação)
- A abertura de qualquer tela (GET de página `.jsp`) é registrada como nível **ACESSO**, com a funcionalidade = nome da tela (pasta), via a regra "Acesso a tela (abertura de pagina)" (`20261105_regra_acesso_telas.sql`, ordem 95).
- O `CapturaLogFilter` envia `Cache-Control: no-store` nas navegações HTML (GET com `Accept: text/html`), impedindo o navegador de servir a tela do cache — assim **toda reabertura chega ao servidor e é registrada**, mesmo que nada seja feito na tela.
- Validado: reabrir a mesma tela gera um novo evento ACESSO a cada abertura.

## Como usar / configurar
- **Regras:** Cadastro e Configuração → *Captura de Logs — Regras*. Primeira regra (por ordem) que casa decide. `%` é curinga.
- **Retenção/Expurgo:** Cadastro e Configuração → *Retenção e Expurgo de Logs*.
- **Expurgo automático:** desligado por padrão; ligar com `-Dgtw.logs.expurgo.auto=true`.
- **Diretório de arquivamento:** `-Dgtw.logs.expurgo.dir=<caminho>` (padrão: `%TEMP%/gtw-expurgo-logs`).
