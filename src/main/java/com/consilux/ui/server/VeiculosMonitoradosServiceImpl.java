package com.consilux.ui.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.VeiculoMonitorado;
import com.consilux.model.disparador.DisparadorVeiculoIrregularRefresh;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.ui.client.VeiculosMonitoradosService;
import com.consilux.ui.client.beans.GwtBean;
import com.consilux.ui.client.beans.VeiculoMonitoradoGwtBean;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class VeiculosMonitoradosServiceImpl extends GwtBaseServlet implements
VeiculosMonitoradosService {

	private static final long serialVersionUID = -1592054384752672584L;
	private static final long TEMPO_MAXIMO_ESPERA_POOLING = 30000; 
	private static final String NOME_SECTION = "ultimoPoolingVeiculoMonitorado";

	private static Logger logger = Logger.getLogger(VeiculosMonitoradosServiceImpl.class);  
	
	private static VeiculosMonitoradosService instance = null;

	public static void notificar() {

		if( instance == null)
			return;

		synchronized (instance) {
			instance.notifyAll();
		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
		if (instance == null){
			instance = this;
		}
	}

	@Override
	public List<VeiculoMonitoradoGwtBean> getVeiculosIrregulares() throws Exception {

		verificarSessaoLogada();
		Integer idUsuario = getIdUsuario();
		
		List<VeiculoMonitoradoGwtBean> result = new ArrayList<VeiculoMonitoradoGwtBean>(0);

		try{

			Date novoPooling = new Date();
			Date ultimoPooling = (Date)getSession().getAttribute( NOME_SECTION );

			logger.info("Veiculos Monitorados foi solicitado ...[" + idUsuario.toString() + "]");

			synchronized (this) {

				result = VeiculoMonitorado.toGwtBeans(
					VeiculoMonitorado.buscaUltimosVeiculo(idUsuario , ultimoPooling));

				if (result.size() == 0){
					this.wait(TEMPO_MAXIMO_ESPERA_POOLING);

					// atualiza o poolling, pois estava parado no wait 
					novoPooling = new Date();

					result = VeiculoMonitorado.toGwtBeans(VeiculoMonitorado.buscaUltimosVeiculo(idUsuario, ultimoPooling));
				}

			}
			getSession().setAttribute(NOME_SECTION , novoPooling);
		}
		catch (Exception e) {
			logger.error("Erro no pooling de veículos monitorados", e);
		}

		if( (result != null) && (result.size() > 0) ){
			logger.info("Veiculos Monitorados sendo entregues ... [" + idUsuario + "]");
		}  else {
			logger.info("Não existem novos veiculos monitorados ... [" + idUsuario + "]");
		}

		for (VeiculoMonitoradoGwtBean veiculoMonitoradoGwtBean : result) {
			logger.debug("Veículo:" + veiculoMonitoradoGwtBean.toString());
		}

		return result;

	}

	@Override
	public void setFalsoPositivo( Integer id, Boolean valor ) throws Exception {

		if (VeiculoMonitorado.setFalsoPositivo(id, valor) == true) {
			notificar();
			logger.info("FalsoPositivo setado ...[" + getUsuario() + "]");
		} else{
			throw new Exception("Não foi possível atualizar o estado do id_veículo " + id.toString() );
		}

	}

	public HttpSession getSession() {
		return getThreadLocalRequest().getSession();
	}

	@Override
	public void reset() throws Exception {

		logger.info("Reset da lista de veiculos [" + getUsuario() + "]");
		getSession().setAttribute( NOME_SECTION , null );

	}

	private void notificarCapturaServer() 
	{

		DisparadorVeiculoIrregularRefresh disp = new DisparadorVeiculoIrregularRefresh();
		disp.disparar();

	}

	@Override
	public void insertVeiculoMonitorado(VeiculoMonitoradoGwtBean veiculoMonitorado)
	throws Exception {

		verificarSessaoLogada();

		if (!ExpValida.EMAIL.validar(veiculoMonitorado.getEmailDestino()))
			throw new Exception("Email fornecido inválido.");
		
		try {
			if (!VeiculoMonitorado.insereVeiculoMonitorado(getIdUsuario(), veiculoMonitorado)){
				throw new Exception("Ocorreu um erro ao tentar inserir registro " + veiculoMonitorado );
			}
			notificarCapturaServer();		

		} catch (Exception e) {
			logger.error("Não foi possível inserir o veículo monitorado! " + veiculoMonitorado, e );
		}
	}

	@Override
	public List<GwtBean> getVeiculoMonitoradoSituacoes() throws Exception {
		verificarSessaoLogada();
		return VeiculoMonitorado.getSituacoes();
	}

	@Override
	public List<VeiculoMonitoradoGwtBean> getVeiculosMonitoradosCadastrados(String placa, 
			Boolean ativo, PagingLoadConfig config) throws Exception {

		verificarSessaoLogada();
		
		return VeiculoMonitorado.buscaVeiculosCadastrados(placa, ativo, config.getLimit(),
			config.getOffset(), config.getSortInfo().getSortField(),
			config.getSortInfo().getSortDir().name());

	}

	@Override
	public void removeVeiculoMonitorado(Integer idVeiculo, String motivo) throws Exception {

		verificarSessaoLogada();

		try {

			VeiculoMonitorado.removeVeiculoMonitorado(getIdUsuario(), idVeiculo, motivo);
			notificarCapturaServer();		

		} catch (Exception e) {
			logger.error("Não foi possível remover o veículo!", e);
		}
	}


}
