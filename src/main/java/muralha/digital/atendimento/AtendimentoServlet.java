package muralha.digital.atendimento;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.guarnicao.Guarnicao;
import muralha.digital.guarnicao.Guarnicoes;
import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/Atendimento")
public class AtendimentoServlet extends HttpServlet implements Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AtendimentoServlet.class);
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
	    	
	    	if(strAcao.equals("obterOcorrencias"))	    	
	    		ObterOcorrencias(request, response);
	    	
	    	if(strAcao.equals("obterGuarnicoes"))	    	
	    		ObterGuarnicoes(request, response);
	    	
	    	if(strAcao.equals("obterGuarnicoesStatus"))	    	
	    		obterGuarnicoesStatus(request, response);
	    	
	    	if(strAcao.equals("obterTiposRegistroFato"))	    	
	    		obterTiposRegistroFato(request, response);
	    	
	    	if(strAcao.equals("obterVeiculosSinistradosRecuperados"))	    	
	    		ObterVeiculosSinistradosRecuperados(request, response);
	    	
	    	if(strAcao.equals("obterHistorico")) {	
	    		int idAtendimento = Integer.parseInt(request.getParameter("idAtendimento"));
	    		ObterHistorico(request, idAtendimento, response);
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
		
		try {
		    if (strAcao == null || strAcao.isEmpty()) {
		        msg = "Ação não informada!";
		        logger.error(msg);
		        respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		        return;
		    }

		    if (strAcao.equals("iniciarAtendimento")) {
		        String idOcorrencia = request.getParameter("idOcorrencia");
		        String origemRegistro = request.getParameter("origemRegistro");
		        String idRegistroFato = request.getParameter("idRegistroFato");

		        if (idOcorrencia == null || origemRegistro == null) {
		            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Parâmetros inválidos.");
		            return;
		        }
		        IniciarAtendimento(response, idOcorrencia, origemRegistro, idRegistroFato, idUsuario);
		    }

		    if (strAcao.equals("enviarGuarnicoes")) {
		        String idAtendimentoStr = request.getParameter("idAtendimento");
		        String idguarnicaoStr = request.getParameter("idGuarnicao");
		        String observacao = request.getParameter("obsGuarnicao");
		        String nomeGuarnicao = request.getParameter("nomeGuarnicao");

		        if (idAtendimentoStr == null || idguarnicaoStr == null) {
		            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Parâmetros inválidos.");
		            return;
		        }
		        int idAtendimento = Integer.parseInt(idAtendimentoStr.trim());
		        int idguarnicao = Integer.parseInt(idguarnicaoStr.trim());
		        EnviarGuarnicao(response, idAtendimento, idguarnicao, observacao, nomeGuarnicao, idUsuario);
		    }
		    if(strAcao.equals("encerrarAtendimento")) {
		    	String idAtendimentoStr = request.getParameter("idAtendimento");
		        String idguarnicaoStr = request.getParameter("idGuarnicao");		       

		        if (idAtendimentoStr == null || idguarnicaoStr == null) {
		            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Parâmetros inválidos.");
		            return;
		        }
		        int idAtendimento = Integer.parseInt(idAtendimentoStr.trim());
		        int idguarnicao = Integer.parseInt(idguarnicaoStr.trim());		        
		        EncerrarAtendimento(response, idAtendimento, idguarnicao, idUsuario);
		    	
		    }
		    
		    if(strAcao.equals("anexarDocumentos")) {
		    	String idAtendimentoStr = request.getParameter("idAtendimento");
		        String detalhamentoStr = request.getParameter("obsString");		       

		        if (idAtendimentoStr == null || detalhamentoStr == null) {
		            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Parâmetros inválidos.");
		            return;
		        }
		        int idAtendimento = Integer.parseInt(idAtendimentoStr.trim());		        
		        AnexarDocumento(response, idAtendimento, detalhamentoStr, idUsuario);		    	
		    }
		    
		    if(strAcao.equals("vincularBoletimAtendimento")) {
		    	int idAtendimento = Integer.parseInt(request.getParameter("idAtendimento"));
		        int idBoletim = Integer.parseInt(request.getParameter("idBoletim"));		       

		        if (idAtendimento <= 0 || idBoletim <= 0) {
		            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Parâmetros inválidos.");
		            return;
		        }
		       		        
		        VincularAtendimentoBo(response, idAtendimento, idBoletim, idUsuario);		    	
		    }

		} catch (Exception e) {
		    logger.error("Erro no processo doPost() de requisição de alerta: " + e.getMessage(), e);
		}
    }     
    
    protected void ObterOcorrencias(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{	    	
			Atendimentos atendimentos = new Atendimentos();
			atendimentos.setAlertas(new ArrayList<Atendimento>());			
			List<Atendimento> listaAtendimento = Atendimentos.ObterListaOcorrencias();
			atendimentos.setAlertas(listaAtendimento);		
			EnviarRespostaXML(response, atendimentos);			
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter ObterAlertasNaoTratados(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao ObterAlertasNaoTratados()!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}		
	}
    
    protected void ObterVeiculosSinistradosRecuperados(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
        	String tipoParam = request.getParameter("tipo");
            List<Integer> tipos = new ArrayList<>();

            if (tipoParam != null && !tipoParam.isEmpty()) {
                String[] partes = tipoParam.split(",");
                for (String parte : partes) {
                    try {
                        tipos.add(Integer.parseInt(parte.trim()));
                    } catch (NumberFormatException e) {
                        logger.warn("Tipo inválido ignorado: " + parte);
                    }
                }
            }
            
            List<RegistroFato> listaRegistroFato = RegistroFato.listarRegistroFato(tipos);

            response.setContentType("application/xml;charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            out.println("<registrosFato>");
            for (RegistroFato rf : listaRegistroFato) {
                out.println("  <registroFato>");
                out.println("    <id>" + rf.getId() + "</id>");
                out.println("    <placa>" + safe(rf.getPlaca()) + "</placa>");
                out.println("    <descricaoEvento>" + safe(rf.getDescricaoEvento()) + "</descricaoEvento>");
                out.println("    <idEvento>" + rf.getIdEvento() + "</idEvento>");
                out.println("    <latitude>" + rf.getLatitude() + "</latitude>");
                out.println("    <longitude>" + rf.getLongitude() + "</longitude>");
                out.println("    <rua>" + safe(rf.getRua()) + "</rua>");
                out.println("    <numero>" + safe(rf.getNumero()) + "</numero>");
                out.println("    <bairro>" + safe(rf.getBairro()) + "</bairro>");
                out.println("    <idTipoEvento>" + rf.getIdTipoEvento() + "</idTipoEvento>");
                out.println("    <idRegistroFato>" + rf.getIdRegistroFato() + "</idRegistroFato>");
                out.println("    <idEndereco>" + rf.getIdEndereco() + "</idEndereco>");
                out.println("    <idStatus>" + rf.getIdStatus() + "</idStatus>");
                out.println("    <idTipo>" + rf.getIdTipo() + "</idTipo>");
                out.println("    <temBoletim>" + rf.getTemBoletim() + "</temBoletim>");
                out.println("    <idUsuario>" + rf.getIdUsuario() + "</idUsuario>");
                out.println("    <dataCriacao>" + formatDate(rf.getDataCriacao()) + "</dataCriacao>");
                out.println("    <dataEncerramento>" + formatDate(rf.getDataEncerramento()) + "</dataEncerramento>");
                out.println("    <privado>" + rf.getPrivado() + "</privado>");
                out.println("  </registroFato>");
            }
            out.println("</registrosFato>");
            out.flush();
        } catch (Exception e) {
            logger.error("Erro ao obter ObterVeiculosSinistradosRecuperados(): " + e.getMessage(), e);
            String msg = "Ocorreu um erro ao ObterVeiculosSinistradosRecuperados()!";
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
        }
    }

    private String safe(Object object) {
        return object != null ? (String) object : "";
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toString() : "";
    }

    protected void obterTiposRegistroFato(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Map<String, Object>> tipos = RegistroFato.listarRegistroFatoTipo();

            response.setContentType("application/xml;charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            out.println("<tiposRegistroFato>");
            for (Map<String, Object> tipo : tipos) {
                out.println("  <tipo>");
                out.println("    <id>" + tipo.get("id") + "</id>");
                out.println("    <descricao>" + safe(tipo.get("tipo_desc")) + "</descricao>");
                out.println("  </tipo>");
            }
            out.println("</tiposRegistroFato>");
            out.flush();

        } catch (Exception e) {
            logger.error("Erro ao obter tipos de registro de fato: " + e.getMessage(), e);
            String msg = "Ocorreu um erro ao obter os tipos de registro de fato!";
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
        }
    }

    
    protected void ObterGuarnicoes(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{	 
			Guarnicoes  guarnicoes = new Guarnicoes();
			guarnicoes.setGuarnicoes(new ArrayList<Guarnicao>());			
			List<Guarnicao> listaGuarnicao = Atendimentos.ObterGuarnicoes();
			guarnicoes.setGuarnicoes(listaGuarnicao);			
			EnviarRespostaXML(response, guarnicoes);			
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter ObterAlertasNaoTratados(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao ObterAlertasNaoTratados()!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}		
	} 
    
    protected void obterGuarnicoesStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{	 
			Guarnicoes  guarnicoes = new Guarnicoes();
			guarnicoes.setGuarnicoes(new ArrayList<Guarnicao>());			
			List<Guarnicao> listaGuarnicao = Atendimentos.ObterGuarnicoesStatus();
			guarnicoes.setGuarnicoes(listaGuarnicao);			
			EnviarRespostaXML(response, guarnicoes);			
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter ObterAlertasNaoTratados(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao ObterAlertasNaoTratados()!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}		
	} 
    
    protected void ObterHistorico(HttpServletRequest request, int idAtendimento, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{	 
			Historicos historicos = new Historicos();
			historicos.setHitoricos(new ArrayList<Historico>());		
			List<Historico> listaHistorico = Historicos.ObterHistorico(idAtendimento);
			historicos.setHitoricos(listaHistorico);			
			EnviarRespostaXML(response, historicos);			
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter ObterAlertasNaoTratados(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao ObterAlertasNaoTratados()!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}		
	} 
    
    
    protected void IniciarAtendimento(HttpServletResponse response, String idOcorrencia, String origemRegistro, String idRegistroFato, int idUsuario) throws ServletException, IOException, ConexaoException {
        try {
            Atendimentos atendimento = new Atendimentos();
            Atendimento atend = atendimento.IniciarAtendimento(idOcorrencia, origemRegistro, idRegistroFato, idUsuario);
            int idAtendimento = atend.getIdAtendimento();
				atendimento.Historico( idAtendimento, 1, "Atendimento iniciado pela central de atendimento", idUsuario);
            EnviarRespostaXML(response, atend);
        } catch (SQLException e) {
            logger.error("Erro ao iniciar atendimento para idOcorrencia: " + idOcorrencia + ", origemRegistro: " + origemRegistro, e);
            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Ocorreu um erro ao iniciar o atendimento: " + e.getMessage());
        }
    }
    
    protected void EnviarGuarnicao(HttpServletResponse response, int idAtendimento, int idguarnicao, String observacao, String nomeGuarnicao, int idUsuario) throws ServletException, IOException, ConexaoException {
        try {
            Atendimentos atendimento = new Atendimentos();
            atendimento.EnviarGuarnicao(response, idAtendimento, idguarnicao, observacao); 
            atendimento.Historico( idAtendimento, 3, "Envio de guarnição pela central de atendimento: "+ nomeGuarnicao, idUsuario);
        } catch (SQLException e) {
            logger.error("Erro ao iniciar enviar guarnicao: ", e);
            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro ao iniciar enviar guarnicao: " + e.getMessage());
        }
    }
    
    
    protected void EncerrarAtendimento(HttpServletResponse response, int idAtendimento, int idguarnicao, int idUsuario) throws ServletException, IOException, ConexaoException {
        try {
            Atendimentos atendimento = new Atendimentos();
            atendimento.EncerrarAtendimento(response, idAtendimento, idguarnicao); 
            atendimento.Historico( idAtendimento, 2, "Atendimento finalizado pela central de atendimento", idUsuario);
        } catch (SQLException e) {
            logger.error("Erro ao iniciar enviar guarnicao: ", e);
            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro ao iniciar enviar guarnicao: " + e.getMessage());
        }
    }
    
    protected void AnexarDocumento(HttpServletResponse response, int idAtendimento, String detalhamentoStr, int idUsuario) throws ServletException, IOException, ConexaoException {
        try {
            Atendimentos atendimento = new Atendimentos();
            atendimento.AnexarDocumento(response, idAtendimento,  detalhamentoStr);            
        } catch (SQLException e) {
            logger.error("Erro ao iniciar enviar guarnicao: ", e);
            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro ao iniciar enviar guarnicao: " + e.getMessage());
        }
    }
    
    protected void  VincularAtendimentoBo(HttpServletResponse response, int idAtendimento, int idBoletim, int idUsuario) throws ServletException, IOException, ConexaoException {
        try {
            Atendimentos atendimento = new Atendimentos();
            atendimento.VincularAtendimentoBo(response, idAtendimento,  idBoletim); 
            atendimento.Historico( idAtendimento, 6, "Vinculando Boletim ao Atendimento ", idUsuario);
        } catch (SQLException e) {
            logger.error("Erro ao iniciar enviar guarnicao: ", e);
            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro ao iniciar enviar guarnicao: " + e.getMessage());
        }
    }
    
    
	
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

	
	
	

	public static boolean validaPermissao(HttpServletRequest request, HttpServletResponse response, String sessaoId) {
		Acesso acesso = new Acesso(request, response, true); 
		String loginAtual = acesso.getUsuario().getNome();
		try {
			return loginAtual != null;
		} catch (Exception e) {
			logger.error("Erro ao validar permissão do usuário pela sessão: " + e.getMessage(), e);
			return false;
		}
	}
}
