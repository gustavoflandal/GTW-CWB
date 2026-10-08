/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 01/10/2021

*********************************************************************************/

package muralha.digital.consulta;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;


@WebServlet("/MuralhaDigital/AlertaOcorrencia/TipoRegistro")
public class TipoRegistroServlet extends HttpServlet
{
	
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TipoRegistroServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		try 
		{
			//Cria objeto de retorno
			TiposRegistros tiposRegistro = new TiposRegistros();
			tiposRegistro.setListaTiposRegistro(new ArrayList<TipoRegistro>());
				
			//Faz a consulta já existente no banco de dados
			List<TipoRegistro> listaTipoRegistro = TiposRegistros.ObterListaTiposRegistro();
			tiposRegistro.setListaTiposRegistro(listaTipoRegistro);

			EnviarRespostaXML(response, tiposRegistro);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter tipos de registro: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar tipos de registro!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, TiposRegistros tiposRegistro) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(TiposRegistros.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(tiposRegistro, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(tiposRegistro.getListaTiposRegistro().size()) );
			tiposRegistro = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de tipo de registro!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}
