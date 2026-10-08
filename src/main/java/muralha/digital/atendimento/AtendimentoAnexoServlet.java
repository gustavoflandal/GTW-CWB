package muralha.digital.atendimento;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;

import muralha.digital.util.RespostaRequisicaoXML;



@WebServlet("/MuralhaDigital/Anexo")
public class AtendimentoAnexoServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(AtendimentoAnexoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();	
	
	private static String UPLOAD_DIRECTORY = "";	
    private static final int THRESHOLD_SIZE     = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		 Atendimentos atendimento = new Atendimentos();
		
		Integer idUsuario = null;
				
		final Acesso acessoUsuario = new Acesso(request, response, true);
		if (!acessoUsuario.verificaAcesso())
		{
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}
		else
		{
			idUsuario = acessoUsuario.getUsuario().getId();			
		}
		
		ObterDir();
		
        Integer idAtendimentoDocumento 	= 0;
        String caminho_arquivo 		= "";
        String caminho_completo 	= "";
        int idAtendimento			= 0;
        String nomeArquivoFinal = "";
		
		Integer id_usuario = null;
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(false)) return; 
		else id_usuario = acesso.getUsuario().getId();
		
        try 
        {        	
        	if (!ServletFileUpload.isMultipartContent(request)) {
                logger.error("ERRO doPost() Justificativa:: Requisição não é multipart.");
                EnviarRespostaTexto(response, "erro: Requisição não é multipart.", false);
                return;
            }
             
            DiskFileItemFactory fabrica = new DiskFileItemFactory();
            fabrica.setSizeThreshold(THRESHOLD_SIZE);
            fabrica.setRepository(new File(System.getProperty("java.io.tmpdir")));        	
        	
            ServletFileUpload upload = new ServletFileUpload(fabrica);
            upload.setFileSizeMax(MAX_FILE_SIZE);
            upload.setSizeMax(MAX_REQUEST_SIZE);   
                       
            List lista_itens = upload.parseRequest(request);            
            Iterator itens = lista_itens.iterator();
             
            while (itens.hasNext()) 
            {
                FileItem item = (FileItem) itens.next();
                                
                String tipo 	= item.getFieldName();
                String dados 	= item.getString();

                if(tipo.equals("info_json"))
                {
                	JSONObject obj = new JSONObject(dados);        			        			
                	                	                	
        			if (obj.getString("acao").equals("anexarDocumentos"))
        			{       	
        				idAtendimento = Integer.parseInt(obj.getString("idAtendimento"));        			
        				
        				idAtendimentoDocumento = SalvarRegistro(dados, id_usuario, caminho_completo);
        				
                        if (idAtendimentoDocumento <= 0) {
        					EnviarRespostaTexto(response, "Erro ao anexar documento ", false);
        				}
        			}	
                }                
                
                if(tipo.equals("arquivo"))
                {
                	int randomNumber = ThreadLocalRandom.current().nextInt(1000, 10000); 

					String extensao = getFileExtension(item.getName());
					nomeArquivoFinal = idAtendimento + "_" + randomNumber + "." + extensao;
                    caminho_arquivo = UPLOAD_DIRECTORY +  File.separator + idAtendimento +  File.separator + new SimpleDateFormat("dd_MM_yyyy").format(new Date().getTime());              
                    caminho_completo = caminho_arquivo + File.separator  +  nomeArquivoFinal;        				
            		
            		File dir = new File(caminho_arquivo);
                    if (!dir.exists())
                    {
                        if (dir.mkdirs()) { 
                            logger.info("Diretorio criado: " + caminho_arquivo); 	    	                    
                        } 
                        else { 
                            logger.error("Erro ao criar diretorio: "+ caminho_arquivo); 
                        } 
                    }

                    File salva_arquivo = new File(caminho_completo);
                    item.write(salva_arquivo);	  
                    AtualizarCaminho(idAtendimentoDocumento, caminho_completo);    	                    	                    
                	
                }            
            }            
            
            EnviarRespostaTexto(response, "Documento anexado com sucesso", true);
            atendimento.Historico( idAtendimento, 6, "Novo documento anexado pela Central de atendimento: "+ nomeArquivoFinal, idUsuario);
         
            
        } catch (Exception ex) {
        	logger.error("Falha no cadastro/alteração de justificativa");
        	EnviarRespostaTexto(response, "erro: " + ex.getMessage(), false);
        }				
	}
	
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
	    	
	    	if(strAcao.equals("obterDocumentos")) {
	    		int idAtendimento = Integer.parseInt(request.getParameter("idAtendimento"));
	    		ObterDocumentos(request, idAtendimento, response);    	
	    	}
	    	if (strAcao.equals("downloadDocumento")){
				try {	
					 int idArquivo = Integer.parseInt(request.getParameter("id"));
					DownloadArquivo(response, idArquivo);
				} catch(Exception err) {
					logger.error("XXX Lote 9 Erro ao fazer download de arquivo de justificativa", err);
					return;
				}					
			}
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doPost() de requisição de Alerta: " + e.getMessage(), e);
	    }
    }
	
	 protected void ObterDocumentos(HttpServletRequest request, int idAtendimento, HttpServletResponse response) throws ServletException, IOException 
		{
			try 
			{	    	
				Documentos docs = new Documentos();
				docs.setDocumentos(new ArrayList<Documento>()); 			
				List<Documento> listaDocs = Documentos.ObterDocumentos(idAtendimento);
				docs.setDocumentos(listaDocs);		
				EnviarRespostaXML(response, docs);			
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
	
	private void ObterDir()
	{
		boolean possuiDir = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_justificativa") != null && ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_justificativa") != "";
		if(possuiDir) {
			UPLOAD_DIRECTORY =  ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_justificativa");
		}else {
			UPLOAD_DIRECTORY =  "C:\\Consilux\\Documentos_atendimento";
		}
		logger.info(UPLOAD_DIRECTORY);
	}
	
	private String getFileExtension(String nome_arquivo) {
		String extension = "";
		int i = nome_arquivo.lastIndexOf('.');
		if (i >= 0) {
		    extension = nome_arquivo.substring(i+1);
		}
		return extension;
	}
	
	private Integer SalvarRegistro(String json, Integer id_usuario, String caminho_completo) throws JSONException, ParseException, ConexaoException, SQLException  
	{	
		
		Integer id_ret = 0;
		StringBuilder sbSQL = new StringBuilder();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;		

		try	
		{
			logger.info("Novo documento adicionado:: " + id_usuario.toString() + " -- json:: " + json);
			
			JSONObject obj = new JSONObject(json);
			
			int idAtendimento			= Integer.parseInt(obj.getString("idAtendimento"));			
			String tipoArquivo			= obj.getString("tipoArquivo");
			String detalhamento			= obj.getString("obsString");
			
			
			String tipo = "";
			if (tipoArquivo != null && tipoArquivo.contains("/")) {
			    tipo = tipoArquivo.substring(tipoArquivo.lastIndexOf('/') + 1).toLowerCase();
			}
		    
			
			
						
			sbSQL.append(" INSERT INTO muralha.atendimento_documento ");
			sbSQL.append(" (id_atendimento, tipo, detalhamento, dir_arquivo ) ");
			sbSQL.append(" values ( ?, ?, ?, ? ) ");	 					
								
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);
				
			ps.setInt		(1, idAtendimento);
			ps.setString	(2, tipo);
			ps.setString	(3, detalhamento);
			ps.setString	(4, caminho_completo);
			//ps.setDate		(4, new java.sql.Date(data_evento.getTime()));			
					
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			rs.next();
			id_ret = rs.getInt(1);	
			

		} catch (Exception e) {
			logger.error("Erro ao gravar justificativa Lote 9 no banco de dados.", e);
		
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}
		return id_ret;
	}
	
	private void AtualizarCaminho(int idAtendimentoDocumento, String caminho_completo) throws JSONException, ParseException, ConexaoException, SQLException  
	{	
		
		
		StringBuilder sbSQL = new StringBuilder();
		PreparedStatement ps = null;		
		Connection conn = null;		

		try	
		{
			logger.info("Caminho do documento adicionado  ");		
						
			sbSQL.append(" UPDATE muralha.atendimento_documento ");
			sbSQL.append(" SET dir_arquivo = ? ");
			sbSQL.append(" WHERE id = ? ");	 					
								
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);
				
			ps.setString	(1, caminho_completo);
			ps.setInt		(2, idAtendimentoDocumento);			
					
			ps.executeUpdate();
			
			

		} catch (Exception e) {
			logger.error("Erro ao gravar justificativa Lote 9 no banco de dados.", e);
		
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}
	}
	
	
	private void EnviarRespostaTexto(HttpServletResponse response, String texto, boolean sucesso) throws IOException
	{
		response.setContentType("text/plain");
		
		if (sucesso) { 
			response.setStatus(HttpServletResponse.SC_OK); 
		}else { 
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
		}
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(texto);                         
		response.getWriter().flush();
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
	
	public void DownloadArquivo(HttpServletResponse response, int idArquivo) {
	    try {
	        Documento doc = (Documento) Documento.obterPorId(idArquivo);  
	        if (doc == null) {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Documento não encontrado.");
	            return;
	        }

	        File arquivo = new File(doc.getDirArquivo());
	        if (!arquivo.exists()) {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo não encontrado.");
	            return;
	        }

	        String extensao = getFileExtension(arquivo.getName()).toLowerCase();
	        String nomeAmigavel = doc.getDetalhamento().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

	        switch (extensao) {
	            case "pdf":
	                response.setContentType(TipoMime.PDF.getTipo());
	                response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeAmigavel + ".pdf\"");
	                break;
	            case "jpg":
	            case "jpeg":
	                response.setContentType(TipoMime.JPG.getTipo());
	                response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeAmigavel + ".jpg\"");
	                break;
	            case "png":
	                response.setContentType(TipoMime.PNG.getTipo());
	                response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeAmigavel + ".png\"");
	                break;
	            default:
	                response.setContentType("application/octet-stream");
	                response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeAmigavel + "." + extensao + "\"");
	                break;
	        }

	        try (FileInputStream fis = new FileInputStream(arquivo);
	             OutputStream os = response.getOutputStream()) {
	            byte[] buffer = new byte[4096];
	            int bytesRead;
	            while ((bytesRead = fis.read(buffer)) != -1) {
	                os.write(buffer, 0, bytesRead);
	            }
	            os.flush();
	        }

	    } catch (Exception e) {
	        logger.error("Erro ao realizar download: " + e.getMessage(), e);
	        try {
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar download.");
	        } catch (IOException ioe) {
	            logger.error("Erro ao enviar erro HTTP: " + ioe.getMessage(), ioe);
	        }
	    }
	}


}
