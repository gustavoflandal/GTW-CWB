/**********************************************************************************
    Projeto: Muralha Digital
    Empresa: Consilux Tecnologia
    Autor: Thiago Guislotti
    Data: 27/09/2025
 **********************************************************************************/
package muralha.digital.relatorios.placasveiculares;

import java.sql.*;
import java.util.*;
import java.util.Date;

import org.apache.log4j.Logger;
import com.consilux.lib.Conexao;

/**
 * Classe responsável por consultar informações do histórico de correção de placas veiculares.
 * 
 * Utiliza a stored procedure muralha.spu_relatorio_placas_veiculares para obter dados
 * sobre placas que foram corrigidas pelos operadores do sistema.
 */
public class RelatorioPlacasVeiculares {

    /**
     * Logger para registrar eventos e erros da classe.
     */
    private static final Logger logger = Logger.getLogger(RelatorioPlacasVeiculares.class);

    /**
     * Consulta o histórico de correção de placas veiculares.
     * 
     * Retorna informações sobre placas veiculares que foram corrigidas pelos operadores,
     * incluindo identificação do operador, placa anterior, nova placa, dados e hora da correção.
     * Utiliza a stored procedure muralha.spu_relatorio_placas_veiculares.
     *
     * @param placas      Placas dos veículos para filtro (opcional - pode ser nulo ou vazio).
     * @param dataInicio  Data inicial do período de consulta.
     * @param dataFinal   Data final do período de consulta.
     * @return Lista de mapas contendo os dados do relatório.
     * @throws SQLException Em caso de falha na execução da consulta SQL.
     */
    public static List<Map<String, Object>> consultarHistoricoCorrecaoPlacas(String placas, Date dataInicio, Date dataFinal) throws SQLException {
        List<Map<String, Object>> resultado = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();

            // Converte string de placas em lista
            List<String> listaPlacas = new ArrayList<>();
            if (placas != null && !placas.trim().isEmpty()) {
                String[] arrayPlacas = placas.trim().split(",");
                for (String placa : arrayPlacas) {
                    String placaLimpa = placa.trim();
                    if (!placaLimpa.isEmpty()) {
                        listaPlacas.add(placaLimpa);
                    }
                }
            }

            // Monta SQL dinâmico para passagem de lista de placas
            StringBuilder sql = new StringBuilder();
            sql.append("SET NOCOUNT ON; "); // <-- ADICIONADO
            sql.append("DECLARE @placas dbo.udtt_ListaPlacas; ");
            if (!listaPlacas.isEmpty()) {
                for (int i = 0; i < listaPlacas.size(); i++) {
                    sql.append("INSERT INTO @placas (placa) VALUES (?); ");
                }
            }

            // Chama stored procedure com parâmetros
            sql.append("EXEC muralha.spu_relatorio_placas_veiculares @placas, ?, ?;");
            
            int paramIndex = 1;
            stmt = conn.prepareStatement(sql.toString());
            
            // Define parâmetros das placas
            for (String placa : listaPlacas) {
                stmt.setString(paramIndex++, placa);
            }

            // Define parâmetros de datas
            stmt.setTimestamp(paramIndex++, new Timestamp(dataInicio.getTime()));
            stmt.setTimestamp(paramIndex, new Timestamp(dataFinal.getTime()));

            // EXECUÇÃO: usar executeQuery() (sem loop)  <-- ALTERADO
            rs = stmt.executeQuery();

            if (rs != null) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();

                    for (int i = 1; i <= colCount; i++) {
                        String coluna = meta.getColumnLabel(i);
                        Object valor = rs.getObject(i);
                        item.put(coluna, valor);
                    }

                    resultado.add(item);
                }
            }

        } catch (Exception e) {
            logger.error("Erro ao executar stored procedure spu_relatorio_placas_veiculares", e);
            throw new SQLException("Erro ao executar consulta do relatório de placas veiculares", e);
        } finally {
            // Fechamento dos recursos
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
}