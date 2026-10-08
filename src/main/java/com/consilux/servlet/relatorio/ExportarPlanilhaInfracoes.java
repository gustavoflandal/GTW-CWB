package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.exportalista.ExportaLista;
import com.consilux.exportalista.InfracaoCompletaBean;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.TipoMime;

/**
 * Servlet implementation class ImprimirRecurso
 */
public class ExportarPlanilhaInfracoes extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String ARQ_NAI = "/WEB-INF/relatorio/ExportarConsultaInfracoes.jasper";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExportarPlanilhaInfracoes() {
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
			
			String sLocal = request.getParameter("local");
			relatorio.adicParametro("LOCAL", sLocal);
			
			relatorio.adicParametro("DIR_IMAGENS",getServletContext().getRealPath(DIR_IMAGENS) + System.getProperty("file.separator"));
			
			List<? extends ExportaLista> lista = InfracaoCompletaBean.getListaParaRelatorio();
			
			if(lista == null){
				String goUrl = "javascript: history.back()";
				String sMens = "";
				goUrl = URLEncoder.encode(goUrl, "UTF-8");
				sMens = URLEncoder.encode("É preciso realizar uma consulta antes de Exportar a planilha.", "UTF-8");
				response.sendRedirect("/includes/erro.jsp?m="+sMens+"&p="+goUrl);
			}
			
			try {
				relatorio.preencheRelatorio(lista);
				response.setContentType(TipoMime.XLS.getTipo());
				response.setHeader("Content-Disposition","inline; filename=\"Infracoes.xls\"");
			    relatorio.exportReportToXlsStream(response.getOutputStream());
			}
			catch (Exception e) {
				e.printStackTrace();
				new ServletException("Erro ao gerar o relatório: "+e.getMessage());
			}
		}catch(Exception e){
			new ServletException("Data inválida.");
		}
	}
}