package muralha.digital.relatorios;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.TipoMime;

public class RelatorioUtils {

	private static Logger logger = Logger.getLogger(RelatorioUtils.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RelatorioUtils() {
        super();
    }

    public static RelatorioValidacao ValidarFiltros(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	RelatorioValidacao relatorioValidacao = new RelatorioValidacao();
    	
    	String sDataInicio = request.getParameter("dataIni") != null ? request.getParameter("dataIni").trim() : null;
    	sDataInicio = sDataInicio != null && sDataInicio.length() == 0 ? null : sDataInicio;

    	String sDataFim = request.getParameter("dataFim") != null ? request.getParameter("dataFim").trim() : null;
    	sDataFim = sDataFim != null && sDataFim.length() == 0 ? null : sDataFim;
    	
    	String sTipoAgrupamento = request.getParameter("tipoAgrupamento") != null ? request.getParameter("tipoAgrupamento").trim() : null;
    	
        String sFormato = request.getParameter("formato");
    	
    	if (sDataInicio == null || sDataInicio.equals(""))
    	{
            relatorioValidacao.setFiltroValido(false);
    		relatorioValidacao.setMensagem("Data Inicio não informada!");
            logger.error(relatorioValidacao.getMensagem());
    		return relatorioValidacao;
        }
    	
    	sDataInicio = sDataInicio + ":00";
        if (sDataInicio != null && !ExpValida.DATA_HORA.validar(sDataInicio))
        {
        	relatorioValidacao.setFiltroValido(false);
    		relatorioValidacao.setMensagem("Data Inicio inválida!");
            logger.error(relatorioValidacao.getMensagem());
    		return relatorioValidacao;
        }
        
        if (sDataFim == null || sDataFim.equals(""))
        {
        	relatorioValidacao.setFiltroValido(false);
    		relatorioValidacao.setMensagem("Data Fim não informada!");
            logger.error(relatorioValidacao.getMensagem());
    		return relatorioValidacao;
        }
        
        sDataFim = sDataFim + ":59";
        if (sDataFim != null && !ExpValida.DATA_HORA.validar(sDataFim))
        {
        	relatorioValidacao.setFiltroValido(false);
    		relatorioValidacao.setMensagem("Data Fim inválida!");
            logger.error(relatorioValidacao.getMensagem());
    		return relatorioValidacao;
        }
        
        if ( sTipoAgrupamento != null && !Pattern.matches("(EQPTO)|(TPALERTA)", sTipoAgrupamento ) )
        {
        	relatorioValidacao.setFiltroValido(false);
    		relatorioValidacao.setMensagem("Formato inválido!");
            logger.error(relatorioValidacao.getMensagem());
    		return relatorioValidacao;
        }
        
        if ( sFormato == null || !Pattern.matches("(pdf)|(xls)", sFormato ) )
        {
        	relatorioValidacao.setFiltroValido(false);
    		relatorioValidacao.setMensagem("Formato inválido!");
            logger.error(relatorioValidacao.getMensagem());
    		return relatorioValidacao;
        }
        
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date dtIni = null, dtFim = null;
		
		try
		{
			dtIni = dateFormat.parse(sDataInicio);
			dtFim = dateFormat.parse(sDataFim);
			
			if (dtFim.before(dtIni))
			{
				relatorioValidacao.setFiltroValido(false);
	    		relatorioValidacao.setMensagem("A Data Inicio deve ser menor que a Data Fim!");
	            logger.error(relatorioValidacao.getMensagem());
	    		return relatorioValidacao;
			}
			
			relatorioValidacao.setDataInicioFiltro(new Timestamp(dtIni.getTime()));
			relatorioValidacao.setDataFimFiltro(new Timestamp(dtFim.getTime()));
			relatorioValidacao.setTipoAgrupamento(sTipoAgrupamento);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			new ServletException("Erro ao converter data: " + e.getMessage());
		}

		// validação garante que será um dos dois
        if ( "pdf".compareToIgnoreCase( sFormato ) == 0 )
        	relatorioValidacao.setFormato(TipoMime.PDF);
        else if ( "xls".compareToIgnoreCase(sFormato) == 0 )
        	relatorioValidacao.setFormato(TipoMime.XLS);
        
        relatorioValidacao.setFiltroValido(true);
        relatorioValidacao.setMensagem("Campos validados com sucesso!");
        
        return relatorioValidacao;
    }
}
