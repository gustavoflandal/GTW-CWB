package muralha.digital.relatorios.exportarImagens;

import com.consilux.model.Acesso; // Import necessário para validação
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/MuralhaDigital/Relatorios/ExportarImagens")
public class RelatoriosExportarImagensServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson gson = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    
        String acao = request.getParameter("acao");
        System.out.println("[RelatoriosExportarImagensServlet] Ação recebida: " + acao);

        try (PrintWriter out = response.getWriter()) {
            if ("getOperadores".equals(acao)) {
                System.out.println("[RelatoriosExportarImagensServlet] Buscando operadores...");
                List<Map<String, String>> operadores = RelatorioExportarImagens.getOperadores();
                System.out.println("[RelatoriosExportarImagensServlet] Operadores encontrados: " + operadores.size());
                out.print(gson.toJson(operadores));
            
            } else if ("getRelatorio".equals(acao)) {
                String operador = request.getParameter("operador");
                String dataInicial = request.getParameter("dataInicial");
                String dataFinal = request.getParameter("dataFinal");

                if (dataInicial == null || dataFinal == null || dataInicial.isEmpty() || dataFinal.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"erro\":\"As datas inicial e final são obrigatórias.\"}");
                    return;
                }

                List<RelatorioExportarImagens> resultado = RelatorioExportarImagens.getRelatorioData(operador, dataInicial, dataFinal);
                out.print(gson.toJson(resultado));
            
            } else {
                 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                 out.print("{\"erro\":\"Ação inválida ou não especificada.\"}");
            }
        } catch (Exception e) {
            System.err.println("[RelatoriosExportarImagensServlet] ERRO: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\":\"Erro interno no servidor: " + e.getMessage() + "\"}");
        }
    }
}