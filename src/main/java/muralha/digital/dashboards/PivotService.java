package muralha.digital.dashboards;

import com.google.gson.*;

import java.util.*;

/**
 * Traduz configuracao de pivot para consulta do dashboard e formata resultado pivotado.
 * A agregacao e feita no SQL Server; o pivot e montado aqui, NAO no AG Grid (que e Enterprise/pago).
 */
public class PivotService {

    public JsonObject montarConsultaPivot(JsonObject config) {
        JsonObject result = new JsonObject();

        JsonArray linhas = config.has("linhas") ? config.getAsJsonArray("linhas") : new JsonArray();
        JsonArray colunas = config.has("colunas") ? config.getAsJsonArray("colunas") : new JsonArray();
        JsonArray valores = config.has("valores") ? config.getAsJsonArray("valores") : new JsonArray();

        JsonArray dimensions = new JsonArray();
        for (var el : linhas) dimensions.add(el.getAsString());
        for (var el : colunas) dimensions.add(el.getAsString());

        JsonObject query = new JsonObject();
        query.add("measures", valores);
        query.add("dimensions", dimensions);

        if (config.has("timeDimensions")) {
            query.add("timeDimensions", config.get("timeDimensions"));
        }
        if (config.has("filters")) {
            query.add("filters", config.get("filters"));
        }

        int limite = config.has("limite") ? config.get("limite").getAsInt() : 10000;
        query.addProperty("limit", limite);

        result.addProperty("ok", true);
        result.add("query", query);
        return result;
    }

    public JsonObject pivotar(JsonArray dadosBrutos, JsonObject config) {
        JsonObject result = new JsonObject();

        JsonArray linhas = config.has("linhas") ? config.getAsJsonArray("linhas") : new JsonArray();
        JsonArray colunas = config.has("colunas") ? config.getAsJsonArray("colunas") : new JsonArray();
        JsonArray valores = config.has("valores") ? config.getAsJsonArray("valores") : new JsonArray();
        boolean subtotais = config.has("subtotais") && config.get("subtotais").getAsBoolean();

        List<String> dimLinhas = toStringList(linhas);
        List<String> dimColunas = toStringList(colunas);
        List<String> medidas = toStringList(valores);

        Set<String> valoresColunaOrdenados = new LinkedHashSet<>();
        for (var el : dadosBrutos) {
            JsonObject row = el.getAsJsonObject();
            String chaveCol = buildKey(row, dimColunas);
            valoresColunaOrdenados.add(chaveCol);
        }
        List<String> colHeaders = new ArrayList<>(valoresColunaOrdenados);

        List<String> headerRow = new ArrayList<>(dimLinhas);
        for (String colVal : colHeaders) {
            for (String med : medidas) {
                String label = colVal.isEmpty() ? med : colVal + " | " + med;
                headerRow.add(label);
            }
        }
        if (subtotais && colHeaders.size() > 1) {
            for (String med : medidas) {
                headerRow.add("Total | " + med);
            }
        }

        Map<String, Map<String, Map<String, Double>>> pivotData = new LinkedHashMap<>();

        for (var el : dadosBrutos) {
            JsonObject row = el.getAsJsonObject();
            String rowKey = buildKey(row, dimLinhas);
            String colKey = buildKey(row, dimColunas);

            pivotData.computeIfAbsent(rowKey, k -> new LinkedHashMap<>());
            pivotData.get(rowKey).computeIfAbsent(colKey, k -> new LinkedHashMap<>());

            for (String med : medidas) {
                double val = row.has(med) && !row.get(med).isJsonNull()
                        ? row.get(med).getAsDouble() : 0;
                pivotData.get(rowKey).get(colKey).merge(med, val, Double::sum);
            }
        }

        JsonArray cabecalhos = new JsonArray();
        headerRow.forEach(cabecalhos::add);

        JsonArray rows = new JsonArray();
        Map<String, Double> grandTotals = new LinkedHashMap<>();

        for (var entry : pivotData.entrySet()) {
            JsonObject pivotRow = new JsonObject();
            String[] rowParts = entry.getKey().split("\\|", -1);
            for (int i = 0; i < dimLinhas.size() && i < rowParts.length; i++) {
                pivotRow.addProperty(dimLinhas.get(i), rowParts[i].trim());
            }

            Map<String, Double> rowTotals = new LinkedHashMap<>();
            for (String colVal : colHeaders) {
                Map<String, Double> vals = entry.getValue().getOrDefault(colVal, Collections.emptyMap());
                for (String med : medidas) {
                    String colName = colVal.isEmpty() ? med : colVal + " | " + med;
                    double v = vals.getOrDefault(med, 0.0);
                    pivotRow.addProperty(colName, v);

                    if (subtotais) {
                        rowTotals.merge(med, v, Double::sum);
                        grandTotals.merge(colName, v, Double::sum);
                    }
                }
            }

            if (subtotais && colHeaders.size() > 1) {
                for (String med : medidas) {
                    pivotRow.addProperty("Total | " + med, rowTotals.getOrDefault(med, 0.0));
                    grandTotals.merge("Total | " + med, rowTotals.getOrDefault(med, 0.0), Double::sum);
                }
            }

            rows.add(pivotRow);
        }

        if (subtotais && pivotData.size() > 1) {
            JsonObject totalRow = new JsonObject();
            for (String dim : dimLinhas) {
                totalRow.addProperty(dim, dim.equals(dimLinhas.get(0)) ? "TOTAL" : "");
            }
            totalRow.addProperty("_subtotal", true);
            for (var e : grandTotals.entrySet()) {
                totalRow.addProperty(e.getKey(), e.getValue());
            }
            rows.add(totalRow);
        }

        result.addProperty("ok", true);
        result.add("cabecalhos", cabecalhos);
        result.add("dados", rows);
        result.addProperty("totalLinhas", rows.size());
        return result;
    }

    private String buildKey(JsonObject row, List<String> dims) {
        if (dims.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dims.size(); i++) {
            if (i > 0) sb.append(" | ");
            String d = dims.get(i);
            sb.append(row.has(d) && !row.get(d).isJsonNull() ? row.get(d).getAsString() : "");
        }
        return sb.toString();
    }

    private List<String> toStringList(JsonArray arr) {
        List<String> list = new ArrayList<>();
        for (var el : arr) list.add(el.getAsString());
        return list;
    }
}
