/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/01/2007

  Descricao: Classe de negócio para controle de acesso.

  Historico:

    $Log: Acesso.java,v $
    Revision 1.9  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.7  2008/08/29 20:49:39  fos
    Retirados imports não utilizados.

    Revision 1.6  2008/08/20 13:41:29  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.5  2008/08/12 12:58:31  fos
    Ajustada a variavel de sessão para ficar padrão.

    Revision 1.4  2007/12/14 12:44:11  fernando
    Adptado para usar o id_usuario, e não mais o usuario

    Revision 1.3  2007/03/16 12:56:15  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.model;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Log.TipoLog;

/**
 * Classe de negócio para controle de acesso.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.9 $ $Date: 2009/01/12 12:49:43 $ $Author: fos $
 */
public class Acesso {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private static Logger logger = Logger.getLogger(Acesso.class); 
	/**
	 * Constrói o objeto Acesso com os seus respectivos atributos.
	 * @param request Referência a requisição HTTP
	 * @param response Referência a resposta HTTP
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public Acesso(HttpServletRequest request, HttpServletResponse response) {
		this(request, response, true);
	}
	/**
	 * Constrói o objeto Acesso com os seus respectivos atributos.
	 * @param request Referência a requisição HTTP
	 * @param response Referência a resposta HTTP
	 * @param gravaLog Indica se deve ser gravado log da requisição.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public Acesso(HttpServletRequest request, HttpServletResponse response, Boolean gravaLog) {
		this.request = request;
		this.response = response;
		if (gravaLog) {
			try {
				gravaLog();
			} catch (SQLException e) {
				logger.error("Erro ao gravar log da requisição HTTP", e);
			} catch (ConexaoException e) {
				logger.error("Erro ao gravar log da requisição HTTP", e);
			}
		}
	}
	/**
	 * Grava o log de acesso do usuário;
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	private void gravaLog() throws SQLException, ConexaoException {
		String descricao = this.request.getServletPath();

		Usuario usuario = ((Usuario)request.getSession().getAttribute("[usuario]"));
		Integer idUsuario = usuario != null ? usuario.getId() : null;
		
		String detalhe = this.request.getQueryString();
		
		Log.gravaLog(TipoLog.TIPO_ACESSO, idUsuario, descricao, detalhe);
	}
	/**
	 * Verifica o acesso do usuário, caso este não tenha acesso então o
	 * browser é direcionado para a tela de login.
	 * @return True se o usuário possui acesso ou False caso não possua
	 * @throws IOException
	 * @see #verificaAcesso(boolean)
	 */
	public boolean verificaAcesso() throws IOException {
		return verificaAcesso(true);
	}
	
	private boolean verificaAcesso(String url, Usuario usuario)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		int cnt = 0, iacesso = 0;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT cnt, acesso FROM fcn_VerificaAcesso(?, ?)");
			ps.setString(1, url);
			ps.setInt(2, usuario.getId());
			rs = ps.executeQuery();
			if (rs.next())
			{
				cnt = rs.getInt(1);
				if (cnt > 0)
					iacesso = rs.getInt(2);
			}
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			try 
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {}
		}
		
		return (cnt == 0) || (cnt > 0 && iacesso > 0);
	}
	/**
	 * Verifica o acesso do usuário.
	 * @param gotoLogin Se True o usuário será direcionado para tela de login se esta não possui acesso.
	 * @return True se o usuário possui acesso ou False caso não possua
	 * @throws IOException
	 */
	public boolean verificaAcesso(boolean gotoLogin) throws IOException {
		boolean bRet = false;
		
		Usuario obj_usuario = (Usuario)request.getSession().getAttribute("[usuario]");
		
		if (obj_usuario == null) {
			if (gotoLogin)
				goToLogin();
			bRet = false;
		} 
		else if(!verificaAcesso(request.getRequestURI(), obj_usuario))
		{
			response.sendRedirect("/login/gtw_principal.jsp");
			bRet = false;
		}
		else {
			bRet = true;
		}
		return bRet;
	}
	/**
	 * Direciona o browser cliente para a tela de login.
	 * @throws IOException
	 */
	private void goToLogin() throws IOException {
		String retURL = request.getRequestURL() + (request.getQueryString() != null ? "?"+request.getQueryString() : "");
		try {
			retURL = URLEncoder.encode(retURL, "UTF-8");
		}
		catch (Exception e) {}
		
		// após logar, sempre mande para tela inicial. Alguns processos não funcionam bem com o redirect
		response.sendRedirect("/login/login.jsp?p="+retURL);
	}
	
	public Usuario getUsuario(){

		if( request.getSession().getAttribute("[usuario]") == null)
			return null;
		else
			return ((Usuario)request.getSession().getAttribute("[usuario]"));
			
	}
}
