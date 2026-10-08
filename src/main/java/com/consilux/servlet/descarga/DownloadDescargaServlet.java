package com.consilux.servlet.descarga;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.descarga.Descarga;
import com.consilux.model.descarga.beans.DescargaBean;

/**
 * Implementação do servlet para realizar download da ISO de uma descarga.
 * @author raoni
 */
public class DownloadDescargaServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DownloadDescargaServlet.class); 
       
    public DownloadDescargaServlet() {
        super();
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Verifica segurança
		Acesso acesso = new Acesso(request, response, false); 
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");		
		
		// Recupera os parâmetros
		String sIdDescarga = request.getParameter("id_descarga");

		// Valida o(s) parâmetros.
		if (sIdDescarga == null || !ExpValida.NATURAL.validar(sIdDescarga))	{
			new Mensagem(response).showErro("Identificador da Descarga enviado inválido!");
			return;
		}

		// Converte
		Integer idDescarga = null;
		try {
			idDescarga= Integer.parseInt(sIdDescarga);
		} catch (NumberFormatException fe) {
			new Mensagem(response).showErro("Identificador da Descarga enviado inválido!");
			return;			
		}
		
		FileInputStream fis = null;
		try {
			// Busca o bean da descarga.
			DescargaBean bean = Descarga.buscarDescargaPorId(idDescarga);
			if (bean == null) {
				new Mensagem(response).showErro("Identificador da Descarga enviado inválido!");
				return;				
			}
			
			// Determina a localização do arquivo.
			File isoDir = new File(ConfiguracaoProvider.getInstance().getConfiguracaoDescarga().getDiretorioSaida());
			File descargaISO = new File(isoDir.getPath() + File.separatorChar + Descarga.getIsoFileName(bean.getId(), bean.getDiaInicio(), bean.getDiaFim()));
			
			if (!descargaISO.exists()) {
				logger.warn("Não foi possível localizar o arquivo ISO da Descarga: "+descargaISO.getAbsolutePath());
				new Mensagem(response).showErro("Não foi possível localizar o arquivo ISO da Descarga!");
				return;					
			}
			
			Long tamanhoArquivo = descargaISO.length();
			
			// Muda os cabeçalhos			
			response.setContentType(TipoMime.ISO.getTipo());
			response.setHeader("Content-Disposition", "inline; filename=\"" + descargaISO.getName() +"\"");
			
			response.setHeader("Cache-Control", "max-age=86400");
			response.setHeader("Content-Length", tamanhoArquivo.toString());
			
			// Abre o arquivo ISO.
			fis = new FileInputStream(descargaISO);
			OutputStream os = response.getOutputStream();
			byte[] buff = new byte [4096];
			int bytesLidos = 0;
			
			// Transfere os dados
			while ((bytesLidos = fis.read(buff)) > 0) {
				os.write(buff, 0, bytesLidos);
			}
			os.close();
			
		} 
		catch (IOException e) {
			logger.error("Não foi possível realizar o download da ISO da Descarga, erro de IO.");
			new Mensagem(response).showErro("Não foi possível realizar o download da ISO da Descarga, erro de IO.");
			return;	
		}
		catch (Exception e) {
			logger.error("Não foi possível realizar o download da ISO da Descarga: "+e.getMessage(), e);
			new Mensagem(response).showErro("Não foi possível realizar o download da ISO da Descarga: "+e.getMessage());
			return;	
		}
		finally {
			if( fis != null)
			{
				fis.close();
			}
		}
	}
}
