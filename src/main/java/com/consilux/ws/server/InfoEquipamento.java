/**
 * 
 */
package com.consilux.ws.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.LocalWS;
import com.consilux.model.LocalVigente;
import com.consilux.model.beans.LocalVigenteBean;
import com.consilux.model.exception.ModelException;

/**
 * @author fos
 *
 */
public class InfoEquipamento extends LocalWS {

	private static Logger logger = Logger.getLogger(InfoEquipamento.class); 
	public LocalVigenteBean[] getLocaisVigente( String usuario , String senha , Integer idGrupo ) throws Exception {
		LocalVigenteBean[] lRet = null;

		logger.debug("WSCALL getLocaisVigente");

		try {
			
			if( verificarUsuarioESenha(usuario, senha) == false ) 
				throw new ModelException("Usuário e/ou senha inválidos!");
			
			
			Map<String, Object> mFiltros = new HashMap<String, Object>();

			if( idGrupo <= 0 ) 
				idGrupo = ConfiguracaoProvider.getInstance().getIdGrupoEquipamento();
			
			mFiltros.put("grupo", idGrupo);
			
			List<LocalVigente> lv = LocalVigente.buscaLocalVigentePor( mFiltros , 1 );
			List<LocalVigenteBean> l = new ArrayList<LocalVigenteBean>();
			for (LocalVigente localVigente : lv) {
				LocalVigenteBean bean = new LocalVigenteBean();
				localVigente.getToGrupoBean(bean);
				l.add(bean);
			}
			lRet = l.toArray(new LocalVigenteBean[l.size()]);
		}
		catch(Exception e) {
			logger.error("Erro ao buscar os locais vigentes.", e);
			throw e;
		}

		logger.debug("END_WSCALL getLocaisVigente");
		
		return lRet;
	}
}
