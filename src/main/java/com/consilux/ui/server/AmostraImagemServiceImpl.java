package com.consilux.ui.server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.consilux.model.AmostraImagem;
import com.consilux.model.VeiculoImagem;
import com.consilux.model.beans.AmostraVeiculoBean;
import com.consilux.model.beans.AmostraVeiculoThumbBean;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.ui.client.AmostraImagemService;
import com.consilux.ui.client.beans.AmostraVeiculoGwtBean;
import com.consilux.ui.client.beans.AmostraVeiculoThumbGwtBean;
import com.consilux.ui.client.beans.VeiculoImagensGwtBean;
import com.consilux.ui.tabela.PistaAmostraImagem;
import com.consilux.ui.tabela.TabelaAmostraImagem;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 * Implementação no GTW do serviço de amostras.
 * @author raoni
 */
public class AmostraImagemServiceImpl extends GwtBaseServlet
implements AmostraImagemService{

	private static final long serialVersionUID = 4562880828291250522L;
	private static Logger logger = Logger.getLogger(AmostraImagemServiceImpl.class);  
	
	/**
	 * Busca uma amostra específica, para uma combinação dos parâmetros. Retorna uma lista (com apenas um elemento).
	 * Feito desta maneira para garantir compatibilidade de callback com a parte GWT. 
	 * @param idLocal
	 * @param idPista
	 * @param dia
	 * @param metrologica
	 * @return
	 * @throws Exception
	 */
	public List<AmostraVeiculoGwtBean> buscaAmostraEspecifica(int idLocal, int idPista, Date dia, boolean metrologica)
	throws Exception {
		
		verificarSessaoLogada();

		List<AmostraVeiculoGwtBean> lRet;
		Connection conn = null;
		
		try {
			
			conn = Conexao.getConexao();
			
			/// Transforma o Date em Calendar e trunca (apenas ano,mes,dia)
			Calendar diaPesquisa = Calendar.getInstance();
			diaPesquisa.setTime(dia);
			diaPesquisa = DateUtils.truncate(diaPesquisa, Calendar.DATE);
			
			// Busca as amostras que estão no banco de dados.
			Queue<AmostraVeiculoBean> amostrasNoBanco = AmostraImagem.buscaAmostrasPeriodo(conn, diaPesquisa, diaPesquisa, idLocal, idPista, metrologica);

			// Se tem amostra no banco
			if (amostrasNoBanco.size() == 1)
			{
				lRet = AmostraVeiculoBean.toGwtBeans(amostrasNoBanco);
			}
			else
			{
				// Caso contrário, precia criar uma amostra "dummy".
				lRet = new ArrayList<AmostraVeiculoGwtBean>(1);
				lRet.add(TabelaAmostraImagem.criarAmostraDummy(diaPesquisa, idLocal, idPista, metrologica).toGwtBean());
			}
		}
		catch (Exception ex) {
			logger.error("Erro ao obter as amostras do banco de dados.", ex);
			throw new Exception("Erro ao obter as amostras do banco de dados.", ex);
		}
		finally {
			if (conn != null)
				conn.close();			
		}
		
		return lRet;
	}
	
	@Override
	public List<AmostraVeiculoGwtBean> listarAmostrasDoPeriodo(Date dataInicio, Date dataFim) throws Exception {
		
		verificarSessaoLogada();

		List<AmostraVeiculoGwtBean> lRet = new ArrayList<AmostraVeiculoGwtBean>();
		
		try {
			// COnverte Date em Calendar e trunca (apenas ano, mes dia)
			Calendar diaInicio = Calendar.getInstance();
			diaInicio.setTime(dataInicio);
			diaInicio = DateUtils.truncate(diaInicio, Calendar.DATE);
			
			Calendar diaFim = Calendar.getInstance();
			diaFim.setTime(dataFim);
			diaFim = DateUtils.truncate(diaFim, Calendar.DATE);
			
			TabelaAmostraImagem tabAmostras = null;
			
			// Verificar se já não existe uma tabela de amostras na sesssão...
			if (getSession().getAttribute("[TabelaAmostraImagem]") != null)
				tabAmostras = (TabelaAmostraImagem) getSession().getAttribute("[TabelaAmostraImagem]");
			else
				tabAmostras = new TabelaAmostraImagem(diaInicio, diaFim);
			
			// Itera em todas as pistas deste período.
			for (Entry<PistaAmostraImagem, List<AmostraVeiculoBean>> pistaAmostras : tabAmostras.entrySet())
			{
				// Itera nas amostras desta pista.
				for (AmostraVeiculoBean currAmostra : pistaAmostras.getValue())
				{
					// Transforma a amostra corrente em um bean do GWT e adiciona na
					// lista de retorno.
					lRet.add(AmostraImagem.toGwtBean(currAmostra));
				}
			}
			return lRet;
		}
		catch (Exception ex) {
			logger.error("Erro ao obter a lista de amostras do banco de dados.", ex);
			throw new Exception("Erro ao obter a lista de amostras do banco de dados.", ex);
		}
	}

	@Override
	public PagingLoadResult<AmostraVeiculoThumbGwtBean> listarSugestoes(int idLocal, byte idPista,
		Date dia, boolean metrologica, PagingLoadConfig pageConfig) throws Exception {
		
		verificarSessaoLogada();
		
		try {
			int qtdSugestoes = AmostraImagem.contarSugestoes(idLocal, idPista, dia, metrologica);
			
			List<AmostraVeiculoThumbGwtBean> listaSugestoes = AmostraVeiculoThumbBean.toGwtBeansThumb(
				AmostraImagem.listarSugestoes(idLocal, idPista, dia, metrologica,
				pageConfig.getOffset(), pageConfig.getOffset() + pageConfig.getLimit()));
			
			return new BasePagingLoadResult<AmostraVeiculoThumbGwtBean>(listaSugestoes,
				pageConfig.getOffset(), qtdSugestoes);
		}
		catch (Exception ex) {
			logger.error("Erro ao obter a lista paginada de amostras do banco de dados.", ex);
			throw new Exception("Erro ao obter a lista paginada de amostras do banco de dados.", ex);
		}
	}

	@Override
	public void definirManualmenteIdImagem(int idLocal, byte idPista,
			Date dia, boolean metrologica, String sVeiculo, boolean aplicavel) throws Exception {
		
		verificarSessaoLogada();
		try {
			Long idVeiculo = Long.parseLong(sVeiculo);
			AmostraImagem.definirManualmenteIdImagem(super.getIdUsuario(), dia,
				idLocal, idPista, metrologica, idVeiculo, aplicavel);
		}
		catch (Exception ex) {
			logger.error("Erro ao definir manualmente a mostra no banco de dados.", ex);
			throw new Exception("Erro ao definir manualmente a mostra no banco de dados.", ex);
		}
		
	}

	@Override
	public VeiculoImagensGwtBean listarImagensDoVeiculo(String idVeiculo) throws Exception {

		verificarSessaoLogada();
		
		try {
			return VeiculoImagem.buscaVeiculoImagemPorIdVeiculo(Long.parseLong(idVeiculo));
		}
		catch (Exception ex) {
			logger.error("Erro ao obter a listar as imagens do veículo no banco de dados.", ex);
			throw new Exception("Erro ao obter a listar as imagens do veículo no banco de dados..", ex);
		}
	}
	
}
