package muralha.digital.relatorios.auditoria;

import com.consilux.lib.Conexao;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioAuditoria {

    // Atributos para o JSON de resposta
    private final String usuario;
    private final String tipoAcao;
    private final String modulo;
    private final String detalheAcao;
    private final String dataHora;

    // Construtor
    public RelatorioAuditoria(String usuario, String tipoAcao, String modulo, String detalheAcao, String dataHora) {
        this.usuario = usuario;
        this.tipoAcao = tipoAcao;
        this.modulo = modulo;
        this.detalheAcao = detalheAcao;
        this.dataHora = dataHora;
    }
    
    // Método auxiliar para formatar o timestamp
    private static String formatarDataTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(timestamp);
    }

    /**
     * Busca os dados principais do relatório de auditoria.
     */
    public static List<RelatorioAuditoria> getRelatorioData(String usuarioId, String dataInicial, String dataFinal) throws SQLException {
        List<RelatorioAuditoria> dados = new ArrayList<>();
        String sql = "SELECT u.nome AS usuario, resultado.tipo_acao, resultado.modulo, resultado.detalhe_acao, resultado.data_hora " +
                     "FROM dbo.sis_usuario u " +
                     "JOIN ( " +
                     "    SELECT id_usuario, 'ACESSO' AS tipo_acao, 'SISTEMA' AS modulo, descricao AS detalhe_acao, data AS data_hora FROM dbo.sis_log WHERE descricao LIKE 'Login efetuado%' " +
                     "    UNION ALL " +
                     "    SELECT id_usuario, 'CADASTRO/ALTERAÇÃO', 'REGISTRO DE FATO', 'Criação/Alteração de Registro de Fato', data_criacao FROM muralha.registro_fato " +
                     "    UNION ALL " +
                     "    SELECT id_usuario, 'CADASTRO/ALTERAÇÃO', 'BOLETIM', 'Criação/Alteração de Boletim', data_criacao FROM muralha.boletim " +
                     "    UNION ALL " +
                     "    SELECT l.id_usuario, CASE WHEN l.descricao LIKE '%cadastr%' THEN 'CADASTRO' WHEN l.descricao LIKE '%alter%' THEN 'ALTERAÇÃO' WHEN l.descricao LIKE '%exclu%' THEN 'EXCLUSÃO' ELSE 'OUTRA AÇÃO' END, " +
                     "    'SISTEMA', l.descricao, l.data FROM dbo.sis_log l WHERE l.descricao LIKE '%cadastr%' OR l.descricao LIKE '%alter%' OR l.descricao LIKE '%exclu%' " +
                     ") AS resultado ON u.id_usuario = resultado.id_usuario " +
                     "WHERE (? IS NULL OR resultado.id_usuario = ?) " +
                     "AND resultado.data_hora BETWEEN ? AND ? " +
                     "ORDER BY resultado.data_hora DESC";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (usuarioId != null && !usuarioId.isEmpty()) {
                pstmt.setInt(1, Integer.parseInt(usuarioId));
                pstmt.setInt(2, Integer.parseInt(usuarioId));
            } else {
                pstmt.setNull(1, Types.INTEGER);
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, dataInicial + " 00:00:00");
            pstmt.setString(4, dataFinal + " 23:59:59");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dados.add(new RelatorioAuditoria(
                        rs.getString("usuario"),
                        rs.getString("tipo_acao"),
                        rs.getString("modulo"),
                        rs.getString("detalhe_acao"),
                        formatarDataTimestamp(rs.getTimestamp("data_hora"))
                    ));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao executar a consulta do relatório de auditoria.", e);
        }
        return dados;
    }

    /**
     * Busca a lista de usuários para o filtro.
     */
    public static List<Map<String, String>> getUsuarios() throws SQLException {
        List<Map<String, String>> usuarios = new ArrayList<>();
        String sql = "SELECT id_usuario, nome FROM dbo.sis_usuario ORDER BY nome";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> op = new HashMap<>();
                op.put("id_usuario", rs.getString("id_usuario")); // Chave ajustada
                op.put("nome", rs.getString("nome"));             // Chave ajustada
                usuarios.add(op);
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao buscar a lista de usuários.", e);
        }
        return usuarios;
    }
}