# Time-lapse de Passagens por Equipamento — Plano de Implementação

> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Exibir sequência animada de imagens de passagens de um mesmo equipamento em um intervalo de tempo, permitindo análise visual de padrões de tráfego; navegação frame-a-frame e controle de velocidade.

**Architecture:** Servlet retorna lista de imagens (IDs + timestamps) de um equipamento/período. Front-end carrega imagens uma a uma via `<img src="/MuralhaDigital/ImagemPassagem?id=X">` e usa `setInterval` para animação. Sem geração de vídeo no servidor — tudo no browser.

**Tech Stack:** Java Servlet · Bootstrap 5.3 · JavaScript puro

## Global Constraints

- Imagens servidas pelo servlet existente de imagem (reutilizar ou criar)
- Máximo de 200 frames por sessão de time-lapse (paginado)
- Sem nova dependência Maven
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/imagem/TimelapseServlet.java` |
| Criar | `src/main/java/muralha/digital/imagem/ImagemPassagemServlet.java` (se não existir) |
| Criar | `src/main/webapp/muralha-digital/pages/monitoramento/timelapse/index.jsp` |

---

### Tarefa 1: ImagemPassagemServlet (servir bytes de imagem)

- [ ] **Verificar se já existe servlet que serve imagens por ID**

```bash
grep -r "imagem\|image/jpeg\|image/png" src/main/java --include="*.java" -l
```

Se já existir, usar o endpoint existente. Se não:

- [ ] **Criar `ImagemPassagemServlet.java`**

```java
// src/main/java/muralha/digital/imagem/ImagemPassagemServlet.java
package muralha.digital.imagem;

import com.consilux.lib.Conexao;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

@WebServlet("/MuralhaDigital/ImagemPassagem")
public class ImagemPassagemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();

        long id = Long.parseLong(req.getParameter("id"));
        // Servir imagem obliterada se existir, senão original
        String sql =
            "SELECT TOP 1 imagem FROM muralha.veiculo_tempo_real_imagem " +
            "WHERE (id=? AND obliterada=0) OR (id_original=? AND obliterada=1) " +
            "ORDER BY obliterada DESC";

        Conexao conn = Conexao.getConexao();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id); ps.setLong(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    byte[] img = rs.getBytes("imagem");
                    // Detectar tipo pelo magic bytes
                    String ct = img.length >= 2 && img[0]==(byte)0xFF && img[1]==(byte)0xD8
                        ? "image/jpeg" : "image/png";
                    resp.setContentType(ct);
                    resp.setHeader("Cache-Control", "private, max-age=3600");
                    OutputStream out = resp.getOutputStream();
                    out.write(img);
                    out.flush();
                } else {
                    resp.sendError(404);
                }
            }
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
    }
}
```

- [ ] **Build e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/java/muralha/digital/imagem/ImagemPassagemServlet.java
git commit -m "Adiciona ImagemPassagemServlet para servir bytes de imagens de passagens

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: TimelapseServlet

- [ ] **Criar `TimelapseServlet.java`**

```java
// src/main/java/muralha/digital/imagem/TimelapseServlet.java
package muralha.digital.imagem;

import com.consilux.lib.Conexao;
import com.google.gson.*;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/MuralhaDigital/Timelapse")
public class TimelapseServlet extends HttpServlet {

    private static final int MAX_FRAMES = 200;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");

        String acao = req.getParameter("acao");
        JsonObject result = new JsonObject();
        Conexao conn = Conexao.getConexao();
        try {
            if ("equipamentos".equals(acao)) {
                // Lista equipamentos disponíveis
                JsonArray equips = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT DISTINCT equipamento FROM muralha.veiculo_tempo_real " +
                        "WHERE equipamento IS NOT NULL ORDER BY equipamento");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) equips.add(rs.getString("equipamento"));
                }
                result.addProperty("ok", true);
                result.add("equipamentos", equips);

            } else if ("frames".equals(acao)) {
                String equipamento = req.getParameter("equipamento");
                String dtInicio    = req.getParameter("dtInicio"); // YYYY-MM-DD HH:mm
                String dtFim       = req.getParameter("dtFim");

                JsonArray frames = new JsonArray();
                String sql =
                    "SELECT TOP " + MAX_FRAMES + " vtr.id, vtri.id id_imagem, " +
                    "  CONVERT(VARCHAR,vtr.dt_captura_equipamento,120) dt_captura, " +
                    "  vtr.placa, vtr.latencia_ms " +
                    "FROM muralha.veiculo_tempo_real vtr " +
                    "JOIN muralha.veiculo_tempo_real_imagem vtri ON vtri.id_veiculo_tempo_real=vtr.id " +
                    "  AND vtri.obliterada=0 " +
                    "WHERE vtr.equipamento=? " +
                    (dtInicio != null ? "  AND vtr.dt_captura_equipamento >= '" + dtInicio + "' " : "") +
                    (dtFim    != null ? "  AND vtr.dt_captura_equipamento <= '" + dtFim    + "' " : "") +
                    "ORDER BY vtr.dt_captura_equipamento";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, equipamento);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            JsonObject frame = new JsonObject();
                            frame.addProperty("id",        rs.getLong("id"));
                            frame.addProperty("idImagem",  rs.getLong("id_imagem"));
                            frame.addProperty("dtCaptura", rs.getString("dt_captura"));
                            frame.addProperty("placa",     rs.getString("placa"));
                            frame.addProperty("latencia",  rs.getInt("latencia_ms"));
                            frames.add(frame);
                        }
                    }
                }
                result.addProperty("ok", true);
                result.addProperty("total", frames.size());
                result.add("frames", frames);

            } else {
                result.addProperty("ok", false); result.addProperty("erro", "acao invalida");
            }
        } catch (Exception e) {
            result.addProperty("ok", false); result.addProperty("erro", e.getMessage());
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }
}
```

- [ ] **Build e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/java/muralha/digital/imagem/TimelapseServlet.java
git commit -m "Adiciona TimelapseServlet para listagem de frames por equipamento e período

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: Tela de time-lapse

- [ ] **Criar `index.jsp`**

```html
<%-- src/main/webapp/muralha-digital/pages/monitoramento/timelapse/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-play-circle me-2"></i>Time-lapse de Passagens</h4>

  <!-- Filtros -->
  <div class="card mb-3">
    <div class="card-body">
      <div class="row g-2 align-items-end">
        <div class="col-md-3">
          <label class="form-label">Equipamento</label>
          <select id="selEquip" class="form-select"></select>
        </div>
        <div class="col-md-3">
          <label class="form-label">Início</label>
          <input type="datetime-local" id="dtInicio" class="form-control">
        </div>
        <div class="col-md-3">
          <label class="form-label">Fim</label>
          <input type="datetime-local" id="dtFim" class="form-control">
        </div>
        <div class="col-md-2">
          <label class="form-label">FPS</label>
          <input type="range" id="fps" class="form-range" min="1" max="10" value="3">
          <small id="lblFps" class="text-muted">3 fps</small>
        </div>
        <div class="col-md-1">
          <button class="btn btn-primary w-100" onclick="carregar()">
            <i class="bi bi-search"></i>
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Player -->
  <div class="row">
    <div class="col-md-8">
      <div class="card">
        <div class="card-body text-center">
          <img id="frameImg" src="" alt="frame"
               style="max-width:100%;max-height:500px;display:none;border-radius:4px;">
          <div id="semImagem" class="text-muted py-5">
            <i class="bi bi-camera-video display-4"></i>
            <p class="mt-2">Selecione um equipamento e período</p>
          </div>
        </div>
        <div class="card-footer">
          <div class="d-flex align-items-center gap-2">
            <button class="btn btn-sm btn-outline-secondary" onclick="anterior()">
              <i class="bi bi-skip-backward"></i>
            </button>
            <button class="btn btn-sm btn-primary" id="btnPlay" onclick="togglePlay()">
              <i class="bi bi-play-fill"></i>
            </button>
            <button class="btn btn-sm btn-outline-secondary" onclick="proximo()">
              <i class="bi bi-skip-forward"></i>
            </button>
            <input type="range" id="slider" class="form-range flex-grow-1" min="0" value="0"
                   oninput="irPara(this.value)">
            <span id="lblFrame" class="text-muted small">0/0</span>
          </div>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-header">Informações do frame</div>
        <div class="card-body">
          <dl class="row mb-0">
            <dt class="col-5">Data/Hora</dt><dd class="col-7" id="infoData">—</dd>
            <dt class="col-5">Placa</dt>     <dd class="col-7" id="infoPlaca">—</dd>
            <dt class="col-5">Latência</dt>  <dd class="col-7" id="infoLatencia">—</dd>
          </dl>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
let frames = [], frameAtual = 0, timer = null, reproduzindo = false;

document.getElementById('fps').addEventListener('input', function() {
  document.getElementById('lblFps').textContent = this.value + ' fps';
  if (reproduzindo) { pararPlay(); iniciarPlay(); }
});

async function carregarEquips() {
  const r = await fetch('/MuralhaDigital/Timelapse?acao=equipamentos');
  const d = await r.json();
  const sel = document.getElementById('selEquip');
  sel.innerHTML = '';
  d.equipamentos.forEach(e => {
    sel.insertAdjacentHTML('beforeend', `<option>${e}</option>`);
  });
}

async function carregar() {
  pararPlay();
  frames = []; frameAtual = 0;
  const equip = document.getElementById('selEquip').value;
  const ini   = document.getElementById('dtInicio').value.replace('T',' ');
  const fim   = document.getElementById('dtFim').value.replace('T',' ');
  const params = new URLSearchParams({ acao:'frames', equipamento:equip,
    ...(ini ? {dtInicio:ini} : {}), ...(fim ? {dtFim:fim} : {}) });
  const r = await fetch('/MuralhaDigital/Timelapse?' + params);
  const d = await r.json();
  if (!d.ok || !d.frames.length) { alert('Nenhum frame encontrado.'); return; }
  frames = d.frames;
  document.getElementById('slider').max = frames.length - 1;
  exibirFrame(0);
}

function exibirFrame(idx) {
  if (!frames.length) return;
  idx = Math.max(0, Math.min(idx, frames.length - 1));
  frameAtual = idx;
  const f = frames[idx];
  const img = document.getElementById('frameImg');
  img.src = '/MuralhaDigital/ImagemPassagem?id=' + f.idImagem;
  img.style.display = '';
  document.getElementById('semImagem').style.display = 'none';
  document.getElementById('slider').value   = idx;
  document.getElementById('lblFrame').textContent = (idx+1) + '/' + frames.length;
  document.getElementById('infoData').textContent     = f.dtCaptura;
  document.getElementById('infoPlaca').textContent    = f.placa || '—';
  document.getElementById('infoLatencia').textContent = (f.latencia || 0) + ' ms';
}

function irPara(v)  { exibirFrame(parseInt(v)); }
function proximo()  { exibirFrame(frameAtual + 1); }
function anterior() { exibirFrame(frameAtual - 1); }

function iniciarPlay() {
  const ms = Math.round(1000 / parseInt(document.getElementById('fps').value));
  timer = setInterval(() => {
    if (frameAtual >= frames.length - 1) { pararPlay(); return; }
    exibirFrame(frameAtual + 1);
  }, ms);
  reproduzindo = true;
  document.getElementById('btnPlay').innerHTML = '<i class="bi bi-pause-fill"></i>';
}

function pararPlay() {
  if (timer) clearInterval(timer);
  timer = null; reproduzindo = false;
  document.getElementById('btnPlay').innerHTML = '<i class="bi bi-play-fill"></i>';
}

function togglePlay() { reproduzindo ? pararPlay() : iniciarPlay(); }

carregarEquips();
</script>
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Acessar `http://localhost:8080/muralha-digital/pages/monitoramento/timelapse/index.jsp`
2. Selecionar equipamento e período com passagens
3. Clicar buscar → frames carregam, slider habilitado
4. Play → imagens avançam automaticamente
5. Controles anterior/próximo funcionam

- [ ] **Commit**

```bash
git add src/main/webapp/muralha-digital/pages/monitoramento/timelapse/index.jsp
git commit -m "Adiciona player de time-lapse de passagens por equipamento

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
