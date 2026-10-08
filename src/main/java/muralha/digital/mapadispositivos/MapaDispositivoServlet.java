/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Fernando Amaral
  Data: 22/09/2021

*********************************************************************************/
package muralha.digital.mapadispositivos;

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

import muralha.digital.dispositivo.DispositivoEquipamento;
import muralha.digital.dispositivo.DispositivosEquipamentos;
import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/MapaDispositivosEquipamentos")
public class MapaDispositivoServlet 	extends javax.servlet.http.HttpServlet 
								implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MapaDispositivoServlet.class); 
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	boolean temp;
	
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
	    	
	    	if(strAcao.equals("obterDispositivosEquipamentos"))
	    		ObterDispositivosEquipamentos(request, response, "completo");
	    	if(strAcao.equals("obterDispositivosEquipamentosSimplificado"))
	    		ObterDispositivosEquipamentos(request, response, "simplificado");
	    	if(strAcao.equals("obterDispositivosEquipamentosMisto"))
	    		ObterDispositivosEquipamentos(request, response, "misto");
			
		}
		catch(Exception err) {
			logger.error("Erro ao obter dados de dispositivos/equipamentos.", err);
			return;
		}
		
	}  	
	
	private void ObterDispositivosEquipamentos(HttpServletRequest request, HttpServletResponse response, String tipoMapa)
	{
		try
		{
			String strEquipamentos = (request.getParameter("equipamentos") != null && (!request.getParameter("equipamentos").equals("")) ? request.getParameter("equipamentos") : null);
			List<Integer> equipamentos = new ArrayList<>();
			String msg = "";
			
			try
	    	{
				String[] gEquipamentos = (strEquipamentos != null ? strEquipamentos.split(",") : new String[0]);
				
				if (gEquipamentos.length > 0)
				{
					for (String equipamento : gEquipamentos) {
						if (equipamento != null && !equipamento.trim().equals(""))
						{
							int idLocal = Integer.parseInt(equipamento);
							equipamentos.add(idLocal);
						}
					}
				}
			}
	    	catch (Exception e)
	    	{
				msg = "Erro ao preparar dados!";
				logger.error(msg + ": " + e.getMessage(), e);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
			
			//Cria objeto de retorno
			DispositivosEquipamentos dispositivosEquips = new DispositivosEquipamentos();
			dispositivosEquips.setListaDispositivos(new ArrayList<DispositivoEquipamento>());
				
			//Faz a consulta já existente no banco de dados
			List<DispositivoEquipamento> listaDisp = new ArrayList<>();
			switch (tipoMapa)
			{
				case "completo":
				{
					listaDisp = DispositivosEquipamentos.ObterListaDispositivosContagens(equipamentos);
					break;
				}
				case "simplificado":
				{
					listaDisp = DispositivosEquipamentos.ObterListaDispositivosContagensSimplificado(equipamentos);
					break;
				}
				case "misto":
				{
					listaDisp = DispositivosEquipamentos.ObterListaDispositivosContagensMisto(equipamentos);
					break;
				}
				default:
				{
					logger.error("Opção de carregamento do mapa inválida! Tipo mapa: " + tipoMapa);
					break;
				}
			}
			
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
}
