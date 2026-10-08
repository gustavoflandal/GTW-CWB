import java.net.URL;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;


public abstract class ConfiguracaoTestes {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = -7842247483693978770L;
	private static ConfiguracaoProvider confTestes;

	private ConfiguracaoTestes() {
		// Construtor privado, com classe abstrata, para evitar inicialização.
	}
	
	public static void ajustaConfiguracaoTestes() {
	
		if (confTestes == null)
		{
		
			try {
				confTestes = new ConfiguracaoProvider()
				{
						@Override
						protected void initializar() throws ConfiguracaoException {
								
							URL arquivoConf = null;
							try {
								arquivoConf = getClass().getResource(Configuracao.ARQ_CONF_TESTES);
								if (arquivoConf == null)
									throw new ConfiguracaoException("Arquivo " + Configuracao.ARQ_CONF_TESTES + " não encontrado.");
//								ConfiguracaoProvider.instance = new ConfiguracaoXML(arquivoConf);
							} catch (Exception err) {
								throw new ConfiguracaoException("Erro (" + err.getMessage() + ") ao processar arquivo.", err);
							}					
						};
				};
			} catch(ConfiguracaoException err) {
				throw new RuntimeException("Erro ao carregar a configuração. ["
				+ err.getMessage() + "]", err);
	    	}
		}
	}
	
}
