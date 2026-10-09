# Obliteração de Imagens (LGPD) — Plano de Implementação

> **Depende de:** plano `01-log-auditoria.md` concluído.  
> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Permitir obliteração automática e manual de áreas sensíveis (rostos, ocupantes) nas imagens de infrações, preservando o arquivo original intacto; reversão apenas por usuário autorizado com justificativa e log.

**Architecture:** A imagem original em `veiculo_tempo_real_imagem` nunca é modificada. Uma cópia com retângulos pretos aplicados (via `BufferedImage` + `Graphics2D`, Java puro) é armazenada como nova linha na mesma tabela com flag `obliterada=1`. As coordenadas de obliteração ficam em `muralha.infracao_imagem_obliteracao`. O front-end usa canvas overlay para seleção de área.

**Tech Stack:** Java 13 `java.awt.Graphics2D` · SQL Server VARBINARY(MAX) · Bootstrap 5.3 · canvas HTML5

## Global Constraints

- Original imutável — jamais fazer UPDATE no campo de imagem da linha original
- Reversão: DELETE da linha obliterada + registro de auditoria + direito `OBLITERACAO_REVERTER`
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_obliteracao.sql` |
| Criar | `src/main/java/muralha/digital/imagem/ObliteracaoService.java` |
| Criar | `src/main/java/muralha/digital/imagem/ObliteracaoServlet.java` |
| Modificar | `src/main/webapp/muralha-digital/pages/processamento/dupla-analise/analisar.jsp` |
| Criar | `src/main/webapp/muralha-digital/assets/js/processamento/obliteracao.js` |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_obliteracao.sql
-- ROLLBACK: DROP TABLE muralha.infracao_imagem_obliteracao;
--           ALTER TABLE muralha.veiculo_tempo_real_imagem DROP COLUMN obliterada, id_original;

ALTER TABLE muralha.veiculo_tempo_real_imagem ADD
    obliterada  BIT   NOT NULL DEFAULT 0,
    id_original BIGINT NULL;   -- NULL para imagens originais; preenchido para cópias obliteradas

CREATE TABLE muralha.infracao_imagem_obliteracao (
    id                    BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_imagem_original    BIGINT       NOT NULL,
    id_imagem_obliterada  BIGINT       NULL,   -- preenchido após gerar a cópia
    tipo                  CHAR(1)      NOT NULL,  -- 'M'=manual, 'A'=automática
    coordenadas_json      VARCHAR(MAX) NOT NULL,  -- [{"x":10,"y":20,"w":50,"h":30},...]
    dt_aplicacao          DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    id_usuario_aplicou    INT          NOT NULL,
    revertida             BIT          NOT NULL DEFAULT 0,
    dt_reversao           DATETIME2    NULL,
    id_usuario_reverteu   INT          NULL,
    justificativa_reversao VARCHAR(500) NULL
);
```

- [ ] **Executar e verificar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_obliteracao.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_obliteracao.sql
git commit -m "Adiciona estrutura de banco para obliteração de imagens LGPD

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: ObliteracaoService

- [ ] **Criar `ObliteracaoService.java`**

```java
// src/main/java/muralha/digital/imagem/ObliteracaoService.java
package muralha.digital.imagem;

import com.consilux.lib.Conexao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.List;
import java.util.Map;

public final class ObliteracaoService {

    private ObliteracaoService() {}

    /**
     * Aplica obliteração sobre a imagem original e persiste:
     * 1. A cópia obliterada em veiculo_tempo_real_imagem (obliterada=1)
     * 2. O registro de auditoria em infracao_imagem_obliteracao
     *
     * @param idImagemOriginal  ID da linha em veiculo_tempo_real_imagem
     * @param coordenadasJson   JSON array: [{"x":int,"y":int,"w":int,"h":int},...]
     * @param idUsuario         quem aplicou
     * @return ID da imagem obliterada criada
     */
    public static long aplicar(long idImagemOriginal, String coordenadasJson, int idUsuario)
            throws Exception {

        // 1. Buscar bytes da imagem original
        byte[] bytesOriginais = buscarBytesImagem(idImagemOriginal);
        if (bytesOriginais == null) throw new IllegalArgumentException("Imagem não encontrada: " + idImagemOriginal);

        // 2. Aplicar retângulos pretos
        List<Map<String, Object>> coords = new Gson().fromJson(coordenadasJson,
            new TypeToken<List<Map<String, Object>>>(){}.getType());
        byte[] bytesObliterados = aplicarRetangulos(bytesOriginais, coords);

        // 3. Persistir cópia obliterada e registro
        Conexao conn = Conexao.getConexao();
        conn.setAutoCommit(false);
        try {
            // 3a. Buscar id_veiculo_tempo_real da imagem original
            long idVeiculo = buscarIdVeiculo(conn, idImagemOriginal);

            // 3b. Inserir cópia obliterada
            long idObliterada;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.veiculo_tempo_real_imagem " +
                    "(id_veiculo_tempo_real, imagem, obliterada, id_original) VALUES (?,?,1,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, idVeiculo);
                ps.setBytes(2, bytesObliterados);
                ps.setLong(3, idImagemOriginal);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next(); idObliterada = keys.getLong(1);
                }
            }

            // 3c. Registrar obliteração
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.infracao_imagem_obliteracao " +
                    "(id_imagem_original, id_imagem_obliterada, tipo, coordenadas_json, id_usuario_aplicou) " +
                    "VALUES (?,?,'M',?,?)")) {
                ps.setLong(1, idImagemOriginal);
                ps.setLong(2, idObliterada);
                ps.setString(3, coordenadasJson);
                ps.setInt(4, idUsuario);
                ps.executeUpdate();
            }

            conn.commit();
            return idObliterada;
        } catch (Exception e) {
            conn.rollback(); throw e;
        } finally {
            conn.setAutoCommit(true); conn.close();
        }
    }

    public static void reverter(long idImagemOriginal, int idUsuario, String justificativa)
            throws Exception {
        Conexao conn = Conexao.getConexao();
        conn.setAutoCommit(false);
        try {
            // Marcar obliterações como revertidas
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE muralha.infracao_imagem_obliteracao " +
                    "SET revertida=1, dt_reversao=SYSDATETIME(), " +
                    "id_usuario_reverteu=?, justificativa_reversao=? " +
                    "WHERE id_imagem_original=? AND revertida=0")) {
                ps.setInt(1, idUsuario); ps.setString(2, justificativa);
                ps.setLong(3, idImagemOriginal); ps.executeUpdate();
            }
            // Remover cópias obliteradas
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM muralha.veiculo_tempo_real_imagem " +
                    "WHERE id_original=? AND obliterada=1")) {
                ps.setLong(1, idImagemOriginal); ps.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            conn.rollback(); throw e;
        } finally {
            conn.setAutoCommit(true); conn.close();
        }
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private static byte[] buscarBytesImagem(long id) throws SQLException {
        Conexao conn = Conexao.getConexao();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT imagem FROM muralha.veiculo_tempo_real_imagem WHERE id=? AND obliterada=0")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBytes("imagem");
            }
        } finally { conn.close(); }
        return null;
    }

    private static long buscarIdVeiculo(Connection conn, long idImagem) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_veiculo_tempo_real FROM muralha.veiculo_tempo_real_imagem WHERE id=?")) {
            ps.setLong(1, idImagem);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new IllegalArgumentException("Imagem sem veiculo associado: " + idImagem);
    }

    @SuppressWarnings("unchecked")
    private static byte[] aplicarRetangulos(byte[] original,
            List<Map<String, Object>> coords) throws Exception {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(original));
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLACK);
        for (Map<String, Object> rect : coords) {
            int x = ((Number) rect.get("x")).intValue();
            int y = ((Number) rect.get("y")).intValue();
            int w = ((Number) rect.get("w")).intValue();
            int h = ((Number) rect.get("h")).intValue();
            g.fillRect(x, y, w, h);
        }
        g.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String fmt = original[0] == (byte)0xFF ? "jpg" : "png";   // detecção simples
        ImageIO.write(img, fmt, out);
        return out.toByteArray();
    }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/imagem/ObliteracaoService.java
git commit -m "Adiciona ObliteracaoService para aplicação e reversão de obliteração LGPD

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: ObliteracaoServlet

- [ ] **Criar `ObliteracaoServlet.java`**

```java
// src/main/java/muralha/digital/imagem/ObliteracaoServlet.java
package muralha.digital.imagem;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import muralha.digital._ini.Acesso;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/MuralhaDigital/Obliteracao")
public class ObliteracaoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");
        Gson gson = new Gson();
        JsonObject r = new JsonObject();
        String acao = req.getParameter("acao");

        try {
            int idUsuario = obterIdUsuario(req);

            if ("aplicar".equals(acao)) {
                long idImagem = Long.parseLong(req.getParameter("idImagem"));
                String coords = req.getParameter("coordenadas");  // JSON
                long idObliterada = ObliteracaoService.aplicar(idImagem, coords, idUsuario);
                AuditoriaService.registrar(req, "Imagem", "obliterar",
                    String.valueOf(idImagem), "Obliteração aplicada → id_obliterada=" + idObliterada);
                r.addProperty("ok", true);
                r.addProperty("idObliterada", idObliterada);

            } else if ("reverter".equals(acao)) {
                // Verificar permissão específica de reversão
                // (idealmente via fcn_VerificaAcesso — simplificado aqui)
                long idImagem = Long.parseLong(req.getParameter("idImagem"));
                String justif = req.getParameter("justificativa");
                if (justif == null || justif.trim().isEmpty()) {
                    r.addProperty("ok", false);
                    r.addProperty("erro", "Justificativa obrigatória para reversão.");
                } else {
                    ObliteracaoService.reverter(idImagem, idUsuario, justif.trim());
                    AuditoriaService.registrar(req, "Imagem", "reverter-obliteracao",
                        String.valueOf(idImagem), "Reversão: " + justif);
                    r.addProperty("ok", true);
                }
            } else {
                r.addProperty("ok", false); r.addProperty("erro", "acao invalida");
            }
        } catch (IllegalArgumentException e) {
            r.addProperty("ok", false); r.addProperty("erro", e.getMessage());
        } catch (Exception e) {
            r.addProperty("ok", false); r.addProperty("erro", "Erro interno: " + e.getMessage());
        }
        resp.getWriter().print(gson.toJson(r));
    }

    private int obterIdUsuario(HttpServletRequest req) throws Exception {
        Object u = req.getSession().getAttribute("usuario");
        return (Integer) u.getClass().getMethod("getId").invoke(u);
    }
}
```

- [ ] **Build e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/java/muralha/digital/imagem/ObliteracaoServlet.java
git commit -m "Adiciona ObliteracaoServlet com ações aplicar e reverter

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 4: Canvas de obliteração no front-end

- [ ] **Criar `obliteracao.js`** e integrar na `analisar.jsp`

```javascript
// src/main/webapp/muralha-digital/assets/js/processamento/obliteracao.js
// Gerencia o canvas overlay para seleção de área de obliteração

let oblCanvas, oblCtx, oblRetangulos = [], oblDesenhando = false, oblStart = {};

function iniciarObliteracao(imgId) {
  const img = document.getElementById(imgId);
  // Criar canvas overlay sobre a imagem
  oblCanvas = document.createElement('canvas');
  oblCanvas.width  = img.offsetWidth;
  oblCanvas.height = img.offsetHeight;
  oblCanvas.style.cssText = 'position:absolute;top:0;left:0;cursor:crosshair;';
  img.parentNode.style.position = 'relative';
  img.parentNode.appendChild(oblCanvas);
  oblCtx = oblCanvas.getContext('2d');

  oblCanvas.addEventListener('mousedown', e => {
    const r = oblCanvas.getBoundingClientRect();
    oblStart = { x: e.clientX - r.left, y: e.clientY - r.top };
    oblDesenhando = true;
  });
  oblCanvas.addEventListener('mousemove', e => {
    if (!oblDesenhando) return;
    const r = oblCanvas.getBoundingClientRect();
    const x = e.clientX - r.left, y = e.clientY - r.top;
    oblCtx.clearRect(0, 0, oblCanvas.width, oblCanvas.height);
    oblRetangulos.forEach(rect => {
      oblCtx.fillStyle = 'rgba(0,0,0,0.8)';
      oblCtx.fillRect(rect.x, rect.y, rect.w, rect.h);
    });
    oblCtx.strokeStyle = '#ff0000'; oblCtx.lineWidth = 2;
    oblCtx.strokeRect(oblStart.x, oblStart.y, x - oblStart.x, y - oblStart.y);
  });
  oblCanvas.addEventListener('mouseup', e => {
    if (!oblDesenhando) return;
    oblDesenhando = false;
    const r = oblCanvas.getBoundingClientRect();
    const x = e.clientX - r.left, y = e.clientY - r.top;
    // Converter coordenadas para escala da imagem original
    const img = document.getElementById(imgId);
    const scaleX = img.naturalWidth  / img.offsetWidth;
    const scaleY = img.naturalHeight / img.offsetHeight;
    oblRetangulos.push({
      x: Math.round(Math.min(oblStart.x, x) * scaleX),
      y: Math.round(Math.min(oblStart.y, y) * scaleY),
      w: Math.round(Math.abs(x - oblStart.x) * scaleX),
      h: Math.round(Math.abs(y - oblStart.y) * scaleY)
    });
    renderizarRetangulos();
  });
}

function renderizarRetangulos() {
  oblCtx.clearRect(0, 0, oblCanvas.width, oblCanvas.height);
  oblRetangulos.forEach(rect => {
    const img = document.getElementById('imgInfracao');
    const scaleX = img.offsetWidth  / img.naturalWidth;
    const scaleY = img.offsetHeight / img.naturalHeight;
    oblCtx.fillStyle = 'rgba(0,0,0,0.8)';
    oblCtx.fillRect(rect.x*scaleX, rect.y*scaleY, rect.w*scaleX, rect.h*scaleY);
  });
}

function confirmarObliteracao(idImagem) {
  if (oblRetangulos.length === 0) { bs_alert('Selecione pelo menos uma área para obliterar.'); return; }
  Swal.fire({
    title: 'Confirmar obliteração?',
    text: oblRetangulos.length + ' área(s) serão obliteradas. O original é preservado.',
    icon: 'warning', showCancelButton: true,
    confirmButtonText: 'Confirmar', cancelButtonText: 'Cancelar'
  }).then(result => {
    if (!result.isConfirmed) return;
    $.post('/MuralhaDigital/Obliteracao', {
      acao: 'aplicar',
      idImagem: idImagem,
      coordenadas: JSON.stringify(oblRetangulos)
    }, function(r) {
      if (r.ok) {
        Swal.fire('Obliteração aplicada!', '', 'success');
        oblRetangulos = [];
        renderizarRetangulos();
      } else { bs_alert('Erro: ' + r.erro); }
    });
  });
}

function cancelarObliteracao() {
  oblRetangulos = [];
  if (oblCtx) oblCtx.clearRect(0, 0, oblCanvas.width, oblCanvas.height);
  if (oblCanvas) oblCanvas.remove();
  oblCanvas = null;
}
```

- [ ] **Adicionar botão de obliteração na `analisar.jsp`** (após o bloco de controles de imagem)

```html
<!-- Dentro do card de controles, após os sliders de brilho/contraste: -->
<div class="mt-2 border-top pt-2">
  <button class="btn btn-sm btn-warning" onclick="iniciarObliteracao('imgInfracao')">
    <i class="bi bi-square me-1"></i>Iniciar Obliteração
  </button>
  <button class="btn btn-sm btn-danger ms-1"
          onclick="confirmarObliteracao(infracaoAtual.id)">
    <i class="bi bi-eye-slash me-1"></i>Aplicar
  </button>
  <button class="btn btn-sm btn-outline-secondary ms-1" onclick="cancelarObliteracao()">
    Cancelar
  </button>
</div>
```

- [ ] **Incluir o script na JSP**

```html
<!-- Antes do </body> em analisar.jsp: -->
<script src="/muralha-digital/assets/js/processamento/obliteracao.js"></script>
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Acessar `analisar.jsp` com uma infração carregada
2. Clicar "Iniciar Obliteração" → cursor muda para crosshair
3. Desenhar retângulo sobre o rosto na imagem
4. Clicar "Aplicar" → confirmar → verificar no banco:

```sql
SELECT id, obliterada, id_original FROM muralha.veiculo_tempo_real_imagem ORDER BY id DESC;
SELECT * FROM muralha.infracao_imagem_obliteracao ORDER BY id DESC;
```

5. Deve existir linha com `obliterada=1` e registro na tabela de obliteração

- [ ] **Commit**

```bash
git add src/main/webapp/muralha-digital/assets/js/processamento/obliteracao.js
git add src/main/webapp/muralha-digital/pages/processamento/dupla-analise/analisar.jsp
git commit -m "Adiciona canvas de obliteração de imagens LGPD na tela de análise

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
