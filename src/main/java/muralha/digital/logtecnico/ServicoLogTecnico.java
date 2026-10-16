package muralha.digital.logtecnico;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Repositório central de logs técnicos (TR 5.4.7): consulta as fontes em
 * {@link FontesLogTecnico}, normaliza e junta em ordem de data. A paginação é feita
 * buscando as N primeiras linhas de cada fonte e intercalando, por isso há um limite
 * de profundidade ({@link #MAX_PROFUNDIDADE}).
 */
public class ServicoLogTecnico {

    private static final Logger logger = Logger.getLogger(ServicoLogTecnico.class);

    /** Maior posição alcançável pela paginação (página × tamanho). */
    public static final int MAX_PROFUNDIDADE = 10_000;

    private final Connection conn;
    private final ParametrosLogTecnico parametros;
    private final List<FonteLog> fontes;
    private final List<String> fontesAusentes = new ArrayList<>();

    public ServicoLogTecnico(Connection conn, ParametrosLogTecnico parametros) throws SQLException {
        this(conn, parametros, FontesLogTecnico.todas());
    }

    ServicoLogTecnico(Connection conn, ParametrosLogTecnico parametros, List<FonteLog> candidatas) throws SQLException {
        this.conn = conn;
        this.parametros = parametros;
        this.fontes = new ArrayList<>();
        for (FonteLog f : candidatas) {
            if (tabelaExiste(f.tabela())) fontes.add(f);
            else fontesAusentes.add(f.origem());
        }
    }

    /** Fontes cuja tabela não existe neste banco (ex.: migração não executada). */
    public List<String> getFontesAusentes() { return fontesAusentes; }

    public ParametrosLogTecnico getParametros() { return parametros; }

    /** Página de registros, do mais recente para o mais antigo. */
    public Pagina listar(FiltroLogTecnico filtro, int pagina, int tamanho) throws SQLException {
        int profundidade = pagina * tamanho;
        if (profundidade > MAX_PROFUNDIDADE) {
            throw new IllegalArgumentException("Página muito distante: refine os filtros ou exporte em CSV.");
        }
        FiltroLogTecnico f = resolverLocalidades(filtro);
        if (f == null) return new Pagina(0, new ArrayList<>());
        long total = 0;
        for (FonteLog fonte : fontes) {
            FonteLog.Consulta c = fonte.consulta(f, parametros);
            if (c != null) total += contar(c);
        }
        List<RegistroLogTecnico> todos = buscar(f, profundidade, false);
        int de = Math.min((pagina - 1) * tamanho, todos.size());
        int ate = Math.min(de + tamanho, todos.size());
        return new Pagina(total, new ArrayList<>(todos.subList(de, ate)));
    }

    /**
     * Até {@code limite} registros que atendem ao filtro, ordenados por data
     * (crescente ou decrescente).
     */
    public List<RegistroLogTecnico> buscar(FiltroLogTecnico filtro, int limite, boolean crescente) throws SQLException {
        FiltroLogTecnico f = resolverLocalidades(filtro);
        List<RegistroLogTecnico> todos = new ArrayList<>();
        if (f == null) return todos;
        for (FonteLog fonte : fontes) {
            FonteLog.Consulta c = fonte.consulta(f, parametros);
            if (c == null) continue;
            String sql = "SELECT TOP (" + limite + ") * FROM (" + c.sql + ") x ORDER BY x.data_hora " + (crescente ? "ASC" : "DESC");
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                definir(ps, c.parametros);
                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData md = rs.getMetaData();
                    while (rs.next()) {
                        Map<String, Object> linha = new HashMap<>();
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            linha.put(md.getColumnLabel(i).toLowerCase(Locale.ROOT), rs.getObject(i));
                        }
                        todos.add(fonte.normalizar(linha));
                    }
                }
            } catch (SQLException e) {
                logger.error("Falha ao consultar a fonte de log " + fonte.origem(), e);
                throw e;
            }
        }
        ordenar(todos, crescente);
        return todos.size() > limite ? new ArrayList<>(todos.subList(0, limite)) : todos;
    }

    /** Ordena por data (nulos no fim) e, no empate, pela referência, para a página ser estável. */
    static void ordenar(List<RegistroLogTecnico> lista, boolean crescente) {
        Comparator<RegistroLogTecnico> porData = Comparator.comparing(RegistroLogTecnico::getDataHora,
            Comparator.nullsFirst(Comparator.naturalOrder()));
        Comparator<RegistroLogTecnico> c = (crescente ? porData : porData.reversed())
            .thenComparing(RegistroLogTecnico::getIdReferencia, Comparator.nullsLast(Comparator.naturalOrder()));
        lista.sort(c);
    }

    /**
     * Converte localidades em equipamentos. Devolve nulo quando o filtro de localidade
     * não encontra nenhum equipamento (resultado vazio).
     */
    FiltroLogTecnico resolverLocalidades(FiltroLogTecnico f) throws SQLException {
        if (f.getIdLocalidades().isEmpty()) return f;
        Set<Integer> daLocalidade = new LinkedHashSet<>();
        FonteLog.Where w = new FonteLog.Where().emLista("id_localidade", f.getIdLocalidades());
        FonteLog.Consulta c = w.montar("SELECT DISTINCT id_local FROM local_vigente");
        try (PreparedStatement ps = conn.prepareStatement(c.sql)) {
            definir(ps, c.parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) daLocalidade.add(rs.getInt(1));
            }
        }
        if (!f.getIdLocais().isEmpty()) daLocalidade.retainAll(f.getIdLocais());
        return daLocalidade.isEmpty() ? null : f.comEquipamentos(daLocalidade);
    }

    /** Nome de cada equipamento vigente (id_local → "série - nome"). */
    public Map<Integer, String> nomesEquipamentos() throws SQLException {
        Map<Integer, String> r = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_local, RTRIM(CAST(serie_equipamento AS VARCHAR(20))) AS serie, RTRIM(nome) AS nome FROM local_vigente");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) r.put(rs.getInt("id_local"), rs.getString("serie") + " - " + rs.getString("nome"));
        }
        return r;
    }

    private long contar(FonteLog.Consulta c) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT_BIG(*) FROM (" + c.sql + ") x")) {
            definir(ps, c.parametros);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    private boolean tabelaExiste(String tabela) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT OBJECT_ID(?, 'U')")) {
            ps.setString(1, tabela);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getObject(1) != null;
            }
        }
    }

    static void definir(PreparedStatement ps, List<Object> parametros) throws SQLException {
        int i = 1;
        for (Object p : parametros) ps.setObject(i++, p);
    }

    /** Resultado paginado. */
    public static final class Pagina {
        public final long total;
        public final List<RegistroLogTecnico> registros;

        Pagina(long total, List<RegistroLogTecnico> registros) {
            this.total = total;
            this.registros = registros;
        }
    }
}
