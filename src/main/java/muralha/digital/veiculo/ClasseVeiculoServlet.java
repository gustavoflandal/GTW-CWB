/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 11/11/2021

*********************************************************************************/

package muralha.digital.veiculo;

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


@WebServlet("/MuralhaDigital/Veiculo/Classificacao")
public class ClasseVeiculoServlet extends HttpServlet
{
	
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ClasseVeiculoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErroMuralha("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
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
	    	
	    	else if(strAcao.equals("obterListaClassificacoes"))
	    		ObterListaClassificacoes(response);
    	}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar classificações de veículos!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ObterListaClassificacoes(HttpServletResponse response)
	{
		try 
		{
			//Cria objeto de retorno
			ClassesVeiculo equipamento = new ClassesVeiculo();
			equipamento.setListaClassesVeiculo(new ArrayList<ClasseVeiculo>());
				
			//Faz a consulta já existente no banco de dados
			List<ClasseVeiculo> listaEquipamento = ClassesVeiculo.ObterListaClassificacoes();
			equipamento.setListaClassesVeiculo(listaEquipamento);

			EnviarRespostaXML(response, equipamento);
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar os classificações de veículos!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, ClassesVeiculo classificacoes) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(ClassesVeiculo.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(classificacoes, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(classificacoes.getListaClassesVeiculo().size()) );
			classificacoes = null;
			
		}
		catch(Exception e) {
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de classificações de veículos!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}
