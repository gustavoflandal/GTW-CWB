# Guia para Subagentes — GTW-CWB

> **Leia este documento antes de qualquer outra coisa.** Ele mapeia onde cada tipo de informação está e o que você precisa ler dependendo do que vai fazer.

---

## Orientação geral

Você está trabalhando no sistema **GTW-CWB** (Consilux Tecnologia): sistema legado Java/JSP sobre Tomcat 9 + SQL Server 2016, banco `GTW_MURALHA_DEV` em 10.0.0.200.

**Regras que nunca mudam (CLAUDE.md):**
- Não alterar `.setup-gtw/`, `pom.xml`, nem adicionar dependências externas
- Sem `ALTER TABLE` em tabelas existentes — usar tabelas complementares
- Todo código novo: verificar sessão + direito com `muralha.digital.acessos.ControleAcesso` (`exigir`, `exigirTela` ou `exigirSessao`; verificação **estrita**, que nega quando a URL não está cadastrada em `sis_menu`/`sis_menu_direitos` e responde 401/403 em JSON). `Acesso.verificaAcesso(false)` é *fail-open* e fica só para o legado ([Plano 35](../planos-salvador/35-correcoes-seguranca-funcionais.md)). Toda URL nova precisa de entrada em `sis_menu` com direitos (script em `docs/banco-de-dados/migracoes/`)
- SQL sempre parametrizado (`PreparedStatement`)
- DDL/DML somente como script em `docs/banco-de-dados/migracoes/`
- Commits em português, imperativo

---

## Mapa de documentação por tarefa

### Antes de escrever qualquer código

| O que ler | Onde está | Por que |
|---|---|---|
| Errata obrigatória | `docs/planos-salvador/00c-errata-correcoes.md` | Corrige erros nos planos; leia SEMPRE |
| Tabelas complementares | `docs/planos-salvador/00b-tabelas-complementares.md` | Schema das tabelas novas; não usar ALTER TABLE |
| Padrões de código | `docs/requisitos/06-padroes-de-codigo.md` | Sessão, acesso, conexão, SQL |
| Stack e ambiente | `docs/requisitos/01-stack-e-ambiente.md` | Como fazer build/run/debug |

### Quando criar um servlet novo

| O que ler | Onde está |
|---|---|
| Estrutura canônica de servlet | `docs/planos-salvador/00c-errata-correcoes.md` §3 |
| Padrão de sessão/acesso | `docs/planos-salvador/00c-errata-correcoes.md` §1–2; para módulos novos, `ControleAcesso` (`docs/planos-salvador/35-correcoes-seguranca-funcionais.md`, Task 1) |
| Como registrar no menu | `docs/planos-salvador/00c-errata-correcoes.md` §8 |
| Padrão visual (JSP/Bootstrap) | `docs/requisitos/05-padrao-visual.md` |
| Exemplos existentes | `src/main/java/muralha/digital/guarnicao/` (legado); com `ControleAcesso`: `src/main/java/muralha/digital/lote/LoteServlet.java` |

### Quando escrever SQL

| O que ler | Onde está |
|---|---|
| Schema de `veiculo_tempo_real` | `docs/planos-salvador/00c-errata-correcoes.md` §11 |
| Schema de `veiculo_tempo_real_imagem` | `docs/planos-salvador/00c-errata-correcoes.md` §12 |
| Como usar UUID (uniqueidentifier) em Java | `docs/planos-salvador/00c-errata-correcoes.md` §13 |
| Tabela de configurações | `docs/planos-salvador/00c-errata-correcoes.md` §5 e §15 |
| PK de `dbo.sis_usuario` | `docs/planos-salvador/00c-errata-correcoes.md` §6 |
| Estrutura de `dbo.local` (nome do equipamento) | `docs/planos-salvador/00c-errata-correcoes.md` §11 |
| Catálogo completo de tabelas dbo | `docs/banco-de-dados/tabelas/dbo/` |
| Catálogo completo de tabelas muralha | `docs/banco-de-dados/tabelas/muralha/` |

### Quando criar/registrar um job Quartz

| O que ler | Onde está |
|---|---|
| Como registrar jobs | `docs/planos-salvador/00c-errata-correcoes.md` §7 e §14 |
| Arquivo a modificar | `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` |

### Quando precisar de contexto funcional

| O que ler | Onde está |
|---|---|
| Módulos do back-end Muralha | `docs/referencia/modulos-backend-muralha.md` |
| Servlets e endpoints existentes | `docs/referencia/servlets-e-endpoints.md` |
| Telas existentes | `docs/referencia/telas-muralha-digital.md` |
| Menus e permissões | `docs/referencia/menus-e-permissoes.md` |
| Pacotes Java | `docs/referencia/inventario-pacotes-java.md` |
| Correções pendentes de Análise de Dados, KPIs, Lotes e acessos | `docs/referencia/guia-correcoes-analise-kpis-lotes.md` |

---

## Planos Salvador — índice rápido

Todos em `docs/planos-salvador/`. Leia `00-gaps-criticos.md` para entender a ordem de execução.

| Arquivo | O que implementa | Depende de |
|---|---|---|
| `00-gaps-criticos.md` | Plano mestre — ordem de execução e critérios de aceite | — |
| `00b-tabelas-complementares.md` | Migração SQL obrigatória (executar antes de qualquer plano) | — |
| `00c-errata-correcoes.md` | **Leia antes de codificar** — corrige todos os erros dos planos | — |
| `01-log-auditoria.md` | Log de auditoria centralizado (AuditoriaService) | — |
| `02-sha256-integridade.md` | Hash SHA-256 nas imagens recebidas | 00b |
| `03-politica-senhas.md` | Política de senhas: complexidade, bloqueio, expiração | 01 |
| `04-mfa-totp.md` | MFA/TOTP manual via HmacSHA1 | 03 |
| `05-dupla-analise.md` | Dupla análise independente de infrações | 01 |
| `06-obliteracao-imagens.md` | Obliteração de áreas em imagens (LGPD) | 01 |
| `07-sla-latencia.md` | Monitoramento de latência SLA ≤ 4s | 01 |
| `08-painel-assertividade.md` | Painel KPI de assertividade da dupla análise | 05 |
| `09-cpf-matricula.md` | CPF/matrícula no cadastro de usuários | 00b |
| `10-sla-preprocessamento.md` | SLA de 72h para pré-processamento | 01 |
| `11-exportacao-padronizada.md` | Exportação CSV/JSON/XLS/PDF com SHA-256 | — |
| `12-retencao-dados.md` | Retenção e arquivamento mensal | — |
| `13-anonimizacao-placas.md` | View de anonimização de placas | — |
| `14-gestao-lotes.md` | Gestão de lotes de envio | — |
| `15-kpis-configuráveis.md` | KPIs configuráveis via SQL | — |
| `16-exportacao-gis.md` | Exportação GeoJSON/KML e mapa Leaflet | — |
| `17-timelapse.md` | Time-lapse de passagens por equipamento | — |
| `18-disponibilidade.md` | Disponibilidade de equipamentos (uptime) | 07 |
| `19-sensores-externos.md` | Integração Waze/CSV, camada no mapa | — |
| `20-dashboard-builder.md` | Dashboard Builder self-service (entrega 21) | — |
| `21-dashboards-consulta-sql-sem-cube.md` | Camada de consulta SQL própria do Dashboard Builder (remove o Cube Core); sem entrega própria, ver entrega 21 | 20 |
| `22-sistema-chamados.md` | Sistema de chamados (7.2.3/7.2.4); documentação em [`docs/chamados/`](../chamados/) | 01, 11 |
| `23-correcao-documentacao-aderencia.md` | Correção da documentação de aderência (Fase 1 da auditoria de 2026-10-16); matriz canônica em `docs/entregas/itens-edital-x-entregas.md` | — |
| `24-correcoes-pontuais-auditoria.md` | Correções pontuais da auditoria (Fase 2): prioridade de alerta, `AuditoriaService`, IP dos mapas, Waze `jams`, script de `vtr_imagem_complemento` | 23 |
| `25-integridade-sha256-recepcao.md` | Integridade SHA-256 na recepção (Fase 3): conferência com hash de origem por equipamento, `vtr_imagem_integridade`, view de contadores, tela `monitoramento/integridade` | 02, 24 |
| `26-logs-tecnicos-historico-parametros.md` | Logs técnicos (Fase 4): 12 fontes normalizadas em `muralha.digital.logtecnico`, tela `monitoramento/logs-tecnicos`, `parametro_historico` e helper `HistoricoParametros` (use-o ao gravar parâmetros) | 01, 07, 10, 18 |
| `27-score-ocr-revisao-placas.md` | Score de OCR (Fase 5): `vtr_placa_leitura` (N placas, score, origem), fila `vtr_revisao_placa`, `JobOcrRevisao`, `CorrecaoPlacaService` (use-o para qualquer correção de placa: exige motivo e justificativa) | 26 |
| `28-alertas-operacionais.md` | Alertas operacionais (Fase 6): tipos operacionais (estacionamento, congestionamento, bloqueio, falha), tratamentos parametrizáveis, detecção de retenção via `veiculo_sumarizado`, filtros avançados no histórico, CSV | 01, 19 |
| `29-kpis-relatorio-mensal.md` | KPIs obrigatórios + relatório mensal (Fase 7): `kpi_historico`, `KpiSnapshotJob`, metas parametrizáveis, alertas analíticos, `RelatorioGerencialJob`, `RelatorioPreProcMensalJob` (5º dia útil) | 15 |
| `30-paineis-assertividade-preproc.md` | Painéis de assertividade e pré-processamento (Fase 8): auto-refresh, volume/divergência/revisão, alerta <80%, `PreProcessamentoServlet` com 14 métricas de 5.5.3.1 | 08, 10 |
| `31-acabamentos-baixo-custo.md` | Acabamentos de baixo custo (Fase 9): filtros perfil/equipamento + PDF no log de auditoria, 3 camadas GIS (alertas, infrações), inversão de cores em 3 telas de análise | 01, 16 |
| `32-retry-notificacao.md` | Retry de notificações em falha de comunicação (§5.3.5.3) | — |
| `33-timelapse-descarte.md` | Descarte automático de time-lapse (§5.7.7.5-6) | 17 |
| `34-consolidacao-multifonte.md` | Consolidação de dados multifonte (§5.4.1.1) | 19 |
| `23-analises-superset.md` | Plataforma de Análises com Apache Superset (sessões SUP-1 a SUP-14, schema `analise`, SSO via `SsoAnaliseServlet`, carga por `CargaAnaliseJob`); documentação em [`docs/analise/`](../analise/) | — |
| `35-correcoes-seguranca-funcionais.md` | Correções de segurança e funcionais (fases 1 e 2 do [guia de correções](../referencia/guia-correcoes-analise-kpis-lotes.md)): `ControleAcesso` (verificação estrita), `ValidadorSqlKpi`, escape de XSS, Gestão de Lotes (adicionar infrações), ação `aparencia` dos Tipos de Alerta, relatórios gerencial e de pré-processamento, `CargaAnaliseJob` | 15, 23, 28, 29 |

---

## Padrões de código — referência rápida

### Estrutura mínima de servlet
```java
@WebServlet("/MuralhaDigital/NomeDaEntidade")
public class NomeDaEntidadeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(NomeDaEntidadeServlet.class);
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Sessão + direito ao próprio servlet (401/403 em JSON); use exigir(req, resp, acaoDaTela)
        // para exigir o direito da tela, e exigirTela(req, resp) quando o servlet encaminha para JSP.
        if (!ControleAcesso.exigir(req, resp)) return;
        Usuario usuario = ControleAcesso.usuario(req);

        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        // ...
    }
}
```

### Conexão ao banco
```java
Connection conn = null;
PreparedStatement ps = null;
ResultSet rs = null;
try {
    conn = Conexao.getConexao();
    ps = conn.prepareStatement("SELECT ... WHERE col = ?");
    ps.setInt(1, valor);
    rs = ps.executeQuery();
} catch (Exception e) {
    logger.error("Erro", e);
    throw new RuntimeException(e);
} finally {
    if (rs   != null) try { rs.close();   } catch (SQLException ignore) {}
    if (ps   != null) try { ps.close();   } catch (SQLException ignore) {}
    if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
}
```

### Retorno JSON padrão
```java
// Sucesso/falha simples:
resp.getWriter().write(gson.toJson(new Resultado(true, "Operação realizada.")));

// Objeto complexo:
JsonObject obj = new JsonObject();
obj.addProperty("chave", valor);
resp.getWriter().write(gson.toJson(obj));
```

### JOIN para nome do equipamento
```sql
SELECT vtr.placa, vtr.data, l.nome AS local_nome
FROM muralha.veiculo_tempo_real vtr
JOIN dbo.local l ON l.id_local = vtr.id_local AND l.sequencia_local = 1
```

### Passagem com complemento e UUID
```sql
SELECT vtr.id, vtr.placa, vtr.data, c.status_analise, c.latencia_ms
FROM muralha.veiculo_tempo_real vtr
LEFT JOIN muralha.vtr_complemento c ON c.id_vtr = vtr.id
-- Para filtrar por uuid: WHERE vtr.id = CAST(? AS UNIQUEIDENTIFIER)
```

---

## Onde NÃO buscar informações

- **Não use** `muralha.configuracao` — a tabela real é `muralha.config_chave_valor`
- **Não confie** nas colunas `dt_captura_equipamento`, `dt_recepcao_servidor`, `equipamento` em `veiculo_tempo_real` — não existem
- **Não assuma** que PKs de `veiculo_tempo_real*` são `bigint` — são `uniqueidentifier`
- **Não leia** `.setup-gtw/` para entender a stack — leia `docs/requisitos/01-stack-e-ambiente.md`
- **Não execute** DDL/DML direto — crie script em `docs/banco-de-dados/migracoes/`

---

## Onde encontrar exemplos de código Java existente

| O que procurar | Onde está no repositório |
|---|---|
| Servlet com CRUD completo | `src/main/java/muralha/digital/guarnicao/GuarnicaoServlet.java` |
| Tela JSP com Bootstrap 5.3 | `src/main/webapp/muralha-digital/pages/guarnicao/` |
| Job Quartz existente | `src/main/java/com/consilux/servlet/ferramentas/` (classes `Job*`) |
| Registro de job no Agendador | `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` método `verificarJobsGTW()` |
| Utilitário de resultado JSON | `src/main/java/muralha/digital/util/Resultado.java` |
| Constantes de sessão | `src/main/java/com/consilux/infra/SessaoConstantes.java` |
