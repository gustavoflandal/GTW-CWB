package muralha.digital.dashboards.consulta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Consulta recebida do navegador (formato measures/dimensions/timeDimensions/filters/order/limit),
 * com os nomes de campo normalizados para o nome_alias do dataset.
 */
public final class ConsultaDashboard {

    public static final int LIMITE_MAXIMO = 10000;
    public static final int MAX_MEMBROS = 50;
    public static final int MAX_FILTROS = 50;

    static final Set<String> GRANULARIDADES = Set.of("hour", "day", "week", "month", "quarter", "year");

    private static final Pattern PREFIXO_DATASET = Pattern.compile("^dataset_\\d+\\..+");

    /** Nome como veio do navegador (usado nas chaves da resposta) e alias resolvido. */
    public static final class Membro {
        public final String original;
        public final String alias;

        Membro(String original, String alias) {
            this.original = original;
            this.alias = alias;
        }
    }

    public static final class Tempo {
        public final Membro membro;
        public final String granularidade;
        public final JsonElement periodo;

        Tempo(Membro membro, String granularidade, JsonElement periodo) {
            this.membro = membro;
            this.granularidade = granularidade;
            this.periodo = periodo;
        }
    }

    public static final class Filtro {
        public final Membro membro;
        public final String operador;
        public final List<String> valores;

        Filtro(Membro membro, String operador, List<String> valores) {
            this.membro = membro;
            this.operador = operador;
            this.valores = valores;
        }
    }

    public static final class Ordem {
        public final Membro membro;
        public final boolean decrescente;

        Ordem(Membro membro, boolean decrescente) {
            this.membro = membro;
            this.decrescente = decrescente;
        }
    }

    public final List<Membro> medidas;
    public final List<Membro> dimensoes;
    public final List<Tempo> tempos;
    public final List<Filtro> filtros;
    public final List<Ordem> ordem;
    public final int limite;

    private ConsultaDashboard(List<Membro> medidas, List<Membro> dimensoes, List<Tempo> tempos,
                              List<Filtro> filtros, List<Ordem> ordem, int limite) {
        this.medidas = Collections.unmodifiableList(medidas);
        this.dimensoes = Collections.unmodifiableList(dimensoes);
        this.tempos = Collections.unmodifiableList(tempos);
        this.filtros = Collections.unmodifiableList(filtros);
        this.ordem = Collections.unmodifiableList(ordem);
        this.limite = limite;
    }

    public static ConsultaDashboard deJson(JsonObject q, int idDataset) {
        if (q == null) {
            throw new ErroConsulta("Consulta ausente");
        }
        String prefixo = "dataset_" + idDataset + ".";

        if (q.has("segments") && q.get("segments").isJsonArray() && q.getAsJsonArray("segments").size() > 0) {
            throw new ErroConsulta("Segmentos nao sao suportados");
        }
        if (q.has("ungrouped") && q.get("ungrouped").isJsonPrimitive() && q.get("ungrouped").getAsBoolean()) {
            throw new ErroConsulta("Consulta sem agrupamento nao e suportada");
        }
        if (q.has("offset") && q.get("offset").isJsonPrimitive() && q.get("offset").getAsInt() > 0) {
            throw new ErroConsulta("Paginacao por offset nao e suportada");
        }

        List<Membro> medidas = lerMembros(q, "measures", prefixo);
        List<Membro> dimensoes = lerMembros(q, "dimensions", prefixo);

        List<Tempo> tempos = new ArrayList<>();
        for (JsonElement el : lerArray(q, "timeDimensions")) {
            JsonObject td = comoObjeto(el, "timeDimensions");
            Membro m = membro(texto(td, "dimension", "timeDimensions"), prefixo);
            String gran = null;
            if (td.has("granularity") && !td.get("granularity").isJsonNull()) {
                gran = td.get("granularity").getAsString().trim().toLowerCase(Locale.ROOT);
                if (!GRANULARIDADES.contains(gran)) {
                    throw new ErroConsulta("Granularidade nao suportada: " + gran);
                }
            }
            JsonElement periodo = td.has("dateRange") ? td.get("dateRange") : null;
            tempos.add(new Tempo(m, gran, periodo));
        }

        List<Filtro> filtros = new ArrayList<>();
        for (JsonElement el : lerArray(q, "filters")) {
            JsonObject f = comoObjeto(el, "filters");
            if (f.has("or") || f.has("and")) {
                throw new ErroConsulta("Grupos de filtro OR/AND nao sao suportados");
            }
            Membro m = membro(texto(f, "member", "filters"), prefixo);
            String op = texto(f, "operator", "filters");
            List<String> valores = new ArrayList<>();
            if (f.has("values") && f.get("values").isJsonArray()) {
                for (JsonElement v : f.getAsJsonArray("values")) {
                    if (v.isJsonNull()) continue;
                    if (!v.isJsonPrimitive()) {
                        throw new ErroConsulta("Valor de filtro invalido para " + m.original);
                    }
                    valores.add(v.getAsString());
                }
            }
            filtros.add(new Filtro(m, op, valores));
        }
        if (filtros.size() > MAX_FILTROS) {
            throw new ErroConsulta("Muitos filtros (maximo " + MAX_FILTROS + ")");
        }

        List<Ordem> ordem = new ArrayList<>();
        if (q.has("order") && !q.get("order").isJsonNull()) {
            JsonElement o = q.get("order");
            if (o.isJsonObject()) {
                for (Map.Entry<String, JsonElement> e : o.getAsJsonObject().entrySet()) {
                    ordem.add(new Ordem(membro(e.getKey(), prefixo), direcao(e.getValue().getAsString())));
                }
            } else if (o.isJsonArray()) {
                for (JsonElement par : o.getAsJsonArray()) {
                    if (!par.isJsonArray() || par.getAsJsonArray().size() != 2) {
                        throw new ErroConsulta("Ordenacao invalida");
                    }
                    JsonArray p = par.getAsJsonArray();
                    ordem.add(new Ordem(membro(p.get(0).getAsString(), prefixo), direcao(p.get(1).getAsString())));
                }
            } else {
                throw new ErroConsulta("Ordenacao invalida");
            }
        }

        int limite = LIMITE_MAXIMO;
        if (q.has("limit") && q.get("limit").isJsonPrimitive()) {
            int l = q.get("limit").getAsInt();
            if (l > 0) limite = Math.min(l, LIMITE_MAXIMO);
        }

        if (medidas.size() + dimensoes.size() + tempos.size() > MAX_MEMBROS) {
            throw new ErroConsulta("Muitos campos na consulta (maximo " + MAX_MEMBROS + ")");
        }

        return new ConsultaDashboard(medidas, dimensoes, tempos, filtros, ordem, limite);
    }

    private static List<Membro> lerMembros(JsonObject q, String chave, String prefixo) {
        List<Membro> lista = new ArrayList<>();
        for (JsonElement el : lerArray(q, chave)) {
            if (!el.isJsonPrimitive()) {
                throw new ErroConsulta("Campo invalido em " + chave);
            }
            lista.add(membro(el.getAsString(), prefixo));
        }
        return lista;
    }

    private static JsonArray lerArray(JsonObject q, String chave) {
        if (!q.has(chave) || q.get(chave).isJsonNull()) {
            return new JsonArray();
        }
        if (!q.get(chave).isJsonArray()) {
            throw new ErroConsulta("'" + chave + "' deve ser uma lista");
        }
        return q.getAsJsonArray(chave);
    }

    private static JsonObject comoObjeto(JsonElement el, String chave) {
        if (!el.isJsonObject()) {
            throw new ErroConsulta("Item invalido em " + chave);
        }
        return el.getAsJsonObject();
    }

    private static String texto(JsonObject o, String chave, String contexto) {
        if (!o.has(chave) || o.get(chave).isJsonNull() || !o.get(chave).isJsonPrimitive()) {
            throw new ErroConsulta("'" + chave + "' ausente em " + contexto);
        }
        return o.get(chave).getAsString();
    }

    static Membro membro(String nome, String prefixo) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ErroConsulta("Nome de campo vazio");
        }
        String original = nome.trim();
        if (original.length() > 200) {
            throw new ErroConsulta("Nome de campo muito longo");
        }
        String alias = original;
        if (original.startsWith(prefixo)) {
            alias = original.substring(prefixo.length());
        } else if (PREFIXO_DATASET.matcher(original).matches()) {
            throw new ErroConsulta("Campo de outro dataset: " + original);
        }
        if (alias.isEmpty() || alias.contains(".")) {
            throw new ErroConsulta("Campo invalido: " + original);
        }
        return new Membro(original, alias);
    }

    private static boolean direcao(String dir) {
        String d = dir == null ? "" : dir.trim().toLowerCase(Locale.ROOT);
        if ("asc".equals(d)) return false;
        if ("desc".equals(d)) return true;
        throw new ErroConsulta("Direcao de ordenacao invalida: " + dir);
    }
}
