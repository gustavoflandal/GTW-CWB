/**********************************************************************************
 Projeto: Muralha Digital
 Empresa: Consilux Tecnologia
 Autor: Thiago Guislotti
 Data: 31/07/2025
 **********************************************************************************/
package muralha.digital.perfilcomportamental;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.Base64;
import org.apache.log4j.Logger;
import com.consilux.lib.Conexao;

/**
 * Classe responsável por consultar informações do perfil comportamental dos veículos.
 */
public class PerfilComportamental {

    private static final Logger logger = Logger.getLogger(PerfilComportamental.class);

    /**
     * Executa uma consulta parametrizada com placa e período, retornando o resultado como lista de mapas.
     *
     * @param sql         Consulta SQL com placeholders (?, ?, ?) para placa, dataInicio e dataFim.
     * @param placa       Placa do veículo (ex: HAR5B39).
     * @param dataInicio  Data inicial do período.
     * @param dataFim     Data final do período.
     * @return Lista de linhas, onde cada linha é representada por um mapa de colunas.
     * @throws SQLException Em caso de erro de conexão ou execução da query.
     */
    private static List<Map<String, Object>> executarConsulta(String sql, String placa, Date dataInicio, Date dataFim) throws SQLException {
        List<Map<String, Object>> resultado = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, placa);
            stmt.setTimestamp(2, new Timestamp(dataInicio.getTime()));
            stmt.setTimestamp(3, new Timestamp(dataFim.getTime()));

            rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    String coluna = meta.getColumnLabel(i);
                    Object valor = rs.getObject(i);

                    if (valor instanceof String) {
                        valor = ((String) valor).trim();
                    }

                    item.put(coluna, valor);
                }
                resultado.add(item);
            }
        } catch (Exception e) {
            logger.error("Erro ao executar consulta de perfil comportamental", e);
            throw new SQLException("Erro ao executar consulta", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                logger.warn("Erro ao fechar recursos de banco", ex);
            }
        }

        return resultado;
    }
    
    /**
     * Retorna os dados do veículo (placa, marca, modelo, cor, ano de fabricação)
     * juntamente com a imagem mais recente disponível no período informado.
     *
     * A imagem é convertida para Base64 e pode ser utilizada diretamente em tags HTML <img>.
     *
     * @param placa       Placa do veículo (ex: "HAR5B39").
     * @param dataInicio  Data inicial do período (inclusive).
     * @param dataFim     Data final do período (inclusive).
     * @return Lista contendo um único mapa com os dados do veículo e imagem em Base64.
     * @throws SQLException Em caso de falha na execução da consulta SQL.
     */
    public static List<Map<String, Object>> infoVeiculo(String placa, Date dataInicio, Date dataFim) throws SQLException {
        String sql = "SELECT * FROM muralha.fcn_perfil_comportamental_info_veiculo(?, ?, ?);";
        List<Map<String, Object>> resultado = executarConsulta(sql, placa, dataInicio, dataFim);

        for (Map<String, Object> item : resultado) {
            Object imagem = item.get("imagem");
            if (imagem instanceof byte[]) {
                String base64 = Base64.getEncoder().encodeToString((byte[]) imagem);
                item.put("imagem", base64);
            }
        }

        return resultado;
    }

    /**
     * Retorna a quantidade de passagens por PCL agrupadas por dia da semana,
     * com os dados dos locais e áreas monitoradas.
     *
     * @param placa       Placa do veículo.
     * @param dataInicio  Início do período.
     * @param dataFim     Fim do período.
     * @return Lista com os dados agrupados por local e dia da semana.
     * @throws SQLException Em caso de erro na consulta.
     */
    public static List<Map<String, Object>> passagensPorDiaComMancha(String placa, Date dataInicio, Date dataFim) throws SQLException {
        String sql = "SELECT * FROM muralha.fcn_perfil_comportamental_passagens_por_dia_com_mancha(?, ?, ?) " +
                "ORDER BY id_local, dia_semana, total_passagens DESC;";
        return executarConsulta(sql, placa, dataInicio, dataFim);
    }

    /**
     * Retorna a quantidade de passagens por PCL agrupadas por dia da semana e hora,
     * com os dados dos locais e áreas monitoradas (manchas).
     *
     * @param placa       Placa do veículo.
     * @param dataInicio  Início do período.
     * @param dataFim     Fim do período.
     * @return Lista com os dados agrupados por local, dia e hora.
     * @throws SQLException Em caso de erro na consulta.
     */
    public static List<Map<String, Object>> passagensPorDiaHoraComMancha(String placa, Date dataInicio, Date dataFim) throws SQLException {
        String sql = "SELECT * FROM muralha.fcn_perfil_comportamental_passagens_por_dia_hora_com_mancha(?, ?, ?) " +
                "ORDER BY id_local, dia_semana, hora, total_passagens DESC;";
        return executarConsulta(sql, placa, dataInicio, dataFim);
    }

    /**
     * Retorna a quantidade total de passagens por PCL, com dados do local e da área monitorada.
     *
     * @param placa       Placa do veículo.
     * @param dataInicio  Início do período.
     * @param dataFim     Fim do período.
     * @return Lista com os dados agrupados por local.
     * @throws SQLException Em caso de erro na consulta.
     */
    public static List<Map<String, Object>> passagensPorPclComMancha(String placa, Date dataInicio, Date dataFim) throws SQLException {
        String sql = "SELECT * FROM muralha.fcn_perfil_comportamental_passagens_por_pcl_com_mancha(?, ?, ?) ORDER BY id_local;";
        return executarConsulta(sql, placa, dataInicio, dataFim);
    }

    /**
     * Retorna o tempo de permanência entre áreas monitoradas (manchas),
     * com base nas transições entre PCLs consecutivos.
     * Os dados incluem identificadores e nomes das manchas de entrada e saída,
     * quantidade de transições e tempo total de estadia entre elas.
     *
     * @param placa       Placa do veículo.
     * @param dataInicio  Início do período.
     * @param dataFim     Fim do período.
     * @return Lista com informações de permanência entre manchas.
     * @throws SQLException Em caso de erro na consulta.
     */
    public static List<Map<String, Object>> estadiaPorManchas(String placa, Date dataInicio, Date dataFim) throws SQLException {
        String sql = "SELECT * FROM muralha.fcn_perfil_comportamental_estadia_por_manchas(?, ?, ?) ORDER BY id_area_monitorada_entrada, qtd_transicoes_entre_manchas DESC;";
        return executarConsulta(sql, placa, dataInicio, dataFim);
    }
    
    
    /**
     * @param placa       Placa do veículo.
     * @param dataInicio  Início do período.
     * @param dataFim     Fim do período.
     * @return Lista com os detalhes de cada passagem.
     * @throws SQLException Em caso de erro na consulta.
     */
    public static List<Map<String, Object>> passagensIndividuais(String placa, Date dataInicio, Date dataFim) throws SQLException {
        String sql = "SELECT vtr.id_local, lv.nome AS nome_local, lv.serie_equipamento, vtr.placa, vtr.data, vtr.id_pista, vtr.velocidade " +
                     "FROM muralha.veiculo_tempo_real AS vtr " +
                     "INNER JOIN dbo.local_vigente AS lv ON vtr.id_local = lv.id_local " +
                     "WHERE vtr.placa = ? AND vtr.data BETWEEN ? AND ? " +
                     "ORDER BY vtr.data ASC;";
        return executarConsulta(sql, placa, dataInicio, dataFim);
    }
}