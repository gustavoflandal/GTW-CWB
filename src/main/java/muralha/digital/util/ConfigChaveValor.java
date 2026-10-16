package muralha.digital.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Leitura e gravação de parâmetros em muralha.config_chave_valor
 * (chave VARCHAR(100) PK, valor VARCHAR(255)).
 */
public final class ConfigChaveValor {

    private ConfigChaveValor() {}

    /** Valor da chave, ou {@code padrao} se a chave não existir ou estiver vazia. */
    public static String obter(Connection conn, String chave, String padrao) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.config_chave_valor WHERE chave = ?")) {
            ps.setString(1, chave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String valor = rs.getString(1);
                    if (valor != null && !valor.trim().isEmpty()) return valor.trim();
                }
            }
        }
        return padrao;
    }

    /** Valor inteiro da chave; {@code padrao} se ausente ou inválido. */
    public static int obterInt(Connection conn, String chave, int padrao) throws SQLException {
        String valor = obter(conn, chave, null);
        if (valor == null) return padrao;
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    /** Insere ou atualiza a chave. */
    public static void gravar(Connection conn, String chave, String valor) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "MERGE muralha.config_chave_valor AS t " +
                "USING (SELECT ? AS chave, ? AS valor) AS s ON t.chave = s.chave " +
                "WHEN MATCHED THEN UPDATE SET valor = s.valor " +
                "WHEN NOT MATCHED THEN INSERT (chave, valor) VALUES (s.chave, s.valor);")) {
            ps.setString(1, chave);
            ps.setString(2, valor);
            ps.executeUpdate();
        }
    }
}
