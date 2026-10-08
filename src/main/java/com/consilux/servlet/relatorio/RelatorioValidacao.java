package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Remessa;
import com.consilux.model.TipoMime;

/**
 * Servlet implementation class RelatorioValidacao
 */
public class RelatorioValidacao extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RelatorioValidacao.class);
       
	public static final String ARQ_NAI = "/WEB-INF/relatorio/RelatorioValidacao.jasper";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RelatorioValidacao() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		try {
			RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(ARQ_NAI)); 
			
			String s_id_remessa = request.getParameter("id_remessa");
			Integer id_remessa = Integer.parseInt(s_id_remessa);
			Remessa remessa = Remessa.buscarRemessaPorId(id_remessa);
			
			boolean integridade = remessa.getIntegridade();
			int erros = remessa.getErrosProcessamentoRelatorio();
			String descricao = remessa.getIntegridadeDescricao();
			String descricao_ext = remessa.getIntegridadeDescricaoExt();
			if(integridade == true && erros == 0)
				descricao_ext = "O processamento foi realizado sem apontamento de erros.";
			if(descricao_ext == null && erros > 0)
				descricao_ext = "Os seguintes erros de processamento foram encontrados:";
			
			relatorio.adicParametro("ID_REMESSA", id_remessa);
			relatorio.adicParametro("MENSAGEM", descricao);
			relatorio.adicParametro("MENSAGEM_EXT", descricao_ext);
			relatorio.adicParametro("DIR_IMAGENS", getServletContext().getRealPath(DIR_IMAGENS));
			relatorio.adicParametro("DESCRICAO_LOTE", remessa.getDescricaoApaitAlt());
			
			relatorio.preencheRelatorio();
			response.setContentType(TipoMime.PDF.getTipo());
			response.setHeader("Content-Disposition","inline; filename=\"RelatorioValidacao.pdf\"");
			relatorio.exportReportToPdfStream(response.getOutputStream());
		} catch (Exception e) {
			logger.error("Erro ao gerar o Relatorio de Validacao:", e);
			String goUrl = "javascript: self.close();";
			String sMens = "";
			goUrl = URLEncoder.encode(goUrl, "UTF-8");
			sMens = URLEncoder.encode("Erro ao gerar o Relatorio de Validacao.", "UTF-8");
			response.sendRedirect("/includes/erro.jsp?m="+sMens+"&p="+goUrl);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
