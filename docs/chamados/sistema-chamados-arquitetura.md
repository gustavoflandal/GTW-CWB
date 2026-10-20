# Sistema de Chamados — Arquitetura

**Data:** 2026-10-15
**Módulo:** Muralha Digital — módulo `muralha.digital.chamado`
**Status:** Implementado (2026-10-15). Este documento descreve a arquitetura vigente, já com os complementos de administração (perfil `ADMIN`, parâmetros de SLA, perfis de acesso, categoria e ícones) e de SMTP, e com os ajustes da revisão final.
**Plano de implementação:** [`22-sistema-chamados.md`](../planos-salvador/22-sistema-chamados.md)
**Documentos relacionados:** [entrega](../entregas/22-sistema-chamados.md) · [roteiro de testes](../testes/22-sistema-chamados.md) · [manual do usuário](manual-sistema-chamados.md) · [sessões](sessoes/README.md)

> Onde o texto original foi alterado pelos complementos, a nota "(vigente)" indica o comportamento atual. As seções "Complemento" e "Ajustes da revisão final", no fim do documento, prevalecem quando houver divergência.

## Origem

- **Edital:** planilha [`Atividades Edital Salvador 2026.xlsx`](../../docs-editais/edital-salvador/Atividades%20Edital%20Salvador%202026.xlsx), aba **"Sist. Chamado"**, linhas 2–7 (itens 7.2 a 7.2.5).
- **Itens cobertos:** **7.2.3** (a–f) e **7.2.4**.
- **Itens relacionados, fora deste escopo:** 7.2.1 (central 24x7 — obrigação de serviço), 7.2.2.a (telefone), 7.2.2.c (abertura por e-mail), 7.2.2.d (mensageria para críticos), 7.2.5 (preposto).
- **Lacuna documental:** o Capítulo 7 do TR não está no repositório (`docs-editais/edital-salvador/` só contém Cap. 5, Cap. 21 e Anexo A). Não há tabela oficial de prioridades × prazos nem lista de categorias; por decisão do responsável, ambos ficam **configuráveis**, com valores iniciais sugeridos (§2.3).

### Texto dos requisitos (planilha, aba "Sist. Chamado")

> **7.2.3** — O sistema de gerenciamento de chamados deverá permitir à CONTRATANTE:
> a) abertura de chamados com classificação de prioridade;
> b) acompanhamento em tempo real do status de cada chamado;
> c) registro e consulta do histórico integral de chamados;
> d) geração de relatórios de atendimento por período, categoria e equipamento;
> e) avaliação da qualidade do atendimento recebido;
> f) exportação dos registros em formatos PDF, CSV e XLS.
>
> **7.2.4** — Todos os chamados abertos deverão receber número de protocolo único, com confirmação automática ao solicitante contendo o número de protocolo, a classificação de prioridade atribuída e o prazo previsto para atendimento e resolução.

## Decisões de design (tomadas com o responsável em 2026-10-15)

| # | Tema | Decisão |
|---|---|---|
| D1 | Plataforma | Módulo **integrado à Muralha Digital** (mesmo login, padrão visual, vínculo com `dbo.local`). Ferramenta externa descartada. |
| D2 | Operação | CONTRATANTE **e** CONTRATADA usam o mesmo sistema, com perfis distintos. |
| D3 | Escopo | 7.2.3 (a–f) + 7.2.4. |
| D4 | Prioridades/prazos/categorias | Configuráveis por tela; valores iniciais sugeridos. |
| D5 | Relógio de SLA | **Horas corridas 24x7**; pausa somente em `AGUARDANDO_SOLICITANTE` e entre `RESOLVIDO` e uma reabertura. |
| D6 | Prioridade | Solicitante sugere; equipe técnica pode **reclassificar com justificativa**; prazos recalculados; solicitante avisado por e-mail. |
| D7 | Encerramento | `RESOLVIDO` → solicitante **avalia (nota 1–5 + comentário) e fecha**, ou **reabre**; sem ação em N dias → fecha automaticamente como "não avaliado". |
| D8 | Tempo real | **Polling de 30 s** nas telas de lista e detalhe. Sem WebSocket. **Sem e-mail por mudança de status.** |
| D9 | Visibilidade | Solicitante vê **só os próprios**; **Gestor do contrato** (CONTRATANTE) vê todos; equipe técnica vê todos. |
| D10 | Categorias | Configuráveis, dois níveis (categoria/subcategoria); cada categoria indica se **exige equipamento**. |
| D11 | Extras | **Anexos** (imagens/PDF) e **alerta de SLA em risco/vencido**. Fora: comentários livres/notas internas, chamado automático por indisponibilidade. |
| D12 | Interação | Toda transição exige **observação** (vai ao histórico). Solicitante tem a ação **"Responder"** (texto + anexos) quando o chamado aguarda retorno. |
| D13 | Exportação | **CSV e XLSX no servidor** (`ExportacaoService` + rodapé SHA-256 + auditoria); **PDF no cliente** (`export-table.js`/jsPDF). Padrão do Plano 11. |
| D14 | E-mails | Somente **confirmação de abertura** (7.2.4) e **reclassificação de prioridade**, via fila + job. |

## 1. Perfis, ciclo de vida e SLA (seção aprovada)

### 1.1 Perfis

Três grupos novos em `dbo.sis_grupo` (107–109, abaixo do grupo 11) mais o grupo de administradores já existente. Os IDs dos grupos ficam em `muralha.config_chave_valor` (`chamado_grupo_solicitante`, `chamado_grupo_gestor`, `chamado_grupo_equipe`, `chamado_grupo_admin`) para não depender de IDs fixos no código. O perfil é resolvido no servlet a partir de `dbo.sis_usuario_grupo` — **não** apenas pela URL.

| Perfil | Lado | Permissões |
|---|---|---|
| `SOLICITANTE` | CONTRATANTE | Abrir; ver **apenas os próprios**; responder; avaliar/fechar/reabrir os próprios; cancelar os próprios enquanto `ABERTO`; exportar os próprios |
| `GESTOR` | CONTRATANTE | Tudo do solicitante + ver **todos**, relatórios e exportação geral |
| `EQUIPE` | CONTRATADA | Ver todos; assumir; mudar status; reclassificar prioridade; cancelar com justificativa; relatórios (vigente: **não** configura mais) |
| `ADMIN` | Administração | Grupo em `chamado_grupo_admin` (padrão 11, Administradores). **Único** perfil que acessa a Configuração (prioridades e prazos, categorias, parâmetros de SLA, perfis de acesso, SMTP). Vê todos os chamados e os relatórios em modo somente leitura: sozinho não executa nenhuma ação de atendimento (`MaquinaEstados` não o reconhece) |

Usuário em mais de um grupo recebe a **união** das permissões. Usuário sem nenhum dos perfis é recusado (a negação responde HTTP 200 com `{ok:false}`; ver "Ajustes da revisão final").

### 1.2 Estados e transições

```
ABERTO ──► EM_ATENDIMENTO ◄──► AGUARDANDO_SOLICITANTE (relógio pausado)
   │             │
   │             ▼
   │         RESOLVIDO ──(avaliar 1–5 + comentário)──► FECHADO
   │             │  └──(sem ação em N dias)──────────► FECHADO (fechado_automaticamente = 1)
   │             └──(reabrir, com justificativa)─────► EM_ATENDIMENTO
   └──► CANCELADO
```

| Ação | De | Para | Quem | Observação |
|---|---|---|---|---|
| `abrir` | — | `ABERTO` | SOLICITANTE, GESTOR | descrição obrigatória |
| `assumir` | `ABERTO`, `EM_ATENDIMENTO` | (mantém; define responsável) | EQUIPE | opcional |
| `iniciar` | `ABERTO` | `EM_ATENDIMENTO` | EQUIPE | obrigatória; marca `dt_primeiro_atendimento` |
| `aguardar` | `EM_ATENDIMENTO` | `AGUARDANDO_SOLICITANTE` | EQUIPE | obrigatória (o que se pede ao solicitante); inicia pausa |
| `responder` | `AGUARDANDO_SOLICITANTE` | `EM_ATENDIMENTO` | autor (ou GESTOR) | obrigatória + anexos opcionais; encerra pausa |
| `resolver` | `EM_ATENDIMENTO` | `RESOLVIDO` | EQUIPE | obrigatória (solução); marca `dt_resolucao`; inicia pausa |
| `avaliar` | `RESOLVIDO` | `FECHADO` | autor | nota 1–5 obrigatória, comentário opcional |
| `reabrir` | `RESOLVIDO` | `EM_ATENDIMENTO` | autor | justificativa obrigatória; encerra pausa; limpa `dt_resolucao` |
| `cancelar` | `ABERTO` | `CANCELADO` | autor | obrigatória |
| `cancelar` | `ABERTO`, `EM_ATENDIMENTO`, `AGUARDANDO_SOLICITANTE` | `CANCELADO` | EQUIPE | justificativa obrigatória |
| `reclassificar` | qualquer não terminal | (mantém) | EQUIPE | justificativa obrigatória; recalcula prazos; gera e-mail |
| *(job)* fechar automático | `RESOLVIDO` há ≥ N dias | `FECHADO` | sistema | `fechado_automaticamente = 1` |

Estados terminais: `FECHADO`, `CANCELADO`. Toda ação grava uma linha em `chamado_historico` e chama `AuditoriaService.registrar`.

A transição é aplicada com `UPDATE … WHERE id = ? AND status = ?` (controle otimista): se nenhuma linha for afetada, outro usuário alterou o chamado e a operação retorna erro amigável "Chamado alterado por outro usuário; recarregue".

### 1.3 SLA (24x7, horas corridas)

- `dt_prazo_atendimento = dt_abertura + minutos_atendimento(prioridade)`. Cumprido se `dt_primeiro_atendimento <= dt_prazo_atendimento`.
- `dt_prazo_resolucao = dt_abertura + minutos_resolucao(prioridade) + minutos_pausados`. Cumprido se `dt_resolucao <= dt_prazo_resolucao`.
- **Pausa:** ao entrar em `AGUARDANDO_SOLICITANTE` ou `RESOLVIDO`, grava `dt_inicio_pausa`; ao sair (para `EM_ATENDIMENTO`), soma a diferença em `minutos_pausados`, zera `dt_inicio_pausa` e **estende o `dt_prazo_resolucao` gravado** pelo tempo pausado. A pausa de `RESOLVIDO` só tem efeito quando há reabertura.
- **Reclassificação:** recalcula os dois prazos a partir de `dt_abertura` original com os minutos da nova prioridade (mantendo `minutos_pausados`).
- **Marcas** (`sla_atendimento`, `sla_resolucao`): `NO_PRAZO` → `EM_RISCO` (consumido ≥ `chamado_sla_risco_percentual`, padrão 80%, configurável de 1 a 99) → `VENCIDO` (prazo passou e ainda não cumprido); ao cumprir, vira `CUMPRIDO` ou `VIOLADO`, marcas **definitivas** que o job e as transições seguintes não alteram. Atualizadas na própria transição e pelo `ChamadoSlaJob` a cada 5 minutos.
- Enquanto pausado, a marca de resolução não evolui.

### 1.4 Protocolo e confirmação (7.2.4)

- Formato `CH-AAAA-NNNNNN` (ex.: `CH-2026-000123`), contador reiniciado a cada ano.
- Gerado na mesma transação da abertura, a partir de `muralha.chamado_protocolo (ano, ultimo)` com `UPDATE … WITH (UPDLOCK, HOLDLOCK)` (insere a linha do ano se não existir). `UNIQUE` em `chamado.protocolo` garante a unicidade.
- *Observação:* `SEQUENCE` do SQL Server não reinicia por ano; por isso o contador em tabela.
- Ao abrir: (1) a tela exibe SweetAlert com protocolo, prioridade e os dois prazos; (2) enfileira e-mail `CONFIRMACAO_ABERTURA` em `chamado_notificacao` para o e-mail do solicitante (`dbo.sis_usuario.email`). Ao reclassificar: enfileira `RECLASSIFICACAO` com a nova prioridade e os novos prazos.
- Solicitante sem e-mail cadastrado: a notificação é registrada com `status = 'ERRO'` e `erro = 'Solicitante sem e-mail'`; a confirmação na tela continua valendo.

## 2. Modelo de dados e telas (seção aprovada)

### 2.1 Tabelas (schema `muralha`)

Migração única e idempotente `docs/banco-de-dados/migracoes/<AAAAMMDD>_sistema_chamados.sql`, sem `ALTER` em tabelas existentes, com rollback comentado. Convenções do projeto: `DATETIME2(3)` com `SYSDATETIME()`, prefixo `dt_`, `id_usuario_*` sem FK declarada, status como `VARCHAR` em maiúsculas.

**`muralha.chamado_prioridade`**
| Coluna | Tipo | Nota |
|---|---|---|
| `id` | INT IDENTITY PK | |
| `codigo` | VARCHAR(20) UNIQUE | CRITICA, ALTA, MEDIA, BAIXA |
| `nome` | NVARCHAR(50) | |
| `ordem` | TINYINT | 1 = mais grave |
| `minutos_atendimento` | INT | > 0 |
| `minutos_resolucao` | INT | ≥ minutos_atendimento |
| `cor` | VARCHAR(7) | hex para badge |
| `ativo` | BIT DEFAULT 1 | |

**`muralha.chamado_categoria`**
| Coluna | Tipo | Nota |
|---|---|---|
| `id` | INT IDENTITY PK | |
| `nome` | NVARCHAR(100) | |
| `id_pai` | INT NULL FK → `chamado_categoria.id` | NULL = categoria; preenchido = subcategoria (máx. 2 níveis) |
| `exige_equipamento` | BIT DEFAULT 0 | herdado pela subcategoria |
| `ativo` | BIT DEFAULT 1 | |

**`muralha.chamado`**
| Coluna | Tipo | Nota |
|---|---|---|
| `id` | BIGINT IDENTITY PK | |
| `protocolo` | VARCHAR(20) NOT NULL UNIQUE | |
| `titulo` | NVARCHAR(150) NOT NULL | |
| `descricao` | NVARCHAR(MAX) NOT NULL | |
| `id_categoria` | INT NOT NULL FK | subcategoria ou categoria folha |
| `id_prioridade` | INT NOT NULL FK | vigente |
| `id_prioridade_solicitada` | INT NOT NULL FK | escolhida na abertura |
| `status` | VARCHAR(30) NOT NULL | CHECK nos 6 valores |
| `origem` | VARCHAR(20) DEFAULT 'PORTAL' | reservado para canais futuros (7.2.2) |
| `id_local` | INT NULL | equipamento (`dbo.local`, `sequencia_local = 1`) |
| `id_usuario_solicitante` | INT NOT NULL | |
| `id_usuario_responsavel` | INT NULL | técnico que assumiu |
| `dt_abertura` | DATETIME2(3) DEFAULT SYSDATETIME() | |
| `dt_prazo_atendimento`, `dt_prazo_resolucao` | DATETIME2(3) NOT NULL | |
| `dt_primeiro_atendimento`, `dt_resolucao`, `dt_fechamento` | DATETIME2(3) NULL | |
| `minutos_pausados` | INT DEFAULT 0 | |
| `dt_inicio_pausa` | DATETIME2(3) NULL | |
| `sla_atendimento`, `sla_resolucao` | VARCHAR(10) DEFAULT 'NO_PRAZO' | NO_PRAZO, EM_RISCO, VENCIDO, CUMPRIDO, VIOLADO |
| `avaliacao_nota` | TINYINT NULL | CHECK 1–5 |
| `avaliacao_comentario` | NVARCHAR(1000) NULL | |
| `dt_avaliacao` | DATETIME2(3) NULL | |
| `fechado_automaticamente` | BIT DEFAULT 0 | |
| `dt_atualizacao` | DATETIME2(3) DEFAULT SYSDATETIME() | usada pelo polling (`desde`) |

Índices: `(status, dt_abertura)`, `(id_usuario_solicitante, dt_abertura)`, `(id_categoria)`, `(id_local)`, `(dt_atualizacao)`.

**`muralha.chamado_historico`** — `id` BIGINT IDENTITY PK, `id_chamado` BIGINT FK, `dt_evento` DATETIME2(3), `id_usuario` INT NULL (NULL = sistema), `tipo` VARCHAR(20) (ABERTURA, TRANSICAO, RESPOSTA, RECLASSIFICACAO, ATRIBUICAO, AVALIACAO, SLA), `status_anterior` VARCHAR(30) NULL, `status_novo` VARCHAR(30) NULL, `texto` NVARCHAR(2000) NULL. Índice `(id_chamado, dt_evento)`.

**`muralha.chamado_anexo`** — `id` BIGINT IDENTITY PK, `id_chamado` BIGINT FK, `id_historico` BIGINT NULL FK, `nome_original` NVARCHAR(255), `caminho` NVARCHAR(400) (relativo ao diretório configurado), `tipo_mime` VARCHAR(100), `tamanho_bytes` BIGINT, `sha256` CHAR(64), `dt_envio` DATETIME2(3), `id_usuario` INT.

**`muralha.chamado_notificacao`** — `id` BIGINT IDENTITY PK, `id_chamado` BIGINT FK, `tipo` VARCHAR(30) (CONFIRMACAO_ABERTURA, RECLASSIFICACAO), `destinatario` VARCHAR(200) NULL, `status` VARCHAR(10) DEFAULT 'PENDENTE' (PENDENTE, ENVIADO, ERRO), `tentativas` TINYINT DEFAULT 0, `dt_criacao`, `dt_envio` NULL, `erro` NVARCHAR(500) NULL. Índice `(status, dt_criacao)`.

**`muralha.chamado_protocolo`** — `ano` SMALLINT PK, `ultimo` INT NOT NULL.

### 2.2 Parâmetros (`muralha.config_chave_valor`, colunas `chave`/`valor` — sem `descricao`, ver errata §5)

| Chave | Valor inicial |
|---|---|
| `chamado_sla_risco_percentual` | `80` |
| `chamado_fechamento_auto_dias` | `5` (configurável de 1 a 60) |
| `chamado_diretorio_anexos` | `C:\Consilux\MuralhaDigital\chamados` (ajustar por ambiente) |
| `chamado_anexo_max_mb` | `10` |
| `chamado_email_ativo` | `1` |
| `chamado_grupo_solicitante`, `chamado_grupo_gestor`, `chamado_grupo_equipe` | IDs dos grupos criados pelo seed |
| `chamado_grupo_admin` | `11` (grupo Administradores; criada pela migração `_admin`) |
| `chamado_smtp_host`, `_porta`, `_usuario`, `_senha`, `_remetente`, `_starttls` | Vazias até o administrador configurar (migração `_smtp`; ver complemento de SMTP) |

### 2.3 Seed (script separado `<AAAAMMDD>_sistema_chamados_seed.sql`)

**Prioridades** (minutos atendimento / resolução): Crítica 60 / 240 · Alta 120 / 480 · Média 240 / 1440 · Baixa 480 / 4320.

**Categorias** (✱ = exige equipamento):
- Equipamento ✱ → Falha de captura/OCR · Falha de comunicação · Falha de energia · Dano físico/vandalismo · Outros
- Sistema → Erro de funcionalidade · Lentidão · Indisponibilidade · Outros
- Acesso/usuário → Criação/bloqueio de usuário · Permissões · Senha/MFA
- Integração → Falha de integração externa · Outros
- Dúvida/solicitação → Dúvida de uso · Solicitação de melhoria · Outros

**Grupos** em `dbo.sis_grupo` ("Chamados – Solicitante", "Chamados – Gestor do contrato", "Chamados – Equipe técnica"), com IDs livres consultados no próprio script (`sis_grupo.id_grupo` não é IDENTITY) e gravados em `config_chave_valor`.

**Menus** em `sis_menu` / `sis_menu_infos` / `sis_menu_direitos` para as telas da §2.4 (próximos IDs livres a confirmar em `docs/referencia/menus-e-permissoes.md`; padrão de `20261009_menu_modulos_desenvolvidos.sql`). Referência 7.2.3/7.2.4 em `ReferenciaEdital`.

### 2.4 Telas (`src/main/webapp/muralha-digital/pages/chamados/`)

Todas incluem `utils/credenciais/cabecalho_bootstrap_simples.jsp` e seguem `docs/requisitos/05-padrao-visual.md` (Bootstrap 5.3, Bootstrap Icons, SweetAlert2). Todo texto vindo do servidor é inserido com escape (`textContent` ou função `esc()`), nunca por `innerHTML` cru.

1. **`index.jsp` — Chamados**
   - Filtros: protocolo, status, prioridade, categoria, equipamento, período de abertura.
   - Tabela: protocolo, título, categoria, equipamento, prioridade (badge na cor da prioridade), status (badge), responsável, abertura, badge de SLA (verde no prazo, amarelo em risco, vermelho vencido/violado, cinza cumprido).
   - Paginação no servidor (`OFFSET/FETCH`, 50 por página).
   - Polling de 30 s (`acao=listar&desde=<dt_atualizacao máx.>`); linhas alteradas piscam brevemente.
   - Botões: "Novo chamado" (SOLICITANTE ou GESTOR), "Relatórios" (GESTOR, EQUIPE ou ADMIN), "Configuração" (só ADMIN), exportar **CSV / XLS / PDF** (do conjunto filtrado).
   - Alcance pelo perfil (D9).
2. **Modal "Novo chamado"** (em `index.jsp`)
   - Categoria → subcategoria; combo de equipamento aparece e torna-se obrigatório quando a categoria exige.
   - Prioridade (com os prazos exibidos ao lado), título, descrição, anexos (múltiplos).
   - Ao salvar: SweetAlert com protocolo, prioridade e prazos previstos.
3. **`detalhe.jsp?protocolo=`**
   - Cabeçalho: protocolo, status, prioridade (solicitada × vigente se diferente), categoria, equipamento, solicitante, responsável, prazos com contador regressivo e badge de SLA.
   - Linha do tempo do histórico (mais recente no topo) com anexos ligados a cada evento.
   - Botões conforme perfil × status (tabela §1.2); cada ação abre SweetAlert com campo de observação obrigatório (e upload em "Responder"); "Avaliar e fechar" usa estrelas 1–5 + comentário.
   - Polling de 30 s.
4. **`relatorios.jsp`** (GESTOR, EQUIPE e ADMIN)
   - Filtros: período, categoria, equipamento, prioridade; agrupamento por **período (dia/semana/mês)**, **categoria**, **equipamento** ou **prioridade**.
   - Indicadores por grupo: quantidade aberta, resolvida, cancelada; % SLA de atendimento cumprido; % SLA de resolução cumprido; tempo médio de atendimento (TMA) e de resolução (TMR, descontando pausas); nota média; % avaliados.
   - Cards de totais + gráfico Chart.js 4.4.1 (cdnjs) + tabela; exportação CSV / XLS / PDF.
5. **`configuracao.jsp`** (somente `ADMIN`, vigente)
   - Cinco abas: **Parâmetros de SLA**, **Perfis de acesso**, **Prioridades e prazos**, **Categorias** e **E-mail (SMTP)**. Edição em modal; desativação lógica (`ativo = 0`); não exclui registros referenciados.

## 3. Back-end (aguardando revisão)

### 3.1 Pacote `src/main/java/muralha/digital/chamado/`

Unidades com responsabilidade única, para que regras de negócio sejam testáveis sem banco:

| Classe | Responsabilidade | Depende de |
|---|---|---|
| `StatusChamado` (enum) | Estados e se são terminais | — |
| `AcaoChamado` (enum) | Ações da tabela §1.2 | — |
| `PerfilChamado` (enum) + `PerfisUsuario` | Conjunto de perfis do usuário | — |
| `MaquinaEstados` | Dado status atual, ação e perfis → status destino ou erro (pura, sem I/O) | enums |
| `CalculadoraSla` | Prazos, pausa, reclassificação, marcas NO_PRAZO/EM_RISCO/… (pura; recebe `Clock`) | — |
| `GeradorProtocolo` | Formata `CH-AAAA-NNNNNN` e incrementa contador na transação recebida | `Connection` |
| `ChamadoDao` | SQL parametrizado de `chamado`, `chamado_historico`, `chamado_anexo`, consultas de lista/relatório | `Conexao` |
| `ChamadoConfigDao` | Prioridades, categorias, parâmetros de `config_chave_valor`, resolução de perfis por grupo | `Conexao` |
| `ChamadoService` | Orquestra cada ação em **uma transação** (`setAutoCommit(false)`): valida (máquina de estados + perfil + visibilidade), atualiza com controle otimista, grava histórico, enfileira notificação, chama auditoria | DAOs, `MaquinaEstados`, `CalculadoraSla`, `GeradorProtocolo`, `AuditoriaService` |
| `ArmazenamentoAnexo` | Valida e grava/lê arquivos (§3.4) | `HashUtil` |
| `ExportacaoChamados` | Monta CSV/XLSX com `ExportacaoService` + rodapé SHA-256 | `ExportacaoService`, `HashUtil` |
| `EmailChamado` | Monta HTML da confirmação/reclassificação e envia por SMTP com JavaMail próprio (vigente; ver complemento de SMTP) | `ConfiguracaoSmtpChamado` |
| `CifraSegredo` | Cifra e decifra a senha do SMTP (AES/GCM, pura) | JDK |
| `ConfiguracaoSmtpChamado` | Resolve o SMTP efetivo (chaves `chamado_smtp_*` ou `confGTW.xml`) e valida os campos | `Connection`, `CifraSegredo` |
| `ChamadoServlet` | `/MuralhaDigital/Chamado` — operações e consultas do chamado | `ChamadoService` |
| `ChamadoRelatorioServlet` | `/MuralhaDigital/ChamadoRelatorio` — indicadores e exportação | `ChamadoDao`, `ExportacaoChamados` |
| `ChamadoConfigServlet` | `/MuralhaDigital/ChamadoConfig` — prioridades, categorias, parâmetros de SLA, perfis de acesso e SMTP (`ADMIN`) | `ChamadoConfigDao`, `ConfiguracaoSmtpChamado`, `CifraSegredo` |
| `ChamadoSlaJob` | A cada 5 min: atualiza marcas de SLA; fecha automaticamente `RESOLVIDO` antigos | `ChamadoService`, `CalculadoraSla` |
| `ChamadoEmailJob` | A cada 1 min: envia `chamado_notificacao` pendentes (até 5 tentativas) | `EmailChamado` |

### 3.2 Contrato HTTP

Todas as respostas JSON (Gson) no formato `{ "ok": true, ... }` ou `{ "ok": false, "erro": "mensagem" }`.

**`/MuralhaDigital/Chamado`**
| Método | `acao` | Parâmetros | Retorno |
|---|---|---|---|
| GET | `perfil` | — | `{perfis:[...], idUsuario}` |
| GET | `listar` | filtros, `pagina`, `desde` (opcional) | `{itens:[...], total, servidorAgora}` |
| GET | `detalhe` | `protocolo` | `{chamado, historico:[...], anexos:[...], acoesPermitidas:[...]}` |
| GET | `opcoes` | — | `{prioridades, categorias, equipamentos}` (equipamentos de `local_vigente`, `desativado = 0`) |
| GET | `anexo` | `id` | arquivo (stream), após checar visibilidade do chamado |
| POST (multipart) | `abrir` | `titulo`, `descricao`, `idCategoria`, `idPrioridade`, `idLocal?`, `arquivos[]` | `{protocolo, prioridade, prazoAtendimento, prazoResolucao}` |
| POST (multipart) | `responder` | `protocolo`, `texto`, `arquivos[]` | `{ok}` |
| POST | `assumir`, `iniciar`, `aguardar`, `resolver`, `cancelar`, `reabrir` | `protocolo`, `texto` | `{ok}` |
| POST | `reclassificar` | `protocolo`, `idPrioridade`, `texto` | `{prazoAtendimento, prazoResolucao}` |
| POST | `avaliar` | `protocolo`, `nota`, `comentario?` | `{ok}` |

**`/MuralhaDigital/ChamadoRelatorio`** — GET `indicadores` (filtros + `agrupamento`), GET `exportar` (`formato=csv|xlsx`, `origem=lista|relatorio` + filtros). PDF é gerado no cliente a partir da tabela exibida.

**`/MuralhaDigital/ChamadoConfig`** (todas as ações exigem `ADMIN`) — GET `listar`, `parametros`, `perfis`, `buscarUsuarios`, `smtp`; POST `salvarPrioridade`, `salvarCategoria`, `alternarAtivo`, `salvarParametros`, `adicionarMembro`, `removerMembro`, `salvarSmtp`, `testarSmtp`.

### 3.3 Segurança

- Todo servlet: sessão (`SessaoConstantes.SESSAO_USUARIO`, 401 se ausente) + `new Acesso(req, resp, true).verificaAcesso(false)` + checagem de perfil por ação (a negação responde 200 com `{ok:false, erro}`, não 403).
- **Visibilidade no SQL:** para usuário só SOLICITANTE, toda consulta (lista, detalhe, anexo, exportação, relatório) recebe `AND c.id_usuario_solicitante = ?`. Protocolo de outro usuário retorna 404 (não revela existência).
- Rotas cadastradas em `sis_menu` (evita o *fail-open* de `fcn_VerificaAcesso`, ver `12-riscos` S4).
- SQL sempre parametrizado; filtros dinâmicos montados com lista branca de colunas.
- Textos limitados ao tamanho das colunas; HTML escapado no cliente.

### 3.4 Anexos

- Extensões permitidas: `pdf`, `png`, `jpg`, `jpeg`; conferência do tipo pelo conteúdo (assinatura do arquivo), não só pela extensão.
- Tamanho máximo por arquivo: `chamado_anexo_max_mb`; até 5 arquivos por envio.
- Gravados **fora do webapp**, em `<chamado_diretorio_anexos>/<ano>/<protocolo>/<uuid>.<ext>`; o nome original fica só no banco. SHA-256 calculado na gravação.
- Download apenas via `acao=anexo` (com checagem de visibilidade), `Content-Disposition: attachment`.
- Falha ao gravar algum anexo (diretório inexistente, sem permissão, disco cheio): a operação inteira (`abrir` ou `responder`) é revertida — transação desfeita e arquivos já gravados naquela requisição removidos — e o usuário recebe "Não foi possível gravar os anexos; tente novamente". Operações sem anexos não dependem do diretório.

### 3.5 Jobs Quartz

Registrados em `Agendador.java` seguindo o padrão `possuiJob` usado pelos planos 07/10/18.
- `ChamadoSlaJob` — a cada 5 min. Atualiza `sla_atendimento`/`sla_resolucao` dos chamados não terminais (uma consulta por lote, não por chamado) e grava histórico tipo `SLA` só quando a marca muda para `EM_RISCO` ou `VENCIDO`. Fecha `RESOLVIDO` com `dt_inicio_pausa` há ≥ `chamado_fechamento_auto_dias` dias.
- `ChamadoEmailJob` — a cada 1 min, só se `chamado_email_ativo = 1`. Lê até 50 pendentes, envia, marca `ENVIADO` ou incrementa `tentativas` (após 5 → `ERRO`). Usa o SMTP resolvido por `ConfiguracaoSmtpChamado` (vigente) e o remetente configurado (não o fixo `admin@consilux.com`).

### 3.6 Exportação (7.2.3.f)

- **CSV**: UTF-8 com BOM, separador `;`, cabeçalho `# Sistema de Chamados — <filtros> — <data/hora> — <usuário>`, rodapé `# Total: N` e `# SHA-256: <hash do conteúdo>`.
- **XLSX**: `ExportacaoService.exportarXlsx` (Apache POI SXSSF, já no `pom.xml`), aba "Chamados" + aba "Metadados" (filtros, data, usuário, total, SHA-256).
- **PDF**: `GerarRelatorioPDF` de `utils/export-table.js` (jsPDF + autotable), título e filtros no cabeçalho.
- Toda exportação gera `AuditoriaService.registrar(req, "Chamado", "EXPORTAR", formato, filtros)`. A exportação respeita a visibilidade do perfil.

### 3.7 Testes

- **Unitários (TestNG, padrão de `src/test/java/muralha/digital/dashboards/consulta/`)**:
  - `MaquinaEstadosTest` — toda célula da tabela §1.2 (permitidas e negadas por perfil).
  - `CalculadoraSlaTest` — prazos por prioridade, pausa/retomada, reabertura, reclassificação, limiar de risco, `CUMPRIDO` × `VIOLADO`, com `Clock` fixo.
  - `GeradorProtocoloTest` — formatação e virada de ano.
  - `ArmazenamentoAnexoTest` — extensão/assinatura inválida, tamanho excedido, caminho sem *path traversal*.
- **Automatizados hoje:** 123 testes TestNG (módulo `chamado` mais `ReferenciaEditalTest`), executados direto pelo TestNG porque o provider `surefire-testng` não está no cache Maven offline.
- **Manuais:** roteiro em `docs/testes/22-sistema-chamados.md` cobrindo os três perfis, o ciclo completo, e-mails, SLA (com prazos reduzidos via configuração) e exportações.
- **Build:** `BUILD SUCCESS` com o comando de build do projeto.

### 3.8 Documentação a atualizar na entrega

- `docs/entregas/22-sistema-chamados.md` (com seção "## Origem" apontando para o plano 22).
- `docs/entregas/README.md`, `docs/entregas/itens-edital-x-entregas.md` (nova seção para o item 7.2), `docs/entregas/rastreabilidade-tr.md`.
- `docs/requisitos/04-modulos-funcionais.md`, `docs/referencia/` (servlets, telas, menus), `docs-editais/edital-salvador/README.md`.

## Ajustes feitos ao detalhar o plano

Refinamentos técnicos sem mudança de comportamento para o usuário:

- **Polling sem `desde`:** a lista recarrega a página atual a cada 30 s e destaca as linhas cujo `dt_atualizacao` mudou (§2.4/§3.2 previam o parâmetro `desde`).
- **Controle otimista:** o `UPDATE` compara `status` **e** `dt_atualizacao`, para detectar também alterações que não mudam o status (assumir, reclassificar). Na revisão final a comparação passou a usar a **versão enviada pelo cliente** (ver "Ajustes da revisão final").
- **Exportação:** `ExportacaoChamados` próprio (Apache POI + `HashUtil`) em vez de `dashboards.ExportacaoService`, que não aceita cabeçalho, rodapé nem aba de metadados. O SHA-256 cobre só o bloco de dados, por isso CSV, XLSX e PDF do mesmo filtro mostram o mesmo hash. O PDF é montado no navegador com jsPDF a partir de `formato=json` (dados completos, não só a página visível). Células iniciadas por `=`, `+`, `-` ou `@` recebem `'` (proteção contra injeção de fórmula).
- **E-mail:** o método novo em `ServicoEmailMuralha` é `enviarEmailChamado(remetente, destinatarios, assunto, corpoHtml)`; o HTML é montado em `EmailChamado`.
- **ID do usuário:** `com.consilux.model.Usuario#getId()` (a errata §1 cita `getIdUsuario()`, que não existe nessa classe).

### Complemento — administração, categoria própria e ícones (2026-10-15)

Pedido do responsável após a primeira entrega, com a migração já executada no `GTW_MURALHA_DEV`: quem pode abrir chamados é definido na configuração dos poderes do usuário; o sistema ganha categoria própria na tela inicial, com ícones no estilo já usado; o acordo de nível de serviço é configurável pelo administrador; configuração e definição de quem usa o sistema aparecem só para o administrador; todas as alterações passam pelo motor de logs. Decisões:

- **Perfil `ADMIN`** (§1.1), mapeado do grupo em `chamado_grupo_admin` (padrão `11`, Administradores). A tela e a API de Configuração passam a exigir `ADMIN`; a `EQUIPE` deixa de configurar. `ADMIN` enxerga todos os chamados, somente leitura, a menos que também tenha outro perfil.
- **Hierarquia dos grupos:** os grupos 107–109 passam a ter `id_grupo_pai = 11`, para aparecerem na tela legada de usuários (GWT), que lista os grupos abaixo do grupo de quem edita.
- **Aba "Perfis de acesso"** na Configuração: o administrador inclui ou remove usuários dos três grupos de perfil, na mesma tabela `dbo.sis_usuario_grupo` da tela legada; o grupo do `ADMIN` não é gerenciado por ali.
- **Aba "Parâmetros de SLA":** `chamado_sla_risco_percentual` (1–99) e `chamado_fechamento_auto_dias` (1–60), validados no servidor e registrados em `muralha.config_chave_valor_hist`.
- **Categoria "Sistema de Chamados"** na tela inicial (`sis_menu` 383, `sis_menu_infos` 49, `menu_pai NULL`, `ordenacao 10`); os cards 46, 47 e 48 passam para `menu_pai = 49`, com ícones SVG novos em `assets/images/menu-icons/`. O direito do menu 382 (Configuração) fica só com o grupo administrador. *(Desde 2026-10-18, `20261018_remove_cards_duplicados.sql` removeu os cards 47 e 48 (Relatórios e Configuração); as telas são abertas pelos botões da tela Chamados e só o card 46 fica na categoria.)*
- **Motor de logs:** parâmetros (`SALVAR_PARAMETROS_SLA`) e perfis (`ADICIONAR_PERFIL`, `REMOVER_PERFIL`) se somam às operações já auditadas, via `AuditoriaService` (funcionalidade "Chamado").
- Script: `docs/banco-de-dados/migracoes/20261015_sistema_chamados_admin.sql` (idempotente, rollback no cabeçalho).

### Complemento — SMTP dos chamados pelo administrador (2026-10-15)

Pedido do responsável: "As configurações do SMTP também deverão constar em configurações do sistema de chamados." Decisões (substituem, para o envio, o uso de `ServicoEmailMuralha.enviarEmailChamado` e de `ConfiguracaoProvider` descrito em §3.1 e §3.5):

- **Chaves** em `muralha.config_chave_valor`, criadas vazias por `20261015_sistema_chamados_smtp.sql`: `chamado_smtp_host`, `chamado_smtp_porta`, `chamado_smtp_usuario`, `chamado_smtp_senha`, `chamado_smtp_remetente`, `chamado_smtp_starttls` (`1`/`0`), além da já existente `chamado_email_ativo`. Host vazio = primeiro servidor do `confGTW.xml` (comportamento anterior).
- **Senha cifrada:** `CifraSegredo` (AES/GCM/NoPadding do JDK, IV aleatório de 12 bytes, tag de 128 bits) grava `v1:` + Base64(IV ‖ cifrado+tag); chave de 32 bytes em Base64 na variável de ambiente `GTW_CHAMADO_CHAVE_SMTP`; senha limitada a 120 caracteres para caber em `varchar(255)`. A senha nunca sai pela API, logs, histórico ou auditoria.
- **Envio:** `EmailChamado.enviar(Connection, …)` monta a sessão JavaMail com `ConfiguracaoSmtpChamado.resolver` (STARTTLS conforme a chave, autenticação só com usuário, tempo-limite de 10 s). `ServicoEmailMuralha` volta ao estado anterior ao plano.
- **API** (`ChamadoConfig`, `ADMIN`): GET `smtp` (sem a senha; informa `senhaDefinida`, `chaveDisponivel` e `origemAtual`), POST `salvarSmtp` (senha vazia mantém, `limparSenha` apaga; validação de host DNS/IPv4, porta 1–65535, remetente e usuário ≤ 100; histórico em `config_chave_valor_hist` na mesma transação) e POST `testarSmtp` (e-mail de teste ao administrador logado). Auditoria `SALVAR_SMTP` e `TESTAR_SMTP`.
- **Tela:** aba "E-mail (SMTP)" na Configuração.

### Ajustes da revisão final

Correções feitas após a revisão final das Tarefas 1–13, sem mudar as decisões D1–D14:

- **Marcas definitivas:** `CUMPRIDO` e `VIOLADO` não são mais sobrescritas pelo `ChamadoSlaJob` nem por transições posteriores.
- **Controle otimista com versão do cliente:** a tela envia a `dtAtualizacao` que carregou (`versao`); a ação só é aplicada se a versão gravada for a mesma. Se outro usuário (ou o job de SLA) alterou o chamado nesse intervalo, a operação responde "Chamado alterado por outro usuário; recarregue". O conflito causado pelo job de SLA é aceito como comportamento correto.
- **Prazo estendido na saída da pausa:** ao sair de `AGUARDANDO_SOLICITANTE` (ou ao reabrir), o `dt_prazo_resolucao` gravado é estendido pelo tempo pausado, de modo que lista, contador e job leiam o prazo já correto.
- **Exportação:** injeção no cabeçalho do CSV corrigida (quebras de linha e valores iniciados por `=`, `+`, `-` ou `@` são neutralizados).
- **Negação por perfil:** responde HTTP 200 com `{ok:false, erro}` em vez de 403; o front mostra a mensagem.
- **Datas:** o DAO trunca datas de abertura e prazos a milissegundos ao gravar (precisão do `DATETIME2(3)`).
- **Correções menores:** SweetAlert com `text` em vez de HTML cru, script duplicado removido, auditoria fora do `try` da transação, recursos fechados em `finally`, registro de notificação com `try/catch`, nomes de grupos e tratamento de e-mail vazio.

## Fora do escopo

- Abertura de chamado por e-mail recebido (7.2.2.c) e por aplicativo de mensagens (7.2.2.d); a coluna `origem` fica preparada.
- Comentários livres e notas internas da CONTRATADA.
- Chamado automático por indisponibilidade de equipamento (Plano 18) — candidato a plano futuro.
- WebSocket e e-mail a cada mudança de status.
- Calendário de expediente/feriados (SLA é 24x7).
- Base de conhecimento, pesquisa de satisfação além da nota por chamado, aplicativo móvel.
