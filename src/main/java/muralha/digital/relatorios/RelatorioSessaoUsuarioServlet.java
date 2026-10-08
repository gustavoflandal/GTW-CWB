
package muralha.digital.relatorios;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
// import java.io.PrintWriter;
// import java.util.List;
// import java.util.Map;

@WebServlet("/MuralhaDigital/Relatorios/SessaoUsuario")
public class RelatorioSessaoUsuarioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson gson = new GsonBuilder().create();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String acao = request.getParameter("acao");
        String dataInicial = request.getParameter("dataInicial");
        String dataFinal   = request.getParameter("dataFinal");
        String usuarioStr  = request.getParameter("usuario");

        try {
            if ("navegacao".equalsIgnoreCase(acao)) {
                // Navegação do usuário
                if (usuarioStr == null || usuarioStr.isEmpty() || dataInicial == null || dataFinal == null || dataInicial.isEmpty() || dataFinal.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().print("{\"erro\":\"Parâmetros obrigatórios ausentes.\"}");
                    return;
                }
                int idUsuario = Integer.parseInt(usuarioStr);
                RelatorioSessao relatorio = new RelatorioSessao("", ""); // datas não usadas para navegação
                List<java.util.Map.Entry<String, String>> lista = relatorio.getDadosNavegacao(idUsuario, dataInicial, dataFinal);
                // Monta lista de objetos para JSON
                List<Map<String, String>> jsonList = new ArrayList<>();
                for (Map.Entry<String, String> entry : lista) {
                    Map<String, String> obj = new HashMap<>();
                    obj.put("descricao", entry.getKey());
                    obj.put("data", entry.getValue());
                    jsonList.add(obj);
                }
                response.getWriter().print(gson.toJson(jsonList));
                return;
            }

            // Sessões padrão
            if (dataInicial == null || dataFinal == null || dataInicial.isEmpty() || dataFinal.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"erro\":\"As datas inicial e final são obrigatórias.\"}");
                return;
            }

            RelatorioSessao relatorio = new RelatorioSessao(dataInicial, dataFinal);
            response.getWriter().print(gson.toJson(relatorio.getDados()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\":\"Erro interno no servidor: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}
