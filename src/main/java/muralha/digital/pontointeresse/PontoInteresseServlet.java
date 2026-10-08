/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: GuilhermeBassan
  Data: 12/01/2022

*********************************************************************************/
package muralha.digital.pontointeresse;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/PontoInteresse")
public class PontoInteresseServlet
	extends		javax.servlet.http.HttpServlet
	implements	javax.servlet.Servlet
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(PontoInteresseServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		// Verifica acesso
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  
		{
			logger.warn("Usuario nao autenticado!");
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}
		
		try
		{
			logger.info("Realizando consulta");
			
			String strAcao = request.getParameter("acao");
			String strConsulta = request.getParameter("consulta");
			//logger.info(strAcao);
			
			if(strAcao.equals("salvaPontoInteresse"))
			{
				logger.info("Requisicao: gravacao");
				
				PontoInteresse pontoInteresse = new PontoInteresse();
				
				UUID uuid_ponto = UUID.randomUUID();
				String strNome = request.getParameter("nome");
				String strDesc = request.getParameter("desc");
				String strTipo = request.getParameter("tipo");
				String strLati = request.getParameter("lati");
				String strLong = request.getParameter("long");
				String strEqui = request.getParameter("equi");
				
				logger.info(strEqui);
				
				pontoInteresse.setId(uuid_ponto);
				pontoInteresse.setNome(strNome);
				pontoInteresse.setDescricao(strDesc);
				pontoInteresse.setTipo(Integer.parseInt(strTipo));
				pontoInteresse.setLatitude(Float.parseFloat(strLati));
				pontoInteresse.setLongitude(Float.parseFloat(strLong));
				pontoInteresse.setEquipamentos(strEqui);
				
				PontosInteresse.inserirPontoInteresse(pontoInteresse);
				
				String msg = "Ponto inserido com sucesso";
				respostaXML.EnviarRespostaRequisicaoXML(response, true, msg);
			}
			else if(strAcao.equals("obterPontosInteresse"))
			{
				logger.info("Requisicao: consulta");
				
				PontosInteresse pontosInteresse = new PontosInteresse();
				pontosInteresse.setListaPontosInteresse(PontosInteresse.consultarPontosInteresse());
				//List<PontoInteresse> pontosInteresse = PontosInteresse.consultarPontosInteresse();
				
				EnviarRespostaXML(response, pontosInteresse);
				//enviarRespostaXML(response, pontos);
			}
			else if (strConsulta.equals("equipamento"))
			{
				consultaEquip();
			}
			else if (strConsulta.equals("local"))
			{
				consultaLocal();
			}
			else
			{
				logger.warn("Ação não reconhecida");
			}
		}
		catch(Exception e)
		{
			String msg = "Problema ao consultar tipo de equipamento/local";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, PontosInteresse pontosInteresse)
			throws JAXBException, IOException
	{
		logger.info("Iniciando resposta XML");
		
		JAXBContext context;
		try
		{
			context = JAXBContext.newInstance(PontosInteresse.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(pontosInteresse, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(pontosInteresse.getListaPontosInteresse().size()) );
			pontosInteresse = null;
			logger.info("Resposta XML - Sucesso");
			
		}
		catch (Exception e)
		{
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de pontos de interesse!";
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void consultaEquip()
	{
		logger.info("[consultaEquip] -> Realizando consulta");
	}
	
	private void consultaLocal()
	{
		logger.info("[consultaLocal] -> Realizando consulta");
	}
}
