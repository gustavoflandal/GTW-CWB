package muralha.digital.relatorios.auditoria;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/MuralhaDigital/Relatorios/Auditoria")
public class RelatorioAuditoriaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson gson = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    
        String acao = request.getParameter("acao");

        try (PrintWriter out = response.getWriter()) {
            if ("getUsuarios".equals(acao)) {
                List<Map<String, String>> usuarios = RelatorioAuditoria.getUsuarios();
                out.print(gson.toJson(usuarios));
            } else if ("getRelatorio".equals(acao)) {
                String usuarioId = request.getParameter("usuario");
                String dataInicial = request.getParameter("dataInicial");
                String dataFinal = request.getParameter("dataFinal");

                if (dataInicial == null || dataFinal == null || dataInicial.isEmpty() || dataFinal.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"erro\":\"As datas inicial e final são obrigatórias.\"}");
                    return;
                }

                List<RelatorioAuditoria> resultado = RelatorioAuditoria.getRelatorioData(usuarioId, dataInicial, dataFinal);
                out.print(gson.toJson(resultado));
            } else {
                 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                 out.print("{\"erro\":\"Ação inválida ou não especificada.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\":\"Erro interno no servidor: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}