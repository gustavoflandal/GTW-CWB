/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Fernando Amaral
  Data: 22/09/2021

*********************************************************************************/
package muralha.digital.mapacalor;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

@WebServlet("/MuralhaDigital/MapaCalor")
public class MapaCalorServlet 
						extends javax.servlet.http.HttpServlet 
						implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MapaCalorServlet.class); 
	
	boolean temp;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

		// Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		String categoria = request.getParameter("categoria");
		
		if (categoria == null || categoria.isEmpty())
		{
			try 
			{
				String strDataIni 	= request.getParameter("dataIni");
				String strDataFim 	= request.getParameter("dataFim");
				
				Boolean dtAtual 	= false;
				Date dataIni 		= null;
				Date dataFim 		= null;
				
				if ( (strDataIni == null || strDataIni.trim().equals("")) || (strDataFim == null || strDataFim.trim().equals("")) )
		    	{
		    		dtAtual = true;
		    	}
		    	else
		    	{				
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					dataIni = sdf.parse(strDataIni);
					dataFim = sdf.parse(strDataFim);
		    	}
				
				obterPontosMapaCalor(response, dataIni, dataFim, dtAtual);
			}
			catch(Exception err)
			{
				logger.error("Erro ao obter dados mapa de calor.", err);
				return;
			}
		}
		else
		{
			if (categoria.equals("Fluxo"))
			{
				try 
				{
					String strDataIni 	= request.getParameter("dataIni");
					String strDataFim 	= request.getParameter("dataFim");
	
					Boolean dtAtual 	= false;
					Date dataIni 		= null;
					Date dataFim 		= null;
					
			    	if ( (strDataIni == null || strDataIni.trim().equals("")) || (strDataFim == null || strDataFim.trim().equals("")) )
			    	{
			    		dtAtual = true;
			    	}
			    	else
			    	{				
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						dataIni = sdf.parse(strDataIni);
						dataFim = sdf.parse(strDataFim);
			    	}
					
					obterPontosMapaCalor(response, dataIni, dataFim, dtAtual);
					
				}
				catch(Exception err)
				{
					logger.error("Erro ao obter dados mapa de calor.", err);
					return;
				}
			}
			if (categoria.equals("Alertas"))
			{
				try 
				{
					String tipoAlerta 	= request.getParameter("tipoAlerta");
					String strDataIni 	= request.getParameter("dataIni");
					String strDataFim 	= request.getParameter("dataFim");
	
					Boolean dtAtual 	= false;
					Date dataIni 		= null;
					Date dataFim 		= null;
					
			    	if ( (strDataIni == null || strDataIni.trim().equals("")) || (strDataFim == null || strDataFim.trim().equals("")) )
			    	{
			    		dtAtual = true;
			    	}
			    	else
			    	{				
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						dataIni = sdf.parse(strDataIni);
						dataFim = sdf.parse(strDataFim);
			    	}
					
			    	obterPontosMapaCalorAlerta(response, dataIni, dataFim, dtAtual, tipoAlerta);
					
				}
				catch(Exception err)
				{
					logger.error("Erro ao obter dados mapa de calor.", err);
					return;
				}
			}
			if (categoria.equals("Irregularidades"))
			{
				try 
				{
					String tipoAlerta 	= request.getParameter("tipoAlerta");
					String strDataIni 	= request.getParameter("dataIni");
					String strDataFim 	= request.getParameter("dataFim");
	
					Boolean dtAtual 	= false;
					Date dataIni 		= null;
					Date dataFim 		= null;
					
			    	if ( (strDataIni == null || strDataIni.trim().equals("")) || (strDataFim == null || strDataFim.trim().equals("")) )
			    	{
			    		dtAtual = true;
			    	}
			    	else
			    	{				
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						dataIni = sdf.parse(strDataIni);
						dataFim = sdf.parse(strDataFim);
			    	}
					
			    	obterPontosMapaCalorIregularidade(response, dataIni, dataFim, dtAtual, tipoAlerta);
					
				}
				catch(Exception err)
				{
					logger.error("Erro ao obter dados mapa de calor.", err);
					return;
				}
			}
		}
	}  	

	private void obterPontosMapaCalor(HttpServletResponse response, Date dtIni, Date dtFim, Boolean dtAtual)
	{
		try
		{		
			
			List<MapaCalorPonto> listaEqptos = MapaCalorPontos.ObterQuantitativosPorEquipamento(dtIni, dtFim, dtAtual);

			MapaCalorPontos pontosMapaCalor = new MapaCalorPontos();
			pontosMapaCalor.setListaPontosMapaCalor(listaEqptos);
	
			enviaRespostaPontosMapaCalorXML(response, pontosMapaCalor);			
		}
		catch(Exception err) {
			logger.error("Erro ao obterPontosMapaCalor().", err);
			return;
		}			
		
	}
	
	private void obterPontosMapaCalorAlerta(HttpServletResponse response, Date dtIni, Date dtFim, Boolean dtAtual, String idTipoAlertaOcorrencia)
	{
		try
		{		
			
			List<MapaCalorPonto> listaEqptos = MapaCalorPontos.ObterContagemPorTipoAlerta(dtIni, dtFim, dtAtual, idTipoAlertaOcorrencia);

			MapaCalorPontos pontosMapaCalor = new MapaCalorPontos();
			pontosMapaCalor.setListaPontosMapaCalor(listaEqptos);
	
			enviaRespostaPontosMapaCalorXML(response, pontosMapaCalor);			
		}
		catch(Exception err) {
			logger.error("Erro ao obterPontosMapaCalor().", err);
			return;
		}			
		
	}
	
	private void obterPontosMapaCalorIregularidade(HttpServletResponse response, Date dtIni, Date dtFim, Boolean dtAtual, String idTipoAlertaOcorrencia)
	{
		try
		{		
			
			List<MapaCalorPonto> listaEqptos = MapaCalorPontos.ObterContagemPorTipoIregularidade(dtIni, dtFim, dtAtual, idTipoAlertaOcorrencia);

			MapaCalorPontos pontosMapaCalor = new MapaCalorPontos();
			pontosMapaCalor.setListaPontosMapaCalor(listaEqptos);
	
			enviaRespostaPontosMapaCalorXML(response, pontosMapaCalor);			
		}
		catch(Exception err) {
			logger.error("Erro ao obterPontosMapaCalor().", err);
			return;
		}			
		
	}
	
	
	private void enviaRespostaPontosMapaCalorXML( HttpServletResponse response, MapaCalorPontos pontos) throws JAXBException, IOException
	{
		//Formando dados para envio
		JAXBContext pontos_context = JAXBContext.newInstance(MapaCalorPontos.class);
		Marshaller marsHall = pontos_context.createMarshaller();
		marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		StringWriter sw = new StringWriter();
		marsHall.marshal(pontos, sw);
		String xml = sw.toString();
		sw.close();
		
		response.setHeader("Content-Type", "text/xml");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(xml);
		response.getWriter().flush();
		
		pontos = null;
	}


	
}
