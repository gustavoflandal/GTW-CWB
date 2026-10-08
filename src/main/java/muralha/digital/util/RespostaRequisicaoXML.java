package muralha.digital.util;

import java.io.StringWriter;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import muralha.digital.veiculo.Veiculos;
import muralha.digital.veiculo.VeiculosBlitz;

@XmlRootElement		(namespace = "respostaRequisicaoXML")
@XmlAccessorType	(XmlAccessType.FIELD)
public class RespostaRequisicaoXML 
{

	private static Logger logger = Logger.getLogger(RespostaRequisicaoXML.class); 
	
	private boolean sucesso;
	private String msgResposta;
	private UUID id;
	private boolean possuiAlertaPendente;
	private boolean cadastroAtivo;
	private Integer valorInt1;
	private Integer valorInt2;
	
	
	public boolean isSucesso() {
		return sucesso;
	}
	public void setSucesso(boolean sucesso) {
		this.sucesso = sucesso;
	}
	
	public String getMsgResposta() {
		return msgResposta;
	}
	public void setMsgResposta(String msgResposta) {
		this.msgResposta = msgResposta;
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	
	public boolean isPossuiAlertaPendente() {
		return possuiAlertaPendente;
	}
	public void setPossuiAlertaPendente(boolean possuiAlertaPendente) {
		this.possuiAlertaPendente = possuiAlertaPendente;
	}
	
	public boolean isCadastroAtivo() {
		return cadastroAtivo;
	}
	public void setCadastroAtivo(boolean cadastroAtivo) {
		this.cadastroAtivo = cadastroAtivo;
	}
	
	public Integer getValorInt1() {
		return valorInt1;
	}
	public void setValorInt1(Integer valorInt1) {
		this.valorInt1 = valorInt1;
	}

	public Integer getValorInt2() {
		return valorInt2;
	}
	public void setValorInt2(Integer valorInt2) {
		this.valorInt2 = valorInt2;
	}
	
	
	public void EnviarRespostaRequisicaoXML(HttpServletResponse response, boolean sucesso, String msg) 
	{

		RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
		resposta.setSucesso(sucesso);
		resposta.setMsgResposta(msg);
		
    	//Formando dados para envio
		JAXBContext evidencia_context;
		try
		{
			evidencia_context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
			Marshaller marsHall = evidencia_context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(resposta, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
		
		}
		catch (Exception e)
		{
			String msgErro = "Erro gravíssimo ao preparar resposta a requisição! " + e.getMessage();
			logger.error(msgErro, e);
			return;
		}
	}
	
	public void EnviarRespostaRequisicaoXML(HttpServletResponse response, boolean sucesso, String msg, UUID id) 
	{

		RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
		resposta.setSucesso(sucesso);
		resposta.setMsgResposta(msg);
		resposta.setId(id);
		
    	//Formando dados para envio
		JAXBContext evidencia_context;
		try
		{
			evidencia_context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
			Marshaller marsHall = evidencia_context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(resposta, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
		
		}
		catch (Exception e)
		{
			String msgErro = "Erro gravíssimo ao preparar resposta a requisição! " + e.getMessage();
			logger.error(msgErro, e);
			return;
		}
	}
	
	public void EnviarRespostaRequisicaoXML(HttpServletResponse response, boolean sucesso, String msg, Integer valorInt1, Integer valorInt2) 
	{

		RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
		resposta.setSucesso(sucesso);
		resposta.setMsgResposta(msg);
		resposta.setValorInt1(valorInt1);
		resposta.setValorInt2(valorInt2);
		
    	//Formando dados para envio
		JAXBContext evidencia_context;
		try
		{
			evidencia_context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
			Marshaller marsHall = evidencia_context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(resposta, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
		
		}
		catch (Exception e)
		{
			String msgErro = "Erro gravíssimo ao preparar resposta a requisição! " + e.getMessage();
			logger.error(msgErro, e);
			return;
		}
	}
	
	public void EnviarRespostaAlertaPendenteXML(HttpServletResponse response, boolean sucesso, String msg, UUID id, boolean possuiAlertaPendente) 
	{

		RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
		resposta.setSucesso(sucesso);
		resposta.setMsgResposta(msg);
		resposta.setId(id);
		resposta.setPossuiAlertaPendente(possuiAlertaPendente);
		
    	//Formando dados para envio
		JAXBContext evidencia_context;
		try
		{
			evidencia_context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
			Marshaller marsHall = evidencia_context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(resposta, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
		
		}
		catch (Exception e)
		{
			String msgErro = "Erro gravíssimo ao preparar resposta a requisição! " + e.getMessage();
			logger.error(msgErro, e);
			return;
		}
	}
	
	public void EnviarRespostaAlertaVinculadoXML(HttpServletResponse response, boolean sucesso, String msg, boolean cadastroAtivo) 
	{

		RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
		resposta.setSucesso(sucesso);
		resposta.setMsgResposta(msg);
		resposta.setId(id);
		resposta.setCadastroAtivo(cadastroAtivo);
		
    	//Formando dados para envio
		JAXBContext evidencia_context;
		try
		{
			evidencia_context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
			Marshaller marsHall = evidencia_context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(resposta, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
		
		}
		catch (Exception e)
		{
			String msgErro = "Erro gravíssimo ao preparar resposta a requisição! " + e.getMessage();
			logger.error(msgErro, e);
			return;
		}
	}
	
	public static String MontarVeicXML(Veiculos veics)
	{
		String ret = "";
		try
		{
			JAXBContext veics_context = JAXBContext.newInstance(Veiculos.class);
			Marshaller marsHall = veics_context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(veics, sw);
			String xml = sw.toString();
			sw.close();
			
			//logger.info(xml);
			
			ret = xml;
		
		}
		catch (Exception e) 
		{
			logger.error("Falha envio novos Veiculos e envio aos clientes websocket(browser)." + e.getMessage(), e);
		}		
		
		return ret;
	}
	
	public static String MontarVeicBlitzXML(VeiculosBlitz veics)
	{
		String ret = "";
		try
		{
			JAXBContext veics_context = JAXBContext.newInstance(VeiculosBlitz.class);
			Marshaller marsHall = veics_context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(veics, sw);
			String xml = sw.toString();
			sw.close();
			
			//logger.info(xml);
			
			ret = xml;
		
		}
		catch (Exception e) 
		{
			logger.error("Falha envio novos veiculos para blitz eletronica e envio aos clientes websocket(browser)." + e.getMessage(), e);
		}		
		
		return ret;
	}
}
