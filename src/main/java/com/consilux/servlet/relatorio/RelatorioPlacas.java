package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

/**
 * Servlet implementation class ImprimirRecurso
 */
public class RelatorioPlacas extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String ARQ_NAI = "/WEB-INF/relatorio/RelatorioDePlacas.jasper";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RelatorioPlacas() {
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
			
			String sHoraIni = request.getParameter("horaini");
			String sHoraFim = request.getParameter("horafim");
			
			
			if (local == null || !Pattern.matches("[1-9][0-9]{0,7}",local)) {
			    new Mensagem(response).showErro("Local enviado inválido!");
			    return;
			}
			else if (pista == null || !Pattern.matches("[0-9]",pista)) {
			    new Mensagem(response).showErro("Pista enviada inválida!");
			    return;
			}
			else if (dataIni == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",dataIni)) {
			    new Mensagem(response).showErro("Data inicial enviada inválida!");
			    return;
			}
			else if (sHoraIni == null || !Pattern.matches("([01][0-9]|2[0-3]):([0-5][0-9])",sHoraIni)) {
			    new Mensagem(response).showErro("Hora inicial enviada inválida!");
			    return;
			}
			else if (dataFinal == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",dataFinal)) {
			    new Mensagem(response).showErro("Data final enviada inválida!");
			    return;
			}
			else if (sHoraFim == null || !Pattern.matches("([0-1][0-9]|2[0-3]):([0-5][0-9])",sHoraFim)) {
			    new Mensagem(response).showErro("Hora final enviada inválida!");
			    return;
			}
			
			
			
			
			Date dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataIni+" "+sHoraIni+":00");
			Date dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataFinal+" "+sHoraFim+":59");


			try {
				relatorio.adicParametro("DATA_INI",new Timestamp(dtIni.getTime()));
				relatorio.adicParametro("DATA_FIM",new Timestamp(dtFim.getTime()));
				relatorio.adicParametro("LOCAL",Integer.parseInt(local.trim()));
				relatorio.adicParametro("PISTA",Integer.parseInt(pista.trim()));
				relatorio.adicParametro("DIR_IMAGENS",getServletContext().getRealPath(DIR_IMAGENS));
				
				relatorio.preencheRelatorio();
				//response.setContentType("application/vnd.ms-excel");
				response.setContentType("application/xls");
				response.setHeader("Content-Disposition","inline; filename=\"RelatorioDePlacas.xls\"");
			    relatorio.exportReportToXlsStream(response.getOutputStream());

			}
			catch (Exception e) {
				String goUrl = "javascript: history.back()";
				String sMens = "";
				goUrl = URLEncoder.encode(goUrl, "UTF-8");
				sMens = URLEncoder.encode("Erro ao gerar a planilha.", "UTF-8");
				response.sendRedirect("/includes/erro.jsp?m="+sMens+"&p="+goUrl);
			}
		}catch(Exception e){
			new ServletException("Data inválida.");
		}
	}
}