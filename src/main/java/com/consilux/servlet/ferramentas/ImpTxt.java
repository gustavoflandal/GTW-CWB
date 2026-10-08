package com.consilux.servlet.ferramentas;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.model.Evento;
import com.consilux.model.EventoCSX;
import com.consilux.model.Usuario;
import com.consilux.model.EventoCSX.TipoEvento;

public class ImpTxt extends HttpServlet {

	private static final long serialVersionUID = 2347766585263278587L;
	private static final String APP_IMP_TXT = "ImpTxt";
	
	public static AtomicBoolean isReceivingFile = new AtomicBoolean(false);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
		if (isReceivingFile.compareAndSet(false, true)){
			if (ServletFileUpload.isMultipartContent(req)){
				Configuracao conf;
				ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());
				try {
					conf = ConfiguracaoProvider.getInstance();
					List<FileItem> items = fileUpload.parseRequest(req);
					
					for (FileItem fileItem : items) {
						URI uri = new URI(conf.getImpTxtDir());
						File f = new File(uri.getPath() + "/" + fileItem.getName());
						fileItem.write(f);
						Usuario usuarioAtual = (Usuario) req.getSession().getAttribute("[usuario]");
						EventoCSX eventoCsx = new EventoCSX(TipoEvento.ADD_IMP_TXT,
								usuarioAtual.getNome(), APP_IMP_TXT, 
								"Arquivo:" + fileItem.getName());
						Evento.incluirEventoCSX(eventoCsx);
					}
					
				} catch (FileUploadException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			isReceivingFile.set(false);
		}
	}

	
}
