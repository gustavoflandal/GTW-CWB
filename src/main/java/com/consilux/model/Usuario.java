/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 11/01/2007

  Descricao: Classe para busca de usuários no BD.

  Historico:

    $Log: Usuario.java,v $
    Revision 1.32  2009/06/05 13:32:19  raoni
    Correções para o bug 376.
    Realiza a criptografia da senha, quando ocorre UPDATE.

    Revision 1.31  2009/05/12 17:29:05  raoni
    Removido DaoException, para um controle mais granular em favor de ConexaoException, SQLException e ModelException. Ajustando blocos de try/catch.

    Revision 1.30  2009/05/12 14:50:45  raoni
    Removido DaoException, para um controle mais granular em favor de ConexaoException, SQLException e ModelException.

    Revision 1.29  2009/05/11 17:30:56  raoni
    Cdiado overload para o método "buscaUsuarioPorIdUsuario", que permite receber uma conexão como parâmetro.

    Revision 1.28  2009/05/08 18:55:29  fos
    Colocado to string para identificar o usuário na sessão.

    Revision 1.27  2009/05/08 18:24:41  raoni
    Adicionado novos métodos e mmodificfados outros para trabalhar sempre com 2 versões (overloades) . Uma permite definir qual é a conexão e a outra já pega direto.

    Revision 1.26  2009/04/24 20:19:23  raoni
    Adicionaod método para salvar usuário (ainda não está completo).

    Revision 1.25  2009/04/22 16:48:36  raoni
    Adicionado método para conversão para beans to GWT.

    Revision 1.24  2009/04/22 14:53:45  fernando
    - removido método verifica senha

    Revision 1.23  2009/04/22 13:36:47  fos
    Criada funNão que criptografa os dados para armazenar a senha do usuário.

    Revision 1.22  2009/04/17 20:39:42  raoni
    Formatação.

    Revision 1.21  2009/04/08 13:48:00  fernando
    - removido as mensagens de erro, evitando duplicidade.

    Revision 1.20  2009/04/06 18:29:34  fernando
    - Autenticação no Web Service

    Revision 1.19  2009/04/03 20:21:04  fernando
    - Adicionado usuario e senha para acessar o WS

    Revision 1.18  2009/03/31 20:29:26  raoni
    Adicionado método para busca de usuários (técnicos) para o cadastro de manutenção.

    Revision 1.17  2009/03/12 13:07:38  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.16  2009/03/10 15:02:45  raoni
    Otimizado o uso de StringBuilder.
    Utiliza LinkedList quando possível.
    Adicionado lógica para realizar o clean up (close) dos Statements.

    Revision 1.15  2009/03/03 21:36:39  fos
    Consertado ajuste de senha que não estava funcionando.

    Revision 1.14  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.12  2008/08/21 21:10:48  fos
    Alterada nome de tabelas e campos no BD.

    Revision 1.11  2008/08/12 13:02:23  fos
    Agora busca o id do grupo de equipamentos do usuário.

    Revision 1.10  2008/07/23 14:28:08  fos
    Ajustado o nome da função.

    Revision 1.9  2008/01/15 14:22:45  fos
    Agora grabalha com a edição de beans.

    Revision 1.8  2007/12/17 20:07:30  fos
    Agora realiza a inclusão do usuário.

    Revision 1.7  2007/12/14 17:23:50  fos
    Consertado nomenclatura do atributo alterarSenha.

    Revision 1.6  2007/12/14 12:46:09  fernando
    classe usuario contêm o id_usuario agora

    Revision 1.5  2007/12/13 14:38:42  fernando
    Adptado Usuario para nova estrutura do banco de dados.

    Revision 1.4  2007/07/06 13:04:54  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.3  2007/03/16 12:56:15  fos
    Ajustes para documentação.


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.EventoCSX.TipoEvento;
import com.consilux.model.beans.GrupoUsuarioBean;
import com.consilux.model.beans.PermissaoMenuBean;
import com.consilux.model.beans.UsuarioBean;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.GrupoGwtBean;
import com.consilux.ui.client.beans.PermissaoGwtBean;
import com.consilux.ui.client.beans.UsuarioGwtBean;


/**
 * Classe para busca de usuários no BD.
 * @author Fernando Oliveira da Silva / Fernando de Souza - Consilux Tecnologia
 * @version $Revision: 1.32 $ $Date: 2009/06/05 13:32:19 $ $Author: raoni $
 */
public class Usuario {
	
	private static List<UsuarioListener> listaListeners =
		new ArrayList<UsuarioListener>();
	
	//private static final String CHAVE_CRIPT = "CSXGTW";
	
	private Integer id;
	private String usuario;
	private String nome;
	private String senha;
	private String email;
	private Boolean alterarSenha;
	private Boolean ativo;
	private Integer idGrupoEquipamentoConfig;
	private Integer codigoAgente;
	private String agenteUF;
	private String telefone;
	
	/**
	 * @param bean Bean que possui os atributos do usuário.
	 */
	private Usuario(UsuarioBean bean) {
		this.id = bean.getId();
		this.usuario = bean.getUsuario();
		this.nome = bean.getNome();
		setSenha(bean.getSenha());
		this.email = bean.getEmail();
		this.alterarSenha = bean.isAlterarSenha();
		this.ativo = bean.isAtivo();
		this.idGrupoEquipamentoConfig = bean.getIdGrupoEquipamentoConfig();
		this.codigoAgente = bean.getCodigoAgente();
		this.agenteUF = bean.getAgenteUF();
	}

	/**
	 * Constroi o Objeto com seus respectivos atribultos
	 * @param id Identificador do usuário
	 * @param usuario Login do usuário
	 * @param nome Nome completo do usuário
	 * @param senha Senha do Login
	 * @param email E-mail do usuário
	 * @param alterarSenha Se a proxima vez que for executar login, deve-se trocar a senha
	 * @param ativo Indica se o usuário esta ou não ativo
	 * @param idGrupoEquipamentoConfig Identificador do grupo de equipamentos que este usuário pode acessar.
	 */
	private Usuario(Integer id, String usuario, String nome, String senha, String email,
			Boolean alterarSenha, Boolean ativo, Integer idGrupoEquipamentoConfig, Integer codigoAgente, String agenteUF) {
		super();
		this.id = id;
		this.usuario = usuario != null ? usuario.trim() : null;
		this.nome = nome != null ? nome.trim() : null;
		this.senha = senha != null ? senha.trim() : null;
		this.email = email != null ? email.trim() : null;
		this.alterarSenha = alterarSenha;
		this.ativo = ativo;
		this.idGrupoEquipamentoConfig = idGrupoEquipamentoConfig;
		this.codigoAgente = codigoAgente;
		this.agenteUF = agenteUF;
	}
	
	/**
	 * @return Retorna usuários do CAI.
	 */
	private Usuario(Integer idUsuario, String usuario, String nome, String email) {
		super();
		this.id = idUsuario;
		this.usuario = usuario != null ? usuario.trim() : null;
		this.nome = nome != null ? nome.trim() : null;
		this.email = email != null ? email.trim() : null;
		this.telefone = telefone != null ? telefone.trim() : null;
	}
	
	/**
	 * @return Retorna auditores do CAV.
	 */
	private Usuario(Integer idUsuario, String usuario, String nome, String email, Integer codigoAgente, String agenteUF) {
		super();
		this.id = idUsuario;
		this.usuario = usuario != null ? usuario.trim() : null;
		this.nome = nome != null ? nome.trim() : null;
		this.email = email != null ? email.trim() : null;
		this.codigoAgente = codigoAgente;
		this.agenteUF = agenteUF != null ? agenteUF.trim() : null;
	}
	
	public Usuario(Integer id, String usuario, String nome, String senha, String email,
            Boolean alterarSenha, Boolean ativo, Integer idGrupoEquipamentoConfig,
            Integer codigoAgente, String agenteUF, String telefone) {
				 this.id = id;
				 this.usuario = usuario != null ? usuario.trim() : null;
				 this.nome = nome != null ? nome.trim() : null;
				 this.senha = senha != null ? senha.trim() : null;
				 this.email = email != null ? email.trim() : null;
				 this.alterarSenha = alterarSenha;
				 this.ativo = ativo;
				 this.idGrupoEquipamentoConfig = idGrupoEquipamentoConfig;
				 this.codigoAgente = codigoAgente;
				 this.agenteUF = agenteUF != null ? agenteUF.trim() : null;
				 this.telefone = telefone != null ? telefone.trim() : null;
			}

	
	/**
	 * Insere um novo registro de usuario.
	 * @param conn Uma conexão (aberta) com o banco.
	 * @param usuarioBean Um bean que possui os atributos do usuário.
	 * @return Objeto Usuario materializado.
	 * @throws SQLException 
	 * @throws ModelException
	 */
	private static Usuario incluiUsuario(Connection conn, UsuarioBean usuarioBean) throws SQLException, ModelException
	{
		
		Usuario uRet = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO sis_usuario ( ");
		sbSQL.append("		usuario, ");
		sbSQL.append("		nome, ");
		sbSQL.append("		email, ");
		sbSQL.append("		alterar_senha, ");
		sbSQL.append("		ativo, ");
		sbSQL.append("		id_grupo_equipamento, ");
		sbSQL.append("		senha, ");
		sbSQL.append("		cod_agente, ");
		sbSQL.append("		uf_agente ");
		sbSQL.append("	) VALUES (?,?,?,?,?,?,?,?,?)");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String tmpString;
		
		ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);
		//Ajustando os valores dos parametros:
		
		tmpString = usuarioBean.getUsuario();
		if (tmpString != null && tmpString.length() > 0)
			tmpString = tmpString.trim();
		ps.setString(1, tmpString);
		
		tmpString = usuarioBean.getNome();
		if (tmpString != null && tmpString.length() > 0)
			tmpString = tmpString.trim();
		ps.setString(2, tmpString);
		
		tmpString = usuarioBean.getEmail();
		if (tmpString != null && tmpString.length() > 0)
			tmpString = tmpString.trim();
		ps.setString(3, tmpString);
		
		ps.setBoolean(4, usuarioBean.isAlterarSenha());
		ps.setBoolean(5, usuarioBean.isAtivo());
		ps.setInt(6, usuarioBean.getIdGrupoEquipamentoConfig());
		
		ps.setString(7, new String(Funcoes.geraMD5(usuarioBean.getSenha().getBytes())));

		if (usuarioBean.getCodigoAgente() != null) {
			ps.setInt(8,  usuarioBean.getCodigoAgente());
		} else {
			ps.setNull(8, Types.INTEGER);
		}
		
		tmpString = usuarioBean.getAgenteUF();
		if (tmpString != null && tmpString.length() > 0 && usuarioBean.getCodigoAgente() != null){
			tmpString = tmpString.trim();
			ps.setString(9, tmpString);
		}else{
			ps.setNull(9, Types.VARCHAR);
		}
		
		
		if (ps.executeUpdate() > 0) {
			// Pegando o identity.
			rs = ps.getGeneratedKeys();
			rs.next();
			usuarioBean.setId(rs.getInt(1));
			uRet = new Usuario(usuarioBean);
		}
		

		return uRet;
	}

	/**
	 * Insere um novo registro de usuario.
	 * @param usuarioBean Um bean que possui os atributos do usuário.
	 * @return Objeto Usuario materializado.
	 * @throws SQLException 
	 * @throws ConexaoException
	 * @throws ModelException 
	 */
	public static Usuario incluiUsuario(UsuarioBean usuarioBean) 
	throws SQLException, ModelException, ConexaoException	{

		Usuario ret = null;
		
		Connection conn = Conexao.getConexao();
		conn.setAutoCommit(false);
 

		try {
			ret = incluiUsuario(conn, usuarioBean);
			conn.commit();
			return ret;
		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		} catch (ModelException ex) {
			conn.rollback();
			throw ex;			
		} catch (Exception ex) {
			conn.rollback();
			throw new ModelException("Erro não esperado.", ex);		
		}					
		finally {
			conn.close();
		}		
	}

	public void alteraUsuario() throws ConexaoException, SQLException, ModelException  {
		Connection conn = Conexao.getConexao();
		try {
			alteraUsuario(conn);
		}
		catch (Exception ex) {
			conn.close();
		}
	}

	public void alteraUsuario(Connection conn) throws ConexaoException, SQLException, ModelException  {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("UPDATE sis_usuario SET ");
		sbSQL.append("		usuario=?, ");
		sbSQL.append("		nome=?, ");
		sbSQL.append("		email=?, ");
		sbSQL.append("		alterar_senha=?, ");
		sbSQL.append("		ativo=?, ");
		sbSQL.append("		id_grupo_equipamento=?, ");
		sbSQL.append("		senha=?, ");
		sbSQL.append("		cod_agente=?, ");
		sbSQL.append("		uf_agente=? ");
		
		sbSQL.append(" WHERE id_usuario=?");
		PreparedStatement ps = null;

		ps = conn.prepareStatement(sbSQL.toString());

		ps.setString(1, this.usuario);
		ps.setString(2, this.nome);
		ps.setString(3, this.email);
		ps.setBoolean(4, this.alterarSenha);
		ps.setBoolean(5, this.ativo);
		ps.setInt(6, this.idGrupoEquipamentoConfig);
		ps.setString(7, this.senha);

		if (this.codigoAgente != null) {
			ps.setInt(8, this.codigoAgente);
		} else {
			ps.setNull(8, Types.INTEGER);
		}
		

		String tmpString = this.agenteUF;
		if (tmpString != null && tmpString.length() > 0 && this.codigoAgente != null){
			tmpString = tmpString.trim();
			ps.setString(9, tmpString);
		}else{
			ps.setNull(9, Types.VARCHAR);
		}
		
		
		ps.setInt(10, this.id);
		
		ps.executeUpdate();
	}
	
	/**
	 * Remove um registro de usuario.
	 * @throws ConexaoException
	 * @throws ModelException 
	 * @throws SQLException 
	 * @throws SQLException
	 */
	public void removeUsuario() throws ConexaoException, ModelException, SQLException {

		Connection conn = Conexao.getConexao();
		conn.setAutoCommit(false);

		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("DELETE FROM sis_usuario ");
		sbSQL.append("WHERE id_usuario=?");
		
		try {
			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());	
			ps.setInt(1, this.id);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} catch (Exception ex) {
			conn.rollback();
			throw new ModelException("Erro não esperado.", ex);		
		}
		finally {
			conn.close();
		}
	}

	/**
	 * Busca usuários no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: nome.
	 * @return Lista de objetos Usuario
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static List<Usuario> buscaUsuarioPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException {
		List<Usuario> lRet = new ArrayList<Usuario>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT id_usuario,");
		sbSQL.append("		usuario,");
		sbSQL.append("		nome,");
		sbSQL.append("		senha,");
		sbSQL.append("		email," );
		sbSQL.append("		alterar_senha,");
		sbSQL.append("		ativo,");
		sbSQL.append("		id_grupo_equipamento, ");
		sbSQL.append("		cod_agente, ");
		sbSQL.append("		uf_agente ");
		
		sbSQL.append("	FROM");
		sbSQL.append("		sis_usuario WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");

		//Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("nome", "nome LIKE ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			//Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
	
			rs = ps.executeQuery();
			while (rs.next()) {
				
				Integer codAgente = rs.getInt("cod_agente");
				if (rs.wasNull())
					codAgente = null;
				
				lRet.add(new Usuario(
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getString("nome"),
						rs.getString("senha"),
						rs.getString("email"),
						rs.getBoolean("alterar_senha"),
						rs.getBoolean("ativo"),
						rs.getInt("id_grupo_equipamento"),
						codAgente, 
						rs.getString("uf_agente")
					));
			}
		} catch (ModelException e) {
			throw new SQLException("Erro ao montar SQL.", e);
		}				
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		
		return lRet;
	}


	public static boolean usuarioPertenceAoGrupo(int id_usuario , int id_grupo) throws ConexaoException, SQLException {

		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT TOP 1 id_usuario ");
		sbSQL.append(" FROM ");
		sbSQL.append("		sis_usuario_grupo WITH (NOLOCK) ");
		sbSQL.append(" WHERE ");
		sbSQL.append("		id_usuario = ? "); 
		sbSQL.append("		AND id_grupo = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setInt( 1, id_usuario );
			ps.setInt( 2, id_grupo );
			
			rs = ps.executeQuery();
			if (rs.next())
				bRet = rs.getInt(1) > 0;
		}
		finally {
			if (conn != null)
				conn.close();
		}
		
		return bRet;
	}	
	
	/**
	 * Busca um usuário do BD por id.
	 * @param idUsuario Identificador do usuário
	 * @return Objeto Usuário materializado.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public static Usuario buscaUsuarioPorIdUsuario(int idUsuario)
	throws ConexaoException, SQLException
	{
		
		Connection conn = Conexao.getConexao();
		try {
			return buscaUsuarioPorIdUsuario(conn, idUsuario);
		}
		finally {
			conn.close();
		}
	}
	
	public static String buscaUsuarioImportacaoPorIdUsuario(int idUsuario) {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String usuario = null;
		String sbSQL = "SELECT nome FROM sis_usuario_importacao (NOLOCK) WHERE id_usuario = ?";
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL);
			ps.setInt(1, idUsuario);
			rs = ps.executeQuery();
			if (rs.next())
				usuario = rs.getString("nome");
		} 
		catch(Exception e) {}
		finally {
			try {
				if(rs != null)
					rs.close();
				if(ps != null)
					ps.close();
				if(conn != null)
					conn.close();
			}catch(Exception e) {}
		}
		
		return usuario;
		
	}
	/**
	 * Busca um usuário do BD por id.
	 * @param conn Conexão com o banco de dados.
	 * @param idUsuario Identificador do usuário
	 * @return Objeto Usuário materializado.
	 * @throws SQLException 
	 */
	public static Usuario buscaUsuarioPorIdUsuario(Connection conn, int idUsuario)
	throws SQLException
	{
		
		Usuario uRet = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("   SELECT ");
		sbSQL.append("		id_usuario,");
		sbSQL.append("		usuario,");
		sbSQL.append("		nome,");
		sbSQL.append("		senha,");
		sbSQL.append("		email,");
		sbSQL.append("		alterar_senha,");
		sbSQL.append("		ativo,");
		sbSQL.append("		id_grupo_equipamento, ");
		sbSQL.append("		cod_agente, ");
		sbSQL.append("		uf_agente ");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_usuario WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_usuario = ?");

		PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
		ps.setInt(1, idUsuario);

		rs = ps.executeQuery();
		if (rs.next()) {
			
			Integer codAgente = rs.getInt("cod_agente");
			if (rs.wasNull())
				codAgente = null;
				
			uRet =  new Usuario(
					rs.getInt("id_usuario"),
					rs.getString("usuario"),
					rs.getString("nome"),
					rs.getString("senha"),
					rs.getString("email"),
					rs.getBoolean("alterar_senha"),
					rs.getBoolean("ativo"),
					rs.getInt("id_grupo_equipamento"),
					codAgente, 
					rs.getString("uf_agente")
			);			
		}

		return uRet;
	}
	
	public static List<UsuarioBean> buscaUsuariosAbaixo(Integer idUsuario) 
	throws ConexaoException, ModelException, SQLException {
		
		List<UsuarioBean> lRet = new ArrayList<UsuarioBean>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("   SELECT ");
		sbSQL.append("		id_usuario, ");
		sbSQL.append("		usuario, ");
		sbSQL.append("		nome, ");
		sbSQL.append("		senha, ");
		sbSQL.append("		email, ");
		sbSQL.append("		alterar_senha, ");
		sbSQL.append("		ativo, ");
		sbSQL.append("		id_grupo_equipamento, ");
		sbSQL.append("		cod_agente, ");
		sbSQL.append("		uf_agente ");
		sbSQL.append("	FROM");
		sbSQL.append("		fcn_getUsuariosAbaixo(?)");
		sbSQL.append("	ORDER BY usuario");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);
			rs = ps.executeQuery();
			UsuarioBean usuarioBean;
			
			String tmpString;
			Integer tmpInteger;
			while (rs.next()) {
				usuarioBean = new UsuarioBean();
				usuarioBean.setId(rs.getInt("id_usuario"));
				
				tmpString = rs.getString("usuario");
				if (tmpString != null && tmpString.length() > 0)
					tmpString = tmpString.trim();
				usuarioBean.setUsuario(tmpString);
				
				tmpString = rs.getString("nome");
				if (tmpString != null && tmpString.length() > 0)
					tmpString = tmpString.trim();
				usuarioBean.setNome(tmpString);
				
				// Não pega a senha do banco.
				//usuarioBean.setSenha(Usuario.SEM_SENHA);
				usuarioBean.setSenha("");
				
				tmpString = rs.getString("email");
				if (tmpString != null && tmpString.length() > 0)
					tmpString = tmpString.trim();
				usuarioBean.setEmail(tmpString);
				
				usuarioBean.setAlterarSenha(rs.getBoolean("alterar_senha"));
				usuarioBean.setAtivo(rs.getBoolean("ativo"));
				usuarioBean.setIdGrupoEquipamentoConfig(rs.getInt("id_grupo_equipamento"));
				usuarioBean.setAgenteUF(rs.getString("uf_agente"));
				
				tmpInteger = rs.getInt("cod_agente"); 
				if (rs.wasNull())
					tmpInteger = null;
				
				usuarioBean.setCodigoAgente(tmpInteger);
				
				lRet.add(usuarioBean);
			}
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return lRet;		
	}

	/**
	 * Lista todos os usuarios do BD.
	 * @return Lista com beans de usuário materializados.
	 * @throws ConexaoException
	 * @throws ModelException 
	 * @throws SQLException 
	 */
	public static List<UsuarioBean> listarUsuarios()
	throws ConexaoException, ModelException, SQLException {
		
		List<UsuarioBean> lRet = new ArrayList<UsuarioBean>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("   SELECT ");
		sbSQL.append("		id_usuario, ");
		sbSQL.append("		usuario, ");
		sbSQL.append("		nome, ");
		sbSQL.append("		senha, ");
		sbSQL.append("		email, ");
		sbSQL.append("		alterar_senha, ");
		sbSQL.append("		ativo, ");
		sbSQL.append("		id_grupo_equipamento, ");
		sbSQL.append("		cod_agente, ");
		sbSQL.append("		uf_agente ");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_usuario WITH (NOLOCK) ");
		sbSQL.append("	ORDER BY usuario");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			UsuarioBean usuarioBean;
			
			String tmpString;
			Integer tmpInteger;
			while (rs.next()) {
				usuarioBean = new UsuarioBean();
				usuarioBean.setId(rs.getInt("id_usuario"));
				
				tmpString = rs.getString("usuario");
				if (tmpString != null && tmpString.length() > 0)
					tmpString = tmpString.trim();
				usuarioBean.setUsuario(tmpString);
				
				tmpString = rs.getString("nome");
				if (tmpString != null && tmpString.length() > 0)
					tmpString = tmpString.trim();
				usuarioBean.setNome(tmpString);
				
				// Não pega a senha do banco.
				//usuarioBean.setSenha(Usuario.SEM_SENHA);
				usuarioBean.setSenha("");
				
				tmpString = rs.getString("email");
				if (tmpString != null && tmpString.length() > 0)
					tmpString = tmpString.trim();
				usuarioBean.setEmail(tmpString);
				
				usuarioBean.setAlterarSenha(rs.getBoolean("alterar_senha"));
				usuarioBean.setAtivo(rs.getBoolean("ativo"));
				usuarioBean.setIdGrupoEquipamentoConfig(rs.getInt("id_grupo_equipamento"));
				usuarioBean.setAgenteUF(rs.getString("uf_agente"));
				
				tmpInteger = rs.getInt("cod_agente"); 
				if (rs.wasNull())
					tmpInteger = null;
				
				usuarioBean.setCodigoAgente(tmpInteger);
				
				lRet.add(usuarioBean);
			}
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return lRet;
	}	


	/**
	 * Lista todos os usuarios do CAI.
	 * @return Lista com beans de usuário materializados.
	 * @throws ConexaoException
	 * @throws ModelException 
	 * @throws SQLException 
	 */
	public static List<Usuario> listarUsuariosCAI() throws ConexaoException, SQLException {
		
		List<Usuario> lRet = new ArrayList<Usuario>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT sui.id_usuario ");
		sbSQL.append("		 ,LTRIM(RTRIM(sui.usuario)) AS usuario ");
		sbSQL.append("		 ,LTRIM(RTRIM(sui.nome)) AS nome ");
		sbSQL.append("		 ,LTRIM(RTRIM(sui.email)) AS email ");
		sbSQL.append("		 ,sui.id_usuario_local ");
		sbSQL.append(" FROM   sis_usuario_importacao sui (NOLOCK) ");
		sbSQL.append(" 		  INNER JOIN ( ");
		sbSQL.append(" 						SELECT mi.cod_operador ");
		sbSQL.append(" 						FROM   movimento_importacao mi (NOLOCK) ");
		sbSQL.append(" 						GROUP BY ");
		sbSQL.append(" 							   mi.cod_operador ");
		sbSQL.append(" 		  ) AS usuario_cai ");
		sbSQL.append(" 				ON  usuario_cai.cod_operador = sui.id_usuario ");
		sbSQL.append(" GROUP BY ");
		sbSQL.append("		  sui.id_usuario ");
		sbSQL.append("		 ,LTRIM(RTRIM(sui.usuario)) ");
		sbSQL.append("		 ,LTRIM(RTRIM(sui.nome)) ");
		sbSQL.append("		 ,LTRIM(RTRIM(sui.email)) ");
		sbSQL.append("		 ,sui.id_usuario_local ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("		  LTRIM(RTRIM(sui.nome)) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Usuario usuario;

		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				
				usuario =  new Usuario(rs.getInt("id_usuario"),
									   rs.getString("usuario"),
									   rs.getString("nome"),
									   rs.getString("email")
				);
				
				lRet.add(usuario);
			}
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return lRet;
	}
	
	/**
	 * Lista todos os auditores do CAV.
	 * @return Lista com beans de usuário materializados.
	 * @throws ConexaoException
	 * @throws ModelException 
	 * @throws SQLException 
	 */
	public static List<Usuario> listarAuditoresCAV() throws ConexaoException, SQLException {
		
		List<Usuario> lRet = new ArrayList<Usuario>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT su.id_usuario ");
		sbSQL.append("		 ,LTRIM(RTRIM(su.usuario)) AS usuario ");
		sbSQL.append("		 ,LTRIM(RTRIM(su.nome)) AS nome ");
		sbSQL.append("		 ,LTRIM(RTRIM(su.email)) AS email ");
		sbSQL.append("		 ,cod_agente ");
		sbSQL.append("		 ,uf_agente ");
		sbSQL.append(" FROM   sis_usuario su (NOLOCK) ");
		sbSQL.append(" WHERE  su.cod_agente IS NOT NULL ");
		sbSQL.append(" GROUP BY ");
		sbSQL.append("		  su.id_usuario ");
		sbSQL.append("		 ,LTRIM(RTRIM(su.usuario)) ");
		sbSQL.append("		 ,LTRIM(RTRIM(su.nome)) ");
		sbSQL.append("		 ,LTRIM(RTRIM(su.email)) ");
		sbSQL.append("		 ,cod_agente ");
		sbSQL.append("		 ,uf_agente ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	      LTRIM(RTRIM(su.nome)) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Usuario usuario;

		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				
				usuario =  new Usuario(rs.getInt("id_usuario"),
									   rs.getString("usuario"),
									   rs.getString("nome"),
									   rs.getString("email"),
									   rs.getInt("cod_agente"),
									   rs.getString("uf_agente")
				);
				
				lRet.add(usuario);
			}
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return lRet;
	}	
	
	
	/**
	 * Busca um usuário do BD pelo usuário.
	 * @param usuario Representa o usuário (USERNAME)
	 * @return Objeto Usuário materializado.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Usuario buscaUsuarioPorUsuario(String usuario)
	throws ConexaoException, SQLException {
		Usuario uRet = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("   SELECT ");
		sbSQL.append("		id_usuario,");
		sbSQL.append("		usuario,");
		sbSQL.append("		nome,");
		sbSQL.append("		senha,");
		sbSQL.append("		email,");
		sbSQL.append("		alterar_senha,");
		sbSQL.append("		ativo,");
		sbSQL.append("		id_grupo_equipamento, ");
		sbSQL.append("		cod_agente, ");
		sbSQL.append("		uf_agente ");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_usuario WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		usuario = ?");
		sbSQL.append("		AND ativo = 1");
		sbSQL.append("	ORDER BY ");
		sbSQL.append("		nome");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, usuario);

			rs = ps.executeQuery();
			if (rs.next()) {
				
				Integer tmpInteger = rs.getInt("cod_agente");
				if (rs.wasNull())
					tmpInteger = null;
				
				uRet =  new Usuario(
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getString("nome"),
						rs.getString("senha"),
						rs.getString("email"),
						rs.getBoolean("alterar_senha"),
						rs.getBoolean("ativo"),
						rs.getInt("id_grupo_equipamento"),
						tmpInteger, 
						rs.getString("uf_agente")
				);			
			}
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return uRet;
	}	

	/**
	 * Busca um usuário do BD por nome de usuário.
	 * @param nomeUsuario Representa o nome do usuário.
	 * @return Objeto Usuário materializado.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Usuario buscaUsuarioPorNome(String nomeUsuario)
	throws ConexaoException, SQLException {
		Usuario uRet = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("   SELECT ");
		sbSQL.append("		id_usuario,");
		sbSQL.append("		usuario,");
		sbSQL.append("		nome,");
		sbSQL.append("		senha,");
		sbSQL.append("		email,");
		sbSQL.append("		alterar_senha,");
		sbSQL.append("		ativo,");
		sbSQL.append("		id_grupo_equipamento, ");
		sbSQL.append("		cod_agente, ");
		sbSQL.append("		uf_agente ");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_usuario WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		nome = ?");
		sbSQL.append("		AND ativo = 1");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, nomeUsuario);

			rs = ps.executeQuery();
			if (rs.next()) {
				
				Integer tmpInteger = rs.getInt("cod_agente");
				if (rs.wasNull())
					tmpInteger = null;				
				
				uRet =  new Usuario(
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getString("nome"),
						rs.getString("senha"),
						rs.getString("email"),
						rs.getBoolean("alterar_senha"),
						rs.getBoolean("ativo"),
						rs.getInt("id_grupo_equipamento"),
						tmpInteger, 
						rs.getString("uf_agente")
				);			
			}
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return uRet;
	}
	
	/**
	 * Busca um usuário do BD por nome de usuário.
	 * @param nomeUsuario Representa o nome do usuário.
	 * @return Objeto Usuário materializado.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Usuario> buscaUsuarioPorGrupo(int id_grupo)
	throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("   SELECT \n");
		sbSQL.append("		u.id_usuario, \n");
		sbSQL.append("		u.usuario, \n");
		sbSQL.append("		u.nome, \n");
		sbSQL.append("		u.senha, \n");
		sbSQL.append("		u.email, \n");
		sbSQL.append("		u.alterar_senha, \n");
		sbSQL.append("		u.ativo, \n");
		sbSQL.append("		u.id_grupo_equipamento, \n");
		sbSQL.append("		u.cod_agente, \n");
		sbSQL.append("		u.uf_agente, \n");
		sbSQL.append("		u.telefone \n");
		sbSQL.append("	FROM \n");
		sbSQL.append("		sis_usuario u WITH (NOLOCK) \n");
		sbSQL.append("		JOIN sis_usuario_grupo ug ON ug.id_usuario = u.id_usuario \n");
		sbSQL.append("		JOIN sis_grupo g ON g.id_grupo = ug.id_grupo \n");
		sbSQL.append("	WHERE \n");
		sbSQL.append("		g.id_grupo = ? \n");
		System.out.println(sbSQL.toString());
		System.out.println("ID_GRUPO => "+id_grupo);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Usuario> lista;
		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id_grupo);

			rs = ps.executeQuery();
			lista = new ArrayList<Usuario>();
			while (rs.next()) {
				
				Integer tmpInteger = rs.getInt("cod_agente");
				if (rs.wasNull())
					tmpInteger = null;				
				
				lista.add(new Usuario(
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getString("nome"),
						rs.getString("senha"),
						rs.getString("email"),
						rs.getBoolean("alterar_senha"),
						rs.getBoolean("ativo"),
						rs.getInt("id_grupo_equipamento"),
						tmpInteger, 
						rs.getString("uf_agente"),
						rs.getString("telefone"))
				);			
			}
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return lista;
	}	

	/**
	 * @param usuario Login do usuário a ser validado (USERNAME)
	 * @param senha Senha para ser validada
	 * @return Retorna se o usuário e a senha estão corretos para um usuário ativo
	 * @throws ConexaoException
	 * @throws SQLException 
	 * @throws ModelException 
	 */

 	public static boolean verificarUsuarioESenha( String usuario , String senha )
 	throws ConexaoException, SQLException, ModelException{

		Usuario usuarioGTW = buscaUsuarioPorUsuario(usuario);
		return usuarioGTW.comparaSenha( senha ) && ( usuarioGTW.isAtivo() );

	}	

	/**
	 * Preenche um bean com os dados deste usuário.
	 * @param bean Objeto bean que será preenchido.
	 */
	public void getToUsuarioBean(UsuarioBean bean) {
		bean.setId(this.id);
		bean.setUsuario(this.usuario);
		bean.setNome(this.nome);
		bean.setSenha("");
		bean.setEmail(this.email);
		bean.setAlterarSenha(this.alterarSenha);
		bean.setAtivo(this.ativo);
		bean.setIdGrupoEquipamentoConfig(this.idGrupoEquipamentoConfig);
		bean.setAgenteUF(this.agenteUF);
		bean.setCodigoAgente(this.codigoAgente);
	}

	/**
	 * Preenche os dados deste usuário vindos de um bean.
	 * @param bean Objeto bean que contêm as informaçães.
	 */
	public void setFromUsuarioBean(UsuarioBean bean) {
		this.usuario = bean.getUsuario();
		this.nome = bean.getNome();
		if (bean.getSenha().length() > 0)
			this.setSenha(bean.getSenha());
		this.email = bean.getEmail();
		this.alterarSenha = bean.isAlterarSenha();
		this.ativo = bean.isAtivo();
		this.idGrupoEquipamentoConfig = bean.getIdGrupoEquipamentoConfig();
		this.agenteUF = bean.getAgenteUF();
		this.codigoAgente = bean.getCodigoAgente();
	}

	/**
	 * Compara uma senha externa com a senha criptogravada no objeto
	 * @param sSenha Senha externa
	 * @return True se é igual, False se é diferente
	 * @throws ModelException 
	 */
	public boolean comparaSenha(String sSenha) throws ModelException {
		String sSenhaCript = null;
		
		sSenhaCript = new String(Funcoes.geraMD5(sSenha.getBytes()));
		
		return sSenhaCript.compareTo(this.senha) == 0;
	}

	/**
	 * Retorna o identificador do usuário.
	 * @return Id - Identificador do usuário.
	 */
	public Integer getId() {
		return id;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getNome() {
		return nome;
	}

	public void setSenha(String senha) {
		this.senha = new String(Funcoes.geraMD5(senha.getBytes()));
	}

	public String getEmail() {
		return email;
	}

	public boolean isAlterarSenha() {
		return alterarSenha;
	}

	public void setAlterarSenha(Boolean alterarSenha) {
		this.alterarSenha = alterarSenha;
	}

	public boolean isAtivo() {
		return ativo;
	}

	/**
	 * @return Retorna o valor de idGrupoEquipamentoConfig atual.
	 */
	public Integer getIdGrupoEquipamentoConfig() {
		return idGrupoEquipamentoConfig;
	}

	public Integer getCodigoAgente() {
		return codigoAgente;
	}

	public UsuarioBean toUsuarioBean() {
		UsuarioBean ret = new UsuarioBean();
		
		ret.setAlterarSenha(this.alterarSenha);
		ret.setAtivo(this.ativo);
		ret.setEmail(this.email);
		ret.setId(this.id);
		ret.setIdGrupoEquipamentoConfig(this.idGrupoEquipamentoConfig);
		ret.setNome(this.nome);
		ret.setSenha("");
		ret.setUsuario(this.usuario);
		ret.setAgenteUF(this.agenteUF);
		ret.setCodigoAgente(this.codigoAgente);
		
		return ret;
	}
	
	/**
	 * Converte um usuario em um bean do GWT.
	 * @param usuario O usuario que se deseja converter.
	 * @return Um bean de usuário do GWT.
	 * @throws ModelException caso o usuario passado seja nulo.
	 * @throws SQLException 
	 */
	public static UsuarioGwtBean toGwtBean(UsuarioBean usuario)
	throws ModelException, ConexaoException, SQLException {

		if (usuario == null)
			throw new ModelException("Argumento nulo: usuario");

		UsuarioGwtBean gwtBean = new UsuarioGwtBean();
		gwtBean.setAlterarSenha(usuario.isAlterarSenha());
		gwtBean.setAtivo(usuario.isAtivo());
		gwtBean.setEmail(usuario.getEmail());
		gwtBean.setIdGrupoEquipamento(usuario.getIdGrupoEquipamentoConfig());
		gwtBean.setIdUsuario(usuario.getId());
		gwtBean.setLogin(usuario.getUsuario());
		gwtBean.setNome(usuario.getNome());
		gwtBean.setSenha(usuario.getSenha());
		gwtBean.setUfAgente(usuario.getAgenteUF());
		gwtBean.setCodigoAgente(usuario.getCodigoAgente());

		int idUsuario = usuario.getId();
		gwtBean.setPermissoes(PermissaoMenu.toGwtBean(
				PermissaoMenu.buscaPermissoesMenuPorIdUsuario(idUsuario)));

		gwtBean.setGrupos(Grupo.toGwtBean(
				Grupo.buscaGruposPorIdUsuario(idUsuario)));
		return gwtBean;
	}

	
	/**
	 * Converte uma coleção de beans do GTW para uma lista de bean do GWT.
	 * @param listaUsuarios Uma coleção de beans de usuário (normais).
	 * @return Uma lista de bean de usuário (do GWT).
	 * @throws ModelException caso o argumento passado seja nulo.
	 * @throws ConexaoException caso ocorram erros de conexão.
	 * @throws SQLException caso ocorram erros de SQL.
	 */		
	public static List<UsuarioGwtBean> toGwtBean(Iterable<UsuarioBean> listaUsuarios)
	throws ModelException, ConexaoException, SQLException
	{
		if (listaUsuarios == null)
			throw new ModelException("Argumento nulo: listaPermissao");	

		List<UsuarioGwtBean> lRet = new ArrayList<UsuarioGwtBean>();
		for (UsuarioBean usuario : listaUsuarios) {
			lRet.add(Usuario.toGwtBean(usuario));
		}
			
		return lRet;
	}
	
	private static void validarCamposUsuario(UsuarioBean usuarioValidar) throws ModelException
	{
		if (usuarioValidar == null)
			throw new ModelException("Erro: usuário nulo.");

		if (usuarioValidar.getId() == null)
			throw new ModelException("Erro: usuário sem id.");		
		
		if (usuarioValidar.getUsuario() == null)
			throw new ModelException("Erro: usuário sem login.");
		
		if (usuarioValidar.getSenha() == null)
			throw new ModelException("Erro: usuário sem senha.");		
		
		if (usuarioValidar.getNome() == null)
			throw new ModelException("Erro: usuário sem nome.");

	}
	
	public static void salvarUsuario(UsuarioGwtBean usuarioSalvar, String usuarioAtual)
	throws ModelException, SQLException, ConexaoException {

		UsuarioBean usuarioBean = new UsuarioBean(usuarioSalvar);
		validarCamposUsuario(usuarioBean);
		
		Connection conn = Conexao.getConexao();
		conn.setAutoCommit(false);
		
		//String descricaoLog = null;
		//String detalheLog = null;
		EventoCSX eventoCSX = null;
		
		try {
			if (usuarioSalvar.getIdUsuario() == 0) {
				// Trata-se de um novo usuário. Incluir ele no banco.
				incluiUsuario(conn, usuarioBean);
				
				eventoCSX = new EventoCSX(TipoEvento.CREATE_USER,
						usuarioAtual, usuarioBean.getUsuario(), "");
				
			} else {
				// Trata-se de um usuário que já existe. Atualizar no banco.
				Usuario usuario = Usuario.buscaUsuarioPorIdUsuario(usuarioSalvar.getIdUsuario());
				if (usuario == null)
					throw new ModelException("Usuário enviado não existe!");
				
				usuario.setFromUsuarioBean(new UsuarioBean(usuarioSalvar));
				usuario.alteraUsuario(conn);
				
				// Remove as permissões que estão no banco.
				PermissaoMenu.removerPermissoesByIdUsuario(conn, usuarioBean.getId());
				
				// Remove as associações (usuário com grupos) que estão no banco)
				GrupoUsuario.removerGrupoUsuarioByIdUsuario(conn, usuarioBean.getId());
				
				eventoCSX = new EventoCSX(TipoEvento.UPDATE_USER,
						usuarioAtual, usuarioBean.getUsuario(), "");
				
			}

			// Agora itera as permissões do bean de usuário.
			PermissaoMenuBean permissaoMenuBean = null;
			for (PermissaoGwtBean permissaoGwtBean : usuarioSalvar.getPermissoes()) {
				
				// Converte de bean (do gwt) para bean (normal).
				permissaoMenuBean = new PermissaoMenuBean(permissaoGwtBean);
				
				// Como é uma nova permissão, associar ela com o usuário.
				permissaoMenuBean.setIdUsuario(usuarioBean.getId());
				
				// Insere no banco.
				PermissaoMenu.inserir(conn, permissaoMenuBean);
			}

			// Agora itera os grupos associados ao usuário.
			GrupoUsuarioBean grupoUsuarioBean = null; 
			for (GrupoGwtBean grupoGwtBean : usuarioSalvar.getGrupos())
			{
				// Cria um bean (normal) baseado no id do usuário e do grupo.
				grupoUsuarioBean = new GrupoUsuarioBean(usuarioBean.getId(), grupoGwtBean.getIdGrupo());
				GrupoUsuario.inserirGrupoUsuario(conn, grupoUsuarioBean);
			}
			
			// Armazena um evento desta operação.
			Evento.incluirEventoCSX(conn, eventoCSX);
			
			// Por fim, commit da transação toda.
			conn.commit();
			
			// Recupera o id.
			usuarioSalvar.setIdUsuario(usuarioBean.getId());
			
			// Se tiver interessados em saber.
			if (listaListeners.size() > 0) {
				Thread threadAvisa = new Thread() {
					@Override
					public void run() {
						for (UsuarioListener listener : listaListeners) {
							listener.usuarioChanged();
						}
					}
				};
				
				// E avisa eles que algo mudou.
				threadAvisa.setDaemon(true);
				threadAvisa.start();
			}
			
		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		}			
		finally {
			conn.close();
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.usuario;
	}

	public String getAgenteUF() {
		return agenteUF;
	}

	public void setAgenteUF(String agenteUF) {
		this.agenteUF = agenteUF;
	}
	
	public String getSenhaMD5() {
	    if (this.senha == null) {
	        return null;
	    }
	    return this.senha;
	}
	
	public static Usuario buscaUsuarioPorGoogleToken(String google_id_token_temp)
	throws ConexaoException, SQLException 
	{
		Usuario uRet = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("   SELECT ");
		sbSQL.append("		id_usuario,");
		sbSQL.append("		usuario,");
		sbSQL.append("		nome,");
		sbSQL.append("		senha,");
		sbSQL.append("		email,");
		sbSQL.append("		alterar_senha,");
		sbSQL.append("		ativo,");
		sbSQL.append("		id_grupo_equipamento, ");
		sbSQL.append("		cod_agente, ");
		sbSQL.append("		uf_agente ");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_usuario WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		google_id_token_temp = ?");
		sbSQL.append("	ORDER BY ");
		sbSQL.append("		nome");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, google_id_token_temp);

			rs = ps.executeQuery();
			
			if (rs.next()) 
			{
				
				Integer codAgente = rs.getInt("cod_agente");
				if (rs.wasNull())
					codAgente = null;				
				
				uRet =  new Usuario(
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getString("nome"),
						rs.getString("senha"),
						rs.getString("email"),
						rs.getBoolean("alterar_senha"),
						rs.getBoolean("ativo"),
						rs.getInt("id_grupo_equipamento"),
						codAgente, 
						rs.getString("uf_agente"));
				
				Usuario.limpaGoogleToken(rs.getInt("id_usuario"));
			}
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return uRet;
	}		
	
	public static void limpaGoogleToken(int id_usuario) throws ConexaoException, SQLException
	{
		
		StringBuilder sbSQL = new StringBuilder();
		PreparedStatement ps = null;
		PreparedStatement psItem = null;
		Connection conn = null;
		
		try {
				sbSQL.append(" UPDATE sis_usuario set google_id_token_temp = NULL WHERE id_usuario = ? ");
				
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sbSQL.toString());
				
				ps.setInt(1, id_usuario);				
				ps.executeUpdate();
			

		} catch (SQLException e) {
			e.printStackTrace();
		
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}
	}	
	
	public static Usuario buscaUsuarioPorGoogleID(String google_id)
	throws ConexaoException, SQLException 
	{
		Usuario uRet = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("   SELECT ");
		sbSQL.append("		id_usuario,");
		sbSQL.append("		usuario,");
		sbSQL.append("		nome,");
		sbSQL.append("		senha,");
		sbSQL.append("		email,");
		sbSQL.append("		alterar_senha,");
		sbSQL.append("		ativo,");
		sbSQL.append("		id_grupo_equipamento, ");
		sbSQL.append("		cod_agente, ");
		sbSQL.append("		uf_agente ");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_usuario WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		google_id = ?");
		sbSQL.append("	ORDER BY ");
		sbSQL.append("		nome");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, google_id);

			rs = ps.executeQuery();
			
			if (rs.next()) 
			{
				
				Integer codAgente = rs.getInt("cod_agente");
				if (rs.wasNull())
					codAgente = null;				
				
				uRet =  new Usuario(
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getString("nome"),
						rs.getString("senha"),
						rs.getString("email"),
						rs.getBoolean("alterar_senha"),
						rs.getBoolean("ativo"),
						rs.getInt("id_grupo_equipamento"),
						codAgente, 
						rs.getString("uf_agente"));
			}
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return uRet;
	}		
}
