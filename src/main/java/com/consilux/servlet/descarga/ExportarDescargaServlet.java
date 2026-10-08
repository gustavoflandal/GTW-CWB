package com.consilux.servlet.descarga;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.descarga.Descarga;
import com.consilux.model.descarga.ExportaDescarga;

public class ExportarDescargaServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ExportarDescargaServlet.class);
	private Exception erroDescarga = null;
	
    public ExportarDescargaServlet() {
        super();
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {

		// Verifica segurança
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(true))
			throw new ServletException("Usuário não atenticado!");		
		
		// Recupera os parâmetros
		String sIdDescarga = request.getParameter("id_descarga");

		// Valida o(s) parâmetros.
		if (sIdDescarga == null || !Pattern.matches("[0-9]{1,8}", sIdDescarga))	{
			new Mensagem(response).showErro("Identificador da Descarga enviado inválido!");
			return;
		};

		// Converte
		Integer idDescarga = null;
		try {
			idDescarga= Integer.parseInt(sIdDescarga);
		} catch (NumberFormatException fe) {
			new Mensagem(response).showErro("Identificador da Descarga enviado inválido!");
			return;			
		}
		
		// Começo
		logger.info("Servlet de exportação de descarga iniciado para descarga: [" + idDescarga + "] ...");
		
		// Meio
		try {
			final Integer id = idDescarga;
			erroDescarga = null;
			
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						ExportaDescarga ed = new ExportaDescarga();
						ed.exportarDescarga(id);
						Descarga.confirmaExportacaoDescarga(id, ed.getMapaMd5());
					}
					catch (Exception e) {
						erroDescarga = e;
					}
				}
			});
			
			t.start();
			t.join();

			if (erroDescarga != null)
				throw erroDescarga;

			logger.info("ISO da descarga [" + idDescarga + "] foi criado com sucesso.");
			new Mensagem(response).showSucesso("ISO da descarga [" + idDescarga + "] foi criado com sucesso.");
		}
		catch(Exception ex) {
			new Mensagem(response).showErro("Erro ao gerar a descarga: "+ex.getMessage());
			logger.fatal("Erro ao gerar a descarga: "+ex.getMessage(), ex);
		}
    }

}
