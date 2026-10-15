package muralha.digital.chamado;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muralha.digital.util.HashUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Exportacao de chamados com SHA-256 dos dados (spec §3.6). */
public final class ExportacaoChamados {

    public static final List<String> LISTA_CHAVES = List.of("protocolo", "titulo", "categoria", "equipamento",
            "prioridade", "status", "solicitante", "responsavel", "abertura", "prazoAtendimento", "prazoResolucao",
            "slaAtendimento", "slaResolucao", "resolucao", "fechamento", "nota");
    public static final List<String> LISTA_ROTULOS = List.of("Protocolo", "Título", "Categoria", "Equipamento",
            "Prioridade", "Status", "Solicitante", "Responsável", "Abertura", "Prazo de atendimento",
            "Prazo de resolução", "SLA de atendimento", "SLA de resolução", "Resolução", "Fechamento", "Nota");

    public static final List<String> INDICADORES_CHAVES = List.of("grupo", "total", "resolvidos", "cancelados",
            "pctSlaAtendimento", "pctSlaResolucao", "tmaMin", "tmrMin", "notaMedia", "pctAvaliados");
    public static final List<String> INDICADORES_ROTULOS = List.of("Grupo", "Total", "Resolvidos", "Cancelados",
            "% SLA atendimento", "% SLA resolução", "TMA (min)", "TMR (min)", "Nota média", "% avaliados");

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final byte[] BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    private ExportacaoChamados() {
    }

    public static String hashDados(List<String> chaves, List<String> rotulos, JsonArray linhas) {
        return HashUtil.sha256Hex(blocoDados(chaves, rotulos, linhas).getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] csv(List<String> chaves, List<String> rotulos, JsonArray linhas,
                             String titulo, String filtros, String usuario, LocalDateTime agora) {
        String bloco = blocoDados(chaves, rotulos, linhas);
        String texto = "# " + umaLinha(titulo) + " — " + umaLinha(filtros) + " — " + agora.format(FMT) + " — "
                + umaLinha(usuario) + "\r\n"
                + bloco
                + "# Total: " + linhas.size() + "\r\n"
                + "# SHA-256: " + HashUtil.sha256Hex(bloco.getBytes(StandardCharsets.UTF_8)) + "\r\n";
        byte[] corpo = texto.getBytes(StandardCharsets.UTF_8);
        byte[] saida = new byte[BOM.length + corpo.length];
        System.arraycopy(BOM, 0, saida, 0, BOM.length);
        System.arraycopy(corpo, 0, saida, BOM.length, corpo.length);
        return saida;
    }

    public static byte[] xlsx(List<String> chaves, List<String> rotulos, JsonArray linhas,
                              String titulo, String filtros, String usuario, LocalDateTime agora) throws IOException {
        try (SXSSFWorkbook wb = new SXSSFWorkbook(200); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle negrito = wb.createCellStyle();
            Font fonte = wb.createFont();
            fonte.setBold(true);
            negrito.setFont(fonte);

            Sheet dados = wb.createSheet("Chamados");
            Row cab = dados.createRow(0);
            for (int c = 0; c < rotulos.size(); c++) {
                cab.createCell(c).setCellValue(rotulos.get(c));
                cab.getCell(c).setCellStyle(negrito);
            }
            for (int r = 0; r < linhas.size(); r++) {
                JsonObject o = linhas.get(r).getAsJsonObject();
                Row row = dados.createRow(r + 1);
                for (int c = 0; c < chaves.size(); c++) {
                    row.createCell(c).setCellValue(valor(o, chaves.get(c)));
                }
            }

            Sheet meta = wb.createSheet("Metadados");
            String[][] pares = {
                    {"Relatório", umaLinha(titulo)},
                    {"Filtros", umaLinha(filtros)},
                    {"Gerado em", agora.format(FMT)},
                    {"Usuário", umaLinha(usuario)},
                    {"Total de registros", String.valueOf(linhas.size())},
                    {"SHA-256 dos dados", hashDados(chaves, rotulos, linhas)}};
            for (int i = 0; i < pares.length; i++) {
                Row row = meta.createRow(i);
                row.createCell(0).setCellValue(pares[i][0]);
                row.getCell(0).setCellStyle(negrito);
                row.createCell(1).setCellValue(pares[i][1]);
            }
            wb.write(out);
            wb.dispose();
            return out.toByteArray();
        }
    }

    private static String blocoDados(List<String> chaves, List<String> rotulos, JsonArray linhas) {
        StringBuilder sb = new StringBuilder();
        sb.append(linhaCsv(rotulos));
        for (JsonElement e : linhas) {
            JsonObject o = e.getAsJsonObject();
            String[] valores = new String[chaves.size()];
            for (int c = 0; c < chaves.size(); c++) valores[c] = valor(o, chaves.get(c));
            sb.append(linhaCsv(List.of(valores)));
        }
        return sb.toString();
    }

    private static String linhaCsv(List<String> valores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) sb.append(';');
            sb.append(celula(valores.get(i)));
        }
        return sb.append("\r\n").toString();
    }

    /** Texto de cabecalho/metadado nao pode abrir nova linha (injecao de linha/formula). */
    private static String umaLinha(String v) {
        return v == null ? "" : v.replace("\r", " ").replace("\n", " ");
    }

    private static String celula(String v) {
        String s = v == null ? "" : v.replace("\r", " ").replace("\n", " ");
        if (!s.isEmpty() && "=+-@".indexOf(s.charAt(0)) >= 0) {
            s = "'" + s;   // impede que o Excel interprete como formula
        }
        if (s.contains(";") || s.contains("\"")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String valor(JsonObject o, String chave) {
        return o.has(chave) && !o.get(chave).isJsonNull() ? o.get(chave).getAsString() : "";
    }
}
