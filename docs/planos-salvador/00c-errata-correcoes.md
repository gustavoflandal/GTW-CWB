# Errata — Correções Obrigatórias antes de Codificar

> Leia este documento **antes** de qualquer plano. As correções aqui substituem o código dos planos 01–19.

---

## 1. Objeto `Usuario` na sessão

**Nos planos:** `req.getSession().getAttribute("usuario")` + reflexão `getId()` / `getLogin()`  
**Correto:**

```java
import com.consilux.model.Usuario;
import com.consilux.infra.SessaoConstantes;

// Chave da sessão é "[usuario]", via constante:
Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);

// Campos reais (mapeados de dbo.sis_usuario):
int    id     = usuario.getIdUsuario();   // coluna: id_usuario
String login  = usuario.getUsuario();     // coluna: usuario
String nome   = usuario.getNome();        // coluna: nome
boolean ativo = usuario.isAtivo();        // coluna: ativo
```

**Substituir em todos os planos:** toda reflexão `.getClass().getMethod("getId").invoke(u)` → `usuario.getIdUsuario()`.  
**Substituir em todos os planos:** `getAttribute("usuario")` → `getAttribute(SessaoConstantes.SESSAO_USUARIO)`.

---

## 2. Padrão de autenticação em servlets

**Nos planos:** `new Acesso(req, resp, true).verificaAcesso()` (sem parâmetro)  
**Correto** (06-padroes-de-codigo.md §1.1):

```java
Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
if (!new Acesso(req, resp, true).verificaAcesso(false)) { return; }
// A partir daqui usuario != null e acesso liberado
```

`verificaAcesso(false)` = sem redirect ao login; responde erro HTTP diretamente.

---

## 3. Estrutura canônica de servlet

**Nos planos:** vários padrões inconsistentes  
**Correto:**

```java
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.consilux.infra.SessaoConstantes;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

@WebServlet("/MuralhaDigital/NomeEntidade")
public class NomeEntidadeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(NomeEntidadeServlet.class);
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
        if (!new Acesso(req, resp, true).verificaAcesso(false)) { return; }

        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");
        try {
            if ("listar".equals(acao)) {
                // ...
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida.");
            }
        } catch (Exception e) {
            logger.error("Erro em NomeEntidadeServlet", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno.");
        }
    }
}
```

**Classe de retorno padrão para operações:**

```java
import muralha.digital.util.Resultado;
// Retorno: {"sucesso": true, "mensagem": "..."}
resp.getWriter().write(gson.toJson(new Resultado(true, "Salvo com sucesso.")));
```

---

## 4. Padrão `Conexao` — try-with-resources

**Nos planos:** mix de `try/finally` manual e try-with-resources incompleto  
**Correto (Java 13, preferência do projeto):**

```java
Connection conn = null;
PreparedStatement ps = null;
ResultSet rs = null;
try {
    conn = Conexao.getConexao();
    ps = conn.prepareStatement("SELECT ... WHERE col = ?");
    ps.setInt(1, valor);
    rs = ps.executeQuery();
    while (rs.next()) { /* mapear */ }
} catch (Exception e) {
    logger.error("Erro na consulta", e);
    throw new RuntimeException(e);
} finally {
    if (rs   != null) try { rs.close();   } catch (SQLException ignore) {}
    if (ps   != null) try { ps.close();   } catch (SQLException ignore) {}
    if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
}
```

`Conexao.getConexao()` retorna `ConnectionAdapter` (DBCP pool). `close()` devolve ao pool. Auto-commit=true por padrão — para transações usar `conn.setAutoCommit(false)` explicitamente.

---

## 5. Tabela de configurações — nome real

**Nos planos:** `muralha.configuracao`  
**Correto:** `muralha.config_chave_valor` (documentado em `docs/banco-de-dados/tabelas/muralha/`)

Substituir **em todos os planos** o uso de `muralha.configuracao` por `muralha.config_chave_valor`.

Verificar colunas reais antes de inserir:
```sql
SELECT TOP 1 * FROM muralha.config_chave_valor;
```

Se a tabela de configuração dos planos não existir, criar como nova:
```sql
-- só se config_chave_valor não tiver coluna adequada:
CREATE TABLE muralha.muralha_digital_config (
    chave    VARCHAR(80) PRIMARY KEY,
    valor    VARCHAR(500) NOT NULL,
    descricao VARCHAR(200) NULL
);
```

---

## 6. Coluna PK de `dbo.sis_usuario`

**Nos planos:** `id` (genérico)  
**Correto:** `id_usuario`

Substituir em todos os SQLs que referenciam a PK de `dbo.sis_usuario`:
- `WHERE id = ?` → `WHERE id_usuario = ?`
- `JOIN ... ON u.id = ...` → `JOIN ... ON u.id_usuario = ...`
- Na tabela complementar: `id_usuario INT PRIMARY KEY` (FK implícita para `dbo.sis_usuario.id_usuario`)

---

## 7. Jobs Quartz — como registrar

**Nos planos:** código genérico de `JobBuilder` sem indicar onde inserir  
**Correto:** adicionar ao servlet `com.consilux.servlet.ferramentas.Agendador` (load-on-startup 4).

Localizar o método de registro (provavelmente `contextInitialized` ou `init`) e adicionar:

```java
// Dentro de Agendador.init() ou similar, após os jobs existentes:
JobDetail novoJob = JobBuilder.newJob(NovoJob.class)
    .withIdentity("novoJob", "muralha")
    .build();
Trigger novoTrigger = TriggerBuilder.newTrigger()
    .withIdentity("novoTrigger", "muralha")
    .withSchedule(/* SimpleSchedule ou CronSchedule */)
    .startNow()
    .build();
scheduler.scheduleJob(novoJob, novoTrigger);
```

Quartz versão: **2.3.2**, RAMJobStore, 4 threads. Jobs não persistem entre reinicializações.

---

## 8. Registrar novas telas no menu

Para cada tela nova criada nos planos, adicionar ao banco:

```sql
-- 1. Registrar a URL como funcionalidade protegida
INSERT INTO dbo.sis_menu (descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
VALUES (
    'Nome da Tela',                                    -- descricao
    '/muralha-digital/pages/modulo/tela/index.jsp',   -- acao (URL exata)
    'U',                                               -- tipo
    1,                                                 -- nivel
    222,                                               -- id_pai_menu (raiz Muralha Digital)
    'Nome da Tela',                                    -- menu
    1,                                                 -- Ativo
    'Muralha-Digital'                                  -- nome_sistema
);

-- 2. Dar acesso ao grupo Administradores (id=11) e Supervisores (id=6)
INSERT INTO dbo.sis_menu_direitos (id_menu, id_grupo)
VALUES (SCOPE_IDENTITY(), 11);
INSERT INTO dbo.sis_menu_direitos (id_menu, id_grupo)
VALUES (SCOPE_IDENTITY(), 6);

-- 3. Aparecer na navbar (opcional — escolher menu pai adequado)
-- Pai sugerido por tipo de tela:
--   Monitoramento = 17,  Mapas = 7,  Dashboards = 1
--   Cadastro/Config = 16,  Análise de Dados = 15
INSERT INTO dbo.sis_menu_infos (id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
VALUES (
    (SELECT id_menu FROM dbo.sis_menu WHERE acao = '/muralha-digital/pages/modulo/tela/index.jsp'),
    'bi-nome-icone',                                   -- Bootstrap Icon
    'Nome da Tela',
    '/muralha-digital/pages/modulo/tela/index.jsp',
    'Descrição detalhada do item de menu',
    99,                                                -- ordenação
    17                                                 -- id do menu pai (Monitoramento, Mapas, etc.)
);
```

---

## 9. Coluna `senha` em `dbo.sis_usuario`

**Nos planos (03):** criamos `senha_hash VARCHAR(64)` na tabela complementar  
**Situação real:** `dbo.sis_usuario.senha` é `CHAR(40)` MD5 sem sal — **usado pelo login existente**.  
A tabela complementar `dbo.sis_usuario_complemento.senha_hash` é a **nova hash SHA-256** para o novo módulo de política de senhas — conviver com a coluna `senha` existente durante a transição. O login legado continua usando `senha` (MD5); o novo fluxo passa a usar `senha_hash` (SHA-256) quando o complemento existir para o usuário.

---

## 10. AuditoriaService — dependência de `SessaoConstantes`

**Nos planos (01):** usamos reflexão para evitar import circular  
**Correto:** importar diretamente `com.consilux.model.Usuario` e `SessaoConstantes` — eles ficam em `com.consilux.*` que é um pacote de infraestrutura sem dependência circular com `muralha.digital.*`.

```java
// Em AuditoriaService.registrar():
Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
int idUsuario   = (usuario != null) ? usuario.getIdUsuario() : 0;
String login    = (usuario != null) ? usuario.getUsuario()   : "sistema";
```

---

## 11. Schema real de `muralha.veiculo_tempo_real`

**Nos planos:** referências a `dt_captura_equipamento`, `dt_recepcao_servidor`, `equipamento` (coluna)  
**Colunas reais** (sem ALTER TABLE):

| Coluna | Tipo | Observação |
|---|---|---|
| `id` | `uniqueidentifier` | PK — **não é bigint** |
| `placa` | `char(7)` | |
| `data` | `datetime` | data/hora da passagem |
| `id_local` | `int` | FK para `dbo.local.id_local` |
| `id_pista` | `tinyint` | |
| `velocidade` | `smallint` | |
| `classificacao` | `char(1)` | |

**Não existem** as colunas `dt_captura_equipamento`, `dt_recepcao_servidor`, `equipamento`.  
Para obter o nome do local/equipamento, fazer JOIN com `dbo.local`:

```sql
SELECT vtr.data, l.nome AS local_nome, l.posicao_lat, l.posicao_lon
FROM muralha.veiculo_tempo_real vtr
JOIN dbo.local l ON l.id_local = vtr.id_local AND l.sequencia_local = 1
```

`dbo.local` colunas relevantes: `id_local` int (PK composta com `sequencia_local` tinyint default 1), `nome` char(100), `posicao_lat` decimal(19,17), `posicao_lon` decimal(19,17).

---

## 12. Schema real de `muralha.veiculo_tempo_real_imagem`

**Nos planos:** referências a `sha256`, `obliterada`, tipo `varbinary(max)`  
**Colunas reais:**

| Coluna | Tipo | Observação |
|---|---|---|
| `id` | `uniqueidentifier` | PK — **não é bigint** |
| `id_veiculo_tempo_real` | `uniqueidentifier` | FK para `veiculo_tempo_real.id` |
| `imagem` | `image` | tipo legado SQL Server — **não é varbinary(max)** |
| `indice_imagem` | `tinyint` | índice da câmera (0, 1, 2, ...) |

**Não existem** as colunas `sha256`, `obliterada`, `id_original`.  
Essas colunas ficam em `muralha.vtr_imagem_complemento` (ver `00b`).

**JDBC com tipo `image`** — leitura funciona igual a `varbinary`:
```java
byte[] bytes = rs.getBytes("imagem");  // funciona com tipo image legado
// Para BufferedImage:
BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
```

---

## 13. PKs uniqueidentifier — como usar em Java (JDBC)

Os PKs de `veiculo_tempo_real` e `veiculo_tempo_real_imagem` são `uniqueidentifier` gerados por `newid()`.  
No Java (JDBC com SQL Server), tratar como `String`:

```java
// Ler UUID de ResultSet:
String uuid = rs.getString("id");  // ex: "550e8400-e29b-41d4-a716-446655440000"

// Usar como parâmetro (com CAST no SQL):
ps = conn.prepareStatement(
    "SELECT * FROM muralha.vtr_complemento WHERE id_vtr = CAST(? AS UNIQUEIDENTIFIER)");
ps.setString(1, uuid);

// Ou usar setObject com Microsoft JDBC:
ps.setObject(1, UUID.fromString(uuid));
```

Nas tabelas complementares (`vtr_complemento`, `vtr_imagem_complemento`), os PKs também são `UNIQUEIDENTIFIER` — usar o mesmo padrão acima.

---

## 14. Agendador Quartz — localização exata do registro de jobs

**Nos planos:** "adicionar em `Agendador.init()` ou similar"  
**Correto:** adicionar ao **método privado `verificarJobsGTW()`** dentro de `com.consilux.servlet.ferramentas.Agendador`:

```java
// Em Agendador.java, dentro do método verificarJobsGTW():
// Após os jobs existentes (reajustaJobEnviaEmailsAlertaMuralha, etc.):

JobDetail novoJob = JobBuilder.newJob(MinhaJob.class)
    .withIdentity("minhaJob", "muralha")
    .build();

Trigger novoTrigger = TriggerBuilder.newTrigger()
    .withIdentity("minhaTrigger", "muralha")
    .startNow()
    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
        .withIntervalInMinutes(5)
        .repeatForever())
    .build();

quartzScheduler.scheduleJob(novoJob, novoTrigger);
```

`quartzScheduler` é o campo estático já inicializado. `verificarJobsGTW()` é chamado no `init()` do servlet (load-on-startup 4). Verificar se job já existe antes de registrar (copiar o padrão `possuiJob(grupo, nome)` dos jobs existentes).

---

## 15. `muralha.configuracao` versus `muralha.config_chave_valor`

**Planos 18 e 19** (e outros) ainda usam `muralha.configuracao` para INSERTs de configuração.  
**Correto:** a tabela existente é `muralha.config_chave_valor` com colunas `chave VARCHAR(100) PK` e `valor VARCHAR(255)`.

```sql
-- Substituir em todos os planos:
INSERT INTO muralha.configuracao (chave, valor, descricao) VALUES (...)
-- Por:
INSERT INTO muralha.config_chave_valor (chave, valor) VALUES (...)
-- (sem coluna descricao — ela não existe em config_chave_valor)
```

---

## Checklist de verificação pré-codificação

- [x] Confirmar colunas reais de `muralha.veiculo_tempo_real` e `muralha.veiculo_tempo_real_imagem`
  — **Confirmado** (ver seções 11 e 12 deste documento)
- [x] Confirmar se `muralha.config_chave_valor` tem colunas `chave` e `valor`
  — **Confirmado** (chave VARCHAR(100) PK, valor VARCHAR(255) — sem coluna descricao)
- [x] Confirmar package e imports de `Resultado`, `SessaoConstantes`, `Acesso`
  — **Confirmado** (ver seções 1, 2, 3 deste documento)
- [x] Localizar servlet `Agendador` para adicionar jobs
  — **Confirmado**: `com.consilux.servlet.ferramentas.Agendador`, método `verificarJobsGTW()` (ver seção 14)
- [x] Confirmar id do nó raiz Muralha Digital em `sis_menu`
  — **Confirmado**: `id_menu = 222`, `nome_sistema = 'Muralha-Digital'`
- [x] Confirmar estrutura de `dbo.local` para obter nome do equipamento via `id_local`
  — **Confirmado**: `JOIN dbo.local l ON l.id_local = vtr.id_local AND l.sequencia_local = 1` → `l.nome char(100)`, `l.posicao_lat`, `l.posicao_lon`
- [x] Confirmar PK type de `veiculo_tempo_real` e `veiculo_tempo_real_imagem`
  — **Confirmado**: ambos `uniqueidentifier` (não bigint) — ver seção 13
- [x] Confirmar tipo da coluna `imagem` em `veiculo_tempo_real_imagem`
  — **Confirmado**: tipo legado `image` (não `varbinary(max)`) — `rs.getBytes("imagem")` funciona
