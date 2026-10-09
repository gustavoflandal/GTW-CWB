# Exportação Padronizada (PDF/CSV/XLS) — Plano de Implementação

> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Implementar exportação padronizada de infrações em PDF (relatório formatado), CSV (dados brutos) e XLS (planilha) a partir do painel de gestão de infrações, com cabeçalho institucional e rodapé de integridade.

**Architecture:** `ExportacaoServlet` recebe filtros (período, equipamento, status) e responde com o formato solicitado. PDF via `iText` (já no projeto — verificar; se não: gerar HTML→PDF no cliente com `jsPDF`). CSV via streaming puro (sem biblioteca). XLS via `SheetJS` no cliente (já no projeto). Rodapé de integridade inclui total de registros, data/hora de geração e hash SHA-256 do conteúdo.

**Tech Stack:** Java Servlet · SheetJS (já no projeto) · jsPDF (já no projeto) · SQL Server

## Global Constraints

- Verificar se `iText` ou equivalente já está em `pom.xml` antes de usar; se não estiver, gerar PDF no cliente via `jsPDF`
- CSV: UTF-8 com BOM para compatibilidade com Excel
- Sem nova dependência Maven sem aprovação
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/exportacao/ExportacaoServlet.java` |
| Criar | `src/main/webapp/muralha-digital/assets/js/exportacao/exportacao.js` |
| Modificar | JSP do painel de infrações (adicionar botões de exportação) |

---

### Tarefa 1: ExportacaoServlet (CSV)

- [ ] **Verificar pom.xml para iText**

```bash
grep -i "itext\|openpdf\|pdfbox" pom.xml
```

Se não encontrado, a geração de PDF será feita no cliente (jsPDF).

- [ ] **Criar `ExportacaoServlet.java`**

```java
// src/main/java/muralha/digital/exportacao/ExportacaoServlet.java
package muralha.digital.exportacao;

import com.consilux.lib.Conexao;
import muralha.digital._ini.Acesso;
import muralha.digital.util.HashUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/MuralhaDigital/Exportacao")
public class ExportacaoServlet extends HttpServlet {

    private static final String[] COLUNAS = {
        "id", "placa", "equipamento", "dt_captura_equipamento",
        "dt_recepcao_servidor", "status_analise", "latencia_ms"
    };

    private static final String[] HEADERS_CSV = {
        "ID", "Placa", "Equipamento", "Data Captura",
        "Data Recepção", "Status", "Latência (ms)"
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        new Acesso(req, resp, true).verificaAcesso();

        String formato    = req.getParameter("formato");   // csv, json
        String dtInicio   = req.getParameter("dtInicio");
        String dtFim      = req.getParameter("dtFim");
        String equipamento = req.getParameter("equipamento");
        String status     = req.getParameter("status");

        if ("csv".equals(formato)) {
            exportarCsv(resp, dtInicio, dtFim, equipamento, status);
        } else if ("json".equals(formato)) {
            // JSON puro — o cliente (SheetJS/jsPDF) converte para XLS/PDF
            exportarJson(resp, dtInicio, dtFim, equipamento, status);
        } else {
            resp.sendError(400, "formato invalido: use csv ou json");
        }
    }

    private void exportarCsv(HttpServletResponse resp,
            String dtInicio, String dtFim, String equip, String status)
            throws IOException {

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"infracoes_" + ts + ".csv\"");

        PrintWriter w = new PrintWriter(
            new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8));
        // BOM para Excel
        w.print('﻿');

        // Cabeçalho institucional
        w.println("# GTW-CWB — Relatório de Infrações");
        w.println("# Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        w.println("# Período: " + dtInicio + " a " + dtFim);
        w.println(String.join(";", HEADERS_CSV));

        StringBuilder conteudo = new StringBuilder();
        Conexao conn = Conexao.getConexao();
        int total = 0;
        try {
            String sql = construirSql(dtInicio, dtFim, equip, status);
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String linha = rs.getLong("id") + ";"
                        + escCsv(rs.getString("placa")) + ";"
                        + escCsv(rs.getString("equipamento")) + ";"
                        + escCsv(rs.getString("dt_captura_equipamento")) + ";"
                        + escCsv(rs.getString("dt_recepcao_servidor")) + ";"
                        + escCsv(rs.getString("status_analise")) + ";"
                        + rs.getInt("latencia_ms");
                    conteudo.append(linha).append("\n");
                    w.println(linha);
                    total++;
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }

        // Rodapé de integridade
        String hash = HashUtil.sha256Hex(conteudo.toString().getBytes(StandardCharsets.UTF_8));
        w.println("# Total de registros: " + total);
        w.println("# SHA-256: " + hash);
        w.flush();
    }

    private void exportarJson(HttpServletResponse resp,
            String dtInicio, String dtFim, String equip, String status)
            throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter w = resp.getWriter();
        w.print("{\"headers\":[");
        for (int i = 0; i < HEADERS_CSV.length; i++) {
            if (i > 0) w.print(",");
            w.print("\"" + HEADERS_CSV[i] + "\"");
        }
        w.print("],\"rows\":[");

        Conexao conn = Conexao.getConexao();
        try {
            String sql = construirSql(dtInicio, dtFim, equip, status);
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) w.print(",");
                    w.print("[" + rs.getLong("id")
                        + ",\"" + esc(rs.getString("placa")) + "\""
                        + ",\"" + esc(rs.getString("equipamento")) + "\""
                        + ",\"" + esc(rs.getString("dt_captura_equipamento")) + "\""
                        + ",\"" + esc(rs.getString("dt_recepcao_servidor")) + "\""
                        + ",\"" + esc(rs.getString("status_analise")) + "\""
                        + "," + rs.getInt("latencia_ms") + "]");
                    first = false;
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        w.print("]}");
    }

    private String construirSql(String dtInicio, String dtFim, String equip, String status) {
        StringBuilder sb = new StringBuilder(
            "SELECT id, placa, equipamento, " +
            "  CONVERT(VARCHAR, dt_captura_equipamento, 120) dt_captura_equipamento, " +
            "  CONVERT(VARCHAR, dt_recepcao_servidor, 120) dt_recepcao_servidor, " +
            "  ISNULL(status_analise, 'PENDENTE') status_analise, " +
            "  ISNULL(latencia_ms, 0) latencia_ms " +
            "FROM muralha.veiculo_tempo_real WHERE 1=1");
        if (dtInicio != null && !dtInicio.isEmpty())
            sb.append(" AND CAST(dt_recepcao_servidor AS DATE) >= '").append(dtInicio).append("'");
        if (dtFim != null && !dtFim.isEmpty())
            sb.append(" AND CAST(dt_recepcao_servidor AS DATE) <= '").append(dtFim).append("'");
        if (equip != null && !equip.isEmpty())
            sb.append(" AND equipamento = '").append(equip.replace("'", "''")).append("'");
        if (status != null && !status.isEmpty())
            sb.append(" AND status_analise = '").append(status.replace("'", "''")).append("'");
        sb.append(" ORDER BY id DESC");
        return sb.toString();
    }

    private String escCsv(String s) {
        if (s == null) return "";
        return s.contains(";") || s.contains("\"") ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }
    private String esc(String s) { return s == null ? "" : s.replace("\\","\\\\").replace("\"","\\\""); }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/exportacao/ExportacaoServlet.java
git commit -m "Adiciona ExportacaoServlet com suporte CSV e JSON para exportação de infrações

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: Exportação XLS e PDF no cliente

- [ ] **Criar `exportacao.js`**

```javascript
// src/main/webapp/muralha-digital/assets/js/exportacao/exportacao.js
// Depende de SheetJS (XLSX) e jsPDF já presentes no projeto

async function exportarXls(filtros) {
  const params = new URLSearchParams({ formato: 'json', ...filtros });
  const r = await fetch('/MuralhaDigital/Exportacao?' + params);
  const d = await r.json();

  const wsData = [d.headers, ...d.rows];
  const wb = XLSX.utils.book_new();
  const ws = XLSX.utils.aoa_to_sheet(wsData);
  XLSX.utils.book_append_sheet(wb, ws, 'Infrações');
  XLSX.writeFile(wb, 'infracoes_' + new Date().toISOString().substring(0,10) + '.xlsx');
}

async function exportarPdf(filtros) {
  const params = new URLSearchParams({ formato: 'json', ...filtros });
  const r = await fetch('/MuralhaDigital/Exportacao?' + params);
  const d = await r.json();

  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({ orientation: 'landscape' });

  doc.setFontSize(14);
  doc.text('GTW-CWB — Relatório de Infrações', 14, 15);
  doc.setFontSize(9);
  doc.text('Gerado em: ' + new Date().toLocaleString('pt-BR'), 14, 22);

  // Tabela simples
  let y = 32;
  doc.setFontSize(8);
  doc.setFont(undefined, 'bold');
  const colWidths = [15, 25, 40, 40, 40, 28, 22];
  d.headers.forEach((h, i) => {
    doc.text(h, 14 + colWidths.slice(0,i).reduce((a,b)=>a+b,0), y);
  });
  doc.setFont(undefined, 'normal');
  y += 6;

  d.rows.forEach(row => {
    if (y > 185) { doc.addPage(); y = 20; }
    row.forEach((cell, i) => {
      doc.text(String(cell ?? ''), 14 + colWidths.slice(0,i).reduce((a,b)=>a+b,0), y, { maxWidth: colWidths[i] - 1 });
    });
    y += 5;
  });

  doc.setFontSize(7);
  doc.text('Total: ' + d.rows.length + ' registros', 14, y + 5);
  doc.save('infracoes_' + new Date().toISOString().substring(0,10) + '.pdf');
}

function exportarCsv(filtros) {
  const params = new URLSearchParams({ formato: 'csv', ...filtros });
  window.location.href = '/MuralhaDigital/Exportacao?' + params;
}
```

- [ ] **Adicionar botões de exportação na JSP do painel de infrações**

```html
<!-- Botões de exportação — adicionar na barra de ações do painel de infrações -->
<div class="btn-group">
  <button class="btn btn-sm btn-outline-secondary" onclick="exportarCsv(obterFiltros())">
    <i class="bi bi-filetype-csv me-1"></i>CSV
  </button>
  <button class="btn btn-sm btn-outline-success" onclick="exportarXls(obterFiltros())">
    <i class="bi bi-file-earmark-excel me-1"></i>XLS
  </button>
  <button class="btn btn-sm btn-outline-danger" onclick="exportarPdf(obterFiltros())">
    <i class="bi bi-file-earmark-pdf me-1"></i>PDF
  </button>
</div>

<!-- No <script> da JSP, definir obterFiltros(): -->
<script>
function obterFiltros() {
  return {
    dtInicio:    document.getElementById('dtInicio')?.value   || '',
    dtFim:       document.getElementById('dtFim')?.value      || '',
    equipamento: document.getElementById('selEquip')?.value   || '',
    status:      document.getElementById('selStatus')?.value  || ''
  };
}
</script>
<!-- Incluir exportacao.js antes de </body>: -->
<script src="/muralha-digital/assets/js/exportacao/exportacao.js"></script>
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Acessar painel de infrações
2. Clicar CSV → arquivo baixado com BOM e cabeçalho institucional
3. Clicar XLS → planilha abre corretamente no Excel com cabeçalhos
4. Clicar PDF → PDF com título e tabela de dados

- [ ] **Commit**

```bash
git add src/main/webapp/muralha-digital/assets/js/exportacao/exportacao.js
git add src/main/webapp/   # JSP modificada
git commit -m "Adiciona exportação XLS e PDF no cliente e botões de exportação no painel

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
