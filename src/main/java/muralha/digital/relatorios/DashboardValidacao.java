package muralha.digital.relatorios;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class DashboardValidacao
{
	private static Logger logger = Logger.getLogger(DashboardValidacao.class);
	
	private boolean filtroValido;
	private String mensagem;
	private Date dataIni;
	private Date dataFim;
	private Integer idLocal;
	private Integer municipio;
	private Integer regiao;
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
	public Integer getMunicipio() {
		return municipio;
	}
	public void setMunicipio(Integer municipio) {
		this.municipio = municipio;
	}
	public Integer getRegiao() {
		return regiao;
	}
	public void setRegiao(Integer regiao) {
		this.regiao = regiao;
	}


	public static DashboardValidacao ValidarFiltros(HttpServletRequest request, HttpServletResponse response) throws ParseException, Exception 
	{
		DashboardValidacao dashboardValidacao = new DashboardValidacao();
		try
		{
			String strDataIni = request.getParameter("dataIni");
			String strDataFim = request.getParameter("dataFim");
			String strEquipamento = request.getParameter("equipamento");
			String strMunicipio = request.getParameter("municipio");
			String strRegiao = request.getParameter("regiao");
			String strtipoRelatorio = request.getParameter("tipoRelatorio");
			
	    	if ( (strDataIni == null || strDataIni.trim().equals("")) || (strDataFim == null || strDataFim.trim().equals("")) )
	    	{
	    		String msg = "Favor informar datas de início e fim!";
				logger.error(msg);	
				dashboardValidacao.setFiltroValido(false);
				dashboardValidacao.setMensagem(msg);
	    		return dashboardValidacao;
	    	}

	    	if (strtipoRelatorio == null || strtipoRelatorio.trim().equals("") || strtipoRelatorio.trim().equals("0"))
	    	{
	    		String msg = "Favor informar o tipo do Relatório!";
				logger.error(msg);
				dashboardValidacao.setFiltroValido(false);
				dashboardValidacao.setMensagem(msg);
	    		return dashboardValidacao;
	    	}
	    	
	    	if (strMunicipio == null || strMunicipio.trim().equals(""))
	    	{
	    		String msg = "Favor informar o Município!";
				logger.error(msg);
				dashboardValidacao.setFiltroValido(false);
				dashboardValidacao.setMensagem(msg);
	    		return dashboardValidacao;
	    	}

	    	if (strRegiao == null || strRegiao.trim().equals(""))
	    	{
	    		String msg = "Favor informar Região!";
				logger.error(msg);
				dashboardValidacao.setFiltroValido(false);
				dashboardValidacao.setMensagem(msg);
	    		return dashboardValidacao;
	    	}
	    	
	    	if (strEquipamento == null || strEquipamento.trim().equals(""))
	    	{
	    		String msg = "Favor informar o equipamento!";
				logger.error(msg);
				dashboardValidacao.setFiltroValido(false);
				dashboardValidacao.setMensagem(msg);
	    		return dashboardValidacao;
	    	}
	    	
	    	Integer idLocal = 0, tipoRelatorio = null, municipio = null, regiao = null;
			Date dataIni = null, dataFim = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
	    	try
	    	{
	    		if (strEquipamento != null && !strEquipamento.trim().equals(""))
	    			idLocal = Integer.parseInt(strEquipamento);	
	    		
	    		if (strtipoRelatorio != null && !strtipoRelatorio.trim().equals("") && !strtipoRelatorio.trim().equals("0"))
	    			tipoRelatorio = Integer.parseInt(strtipoRelatorio);
	    		
	    		if (strMunicipio != null && !strMunicipio.trim().equals("") && !strMunicipio.trim().equals("0"))
	    			municipio = Integer.parseInt(strMunicipio);
	    		
	    		if (strRegiao != null && !strRegiao.trim().equals("") && !strRegiao.trim().equals("0"))
	    			regiao = Integer.parseInt(strRegiao);
	    		
	    		if (strDataIni != null && !strDataIni.trim().equals(""))
		    		dataIni = sdf.parse(strDataIni);
		    	
	    		if (strDataFim != null && !strDataFim.trim().equals(""))
	    			dataFim = sdf.parse(strDataFim);
	    		
			}
	    	catch (ParseException e)
	    	{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg, e);
				dashboardValidacao.setFiltroValido(false);
				dashboardValidacao.setMensagem(msg);
	    		return dashboardValidacao;
			}
	    	
	    	if ( (dataIni != null && dataFim != null) && dataFim.before(dataIni) )
	    	{
	    		String msg = "A data de início deve ser menor ou igual que a data fim!";
				logger.error(msg);
				dashboardValidacao.setFiltroValido(false);
				dashboardValidacao.setMensagem(msg);
	    		return dashboardValidacao;
	    	}
	    	
	    	dashboardValidacao.setFiltroValido(true);
	    	dashboardValidacao.setDataIni(dataIni);
	    	dashboardValidacao.setDataFim(dataFim);
	    	dashboardValidacao.setIdLocal(idLocal);
	    	dashboardValidacao.setMunicipio(municipio);
	    	dashboardValidacao.setRegiao(regiao);
	    	dashboardValidacao.setTipoRelatorio(tipoRelatorio);
	    	
	    	return dashboardValidacao;
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao validar os filtros do dashboard!";
			logger.error(msg, e);
			dashboardValidacao.setFiltroValido(false);
			dashboardValidacao.setMensagem(msg);
    		return dashboardValidacao;
		}			
	}
}
