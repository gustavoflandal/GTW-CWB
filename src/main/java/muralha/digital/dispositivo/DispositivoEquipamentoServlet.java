/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Fernando Amaral
  Data: 13/10/2021

*********************************************************************************/
package muralha.digital.dispositivo;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
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

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/DispositivoEquipamento")
public class DispositivoEquipamentoServlet extends javax.servlet.http.HttpServlet 
								implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DispositivoEquipamentoServlet.class); 
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		try 
		{
       		String msg = null;
			String strAcao = request.getParameter("acao");
	    	
	    	if (strAcao == null || strAcao == "") 
	    	{
	    		msg = "Ação não informada!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	else if(strAcao.equals("obterListaEquipamentos"))
	    		ObterDispositivosEquipamentos(response);
	    	else if(strAcao.equals("obterListaEquipamentosPorCategoria"))
	    		ObterDispositivosEquipamentosPorCategoria(request, response);
	    	else if(strAcao.equals("obterListaEquipamentosSentido"))
	    		ObterDispositivosEquipamentosSentido(response);
//	    	else if(strAcao.equals("ObterCamerasPorNumeroSerie"))
//	    		ObterCamerasPorNumeroSerie(request, response);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter dados de dispositivos/equipamentos: " + e.getMessage(), e);
			String msg = "Erro ao obter dados de dispositivos/equipamentos!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}		
	}  	
	
	private void ObterDispositivosEquipamentos(HttpServletResponse response)
	{
		try
		{
			//Cria objeto de retorno
			DispositivosEquipamentos dispositivosEquips = new DispositivosEquipamentos();
			dispositivosEquips.setListaDispositivos(new ArrayList<DispositivoEquipamento>());
				
			//Faz a consulta já existente no banco de dados
			List<DispositivoEquipamento> listaDisp = DispositivosEquipamentos.ObterListaDispositivos();
			dispositivosEquips.setListaDispositivos(listaDisp);

			EnviaRespostaDispositivosXML(response, dispositivosEquips);
			
		}
		catch(Exception err) {
			logger.error("Erro ao ObterDispositivosEquipamentos().", err);
			return;
		}			
		
	}
	
	private void ObterDispositivosEquipamentosPorCategoria(HttpServletRequest request, HttpServletResponse response)
	{
		String categoria = request.getParameter("categoria");
		try
		{
			//Cria objeto de retorno
			DispositivosEquipamentos dispositivosEquips = new DispositivosEquipamentos();
			dispositivosEquips.setListaDispositivos(new ArrayList<DispositivoEquipamento>());

			//Faz a consulta já existente no banco de dados
			List<DispositivoEquipamento> listaDisp = DispositivosEquipamentos.ObterListaDispositivosPorCategoria(categoria);
			dispositivosEquips.setListaDispositivos(listaDisp);

			EnviaRespostaDispositivosXML(response, dispositivosEquips);

		}
		catch(Exception err) {
			logger.error("Erro ao ObterDispositivosEquipamentos().", err);
			return;
		}

	}

	private void ObterDispositivosEquipamentosSentido(HttpServletResponse response)
	{
		try
		{
			//Cria objeto de retorno
			DispositivosEquipamentos dispositivosEquips = new DispositivosEquipamentos();
			dispositivosEquips.setListaDispositivos(new ArrayList<DispositivoEquipamento>());
				
			//Faz a consulta já existente no banco de dados
			List<DispositivoEquipamento> listaDisp = DispositivosEquipamentos.ObterListaDispositivosSentido();
			dispositivosEquips.setListaDispositivos(listaDisp);

			EnviaRespostaDispositivosXML(response, dispositivosEquips);
			
		}
		catch(Exception err) {
			logger.error("Erro ao ObterDispositivosEquipamentos().", err);
			return;
		}			
		
	}
	
	private void EnviaRespostaDispositivosXML( HttpServletResponse response, DispositivosEquipamentos dispositivos) throws JAXBException, IOException
	{
		//Formando dados para envio
		JAXBContext dispositivos_context = JAXBContext.newInstance(DispositivosEquipamentos.class);
		Marshaller marsHall = dispositivos_context.createMarshaller();
		marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		StringWriter sw = new StringWriter();
		marsHall.marshal(dispositivos, sw);
		String xml = sw.toString();
		sw.close();
		
		//logger.info(xml);
		
		response.setHeader("Content-Type", "text/xml");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(xml);
		response.getWriter().flush();
	}	
	
	private void EnviaRespostaCamerasXML(HttpServletResponse response, List<CameraEquipamento> cameras)
	        throws IOException 
	{
	    StringBuilder xml = new StringBuilder();
	    xml.append("<cameras>");

	    for (CameraEquipamento cam : cameras) {
	        xml.append("<camera>");
	        xml.append("<serieEquipamento>").append(cam.getSerieEquipamento()).append("</serieEquipamento>");
	        xml.append("<tipoCamera>").append(cam.getTipoCamera()).append("</tipoCamera>");
	        xml.append("<desativado>").append(cam.isDesativado()).append("</desativado>");
	        xml.append("<emOperacao>").append(cam.isEmOperacao()).append("</emOperacao>");
	        xml.append("<posicaoLat>").append(cam.getPosicaoLat()).append("</posicaoLat>");
	        xml.append("<posicaoLon>").append(cam.getPosicaoLon()).append("</posicaoLon>");
	        xml.append("<tipo>").append(cam.getTipo()).append("</tipo>");
	        xml.append("<usuario>").append(cam.getUsuario()).append("</usuario>");
	        xml.append("</camera>");
	    }

	    xml.append("</cameras>");

	    response.setHeader("Content-Type", "text/xml");
	    response.setStatus(HttpServletResponse.SC_OK);
	    response.getWriter().write(xml.toString());
	    response.getWriter().flush();
	}


	
//	private void ObterCamerasPorNumeroSerie(HttpServletRequest request, HttpServletResponse response) {
//	    try {
//	        int numeroSerie = request.getParameter("numeroSerie");
//
//	        if (numeroSerie == null || numeroSerie.trim().isEmpty()) {
//	            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Parâmetro 'numeroSerie' não informado.");
//	            return;
//	        }
//
//	        List<CameraEquipamento> listaCameras = CameraEquipamento.ObterCamerasPorNumeroSerie(numeroSerie);
//
//	        EnviaRespostaCamerasXML(response, listaCameras);
//
//	    } catch (Exception err) {
//	        logger.error("Erro ao ObterCamerasPorNumeroSerie().", err);
//	        respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro ao obter câmeras do equipamento.");
//	    }
//	}


}
