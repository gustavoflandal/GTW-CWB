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
 * Servlet implementation class RelatorioManutencao
 */
public class RelatorioManutencao extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */

	public static final String ARQ_NAI = "/WEB-INF/relatorio/Manutencao.jasper";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Acesso acesso = new Acesso(request, response); 
		
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		try{
			RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(ARQ_NAI));
			
			String dataIni = request.getParameter("dataini");
			String dataFinal = request.getParameter("datafim");

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

				relatorio.preencheRelatorio();
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition","inline; filename=\"RelatorioManutencao.xls\"");
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
