/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 11/11/2021

*********************************************************************************/

package muralha.digital.equipamento;

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


@WebServlet("/MuralhaDigital/Equipamento")
public class EquipamentoServlet extends HttpServlet
{
	
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(EquipamentoServlet.class);
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
	    	
	    	else if(strAcao.equals("obterListaEquipamentos"))
	    		ObterListaEquipamentos(response);
	    	else if(strAcao.equals("obterListaEquipamentosMunReg"))
	    		ObterListaEquipamentosMunReg(request, response);
	    	else if (strAcao.equals("obterListaMunicipiosEquipamentos"))
	    		ObterListaMunicipiosEquipamentos(request, response);
	    	else if (strAcao.equals("obterListaRegioesEquipamentos"))
	    		ObterListaRegioesEquipamentos(request, response);
	    	
    	}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar equipamentos!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ObterListaEquipamentos(HttpServletResponse response)
	{
		try 
		{
			//Cria objeto de retorno
			Equipamentos equipamento = new Equipamentos();
			equipamento.setListaEquipamentos(new ArrayList<Equipamento>());
				
			//Faz a consulta já existente no banco de dados
			List<Equipamento> listaEquipamento = Equipamentos.ObterListaEquipamentos();
			equipamento.setListaEquipamentos(listaEquipamento);

			EnviarRespostaXML(response, equipamento);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter equipamentos: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar os equipamentos!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	private void ObterListaEquipamentosMunReg(HttpServletRequest request, HttpServletResponse response)
	{
		try 
		{
			// Validação
			Boolean filtroValido = true;
			String msg = "";
			String strMunicipio = request.getParameter("municipio");
			String strRegiao = request.getParameter("regiao");

	    	if (strMunicipio == null || strMunicipio.trim().equals(""))
	    	{
	    		msg = "Favor informar o Município!";
				logger.error(msg);
				filtroValido = false;
	    	}

	    	if (strRegiao == null || strRegiao.trim().equals("")) //  || strRegiao.trim().equals("0")
	    	{
	    		msg = "Favor informar Região!";
				logger.error(msg);
				filtroValido = false;
	    	}
	    	
	    	Integer municipio = null;
	    	Integer regiao = null;
	    	
	    	try
	    	{
	    		if (strMunicipio != null && !strMunicipio.trim().equals("") && !strMunicipio.trim().equals("0"))
	    			municipio = Integer.parseInt(strMunicipio);
	    		
	    		if (strRegiao != null && !strRegiao.trim().equals("") && !strRegiao.trim().equals("0"))
	    			regiao = Integer.parseInt(strRegiao);
			}
	    	catch (NumberFormatException e)
	    	{
				msg = "Erro ao preparar dados para consulta!";
				logger.error(msg, e);
				filtroValido = false;
			}
			
	    	// ############
			if (filtroValido) {
				//Cria objeto de retorno
				Equipamentos equipamento = new Equipamentos();
				equipamento.setListaEquipamentos(new ArrayList<Equipamento>());
				
				//Faz a consulta já existente no banco de dados
				List<Equipamento> listaEquipamento = Equipamentos.ObterListaEquipamentos(municipio, regiao);
				equipamento.setListaEquipamentos(listaEquipamento);
				EnviarRespostaXML(response, equipamento);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, filtroValido, msg);
			}
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter equipamentos: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar os equipamentos!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ObterListaMunicipiosEquipamentos(HttpServletRequest request, HttpServletResponse response)
	{
		try 
		{
			//Cria objeto de retorno
			Equipamentos equipamento = new Equipamentos();
			equipamento.setListaEquipamentos(new ArrayList<Equipamento>());
			
			//Faz a consulta já existente no banco de dados
			List<Equipamento> listaEquipamento = Equipamentos.ObterListaMunicipiosEquipamentos();
			equipamento.setListaEquipamentos(listaEquipamento);
			EnviarRespostaXML(response, equipamento);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter municipios: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar os municipios!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ObterListaRegioesEquipamentos(HttpServletRequest request, HttpServletResponse response)
	{
		try 
		{
			//Cria objeto de retorno
			Equipamentos equipamento = new Equipamentos();
			equipamento.setListaEquipamentos(new ArrayList<Equipamento>());
			
			//Faz a consulta já existente no banco de dados
			List<Equipamento> listaEquipamento = Equipamentos.ObterListaRegioesEquipamentos();
			equipamento.setListaEquipamentos(listaEquipamento);
			EnviarRespostaXML(response, equipamento);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter regiões: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar as regiões!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, Equipamentos equipamentos) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(Equipamentos.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(equipamentos, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(equipamentos.getListaEquipamentos().size()) );
			equipamentos = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de equipamentos!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}
