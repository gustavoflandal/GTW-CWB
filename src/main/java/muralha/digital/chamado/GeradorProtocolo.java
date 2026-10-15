package muralha.digital.chamado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

/** Protocolo CH-AAAA-NNNNNN com contador por ano em muralha.chamado_protocolo (spec §1.4). */
public final class GeradorProtocolo {

    private static final Pattern FORMATO = Pattern.compile("^CH-\\d{4}-\\d{6}$");
    private static final int VIOLACAO_PK = 2627;

    private GeradorProtocolo() {
    }

    public static String formatar(int ano, int numero) {
        if (numero < 1 || numero > 999_999) {
            throw new IllegalArgumentException("Número de protocolo fora da faixa: " + numero);
        }
        return String.format("CH-%04d-%06d", ano, numero);
    }

    public static boolean valido(String protocolo) {
        return protocolo != null && FORMATO.matcher(protocolo).matches();
    }

    /** Deve ser chamado dentro da transacao que grava o chamado. */
    public static String proximo(Connection conn, int ano) throws SQLException {
        Integer numero = incrementar(conn, ano);
        if (numero == null) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.chamado_protocolo (ano, ultimo) VALUES (?, 1)")) {
                ps.setInt(1, ano);
                ps.executeUpdate();
                numero = 1;
            } catch (SQLException e) {
                if (e.getErrorCode() != VIOLACAO_PK) throw e;
                numero = incrementar(conn, ano); // outra transacao criou o ano antes
            }
        }
        return formatar(ano, numero);
    }

    private static Integer incrementar(Connection conn, int ano) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE muralha.chamado_protocolo WITH (UPDLOCK, HOLDLOCK) SET ultimo = ultimo + 1 " +
                "OUTPUT inserted.ultimo WHERE ano = ?")) {
            ps.setInt(1, ano);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }
}
