# Projeto de Adaptação — GTW-CWB para Edital Salvador (TRANSALVADOR)

> **Objetivo:** Detalhar as adaptações necessárias no sistema GTW-CWB para atingir aderência suficiente ao Termo de Referência do edital de Fiscalização Eletrônica de Salvador, com foco na aprovação na Prova de Conceito (PoC) e na execução contratual.  
> **Base:** Análise de aderência `analise-aderencia.md` (2026-10-08)  
> **Arquitetura alvo:** Java/JSP (Tomcat 9, SQL Server), sem novos frameworks — conforme restrições de `CLAUDE.md`

---

## 1. Escopo e Priorização

As adaptações são divididas em três ondas:

| Onda | Prazo sugerido | Foco |
|---|---|---|
| **Onda 1 — PoC** | Antes da Prova de Conceito | Gaps que causam reprovação imediata |
| **Onda 2 — Contrato** | Primeiros 30 dias de vigência | Requisitos obrigatórios da execução contratual |
| **Onda 3 — Maturidade** | Durante a vigência | Indicadores, analytics e infraestrutura |

---

## 2. Onda 1 — Gaps Críticos para a PoC

### 2.1 Dupla Análise Independente de Registros (§5.2.3 e §5.5)

**Requisito:** O sistema deve impedir que o mesmo operador valide um registro mais de uma vez e exigir dois operadores independentes. Em caso de divergência, encaminhar automaticamente para um terceiro desempatador sem acesso às análises anteriores.

**Impacto:** Eliminatório na PoC (§5.2.8.3 do Anexo A).

**Solução:**

*Banco de dados — nova tabela:*
```sql
-- Script em docs/banco-de-dados/migracoes/
CREATE TABLE muralha.infracao_analise (
    id              BIGINT IDENTITY PRIMARY KEY,
    id_infracao     BIGINT NOT NULL,
    id_usuario      INT NOT NULL,
    classificacao   VARCHAR(50) NOT NULL,   -- 'VALIDA', 'INVALIDA', 'DUVIDA'
    justificativa   VARCHAR(500),
    dt_analise      DATETIME2 DEFAULT SYSDATETIME(),
    sequencia       TINYINT NOT NULL,       -- 1=primeira, 2=segunda, 3=desempate
    CONSTRAINT uq_infracao_usuario UNIQUE (id_infracao, id_usuario)
);
```

*Back-end (`muralha.digital.processamento`):*
- Novo servlet `InfracaoAnaliseServlet` com ações: `obterProximaParaAnalise`, `registrarAnalise`, `obterStatusDupla`.
- `obterProximaParaAnalise`: exclui registros já analisados pelo usuário logado (`NOT EXISTS` na `infracao_analise`).
- `registrarAnalise`: insere na `infracao_analise`; se `sequencia=2` e classificações divergem, cria fila de desempate; se `sequencia=2` e concordam, marca infração como pré-aprovada.
- Bloqueio sistêmico: verificar antes de exibir registro se o usuário logado já aparece na `infracao_analise` para aquele `id_infracao`.

*Front-end (`pages/processamento/dupla-analise/`):*
- Tela Bootstrap 5 com fila de trabalho do operador.
- Campo de classificação e justificativa obrigatórios.
- Sem exibir a análise anterior ao segundo operador (dados ocultos até submit).

---

### 2.2 Obliteração de Imagens — LGPD (§5.2.4.3-4)

**Requisito:** Obliteração automática e manual de rostos/ocupantes não relacionados à infração. Reversão apenas por usuário autorizado com justificativa + log.

**Solução:**

*Banco de dados:*
```sql
CREATE TABLE muralha.infracao_imagem_obliteracao (
    id                  BIGINT IDENTITY PRIMARY KEY,
    id_imagem           BIGINT NOT NULL,
    tipo                CHAR(1) NOT NULL,   -- 'A'=automática, 'M'=manual
    coordenadas_json    VARCHAR(MAX),       -- [{x, y, w, h}]
    dt_aplicacao        DATETIME2 DEFAULT SYSDATETIME(),
    id_usuario_aplic    INT NOT NULL,
    revertida           BIT DEFAULT 0,
    dt_reversao         DATETIME2,
    id_usuario_revers   INT,
    justificativa_revers VARCHAR(500)
);
```

*Back-end (`muralha.digital.imagem`):*
- `ImagemObliteracaoServlet`: ações `aplicar`, `reverter`, `obterStatus`.
- `reverter`: exige perfil com direito `OBLITERACAO_REVERTER` + justificativa obrigatória; registra em log.
- Geração de imagem obliterada: aplicar retângulos pretos via Java (BufferedImage + Graphics2D) sobre cópia da imagem; original preservado intacto em banco.

*Front-end:*
- Ferramenta de seleção de área retangular na tela de análise de imagens (canvas overlay).
- Reversão só visível para perfis com permissão `OBLITERACAO_REVERTER`.

---

### 2.3 Política de Senhas e MFA (§5.1.5)

**Requisito:** Complexidade mínima, prazo de validade, histórico de reutilização, bloqueio após tentativas inválidas, suporte a MFA.

**Solução:**

*Banco de dados:*
```sql
ALTER TABLE dbo.sis_usuario ADD
    senha_hash_list     VARCHAR(MAX),   -- JSON com últimas 5 hashes
    dt_ultima_troca     DATE,
    tentativas_invalidas TINYINT DEFAULT 0,
    bloqueado_ate       DATETIME2,
    totp_secret         VARCHAR(100);   -- null = MFA desabilitado

CREATE TABLE dbo.sis_senha_config (
    chave   VARCHAR(50) PRIMARY KEY,
    valor   VARCHAR(200)
);
-- Inserir: min_length=8, requer_maiuscula=1, requer_numero=1, requer_especial=1,
--          validade_dias=90, historico_qtde=5, max_tentativas=5, bloqueio_minutos=30
```

*Back-end (`com.consilux.acesso.SenhaService`):*
- `validarComplexidade(senha)`: regex configurável via `sis_senha_config`.
- `verificarHistorico(idUsuario, novaSenha)`: checar hash SHA-256 contra `senha_hash_list`.
- `registrarFalha(idUsuario)`: incrementar `tentativas_invalidas`; se >= max, setar `bloqueado_ate`.
- `validarMFA(idUsuario, totp)`: validar TOTP com biblioteca `com.warrenstrange:googleauth` (já no Maven ou implementar manualmente com HMAC-SHA1 — sem nova dependência: usar `javax.crypto.Mac`).

*Front-end (`login/login.jsp`):*
- Campo TOTP na tela de login quando `totp_secret IS NOT NULL`.
- Tela de setup de MFA com QR Code gerado server-side (URL `otpauth://`).
- Tela de troca de senha obrigatória na primeira tentativa com senha vencida.

---

### 2.4 Verificação de Integridade SHA-256 (§5.2.1.2)

**Requisito:** Cada arquivo recebido dos equipamentos deve ter hash SHA-256 verificado na recepção.

**Solução:**

*Back-end:*
- Em `CSXEventsWS` (ou no job de recepção de imagens), calcular `MessageDigest.getInstance("SHA-256")` sobre o byte array da imagem recebida.
- Armazenar hash em nova coluna `sha256` na tabela `muralha.veiculo_tempo_real_imagem`.
- Se equipamento não envia o hash esperado, sinalizar com `status_integridade = 'FALHA'` e gerar alerta operacional.

```sql
ALTER TABLE muralha.veiculo_tempo_real_imagem ADD
    sha256              CHAR(64),
    status_integridade  VARCHAR(20) DEFAULT 'OK';
```

---

### 2.5 Log de Auditoria Completo com Exportação (§5.1.7-10)

**Requisito:** Log com: usuário, timestamp (precisão segundos), IP, funcionalidade, operação, ID do registro. Consulta com filtros e exportação PDF/CSV/XLS.

**Solução:**

*Banco de dados:*
```sql
CREATE TABLE dbo.sis_log_auditoria (
    id              BIGINT IDENTITY PRIMARY KEY,
    id_usuario      INT,
    login           VARCHAR(50),
    dt_operacao     DATETIME2(3) DEFAULT SYSDATETIME(),
    ip_terminal     VARCHAR(45),
    funcionalidade  VARCHAR(100),
    operacao        VARCHAR(50),
    id_registro     VARCHAR(100),
    descricao       VARCHAR(500),
    INDEX ix_log_audit_dt (dt_operacao),
    INDEX ix_log_audit_user (id_usuario)
);
```

*Back-end:*
- `AuditoriaService.registrar(request, operacao, funcionalidade, idRegistro, descricao)`: captura IP via `request.getRemoteAddr()` (considerar header `X-Forwarded-For` quando há nginx).
- Invocar em todos os servlets críticos (processamento, validação, exportação, configuração, login/logout).

*Front-end (`pages/auditoria/consulta-log.jsp`):*
- Filtros: usuário, período, IP, funcionalidade, tipo de operação.
- Tabela paginada com exportação SheetJS (XLS), jsPDF (PDF) e download CSV.
- Acesso restrito a perfil `AUDITORIA_CONSULTA`.

---

### 2.6 Painel de Assertividade da Pré-classificação (§5.2.2.6)

**Requisito:** Painel com indicadores em tempo real: taxa de assertividade, volume processado, divergências, registros para revisão manual.

**Solução:**

*Back-end (`muralha.digital.processamento.AssertividadeServlet`):*
```java
// acao=obterIndicadores
// Query:
// SELECT
//   COUNT(*) total,
//   SUM(CASE WHEN ia.classificacao = fa.classificacao_final THEN 1 ELSE 0 END) assertivos,
//   SUM(CASE WHEN ia.sequencia=3 THEN 1 ELSE 0 END) desempates,
//   COUNT(*) - SUM(...assertivos) divergencias
// FROM muralha.infracao_analise ia ...
// WHERE MONTH(ia.dt_analise) = MONTH(SYSDATETIME())
```

*Front-end (`pages/processamento/painel-assertividade/`):*
- Cards com indicadores (Bootstrap 5, Chart.js para série temporal).
- Atualização automática a cada 30s (AJAX polling).
- Indicadores: taxa de assertividade %, volume dia/mês, divergências, backlog pendente.

---

### 2.7 SLA de Latência ≤4s Monitorado (§5.6.2.3 e §5.7.9)

**Requisito:** Tempo máximo entre evento no equipamento e recepção na plataforma central: ≤4 segundos. Monitorar continuamente e alertar ao exceder.

**Solução:**

*Banco de dados:*
```sql
ALTER TABLE muralha.veiculo_tempo_real ADD
    dt_captura_equipamento  DATETIME2(3),   -- enviado pelo equipamento
    dt_recepcao_servidor    DATETIME2(3),   -- SYSDATETIME() na recepção
    latencia_ms             AS DATEDIFF(MILLISECOND, dt_captura_equipamento, dt_recepcao_servidor) PERSISTED;

CREATE TABLE muralha.sla_latencia_alerta (
    id          BIGINT IDENTITY PRIMARY KEY,
    dt_alerta   DATETIME2 DEFAULT SYSDATETIME(),
    latencia_ms INT,
    id_equipamento INT,
    resolvido   BIT DEFAULT 0
);
```

*Back-end:*
- Job Quartz (ciclo 60s) calcula média de latência dos últimos 5 minutos por equipamento.
- Se média > 4000ms: inserir em `sla_latencia_alerta` e gerar alerta via `muralha.alerta`.

*Front-end:*
- Widget no painel de telemetria mostrando latência média por equipamento (semáforo verde/amarelo/vermelho).

---

## 3. Onda 2 — Requisitos para Início da Execução Contratual

### 3.1 Controle de SLA de Pré-processamento (§5.5.2.1)

**Requisito:** Prazo máximo 72h por lote. Relatório mensal até o 5º dia útil.

**Solução:**
- Coluna `dt_captura` + `dt_conclusao_preproc` na tabela de lotes.
- Job Quartz diário: lotes com `dt_captura < SYSDATETIME() - 60h` e não concluídos → alerta automático.
- Relatório mensal agendado em Quartz para geração no 1º dia útil do mês seguinte, e-mail para fiscalização.

---

### 3.2 Identificação por CPF/Matrícula (§5.1.5a)

**Requisito:** Login vinculado obrigatoriamente a CPF ou matrícula funcional.

**Solução:**
```sql
ALTER TABLE dbo.sis_usuario ADD
    cpf         CHAR(11),
    matricula   VARCHAR(20);
-- Validar CPF com algoritmo de dígito verificador no back-end.
```

- `UsuarioServlet`: incluir CPF/matrícula no cadastro e na validação de unicidade.
- Tela de cadastro: campo CPF com máscara e validação.

---

### 3.3 Exportação Padronizada PDF/CSV/XLS (§5.2.8.3 e §5.4.5.2)

**Requisito:** Todos os relatórios devem exportar em PDF, CSV e XLS.

**Solução:**
- Criar `ExportacaoService` (Java) com métodos `exportarCSV(List<Object[]> dados, String[] cabecalhos)`, `exportarXLS(...)` (Apache POI — verificar se já no pom.xml), `exportarPDF(...)` (iText ou jsPDF no cliente).
- Padronizar: todos os ~35 relatórios existentes chamar o mesmo service.
- Front-end: botões "Exportar PDF / CSV / XLS" padronizados via componente Bootstrap reutilizável.

---

### 3.4 Retenção de Dados e Logs (§5.5.4.6 e §5.7.5.3)

**Requisito:** Logs e histórico de LAP retidos por 5 anos após encerramento contratual.

**Solução:**
- Definir política em `sis_senha_config` / nova tabela `dbo.politica_retencao` com prazo por tipo de dado.
- Job Quartz anual para geração de relatório de inventário de dados para auditoria.
- **Não implementar exclusão automática** — dados ficam em banco; armazenamento em fitas/cold storage é responsabilidade de infra.

---

### 3.5 Anonimização de Placas para Fins Estatísticos (§5.7.8)

**Requisito:** Anonimização parametrizável de placas em registros para fins estatísticos/analíticos.

**Solução:**
- Criar `view muralha.vw_veiculo_anonimizado` que retorna `SUBSTRING(placa, 1, 3) + '****'` e demais campos não identificadores.
- Configuração `ANONIMIZAR_ESTATISTICAS` em `muralha.config_chave_valor`.
- Relatórios/dashboards estatísticos usam a view anonimizada quando configuração habilitada.

---

### 3.6 Gestão de Lotes e Painel de Pré-processamento (§5.5.3)

**Novo módulo:** `pages/processamento/gestao-lotes/`

*Funcionalidades:*
- Lista de lotes com: equipamento, data/hora captura, qtde registros, status, tempo decorrido, SLA.
- Filtros: período, equipamento, status, operador.
- Drill-down por lote: registros individuais com status de análise.
- KPIs: aproveitamento, taxa de divergência, produtividade por operador, backlog.
- Exportação completa em PDF/CSV/XLS.

---

## 4. Onda 3 — Indicadores, Analytics e Infraestrutura

### 4.1 KPIs Configuráveis com Metas e Alertas Analíticos (§5.4.6.3)

**Novo módulo:** `pages/inteligencia/kpis/`

- Tabela `muralha.kpi_definicao` (nome, fórmula, meta, limiar_atencao, limiar_critico).
- Job Quartz: calcula KPIs diariamente, grava em `muralha.kpi_historico`.
- Alert: se KPI cruza limiar, insere em `muralha.alerta` com tipo `ANALITICO`.
- Dashboard de KPIs com série temporal (Chart.js), semáforo de status, comparação com período anterior.

---

### 4.2 Exportação GIS (§5.4.4.4)

**Requisito:** Exportação de dados georreferenciados em formato GIS (GeoJSON mínimo).

**Solução:**
- `GisExportServlet` com ação `exportarGeoJSON`: query em `local_vigente` + `muralha.alerta` com coordenadas; retorna FeatureCollection JSON padrão GeoJSON.
- KML: transformar GeoJSON em KML (servidor) para compatibilidade com Google Earth/ArcGIS.

---

### 4.3 Vídeos Time-Lapse (§5.7.7)

**Requisito:** Geração automatizada de time-lapse associado a registros LAP.

**Solução:**
- Job Quartz: para cada passagem com imagens múltiplas (`veiculo_tempo_real_imagem`), chamar FFmpeg:
  ```
  ffmpeg -framerate 6 -i frame_%d.jpg -vf scale=1280:720 output.mp4
  ```
- Armazenar caminho em `veiculo_tempo_real.path_timelapse`.
- Visualização na tela de detalhes da passagem (player de vídeo HTML5).
- Descarte automático após 72h se não vinculado a infração/alerta/auditoria.

---

### 4.4 Módulo de Auditoria de Disponibilidade (§5.8.2.1)

**Requisito:** Disponibilidade mínima 99,5% (sistema) e 99% (datacenter). Relatório mensal.

**Solução:**
- Job Quartz (ciclo 5 min): health-check do banco e do sistema; registrar em `dbo.disponibilidade_log` (timestamp, status, latencia_ms).
- Cálculo de uptime mensal: `SUM(CASE WHEN status='OK' THEN 5 ELSE 0 END) / (total_minutos_mes) * 100`.
- Relatório mensal de disponibilidade, exportável em PDF.
- Integrar a dashboard de operações.

---

### 4.5 Integração com Sensores Externos e Waze (§5.3.7.2-3)

**Requisito:** Integração com sensores meteorológicos, PMV, sensores de tráfego e plataformas de navegação.

**Solução:**
- `IntegracaoExternaServlet`: abstração por tipo de integração (METEO, PMV, WAZE).
- Waze: consumir API pública de tráfego (`alerts` e `jams`) → armazenar em `mobilidade.waze_alerta` (schema já existe).
- Meteorologia: API aberta (OpenWeatherMap) com chave em variável de ambiente.
- PMV: protocolo NTCIP 1203 via REST (definir com a TRANSALVADOR).

---

## 5. Matriz de Esforço por Item

| Item | Onda | Complexidade | Arquivos novos | Tabelas novas |
|---|---|---|---|---|
| 2.1 Dupla análise independente | 1 | Alta | 2 servlets, 2 JSP | 1 |
| 2.2 Obliteração de imagens | 1 | Alta | 1 servlet, 1 JSP | 1 |
| 2.3 Política de senhas + MFA | 1 | Alta | 1 service, 2 JSP | 2 |
| 2.4 Hash SHA-256 | 1 | Baixa | — | 2 colunas |
| 2.5 Log de auditoria + exportação | 1 | Média | 1 servlet, 1 JSP | 1 |
| 2.6 Painel de assertividade | 1 | Média | 1 servlet, 1 JSP | — |
| 2.7 SLA de latência ≤4s | 1 | Média | 1 job, 1 widget | 2 tabelas |
| 3.1 SLA pré-processamento 72h | 2 | Baixa | 1 job | 2 colunas |
| 3.2 CPF/matrícula obrigatório | 2 | Baixa | — | 2 colunas |
| 3.3 Exportação padronizada | 2 | Média | 1 service | — |
| 3.4 Política de retenção | 2 | Baixa | — | 1 tabela |
| 3.5 Anonimização de placas | 2 | Baixa | 1 view | — |
| 3.6 Gestão de lotes + painel | 2 | Alta | 2 servlets, 3 JSP | — |
| 4.1 KPIs configuráveis | 3 | Alta | 2 servlets, 2 JSP | 2 tabelas |
| 4.2 Exportação GIS | 3 | Média | 1 servlet | — |
| 4.3 Time-lapse | 3 | Média | 1 job | 1 coluna |
| 4.4 Auditoria de disponibilidade | 3 | Média | 1 job, 1 JSP | 1 tabela |
| 4.5 Integração sensores/Waze | 3 | Alta | 2 servlets | 2 tabelas |

---

## 6. Padrões de Implementação

Todos os itens devem seguir as regras do projeto:

- **Sessão e permissão:** Toda nova tela inclui `cabecalho_bootstrap_simples.jsp` com `Acesso.verificaAcesso()`. Todo novo servlet valida sessão antes de processar.
- **SQL parametrizado:** Apenas `PreparedStatement`/`CallableStatement`. Sem concatenação de string com dados do usuário.
- **Recursos fechados:** Sempre `finally { rs.close(); stmt.close(); conn.close(); }`.
- **Padrão visual:** Bootstrap 5.3, navbar `#0d75bf`, Bootstrap Icons, SweetAlert2 para confirmações.
- **Scripts SQL:** Toda nova DDL/DML vai em `docs/banco-de-dados/migracoes/` com nome `YYYYMMDD_descricao.sql`.
- **Segredos:** Nenhuma chave ou senha no código — usar `muralha-digital-config.xml` (variáveis de ambiente) conforme `docs/07-configuracao-e-segredos.md`.
- **Sem novos frameworks:** Usar apenas as dependências já declaradas em `pom.xml`.

---

## 7. Pré-condições

Antes de iniciar a Onda 1:

1. **Verificar disponibilidade de `javax.crypto.Mac`** no JDK 13.0.1 para TOTP — disponível nativamente.
2. **Verificar Apache POI** no `pom.xml` — se ausente, avaliar alternativa (exportação XLS via SheetJS no cliente já está em uso nos relatórios existentes; preferir essa abordagem para manter consistência).
3. **Confirmar esquema `muralha`** no banco de desenvolvimento tem permissão de DDL para o usuário configurado.
4. **Alinhar com a TRANSALVADOR** a especificação de formato de exportação do lote infracional (item 5.2.5 do TR) — pode exigir adaptação do formato de remessa existente.
5. **Definir** qual dos equipamentos do parque atual envia hash SHA-256 com os arquivos — caso não enviem, a verificação será apenas server-side (calcular e registrar no recebimento).

---

## 8. Riscos do Projeto

| Risco | Probabilidade | Impacto | Mitigação |
|---|---|---|---|
| Dupla análise quebra fluxo GTW clássico de validação | Alta | Alto | Implementar no Muralha Digital independente; integrar gradualmente |
| MFA sem nova dependência: TOTP manual pode ter bugs criptográficos | Média | Alto | Testar com authenticators (Google, Microsoft, Authy) antes da PoC |
| Obliteração de imagens muda armazenamento de arquivos | Média | Médio | Preservar original no banco; trabalhar com cópia obliterada |
| Performance de SHA-256 em volume alto de imagens | Baixa | Médio | Calcular assíncrono no job de recepção, não no path síncrono |
| Integração com sensores externos: protocolos não documentados | Alta | Médio | Iniciar pelo Waze (API pública), deixar PMV para negociação com TRANSALVADOR |
| Infra de datacenter sem certificação Tier III | Alta | Alto | Contratar datacenter certificado antes de assinar contrato |

---

## 9. Definição de Pronto (DoD) por Item

- Código revisado e sem segredos expostos.
- Script de migração SQL em `docs/banco-de-dados/migracoes/`.
- Permissão de acesso cadastrada em `config_grupo_permissao` e `sis_menu`.
- Exportação (quando aplicável) testada para PDF, CSV e XLS.
- Log de auditoria funcionando para a nova funcionalidade.
- Build limpo (`..\.setup-gtw\build.ps1`) sem erros.
- Testado manualmente no fluxo completo com dois usuários distintos (quando dupla análise envolvida).
