package muralha.configuracaoequipamento;

import java.io.IOException;
import java.io.StringWriter;

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
import com.consilux.model.Usuario;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/ConfigurarEquipamento")
public class ConfiguracaoEquipamentoServlet 
	extends		javax.servlet.http.HttpServlet
	implements	javax.servlet.Servlet
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ConfiguracaoEquipamentoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		logger.info("Servlet Iniciado");
		
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  
		{
			logger.warn("Usuario nao autenticado!");
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}
		
		Integer idUsuario = ((Usuario)request.getSession().getAttribute("[usuario]")).getId();
		logger.debug("Id do usuario: " + idUsuario);
		
		logger.info("Realizando consulta");
		
		String strAcao = request.getParameter("acao");
		
		if(strAcao.equals("comboio"))
		{
			logger.info("Configuracao: comboio");
			
			String strTempoComboio = request.getParameter("tempo");
			String strStatus = request.getParameter("status");
			int status = (strStatus.equals("true")?1:0);
			
			try {
				ConfiguracaoEquipamentos.configComboio(idUsuario, strTempoComboio, status);
			} catch (Exception e) {
				logger.error("Erro ao gravar config: " + e.getMessage());
			}
		}
		
		if(strAcao.equals("roubado"))
		{
			logger.info("Configuracao: roubado");
			
			String strStatus = request.getParameter("status");
			int status = (strStatus.equals("true")?1:0);
			
			try {
				ConfiguracaoEquipamentos.configRoubo(status);
			} catch (Exception e) {
				logger.error("Erro ao gravar config: " + e.getMessage());
			}
		}
		
		if(strAcao.equals("clonado"))
		{
			logger.info("Configuracao: clonado");
			
			String strStatus = request.getParameter("status");
			int status = (strStatus.equals("true")?1:0);
			
			try {
				ConfiguracaoEquipamentos.configClonado(status);
			} catch (Exception e) {
				logger.error("Erro ao gravar config: " + e.getMessage());
			}
		}
		
		if(strAcao.equals("furtado"))
		{
			logger.info("Configuracao: furtado");
			
			String strStatus = request.getParameter("status");
			int status = (strStatus.equals("true")?1:0);
			
			try {
				ConfiguracaoEquipamentos.configFurto(status);
			} catch (Exception e) {
				logger.error("Erro ao gravar config: " + e.getMessage());
			}
		}
		
		if(strAcao.equals("licenciamento"))
		{
			logger.info("Configuracao: licenciamento");
			
			String strStatus = request.getParameter("status");
			int status = (strStatus.equals("true")?1:0);
			
			try {
				ConfiguracaoEquipamentos.configLicenciamento(status);
			} catch (Exception e) {
				logger.error("Erro ao gravar config: " + e.getMessage());
			}
		}
		
		if(strAcao.equals("monitorado"))
		{
			logger.info("Configuracao: monitorado");
			
			String strStatus = request.getParameter("status");
			int status = (strStatus.equals("true")?1:0);
			
			try {
				ConfiguracaoEquipamentos.configMonitorado(status);
			} catch (Exception e) {
				logger.error("Erro ao gravar config: " + e.getMessage());
			}
		}
		
		if(strAcao.equals("sequestro"))
		{
			logger.info("Configuracao: sequestro");
			
			String strStatus = request.getParameter("status");
			int status = (strStatus.equals("true")?1:0);
			
			try {
				ConfiguracaoEquipamentos.configSequestro(status);
			} catch (Exception e) {
				logger.error("Erro ao gravar config: " + e.getMessage());
			}
		}
		
		if(strAcao.equals("clandestino"))
		{
			logger.info("Configuracao: clandestino");
			
			String strStatus 	= request.getParameter("status");
			int status 			= (strStatus.equals("true")?1:0);
			String init_manha	= request.getParameter("initManha");
			String fim_manha 	= request.getParameter("fimManha");
			String init_tarde 	= request.getParameter("initTarde");
			String fim_tarde 	= request.getParameter("fimTarde");
			String passagens 	= request.getParameter("passagens");
			String tipo 		= request.getParameter("tipo");
			
			logger.debug(
				"Status: " 		+ status +
				", initManha: " + init_manha +
				", fimManha: " 	+ fim_manha + 
				", initTarde: " + init_tarde +
				", fimTarde: " 	+ fim_tarde +
				", passagens: "	+ passagens +
				", tipo: " 		+ tipo
			);
			
			try {
				ConfiguracaoEquipamentos.configClandestino(idUsuario, status, 
						init_manha, fim_manha, init_tarde, fim_tarde, passagens, tipo);
			} catch (Exception e) {
				logger.error("Erro ao gravar config: " + e.getMessage());
			}
		}
		
		if(strAcao.equals("ponto"))
		{
			logger.info("Configuracao: ponto de interesse");
			
			String strStatus = request.getParameter("status");
			int status = (strStatus.equals("true")?1:0);
			String tempo = request.getParameter("tempo");
			
			try {
				ConfiguracaoEquipamentos.configPonto(idUsuario, status, tempo);
			} catch (Exception e) {
				logger.error("Erro ao gravar Ponto de Interesse: " + e.getMessage());
			}
		}
		
		if(strAcao.equals("Semelhanca"))
		{
			logger.info("Configuracao: Semelhanca");			
			String qtde_erros = request.getParameter("qtde_erros");
			
			try {
				
				ConfiguracaoEquipamentos.configSemelhanca(
					idUsuario,
					Integer.parseInt(qtde_erros));
				
			} catch (Exception e) {
				logger.error("Erro ao gravar config: " + e.getMessage());
			}
		}

		if(strAcao.equals("VeiculosCorrelacionados"))
		{
			logger.info("Configuracao: VeiculosCorrelacionados");
			String qtdPasCorrelacaoBaixa = request.getParameter("qtd_pas_correlacao_baixa");
			String qtdPasCorrelacaoMedia = request.getParameter("qtd_pas_correlacao_media");
			String qtdPasCorrelacaoAlta = request.getParameter("qtd_pas_correlacao_alta");
			
			try {
				
				ConfiguracaoEquipamentos.configVeiculosCorrelacionados(
					idUsuario,
					Integer.parseInt(qtdPasCorrelacaoBaixa),
					Integer.parseInt(qtdPasCorrelacaoMedia),
					Integer.parseInt(qtdPasCorrelacaoAlta));
				
			} catch (Exception e) {
				logger.error("Erro ao gravar config: " + e.getMessage());
			}
		}
		
		if(strAcao.equals("obterConfigs"))
		{
			logger.info("Configuracao: obter dados");
			
			ConfiguracaoEquipamentos configEquips = new ConfiguracaoEquipamentos();
			ConfiguracaoEquipamento  configEquip  = new ConfiguracaoEquipamento();
			
			try {
				configEquips.definirConfiguracoes();
			} catch (Exception e) {
				logger.error("Erro ao obter config: " + e.getMessage());
			}

			configEquip = configEquips.obterConfiguracoes();
			
			try {
				EnviarRespostaXML(response, configEquip);
			} catch (Exception e) {
				logger.error("Erro ao obter config (2): " + e.getMessage());
			}
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, ConfiguracaoEquipamento configEquip)
			throws JAXBException, IOException
	{
		logger.info("Iniciando resposta XML");
		
		JAXBContext context;
		try
		{
			context = JAXBContext.newInstance(ConfiguracaoEquipamento.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(configEquip, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			//logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(configEquip.obterConfiguracoes().size()) );
			configEquip = null;
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
}