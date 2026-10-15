package muralha.digital.dashboards.consulta;

import java.util.Collections;
import java.util.List;

/**
 * SQL gerado pelo MontadorSql, seus parametros e o mapeamento das colunas para a resposta.
 */
public final class SqlMontado {

    public static final class ColunaSaida {
        /** Alias da coluna no SQL (c0, c1...). */
        public final String aliasSql;
        /** Chaves com que o valor aparece em cada linha da resposta. */
        public final List<String> chaves;
        /** "dimensao", "medida" ou "tempo". */
        public final String papel;
        public final String rotulo;
        public final String granularidade;

        ColunaSaida(String aliasSql, List<String> chaves, String papel, String rotulo, String granularidade) {
            this.aliasSql = aliasSql;
            this.chaves = Collections.unmodifiableList(chaves);
            this.papel = papel;
            this.rotulo = rotulo;
            this.granularidade = granularidade;
        }
    }

    public final String sql;
    public final List<Object> parametros;
    public final List<ColunaSaida> colunas;

    SqlMontado(String sql, List<Object> parametros, List<ColunaSaida> colunas) {
        this.sql = sql;
        this.parametros = Collections.unmodifiableList(parametros);
        this.colunas = Collections.unmodifiableList(colunas);
    }
}
