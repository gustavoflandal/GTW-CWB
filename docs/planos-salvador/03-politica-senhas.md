# Política de Senhas — Plano de Implementação

> **Para agentes:** use `superpowers:executing-plans` para executar tarefa por tarefa.

**Goal:** Implementar política parametrizável de senhas: complexidade mínima, prazo de validade, histórico de reutilização e bloqueio automático após tentativas inválidas.

**Architecture:** Nova tabela `dbo.sis_senha_config` para parâmetros + colunas adicionais em `dbo.sis_usuario`. Classe `SenhaService` com toda a lógica. Integração no fluxo de login (`login_action.jsp`) e na tela de troca de senha (`login_change.jsp`). O MFA fica em plano separado (`04-mfa-totp.md`).

**Tech Stack:** Java 13 · `java.security.MessageDigest` (SHA-256 para hash da senha) · SQL Server · JSP/Bootstrap 5.3

## Global Constraints

- Hash de senha: SHA-256 hex — sem bcrypt (sem nova dependência)
- Validação de complexidade configurada em `dbo.sis_senha_config` (não hardcoded)
- Desbloqueio de usuário: apenas via tela de admin ou expiração automática do `bloqueado_ate`
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_politica_senhas.sql` |
| Criar | `src/main/java/muralha/digital/acesso/SenhaService.java` |
| Modificar | `src/main/webapp/login/login_action.jsp` |
| Modificar | `src/main/webapp/login/login_change.jsp` (ou criar se inexistente) |
| Modificar | `src/main/java/com/consilux/acesso/UsuarioServlet.java` (cadastro de usuário) |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_politica_senhas.sql
-- ROLLBACK:
-- DROP TABLE dbo.sis_senha_config;
-- ALTER TABLE dbo.sis_usuario DROP COLUMN senha_historico, tentativas_invalidas,
--   bloqueado_ate, dt_ultima_troca_senha, fl_troca_obrigatoria;

CREATE TABLE dbo.sis_senha_config (
    chave   VARCHAR(50) NOT NULL PRIMARY KEY,
    valor   VARCHAR(200) NOT NULL,
    descricao VARCHAR(300)
);

INSERT INTO dbo.sis_senha_config (chave, valor, descricao) VALUES
('min_tamanho',        '8',   'Tamanho mínimo da senha'),
('requer_maiuscula',   '1',   '1=sim, 0=não'),
('requer_numero',      '1',   '1=sim, 0=não'),
('requer_especial',    '1',   '1=sim, 0=não — caracteres: !@#$%^&*()_+-='),
('validade_dias',      '90',  'Dias até expiração; 0=nunca expira'),
('historico_qtde',     '5',   'Quantas senhas anteriores não podem ser reutilizadas'),
('max_tentativas',     '5',   'Tentativas inválidas antes do bloqueio'),
('bloqueio_minutos',   '30',  'Minutos de bloqueio; 0=bloqueio permanente (só admin desbloqueia)');

ALTER TABLE dbo.sis_usuario ADD
    senha_hash          VARCHAR(64)   NULL,   -- SHA-256 da senha atual
    senha_historico     VARCHAR(MAX)  NULL,   -- JSON array de hashes anteriores
    tentativas_invalidas TINYINT      NOT NULL DEFAULT 0,
    bloqueado_ate       DATETIME2     NULL,   -- NULL = não bloqueado
    dt_ultima_troca_senha DATE        NULL,
    fl_troca_obrigatoria BIT          NOT NULL DEFAULT 0;
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_politica_senhas.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_politica_senhas.sql
git commit -m "Adiciona tabela sis_senha_config e colunas de política de senhas em sis_usuario

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: SenhaService

- [ ] **Criar `SenhaService.java`**

```java
// src/main/java/muralha/digital/acesso/SenhaService.java
package muralha.digital.acesso;

import com.consilux.lib.Conexao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import muralha.digital.util.HashUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class SenhaService {

    private SenhaService() {}

    // ── Configuração ──────────────────────────────────────────────────────────

    public static int getConfig(String chave, int padrao) {
        Conexao conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT valor FROM dbo.sis_senha_config WHERE chave = ?")) {
                ps.setString(1, chave);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return Integer.parseInt(rs.getString("valor").trim());
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return padrao;
    }

    // ── Validação de complexidade ─────────────────────────────────────────────

    /** Retorna null se válida; mensagem de erro se inválida. */
    public static String validarComplexidade(String senha) {
        if (senha == null) return "Senha não pode ser vazia.";
        int min = getConfig("min_tamanho", 8);
        if (senha.length() < min) return "Senha deve ter pelo menos " + min + " caracteres.";
        if (getConfig("requer_maiuscula", 1) == 1 && !senha.matches(".*[A-Z].*"))
            return "Senha deve conter pelo menos uma letra maiúscula.";
        if (getConfig("requer_numero", 1) == 1 && !senha.matches(".*[0-9].*"))
            return "Senha deve conter pelo menos um número.";
        if (getConfig("requer_especial", 1) == 1 && !senha.matches(".*[!@#$%^&*()_+\\-=].*"))
            return "Senha deve conter pelo menos um caractere especial (!@#$%^&*()_+-=).";
        return null;
    }

    // ── Histórico de reutilização ─────────────────────────────────────────────

    public static boolean jaUsouRecentemente(int idUsuario, String novaSenha) {
        int qtde = getConfig("historico_qtde", 5);
        if (qtde == 0) return false;
        String novoHash = HashUtil.sha256Hex(novaSenha.getBytes());
        Conexao conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT senha_hash, senha_historico FROM dbo.sis_usuario WHERE id = ?")) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String atual = rs.getString("senha_hash");
                        if (novoHash.equals(atual)) return true;
                        String histJson = rs.getString("senha_historico");
                        if (histJson != null && !histJson.isEmpty()) {
                            List<String> hist = new Gson().fromJson(histJson,
                                new TypeToken<List<String>>(){}.getType());
                            int checar = Math.min(qtde - 1, hist.size());
                            for (int i = 0; i < checar; i++) {
                                if (novoHash.equals(hist.get(hist.size() - 1 - i))) return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return false;
    }

    // ── Atualizar senha ───────────────────────────────────────────────────────

    public static void atualizarSenha(int idUsuario, String novaSenha) throws SQLException {
        String novoHash = HashUtil.sha256Hex(novaSenha.getBytes());
        Conexao conn = null;
        try {
            conn = Conexao.getConexao();
            // Ler histórico atual
            String histJson = "[]";
            String hashAtual = null;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT senha_hash, senha_historico FROM dbo.sis_usuario WHERE id = ?")) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        hashAtual = rs.getString("senha_hash");
                        String h = rs.getString("senha_historico");
                        if (h != null && !h.isEmpty()) histJson = h;
                    }
                }
            }
            // Adicionar hash atual ao histórico
            List<String> hist = new Gson().fromJson(histJson,
                new TypeToken<List<String>>(){}.getType());
            if (hashAtual != null) hist.add(hashAtual);
            int max = getConfig("historico_qtde", 5);
            while (hist.size() > max) hist.remove(0);

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dbo.sis_usuario SET senha_hash=?, senha_historico=?, " +
                    "dt_ultima_troca_senha=CAST(SYSDATETIME() AS DATE), fl_troca_obrigatoria=0, " +
                    "tentativas_invalidas=0, bloqueado_ate=NULL WHERE id=?")) {
                ps.setString(1, novoHash);
                ps.setString(2, new Gson().toJson(hist));
                ps.setInt(3, idUsuario);
                ps.executeUpdate();
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
    }

    // ── Verificar bloqueio e tentativas ──────────────────────────────────────

    /** @return null se desbloqueado; mensagem se bloqueado */
    public static String verificarBloqueio(String loginOuId) throws SQLException {
        Conexao conn = null;
        try {
            conn = Conexao.getConexao();
            String col = loginOuId.matches("\\d+") ? "id" : "login";
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT bloqueado_ate, tentativas_invalidas FROM dbo.sis_usuario WHERE " + col + "=?")) {
                ps.setString(1, loginOuId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp bloqueado = rs.getTimestamp("bloqueado_ate");
                        if (bloqueado != null && bloqueado.after(new java.util.Date()))
                            return "Usuário bloqueado até " + bloqueado + ". Contate o administrador.";
                    }
                }
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return null;
    }

    public static void registrarFalha(String login) throws SQLException {
        int max = getConfig("max_tentativas", 5);
        int min = getConfig("bloqueio_minutos", 30);
        Conexao conn = null;
        try {
            conn = Conexao.getConexao();
            String sql = min > 0
                ? "UPDATE dbo.sis_usuario SET tentativas_invalidas = tentativas_invalidas+1, " +
                  "bloqueado_ate = CASE WHEN tentativas_invalidas+1 >= " + max +
                  " THEN DATEADD(MINUTE," + min + ",SYSDATETIME()) ELSE bloqueado_ate END WHERE login=?"
                : "UPDATE dbo.sis_usuario SET tentativas_invalidas = tentativas_invalidas+1, " +
                  "bloqueado_ate = CASE WHEN tentativas_invalidas+1 >= " + max +
                  " THEN '9999-12-31' ELSE bloqueado_ate END WHERE login=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, login);
                ps.executeUpdate();
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
    }

    public static boolean senhaExpirada(int idUsuario) {
        int validade = getConfig("validade_dias", 90);
        if (validade == 0) return false;
        Conexao conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT dt_ultima_troca_senha FROM dbo.sis_usuario WHERE id=?")) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Date dt = rs.getDate("dt_ultima_troca_senha");
                        if (dt == null) return true; // nunca trocou = expirada
                        long dias = (System.currentTimeMillis() - dt.getTime()) / 86400000L;
                        return dias >= validade;
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return false;
    }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/acesso/SenhaService.java
git commit -m "Adiciona SenhaService com política de complexidade, histórico e bloqueio

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: Integrar no fluxo de login

- [ ] **Localizar `login_action.jsp`** (em `src/main/webapp/login/login_action.jsp`)

Encontrar o trecho onde a senha é comparada (`Usuario.comparaSenha` ou equivalente) e adicionar:

```java
<%-- Após buscar o usuário e ANTES de comparar a senha: --%>
<%
String errobloqueio = SenhaService.verificarBloqueio(login);
if (errobloqueio != null) {
    response.sendRedirect("login.jsp?erro=" + java.net.URLEncoder.encode(errobloqueio, "UTF-8"));
    return;
}
%>

<%-- Onde a senha está INCORRETA (bloco de senha inválida): --%>
<%
SenhaService.registrarFalha(login);
response.sendRedirect("login.jsp?erro=Usuário ou senha inválidos.");
return;
%>

<%-- Após login BEM-SUCEDIDO, antes do redirect final: --%>
<%
// Resetar contador de tentativas após login bem-sucedido
// (o atualizarSenha não é chamado aqui, apenas zerar tentativas)
// Verificar se senha expirou
if (SenhaService.senhaExpirada(usuario.getId())) {
    session.setAttribute("trocaSenhaObrigatoria", true);
    response.sendRedirect("login_change.jsp?motivo=expiracao");
    return;
}
%>
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Teste manual**: tentar login com senha incorreta 5 vezes → conta deve bloquear → tentar novamente → mensagem de bloqueio com prazo.

- [ ] **Commit**

```bash
git add src/main/webapp/login/login_action.jsp
git commit -m "Integra verificação de bloqueio e expiração de senha no fluxo de login

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 4: Tela de troca de senha com validação de complexidade

- [ ] **Localizar ou criar `login_change.jsp`**

Adicionar (ou substituir o formulário existente de troca) validação:

```java
<%-- No processamento POST de login_change.jsp: --%>
<%
String novaSenha    = request.getParameter("novaSenha");
String confirma     = request.getParameter("confirma");

if (!novaSenha.equals(confirma)) {
    pageContext.setAttribute("erro", "Senhas não conferem.");
} else {
    String erroComplexidade = SenhaService.validarComplexidade(novaSenha);
    if (erroComplexidade != null) {
        pageContext.setAttribute("erro", erroComplexidade);
    } else if (SenhaService.jaUsouRecentemente(usuario.getId(), novaSenha)) {
        pageContext.setAttribute("erro", "Esta senha já foi usada recentemente. Escolha uma nova.");
    } else {
        SenhaService.atualizarSenha(usuario.getId(), novaSenha);
        AuditoriaService.registrar(request, "Acesso", "troca-senha",
            String.valueOf(usuario.getId()), "Senha atualizada");
        response.sendRedirect("abertura-sistemas.jsp");
        return;
    }
}
%>
```

- [ ] **Build e teste**: login com senha expirada (definir `dt_ultima_troca_senha` = 91 dias atrás no banco) → sistema deve redirecionar para troca → tentar senha fraca → exibir erro de complexidade → digitar senha forte e sem histórico → troca deve funcionar.

```sql
-- Para forçar expiração em teste:
UPDATE dbo.sis_usuario SET dt_ultima_troca_senha = DATEADD(DAY,-91,CAST(SYSDATETIME() AS DATE))
WHERE login = 'seu_usuario_de_teste';
```

- [ ] **Commit**

```bash
git add src/main/webapp/login/login_change.jsp
git commit -m "Adiciona validação de complexidade e histórico na troca de senha

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 5: Admin — desbloquear usuário

- [ ] **Adicionar ação `desbloquear` em `UsuarioServlet`** (já existente em `com.consilux.acesso`)

```java
// Dentro do dispatch de ações do UsuarioServlet, novo caso:
case "desbloquear":
    new Acesso(req, resp, true).verificaAcesso();
    int idAlvo = Integer.parseInt(req.getParameter("id"));
    Conexao conn = Conexao.getConexao();
    try (PreparedStatement ps = conn.prepareStatement(
            "UPDATE dbo.sis_usuario SET tentativas_invalidas=0, bloqueado_ate=NULL WHERE id=?")) {
        ps.setInt(1, idAlvo);
        ps.executeUpdate();
    } finally { conn.close(); }
    AuditoriaService.registrar(req, "Cadastro", "desbloquear-usuario",
        String.valueOf(idAlvo), "Usuário desbloqueado por admin");
    JsonObject ok = new JsonObject();
    ok.addProperty("ok", true);
    resp.getWriter().print(new Gson().toJson(ok));
    break;
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/com/consilux/acesso/UsuarioServlet.java
git commit -m "Adiciona ação desbloquear-usuario no UsuarioServlet

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
