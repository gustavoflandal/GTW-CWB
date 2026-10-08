package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;

import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;

/**
 * Servlet implementation class ImprimirRecurso
 */
public class RelatorioAproveitaveis extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String ARQ_NAI = "/WEB-INF/relatorio/RelatorioAproveitaveis.jasper";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";


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

			Date dtIni = null;
			Date dtFim = null;
			dtIni = new SimpleDateFormat("dd/MM/yyyy").parse(dataIni);
			dtFim = new SimpleDateFormat("dd/MM/yyyy").parse(dataFinal);
			
			dtFim = DateUtils.addHours(dtFim, 23);
			dtFim = DateUtils.addSeconds(dtFim, 3595);
			relatorio.adicParametro("DIR_IMAGENS",getServletContext().getRealPath(DIR_IMAGENS));
			try {
				relatorio.adicParametro("DATA_INI",new Timestamp(dtIni.getTime()));
				relatorio.adicParametro("DATA_FIM",new Timestamp(dtFim.getTime()));
				relatorio.adicParametro("LOCAL",Integer.parseInt(local.trim()));
				
				relatorio.preencheRelatorio();
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition","inline; filename=\"RelatorioAproveitaveis.xls\"");
			    relatorio.exportReportToXlsStream(response.getOutputStream());
			}catch (Exception e) {
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