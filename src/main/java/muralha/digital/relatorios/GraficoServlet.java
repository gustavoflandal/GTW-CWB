/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 01/10/2021

*********************************************************************************/

package muralha.digital.relatorios;

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

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/Grafico")
public class GraficoServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(GraficoServlet.class);
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
	    	
	    	
	    	if(strAcao.equals("ocorrenciaPorteVeicular"))
	    		OcorrenciaPorteVeicular(request, response);
	    	else if(strAcao.equals("veiculosPorteVeicular"))
	    		VeiculosPorteVeicular(request, response);
	    	else if(strAcao.equals("veiculosPorPeriodo"))
	    		VeiculosPorPeriodo(request,response);
	    	else if(strAcao.equals("velocidadeMediaPorPeriodo"))
	    		VelociadeMediaPorPeriodo(request,response);
	    	else if(strAcao.equals("ocorrenciaFaixaVel"))
	    		OcorrenciaFaixaVelocidade(request, response);
	    	else if(strAcao.equals("comparativoPassagensInfracoes"))
	    		ComparativoPassagensInfracoes(request, response);
	    	else if(strAcao.equals("comparativoAnoAnterior"))
	    		ComparativoAnoAnterior(request, response);
	    	else if(strAcao.equals("comparativoMesAnterior"))
	    		ComparativoMesAnterior(request, response);
	    	else if(strAcao.equals("comparativoEvolucaoClassificacao"))
	    		ComparativoEvolucaoClassificacao(request, response);
	    	else if(strAcao.equals("distribuicaoPorFaixa"))
	    		DistribuicaoPorFaixa(request, response);
	    	else if(strAcao.equals("comparativoPrevisaoFuturo"))
	    		ComparativoPrevisaoFuturo(request, response);
	    	else if(strAcao.equals("rankingPorFaixa"))
	    		RankingPorFaixa(request, response);
	    	else if(strAcao.equals("fluxoVelMediaPorHorarioMapa"))
	    		FluxoVelMediaPorHorarioMapa(request, response);
	    	else if(strAcao.equals("fluxoVelMediaPorMinutoMapa"))
	    		FluxoVelMediaPorMinutoMapa(request, response);
	    	else if(strAcao.equals("infracoesPorDiaMapa"))
	    		InfracoesPorDiaMapa(request, response);
	    	
    	}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados do gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		
	}
	
	/*
	 * Gráficos com telas independentes
	 */
	private void VeiculosPorPeriodo(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			GraficoValidacao graficoValidacao = GraficoValidacao.ValidarFiltros(request, response, true);
			
			if (graficoValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosVeiculosPorPeriodo(graficoValidacao.getDataIni(), graficoValidacao.getDataFim(), graficoValidacao.getIdLocal(), graficoValidacao.getTipoRelatorio());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, graficoValidacao.isFiltroValido(), graficoValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	

	private void VelociadeMediaPorPeriodo(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			GraficoValidacao graficoValidacao = GraficoValidacao.ValidarFiltros(request, response, true);
			
			if (graficoValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosVelociadeMediaPorPeriodo(graficoValidacao.getDataIni(), graficoValidacao.getDataFim(), graficoValidacao.getIdLocal(), graficoValidacao.getTipoRelatorio());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, graficoValidacao.isFiltroValido(), graficoValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void VeiculosPorteVeicular(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			GraficoValidacao graficoValidacao = GraficoValidacao.ValidarFiltros(request, response, true);
			
			if (graficoValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosVeiculosPorteVeicular(graficoValidacao.getDataIni(), graficoValidacao.getDataFim(), graficoValidacao.getIdLocal());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, graficoValidacao.isFiltroValido(), graficoValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void OcorrenciaPorteVeicular(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			GraficoValidacao graficoValidacao = GraficoValidacao.ValidarFiltros(request, response, false);
			
			if (graficoValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosFluxoPorteVeicular(graficoValidacao.getDataIni(), graficoValidacao.getDataFim(), graficoValidacao.getIdLocal());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, graficoValidacao.isFiltroValido(), graficoValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void OcorrenciaFaixaVelocidade(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			GraficoValidacao graficoValidacao = GraficoValidacao.ValidarFiltros(request, response, false);
			
			if (graficoValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosDistribOcorrenciaFaixaVel(graficoValidacao.getDataIni(), graficoValidacao.getDataFim(), graficoValidacao.getIdLocal());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, graficoValidacao.isFiltroValido(), graficoValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	
	/*
	 * Gráficos da tela do dashboard
	 */
	private void ComparativoPassagensInfracoes(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			DashboardValidacao dashboardValidacao = DashboardValidacao.ValidarFiltros(request, response);
			
			if (dashboardValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosComparativoPassagensInfracoes(dashboardValidacao.getDataIni(), dashboardValidacao.getDataFim(), dashboardValidacao.getIdLocal(), dashboardValidacao.getMunicipio(), dashboardValidacao.getRegiao());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, dashboardValidacao.isFiltroValido(), dashboardValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	
	private void ComparativoAnoAnterior(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			DashboardValidacao dashboardValidacao = DashboardValidacao.ValidarFiltros(request, response);
			
			if (dashboardValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosComparativoAnoAnterior(dashboardValidacao.getDataIni(), dashboardValidacao.getDataFim(), dashboardValidacao.getIdLocal(), dashboardValidacao.getTipoRelatorio(), dashboardValidacao.getMunicipio(), dashboardValidacao.getRegiao());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, dashboardValidacao.isFiltroValido(), dashboardValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	
	private void ComparativoEvolucaoClassificacao(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			DashboardValidacao dashboardValidacao = DashboardValidacao.ValidarFiltros(request, response);
			
			if (dashboardValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosComparativoEvolucaoClassificacao(dashboardValidacao.getDataIni(), dashboardValidacao.getDataFim(), dashboardValidacao.getIdLocal(), dashboardValidacao.getTipoRelatorio(), dashboardValidacao.getMunicipio(), dashboardValidacao.getRegiao());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, dashboardValidacao.isFiltroValido(), dashboardValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	

	private void ComparativoPrevisaoFuturo(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			DashboardValidacao dashboardValidacao = DashboardValidacao.ValidarFiltros(request, response);
			
			if (dashboardValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosComparativoPrevisaoFuturo(dashboardValidacao.getDataIni(), dashboardValidacao.getDataFim(), dashboardValidacao.getIdLocal(), dashboardValidacao.getTipoRelatorio(), dashboardValidacao.getMunicipio(), dashboardValidacao.getRegiao());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, dashboardValidacao.isFiltroValido(), dashboardValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	
	private void DistribuicaoPorFaixa(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			DashboardValidacao dashboardValidacao = DashboardValidacao.ValidarFiltros(request, response);
			
			if (dashboardValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosDistribuicaoPorFaixa(dashboardValidacao.getDataIni(), dashboardValidacao.getDataFim(), dashboardValidacao.getIdLocal(), dashboardValidacao.getTipoRelatorio(), dashboardValidacao.getMunicipio(), dashboardValidacao.getRegiao());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, dashboardValidacao.isFiltroValido(), dashboardValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}	
	
	private void RankingPorFaixa(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			DashboardValidacao dashboardValidacao = DashboardValidacao.ValidarFiltros(request, response);
			
			if (dashboardValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosRankingPorFaixa(dashboardValidacao.getDataIni(), dashboardValidacao.getDataFim(), dashboardValidacao.getIdLocal(), dashboardValidacao.getTipoRelatorio(), dashboardValidacao.getMunicipio(), dashboardValidacao.getRegiao());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, dashboardValidacao.isFiltroValido(), dashboardValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void ComparativoMesAnterior(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			DashboardValidacao dashboardValidacao = DashboardValidacao.ValidarFiltros(request, response);
			
			if (dashboardValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.ObterDadosComparativoMesAnterior(dashboardValidacao.getDataIni(), dashboardValidacao.getDataFim(), dashboardValidacao.getIdLocal(), dashboardValidacao.getTipoRelatorio(), dashboardValidacao.getMunicipio(), dashboardValidacao.getRegiao());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, dashboardValidacao.isFiltroValido(), dashboardValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void FluxoVelMediaPorHorarioMapa(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			GraficoValidacao graficoValidacao = GraficoValidacao.ValidarFiltros(request, response, true);
			
			if (graficoValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.FluxoVelMediaPorHorarioMapa(graficoValidacao.getDataIni(), graficoValidacao.getDataFim(), graficoValidacao.getIdLocal());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, graficoValidacao.isFiltroValido(), graficoValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void FluxoVelMediaPorMinutoMapa(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			GraficoValidacao graficoValidacao = GraficoValidacao.ValidarFiltros(request, response, true);
			
			if (graficoValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.FluxoVelMediaPorMinutoMapa(graficoValidacao.getDataIni(), graficoValidacao.getDataFim(), graficoValidacao.getIdLocal());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, graficoValidacao.isFiltroValido(), graficoValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void InfracoesPorDiaMapa(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			GraficoValidacao graficoValidacao = GraficoValidacao.ValidarFiltros(request, response, true);
			
			if (graficoValidacao.isFiltroValido())
			{
				Graficos graficos = Graficos.InfracoesPorDiaMapa(graficoValidacao.getDataIni(), graficoValidacao.getDataFim(), graficoValidacao.getIdLocal());
				EnviarRespostaXML(response, graficos);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, graficoValidacao.isFiltroValido(), graficoValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o gráfico!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	/*
	 * Enviar resposta em XML do gráfico
	 */
	private void EnviarRespostaXML(HttpServletResponse response, Graficos grafico) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(Graficos.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(grafico, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			grafico = null;
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao retornar o os dados para o gráfico!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}	
}
