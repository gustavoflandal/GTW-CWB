/**
 * 
 */
package com.consilux.infra;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Usuario;
import com.consilux.model.exception.ModelException;

/**
 * @author fos
 *
 */
public class LocalWS {
	
	private static Logger logger = Logger.getLogger(LocalWS.class); 

	private Conexao con;
	public LocalWS() {
		try {
			con = Conexao.initConexao();
		} 
		catch (ConexaoException e) {
			logger.error("Erro ao obter conexão", e);
		}
	}
	protected Conexao getCon() {
		return con;
	}

	protected void handleException( Exception e ) throws Exception {

		logger.error("Exceção externa WS.", e);

		if( e.getCause() == null )
			throw e;
		else
			throw e.getClass().getConstructor(String.class).newInstance(( 
					( e.getMessage() == null ? "" : e.getMessage() ) + 
					" [" + 
					( e.getCause().getMessage() == null ? "" : e.getCause().getMessage() ) + 
					"]" ) );
	}
	

	protected boolean verificarUsuarioESenha( String usuario , String senha )
	throws ConexaoException, SQLException, ModelException{

		Usuario usuarioGTW = Usuario.buscaUsuarioPorUsuario( usuario );
		
		if( usuarioGTW == null )
			return false;
		
		boolean usuarioValido = ( usuarioGTW.comparaSenha( senha ) && usuarioGTW.isAtivo() );
		return usuarioValido;

	}
}
