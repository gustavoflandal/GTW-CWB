package com.consilux.ui.server;

import java.util.List;

import org.apache.log4j.Logger;

import com.consilux.model.Grupo;
import com.consilux.model.GrupoEquipamento;
import com.consilux.model.PermissaoMenu;
import com.consilux.model.Usuario;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.ui.client.UsuarioService;
import com.consilux.ui.client.beans.GrupoEquipamentoGwtBean;
import com.consilux.ui.client.beans.GrupoGwtBean;
import com.consilux.ui.client.beans.PermissaoGwtBean;
import com.consilux.ui.client.beans.UsuarioGwtBean;

public class UsuarioServiceImpl extends GwtBaseServlet implements UsuarioService {

	private static final long serialVersionUID = 3349380013186437798L;

	private static Logger logger = Logger.getLogger(UsuarioServiceImpl.class);
	
	@Override
	public List<GrupoGwtBean> listarGruposUsuario(int idUsuario) throws Exception {
		verificarSessaoLogada();

		try {
			return Grupo.toGwtBean(Grupo.buscaGruposPorIdUsuario(idUsuario));
		} catch (Exception ex) {
			logger.error("Erro ao listar os grupos do usuário.", ex);
			throw new Exception(ex.getLocalizedMessage());
		}
			
	}

	@Override
	public List<GrupoGwtBean> listarTodosGrupos() throws Exception {
		verificarSessaoLogada();
		try {
			return Grupo.toGwtBean(Grupo.buscaGruposAbaixo(getIdUsuario()));
		} catch (Exception ex) {
			logger.error("Erro ao listar todos os grupos.", ex);
			throw new Exception(ex.getLocalizedMessage());
		}
			
	}

	
	@Override
	public List<PermissaoGwtBean> listarPermissoesUsuario(int idUsuario) throws Exception {
		verificarSessaoLogada();
		
		try {
			return PermissaoMenu.toGwtBean(PermissaoMenu.buscaPermissoesMenuPorIdUsuario(idUsuario));
		} catch (Exception ex) {
			logger.error("Erro ao listar permissões do usuário.", ex);
			throw new Exception(ex.getLocalizedMessage());
		}
			
	}

	@Override
	public List<GrupoEquipamentoGwtBean> listarTodosGrupoEquipamento() throws Exception {
		verificarSessaoLogada();
		
		try {
			return GrupoEquipamento.toGwtBean(GrupoEquipamento.buscaTodosGrupoEquipamento());
		} catch (Exception ex) {
			logger.error("Erro ao listar todos os grupos de equipamentos.", ex);
			throw new Exception(ex.getLocalizedMessage());
		}
			
	}	
	
	@Override
	public UsuarioGwtBean salvarUsuario(UsuarioGwtBean usuarioSalvar) throws Exception {
		verificarSessaoLogada();
		
		if (usuarioSalvar == null)
			throw new Exception("Erro: Não é possível salvar um usuário nulo.");

		if (usuarioSalvar.getSenha() == null)
			throw new Exception("Erro: Não é possível salvar um usuário sem senha.");
		
		try {
			
			// Se o id é zero, trata-se de um usuário novo. Validar. 
			if (usuarioSalvar.getIdUsuario() == 0)
			{
				Usuario usuarioNoBanco = Usuario.buscaUsuarioPorUsuario(usuarioSalvar.getLogin());
				if (usuarioNoBanco != null)
					throw new Exception("Erro: já existe um usuário com este login.");
			}
			
			Usuario.salvarUsuario(usuarioSalvar, this.getUsuario());
			return usuarioSalvar;
		} catch (Exception ex) {
			logger.error("Erro ao salvar o usuário.", ex);
			throw new Exception(ex.getLocalizedMessage());
		}
	}

	@Override
	public List<UsuarioGwtBean> listarTodosUsuarios() throws Exception {
		verificarSessaoLogada();
		try {
			return Usuario.toGwtBean(Usuario.buscaUsuariosAbaixo(getIdUsuario()));
		} catch (Exception ex) {
			logger.error("Erro ao listar todos os usuários.", ex);
			throw new Exception(ex.getLocalizedMessage());
		}
			
	}

}
