# MFA / TOTP — Plano de Implementação

> **Depende de:** plano `03-politica-senhas.md` concluído.  
> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Adicionar suporte nativo a autenticação multifator via TOTP (RFC 6238 / Google Authenticator) sem nova dependência — implementação manual com `javax.crypto.Mac` (disponível no JDK 13).

**Architecture:** Coluna `totp_secret` em `sis_usuario`. Classe `TotpService` com geração de chave Base32 e validação HMAC-SHA1. Fluxo: login normal → se MFA habilitado → tela de código → validar → criar sessão. Setup via tela dedicada com QR Code em URI `otpauth://`.

**Tech Stack:** Java 13 (`javax.crypto.Mac`, `HmacSHA1`) · Base32 manual · SQL Server · Bootstrap 5.3

## Global Constraints

- TOTP: janela de ±1 período (30s) para tolerância de clock skew
- QR Code: gerado no cliente via biblioteca `qrcode.js` (CDN `cdnjs.cloudflare.com`)
- `totp_secret` criptografado em repouso: XOR com chave em `muralha-digital-config.xml` (simples mas melhor que plaintext)
- MFA é **opt-in** para usuários mas pode ser tornado obrigatório por grupo via `sis_senha_config`
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_mfa_totp.sql` |
| Criar | `src/main/java/muralha/digital/acesso/TotpService.java` |
| Criar | `src/main/webapp/login/mfa_codigo.jsp` |
| Criar | `src/main/webapp/muralha-digital/pages/meu-perfil/configurar-mfa.jsp` |
| Modificar | `src/main/webapp/login/login_action.jsp` |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_mfa_totp.sql
-- ROLLBACK: ALTER TABLE dbo.sis_usuario DROP COLUMN totp_secret, totp_habilitado;
--           DELETE FROM dbo.sis_senha_config WHERE chave = 'mfa_obrigatorio';

ALTER TABLE dbo.sis_usuario ADD
    totp_secret    VARCHAR(200)  NULL,   -- Base32 cifrado; NULL = MFA não configurado
    totp_habilitado BIT          NOT NULL DEFAULT 0;

INSERT INTO dbo.sis_senha_config (chave, valor, descricao)
VALUES ('mfa_obrigatorio', '0', '1=MFA obrigatório para todos os usuários');
```

- [ ] **Executar e verificar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_mfa_totp.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_mfa_totp.sql
git commit -m "Adiciona colunas TOTP e config mfa_obrigatorio

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: TotpService (HMAC-SHA1 manual)

- [ ] **Criar `TotpService.java`**

```java
// src/main/java/muralha/digital/acesso/TotpService.java
package muralha.digital.acesso;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public final class TotpService {

    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int    PERIOD       = 30;   // segundos
    private static final int    DIGITS       = 6;

    private TotpService() {}

    // ── Geração de chave ─────────────────────────────────────────────────────

    /** Gera uma chave Base32 de 20 bytes (160 bits) para uso como seed TOTP. */
    public static String gerarSegredo() {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        return base32Encode(bytes);
    }

    // ── Validação ─────────────────────────────────────────────────────────────

    /**
     * Valida o código TOTP digitado pelo usuário.
     * Aceita o período atual, o anterior e o próximo (janela ±1).
     */
    public static boolean validar(String segredoBase32, String codigoDigitado) {
        if (segredoBase32 == null || codigoDigitado == null) return false;
        long t = System.currentTimeMillis() / 1000L / PERIOD;
        for (long delta = -1; delta <= 1; delta++) {
            String esperado = calcularCodigo(segredoBase32, t + delta);
            if (esperado != null && esperado.equals(codigoDigitado.trim())) return true;
        }
        return false;
    }

    // ── URI para QR Code ─────────────────────────────────────────────────────

    public static String gerarOtpAuthUri(String segredo, String login, String issuer) {
        try {
            String enc = java.net.URLEncoder.encode(login, "UTF-8");
            String iss = java.net.URLEncoder.encode(issuer, "UTF-8");
            return "otpauth://totp/" + iss + ":" + enc +
                   "?secret=" + segredo + "&issuer=" + iss + "&algorithm=SHA1&digits=6&period=30";
        } catch (Exception e) { return ""; }
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private static String calcularCodigo(String segredoBase32, long contagem) {
        try {
            byte[] key = base32Decode(segredoBase32);
            byte[] msg = ByteBuffer.allocate(8).putLong(contagem).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(msg);
            int offset = hash[hash.length - 1] & 0x0F;
            int trunc = ((hash[offset]     & 0x7F) << 24)
                      | ((hash[offset + 1] & 0xFF) << 16)
                      | ((hash[offset + 2] & 0xFF) << 8)
                      | (hash[offset + 3]  & 0xFF);
            int code = trunc % (int) Math.pow(10, DIGITS);
            return String.format("%0" + DIGITS + "d", code);
        } catch (Exception e) { return null; }
    }

    // ── Base32 ────────────────────────────────────────────────────────────────

    public static String base32Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int buffer = 0, bitsLeft = 0;
        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                bitsLeft -= 5;
                sb.append(BASE32_CHARS.charAt((buffer >> bitsLeft) & 0x1F));
            }
        }
        if (bitsLeft > 0) sb.append(BASE32_CHARS.charAt((buffer << (5 - bitsLeft)) & 0x1F));
        return sb.toString();
    }

    private static byte[] base32Decode(String s) {
        s = s.toUpperCase().replaceAll("[^A-Z2-7]", "");
        int outLen = s.length() * 5 / 8;
        byte[] out = new byte[outLen];
        int buffer = 0, bitsLeft = 0, idx = 0;
        for (char c : s.toCharArray()) {
            buffer = (buffer << 5) | BASE32_CHARS.indexOf(c);
            bitsLeft += 5;
            if (bitsLeft >= 8) { bitsLeft -= 8; out[idx++] = (byte)(buffer >> bitsLeft); }
        }
        return out;
    }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Teste rápido de validação** — criar uma JSP de teste temporária, ou verificar manualmente:

```java
// Teste em console (pode colocar num main temporário):
String segredo = TotpService.gerarSegredo();
System.out.println("Segredo: " + segredo);
System.out.println("URI: " + TotpService.gerarOtpAuthUri(segredo, "test@example.com", "GTW"));
// Escanear o QR e digitar o código para validar:
// System.out.println(TotpService.validar(segredo, "CODIGO_DO_APP"));
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/acesso/TotpService.java
git commit -m "Adiciona TotpService com HMAC-SHA1 manual para MFA/TOTP

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: Tela de setup de MFA (perfil do usuário)

- [ ] **Criar `configurar-mfa.jsp`**

```jsp
<%-- src/main/webapp/muralha-digital/pages/meu-perfil/configurar-mfa.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<%
    muralha.digital.acesso.TotpService totpSvc = null; // importar no scriptlet
    String segredo   = muralha.digital.acesso.TotpService.gerarSegredo();
    String login     = ((com.consilux.acesso.Usuario) session.getAttribute("usuario")).getLogin();
    String uri       = muralha.digital.acesso.TotpService.gerarOtpAuthUri(segredo, login, "GTW-Muralha");
    session.setAttribute("totp_setup_segredo", segredo);
%>
<div class="container py-4" style="max-width:480px">
  <h5 class="mb-3"><i class="bi bi-shield-check me-2"></i>Configurar Autenticação em Dois Fatores</h5>
  <p class="text-muted">Escaneie o QR Code com Google Authenticator, Microsoft Authenticator ou Authy.</p>
  <div class="text-center my-3">
    <div id="qrcode"></div>
    <small class="text-muted d-block mt-2">Ou digite manualmente: <code><%= segredo %></code></small>
  </div>
  <div class="mb-3">
    <label class="form-label">Digite o código do app para confirmar</label>
    <input id="totp" class="form-control" maxlength="6" placeholder="000000" autocomplete="off">
  </div>
  <button class="btn btn-primary w-100" onclick="confirmarMFA()">Ativar MFA</button>
  <div id="msgMFA" class="mt-2"></div>
</div>
<script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
<script>
  new QRCode(document.getElementById("qrcode"), { text: "<%= uri %>", width:200, height:200 });
  function confirmarMFA() {
    $.post('/MuralhaDigital/MfaConfig', { acao: 'ativar', codigo: $('#totp').val() }, function(r) {
      if (r.ok) {
        $('#msgMFA').html('<div class="alert alert-success">MFA ativado com sucesso!</div>');
        setTimeout(() => location.href = '/muralha-digital/pages/meu-perfil/', 1500);
      } else {
        $('#msgMFA').html('<div class="alert alert-danger">' + r.erro + '</div>');
      }
    });
  }
</script>
<%@ include file="/muralha-digital/utils/credenciais/rodape.jsp" %>
```

- [ ] **Criar `MfaConfigServlet.java`**

```java
// src/main/java/muralha/digital/acesso/MfaConfigServlet.java
package muralha.digital.acesso;

import com.consilux.lib.Conexao;
import com.google.gson.JsonObject;
import muralha.digital._ini.Acesso;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.PreparedStatement;

@WebServlet("/MuralhaDigital/MfaConfig")
public class MfaConfigServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");
        String acao = req.getParameter("acao");
        JsonObject r = new JsonObject();

        if ("ativar".equals(acao)) {
            String segredo = (String) req.getSession().getAttribute("totp_setup_segredo");
            String codigo  = req.getParameter("codigo");
            if (segredo == null) { r.addProperty("ok", false); r.addProperty("erro", "Sessão expirada."); }
            else if (!TotpService.validar(segredo, codigo)) {
                r.addProperty("ok", false); r.addProperty("erro", "Código inválido. Tente novamente.");
            } else {
                try {
                    Object usuario = req.getSession().getAttribute("usuario");
                    int idUsuario  = (Integer) usuario.getClass().getMethod("getId").invoke(usuario);
                    Conexao conn = Conexao.getConexao();
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE dbo.sis_usuario SET totp_secret=?, totp_habilitado=1 WHERE id=?")) {
                        ps.setString(1, segredo);
                        ps.setInt(2, idUsuario);
                        ps.executeUpdate();
                    } finally { conn.close(); }
                    req.getSession().removeAttribute("totp_setup_segredo");
                    AuditoriaService.registrar(req, "Acesso", "mfa-ativado", String.valueOf(idUsuario), "MFA TOTP ativado");
                    r.addProperty("ok", true);
                } catch (Exception e) { r.addProperty("ok", false); r.addProperty("erro", e.getMessage()); }
            }
        } else { r.addProperty("ok", false); r.addProperty("erro", "acao invalida"); }

        resp.getWriter().print(r.toString());
    }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/acesso/MfaConfigServlet.java
git add src/main/webapp/muralha-digital/pages/meu-perfil/configurar-mfa.jsp
git commit -m "Adiciona tela e servlet de configuração de MFA/TOTP

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 4: Integrar verificação TOTP no fluxo de login

- [ ] **Criar `mfa_codigo.jsp`** (tela intermediária após login)

```jsp
<%-- src/main/webapp/login/mfa_codigo.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Verificação MFA — GTW</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light d-flex align-items-center justify-content-center" style="min-height:100vh">
  <div class="card shadow p-4" style="width:320px">
    <h5 class="text-center mb-3">Código de Verificação</h5>
    <p class="text-muted text-center small">Digite o código de 6 dígitos do seu app autenticador.</p>
    <form method="POST" action="mfa_verificar.jsp">
      <input type="hidden" name="retUrl" value="<%= request.getParameter("retUrl") %>">
      <input name="codigo" class="form-control text-center mb-3" maxlength="6"
             placeholder="000000" autocomplete="off" autofocus>
      <button class="btn btn-primary w-100">Verificar</button>
    </form>
  </div>
</body>
</html>
```

- [ ] **Criar `mfa_verificar.jsp`** (processa o código)

```java
<%-- src/main/webapp/login/mfa_verificar.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%
    String codigo = request.getParameter("codigo");
    String retUrl = request.getParameter("retUrl");

    Object usuarioPendente = session.getAttribute("mfa_pendente_usuario");
    if (usuarioPendente == null) {
        response.sendRedirect("login.jsp"); return;
    }

    String segredo = (String) session.getAttribute("mfa_pendente_segredo");
    if (muralha.digital.acesso.TotpService.validar(segredo, codigo)) {
        // MFA válido — finalizar sessão
        session.removeAttribute("mfa_pendente_usuario");
        session.removeAttribute("mfa_pendente_segredo");
        session.setAttribute("usuario", usuarioPendente);
        // Registrar login completo com MFA
        muralha.digital.auditoria.AuditoriaService.registrar(
            request, "Acesso", "login-mfa-ok", null, "Login com MFA verificado");
        response.sendRedirect(retUrl != null && !retUrl.isEmpty() ? retUrl : "/login/abertura-sistemas.jsp");
    } else {
        response.sendRedirect("mfa_codigo.jsp?erro=Código inválido&retUrl=" +
            java.net.URLEncoder.encode(retUrl != null ? retUrl : "", "UTF-8"));
    }
%>
```

- [ ] **Modificar `login_action.jsp`** — após login bem-sucedido, verificar se MFA habilitado:

```java
<%-- Após validar senha e ANTES de criar a sessão final: --%>
<%
    // Buscar totp_habilitado do usuário
    boolean mfaHabilitado = false;
    String totpSegredo = null;
    // ... (query: SELECT totp_habilitado, totp_secret FROM sis_usuario WHERE id = ?)
    if (mfaHabilitado) {
        // Não criar sessão ainda — guardar temporariamente
        session.setAttribute("mfa_pendente_usuario", usuario);
        session.setAttribute("mfa_pendente_segredo", totpSegredo);
        response.sendRedirect("mfa_codigo.jsp?retUrl=" +
            java.net.URLEncoder.encode(retUrl, "UTF-8"));
        return;
    }
    // Caso MFA não habilitado: criar sessão normalmente
    session.setAttribute("usuario", usuario);
    // ... fluxo existente de redirecionamento
%>
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Teste completo**:
  1. Acessar `configurar-mfa.jsp` → escanear QR Code com app → confirmar código → verificar que `totp_habilitado=1` no banco
  2. Fazer logout → login → deve exibir `mfa_codigo.jsp` → digitar código do app → deve completar login
  3. Digitar código errado → deve exibir erro e solicitar novamente

- [ ] **Commit**

```bash
git add src/main/webapp/login/mfa_codigo.jsp src/main/webapp/login/mfa_verificar.jsp
git add src/main/webapp/login/login_action.jsp
git commit -m "Integra verificação TOTP no fluxo de login para usuários com MFA habilitado

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
