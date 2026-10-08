package com.consilux.servlet.relatorio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.AcessoArquivosFTP;
import com.consilux.model.Mensagem;
import com.consilux.model.OcorrenciaLista;
import com.consilux.model.exception.ModelException;

/**
 * Servlet para a geração de arquivo de ocorrencia para download
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 05/05/2015
 */
public class RelatorioOcorrencia extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatorioOcorrencia.class);
	private AcessoArquivosFTP acessoFTP = new AcessoArquivosFTP();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		final Acesso acessoUsuario = new Acesso(request, response, true);

		if (!acessoUsuario.verificaAcesso())return;
		
		String strIdOcorrencia = request.getParameter("idOcorrenciaHidden");
		Integer intIdOcorrencia = 0;
		
		//Validações Necessárias para gerar arquivo
		try {
			
			if(strIdOcorrencia == "" || strIdOcorrencia == null){
				new Mensagem(response).showErro("Identificador do arquivo não encontrado.");
				return; 				
			}
			
			intIdOcorrencia = Integer.parseInt(strIdOcorrencia);
			
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar arquivo de ocorrência para download.", e);
			return;
		}
		
		try {
	        //Consultando dados do arquivo a ser visualizado
			OcorrenciaLista ocorrencia = OcorrenciaLista.buscaOcorrenciaPorId(intIdOcorrencia);
			
	        //Definindo o endereço do FTP de download
			String strCaminhoArquivo = "";
			String strNomeArquivo = "";
			String strTipoMime = "";
			
			if (ocorrencia == null) {
				new Mensagem(response).showErro("Não foi encontrado nenhum arquivo para este identificador.");
				return; 	
			}else {
				strCaminhoArquivo = ocorrencia.getCaminhoArquivo();
				strNomeArquivo = ocorrencia.getNomeArquivo();
				strTipoMime = ocorrencia.getTipoMime();
			}
	        
			gerarArquivoFTP(response, strCaminhoArquivo, strNomeArquivo, strTipoMime);			

            return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar arquivo de ocorrência.", e);
			new ServletException("Erro ao gerar arquivo de ocorrência: " + e.getMessage());
		}
	}


	/**
 	 * Servlet para a geração arquivo para download
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/05/2015
	 */
	public void gerarArquivoFTP(HttpServletResponse response, String strCaminhoArquivo,
							 	String strNomeArquivo, String strTipoMime) throws IOException, ConexaoException, SQLException, ModelException{
		
		try{
			
			byte[] arquivo = acessoFTP.ObterArquivo(strCaminhoArquivo);
			
			if(arquivo == null) {
				new Mensagem(response).showErro("Arquivo não encontrado.");
				return;
				
			}else {
				if(arquivo.length == 0){
					new Mensagem(response).showErro("Arquivo não encontrado.");
					return;
				}
				
				receberArquivoFTP(response, arquivo, strNomeArquivo, strCaminhoArquivo, strTipoMime);
				
			}
			
		}catch(Exception e){
			logger.error("Erro ao preparar arquivo para download.", e);
			return;
		}
	}
	
	/**
	 * Servlet para buscar arquivo no FTP e disponibilizar ao usuário
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/05/2015
	 */
	private void receberArquivoFTP(HttpServletResponse response, byte[] arquivo,
							 	   String strNomeArquivo, String strCaminhoArquivo, String strTipoMime) throws IOException {
		
	    InputStream is = null;
	  	OutputStream os = null;

	  	try {
			response.setContentType(strTipoMime);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + strNomeArquivo + "\"");
			response.setHeader("Refresh", "300");
			response.setHeader("Pragma","no-cache"); //HTTP 1.0
			response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
			response.setDateHeader("Expires", 0); //prevents caching at the proxy server

		    os = response.getOutputStream();
		    os.write(acessoFTP.ObterArquivo(strCaminhoArquivo));
			os.flush();
			
		} catch(FileNotFoundException ex){
			logger.error("Erro ao preparar arquivo para download.", ex);
			return;

		} finally {
			if(is != null){
				is.close();
			}
			if(os != null){
			  	os.close();
			}
		}
		
	}

	
	
	
	
	/**
 	 * Servlet para a geração arquivo para download
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/05/2015
	 */
	public void gerarArquivo(HttpServletResponse response, String strCaminhoArquivo,
							 String strNomeArquivo, String strTipoMime) throws IOException, ConexaoException, SQLException, ModelException{
		
		try{
			
			File f = new File(strCaminhoArquivo);

			if(f.exists() && !f.isDirectory()) {
				receberArquivo(response, strNomeArquivo, strCaminhoArquivo, strTipoMime);
			}
			
			
		}catch(Exception e){
			logger.error("Erro ao preparar arquivo para download.", e);
			return;
		}
	}
	
	/**
	 * Servlet para buscar arquivo no FTP e disponibilizar ao usuário
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/05/2015
	 */
	@SuppressWarnings("resource")
	private void receberArquivo(HttpServletResponse response, String strNomeArquivo, String strCaminhoArquivo, String strTipoMime) throws IOException {
		
	    InputStream is = null;
	  	OutputStream os = null;
		try {
			
	        File file = new File(strCaminhoArquivo);
	        
	        FileInputStream fis = new FileInputStream(file);

	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] buf = new byte[1024];
	        
	        try {
	            for (int readNum; (readNum = fis.read(buf)) != -1;) {
	                bos.write(buf, 0, readNum); //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
	            }
	        } catch (IOException ex) {
	        	logger.error("erro", ex);
	        }
	        
	        byte[] bytes = bos.toByteArray();
	 
	        os = response.getOutputStream();
	        os.write(bytes);
	        os.flush();
			
		} catch(FileNotFoundException ex){
			logger.error("Erro ao preparar arquivo para download.", ex);
			return;

		} finally {
			if(is != null){
				is.close();
			}
			if(os != null){
			  	os.close();
			}
		}
		
	}
	
}
