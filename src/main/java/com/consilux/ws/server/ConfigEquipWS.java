/**
 * 
 */
package com.consilux.ws.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.consilux.infra.Funcoes;
import com.consilux.infra.LocalWS;
import com.consilux.infra.SerializadorXML;
import com.consilux.model.ConfiguracaoEquipamento;
import com.consilux.model.LocalVigente;
import com.consilux.model.TConfigEquip;
import com.consilux.model.Usuario;
import com.consilux.model.disparador.DisparadorConfigEquipChange;
import com.consilux.model.exception.ModelException;


/**
 * @author fos / fernando de souza (fds)
 *
 */
public class ConfigEquipWS extends LocalWS {

	private static Logger logger = Logger.getLogger(ConfigEquipWS.class); 

	public double buscaDataModificacaoConfigEquip(String usuario , String senha ,Integer idEquipamento) throws Exception {

		double ret = 0;
		Date date = null;

		try {

			if( verificarUsuarioESenha(usuario, senha) == false ) 
				throw new ModelException("Usuário e/ou senha inválidos!");

			Map<String,Object> mFiltros = new HashMap<String, Object>();
			mFiltros.put("l.serie_equipamento", idEquipamento);

			List<LocalVigente> lv = LocalVigente.buscaLocalVigentePor(mFiltros,1);

			if (lv.size() == 0)
				throw new ModelException("Identificador do equipamento inexistente: "+ idEquipamento );

			date = ConfiguracaoEquipamento.buscaDataModificacaoConfigEquip( lv.get(0).getIdConfiguracaEquipamento());

			ret = Funcoes.convertUTCToPascalDate( date );			

		}
		catch(Exception e) {

			handleException( e );

		}

		return ret;

	}

	public String buscaUsuarioConfigEquip ( String usuario , String senha , Integer idEquipamento ) throws Exception {

		String ret = "";

		System.out.println("WSCALL buscaUsuarioConfigEquip: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		try {

			if( verificarUsuarioESenha(usuario, senha) == false ) 
				throw new ModelException("Usuário e/ou senha inválidos!");

			Map<String,Object> mFiltros = new HashMap<String, Object>();
			mFiltros.put("l.serie_equipamento", idEquipamento);

			List<LocalVigente> lv = LocalVigente.buscaLocalVigentePor(mFiltros,1);

			if (lv.size() == 0){
				return "";
			}

			ret = ConfiguracaoEquipamento.buscaUsuarioConfigEquip( lv.get(0).getIdConfiguracaEquipamento());

		}
		catch(Exception e) {

			handleException( e );

		}

		System.out.println("END_WSCALL buscaUsuarioConfigEquip: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		return ret;

	}

	public String buscarConfigEquip( String usuario , String senha , Integer idEquipamento) throws Exception {
		StringBuffer buf = new StringBuffer();

		System.out.println("WSCALL buscarConfigEquip: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		try {

			if( verificarUsuarioESenha(usuario, senha) == false ) 
				throw new ModelException("Usuário e/ou senha inválidos!");

			Map<String,Object> mFiltros = new HashMap<String, Object>();
			mFiltros.put("l.serie_equipamento", idEquipamento);

			List<LocalVigente> lv = LocalVigente.buscaLocalVigentePor(mFiltros,1);

			if (lv.size() == 0){
				return "";
				//throw new ModelException("Identificador do equipamento inexistente: "+idEquipamento);
			}

			TConfigEquip ce = ConfiguracaoEquipamento.buscarConfigEquipPorId( lv.get(0).getIdConfiguracaEquipamento()).getTconfigEqup();
			SerializadorXML ser = new SerializadorXML();
			ser.saveObject(ce, TConfigEquip.NAME_CLASS );
			ser.saveToStringBuffer(buf);

			logger.info("Entregue configEquip ao usuário: ["+usuario+"]");

		}
		catch(Exception e) {

			handleException( e );

		}

		System.out.println("END_WSCALL buscarConfigEquip: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		return buf.toString();
	}

	public void setEmOperacao(String usuario , String senha , Integer idEquipamento, Boolean operando) throws Exception {

		System.out.println("WSCALL setEmOperacao: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		try {

			if( verificarUsuarioESenha(usuario, senha) == false ) 
				throw new ModelException("Usuário e/ou senha inválidos!");

			Map<String,Object> mFiltros = new HashMap<String, Object>();
			mFiltros.put("l.serie_equipamento", idEquipamento);

			List<LocalVigente> lv = LocalVigente.buscaLocalVigentePor(mFiltros,1);

			if (lv.size() == 0)
				throw new ModelException("Identificador do equipamento inexistente: "+idEquipamento);

			ConfiguracaoEquipamento.setEmOperacao(lv.get(0).getIdConfiguracaEquipamento(), operando);
		}
		catch(Exception e) {

			handleException( e );

		}

		System.out.println("END_WSCALL setEmOperacao: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

	}

	public void incluirConfigEquip(String loginUsuario , String senha , String xmlConfigEquip) throws Exception {

		System.out.println("WSCALL incluirConfigEquip: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		try {

			if( verificarUsuarioESenha(loginUsuario, senha) == false ) 
				throw new ModelException("Usuário e/ou senha inválidos!");

			Usuario usuario = Usuario.buscaUsuarioPorUsuario(loginUsuario);

			TConfigEquip ce = TConfigEquip.deserializeConfigEquip(xmlConfigEquip);

			// atualiza com a data do servidor.
			ce.dataHoraConfiguracao = Funcoes.convertUTCToPascalDate( new Date() );  
			ConfiguracaoEquipamento.incluirConfigEquip( ce , usuario.getId() , true );

			notificarCapturaServer( ce.serie );

			logger.info("Gravado novo ConfigEquip: ["+usuario+"]");
		}
		catch(Exception e) {

			handleException( e );

		}

		System.out.println("END_WSCALL incluirConfigEquip: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

	}

	private void notificarCapturaServer(Integer idEquipamento) {

		DisparadorConfigEquipChange disp = new DisparadorConfigEquipChange( idEquipamento );

		disp.disparar();

	}

	public boolean validarUsuario( String usuario , String senha )  throws Exception{

		Boolean bRet = false;

		System.out.println("WSCALL validarUsuario: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		try {

			bRet = verificarUsuarioESenha(usuario, senha);

		} catch (Exception e) {

			handleException( e );

		}

		System.out.println("END_WSCALL validarUsuario: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		return bRet;

	}

}
