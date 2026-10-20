# Arquitetura do Sistema GTW-CWB

Diagramas Mermaid da arquitetura completa do sistema GTW clássico + Muralha Digital.

---

## 1. Visão Geral — Camadas

```mermaid
graph TB
    subgraph Cliente["🖥️ Cliente (Browser)"]
        GWT["GWT/GXT<br/>(legado compilado)"]
        JSP["JSP + Bootstrap 5.3<br/>+ Chart.js / Leaflet"]
        WS_C["WebSocket Client"]
    end

    subgraph Servidor["☕ Tomcat 9 — JDK 13"]
        subgraph Filtros["Filtros & Listeners"]
            CF["CacheFilter"]
            SA["SessoesAtivas"]
            VS["ValidaSessao"]
        end

        subgraph Legado["com.consilux (Legado)"]
            WEB_XML["web.xml Servlets"]
            GWT_RPC["GWT RPC Services<br/>/GtwWidgets/* /GtwMenu/*"]
            REST["Jersey REST<br/>/rest/*"]
            SOAP["Axis SOAP<br/>/services/*"]
        end

        subgraph Muralha["muralha.digital (Moderno)"]
            SERVLETS["@WebServlet<br/>/MuralhaDigital/*"]
            SERVICES["Services<br/>(Auditoria, Senha, TOTP,<br/>Obliteração, Assinatura)"]
            JOBS["Quartz Jobs<br/>(21 jobs)"]
            CHAMADOS["Sistema de Chamados<br/>/MuralhaDigital/Chamado<br/>ChamadoRelatorio, ChamadoConfig<br/>ChamadoSlaJob, ChamadoEmailJob"]
            DASHB["Construtor de Dashboards<br/>(datasets, painéis, widgets)"]
        end

        subgraph Infra["Infraestrutura"]
            CONEXAO["Conexao.getConexao()"]
            CONFIG["ConfiguracaoProvider<br/>confGTW.xml"]
            INIT["Inicializacao<br/>Agendador"]
            SESSAO["SessaoConstantes"]
            AUDIT["AuditoriaService"]
        end

        WS_S["WebSocket Server"]
    end

    subgraph Banco["🗄️ SQL Server — GTW_MURALHA_DEV"]
        DBO["dbo.*<br/>(sistema, usuários, menus)"]
        MUR["muralha.*<br/>(passagens, infrações,<br/>monitoramento, chamado*, vtr_*)"]
        DASH["dash.*<br/>(construtor de dashboards)"]
        MOB["mobilidade.* · ia.*<br/>(Waze; IA de veículos)"]
        VIEWS["Views<br/>(vw_passagem_anonimizada)"]
    end

    subgraph Externos["🌐 Integrações Externas"]
        WAZE["Waze for Cities API"]
        EMAIL["SMTP (e-mail)"]
        SMS["SMS Gateway"]
        CAMERAS["Câmeras IP"]
    end

    GWT --> GWT_RPC
    JSP --> SERVLETS
    WS_C --> WS_S

    WEB_XML --> CONEXAO
    GWT_RPC --> CONEXAO
    SERVLETS --> SERVICES
    SERVICES --> CONEXAO
    SERVLETS --> AUDIT
    SERVLETS --> CHAMADOS
    SERVLETS --> DASHB
    JOBS --> CONEXAO
    CHAMADOS --> CONEXAO
    DASHB --> CONEXAO

    CONEXAO --> DBO
    CONEXAO --> MUR
    CONEXAO --> DASH
    CONEXAO -.-> MOB

    JOBS --> WAZE
    JOBS --> EMAIL
    JOBS --> SMS
```

O banco `GTW_MURALHA_DEV` (SQL Server 2016, ≈ 722 GB) tem **6 schemas e 812 tabelas** em 2026-10-20: `dbo` (437), `muralha` (167), `mobilidade` (183, não usado pelo Java deste projeto), `ia` (6), `dash` (15, criado em 2026-10-13) e `analise` (4, camada do Superset, criado em 2026-10-16), além de 140 views, 401 procedures, 302 funções, 8 triggers e 673 chaves estrangeiras. O detalhe está em [`../banco-de-dados/README.md`](../banco-de-dados/README.md).

---

## 2. Módulos Muralha Digital — PoC TRANSALVADOR

```mermaid
graph LR
    subgraph Fase1["Fase 1 — Infraestrutura e Segurança"]
        P01["01 Auditoria<br/>AuditoriaServlet<br/>sis_log_auditoria"]
        P02["02 SHA-256<br/>JobSha256Imagem<br/>vtr_imagem_complemento"]
        P03["03 Política Senhas<br/>SenhaService<br/>sis_senha_config"]
        P04["04 MFA/TOTP<br/>MfaConfigServlet<br/>TotpService"]
    end

    subgraph Fase2["Fase 2 — Processamento de Infrações"]
        P05["05 Dupla Análise<br/>InfracaoAnaliseServlet<br/>infracao_analise"]
        P06["06 Obliteração<br/>ObliteracaoServlet<br/>vtr_imagem_obliterada"]
        P07["07 SLA Latência<br/>SlaLatenciaServlet<br/>alerta_sla"]
        P08["08 Assertividade<br/>AssertividadeServlet"]
    end

    subgraph Fase3["Fase 3 — Identidade e Compliance"]
        P09["09 CPF/Matrícula<br/>UsuarioCpfMatriculaServlet<br/>sis_usuario_complemento"]
        P10["10 SLA 72h<br/>SlaPreprocessamentoServlet<br/>alerta_sla_preproc"]
        P11["11 Exportação<br/>ExportacaoServlet<br/>CSV/XLS/PDF"]
        P12["12 Retenção<br/>RetencaoServlet<br/>expurgo_log"]
        P13["13 Anonimização<br/>ConsultaAnonimizadaServlet<br/>vw_passagem_anonimizada"]
    end

    subgraph Fase4["Fase 4 — Gestão e Analytics"]
        P14["14 Lotes<br/>LoteServlet<br/>lote_infracao"]
        P15["15 KPIs<br/>KpiServlet<br/>kpi_config"]
        P16["16 GIS<br/>GisServlet<br/>Leaflet + GeoJSON/KML"]
    end

    subgraph Fase5["Fase 5 — Monitoramento e Integração"]
        P17["17 Time-lapse<br/>TimelapseServlet"]
        P18["18 Disponibilidade<br/>DisponibilidadeServlet<br/>equipamento_disponibilidade"]
        P19["19 Sensores<br/>IncidenteServlet<br/>incidente_externo"]
    end

    subgraph Fase6["Fase 6 — Dashboards e Atendimento"]
        P20["20/21 Construtor de Dashboards<br/>schema dash (15 tabelas)"]
        P22["22 Sistema de Chamados<br/>ChamadoServlet · ChamadoRelatorioServlet<br/>ChamadoConfigServlet · muralha.chamado*"]
    end
```

---

## 3. Quartz Jobs — Agendamento

```mermaid
gantt
    title Jobs Quartz — Intervalos de Execução
    dateFormat X
    axisFormat %s min

    section PoC
    SHA-256 Integridade (5 min)       :a1, 0, 5
    Revisão OCR (5 min)               :a7, 0, 5
    SLA Latência (5 min)              :a2, 0, 5
    Tratamento autom. alertas (1 min) :a8, 0, 1
    Alerta retenção tráfego (10 min)  :a9, 0, 10
    Disponibilidade Equip (10 min)    :a3, 0, 10
    Incidentes Waze (30 min)          :a4, 0, 30
    Carga Análises (30 min)           :a10, 0, 30
    SLA Pré-proc 72h (60 min)        :a5, 0, 60
    KPI Snapshot (60 min)             :a11, 0, 60
    Descarte Timelapse (60 min)       :a12, 0, 60
    Retenção Dados (1440 min)         :a6, 0, 60
    Relatório Gerencial (1440 min)    :a13, 0, 60
    Relatório Pré-proc (1440 min)     :a14, 0, 60

    section Notificações
    E-mail Alertas (1 min)            :b1, 0, 1
    E-mail Ocorrências (1 min)        :b2, 0, 1
    SMS Alertas (5 min)               :b3, 0, 5
    SMS Ocorrências (5 min)           :b4, 0, 5
    Retry Notificações (5 min)        :b5, 0, 5

    section Chamados
    SLA dos Chamados (5 min)          :d1, 0, 5
    E-mail dos Chamados (1 min)       :d2, 0, 1

    section Legado
    Processa Automático (10 min)      :c1, 0, 10
    Agendamento Infração (10 min)     :c2, 0, 10
```

---

## 4. Banco de Dados — Tabelas PoC

Convenção: nos diagramas `erDiagram`, `schema.tabela` é escrito `schema_tabela` (o Mermaid não aceita ponto no nome). Os nomes sem prefixo abaixo estão em `muralha` (exceto `sis_*`, em `dbo`). Os atributos do diagrama a seguir são ilustrativos; as colunas reais estão em [`../banco-de-dados/tabelas/`](../banco-de-dados/tabelas/).

```mermaid
erDiagram
    sis_usuario ||--o{ sis_log_auditoria : "registra"
    sis_usuario ||--o| sis_usuario_complemento : "1:1 complemento"
    sis_senha_config }o--|| sis_usuario : "politica"

    sis_usuario_complemento {
        int id_usuario PK
        varchar senha_hash
        int tentativas_invalidas
        datetime bloqueado_ate
        varchar totp_secret
        bit totp_habilitado
        varchar cpf
        varchar matricula
    }

    sis_log_auditoria {
        bigint id PK
        int id_usuario FK
        varchar funcionalidade
        varchar operacao
        datetime dt_operacao
    }

    sis_senha_config {
        int id PK
        varchar chave
        varchar valor
    }

    veiculo_tempo_real ||--o{ vtr_imagem_complemento : "hash SHA-256"
    veiculo_tempo_real ||--o{ infracao_analise : "dupla análise"
    veiculo_tempo_real ||--o{ vtr_status_analise : "status"
    veiculo_tempo_real ||--o{ vtr_imagem_obliterada : "obliteração"

    infracao_analise {
        bigint id PK
        bigint id_passagem FK
        int id_usuario_analista FK
        varchar classificacao
        int ordem_analise
        datetime dt_analise
    }

    vtr_imagem_obliterada {
        bigint id PK
        bigint id_imagem FK
        int x
        int y
        int largura
        int altura
    }

    infracao_imagem_obliteracao {
        bigint id PK
        bigint id_passagem FK
        int id_usuario FK
        datetime dt_obliteracao
    }

    alerta_sla {
        bigint id PK
        int total_passagens
        numeric media_ms
        numeric maximo_ms
        int threshold_ms
        bit violado
        datetime dt_alerta
    }

    alerta_sla_preproc {
        bigint id PK
        int total_pendentes
        int faixa_24h
        int faixa_48h
        int faixa_72h
        int faixa_acima
        datetime dt_snapshot
    }

    expurgo_log {
        bigint id PK
        varchar tabela
        bigint registros_removidos
        datetime dt_execucao
    }

    lote_infracao ||--o{ lote_infracao_item : "contém"

    lote_infracao {
        bigint id PK
        varchar codigo
        varchar descricao
        varchar status
        datetime dt_criacao
        datetime dt_envio
    }

    lote_infracao_item {
        bigint id PK
        bigint id_lote FK
        bigint id_passagem FK
    }

    kpi_config {
        int id PK
        varchar nome
        varchar query_sql
        varchar unidade
        numeric threshold_verde
        numeric threshold_amarelo
        int ordem
        bit ativo
    }

    equipamento_disponibilidade {
        bigint id PK
        int id_local FK
        bit disponivel
        datetime ultima_passagem
        int minutos_offline
        datetime dt_verificacao
    }

    incidente_externo {
        bigint id PK
        varchar fonte
        varchar tipo
        varchar descricao
        decimal latitude
        decimal longitude
        int severidade
        datetime dt_ocorrencia
    }

    config_chave_valor {
        int id PK
        varchar chave UK
        varchar valor
    }
```

### 4.1 Sistema de Chamados (Plano 22)

```mermaid
erDiagram
    muralha_chamado_categoria ||--o{ muralha_chamado : "classifica"
    muralha_chamado_prioridade ||--o{ muralha_chamado : "define SLA"
    muralha_chamado ||--o{ muralha_chamado_historico : "eventos"
    muralha_chamado ||--o{ muralha_chamado_anexo : "arquivos"
    muralha_chamado ||--o{ muralha_chamado_notificacao : "fila de e-mail"
    dbo_sis_usuario ||..o{ muralha_chamado : "solicitante e responsavel (logica)"
    dbo_local ||..o{ muralha_chamado : "equipamento (logica)"
    muralha_chamado_protocolo
```

Linha tracejada = relação lógica, sem FK. `ChamadoSlaJob` (5 min) recalcula os prazos/SLA e `ChamadoEmailJob` (1 min) envia as linhas pendentes de `chamado_notificacao`. Colunas completas e diagrama detalhado em [`../banco-de-dados/README.md`](../banco-de-dados/README.md) §6A.

### 4.2 Construtor de Dashboards (schema `dash`)

```mermaid
erDiagram
    dash_catalogo_tabela ||--o{ dash_catalogo_coluna : "colunas"
    dash_catalogo_tabela ||--o{ dash_dataset : "tabela base"
    dash_dataset ||--o{ dash_dataset_tabela : "tabelas"
    dash_dataset ||--o{ dash_dataset_campo : "campos"
    dash_dataset ||--o{ dash_dataset_relacao : "juncoes"
    dash_dashboard ||--o{ dash_widget : "graficos"
    dash_dataset ||--o{ dash_widget : "fonte de dados"
    dbo_sis_usuario ||--o{ dash_dashboard : "autor"
```

Também fazem parte do schema `catalogo_relacao`, `dataset_filtro_fixo`, `dataset_modelador`, `dataset_estatistica`, `compartilhamento`, `historico_versao` e `consulta_log` (15 tabelas no total; ver [`../banco-de-dados/README.md`](../banco-de-dados/README.md) §5A e §6B).

---

## 5. Módulos Legados — Visão Geral

```mermaid
mindmap
  root((GTW-CWB))
    com.consilux
      conf
        ConfiguracaoProvider
        ConfiguracaoServlet
      infra
        Conexao
        SessaoConstantes
        ValidaSessao
        SessoesAtivas
        ServicoEmail
        CriptografiaAES
      lib
        DateUtil
        GenericCache
        QuartzConnectionProvider
      model
        527+ classes
        Jobs legados
        Modelos de domínio
      servlet
        ajax
        processamento
        relatorio
        remessa
        descarga
        medicao
        manutencao
        ferramentas
      ui.server
        GWT RPC Services
      rest
        Jersey JAX-RS
    muralha.digital
      66 módulos
      151 servlets
      21 Quartz jobs
      6 services
      WebSocket
