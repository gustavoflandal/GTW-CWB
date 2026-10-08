package muralha.digital.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

@XmlRootElement		(name="Paginacao") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class Paginacao 
{
	private static Logger logger = Logger.getLogger(Paginacao.class); 
	
	private int itensPorPagina;
	private int offset;
	private int totalRegistros;
	private List<String> erros;
	
	private Paginacao()
	{
		super();
	}
	
	public Paginacao (HttpServletRequest request)
	{
		new Paginacao();
		this.erros = new ArrayList<String>();
		ValidarParametros(request);
	}
	
	private void ValidarParametros(HttpServletRequest request) 
	{
		try 
		{
			String strPaginacaoItensPorPagina = request.getParameter("paginacaoItensPorPagina");
			String strPaginacaoOffset = request.getParameter("paginacaoOffset");
			
	    	if (strPaginacaoItensPorPagina == null || strPaginacaoItensPorPagina.equals("")) 
	    	{
	    		String msg = "A quantidade de itens por página não foi informada!";
	    		logger.error(msg);	
	    		this.erros.add(msg);
	    	}
	    	
	    	if (strPaginacaoOffset == null || strPaginacaoOffset.equals("")) 
	    	{
	    		String msg = "A quantidade de itens de deslocamento não foi informada!";
	    		logger.error(msg);	
	    		this.erros.add(msg);
	    	}
	    	
	    	if (OperacaoValida())
	    	{
	    		this.itensPorPagina = Integer.parseInt(strPaginacaoItensPorPagina);
	    		this.offset = Integer.parseInt(strPaginacaoOffset);
	    	}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao preparar os dados para paginação!";
			logger.error(msg, e);	
			this.erros.add(msg);
		}
	}
	
	public int ItensPorPagina() { return this.itensPorPagina; }
	
	public int Offset() { return this.offset; }
	
	public int TotalRegistros() { return this.totalRegistros; }
	
	public String QueryTotalRegistros() { return " COUNT(*) OVER() AS total_registros "; }
	
	public String QueryPaginacao() { return String.format(" OFFSET %d ROWS FETCH NEXT %d ROWS ONLY ", this.offset, this.itensPorPagina); }
	
	public boolean OperacaoValida() { return this.erros.isEmpty(); }
	
	public List<String> Erros() { return this.erros; }
	
	public void TotalRegistros(int totalRegistros) { this.totalRegistros = totalRegistros; }
}
