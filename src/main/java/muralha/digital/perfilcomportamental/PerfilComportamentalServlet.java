/**********************************************************************************
 Projeto: Muralha Digital
 Empresa: Consilux Tecnologia
 Autor: Thiago Guislotti
 Data: 31/07/2025
 **********************************************************************************/
package muralha.digital.perfilcomportamental;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Servlet responsável por processar as requisições de perfil comportamental de veículos.
 */
@WebServlet("/MuralhaDigital/PerfilComportamental")
public class PerfilComportamentalServlet extends HttpServlet {

/**
     * Identificador de versão para controle de serialização da classe.
     * Utilizado internamente pelo Java para garantir compatibilidade de versões durante a serialização.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger da aplicação. Usado para registrar mensagens de log no servidor (info, warn, error).
     */
    private static final Logger logger = Logger.getLogger(PerfilComportamentalServlet.class);

    /**
     * Instância do Gson com configuração para serializar valores nulos.
     * Responsável por converter objetos Java para JSON e vice-versa.
     */
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processarRequisicao(request, response);
    }

    private void processarRequisicao(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	// Habilita CORS apenas para chamadas vindas do front local
        String origin = request.getHeader("Origin");
        if (origin != null && (origin.contains("localhost") || origin.contains("127.0.0.1"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String placa = request.getParameter("placa");
        String dataInicioStr = request.getParameter("dataInicio");
        String dataFimStr = request.getParameter("dataFim");
        String horaInicioStr = request.getParameter("horaInicio");
        String horaFimStr = request.getParameter("horaFim");
        String tipoConsulta = request.getParameter("tipoConsulta");

        Map<String, Object> retorno = new HashMap<>();

        try (PrintWriter out = response.getWriter()) {
            if (placa == null || dataInicioStr == null || dataInicioStr.isEmpty() ||
                dataFimStr == null || dataFimStr.isEmpty() || tipoConsulta == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                retorno.put("erro", "Parâmetros obrigatórios: placa, dataInicio, dataFim, tipoConsulta");
                out.print(gson.toJson(retorno));
                return;
            }

            //Garante que a hora não seja nula ou vazia antes de montar a string final
            if (horaInicioStr == null || horaInicioStr.isEmpty()) {
                horaInicioStr = "00:00";
            }
            if (horaFimStr == null || horaFimStr.isEmpty()) {
                horaFimStr = "23:59";
            }

            //Monta a string de data/hora completa, garantindo que os segundos estejam presentes
            String dataHoraInicioCompleta = dataInicioStr + " " + horaInicioStr + ":00";
            String dataHoraFimCompleta = dataFimStr + " " + horaFimStr + ":59";

            SimpleDateFormat sdfCompleto = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date dataInicio = sdfCompleto.parse(dataHoraInicioCompleta);
            Date dataFim = sdfCompleto.parse(dataHoraFimCompleta);

            List<Map<String, Object>> resultado;

            switch (tipoConsulta.toLowerCase()) {
                case "info":
                    resultado = PerfilComportamental.infoVeiculo(placa, dataInicio, dataFim);
                    break;
                case "pordia":
                    resultado = PerfilComportamental.passagensPorDiaComMancha(placa, dataInicio, dataFim);
                    break;
                case "porhora":
                    resultado = PerfilComportamental.passagensPorDiaHoraComMancha(placa, dataInicio, dataFim);
                    break;
                case "porpcl":
                    resultado = PerfilComportamental.passagensPorPclComMancha(placa, dataInicio, dataFim);
                    break;
                case "permanencia":
                    resultado = PerfilComportamental.estadiaPorManchas(placa, dataInicio, dataFim);
                    break;
                case "passagensindividuais":
                    resultado = PerfilComportamental.passagensIndividuais(placa, dataInicio, dataFim);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    retorno.put("erro", "Valor inválido para tipoConsulta");
                    out.print(gson.toJson(retorno));
                    return;
            }

            out.print(gson.toJson(resultado));
        } catch (Exception e) {
            logger.error("Erro ao processar perfil comportamental", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                retorno.put("erro", "Erro interno: " + e.getMessage());
                out.print(gson.toJson(retorno));
            }
        }
    }
}