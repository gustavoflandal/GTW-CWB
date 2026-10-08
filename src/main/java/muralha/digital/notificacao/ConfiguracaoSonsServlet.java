package muralha.digital.notificacao;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.Servlet;
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

@WebServlet("/MuralhaDigital/ConfiguracaoTempo")
public class ConfiguracaoSonsServlet extends HttpServlet implements Servlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ConfiguracaoSonsServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		/*
			Aqui abaixo começa o servlet doGet de configuração de sons e tempos possuindo as ações para:
			obterConfigTempos -> Pega o tempo de execução do alerta sonoro;
			obterSonsAtivo -> Pega se o som está ativo ou não;
			obterAlertaContinuo -> Pega se o alerta sonoro é contínuo ou não.
			E uma mensagem de erro caso não consiga pegar as informações.
		*/
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

			if(strAcao.equals("obterConfigTempos")) {
				ObterConfigTempos(request, response);
			}
			
			if(strAcao.equals("obterSonsAtivo")) {
				ObterSonsAtivo(request, response);
			}			
			

			if(strAcao.equals("obterAlertaContinuo")) {
				ObterAlertasContinuo(request, response);
			}

			if(strAcao.equals("obterTempoMaximoEmissao")) {
				ObterTempoMaximoEmissao(request, response);
			}
			if(strAcao.equals("obterTiposAlertas")) {
				ObterTiposAlertas(request, response);
			}
			if(strAcao.equals("obterPrioridade")) {
				ObterPrioridade(request, response);
			}
		}

		catch(Exception e)
		{
			logger.error("Erro no processo doGet() de requisição de Alerta: " + e.getMessage(), e);
		}
   }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
   {
		Integer idUsuario = null;
		//String nomeUsuario = null;
		//Validando acesso do usuário		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		if (!acessoUsuario.verificaAcesso())
		{
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}
		else
		{
			idUsuario = acessoUsuario.getUsuario().getId();
			//nomeUsuario = acessoUsuario.getUsuario().getNome();
		}
		
		String msg = null;
		String strAcao = request.getParameter("acao");
		
		/*
			Aqui abaixo começa o servlet doPost de configuração de sons e tempos possuindo a ação para:
			configurarAlertaSonoro -> Atualiza o tempo de execução do alerta sonoro, se o som está ativo ou não e se o alerta sonoro é contínuo ou não.
			E uma mensagem de erro para caso o update não seja realizado.
		*/
		try {
			if (strAcao == null || strAcao.isEmpty()) {
				msg = "Ação não informada!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			if (strAcao.equals("configurarAlertaSonoro")) {
				String tempo = request.getParameter("tempo");
				String ativo = request.getParameter("ativo");
				String alarmeContinuo = request.getParameter("alarme_continuo");

				AtualizarConfigAlarmeSonoro(response, tempo, ativo, alarmeContinuo, idUsuario);
			}

			if (strAcao.equals("configurarTempoMaximoEmissao")) {
				String tempo = request.getParameter("tempo");
				AtualizarTempoMaximoEmissao(response, tempo, idUsuario);
			}
			if (strAcao.equals("cadastrarPrioridade")) {
				String idTipo = request.getParameter("idTipo");
				String prioridadeStr = request.getParameter("prioridade");
				int prioridade = Integer.parseInt(prioridadeStr);
				salvar(response, idTipo, prioridade);
			}
		} catch (Exception e) {
			logger.error("Erro no processo doPost() de requisição de alerta: " + e.getMessage(), e);
		}
   }

	/*
		Aqui abaixo começa o método de atualizar a Configuração do Alarme Sonoro, onde pega o objeto ConfiguracaoSom,
		puxa os dados do banco, atualiza os dados conforme o usuário alterou e os envia em uma string para a tabela no banco.
		E possui uma mensagem de erro no final caso o update não seja realizado.
	*/
	protected void AtualizarConfigAlarmeSonoro(HttpServletResponse response, String tempo, String ativo, String alarme_continuo, int idUsuario) throws ServletException, IOException 
	{
		try
		{
			String evento_tempo = "";
			String evento_som_ativo = "";
			String evento_alarme_continuo = "";
			String ativo_atual = "";
			String alarme_continuo_atual = "";
			ConfiguracaoSons configs = new ConfiguracaoSons();

			if (ativo.equals("true")) {
				ativo_atual = "1";
			} else {
				ativo_atual = "0";
			}

			if (alarme_continuo.equals("true")) {
				alarme_continuo_atual = "1";
			} else {
				alarme_continuo_atual = "0";
			}
			
			ConfiguracaoSom configuracaoTempo = ConfiguracaoSons.ObterConfigTempos();
			ConfiguracaoSom configuracaoSomAtivo = ConfiguracaoSons.ObterSonsAtivo();
			ConfiguracaoSom configuracaoAlertaContinuo = ConfiguracaoSons.ObterAlertasContinuo();

			if(evento_tempo != null && !evento_tempo.equals(tempo)) {
				evento_tempo = configuracaoTempo.getChave() + " = " + configuracaoTempo.getValor() + " -> " + tempo;
				configs.Historico = ConfiguracaoSons.Historico(idUsuario, evento_tempo);
			}
			if(evento_som_ativo != null && !evento_som_ativo.equals(ativo)) {
				evento_som_ativo =  configuracaoSomAtivo.getChave() + " = " + configuracaoSomAtivo.getValor() + " -> " + ativo_atual;
				configs.Historico = ConfiguracaoSons.Historico(idUsuario, evento_som_ativo);
			}
			if(evento_alarme_continuo != null && !evento_alarme_continuo.equals(alarme_continuo)) {
				evento_alarme_continuo = configuracaoAlertaContinuo.getChave() + " = " + configuracaoAlertaContinuo.getValor() + " -> " + alarme_continuo_atual;
				configs.Historico = ConfiguracaoSons.Historico(idUsuario, evento_alarme_continuo);
			}
			ConfiguracaoSom config = ConfiguracaoSons.AtualizarConfigAlarmeSonoro(tempo, ativo, alarme_continuo, idUsuario);
			EnviarRespostaXML(response, config);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter AtualizarConfigAlarmeSonoro(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro em AtualizarConfigAlarmeSonoro()!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}

	/*
		Nessa função abaixo ela pega os valores do ConfiguracaoSom de alerta contínuo (fazendo um get) e os envia em XML para o banco.
		E abaixo possui uma mensagem de erro caso não consiga pegar as informações.
	*/
   protected void ObterAlertasContinuo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			ConfiguracaoSons alarmes_continuos = new ConfiguracaoSons();
			ConfiguracaoSom configuracaoAlertaContinuo = ConfiguracaoSons.ObterAlertasContinuo();
			alarmes_continuos.setAlertasContinuo(configuracaoAlertaContinuo);
			EnviarRespostaXML(response, alarmes_continuos);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter ObterAlertasContinuo(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao ObterAlertasContinuo()!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}
	
	/*
		Nessa função abaixo ela pega os valores do ConfiguracaoSom de tempo de execução (fazendo um get) e os envia em XML para o banco.
		E abaixo possui uma mensagem de erro caso não consiga pegar as informações.
	*/
	protected void ObterConfigTempos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{	    	
			ConfiguracaoSons alarmes_tempo_execucoes = new ConfiguracaoSons();
			ConfiguracaoSom configuracaoTempo = ConfiguracaoSons.ObterConfigTempos();
			alarmes_tempo_execucoes.setConfiguracaoTempos(configuracaoTempo);
			EnviarRespostaXML(response, alarmes_tempo_execucoes);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter ObterConfigTempos(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao ObterConfigTempos()!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}		
	}
	
	/*
		Nessa função abaixo ela pega os valores do ConfiguracaoSom de sons ativos (fazendo um get) e os envia em XML para o banco.
		E abaixo possui uma mensagem de erro caso não consiga pegar as informações.
	*/
	protected void ObterSonsAtivo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			ConfiguracaoSons alarmes_sons_ativo = new ConfiguracaoSons();
			ConfiguracaoSom somAtivo = ConfiguracaoSons.ObterSonsAtivo();
			alarmes_sons_ativo.setSonsAtivo(somAtivo);
			EnviarRespostaXML(response, alarmes_sons_ativo);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter ObterSonsAtivo(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao ObterSonsAtivo()!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}	
	
	/*
		Essa função abaixo envia o objeto (citado nas funções acima) em XML para o banco.
		E possui uma mensagem de erro caso não consiga serializar o objeto para XML.
	*/
	private void EnviarRespostaXML(HttpServletResponse response, Object objeto) throws IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(objeto.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marshaller.marshal(objeto, sw);
			String xml = sw.toString();
			sw.close();

			response.setContentType("text/xml; charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
		} catch (JAXBException e) {
			logger.error("Erro ao serializar objeto para XML: " + e.getMessage(), e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Ocorreu um erro ao retornar o resultado do atendimento!");
		}
   }

   protected void ObterTempoMaximoEmissao(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			ConfiguracaoSons tempoMaximo = new ConfiguracaoSons();
			ConfiguracaoSom configuracaoTempoMaximo = ConfiguracaoSons.ObterTempoMaximoEmissao();
			tempoMaximo.setTempoMaximoEmissao(configuracaoTempoMaximo);
			EnviarRespostaXML(response, tempoMaximo);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter ObterTempoMaximoEmissao(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao ObterTempoMaximoEmissao()!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}   
  
   protected void ObterTiposAlertas(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    try {


	        ConfiguracaoSons resposta = new ConfiguracaoSons();
	        resposta.setTiposAlertas(ConfiguracaoSons.obterTiposAlertas());

	        EnviarRespostaXML(response, resposta);


	    } catch (Exception e) {
	        logger.error("Erro ao obter ObterTiposAlertas(): " + e.getMessage(), e);

	        String msg = "Ocorreu um erro ao obter os tipos de alerta!";
	        respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    }
	}
   
   protected void ObterPrioridade(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    try {
	    	String id = request.getParameter("id");	    	
	    	
			ConfiguracaoSom prioridadeCadastrada = ConfiguracaoSons.ObterPrioridade(id);			
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().write(
			    String.valueOf(prioridadeCadastrada.getPrioridade())
			);




	    } catch (Exception e) {
	        logger.error("Erro ao obter ObterTiposAlertas(): " + e.getMessage(), e);

	        String msg = "Ocorreu um erro ao obter os tipos de alerta!";
	        respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    }
	}

	protected void AtualizarTempoMaximoEmissao(HttpServletResponse response, String tempo, int idUsuario) throws ServletException, IOException 
	{
		try
		{
			String eventoTempo = "";
			ConfiguracaoSons configs = new ConfiguracaoSons();
			
			ConfiguracaoSom configuracaoAtual = ConfiguracaoSons.ObterTempoMaximoEmissao();
			
			if(configuracaoAtual != null && !configuracaoAtual.getValor().equals(tempo)) {
				eventoTempo = configuracaoAtual.getChave() + " = " + configuracaoAtual.getValor() + " -> " + tempo;
				configs.Historico = ConfiguracaoSons.Historico(idUsuario, eventoTempo);
			}
			
			ConfiguracaoSom config = ConfiguracaoSons.AtualizarTempoMaximoEmissao(tempo, idUsuario);
			EnviarRespostaXML(response, config);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter AtualizarTempoMaximoEmissao(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro em AtualizarTempoMaximoEmissao()!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}
	
	protected void salvar(HttpServletResponse response, String idTipo, Integer prioridade) throws ServletException, IOException 
	{
		try
		{
			String eventoTempo = "";
			ConfiguracaoSons configs = new ConfiguracaoSons();
			
			ConfiguracaoSom configuracaoAtual = ConfiguracaoSons.ObterTempoMaximoEmissao();
			
			
			
			ConfiguracaoSom config = ConfiguracaoSons.SalvarPrioridade(idTipo, prioridade);
			EnviarRespostaXML(response, config);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter AtualizarTempoMaximoEmissao(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro em AtualizarTempoMaximoEmissao()!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}
}