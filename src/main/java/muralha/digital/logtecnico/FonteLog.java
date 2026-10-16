package muralha.digital.logtecnico;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Uma fonte do repositório de logs técnicos: monta a consulta da tabela de origem conforme o
 * filtro e normaliza cada linha. A consulta devolve a coluna {@code data_hora} (usada na
 * ordenação) e as colunas brutas que {@link #normalizar} lê.
 */
public interface FonteLog {

    /** Nome da origem gravado no registro normalizado (ex.: CAPTURA). */
    String origem();

    /** Tabela principal, para conferir se existe antes de consultar. */
    String tabela();

    /** Consulta da fonte para o filtro, ou nulo se a fonte não tem nada que atenda ao filtro. */
    Consulta consulta(FiltroLogTecnico filtro, ParametrosLogTecnico parametros);

    /** Converte uma linha da consulta (coluna → valor) no registro normalizado. */
    RegistroLogTecnico normalizar(Map<String, Object> linha);

    /** SQL e parâmetros (na ordem dos ?). */
    final class Consulta {
        public final String sql;
        public final List<Object> parametros;

        public Consulta(String sql, List<Object> parametros) {
            this.sql = sql;
            this.parametros = parametros;
        }
    }

    /** Ajuda a montar o WHERE com parâmetros. */
    final class Where {
        private final StringBuilder sb = new StringBuilder();
        private final List<Object> ps = new ArrayList<>();

        public Where e(String condicao, Object... valores) {
            sb.append(sb.length() == 0 ? " WHERE " : " AND ").append(condicao);
            for (Object v : valores) ps.add(v);
            return this;
        }

        /** "coluna IN (?,?,...)". */
        public Where emLista(String coluna, Iterable<?> valores) {
            StringBuilder in = new StringBuilder(coluna).append(" IN (");
            boolean primeiro = true;
            for (Object v : valores) {
                in.append(primeiro ? "?" : ",?");
                ps.add(v);
                primeiro = false;
            }
            return primeiro ? e("1 = 0") : e(in.append(")").toString());
        }

        public Consulta montar(String selectFrom) {
            return new Consulta(selectFrom + sb, ps);
        }
    }
}
