package muralha.digital.relatorios.exportarImagens;

import com.consilux.lib.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioExportarImagens {

    // Campos da classe
    private int id_usuario;
    private String placa;
    private String dataHoraExportacao;
    private String dataHoraPassagem;
    private String nomeOperador;
    private String pontoCaptura;

    // Construtor
    public RelatorioExportarImagens(int id_usuario, String placa, String dataHoraExportacao, String dataHoraPassagem, String nomeOperador, String pontoCaptura) {
        this.id_usuario = id_usuario;
        this.placa = placa;
        this.dataHoraExportacao = dataHoraExportacao;
        this.dataHoraPassagem = dataHoraPassagem;
        this.nomeOperador = nomeOperador;
        this.pontoCaptura = pontoCaptura;
    }

    private static String formatarData(String dataCompleta) {
        if (dataCompleta != null && dataCompleta.length() >= 16) {
            String parteData = dataCompleta.substring(0, 10);
            String parteHora = dataCompleta.substring(11, 16);
            String[] partesData = parteData.split("-");
            if (partesData.length == 3) {
                return partesData[2] + "/" + partesData[1] + "/" + partesData[0] + " " + parteHora;
            }
        }
        return dataCompleta;
    }

    public static List<RelatorioExportarImagens> getRelatorioData(String operadorId, String dataInicial, String dataFinal) throws SQLException {
        List<RelatorioExportarImagens> dados = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    r.id_usuario, " +
                     "    ISNULL(r.placa, vtr.placa) AS placa, " +
                     "    r.data_hora_exportacao, " +
                     "    ISNULL(r.data_hora_passagem, vtr.data) AS data_hora_passagem, " +
                     "    u.nome AS nome_operador, " +
                     "    CAST(lv.serie_equipamento AS VARCHAR) + ' - ' + RTRIM(lv.nome) AS ponto_captura " +
                     "FROM muralha.relatorio_imagens_exportadas r " +
                     "LEFT JOIN dbo.sis_usuario u ON r.id_usuario = u.id_usuario " +
                     "LEFT JOIN dbo.local_vigente lv ON r.id_local = lv.id_local " +

                     "LEFT JOIN muralha.veiculo_tempo_real vtr ON CAST(r.id_veiculo_tempo_real AS VARCHAR(36)) = CAST(vtr.id AS VARCHAR(36)) " +
                     "WHERE (? IS NULL OR r.id_usuario = ?) " +
                     "AND (r.data_hora_exportacao BETWEEN ? AND ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (operadorId != null && !operadorId.isEmpty()) {
                pstmt.setInt(1, Integer.parseInt(operadorId));
                pstmt.setInt(2, Integer.parseInt(operadorId));
            } else {
                pstmt.setNull(1, Types.INTEGER);
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, dataInicial + " 00:00:00");
            pstmt.setString(4, dataFinal + " 23:59:59");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dados.add(new RelatorioExportarImagens(
                            rs.getInt("id_usuario"),
                            rs.getString("placa"),
                            formatarData(rs.getString("data_hora_exportacao")),
                            formatarData(rs.getString("data_hora_passagem")),
                            rs.getString("nome_operador"),
                            rs.getString("ponto_captura")
                    ));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao executar a consulta de relatório. Query: " + sql, e);
        }
        return dados;
    }

    public static List<Map<String, String>> getOperadores() throws SQLException {
        List<Map<String, String>> operadores = new ArrayList<>();
        String sql = "SELECT DISTINCT u.id_usuario, u.nome " +
                     "FROM dbo.sis_usuario u " +
                     "JOIN muralha.relatorio_imagens_exportadas r ON u.id_usuario = r.id_usuario " +
                     "ORDER BY u.nome";

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
    
    // Getters
    public int getId_usuario() { return id_usuario; }
    public String getPlaca() { return placa; }
    public String getDataHoraExportacao() { return dataHoraExportacao; }
    public String getDataHoraPassagem() { return dataHoraPassagem; }
    public String getNomeOperador() { return nomeOperador; }
    public String getPontoCaptura() { return pontoCaptura; }
}