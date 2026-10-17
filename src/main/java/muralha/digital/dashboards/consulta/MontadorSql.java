package muralha.digital.dashboards.consulta;

import muralha.digital.dashboards.GramaticaCampoCalculado;
import muralha.digital.dashboards.consulta.DatasetResolvido.Campo;
import muralha.digital.dashboards.consulta.DatasetResolvido.Tabela;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Monta um SELECT parametrizado a partir de uma ConsultaDashboard e dos metadados do dataset.
 * Identificadores vem somente do catalogo (validados e entre colchetes); valores sempre como parametros.
 */
public final class MontadorSql {

    private static final Pattern IDENTIFICADOR = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");
    private static final int MAX_VALORES_IN = 500;
    private static final GramaticaCampoCalculado GRAMATICA = new GramaticaCampoCalculado();

    private static final Set<String> PARTES_DATA = Set.of(
            "YEAR", "YYYY", "YY", "QUARTER", "QQ", "MONTH", "MM", "DAYOFYEAR", "DY", "DAY", "DD",
            "WEEK", "WK", "WW", "WEEKDAY", "DW", "HOUR", "HH", "MINUTE", "MI", "SECOND", "SS");

    private static final Set<String> AGREGACOES_MULTIPLICAVEIS = Set.of("SUM", "AVG", "COUNT");

    enum Operador { IGUAL, DIFERENTE, MAIOR, MAIOR_IGUAL, MENOR, MENOR_IGUAL, EM, FORA_DE, ENTRE,
        CONTEM, NAO_CONTEM, NULO, NAO_NULO }

    private static final Map<String, Operador> OPERADORES = new HashMap<>();

    static {
        OPERADORES.put("=", Operador.IGUAL);
        OPERADORES.put("EQUALS", Operador.IGUAL);
        OPERADORES.put("!=", Operador.DIFERENTE);
        OPERADORES.put("<>", Operador.DIFERENTE);
        OPERADORES.put("NOTEQUALS", Operador.DIFERENTE);
        OPERADORES.put(">", Operador.MAIOR);
        OPERADORES.put("GT", Operador.MAIOR);
        OPERADORES.put("AFTERDATE", Operador.MAIOR);
        OPERADORES.put(">=", Operador.MAIOR_IGUAL);
        OPERADORES.put("GTE", Operador.MAIOR_IGUAL);
        OPERADORES.put("<", Operador.MENOR);
        OPERADORES.put("LT", Operador.MENOR);
        OPERADORES.put("BEFOREDATE", Operador.MENOR);
        OPERADORES.put("<=", Operador.MENOR_IGUAL);
        OPERADORES.put("LTE", Operador.MENOR_IGUAL);
        OPERADORES.put("IN", Operador.EM);
        OPERADORES.put("NOT IN", Operador.FORA_DE);
        OPERADORES.put("BETWEEN", Operador.ENTRE);
        OPERADORES.put("INDATERANGE", Operador.ENTRE);
        OPERADORES.put("LIKE", Operador.CONTEM);
        OPERADORES.put("CONTAINS", Operador.CONTEM);
        OPERADORES.put("NOT LIKE", Operador.NAO_CONTEM);
        OPERADORES.put("NOTCONTAINS", Operador.NAO_CONTEM);
        OPERADORES.put("IS NULL", Operador.NULO);
        OPERADORES.put("NOTSET", Operador.NULO);
        OPERADORES.put("IS NOT NULL", Operador.NAO_NULO);
        OPERADORES.put("SET", Operador.NAO_NULO);
    }

    private final PeriodoRelativo periodo;

    public MontadorSql(PeriodoRelativo periodo) {
        this.periodo = periodo;
    }

    // =====================================================================
    // API
    // =====================================================================

    public SqlMontado montar(DatasetResolvido ds, ConsultaDashboard q) {
        Contexto ctx = new Contexto(ds);

        List<Item> itens = new ArrayList<>();
        List<SqlMontado.ColunaSaida> saida = new ArrayList<>();
        Map<String, String> colunaPorMembro = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> incluidos = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        List<Expr> agregadas = new ArrayList<>();
        String primeiraTempo = null;
        String primeiraAgregada = null;

        for (ConsultaDashboard.Tempo t : q.tempos) {
            if (t.granularidade == null) continue;
            String chave = t.membro.original + "." + t.granularidade;
            if (!incluidos.add(chave)) continue;
            Expr e = ctx.usar(ctx.campo(t.membro.alias));
            exigirTempo(e);
            String col = "c" + itens.size();
            itens.add(new Item(col, e, t.granularidade, false));
            saida.add(new SqlMontado.ColunaSaida(col, List.of(chave, t.membro.original), "tempo",
                    e.rotulo, t.granularidade));
            registrar(colunaPorMembro, t.membro, col);
            if (primeiraTempo == null) primeiraTempo = col;
        }

        for (ConsultaDashboard.Membro m : q.dimensoes) {
            if (!incluidos.add(m.original)) continue;
            Expr e = ctx.usar(ctx.campo(m.alias));
            if (e.campo.agregado()) {
                throw new ErroConsulta("O campo '" + m.alias + "' e uma medida; use-o em medidas");
            }
            String col = "c" + itens.size();
            itens.add(new Item(col, e, null, false));
            saida.add(new SqlMontado.ColunaSaida(col, List.of(m.original), "dimensao", e.rotulo, null));
            registrar(colunaPorMembro, m, col);
        }

        for (ConsultaDashboard.Membro m : q.medidas) {
            if (!incluidos.add(m.original)) continue;
            Expr e = ctx.usar(ctx.campo(m.alias));
            String col = "c" + itens.size();
            if (e.campo.agregado()) {
                agregar(e);
                itens.add(new Item(col, e, null, true));
                agregadas.add(e);
                if (primeiraAgregada == null) primeiraAgregada = col;
            } else if ("medida".equals(e.campo.tipoCampo)) {
                itens.add(new Item(col, e, null, false));
            } else {
                throw new ErroConsulta("O campo '" + m.alias + "' e uma dimensao; use-o em dimensoes");
            }
            saida.add(new SqlMontado.ColunaSaida(col, List.of(m.original), "medida", e.rotulo, null));
            registrar(colunaPorMembro, m, col);
        }

        if (itens.isEmpty()) {
            throw new ErroConsulta("Informe ao menos uma medida ou dimensao");
        }

        List<Condicao> condicoes = new ArrayList<>();
        List<FiltroAgregado> filtrosAgregados = new ArrayList<>();

        for (DatasetResolvido.FiltroFixo f : ds.filtrosFixos()) {
            Expr e = ctx.usar(ctx.colunaFiltroFixo(f.coluna));
            Operador op = operador(f.operador);
            String nome = "filtro fixo em '" + f.coluna + "'";
            condicoes.add(criarCondicao(e, op, expandir(op, Collections.singletonList(f.valor)), nome));
        }

        for (ConsultaDashboard.Tempo t : q.tempos) {
            PeriodoRelativo.Intervalo iv = periodo.resolver(t.periodo);
            if (iv == null) continue;
            Expr e = ctx.usar(ctx.campo(t.membro.alias));
            exigirTempo(e);
            condicoes.add(new Condicao(e.sql + " >= ?", List.of(Timestamp.valueOf(iv.inicio)), e.tabelas));
            condicoes.add(new Condicao(e.sql + (iv.fimInclusivo ? " <= ?" : " < ?"),
                    List.of(Timestamp.valueOf(iv.fim)), e.tabelas));
        }

        for (ConsultaDashboard.Filtro f : q.filtros) {
            Expr e = ctx.usar(ctx.campo(f.membro.alias));
            Operador op = operador(f.operador);
            List<String> valores = expandir(op, f.valores);
            if (e.campo.agregado()) {
                condicao(new ArrayList<>(), new ArrayList<>(), agregar(e), "numerico", op, valores, f.membro.alias);
                filtrosAgregados.add(new FiltroAgregado(e, op, valores, f.membro.alias));
                agregadas.add(e);
            } else {
                condicoes.add(criarCondicao(e, op, valores, f.membro.alias));
            }
        }

        List<Juncao> juncoes = ctx.juncoesNecessarias();
        verificarMultiplicacao(ds, juncoes, agregadas);

        List<String> orderBy = new ArrayList<>();
        for (ConsultaDashboard.Ordem o : q.ordem) {
            String col = colunaPorMembro.get(o.membro.original);
            if (col == null) col = colunaPorMembro.get(o.membro.alias);
            if (col == null) {
                throw new ErroConsulta("Ordenacao por campo que nao esta na consulta: " + o.membro.original);
            }
            orderBy.add("[" + col + "]" + (o.decrescente ? " DESC" : " ASC"));
        }
        if (orderBy.isEmpty()) {
            if (primeiraTempo != null) orderBy.add("[" + primeiraTempo + "] ASC");
            else if (primeiraAgregada != null) orderBy.add("[" + primeiraAgregada + "] DESC");
            else orderBy.add("[c0] ASC");
        }

        Montagem m = new Montagem(ctx, ds, q.limite, itens, condicoes, filtrosAgregados, juncoes, orderBy, saida);
        return podePreAgregar(m) ? montarPreAgregado(m) : montarDireto(m);
    }

    private static final class Item {
        final String col;
        final Expr expr;
        final String granularidade;
        final boolean agregado;

        Item(String col, Expr expr, String granularidade, boolean agregado) {
            this.col = col;
            this.expr = expr;
            this.granularidade = granularidade;
            this.agregado = agregado;
        }

        String sqlAgrupamento() {
            return granularidade == null ? expr.sql : granularidade(granularidade, expr.sql);
        }
    }

    private static final class Condicao {
        final String sql;
        final List<Object> parametros;
        final Set<Integer> tabelas;

        Condicao(String sql, List<Object> parametros, Set<Integer> tabelas) {
            this.sql = sql;
            this.parametros = parametros;
            this.tabelas = tabelas;
        }
    }

    private static final class FiltroAgregado {
        final Expr expr;
        final Operador operador;
        final List<String> valores;
        final String nome;

        FiltroAgregado(Expr expr, Operador operador, List<String> valores, String nome) {
            this.expr = expr;
            this.operador = operador;
            this.valores = valores;
            this.nome = nome;
        }
    }

    private static final class Montagem {
        final Contexto ctx;
        final DatasetResolvido ds;
        final int limite;
        final List<Item> itens;
        final List<Condicao> condicoes;
        final List<FiltroAgregado> filtrosAgregados;
        final List<Juncao> juncoes;
        final List<String> orderBy;
        final List<SqlMontado.ColunaSaida> saida;

        Montagem(Contexto ctx, DatasetResolvido ds, int limite, List<Item> itens, List<Condicao> condicoes,
                 List<FiltroAgregado> filtrosAgregados, List<Juncao> juncoes, List<String> orderBy,
                 List<SqlMontado.ColunaSaida> saida) {
            this.ctx = ctx;
            this.ds = ds;
            this.limite = limite;
            this.itens = itens;
            this.condicoes = condicoes;
            this.filtrosAgregados = filtrosAgregados;
            this.juncoes = juncoes;
            this.orderBy = orderBy;
            this.saida = saida;
        }
    }

    private static Condicao criarCondicao(Expr e, Operador op, List<String> valores, String nome) {
        List<String> sql = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        condicao(sql, params, e.sql, e.tipo, op, valores, nome);
        return new Condicao(sql.get(0), params, e.tabelas);
    }

    private SqlMontado montarDireto(Montagem m) {
        List<String> select = new ArrayList<>();
        List<String> groupBy = new ArrayList<>();
        for (Item it : m.itens) {
            if (it.agregado) {
                select.add(agregar(it.expr) + " AS [" + it.col + "]");
            } else {
                String e = it.sqlAgrupamento();
                select.add(e + " AS [" + it.col + "]");
                groupBy.add(e);
            }
        }
        List<String> having = new ArrayList<>();
        List<Object> parametrosHaving = new ArrayList<>();
        for (FiltroAgregado f : m.filtrosAgregados) {
            condicao(having, parametrosHaving, agregar(f.expr), "numerico", f.operador, f.valores, f.nome);
        }

        List<Object> parametros = new ArrayList<>();
        parametros.add(m.limite);
        StringBuilder sql = new StringBuilder("SELECT TOP (?) ").append(String.join(", ", select));
        sql.append(" FROM ").append(m.ctx.tabelaSql(m.ds.idTabelaBase)).append(" AS t0");
        for (Juncao j : m.juncoes) {
            juntar(sql, m.ctx, j, m.ctx.colunaSql(j.idPai, j.colunaPai));
        }
        anexarWhere(sql, parametros, m.condicoes);
        finalizar(sql, parametros, groupBy, having, parametrosHaving, m.orderBy);
        return new SqlMontado(sql.toString(), parametros, m.saida);
    }

    /**
     * Agrega a tabela base numa subconsulta (pelas colunas de join e dimensoes da base) antes de juntar
     * as tabelas de dimensao. No SQL Server 2016 isso reduz consultas de minutos para menos de 1 s.
     */
    private SqlMontado montarPreAgregado(Montagem m) {
        int base = m.ds.idTabelaBase;
        Map<String, String> internas = new LinkedHashMap<>();
        List<String> grupoInterno = new ArrayList<>();

        List<String> select = new ArrayList<>();
        List<String> groupBy = new ArrayList<>();
        for (Item it : m.itens) {
            if (it.agregado) {
                select.add(agregadoFinal(it.expr, internas) + " AS [" + it.col + "]");
            } else if (it.expr.tabelas.contains(base)) {
                String ref = chaveInterna(it.sqlAgrupamento(), internas, grupoInterno);
                select.add(ref + " AS [" + it.col + "]");
                groupBy.add(ref);
            } else {
                String e = it.sqlAgrupamento();
                select.add(e + " AS [" + it.col + "]");
                groupBy.add(e);
            }
        }

        StringBuilder juncoes = new StringBuilder();
        for (Juncao j : m.juncoes) {
            String esquerda = j.idPai == base
                    ? chaveInterna(m.ctx.colunaSql(base, j.colunaPai), internas, grupoInterno)
                    : m.ctx.colunaSql(j.idPai, j.colunaPai);
            juntar(juncoes, m.ctx, j, esquerda);
        }

        List<String> having = new ArrayList<>();
        List<Object> parametrosHaving = new ArrayList<>();
        for (FiltroAgregado f : m.filtrosAgregados) {
            condicao(having, parametrosHaving, agregadoFinal(f.expr, internas), "numerico",
                    f.operador, f.valores, f.nome);
        }

        List<Condicao> daBase = new ArrayList<>();
        List<Condicao> externas = new ArrayList<>();
        for (Condicao c : m.condicoes) {
            (c.tabelas.contains(base) ? daBase : externas).add(c);
        }

        List<String> selectInterno = new ArrayList<>();
        internas.forEach((expr, chave) -> selectInterno.add(expr + " AS [" + chave + "]"));

        List<Object> parametros = new ArrayList<>();
        parametros.add(m.limite);
        StringBuilder sql = new StringBuilder("SELECT TOP (?) ").append(String.join(", ", select));
        sql.append(" FROM (SELECT ").append(String.join(", ", selectInterno));
        sql.append(" FROM ").append(m.ctx.tabelaSql(base)).append(" AS t0");
        anexarWhere(sql, parametros, daBase);
        sql.append(" GROUP BY ").append(String.join(", ", grupoInterno)).append(") AS t0");
        sql.append(juncoes);
        anexarWhere(sql, parametros, externas);
        finalizar(sql, parametros, groupBy, having, parametrosHaving, m.orderBy);
        return new SqlMontado(sql.toString(), parametros, m.saida);
    }

    private static final Set<String> AGREGACOES_PRE_AGREGAVEIS = Set.of("SUM", "COUNT", "MIN", "MAX", "AVG");

    private static boolean podePreAgregar(Montagem m) {
        int base = m.ds.idTabelaBase;
        if (m.juncoes.isEmpty()) return false;
        for (Juncao j : m.juncoes) {
            if ("one_to_many".equals(j.cardinalidade)) return false;
        }
        boolean temAgregada = false;
        for (Item it : m.itens) {
            if (it.agregado) {
                temAgregada = true;
                if (!agregavelNaBase(it.expr, base)) return false;
            } else if (misturaBase(it.expr.tabelas, base)) {
                return false;
            }
        }
        for (FiltroAgregado f : m.filtrosAgregados) {
            if (!agregavelNaBase(f.expr, base)) return false;
        }
        for (Condicao c : m.condicoes) {
            if (misturaBase(c.tabelas, base)) return false;
        }
        return temAgregada;
    }

    private static boolean agregavelNaBase(Expr e, int base) {
        return e.tabelas.size() == 1 && e.tabelas.contains(base)
                && AGREGACOES_PRE_AGREGAVEIS.contains(e.campo.agregacao);
    }

    private static boolean misturaBase(Set<Integer> tabelas, int base) {
        return tabelas.contains(base) && tabelas.size() > 1;
    }

    private static String chaveInterna(String expr, Map<String, String> internas, List<String> grupoInterno) {
        String chave = internas.get(expr);
        if (chave == null) {
            chave = "k" + internas.size();
            internas.put(expr, chave);
            if (grupoInterno != null) grupoInterno.add(expr);
        }
        return "t0.[" + chave + "]";
    }

    private static String agregadoFinal(Expr e, Map<String, String> internas) {
        switch (e.campo.agregacao) {
            case "SUM":   return "SUM(" + chaveInterna("SUM(" + e.sql + ")", internas, null) + ")";
            case "COUNT": return "ISNULL(SUM(" + chaveInterna("COUNT(" + e.sql + ")", internas, null) + "), 0)";
            case "MIN":   return "MIN(" + chaveInterna("MIN(" + e.sql + ")", internas, null) + ")";
            case "MAX":   return "MAX(" + chaveInterna("MAX(" + e.sql + ")", internas, null) + ")";
            case "AVG":
                return "SUM(" + chaveInterna("SUM(CAST(" + e.sql + " AS FLOAT))", internas, null) + ") / NULLIF(SUM("
                        + chaveInterna("COUNT(" + e.sql + ")", internas, null) + "), 0)";
            default:
                throw new ErroConsulta("Agregacao nao pre-agregavel: " + e.campo.agregacao);
        }
    }

    private static void juntar(StringBuilder sql, Contexto ctx, Juncao j, String esquerda) {
        sql.append(" LEFT JOIN ").append(ctx.tabelaSql(j.idFilho)).append(" AS ").append(ctx.alias(j.idFilho))
           .append(" ON ").append(esquerda)
           .append(" = ").append(ctx.colunaSql(j.idFilho, j.colunaFilho));
    }

    private static void anexarWhere(StringBuilder sql, List<Object> parametros, List<Condicao> condicoes) {
        if (condicoes.isEmpty()) return;
        List<String> textos = new ArrayList<>();
        for (Condicao c : condicoes) {
            textos.add(c.sql);
            parametros.addAll(c.parametros);
        }
        sql.append(" WHERE (").append(String.join(") AND (", textos)).append(")");
    }

    private static void finalizar(StringBuilder sql, List<Object> parametros, List<String> groupBy,
                                  List<String> having, List<Object> parametrosHaving, List<String> orderBy) {
        if (!groupBy.isEmpty()) sql.append(" GROUP BY ").append(String.join(", ", groupBy));
        if (!having.isEmpty()) sql.append(" HAVING (").append(String.join(") AND (", having)).append(")");
        parametros.addAll(parametrosHaving);
        sql.append(" ORDER BY ").append(String.join(", ", orderBy));
    }

    /** Verifica se todos os campos e filtros fixos do dataset podem ser consultados. */
    public List<String> validarDataset(DatasetResolvido ds) {
        List<String> erros = new ArrayList<>();
        Contexto ctx = new Contexto(ds);
        for (Campo c : ds.campos()) {
            try {
                Expr e = ctx.campo(c.alias);
                ctx.exigirAlcancavel(e);
                if ("tempo".equals(c.tipoCampo)) exigirTempo(e);
                if (c.agregado()) agregar(e);
            } catch (ErroConsulta ex) {
                erros.add(ex.getMessage());
            }
        }
        for (DatasetResolvido.FiltroFixo f : ds.filtrosFixos()) {
            try {
                Expr e = ctx.colunaFiltroFixo(f.coluna);
                ctx.exigirAlcancavel(e);
                Operador op = operador(f.operador);
                condicao(new ArrayList<>(), new ArrayList<>(), e.sql, e.tipo, op,
                        expandir(op, Collections.singletonList(f.valor)), "filtro fixo em '" + f.coluna + "'");
            } catch (ErroConsulta ex) {
                erros.add(ex.getMessage());
            }
        }
        return erros;
    }

    // =====================================================================
    // Resolucao de campos e joins
    // =====================================================================

    static final class Expr {
        final Campo campo;
        final String sql;
        final Set<Integer> tabelas;
        final String tipo;
        final String rotulo;

        Expr(Campo campo, String sql, Set<Integer> tabelas, String tipo, String rotulo) {
            this.campo = campo;
            this.sql = sql;
            this.tabelas = tabelas;
            this.tipo = tipo;
            this.rotulo = rotulo;
        }
    }

    static final class Juncao {
        final int idPai;
        final String colunaPai;
        final int idFilho;
        final String colunaFilho;
        /** Cardinalidade vista do pai para o filho. */
        final String cardinalidade;

        Juncao(int idPai, String colunaPai, int idFilho, String colunaFilho, String cardinalidade) {
            this.idPai = idPai;
            this.colunaPai = colunaPai;
            this.idFilho = idFilho;
            this.colunaFilho = colunaFilho;
            this.cardinalidade = cardinalidade;
        }
    }

    private static final class Contexto {
        final DatasetResolvido ds;
        final Map<Integer, String> aliasTabela = new HashMap<>();
        final Set<Integer> usadas = new LinkedHashSet<>();
        final Map<Integer, Juncao> arvore;
        final List<Integer> ordemArvore = new ArrayList<>();

        Contexto(DatasetResolvido ds) {
            this.ds = ds;
            aliasTabela.put(ds.idTabelaBase, "t0");
            int n = 1;
            for (Tabela t : ds.tabelas()) {
                if (t.id != ds.idTabelaBase) aliasTabela.put(t.id, "t" + n++);
            }
            this.arvore = montarArvore();
        }

        Expr usar(Expr e) {
            usadas.addAll(e.tabelas);
            return e;
        }

        String alias(int idTabela) {
            return aliasTabela.get(idTabela);
        }

        String tabelaSql(int idTabela) {
            Tabela t = ds.tabela(idTabela);
            return "[" + identificador(t.schema) + "].[" + identificador(t.nome) + "]";
        }

        String colunaSql(int idTabela, String coluna) {
            Tabela t = ds.tabela(idTabela);
            return alias(idTabela) + ".[" + identificador(t.nomeColuna(coluna)) + "]";
        }

        Expr campo(String alias) {
            Campo c = ds.campo(alias);
            if (c == null) {
                throw new ErroConsulta("Campo '" + alias + "' nao existe no dataset");
            }
            if (c.calculado()) {
                return expressao(c);
            }
            if (c.idTabela == null || c.coluna == null) {
                throw new ErroConsulta("Campo '" + alias + "' nao tem coluna de origem nem expressao");
            }
            Tabela t = ds.tabela(c.idTabela);
            if (t == null) {
                throw new ErroConsulta("Campo '" + alias + "' usa uma tabela que nao faz parte do dataset");
            }
            if (!c.colunaAtiva || !t.temColuna(c.coluna)) {
                throw new ErroConsulta("Campo '" + alias + "': coluna '" + c.coluna + "' inativa no catalogo");
            }
            return new Expr(c, colunaSql(t.id, c.coluna), Set.of(t.id), c.tipoColuna, rotulo(c));
        }

        Expr colunaFiltroFixo(String coluna) {
            if (coluna == null || coluna.trim().isEmpty()) {
                throw new ErroConsulta("Filtro fixo do dataset sem coluna");
            }
            String nome = coluna.trim();
            if (ds.campo(nome) != null) {
                return campo(nome);
            }
            int ponto = nome.lastIndexOf('.');
            if (ponto > 0) {
                String tab = nome.substring(0, ponto);
                String col = nome.substring(ponto + 1);
                for (Tabela t : ds.tabelas()) {
                    if (t.atendePorNome(tab) && t.temColuna(col)) {
                        return new Expr(null, colunaSql(t.id, col), Set.of(t.id), t.tipoColuna(col), nome);
                    }
                }
            } else {
                Tabela base = ds.tabela(ds.idTabelaBase);
                if (base.temColuna(nome)) {
                    return new Expr(null, colunaSql(base.id, nome), Set.of(base.id), base.tipoColuna(nome), nome);
                }
            }
            throw new ErroConsulta("Filtro fixo do dataset usa coluna invalida: " + nome);
        }

        private Expr expressao(Campo c) {
            GramaticaCampoCalculado.Resultado r = GRAMATICA.validar(c.expressao);
            if (!r.valido) {
                throw new ErroConsulta("Campo calculado '" + c.alias + "': " + r.erro);
            }
            String e = c.expressao;
            StringBuilder out = new StringBuilder();
            Set<Integer> tabelas = new LinkedHashSet<>();
            int i = 0;
            int len = e.length();
            while (i < len) {
                char ch = e.charAt(i);
                if (ch == '\'') {
                    int fim = e.indexOf('\'', i + 1);
                    if (fim < 0) {
                        throw new ErroConsulta("Campo calculado '" + c.alias + "': texto sem aspas de fechamento");
                    }
                    out.append(e, i, fim + 1);
                    i = fim + 1;
                } else if (Character.isLetter(ch) || ch == '_') {
                    int j = i;
                    while (j < len && (Character.isLetterOrDigit(e.charAt(j)) || e.charAt(j) == '_')) j++;
                    String token = e.substring(i, j);
                    if (GramaticaCampoCalculado.ehPalavraReservada(token)
                            || PARTES_DATA.contains(token.toUpperCase(Locale.ROOT))) {
                        out.append(token);
                    } else {
                        Tabela t = tabelaDaColuna(c.alias, token);
                        out.append(colunaSql(t.id, token));
                        tabelas.add(t.id);
                    }
                    i = j;
                } else if (Character.isDigit(ch)) {
                    int j = i;
                    while (j < len && (Character.isDigit(e.charAt(j)) || e.charAt(j) == '.')) j++;
                    out.append(e, i, j);
                    i = j;
                } else {
                    out.append(ch);
                    i++;
                }
            }
            if (tabelas.isEmpty()) {
                tabelas.add(ds.idTabelaBase);
            }
            String tipo = "tempo".equals(c.tipoCampo) ? "data_hora" : null;
            return new Expr(c, "(" + out + ")", tabelas, tipo, rotulo(c));
        }

        private Tabela tabelaDaColuna(String campo, String coluna) {
            List<Tabela> achadas = new ArrayList<>();
            for (Tabela t : ds.tabelas()) {
                if (t.temColuna(coluna)) achadas.add(t);
            }
            if (achadas.isEmpty()) {
                throw new ErroConsulta("Campo calculado '" + campo + "': coluna '" + coluna
                        + "' nao existe nas tabelas do dataset");
            }
            if (achadas.size() > 1) {
                throw new ErroConsulta("Campo calculado '" + campo + "': coluna '" + coluna
                        + "' existe em mais de uma tabela do dataset");
            }
            return achadas.get(0);
        }

        private Map<Integer, Juncao> montarArvore() {
            Map<Integer, List<Juncao>> adjacencia = new LinkedHashMap<>();
            for (DatasetResolvido.Relacao r : ds.relacoes()) {
                Tabela o = ds.tabela(r.idOrigem);
                Tabela d = ds.tabela(r.idDestino);
                if (o == null || d == null || o.id == d.id) continue;
                if (!o.temColuna(r.colunaOrigem) || !d.temColuna(r.colunaDestino)) continue;
                String card = r.cardinalidade == null ? "" : r.cardinalidade;
                if (!card.equals("many_to_one") && !card.equals("one_to_many") && !card.equals("one_to_one")) continue;
                adjacencia.computeIfAbsent(o.id, k -> new ArrayList<>())
                        .add(new Juncao(o.id, r.colunaOrigem, d.id, r.colunaDestino, card));
                adjacencia.computeIfAbsent(d.id, k -> new ArrayList<>())
                        .add(new Juncao(d.id, r.colunaDestino, o.id, r.colunaOrigem, inverter(card)));
            }
            Map<Integer, Juncao> pai = new HashMap<>();
            Set<Integer> visitadas = new HashSet<>();
            Deque<Integer> fila = new ArrayDeque<>();
            fila.add(ds.idTabelaBase);
            visitadas.add(ds.idTabelaBase);
            while (!fila.isEmpty()) {
                int atual = fila.poll();
                for (Juncao j : adjacencia.getOrDefault(atual, Collections.emptyList())) {
                    if (visitadas.add(j.idFilho)) {
                        pai.put(j.idFilho, j);
                        ordemArvore.add(j.idFilho);
                        fila.add(j.idFilho);
                    }
                }
            }
            return pai;
        }

        void exigirAlcancavel(Expr e) {
            for (int id : e.tabelas) {
                if (id != ds.idTabelaBase && !arvore.containsKey(id)) {
                    throw new ErroConsulta("A tabela '" + ds.tabela(id).nome
                            + "' nao tem relacionamento valido ate a tabela base '"
                            + ds.tabela(ds.idTabelaBase).nome + "'");
                }
            }
        }

        List<Juncao> juncoesNecessarias() {
            Set<Integer> necessarias = new HashSet<>();
            for (int id : usadas) {
                if (id == ds.idTabelaBase) continue;
                if (!arvore.containsKey(id)) {
                    throw new ErroConsulta("A tabela '" + ds.tabela(id).nome
                            + "' nao tem relacionamento valido ate a tabela base '"
                            + ds.tabela(ds.idTabelaBase).nome + "'");
                }
                int atual = id;
                while (atual != ds.idTabelaBase && necessarias.add(atual)) {
                    atual = arvore.get(atual).idPai;
                }
            }
            List<Juncao> lista = new ArrayList<>();
            for (int id : ordemArvore) {
                if (necessarias.contains(id)) lista.add(arvore.get(id));
            }
            return lista;
        }
    }

    // =====================================================================
    // Regras auxiliares
    // =====================================================================

    private static void registrar(Map<String, String> mapa, ConsultaDashboard.Membro m, String col) {
        mapa.putIfAbsent(m.original, col);
        mapa.putIfAbsent(m.alias, col);
    }

    private static String rotulo(Campo c) {
        return c.rotulo != null ? c.rotulo : c.alias;
    }

    public static String identificador(String nome) {
        if (nome == null || !IDENTIFICADOR.matcher(nome).matches()) {
            throw new ErroConsulta("Identificador invalido no catalogo: " + nome);
        }
        return nome;
    }

    private static String inverter(String cardinalidade) {
        if ("many_to_one".equals(cardinalidade)) return "one_to_many";
        if ("one_to_many".equals(cardinalidade)) return "many_to_one";
        return cardinalidade;
    }

    private static void exigirTempo(Expr e) {
        if (!"data".equals(e.tipo) && !"data_hora".equals(e.tipo)) {
            throw new ErroConsulta("O campo '" + e.campo.alias + "' nao e de data/hora");
        }
    }

    static String granularidade(String g, String expr) {
        switch (g) {
            case "hour":    return "DATEADD(HOUR, DATEDIFF(HOUR, 0, " + expr + "), 0)";
            case "day":     return "CAST(" + expr + " AS DATE)";
            case "week":    return "DATEADD(DAY, -((DATEPART(WEEKDAY, " + expr + ") + @@DATEFIRST - 2) % 7), CAST("
                                    + expr + " AS DATE))";
            case "month":   return "DATEFROMPARTS(YEAR(" + expr + "), MONTH(" + expr + "), 1)";
            case "quarter": return "DATEFROMPARTS(YEAR(" + expr + "), (DATEPART(QUARTER, " + expr + ") - 1) * 3 + 1, 1)";
            case "year":    return "DATEFROMPARTS(YEAR(" + expr + "), 1, 1)";
            default:
                throw new ErroConsulta("Granularidade nao suportada: " + g);
        }
    }

    private static String agregar(Expr e) {
        String a = e.campo.agregacao;
        switch (a) {
            case "SUM":            return "SUM(" + e.sql + ")";
            case "AVG":            return "AVG(CAST(" + e.sql + " AS FLOAT))";
            case "COUNT":          return "COUNT(" + e.sql + ")";
            case "COUNT_DISTINCT": return "COUNT(DISTINCT " + e.sql + ")";
            case "MIN":            return "MIN(" + e.sql + ")";
            case "MAX":            return "MAX(" + e.sql + ")";
            default:
                throw new ErroConsulta("Agregacao invalida no campo '" + e.campo.alias + "': " + a);
        }
    }

    private static void verificarMultiplicacao(DatasetResolvido ds, List<Juncao> juncoes, List<Expr> agregadas) {
        for (Juncao j : juncoes) {
            if (!"one_to_many".equals(j.cardinalidade)) continue;
            Set<Integer> ladoMuitos = new HashSet<>();
            ladoMuitos.add(j.idFilho);
            boolean cresceu = true;
            while (cresceu) {
                cresceu = false;
                for (Juncao k : juncoes) {
                    if (ladoMuitos.contains(k.idPai) && ladoMuitos.add(k.idFilho)) cresceu = true;
                }
            }
            for (Expr m : agregadas) {
                if (AGREGACOES_MULTIPLICAVEIS.contains(m.campo.agregacao) && !ladoMuitos.containsAll(m.tabelas)) {
                    String pai = ds.tabela(j.idPai).nome;
                    String filho = ds.tabela(j.idFilho).nome;
                    throw new ErroConsulta("A medida '" + m.campo.alias + "' (" + m.campo.agregacao
                            + ") seria multiplicada pela relacao 1:N entre '" + pai + "' e '" + filho
                            + "'. Use COUNT_DISTINCT, MIN ou MAX, ou crie um dataset com base em '" + filho + "'.");
                }
            }
        }
    }

    static Operador operador(String op) {
        String chave = op == null ? "" : op.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        Operador o = OPERADORES.get(chave);
        if (o == null) {
            throw new ErroConsulta("Operador de filtro nao suportado: " + op);
        }
        return o;
    }

    private static List<String> expandir(Operador op, List<String> valores) {
        List<String> lista = new ArrayList<>();
        boolean dividir = (op == Operador.EM || op == Operador.FORA_DE || op == Operador.ENTRE)
                && valores.size() == 1 && valores.get(0) != null && valores.get(0).contains(",");
        for (String v : valores) {
            if (v == null) continue;
            if (dividir) {
                for (String parte : v.split(",")) {
                    if (!parte.trim().isEmpty()) lista.add(parte.trim());
                }
            } else if (!v.trim().isEmpty()) {
                lista.add(v);
            }
        }
        return lista;
    }

    private static void condicao(List<String> lista, List<Object> params, String expr, String tipo,
                                 Operador op, List<String> valores, String campo) {
        switch (op) {
            case NULO:
                lista.add(expr + " IS NULL");
                return;
            case NAO_NULO:
                lista.add(expr + " IS NOT NULL");
                return;
            default:
                break;
        }
        if (valores.isEmpty()) {
            throw new ErroConsulta("Informe um valor para o filtro de '" + campo + "'");
        }
        switch (op) {
            case CONTEM:
            case NAO_CONTEM:
                exigirQuantidade(valores, 1, campo);
                lista.add(expr + (op == Operador.NAO_CONTEM ? " NOT LIKE ?" : " LIKE ?") + " ESCAPE '\\'");
                params.add("%" + escaparLike(valores.get(0)) + "%");
                return;
            case IGUAL:
            case DIFERENTE:
            case EM:
            case FORA_DE:
                boolean negar = op == Operador.DIFERENTE || op == Operador.FORA_DE;
                if (valores.size() == 1) {
                    lista.add(expr + (negar ? " <> ?" : " = ?"));
                    params.add(converter(valores.get(0), tipo, campo));
                } else {
                    if (valores.size() > MAX_VALORES_IN) {
                        throw new ErroConsulta("Filtro de '" + campo + "' com mais de " + MAX_VALORES_IN + " valores");
                    }
                    StringBuilder sb = new StringBuilder(expr).append(negar ? " NOT IN (" : " IN (");
                    for (int i = 0; i < valores.size(); i++) {
                        sb.append(i == 0 ? "?" : ", ?");
                        params.add(converter(valores.get(i), tipo, campo));
                    }
                    lista.add(sb.append(")").toString());
                }
                return;
            case MAIOR:
            case MAIOR_IGUAL:
            case MENOR:
            case MENOR_IGUAL:
                exigirQuantidade(valores, 1, campo);
                String simbolo = op == Operador.MAIOR ? " > ?" : op == Operador.MAIOR_IGUAL ? " >= ?"
                        : op == Operador.MENOR ? " < ?" : " <= ?";
                lista.add(expr + simbolo);
                params.add(converter(valores.get(0), tipo, campo));
                return;
            case ENTRE:
                exigirQuantidade(valores, 2, campo);
                String fim = valores.get(1).trim();
                boolean ehData = "data".equals(tipo) || "data_hora".equals(tipo);
                if (ehData && PeriodoRelativo.ehSoData(fim)) {
                    lista.add(expr + " >= ? AND " + expr + " < ?");
                    params.add(converter(valores.get(0), tipo, campo));
                    params.add(Timestamp.valueOf(PeriodoRelativo.parseDataHora(fim).plusDays(1)));
                } else {
                    lista.add(expr + " >= ? AND " + expr + " <= ?");
                    params.add(converter(valores.get(0), tipo, campo));
                    params.add(converter(fim, tipo, campo));
                }
                return;
            default:
                throw new ErroConsulta("Operador de filtro nao suportado: " + op);
        }
    }

    private static void exigirQuantidade(List<String> valores, int n, String campo) {
        if (valores.size() != n) {
            throw new ErroConsulta("O filtro de '" + campo + "' exige " + n + (n == 1 ? " valor" : " valores"));
        }
    }

    static String escaparLike(String v) {
        return v.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_").replace("[", "\\[");
    }

    static Object converter(String valor, String tipo, String campo) {
        String v = valor.trim();
        try {
            if (tipo == null) return valor;
            switch (tipo) {
                case "inteiro":
                    return Long.valueOf(v);
                case "numerico":
                    return new BigDecimal(v.indexOf('.') < 0 ? v.replace(',', '.') : v);
                case "data":
                case "data_hora":
                    return Timestamp.valueOf(PeriodoRelativo.parseDataHora(v));
                case "booleano":
                    String b = v.toLowerCase(Locale.ROOT);
                    if (b.equals("true") || b.equals("1") || b.equals("sim")) return Boolean.TRUE;
                    if (b.equals("false") || b.equals("0") || b.equals("nao")) return Boolean.FALSE;
                    throw new NumberFormatException(v);
                default:
                    return valor;
            }
        } catch (NumberFormatException | ErroConsulta e) {
            throw new ErroConsulta("Valor '" + valor + "' invalido para o filtro de '" + campo + "'");
        }
    }
}
