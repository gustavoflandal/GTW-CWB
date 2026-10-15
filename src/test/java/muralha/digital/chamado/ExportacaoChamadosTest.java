package muralha.digital.chamado;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.util.HashUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.testng.Assert.*;

public class ExportacaoChamadosTest {

    private static final List<String> CHAVES = List.of("protocolo", "titulo");
    private static final List<String> ROTULOS = List.of("Protocolo", "Título");
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 15, 10, 30);

    private static JsonArray linhas(String... titulos) {
        JsonArray arr = new JsonArray();
        int n = 1;
        for (String t : titulos) {
            JsonObject o = new JsonObject();
            o.addProperty("protocolo", GeradorProtocolo.formatar(2026, n++));
            o.addProperty("titulo", t);
            arr.add(o);
        }
        return arr;
    }

    private static String csvTexto(JsonArray linhas) {
        byte[] b = ExportacaoChamados.csv(CHAVES, ROTULOS, linhas, "Chamados", "todos", "joao", AGORA);
        assertEquals(b[0] & 0xFF, 0xEF, "CSV deve comecar com BOM UTF-8");
        return new String(b, 3, b.length - 3, StandardCharsets.UTF_8);
    }

    @Test
    public void rodapeTrazTotalEHashDosDados() {
        JsonArray dados = linhas("Câmera parada", "Lentidão");
        String texto = csvTexto(dados);
        assertTrue(texto.startsWith("# Chamados — todos — 15/10/2026 10:30:00 — joao\r\n"));
        assertTrue(texto.contains("# Total: 2\r\n"));
        String bloco = "Protocolo;Título\r\nCH-2026-000001;Câmera parada\r\nCH-2026-000002;Lentidão\r\n";
        assertTrue(texto.contains(bloco));
        assertTrue(texto.endsWith("# SHA-256: " + HashUtil.sha256Hex(bloco.getBytes(StandardCharsets.UTF_8)) + "\r\n"));
        assertEquals(ExportacaoChamados.hashDados(CHAVES, ROTULOS, dados),
                HashUtil.sha256Hex(bloco.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void escapaSeparadorAspasEQuebraDeLinha() {
        String texto = csvTexto(linhas("a;b \"c\"\nd"));
        assertTrue(texto.contains("CH-2026-000001;\"a;b \"\"c\"\" d\"\r\n"), texto);
    }

    @Test
    public void neutralizaInjecaoDeFormula() {
        String texto = csvTexto(linhas("=HYPERLINK(\"x\")", "+1", "-2", "@SUM(A1)"));
        assertTrue(texto.contains(";\"'=HYPERLINK(\"\"x\"\")\"\r\n"), texto);
        assertTrue(texto.contains(";'+1\r\n"));
        assertTrue(texto.contains(";'-2\r\n"));
        assertTrue(texto.contains(";'@SUM(A1)\r\n"));
    }

    @Test
    public void xlsxTemAbaDeDadosEAbaDeMetadados() throws Exception {
        JsonArray dados = linhas("Câmera parada");
        byte[] b = ExportacaoChamados.xlsx(CHAVES, ROTULOS, dados, "Chamados", "todos", "joao", AGORA);
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(b))) {
            assertEquals(wb.getSheetAt(0).getSheetName(), "Chamados");
            assertEquals(wb.getSheetAt(0).getRow(1).getCell(1).getStringCellValue(), "Câmera parada");
            assertEquals(wb.getSheetAt(1).getSheetName(), "Metadados");
            assertEquals(wb.getSheetAt(1).getRow(5).getCell(1).getStringCellValue(),
                    ExportacaoChamados.hashDados(CHAVES, ROTULOS, dados));
        }
    }
}
