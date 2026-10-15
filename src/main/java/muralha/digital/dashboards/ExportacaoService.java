package muralha.digital.dashboards;

import com.google.gson.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportacaoService {

    private static final int LIMITE_CSV = 50_000;
    private static final int LIMITE_XLSX = 100_000;
    private static final byte[] BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    public byte[] exportarCsv(JsonArray dados, List<String> cabecalhos, String separador) {
        if (separador == null || separador.isEmpty()) separador = ";";
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write(BOM);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cabecalhos.size(); i++) {
                if (i > 0) sb.append(separador);
                sb.append(escaparCsv(cabecalhos.get(i)));
            }
            sb.append("\r\n");

            int limite = Math.min(dados.size(), LIMITE_CSV);
            for (int r = 0; r < limite; r++) {
                JsonObject row = dados.get(r).getAsJsonObject();
                for (int c = 0; c < cabecalhos.size(); c++) {
                    if (c > 0) sb.append(separador);
                    String key = cabecalhos.get(c);
                    String val = row.has(key) && !row.get(key).isJsonNull()
                            ? row.get(key).getAsString() : "";
                    sb.append(escaparCsv(val));
                }
                sb.append("\r\n");
            }

            if (dados.size() > LIMITE_CSV) {
                sb.append("\r\n# Exportacao truncada: ").append(LIMITE_CSV)
                  .append(" de ").append(dados.size()).append(" linhas.\r\n");
            }

            baos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar CSV", e);
        }
    }

    public byte[] exportarXlsx(JsonArray dados, List<String> cabecalhos,
                                List<String> rotulos, String nomeAba) {
        if (nomeAba == null || nomeAba.isEmpty()) nomeAba = "Dados";
        if (rotulos == null || rotulos.size() != cabecalhos.size()) rotulos = cabecalhos;

        try (SXSSFWorkbook wb = new SXSSFWorkbook(200)) {
            Sheet sheet = wb.createSheet(nomeAba);

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle numberStyle = wb.createCellStyle();
            DataFormat fmt = wb.createDataFormat();
            numberStyle.setDataFormat(fmt.getFormat("#,##0.00"));

            CellStyle intStyle = wb.createCellStyle();
            intStyle.setDataFormat(fmt.getFormat("#,##0"));

            Row headerRow = sheet.createRow(0);
            for (int c = 0; c < rotulos.size(); c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(rotulos.get(c));
                cell.setCellStyle(headerStyle);
            }

            int limite = Math.min(dados.size(), LIMITE_XLSX);
            for (int r = 0; r < limite; r++) {
                Row row = sheet.createRow(r + 1);
                JsonObject obj = dados.get(r).getAsJsonObject();
                for (int c = 0; c < cabecalhos.size(); c++) {
                    Cell cell = row.createCell(c);
                    String key = cabecalhos.get(c);
                    if (!obj.has(key) || obj.get(key).isJsonNull()) {
                        cell.setCellValue("");
                        continue;
                    }

                    JsonElement el = obj.get(key);
                    if (el.isJsonPrimitive()) {
                        JsonPrimitive prim = el.getAsJsonPrimitive();
                        if (prim.isNumber()) {
                            double val = prim.getAsDouble();
                            cell.setCellValue(val);
                            if (val == Math.floor(val) && !Double.isInfinite(val)) {
                                cell.setCellStyle(intStyle);
                            } else {
                                cell.setCellStyle(numberStyle);
                            }
                        } else {
                            cell.setCellValue(prim.getAsString());
                        }
                    } else {
                        cell.setCellValue(el.toString());
                    }
                }
            }

            if (dados.size() > LIMITE_XLSX) {
                Row infoRow = sheet.createRow(limite + 2);
                Cell infoCell = infoRow.createCell(0);
                infoCell.setCellValue("Exportacao truncada: " + LIMITE_XLSX +
                        " de " + dados.size() + " linhas.");
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                wb.write(baos);
                return baos.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar XLSX", e);
        }
    }

    public byte[] exportarPainelZip(Map<String, JsonArray> widgetsDados,
                                      Map<String, List<String>> widgetsCabecalhos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (var entry : widgetsDados.entrySet()) {
                String nome = sanitizarNomeArquivo(entry.getKey()) + ".csv";
                List<String> cabecalhos = widgetsCabecalhos.getOrDefault(entry.getKey(), List.of());
                byte[] csv = exportarCsv(entry.getValue(), cabecalhos, ";");

                zos.putNextEntry(new ZipEntry(nome));
                zos.write(csv);
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar ZIP", e);
        }
    }

    private String escaparCsv(String valor) {
        if (valor == null) return "";
        if (valor.contains(";") || valor.contains("\"") || valor.contains("\n") || valor.contains("\r")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    private String sanitizarNomeArquivo(String nome) {
        return nome.replaceAll("[^a-zA-Z0-9_\\-\\s]", "")
                   .replaceAll("\\s+", "_")
                   .substring(0, Math.min(nome.length(), 60));
    }
}
