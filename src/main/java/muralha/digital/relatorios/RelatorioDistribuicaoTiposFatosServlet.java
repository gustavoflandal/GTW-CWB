package muralha.digital.relatorios;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;

@WebServlet("/relatorio/RelatorioDistribuicaoTiposFatosServlet")
public class RelatorioDistribuicaoTiposFatosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RelatorioDistribuicaoTiposFatosServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Verificar acesso
            Acesso acesso = new Acesso(request, response, true);
            
            // Apenas buscar dados
            buscarDadosDistribuicaoTiposFatos(request, response);
        } catch (Exception e) {
            logger.error("Erro geral no servlet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\": \"Erro interno do servidor\"}");
        }
    }

    private void buscarDadosDistribuicaoTiposFatos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();

            // Obter parâmetros de data
            String dataInicio = request.getParameter("dataInicio");
            String dataFim = request.getParameter("dataFim");

            // Validar e definir datas padrão se necessário
            if (dataInicio == null || dataInicio.trim().isEmpty() ||
                    dataFim == null || dataFim.trim().isEmpty()) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dataFimDate = new Date();
                Date dataInicioDate = new Date();
                dataInicioDate.setTime(dataInicioDate.getTime() - (30L * 24 * 60 * 60 * 1000)); // 30 dias atrás

                dataInicio = sdf.format(dataInicioDate);
                dataFim = sdf.format(dataFimDate);
            }

            logger.info("Buscando dados de distribuição de tipos de fatos - Data Início: '" + dataInicio
                    + "', Data Fim: '" + dataFim + "'");

            // Chamar a procedure
            String sql = "EXEC Muralha.spu_RelatorioDistribuicaoFatos ?, ?";
            logger.info("Executando procedure: " + sql + " com parâmetros: [" + dataInicio + ", " + dataFim + "]");

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, dataInicio);
            stmt.setString(2, dataFim);

            rs = stmt.executeQuery();

            // A procedure deve retornar o JSON diretamente
            String jsonResult = null;
            if (rs.next()) {
                jsonResult = rs.getString(1);
            }

            // Configurar resposta
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            if (jsonResult != null && !jsonResult.trim().isEmpty()) {
                out.write(jsonResult);
                logger.info("Dados retornados com sucesso");
            } else {
                // Retornar array vazio se não houver dados
                out.write("[]");
                logger.info("Nenhum dado encontrado para o período especificado");
            }

        } catch (SQLException e) {
            logger.error("Erro SQL ao buscar dados de distribuição de tipos de fatos", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\": \"Erro ao buscar dados: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Erro geral ao buscar dados de distribuição de tipos de fatos", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\": \"Erro interno do servidor\"}");
        } finally {
            // Fechar recursos
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.error("Erro ao fechar recursos de banco", e);
            }
        }
    }

}
