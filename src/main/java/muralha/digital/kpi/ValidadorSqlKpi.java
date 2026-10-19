package muralha.digital.kpi;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Valida o SQL de um KPI antes de gravar e de executar: uma unica consulta SELECT, sem comandos que alterem dados. */
public final class ValidadorSqlKpi {

    public static final int TAMANHO_MAXIMO = 4000;
    public static final int TIMEOUT_SEGUNDOS = 30;

    /*
     * Palavras inteiras sem usar \b: o T-SQL separa "1DELETE" em "1" + "DELETE", entao um digito antes
     * da palavra nao a protege. A lista inclui os iniciadores de comando que nunca aparecem dentro de um
     * SELECT, pois o T-SQL nao exige ";" entre comandos.
     */
    private static final String ANTES = "(?<![A-Za-z_@#$])";
    private static final String DEPOIS = "(?![A-Za-z0-9_@#$])";
    private static final Pattern PROIBIDAS = Pattern.compile(
            ANTES + "(INSERT|UPDATE|DELETE|MERGE|DROP|ALTER|CREATE|TRUNCATE|EXEC|EXECUTE|GRANT|REVOKE|DENY|INTO|"
            + "OPENROWSET|OPENQUERY|OPENDATASOURCE|OPENXML|BULK|SHUTDOWN|WAITFOR|DBCC|BACKUP|RESTORE|KILL|RECONFIGURE|USE|"
            + "DECLARE|SET|IF|WHILE|BEGIN|COMMIT|ROLLBACK|SAVE|GOTO|RAISERROR|THROW|PRINT|DISABLE|ENABLE|SETUSER|REVERT|"
            + "UPDATETEXT|WRITETEXT|CHECKPOINT|OPEN|FETCH|DEALLOCATE)" + DEPOIS
            + "|" + ANTES + "(XP|SP)_\\w*",
            Pattern.CASE_INSENSITIVE);

    private ValidadorSqlKpi() {}

    /** @return null se valido; senao a mensagem para o usuario. */
    public static String validar(String sql) {
        if (sql == null || sql.trim().isEmpty()) return "Informe a consulta SQL.";
        String s = sql.trim();
        if (s.length() > TAMANHO_MAXIMO) return "A consulta passa de " + TAMANHO_MAXIMO + " caracteres.";
        String semLiterais = neutralizarDelimitados(s);
        if (semLiterais == null) return "Há um texto ou identificador sem fechamento.";
        if (semLiterais.contains("--") || semLiterais.contains("/*")) return "Comentários não são permitidos na consulta.";
        if (!semLiterais.toUpperCase(Locale.ROOT).startsWith("SELECT")) return "A consulta deve começar com SELECT.";
        if (semLiterais.indexOf(';') >= 0) return "Use uma única consulta, sem ponto e vírgula.";
        Matcher m = PROIBIDAS.matcher(semLiterais);
        if (m.find()) return "Comando não permitido na consulta: " + m.group().toUpperCase(Locale.ROOT);
        return null;
    }

    /**
     * Troca por espacos o conteudo de literais '...' (escape ''), identificadores [...] (escape ]]) e
     * identificadores "..." (escape ""), mantendo os delimitadores; null se algum nao fechar.
     */
    static String neutralizarDelimitados(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        char fecha = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (fecha == 0) {
                if (c == '\'' || c == '"') fecha = c;
                else if (c == '[') fecha = ']';
                sb.append(c);
            } else if (c == fecha) {
                if (i + 1 < s.length() && s.charAt(i + 1) == fecha) { sb.append("  "); i++; continue; }
                fecha = 0;
                sb.append(c);
            } else {
                sb.append(' ');
            }
        }
        return fecha != 0 ? null : sb.toString();
    }
}
