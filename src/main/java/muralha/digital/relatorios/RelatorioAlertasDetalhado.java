package muralha.digital.relatorios;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.TipoMime;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/Relatorio/AlertasDetalhado")
public class RelatorioAlertasDetalhado extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static final String DIR_RELATORIO = "/muralha-digital/relatorios/";
	public static final String NOME_RELATORIO = DIR_RELATORIO + "RelatorioAlertasDetalhado.jasper";
	private static Logger logger = Logger.getLogger(RelatorioAlertasDetalhado.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RelatorioAlertasDetalhado() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		if (!new Acesso(request, response, true).verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!
    	
		try
		{
			RelatorioValidacao relatorioValidacao = RelatorioUtils.ValidarFiltros(request, response);
	        
			logger.debug(relatorioValidacao.getMensagem());	
			respostaXML.EnviarRespostaRequisicaoXML(response, relatorioValidacao.isFiltroValido(), relatorioValidacao.getMensagem());
		}
		catch (Exception e)
		{
			logger.debug("Erro ao gerar relatório: " + e.getMessage());
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Ocorreu um erro ao gerar o relatório. Tente novamente ou contate o administrador do sistema!");
		}
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		if (!new Acesso(request, response, true).verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!
    	
		RelatorioValidacao relatorioValidacao = RelatorioUtils.ValidarFiltros(request, response);
		
		if (!relatorioValidacao.isFiltroValido())
		{
			String msg = "Ocorreu um erro ao gerar o relatório. Tente novamente ou contate o administrador do sistema!";
			logger.debug(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		else
		{
			RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(NOME_RELATORIO));
			
			try
			{
				relatorio.adicParametro("data_inicio", relatorioValidacao.getDataInicioFiltro());
				relatorio.adicParametro("data_fim", relatorioValidacao.getDataFimFiltro());
	
				relatorio.preencheRelatorio();
				response.setContentType(relatorioValidacao.getFormato() != null ? relatorioValidacao.getFormato().getTipo() : TipoMime.HTML.getTipo() );
				response.setHeader("Content-Disposition","inline; filename=\"Relatorio de Alertas Detalhado" + relatorioValidacao.getFormato().getExtensao() + "\"");
				
				if ( relatorioValidacao.getFormato().compareTo( TipoMime.PDF ) == 0 )
					relatorio.exportReportToPdfStream(response.getOutputStream());
				else
					relatorio.exportReportToXlsStream(response.getOutputStream());
				
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				new ServletException("Erro ao gerar o relatório: "+e.getMessage());
			}
		}
    }
}
