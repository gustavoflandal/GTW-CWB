package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;

/**
 * Servlet implementation class ImprimirRecurso
 */
public class AproveitamentoImagens extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String ARQ_NAI = "/WEB-INF/relatorio/AproveitamentoImagens.jasper";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";
	private static final Logger logger = Logger.getLogger(AproveitamentoImagens.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AproveitamentoImagens() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");


		
		try{

			RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(ARQ_NAI));
			
			String dataIni = request.getParameter("dataini");
			String dataFinal = request.getParameter("datafim");
			String local = request.getParameter("id_local");
			String pista = request.getParameter("pista");

			Date dtIni = null;
			Date dtFim = null;
			dtIni = new SimpleDateFormat("dd/MM/yyyy").parse(dataIni);
			dtFim = new SimpleDateFormat("dd/MM/yyyy").parse(dataFinal);
			
			dtFim = DateUtils.addHours(dtFim, 23);
			dtFim = DateUtils.addSeconds(dtFim, 3595);
			
			//relatorio.adicParametro("DIR_IMAGENS",getServletContext().getRealPath(DIR_IMAGENS));

			try {
				relatorio.adicParametro("DATA_INI",new Timestamp(dtIni.getTime()));
				relatorio.adicParametro("DATA_FIM",new Timestamp(dtFim.getTime()));
				relatorio.adicParametro("LOCAL",Integer.parseInt(local.trim()));
				relatorio.adicParametro("PISTA",Integer.parseInt(pista.trim()));
				
				relatorio.preencheRelatorio();
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition","inline; filename=\"AproveitamentoImagens.xls\"");
			    relatorio.exportReportToXlsStream(response.getOutputStream());
			}
			catch (Exception e) {
				logger.error("Erro ao gerar o relatório. Erro no Jasper.", e);
				new ServletException("Erro ao gerar o relatório: " + e.getMessage());
			}
		}
		catch(Exception e){
			new ServletException("Data inválida.",e);
		}
	}
}