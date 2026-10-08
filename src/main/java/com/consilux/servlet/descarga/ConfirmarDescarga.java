package com.consilux.servlet.descarga;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.consilux.infra.AjaxXMLLeitor;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.MensagemJS;
import com.consilux.model.Usuario;
import com.consilux.model.descarga.Descarga;
import com.consilux.model.descarga.beans.DescargaBean;
import com.consilux.model.exception.ModelException;

/**
 * Servlet implementation class ConfirmarDescarga
 */
public class ConfirmarDescarga extends HttpServlet {
	private static Logger logger = Logger.getLogger(ConfirmarDescarga.class);  
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmarDescarga() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; //O usuário não tem acesso...então cai fora!
		}

		try	{
			DiskFileItemFactory factory = new DiskFileItemFactory();
	        // maximum size that will be stored in memory
	        factory.setSizeThreshold(4096);
	        // the location for saving data that is larger than getSizeThreshold()
	        factory.setRepository(new File("/tmp"));

	        ServletFileUpload upload = new ServletFileUpload(factory);
	        // maximum size before a FileUploadException will be thrown
	        upload.setSizeMax(1000000);

	        List<FileItem> fileItems = (List<FileItem>)upload.parseRequest(request);
	        
	        if (fileItems.size() != 1) {
	        	throw new ModelException("Arquivo não enviado!");
	        }

	        FileItem fileItem = fileItems.get(0);
			
	        InputStream conteudoArquivo = extraiConteudoArquivo(fileItem.getInputStream());			
	        AjaxXMLLeitor xml = extraiXML(conteudoArquivo);
			
			String sIdDescarga = null;
			String sDataCriacao = null;
			String sTotalImagens = null;
			
			sIdDescarga = xml.lerCampo("ID_DESCARGA");
			sDataCriacao = xml.lerCampo("DATA_CRIACAO");
			sTotalImagens = xml.lerCampo("TOTAL_IMAGENS");
			
			Integer idDescarga = Integer.valueOf(sIdDescarga);
			Date dataCriacao = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").parse(sDataCriacao);
			Integer totalImagens = Integer.valueOf(sTotalImagens);
			
			if (!confirmaDescarga(idDescarga, dataCriacao, totalImagens, acesso.getUsuario())) {
				new MensagemJS(response).showErro("Não foi possível confirmar a descarga, dados do arquivo enviado inconsistente.");
	    		logger.error("Não foi possível confirmar a descarga, dados do arquivo enviado inconsistente.");
			}
			else {
				new MensagemJS(response).showSucesso("Confirmação realizada com sucesso!");
			}
		}
		catch(Exception e)	{
    		new MensagemJS(response).showErro("Erro ao confirmar a descarga: "+e.getMessage());
    		logger.error("Erro ao confirmar a descarga!", e);
		}
	}

	private Boolean confirmaDescarga(Integer idDescarga, Date dataCriacao, Integer totalImagens, Usuario usu) throws ConexaoException, SQLException {
		Boolean bRet = false;
		
		DescargaBean descarga = Descarga.buscarDescargaPorId(idDescarga);
		bRet = dataCriacao.equals(descarga.getDataCriacao()) && totalImagens.equals(totalImagens);

		if (bRet)
			bRet = Descarga.confirmaDescarga(descarga.getId(), usu.getId());
		
		return bRet;
	}

	private AjaxXMLLeitor extraiXML(InputStream conteudoArquivo) throws ParserConfigurationException, SAXException, IOException, ModelException {
		AjaxXMLLeitor ret = new AjaxXMLLeitor(conteudoArquivo, "CONFIRMACAO_DESCARGA");

		return ret;
	}

	private InputStream extraiConteudoArquivo(InputStream inputStream) throws IOException {
		GZIPInputStream gzis = new GZIPInputStream(inputStream);
		
		return gzis;
	}
}
