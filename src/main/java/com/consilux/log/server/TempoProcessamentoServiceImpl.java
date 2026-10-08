package com.consilux.log.server;

import java.util.List;

import org.apache.log4j.Logger;

import com.consilux.model.TempoProcessamento;
import com.consilux.model.beans.TempoProcessamentoBean;
import com.consilux.model.beans.TempoProcessamentoBean.Classificacao;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.log.client.TempoProcessamentoService;
import com.consilux.log.client.beans.TempoProcessamentoGwtBean;

/**
 * Implementação no GTW do serviço de tempos de processamento.
 * @author raoni
 *
 */
public class TempoProcessamentoServiceImpl extends GwtBaseServlet implements TempoProcessamentoService {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(TempoProcessamentoServiceImpl.class);
	
	@Override
	public void salvarTemposProcessamento(List<TempoProcessamentoGwtBean> listaTempos) throws Exception {
		
		// Verifica a sessão.
		verificarSessaoLogada();
		
		if (listaTempos != null && listaTempos.size() > 0)
		{
			// Itera nos beans recebidos do GWT.
			for (TempoProcessamentoGwtBean gwtBean : listaTempos) {
				
				try {
					// Transforma os beans do GWT em beans do GTW.
					TempoProcessamentoBean beanSalvar = new TempoProcessamentoBean();
					beanSalvar.setIdUsuario(super.getIdUsuario());
					beanSalvar.setTempoGasto(gwtBean.getTempoGasto());
					beanSalvar.setDataInicioCliente(gwtBean.getDataInicioCliente());
					beanSalvar.setIdentificador(gwtBean.getIdentificador());
					beanSalvar.setClassificacao(Classificacao.valueOfCodigo(gwtBean.getClassificacao()));
					beanSalvar.setSubIdentificador(gwtBean.getSubIdentificador());
					
					// Salva o bean.
					TempoProcessamento.salvar(beanSalvar);
				} catch (Exception ex) {
					logger.error("Erro ao salvar tempo de processamento no banco de dados.", ex);
					throw new Exception(ex.getLocalizedMessage());
				}			
			}
		}
	}
	
	

}
