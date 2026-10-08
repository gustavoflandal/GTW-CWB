/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 09/01/2009

  Descricao: Servlet para visualização de notificações geradas externamente.

  Historico:

    $Log: AITPDF.java,v $
    Revision 1.6  2009/06/01 18:41:23  fos
    Consertado expressão regular de inteiros.

    Revision 1.5  2009/04/03 15:52:09  fos
    Agora têm a opção para imprimir com obliteração ou não.

    Revision 1.4  2009/03/19 23:07:30  fos
    Agora filtra por série e tipo da remessa.

    Revision 1.3  2009/01/16 18:44:32  fos
    Agora envio o diretório das imagens ao Jasper.

    Revision 1.2  2009/01/16 14:01:23  fos
    Agora utiliza biblioteca indireta para o JasperReports.

    Revision 1.1  2009/01/12 12:49:45  fos
    Recuperação de repositório.


*********************************************************************************/
package com.consilux.servlet.ait;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.ConfiguracaoRemessa;
import com.consilux.lib.Conexao;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Infracao;
import com.consilux.model.InfracaoCompletaLista;
import com.consilux.model.InfracaoObliteracao;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;

 /**
 * Servlet para visualização de notificações geradas externamente.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.6 $ $Date: 2009/06/01 18:41:23 $ $Author: fos $
 */
public class AITPDF extends HttpServlet implements javax.servlet.Servlet {
	
	private static final long serialVersionUID = 1L;
	public static final String DIR_RELATORIOS = "/WEB-INF/relatorio/";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";
	private static Logger logger = Logger.getLogger(AITPDF.class);  
	
	/**
	 * Constrói o objeto 
	 */
	public AITPDF() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; //O usuário não tem acesso...então cai fora!
		}

		ConfiguracaoRemessa remessaConf = 
			ConfiguracaoProvider.getInstance().getConfiguracaoRemessa();
		
		
		String sObliteracao = request.getParameter("obliteracao");
		String sTipoRemessa = request.getParameter("tipo_remessa");

		if (sTipoRemessa == null || sTipoRemessa.length() == 0) {
			new Mensagem(response).showErro("Identificador do tipo-remessa enviado inválido!", "javascript:window.close();");
			return;
		}

		String sIdsAuto = request.getParameter("ids_auto");
		
		if (sIdsAuto != null) {
			sIdsAuto = sIdsAuto.trim().replaceAll("\r\n", ",").replaceAll("\n", ",");
		}
		
		if (sIdsAuto == null || !Pattern.matches("([1-9][0-9]{0,8})+(,([1-9][0-9]{0,8})+)*",sIdsAuto)) {
			new Mensagem(response).showErro("Identificadores do auto enviado invalido!","javascript:window.close();");
			return;
		}

		String sSerie = request.getParameter("serie");
		
		if (sSerie != null && sSerie.length() > 10) {
			new Mensagem(response).showErro("Identificador da série enviado invalido!","javascript:window.close();");
			return;
		}

		if (sObliteracao != null && !sObliteracao.equals("true")) {
			new Mensagem(response).showErro("Indicador resídual inválido!");
			return;
		}

		Boolean comObliteracao = Boolean.valueOf(sObliteracao);
		String sArquivoJasper;
		//String sArquivoJasper = ARQ_AIT_OUTROS;
	    Map<String,Object> mFiltro = new HashMap<String,Object>();
	    
	    try {
		    if (sTipoRemessa != null)
		        mFiltro.put("tipo_remessa", sTipoRemessa);	    	
		    if (sSerie != null && sSerie.length() > 0)
		    	mFiltro.put("serie", sSerie.toUpperCase());
	    }
	    catch (Exception e) {
			new ServletException("Não foi possível carregar os parâmetros da remessa.", e);
		}
	    
        mFiltro.put("autos", sIdsAuto);
        
        String[] aAutosEmOrdem = sIdsAuto.split(",\\s*"); 

        List<InfracaoCompletaLista> listaInfracaoSemOrdem = null;
        List<InfracaoCompletaLista> listaInfracao = null;
        List<ByteArrayInputStream> lbimgs = new ArrayList<ByteArrayInputStream>();

		Integer idInfracao = null;
		
		try {
			listaInfracaoSemOrdem = InfracaoCompletaLista.buscaInfracaoCompletaPor(mFiltro, 1, false);
			listaInfracao = new ArrayList<InfracaoCompletaLista>(listaInfracaoSemOrdem);

			//Proteção, limpa a array antes, para evitar esquecimento de autos...
			for (int i = 0; i < listaInfracao.size(); i++) {
				listaInfracao.set(i, null);
			}

			Integer ordemAuto;
			for (InfracaoCompletaLista icl : listaInfracaoSemOrdem) {
				ordemAuto = ArrayUtils.indexOf(aAutosEmOrdem, String.valueOf(icl.getAuto()));
				listaInfracao.set(ordemAuto, icl);
			}


			if (listaInfracao.size() == 0) {
				new Mensagem(response).showErro("Auto não encontrado!","javascript:window.close();");
				return;
			}
			
			for (InfracaoCompletaLista icl : listaInfracao) {
				idInfracao = icl.getId();
				Infracao inf = Infracao.buscaInfracaoPorId(idInfracao);
				InfracaoObliteracao io = InfracaoObliteracao.buscaInfracaoObliteracaoPorIdInfracao(idInfracao, inf.getIdImagemOBJ());
				lbimgs.add(new ByteArrayInputStream(io != null && comObliteracao ? io.getImagemObliterada() : inf.buscaImagemObjetiva()));
			}
			
		}
		catch (Exception e) {
			logger.error("Erro ao gerar PDF de AIT. Erro ao buscar.", e);
			throw new ServletException(e);
		}
		
		System.out.println(sTipoRemessa);
		
		sArquivoJasper = DIR_RELATORIOS + remessaConf.getModeloAIT(sTipoRemessa);
		RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(sArquivoJasper));
		
		relatorio.adicParametro("IMGS_INFRACAO", lbimgs);
		relatorio.adicParametro("DIR_IMAGENS", getServletContext().getRealPath(DIR_IMAGENS) + System.getProperty("file.separator"));
		
		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			Statement st = conn.createStatement();
			try {
				st.execute("CREATE TABLE #lista_infracoes (id_infracao INT, ordem INT IDENTITY(1,1))");
				for (InfracaoCompletaLista inf: listaInfracao) {
					st.execute("INSERT INTO #lista_infracoes VALUES ("+String.valueOf(inf.getId())+")");
				}
				st.close();
				relatorio.preencheRelatorio(conn);
				st = conn.createStatement();
			}
			finally {
				st.execute("DROP table #lista_infracoes");
			}
			st.close();
			response.setContentType(TipoMime.PDF.getTipo());
			response.setHeader("Content-Disposition","inline; filename=\"AIT.pdf\"");
		    relatorio.exportReportToPdfStream(response.getOutputStream());
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar PDF de AIT. Erro no Jasper.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				}
				catch (SQLException e) {
					logger.error("Erro ao fechar a conexão!", e);
				}
			}
		}
	}
}