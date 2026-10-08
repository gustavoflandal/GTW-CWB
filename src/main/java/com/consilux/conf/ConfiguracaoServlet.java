package com.consilux.conf;

import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.ChaveValor;
import com.consilux.model.exception.ModelException;

/**
 * Servlet responsável por inicializar as configurações do sistema.
 * @author raoni
 */
public class ConfiguracaoServlet extends HttpServlet {

	private static ConfiguracaoProvider confProvider;
	private static final long serialVersionUID = 336883379154577290L;
	private static Logger logger = Logger.getLogger(ConfiguracaoServlet.class);
	
	public void init(final ServletConfig config) throws ServletException {
		
		if (confProvider == null)
		{
			try 
			{
				ConfiguracaoServlet.confProvider = new ConfiguracaoProvider() {
					@Override
					protected void initializar() throws ConfiguracaoException {
						
						try {
							String arquivoConf_path = config.getServletContext().getRealPath(Configuracao.ARQ_CONF);
							ConfiguracaoProvider.instance = new ConfiguracaoXML(arquivoConf_path);
							
							// Recupera o mapa chave/valor do XML
							ConfiguracaoChaveValor mapaChaveValor = ConfiguracaoProvider.instance.getConfiguracaoChaveValor();
							
							// E salva eles no banco.
							logger.info("Transferindo chave-valor do conf para o banco.");
							ChaveValor.salvarMapa(mapaChaveValor);
							logger.info("Transferência de chave-valor concluída.");
							
						} catch (ModelException err) {
							throw new ConfiguracaoException("Erro ao transferir chave/valores do XML para o banco.", err);
						} catch (ConexaoException err) {
							throw new ConfiguracaoException("Erro ao transferir chave/valores do XML para o banco.", err);
						} catch (SQLException err) {
							throw new ConfiguracaoException("Erro ao transferir chave/valores do XML para o banco.", err);
						} catch (Exception err) {
							throw new ConfiguracaoException("Erro não tratável!", err);
						}					
					}
				};
			}
	    	catch(ConfiguracaoException ce) {
	    		String message = "Erro ao carregar a configuração.";
	    		logger.error(message, ce);
	    		synchronized (this) {
		    		notifyAll();
				}
	    		System.exit(1); //Forçando o cancelamento da inicialização.
	    	}
		}
    }
}
