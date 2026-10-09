package muralha.digital.usuario;

public final class UsuarioCpfUtil {

    private UsuarioCpfUtil() {}

    public static String normalizar(String cpf) {
        if (cpf == null) return null;
        String nums = cpf.replaceAll("[^0-9]", "");
        if (nums.length() != 11) return null;
        if (nums.chars().distinct().count() == 1) return null;
        if (!validarDigitos(nums)) return null;
        return nums;
    }

    private static boolean validarDigitos(String cpf) {
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (cpf.charAt(i) - '0') * (10 - i);
        int d1 = 11 - (sum % 11);
        if (d1 >= 10) d1 = 0;
        if (d1 != (cpf.charAt(9) - '0')) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) sum += (cpf.charAt(i) - '0') * (11 - i);
        int d2 = 11 - (sum % 11);
        if (d2 >= 10) d2 = 0;
        return d2 == (cpf.charAt(10) - '0');
    }
}
