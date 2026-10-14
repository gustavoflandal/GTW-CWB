package muralha.digital.dashboards.consulta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Metadados de um dataset ja lidos do catalogo: tabelas, colunas validas, campos, relacoes e filtros fixos.
 * Nomes de coluna e alias sao comparados sem diferenciar maiusculas (como no SQL Server).
 */
public final class DatasetResolvido {

    public static final class Tabela {
        public final int id;
        public final String schema;
        public final String nome;
        public final String aliasDataset;
        public final String chavePrimaria;
        private final Map<String, String> colunas = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        private final Map<String, String> grafia = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Tabela(int id, String schema, String nome, String aliasDataset, String chavePrimaria) {
            this.id = id;
            this.schema = schema;
            this.nome = nome;
            this.aliasDataset = aliasDataset;
            this.chavePrimaria = chavePrimaria;
        }

        public boolean temColuna(String coluna) {
            return coluna != null && colunas.containsKey(coluna);
        }

        public String tipoColuna(String coluna) {
            return colunas.get(coluna);
        }

        /** Grafia da coluna como cadastrada no catalogo. */
        public String nomeColuna(String coluna) {
            return grafia.get(coluna);
        }

        public Collection<String> nomesColunas() {
            return grafia.values();
        }

        public boolean atendePorNome(String nomeOuAlias) {
            return nome.equalsIgnoreCase(nomeOuAlias)
                    || (aliasDataset != null && aliasDataset.equalsIgnoreCase(nomeOuAlias));
        }
    }

    public static final class Campo {
        public final String alias;
        public final String rotulo;
        public final Integer idTabela;
        public final String coluna;
        public final String tipoColuna;
        public final boolean colunaAtiva;
        public final String agregacao;
        public final String tipoCampo;
        public final String expressao;

        Campo(String alias, String rotulo, Integer idTabela, String coluna, String tipoColuna,
              boolean colunaAtiva, String agregacao, String tipoCampo, String expressao) {
            this.alias = alias;
            this.rotulo = rotulo;
            this.idTabela = idTabela;
            this.coluna = coluna;
            this.tipoColuna = tipoColuna;
            this.colunaAtiva = colunaAtiva;
            this.agregacao = agregacao == null ? "NONE" : agregacao;
            this.tipoCampo = tipoCampo == null ? "dimensao" : tipoCampo;
            this.expressao = expressao;
        }

        public boolean calculado() {
            return expressao != null && !expressao.trim().isEmpty();
        }

        public boolean agregado() {
            return "medida".equals(tipoCampo) && !"NONE".equals(agregacao);
        }
    }

    public static final class Relacao {
        public final int idOrigem;
        public final String colunaOrigem;
        public final int idDestino;
        public final String colunaDestino;
        public final String cardinalidade;

        Relacao(int idOrigem, String colunaOrigem, int idDestino, String colunaDestino, String cardinalidade) {
            this.idOrigem = idOrigem;
            this.colunaOrigem = colunaOrigem;
            this.idDestino = idDestino;
            this.colunaDestino = colunaDestino;
            this.cardinalidade = cardinalidade;
        }
    }

    public static final class FiltroFixo {
        public final String coluna;
        public final String operador;
        public final String valor;

        FiltroFixo(String coluna, String operador, String valor) {
            this.coluna = coluna;
            this.operador = operador;
            this.valor = valor;
        }
    }

    public final int id;
    public final int idTabelaBase;
    private final Map<Integer, Tabela> tabelas = new LinkedHashMap<>();
    private final Map<String, Campo> campos = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final List<Campo> camposEmOrdem = new ArrayList<>();
    private final List<Relacao> relacoes = new ArrayList<>();
    private final List<FiltroFixo> filtrosFixos = new ArrayList<>();

    public DatasetResolvido(int id, int idTabelaBase) {
        this.id = id;
        this.idTabelaBase = idTabelaBase;
    }

    public Tabela adicionarTabela(int idTabela, String schema, String nome, String aliasDataset, String chavePrimaria) {
        Tabela t = new Tabela(idTabela, schema, nome, aliasDataset, chavePrimaria);
        tabelas.put(idTabela, t);
        return t;
    }

    public void adicionarColuna(int idTabela, String coluna, String tipo) {
        Tabela t = tabelas.get(idTabela);
        if (t != null && coluna != null) {
            t.colunas.put(coluna, tipo);
            t.grafia.put(coluna, coluna);
        }
    }

    public void adicionarCampo(String alias, String rotulo, Integer idTabela, String coluna, String tipoColuna,
                               boolean colunaAtiva, String agregacao, String tipoCampo, String expressao) {
        if (campos.containsKey(alias)) {
            throw new ErroConsulta("Alias duplicado no dataset: " + alias);
        }
        Campo c = new Campo(alias, rotulo, idTabela, coluna, tipoColuna, colunaAtiva, agregacao, tipoCampo, expressao);
        campos.put(alias, c);
        camposEmOrdem.add(c);
    }

    public void adicionarRelacao(int idOrigem, String colunaOrigem, int idDestino, String colunaDestino,
                                 String cardinalidade) {
        relacoes.add(new Relacao(idOrigem, colunaOrigem, idDestino, colunaDestino, cardinalidade));
    }

    public void adicionarFiltroFixo(String coluna, String operador, String valor) {
        filtrosFixos.add(new FiltroFixo(coluna, operador, valor));
    }

    public Tabela tabela(int idTabela) {
        return tabelas.get(idTabela);
    }

    public Collection<Tabela> tabelas() {
        return Collections.unmodifiableCollection(tabelas.values());
    }

    public Campo campo(String alias) {
        return campos.get(alias);
    }

    public List<Campo> campos() {
        return Collections.unmodifiableList(camposEmOrdem);
    }

    public List<Relacao> relacoes() {
        return Collections.unmodifiableList(relacoes);
    }

    public List<FiltroFixo> filtrosFixos() {
        return Collections.unmodifiableList(filtrosFixos);
    }
}
