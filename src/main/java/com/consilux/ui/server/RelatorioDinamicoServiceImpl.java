package com.consilux.ui.server;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.RelatorioDinamico;
import com.consilux.model.beans.Periodo;
import com.consilux.model.beans.RelatorioDinamicoBean;
import com.consilux.model.exception.ModelException;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.ui.client.RelatorioDinamicoService;
import com.consilux.ui.client.beans.RelatorioDinamicoGwtBean;

/**
 * Implementação verdadeira do serviço de relatório dinâmico.
 * @author raoni
 */
public class RelatorioDinamicoServiceImpl extends GwtBaseServlet implements RelatorioDinamicoService {

	private static final long serialVersionUID = 6207067872269454416L;
	private static Logger logger = Logger.getLogger(RelatorioDinamicoServiceImpl.class);
	
	
	/**
	 * Um limite de linhas, para geração de XLS
	 */
	private static int LIMITE_LINHAS = 1048576;
	
	@Override
	public List<RelatorioDinamicoGwtBean> buscarRelatoriosDisponiveis() throws Exception {
		
		verificarSessaoLogada();
		
		try {
			return RelatorioDinamico.toGwtBeans( RelatorioDinamico.buscarRelatoriosDisponiveis(getIdUsuario())); 
		}
		catch (Exception ex) {
			logger.error("Erro ao obter os relatórios disponíveis do banco de dados.", ex);
			throw new Exception("Erro ao obter os relatórios disponíveis do banco de dados.");
		}
	}

	@Override
	public String exportarXLS(RelatorioDinamicoGwtBean relatorioExportar, Date dataInicial, Date dataFinal) throws Exception {

		verificarSessaoLogada();
		String urlDownload = null;
		
		try {

			// Busca as informações sobre o relatório desejado do banco de dados.
			RelatorioDinamicoBean beanRelatorio = RelatorioDinamico.buscarRelatorioDisponivelById(relatorioExportar.getId());
			if (beanRelatorio == null)
				throw new Exception("Não foi possível localizar o relatório [" + relatorioExportar.getDescricao() + "] no banco de dados.");
			
			// Verifica com COUNT se realmente podemos fazer este relatório.
			int qtdRegistros = RelatorioDinamico.contarDados(beanRelatorio.getFunctionSQL(), new Periodo(dataInicial, dataFinal));
			
			if (qtdRegistros <= LIMITE_LINHAS) {
				// Sim, podemos gerar o relatório. Devolve a URL de download para o browser.
				DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				urlDownload = "/ferramentas/DownloadRelatorio?id_relatorio=" + relatorioExportar.getId()
					+ "&data_inicio=" + fmt.format(dataInicial) + "&data_fim=" + fmt.format(dataFinal);
			} else {
				// Não podemos prosseguir. Levanta uma exceção.
				throw new Exception("O relatório excedeu o limite máximo de " + LIMITE_LINHAS + " linhas.\r\nUtilize um período menor.");
			}
		}
		catch (ModelException me) {
			logger.warn("Tentativa de criação de relatório com parâmetros inválidos", me);
			throw new Exception(me.getLocalizedMessage());
		}		
		catch (SQLException se) {
			logger.error("Erro de execução (SQL) no banco de dados.", se);			
			throw new Exception("Erro de execução (SQL) no banco de dados.");
		}
		catch (ConexaoException ce) {
			logger.error("Erro de conexão com o banco de dados.", ce);			
			throw new Exception("Erro de conexão com o banco de dados.");
		}
		return urlDownload;
	}
	
}
