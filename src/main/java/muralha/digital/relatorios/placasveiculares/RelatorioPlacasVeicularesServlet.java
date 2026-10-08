/**********************************************************************************
    Projeto: Muralha Digital
    Empresa: Consilux Tecnologia
    Autor: Thiago Guislotti
    Data: 27/09/2025
 **********************************************************************************/
package muralha.digital.relatorios.placasveiculares;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Servlet responsável por processar as requisições do Relatório Histórico de Correção de Placas Veiculares.
 * 
 * Este servlet gerencia consultas sobre placas veiculares que foram corrigidas pelos operadores,
 * exibindo informações como identificação do operador, placa anterior, nova placa, dados e hora da correção.
 */
@WebServlet("/MuralhaDigital/RelatorioPlacasVeiculares")
public class RelatorioPlacasVeicularesServlet extends HttpServlet {

    /**
     * Identificador de versão para controle de serialização da classe.
     * Utilizado internamente pelo Java para garantir compatibilidade de versões durante a serialização.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger da aplicação. Usado para registrar mensagens de log no servidor (info, warn, error).
     */
    private static final Logger logger = Logger.getLogger(RelatorioPlacasVeicularesServlet.class);

    /**
     * Instância do Gson com configuração para serializar valores nulos.
     * Responsável por converter objetos Java para JSON e vice-versa.
     */
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * Formato padrão utilizado para interpretar e formatar as datas recebidas na requisição.
     * Espera o formato: yyyy-MM-dd (ex: 2025-09-27).
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static final int LIMITE_MAXIMO_DIAS = 365;

    static {
        sdf.setLenient(false);
    }

    /**
     * Processa requisições GET para consulta do relatório de placas veiculares.
     * 
     * @param request  Requisição HTTP contendo os parâmetros de consulta.
     * @param response Resposta HTTP onde será retornado o JSON com os dados.
     * @throws ServletException Em caso de erro de servlet.
     * @throws IOException Em caso de falha de I/O ao escrever a resposta.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String placas = request.getParameter("placas");
        String dataInicioStr = request.getParameter("dataInicio");
        String dataFinalStr = request.getParameter("dataFinal");

        Map<String, Object> retorno = new HashMap<>();
        PrintWriter out = response.getWriter();

        try {
            // Validações básicas
            if (dataInicioStr == null || dataInicioStr.isEmpty() || dataFinalStr == null || dataFinalStr.isEmpty()) {
                throw new IllegalArgumentException("Parâmetros obrigatórios: dataInicio, dataFinal");
            }

            // Conversão de datas
            Date dataInicio = sdf.parse(dataInicioStr);
            Date dataFinal = sdf.parse(dataFinalStr);

            // Validação do período
            if (dataInicio.after(dataFinal)) {
                throw new IllegalArgumentException("A data inicial não pode ser maior que a data final");
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date hoje = calendar.getTime();

            if (dataInicio.after(hoje) || dataFinal.after(hoje)) {
                throw new IllegalArgumentException("As datas não podem ser maiores que a data atual");
            }

            long diffDias = TimeUnit.MILLISECONDS.toDays(dataFinal.getTime() - dataInicio.getTime());
            if (diffDias > LIMITE_MAXIMO_DIAS) {
                throw new IllegalArgumentException("O período não pode ser superior a " + LIMITE_MAXIMO_DIAS + " dias");
            }

            // Normalização das placas (opcional)
            String placasNormalizadas = null;
            if (placas != null && !placas.trim().isEmpty()) {
                placasNormalizadas = placas.trim().toUpperCase().replaceAll("[^A-Z0-9,]", "");
            }

            List<Map<String, Object>> resultado = RelatorioPlacasVeiculares.consultarHistoricoCorrecaoPlacas(
                placasNormalizadas, dataInicio, dataFinal);

            retorno.put("success", true);
            retorno.put("dados", resultado);
            retorno.put("totalRegistros", resultado.size());

            out.print(gson.toJson(retorno));

        } catch (IllegalArgumentException | ParseException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            retorno.put("success", false);
            retorno.put("erro", ex.getMessage());
            out.print(gson.toJson(retorno));
        } catch (Exception e) {
            logger.error("Erro ao processar relatório de placas veiculares", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            retorno.put("success", false);
            retorno.put("erro", "Erro interno: " + e.getMessage());
            out.print(gson.toJson(retorno));
        } finally {
            out.flush();
        }
    }
}