package muralha.digital.ocorrencialigacao;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/OcorrenciaLigacao")
public class OcorrenciaLigacaoServlet extends HttpServlet implements Servlet {
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(OcorrenciaLigacaoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//Validando acesso do usuário		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		if (!acessoUsuario.verificaAcesso())
		{
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}
		
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
	    	
	    	if(strAcao.equals("obterTiposSolicitante"))
	    		ObterTiposSolicitante(request, response, strAcao);	
	    	else
	    		if(strAcao.equals("obterTiposOcorrencias"))
	    			ObterTiposOcorrencias(request, response, strAcao);	
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doGet() da OcorrenciaLigacaoServlet: " + e.getMessage(), e);
	    }
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Integer idUsuario = null;
	    final Acesso acessoUsuario = new Acesso(request, response, true);
	    if (!acessoUsuario.verificaAcesso()) {
	        new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
	        return;
	    } else {
	        idUsuario = acessoUsuario.getUsuario().getId();
	    }

	    try {
	        String msg = null;
	        String strAcao = request.getParameter("acao");

	        if (strAcao == null || strAcao.isEmpty()) {
	            msg = "Ação não informada!";
	            logger.error(msg);
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	            return;
	        }

	        if (strAcao.equals("cadastrarOcorrencia")) {
	            CadastrarOcorrenciaLigacao(request, response, strAcao, idUsuario);
	        }
	        
	    } catch (Exception e) {
	        logger.error("Erro no processo doPost() da OcorrenciaLigacaoServlet: " + e.getMessage(), e);
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.getWriter().write("Erro interno no servidor.");
	    }
	}
	
	public void ObterTiposSolicitante (HttpServletRequest request, HttpServletResponse response, String strAcao) throws JAXBException, IOException, ConexaoException, SQLException {
		
		//Cria objeto de retorno
		OcorrenciaLigacaoTipoSolicitantes listaOcorrencias = new OcorrenciaLigacaoTipoSolicitantes();
		listaOcorrencias.setListaSolicitantes(new ArrayList<OcorrenciaLigacaoTipoSolicitante>());
			
		List<OcorrenciaLigacaoTipoSolicitante> resultListaOcorrencias = new ArrayList<OcorrenciaLigacaoTipoSolicitante>();
		resultListaOcorrencias = OcorrenciaLigacaoTipoSolicitantes.ObterListaSolicitantes();			
		
		listaOcorrencias.setListaSolicitantes(resultListaOcorrencias);

		EnviarRespostaXML(response, listaOcorrencias, strAcao);
	}
	
	public void ObterTiposOcorrencias (HttpServletRequest request, HttpServletResponse response, String strAcao) throws JAXBException, IOException, ConexaoException, SQLException {
		
		//Cria objeto de retorno
		OcorrenciaLigacaoTipos listaTiposOcorrencias = new OcorrenciaLigacaoTipos();
		listaTiposOcorrencias.setListaTiposOCorrencias(new ArrayList<OcorrenciaLigacaoTipo>());
			
		List<OcorrenciaLigacaoTipo> resultListaTiposOcorrencias = new ArrayList<OcorrenciaLigacaoTipo>();
		resultListaTiposOcorrencias = OcorrenciaLigacaoTipos.ObterTiposOcorrencias();			
		
		listaTiposOcorrencias.setListaTiposOCorrencias(resultListaTiposOcorrencias);

		EnviarRespostaXML(response, listaTiposOcorrencias, strAcao);
	}
	
	public void CadastrarOcorrenciaLigacao(HttpServletRequest request, HttpServletResponse response, String strAcao, int idUsuario) 
	        throws JAXBException, IOException, ConexaoException, SQLException {

	    try {
	    	
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	        OcorrenciaLigacao ocorrencia = new OcorrenciaLigacao();
	        ocorrencia.setDataHoraEvento(sdf.parse(request.getParameter("dataHoraOcorrido")));
	        ocorrencia.setIdTipoSolicitante(Integer.parseInt(request.getParameter("idTipoSolicitante")));
	        ocorrencia.setNomeSolicitante(request.getParameter("nomeSolicitante"));
	        ocorrencia.setCpfSolicitante(request.getParameter("cpfSolicitante"));
	        ocorrencia.setIdTipoOcorrencia(Integer.parseInt(request.getParameter("idTipoOcorrencia")));
			ocorrencia.setIdCidade(Integer.parseInt(request.getParameter("idCidade")));
	        ocorrencia.setBairro(request.getParameter("bairro"));
	        ocorrencia.setRua(request.getParameter("rua"));
	        ocorrencia.setNumero(Integer.parseInt(request.getParameter("numero")));
	        ocorrencia.setComplemento(request.getParameter("complemento"));
	        ocorrencia.setNomeVitima(request.getParameter("nomeVitima"));
	        ocorrencia.setDetalhamento(request.getParameter("detalhamento"));
	        ocorrencia.setExisteArmaEnvolvida(Integer.parseInt(request.getParameter("existeArmaEnvolvida")));
	        ocorrencia.setIdUsuario(idUsuario);

	        boolean sucesso = OcorrenciasLigacao.CadastrarOcorrenciaLigacao(ocorrencia);

	        if (sucesso) {
	            response.setStatus(HttpServletResponse.SC_OK);
	            response.getWriter().write("Ocorrência cadastrada com sucesso.");
	        } else {
	            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            response.getWriter().write("Falha ao cadastrar ocorrência.");
	        }
	    } catch (Exception e) {
	    	logger.error("Erro ao processar requisição", e);
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.getWriter().write("Erro ao processar requisição: " + e.getMessage());
	    }
	}
	
	private <T> void EnviarRespostaXML(HttpServletResponse response, T objetoResposta, String strAcao) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(objetoResposta.getClass());
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(objetoResposta, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			objetoResposta = null;			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da ação "+ strAcao;
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}
