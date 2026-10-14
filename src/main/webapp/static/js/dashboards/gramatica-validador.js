/**
 * Validacao client-side de expressoes calculadas.
 * Espelha GramaticaCampoCalculado.java — validacao real e no servidor.
 */

const FUNCOES_PERMITIDAS = new Set([
    'YEAR', 'MONTH', 'DAY', 'DATEPART', 'DATEDIFF',
    'CAST', 'COALESCE', 'NULLIF', 'IIF',
    'LEN', 'LEFT', 'RIGHT', 'SUBSTRING', 'UPPER', 'LOWER',
    'ROUND', 'ABS', 'CEILING', 'FLOOR'
]);

const FUNCOES_PROIBIDAS = new Set([
    'STRING_AGG', 'TRIM', 'CONCAT_WS', 'TRANSLATE'
]);

const KEYWORDS = new Set([
    'CASE', 'WHEN', 'THEN', 'ELSE', 'END',
    'AND', 'OR', 'NOT', 'IS', 'NULL', 'IN',
    'BETWEEN', 'LIKE', 'AS', 'INT', 'VARCHAR',
    'NVARCHAR', 'FLOAT', 'DECIMAL', 'DATE', 'DATETIME',
    'BIT', 'BIGINT'
]);

const PAT_INJECTION = /(?:;|--|\/\*|\*\/|xp_|sp_|exec\s|execute\s|drop\s|alter\s|create\s|insert\s|update\s|delete\s|truncate\s|grant\s|revoke\s|deny\s)/i;
const PAT_SUBQUERY = /(?:SELECT\s|FROM\s|WHERE\s)/i;
const PAT_IDENTIFICADOR = /^[a-zA-Z_][a-zA-Z0-9_]*$/;
const PAT_NUMERO = /^-?\d+(\.\d+)?$/;
const PAT_STRING = /^'[^']*'$/;
const OPERADORES = new Set(['+', '-', '*', '/', '%', '=', '!=', '<', '>', '<=', '>=', '||']);
const PONTUACAO = new Set(['(', ')', ',']);

export function validarExpressao(expressao) {
    if (!expressao || !expressao.trim()) {
        return { valido: false, erro: 'Expressao vazia' };
    }

    var expr = expressao.trim();

    if (PAT_INJECTION.test(expr)) {
        return { valido: false, erro: 'Expressao contem tokens proibidos' };
    }

    if (PAT_SUBQUERY.test(expr)) {
        return { valido: false, erro: 'Subqueries nao sao permitidas' };
    }

    var tokens = tokenizar(expr);

    for (var i = 0; i < tokens.length; i++) {
        var token = tokens[i];
        var upper = token.toUpperCase();

        if (FUNCOES_PROIBIDAS.has(upper)) {
            return { valido: false, erro: 'Funcao \'' + token + '\' nao compativel com SQL Server 2016' };
        }

        if (FUNCOES_PERMITIDAS.has(upper) || KEYWORDS.has(upper)) continue;
        if (OPERADORES.has(token) || PONTUACAO.has(token)) continue;
        if (PAT_NUMERO.test(token)) continue;
        if (PAT_STRING.test(token)) continue;
        if (PAT_IDENTIFICADOR.test(token)) continue;

        return { valido: false, erro: 'Token nao reconhecido: \'' + token + '\'' };
    }

    var parens = 0;
    for (var j = 0; j < tokens.length; j++) {
        if (tokens[j] === '(') parens++;
        else if (tokens[j] === ')') parens--;
        if (parens < 0) return { valido: false, erro: 'Parenteses desbalanceados' };
    }
    if (parens !== 0) return { valido: false, erro: 'Parenteses desbalanceados' };

    return { valido: true, erro: null };
}

function tokenizar(expr) {
    var tokens = [];
    var i = 0;
    var len = expr.length;

    while (i < len) {
        var c = expr[i];

        if (/\s/.test(c)) { i++; continue; }

        if (c === "'") {
            var fim = expr.indexOf("'", i + 1);
            if (fim === -1) fim = len;
            tokens.push(expr.substring(i, fim + 1));
            i = fim + 1;
            continue;
        }

        if (c === '(' || c === ')' || c === ',') {
            tokens.push(c);
            i++;
            continue;
        }

        if ('+-*/%=!<>|'.indexOf(c) !== -1) {
            var op = c;
            if (i + 1 < len) {
                var next = expr[i + 1];
                if ((c === '!' && next === '=') || (c === '<' && next === '=') ||
                    (c === '>' && next === '=') || (c === '|' && next === '|')) {
                    op += next;
                    i++;
                }
            }
            tokens.push(op);
            i++;
            continue;
        }

        if (/[a-zA-Z0-9_.]/.test(c)) {
            var word = '';
            while (i < len && /[a-zA-Z0-9_.]/.test(expr[i])) {
                word += expr[i];
                i++;
            }
            tokens.push(word);
            continue;
        }

        tokens.push(c);
        i++;
    }

    return tokens;
}
