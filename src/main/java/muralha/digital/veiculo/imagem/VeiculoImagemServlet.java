/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 06/10/2021

*********************************************************************************/

package muralha.digital.veiculo.imagem;

import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;

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
import muralha.digital.veiculo.Veiculo;
import muralha.digital.veiculo.Veiculos;

@WebServlet("/MuralhaDigital/Veiculo/Imagem/Lista")
public class VeiculoImagemServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(VeiculoImagemServlet.class);
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
	    		    	
	    	if(strAcao.equals("obterListaImagensPorIdVeiculo"))	    	
	    		ObterListaImagensPorIdVeiculo(request, response);
	    		
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doPost() de requisição de Alerta: " + e.getMessage(), e);
	    }
    }

	protected void ObterListaImagensPorIdVeiculo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			String strIdVeiculo = request.getParameter("idVeiculo");
			
	    	if (strIdVeiculo == null || strIdVeiculo.equals("") || strIdVeiculo.equals("0")) 
	    	{
	    		String msg = "Identificador do Veículo não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
			UUID idVeiculo = null;

			try
	    	{
    			idVeiculo = UUID.fromString(strIdVeiculo.trim());
			}
	    	catch (Exception e)
	    	{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
	    	
			//Cria objeto de retorno
			Veiculo veiculo = Veiculos.ObterListaImagensPorIdVeiculo(idVeiculo);

			EnviarRespostaXML(response, veiculo);
			
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter imagens do veículo: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar o detalhe do veículo!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, Veiculo veiculo) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(Veiculo.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(veiculo, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			veiculo = null;
			
		}
		catch(Exception e)
		{
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de detalhes do veículo!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}
