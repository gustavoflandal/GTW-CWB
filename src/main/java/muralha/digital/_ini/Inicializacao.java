package muralha.digital._ini;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import muralha.digital.monitoramento.Camera;
import muralha.digital.monitoramento.CamerasMonitoramentoAoVivo;
import muralha.digital.monitoramento.EquipamentoCameras;
import muralha.digital.websocket.ClienteSessoes;

 /*
 * Classe que inicia os processos da Muralha Digital
 */
public class Inicializacao extends HttpServlet 
{
	private static final long serialVersionUID = 336883379154578465L;
	private static final Logger logger = Logger.getLogger(Inicializacao.class);
	
	public static 	List<EquipamentoCameras> listaEqptoCameras   	= Collections.synchronizedList(new ArrayList<EquipamentoCameras>());
	public static  	Integer totalCamerasGrupoExibicao 				= 9;
	public static 	Integer grupoCamerasEmExibicao 					= 1;
	
	public static 	ClienteSessoes 	serverWebSocket 				= null;
	public static  	ServletContext 	servletContexto					= null;
	public static 	String 			AlertasAtivo					= "";
	public static 	String 			VeiculosTempoRealWebSocket		= "";
	public static 	String 			BlitzEletronica					= "";
	public static 	String 			BlitzDigital 					= "";
	public static	String			BlitzDigitalDiretorioDocumentos	= "";
	public static	String			BlitzDigitalApiKeyGeocoding		= "";
	public static	String			BlitzDigitalDiretorioImagens	= "";
	
	//Notificação Muralha
	public static	boolean			emailAtivo						= false;
	public static	boolean			smsAtivo						= false;
	public static	Integer			IntervaloExecucaoEmailMinutos	= null;
	public static	Integer			IntervaloExecucaoSmsMinutos		= null;
	public static   String 			LinkPaginaDetalheNotificacao	= null;
	
	//Integracao SMS
	//Twilio
	public static   String 			AccountSID 						= null; 
	public static   String 			AuthToken 						= null;
	public static   String 			MessagingServiceSID 			= null;
	public static   String 			NotifyServiceSID 				= null;
	//Facilita Móvel
	public static 	String 			FacilitaMovelUser				= null;
	public static 	String 			FacilitaMovelPassword			= null;
	//SMSDEV
	public static 	String 			SmsDevKey						= null;
	//Facilita Móvel
	public static 	String 			ComteleEndpoint					= null;
	public static 	String 			ComteleAuthKey					= null;
	
	//Bitly URL Shortener
	public static   String 			BitlyShortenEndpoint			= null;
	public static   String 			BitlyToken						= null;
	public static   String 			BitlyDomain						= null;
	public static   String 			BitlyGroupGuid					= null;
	
	public static	Integer			TempoMaxConsultaVideosMonEmMinutos	= null;
	public static	String			DirFFMPEG						= null;
	public static	String			DirExeFFMPEG					= null;
	public static	String			DirTemporarioVideos				= null;
	public static	String			FormatoVideoTemp				= null;
	
	public static 	boolean			LimiteConsultaAtivo					= false;
	public static	Integer			LimiteConsultaVeiculosEmSegundos	= null;
	public static	boolean			ExigirPlacaCompleta 				= false;
	public static	Integer			QtdeMaxCaracterEspecialPlaca		= null;
	public static	Integer			TamanhoMinimoPlaca					= null;
	
	public static 	boolean			ExportarCadMonitorado				= false;
	public static	Integer			QtdeMaxCaracterEspecialPlacaCadMon	= null;
	
	public void init(final ServletConfig config) throws ServletException
	{
		try 
		{
			logger.info("------------------------------------------------------" );
			logger.info("--- Iniciando serviços da Muralha Digital");
			logger.info("------------------------------------------------------" ); 
			
	    	servletContexto = config.getServletContext();
	    	
	    	//Obtem as informações gerais do GPW
	    	ObterConfiguracao(servletContexto);
	    	
	    	
	    	//Conexao com os clientes Web Browser para envio de informacoes
	    	//via websocket 
	    	serverWebSocket = new ClienteSessoes();
	    	serverWebSocket.iniciaListaClientesWebSockets(); 
		}
		catch (Exception e)
		{
			logger.error("Erro na inicializacao da Muralha Digital. " + e.getMessage(), e);
		}
	}
	
	public void destroy()
	{
		try 
		{
			serverWebSocket.PararEnvioAosClientes();
			
		} catch (Exception e) {
			logger.error("Erro na finalização da Muralha Digital. " + e.getMessage(), e);
		}
	}

    
    public void ObterConfiguracao(ServletContext servletContext)
    {
    	try 
  	  	{
    		logger.info("--- LENDO XML DE CONFIGURAÇÃO DA MURALHA DIGITAL -------------------");
  		  
    		File fXmlFile = new File( servletContext.getRealPath("/WEB-INF/muralha-digital-config.xml") );
  		  	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
  		  	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
  		  	Document doc = dBuilder.parse(fXmlFile);

  		  	doc.getDocumentElement().normalize();
	    
  		  	ConfigAlertaTempoReal(doc);
  		  	ConfigVeiculoTempoReal(doc);
  		  	ConfigBlitzEletronica(doc);
			ConfigBlitzDigital(doc);
  		  	
  		  	//Notificação de Alerta/Ocorrência
  		  	ConfigNotificacaoMuralha(doc);
  		  	
  		  	//Integração SMS
  		  	ConfigIntegracaoSMS_Twilio(doc);
  		  	ConfigIntegracaoSMS_FacilitaMovel(doc);
  		  	ConfigIntegracaoSMS_SmsDev(doc);
  		  	ConfigIntegracaoSMS_Comtele(doc);
  		  	
  		  	//Bitly URL Shortener
  		  	ConfigBitlyUrlShortener(doc);
  		  	
  		  	//Configurações para consulta de histórico de vídeos de monitoramento
  		  	ConfigHistoricoVideoMonitoramento(doc);
  		  	
  		  	//Configuração de equipamentos e câmeras para monitoramento ao vivo
//  		  	ConfigEquipamentoCameras(doc);
  		  	ConfigEquipamentoCamerasBD();
  		  	
  		  	//Configurações para consulta de veículos
  		  	ConfigConsultaVeiculos(doc);
  		  	
  		  	//Configurações para veículos monitorados
  		  	ConfigVeiculoMonitorado(doc);
  	  	}
  	  	catch (Exception e) {
    		logger.error("Falha na leitura do arquivo XML de configurações da Muralha Digital. " + e.getMessage(), e);
  	  	}
    }  
        
    private void ConfigAlertaTempoReal(Document doc)
    {
	    NodeList nList = doc.getElementsByTagName("Alertas");
	    
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            AlertasAtivo = eElement.getElementsByTagName("ativo").item(0).getTextContent();		            
	        }
	    }  		    		   	    
	    logger.info("--- Alertas em Tempo Real na Muralha Digital ==> " + AlertasAtivo);
    }
    
    private void ConfigVeiculoTempoReal(Document doc)
    {
    	NodeList nList = doc.getElementsByTagName("VeiculoTempoReal");
    	
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            VeiculosTempoRealWebSocket = eElement.getElementsByTagName("ativo").item(0).getTextContent();		            
	        }
	    }  		    		   	    
	    logger.info("--- Veiculos em Tempo Real via WebSocket na Muralha Digital ==> " + VeiculosTempoRealWebSocket);
    }
    
    private void ConfigBlitzEletronica(Document doc)
    {
    	NodeList nList = doc.getElementsByTagName("BlitzEletronica");
    	
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            BlitzEletronica = eElement.getElementsByTagName("ativo").item(0).getTextContent();		            
	        }
	    }  		    		   	    
	    logger.info("--- Blitz Eletronica via WebSocket na Muralha Digital ==> " + BlitzEletronica);
    }
    
    
    private void ConfigNotificacaoMuralha(Document doc)
    {
    	try
    	{
			NodeList nList = doc.getElementsByTagName("NotificacaoMuralha");
	    	
		    for (int temp = 0; temp < nList.getLength(); temp++) 
		    {
		        Node nNode = nList.item(temp);
	
		        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
		        {
		            Element eElement = (Element) nNode;

		            if (eElement.getElementsByTagName("EmailAtivo") != null)
		            	emailAtivo = eElement.getElementsByTagName("EmailAtivo").item(0).getTextContent().trim().equals("") ? false : "1".equals((eElement.getElementsByTagName("EmailAtivo").item(0).getTextContent().trim()));
		            
		            if (eElement.getElementsByTagName("SmsAtivo") != null)
		            	smsAtivo = eElement.getElementsByTagName("SmsAtivo").item(0).getTextContent().trim().equals("") ? false : "1".equals((eElement.getElementsByTagName("SmsAtivo").item(0).getTextContent().trim()));
		            
		            if (eElement.getElementsByTagName("IntervaloExecucaoEmailMinutos") != null)
		            	IntervaloExecucaoEmailMinutos = eElement.getElementsByTagName("IntervaloExecucaoEmailMinutos").item(0).getTextContent().trim().equals("") ? null : Integer.parseInt(eElement.getElementsByTagName("IntervaloExecucaoEmailMinutos").item(0).getTextContent().trim());
		            
		            if (eElement.getElementsByTagName("IntervaloExecucaoSmsMinutos") != null)
		            	IntervaloExecucaoSmsMinutos = eElement.getElementsByTagName("IntervaloExecucaoSmsMinutos").item(0).getTextContent().trim().equals("") ? null : Integer.parseInt(eElement.getElementsByTagName("IntervaloExecucaoSmsMinutos").item(0).getTextContent().trim());
		            
		            if (eElement.getElementsByTagName("LinkPaginaDetalheNotificacao") != null)
		            	LinkPaginaDetalheNotificacao = eElement.getElementsByTagName("LinkPaginaDetalheNotificacao").item(0).getTextContent();
		        }
		    }
    	}
    	catch (Exception e)
    	{
			logger.error("Erro ao carregar configurações de notificação de Email e SMS na Muralha Digital!", e);
		}
	    
    	
    	if (emailAtivo && !(IntervaloExecucaoEmailMinutos > 0))
    		emailAtivo = false;
    	
    	if (smsAtivo && !(IntervaloExecucaoSmsMinutos > 0))
    		smsAtivo = false;
    	
    	
    	if (!emailAtivo && !smsAtivo)
    	{
    		logger.info("--- Notificações de Email e SMS estão desabilitadas!");
    	}
    	else if (emailAtivo && smsAtivo)
    	{
    		logger.info("--- Notificações de Email e SMS estão habilitadas!");
	    	logger.info("--- Intervalo de Envio de Notificação em Minutos na Muralha Digital ==> Email: " + IntervaloExecucaoEmailMinutos + " | SMS: " + IntervaloExecucaoSmsMinutos);
    	}
    	else if (emailAtivo && !smsAtivo)
    	{
    		logger.info("--- Notificação de Email habilitada!");
	    	logger.info("--- Intervalo de Envio de Email na Muralha Digital ==> " + IntervaloExecucaoEmailMinutos + " minuto" + (IntervaloExecucaoEmailMinutos > 1 ? "s" : ""));
	    	logger.info("--- Notificação de SMS desabilitada!");
    	}
    	else if (!emailAtivo && smsAtivo)
    	{
    		logger.info("--- Notificação de Email desabilitada!");
    		logger.info("--- Notificação de SMS habilitada!");
    		logger.info("--- Intervalo de Envio de SMS na Muralha Digital ==> " + IntervaloExecucaoSmsMinutos + " minuto" + (IntervaloExecucaoSmsMinutos > 1 ? "s" : ""));
    	}
    	
	    logger.info("--- Pagina de detalhe Alerta/Irregularidade na Muralha Digital ==> " + LinkPaginaDetalheNotificacao.trim());
    }
    
    private void ConfigIntegracaoSMS_Twilio(Document doc)
    {
    	NodeList nList = doc.getElementsByTagName("Twilio");
    	boolean configTwilio = false;
    	
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            if (eElement.getElementsByTagName("AccountSID") != null)
	            	AccountSID = eElement.getElementsByTagName("AccountSID").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("AccountSID").item(0).getTextContent().trim();
	            
	            if (eElement.getElementsByTagName("AuthToken") != null)
	            	AuthToken = eElement.getElementsByTagName("AuthToken").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("AuthToken").item(0).getTextContent().trim();
	            
	            if (eElement.getElementsByTagName("MessagingServiceSID") != null)
	            	MessagingServiceSID = eElement.getElementsByTagName("MessagingServiceSID").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("MessagingServiceSID").item(0).getTextContent().trim();
	            
	            if (eElement.getElementsByTagName("NotifyServiceSID") != null)
	            	NotifyServiceSID = eElement.getElementsByTagName("NotifyServiceSID").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("NotifyServiceSID").item(0).getTextContent().trim();
	        }
	    }
	    
	    configTwilio = (AccountSID != null && AuthToken != null && MessagingServiceSID != null && NotifyServiceSID != null);
	    
	    if (smsAtivo)
	    	logger.info("--- Integração SMS Twilio na Muralha Digital ==> " + configTwilio);
    }
    
    private void ConfigIntegracaoSMS_FacilitaMovel(Document doc)
    {
    	NodeList nList = doc.getElementsByTagName("FacilitaMovel");
    	boolean configFacilitaMovel = false;
    	
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            if (eElement.getElementsByTagName("FacilitaMovelUser") != null)
	            	FacilitaMovelUser = eElement.getElementsByTagName("FacilitaMovelUser").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("FacilitaMovelUser").item(0).getTextContent().trim();
	            
	            if (eElement.getElementsByTagName("FacilitaMovelPassword") != null)
	            	FacilitaMovelPassword = eElement.getElementsByTagName("FacilitaMovelPassword").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("FacilitaMovelPassword").item(0).getTextContent().trim();
	        }
	    }
	    
	    configFacilitaMovel = (FacilitaMovelUser != null && FacilitaMovelPassword != null);
	    
	    if (smsAtivo)
	    	logger.info("--- Integração SMS Facilita Móvel na Muralha Digital ==> " + configFacilitaMovel);
    }
    
    private void ConfigIntegracaoSMS_SmsDev(Document doc)
    {
    	NodeList nList = doc.getElementsByTagName("SmsDev");
    	boolean configSmsDev = false;
    	
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            if (eElement.getElementsByTagName("SmsDevKey") != null)
	            	SmsDevKey = eElement.getElementsByTagName("SmsDevKey").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("SmsDevKey").item(0).getTextContent().trim();
	        }
	    }
	    
	    configSmsDev = (SmsDevKey != null);
	    
	    if (smsAtivo)
	    	logger.info("--- Integração SMS SMSDEV na Muralha Digital ==> " + configSmsDev);
    }
    
    private void ConfigIntegracaoSMS_Comtele(Document doc)
    {
    	NodeList nList = doc.getElementsByTagName("Comtele");
    	boolean configComtele = false;
    	
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            if (eElement.getElementsByTagName("ComteleEndpoint") != null)
	            	ComteleEndpoint = eElement.getElementsByTagName("ComteleEndpoint").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("ComteleEndpoint").item(0).getTextContent().trim();
	            
	            if (eElement.getElementsByTagName("ComteleAuthKey") != null)
	            	ComteleAuthKey = eElement.getElementsByTagName("ComteleAuthKey").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("ComteleAuthKey").item(0).getTextContent().trim();
	        }
	    }
	    
	    configComtele = (ComteleEndpoint != null && ComteleAuthKey != null);
	    
	    if (smsAtivo)
	    	logger.info("--- Integração SMS Comtele na Muralha Digital ==> " + configComtele);
    }
    
    private void ConfigBitlyUrlShortener(Document doc)
    {
    	NodeList nList = doc.getElementsByTagName("BitlyUrlShortener");
    	boolean configBitly = false;
    	
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            if (eElement.getElementsByTagName("BitlyShortenEndpoint") != null)
	            	BitlyShortenEndpoint = eElement.getElementsByTagName("BitlyShortenEndpoint").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("BitlyShortenEndpoint").item(0).getTextContent().trim();
	            
	            if (eElement.getElementsByTagName("BitlyToken") != null)
	            	BitlyToken = eElement.getElementsByTagName("BitlyToken").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("BitlyToken").item(0).getTextContent().trim();
	            
	            if (eElement.getElementsByTagName("BitlyDomain") != null)
	            	BitlyDomain = eElement.getElementsByTagName("BitlyDomain").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("BitlyDomain").item(0).getTextContent().trim();
	            
	            if (eElement.getElementsByTagName("BitlyGroupGuid") != null)
	            	BitlyGroupGuid = eElement.getElementsByTagName("BitlyGroupGuid").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("BitlyGroupGuid").item(0).getTextContent().trim();
	        }
	    }
	    
	    configBitly = (BitlyShortenEndpoint != null && BitlyToken != null && BitlyDomain != null && BitlyGroupGuid != null);
	    
	    logger.info("--- Bitly URL Shortener na Muralha Digital ==> " + configBitly);
    }
    
    private void ConfigHistoricoVideoMonitoramento(Document doc)
    {
    	NodeList nList = doc.getElementsByTagName("VideosMonitoramento");
    	
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            if (eElement.getElementsByTagName("TempoMaximoConsultaEmMinutos") != null)
	            	TempoMaxConsultaVideosMonEmMinutos = eElement.getElementsByTagName("TempoMaximoConsultaEmMinutos").item(0).getTextContent().trim().equals("") ? null : Integer.parseInt(eElement.getElementsByTagName("TempoMaximoConsultaEmMinutos").item(0).getTextContent().trim());
	            
	            if (eElement.getElementsByTagName("DirFFMPEG") != null)
	            {
	            	DirFFMPEG = eElement.getElementsByTagName("DirFFMPEG").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("DirFFMPEG").item(0).getTextContent().trim();
	            	if (!Files.exists(Paths.get(DirFFMPEG)))
	            		logger.error("--- Diretório do FFMPEG não encontrado ==> " + DirFFMPEG);
	            }
	            
	            if (eElement.getElementsByTagName("DirExeFFMPEG") != null)
	            {
	            	DirExeFFMPEG = eElement.getElementsByTagName("DirExeFFMPEG").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("DirExeFFMPEG").item(0).getTextContent().trim();
	            	if (!Files.exists(Paths.get(DirExeFFMPEG)))
	            		logger.error("--- Executável do FFMPEG não encontrado ==> " + DirExeFFMPEG);
	            }
	            
	            if (eElement.getElementsByTagName("DirTemporarioVideos") != null)
	            {
	            	DirTemporarioVideos = eElement.getElementsByTagName("DirTemporarioVideos").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("DirTemporarioVideos").item(0).getTextContent().trim();
	            	if (!Files.exists(Paths.get(DirTemporarioVideos)))
	            		logger.error("--- Diretório temporário de videos não encontrado ==> " + DirTemporarioVideos);
	            }

	            if (eElement.getElementsByTagName("FormatoVideoTemp") != null)
	            	FormatoVideoTemp = eElement.getElementsByTagName("FormatoVideoTemp").item(0).getTextContent().trim().equals("") ? null : eElement.getElementsByTagName("FormatoVideoTemp").item(0).getTextContent().trim();
	        }
	    }
	    
	    logger.info("--- Tempo máximo para consulta de videos de monitoramento (minutos) na Muralha Digital ==> " + TempoMaxConsultaVideosMonEmMinutos);
	    logger.info("--- Diretório FFMPEG ==> " + DirFFMPEG);
	    logger.info("--- Diretório com executável FFMPEG ==> " + DirExeFFMPEG);
	    logger.info("--- Diretório temporário de videos ==> " + DirTemporarioVideos);
	    logger.info("--- Formato do video temporário ==> " + FormatoVideoTemp);
    }
    
    @SuppressWarnings("unused")
	private void ConfigEquipamentoCameras(Document doc)
    {
    	try
    	{
    		Integer idGrupoExibicao = 1, contadorCameras = 1;
    		
    		NodeList nList = doc.getElementsByTagName("equipamento");
    		
		    for (int temp = 0; temp < nList.getLength(); temp++) 
		    {
		        Node nNode = nList.item(temp);
	
		        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
		        {
		            Element eElement = (Element) nNode;
		            
		            Integer idLocal = null, serieEquipamento = null;
		            String nomeEquipamento = null;
		            
		            idLocal = Integer.parseInt(eElement.getAttribute("idLocal").trim());
		            serieEquipamento = Integer.parseInt(eElement.getAttribute("serieEquipamento").trim());
		            nomeEquipamento = eElement.getAttribute("nome").trim();
		            
		            logger.info("Equipamento Id : " + idLocal + " | Série: " + serieEquipamento + " | Nome: " + nomeEquipamento);
		            
		            EquipamentoCameras equipamentoCameras = new EquipamentoCameras();
		            equipamentoCameras.setIdLocal(idLocal);
		            equipamentoCameras.setSerieEquipamento(serieEquipamento);
		            equipamentoCameras.setNome(nomeEquipamento);
		            listaEqptoCameras.add(equipamentoCameras);
		            
		            int qtdeMonitoramento = eElement.getElementsByTagName("monitoramento").getLength();
		            for (int i = 0; i < qtdeMonitoramento; i++) 
		            {
		            	if (contadorCameras > totalCamerasGrupoExibicao)
		            	{
		            		idGrupoExibicao++;
		            		contadorCameras = 1;
		            	}
		            	
		            	String descricaoCamera = eElement.getElementsByTagName("monitoramento").item(i).getTextContent().trim();
		            	logger.info("Cameras de Monitoramento: " + serieEquipamento + " - " + descricaoCamera);
		            	
		            	Camera cam = new Camera();
		            	cam.setDescricao(descricaoCamera);
		            	cam.setIp(((Element) eElement.getElementsByTagName("monitoramento").item(i)).getAttribute("ip"));
		            	cam.setIpLocal(((Element) eElement.getElementsByTagName("monitoramento").item(i)).getAttribute("iplocal"));
		            	cam.setQualidade(((Element) eElement.getElementsByTagName("monitoramento").item(i)).getAttribute("qualidade"));
		            	cam.setFramerate(((Element) eElement.getElementsByTagName("monitoramento").item(i)).getAttribute("framerate"));
		            	cam.setResolution(((Element) eElement.getElementsByTagName("monitoramento").item(i)).getAttribute("resolution"));
		            	cam.setTipoCam(((Element) eElement.getElementsByTagName("monitoramento").item(i)).getAttribute("tp"));
		            	cam.setPista(((Element) eElement.getElementsByTagName("monitoramento").item(i)).getAttribute("pista"));
		            	
		            	cam.setIdGrupoExibicao(idGrupoExibicao);
		            	
		            	equipamentoCameras.AdicionarCamera(cam);
		            	
		            	contadorCameras++;
					}
		        }
		    }
    	}
    	catch (Exception e)
    	{
			logger.error("Erro ao carregar configurações de equipamentos e câmeras para monitoramento ao vivo na Muralha Digital!", e);
		}
	    
	    logger.info("--- Equipamentos e Câmeras para monitoramento ao vivo carregadas com sucesso ---");
    }
    
    private void ConfigEquipamentoCamerasBD()
    {
    	try
    	{
    		listaEqptoCameras = CamerasMonitoramentoAoVivo.ObterCamerasMonitoramentoAoVivoBD();
    	}
    	catch (Exception e)
    	{
			logger.error("Erro ao carregar configurações de equipamentos e câmeras para monitoramento ao vivo na Muralha Digital!", e);
		}
	    
	    logger.info("--- Equipamentos e Câmeras para monitoramento ao vivo carregadas com sucesso ---");
    }
    
    private void ConfigConsultaVeiculos(Document doc)
    {
    	NodeList nList = doc.getElementsByTagName("ConsultaVeiculos");
    	
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            if (eElement.getElementsByTagName("LimiteConsultaAtivo") != null)
	            	LimiteConsultaAtivo = eElement.getElementsByTagName("LimiteConsultaAtivo").item(0).getTextContent().trim().equals("") ? false : "1".equals((eElement.getElementsByTagName("LimiteConsultaAtivo").item(0).getTextContent().trim()));
	            
	            if (eElement.getElementsByTagName("LimiteConsultaEmSegundos") != null)
	            	LimiteConsultaVeiculosEmSegundos = eElement.getElementsByTagName("LimiteConsultaEmSegundos").item(0).getTextContent().trim().equals("") ? null : Integer.parseInt(eElement.getElementsByTagName("LimiteConsultaEmSegundos").item(0).getTextContent().trim());
	            
	            if (eElement.getElementsByTagName("ExigirPlacaCompleta") != null)
	            	ExigirPlacaCompleta = eElement.getElementsByTagName("ExigirPlacaCompleta").item(0).getTextContent().trim().equals("") ? false : "1".equals((eElement.getElementsByTagName("ExigirPlacaCompleta").item(0).getTextContent().trim()));
	            
	            if (eElement.getElementsByTagName("QtdeMaxCaracterEspecialPlaca") != null)
	            	QtdeMaxCaracterEspecialPlaca = eElement.getElementsByTagName("QtdeMaxCaracterEspecialPlaca").item(0).getTextContent().trim().equals("") ? null : Integer.parseInt(eElement.getElementsByTagName("QtdeMaxCaracterEspecialPlaca").item(0).getTextContent().trim());
	            
	            if (eElement.getElementsByTagName("TamanhoMinimoPlaca") != null)
	            	TamanhoMinimoPlaca = eElement.getElementsByTagName("TamanhoMinimoPlaca").item(0).getTextContent().trim().equals("") ? null : Integer.parseInt(eElement.getElementsByTagName("TamanhoMinimoPlaca").item(0).getTextContent().trim());
	        }
	    }
	    
	    logger.info("--- Limite de tempo máximo para consulta de veículos na Muralha Digital ==> " + LimiteConsultaAtivo);
	    logger.info("--- Tempo máximo para consulta de veículos (segundos) na Muralha Digital ==> " + LimiteConsultaVeiculosEmSegundos);
	    logger.info("--- Exigir placa completa para consulta de veículos na Muralha Digital ==> " + ExigirPlacaCompleta);
	    logger.info("--- Quantidade máxima de caracteres especiais na placa para consulta de veículos na Muralha Digital ==> " + QtdeMaxCaracterEspecialPlaca);
	    logger.info("--- Tamanho minimo da placa para consulta de veículos na Muralha Digital ==> " + TamanhoMinimoPlaca);
    }
    
    private void ConfigVeiculoMonitorado(Document doc)
    {
    	NodeList nList = doc.getElementsByTagName("VeiculoMonitorado");
    	
	    for (int temp = 0; temp < nList.getLength(); temp++) 
	    {
	        Node nNode = nList.item(temp);

	        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
	        {
	            Element eElement = (Element) nNode;
	            
	            if (eElement.getElementsByTagName("ExportarCadMonitorado") != null)
	            	ExportarCadMonitorado = eElement.getElementsByTagName("ExportarCadMonitorado").item(0).getTextContent().trim().equals("") ? false : "1".equals((eElement.getElementsByTagName("ExportarCadMonitorado").item(0).getTextContent().trim()));
	            
	            if (eElement.getElementsByTagName("QtdeMaxCaracterEspecialPlacaCadMon") != null)
	            	QtdeMaxCaracterEspecialPlacaCadMon = eElement.getElementsByTagName("QtdeMaxCaracterEspecialPlacaCadMon").item(0).getTextContent().trim().equals("") ? null : Integer.parseInt(eElement.getElementsByTagName("QtdeMaxCaracterEspecialPlacaCadMon").item(0).getTextContent().trim());
	        }
	    }
	    
	    logger.info("--- Exportar cadastros de monitoramento na Muralha Digital ==> " + ExportarCadMonitorado);
	    logger.info("--- Quantidade máxima de caracteres especiais na placa para cadastros de monitoramento na Muralha Digital ==> " + QtdeMaxCaracterEspecialPlacaCadMon);
    }

	private void ConfigBlitzDigital(Document doc) {
		NodeList nList = doc.getElementsByTagName("BlitzDigital");
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				
				BlitzDigital = eElement.getElementsByTagName("ativo").item(0).getTextContent();
				
				if (eElement.getElementsByTagName("diretorio_documentos") != null)
					BlitzDigitalDiretorioDocumentos = eElement.getElementsByTagName("diretorio_documentos").item(0).getTextContent().trim();
				
				if (eElement.getElementsByTagName("diretorio_imagens") != null)
					BlitzDigitalDiretorioImagens = eElement.getElementsByTagName("diretorio_imagens").item(0).getTextContent().trim();
				
				if (eElement.getElementsByTagName("api_key_geocoding") != null)
					BlitzDigitalApiKeyGeocoding = eElement.getElementsByTagName("api_key_geocoding").item(0).getTextContent().trim();
			}
		}
	}
}
