package com.consilux.servlet.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.medicao.ProcessoMedicao;

/**
 * Servlet implementation class AtualizaProcessoMedicao
 */
public class AtualizaProcessoMedicao extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AtualizaProcessoMedicao.class); 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AtualizaProcessoMedicao() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, false).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sIdProcessoMedicao = request.getParameter("id_processo_medicao");
		
		if (sIdProcessoMedicao == null || !ExpValida.NATURAL.validar(sIdProcessoMedicao))
			throw new ServletException("Identificador do processo de medição enviado invalido!");

		try {
			ProcessoMedicao.atualizaEtapas(Integer.valueOf(sIdProcessoMedicao));
		}
		catch(Exception e) {
			logger.error("Erro ao atualizar o processo de medição.", e);
			throw new ServletException("Erro ao atualizar o processo de medição: "+e.getMessage());
		}
	}
}
