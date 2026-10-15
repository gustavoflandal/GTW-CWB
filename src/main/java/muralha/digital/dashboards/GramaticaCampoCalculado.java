package muralha.digital.dashboards;

import java.util.*;
import java.util.regex.*;

/**
 * Parser de expressoes calculadas com gramatica fechada.
 * Valida que a expressao usa apenas funcoes e operadores permitidos,
 * compativel com SQL Server 2016.
 */
public final class GramaticaCampoCalculado {

    private static final Set<String> FUNCOES_PERMITIDAS = Set.of(
            "YEAR", "MONTH", "DAY", "DATEPART", "DATEDIFF",
            "CAST", "COALESCE", "NULLIF", "IIF",
            "LEN", "LEFT", "RIGHT", "SUBSTRING", "UPPER", "LOWER",
            "ROUND", "ABS", "CEILING", "FLOOR"
    );

    private static final Set<String> FUNCOES_PROIBIDAS = Set.of(
            "STRING_AGG", "TRIM", "CONCAT_WS", "TRANSLATE"
    );

    private static final Set<String> KEYWORDS_PERMITIDOS = Set.of(
            "CASE", "WHEN", "THEN", "ELSE", "END",
            "AND", "OR", "NOT", "IS", "NULL", "IN",
            "BETWEEN", "LIKE", "AS", "INT", "VARCHAR",
            "NVARCHAR", "FLOAT", "DECIMAL", "DATE", "DATETIME",
            "BIT", "BIGINT"
    );

    private static final Pattern PAT_INJECTION = Pattern.compile(
            "(?i)(;|--|/\\*|\\*/|xp_|sp_|exec\\s|execute\\s|drop\\s|alter\\s|create\\s|insert\\s|update\\s|delete\\s|truncate\\s|grant\\s|revoke\\s|deny\\s)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PAT_IDENTIFICADOR = Pattern.compile(
            "^[a-zA-Z_][a-zA-Z0-9_]*$"
    );

    private static final Pattern PAT_NUMERO = Pattern.compile(
            "^-?\\d+(\\.\\d+)?$"
    );

    private static final Pattern PAT_STRING_LITERAL = Pattern.compile(
            "^'[^']*'$"
    );

    public static class Resultado {
        public final boolean valido;
        public final String erro;
        public final String expressaoLimpa;

        private Resultado(boolean valido, String erro, String expressaoLimpa) {
            this.valido = valido;
            this.erro = erro;
            this.expressaoLimpa = expressaoLimpa;
        }

        static Resultado ok(String expr) { return new Resultado(true, null, expr); }
        static Resultado falha(String erro) { return new Resultado(false, erro, null); }
    }

    public static boolean ehPalavraReservada(String token) {
        String upper = token.toUpperCase(Locale.ROOT);
        return FUNCOES_PERMITIDAS.contains(upper) || KEYWORDS_PERMITIDOS.contains(upper);
    }

    public Resultado validar(String expressao) {
        if (expressao == null || expressao.trim().isEmpty()) {
            return Resultado.falha("Expressao vazia");
        }

        String expr = expressao.trim();

        if (PAT_INJECTION.matcher(expr).find()) {
            return Resultado.falha("Expressao contem tokens proibidos (possivel SQL injection)");
        }

        if (contemSubquery(expr)) {
            return Resultado.falha("Subqueries nao sao permitidas");
        }

        List<String> tokens = tokenizar(expr);
        if (tokens.isEmpty()) {
            return Resultado.falha("Expressao vazia apos tokenizacao");
        }

        for (String token : tokens) {
            String upper = token.toUpperCase();

            if (FUNCOES_PROIBIDAS.contains(upper)) {
                return Resultado.falha("Funcao '" + token + "' nao compativel com SQL Server 2016");
            }

            if (FUNCOES_PERMITIDAS.contains(upper) || KEYWORDS_PERMITIDOS.contains(upper)) {
                continue;
            }

            if (isOperador(token) || isPontuacao(token)) {
                continue;
            }

            if (PAT_NUMERO.matcher(token).matches()) {
                continue;
            }

            if (PAT_STRING_LITERAL.matcher(token).matches()) {
                continue;
            }

            if (PAT_IDENTIFICADOR.matcher(token).matches()) {
                continue;
            }

            return Resultado.falha("Token nao reconhecido: '" + token + "'");
        }

        int parentesesAbertos = 0;
        for (String token : tokens) {
            if ("(".equals(token)) parentesesAbertos++;
            else if (")".equals(token)) parentesesAbertos--;
            if (parentesesAbertos < 0) {
                return Resultado.falha("Parenteses desbalanceados");
            }
        }
        if (parentesesAbertos != 0) {
            return Resultado.falha("Parenteses desbalanceados");
        }

        int caseCount = 0, endCount = 0;
        for (String token : tokens) {
            if ("CASE".equalsIgnoreCase(token)) caseCount++;
            if ("END".equalsIgnoreCase(token)) endCount++;
        }
        if (caseCount != endCount) {
            return Resultado.falha("CASE/END desbalanceados");
        }

        return Resultado.ok(expr);
    }

    private boolean contemSubquery(String expr) {
        String upper = expr.toUpperCase();
        return upper.contains("SELECT ") || upper.contains("FROM ") || upper.contains("WHERE ");
    }

    private List<String> tokenizar(String expr) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        int len = expr.length();

        while (i < len) {
            char c = expr.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '\'') {
                int fim = expr.indexOf('\'', i + 1);
                if (fim == -1) fim = len;
                tokens.add(expr.substring(i, fim + 1));
                i = fim + 1;
                continue;
            }

            if (c == '(' || c == ')' || c == ',') {
                tokens.add(String.valueOf(c));
                i++;
                continue;
            }

            if (isOperadorChar(c)) {
                StringBuilder op = new StringBuilder();
                op.append(c);
                if (i + 1 < len && isOperadorChar(expr.charAt(i + 1)) && isOperadorDuplo(c, expr.charAt(i + 1))) {
                    op.append(expr.charAt(i + 1));
                    i++;
                }
                tokens.add(op.toString());
                i++;
                continue;
            }

            if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
                StringBuilder word = new StringBuilder();
                while (i < len && (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_' || expr.charAt(i) == '.')) {
                    word.append(expr.charAt(i));
                    i++;
                }
                tokens.add(word.toString());
                continue;
            }

            tokens.add(String.valueOf(c));
            i++;
        }
        return tokens;
    }

    private boolean isOperadorChar(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%'
                || c == '=' || c == '!' || c == '<' || c == '>' || c == '|';
    }

    private boolean isOperadorDuplo(char a, char b) {
        return (a == '!' && b == '=') || (a == '<' && b == '=')
                || (a == '>' && b == '=') || (a == '|' && b == '|');
    }

    private boolean isOperador(String token) {
        return "+".equals(token) || "-".equals(token) || "*".equals(token)
                || "/".equals(token) || "%".equals(token) || "=".equals(token)
                || "!=".equals(token) || "<".equals(token) || ">".equals(token)
                || "<=".equals(token) || ">=".equals(token) || "||".equals(token);
    }

    private boolean isPontuacao(String token) {
        return "(".equals(token) || ")".equals(token) || ",".equals(token);
    }
}
