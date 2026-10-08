package muralha.digital.relatorios.pesquisaVeiculosRealizados;

import com.consilux.lib.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class pesquisaVeiculosRealizados {

    private String nomeOperador;
    private String dataHoraPesquisa;
    private String placaPesquisada;
    private String motivo;

    public pesquisaVeiculosRealizados(String nomeOperador, String dataHoraPesquisa, String placaPesquisada, String motivo) {
        this.nomeOperador = nomeOperador;
        this.dataHoraPesquisa = dataHoraPesquisa;
        this.placaPesquisada = placaPesquisada;
        this.motivo = motivo;
    }

    public static List<pesquisaVeiculosRealizados> getRelatorioData(String operadorId, String placa, String dataInicial, String dataFinal) throws SQLException {
        List<pesquisaVeiculosRealizados> dados = new ArrayList<>();
        
        String sql = "SELECT u.nome, m.data_criacao, m.placa, m.motivo " +
                     "FROM muralha.motivo_solicitacao_relatorio m " +
                     "JOIN dbo.sis_usuario u ON m.id_usuario = u.id_usuario " +
                     "WHERE (? IS NULL OR m.id_usuario = ?) " +
                     "AND m.data_criacao BETWEEN ? AND ? ";

        if (placa != null && !placa.isEmpty()) {
            sql += "AND m.placa LIKE ? ";
        }
        sql += "ORDER BY m.data_criacao DESC";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;

            if (operadorId != null && !operadorId.isEmpty()) {
                ps.setInt(paramIndex++, Integer.parseInt(operadorId));
                ps.setInt(paramIndex++, Integer.parseInt(operadorId));
            } else {
                ps.setNull(paramIndex++, Types.INTEGER);
                ps.setNull(paramIndex++, Types.INTEGER);
            }

            ps.setString(paramIndex++, dataInicial + " 00:00:00");
            ps.setString(paramIndex++, dataFinal + " 23:59:59");
            
            if (placa != null && !placa.isEmpty()) {
                ps.setString(paramIndex++, "%" + placa + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                while (rs.next()) {
                    dados.add(new pesquisaVeiculosRealizados(
                        rs.getString("nome"),
                        sdf.format(rs.getTimestamp("data_criacao")),
                        rs.getString("placa"),
                        rs.getString("motivo")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Erro ao buscar dados do relatório.", e);
        }

        return dados;
    }

    public static List<Map<String, String>> getOperadores() throws SQLException {
        List<Map<String, String>> operadores = new ArrayList<>();
        
        String sql = "SELECT DISTINCT u.id_usuario, u.nome " +
                     "FROM dbo.sis_usuario u " +
                     "JOIN muralha.motivo_solicitacao_relatorio m ON u.id_usuario = m.id_usuario " +
                     "ORDER BY u.nome ASC";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, String> operador = new HashMap<>();
                operador.put("id_usuario", rs.getString("id_usuario"));
                operador.put("nome", rs.getString("nome"));
                operadores.add(operador);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Erro ao buscar operadores.", e);
        }
        return operadores;
    }

    // Getters
    public String getNomeOperador() { return nomeOperador; }
    public String getDataHoraPesquisa() { return dataHoraPesquisa; }
    public String getPlacaPesquisada() { return placaPesquisada; }
    public String getMotivo() { return motivo; }
}