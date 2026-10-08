package com.consilux.ui.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.model.Local;
import com.consilux.model.Manutencao;
import com.consilux.model.ManutencaoDescricao;
import com.consilux.model.Tecnico;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.ui.client.ManutencaoService;
import com.consilux.ui.client.beans.LocalGwtBean;
import com.consilux.ui.client.beans.ManutencaoDescricaoGwtBean;
import com.consilux.ui.client.beans.ManutencaoGwtBean;
import com.consilux.ui.client.beans.TecnicoGwtBean;

public class ManutencaoServiceImpl extends GwtBaseServlet
implements ManutencaoService {

	private static final long serialVersionUID = 7707504084657611035L;

	private LocalGwtBean convertToLocalBean(Local local) {
		LocalGwtBean bean = new LocalGwtBean(local.getIdLocal(), local.getSequenciaLocal(),
				local.getIdConfiguracaoEquipamento(), local.getNome());
		return bean;
	}

	public ManutencaoGwtBean salvarManutencao(ManutencaoGwtBean bean)
	throws Exception {

		verificarSessaoLogada();
		if (bean == null)
			throw new Exception("Erro: Tentativa de salvar uma manutenção nula.");

		if (bean.getAtividades() == null || bean.getAtividades().size() <= 0)
			throw new Exception("Erro: Não é possível salvar uma" +
			" manutenção sem atividades.");

		if (bean.getNomeTecnico() == null || bean.getNomeTecnico().length() <= 0)
			throw new Exception("Erro: Não é possível salvar uma" +
			" manutenção sem o técnico.");

		if (bean.getDataUtilizacao() == null)
			throw new Exception("Erro: Não é possível salvar uma" +
			" manutenção sem a data de utilização.");

		// Ignora a data de criação que veio do cliente e define a nossa data.
		bean.setDataCriacao(new Date());

		// Ignora o nome do usuário que veio do cliente e define o nosso usuário.		
		bean.setNomeUsuario(this.getUsuario());
		
		try {
			Manutencao mnuSalvou = Manutencao.incluirManutencao(Manutencao.fromGwtBean(bean));
			bean.setIdManutencao(mnuSalvou.getIdManutencao());
			bean.setSenha(mnuSalvou.getSenha());
		} catch (Exception ex) {
			throw new Exception("Erro ao tentar incluir uma nova manutenção.");			
		}

		return bean;
	}

	public List<TecnicoGwtBean> listarUsuariosTecnicos()
	throws Exception {

		verificarSessaoLogada();
		List<TecnicoGwtBean> listaBeans = new ArrayList<TecnicoGwtBean>();

		try {
			for (Tecnico tecnico : Tecnico.buscarTecnicosAtivos()) {
				listaBeans.add(tecnico.toTecnicoBean());
			}
		} catch (Exception ex) {
			throw new Exception("Erro no serviço de manutenção\nao listar os técnicos.");
		}
		return listaBeans;
	}

	public List<LocalGwtBean> listarLocais()
	throws Exception {

		verificarSessaoLogada();

		List<LocalGwtBean> listaBeans = new ArrayList<LocalGwtBean>();

		try {
			for (Local local : Local.listarLocaisVigentes()) {
				listaBeans.add(convertToLocalBean(local));
			}
		} catch (Exception ex) {
			throw new Exception("Erro no serviço de manutenção\n ao listar os locais.");
		}

		return listaBeans;
	}

	public List<ManutencaoDescricaoGwtBean> listarDescricoes()
	throws Exception {

		verificarSessaoLogada();
		List<ManutencaoDescricaoGwtBean> listaBeans = new ArrayList<ManutencaoDescricaoGwtBean>();

		try {
			for (ManutencaoDescricao man : ManutencaoDescricao.listarManutencaoDescricao()) {
				listaBeans.add(man.toManutencaoDescricaoBean());
			}
		} catch (Exception ex) {
			throw new Exception("Erro no serviço de manutenção\n ao listar as atividades.");
		}

		return listaBeans;		
	}

	public List <ManutencaoGwtBean> listarManutencaoPendente()
	throws Exception {

		verificarSessaoLogada();
		List<ManutencaoGwtBean> lRet = new ArrayList<ManutencaoGwtBean>(0);

		try {
			lRet = Manutencao.listarManutencaoPendente();
		} catch (Exception ex) {
			throw new Exception("Erro no serviço de manutenção\nao listar as manutenções pendentes.");
		}
		return lRet;
	}

}
