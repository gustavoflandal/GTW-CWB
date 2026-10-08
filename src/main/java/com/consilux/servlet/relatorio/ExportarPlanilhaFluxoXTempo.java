package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.exportalista.ExportaLista;
import com.consilux.exportalista.FluxoTempoBean;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;

/**
 * Servlet implementation class ImprimirRecurso
 */
public class ExportarPlanilhaFluxoXTempo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String ARQ_NAI = "/WEB-INF/relatorio/FluxoXTemp.jasper";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExportarPlanilhaFluxoXTempo() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unused")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");


		
		try{

			RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(ARQ_NAI));
			
			//String urlImagem = request.getParameter("urlImagem");		
			//DisplayChart display = new DisplayChart();
			
			String sLocal = request.getParameter("local");
			
			
			//relatorio.adicParametro("GRAFICO",getServletContext().getRealPath(DIR_IMAGENS + urlImagem));
			
			relatorio.adicParametro("DIR_IMAGENS",getServletContext().getRealPath(DIR_IMAGENS));
			
			List<? extends ExportaLista> lista = FluxoTempoBean.getListaParaRelatorio();
			
			for(int i = 0; i < lista.size() ; i++){
				FluxoTempoBean ftb = (FluxoTempoBean) lista.get(i);
				ftb.setLocal(sLocal);
			}
			
			if(lista == null){
				String goUrl = "javascript: history.back()";
				String sMens = "";
				goUrl = URLEncoder.encode(goUrl, "UTF-8");
				sMens = URLEncoder.encode("É preciso realizar uma consulta antes de Exportar a planilha.", "UTF-8");
				response.sendRedirect("/includes/erro.jsp?m="+sMens+"&p="+goUrl);
			}
			
			try {
				relatorio.preencheRelatorio(lista);
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition","inline; filename=\"FluxoXTempo.xls\"");
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