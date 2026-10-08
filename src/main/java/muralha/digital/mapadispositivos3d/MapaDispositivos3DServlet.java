package muralha.digital.mapadispositivos3d;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import muralha.digital.dispositivo.DispositivoEquipamento;
import muralha.digital.dispositivo.DispositivosEquipamentos;
import muralha.digital.mapadispositivos.MapaDispositivoServlet;
import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/MapaDispositivos3D")
public class MapaDispositivos3DServlet extends javax.servlet.http.HttpServlet  implements javax.servlet.Servlet
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MapaDispositivoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	
	@Override
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

	    	if(strAcao.equals("obterMapaDispositivos3D"))
	    		ObterDispositivosEquipamentos3D(request, response);
			
		}
		catch(Exception err) {
			logger.error("Erro ao obter dados de dispositivos/equipamentos para mapa 3D.", err);
			return;
		}
	} 

	private void ObterDispositivosEquipamentos3D(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			Mapa3DValidacao mapa3DValidacao = Mapa3DValidacao.ValidarFiltros(request, response);
			
			if (mapa3DValidacao.isFiltroValido()) 
			{
				//Cria objeto de retorno
				DispositivosEquipamentos dispositivosEquips = new DispositivosEquipamentos();
				dispositivosEquips.setListaDispositivos(new ArrayList<DispositivoEquipamento>());
				
				//Faz a consulta já existente no banco de dados
				List<DispositivoEquipamento> listaDisp = DispositivosEquipamentos.ObterDispositivos3DQuantitativo(mapa3DValidacao.getDataIni(), mapa3DValidacao.getDataFim(), mapa3DValidacao.getTipoRelatorio(), mapa3DValidacao.getMunicipio(), mapa3DValidacao.getRegiao());
				dispositivosEquips.setListaDispositivos(listaDisp);
				
				EnviaRespostaDispositivosXML(response, dispositivosEquips);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, mapa3DValidacao.isFiltroValido(), mapa3DValidacao.getMensagem());
			}
			
		}
		catch(Exception err) {
			logger.error("Erro ao ObterDispositivosEquipamentos3D().", err);
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
	
	private static class Mapa3DValidacao
	{
		
		private boolean filtroValido;
		private String mensagem;
		private Date dataIni;
		private Date dataFim;
		private Integer municipio;
		private Integer regiao;
		private Integer tipoRelatorio;
		
		public boolean isFiltroValido() {
			return filtroValido;
		}
		public void setFiltroValido(boolean filtroValido) {
			this.filtroValido = filtroValido;
		}
		public String getMensagem() {
			return mensagem;
		}
		public void setMensagem(String mensagem) {
			this.mensagem = mensagem;
		}
		public Date getDataIni() {
			return dataIni;
		}
		public void setDataIni(Date dataIni) {
			this.dataIni = dataIni;
		}
		public Date getDataFim() {
			return dataFim;
		}
		public void setDataFim(Date dataFim) {
			this.dataFim = dataFim;
		}
		public Integer getTipoRelatorio() {
			return tipoRelatorio;
		}
		public void setTipoRelatorio(Integer tipoRelatorio) {
			this.tipoRelatorio = tipoRelatorio;
		}
		public Integer getMunicipio() {
			return municipio;
		}
		public void setMunicipio(Integer municipio) {
			this.municipio = municipio;
		}
		public Integer getRegiao() {
			return regiao;
		}
		public void setRegiao(Integer regiao) {
			this.regiao = regiao;
		}
		
		public static Mapa3DValidacao ValidarFiltros(HttpServletRequest request, HttpServletResponse response) throws ParseException, Exception 
		{
			Mapa3DValidacao mapa3DValidacao = new Mapa3DValidacao();

			try
			{
				String strDataIni = request.getParameter("dataIni");
				String strDataFim = request.getParameter("dataFim");
				String strtipoRelatorio = request.getParameter("tipoRelatorio");
				String strMunicipio = request.getParameter("municipio");
				String strRegiao = request.getParameter("regiao");
				
		    	if ( (strDataIni == null || strDataIni.trim().equals("")) || (strDataFim == null || strDataFim.trim().equals("")) )
		    	{
		    		String msg = "Favor informar datas de início e fim!";
					logger.error(msg);	
					mapa3DValidacao.setFiltroValido(false);
					mapa3DValidacao.setMensagem(msg);
		    		return mapa3DValidacao;
		    	}
		    	
		    	if (strtipoRelatorio == null || strtipoRelatorio.trim().equals("") || strtipoRelatorio.trim().equals("0"))
		    	{
		    		String msg = "Favor informar o tipo do Relatório!";
					logger.error(msg);
					mapa3DValidacao.setFiltroValido(false);
					mapa3DValidacao.setMensagem(msg);
		    		return mapa3DValidacao;
		    	}

		    	if (strMunicipio == null || strMunicipio.trim().equals("") || strMunicipio.trim().equals("0"))
		    	{
		    		String msg = "Favor informar o Município!";
					logger.error(msg);
					mapa3DValidacao.setFiltroValido(false);
					mapa3DValidacao.setMensagem(msg);
		    		return mapa3DValidacao;
		    	}

		    	if (strRegiao == null || strRegiao.trim().equals(""))
		    	{
		    		String msg = "Favor informar Região!";
					logger.error(msg);
					mapa3DValidacao.setFiltroValido(false);
					mapa3DValidacao.setMensagem(msg);
		    		return mapa3DValidacao;
		    	}
		    	
		    	Integer tipoRelatorio = null;
		    	Integer municipio = null;
		    	Integer regiao = null;
				Date dataIni = null, dataFim = null;
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				
		    	try
		    	{
		    		
		    		if (strtipoRelatorio != null && !strtipoRelatorio.trim().equals("") && !strtipoRelatorio.trim().equals("0"))
		    			tipoRelatorio = Integer.parseInt(strtipoRelatorio);
		    		
		    		if (strMunicipio != null && !strMunicipio.trim().equals("") && !strMunicipio.trim().equals("0"))
		    			municipio = Integer.parseInt(strMunicipio);
		    		
		    		if (strRegiao != null && !strRegiao.trim().equals("") && !strRegiao.trim().equals("0"))
		    			regiao = Integer.parseInt(strRegiao);
		    		
		    		if (strDataIni != null && !strDataIni.trim().equals("")) {
			    		dataIni = sdf.parse(strDataIni);
			    	}
			    	
		    		if (strDataFim != null && !strDataFim.trim().equals("")) {
		    			dataFim = sdf.parse(strDataFim);
			    	}
				}
		    	catch (ParseException e)
		    	{
					String msg = "Erro ao preparar dados para consulta!";
					logger.error(msg, e);
					mapa3DValidacao.setFiltroValido(false);
					mapa3DValidacao.setMensagem(msg);
		    		return mapa3DValidacao;
				}
		    	
		    	if ( (dataIni != null && dataFim != null) && dataFim.before(dataIni) )
		    	{
		    		String msg = "A data de início deve ser menor ou igual que a data fim!";
					logger.error(msg);
					mapa3DValidacao.setFiltroValido(false);
					mapa3DValidacao.setMensagem(msg);
		    		return mapa3DValidacao;
		    	}
		    	
		    	mapa3DValidacao.setFiltroValido(true);
		    	mapa3DValidacao.setDataIni(dataIni);
		    	mapa3DValidacao.setDataFim(dataFim);
		    	mapa3DValidacao.setMunicipio(municipio);
		    	mapa3DValidacao.setRegiao(regiao);
		    	mapa3DValidacao.setTipoRelatorio(tipoRelatorio);
		    	
		    	return mapa3DValidacao;
			}
			catch(Exception e)
			{
				String msg = "Ocorreu um erro ao validar os filtros do dashboard!";
				logger.error(msg, e);
				mapa3DValidacao.setFiltroValido(false);
				mapa3DValidacao.setMensagem(msg);
	    		return mapa3DValidacao;
			}	
		}
	}

}
