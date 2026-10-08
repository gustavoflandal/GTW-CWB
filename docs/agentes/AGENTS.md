# Guia para Subagentes — GTW-CWB

> **Leia este documento antes de qualquer outra coisa.** Ele mapeia onde cada tipo de informação está e o que você precisa ler dependendo do que vai fazer.

---

## Orientação geral

Você está trabalhando no sistema **GTW-CWB** (Consilux Tecnologia): sistema legado Java/JSP sobre Tomcat 9 + SQL Server 2016, banco `GTW_MURALHA_DEV` em 10.0.0.200.

**Regras que nunca mudam (CLAUDE.md):**
- Não alterar `.setup-gtw/`, `pom.xml`, nem adicionar dependências externas
- Sem `ALTER TABLE` em tabelas existentes — usar tabelas complementares
- Todo código novo: verificar sessão (`SessaoConstantes.SESSAO_USUARIO`) + acesso (`Acesso.verificaAcesso(false)`)
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
| Padrões de código | `docs/06-padroes-de-codigo.md` | Sessão, acesso, conexão, SQL |
| Stack e ambiente | `docs/01-stack-e-ambiente.md` | Como fazer build/run/debug |

### Quando criar um servlet novo

| O que ler | Onde está |
|---|---|
| Estrutura canônica de servlet | `docs/planos-salvador/00c-errata-correcoes.md` §3 |
| Padrão de sessão/acesso | `docs/planos-salvador/00c-errata-correcoes.md` §1–2 |
| Como registrar no menu | `docs/planos-salvador/00c-errata-correcoes.md` §8 |
| Padrão visual (JSP/Bootstrap) | `docs/05-padrao-visual.md` |
| Exemplos existentes | `src/main/java/muralha/digital/guarnicao/` |

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
        Usuario usuario = (Usuario) req.getSession()
            .getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(401); return; }
        if (!new Acesso(req, resp, true).verificaAcesso(false)) { return; }

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
- **Não leia** `.setup-gtw/` para entender a stack — leia `docs/01-stack-e-ambiente.md`
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
