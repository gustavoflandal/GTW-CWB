package muralha.digital.relatorios.acoesAlarmes;

import com.consilux.lib.Conexao;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioAcoesAlarmes {

    // --- Atributos Atualizados ---
    private final String fotoMimeType;
    private final String fotoBase64;
    private final String dataAlerta;
    private final String placa;
    private final String tipoOcorrenciaFato;
    private final String situacaoAtualFato;
    private final String descricaoDoFato; // NOVO
    private final boolean temBoletimFato; // NOVO
    private final String operadorAcao;
    private final String dataAcao;
    private final String descricaoAcao;
    private final String motivoDescarte;

    public RelatorioAcoesAlarmes(String fotoMimeType, String fotoBase64, String dataAlerta, String placa,
                                 String tipoOcorrenciaFato, String situacaoAtualFato, String descricaoDoFato, boolean temBoletimFato,
                                 String operadorAcao, String dataAcao, String descricaoAcao, String motivoDescarte) {
        this.fotoMimeType = fotoMimeType;
        this.fotoBase64 = fotoBase64;
        this.dataAlerta = dataAlerta;
        this.placa = placa;
        this.tipoOcorrenciaFato = tipoOcorrenciaFato;
        this.situacaoAtualFato = situacaoAtualFato;
        this.descricaoDoFato = descricaoDoFato;
        this.temBoletimFato = temBoletimFato;
        this.operadorAcao = operadorAcao;
        this.dataAcao = dataAcao;
        this.descricaoAcao = descricaoAcao;
        this.motivoDescarte = motivoDescarte;
    }

    private static String formatarDataTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(timestamp);
    }

    private static String getMimeTypeFromImageBytes(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length < 12) {
            return "application/octet-stream";
        }
        if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8 && imageBytes[2] == (byte) 0xFF) {
            return "image/jpeg";
        }
        if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == (byte) 0x50 && imageBytes[2] == (byte) 0x4E && imageBytes[3] == (byte) 0x47) {
            return "image/png";
        }
        if (imageBytes[0] == (byte) 0x47 && imageBytes[1] == (byte) 0x49 && imageBytes[2] == (byte) 0x46 && imageBytes[3] == (byte) 0x38) {
            return "image/gif";
        }
        if (imageBytes[0] == (byte) 0x52 && imageBytes[1] == (byte) 0x49 && imageBytes[2] == (byte) 0x46 && imageBytes[3] == (byte) 0x46 &&
            imageBytes[8] == (byte) 0x57 && imageBytes[9] == (byte) 0x45 && imageBytes[10] == (byte) 0x42 && imageBytes[11] == (byte) 0x50) {
            return "image/webp";
        }
        return "application/octet-stream";
    }
    
    private static String extrairTexto(String html) {
        if (html == null) return "";
        return html.replaceAll("\\<.*?\\>", " ").trim();
    }

    public static List<RelatorioAcoesAlarmes> getRelatorioData(String operadorId, String dataInicial, String dataFinal) throws SQLException {
        List<RelatorioAcoesAlarmes> dados = new ArrayList<>();
        
        String sql = "WITH UnicoAlertaVeiculo AS ( " +
                     "    SELECT id_alerta, id_veiculo_tempo_real, ROW_NUMBER() OVER(PARTITION BY id_alerta ORDER BY id_veiculo_tempo_real) as rn " +
                     "    FROM muralha.alerta_veiculo " +
                     ") " +
                     "SELECT " +
                     "    vtri.imagem AS foto, " +
                     "    a.data AS data_alerta, " +
                     "    p.placa, " +
                     "    fato.tipoOcorrencia, " +
                     "    fato.situacaoAtual, " +
                     "    fato.descricao AS fato_descricao, " +
                     "    fato.temBoletim, " +
                     "    u.nome AS operador_acao, " +
                     "    an.data_cadastro AS data_acao, " +
                     "    an.descricao AS descricao_acao, " +
                     "    md.descricao AS motivo_descarte " +
                     "FROM muralha.anotacao_contributiva an " +
                     "JOIN muralha.alerta a ON an.id_alerta = a.id " +
                     "LEFT JOIN dbo.sis_usuario u ON an.id_usuario = u.id_usuario " +
                     "LEFT JOIN UnicoAlertaVeiculo av ON a.id = av.id_alerta AND av.rn = 1 " +
                     "LEFT JOIN muralha.veiculo_tempo_real p ON av.id_veiculo_tempo_real = p.id " +
                     "LEFT JOIN muralha.veiculo_tempo_real_imagem vtri ON p.id = vtri.id_veiculo_tempo_real AND vtri.indice_imagem = 0 " +
                     "LEFT JOIN muralha.motivo_descarte md ON a.id_motivo_descarte = md.id " +
                     "OUTER APPLY ( " +
                     "    SELECT TOP 1 * " +
                     "    FROM muralha.fcn_ObterInfoRegistroFatoAlertaPorPlaca(p.placa) " +
                     "    ORDER BY data DESC " +
                     ") fato " +
                     "WHERE an.data_cadastro BETWEEN ? AND ? " +
                     "AND (? IS NULL OR an.id_usuario = ?) " +
                     "ORDER BY an.data_cadastro DESC";

        try (Connection conn = Conexao.getConexao(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dataInicial + " 00:00:00");
            pstmt.setString(2, dataFinal + " 23:59:59");
            if (operadorId != null && !operadorId.isEmpty()) {
                pstmt.setInt(3, Integer.parseInt(operadorId));
                pstmt.setInt(4, Integer.parseInt(operadorId));
            } else {
                pstmt.setNull(3, Types.INTEGER);
                pstmt.setNull(4, Types.INTEGER);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    byte[] fotoBytes = rs.getBytes("foto");
                    String mimeType = getMimeTypeFromImageBytes(fotoBytes);
                    String fotoBase64 = (fotoBytes != null) ? Base64.getEncoder().encodeToString(fotoBytes) : null;

                    dados.add(new RelatorioAcoesAlarmes(
                        mimeType,
                        fotoBase64,
                        formatarDataTimestamp(rs.getTimestamp("data_alerta")),
                        rs.getString("placa"),
                        rs.getString("tipoOcorrencia"),
                        rs.getString("situacaoAtual"),
                        rs.getString("fato_descricao"),
                        rs.getBoolean("temBoletim"),
                        rs.getString("operador_acao"),
                        formatarDataTimestamp(rs.getTimestamp("data_acao")),
                        extrairTexto(rs.getString("descricao_acao")),
                        rs.getString("motivo_descarte")
                    ));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao executar a consulta consolidada do relatório de ações.", e);
        }
        return dados;
    }

    public static List<Map<String, String>> getOperadores() throws SQLException {
        List<Map<String, String>> operadores = new ArrayList<>();
        String sql = "SELECT id_usuario, nome FROM dbo.sis_usuario ORDER BY nome";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> op = new HashMap<>();
                op.put("id_usuario", rs.getString("id_usuario"));
                op.put("nome", rs.getString("nome"));
                operadores.add(op);
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao buscar a lista de operadores.", e);
        }
        return operadores;
    }
}