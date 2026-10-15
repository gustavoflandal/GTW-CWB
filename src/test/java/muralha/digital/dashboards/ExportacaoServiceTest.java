package muralha.digital.dashboards;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.List;

public class ExportacaoServiceTest {

    private final ExportacaoService service = new ExportacaoService();

    @Test
    public void csvDeveConterBom() {
        JsonArray dados = criarDados();
        List<String> cabecalhos = Arrays.asList("equipamento", "total");

        byte[] csv = service.exportarCsv(dados, cabecalhos, ";");

        assertNotNull(csv);
        assertTrue(csv.length > 3);
        assertEquals(csv[0] & 0xFF, 0xEF, "BOM byte 1");
        assertEquals(csv[1] & 0xFF, 0xBB, "BOM byte 2");
        assertEquals(csv[2] & 0xFF, 0xBF, "BOM byte 3");
    }

    @Test
    public void csvDeveConterCabecalhoELinhas() {
        JsonArray dados = criarDados();
        List<String> cabecalhos = Arrays.asList("equipamento", "total");

        byte[] csv = service.exportarCsv(dados, cabecalhos, ";");
        String conteudo = new String(csv, java.nio.charset.StandardCharsets.UTF_8);

        assertTrue(conteudo.contains("equipamento;total"), "Cabecalho com separador ;");
        assertTrue(conteudo.contains("Radar A;100"), "Dados da primeira linha");
        assertTrue(conteudo.contains("Radar B;200"), "Dados da segunda linha");
    }

    @Test
    public void csvDeveEscaparValoresComSeparador() {
        JsonArray dados = new JsonArray();
        JsonObject row = new JsonObject();
        row.addProperty("nome", "Radar; especial");
        row.addProperty("total", 50);
        dados.add(row);

        byte[] csv = service.exportarCsv(dados, Arrays.asList("nome", "total"), ";");
        String conteudo = new String(csv, java.nio.charset.StandardCharsets.UTF_8);

        assertTrue(conteudo.contains("\"Radar; especial\""), "Valor com ; deve ser escapado entre aspas");
    }

    @Test
    public void xlsxDeveGerarBytesValidos() {
        JsonArray dados = criarDados();
        List<String> cabecalhos = Arrays.asList("equipamento", "total");
        List<String> rotulos = Arrays.asList("Equipamento", "Total");

        byte[] xlsx = service.exportarXlsx(dados, cabecalhos, rotulos, "Teste");

        assertNotNull(xlsx);
        assertTrue(xlsx.length > 100, "XLSX deve ter conteudo");
        assertEquals(xlsx[0] & 0xFF, 0x50, "PK signature byte 1 (ZIP)");
        assertEquals(xlsx[1] & 0xFF, 0x4B, "PK signature byte 2 (ZIP)");
    }

    @Test
    public void csvVazioDeveConterSoCabecalho() {
        JsonArray dados = new JsonArray();
        List<String> cabecalhos = Arrays.asList("col1", "col2");

        byte[] csv = service.exportarCsv(dados, cabecalhos, ";");
        String conteudo = new String(csv, java.nio.charset.StandardCharsets.UTF_8);

        assertTrue(conteudo.contains("col1;col2"));
        String[] linhas = conteudo.trim().split("\r\n");
        assertEquals(linhas.length, 1, "So cabecalho quando sem dados");
    }

    @Test
    public void zipDeveConterMultiplosArquivos() {
        JsonArray dados1 = criarDados();
        JsonArray dados2 = criarDados();

        java.util.Map<String, JsonArray> wd = new java.util.LinkedHashMap<>();
        wd.put("Widget A", dados1);
        wd.put("Widget B", dados2);

        java.util.Map<String, List<String>> wc = new java.util.LinkedHashMap<>();
        wc.put("Widget A", Arrays.asList("equipamento", "total"));
        wc.put("Widget B", Arrays.asList("equipamento", "total"));

        byte[] zip = service.exportarPainelZip(wd, wc);
        assertNotNull(zip);
        assertTrue(zip.length > 100, "ZIP deve ter conteudo");
        assertEquals(zip[0] & 0xFF, 0x50, "PK signature");
        assertEquals(zip[1] & 0xFF, 0x4B, "PK signature");
    }

    private JsonArray criarDados() {
        JsonArray arr = new JsonArray();
        JsonObject r1 = new JsonObject();
        r1.addProperty("equipamento", "Radar A");
        r1.addProperty("total", 100);
        arr.add(r1);

        JsonObject r2 = new JsonObject();
        r2.addProperty("equipamento", "Radar B");
        r2.addProperty("total", 200);
        arr.add(r2);
        return arr;
    }
}
