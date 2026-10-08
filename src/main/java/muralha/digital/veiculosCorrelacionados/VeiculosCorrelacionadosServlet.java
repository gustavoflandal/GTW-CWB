/**********************************************************************************
  Projeto: Muralha Digital
  Empresa: Consilux Tecnologia
  Autor: Thiago Guislotti
  Data: 29/05/2025
 *********************************************************************************/
package muralha.digital.veiculosCorrelacionados;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.google.gson.*;

/**
 * Servlet responsável por atender requisições de análise de veículos correlacionados.
 * Realiza a consulta à stored procedure 'spu_correlacionamento_placas'
 * e retorna os dados em formato JSON.
 */
@WebServlet("/MuralhaDigital/VeiculosCorrelacionados")
public class VeiculosCorrelacionadosServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Trata requisições GET para a análise de veículos correlacionados ou alertas.
	 * A operação executada depende do parâmetro "acao".
	 *
	 * Parâmetros de entrada esperados via query string:
	 *
	 * @param acao         (opcional) Define o tipo de operação:
	 * - "correlacionados" (padrão): busca correlação de placas
	 * - "detalhes": busca os Detalhes do veículo
	 *
	 * @param placa        (obrigatório) Placa do veículo, com ou sem traço.
	 * Exemplo: "ABC1234" ou "ABC-1234"
	 *
	 * @param dataInicio   (obrigatório) Data e hora inicial no formato 'yyyy-MM-dd HH:mm:ss'.
	 * Exemplo: "2024-09-01 00:00:00"
	 *
	 * @param dataFim      (obrigatório) Data e hora final no formato 'yyyy-MM-dd HH:mm:ss'.
	 * Exemplo: "2024-09-02 23:59:59"
	 *
    * @param numMinPassagensCorrelacionadas (obrigatório para correlacionados) Quantidade mínima de passagens correlacionadas.
	 * Valor mínimo: 1. Se não informado ou menor que 1, será aplicado 1.
	 * Exemplo: "3"
	 *
	 * Retorno:
	 * - JSON com a lista de resultados da operação selecionada.
	 *
	 * Códigos de erro:
	 * - 400: Parâmetros obrigatórios ausentes
	 * - 401: Usuário não autenticado
	 * - 500: Erro interno ao executar consulta no banco
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String acao = request.getParameter("acao");
    	String placasJson = request.getParameter("placas");
    	String placa = request.getParameter("placa");
        String dataInicio = request.getParameter("dataInicio");
        String dataFim = request.getParameter("dataFim");
        String numMinPassagensCorrelacionadasStr = request.getParameter("numMinPassagensCorrelacionadas");
        if (numMinPassagensCorrelacionadasStr == null || numMinPassagensCorrelacionadasStr.trim().isEmpty()) {
            numMinPassagensCorrelacionadasStr = request.getParameter("minCorrelacoes");
        }
        String tempoPermanenciaStr = request.getParameter("tempoPermanencia");

        // Validando acesso do usuário
     	if ( ! new Acesso(request, response, true).verificaAcesso(false))  { 
     		new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; 
 		}

     	// Validação de parâmetros obrigatórios
        if (dataInicio == null || dataFim == null || dataInicio.isEmpty() || dataFim.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros 'dataInicio' e 'dataFim' são obrigatórios.");
            return;
        }

        try {
            Object resultado;

            if ("detalhes".equalsIgnoreCase(acao)) {
                if (placa == null || placa.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro 'placa' é obrigatório para a ação 'detalhes'.");
                    return;
                }
                Date dataInicioDate = parseDataHora(dataInicio);
                Date dataFimDate = parseDataHora(dataFim);
                resultado = VeiculosCorrelacionados.buscarDetalhesVeiculo(placa, dataInicioDate, dataFimDate);
            } else if ("correlacionados".equalsIgnoreCase(acao)){
                if (placasJson == null || placasJson.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro 'placas' é obrigatório para a ação 'correlacionados'.");
                    return;
                }
                
                Gson gson = new Gson();
                String[] placas = gson.fromJson(placasJson, String[].class);
                
                if (placas == null || placas.length == 0) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "O array 'placas' não pode estar vazio.");
                    return;
                }
                
                int numMinPassagensCorrelacionadas = 1;
                if (numMinPassagensCorrelacionadasStr != null && !numMinPassagensCorrelacionadasStr.trim().isEmpty()) {
                    try {
                        numMinPassagensCorrelacionadas = Integer.parseInt(numMinPassagensCorrelacionadasStr.trim());
                        if (numMinPassagensCorrelacionadas < 1) {
                            numMinPassagensCorrelacionadas = 1;
                        }
                    } catch (NumberFormatException e) {
                        numMinPassagensCorrelacionadas = 1;
                    }
                }
                
                int tempoPermanencia;
                if (tempoPermanenciaStr == null || tempoPermanenciaStr.trim().isEmpty()) {
                    tempoPermanencia = 3;
                } else {
                    try {
                        tempoPermanencia = Integer.parseInt(tempoPermanenciaStr);
                        if (tempoPermanencia < 1) {
                            tempoPermanencia = 3;
                        }
                    } catch (NumberFormatException e) {
                        tempoPermanencia = 3;
                    }
                }
                
                Date dataInicioDate = parseDataHora(dataInicio);
                Date dataFimDate = parseDataHora(dataFim);
                resultado = VeiculosCorrelacionados.buscarVeiculosCorrelacionadosMultiplos(
                    placas,
                    dataInicioDate,
                    dataFimDate,
                    tempoPermanencia,
                    numMinPassagensCorrelacionadas);

            } else if ("passagens".equalsIgnoreCase(acao)) {
                if (placa == null || placa.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro 'placa' é obrigatório para a ação 'passagens'.");
                    return;
                }
                
                boolean placaSemIdentificacao = "SEM_PLACA".equalsIgnoreCase(placa);
                
                String paginaStr = request.getParameter("pagina");
                String tamanhoPaginaStr = request.getParameter("tamanhoPagina");
                
                int pagina = 1;
                int tamanhoPagina = 10;
                
                if (paginaStr != null && !paginaStr.trim().isEmpty()) {
                    try {
                        pagina = Integer.parseInt(paginaStr);
                        if (pagina < 1) {
                            pagina = 1;
                        }
                    } catch (NumberFormatException e) {
                        pagina = 1;
                    }
                }
                
                if (tamanhoPaginaStr != null && !tamanhoPaginaStr.trim().isEmpty()) {
                    try {
                        tamanhoPagina = Integer.parseInt(tamanhoPaginaStr);
                        if (tamanhoPagina < 1) {
                            tamanhoPagina = 10;
                        }
                    } catch (NumberFormatException e) {
                        tamanhoPagina = 10;
                    }
                }
                
                Date dataInicioDate = parseDataHora(dataInicio);
                Date dataFimDate = parseDataHora(dataFim);
                
                String placasAlvoStr = request.getParameter("placasAlvo");
                String[] placasAlvo = null;
                if (placasAlvoStr != null && !placasAlvoStr.trim().isEmpty()) {
                    placasAlvo = placasAlvoStr.split(",");
                }
                
                boolean apenasCorrelacionadas = "true".equalsIgnoreCase(request.getParameter("apenasCorrelacionadas"));
                
                resultado = VeiculosCorrelacionados.buscarPassagensVeiculo(
                    placaSemIdentificacao ? null : placa,
                    dataInicioDate, dataFimDate, pagina, tamanhoPagina, placasAlvo, apenasCorrelacionadas);

            } else {
            	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro 'ação' inválido.");
                return;
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Gson gson = new Gson();
            response.getWriter().write(gson.toJson(resultado));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao consultar os dados no banco.");
        }
    }

    /**
     * Converte string de data/hora para java.util.Date.
     * Aceita formatos: yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd'T'HH:mm, yyyy-MM-dd HH:mm:ss
     */
    private Date parseDataHora(String valor) throws Exception {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Parâmetro de data/hora nulo ou vazio");
        }
        String v = valor.trim();
        String[] padroes = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd HH:mm:ss" };
        for (String padrao : padroes) {
            try {
                return new SimpleDateFormat(padrao).parse(v);
            } catch (ParseException ignore) {}
        }
        throw new IllegalArgumentException("Formato de data/hora inválido: '" + valor + "'");
    }
}