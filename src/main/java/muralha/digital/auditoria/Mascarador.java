package muralha.digital.auditoria;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Mascaramento de dados sensíveis antes de persistir no log.
 *
 * Aplica duas camadas:
 *  1) Campos nomeados: nomes padrão (senha, token, cpf...) + os informados na
 *     regra ({@code mascarar_campos}) têm o VALOR substituído por "***".
 *  2) Conteúdo: CPF e placas (padrão antigo e Mercosul) são mascarados no texto,
 *     independentemente do nome do campo, como defesa em profundidade (LGPD).
 */
public final class Mascarador {

    // Nomes de parâmetros sempre mascarados (minúsculo).
    private static final Set<String> CAMPOS_PADRAO = new HashSet<>();
    static {
        CAMPOS_PADRAO.add("senha");
        CAMPOS_PADRAO.add("password");
        CAMPOS_PADRAO.add("pwd");
        CAMPOS_PADRAO.add("j_password");
        CAMPOS_PADRAO.add("novasenha");
        CAMPOS_PADRAO.add("confirmasenha");
        CAMPOS_PADRAO.add("token");
        CAMPOS_PADRAO.add("secret");
        CAMPOS_PADRAO.add("authorization");
    }

    // CPF: 000.000.000-00 ou 00000000000
    private static final Pattern CPF =
        Pattern.compile("\\b\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}\\b");
    // Placa antiga ABC1234 / ABC-1234 e Mercosul ABC1D23
    private static final Pattern PLACA =
        Pattern.compile("\\b[A-Za-z]{3}-?\\d[A-Za-z0-9]\\d{2}\\b");

    private Mascarador() {}

    /** True se o campo de nome informado deve ter o valor totalmente ocultado. */
    public static boolean campoSensivel(String nome, Set<String> extras) {
        if (nome == null) return false;
        String n = nome.trim().toLowerCase();
        if (CAMPOS_PADRAO.contains(n)) return true;
        return extras != null && extras.contains(n);
    }

    /** Mascara CPF e placas dentro de um texto livre. */
    public static String mascararConteudo(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        String r = CPF.matcher(texto).replaceAll("***.***.***-**");
        r = PLACA.matcher(r).replaceAll("***PLACA***");
        return r;
    }

    /** Converte a lista "a,b,c" da regra num conjunto normalizado (minúsculo). */
    public static Set<String> parseCampos(String csv) {
        Set<String> set = new HashSet<>();
        if (csv == null) return set;
        for (String p : csv.split(",")) {
            String s = p.trim().toLowerCase();
            if (!s.isEmpty()) set.add(s);
        }
        return set;
    }
}
