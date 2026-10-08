package muralha.digital.relatorios;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class GraficoValidacao
{
	private static Logger logger = Logger.getLogger(GraficoValidacao.class);
	
	private boolean filtroValido;
	private String mensagem;
	private Date dataIni;
	private Date dataFim;
	private Integer idLocal;
	private Integer tipoRelatorio;
	
	public Integer getTipoRelatorio() {
		return tipoRelatorio;
	}
	public void setTipoRelatorio(Integer tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}
	public boolean isFiltroValido() {
		return filtroValido;
	}
	public void setFiltroValido(boolean filtroValido) {
		this.filtroValido = filtroValido;
	}
	
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	public Date getDataIni() {
		return dataIni;
	}
	public void setDataIni(Date dataIni) {
		this.dataIni = dataIni;
	}
	
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	public Integer getIdLocal() {
		return idLocal;
	}
	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}
	
	
	public static GraficoValidacao ValidarFiltros(HttpServletRequest request, HttpServletResponse response, boolean dataComHora) throws ParseException, Exception 
	{
		GraficoValidacao graficoValidacao = new GraficoValidacao();
		try
		{
			String strDataIni = request.getParameter("dataIni");
			String strDataFim = request.getParameter("dataFim");
			String strEquipamento = request.getParameter("equipamento");
			String strtipoRelatorio = request.getParameter("tipoRelatorio");
			
	    	if ( (strDataIni == null || strDataIni.trim().equals("")) || (strDataFim == null || strDataFim.trim().equals("")) )
	    	{
	    		String msg = "Favor informar datas de início e fim!";
				logger.error(msg);	
				graficoValidacao.setFiltroValido(false);
				graficoValidacao.setMensagem(msg);
	    		return graficoValidacao;
	    	}
	    	
	    	if (strEquipamento == null || strEquipamento.trim().equals(""))
	    	{
	    		String msg = "Favor informar o equipamento!";
				logger.error(msg);
				graficoValidacao.setFiltroValido(false);
				graficoValidacao.setMensagem(msg);
	    		return graficoValidacao;
	    	}
	    	
	    	if (strtipoRelatorio == null || strtipoRelatorio.trim().equals("") || strtipoRelatorio.trim().equals("0"))
	    	{
	    		String msg = "Favor informar o tipo do Relatório!";
				logger.error(msg);
				graficoValidacao.setFiltroValido(false);
				graficoValidacao.setMensagem(msg);
	    		return graficoValidacao;
	    	}

	    	Integer idLocal = null;
	    	Integer tipoRelatorio = null;
			Date dataIni = null, dataFim = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
	    	try
	    	{
	    		if (strEquipamento != null && !strEquipamento.trim().equals(""))
	    			idLocal = Integer.parseInt(strEquipamento);	
	    		
	    		if (strtipoRelatorio != null && !strtipoRelatorio.trim().equals("") && !strtipoRelatorio.trim().equals("0"))
	    			tipoRelatorio = Integer.parseInt(strtipoRelatorio);
	    		
	    		if (strDataIni != null && !strDataIni.trim().equals("")) {
		    		if (dataComHora)
		    			dataIni = sdfHora.parse(strDataIni+":00");
		    		else
		    			dataIni = sdf.parse(strDataIni);
		    	}
		    	
	    		if (strDataFim != null && !strDataFim.trim().equals("")) {
	    			if (dataComHora)
	    				dataFim = sdfHora.parse(strDataFim+":59");
		    		else
		    			dataFim = sdf.parse(strDataFim);
		    	}
			}
	    	catch (ParseException e)
	    	{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg, e);
				graficoValidacao.setFiltroValido(false);
				graficoValidacao.setMensagem(msg);
	    		return graficoValidacao;
			}
	    	
	    	if ( (dataIni != null && dataFim != null) && dataFim.before(dataIni) )
	    	{
	    		String msg = "A data de início deve ser menor ou igual que a data fim!";
				logger.error(msg);
				graficoValidacao.setFiltroValido(false);
				graficoValidacao.setMensagem(msg);
	    		return graficoValidacao;
	    	}
	    	
	    	graficoValidacao.setFiltroValido(true);
	    	graficoValidacao.setDataIni(dataIni);
	    	graficoValidacao.setDataFim(dataFim);
	    	graficoValidacao.setIdLocal(idLocal);
	    	graficoValidacao.setTipoRelatorio(tipoRelatorio);
	    	
	    	return graficoValidacao;
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao validar os filtros do dashboard!";
			logger.error(msg, e);
			graficoValidacao.setFiltroValido(false);
			graficoValidacao.setMensagem(msg);
    		return graficoValidacao;
		}			
	}
}
