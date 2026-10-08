package com.consilux.servlet.ferramentas;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoExportaImagens;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.ExportacaoImagem;
import com.consilux.model.Mensagem;
import com.consilux.model.ferramenta.ExportaImagens;

public class ExportarImagensServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ExportarImagensServlet.class);
	private Exception erroRemessa = null;
	
    public ExportarImagensServlet() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {

		// Verifica segurança
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(true))
			throw new ServletException("Usuário não atenticado!");		
		
		// Recupera os parâmetros
		String sIdRemessa = request.getParameter("id_remessa");

		// Valida o(s) parâmetros.
		if (sIdRemessa == null || !ExpValida.NATURAL.validar(sIdRemessa))	{
			new Mensagem(response).showErro("Identificador da Remessa enviado inválido!");
			return;
		};

		Integer idRemessa = Integer.parseInt(sIdRemessa);
		
		// Começo
		logger.info("Servlet de exportação de imagens iniciado para remessa: [" + idRemessa + "] ...");
		
		// Meio
		try {
			final Integer id = idRemessa;
			erroRemessa = null;
			
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {

						ConfiguracaoExportaImagens conf = ConfiguracaoProvider.getInstance().getConfiguracaoExportaImagens();
						File directory = new File(conf.getDiretorio());
						
						if (!directory.isDirectory() || !directory.exists()) {
							logger.error("Erro: O diretório de exportação de imagens [" + directory.getPath() +  "] não existe.");
							String tempDir = System.getProperty("java.io.tmpdir");
							logger.info("Exportação Imagens: Tentando gravar no diretório alternativo: '"+tempDir+"'...");
							directory = new File(tempDir);
							if (!directory.isDirectory() || !directory.exists()) {
								logger.fatal("Erro: O diretório de exportação de imagens [" + directory.getPath() +  "] não existe.");
								return;
							}
						}

						ExportaImagens ed = new ExportaImagens();
						ed.setDiretorioSaida(directory);
						ExportacaoImagem ei = ed.exportarImagensParaRemessa(id, true);
						ei.confirmaExportacao();
					}
					catch (Exception e) {
						erroRemessa = e;
					}
				}
			});
			
			t.start();
			t.join();

			if (erroRemessa != null)
				throw erroRemessa;

			logger.info("Imagens da remessa [" + String.valueOf(idRemessa) + "] foram exportadas com sucesso.");
			new Mensagem(response).showSucesso("Imagens da remessa [" + String.valueOf(idRemessa) + "] foram exportadas com sucesso.");
		}
		catch(Exception ex) {
			new Mensagem(response).showErro("Erro na exportação: "+ex.getMessage());
			logger.fatal("Erro ao exportar as imagens: "+ex.getMessage(), ex);
		}
    }

}
