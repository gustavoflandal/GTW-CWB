# Sistema de Gerenciamento de Chamados — Plano de Implementação

> **Status:** Aprovado para execução — spec revisada em 2026-10-15.
> **Depende de:** Plano 01 (`AuditoriaService`), Plano 11 (exportação padronizada — padrão de CSV com SHA-256).
> **Para agentes:** REQUIRED SUB-SKILL: use `superpowers:subagent-driven-development` (recomendado) ou `superpowers:executing-plans` para implementar tarefa a tarefa. Os passos usam checkbox (`- [ ]`).

**Goal:** Módulo de chamados de suporte técnico integrado à Muralha Digital, em que a CONTRATANTE abre e acompanha chamados com prioridade, consulta o histórico, gera relatórios, avalia o atendimento e exporta os registros (7.2.3), recebendo protocolo único e confirmação automática com prioridade e prazos (7.2.4).

**Architecture:** Pacote novo `muralha.digital.chamado` com regras puras e testáveis (`MaquinaEstados`, `CalculadoraSla`, `GeradorProtocolo`, `ArmazenamentoAnexo`, `ExportacaoChamados`, `EmailChamado`), DAOs JDBC, um `ChamadoService` transacional e três servlets JSON (`Chamado`, `ChamadoRelatorio`, `ChamadoConfig`). Dois jobs Quartz (SLA/fechamento automático e fila de e-mail). Quatro telas Bootstrap 5.3 em `pages/chamados/` com polling de 30 s. Sete tabelas novas no schema `muralha`.

**Tech Stack:** Java 13 · Servlet 3 · JDBC (SQL Server) · Gson · Quartz 2.3.2 · commons-fileupload 1.4 · Apache POI 4.1.1 · JavaMail · TestNG 6.8 · Bootstrap 5.3 · SweetAlert2 · Chart.js 4.4.1 (cdnjs) · jsPDF + autotable (já em `assets/js`). Sem dependência nova.

**Spec:** [`docs/superpowers/specs/2026-10-15-sistema-chamados-design.md`](../superpowers/specs/2026-10-15-sistema-chamados-design.md) — leia antes de começar; decisões D1–D14 e tabelas §1.2/§2.1 são a referência.

## Referência ao Edital (TR TRANSALVADOR)

| Ref. | Requisito |
|---|---|
| 7.2.3.a | Abertura de chamados com classificação de prioridade |
| 7.2.3.b | Acompanhamento em tempo real do status de cada chamado |
| 7.2.3.c | Registro e consulta do histórico integral de chamados |
| 7.2.3.d | Relatórios de atendimento por período, categoria e equipamento |
| 7.2.3.e | Avaliação da qualidade do atendimento recebido |
| 7.2.3.f | Exportação dos registros em PDF, CSV e XLS |
| 7.2.4 | Protocolo único + confirmação automática com prioridade e prazos previstos de atendimento e resolução |

> Fonte: planilha [`Atividades Edital Salvador 2026.xlsx`](../../docs-editais/edital-salvador/Atividades%20Edital%20Salvador%202026.xlsx), aba "Sist. Chamado". O Capítulo 7 do TR não está no repositório; prioridades, prazos e categorias são configuráveis (spec D4).

## Global Constraints

- Stack imutável: não alterar `.setup-gtw/` nem `pom.xml`; nenhuma dependência nova (spec, CLAUDE.md).
- Sem `ALTER TABLE` em tabelas existentes; DDL/DML só por script em `docs/banco-de-dados/migracoes/`, executado **somente com autorização do responsável**.
- Todo servlet: sessão (`SessaoConstantes.SESSAO_USUARIO`) + `new Acesso(req, resp, true).verificaAcesso(false)` + perfil do Sistema de Chamados (`SOLICITANTE`, `GESTOR`, `EQUIPE`).
- O ID do usuário vem de `com.consilux.model.Usuario#getId()` (a errata §1 cita `getIdUsuario()`, que **não existe** nessa classe — confirmado no código e no `LoteServlet`).
- `muralha.config_chave_valor` tem só `chave` e `valor` (errata §5/§15).
- SQL sempre parametrizado; colunas de ordenação/agrupamento só de listas brancas.
- Relógio de SLA: horas corridas 24x7; pausa só em `AGUARDANDO_SOLICITANTE` e entre `RESOLVIDO` e reabertura (D5).
- Protocolo `CH-AAAA-NNNNNN`, contador por ano em `muralha.chamado_protocolo` (spec §1.4).
- E-mails somente em `CONFIRMACAO_ABERTURA` e `RECLASSIFICACAO`, via fila `chamado_notificacao`, até 5 tentativas (D14).
- Tempo real por polling de 30 s; sem WebSocket e sem e-mail por mudança de status (D8).
- Anexos: `pdf`, `png`, `jpg`, `jpeg`, assinatura conferida, até `chamado_anexo_max_mb` (10) por arquivo, até 5 por envio, gravados fora do webapp (spec §3.4).
- JS das telas fica em arquivos `.js` (não inline na JSP): `${...}` dentro de JSP é avaliado como EL e quebra template literals.
- Todo texto vindo do servidor entra no DOM por `Chamados.esc()` ou `textContent`.
- Código, mensagens e commits em português; commits no imperativo.

## Review Focus

1. **Solicitante tentando ver chamado alheio** (protocolo digitado na URL, ID de anexo, exportação) → deve receber 404/lista vazia, nunca dados de outro solicitante. Teste: `FiltroChamadosTest` (cláusula de visibilidade sempre aplicada) na Tarefa 6 + passo manual na Tarefa 13.
2. **Arquivo malicioso** (`.exe` renomeado para `.pdf`, nome com `..\`, arquivo acima do limite) → rejeitado com mensagem clara, nada gravado fora do diretório. Teste: `ArmazenamentoAnexoTest` na Tarefa 4.
3. **Reclassificação depois de pausas e reabertura** → prazo de resolução preserva os minutos já pausados. Teste: `ChamadoServiceRegrasTest` na Tarefa 7.
4. **Célula de exportação começando com `=`, `+`, `-` ou `@`** (injeção de fórmula no Excel) e textos com `;`/aspas/quebra de linha → valor neutralizado e CSV íntegro. Teste: `ExportacaoChamadosTest` na Tarefa 5.
5. **Título/descrição com HTML ou script** (no e-mail e na tela) → exibido como texto. Teste: `EmailChamadoTest` na Tarefa 5 + uso obrigatório de `Chamados.esc()` nas Tarefas 10–12.

---

## Comandos usados neste plano

Os scripts `.ps1` de `.setup-gtw` usam o operador ternário e exigem PowerShell 7; no Windows PowerShell 5.1 use o Maven direto (mesmos argumentos). Em cada terminal novo:

```powershell
cd D:\GTW-CWB
$s = 'D:\GTW-CWB\.setup-gtw'
$env:JAVA_HOME = "$s\jdk-13.0.1"
$env:PATH = "$env:JAVA_HOME\bin;$s\apache-maven\bin;$env:PATH"
```

- **Build:** `mvn -o -s "$s\settings-legacy-archiva.xml" '-DskipTests' '-Dexec.skip=true' package` → `BUILD SUCCESS`.
- **Teste de uma classe:** `mvn -o -s "$s\settings-legacy-archiva.xml" '-Dexec.skip=true' '-Dtest=NomeDoTest' '-DfailIfNoTests=false' test` (o `-Dtest` substitui a suíte `testng-customsuite.xml`, que não lista classes).
- **Executar:** `mvn -o -s "$s\settings-legacy-archiva.xml" '-Dexec.skip=true' org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run` → http://localhost:8080/.

---

## Arquivos

| Ação | Caminho | Responsabilidade |
|---|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261015_sistema_chamados.sql` | Tabelas e índices |
| Criar | `docs/banco-de-dados/migracoes/20261015_sistema_chamados_seed.sql` | Prioridades, categorias, parâmetros, grupos, menus |
| Criar | `src/main/java/muralha/digital/chamado/StatusChamado.java` | Estados |
| Criar | `src/main/java/muralha/digital/chamado/AcaoChamado.java` | Ações |
| Criar | `src/main/java/muralha/digital/chamado/PerfilChamado.java` | Perfis |
| Criar | `src/main/java/muralha/digital/chamado/RegraChamadoException.java` | Erro de regra (mensagem ao usuário) |
| Criar | `src/main/java/muralha/digital/chamado/ChamadoNaoEncontradoException.java` | 404 sem revelar existência |
| Criar | `src/main/java/muralha/digital/chamado/MaquinaEstados.java` | Transições × perfil (puro) |
| Criar | `src/main/java/muralha/digital/chamado/CalculadoraSla.java` | Prazos e marcas (puro) |
| Criar | `src/main/java/muralha/digital/chamado/GeradorProtocolo.java` | Protocolo anual |
| Criar | `src/main/java/muralha/digital/chamado/ArquivoEnviado.java` | Arquivo recebido no upload |
| Criar | `src/main/java/muralha/digital/chamado/AnexoGravado.java` | Arquivo gravado |
| Criar | `src/main/java/muralha/digital/chamado/ArmazenamentoAnexo.java` | Validação e gravação de anexos |
| Criar | `src/main/java/muralha/digital/chamado/ExportacaoChamados.java` | CSV/XLSX/dados p/ PDF com SHA-256 |
| Criar | `src/main/java/muralha/digital/chamado/EmailChamado.java` | Assunto, corpo HTML e envio |
| Criar | `src/main/java/muralha/digital/chamado/Chamado.java` | Modelo |
| Criar | `src/main/java/muralha/digital/chamado/Prioridade.java` | Modelo + validação |
| Criar | `src/main/java/muralha/digital/chamado/Categoria.java` | Modelo + validação |
| Criar | `src/main/java/muralha/digital/chamado/FiltroChamados.java` | Filtros → WHERE parametrizado |
| Criar | `src/main/java/muralha/digital/chamado/ChamadoConfigDao.java` | Configuração, perfis, cadastros |
| Criar | `src/main/java/muralha/digital/chamado/ChamadoDao.java` | SQL de chamados |
| Criar | `src/main/java/muralha/digital/chamado/ChamadoService.java` | Regras transacionais |
| Criar | `src/main/java/muralha/digital/chamado/ContextoRequisicao.java` | Sessão + acesso + perfis |
| Criar | `src/main/java/muralha/digital/chamado/ChamadoServlet.java` | `/MuralhaDigital/Chamado` |
| Criar | `src/main/java/muralha/digital/chamado/ChamadoRelatorioServlet.java` | `/MuralhaDigital/ChamadoRelatorio` |
| Criar | `src/main/java/muralha/digital/chamado/ChamadoConfigServlet.java` | `/MuralhaDigital/ChamadoConfig` |
| Criar | `src/main/java/muralha/digital/chamado/ChamadoSlaJob.java` | Job SLA + fechamento automático |
| Criar | `src/main/java/muralha/digital/chamado/ChamadoEmailJob.java` | Job fila de e-mail |
| Modificar | `src/main/java/muralha/digital/notificacao/ServicoEmailMuralha.java` | Novo método `enviarEmailChamado` |
| Modificar | `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` | Registrar os dois jobs |
| Modificar | `src/main/java/muralha/digital/acessos/ReferenciaEdital.java` | Menus 379–382 |
| Criar | `src/main/webapp/muralha-digital/pages/chamados/index.jsp` | Lista + novo chamado |
| Criar | `src/main/webapp/muralha-digital/pages/chamados/detalhe.jsp` | Detalhe + ações |
| Criar | `src/main/webapp/muralha-digital/pages/chamados/relatorios.jsp` | Indicadores |
| Criar | `src/main/webapp/muralha-digital/pages/chamados/configuracao.jsp` | Prioridades e categorias |
| Criar | `src/main/webapp/muralha-digital/assets/js/chamados/chamados-comum.js` | Utilitários (esc, badges, fetch) |
| Criar | `src/main/webapp/muralha-digital/assets/js/chamados/chamados-lista.js` | Lógica da lista |
| Criar | `src/main/webapp/muralha-digital/assets/js/chamados/chamados-detalhe.js` | Lógica do detalhe |
| Criar | `src/main/webapp/muralha-digital/assets/js/chamados/chamados-relatorios.js` | Lógica dos relatórios |
| Criar | `src/main/webapp/muralha-digital/assets/js/chamados/chamados-configuracao.js` | Lógica da configuração |
| Criar | `src/test/java/muralha/digital/chamado/*Test.java` | Testes TestNG |
| Modificar | `src/test/java/muralha/digital/acessos/ReferenciaEditalTest.java` | Cobrir 379–382 |
| Criar | `docs/testes/22-sistema-chamados.md` | Roteiro de testes manuais |
| Criar | `docs/entregas/22-sistema-chamados.md` | Documento de entrega |
| Modificar | `docs/entregas/README.md`, `docs/entregas/itens-edital-x-entregas.md`, `docs/entregas/rastreabilidade-tr.md`, `docs/agentes/AGENTS.md`, `docs/requisitos/04-modulos-funcionais.md`, `docs-editais/edital-salvador/README.md` | Rastreabilidade |

---

### Tarefa 1: Migração SQL (estrutura + seed)

**Files:**
- Create: `docs/banco-de-dados/migracoes/20261015_sistema_chamados.sql`
- Create: `docs/banco-de-dados/migracoes/20261015_sistema_chamados_seed.sql`

**Interfaces:**
- Produces: tabelas `muralha.chamado_prioridade`, `chamado_categoria`, `chamado`, `chamado_historico`, `chamado_anexo`, `chamado_notificacao`, `chamado_protocolo`; chaves `chamado_*` em `muralha.config_chave_valor`; grupos "Chamados - Solicitante/Gestor do contrato/Equipe tecnica"; menus 379 (lista), 380 (detalhe), 381 (relatórios), 382 (configuração); `sis_menu_infos` 46–48.

- [ ] **Step 1: Criar o script de estrutura**

```sql
-- =============================================================================
-- Migracao: Sistema de Gerenciamento de Chamados (Plano 22 — itens 7.2.3 e 7.2.4)
-- Data: 2026-10-15
-- Idempotente; nao altera tabelas existentes. Executar somente com autorizacao.
-- ROLLBACK (nesta ordem):
--   DROP TABLE muralha.chamado_notificacao;
--   DROP TABLE muralha.chamado_anexo;
--   DROP TABLE muralha.chamado_historico;
--   DROP TABLE muralha.chamado;
--   DROP TABLE muralha.chamado_categoria;
--   DROP TABLE muralha.chamado_prioridade;
--   DROP TABLE muralha.chamado_protocolo;
-- =============================================================================

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_prioridade')
BEGIN
    CREATE TABLE muralha.chamado_prioridade (
        id                  INT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado_prioridade PRIMARY KEY,
        codigo              VARCHAR(20)  NOT NULL CONSTRAINT uq_chamado_prioridade_codigo UNIQUE,
        nome                NVARCHAR(50) NOT NULL,
        ordem               TINYINT      NOT NULL,
        minutos_atendimento INT          NOT NULL,
        minutos_resolucao   INT          NOT NULL,
        cor                 VARCHAR(7)   NOT NULL CONSTRAINT df_chamado_prioridade_cor DEFAULT '#6c757d',
        ativo               BIT          NOT NULL CONSTRAINT df_chamado_prioridade_ativo DEFAULT 1,
        CONSTRAINT chk_chamado_prioridade_minutos
            CHECK (minutos_atendimento > 0 AND minutos_resolucao >= minutos_atendimento)
    );
    PRINT 'Criada muralha.chamado_prioridade';
END
ELSE PRINT 'Ja existe muralha.chamado_prioridade';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_categoria')
BEGIN
    CREATE TABLE muralha.chamado_categoria (
        id                INT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado_categoria PRIMARY KEY,
        nome              NVARCHAR(100) NOT NULL,
        id_pai            INT NULL CONSTRAINT fk_chamado_categoria_pai REFERENCES muralha.chamado_categoria(id),
        exige_equipamento BIT NOT NULL CONSTRAINT df_chamado_categoria_exige DEFAULT 0,
        ativo             BIT NOT NULL CONSTRAINT df_chamado_categoria_ativo DEFAULT 1
    );
    PRINT 'Criada muralha.chamado_categoria';
END
ELSE PRINT 'Ja existe muralha.chamado_categoria';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_protocolo')
BEGIN
    CREATE TABLE muralha.chamado_protocolo (
        ano    SMALLINT NOT NULL CONSTRAINT pk_chamado_protocolo PRIMARY KEY,
        ultimo INT      NOT NULL
    );
    PRINT 'Criada muralha.chamado_protocolo';
END
ELSE PRINT 'Ja existe muralha.chamado_protocolo';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado')
BEGIN
    CREATE TABLE muralha.chamado (
        id                       BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado PRIMARY KEY,
        protocolo                VARCHAR(20)    NOT NULL CONSTRAINT uq_chamado_protocolo UNIQUE,
        titulo                   NVARCHAR(150)  NOT NULL,
        descricao                NVARCHAR(MAX)  NOT NULL,
        id_categoria             INT NOT NULL CONSTRAINT fk_chamado_categoria REFERENCES muralha.chamado_categoria(id),
        id_prioridade            INT NOT NULL CONSTRAINT fk_chamado_prioridade REFERENCES muralha.chamado_prioridade(id),
        id_prioridade_solicitada INT NOT NULL CONSTRAINT fk_chamado_prioridade_sol REFERENCES muralha.chamado_prioridade(id),
        status                   VARCHAR(30)    NOT NULL CONSTRAINT df_chamado_status DEFAULT 'ABERTO',
        origem                   VARCHAR(20)    NOT NULL CONSTRAINT df_chamado_origem DEFAULT 'PORTAL',
        id_local                 INT NULL,
        id_usuario_solicitante   INT NOT NULL,
        id_usuario_responsavel   INT NULL,
        dt_abertura              DATETIME2(3) NOT NULL CONSTRAINT df_chamado_dt_abertura DEFAULT SYSDATETIME(),
        dt_prazo_atendimento     DATETIME2(3) NOT NULL,
        dt_prazo_resolucao       DATETIME2(3) NOT NULL,
        dt_primeiro_atendimento  DATETIME2(3) NULL,
        dt_resolucao             DATETIME2(3) NULL,
        dt_fechamento            DATETIME2(3) NULL,
        minutos_pausados         INT NOT NULL CONSTRAINT df_chamado_min_pausados DEFAULT 0,
        dt_inicio_pausa          DATETIME2(3) NULL,
        sla_atendimento          VARCHAR(10) NOT NULL CONSTRAINT df_chamado_sla_at DEFAULT 'NO_PRAZO',
        sla_resolucao            VARCHAR(10) NOT NULL CONSTRAINT df_chamado_sla_res DEFAULT 'NO_PRAZO',
        avaliacao_nota           TINYINT NULL,
        avaliacao_comentario     NVARCHAR(1000) NULL,
        dt_avaliacao             DATETIME2(3) NULL,
        fechado_automaticamente  BIT NOT NULL CONSTRAINT df_chamado_fech_auto DEFAULT 0,
        dt_atualizacao           DATETIME2(3) NOT NULL CONSTRAINT df_chamado_dt_atualizacao DEFAULT SYSDATETIME(),
        CONSTRAINT chk_chamado_status CHECK (status IN
            ('ABERTO','EM_ATENDIMENTO','AGUARDANDO_SOLICITANTE','RESOLVIDO','FECHADO','CANCELADO')),
        CONSTRAINT chk_chamado_sla_at  CHECK (sla_atendimento IN ('NO_PRAZO','EM_RISCO','VENCIDO','CUMPRIDO','VIOLADO')),
        CONSTRAINT chk_chamado_sla_res CHECK (sla_resolucao   IN ('NO_PRAZO','EM_RISCO','VENCIDO','CUMPRIDO','VIOLADO')),
        CONSTRAINT chk_chamado_nota    CHECK (avaliacao_nota IS NULL OR avaliacao_nota BETWEEN 1 AND 5)
    );
    PRINT 'Criada muralha.chamado';
END
ELSE PRINT 'Ja existe muralha.chamado';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_historico')
BEGIN
    CREATE TABLE muralha.chamado_historico (
        id              BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado_historico PRIMARY KEY,
        id_chamado      BIGINT NOT NULL CONSTRAINT fk_chamado_historico_chamado REFERENCES muralha.chamado(id),
        dt_evento       DATETIME2(3) NOT NULL CONSTRAINT df_chamado_historico_dt DEFAULT SYSDATETIME(),
        id_usuario      INT NULL,
        tipo            VARCHAR(20) NOT NULL,
        status_anterior VARCHAR(30) NULL,
        status_novo     VARCHAR(30) NULL,
        texto           NVARCHAR(2000) NULL,
        CONSTRAINT chk_chamado_historico_tipo CHECK (tipo IN
            ('ABERTURA','TRANSICAO','RESPOSTA','RECLASSIFICACAO','ATRIBUICAO','AVALIACAO','SLA'))
    );
    PRINT 'Criada muralha.chamado_historico';
END
ELSE PRINT 'Ja existe muralha.chamado_historico';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_anexo')
BEGIN
    CREATE TABLE muralha.chamado_anexo (
        id            BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado_anexo PRIMARY KEY,
        id_chamado    BIGINT NOT NULL CONSTRAINT fk_chamado_anexo_chamado REFERENCES muralha.chamado(id),
        id_historico  BIGINT NULL CONSTRAINT fk_chamado_anexo_historico REFERENCES muralha.chamado_historico(id),
        nome_original NVARCHAR(255) NOT NULL,
        caminho       NVARCHAR(400) NOT NULL,
        tipo_mime     VARCHAR(100)  NOT NULL,
        tamanho_bytes BIGINT        NOT NULL,
        sha256        CHAR(64)      NOT NULL,
        dt_envio      DATETIME2(3)  NOT NULL CONSTRAINT df_chamado_anexo_dt DEFAULT SYSDATETIME(),
        id_usuario    INT NOT NULL
    );
    PRINT 'Criada muralha.chamado_anexo';
END
ELSE PRINT 'Ja existe muralha.chamado_anexo';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_notificacao')
BEGIN
    CREATE TABLE muralha.chamado_notificacao (
        id           BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado_notificacao PRIMARY KEY,
        id_chamado   BIGINT NOT NULL CONSTRAINT fk_chamado_notificacao_chamado REFERENCES muralha.chamado(id),
        tipo         VARCHAR(30)  NOT NULL,
        destinatario VARCHAR(200) NULL,
        status       VARCHAR(10)  NOT NULL CONSTRAINT df_chamado_notificacao_status DEFAULT 'PENDENTE',
        tentativas   TINYINT      NOT NULL CONSTRAINT df_chamado_notificacao_tent DEFAULT 0,
        dt_criacao   DATETIME2(3) NOT NULL CONSTRAINT df_chamado_notificacao_dt DEFAULT SYSDATETIME(),
        dt_envio     DATETIME2(3) NULL,
        erro         NVARCHAR(500) NULL,
        CONSTRAINT chk_chamado_notificacao_tipo   CHECK (tipo IN ('CONFIRMACAO_ABERTURA','RECLASSIFICACAO')),
        CONSTRAINT chk_chamado_notificacao_status CHECK (status IN ('PENDENTE','ENVIADO','ERRO'))
    );
    PRINT 'Criada muralha.chamado_notificacao';
END
ELSE PRINT 'Ja existe muralha.chamado_notificacao';
GO

-- Indices (cada um idempotente)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_status_abertura' AND object_id = OBJECT_ID('muralha.chamado'))
    CREATE INDEX ix_chamado_status_abertura ON muralha.chamado (status, dt_abertura);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_solicitante' AND object_id = OBJECT_ID('muralha.chamado'))
    CREATE INDEX ix_chamado_solicitante ON muralha.chamado (id_usuario_solicitante, dt_abertura);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_categoria' AND object_id = OBJECT_ID('muralha.chamado'))
    CREATE INDEX ix_chamado_categoria ON muralha.chamado (id_categoria);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_local' AND object_id = OBJECT_ID('muralha.chamado'))
    CREATE INDEX ix_chamado_local ON muralha.chamado (id_local);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_atualizacao' AND object_id = OBJECT_ID('muralha.chamado'))
    CREATE INDEX ix_chamado_atualizacao ON muralha.chamado (dt_atualizacao);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_historico_chamado' AND object_id = OBJECT_ID('muralha.chamado_historico'))
    CREATE INDEX ix_chamado_historico_chamado ON muralha.chamado_historico (id_chamado, dt_evento);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_anexo_chamado' AND object_id = OBJECT_ID('muralha.chamado_anexo'))
    CREATE INDEX ix_chamado_anexo_chamado ON muralha.chamado_anexo (id_chamado);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_notificacao_status' AND object_id = OBJECT_ID('muralha.chamado_notificacao'))
    CREATE INDEX ix_chamado_notificacao_status ON muralha.chamado_notificacao (status, dt_criacao);
PRINT 'Indices verificados';
GO
```

- [ ] **Step 2: Criar o script de seed**

```sql
-- =============================================================================
-- Seed: Sistema de Gerenciamento de Chamados (Plano 22)
-- Data: 2026-10-15 — idempotente. Executar depois de 20261015_sistema_chamados.sql.
-- ROLLBACK:
--   DELETE FROM dbo.sis_menu_direitos WHERE id_menu BETWEEN 379 AND 382;
--   DELETE FROM dbo.sis_menu_infos    WHERE id_infos BETWEEN 46 AND 48;
--   DELETE FROM dbo.sis_menu          WHERE id_menu BETWEEN 379 AND 382;
--   DELETE FROM dbo.sis_usuario_grupo WHERE id_grupo IN (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor
--          WHERE chave IN ('chamado_grupo_solicitante','chamado_grupo_gestor','chamado_grupo_equipe'));
--   DELETE FROM dbo.sis_grupo WHERE RTRIM(descricao) IN
--          ('Chamados - Solicitante','Chamados - Gestor do contrato','Chamados - Equipe tecnica');
--   DELETE FROM muralha.config_chave_valor WHERE chave LIKE 'chamado[_]%';
--   (categorias e prioridades saem com o DROP das tabelas)
-- =============================================================================

-- 1) Prioridades (minutos de atendimento / resolucao) — valores iniciais ajustaveis por tela
INSERT INTO muralha.chamado_prioridade (codigo, nome, ordem, minutos_atendimento, minutos_resolucao, cor)
SELECT v.codigo, v.nome, v.ordem, v.min_at, v.min_res, v.cor
FROM (VALUES ('CRITICA', N'Crítica', 1,  60,  240, '#dc3545'),
             ('ALTA',    N'Alta',    2, 120,  480, '#fd7e14'),
             ('MEDIA',   N'Média',   3, 240, 1440, '#0d6efd'),
             ('BAIXA',   N'Baixa',   4, 480, 4320, '#6c757d')) v(codigo, nome, ordem, min_at, min_res, cor)
WHERE NOT EXISTS (SELECT 1 FROM muralha.chamado_prioridade p WHERE p.codigo = v.codigo);
PRINT 'Prioridades verificadas';
GO

-- 2) Categorias (dois niveis). A subcategoria herda "exige equipamento" da categoria.
DECLARE @cats TABLE (pai NVARCHAR(100), exige BIT, filho NVARCHAR(100));
INSERT INTO @cats VALUES
 (N'Equipamento', 1, N'Falha de captura/OCR'),
 (N'Equipamento', 1, N'Falha de comunicação'),
 (N'Equipamento', 1, N'Falha de energia'),
 (N'Equipamento', 1, N'Dano físico/vandalismo'),
 (N'Equipamento', 1, N'Outros'),
 (N'Sistema', 0, N'Erro de funcionalidade'),
 (N'Sistema', 0, N'Lentidão'),
 (N'Sistema', 0, N'Indisponibilidade'),
 (N'Sistema', 0, N'Outros'),
 (N'Acesso/usuário', 0, N'Criação/bloqueio de usuário'),
 (N'Acesso/usuário', 0, N'Permissões'),
 (N'Acesso/usuário', 0, N'Senha/MFA'),
 (N'Integração', 0, N'Falha de integração externa'),
 (N'Integração', 0, N'Outros'),
 (N'Dúvida/solicitação', 0, N'Dúvida de uso'),
 (N'Dúvida/solicitação', 0, N'Solicitação de melhoria'),
 (N'Dúvida/solicitação', 0, N'Outros');

INSERT INTO muralha.chamado_categoria (nome, id_pai, exige_equipamento)
SELECT DISTINCT c.pai, NULL, c.exige FROM @cats c
WHERE NOT EXISTS (SELECT 1 FROM muralha.chamado_categoria x WHERE x.id_pai IS NULL AND x.nome = c.pai);

INSERT INTO muralha.chamado_categoria (nome, id_pai, exige_equipamento)
SELECT c.filho, p.id, c.exige
FROM @cats c
JOIN muralha.chamado_categoria p ON p.id_pai IS NULL AND p.nome = c.pai
WHERE NOT EXISTS (SELECT 1 FROM muralha.chamado_categoria x WHERE x.id_pai = p.id AND x.nome = c.filho);
PRINT 'Categorias verificadas';
GO

-- 3) Parametros
INSERT INTO muralha.config_chave_valor (chave, valor)
SELECT v.chave, v.valor
FROM (VALUES ('chamado_sla_risco_percentual', '80'),
             ('chamado_fechamento_auto_dias', '5'),
             ('chamado_diretorio_anexos',     'C:\Consilux\MuralhaDigital\chamados'),
             ('chamado_anexo_max_mb',         '10'),
             ('chamado_email_ativo',          '1')) v(chave, valor)
WHERE NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor c WHERE c.chave = v.chave);
PRINT 'Parametros verificados';
GO

-- 4) Grupos de perfil (sis_grupo.id_grupo nao e IDENTITY: usa MAX+1)
DECLARE @grupos TABLE (descricao VARCHAR(50), chave VARCHAR(100));
INSERT INTO @grupos VALUES
 ('Chamados - Solicitante',        'chamado_grupo_solicitante'),
 ('Chamados - Gestor do contrato', 'chamado_grupo_gestor'),
 ('Chamados - Equipe tecnica',     'chamado_grupo_equipe');

DECLARE @descricao VARCHAR(50), @chave VARCHAR(100), @id INT;
DECLARE cur CURSOR LOCAL FAST_FORWARD FOR SELECT descricao, chave FROM @grupos;
OPEN cur;
FETCH NEXT FROM cur INTO @descricao, @chave;
WHILE @@FETCH_STATUS = 0
BEGIN
    IF NOT EXISTS (SELECT 1 FROM dbo.sis_grupo WHERE RTRIM(descricao) = @descricao)
    BEGIN
        SELECT @id = MAX(id_grupo) + 1 FROM dbo.sis_grupo;
        INSERT INTO dbo.sis_grupo (id_grupo, descricao) VALUES (@id, @descricao);
        PRINT 'Criado grupo ' + @descricao + ' = ' + CAST(@id AS VARCHAR(10));
    END
    SELECT @id = id_grupo FROM dbo.sis_grupo WHERE RTRIM(descricao) = @descricao;
    IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = @chave)
        INSERT INTO muralha.config_chave_valor (chave, valor) VALUES (@chave, CAST(@id AS VARCHAR(10)));
    FETCH NEXT FROM cur INTO @descricao, @chave;
END
CLOSE cur; DEALLOCATE cur;
GO

-- 5) Menus (lote unico: o RETURN interrompe tudo se os IDs ja estiverem ocupados)
IF EXISTS (SELECT 1 FROM dbo.sis_menu WHERE id_menu BETWEEN 379 AND 382
           AND acao NOT LIKE '/muralha-digital/pages/chamados/%')
   OR EXISTS (SELECT 1 FROM dbo.sis_menu_infos WHERE id_infos BETWEEN 46 AND 48
           AND href NOT LIKE '/muralha-digital/pages/chamados/%')
BEGIN
    RAISERROR('IDs de menu 379-382 ou infos 46-48 ja usados por outro modulo. Ajuste este script e ReferenciaEdital.java.', 16, 1);
    RETURN;
END

DECLARE @gSol INT = (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_solicitante');
DECLARE @gGes INT = (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_gestor');
DECLARE @gEqu INT = (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_equipe');

INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
SELECT v.id, v.descricao, v.acao, 'U', 1, 222, v.menu, 1, 'Muralha-Digital'
FROM (VALUES
 (379, 'Chamados',                 '/muralha-digital/pages/chamados/index.jsp',        'Chamados'),
 (380, 'Detalhe do Chamado',       '/muralha-digital/pages/chamados/detalhe.jsp',      'Detalhe do Chamado'),
 (381, 'Relatorios de Chamados',   '/muralha-digital/pages/chamados/relatorios.jsp',   'Relatorios de Chamados'),
 (382, 'Configuracao de Chamados', '/muralha-digital/pages/chamados/configuracao.jsp', 'Configuracao Chamados')
) v(id, descricao, acao, menu)
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu m WHERE m.id_menu = v.id);

-- Cards: 16 = Cadastro e Configuracao, 15 = Analise de Dados (detalhe.jsp nao tem card)
INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
SELECT v.id_infos, v.id_menu, v.src, v.descricao, v.href, v.det, v.ord, v.pai
FROM (VALUES
 (46, 379, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg', 'Chamados',
      '/muralha-digital/pages/chamados/index.jsp', 'Abertura e acompanhamento de chamados de suporte', 11, 16),
 (47, 381, '/muralha-digital/assets/images/analise/analise.jpg', 'Relatorios de Chamados',
      '/muralha-digital/pages/chamados/relatorios.jsp', 'Indicadores de atendimento por periodo, categoria e equipamento', 7, 15),
 (48, 382, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg', 'Configuracao de Chamados',
      '/muralha-digital/pages/chamados/configuracao.jsp', 'Prioridades, prazos e categorias de chamados', 12, 16)
) v(id_infos, id_menu, src, descricao, href, det, ord, pai)
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu_infos i WHERE i.id_infos = v.id_infos);

-- Direitos: 379/380 = todos os perfis; 381 = gestor e equipe; 382 = equipe. Grupo 11 = Administradores.
INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT v.g, NULL, v.m
FROM (VALUES (@gSol, 379), (@gGes, 379), (@gEqu, 379), (11, 379),
             (@gSol, 380), (@gGes, 380), (@gEqu, 380), (11, 380),
             (@gGes, 381), (@gEqu, 381), (11, 381),
             (@gEqu, 382), (11, 382)) v(g, m)
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos d WHERE d.id_grupo = v.g AND d.id_menu = v.m);
PRINT 'Menus e direitos verificados';
GO
```

- [ ] **Step 3: Pedir autorização ao responsável e executar no DEV**

O banco `GTW_MURALHA_DEV` é somente leitura sem autorização (CLAUDE.md). Mostre os dois scripts ao responsável; só com o "pode executar":

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -P $env:GTW_DB_PASSWORD -i "docs\banco-de-dados\migracoes\20261015_sistema_chamados.sql"
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -P $env:GTW_DB_PASSWORD -i "docs\banco-de-dados\migracoes\20261015_sistema_chamados_seed.sql"
```

Expected: mensagens `Criada ...`/`verificad...`, sem erro. Rodar de novo deve imprimir `Ja existe ...` (idempotência).

- [ ] **Step 4: Verificar**

```sql
SELECT codigo, minutos_atendimento, minutos_resolucao FROM muralha.chamado_prioridade ORDER BY ordem;   -- 4 linhas
SELECT COUNT(*) FROM muralha.chamado_categoria;                                                          -- 22
SELECT chave, valor FROM muralha.config_chave_valor WHERE chave LIKE 'chamado[_]%';                      -- 8 linhas
SELECT id_menu, acao FROM dbo.sis_menu WHERE id_menu BETWEEN 379 AND 382;                                -- 4 linhas
```

- [ ] **Step 5: Associar usuários de teste aos grupos** (com autorização)

```sql
-- Substituir os IDs de usuario pelos usuarios de teste combinados com o responsavel
INSERT INTO dbo.sis_usuario_grupo (id_usuario, id_grupo)
SELECT 0 /* id_usuario solicitante de teste */, CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_solicitante';
```

- [ ] **Step 6: Commit**

```bash
git add docs/banco-de-dados/migracoes/20261015_sistema_chamados.sql docs/banco-de-dados/migracoes/20261015_sistema_chamados_seed.sql
git commit -m "Adiciona migracao e seed do sistema de chamados (plano 22)

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 2: Domínio — estados, ações, perfis e máquina de estados

**Files:**
- Create: `src/main/java/muralha/digital/chamado/StatusChamado.java`, `AcaoChamado.java`, `PerfilChamado.java`, `RegraChamadoException.java`, `ChamadoNaoEncontradoException.java`, `MaquinaEstados.java`
- Test: `src/test/java/muralha/digital/chamado/MaquinaEstadosTest.java`

**Interfaces:**
- Produces: `enum StatusChamado { ABERTO, EM_ATENDIMENTO, AGUARDANDO_SOLICITANTE, RESOLVIDO, FECHADO, CANCELADO; boolean isTerminal(); }`; `enum AcaoChamado { ASSUMIR, INICIAR, AGUARDAR, RESPONDER, RESOLVER, AVALIAR, REABRIR, CANCELAR, RECLASSIFICAR; String getCodigo(); boolean exigeTexto(); static AcaoChamado deCodigo(String); }`; `enum PerfilChamado { SOLICITANTE, GESTOR, EQUIPE; static boolean veTodos(Set<PerfilChamado>); }`; `MaquinaEstados.destino(StatusChamado, AcaoChamado, Set<PerfilChamado>, boolean autor) : StatusChamado` (lança `RegraChamadoException`); `MaquinaEstados.permitidas(StatusChamado, Set<PerfilChamado>, boolean autor) : List<AcaoChamado>`.

- [ ] **Step 1: Escrever o teste que falha**

```java
// src/test/java/muralha/digital/chamado/MaquinaEstadosTest.java
package muralha.digital.chamado;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static muralha.digital.chamado.AcaoChamado.*;
import static muralha.digital.chamado.StatusChamado.*;
import static org.testng.Assert.*;

public class MaquinaEstadosTest {

    private static final Set<PerfilChamado> SOL = EnumSet.of(PerfilChamado.SOLICITANTE);
    private static final Set<PerfilChamado> GES = EnumSet.of(PerfilChamado.GESTOR);
    private static final Set<PerfilChamado> EQU = EnumSet.of(PerfilChamado.EQUIPE);

    @DataProvider
    public Object[][] permitidas() {
        return new Object[][] {
            // atual, acao, perfis, autor, destino esperado
            {ABERTO, ASSUMIR, EQU, false, ABERTO},
            {EM_ATENDIMENTO, ASSUMIR, EQU, false, EM_ATENDIMENTO},
            {ABERTO, INICIAR, EQU, false, EM_ATENDIMENTO},
            {EM_ATENDIMENTO, AGUARDAR, EQU, false, AGUARDANDO_SOLICITANTE},
            {AGUARDANDO_SOLICITANTE, RESPONDER, SOL, true, EM_ATENDIMENTO},
            {AGUARDANDO_SOLICITANTE, RESPONDER, GES, false, EM_ATENDIMENTO},
            {EM_ATENDIMENTO, RESOLVER, EQU, false, RESOLVIDO},
            {RESOLVIDO, AVALIAR, SOL, true, FECHADO},
            {RESOLVIDO, REABRIR, SOL, true, EM_ATENDIMENTO},
            {ABERTO, CANCELAR, SOL, true, CANCELADO},
            {ABERTO, CANCELAR, EQU, false, CANCELADO},
            {EM_ATENDIMENTO, CANCELAR, EQU, false, CANCELADO},
            {AGUARDANDO_SOLICITANTE, CANCELAR, EQU, false, CANCELADO},
            {ABERTO, RECLASSIFICAR, EQU, false, ABERTO},
            {RESOLVIDO, RECLASSIFICAR, EQU, false, RESOLVIDO},
        };
    }

    @Test(dataProvider = "permitidas")
    public void transicaoPermitida(StatusChamado atual, AcaoChamado acao, Set<PerfilChamado> perfis,
                                   boolean autor, StatusChamado esperado) {
        assertEquals(MaquinaEstados.destino(atual, acao, perfis, autor), esperado);
    }

    @DataProvider
    public Object[][] negadas() {
        return new Object[][] {
            {ABERTO, INICIAR, SOL, true},                    // solicitante nao inicia atendimento
            {ABERTO, INICIAR, GES, false},                   // gestor da CONTRATANTE nao atende
            {EM_ATENDIMENTO, CANCELAR, SOL, true},           // autor so cancela enquanto ABERTO
            {RESOLVIDO, AVALIAR, GES, false},                // gestor nao avalia chamado de outro
            {RESOLVIDO, AVALIAR, EQU, false},                // equipe nao se autoavalia
            {RESOLVIDO, REABRIR, EQU, false},
            {AGUARDANDO_SOLICITANTE, RESPONDER, SOL, false}, // solicitante que nao e o autor
            {FECHADO, RECLASSIFICAR, EQU, false},            // terminal
            {CANCELADO, CANCELAR, EQU, false},
            {FECHADO, REABRIR, SOL, true},
            {EM_ATENDIMENTO, RESOLVER, SOL, true},
            {ABERTO, AGUARDAR, EQU, false},
        };
    }

    @Test(dataProvider = "negadas", expectedExceptions = RegraChamadoException.class)
    public void transicaoNegada(StatusChamado atual, AcaoChamado acao, Set<PerfilChamado> perfis, boolean autor) {
        MaquinaEstados.destino(atual, acao, perfis, autor);
    }

    @Test
    public void permitidasParaSolicitanteAutorEmResolvido() {
        List<AcaoChamado> acoes = MaquinaEstados.permitidas(RESOLVIDO, SOL, true);
        assertEquals(acoes, List.of(AVALIAR, REABRIR));
    }

    @Test
    public void uniaoDePerfisSomaPermissoes() {
        Set<PerfilChamado> ambos = EnumSet.of(PerfilChamado.SOLICITANTE, PerfilChamado.EQUIPE);
        assertEquals(MaquinaEstados.destino(ABERTO, INICIAR, ambos, true), EM_ATENDIMENTO);
    }

    @Test
    public void veTodosSomenteGestorOuEquipe() {
        assertFalse(PerfilChamado.veTodos(SOL));
        assertTrue(PerfilChamado.veTodos(GES));
        assertTrue(PerfilChamado.veTodos(EQU));
    }

    @Test
    public void codigoDaAcao() {
        assertEquals(AcaoChamado.deCodigo("reclassificar"), RECLASSIFICAR);
        assertNull(AcaoChamado.deCodigo("abrir"));
        assertFalse(ASSUMIR.exigeTexto());
        assertFalse(AVALIAR.exigeTexto());
        assertTrue(RESOLVER.exigeTexto());
    }
}
```

- [ ] **Step 2: Rodar e ver falhar**

Run: `mvn -o -s "$s\settings-legacy-archiva.xml" '-Dexec.skip=true' '-Dtest=MaquinaEstadosTest' '-DfailIfNoTests=false' test`
Expected: FAIL de compilação (`cannot find symbol: class MaquinaEstados`).

- [ ] **Step 3: Implementar**

```java
// src/main/java/muralha/digital/chamado/StatusChamado.java
package muralha.digital.chamado;

public enum StatusChamado {
    ABERTO, EM_ATENDIMENTO, AGUARDANDO_SOLICITANTE, RESOLVIDO, FECHADO, CANCELADO;

    public boolean isTerminal() {
        return this == FECHADO || this == CANCELADO;
    }
}
```

```java
// src/main/java/muralha/digital/chamado/AcaoChamado.java
package muralha.digital.chamado;

public enum AcaoChamado {
    ASSUMIR("assumir"), INICIAR("iniciar"), AGUARDAR("aguardar"), RESPONDER("responder"),
    RESOLVER("resolver"), AVALIAR("avaliar"), REABRIR("reabrir"), CANCELAR("cancelar"),
    RECLASSIFICAR("reclassificar");

    private final String codigo;

    AcaoChamado(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /** Observacao obrigatoria na transicao (spec §1.2). */
    public boolean exigeTexto() {
        return this != ASSUMIR && this != AVALIAR;
    }

    public static AcaoChamado deCodigo(String codigo) {
        for (AcaoChamado a : values()) {
            if (a.codigo.equals(codigo)) return a;
        }
        return null;
    }
}
```

```java
// src/main/java/muralha/digital/chamado/PerfilChamado.java
package muralha.digital.chamado;

import java.util.Set;

public enum PerfilChamado {
    SOLICITANTE, GESTOR, EQUIPE;

    /** Gestor do contrato e equipe tecnica enxergam todos os chamados (spec D9). */
    public static boolean veTodos(Set<PerfilChamado> perfis) {
        return perfis.contains(GESTOR) || perfis.contains(EQUIPE);
    }
}
```

```java
// src/main/java/muralha/digital/chamado/RegraChamadoException.java
package muralha.digital.chamado;

/** Violacao de regra de negocio; a mensagem e exibida ao usuario. */
public class RegraChamadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RegraChamadoException(String mensagem) {
        super(mensagem);
    }
}
```

```java
// src/main/java/muralha/digital/chamado/ChamadoNaoEncontradoException.java
package muralha.digital.chamado;

/** Chamado inexistente ou fora da visibilidade do usuario (respondido como 404). */
public class ChamadoNaoEncontradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ChamadoNaoEncontradoException(String protocolo) {
        super("Chamado não encontrado: " + protocolo);
    }
}
```

```java
// src/main/java/muralha/digital/chamado/MaquinaEstados.java
package muralha.digital.chamado;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static muralha.digital.chamado.StatusChamado.*;

/** Tabela de transicoes da spec §1.2. Pura: sem banco, sem sessao. */
public final class MaquinaEstados {

    private MaquinaEstados() {
    }

    public static StatusChamado destino(StatusChamado atual, AcaoChamado acao,
                                        Set<PerfilChamado> perfis, boolean autor) {
        boolean equipe = perfis.contains(PerfilChamado.EQUIPE);
        boolean gestor = perfis.contains(PerfilChamado.GESTOR);
        switch (acao) {
            case ASSUMIR:
                if (equipe && (atual == ABERTO || atual == EM_ATENDIMENTO)) return atual;
                break;
            case INICIAR:
                if (equipe && atual == ABERTO) return EM_ATENDIMENTO;
                break;
            case AGUARDAR:
                if (equipe && atual == EM_ATENDIMENTO) return AGUARDANDO_SOLICITANTE;
                break;
            case RESPONDER:
                if ((autor || gestor) && atual == AGUARDANDO_SOLICITANTE) return EM_ATENDIMENTO;
                break;
            case RESOLVER:
                if (equipe && atual == EM_ATENDIMENTO) return RESOLVIDO;
                break;
            case AVALIAR:
                if (autor && atual == RESOLVIDO) return FECHADO;
                break;
            case REABRIR:
                if (autor && atual == RESOLVIDO) return EM_ATENDIMENTO;
                break;
            case CANCELAR:
                if (autor && atual == ABERTO) return CANCELADO;
                if (equipe && (atual == ABERTO || atual == EM_ATENDIMENTO || atual == AGUARDANDO_SOLICITANTE)) {
                    return CANCELADO;
                }
                break;
            case RECLASSIFICAR:
                if (equipe && !atual.isTerminal()) return atual;
                break;
            default:
                break;
        }
        throw new RegraChamadoException("Ação \"" + acao.getCodigo() + "\" não permitida neste momento.");
    }

    public static List<AcaoChamado> permitidas(StatusChamado atual, Set<PerfilChamado> perfis, boolean autor) {
        List<AcaoChamado> acoes = new ArrayList<>();
        for (AcaoChamado a : AcaoChamado.values()) {
            try {
                destino(atual, a, perfis, autor);
                acoes.add(a);
            } catch (RegraChamadoException ignorada) {
                // acao nao permitida: fica fora da lista
            }
        }
        return acoes;
    }
}
```

- [ ] **Step 4: Rodar e ver passar**

Run: mesmo comando do Step 2. Expected: `Tests run: 31, Failures: 0`.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/muralha/digital/chamado/ src/test/java/muralha/digital/chamado/MaquinaEstadosTest.java
git commit -m "Adiciona maquina de estados do sistema de chamados

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 3: Cálculo de SLA e protocolo

**Files:**
- Create: `src/main/java/muralha/digital/chamado/CalculadoraSla.java`, `GeradorProtocolo.java`
- Test: `src/test/java/muralha/digital/chamado/CalculadoraSlaTest.java`, `GeradorProtocoloTest.java`

**Interfaces:**
- Produces: `CalculadoraSla.Marca { NO_PRAZO, EM_RISCO, VENCIDO, CUMPRIDO, VIOLADO }`; `prazoAtendimento(LocalDateTime abertura, int minutosAtendimento) : LocalDateTime`; `prazoResolucao(LocalDateTime abertura, int minutosResolucao, int minutosPausados) : LocalDateTime`; `minutosEntre(LocalDateTime, LocalDateTime) : int`; `marca(LocalDateTime abertura, LocalDateTime prazo, LocalDateTime cumpridoEm, LocalDateTime agora, int percentualRisco, boolean pausado, Marca atual) : Marca`; `GeradorProtocolo.formatar(int ano, int numero) : String`; `valido(String) : boolean`; `proximo(Connection, int ano) : String`.

- [ ] **Step 1: Escrever os testes que falham**

```java
// src/test/java/muralha/digital/chamado/CalculadoraSlaTest.java
package muralha.digital.chamado;

import muralha.digital.chamado.CalculadoraSla.Marca;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

public class CalculadoraSlaTest {

    private static final LocalDateTime ABERTURA = LocalDateTime.of(2026, 10, 15, 8, 0);

    @Test
    public void prazosDaPrioridadeCritica() {
        assertEquals(CalculadoraSla.prazoAtendimento(ABERTURA, 60), LocalDateTime.of(2026, 10, 15, 9, 0));
        assertEquals(CalculadoraSla.prazoResolucao(ABERTURA, 240, 0), LocalDateTime.of(2026, 10, 15, 12, 0));
    }

    @Test
    public void prazoDeResolucaoSomaMinutosPausados() {
        assertEquals(CalculadoraSla.prazoResolucao(ABERTURA, 240, 90), LocalDateTime.of(2026, 10, 15, 13, 30));
    }

    @Test
    public void prazoAtravessaDiasCorridos() {
        // 24x7: Baixa = 4320 min = 3 dias corridos, sem pular fim de semana
        assertEquals(CalculadoraSla.prazoResolucao(ABERTURA, 4320, 0), LocalDateTime.of(2026, 10, 18, 8, 0));
    }

    @Test
    public void minutosEntreNuncaNegativo() {
        assertEquals(CalculadoraSla.minutosEntre(ABERTURA, ABERTURA.plusMinutes(95)), 95);
        assertEquals(CalculadoraSla.minutosEntre(ABERTURA, ABERTURA.minusMinutes(5)), 0);
    }

    @Test
    public void marcaNoPrazoAntesDoLimiarDeRisco() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, null, ABERTURA.plusMinutes(79), 80, false, null), Marca.NO_PRAZO);
    }

    @Test
    public void marcaEmRiscoNoLimiar() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, null, ABERTURA.plusMinutes(80), 80, false, Marca.NO_PRAZO), Marca.EM_RISCO);
    }

    @Test
    public void marcaVencidoAposPrazo() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, null, ABERTURA.plusMinutes(101), 80, false, Marca.EM_RISCO), Marca.VENCIDO);
    }

    @Test
    public void cumpridoNoLimiteExatoEVioladoDepois() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, prazo, prazo.plusDays(1), 80, false, Marca.VENCIDO), Marca.CUMPRIDO);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, prazo.plusSeconds(1), prazo, 80, false, Marca.VENCIDO), Marca.VIOLADO);
    }

    @Test
    public void pausadoMantemMarcaAtual() {
        LocalDateTime prazo = ABERTURA.plusMinutes(100);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, null, ABERTURA.plusMinutes(500), 80, true, Marca.EM_RISCO), Marca.EM_RISCO);
        assertEquals(CalculadoraSla.marca(ABERTURA, prazo, null, ABERTURA.plusMinutes(500), 80, true, null), Marca.NO_PRAZO);
    }
}
```

```java
// src/test/java/muralha/digital/chamado/GeradorProtocoloTest.java
package muralha.digital.chamado;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class GeradorProtocoloTest {

    @Test
    public void formataComZerosAEsquerda() {
        assertEquals(GeradorProtocolo.formatar(2026, 123), "CH-2026-000123");
    }

    @Test
    public void viradaDeAnoRecomecaNumeracao() {
        assertEquals(GeradorProtocolo.formatar(2027, 1), "CH-2027-000001");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void numeroForaDaFaixa() {
        GeradorProtocolo.formatar(2026, 1_000_000);
    }

    @Test
    public void validaFormato() {
        assertTrue(GeradorProtocolo.valido("CH-2026-000123"));
        assertFalse(GeradorProtocolo.valido("CH-2026-123"));
        assertFalse(GeradorProtocolo.valido("../CH-2026-000123"));
        assertFalse(GeradorProtocolo.valido(null));
    }
}
```

- [ ] **Step 2: Rodar e ver falhar**

Run: `mvn -o -s "$s\settings-legacy-archiva.xml" '-Dexec.skip=true' '-Dtest=CalculadoraSlaTest,GeradorProtocoloTest' '-DfailIfNoTests=false' test`
Expected: FAIL de compilação (`cannot find symbol: class CalculadoraSla`).

- [ ] **Step 3: Implementar**

```java
// src/main/java/muralha/digital/chamado/CalculadoraSla.java
package muralha.digital.chamado;

import java.time.Duration;
import java.time.LocalDateTime;

/** Prazos e marcas de SLA em horas corridas 24x7 (spec §1.3). Pura. */
public final class CalculadoraSla {

    public enum Marca { NO_PRAZO, EM_RISCO, VENCIDO, CUMPRIDO, VIOLADO }

    private CalculadoraSla() {
    }

    public static LocalDateTime prazoAtendimento(LocalDateTime abertura, int minutosAtendimento) {
        return abertura.plusMinutes(minutosAtendimento);
    }

    public static LocalDateTime prazoResolucao(LocalDateTime abertura, int minutosResolucao, int minutosPausados) {
        return abertura.plusMinutes((long) minutosResolucao + minutosPausados);
    }

    public static int minutosEntre(LocalDateTime inicio, LocalDateTime fim) {
        long minutos = Duration.between(inicio, fim).toMinutes();
        return minutos < 0 ? 0 : (int) minutos;
    }

    /**
     * @param cumpridoEm momento em que o prazo foi cumprido (primeiro atendimento ou resolucao); null se ainda nao
     * @param pausado    relogio parado (aguardando solicitante): a marca nao evolui
     * @param atual      marca vigente, preservada enquanto pausado
     */
    public static Marca marca(LocalDateTime abertura, LocalDateTime prazo, LocalDateTime cumpridoEm,
                              LocalDateTime agora, int percentualRisco, boolean pausado, Marca atual) {
        if (cumpridoEm != null) {
            return cumpridoEm.isAfter(prazo) ? Marca.VIOLADO : Marca.CUMPRIDO;
        }
        if (pausado) {
            return atual == null ? Marca.NO_PRAZO : atual;
        }
        if (agora.isAfter(prazo)) {
            return Marca.VENCIDO;
        }
        long total = Duration.between(abertura, prazo).toSeconds();
        long consumido = Duration.between(abertura, agora).toSeconds();
        if (total > 0 && consumido * 100 >= total * percentualRisco) {
            return Marca.EM_RISCO;
        }
        return Marca.NO_PRAZO;
    }
}
```

```java
// src/main/java/muralha/digital/chamado/GeradorProtocolo.java
package muralha.digital.chamado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

/** Protocolo CH-AAAA-NNNNNN com contador por ano em muralha.chamado_protocolo (spec §1.4). */
public final class GeradorProtocolo {

    private static final Pattern FORMATO = Pattern.compile("^CH-\\d{4}-\\d{6}$");
    private static final int VIOLACAO_PK = 2627;

    private GeradorProtocolo() {
    }

    public static String formatar(int ano, int numero) {
        if (numero < 1 || numero > 999_999) {
            throw new IllegalArgumentException("Número de protocolo fora da faixa: " + numero);
        }
        return String.format("CH-%04d-%06d", ano, numero);
    }

    public static boolean valido(String protocolo) {
        return protocolo != null && FORMATO.matcher(protocolo).matches();
    }

    /** Deve ser chamado dentro da transacao que grava o chamado. */
    public static String proximo(Connection conn, int ano) throws SQLException {
        Integer numero = incrementar(conn, ano);
        if (numero == null) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.chamado_protocolo (ano, ultimo) VALUES (?, 1)")) {
                ps.setInt(1, ano);
                ps.executeUpdate();
                numero = 1;
            } catch (SQLException e) {
                if (e.getErrorCode() != VIOLACAO_PK) throw e;
                numero = incrementar(conn, ano); // outra transacao criou o ano antes
            }
        }
        return formatar(ano, numero);
    }

    private static Integer incrementar(Connection conn, int ano) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE muralha.chamado_protocolo WITH (UPDLOCK, HOLDLOCK) SET ultimo = ultimo + 1 " +
                "OUTPUT inserted.ultimo WHERE ano = ?")) {
            ps.setInt(1, ano);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }
}
```

- [ ] **Step 4: Rodar e ver passar**

Run: mesmo comando do Step 2. Expected: `Tests run: 13, Failures: 0`.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/muralha/digital/chamado/CalculadoraSla.java src/main/java/muralha/digital/chamado/GeradorProtocolo.java src/test/java/muralha/digital/chamado/CalculadoraSlaTest.java src/test/java/muralha/digital/chamado/GeradorProtocoloTest.java
git commit -m "Adiciona calculo de SLA 24x7 e gerador de protocolo dos chamados

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 4: Armazenamento de anexos

**Files:**
- Create: `src/main/java/muralha/digital/chamado/ArquivoEnviado.java`, `AnexoGravado.java`, `ArmazenamentoAnexo.java`
- Test: `src/test/java/muralha/digital/chamado/ArmazenamentoAnexoTest.java`

**Interfaces:**
- Consumes: `GeradorProtocolo.valido(String)`, `RegraChamadoException`, `muralha.digital.util.HashUtil.sha256Hex(byte[])`.
- Produces: `new ArquivoEnviado(String nome, byte[] conteudo)` (campos `nome`, `conteudo`); `AnexoGravado` (campos `nomeOriginal`, `caminhoRelativo`, `tipoMime`, `tamanho`, `sha256`); `new ArmazenamentoAnexo(Path raiz, long maxBytes)`; `gravar(String protocolo, ArquivoEnviado) : AnexoGravado`; `resolver(String caminhoRelativo) : Path`; `removerSilenciosamente(String caminhoRelativo)`; `static validarQuantidade(List<ArquivoEnviado>)`; `MAX_ARQUIVOS = 5`.

- [ ] **Step 1: Escrever o teste que falha**

```java
// src/test/java/muralha/digital/chamado/ArmazenamentoAnexoTest.java
package muralha.digital.chamado;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.testng.Assert.*;

public class ArmazenamentoAnexoTest {

    private static final byte[] PDF = {0x25, 0x50, 0x44, 0x46, 0x2D, 0x31};
    private static final byte[] PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A};
    private static final byte[] EXE = {0x4D, 0x5A, (byte) 0x90, 0x00};

    private Path raiz;
    private ArmazenamentoAnexo armazenamento;

    @BeforeMethod
    public void preparar() throws Exception {
        raiz = Files.createTempDirectory("anexos-chamado");
        armazenamento = new ArmazenamentoAnexo(raiz, 1024);
    }

    @Test
    public void gravaPdfDentroDaPastaDoProtocolo() throws Exception {
        AnexoGravado a = armazenamento.gravar("CH-2026-000001", new ArquivoEnviado("Relatório.PDF", PDF));
        assertTrue(a.caminhoRelativo.startsWith("2026/CH-2026-000001/"));
        assertTrue(a.caminhoRelativo.endsWith(".pdf"));
        assertEquals(a.tipoMime, "application/pdf");
        assertEquals(a.nomeOriginal, "Relatório.PDF");
        assertEquals(a.tamanho, PDF.length);
        assertEquals(a.sha256.length(), 64);
        assertTrue(Files.exists(raiz.resolve(a.caminhoRelativo)));
    }

    @Test
    public void nomeComCaminhoGuardaSoONomeDoArquivo() throws Exception {
        AnexoGravado a = armazenamento.gravar("CH-2026-000001", new ArquivoEnviado("..\\..\\windows\\foto.png", PNG));
        assertEquals(a.nomeOriginal, "foto.png");
        assertTrue(raiz.resolve(a.caminhoRelativo).normalize().startsWith(raiz));
    }

    @Test(expectedExceptions = RegraChamadoException.class, expectedExceptionsMessageRegExp = ".*não corresponde.*")
    public void executavelRenomeadoParaPdfERejeitado() throws Exception {
        armazenamento.gravar("CH-2026-000001", new ArquivoEnviado("nota.pdf", EXE));
    }

    @Test(expectedExceptions = RegraChamadoException.class, expectedExceptionsMessageRegExp = ".*não permitido.*")
    public void extensaoForaDaListaERejeitada() throws Exception {
        armazenamento.gravar("CH-2026-000001", new ArquivoEnviado("script.js", PDF));
    }

    @Test(expectedExceptions = RegraChamadoException.class, expectedExceptionsMessageRegExp = ".*limite.*")
    public void arquivoAcimaDoLimiteERejeitado() throws Exception {
        byte[] grande = new byte[2048];
        System.arraycopy(PDF, 0, grande, 0, PDF.length);
        armazenamento.gravar("CH-2026-000001", new ArquivoEnviado("grande.pdf", grande));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void protocoloInvalidoNaoViraCaminho() throws Exception {
        armazenamento.gravar("../../etc", new ArquivoEnviado("a.pdf", PDF));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void resolverNaoSaiDaRaiz() {
        armazenamento.resolver("../fora.pdf");
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void maisDeCincoArquivosERejeitado() {
        ArmazenamentoAnexo.validarQuantidade(Collections.nCopies(6, new ArquivoEnviado("a.pdf", PDF)));
    }
}
```

- [ ] **Step 2: Rodar e ver falhar**

Run: `mvn -o -s "$s\settings-legacy-archiva.xml" '-Dexec.skip=true' '-Dtest=ArmazenamentoAnexoTest' '-DfailIfNoTests=false' test`
Expected: FAIL de compilação (`cannot find symbol: class ArmazenamentoAnexo`).

- [ ] **Step 3: Implementar**

```java
// src/main/java/muralha/digital/chamado/ArquivoEnviado.java
package muralha.digital.chamado;

/** Arquivo recebido em um upload multipart. */
public final class ArquivoEnviado {
    public final String nome;
    public final byte[] conteudo;

    public ArquivoEnviado(String nome, byte[] conteudo) {
        this.nome = nome;
        this.conteudo = conteudo;
    }
}
```

```java
// src/main/java/muralha/digital/chamado/AnexoGravado.java
package muralha.digital.chamado;

/** Anexo ja gravado em disco, pronto para ser registrado em muralha.chamado_anexo. */
public final class AnexoGravado {
    public final String nomeOriginal;
    public final String caminhoRelativo;
    public final String tipoMime;
    public final long tamanho;
    public final String sha256;

    public AnexoGravado(String nomeOriginal, String caminhoRelativo, String tipoMime, long tamanho, String sha256) {
        this.nomeOriginal = nomeOriginal;
        this.caminhoRelativo = caminhoRelativo;
        this.tipoMime = tipoMime;
        this.tamanho = tamanho;
        this.sha256 = sha256;
    }
}
```

```java
// src/main/java/muralha/digital/chamado/ArmazenamentoAnexo.java
package muralha.digital.chamado;

import muralha.digital.util.HashUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** Valida e grava anexos fora do webapp (spec §3.4). */
public final class ArmazenamentoAnexo {

    public static final int MAX_ARQUIVOS = 5;

    private static final Map<String, String> MIME = Map.of(
            "pdf", "application/pdf",
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg");

    private final Path raiz;
    private final long maxBytes;

    public ArmazenamentoAnexo(Path raiz, long maxBytes) {
        this.raiz = raiz.toAbsolutePath().normalize();
        this.maxBytes = maxBytes;
    }

    public static void validarQuantidade(List<ArquivoEnviado> arquivos) {
        if (arquivos != null && arquivos.size() > MAX_ARQUIVOS) {
            throw new RegraChamadoException("Envie no máximo " + MAX_ARQUIVOS + " arquivos por vez.");
        }
    }

    public AnexoGravado gravar(String protocolo, ArquivoEnviado arquivo) throws IOException {
        if (!GeradorProtocolo.valido(protocolo)) {
            throw new IllegalArgumentException("Protocolo inválido: " + protocolo);
        }
        String nome = nomeLimpo(arquivo.nome);
        String ext = validar(nome, arquivo.conteudo);
        String relativo = protocolo.substring(3, 7) + "/" + protocolo + "/" + UUID.randomUUID() + "." + ext;
        Path destino = resolver(relativo);
        Files.createDirectories(destino.getParent());
        Files.write(destino, arquivo.conteudo, StandardOpenOption.CREATE_NEW);
        return new AnexoGravado(nome, relativo, MIME.get(ext), arquivo.conteudo.length,
                HashUtil.sha256Hex(arquivo.conteudo));
    }

    public Path resolver(String caminhoRelativo) {
        Path p = raiz.resolve(caminhoRelativo).normalize();
        if (!p.startsWith(raiz)) {
            throw new IllegalArgumentException("Caminho fora do diretório de anexos: " + caminhoRelativo);
        }
        return p;
    }

    public void removerSilenciosamente(String caminhoRelativo) {
        try {
            Files.deleteIfExists(resolver(caminhoRelativo));
        } catch (Exception ignorada) {
            // limpeza de melhor esforco apos rollback
        }
    }

    private String validar(String nome, byte[] conteudo) {
        if (conteudo == null || conteudo.length == 0) {
            throw new RegraChamadoException("Arquivo vazio: " + nome);
        }
        if (conteudo.length > maxBytes) {
            throw new RegraChamadoException("Arquivo acima do limite de " + Math.max(1, maxBytes / (1024 * 1024))
                    + " MB: " + nome);
        }
        String ext = extensao(nome);
        if (!MIME.containsKey(ext)) {
            throw new RegraChamadoException("Tipo de arquivo não permitido (use PDF, PNG ou JPG): " + nome);
        }
        if (!assinaturaConfere(ext, conteudo)) {
            throw new RegraChamadoException("O conteúdo não corresponde à extensão do arquivo: " + nome);
        }
        return ext;
    }

    static String nomeLimpo(String nome) {
        String n = nome == null || nome.trim().isEmpty() ? "arquivo" : nome.replace('\\', '/');
        n = n.substring(n.lastIndexOf('/') + 1);
        return n.length() > 255 ? n.substring(n.length() - 255) : n;
    }

    static String extensao(String nome) {
        int i = nome.lastIndexOf('.');
        return i < 0 ? "" : nome.substring(i + 1).toLowerCase(Locale.ROOT);
    }

    static boolean assinaturaConfere(String ext, byte[] c) {
        switch (ext) {
            case "pdf":
                return comecaCom(c, 0x25, 0x50, 0x44, 0x46);   // %PDF
            case "png":
                return comecaCom(c, 0x89, 0x50, 0x4E, 0x47);   // .PNG
            case "jpg":
            case "jpeg":
                return comecaCom(c, 0xFF, 0xD8, 0xFF);
            default:
                return false;
        }
    }

    private static boolean comecaCom(byte[] c, int... assinatura) {
        if (c.length < assinatura.length) return false;
        for (int i = 0; i < assinatura.length; i++) {
            if ((c[i] & 0xFF) != assinatura[i]) return false;
        }
        return true;
    }
}
```

- [ ] **Step 4: Rodar e ver passar**

Run: mesmo comando do Step 2. Expected: `Tests run: 8, Failures: 0`.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/muralha/digital/chamado/ArquivoEnviado.java src/main/java/muralha/digital/chamado/AnexoGravado.java src/main/java/muralha/digital/chamado/ArmazenamentoAnexo.java src/test/java/muralha/digital/chamado/ArmazenamentoAnexoTest.java
git commit -m "Adiciona armazenamento validado de anexos dos chamados

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 5: Exportação (CSV/XLSX/PDF) e e-mail

**Files:**
- Create: `src/main/java/muralha/digital/chamado/ExportacaoChamados.java`, `EmailChamado.java`
- Modify: `src/main/java/muralha/digital/notificacao/ServicoEmailMuralha.java` (novo método antes do `}` final, linha 325)
- Test: `src/test/java/muralha/digital/chamado/ExportacaoChamadosTest.java`, `EmailChamadoTest.java`

**Interfaces:**
- Consumes: `HashUtil.sha256Hex`, Apache POI (`SXSSFWorkbook`), `ConfiguracaoProvider.getInstance().getListaServidoresSmtp()`, `ServicoEmailMuralha(String user, String password, String host, Integer port)`.
- Produces: `ExportacaoChamados.LISTA_CHAVES/LISTA_ROTULOS`, `INDICADORES_CHAVES/INDICADORES_ROTULOS` (`List<String>`); `hashDados(List<String> chaves, List<String> rotulos, JsonArray linhas) : String`; `csv(List<String> chaves, List<String> rotulos, JsonArray linhas, String titulo, String filtros, String usuario, LocalDateTime agora) : byte[]`; `xlsx(mesmos parâmetros) : byte[]`; `EmailChamado.CONFIRMACAO_ABERTURA`, `RECLASSIFICACAO`, `MAX_TENTATIVAS`; `assunto(String tipo, String protocolo)`; `corpoHtml(String tipo, String protocolo, String titulo, String prioridade, LocalDateTime prazoAtendimento, LocalDateTime prazoResolucao, String justificativa)`; `statusAposTentativa(boolean enviado, int tentativasAntes) : String`; `enviar(String destinatario, String assunto, String html) : boolean`; `ServicoEmailMuralha#enviarEmailChamado(InternetAddress, List<InternetAddress>, String assunto, String corpoHtml) : boolean`.

- [ ] **Step 1: Escrever os testes que falham**

```java
// src/test/java/muralha/digital/chamado/ExportacaoChamadosTest.java
package muralha.digital.chamado;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.util.HashUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.testng.Assert.*;

public class ExportacaoChamadosTest {

    private static final List<String> CHAVES = List.of("protocolo", "titulo");
    private static final List<String> ROTULOS = List.of("Protocolo", "Título");
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 15, 10, 30);

    private static JsonArray linhas(String... titulos) {
        JsonArray arr = new JsonArray();
        int n = 1;
        for (String t : titulos) {
            JsonObject o = new JsonObject();
            o.addProperty("protocolo", GeradorProtocolo.formatar(2026, n++));
            o.addProperty("titulo", t);
            arr.add(o);
        }
        return arr;
    }

    private static String csvTexto(JsonArray linhas) {
        byte[] b = ExportacaoChamados.csv(CHAVES, ROTULOS, linhas, "Chamados", "todos", "joao", AGORA);
        assertEquals(b[0] & 0xFF, 0xEF, "CSV deve comecar com BOM UTF-8");
        return new String(b, 3, b.length - 3, StandardCharsets.UTF_8);
    }

    @Test
    public void rodapeTrazTotalEHashDosDados() {
        JsonArray dados = linhas("Câmera parada", "Lentidão");
        String texto = csvTexto(dados);
        assertTrue(texto.startsWith("# Chamados — todos — 15/10/2026 10:30:00 — joao\r\n"));
        assertTrue(texto.contains("# Total: 2\r\n"));
        String bloco = "Protocolo;Título\r\nCH-2026-000001;Câmera parada\r\nCH-2026-000002;Lentidão\r\n";
        assertTrue(texto.contains(bloco));
        assertTrue(texto.endsWith("# SHA-256: " + HashUtil.sha256Hex(bloco.getBytes(StandardCharsets.UTF_8)) + "\r\n"));
        assertEquals(ExportacaoChamados.hashDados(CHAVES, ROTULOS, dados),
                HashUtil.sha256Hex(bloco.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void escapaSeparadorAspasEQuebraDeLinha() {
        String texto = csvTexto(linhas("a;b \"c\"\nd"));
        assertTrue(texto.contains("CH-2026-000001;\"a;b \"\"c\"\" d\"\r\n"), texto);
    }

    @Test
    public void neutralizaInjecaoDeFormula() {
        String texto = csvTexto(linhas("=HYPERLINK(\"x\")", "+1", "-2", "@SUM(A1)"));
        assertTrue(texto.contains(";\"'=HYPERLINK(\"\"x\"\")\"\r\n"), texto);
        assertTrue(texto.contains(";'+1\r\n"));
        assertTrue(texto.contains(";'-2\r\n"));
        assertTrue(texto.contains(";'@SUM(A1)\r\n"));
    }

    @Test
    public void xlsxTemAbaDeDadosEAbaDeMetadados() throws Exception {
        JsonArray dados = linhas("Câmera parada");
        byte[] b = ExportacaoChamados.xlsx(CHAVES, ROTULOS, dados, "Chamados", "todos", "joao", AGORA);
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(b))) {
            assertEquals(wb.getSheetAt(0).getSheetName(), "Chamados");
            assertEquals(wb.getSheetAt(0).getRow(1).getCell(1).getStringCellValue(), "Câmera parada");
            assertEquals(wb.getSheetAt(1).getSheetName(), "Metadados");
            assertEquals(wb.getSheetAt(1).getRow(5).getCell(1).getStringCellValue(),
                    ExportacaoChamados.hashDados(CHAVES, ROTULOS, dados));
        }
    }
}
```

```java
// src/test/java/muralha/digital/chamado/EmailChamadoTest.java
package muralha.digital.chamado;

import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

public class EmailChamadoTest {

    private static final LocalDateTime PA = LocalDateTime.of(2026, 10, 15, 9, 0);
    private static final LocalDateTime PR = LocalDateTime.of(2026, 10, 15, 12, 0);

    @Test
    public void confirmacaoTrazProtocoloPrioridadeEPrazos() {
        String html = EmailChamado.corpoHtml(EmailChamado.CONFIRMACAO_ABERTURA, "CH-2026-000001",
                "Câmera parada", "Crítica", PA, PR, null);
        assertTrue(html.contains("CH-2026-000001"));
        assertTrue(html.contains("Crítica"));
        assertTrue(html.contains("15/10/2026 09:00"));
        assertTrue(html.contains("15/10/2026 12:00"));
        assertEquals(EmailChamado.assunto(EmailChamado.CONFIRMACAO_ABERTURA, "CH-2026-000001"),
                "[Chamado CH-2026-000001] Abertura confirmada");
    }

    @Test
    public void reclassificacaoTrazJustificativa() {
        String html = EmailChamado.corpoHtml(EmailChamado.RECLASSIFICACAO, "CH-2026-000001",
                "Câmera parada", "Alta", PA, PR, "Afeta só uma faixa");
        assertTrue(html.contains("Afeta só uma faixa"));
        assertEquals(EmailChamado.assunto(EmailChamado.RECLASSIFICACAO, "CH-2026-000001"),
                "[Chamado CH-2026-000001] Prioridade reclassificada");
    }

    @Test
    public void tituloComHtmlEEscapado() {
        String html = EmailChamado.corpoHtml(EmailChamado.CONFIRMACAO_ABERTURA, "CH-2026-000001",
                "<script>alert(1)</script>", "Alta", PA, PR, null);
        assertFalse(html.contains("<script>"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    @Test
    public void statusAposTentativa() {
        assertEquals(EmailChamado.statusAposTentativa(true, 3), "ENVIADO");
        assertEquals(EmailChamado.statusAposTentativa(false, 0), "PENDENTE");
        assertEquals(EmailChamado.statusAposTentativa(false, 3), "PENDENTE");
        assertEquals(EmailChamado.statusAposTentativa(false, 4), "ERRO");
    }
}
```

- [ ] **Step 2: Rodar e ver falhar**

Run: `mvn -o -s "$s\settings-legacy-archiva.xml" '-Dexec.skip=true' '-Dtest=ExportacaoChamadosTest,EmailChamadoTest' '-DfailIfNoTests=false' test`
Expected: FAIL de compilação (`cannot find symbol: class ExportacaoChamados`).

- [ ] **Step 3: Implementar `ExportacaoChamados`**

Não usa `dashboards.ExportacaoService` porque ele não aceita cabeçalho/rodapé nem aba de metadados; reaproveita `HashUtil` e Apache POI. O hash cobre apenas o bloco de dados (rótulos + linhas), por isso CSV, XLSX e PDF do mesmo conjunto exibem o **mesmo** SHA-256.

```java
// src/main/java/muralha/digital/chamado/ExportacaoChamados.java
package muralha.digital.chamado;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muralha.digital.util.HashUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Exportacao de chamados com SHA-256 dos dados (spec §3.6). */
public final class ExportacaoChamados {

    public static final List<String> LISTA_CHAVES = List.of("protocolo", "titulo", "categoria", "equipamento",
            "prioridade", "status", "solicitante", "responsavel", "abertura", "prazoAtendimento", "prazoResolucao",
            "slaAtendimento", "slaResolucao", "resolucao", "fechamento", "nota");
    public static final List<String> LISTA_ROTULOS = List.of("Protocolo", "Título", "Categoria", "Equipamento",
            "Prioridade", "Status", "Solicitante", "Responsável", "Abertura", "Prazo de atendimento",
            "Prazo de resolução", "SLA de atendimento", "SLA de resolução", "Resolução", "Fechamento", "Nota");

    public static final List<String> INDICADORES_CHAVES = List.of("grupo", "total", "resolvidos", "cancelados",
            "pctSlaAtendimento", "pctSlaResolucao", "tmaMin", "tmrMin", "notaMedia", "pctAvaliados");
    public static final List<String> INDICADORES_ROTULOS = List.of("Grupo", "Total", "Resolvidos", "Cancelados",
            "% SLA atendimento", "% SLA resolução", "TMA (min)", "TMR (min)", "Nota média", "% avaliados");

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final byte[] BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    private ExportacaoChamados() {
    }

    public static String hashDados(List<String> chaves, List<String> rotulos, JsonArray linhas) {
        return HashUtil.sha256Hex(blocoDados(chaves, rotulos, linhas).getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] csv(List<String> chaves, List<String> rotulos, JsonArray linhas,
                             String titulo, String filtros, String usuario, LocalDateTime agora) {
        String bloco = blocoDados(chaves, rotulos, linhas);
        String texto = "# " + titulo + " — " + filtros + " — " + agora.format(FMT) + " — " + usuario + "\r\n"
                + bloco
                + "# Total: " + linhas.size() + "\r\n"
                + "# SHA-256: " + HashUtil.sha256Hex(bloco.getBytes(StandardCharsets.UTF_8)) + "\r\n";
        byte[] corpo = texto.getBytes(StandardCharsets.UTF_8);
        byte[] saida = new byte[BOM.length + corpo.length];
        System.arraycopy(BOM, 0, saida, 0, BOM.length);
        System.arraycopy(corpo, 0, saida, BOM.length, corpo.length);
        return saida;
    }

    public static byte[] xlsx(List<String> chaves, List<String> rotulos, JsonArray linhas,
                              String titulo, String filtros, String usuario, LocalDateTime agora) throws IOException {
        try (SXSSFWorkbook wb = new SXSSFWorkbook(200); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle negrito = wb.createCellStyle();
            Font fonte = wb.createFont();
            fonte.setBold(true);
            negrito.setFont(fonte);

            Sheet dados = wb.createSheet("Chamados");
            Row cab = dados.createRow(0);
            for (int c = 0; c < rotulos.size(); c++) {
                cab.createCell(c).setCellValue(rotulos.get(c));
                cab.getCell(c).setCellStyle(negrito);
            }
            for (int r = 0; r < linhas.size(); r++) {
                JsonObject o = linhas.get(r).getAsJsonObject();
                Row row = dados.createRow(r + 1);
                for (int c = 0; c < chaves.size(); c++) {
                    row.createCell(c).setCellValue(valor(o, chaves.get(c)));
                }
            }

            Sheet meta = wb.createSheet("Metadados");
            String[][] pares = {
                    {"Relatório", titulo},
                    {"Filtros", filtros},
                    {"Gerado em", agora.format(FMT)},
                    {"Usuário", usuario},
                    {"Total de registros", String.valueOf(linhas.size())},
                    {"SHA-256 dos dados", hashDados(chaves, rotulos, linhas)}};
            for (int i = 0; i < pares.length; i++) {
                Row row = meta.createRow(i);
                row.createCell(0).setCellValue(pares[i][0]);
                row.getCell(0).setCellStyle(negrito);
                row.createCell(1).setCellValue(pares[i][1]);
            }
            wb.write(out);
            wb.dispose();
            return out.toByteArray();
        }
    }

    private static String blocoDados(List<String> chaves, List<String> rotulos, JsonArray linhas) {
        StringBuilder sb = new StringBuilder();
        sb.append(linhaCsv(rotulos));
        for (JsonElement e : linhas) {
            JsonObject o = e.getAsJsonObject();
            String[] valores = new String[chaves.size()];
            for (int c = 0; c < chaves.size(); c++) valores[c] = valor(o, chaves.get(c));
            sb.append(linhaCsv(List.of(valores)));
        }
        return sb.toString();
    }

    private static String linhaCsv(List<String> valores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) sb.append(';');
            sb.append(celula(valores.get(i)));
        }
        return sb.append("\r\n").toString();
    }

    private static String celula(String v) {
        String s = v == null ? "" : v.replace("\r", " ").replace("\n", " ");
        if (!s.isEmpty() && "=+-@".indexOf(s.charAt(0)) >= 0) {
            s = "'" + s;   // impede que o Excel interprete como formula
        }
        if (s.contains(";") || s.contains("\"")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String valor(JsonObject o, String chave) {
        return o.has(chave) && !o.get(chave).isJsonNull() ? o.get(chave).getAsString() : "";
    }
}
```

> Observação: `celula` neutraliza `-` no início; números negativos não ocorrem nas exportações de chamados.

- [ ] **Step 4: Implementar `EmailChamado` e o método em `ServicoEmailMuralha`**

```java
// src/main/java/muralha/digital/chamado/EmailChamado.java
package muralha.digital.chamado;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.ConfiguracaoServidorSmtp;
import muralha.digital.notificacao.ServicoEmailMuralha;

import javax.mail.internet.InternetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Confirmacao de abertura e aviso de reclassificacao (spec §1.4, D14). */
public final class EmailChamado {

    public static final String CONFIRMACAO_ABERTURA = "CONFIRMACAO_ABERTURA";
    public static final String RECLASSIFICACAO = "RECLASSIFICACAO";
    public static final int MAX_TENTATIVAS = 5;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private EmailChamado() {
    }

    public static String assunto(String tipo, String protocolo) {
        return RECLASSIFICACAO.equals(tipo)
                ? "[Chamado " + protocolo + "] Prioridade reclassificada"
                : "[Chamado " + protocolo + "] Abertura confirmada";
    }

    public static String corpoHtml(String tipo, String protocolo, String titulo, String prioridade,
                                   LocalDateTime prazoAtendimento, LocalDateTime prazoResolucao,
                                   String justificativa) {
        boolean reclass = RECLASSIFICACAO.equals(tipo);
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family:Arial,sans-serif;font-size:14px;color:#333'>")
          .append("<h2 style='color:#0d75bf'>")
          .append(reclass ? "Prioridade do chamado reclassificada" : "Chamado registrado com sucesso")
          .append("</h2>")
          .append("<table style='border-collapse:collapse'>")
          .append(linha("Protocolo", "<strong>" + esc(protocolo) + "</strong>"))
          .append(linha("Título", esc(titulo)))
          .append(linha(reclass ? "Nova prioridade" : "Prioridade", esc(prioridade)))
          .append(linha("Prazo previsto para atendimento", prazoAtendimento.format(FMT)))
          .append(linha("Prazo previsto para resolução", prazoResolucao.format(FMT)));
        if (reclass && justificativa != null) {
            sb.append(linha("Justificativa", esc(justificativa)));
        }
        sb.append("</table>")
          .append("<p>Acompanhe o chamado no sistema, menu <strong>Chamados</strong>.</p>")
          .append("<hr style='border:0;border-top:1px solid #ccc'/>")
          .append("<p style='font-size:12px;color:#777'>Mensagem automática — não responda este e-mail.</p>")
          .append("</div>");
        return sb.toString();
    }

    public static String statusAposTentativa(boolean enviado, int tentativasAntes) {
        if (enviado) return "ENVIADO";
        return tentativasAntes + 1 >= MAX_TENTATIVAS ? "ERRO" : "PENDENTE";
    }

    public static boolean enviar(String destinatario, String assunto, String html) throws Exception {
        List<ConfiguracaoServidorSmtp> servidores = ConfiguracaoProvider.getInstance().getListaServidoresSmtp();
        if (servidores == null || servidores.isEmpty()) {
            throw new IllegalStateException("Nenhum servidor SMTP configurado em confGTW.xml.");
        }
        ConfiguracaoServidorSmtp s = servidores.get(0);
        ServicoEmailMuralha servico = new ServicoEmailMuralha(s.getUser(), s.getPassword(), s.getHost(), s.getPort());
        return servico.enviarEmailChamado(new InternetAddress(s.getMail()),
                List.of(new InternetAddress(destinatario)), assunto, html);
    }

    static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static String linha(String rotulo, String valorHtml) {
        return "<tr><td style='padding:4px 12px 4px 0;color:#555'>" + rotulo + "</td><td style='padding:4px 0'>"
                + valorHtml + "</td></tr>";
    }
}
```

Em `ServicoEmailMuralha.java`, antes do `}` final (linha 325), acrescentar (mesmo padrão de `enviarEmailNotificacaoUsuarioResponsavel`):

```java
	public boolean enviarEmailChamado(InternetAddress remetente, List<InternetAddress> destinatarios,
	                                  String assunto, String corpoHtml) {
	    try {
	        Properties props = new Properties();
	        props.put("mail.smtp.host", smtpHost);
	        props.put("mail.smtp.port", String.valueOf(smtpPort));
	        props.put("mail.smtp.starttls.enable", "true");

	        Session session;
	        if (smtpUser != null && !smtpUser.isEmpty()) {
	            props.put("mail.smtp.auth", "true");
	            Authenticator auth = new Authenticator() {
	                @Override
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(smtpUser, smtpPassword);
	                }
	            };
	            session = Session.getInstance(props, auth);
	        } else {
	            session = Session.getInstance(props);
	        }

	        MimeMessage msg = new MimeMessage(session);
	        msg.setFrom(remetente);
	        msg.addRecipients(Message.RecipientType.TO, destinatarios.toArray(new InternetAddress[0]));
	        msg.setSubject(assunto, "UTF-8");
	        msg.setContent(corpoHtml, "text/html; charset=UTF-8");
	        msg.setSentDate(new Date());
	        Transport.send(msg);
	        return true;
	    } catch (Exception e) {
	        logger.error("Erro ao enviar e-mail de chamado: ", e);
	        return false;
	    }
	}
```

- [ ] **Step 5: Rodar e ver passar**

Run: mesmo comando do Step 2. Expected: `Tests run: 8, Failures: 0`.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/muralha/digital/chamado/ExportacaoChamados.java src/main/java/muralha/digital/chamado/EmailChamado.java src/main/java/muralha/digital/notificacao/ServicoEmailMuralha.java src/test/java/muralha/digital/chamado/ExportacaoChamadosTest.java src/test/java/muralha/digital/chamado/EmailChamadoTest.java
git commit -m "Adiciona exportacao com SHA-256 e e-mails de confirmacao dos chamados

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 6: Modelos, filtros e DAOs

**Files:**
- Create: `src/main/java/muralha/digital/chamado/Chamado.java`, `Prioridade.java`, `Categoria.java`, `FiltroChamados.java`, `ChamadoConfigDao.java`, `ChamadoDao.java`
- Test: `src/test/java/muralha/digital/chamado/FiltroChamadosTest.java`, `CadastrosChamadoTest.java`

**Interfaces:**
- Consumes: `StatusChamado`, `CalculadoraSla.Marca`, `PerfilChamado`, `AnexoGravado`, `ExportacaoChamados.LISTA_CHAVES`.
- Produces:
  - `Chamado` (campos públicos; ver código), `Prioridade` (`validar()`, `json()`), `Categoria` (`validar()`, `json()`).
  - `FiltroChamados` (campos `protocolo, status, idPrioridade, idCategoria, idLocal, dtInicio, dtFim, idSolicitante, pagina`; `TAMANHO_PAGINA = 50`; `where(List<Object> params) : String`; `descricao() : String`; `static deRequest(HttpServletRequest) : FiltroChamados`).
  - `ChamadoConfigDao`: `valor(Connection, String, String)`, `valorInt(Connection, String, int)`, `perfis(Connection, int idUsuario) : Set<PerfilChamado>`, `emailUsuario(Connection, int)`, `equipamentoExiste(Connection, int)`, `equipamentos(Connection) : JsonArray`, `prioridades(Connection, boolean soAtivas) : List<Prioridade>`, `obterPrioridade(Connection, int)`, `categorias(Connection, boolean soAtivas) : List<Categoria>`, `obterCategoria(Connection, int)`, `salvarPrioridade(Connection, Prioridade)`, `salvarCategoria(Connection, Categoria)`, `alternarAtivo(Connection, String tipo, int id)`.
  - `ChamadoDao`: `inserir(Connection, Chamado) : long`, `obterPorProtocolo(Connection, String) : Chamado`, `atualizarEstado(Connection, Chamado, StatusChamado esperado) : int`, `inserirHistorico(Connection, long idChamado, Integer idUsuario, String tipo, String statusAnterior, String statusNovo, String texto) : long`, `inserirAnexo(Connection, long idChamado, Long idHistorico, AnexoGravado, int idUsuario)`, `historico(Connection, long) : JsonArray`, `anexos(Connection, long) : JsonArray`, `obterAnexo(Connection, long idAnexo) : JsonObject`, `enfileirarNotificacao(Connection, long idChamado, String tipo, String destinatario)`, `listar(Connection, FiltroChamados) : JsonObject`, `listarParaExportacao(Connection, FiltroChamados) : JsonArray`, `indicadores(Connection, FiltroChamados, String agrupamento) : JsonArray`, `listarNaoTerminais(Connection) : List<Chamado>`, `listarResolvidosAntesDe(Connection, LocalDateTime) : List<Chamado>`, `json(Chamado) : JsonObject`.

- [ ] **Step 1: Escrever os testes que falham**

```java
// src/test/java/muralha/digital/chamado/FiltroChamadosTest.java
package muralha.digital.chamado;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class FiltroChamadosTest {

    @Test
    public void semFiltrosNaoRestringe() {
        List<Object> params = new ArrayList<>();
        assertEquals(new FiltroChamados().where(params), " WHERE 1=1");
        assertTrue(params.isEmpty());
    }

    @Test
    public void visibilidadeDoSolicitanteSempreAplicadaPrimeiro() {
        FiltroChamados f = new FiltroChamados();
        f.idSolicitante = 42;
        f.protocolo = "CH-2026";
        List<Object> params = new ArrayList<>();
        String w = f.where(params);
        assertTrue(w.startsWith(" WHERE 1=1 AND c.id_usuario_solicitante = ?"));
        assertEquals(params.get(0), 42);
    }

    @Test
    public void categoriaIncluiSubcategorias() {
        FiltroChamados f = new FiltroChamados();
        f.idCategoria = 7;
        List<Object> params = new ArrayList<>();
        assertTrue(f.where(params).contains("(c.id_categoria = ? OR cat.id_pai = ?)"));
        assertEquals(params, List.of(7, 7));
    }

    @Test
    public void protocoloSoAceitaLetrasNumerosEHifen() {
        FiltroChamados f = new FiltroChamados();
        f.protocolo = "CH-2026%' OR 1=1 --";
        List<Object> params = new ArrayList<>();
        f.where(params);
        assertEquals(params.get(0), "%CH-2026OR11--%");
    }

    @Test
    public void periodoFimEhExclusivoNoDiaSeguinte() {
        FiltroChamados f = new FiltroChamados();
        f.dtInicio = LocalDate.of(2026, 10, 1);
        f.dtFim = LocalDate.of(2026, 10, 31);
        List<Object> params = new ArrayList<>();
        String w = f.where(params);
        assertTrue(w.contains("c.dt_abertura >= ?") && w.contains("c.dt_abertura < ?"));
        assertEquals(params.get(0), Timestamp.valueOf("2026-10-01 00:00:00"));
        assertEquals(params.get(1), Timestamp.valueOf("2026-11-01 00:00:00"));
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void statusInvalidoERejeitado() {
        FiltroChamados f = new FiltroChamados();
        f.status = "QUALQUER'; DROP TABLE x";
        f.where(new ArrayList<>());
    }
}
```

```java
// src/test/java/muralha/digital/chamado/CadastrosChamadoTest.java
package muralha.digital.chamado;

import org.testng.annotations.Test;

public class CadastrosChamadoTest {

    private static Prioridade valida() {
        Prioridade p = new Prioridade();
        p.codigo = "CRITICA";
        p.nome = "Crítica";
        p.ordem = 1;
        p.minutosAtendimento = 60;
        p.minutosResolucao = 240;
        p.cor = "#dc3545";
        return p;
    }

    @Test
    public void prioridadeValida() {
        valida().validar();
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void resolucaoMenorQueAtendimento() {
        Prioridade p = valida();
        p.minutosResolucao = 30;
        p.validar();
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void corInvalida() {
        Prioridade p = valida();
        p.cor = "red;background:url(x)";
        p.validar();
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void codigoInvalido() {
        Prioridade p = valida();
        p.codigo = "crit ica";
        p.validar();
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void categoriaSemNome() {
        Categoria c = new Categoria();
        c.nome = "  ";
        c.validar();
    }
}
```

- [ ] **Step 2: Rodar e ver falhar**

Run: `mvn -o -s "$s\settings-legacy-archiva.xml" '-Dexec.skip=true' '-Dtest=FiltroChamadosTest,CadastrosChamadoTest' '-DfailIfNoTests=false' test`
Expected: FAIL de compilação (`cannot find symbol: class FiltroChamados`).

- [ ] **Step 3: Implementar os modelos**

```java
// src/main/java/muralha/digital/chamado/Chamado.java
package muralha.digital.chamado;

import muralha.digital.chamado.CalculadoraSla.Marca;

import java.time.LocalDateTime;

/** Linha de muralha.chamado + dados de exibicao vindos de joins. */
public class Chamado {
    public long id;
    public String protocolo;
    public String titulo;
    public String descricao;
    public int idCategoria;
    public int idPrioridade;
    public int idPrioridadeSolicitada;
    public StatusChamado status;
    public String origem = "PORTAL";
    public Integer idLocal;
    public int idUsuarioSolicitante;
    public Integer idUsuarioResponsavel;
    public LocalDateTime dtAbertura;
    public LocalDateTime dtPrazoAtendimento;
    public LocalDateTime dtPrazoResolucao;
    public LocalDateTime dtPrimeiroAtendimento;
    public LocalDateTime dtResolucao;
    public LocalDateTime dtFechamento;
    public int minutosPausados;
    public LocalDateTime dtInicioPausa;
    public Marca slaAtendimento = Marca.NO_PRAZO;
    public Marca slaResolucao = Marca.NO_PRAZO;
    public Integer avaliacaoNota;
    public String avaliacaoComentario;
    public LocalDateTime dtAvaliacao;
    public boolean fechadoAutomaticamente;
    public LocalDateTime dtAtualizacao;

    // Somente leitura (joins)
    public String nomeCategoria;
    public String nomePrioridade;
    public String corPrioridade;
    public int minutosResolucaoPrioridade;
    public String nomePrioridadeSolicitada;
    public String nomeLocal;
    public String nomeSolicitante;
    public String emailSolicitante;
    public String nomeResponsavel;
}
```

```java
// src/main/java/muralha/digital/chamado/Prioridade.java
package muralha.digital.chamado;

import com.google.gson.JsonObject;

public class Prioridade {
    public int id;
    public String codigo;
    public String nome;
    public int ordem;
    public int minutosAtendimento;
    public int minutosResolucao;
    public String cor;
    public boolean ativo = true;

    public void validar() {
        if (codigo == null || !codigo.matches("^[A-Z_]{2,20}$")) {
            throw new RegraChamadoException("Código deve ter de 2 a 20 letras maiúsculas ou _.");
        }
        if (nome == null || nome.trim().isEmpty() || nome.length() > 50) {
            throw new RegraChamadoException("Nome obrigatório (até 50 caracteres).");
        }
        if (minutosAtendimento <= 0 || minutosResolucao < minutosAtendimento) {
            throw new RegraChamadoException("Prazos inválidos: atendimento > 0 e resolução ≥ atendimento.");
        }
        if (cor == null || !cor.matches("^#[0-9a-fA-F]{6}$")) {
            throw new RegraChamadoException("Cor deve estar no formato #RRGGBB.");
        }
        if (ordem < 1 || ordem > 99) {
            throw new RegraChamadoException("Ordem deve estar entre 1 e 99.");
        }
    }

    public JsonObject json() {
        JsonObject o = new JsonObject();
        o.addProperty("id", id);
        o.addProperty("codigo", codigo);
        o.addProperty("nome", nome);
        o.addProperty("ordem", ordem);
        o.addProperty("minutosAtendimento", minutosAtendimento);
        o.addProperty("minutosResolucao", minutosResolucao);
        o.addProperty("cor", cor);
        o.addProperty("ativo", ativo);
        return o;
    }
}
```

```java
// src/main/java/muralha/digital/chamado/Categoria.java
package muralha.digital.chamado;

import com.google.gson.JsonObject;

public class Categoria {
    public int id;
    public String nome;
    public Integer idPai;
    public String nomePai;
    /** Efetivo: subcategoria herda o valor da categoria (spec §2.1). */
    public boolean exigeEquipamento;
    public boolean ativo = true;

    public void validar() {
        if (nome == null || nome.trim().isEmpty() || nome.length() > 100) {
            throw new RegraChamadoException("Nome obrigatório (até 100 caracteres).");
        }
    }

    public JsonObject json() {
        JsonObject o = new JsonObject();
        o.addProperty("id", id);
        o.addProperty("nome", nome);
        o.addProperty("idPai", idPai);
        o.addProperty("nomePai", nomePai);
        o.addProperty("exigeEquipamento", exigeEquipamento);
        o.addProperty("ativo", ativo);
        return o;
    }
}
```

```java
// src/main/java/muralha/digital/chamado/FiltroChamados.java
package muralha.digital.chamado;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Filtros da lista/relatorio. O SQL usa os aliases c (chamado) e cat (categoria). */
public class FiltroChamados {

    public static final int TAMANHO_PAGINA = 50;

    public String protocolo;
    public String status;
    public Integer idPrioridade;
    public Integer idCategoria;
    public Integer idLocal;
    public LocalDate dtInicio;
    public LocalDate dtFim;
    /** Preenchido pelo servidor quando o usuario so ve os proprios chamados. */
    public Integer idSolicitante;
    public int pagina = 1;

    public String where(List<Object> params) {
        StringBuilder sb = new StringBuilder(" WHERE 1=1");
        if (idSolicitante != null) {
            sb.append(" AND c.id_usuario_solicitante = ?");
            params.add(idSolicitante);
        }
        if (protocolo != null) {
            String limpo = protocolo.replaceAll("[^A-Za-z0-9-]", "");
            if (!limpo.isEmpty()) {
                sb.append(" AND c.protocolo LIKE ?");
                params.add("%" + limpo + "%");
            }
        }
        if (status != null && !status.isEmpty()) {
            try {
                params.add(StatusChamado.valueOf(status).name());
            } catch (IllegalArgumentException e) {
                throw new RegraChamadoException("Status inválido.");
            }
            sb.append(" AND c.status = ?");
        }
        if (idPrioridade != null) {
            sb.append(" AND c.id_prioridade = ?");
            params.add(idPrioridade);
        }
        if (idCategoria != null) {
            sb.append(" AND (c.id_categoria = ? OR cat.id_pai = ?)");
            params.add(idCategoria);
            params.add(idCategoria);
        }
        if (idLocal != null) {
            sb.append(" AND c.id_local = ?");
            params.add(idLocal);
        }
        if (dtInicio != null) {
            sb.append(" AND c.dt_abertura >= ?");
            params.add(Timestamp.valueOf(dtInicio.atStartOfDay()));
        }
        if (dtFim != null) {
            sb.append(" AND c.dt_abertura < ?");
            params.add(Timestamp.valueOf(dtFim.plusDays(1).atStartOfDay()));
        }
        return sb.toString();
    }

    /** Texto legivel dos filtros, usado no cabecalho das exportacoes. */
    public String descricao() {
        List<String> partes = new ArrayList<>();
        if (protocolo != null && !protocolo.isEmpty()) partes.add("protocolo " + protocolo);
        if (status != null && !status.isEmpty()) partes.add("status " + status);
        if (idPrioridade != null) partes.add("prioridade #" + idPrioridade);
        if (idCategoria != null) partes.add("categoria #" + idCategoria);
        if (idLocal != null) partes.add("equipamento #" + idLocal);
        if (dtInicio != null) partes.add("de " + dtInicio);
        if (dtFim != null) partes.add("até " + dtFim);
        if (idSolicitante != null) partes.add("somente chamados do solicitante");
        return partes.isEmpty() ? "sem filtros" : String.join(", ", partes);
    }

    public static FiltroChamados deRequest(HttpServletRequest req) {
        FiltroChamados f = new FiltroChamados();
        f.protocolo = texto(req, "protocolo");
        f.status = texto(req, "status");
        f.idPrioridade = inteiro(req, "idPrioridade");
        f.idCategoria = inteiro(req, "idCategoria");
        f.idLocal = inteiro(req, "idLocal");
        f.dtInicio = data(req, "dtInicio");
        f.dtFim = data(req, "dtFim");
        Integer pagina = inteiro(req, "pagina");
        f.pagina = pagina == null || pagina < 1 ? 1 : pagina;
        return f;
    }

    private static String texto(HttpServletRequest req, String nome) {
        String v = req.getParameter(nome);
        return v == null || v.trim().isEmpty() ? null : v.trim();
    }

    private static Integer inteiro(HttpServletRequest req, String nome) {
        String v = texto(req, nome);
        try {
            return v == null ? null : Integer.valueOf(v);
        } catch (NumberFormatException e) {
            throw new RegraChamadoException("Parâmetro inválido: " + nome);
        }
    }

    private static LocalDate data(HttpServletRequest req, String nome) {
        String v = texto(req, nome);
        try {
            return v == null ? null : LocalDate.parse(v);
        } catch (Exception e) {
            throw new RegraChamadoException("Data inválida: " + nome);
        }
    }
}
```

- [ ] **Step 4: Rodar os testes e ver passar**

Run: mesmo comando do Step 2. Expected: `Tests run: 11, Failures: 0`.

- [ ] **Step 5: Implementar `ChamadoConfigDao`**

```java
// src/main/java/muralha/digital/chamado/ChamadoConfigDao.java
package muralha.digital.chamado;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Parametros, perfis e cadastros do Sistema de Chamados. */
public final class ChamadoConfigDao {

    private static final String SQL_CATEGORIAS =
            "SELECT c.id, c.nome, c.id_pai, p.nome AS nome_pai, c.ativo, " +
            "CAST(CASE WHEN c.id_pai IS NULL THEN c.exige_equipamento ELSE p.exige_equipamento END AS BIT) AS exige " +
            "FROM muralha.chamado_categoria c LEFT JOIN muralha.chamado_categoria p ON p.id = c.id_pai ";

    private ChamadoConfigDao() {
    }

    public static String valor(Connection conn, String chave, String padrao) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.config_chave_valor WHERE chave = ?")) {
            ps.setString(1, chave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String v = rs.getString(1);
                    if (v != null && !v.trim().isEmpty()) return v.trim();
                }
            }
        }
        return padrao;
    }

    public static int valorInt(Connection conn, String chave, int padrao) throws SQLException {
        try {
            return Integer.parseInt(valor(conn, chave, String.valueOf(padrao)));
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    public static Set<PerfilChamado> perfis(Connection conn, int idUsuario) throws SQLException {
        Map<Integer, PerfilChamado> porGrupo = new HashMap<>();
        mapear(conn, porGrupo, "chamado_grupo_solicitante", PerfilChamado.SOLICITANTE);
        mapear(conn, porGrupo, "chamado_grupo_gestor", PerfilChamado.GESTOR);
        mapear(conn, porGrupo, "chamado_grupo_equipe", PerfilChamado.EQUIPE);
        Set<PerfilChamado> perfis = EnumSet.noneOf(PerfilChamado.class);
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_grupo FROM dbo.sis_usuario_grupo WHERE id_usuario = ?")) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PerfilChamado p = porGrupo.get(rs.getInt(1));
                    if (p != null) perfis.add(p);
                }
            }
        }
        return perfis;
    }

    private static void mapear(Connection conn, Map<Integer, PerfilChamado> mapa, String chave, PerfilChamado perfil)
            throws SQLException {
        int idGrupo = valorInt(conn, chave, -1);
        if (idGrupo > 0) mapa.put(idGrupo, perfil);
    }

    public static String emailUsuario(Connection conn, int idUsuario) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT RTRIM(email) FROM dbo.sis_usuario WHERE id_usuario = ?")) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String e = rs.getString(1);
                    return e == null || e.trim().isEmpty() ? null : e.trim();
                }
            }
        }
        return null;
    }

    public static boolean equipamentoExiste(Connection conn, int idLocal) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM local_vigente WHERE id_local = ? AND desativado = 0")) {
            ps.setInt(1, idLocal);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static JsonArray equipamentos(Connection conn) throws SQLException {
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_local, RTRIM(nome) AS nome FROM local_vigente WHERE desativado = 0 ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject o = new JsonObject();
                o.addProperty("idLocal", rs.getInt("id_local"));
                o.addProperty("nome", rs.getString("nome"));
                arr.add(o);
            }
        }
        return arr;
    }

    public static List<Prioridade> prioridades(Connection conn, boolean soAtivas) throws SQLException {
        List<Prioridade> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM muralha.chamado_prioridade" + (soAtivas ? " WHERE ativo = 1" : "") + " ORDER BY ordem");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(prioridade(rs));
        }
        return lista;
    }

    public static Prioridade obterPrioridade(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM muralha.chamado_prioridade WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? prioridade(rs) : null;
            }
        }
    }

    private static Prioridade prioridade(ResultSet rs) throws SQLException {
        Prioridade p = new Prioridade();
        p.id = rs.getInt("id");
        p.codigo = rs.getString("codigo");
        p.nome = rs.getString("nome");
        p.ordem = rs.getInt("ordem");
        p.minutosAtendimento = rs.getInt("minutos_atendimento");
        p.minutosResolucao = rs.getInt("minutos_resolucao");
        p.cor = rs.getString("cor");
        p.ativo = rs.getBoolean("ativo");
        return p;
    }

    public static List<Categoria> categorias(Connection conn, boolean soAtivas) throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String where = soAtivas ? "WHERE c.ativo = 1 AND (p.id IS NULL OR p.ativo = 1) " : "";
        try (PreparedStatement ps = conn.prepareStatement(
                SQL_CATEGORIAS + where + "ORDER BY COALESCE(p.nome, c.nome), CASE WHEN c.id_pai IS NULL THEN 0 ELSE 1 END, c.nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(categoria(rs));
        }
        return lista;
    }

    public static Categoria obterCategoria(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_CATEGORIAS + "WHERE c.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? categoria(rs) : null;
            }
        }
    }

    private static Categoria categoria(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.id = rs.getInt("id");
        c.nome = rs.getString("nome");
        int idPai = rs.getInt("id_pai");
        c.idPai = rs.wasNull() ? null : idPai;
        c.nomePai = rs.getString("nome_pai");
        c.exigeEquipamento = rs.getBoolean("exige");
        c.ativo = rs.getBoolean("ativo");
        return c;
    }

    public static void salvarPrioridade(Connection conn, Prioridade p) throws SQLException {
        p.validar();
        String sql = p.id == 0
                ? "INSERT INTO muralha.chamado_prioridade (codigo, nome, ordem, minutos_atendimento, minutos_resolucao, cor) VALUES (?,?,?,?,?,?)"
                : "UPDATE muralha.chamado_prioridade SET codigo=?, nome=?, ordem=?, minutos_atendimento=?, minutos_resolucao=?, cor=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.codigo);
            ps.setString(2, p.nome.trim());
            ps.setInt(3, p.ordem);
            ps.setInt(4, p.minutosAtendimento);
            ps.setInt(5, p.minutosResolucao);
            ps.setString(6, p.cor);
            if (p.id != 0) ps.setInt(7, p.id);
            ps.executeUpdate();
        }
    }

    public static void salvarCategoria(Connection conn, Categoria c) throws SQLException {
        c.validar();
        if (c.idPai != null) {
            Categoria pai = obterCategoria(conn, c.idPai);
            if (pai == null || pai.idPai != null) {
                throw new RegraChamadoException("A categoria pai deve ser uma categoria de primeiro nível.");
            }
            if (c.id != 0 && c.idPai == c.id) {
                throw new RegraChamadoException("Uma categoria não pode ser pai de si mesma.");
            }
        }
        String sql = c.id == 0
                ? "INSERT INTO muralha.chamado_categoria (nome, id_pai, exige_equipamento) VALUES (?,?,?)"
                : "UPDATE muralha.chamado_categoria SET nome=?, id_pai=?, exige_equipamento=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.nome.trim());
            if (c.idPai == null) ps.setNull(2, java.sql.Types.INTEGER);
            else ps.setInt(2, c.idPai);
            ps.setBoolean(3, c.idPai == null && c.exigeEquipamento);
            if (c.id != 0) ps.setInt(4, c.id);
            ps.executeUpdate();
        }
    }

    public static void alternarAtivo(Connection conn, String tipo, int id) throws SQLException {
        String tabela;
        if ("prioridade".equals(tipo)) tabela = "muralha.chamado_prioridade";
        else if ("categoria".equals(tipo)) tabela = "muralha.chamado_categoria";
        else throw new RegraChamadoException("Tipo inválido.");
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE " + tabela + " SET ativo = CASE WHEN ativo = 1 THEN 0 ELSE 1 END WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
```

- [ ] **Step 6: Implementar `ChamadoDao`**

```java
// src/main/java/muralha/digital/chamado/ChamadoDao.java
package muralha.digital.chamado;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.chamado.CalculadoraSla.Marca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** SQL de muralha.chamado e tabelas satelite. Quem chama abre/fecha a conexao. */
public final class ChamadoDao {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int LIMITE_EXPORTACAO = 50_000;

    private static final String COLUNAS =
            "SELECT c.id, c.protocolo, c.titulo, c.descricao, c.id_categoria, c.id_prioridade, " +
            "c.id_prioridade_solicitada, c.status, c.origem, c.id_local, c.id_usuario_solicitante, " +
            "c.id_usuario_responsavel, c.dt_abertura, c.dt_prazo_atendimento, c.dt_prazo_resolucao, " +
            "c.dt_primeiro_atendimento, c.dt_resolucao, c.dt_fechamento, c.minutos_pausados, c.dt_inicio_pausa, " +
            "c.sla_atendimento, c.sla_resolucao, c.avaliacao_nota, c.avaliacao_comentario, c.dt_avaliacao, " +
            "c.fechado_automaticamente, c.dt_atualizacao, " +
            "COALESCE(cp.nome + N' / ', N'') + cat.nome AS nome_categoria, p.nome AS nome_prioridade, " +
            "p.cor AS cor_prioridade, p.minutos_resolucao AS minutos_resolucao_prioridade, " +
            "ps.nome AS nome_prioridade_solicitada, RTRIM(l.nome) AS nome_local, RTRIM(us.nome) AS nome_solicitante, " +
            "RTRIM(us.email) AS email_solicitante, RTRIM(ur.nome) AS nome_responsavel ";

    private static final String FROM =
            "FROM muralha.chamado c " +
            "JOIN muralha.chamado_categoria cat ON cat.id = c.id_categoria " +
            "LEFT JOIN muralha.chamado_categoria cp ON cp.id = cat.id_pai " +
            "JOIN muralha.chamado_prioridade p ON p.id = c.id_prioridade " +
            "JOIN muralha.chamado_prioridade ps ON ps.id = c.id_prioridade_solicitada " +
            "LEFT JOIN dbo.local l ON l.id_local = c.id_local AND l.sequencia_local = 1 " +
            "LEFT JOIN dbo.sis_usuario us ON us.id_usuario = c.id_usuario_solicitante " +
            "LEFT JOIN dbo.sis_usuario ur ON ur.id_usuario = c.id_usuario_responsavel ";

    /** Expressoes de agrupamento permitidas no relatorio (lista branca). 1900-01-01 foi segunda-feira. */
    private static final Map<String, String> AGRUPAMENTOS = Map.of(
            "dia", "CONVERT(VARCHAR(10), c.dt_abertura, 120)",
            "semana", "CONVERT(VARCHAR(10), DATEADD(DAY, -(DATEDIFF(DAY, '19000101', c.dt_abertura) % 7), CAST(c.dt_abertura AS DATE)), 120)",
            "mes", "CONVERT(VARCHAR(7), c.dt_abertura, 120)",
            "categoria", "COALESCE(cp.nome + N' / ', N'') + cat.nome",
            "equipamento", "COALESCE(RTRIM(l.nome), N'(sem equipamento)')",
            "prioridade", "p.nome");

    private ChamadoDao() {
    }

    public static long inserir(Connection conn, Chamado c) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.chamado (protocolo, titulo, descricao, id_categoria, id_prioridade, " +
                "id_prioridade_solicitada, status, origem, id_local, id_usuario_solicitante, dt_abertura, " +
                "dt_prazo_atendimento, dt_prazo_resolucao, dt_atualizacao) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.protocolo);
            ps.setString(2, c.titulo);
            ps.setString(3, c.descricao);
            ps.setInt(4, c.idCategoria);
            ps.setInt(5, c.idPrioridade);
            ps.setInt(6, c.idPrioridadeSolicitada);
            ps.setString(7, c.status.name());
            ps.setString(8, c.origem);
            setInt(ps, 9, c.idLocal);
            ps.setInt(10, c.idUsuarioSolicitante);
            ps.setTimestamp(11, ts(c.dtAbertura));
            ps.setTimestamp(12, ts(c.dtPrazoAtendimento));
            ps.setTimestamp(13, ts(c.dtPrazoResolucao));
            ps.setTimestamp(14, ts(c.dtAbertura));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                c.dtAtualizacao = c.dtAbertura;
                return keys.getLong(1);
            }
        }
    }

    public static Chamado obterPorProtocolo(Connection conn, String protocolo) throws SQLException {
        if (!GeradorProtocolo.valido(protocolo)) return null;
        try (PreparedStatement ps = conn.prepareStatement(COLUNAS + FROM + "WHERE c.protocolo = ?")) {
            ps.setString(1, protocolo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Controle otimista: 0 linhas = chamado mudou desde a leitura (spec §1.2). */
    public static int atualizarEstado(Connection conn, Chamado c, StatusChamado esperado) throws SQLException {
        // DATETIME2(3): truncar em milissegundos para a comparacao do proximo UPDATE bater
        LocalDateTime nova = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE muralha.chamado SET status=?, id_prioridade=?, id_usuario_responsavel=?, " +
                "dt_prazo_atendimento=?, dt_prazo_resolucao=?, dt_primeiro_atendimento=?, dt_resolucao=?, " +
                "dt_fechamento=?, minutos_pausados=?, dt_inicio_pausa=?, sla_atendimento=?, sla_resolucao=?, " +
                "avaliacao_nota=?, avaliacao_comentario=?, dt_avaliacao=?, fechado_automaticamente=?, dt_atualizacao=? " +
                "WHERE id=? AND status=? AND dt_atualizacao=?")) {
            ps.setString(1, c.status.name());
            ps.setInt(2, c.idPrioridade);
            setInt(ps, 3, c.idUsuarioResponsavel);
            ps.setTimestamp(4, ts(c.dtPrazoAtendimento));
            ps.setTimestamp(5, ts(c.dtPrazoResolucao));
            ps.setTimestamp(6, ts(c.dtPrimeiroAtendimento));
            ps.setTimestamp(7, ts(c.dtResolucao));
            ps.setTimestamp(8, ts(c.dtFechamento));
            ps.setInt(9, c.minutosPausados);
            ps.setTimestamp(10, ts(c.dtInicioPausa));
            ps.setString(11, c.slaAtendimento.name());
            ps.setString(12, c.slaResolucao.name());
            setInt(ps, 13, c.avaliacaoNota);
            ps.setString(14, c.avaliacaoComentario);
            ps.setTimestamp(15, ts(c.dtAvaliacao));
            ps.setBoolean(16, c.fechadoAutomaticamente);
            ps.setTimestamp(17, ts(nova));
            ps.setLong(18, c.id);
            ps.setString(19, esperado.name());
            ps.setTimestamp(20, ts(c.dtAtualizacao));
            int linhas = ps.executeUpdate();
            if (linhas > 0) c.dtAtualizacao = nova;
            return linhas;
        }
    }

    public static long inserirHistorico(Connection conn, long idChamado, Integer idUsuario, String tipo,
                                        String statusAnterior, String statusNovo, String texto) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.chamado_historico (id_chamado, id_usuario, tipo, status_anterior, status_novo, texto) " +
                "VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, idChamado);
            setInt(ps, 2, idUsuario);
            ps.setString(3, tipo);
            ps.setString(4, statusAnterior);
            ps.setString(5, statusNovo);
            ps.setString(6, texto == null ? null : (texto.length() > 2000 ? texto.substring(0, 2000) : texto));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public static void inserirAnexo(Connection conn, long idChamado, Long idHistorico, AnexoGravado a, int idUsuario)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.chamado_anexo (id_chamado, id_historico, nome_original, caminho, tipo_mime, " +
                "tamanho_bytes, sha256, id_usuario) VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setLong(1, idChamado);
            if (idHistorico == null) ps.setNull(2, Types.BIGINT);
            else ps.setLong(2, idHistorico);
            ps.setString(3, a.nomeOriginal);
            ps.setString(4, a.caminhoRelativo);
            ps.setString(5, a.tipoMime);
            ps.setLong(6, a.tamanho);
            ps.setString(7, a.sha256);
            ps.setInt(8, idUsuario);
            ps.executeUpdate();
        }
    }

    public static JsonArray historico(Connection conn, long idChamado) throws SQLException {
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT h.id, h.dt_evento, h.tipo, h.status_anterior, h.status_novo, h.texto, " +
                "COALESCE(RTRIM(u.nome), N'Sistema') AS usuario " +
                "FROM muralha.chamado_historico h LEFT JOIN dbo.sis_usuario u ON u.id_usuario = h.id_usuario " +
                "WHERE h.id_chamado = ? ORDER BY h.dt_evento DESC, h.id DESC")) {
            ps.setLong(1, idChamado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id", rs.getLong("id"));
                    o.addProperty("dtEvento", iso(ldt(rs, "dt_evento")));
                    o.addProperty("tipo", rs.getString("tipo"));
                    o.addProperty("statusAnterior", rs.getString("status_anterior"));
                    o.addProperty("statusNovo", rs.getString("status_novo"));
                    o.addProperty("texto", rs.getString("texto"));
                    o.addProperty("usuario", rs.getString("usuario"));
                    arr.add(o);
                }
            }
        }
        return arr;
    }

    public static JsonArray anexos(Connection conn, long idChamado) throws SQLException {
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, id_historico, nome_original, tamanho_bytes, sha256, dt_envio " +
                "FROM muralha.chamado_anexo WHERE id_chamado = ? ORDER BY id")) {
            ps.setLong(1, idChamado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id", rs.getLong("id"));
                    long idHist = rs.getLong("id_historico");
                    if (!rs.wasNull()) o.addProperty("idHistorico", idHist);
                    o.addProperty("nome", rs.getString("nome_original"));
                    o.addProperty("tamanho", rs.getLong("tamanho_bytes"));
                    o.addProperty("sha256", rs.getString("sha256"));
                    o.addProperty("dtEnvio", iso(ldt(rs, "dt_envio")));
                    arr.add(o);
                }
            }
        }
        return arr;
    }

    public static JsonObject obterAnexo(Connection conn, long idAnexo) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT a.nome_original, a.caminho, a.tipo_mime, c.protocolo FROM muralha.chamado_anexo a " +
                "JOIN muralha.chamado c ON c.id = a.id_chamado WHERE a.id = ?")) {
            ps.setLong(1, idAnexo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                JsonObject o = new JsonObject();
                o.addProperty("nome", rs.getString("nome_original"));
                o.addProperty("caminho", rs.getString("caminho"));
                o.addProperty("tipoMime", rs.getString("tipo_mime"));
                o.addProperty("protocolo", rs.getString("protocolo"));
                return o;
            }
        }
    }

    /** Sem destinatario a notificacao ja nasce com ERRO (spec §1.4). */
    public static void enfileirarNotificacao(Connection conn, long idChamado, String tipo, String destinatario)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.chamado_notificacao (id_chamado, tipo, destinatario, status, erro) VALUES (?,?,?,?,?)")) {
            ps.setLong(1, idChamado);
            ps.setString(2, tipo);
            ps.setString(3, destinatario);
            ps.setString(4, destinatario == null ? "ERRO" : "PENDENTE");
            ps.setString(5, destinatario == null ? "Solicitante sem e-mail cadastrado" : null);
            ps.executeUpdate();
        }
    }

    public static JsonObject listar(Connection conn, FiltroChamados f) throws SQLException {
        List<Object> params = new ArrayList<>();
        String where = f.where(params);
        JsonObject r = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) " + FROM + where)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                r.addProperty("total", rs.getInt(1));
            }
        }
        JsonArray itens = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(COLUNAS + FROM + where +
                " ORDER BY c.dt_abertura DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY")) {
            List<Object> p2 = new ArrayList<>(params);
            p2.add((f.pagina - 1) * FiltroChamados.TAMANHO_PAGINA);
            p2.add(FiltroChamados.TAMANHO_PAGINA);
            bind(ps, p2);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) itens.add(json(mapear(rs)));
            }
        }
        r.add("itens", itens);
        return r;
    }

    /** Linhas com as chaves de ExportacaoChamados.LISTA_CHAVES, datas no formato brasileiro. */
    public static JsonArray listarParaExportacao(Connection conn, FiltroChamados f) throws SQLException {
        List<Object> params = new ArrayList<>();
        String where = f.where(params);
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                COLUNAS.replaceFirst("SELECT ", "SELECT TOP " + LIMITE_EXPORTACAO + " ") + FROM + where +
                " ORDER BY c.dt_abertura DESC")) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Chamado c = mapear(rs);
                    JsonObject o = new JsonObject();
                    o.addProperty("protocolo", c.protocolo);
                    o.addProperty("titulo", c.titulo);
                    o.addProperty("categoria", c.nomeCategoria);
                    o.addProperty("equipamento", c.nomeLocal);
                    o.addProperty("prioridade", c.nomePrioridade);
                    o.addProperty("status", c.status.name());
                    o.addProperty("solicitante", c.nomeSolicitante);
                    o.addProperty("responsavel", c.nomeResponsavel);
                    o.addProperty("abertura", br(c.dtAbertura));
                    o.addProperty("prazoAtendimento", br(c.dtPrazoAtendimento));
                    o.addProperty("prazoResolucao", br(c.dtPrazoResolucao));
                    o.addProperty("slaAtendimento", c.slaAtendimento.name());
                    o.addProperty("slaResolucao", c.slaResolucao.name());
                    o.addProperty("resolucao", br(c.dtResolucao));
                    o.addProperty("fechamento", br(c.dtFechamento));
                    o.addProperty("nota", c.avaliacaoNota == null ? "" : String.valueOf(c.avaliacaoNota));
                    arr.add(o);
                }
            }
        }
        return arr;
    }

    /** Linhas com as chaves de ExportacaoChamados.INDICADORES_CHAVES. */
    public static JsonArray indicadores(Connection conn, FiltroChamados f, String agrupamento) throws SQLException {
        String grupo = AGRUPAMENTOS.get(agrupamento);
        if (grupo == null) throw new RegraChamadoException("Agrupamento inválido.");
        List<Object> params = new ArrayList<>();
        String where = f.where(params);
        String sql =
                "SELECT " + grupo + " AS grupo, COUNT(*) AS total, " +
                "SUM(CASE WHEN c.dt_resolucao IS NOT NULL THEN 1 ELSE 0 END) AS resolvidos, " +
                "SUM(CASE WHEN c.status = 'CANCELADO' THEN 1 ELSE 0 END) AS cancelados, " +
                "SUM(CASE WHEN c.status <> 'CANCELADO' AND c.sla_atendimento = 'CUMPRIDO' THEN 1 ELSE 0 END) AS at_ok, " +
                "SUM(CASE WHEN c.status <> 'CANCELADO' AND c.sla_atendimento IN ('CUMPRIDO','VIOLADO','VENCIDO') THEN 1 ELSE 0 END) AS at_medido, " +
                "SUM(CASE WHEN c.status <> 'CANCELADO' AND c.sla_resolucao = 'CUMPRIDO' THEN 1 ELSE 0 END) AS res_ok, " +
                "SUM(CASE WHEN c.status <> 'CANCELADO' AND c.sla_resolucao IN ('CUMPRIDO','VIOLADO','VENCIDO') THEN 1 ELSE 0 END) AS res_medido, " +
                "AVG(CAST(DATEDIFF(MINUTE, c.dt_abertura, c.dt_primeiro_atendimento) AS FLOAT)) AS tma, " +
                "AVG(CASE WHEN c.dt_resolucao IS NOT NULL THEN CAST(DATEDIFF(MINUTE, c.dt_abertura, c.dt_resolucao) - c.minutos_pausados AS FLOAT) END) AS tmr, " +
                "AVG(CAST(c.avaliacao_nota AS FLOAT)) AS nota, " +
                "SUM(CASE WHEN c.avaliacao_nota IS NOT NULL THEN 1 ELSE 0 END) AS avaliados, " +
                "SUM(CASE WHEN c.status = 'FECHADO' THEN 1 ELSE 0 END) AS fechados " +
                FROM + where + " GROUP BY " + grupo + " ORDER BY grupo";
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("grupo", rs.getString("grupo"));
                    o.addProperty("total", rs.getInt("total"));
                    o.addProperty("resolvidos", rs.getInt("resolvidos"));
                    o.addProperty("cancelados", rs.getInt("cancelados"));
                    o.addProperty("pctSlaAtendimento", pct(rs.getInt("at_ok"), rs.getInt("at_medido")));
                    o.addProperty("pctSlaResolucao", pct(rs.getInt("res_ok"), rs.getInt("res_medido")));
                    o.addProperty("tmaMin", arred(rs, "tma"));
                    o.addProperty("tmrMin", arred(rs, "tmr"));
                    o.addProperty("notaMedia", arred(rs, "nota"));
                    o.addProperty("pctAvaliados", pct(rs.getInt("avaliados"), rs.getInt("fechados")));
                    arr.add(o);
                }
            }
        }
        return arr;
    }

    public static List<Chamado> listarNaoTerminais(Connection conn) throws SQLException {
        return listarOnde(conn, "WHERE c.status NOT IN ('FECHADO','CANCELADO')", null);
    }

    public static List<Chamado> listarResolvidosAntesDe(Connection conn, LocalDateTime limite) throws SQLException {
        return listarOnde(conn, "WHERE c.status = 'RESOLVIDO' AND c.dt_inicio_pausa <= ?", limite);
    }

    private static List<Chamado> listarOnde(Connection conn, String where, LocalDateTime param) throws SQLException {
        List<Chamado> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(COLUNAS + FROM + where)) {
            if (param != null) ps.setTimestamp(1, ts(param));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public static JsonObject json(Chamado c) {
        JsonObject o = new JsonObject();
        o.addProperty("protocolo", c.protocolo);
        o.addProperty("titulo", c.titulo);
        o.addProperty("descricao", c.descricao);
        o.addProperty("idCategoria", c.idCategoria);
        o.addProperty("categoria", c.nomeCategoria);
        o.addProperty("idPrioridade", c.idPrioridade);
        o.addProperty("prioridade", c.nomePrioridade);
        o.addProperty("corPrioridade", c.corPrioridade);
        o.addProperty("prioridadeSolicitada", c.nomePrioridadeSolicitada);
        o.addProperty("status", c.status.name());
        o.addProperty("idLocal", c.idLocal);
        o.addProperty("equipamento", c.nomeLocal);
        o.addProperty("solicitante", c.nomeSolicitante);
        o.addProperty("responsavel", c.nomeResponsavel);
        o.addProperty("abertura", iso(c.dtAbertura));
        o.addProperty("prazoAtendimento", iso(c.dtPrazoAtendimento));
        o.addProperty("prazoResolucao", iso(c.dtPrazoResolucao));
        o.addProperty("primeiroAtendimento", iso(c.dtPrimeiroAtendimento));
        o.addProperty("resolucao", iso(c.dtResolucao));
        o.addProperty("fechamento", iso(c.dtFechamento));
        o.addProperty("pausado", c.dtInicioPausa != null);
        o.addProperty("slaAtendimento", c.slaAtendimento.name());
        o.addProperty("slaResolucao", c.slaResolucao.name());
        o.addProperty("avaliacaoNota", c.avaliacaoNota);
        o.addProperty("avaliacaoComentario", c.avaliacaoComentario);
        o.addProperty("fechadoAutomaticamente", c.fechadoAutomaticamente);
        o.addProperty("dtAtualizacao", iso(c.dtAtualizacao));
        return o;
    }

    private static Chamado mapear(ResultSet rs) throws SQLException {
        Chamado c = new Chamado();
        c.id = rs.getLong("id");
        c.protocolo = rs.getString("protocolo");
        c.titulo = rs.getString("titulo");
        c.descricao = rs.getString("descricao");
        c.idCategoria = rs.getInt("id_categoria");
        c.idPrioridade = rs.getInt("id_prioridade");
        c.idPrioridadeSolicitada = rs.getInt("id_prioridade_solicitada");
        c.status = StatusChamado.valueOf(rs.getString("status"));
        c.origem = rs.getString("origem");
        c.idLocal = intOuNulo(rs, "id_local");
        c.idUsuarioSolicitante = rs.getInt("id_usuario_solicitante");
        c.idUsuarioResponsavel = intOuNulo(rs, "id_usuario_responsavel");
        c.dtAbertura = ldt(rs, "dt_abertura");
        c.dtPrazoAtendimento = ldt(rs, "dt_prazo_atendimento");
        c.dtPrazoResolucao = ldt(rs, "dt_prazo_resolucao");
        c.dtPrimeiroAtendimento = ldt(rs, "dt_primeiro_atendimento");
        c.dtResolucao = ldt(rs, "dt_resolucao");
        c.dtFechamento = ldt(rs, "dt_fechamento");
        c.minutosPausados = rs.getInt("minutos_pausados");
        c.dtInicioPausa = ldt(rs, "dt_inicio_pausa");
        c.slaAtendimento = Marca.valueOf(rs.getString("sla_atendimento"));
        c.slaResolucao = Marca.valueOf(rs.getString("sla_resolucao"));
        c.avaliacaoNota = intOuNulo(rs, "avaliacao_nota");
        c.avaliacaoComentario = rs.getString("avaliacao_comentario");
        c.dtAvaliacao = ldt(rs, "dt_avaliacao");
        c.fechadoAutomaticamente = rs.getBoolean("fechado_automaticamente");
        c.dtAtualizacao = ldt(rs, "dt_atualizacao");
        c.nomeCategoria = rs.getString("nome_categoria");
        c.nomePrioridade = rs.getString("nome_prioridade");
        c.corPrioridade = rs.getString("cor_prioridade");
        c.minutosResolucaoPrioridade = rs.getInt("minutos_resolucao_prioridade");
        c.nomePrioridadeSolicitada = rs.getString("nome_prioridade_solicitada");
        c.nomeLocal = rs.getString("nome_local");
        c.nomeSolicitante = rs.getString("nome_solicitante");
        c.emailSolicitante = rs.getString("email_solicitante");
        c.nomeResponsavel = rs.getString("nome_responsavel");
        return c;
    }

    private static void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object v = params.get(i);
            if (v instanceof Integer) ps.setInt(i + 1, (Integer) v);
            else if (v instanceof Timestamp) ps.setTimestamp(i + 1, (Timestamp) v);
            else ps.setString(i + 1, String.valueOf(v));
        }
    }

    private static void setInt(PreparedStatement ps, int idx, Integer v) throws SQLException {
        if (v == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, v);
    }

    private static Integer intOuNulo(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    private static Timestamp ts(LocalDateTime d) {
        return d == null ? null : Timestamp.valueOf(d);
    }

    private static LocalDateTime ldt(ResultSet rs, String col) throws SQLException {
        Timestamp t = rs.getTimestamp(col);
        return t == null ? null : t.toLocalDateTime();
    }

    private static String iso(LocalDateTime d) {
        return d == null ? null : d.format(ISO);
    }

    private static String br(LocalDateTime d) {
        return d == null ? "" : d.format(BR);
    }

    private static Double pct(int parte, int total) {
        return total == 0 ? null : Math.round(1000.0 * parte / total) / 10.0;
    }

    private static Double arred(ResultSet rs, String col) throws SQLException {
        double v = rs.getDouble(col);
        return rs.wasNull() ? null : Math.round(v * 10.0) / 10.0;
    }
}
```

- [ ] **Step 7: Build**

Run: `mvn -o -s "$s\settings-legacy-archiva.xml" '-DskipTests' '-Dexec.skip=true' package`
Expected: `BUILD SUCCESS`.

- [ ] **Step 8: Commit**

```bash
git add src/main/java/muralha/digital/chamado/ src/test/java/muralha/digital/chamado/FiltroChamadosTest.java src/test/java/muralha/digital/chamado/CadastrosChamadoTest.java
git commit -m "Adiciona modelos, filtros e DAOs do sistema de chamados

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 7: `ChamadoService` (regras transacionais)

**Files:**
- Create: `src/main/java/muralha/digital/chamado/ChamadoService.java`
- Test: `src/test/java/muralha/digital/chamado/ChamadoServiceRegrasTest.java`

**Interfaces:**
- Consumes: tudo das Tarefas 2–6; `com.consilux.lib.Conexao.getConexao()`; `AuditoriaService.registrar(HttpServletRequest, String, String, String, String)`.
- Produces:
  - `ChamadoService()` / `ChamadoService(Clock)`.
  - `ChamadoService.Operador(int idUsuario, Set<PerfilChamado> perfis, HttpServletRequest req)` com `veTodos()`.
  - `ChamadoService.NovoChamado` (campos `titulo, descricao, idCategoria, idPrioridade, idLocal`).
  - `ChamadoService.ParametrosAcao` (campos `texto, idPrioridade, nota, arquivos`).
  - `abrir(Operador, NovoChamado, List<ArquivoEnviado>) : Chamado`; `executar(Operador, String protocolo, AcaoChamado, ParametrosAcao) : Chamado`; `obterVisivel(Connection, Operador, String protocolo) : Chamado`; `atualizarSla() : void`; `fecharResolvidosAntigos() : int`; `static armazenamento(Connection) : ArmazenamentoAnexo`; `static encerrarPausa(Chamado, LocalDateTime)`; `static recalcularMarcas(Chamado, LocalDateTime agora, int percentualRisco)`.

- [ ] **Step 1: Escrever o teste que falha (regras puras do serviço)**

```java
// src/test/java/muralha/digital/chamado/ChamadoServiceRegrasTest.java
package muralha.digital.chamado;

import muralha.digital.chamado.CalculadoraSla.Marca;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

public class ChamadoServiceRegrasTest {

    private static final LocalDateTime ABERTURA = LocalDateTime.of(2026, 10, 15, 8, 0);

    private static Chamado chamadoAlta() {
        Chamado c = new Chamado();
        c.status = StatusChamado.EM_ATENDIMENTO;
        c.dtAbertura = ABERTURA;
        c.minutosResolucaoPrioridade = 480;
        c.dtPrazoAtendimento = CalculadoraSla.prazoAtendimento(ABERTURA, 120);
        c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(ABERTURA, 480, 0);
        return c;
    }

    @Test
    public void encerrarPausaSomaMinutosEEstendePrazo() {
        Chamado c = chamadoAlta();
        c.dtInicioPausa = ABERTURA.plusHours(1);
        ChamadoService.encerrarPausa(c, ABERTURA.plusHours(3));
        assertEquals(c.minutosPausados, 120);
        assertNull(c.dtInicioPausa);
        assertEquals(c.dtPrazoResolucao, ABERTURA.plusMinutes(480 + 120));
    }

    @Test
    public void encerrarPausaSemPausaNaoFazNada() {
        Chamado c = chamadoAlta();
        ChamadoService.encerrarPausa(c, ABERTURA.plusHours(3));
        assertEquals(c.minutosPausados, 0);
        assertEquals(c.dtPrazoResolucao, ABERTURA.plusMinutes(480));
    }

    @Test
    public void reclassificacaoDepoisDePausaEReaberturaPreservaPausas() {
        Chamado c = chamadoAlta();
        c.dtInicioPausa = ABERTURA.plusHours(1);              // aguardando solicitante
        ChamadoService.encerrarPausa(c, ABERTURA.plusHours(2)); // +60
        c.dtInicioPausa = ABERTURA.plusHours(3);              // resolvido
        ChamadoService.encerrarPausa(c, ABERTURA.plusHours(5)); // reaberto: +120
        assertEquals(c.minutosPausados, 180);
        // reclassifica para Critica (240 min): prazo = abertura + 240 + 180
        c.minutosResolucaoPrioridade = 240;
        c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(c.dtAbertura, 240, c.minutosPausados);
        assertEquals(c.dtPrazoResolucao, ABERTURA.plusMinutes(420));
    }

    @Test
    public void recalcularMarcasUsaPausaDoChamado() {
        Chamado c = chamadoAlta();
        c.slaResolucao = Marca.EM_RISCO;
        c.dtInicioPausa = ABERTURA.plusHours(1);
        ChamadoService.recalcularMarcas(c, ABERTURA.plusDays(2), 80);
        assertEquals(c.slaResolucao, Marca.EM_RISCO, "pausado: marca de resolucao nao evolui");
        assertEquals(c.slaAtendimento, Marca.VENCIDO, "atendimento nao pausa");
    }

    @Test
    public void recalcularMarcasCumpridoAoResolver() {
        Chamado c = chamadoAlta();
        c.dtPrimeiroAtendimento = ABERTURA.plusMinutes(30);
        c.dtResolucao = ABERTURA.plusHours(4);
        ChamadoService.recalcularMarcas(c, ABERTURA.plusHours(4), 80);
        assertEquals(c.slaAtendimento, Marca.CUMPRIDO);
        assertEquals(c.slaResolucao, Marca.CUMPRIDO);
    }
}
```

- [ ] **Step 2: Rodar e ver falhar**

Run: `mvn -o -s "$s\settings-legacy-archiva.xml" '-Dexec.skip=true' '-Dtest=ChamadoServiceRegrasTest' '-DfailIfNoTests=false' test`
Expected: FAIL de compilação (`cannot find symbol: class ChamadoService`).

- [ ] **Step 3: Implementar**

```java
// src/main/java/muralha/digital/chamado/ChamadoService.java
package muralha.digital.chamado;

import com.consilux.lib.Conexao;
import muralha.digital.auditoria.AuditoriaService;
import muralha.digital.chamado.CalculadoraSla.Marca;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Regras do Sistema de Chamados; cada operacao roda em uma transacao (spec §3.1). */
public class ChamadoService {

    private static final Logger logger = Logger.getLogger(ChamadoService.class);

    private final Clock clock;

    public ChamadoService() {
        this(Clock.systemDefaultZone());
    }

    public ChamadoService(Clock clock) {
        this.clock = clock;
    }

    /** Milissegundos, como DATETIME2(3): evita divergencia entre memoria e banco. */
    private LocalDateTime agora() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.MILLIS);
    }

    public static class Operador {
        public final int idUsuario;
        public final Set<PerfilChamado> perfis;
        public final HttpServletRequest req;

        public Operador(int idUsuario, Set<PerfilChamado> perfis, HttpServletRequest req) {
            this.idUsuario = idUsuario;
            this.perfis = perfis;
            this.req = req;
        }

        public boolean veTodos() {
            return PerfilChamado.veTodos(perfis);
        }
    }

    public static class NovoChamado {
        public String titulo;
        public String descricao;
        public int idCategoria;
        public int idPrioridade;
        public Integer idLocal;
    }

    public static class ParametrosAcao {
        public String texto;
        public Integer idPrioridade;
        public Integer nota;
        public List<ArquivoEnviado> arquivos = new ArrayList<>();
    }

    public Chamado abrir(Operador op, NovoChamado d, List<ArquivoEnviado> arquivos) throws Exception {
        if (!op.perfis.contains(PerfilChamado.SOLICITANTE) && !op.perfis.contains(PerfilChamado.GESTOR)) {
            throw new RegraChamadoException("Seu perfil não permite abrir chamados.");
        }
        String titulo = obrigatorio(d.titulo, "Título", 150);
        String descricao = obrigatorio(d.descricao, "Descrição", 8000);
        ArmazenamentoAnexo.validarQuantidade(arquivos);

        Connection conn = null;
        ArmazenamentoAnexo armazenamento = null;
        List<String> gravados = new ArrayList<>();
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);
            Categoria cat = ChamadoConfigDao.obterCategoria(conn, d.idCategoria);
            if (cat == null || !cat.ativo) throw new RegraChamadoException("Categoria inválida.");
            Prioridade pri = ChamadoConfigDao.obterPrioridade(conn, d.idPrioridade);
            if (pri == null || !pri.ativo) throw new RegraChamadoException("Prioridade inválida.");
            if (cat.exigeEquipamento && d.idLocal == null) {
                throw new RegraChamadoException("Esta categoria exige a indicação do equipamento.");
            }
            if (d.idLocal != null && !ChamadoConfigDao.equipamentoExiste(conn, d.idLocal)) {
                throw new RegraChamadoException("Equipamento inválido.");
            }

            LocalDateTime agora = agora();
            Chamado c = new Chamado();
            c.protocolo = GeradorProtocolo.proximo(conn, agora.getYear());
            c.titulo = titulo;
            c.descricao = descricao;
            c.idCategoria = cat.id;
            c.idPrioridade = pri.id;
            c.idPrioridadeSolicitada = pri.id;
            c.status = StatusChamado.ABERTO;
            c.idLocal = d.idLocal;
            c.idUsuarioSolicitante = op.idUsuario;
            c.dtAbertura = agora;
            c.dtPrazoAtendimento = CalculadoraSla.prazoAtendimento(agora, pri.minutosAtendimento);
            c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(agora, pri.minutosResolucao, 0);
            c.nomePrioridade = pri.nome;
            c.id = ChamadoDao.inserir(conn, c);

            long idHist = ChamadoDao.inserirHistorico(conn, c.id, op.idUsuario, "ABERTURA", null,
                    StatusChamado.ABERTO.name(), "Chamado aberto com prioridade " + pri.nome + ".");
            if (arquivos != null && !arquivos.isEmpty()) {
                armazenamento = armazenamento(conn);
                gravarAnexos(conn, armazenamento, c, idHist, op.idUsuario, arquivos, gravados);
            }
            ChamadoDao.enfileirarNotificacao(conn, c.id, EmailChamado.CONFIRMACAO_ABERTURA,
                    ChamadoConfigDao.emailUsuario(conn, op.idUsuario));
            conn.commit();
            auditar(op, "ABRIR", c.protocolo, "Chamado aberto: " + titulo);
            return c;
        } catch (Exception e) {
            desfazer(conn, armazenamento, gravados);
            throw e;
        } finally {
            fechar(conn);
        }
    }

    public Chamado executar(Operador op, String protocolo, AcaoChamado acao, ParametrosAcao p) throws Exception {
        if (acao == null) throw new RegraChamadoException("Ação inválida.");
        String texto = acao.exigeTexto() ? obrigatorio(p.texto, "Observação", 2000) : opcional(p.texto, 1000);
        List<ArquivoEnviado> arquivos = p.arquivos == null ? List.of() : p.arquivos;
        if (!arquivos.isEmpty() && acao != AcaoChamado.RESPONDER) {
            throw new RegraChamadoException("Anexos só podem ser enviados na abertura ou na resposta.");
        }
        ArmazenamentoAnexo.validarQuantidade(arquivos);

        Connection conn = null;
        ArmazenamentoAnexo armazenamento = null;
        List<String> gravados = new ArrayList<>();
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);
            Chamado c = obterVisivel(conn, op, protocolo);
            boolean autor = c.idUsuarioSolicitante == op.idUsuario;
            StatusChamado anterior = c.status;
            StatusChamado destino = MaquinaEstados.destino(anterior, acao, op.perfis, autor);
            LocalDateTime agora = agora();
            int risco = ChamadoConfigDao.valorInt(conn, "chamado_sla_risco_percentual", 80);
            String tipoHistorico = "TRANSICAO";
            boolean notificarReclassificacao = false;

            switch (acao) {
                case ASSUMIR:
                    c.idUsuarioResponsavel = op.idUsuario;
                    tipoHistorico = "ATRIBUICAO";
                    if (texto == null) texto = "Chamado assumido.";
                    break;
                case INICIAR:
                    if (c.idUsuarioResponsavel == null) c.idUsuarioResponsavel = op.idUsuario;
                    c.dtPrimeiroAtendimento = agora;
                    break;
                case AGUARDAR:
                    c.dtInicioPausa = agora;
                    break;
                case RESPONDER:
                    encerrarPausa(c, agora);
                    tipoHistorico = "RESPOSTA";
                    break;
                case RESOLVER:
                    c.dtResolucao = agora;
                    c.dtInicioPausa = agora;
                    break;
                case AVALIAR:
                    if (p.nota == null || p.nota < 1 || p.nota > 5) {
                        throw new RegraChamadoException("Informe uma nota de 1 a 5.");
                    }
                    c.avaliacaoNota = p.nota;
                    c.avaliacaoComentario = texto;
                    c.dtAvaliacao = agora;
                    c.dtFechamento = agora;
                    c.dtInicioPausa = null;
                    tipoHistorico = "AVALIACAO";
                    texto = "Nota " + p.nota + (texto == null ? "." : ": " + texto);
                    break;
                case REABRIR:
                    encerrarPausa(c, agora);
                    c.dtResolucao = null;
                    break;
                case CANCELAR:
                    c.dtFechamento = agora;
                    c.dtInicioPausa = null;
                    break;
                case RECLASSIFICAR:
                    if (p.idPrioridade == null) throw new RegraChamadoException("Informe a nova prioridade.");
                    Prioridade nova = ChamadoConfigDao.obterPrioridade(conn, p.idPrioridade);
                    if (nova == null || !nova.ativo) throw new RegraChamadoException("Prioridade inválida.");
                    if (nova.id == c.idPrioridade) throw new RegraChamadoException("O chamado já está com essa prioridade.");
                    texto = "Prioridade alterada de " + c.nomePrioridade + " para " + nova.nome + ". Justificativa: " + texto;
                    c.idPrioridade = nova.id;
                    c.nomePrioridade = nova.nome;
                    c.minutosResolucaoPrioridade = nova.minutosResolucao;
                    c.dtPrazoAtendimento = CalculadoraSla.prazoAtendimento(c.dtAbertura, nova.minutosAtendimento);
                    c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(c.dtAbertura, nova.minutosResolucao, c.minutosPausados);
                    tipoHistorico = "RECLASSIFICACAO";
                    notificarReclassificacao = true;
                    break;
                default:
                    break;
            }
            c.status = destino;
            recalcularMarcas(c, agora, risco);

            if (ChamadoDao.atualizarEstado(conn, c, anterior) == 0) {
                throw new RegraChamadoException("Chamado alterado por outro usuário; recarregue a página.");
            }
            long idHist = ChamadoDao.inserirHistorico(conn, c.id, op.idUsuario, tipoHistorico,
                    anterior.name(), destino.name(), texto);
            if (!arquivos.isEmpty()) {
                armazenamento = armazenamento(conn);
                gravarAnexos(conn, armazenamento, c, idHist, op.idUsuario, arquivos, gravados);
            }
            if (notificarReclassificacao) {
                ChamadoDao.enfileirarNotificacao(conn, c.id, EmailChamado.RECLASSIFICACAO, c.emailSolicitante);
            }
            conn.commit();
            auditar(op, acao.name(), c.protocolo, texto);
            return c;
        } catch (Exception e) {
            desfazer(conn, armazenamento, gravados);
            throw e;
        } finally {
            fechar(conn);
        }
    }

    /** Lanca ChamadoNaoEncontradoException tambem quando o chamado e de outro solicitante (spec §3.3). */
    public Chamado obterVisivel(Connection conn, Operador op, String protocolo) throws Exception {
        Chamado c = ChamadoDao.obterPorProtocolo(conn, protocolo);
        if (c == null || (!op.veTodos() && c.idUsuarioSolicitante != op.idUsuario)) {
            throw new ChamadoNaoEncontradoException(protocolo);
        }
        return c;
    }

    /** Job: atualiza marcas de SLA e registra no historico a entrada em EM_RISCO/VENCIDO. */
    public void atualizarSla() throws Exception {
        try (Connection conn = Conexao.getConexao()) {
            int risco = ChamadoConfigDao.valorInt(conn, "chamado_sla_risco_percentual", 80);
            LocalDateTime agora = agora();
            for (Chamado c : ChamadoDao.listarNaoTerminais(conn)) {
                Marca antesAt = c.slaAtendimento;
                Marca antesRes = c.slaResolucao;
                recalcularMarcas(c, agora, risco);
                if (antesAt == c.slaAtendimento && antesRes == c.slaResolucao) continue;
                if (ChamadoDao.atualizarEstado(conn, c, c.status) == 0) continue; // alterado agora por alguem
                if (alerta(antesAt, c.slaAtendimento)) {
                    ChamadoDao.inserirHistorico(conn, c.id, null, "SLA", null, null,
                            "SLA de atendimento: " + c.slaAtendimento.name());
                }
                if (alerta(antesRes, c.slaResolucao)) {
                    ChamadoDao.inserirHistorico(conn, c.id, null, "SLA", null, null,
                            "SLA de resolução: " + c.slaResolucao.name());
                }
            }
        }
    }

    /** Job: fecha RESOLVIDO sem acao do solicitante ha N dias (spec D7). */
    public int fecharResolvidosAntigos() throws Exception {
        int fechados = 0;
        try (Connection conn = Conexao.getConexao()) {
            int dias = ChamadoConfigDao.valorInt(conn, "chamado_fechamento_auto_dias", 5);
            LocalDateTime agora = agora();
            for (Chamado c : ChamadoDao.listarResolvidosAntesDe(conn, agora.minusDays(dias))) {
                conn.setAutoCommit(false);
                try {
                    c.status = StatusChamado.FECHADO;
                    c.dtFechamento = agora;
                    c.fechadoAutomaticamente = true;
                    c.dtInicioPausa = null;
                    if (ChamadoDao.atualizarEstado(conn, c, StatusChamado.RESOLVIDO) == 0) {
                        conn.rollback();
                        continue;
                    }
                    ChamadoDao.inserirHistorico(conn, c.id, null, "TRANSICAO", StatusChamado.RESOLVIDO.name(),
                            StatusChamado.FECHADO.name(),
                            "Fechado automaticamente após " + dias + " dias sem avaliação do solicitante.");
                    conn.commit();
                    fechados++;
                } catch (Exception e) {
                    conn.rollback();
                    logger.error("Falha ao fechar automaticamente o chamado " + c.protocolo, e);
                } finally {
                    conn.setAutoCommit(true);
                }
            }
        }
        return fechados;
    }

    public static ArmazenamentoAnexo armazenamento(Connection conn) throws Exception {
        String dir = ChamadoConfigDao.valor(conn, "chamado_diretorio_anexos", null);
        if (dir == null) throw new RegraChamadoException("Diretório de anexos não configurado.");
        long maxBytes = ChamadoConfigDao.valorInt(conn, "chamado_anexo_max_mb", 10) * 1024L * 1024L;
        return new ArmazenamentoAnexo(Paths.get(dir), maxBytes);
    }

    static void encerrarPausa(Chamado c, LocalDateTime agora) {
        if (c.dtInicioPausa == null) return;
        c.minutosPausados += CalculadoraSla.minutosEntre(c.dtInicioPausa, agora);
        c.dtInicioPausa = null;
        c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(c.dtAbertura, c.minutosResolucaoPrioridade, c.minutosPausados);
    }

    static void recalcularMarcas(Chamado c, LocalDateTime agora, int percentualRisco) {
        c.slaAtendimento = CalculadoraSla.marca(c.dtAbertura, c.dtPrazoAtendimento, c.dtPrimeiroAtendimento,
                agora, percentualRisco, false, c.slaAtendimento);
        c.slaResolucao = CalculadoraSla.marca(c.dtAbertura, c.dtPrazoResolucao, c.dtResolucao,
                agora, percentualRisco, c.dtInicioPausa != null, c.slaResolucao);
    }

    private static boolean alerta(Marca antes, Marca depois) {
        return antes != depois && (depois == Marca.EM_RISCO || depois == Marca.VENCIDO);
    }

    private static void gravarAnexos(Connection conn, ArmazenamentoAnexo armazenamento, Chamado c, long idHist,
                                     int idUsuario, List<ArquivoEnviado> arquivos, List<String> gravados)
            throws Exception {
        for (ArquivoEnviado arq : arquivos) {
            AnexoGravado a;
            try {
                a = armazenamento.gravar(c.protocolo, arq);
            } catch (java.io.IOException e) {
                logger.error("Falha ao gravar anexo do chamado " + c.protocolo, e);
                throw new RegraChamadoException("Não foi possível gravar os anexos; tente novamente.");
            }
            gravados.add(a.caminhoRelativo);
            ChamadoDao.inserirAnexo(conn, c.id, idHist, a, idUsuario);
        }
    }

    private static void desfazer(Connection conn, ArmazenamentoAnexo armazenamento, List<String> gravados) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (Exception e) {
                logger.error("Falha no rollback de chamado", e);
            }
        }
        if (armazenamento != null) {
            for (String g : gravados) armazenamento.removerSilenciosamente(g);
        }
    }

    private static void fechar(Connection conn) {
        if (conn == null) return;
        try {
            conn.setAutoCommit(true);
            conn.close();
        } catch (Exception ignorada) {
            // conexao devolvida ao pool
        }
    }

    private static void auditar(Operador op, String operacao, String protocolo, String descricao) {
        if (op.req == null) return;
        String d = descricao == null ? "" : (descricao.length() > 500 ? descricao.substring(0, 500) : descricao);
        AuditoriaService.registrar(op.req, "Chamado", operacao, protocolo, d);
    }

    private static String obrigatorio(String v, String campo, int max) {
        if (v == null || v.trim().isEmpty()) throw new RegraChamadoException(campo + " é obrigatório(a).");
        String t = v.trim();
        if (t.length() > max) throw new RegraChamadoException(campo + " deve ter até " + max + " caracteres.");
        return t;
    }

    private static String opcional(String v, int max) {
        if (v == null || v.trim().isEmpty()) return null;
        String t = v.trim();
        return t.length() > max ? t.substring(0, max) : t;
    }
}
```

- [ ] **Step 4: Rodar e ver passar**

Run: mesmo comando do Step 2. Expected: `Tests run: 5, Failures: 0`.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/muralha/digital/chamado/ChamadoService.java src/test/java/muralha/digital/chamado/ChamadoServiceRegrasTest.java
git commit -m "Adiciona servico transacional do sistema de chamados

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 8: Servlets (`Chamado`, `ChamadoRelatorio`, `ChamadoConfig`)

**Files:**
- Create: `src/main/java/muralha/digital/chamado/ContextoRequisicao.java`, `ChamadoServlet.java`, `ChamadoRelatorioServlet.java`, `ChamadoConfigServlet.java`

**Interfaces:**
- Consumes: `ChamadoService`, `ChamadoDao`, `ChamadoConfigDao`, `FiltroChamados`, `ExportacaoChamados`, `MaquinaEstados.permitidas`.
- Produces: contrato HTTP da spec §3.2. Respostas `{"ok":true,...}` ou `{"ok":false,"erro":"..."}`; `ChamadoNaoEncontradoException` → HTTP 404 com JSON; `RegraChamadoException` → HTTP 200 com `ok:false`; erro inesperado → HTTP 500 com `"Erro interno."`.
  - `GET /MuralhaDigital/Chamado?acao=perfil|opcoes|listar|detalhe|anexo`
  - `POST /MuralhaDigital/Chamado` (multipart ou form): `acao=abrir|assumir|iniciar|aguardar|responder|resolver|avaliar|reabrir|cancelar|reclassificar`
  - `GET /MuralhaDigital/ChamadoRelatorio?acao=indicadores|exportar` (`formato=csv|xlsx|json`, `origem=lista|relatorio`, `agrupamento`)
  - `GET /MuralhaDigital/ChamadoConfig?acao=listar`; `POST acao=salvarPrioridade|salvarCategoria|alternarAtivo`

- [ ] **Step 1: Criar `ContextoRequisicao`**

```java
// src/main/java/muralha/digital/chamado/ContextoRequisicao.java
package muralha.digital.chamado;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.Set;

/** Sessao + acesso + perfis do Sistema de Chamados, comum aos tres servlets. */
final class ContextoRequisicao {

    private static final Gson GSON = new Gson();

    final Usuario usuario;
    final ChamadoService.Operador operador;

    private ContextoRequisicao(Usuario usuario, ChamadoService.Operador operador) {
        this.usuario = usuario;
        this.operador = operador;
    }

    /** Retorna null quando a resposta (401/403) ja foi enviada. */
    static ContextoRequisicao de(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sessao = req.getSession(false);
        Usuario usuario = sessao == null ? null : (Usuario) sessao.getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return null;
        Set<PerfilChamado> perfis;
        try (Connection conn = Conexao.getConexao()) {
            perfis = ChamadoConfigDao.perfis(conn, usuario.getId());
        } catch (Exception e) {
            throw new IOException("Falha ao obter perfis do Sistema de Chamados", e);
        }
        if (perfis.isEmpty()) {
            erro(resp, HttpServletResponse.SC_FORBIDDEN, "Usuário sem perfil no Sistema de Chamados.");
            return null;
        }
        return new ContextoRequisicao(usuario, new ChamadoService.Operador(usuario.getId(), perfis, req));
    }

    static void json(HttpServletResponse resp, JsonObject corpo) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        if (!corpo.has("ok")) corpo.addProperty("ok", true);
        resp.getWriter().write(GSON.toJson(corpo));
    }

    static void erro(HttpServletResponse resp, int status, String mensagem) throws IOException {
        resp.setStatus(status);
        JsonObject o = new JsonObject();
        o.addProperty("ok", false);
        o.addProperty("erro", mensagem);
        json(resp, o);
    }

    void exigir(boolean condicao) {
        if (!condicao) throw new RegraChamadoException("Seu perfil não permite esta operação.");
    }
}
```

- [ ] **Step 2: Criar `ChamadoServlet`**

```java
// src/main/java/muralha/digital/chamado/ChamadoServlet.java
package muralha.digital.chamado;

import com.consilux.lib.Conexao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/MuralhaDigital/Chamado")
public class ChamadoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ChamadoServlet.class);
    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ChamadoService service = new ChamadoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContextoRequisicao ctx = ContextoRequisicao.de(req, resp);
        if (ctx == null) return;
        String acao = req.getParameter("acao");
        try (Connection conn = Conexao.getConexao()) {
            if ("anexo".equals(acao)) {
                baixarAnexo(conn, ctx, req, resp);
                return;
            }
            JsonObject r = new JsonObject();
            if ("perfil".equals(acao)) {
                JsonArray perfis = new JsonArray();
                ctx.operador.perfis.forEach(p -> perfis.add(p.name()));
                r.add("perfis", perfis);
                r.addProperty("idUsuario", ctx.operador.idUsuario);
            } else if ("opcoes".equals(acao)) {
                JsonArray pri = new JsonArray();
                ChamadoConfigDao.prioridades(conn, true).forEach(p -> pri.add(p.json()));
                JsonArray cat = new JsonArray();
                ChamadoConfigDao.categorias(conn, true).forEach(c -> cat.add(c.json()));
                r.add("prioridades", pri);
                r.add("categorias", cat);
                r.add("equipamentos", ChamadoConfigDao.equipamentos(conn));
            } else if ("listar".equals(acao)) {
                FiltroChamados f = FiltroChamados.deRequest(req);
                if (!ctx.operador.veTodos()) f.idSolicitante = ctx.operador.idUsuario;
                r = ChamadoDao.listar(conn, f);
                r.addProperty("servidorAgora", LocalDateTime.now().format(ISO));
            } else if ("detalhe".equals(acao)) {
                Chamado c = service.obterVisivel(conn, ctx.operador, req.getParameter("protocolo"));
                boolean autor = c.idUsuarioSolicitante == ctx.operador.idUsuario;
                r.add("chamado", ChamadoDao.json(c));
                r.add("historico", ChamadoDao.historico(conn, c.id));
                r.add("anexos", ChamadoDao.anexos(conn, c.id));
                JsonArray acoes = new JsonArray();
                MaquinaEstados.permitidas(c.status, ctx.operador.perfis, autor).forEach(a -> acoes.add(a.getCodigo()));
                r.add("acoesPermitidas", acoes);
                r.addProperty("servidorAgora", LocalDateTime.now().format(ISO));
            } else {
                throw new RegraChamadoException("Ação inválida.");
            }
            ContextoRequisicao.json(resp, r);
        } catch (Exception e) {
            tratar(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContextoRequisicao ctx = ContextoRequisicao.de(req, resp);
        if (ctx == null) return;
        try {
            Formulario f = lerFormulario(req);
            String acao = f.campos.get("acao");
            JsonObject r = new JsonObject();
            if ("abrir".equals(acao)) {
                ChamadoService.NovoChamado d = new ChamadoService.NovoChamado();
                d.titulo = f.campos.get("titulo");
                d.descricao = f.campos.get("descricao");
                d.idCategoria = inteiro(f.campos.get("idCategoria"), "categoria");
                d.idPrioridade = inteiro(f.campos.get("idPrioridade"), "prioridade");
                String idLocal = f.campos.get("idLocal");
                d.idLocal = idLocal == null || idLocal.isEmpty() ? null : inteiro(idLocal, "equipamento");
                Chamado c = service.abrir(ctx.operador, d, f.arquivos);
                r.addProperty("protocolo", c.protocolo);
                r.addProperty("prioridade", c.nomePrioridade);
                r.addProperty("prazoAtendimento", c.dtPrazoAtendimento.format(ISO));
                r.addProperty("prazoResolucao", c.dtPrazoResolucao.format(ISO));
            } else {
                ChamadoService.ParametrosAcao p = new ChamadoService.ParametrosAcao();
                p.texto = f.campos.get("texto");
                String idPri = f.campos.get("idPrioridade");
                p.idPrioridade = idPri == null || idPri.isEmpty() ? null : inteiro(idPri, "prioridade");
                String nota = f.campos.get("nota");
                p.nota = nota == null || nota.isEmpty() ? null : inteiro(nota, "nota");
                p.arquivos = f.arquivos;
                Chamado c = service.executar(ctx.operador, f.campos.get("protocolo"), AcaoChamado.deCodigo(acao), p);
                r.addProperty("status", c.status.name());
                r.addProperty("prazoAtendimento", c.dtPrazoAtendimento.format(ISO));
                r.addProperty("prazoResolucao", c.dtPrazoResolucao.format(ISO));
            }
            ContextoRequisicao.json(resp, r);
        } catch (Exception e) {
            tratar(resp, e);
        }
    }

    private void baixarAnexo(Connection conn, ContextoRequisicao ctx, HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        JsonObject a = ChamadoDao.obterAnexo(conn, Long.parseLong(req.getParameter("id")));
        if (a == null) throw new ChamadoNaoEncontradoException("anexo");
        service.obterVisivel(conn, ctx.operador, a.get("protocolo").getAsString());
        Path arquivo = ChamadoService.armazenamento(conn).resolver(a.get("caminho").getAsString());
        if (!Files.exists(arquivo)) throw new ChamadoNaoEncontradoException("anexo");
        String nome = URLEncoder.encode(a.get("nome").getAsString(), StandardCharsets.UTF_8).replace("+", "%20");
        resp.setContentType(a.get("tipoMime").getAsString());
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + nome);
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setContentLengthLong(Files.size(arquivo));
        Files.copy(arquivo, resp.getOutputStream());
    }

    private static final class Formulario {
        final Map<String, String> campos = new HashMap<>();
        final List<ArquivoEnviado> arquivos = new ArrayList<>();
    }

    private Formulario lerFormulario(HttpServletRequest req) throws Exception {
        Formulario f = new Formulario();
        if (!ServletFileUpload.isMultipartContent(req)) {
            req.setCharacterEncoding("UTF-8");
            for (String nome : req.getParameterMap().keySet()) f.campos.put(nome, req.getParameter(nome));
            return f;
        }
        long maxBytes;
        try (Connection conn = Conexao.getConexao()) {
            maxBytes = ChamadoConfigDao.valorInt(conn, "chamado_anexo_max_mb", 10) * 1024L * 1024L;
        }
        DiskFileItemFactory fabrica = new DiskFileItemFactory();
        fabrica.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(fabrica);
        upload.setHeaderEncoding("UTF-8");
        upload.setFileSizeMax(maxBytes);
        upload.setSizeMax(maxBytes * ArmazenamentoAnexo.MAX_ARQUIVOS + 1024L * 1024L);
        try {
            for (FileItem item : upload.parseRequest(req)) {
                if (item.isFormField()) {
                    f.campos.put(item.getFieldName(), item.getString("UTF-8"));
                } else if (item.getSize() > 0) {
                    f.arquivos.add(new ArquivoEnviado(item.getName(), item.get()));
                }
            }
        } catch (FileUploadBase.FileSizeLimitExceededException | FileUploadBase.SizeLimitExceededException e) {
            throw new RegraChamadoException("Arquivo(s) acima do limite de " + (maxBytes / (1024 * 1024)) + " MB.");
        }
        return f;
    }

    private static int inteiro(String v, String campo) {
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            throw new RegraChamadoException("Informe " + campo + ".");
        }
    }

    static void tratar(HttpServletResponse resp, Exception e) throws IOException {
        if (resp.isCommitted()) {
            logger.error("Erro apos inicio da resposta", e);
        } else if (e instanceof ChamadoNaoEncontradoException) {
            ContextoRequisicao.erro(resp, HttpServletResponse.SC_NOT_FOUND, "Chamado não encontrado.");
        } else if (e instanceof RegraChamadoException) {
            ContextoRequisicao.erro(resp, HttpServletResponse.SC_OK, e.getMessage());
        } else {
            logger.error("Erro no Sistema de Chamados", e);
            ContextoRequisicao.erro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno.");
        }
    }
}
```

- [ ] **Step 3: Criar `ChamadoRelatorioServlet`**

```java
// src/main/java/muralha/digital/chamado/ChamadoRelatorioServlet.java
package muralha.digital.chamado;

import com.consilux.lib.Conexao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/MuralhaDigital/ChamadoRelatorio")
public class ChamadoRelatorioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter ARQ = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContextoRequisicao ctx = ContextoRequisicao.de(req, resp);
        if (ctx == null) return;
        String acao = req.getParameter("acao");
        try (Connection conn = Conexao.getConexao()) {
            FiltroChamados f = FiltroChamados.deRequest(req);
            String agrupamento = req.getParameter("agrupamento") == null ? "mes" : req.getParameter("agrupamento");
            if ("indicadores".equals(acao)) {
                ctx.exigir(ctx.operador.veTodos());
                JsonObject r = new JsonObject();
                r.add("linhas", ChamadoDao.indicadores(conn, f, agrupamento));
                ContextoRequisicao.json(resp, r);
            } else if ("exportar".equals(acao)) {
                boolean relatorio = "relatorio".equals(req.getParameter("origem"));
                if (relatorio) ctx.exigir(ctx.operador.veTodos());
                else if (!ctx.operador.veTodos()) f.idSolicitante = ctx.operador.idUsuario;
                exportar(conn, ctx, req, resp, f, relatorio, agrupamento);
            } else {
                throw new RegraChamadoException("Ação inválida.");
            }
        } catch (Exception e) {
            ChamadoServlet.tratar(resp, e);
        }
    }

    private void exportar(Connection conn, ContextoRequisicao ctx, HttpServletRequest req, HttpServletResponse resp,
                          FiltroChamados f, boolean relatorio, String agrupamento) throws Exception {
        List<String> chaves = relatorio ? ExportacaoChamados.INDICADORES_CHAVES : ExportacaoChamados.LISTA_CHAVES;
        List<String> rotulos = relatorio ? ExportacaoChamados.INDICADORES_ROTULOS : ExportacaoChamados.LISTA_ROTULOS;
        JsonArray linhas = relatorio ? ChamadoDao.indicadores(conn, f, agrupamento) : ChamadoDao.listarParaExportacao(conn, f);
        String titulo = relatorio ? "Relatório de chamados por " + agrupamento : "Chamados";
        String filtros = f.descricao();
        String usuario = ctx.usuario.getUsuario() == null ? "" : ctx.usuario.getUsuario().trim();
        LocalDateTime agora = LocalDateTime.now();
        String formato = req.getParameter("formato");
        String base = (relatorio ? "relatorio_chamados_" : "chamados_") + agora.format(ARQ);

        AuditoriaService.registrar(req, "Chamado", "EXPORTAR", formato, titulo + " — " + filtros);

        if ("csv".equals(formato)) {
            enviarArquivo(resp, "text/csv; charset=UTF-8", base + ".csv",
                    ExportacaoChamados.csv(chaves, rotulos, linhas, titulo, filtros, usuario, agora));
        } else if ("xlsx".equals(formato)) {
            enviarArquivo(resp, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", base + ".xlsx",
                    ExportacaoChamados.xlsx(chaves, rotulos, linhas, titulo, filtros, usuario, agora));
        } else if ("json".equals(formato)) {
            // Dados para o PDF gerado no navegador (jsPDF), com o mesmo hash do CSV/XLSX
            JsonObject r = new JsonObject();
            r.addProperty("titulo", titulo);
            r.addProperty("filtros", filtros);
            r.addProperty("usuario", usuario);
            r.addProperty("arquivo", base + ".pdf");
            JsonArray cab = new JsonArray();
            rotulos.forEach(rotulo -> cab.add(rotulo));
            r.add("rotulos", cab);
            JsonArray corpo = new JsonArray();
            for (JsonElement e : linhas) {
                JsonObject o = e.getAsJsonObject();
                JsonArray linha = new JsonArray();
                for (String k : chaves) linha.add(o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "");
                corpo.add(linha);
            }
            r.add("linhas", corpo);
            r.addProperty("sha256", ExportacaoChamados.hashDados(chaves, rotulos, linhas));
            ContextoRequisicao.json(resp, r);
        } else {
            throw new RegraChamadoException("Formato inválido.");
        }
    }

    private static void enviarArquivo(HttpServletResponse resp, String tipo, String nome, byte[] dados)
            throws IOException {
        resp.setContentType(tipo);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + nome + "\"");
        resp.setContentLength(dados.length);
        resp.getOutputStream().write(dados);
    }
}
```

- [ ] **Step 4: Criar `ChamadoConfigServlet`**

```java
// src/main/java/muralha/digital/chamado/ChamadoConfigServlet.java
package muralha.digital.chamado;

import com.consilux.lib.Conexao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/MuralhaDigital/ChamadoConfig")
public class ChamadoConfigServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContextoRequisicao ctx = ContextoRequisicao.de(req, resp);
        if (ctx == null) return;
        try (Connection conn = Conexao.getConexao()) {
            ctx.exigir(ctx.operador.perfis.contains(PerfilChamado.EQUIPE));
            if (!"listar".equals(req.getParameter("acao"))) throw new RegraChamadoException("Ação inválida.");
            JsonObject r = new JsonObject();
            JsonArray pri = new JsonArray();
            ChamadoConfigDao.prioridades(conn, false).forEach(p -> pri.add(p.json()));
            JsonArray cat = new JsonArray();
            ChamadoConfigDao.categorias(conn, false).forEach(c -> cat.add(c.json()));
            r.add("prioridades", pri);
            r.add("categorias", cat);
            ContextoRequisicao.json(resp, r);
        } catch (Exception e) {
            ChamadoServlet.tratar(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContextoRequisicao ctx = ContextoRequisicao.de(req, resp);
        if (ctx == null) return;
        req.setCharacterEncoding("UTF-8");
        try (Connection conn = Conexao.getConexao()) {
            ctx.exigir(ctx.operador.perfis.contains(PerfilChamado.EQUIPE));
            String acao = req.getParameter("acao");
            if ("salvarPrioridade".equals(acao)) {
                Prioridade p = new Prioridade();
                p.id = inteiro(req, "id", 0);
                p.codigo = req.getParameter("codigo");
                p.nome = req.getParameter("nome");
                p.ordem = inteiro(req, "ordem", 0);
                p.minutosAtendimento = inteiro(req, "minutosAtendimento", 0);
                p.minutosResolucao = inteiro(req, "minutosResolucao", 0);
                p.cor = req.getParameter("cor");
                ChamadoConfigDao.salvarPrioridade(conn, p);
                AuditoriaService.registrar(req, "Chamado", "SALVAR_PRIORIDADE", p.codigo,
                        p.nome + " " + p.minutosAtendimento + "/" + p.minutosResolucao + " min");
            } else if ("salvarCategoria".equals(acao)) {
                Categoria c = new Categoria();
                c.id = inteiro(req, "id", 0);
                c.nome = req.getParameter("nome");
                int idPai = inteiro(req, "idPai", 0);
                c.idPai = idPai == 0 ? null : idPai;
                c.exigeEquipamento = "true".equals(req.getParameter("exigeEquipamento"));
                ChamadoConfigDao.salvarCategoria(conn, c);
                AuditoriaService.registrar(req, "Chamado", "SALVAR_CATEGORIA", String.valueOf(c.id), c.nome);
            } else if ("alternarAtivo".equals(acao)) {
                String tipo = req.getParameter("tipo");
                int id = inteiro(req, "id", 0);
                ChamadoConfigDao.alternarAtivo(conn, tipo, id);
                AuditoriaService.registrar(req, "Chamado", "ALTERNAR_ATIVO", tipo + "#" + id, "");
            } else {
                throw new RegraChamadoException("Ação inválida.");
            }
            ContextoRequisicao.json(resp, new JsonObject());
        } catch (Exception e) {
            ChamadoServlet.tratar(resp, e);
        }
    }

    private static int inteiro(HttpServletRequest req, String nome, int padrao) {
        String v = req.getParameter(nome);
        if (v == null || v.trim().isEmpty()) return padrao;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            throw new RegraChamadoException("Valor inválido em " + nome + ".");
        }
    }
}
```

- [ ] **Step 5: Build e teste rápido**

Run: `mvn -o -s "$s\settings-legacy-archiva.xml" '-DskipTests' '-Dexec.skip=true' package` → `BUILD SUCCESS`.

Suba o servidor (comando "Executar"), faça login com um usuário do grupo "Chamados - Solicitante" e, no console do navegador:

```js
fetch('/MuralhaDigital/Chamado?acao=perfil').then(r => r.json()).then(console.log)   // {perfis:["SOLICITANTE"], ok:true}
fetch('/MuralhaDigital/Chamado?acao=opcoes').then(r => r.json()).then(console.log)   // 4 prioridades, 22 categorias, equipamentos
fetch('/MuralhaDigital/ChamadoConfig?acao=listar').then(r => r.json()).then(console.log) // {ok:false, erro:"Seu perfil não permite..."}
```

- [ ] **Step 6: Commit**

```bash
git add src/main/java/muralha/digital/chamado/ContextoRequisicao.java src/main/java/muralha/digital/chamado/ChamadoServlet.java src/main/java/muralha/digital/chamado/ChamadoRelatorioServlet.java src/main/java/muralha/digital/chamado/ChamadoConfigServlet.java
git commit -m "Adiciona servlets do sistema de chamados

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 9: Jobs Quartz e registro no `Agendador`

**Files:**
- Create: `src/main/java/muralha/digital/chamado/ChamadoSlaJob.java`, `ChamadoEmailJob.java`
- Modify: `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` (constantes perto da linha 122; chamadas depois de `reajustaJobIncidenteImport();` na linha ~190; métodos novos depois de `reajustaJobIncidenteImport()`)

**Interfaces:**
- Consumes: `ChamadoService#atualizarSla()`, `#fecharResolvidosAntigos()`, `EmailChamado.*`, `ChamadoConfigDao.valor`.
- Produces: jobs `JOB_CHAMADO_SLA` (5 min) e `JOB_CHAMADO_EMAIL` (1 min) no grupo `GRUPO_CHAMADOS`.

- [ ] **Step 1: Criar os jobs**

```java
// src/main/java/muralha/digital/chamado/ChamadoSlaJob.java
package muralha.digital.chamado;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/** A cada 5 min: marcas de SLA e fechamento automatico (spec §3.5). */
@DisallowConcurrentExecution
public class ChamadoSlaJob implements Job {

    private static final Logger logger = Logger.getLogger(ChamadoSlaJob.class);

    @Override
    public void execute(JobExecutionContext ctx) {
        ChamadoService service = new ChamadoService();
        try {
            service.atualizarSla();
        } catch (Exception e) {
            logger.error("ChamadoSlaJob: falha ao atualizar SLA", e);
        }
        try {
            int fechados = service.fecharResolvidosAntigos();
            if (fechados > 0) logger.info("ChamadoSlaJob: " + fechados + " chamado(s) fechado(s) automaticamente.");
        } catch (Exception e) {
            logger.error("ChamadoSlaJob: falha no fechamento automatico", e);
        }
    }
}
```

```java
// src/main/java/muralha/digital/chamado/ChamadoEmailJob.java
package muralha.digital.chamado;

import com.consilux.lib.Conexao;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** A cada 1 min: envia a fila muralha.chamado_notificacao (spec §3.5). */
@DisallowConcurrentExecution
public class ChamadoEmailJob implements Job {

    private static final Logger logger = Logger.getLogger(ChamadoEmailJob.class);

    private static final class Pendente {
        long id;
        String tipo;
        String destinatario;
        int tentativas;
        String protocolo;
        String titulo;
        String prioridade;
        Timestamp prazoAtendimento;
        Timestamp prazoResolucao;
        String justificativa;
    }

    @Override
    public void execute(JobExecutionContext ctx) {
        try (Connection conn = Conexao.getConexao()) {
            if (!"1".equals(ChamadoConfigDao.valor(conn, "chamado_email_ativo", "1"))) return;
            for (Pendente p : pendentes(conn)) {
                boolean enviado = false;
                String erro = null;
                try {
                    String html = EmailChamado.corpoHtml(p.tipo, p.protocolo, p.titulo, p.prioridade,
                            p.prazoAtendimento.toLocalDateTime(), p.prazoResolucao.toLocalDateTime(), p.justificativa);
                    enviado = EmailChamado.enviar(p.destinatario, EmailChamado.assunto(p.tipo, p.protocolo), html);
                    if (!enviado) erro = "Falha no envio SMTP (ver log)";
                } catch (Exception e) {
                    erro = e.getMessage();
                    logger.warn("ChamadoEmailJob: falha ao enviar notificacao " + p.id, e);
                }
                registrar(conn, p.id, EmailChamado.statusAposTentativa(enviado, p.tentativas), erro);
            }
        } catch (Exception e) {
            logger.error("ChamadoEmailJob: falha geral", e);
        }
    }

    private static List<Pendente> pendentes(Connection conn) throws Exception {
        List<Pendente> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP 50 n.id, n.tipo, n.destinatario, n.tentativas, c.protocolo, c.titulo, p.nome AS prioridade, " +
                "c.dt_prazo_atendimento, c.dt_prazo_resolucao, " +
                "(SELECT TOP 1 h.texto FROM muralha.chamado_historico h WHERE h.id_chamado = c.id " +
                " AND h.tipo = 'RECLASSIFICACAO' ORDER BY h.id DESC) AS justificativa " +
                "FROM muralha.chamado_notificacao n JOIN muralha.chamado c ON c.id = n.id_chamado " +
                "JOIN muralha.chamado_prioridade p ON p.id = c.id_prioridade " +
                "WHERE n.status = 'PENDENTE' ORDER BY n.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pendente p = new Pendente();
                p.id = rs.getLong("id");
                p.tipo = rs.getString("tipo");
                p.destinatario = rs.getString("destinatario");
                p.tentativas = rs.getInt("tentativas");
                p.protocolo = rs.getString("protocolo");
                p.titulo = rs.getString("titulo");
                p.prioridade = rs.getString("prioridade");
                p.prazoAtendimento = rs.getTimestamp("dt_prazo_atendimento");
                p.prazoResolucao = rs.getTimestamp("dt_prazo_resolucao");
                p.justificativa = rs.getString("justificativa");
                lista.add(p);
            }
        }
        return lista;
    }

    private static void registrar(Connection conn, long id, String status, String erro) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE muralha.chamado_notificacao SET status = ?, tentativas = tentativas + 1, " +
                "dt_envio = CASE WHEN ? = 'ENVIADO' THEN SYSDATETIME() ELSE dt_envio END, erro = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setString(3, erro == null ? null : (erro.length() > 500 ? erro.substring(0, 500) : erro));
            ps.setLong(4, id);
            ps.executeUpdate();
        }
    }
}
```

- [ ] **Step 2: Registrar no `Agendador`**

Junto das constantes (após `JOB_INCIDENTE_IMPORT`, linha ~125):

```java
	public static final String GRUPO_CHAMADOS = "Chamados";
	public static final String JOB_CHAMADO_SLA = "Chamados SLA e fechamento automatico";
	public static final String JOB_CHAMADO_EMAIL = "Chamados envio de e-mails";
```

Em `verificarJobsGTW()`, logo após `reajustaJobIncidenteImport();`:

```java
			reajustaJobChamadoSla();
			reajustaJobChamadoEmail();
```

Depois do método `reajustaJobIncidenteImport()`:

```java
	private static void reajustaJobChamadoSla() throws SchedulerException {
		if (!possuiJob(GRUPO_CHAMADOS, JOB_CHAMADO_SLA)) {
			JobDetail tarefa = JobBuilder.newJob(muralha.digital.chamado.ChamadoSlaJob.class)
					.withIdentity(JOB_CHAMADO_SLA, GRUPO_CHAMADOS)
					.build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(JOB_CHAMADO_SLA, GRUPO_CHAMADOS)
					.startNow()
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(5))
					.build();
			quartzScheduler.scheduleJob(tarefa, trigger);
			logger.info("Job Chamados SLA agendada a cada 5 minutos.");
		}
	}

	private static void reajustaJobChamadoEmail() throws SchedulerException {
		if (!possuiJob(GRUPO_CHAMADOS, JOB_CHAMADO_EMAIL)) {
			JobDetail tarefa = JobBuilder.newJob(muralha.digital.chamado.ChamadoEmailJob.class)
					.withIdentity(JOB_CHAMADO_EMAIL, GRUPO_CHAMADOS)
					.build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(JOB_CHAMADO_EMAIL, GRUPO_CHAMADOS)
					.startNow()
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(1))
					.build();
			quartzScheduler.scheduleJob(tarefa, trigger);
			logger.info("Job Chamados e-mail agendada a cada 1 minuto.");
		}
	}
```

- [ ] **Step 3: Build e verificação**

Run: build → `BUILD SUCCESS`. Suba o servidor e confira no log: `Job Chamados SLA agendada a cada 5 minutos.` e `Job Chamados e-mail agendada a cada 1 minuto.`

- [ ] **Step 4: Commit**

```bash
git add src/main/java/muralha/digital/chamado/ChamadoSlaJob.java src/main/java/muralha/digital/chamado/ChamadoEmailJob.java src/main/java/com/consilux/servlet/ferramentas/Agendador.java
git commit -m "Agenda jobs de SLA e de e-mail do sistema de chamados

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 10: Tela de lista e abertura de chamado (`index.jsp`)

**Files:**
- Create: `src/main/webapp/muralha-digital/assets/js/chamados/chamados-comum.js`, `chamados-lista.js`
- Create: `src/main/webapp/muralha-digital/pages/chamados/index.jsp`

**Interfaces:**
- Consumes: `GET Chamado?acao=perfil|opcoes|listar`, `POST Chamado acao=abrir`, `GET ChamadoRelatorio?acao=exportar&origem=lista`.
- Produces: objeto global `Chamados` com `esc(v)`, `badgeStatus(s)`, `badgeSla(s)`, `badgePrioridade(nome, cor)`, `dataHora(iso)`, `get(servlet, params)`, `post(servlet, formData)`, `baixar(url)`, `exportarPdf(params)`, `ROTULO_STATUS`.

- [ ] **Step 1: Criar `chamados-comum.js`**

```js
// src/main/webapp/muralha-digital/assets/js/chamados/chamados-comum.js
var Chamados = (function () {
  var ctx = window.CTX || '';

  var ROTULO_STATUS = {
    ABERTO: ['Aberto', 'secondary'], EM_ATENDIMENTO: ['Em atendimento', 'primary'],
    AGUARDANDO_SOLICITANTE: ['Aguardando solicitante', 'warning'], RESOLVIDO: ['Resolvido', 'info'],
    FECHADO: ['Fechado', 'success'], CANCELADO: ['Cancelado', 'dark']
  };
  var ROTULO_SLA = {
    NO_PRAZO: ['No prazo', 'success'], EM_RISCO: ['Em risco', 'warning'], VENCIDO: ['Vencido', 'danger'],
    CUMPRIDO: ['Cumprido', 'secondary'], VIOLADO: ['Violado', 'danger']
  };

  function esc(v) {
    return String(v == null ? '' : v).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }

  function badge(mapa, chave) {
    var r = mapa[chave] || [chave, 'secondary'];
    return '<span class="badge text-bg-' + r[1] + '">' + esc(r[0]) + '</span>';
  }

  function badgePrioridade(nome, cor) {
    var c = /^#[0-9a-fA-F]{6}$/.test(cor || '') ? cor : '#6c757d';
    return '<span class="badge" style="background-color:' + c + '">' + esc(nome) + '</span>';
  }

  function dataHora(iso) {
    if (!iso) return '—';
    var d = new Date(iso);
    return d.toLocaleDateString('pt-BR') + ' ' + d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  }

  async function tratar(r) {
    if (r.status === 401) { location.href = ctx + '/login/login.jsp'; return { ok: false }; }
    var d;
    try { d = await r.json(); } catch (e) { d = { ok: false, erro: 'Falha de comunicação (HTTP ' + r.status + ').' }; }
    if (!d.ok) Swal.fire('Atenção', d.erro || 'Falha na operação.', 'warning');
    return d;
  }

  async function get(servlet, params) {
    var r = await fetch(ctx + '/MuralhaDigital/' + servlet + '?' + new URLSearchParams(params || {}));
    return tratar(r);
  }

  async function post(servlet, formData) {
    var r = await fetch(ctx + '/MuralhaDigital/' + servlet, { method: 'POST', body: formData });
    return tratar(r);
  }

  function baixar(servlet, params) {
    window.location.href = ctx + '/MuralhaDigital/' + servlet + '?' + new URLSearchParams(params);
  }

  /** PDF no navegador com os dados completos do servidor e o SHA-256 no rodape (spec D13). */
  async function exportarPdf(params) {
    var p = Object.assign({}, params, { acao: 'exportar', formato: 'json' });
    var d = await get('ChamadoRelatorio', p);
    if (!d.ok) return;
    var doc = new window.jspdf.jsPDF('l', 'pt');
    doc.setFontSize(12);
    doc.text(d.titulo, 40, 30);
    doc.setFontSize(8);
    doc.text('Filtros: ' + d.filtros + '  |  Usuário: ' + d.usuario + '  |  Gerado em: ' + new Date().toLocaleString('pt-BR'), 40, 44);
    doc.autoTable({ head: [d.rotulos], body: d.linhas, startY: 54, styles: { fontSize: 7 } });
    var y = doc.lastAutoTable.finalY + 14;
    doc.text('Total: ' + d.linhas.length + '  |  SHA-256 dos dados: ' + d.sha256, 40, y);
    doc.save(d.arquivo);
  }

  return {
    esc: esc, ROTULO_STATUS: ROTULO_STATUS,
    badgeStatus: function (s) { return badge(ROTULO_STATUS, s); },
    badgeSla: function (s) { return badge(ROTULO_SLA, s); },
    badgePrioridade: badgePrioridade, dataHora: dataHora,
    get: get, post: post, baixar: baixar, exportarPdf: exportarPdf
  };
})();
```

- [ ] **Step 2: Criar `index.jsp`**

```jsp
<%-- src/main/webapp/muralha-digital/pages/chamados/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2 flex-wrap">
    <h4 class="mb-0"><i class="bi bi-headset me-2"></i>Chamados</h4>
    <div class="ms-auto d-flex gap-2">
      <a id="lnkRelatorios" class="btn btn-sm btn-outline-secondary d-none" href="relatorios.jsp"><i class="bi bi-bar-chart me-1"></i>Relatórios</a>
      <a id="lnkConfig" class="btn btn-sm btn-outline-secondary d-none" href="configuracao.jsp"><i class="bi bi-gear me-1"></i>Configuração</a>
      <button id="btnNovo" class="btn btn-sm btn-primary d-none"><i class="bi bi-plus-circle me-1"></i>Novo chamado</button>
    </div>
  </div>

  <div class="card mb-3">
    <div class="card-body">
      <form id="formFiltros" class="row g-2 align-items-end">
        <div class="col-md-2"><label class="form-label small" for="fProtocolo">Protocolo</label>
          <input id="fProtocolo" name="protocolo" class="form-control form-control-sm" placeholder="CH-2026-..."></div>
        <div class="col-md-2"><label class="form-label small" for="fStatus">Status</label>
          <select id="fStatus" name="status" class="form-select form-select-sm"><option value="">Todos</option></select></div>
        <div class="col-md-1"><label class="form-label small" for="fPrioridade">Prioridade</label>
          <select id="fPrioridade" name="idPrioridade" class="form-select form-select-sm"><option value="">Todas</option></select></div>
        <div class="col-md-2"><label class="form-label small" for="fCategoria">Categoria</label>
          <select id="fCategoria" name="idCategoria" class="form-select form-select-sm"><option value="">Todas</option></select></div>
        <div class="col-md-2"><label class="form-label small" for="fEquipamento">Equipamento</label>
          <select id="fEquipamento" name="idLocal" class="form-select form-select-sm"><option value="">Todos</option></select></div>
        <div class="col-md-1"><label class="form-label small" for="fInicio">De</label>
          <input id="fInicio" name="dtInicio" type="date" class="form-control form-control-sm"></div>
        <div class="col-md-1"><label class="form-label small" for="fFim">Até</label>
          <input id="fFim" name="dtFim" type="date" class="form-control form-control-sm"></div>
        <div class="col-md-auto">
          <button type="submit" class="btn btn-sm btn-primary"><i class="bi bi-search"></i></button>
          <button type="reset" class="btn btn-sm btn-outline-secondary"><i class="bi bi-x-lg"></i></button>
        </div>
      </form>
    </div>
  </div>

  <div class="card">
    <div class="card-header d-flex align-items-center gap-2">
      <span class="small text-muted" id="lblTotal"></span>
      <span class="small text-muted ms-2" id="lblAtualizado"></span>
      <div class="ms-auto btn-group btn-group-sm">
        <button class="btn btn-outline-success" id="btnCsv"><i class="bi bi-filetype-csv me-1"></i>CSV</button>
        <button class="btn btn-outline-success" id="btnXls"><i class="bi bi-file-earmark-excel me-1"></i>XLS</button>
        <button class="btn btn-outline-danger" id="btnPdf"><i class="bi bi-file-earmark-pdf me-1"></i>PDF</button>
      </div>
    </div>
    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-sm table-hover align-middle mb-0">
          <thead class="table-light"><tr>
            <th>Protocolo</th><th>Título</th><th>Categoria</th><th>Equipamento</th><th>Prioridade</th>
            <th>Status</th><th>Responsável</th><th>Abertura</th><th>SLA atend.</th><th>SLA resol.</th>
          </tr></thead>
          <tbody id="tblChamados"></tbody>
        </table>
      </div>
    </div>
    <div class="card-footer"><ul id="paginacao" class="pagination pagination-sm mb-0"></ul></div>
  </div>
</div>

<div class="modal fade" id="modalNovo" tabindex="-1" aria-labelledby="modalNovoTitulo">
  <div class="modal-dialog modal-lg">
    <form class="modal-content" id="formNovo">
      <div class="modal-header">
        <h5 class="modal-title" id="modalNovoTitulo"><i class="bi bi-plus-circle me-2"></i>Novo chamado</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body row g-3">
        <div class="col-md-6"><label class="form-label" for="nCategoria">Categoria *</label>
          <select id="nCategoria" class="form-select" required><option value="">Selecione</option></select></div>
        <div class="col-md-6"><label class="form-label" for="nSubcategoria">Subcategoria *</label>
          <select id="nSubcategoria" name="idCategoria" class="form-select" required><option value="">Selecione a categoria</option></select></div>
        <div class="col-md-6 d-none" id="divEquipamento"><label class="form-label" for="nEquipamento">Equipamento *</label>
          <select id="nEquipamento" name="idLocal" class="form-select"><option value="">Selecione</option></select></div>
        <div class="col-md-6"><label class="form-label" for="nPrioridade">Prioridade *</label>
          <select id="nPrioridade" name="idPrioridade" class="form-select" required></select>
          <div class="form-text" id="nPrazos"></div></div>
        <div class="col-12"><label class="form-label" for="nTitulo">Título *</label>
          <input id="nTitulo" name="titulo" class="form-control" maxlength="150" required></div>
        <div class="col-12"><label class="form-label" for="nDescricao">Descrição *</label>
          <textarea id="nDescricao" name="descricao" class="form-control" rows="5" maxlength="8000" required></textarea></div>
        <div class="col-12"><label class="form-label" for="nArquivos">Anexos (PDF, PNG ou JPG — até 5)</label>
          <input id="nArquivos" name="arquivos" type="file" class="form-control" multiple accept=".pdf,.png,.jpg,.jpeg"></div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="submit" class="btn btn-primary" id="btnSalvarNovo"><i class="bi bi-send me-1"></i>Abrir chamado</button>
      </div>
    </form>
  </div>
</div>

<script>window.CTX = '<%= request.getContextPath() %>';</script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/jspdf.umd.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/jspdf.plugin.autotable.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-comum.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-lista.js"></script>
```

- [ ] **Step 3: Criar `chamados-lista.js`**

```js
// src/main/webapp/muralha-digital/assets/js/chamados/chamados-lista.js
(function () {
  var esc = Chamados.esc;
  var opcoes = null, pagina = 1, ultimaVersao = {}, timer = null;

  function filtros() {
    var p = {};
    new FormData(document.getElementById('formFiltros')).forEach(function (v, k) { if (v) p[k] = v; });
    return p;
  }

  function preencher(select, itens, valor, rotulo) {
    itens.forEach(function (i) {
      var o = document.createElement('option');
      o.value = valor(i); o.textContent = rotulo(i);
      select.appendChild(o);
    });
  }

  async function iniciar() {
    var perfil = await Chamados.get('Chamado', { acao: 'perfil' });
    if (!perfil.ok) return;
    var p = perfil.perfis;
    if (p.indexOf('SOLICITANTE') >= 0 || p.indexOf('GESTOR') >= 0) document.getElementById('btnNovo').classList.remove('d-none');
    if (p.indexOf('GESTOR') >= 0 || p.indexOf('EQUIPE') >= 0) document.getElementById('lnkRelatorios').classList.remove('d-none');
    if (p.indexOf('EQUIPE') >= 0) document.getElementById('lnkConfig').classList.remove('d-none');

    opcoes = await Chamados.get('Chamado', { acao: 'opcoes' });
    if (!opcoes.ok) return;
    preencher(document.getElementById('fStatus'), Object.keys(Chamados.ROTULO_STATUS), function (s) { return s; },
      function (s) { return Chamados.ROTULO_STATUS[s][0]; });
    preencher(document.getElementById('fPrioridade'), opcoes.prioridades, function (x) { return x.id; }, function (x) { return x.nome; });
    preencher(document.getElementById('fCategoria'), opcoes.categorias.filter(function (c) { return !c.idPai; }),
      function (x) { return x.id; }, function (x) { return x.nome; });
    preencher(document.getElementById('fEquipamento'), opcoes.equipamentos, function (x) { return x.idLocal; }, function (x) { return x.nome; });
    preencherNovo();
    await carregar();
    timer = setInterval(carregar, 30000);
  }

  async function carregar() {
    var d = await Chamados.get('Chamado', Object.assign(filtros(), { acao: 'listar', pagina: pagina }));
    if (!d.ok) return;
    var tbody = document.getElementById('tblChamados');
    var html = '';
    d.itens.forEach(function (c) {
      var mudou = ultimaVersao[c.protocolo] && ultimaVersao[c.protocolo] !== c.dtAtualizacao;
      ultimaVersao[c.protocolo] = c.dtAtualizacao;
      var url = 'detalhe.jsp?protocolo=' + encodeURIComponent(c.protocolo);
      html += '<tr class="' + (mudou ? 'table-info' : '') + '" style="cursor:pointer" data-url="' + esc(url) + '">' +
        '<td><a href="' + esc(url) + '"><strong>' + esc(c.protocolo) + '</strong></a></td>' +
        '<td>' + esc(c.titulo) + '</td><td class="small">' + esc(c.categoria) + '</td>' +
        '<td class="small">' + esc(c.equipamento || '—') + '</td>' +
        '<td>' + Chamados.badgePrioridade(c.prioridade, c.corPrioridade) + '</td>' +
        '<td>' + Chamados.badgeStatus(c.status) + '</td><td class="small">' + esc(c.responsavel || '—') + '</td>' +
        '<td class="small">' + Chamados.dataHora(c.abertura) + '</td>' +
        '<td>' + Chamados.badgeSla(c.slaAtendimento) + '</td><td>' + Chamados.badgeSla(c.slaResolucao) + '</td></tr>';
    });
    tbody.innerHTML = html || '<tr><td colspan="10" class="text-center text-muted py-4">Nenhum chamado encontrado.</td></tr>';
    tbody.querySelectorAll('tr[data-url]').forEach(function (tr) {
      tr.addEventListener('click', function (e) { if (e.target.tagName !== 'A') location.href = tr.dataset.url; });
    });
    setTimeout(function () { tbody.querySelectorAll('tr.table-info').forEach(function (tr) { tr.classList.remove('table-info'); }); }, 3000);
    document.getElementById('lblTotal').textContent = d.total + ' chamado(s)';
    document.getElementById('lblAtualizado').textContent = 'Atualizado às ' + new Date().toLocaleTimeString('pt-BR');
    paginar(d.total);
  }

  function paginar(total) {
    var paginas = Math.max(1, Math.ceil(total / 50));
    var ul = document.getElementById('paginacao');
    ul.innerHTML = '';
    for (var i = 1; i <= paginas && i <= 20; i++) {
      var li = document.createElement('li');
      li.className = 'page-item' + (i === pagina ? ' active' : '');
      var a = document.createElement('a');
      a.className = 'page-link'; a.href = '#'; a.textContent = i; a.dataset.p = i;
      a.addEventListener('click', function (e) { e.preventDefault(); pagina = Number(this.dataset.p); carregar(); });
      li.appendChild(a); ul.appendChild(li);
    }
  }

  function preencherNovo() {
    var selCat = document.getElementById('nCategoria');
    var selSub = document.getElementById('nSubcategoria');
    var selPri = document.getElementById('nPrioridade');
    preencher(selCat, opcoes.categorias.filter(function (c) { return !c.idPai; }), function (x) { return x.id; }, function (x) { return x.nome; });
    preencher(document.getElementById('nEquipamento'), opcoes.equipamentos, function (x) { return x.idLocal; }, function (x) { return x.nome; });
    preencher(selPri, opcoes.prioridades, function (x) { return x.id; }, function (x) { return x.nome; });
    selPri.value = (opcoes.prioridades.find(function (p) { return p.codigo === 'MEDIA'; }) || opcoes.prioridades[0] || {}).id;

    function mostrarPrazos() {
      var p = opcoes.prioridades.find(function (x) { return String(x.id) === selPri.value; });
      document.getElementById('nPrazos').textContent = p
        ? 'Prazo de atendimento: ' + horas(p.minutosAtendimento) + ' · resolução: ' + horas(p.minutosResolucao) + ' (24x7)'
        : '';
    }
    selPri.addEventListener('change', mostrarPrazos);
    mostrarPrazos();

    selCat.addEventListener('change', function () {
      selSub.innerHTML = '<option value="">Selecione</option>';
      var cat = opcoes.categorias.find(function (c) { return String(c.id) === selCat.value; });
      var filhas = opcoes.categorias.filter(function (c) { return String(c.idPai) === selCat.value; });
      if (cat && filhas.length === 0) filhas = [cat];
      preencher(selSub, filhas, function (x) { return x.id; }, function (x) { return x.nome; });
      var exige = cat && cat.exigeEquipamento;
      document.getElementById('divEquipamento').classList.toggle('d-none', !exige);
      document.getElementById('nEquipamento').required = !!exige;
    });
  }

  function horas(min) { return min % 60 === 0 ? (min / 60) + ' h' : min + ' min'; }

  async function abrir(e) {
    e.preventDefault();
    var form = document.getElementById('formNovo');
    var arquivos = document.getElementById('nArquivos').files;
    if (arquivos.length > 5) { Swal.fire('Atenção', 'Envie no máximo 5 arquivos.', 'warning'); return; }
    var fd = new FormData(form);
    fd.append('acao', 'abrir');
    var btn = document.getElementById('btnSalvarNovo');
    btn.disabled = true;
    try {
      var d = await Chamados.post('Chamado', fd);
      if (!d.ok) return;
      bootstrap.Modal.getInstance(document.getElementById('modalNovo')).hide();
      form.reset();
      await Swal.fire({
        icon: 'success', title: 'Chamado registrado',
        html: '<p class="mb-1">Protocolo: <strong>' + esc(d.protocolo) + '</strong></p>' +
              '<p class="mb-1">Prioridade: ' + esc(d.prioridade) + '</p>' +
              '<p class="mb-1">Prazo previsto para atendimento: ' + Chamados.dataHora(d.prazoAtendimento) + '</p>' +
              '<p class="mb-0">Prazo previsto para resolução: ' + Chamados.dataHora(d.prazoResolucao) + '</p>' +
              '<p class="small text-muted mt-2">Uma confirmação foi enviada para o seu e-mail.</p>'
      });
      carregar();
    } finally {
      btn.disabled = false;
    }
  }

  document.getElementById('formFiltros').addEventListener('submit', function (e) { e.preventDefault(); pagina = 1; carregar(); });
  document.getElementById('formFiltros').addEventListener('reset', function () { setTimeout(function () { pagina = 1; carregar(); }, 0); });
  document.getElementById('btnNovo').addEventListener('click', function () { new bootstrap.Modal(document.getElementById('modalNovo')).show(); });
  document.getElementById('formNovo').addEventListener('submit', abrir);
  document.getElementById('btnCsv').addEventListener('click', function () {
    Chamados.baixar('ChamadoRelatorio', Object.assign(filtros(), { acao: 'exportar', formato: 'csv', origem: 'lista' }));
  });
  document.getElementById('btnXls').addEventListener('click', function () {
    Chamados.baixar('ChamadoRelatorio', Object.assign(filtros(), { acao: 'exportar', formato: 'xlsx', origem: 'lista' }));
  });
  document.getElementById('btnPdf').addEventListener('click', function () {
    Chamados.exportarPdf(Object.assign(filtros(), { origem: 'lista' }));
  });
  iniciar();
})();
```

- [ ] **Step 3b: Nota sobre `desde`**

A spec §3.2 previa `acao=listar&desde=`. Este plano simplifica: o polling recarrega a página atual e destaca as linhas cujo `dtAtualizacao` mudou (mesmo efeito para o usuário, sem parâmetro extra). Registrar a simplificação no documento de entrega.

- [ ] **Step 4: Verificar no navegador**

Build + executar; com usuário solicitante, abrir `http://localhost:8080/muralha-digital/pages/chamados/index.jsp`:
1. Botão "Novo chamado" visível; "Relatórios"/"Configuração" ocultos.
2. Abrir chamado em "Equipamento → Falha de energia" sem equipamento → o navegador exige o campo.
3. Abrir com equipamento, prioridade Crítica e um PDF → SweetAlert com protocolo `CH-2026-000001`, prioridade e prazos (+1 h / +4 h).
4. Título `<b>teste</b>` aparece literalmente na lista (sem negrito).
5. CSV/XLS/PDF baixam; o rodapé do CSV e o PDF mostram o mesmo SHA-256.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/muralha-digital/pages/chamados/index.jsp src/main/webapp/muralha-digital/assets/js/chamados/chamados-comum.js src/main/webapp/muralha-digital/assets/js/chamados/chamados-lista.js
git commit -m "Adiciona tela de lista e abertura de chamados

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 11: Tela de detalhe e ações (`detalhe.jsp`)

**Files:**
- Create: `src/main/webapp/muralha-digital/assets/js/chamados/chamados-detalhe.js`
- Create: `src/main/webapp/muralha-digital/pages/chamados/detalhe.jsp`

**Interfaces:**
- Consumes: `GET Chamado?acao=detalhe&protocolo=`, `GET Chamado?acao=opcoes`, `POST Chamado` (ações), `GET Chamado?acao=anexo&id=`, objeto `Chamados`.

- [ ] **Step 1: Criar `detalhe.jsp`**

```jsp
<%-- src/main/webapp/muralha-digital/pages/chamados/detalhe.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <nav aria-label="breadcrumb"><ol class="breadcrumb small">
    <li class="breadcrumb-item"><a href="index.jsp">Chamados</a></li>
    <li class="breadcrumb-item active" id="bcProtocolo" aria-current="page"></li>
  </ol></nav>

  <div class="d-flex align-items-center gap-2 flex-wrap mb-3">
    <h4 class="mb-0" id="tTitulo"></h4>
    <span id="tStatus"></span>
    <div class="ms-auto d-flex gap-2 flex-wrap" id="acoes"></div>
  </div>

  <div class="row g-3">
    <div class="col-lg-4">
      <div class="card mb-3"><div class="card-body small">
        <dl class="row mb-0">
          <dt class="col-5">Protocolo</dt><dd class="col-7" id="dProtocolo"></dd>
          <dt class="col-5">Prioridade</dt><dd class="col-7" id="dPrioridade"></dd>
          <dt class="col-5">Categoria</dt><dd class="col-7" id="dCategoria"></dd>
          <dt class="col-5">Equipamento</dt><dd class="col-7" id="dEquipamento"></dd>
          <dt class="col-5">Solicitante</dt><dd class="col-7" id="dSolicitante"></dd>
          <dt class="col-5">Responsável</dt><dd class="col-7" id="dResponsavel"></dd>
          <dt class="col-5">Abertura</dt><dd class="col-7" id="dAbertura"></dd>
        </dl>
      </div></div>
      <div class="card mb-3"><div class="card-header">Prazos (24x7)</div><div class="card-body small">
        <p class="mb-1">Atendimento: <span id="pAtendimento"></span> <span id="sAtendimento"></span></p>
        <p class="mb-1 text-muted" id="cAtendimento"></p>
        <p class="mb-1">Resolução: <span id="pResolucao"></span> <span id="sResolucao"></span></p>
        <p class="mb-0 text-muted" id="cResolucao"></p>
      </div></div>
      <div class="card mb-3 d-none" id="cardAvaliacao"><div class="card-header">Avaliação</div>
        <div class="card-body small" id="dAvaliacao"></div></div>
      <div class="card"><div class="card-header">Anexos</div>
        <ul class="list-group list-group-flush small" id="lstAnexos"></ul></div>
    </div>
    <div class="col-lg-8">
      <div class="card mb-3"><div class="card-header">Descrição</div>
        <div class="card-body" id="dDescricao" style="white-space:pre-wrap"></div></div>
      <div class="card"><div class="card-header">Histórico</div>
        <ul class="list-group list-group-flush" id="lstHistorico"></ul></div>
    </div>
  </div>
</div>

<script>window.CTX = '<%= request.getContextPath() %>';</script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-comum.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-detalhe.js"></script>
```

- [ ] **Step 2: Criar `chamados-detalhe.js`**

```js
// src/main/webapp/muralha-digital/assets/js/chamados/chamados-detalhe.js
(function () {
  var esc = Chamados.esc;
  var protocolo = new URLSearchParams(location.search).get('protocolo') || '';
  var dados = null, defasagem = 0, prioridades = [];

  var ACOES = {
    assumir:       { rotulo: 'Assumir', icone: 'bi-person-check', cor: 'outline-primary' },
    iniciar:       { rotulo: 'Iniciar atendimento', icone: 'bi-play-circle', cor: 'primary' },
    aguardar:      { rotulo: 'Aguardar solicitante', icone: 'bi-hourglass-split', cor: 'outline-warning' },
    responder:     { rotulo: 'Responder', icone: 'bi-reply', cor: 'primary' },
    resolver:      { rotulo: 'Resolver', icone: 'bi-check2-circle', cor: 'success' },
    avaliar:       { rotulo: 'Avaliar e fechar', icone: 'bi-star', cor: 'success' },
    reabrir:       { rotulo: 'Reabrir', icone: 'bi-arrow-counterclockwise', cor: 'outline-danger' },
    cancelar:      { rotulo: 'Cancelar', icone: 'bi-x-circle', cor: 'outline-danger' },
    reclassificar: { rotulo: 'Reclassificar prioridade', icone: 'bi-sort-down', cor: 'outline-secondary' }
  };

  function texto(id, v) { document.getElementById(id).textContent = v == null || v === '' ? '—' : v; }

  async function carregar() {
    var d = await Chamados.get('Chamado', { acao: 'detalhe', protocolo: protocolo });
    if (!d.ok) { if (!dados) location.href = 'index.jsp'; return; }
    dados = d;
    defasagem = new Date(d.servidorAgora) - new Date();
    var c = d.chamado;
    texto('bcProtocolo', c.protocolo);
    texto('tTitulo', c.titulo);
    document.getElementById('tStatus').innerHTML = Chamados.badgeStatus(c.status);
    texto('dProtocolo', c.protocolo);
    document.getElementById('dPrioridade').innerHTML = Chamados.badgePrioridade(c.prioridade, c.corPrioridade) +
      (c.prioridadeSolicitada !== c.prioridade ? ' <span class="text-muted">(solicitada: ' + esc(c.prioridadeSolicitada) + ')</span>' : '');
    texto('dCategoria', c.categoria);
    texto('dEquipamento', c.equipamento);
    texto('dSolicitante', c.solicitante);
    texto('dResponsavel', c.responsavel);
    texto('dAbertura', Chamados.dataHora(c.abertura));
    texto('dDescricao', c.descricao);
    texto('pAtendimento', Chamados.dataHora(c.prazoAtendimento));
    texto('pResolucao', Chamados.dataHora(c.prazoResolucao));
    document.getElementById('sAtendimento').innerHTML = Chamados.badgeSla(c.slaAtendimento);
    document.getElementById('sResolucao').innerHTML = Chamados.badgeSla(c.slaResolucao) +
      (c.pausado && c.status === 'AGUARDANDO_SOLICITANTE' ? ' <span class="badge text-bg-light">relógio pausado</span>' : '');
    if (c.avaliacaoNota || c.fechadoAutomaticamente) {
      document.getElementById('cardAvaliacao').classList.remove('d-none');
      document.getElementById('dAvaliacao').innerHTML = c.avaliacaoNota
        ? '<span class="text-warning">' + '★'.repeat(c.avaliacaoNota) + '☆'.repeat(5 - c.avaliacaoNota) + '</span><p class="mb-0 mt-1">' + esc(c.avaliacaoComentario || '') + '</p>'
        : '<span class="text-muted">Fechado automaticamente — não avaliado.</span>';
    }
    historico(d.historico, d.anexos);
    anexos(d.anexos);
    acoes(d.acoesPermitidas);
  }

  function historico(itens, anx) {
    var ul = document.getElementById('lstHistorico');
    ul.innerHTML = itens.map(function (h) {
      var arquivos = anx.filter(function (a) { return a.idHistorico === h.id; })
        .map(function (a) { return '<a class="me-2" href="' + esc(linkAnexo(a.id)) + '"><i class="bi bi-paperclip"></i>' + esc(a.nome) + '</a>'; }).join('');
      var mudanca = h.statusNovo && h.statusAnterior !== h.statusNovo
        ? Chamados.badgeStatus(h.statusAnterior) + ' → ' + Chamados.badgeStatus(h.statusNovo) : '';
      return '<li class="list-group-item small">' +
        '<div class="d-flex gap-2"><strong>' + esc(h.usuario) + '</strong><span class="text-muted">' + Chamados.dataHora(h.dtEvento) +
        '</span><span class="badge text-bg-light">' + esc(h.tipo) + '</span><span class="ms-auto">' + mudanca + '</span></div>' +
        '<div style="white-space:pre-wrap">' + esc(h.texto || '') + '</div>' + (arquivos ? '<div class="mt-1">' + arquivos + '</div>' : '') +
        '</li>';
    }).join('');
  }

  function linkAnexo(id) { return (window.CTX || '') + '/MuralhaDigital/Chamado?acao=anexo&id=' + encodeURIComponent(id); }

  function anexos(itens) {
    document.getElementById('lstAnexos').innerHTML = itens.length
      ? itens.map(function (a) {
          return '<li class="list-group-item"><a href="' + esc(linkAnexo(a.id)) + '"><i class="bi bi-paperclip me-1"></i>' + esc(a.nome) +
            '</a><div class="text-muted" title="SHA-256">' + esc(a.sha256.substring(0, 16)) + '…</div></li>';
        }).join('')
      : '<li class="list-group-item text-muted">Nenhum anexo.</li>';
  }

  function acoes(lista) {
    var div = document.getElementById('acoes');
    div.innerHTML = '';
    lista.forEach(function (codigo) {
      var a = ACOES[codigo];
      if (!a) return;
      var b = document.createElement('button');
      b.className = 'btn btn-sm btn-' + a.cor;
      b.innerHTML = '<i class="bi ' + a.icone + ' me-1"></i>' + esc(a.rotulo);
      b.addEventListener('click', function () { executar(codigo); });
      div.appendChild(b);
    });
  }

  async function executar(codigo) {
    var fd = new FormData();
    fd.append('acao', codigo);
    fd.append('protocolo', protocolo);
    var r;
    if (codigo === 'avaliar') {
      r = await Swal.fire({
        title: 'Avaliar atendimento',
        html: '<div class="mb-2" id="estrelas">' + [1, 2, 3, 4, 5].map(function (n) {
                return '<input type="radio" class="btn-check" name="nota" id="n' + n + '" value="' + n + '"><label class="btn btn-outline-warning me-1" for="n' + n + '">' + n + ' ★</label>';
              }).join('') + '</div><textarea id="swTexto" class="form-control" rows="3" placeholder="Comentário (opcional)" maxlength="1000"></textarea>',
        showCancelButton: true, confirmButtonText: 'Enviar avaliação',
        preConfirm: function () {
          var n = document.querySelector('input[name=nota]:checked');
          if (!n) { Swal.showValidationMessage('Escolha uma nota de 1 a 5.'); return false; }
          return { nota: n.value, texto: document.getElementById('swTexto').value };
        }
      });
      if (!r.isConfirmed) return;
      fd.append('nota', r.value.nota);
      fd.append('texto', r.value.texto);
    } else if (codigo === 'reclassificar') {
      if (!prioridades.length) prioridades = (await Chamados.get('Chamado', { acao: 'opcoes' })).prioridades || [];
      r = await Swal.fire({
        title: 'Reclassificar prioridade',
        html: '<select id="swPri" class="form-select mb-2">' + prioridades.map(function (p) {
                return '<option value="' + p.id + '">' + esc(p.nome) + '</option>';
              }).join('') + '</select><textarea id="swTexto" class="form-control" rows="3" placeholder="Justificativa (obrigatória)" maxlength="2000"></textarea>',
        showCancelButton: true, confirmButtonText: 'Reclassificar',
        didOpen: function () { document.getElementById('swPri').value = dados.chamado.idPrioridade; },
        preConfirm: function () {
          var t = document.getElementById('swTexto').value.trim();
          if (!t) { Swal.showValidationMessage('Informe a justificativa.'); return false; }
          return { idPrioridade: document.getElementById('swPri').value, texto: t };
        }
      });
      if (!r.isConfirmed) return;
      fd.append('idPrioridade', r.value.idPrioridade);
      fd.append('texto', r.value.texto);
    } else {
      var obrigatorio = codigo !== 'assumir';
      r = await Swal.fire({
        title: ACOES[codigo].rotulo,
        html: '<textarea id="swTexto" class="form-control" rows="4" maxlength="2000" placeholder="' +
              (obrigatorio ? 'Observação (obrigatória)' : 'Observação (opcional)') + '"></textarea>' +
              (codigo === 'responder' ? '<input id="swArquivos" type="file" class="form-control mt-2" multiple accept=".pdf,.png,.jpg,.jpeg">' : ''),
        showCancelButton: true, confirmButtonText: 'Confirmar',
        preConfirm: function () {
          var t = document.getElementById('swTexto').value.trim();
          if (obrigatorio && !t) { Swal.showValidationMessage('Informe a observação.'); return false; }
          var arqs = document.getElementById('swArquivos');
          if (arqs && arqs.files.length > 5) { Swal.showValidationMessage('No máximo 5 arquivos.'); return false; }
          return { texto: t, arquivos: arqs ? Array.from(arqs.files) : [] };
        }
      });
      if (!r.isConfirmed) return;
      fd.append('texto', r.value.texto);
      r.value.arquivos.forEach(function (f) { fd.append('arquivos', f); });
    }
    var d = await Chamados.post('Chamado', fd);
    if (d.ok) {
      Swal.fire({ icon: 'success', title: 'Chamado atualizado', timer: 1500, showConfirmButton: false });
      carregar();
    }
  }

  function contador(prazoIso, idDestino, cumprido) {
    var el = document.getElementById(idDestino);
    if (!prazoIso || cumprido) { el.textContent = ''; return; }
    var ms = new Date(prazoIso) - (new Date().getTime() + defasagem);
    var neg = ms < 0; ms = Math.abs(ms);
    var h = Math.floor(ms / 3600000), m = Math.floor(ms % 3600000 / 60000);
    el.textContent = (neg ? 'Vencido há ' : 'Restam ') + h + 'h ' + m + 'min';
  }

  setInterval(function () {
    if (!dados) return;
    var c = dados.chamado;
    var terminal = c.status === 'FECHADO' || c.status === 'CANCELADO';
    contador(c.prazoAtendimento, 'cAtendimento', terminal || !!c.primeiroAtendimento);
    contador(c.prazoResolucao, 'cResolucao', terminal || !!c.resolucao || c.pausado);
  }, 1000);

  carregar();
  setInterval(carregar, 30000);
})();
```

- [ ] **Step 2b: Verificar no navegador (três perfis)**

1. **Equipe:** abrir o chamado da Tarefa 10 → botões "Assumir", "Iniciar atendimento", "Cancelar", "Reclassificar". Iniciar com observação → SLA de atendimento "Cumprido".
2. **Equipe:** "Aguardar solicitante" → badge "relógio pausado"; contador de resolução para.
3. **Solicitante (autor):** em outra janela, a tela atualiza em até 30 s; "Responder" com texto + PNG → volta para "Em atendimento"; o anexo aparece no evento do histórico.
4. **Equipe:** "Reclassificar" para Alta com justificativa → prazos recalculados; histórico mostra "Prioridade alterada de Crítica para Alta"; `SELECT * FROM muralha.chamado_notificacao` mostra `RECLASSIFICACAO`.
5. **Equipe:** "Resolver". **Solicitante:** "Avaliar e fechar" com 4 estrelas → status Fechado, card de avaliação.
6. **Outro solicitante:** abrir `detalhe.jsp?protocolo=CH-2026-000001` → volta para a lista (404); abrir `/MuralhaDigital/Chamado?acao=anexo&id=1` → JSON `Chamado não encontrado.`
7. **Concorrência:** duas janelas da equipe no mesmo chamado; resolver em uma e, sem recarregar, "Aguardar" na outra → "Chamado alterado por outro usuário; recarregue a página."

- [ ] **Step 3: Commit**

```bash
git add src/main/webapp/muralha-digital/pages/chamados/detalhe.jsp src/main/webapp/muralha-digital/assets/js/chamados/chamados-detalhe.js
git commit -m "Adiciona tela de detalhe e acoes do chamado

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 12: Relatórios e configuração (`relatorios.jsp`, `configuracao.jsp`)

**Files:**
- Create: `src/main/webapp/muralha-digital/pages/chamados/relatorios.jsp`, `configuracao.jsp`
- Create: `src/main/webapp/muralha-digital/assets/js/chamados/chamados-relatorios.js`, `chamados-configuracao.js`

**Interfaces:**
- Consumes: `GET ChamadoRelatorio?acao=indicadores|exportar&origem=relatorio`, `GET Chamado?acao=opcoes`, `GET/POST ChamadoConfig`, objeto `Chamados`.

- [ ] **Step 1: Criar `relatorios.jsp`**

```jsp
<%-- src/main/webapp/muralha-digital/pages/chamados/relatorios.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <nav aria-label="breadcrumb"><ol class="breadcrumb small">
    <li class="breadcrumb-item"><a href="index.jsp">Chamados</a></li>
    <li class="breadcrumb-item active" aria-current="page">Relatórios</li>
  </ol></nav>
  <h4 class="mb-3"><i class="bi bi-bar-chart me-2"></i>Relatórios de Atendimento</h4>

  <div class="card mb-3"><div class="card-body">
    <form id="formRel" class="row g-2 align-items-end">
      <div class="col-md-2"><label class="form-label small" for="rInicio">De</label>
        <input id="rInicio" name="dtInicio" type="date" class="form-control form-control-sm"></div>
      <div class="col-md-2"><label class="form-label small" for="rFim">Até</label>
        <input id="rFim" name="dtFim" type="date" class="form-control form-control-sm"></div>
      <div class="col-md-2"><label class="form-label small" for="rCategoria">Categoria</label>
        <select id="rCategoria" name="idCategoria" class="form-select form-select-sm"><option value="">Todas</option></select></div>
      <div class="col-md-2"><label class="form-label small" for="rEquipamento">Equipamento</label>
        <select id="rEquipamento" name="idLocal" class="form-select form-select-sm"><option value="">Todos</option></select></div>
      <div class="col-md-1"><label class="form-label small" for="rPrioridade">Prioridade</label>
        <select id="rPrioridade" name="idPrioridade" class="form-select form-select-sm"><option value="">Todas</option></select></div>
      <div class="col-md-2"><label class="form-label small" for="rAgrup">Agrupar por</label>
        <select id="rAgrup" name="agrupamento" class="form-select form-select-sm">
          <option value="mes">Mês</option><option value="semana">Semana</option><option value="dia">Dia</option>
          <option value="categoria">Categoria</option><option value="equipamento">Equipamento</option>
          <option value="prioridade">Prioridade</option></select></div>
      <div class="col-md-auto"><button class="btn btn-sm btn-primary" type="submit"><i class="bi bi-search me-1"></i>Gerar</button></div>
    </form>
  </div></div>

  <div class="row g-3 mb-3" id="cards"></div>

  <div class="card mb-3"><div class="card-body"><canvas id="grafico" height="90"></canvas></div></div>

  <div class="card">
    <div class="card-header d-flex">
      <span>Indicadores</span>
      <div class="ms-auto btn-group btn-group-sm">
        <button class="btn btn-outline-success" id="btnCsv"><i class="bi bi-filetype-csv me-1"></i>CSV</button>
        <button class="btn btn-outline-success" id="btnXls"><i class="bi bi-file-earmark-excel me-1"></i>XLS</button>
        <button class="btn btn-outline-danger" id="btnPdf"><i class="bi bi-file-earmark-pdf me-1"></i>PDF</button>
      </div>
    </div>
    <div class="card-body p-0"><div class="table-responsive">
      <table class="table table-sm table-striped mb-0">
        <thead class="table-light"><tr><th>Grupo</th><th>Total</th><th>Resolvidos</th><th>Cancelados</th>
          <th>% SLA atend.</th><th>% SLA resol.</th><th>TMA (min)</th><th>TMR (min)</th><th>Nota média</th><th>% avaliados</th></tr></thead>
        <tbody id="tblRel"></tbody>
      </table>
    </div></div>
  </div>
</div>

<script>window.CTX = '<%= request.getContextPath() %>';</script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/jspdf.umd.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/jspdf.plugin.autotable.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-comum.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-relatorios.js"></script>
```

- [ ] **Step 2: Criar `chamados-relatorios.js`**

```js
// src/main/webapp/muralha-digital/assets/js/chamados/chamados-relatorios.js
(function () {
  var esc = Chamados.esc;
  var grafico = null;

  function filtros() {
    var p = {};
    new FormData(document.getElementById('formRel')).forEach(function (v, k) { if (v) p[k] = v; });
    return p;
  }

  function fmt(v, sufixo) { return v == null ? '—' : String(v).replace('.', ',') + (sufixo || ''); }

  async function iniciar() {
    var hoje = new Date();
    document.getElementById('rInicio').value = new Date(hoje.getFullYear(), hoje.getMonth() - 5, 1).toISOString().substring(0, 10);
    document.getElementById('rFim').value = hoje.toISOString().substring(0, 10);
    var o = await Chamados.get('Chamado', { acao: 'opcoes' });
    if (!o.ok) return;
    o.categorias.filter(function (c) { return !c.idPai; }).forEach(function (c) { adicionar('rCategoria', c.id, c.nome); });
    o.equipamentos.forEach(function (e) { adicionar('rEquipamento', e.idLocal, e.nome); });
    o.prioridades.forEach(function (p) { adicionar('rPrioridade', p.id, p.nome); });
    gerar();
  }

  function adicionar(id, valor, rotulo) {
    var op = document.createElement('option');
    op.value = valor; op.textContent = rotulo;
    document.getElementById(id).appendChild(op);
  }

  async function gerar() {
    var d = await Chamados.get('ChamadoRelatorio', Object.assign(filtros(), { acao: 'indicadores' }));
    if (!d.ok) return;
    var l = d.linhas;
    var tot = l.reduce(function (a, x) { return a + x.total; }, 0);
    var res = l.reduce(function (a, x) { return a + x.resolvidos; }, 0);
    var canc = l.reduce(function (a, x) { return a + x.cancelados; }, 0);
    document.getElementById('cards').innerHTML = [
      ['Chamados', tot, 'bi-inbox'], ['Resolvidos', res, 'bi-check2-circle'], ['Cancelados', canc, 'bi-x-circle']
    ].map(function (c) {
      return '<div class="col-md-4"><div class="card text-center"><div class="card-body"><h6 class="text-muted"><i class="bi ' +
        c[2] + ' me-1"></i>' + c[0] + '</h6><h2 class="fw-bold mb-0">' + c[1] + '</h2></div></div></div>';
    }).join('');
    document.getElementById('tblRel').innerHTML = l.map(function (x) {
      return '<tr><td>' + esc(x.grupo) + '</td><td>' + x.total + '</td><td>' + x.resolvidos + '</td><td>' + x.cancelados +
        '</td><td>' + fmt(x.pctSlaAtendimento, '%') + '</td><td>' + fmt(x.pctSlaResolucao, '%') + '</td><td>' + fmt(x.tmaMin) +
        '</td><td>' + fmt(x.tmrMin) + '</td><td>' + fmt(x.notaMedia) + '</td><td>' + fmt(x.pctAvaliados, '%') + '</td></tr>';
    }).join('') || '<tr><td colspan="10" class="text-center text-muted py-3">Sem dados no período.</td></tr>';

    if (grafico) grafico.destroy();
    grafico = new Chart(document.getElementById('grafico'), {
      data: {
        labels: l.map(function (x) { return x.grupo; }),
        datasets: [
          { type: 'bar', label: 'Chamados', data: l.map(function (x) { return x.total; }), backgroundColor: '#0d75bf', yAxisID: 'y' },
          { type: 'line', label: '% SLA resolução', data: l.map(function (x) { return x.pctSlaResolucao; }), borderColor: '#198754', yAxisID: 'y1' }
        ]
      },
      options: { scales: { y: { beginAtZero: true }, y1: { position: 'right', min: 0, max: 100, grid: { drawOnChartArea: false } } } }
    });
  }

  document.getElementById('formRel').addEventListener('submit', function (e) { e.preventDefault(); gerar(); });
  document.getElementById('btnCsv').addEventListener('click', function () {
    Chamados.baixar('ChamadoRelatorio', Object.assign(filtros(), { acao: 'exportar', formato: 'csv', origem: 'relatorio' }));
  });
  document.getElementById('btnXls').addEventListener('click', function () {
    Chamados.baixar('ChamadoRelatorio', Object.assign(filtros(), { acao: 'exportar', formato: 'xlsx', origem: 'relatorio' }));
  });
  document.getElementById('btnPdf').addEventListener('click', function () {
    Chamados.exportarPdf(Object.assign(filtros(), { origem: 'relatorio' }));
  });
  iniciar();
})();
```

- [ ] **Step 3: Criar `configuracao.jsp`**

```jsp
<%-- src/main/webapp/muralha-digital/pages/chamados/configuracao.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <nav aria-label="breadcrumb"><ol class="breadcrumb small">
    <li class="breadcrumb-item"><a href="index.jsp">Chamados</a></li>
    <li class="breadcrumb-item active" aria-current="page">Configuração</li>
  </ol></nav>
  <h4 class="mb-3"><i class="bi bi-gear me-2"></i>Configuração de Chamados</h4>

  <ul class="nav nav-tabs" role="tablist">
    <li class="nav-item"><button class="nav-link active" data-bs-toggle="tab" data-bs-target="#abaPri" type="button">Prioridades e prazos</button></li>
    <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#abaCat" type="button">Categorias</button></li>
  </ul>
  <div class="tab-content border border-top-0 p-3">
    <div class="tab-pane fade show active" id="abaPri">
      <button class="btn btn-sm btn-primary mb-2" id="btnNovaPri"><i class="bi bi-plus-circle me-1"></i>Nova prioridade</button>
      <table class="table table-sm align-middle"><thead><tr><th>Ordem</th><th>Código</th><th>Nome</th>
        <th>Atendimento (min)</th><th>Resolução (min)</th><th>Ativa</th><th></th></tr></thead><tbody id="tblPri"></tbody></table>
    </div>
    <div class="tab-pane fade" id="abaCat">
      <button class="btn btn-sm btn-primary mb-2" id="btnNovaCat"><i class="bi bi-plus-circle me-1"></i>Nova categoria</button>
      <table class="table table-sm align-middle"><thead><tr><th>Categoria</th><th>Subcategoria</th>
        <th>Exige equipamento</th><th>Ativa</th><th></th></tr></thead><tbody id="tblCat"></tbody></table>
    </div>
  </div>
</div>

<script>window.CTX = '<%= request.getContextPath() %>';</script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-comum.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-configuracao.js"></script>
```

- [ ] **Step 4: Criar `chamados-configuracao.js`**

```js
// src/main/webapp/muralha-digital/assets/js/chamados/chamados-configuracao.js
(function () {
  var esc = Chamados.esc;
  var dados = { prioridades: [], categorias: [] };

  async function carregar() {
    var d = await Chamados.get('ChamadoConfig', { acao: 'listar' });
    if (!d.ok) return;
    dados = d;
    document.getElementById('tblPri').innerHTML = d.prioridades.map(function (p) {
      return '<tr><td>' + p.ordem + '</td><td><code>' + esc(p.codigo) + '</code></td><td>' + Chamados.badgePrioridade(p.nome, p.cor) +
        '</td><td>' + p.minutosAtendimento + '</td><td>' + p.minutosResolucao + '</td><td>' + (p.ativo ? 'Sim' : 'Não') +
        '</td><td class="text-end"><button class="btn btn-sm btn-outline-primary" data-editar-pri="' + p.id + '"><i class="bi bi-pencil"></i></button> ' +
        '<button class="btn btn-sm btn-outline-secondary" data-ativo="prioridade:' + p.id + '">' + (p.ativo ? 'Desativar' : 'Ativar') + '</button></td></tr>';
    }).join('');
    document.getElementById('tblCat').innerHTML = d.categorias.map(function (c) {
      return '<tr><td>' + esc(c.idPai ? c.nomePai : c.nome) + '</td><td>' + esc(c.idPai ? c.nome : '—') + '</td><td>' +
        (c.exigeEquipamento ? 'Sim' : 'Não') + '</td><td>' + (c.ativo ? 'Sim' : 'Não') +
        '</td><td class="text-end"><button class="btn btn-sm btn-outline-primary" data-editar-cat="' + c.id + '"><i class="bi bi-pencil"></i></button> ' +
        '<button class="btn btn-sm btn-outline-secondary" data-ativo="categoria:' + c.id + '">' + (c.ativo ? 'Desativar' : 'Ativar') + '</button></td></tr>';
    }).join('');
  }

  async function enviar(params) {
    var fd = new URLSearchParams(params);
    var r = await fetch((window.CTX || '') + '/MuralhaDigital/ChamadoConfig', { method: 'POST', body: fd });
    var d = await r.json().catch(function () { return { ok: false, erro: 'Falha de comunicação.' }; });
    if (!d.ok) { Swal.fire('Atenção', d.erro, 'warning'); return; }
    Swal.fire({ icon: 'success', title: 'Salvo', timer: 1200, showConfirmButton: false });
    carregar();
  }

  function campo(id, rotulo, valor, tipo) {
    return '<div class="mb-2 text-start"><label class="form-label small" for="' + id + '">' + rotulo + '</label><input id="' + id +
      '" type="' + (tipo || 'text') + '" class="form-control form-control-sm" value="' + esc(valor == null ? '' : valor) + '"></div>';
  }

  async function editarPrioridade(p) {
    p = p || { id: 0, codigo: '', nome: '', ordem: dados.prioridades.length + 1, minutosAtendimento: 60, minutosResolucao: 240, cor: '#6c757d' };
    var r = await Swal.fire({
      title: p.id ? 'Editar prioridade' : 'Nova prioridade',
      html: campo('sCodigo', 'Código (MAIÚSCULAS)', p.codigo) + campo('sNome', 'Nome', p.nome) + campo('sOrdem', 'Ordem', p.ordem, 'number') +
            campo('sAt', 'Prazo de atendimento (minutos)', p.minutosAtendimento, 'number') +
            campo('sRes', 'Prazo de resolução (minutos)', p.minutosResolucao, 'number') + campo('sCor', 'Cor', p.cor, 'color'),
      showCancelButton: true, confirmButtonText: 'Salvar',
      preConfirm: function () {
        return { acao: 'salvarPrioridade', id: p.id, codigo: document.getElementById('sCodigo').value.trim().toUpperCase(),
          nome: document.getElementById('sNome').value, ordem: document.getElementById('sOrdem').value,
          minutosAtendimento: document.getElementById('sAt').value, minutosResolucao: document.getElementById('sRes').value,
          cor: document.getElementById('sCor').value };
      }
    });
    if (r.isConfirmed) enviar(r.value);
  }

  async function editarCategoria(c) {
    c = c || { id: 0, nome: '', idPai: null, exigeEquipamento: false };
    var pais = dados.categorias.filter(function (x) { return !x.idPai && x.id !== c.id; });
    var r = await Swal.fire({
      title: c.id ? 'Editar categoria' : 'Nova categoria',
      html: campo('sNomeCat', 'Nome', c.nome) +
        '<div class="mb-2 text-start"><label class="form-label small" for="sPai">Categoria pai</label><select id="sPai" class="form-select form-select-sm">' +
        '<option value="0">(nenhuma — categoria de primeiro nível)</option>' +
        pais.map(function (x) { return '<option value="' + x.id + '"' + (x.id === c.idPai ? ' selected' : '') + '>' + esc(x.nome) + '</option>'; }).join('') +
        '</select></div><div class="form-check text-start"><input class="form-check-input" type="checkbox" id="sExige"' +
        (c.exigeEquipamento && !c.idPai ? ' checked' : '') + '><label class="form-check-label small" for="sExige">Exige equipamento (só categoria de primeiro nível; subcategorias herdam)</label></div>',
      showCancelButton: true, confirmButtonText: 'Salvar',
      preConfirm: function () {
        return { acao: 'salvarCategoria', id: c.id, nome: document.getElementById('sNomeCat').value,
          idPai: document.getElementById('sPai').value, exigeEquipamento: document.getElementById('sExige').checked };
      }
    });
    if (r.isConfirmed) enviar(r.value);
  }

  document.addEventListener('click', function (e) {
    var b = e.target.closest('button');
    if (!b) return;
    if (b.dataset.editarPri) editarPrioridade(dados.prioridades.find(function (p) { return String(p.id) === b.dataset.editarPri; }));
    if (b.dataset.editarCat) editarCategoria(dados.categorias.find(function (c) { return String(c.id) === b.dataset.editarCat; }));
    if (b.dataset.ativo) {
      var partes = b.dataset.ativo.split(':');
      enviar({ acao: 'alternarAtivo', tipo: partes[0], id: partes[1] });
    }
  });
  document.getElementById('btnNovaPri').addEventListener('click', function () { editarPrioridade(null); });
  document.getElementById('btnNovaCat').addEventListener('click', function () { editarCategoria(null); });
  carregar();
})();
```

- [ ] **Step 5: Verificar no navegador**

1. **Gestor:** `relatorios.jsp` → cards, gráfico e tabela por mês; trocar para "Equipamento" e "Categoria"; CSV/XLS/PDF baixam com o mesmo SHA-256.
2. **Solicitante:** `relatorios.jsp` → bloqueado (403 da página por `sis_menu_direitos`).
3. **Equipe:** `configuracao.jsp` → mudar "Média" para 120/720 min; abrir chamado novo Média → prazos de +2 h/+12 h. Tentar resolução 30 < atendimento 60 → mensagem de validação.
4. Desativar uma subcategoria → some do modal "Novo chamado" (lista ainda mostra chamados antigos com ela).

- [ ] **Step 6: Commit**

```bash
git add src/main/webapp/muralha-digital/pages/chamados/relatorios.jsp src/main/webapp/muralha-digital/pages/chamados/configuracao.jsp src/main/webapp/muralha-digital/assets/js/chamados/chamados-relatorios.js src/main/webapp/muralha-digital/assets/js/chamados/chamados-configuracao.js
git commit -m "Adiciona telas de relatorios e configuracao de chamados

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Tarefa 13: Referência ao edital no menu, roteiro de testes e documentação de entrega

**Files:**
- Modify: `src/main/java/muralha/digital/acessos/ReferenciaEdital.java`
- Modify: `src/test/java/muralha/digital/acessos/ReferenciaEditalTest.java`
- Create: `docs/testes/22-sistema-chamados.md`, `docs/entregas/22-sistema-chamados.md`
- Modify: `docs/entregas/README.md`, `docs/entregas/itens-edital-x-entregas.md`, `docs/entregas/rastreabilidade-tr.md`, `docs/agentes/AGENTS.md`, `docs/requisitos/04-modulos-funcionais.md`, `docs-editais/edital-salvador/README.md`

- [ ] **Step 1: Escrever o teste que falha**

Em `ReferenciaEditalTest.java`, acrescentar:

```java
    @Test
    public void menusDoSistemaDeChamadosTemItensDoCapitulo7() {
        for (int idMenu = 379; idMenu <= 382; idMenu++) {
            assertFalse(ReferenciaEdital.itens(idMenu).isEmpty(), "id_menu " + idMenu + " sem referencia ao edital");
        }
        assertTrue(ReferenciaEdital.itens(379).contains("7.2.4"));
        assertTrue(ReferenciaEdital.itens(381).contains("7.2.3.d"));
    }
```

- [ ] **Step 2: Rodar e ver falhar**

Run: `mvn -o -s "$s\settings-legacy-archiva.xml" '-Dexec.skip=true' '-Dtest=ReferenciaEditalTest' '-DfailIfNoTests=false' test`
Expected: FAIL `id_menu 379 sem referencia ao edital`.

- [ ] **Step 3: Implementar**

Em `ReferenciaEdital.java`, depois de `Map.entry(378, ...)` (trocar o `)` final da entrada 378 por `),`):

```java
            // Plano 22 — Sistema de Gerenciamento de Chamados (aba "Sist. Chamado")
            Map.entry(379, List.of("7.2.3.a", "7.2.3.b", "7.2.3.c", "7.2.3.f", "7.2.4")),
            Map.entry(380, List.of("7.2.3.b", "7.2.3.c", "7.2.3.e")),
            Map.entry(381, List.of("7.2.3.d", "7.2.3.f")),
            Map.entry(382, List.of("7.2.3.a"))
```

- [ ] **Step 4: Rodar todos os testes do módulo**

Run: `mvn -o -s "$s\settings-legacy-archiva.xml" '-Dexec.skip=true' '-Dtest=ReferenciaEditalTest,MaquinaEstadosTest,CalculadoraSlaTest,GeradorProtocoloTest,ArmazenamentoAnexoTest,ExportacaoChamadosTest,EmailChamadoTest,FiltroChamadosTest,CadastrosChamadoTest,ChamadoServiceRegrasTest' '-DfailIfNoTests=false' test`
Expected: todos passando (`Failures: 0, Errors: 0`).

- [ ] **Step 5: Escrever `docs/testes/22-sistema-chamados.md`**

Roteiro manual com: pré-requisitos (scripts executados, três usuários de teste, um em cada grupo, `chamado_diretorio_anexos` existente, SMTP configurado); e os casos dos passos de verificação das Tarefas 10, 11 e 12, mais:
- **SLA acelerado:** em `configuracao.jsp`, prioridade de teste com 2/5 min; abrir chamado e aguardar o `ChamadoSlaJob` → histórico registra "SLA de atendimento: EM_RISCO" e depois "VENCIDO"; a lista mostra os badges.
- **Fechamento automático:** `UPDATE muralha.config_chave_valor SET valor='0' WHERE chave='chamado_fechamento_auto_dias'` (com autorização) → chamado RESOLVIDO fecha em até 5 min como "não avaliado"; restaurar para 5.
- **E-mail:** `SELECT status, tentativas, erro FROM muralha.chamado_notificacao ORDER BY id DESC` → `ENVIADO`; com SMTP inválido, chega a `ERRO` após 5 tentativas; usuário sem e-mail → `ERRO` imediato e abertura normal.
- **Anexo malicioso:** `.exe` renomeado para `.pdf` → "O conteúdo não corresponde à extensão do arquivo"; nenhum arquivo novo no diretório.
- **Auditoria:** `pages/auditoria/consulta-log.jsp` filtrando funcionalidade "Chamado" mostra ABRIR, transições e EXPORTAR.

- [ ] **Step 6: Escrever `docs/entregas/22-sistema-chamados.md`**

Seguir o formato de `docs/entregas/18-disponibilidade.md`: `# Entrega — Plano 22: Sistema de Gerenciamento de Chamados`, `**Data:**`, `**Status:**`, `## Escopo`, e obrigatoriamente:

```markdown
## Origem

- Plano [`22-sistema-chamados.md`](../planos-salvador/22-sistema-chamados.md) — Sistema de Gerenciamento de Chamados
- **Spec:** [`2026-10-15-sistema-chamados-design.md`](../superpowers/specs/2026-10-15-sistema-chamados-design.md)
- **Requisitos do edital:** itens 7.2.3 (a–f) e 7.2.4 da aba "Sist. Chamado" de [`Atividades Edital Salvador 2026.xlsx`](../../docs-editais/edital-salvador/Atividades%20Edital%20Salvador%202026.xlsx)
```

Depois: `## Arquivos produzidos`, `## Arquitetura`, `## Criterios de aceite` (um checkbox por subitem 7.2.3.a–f e 7.2.4), `## Pendencias` (incluir: Cap. 7 do TR ausente — prazos são os sugeridos; canais 7.2.2.c/d fora do escopo; simplificação do polling sem `desde`).

- [ ] **Step 7: Atualizar índices**

- `docs/entregas/README.md`: linha da entrega 22 na tabela.
- `docs/entregas/itens-edital-x-entregas.md`: nova seção "Capítulo 7 — Central de Suporte Técnico (aba Sist. Chamado)" com 7.2.3.a–f e 7.2.4 → entrega 22; 7.2.1, 7.2.2 e 7.2.5 como "fora do escopo de software / não atendido por este plano".
- `docs/entregas/rastreabilidade-tr.md`: itens 7.2.3 e 7.2.4 → Plano 22.
- `docs/agentes/AGENTS.md`: linha `| 22-sistema-chamados.md | Sistema de chamados (7.2.3/7.2.4) | 01, 11 |` na tabela de planos.
- `docs/requisitos/04-modulos-funcionais.md`: módulo "Sistema de Chamados" (menus 379–382, telas, servlets, tabelas).
- `docs-editais/edital-salvador/README.md`: plano 22 na tabela de planos.

- [ ] **Step 8: Build final**

Run: build → `BUILD SUCCESS`.

- [ ] **Step 9: Commit**

```bash
git add src/main/java/muralha/digital/acessos/ReferenciaEdital.java src/test/java/muralha/digital/acessos/ReferenciaEditalTest.java docs/testes/22-sistema-chamados.md docs/entregas/ docs/agentes/AGENTS.md docs/requisitos/04-modulos-funcionais.md docs-editais/edital-salvador/README.md
git commit -m "Documenta entrega do sistema de chamados e marca itens 7.2.3/7.2.4 no menu

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

## Complemento — administração, categoria própria e ícones (2026-10-15)

> Pedido do responsável após a primeira entrega, com a migração já executada no `GTW_MURALHA_DEV`:
> 1. Quem pode abrir chamados é definido na configuração dos poderes do usuário.
> 2. O sistema de chamados ganha tela própria (categoria na tela inicial), com ícones no estilo já usado.
> 3. O acordo de nível de serviço é configurável pelo administrador.
> 4. O acesso à configuração e à definição de quem usa o sistema aparece **só para o administrador**.
> 5. Todas as alterações são capturadas pelo motor de logs (`AuditoriaService` → `dbo.sis_log_auditoria`).

**Decisões:**
- Novo perfil `ADMIN`, mapeado do grupo em `chamado_grupo_admin` (padrão `11`, Administradores). A tela e a API de Configuração passam a exigir `ADMIN`; a `EQUIPE` deixa de configurar.
- `ADMIN` enxerga todos os chamados (somente leitura, a menos que também tenha outro perfil).
- Os grupos 107–109 passam a ter `id_grupo_pai = 11`, para aparecerem na tela legada de usuários (GWT), que lista os grupos abaixo do grupo de quem edita.
- Nova aba "Perfis de acesso" na Configuração: o administrador inclui ou remove usuários dos três grupos (mesma tabela `dbo.sis_usuario_grupo` da tela legada).
- Nova aba "Parâmetros de SLA": `chamado_sla_risco_percentual` (1–99) e `chamado_fechamento_auto_dias` (1–60).
- Categoria "Sistema de Chamados" na tela inicial (`sis_menu` 383, `sis_menu_infos` 49, `menu_pai NULL`, `ordenacao 10`). Os cards 46, 47 e 48 passam para `menu_pai = 49`. Ícones SVG novos em `assets/images/menu-icons/`.
- O direito do menu 382 (Configuração) fica só com o grupo administrador.

### Tarefa 14: Migração complementar (administração e categoria)

**Files:**
- Create: `docs/banco-de-dados/migracoes/20261015_sistema_chamados_admin.sql`

**Interfaces:**
- Produces: chave `chamado_grupo_admin = '11'`; `sis_grupo.id_grupo_pai = 11` para os grupos de `chamado_grupo_solicitante/gestor/equipe`; `sis_menu` 383 (`acao = '/login/muralha_principal.jsp?menu_info_pai=49'`, `tipo 'U'`, `nivel 1`, `id_pai_menu 222`, `nome_sistema 'Muralha-Digital'`); `sis_menu_infos` 49 (`src '/muralha-digital/assets/images/menu-icons/chamados-categoria.svg'`, `descricao 'Sistema de Chamados'`, `href '/login/muralha_principal.jsp?menu_info_pai=49&desc=Sistema de Chamados'`, `descricao_detalhada 'Central de suporte técnico'`, `ordenacao 10`, `menu_pai NULL`); direitos de 383 para os grupos 107, 108, 109 e o grupo admin; cards 46/47/48 com `menu_pai = 49`, `ordenacao` 1/2/3 e `src` `menu-icons/chamados.svg`, `menu-icons/chamados-relatorios.svg`, `menu-icons/chamados-configuracao.svg`; direitos do menu 382 apenas para o grupo admin (remove o grupo da equipe).

- [ ] **Step 1:** Escrever o script no padrão de `20261015_sistema_chamados_seed.sql`: idempotente (`IF NOT EXISTS`/`WHERE NOT EXISTS`), rollback comentado no cabeçalho, sem `ALTER TABLE`, lendo os IDs dos grupos de `muralha.config_chave_valor` e o ID admin de `chamado_grupo_admin`. Guarda: se `sis_menu` 383 ou `sis_menu_infos` 49 já existirem com `acao`/`href` diferente de `%menu_info_pai=49%`, `RAISERROR` + `RETURN` (bloco de menus em lote único). Textos com acento em `N'...'`.
- [ ] **Step 2:** Executar no DEV (autorizado pelo responsável em 2026-10-15): `sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -b -f 65001 -i docs\banco-de-dados\migracoes\20261015_sistema_chamados_admin.sql`. Rodar duas vezes para provar a idempotência.
- [ ] **Step 3:** Verificar: `SELECT id_grupo, id_grupo_pai FROM dbo.sis_grupo WHERE id_grupo IN (107,108,109)`; `SELECT id_infos, menu_pai, src FROM dbo.sis_menu_infos WHERE id_infos IN (46,47,48,49)`; `SELECT id_grupo FROM dbo.sis_menu_direitos WHERE id_menu = 382` (só o admin).
- [ ] **Step 4:** Commit `Adiciona migracao de administracao e categoria do sistema de chamados`.

### Tarefa 15: Ícones SVG da categoria e dos cards

**Files:**
- Create: `src/main/webapp/muralha-digital/assets/images/menu-icons/chamados-categoria.svg`, `chamados.svg`, `chamados-relatorios.svg`, `chamados-configuracao.svg`

- [ ] **Step 1:** Ler 3 SVGs existentes da pasta (`auditoria.svg`, `config-kpis.svg`, `gestao-lotes.svg`) e reproduzir o estilo: `viewBox="0 0 450 300"`, fundo `#f0f7fc` ou branco com moldura arredondada `#c5dff0` de 2 px, traço 5 px `#2c3e50` com cantos arredondados, destaque `#0d75bf`/`#1a78b0`, azul claro `#85c1e9`, SVG puro sem imagens externas nem fontes externas (texto, se houver, em Arial).
- [ ] **Step 2:** Metáforas: categoria = fone de ouvido (headset) com balão de conversa; chamados = ticket/etiqueta com balão e sinal de "+"; relatórios = gráfico de barras sobre um ticket; configuração = engrenagem com cronômetro (SLA) e silhueta de usuário (perfis).
- [ ] **Step 3:** Validar que cada arquivo é XML bem formado (`[xml](Get-Content arquivo -Raw)` no PowerShell) e tem menos de 4 KB.
- [ ] **Step 4:** Commit `Adiciona icones do sistema de chamados no estilo dos menus`.

### Tarefa 16: Perfil ADMIN, parâmetros de SLA e perfis de acesso (back-end)

**Files:**
- Modify: `PerfilChamado.java`, `ChamadoConfigDao.java`, `ChamadoConfigServlet.java` (pacote `muralha.digital.chamado`)
- Test: `src/test/java/muralha/digital/chamado/MaquinaEstadosTest.java`, `CadastrosChamadoTest.java`

**Interfaces:**
- `enum PerfilChamado { SOLICITANTE, GESTOR, EQUIPE, ADMIN }`; `veTodos` = GESTOR ∨ EQUIPE ∨ ADMIN. `MaquinaEstados` não muda: ADMIN sozinho não executa ações de chamado.
- `ChamadoConfigDao.perfis` também mapeia `chamado_grupo_admin` → `ADMIN`.
- `ChamadoConfigDao`:
  - `JsonObject parametrosSla(Connection)` → `{riscoPercentual, fechamentoAutoDias}` (lê `chamado_sla_risco_percentual` padrão 80 e `chamado_fechamento_auto_dias` padrão 5).
  - `void salvarParametrosSla(Connection, int riscoPercentual, int fechamentoAutoDias)` — valida 1–99 e 1–60 (`RegraChamadoException`); faz UPDATE em `muralha.config_chave_valor` e grava o valor anterior em `muralha.config_chave_valor_hist` se essa tabela tiver colunas compatíveis (conferir em `docs/banco-de-dados/tabelas/muralha/config.md`; se não tiver, só o UPDATE).
  - `JsonArray membrosPerfis(Connection)` → `[{perfil, idUsuario, nome, login, email, ativo}]` dos grupos de solicitante, gestor, equipe e admin (join `sis_usuario_grupo` × `sis_usuario`, `RTRIM`).
  - `JsonArray buscarUsuarios(Connection, String termo)` → até 20 usuários ativos cujo `nome` ou `usuario` contenha o termo (parâmetro `LIKE` com `%` escapado), `[{idUsuario, nome, login}]`.
  - `void adicionarMembro(Connection, PerfilChamado, int idUsuario)` / `void removerMembro(Connection, PerfilChamado, int idUsuario)` — só SOLICITANTE, GESTOR, EQUIPE (o grupo admin não é gerido por aqui → `RegraChamadoException`); insert idempotente (`WHERE NOT EXISTS`) e delete em `dbo.sis_usuario_grupo`.
- `ChamadoConfigServlet`: todas as ações exigem `ADMIN`. Novas ações: GET `parametros`, `perfis`, `buscarUsuarios&termo=`; POST `salvarParametros` (`riscoPercentual`, `fechamentoAutoDias`), `adicionarMembro` / `removerMembro` (`perfil`, `idUsuario`). Cada POST chama `AuditoriaService.registrar(req, "Chamado", <OPERACAO>, <id>, <descrição com antes/depois>)` — operações `SALVAR_PARAMETROS_SLA`, `ADICIONAR_PERFIL`, `REMOVER_PERFIL` (além das existentes) — e `logger.info` (log4j).

- [ ] **Step 1 (TDD):** testes primeiro: `PerfilChamado.veTodos(EnumSet.of(ADMIN))` é verdadeiro; ADMIN sozinho não tem nenhuma ação em `MaquinaEstados.permitidas` para qualquer status; validação dos parâmetros de SLA extraída para um método estático puro `ChamadoConfigDao.validarParametrosSla(int, int)` (limites 1–99 e 1–60, inclusive bordas) e testada em `CadastrosChamadoTest`.
- [ ] **Step 2:** implementar; rodar a bateria completa; build.
- [ ] **Step 3:** Commit `Adiciona perfil administrador, parametros de SLA e perfis de acesso dos chamados`.

### Tarefa 17: Telas — Configuração só do administrador

**Files:**
- Modify: `src/main/webapp/muralha-digital/pages/chamados/configuracao.jsp`, `assets/js/chamados/chamados-configuracao.js`, `assets/js/chamados/chamados-lista.js`

- [ ] **Step 1:** `chamados-lista.js`: link "Configuração" visível só com perfil `ADMIN`; "Relatórios" com `GESTOR`, `EQUIPE` ou `ADMIN`; "Novo chamado" continua com `SOLICITANTE` ou `GESTOR`.
- [ ] **Step 2:** `configuracao.jsp`: duas abas novas antes das existentes — "Parâmetros de SLA" (risco % e dias para fechamento automático, com texto explicando que os prazos por prioridade ficam na aba "Prioridades e prazos" e que as horas são corridas 24x7) e "Perfis de acesso" (três colunas/cartões: Solicitantes, Gestores do contrato, Equipe técnica; cada um com a lista de membros, botão remover e campo de busca com autocompletar para adicionar; administradores listados só para leitura).
- [ ] **Step 3:** `chamados-configuracao.js`: carregar parâmetros e perfis; salvar parâmetros; buscar (debounce 300 ms, mínimo 2 caracteres), adicionar e remover membros com confirmação SweetAlert; todo texto do servidor por `Chamados.esc()`/`textContent`; mensagens com `Swal.fire({title, text, icon})`; usar `Chamados.get`/`Chamados.post` (com `FormData`).
- [ ] **Step 4:** `node --check`; build. Commit `Adiciona abas de parametros de SLA e perfis de acesso na configuracao de chamados`.

### Tarefa 18: Teste integrado com o banco migrado e documentação

- [ ] **Step 1:** Subir o servidor e testar com usuários reais, seguindo `docs/testes/22-sistema-chamados.md` (incluir os casos novos: configuração visível só ao administrador; parâmetros de SLA; incluir/remover perfil; registros no `consulta-log.jsp` com funcionalidade "Chamado"; categoria "Sistema de Chamados" com os ícones novos).
- [ ] **Step 2:** Atualizar a spec (seção de ajustes), `docs/entregas/22-sistema-chamados.md`, `docs/testes/22-sistema-chamados.md`, `ReferenciaEdital` (menu 383 → itens 7.2.3 e 7.2.4, com teste) e os índices que citam os menus do módulo. Somente pastas existentes em `docs/`.
- [ ] **Step 3:** Commit, push e Pull Request para `main`, sem arquivos rastreáveis pendentes (`git status` limpo; `confGTW.xml` e `target/` seguem ignorados por regra do projeto).

## Cobertura da spec

| Spec | Tarefa |
|---|---|
| D1–D3, §2.4 telas integradas à Muralha | 10, 11, 12 |
| §1.1 perfis por grupo, união de permissões, 403 sem perfil | 1 (grupos), 6 (`perfis`), 8 (`ContextoRequisicao`) |
| §1.2 transições, observação obrigatória, controle otimista | 2, 7, 6 (`atualizarEstado`) |
| §1.3 SLA 24x7, pausas, reclassificação, marcas | 3, 7, 9 |
| §1.4 protocolo anual e confirmação/reclassificação por e-mail | 3, 5, 7, 9 |
| §2.1–2.3 tabelas, parâmetros, seed, menus | 1 |
| §3.3 segurança e visibilidade | 6 (`FiltroChamados`), 7 (`obterVisivel`), 8 |
| §3.4 anexos | 4, 7, 8 |
| §3.5 jobs | 9 |
| §3.6 exportação CSV/XLSX/PDF + auditoria | 5, 8, 10, 12 |
| §3.7 testes | 2–7, 13 |
| §3.8 documentação | 13 |
