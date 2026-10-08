/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 17/05/2007

  Descricao: Classe de negócio responsável pelos logons no sistema.

  Historico:

    $Log: LogonLogoff.java,v $
    Revision 1.8  2009/05/11 12:50:41  raoni
    Removido os "close" no PreparedSatement. Estava conflitando com o pool do SQL Server.

    Revision 1.7  2009/05/11 11:10:21  raoni
    Adicionado "WITH (NOLOCK)" para evitar travamento com MAX

    Revision 1.6  2009/03/12 13:07:38  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.5  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.3  2008/01/17 18:44:13  fernando
    Alterado Comentário

    Revision 1.2  2007/12/14 12:44:55  fernando
    Adptado para usar o id_usuario
    Adptado para nova estrutura do banco de dados

    Revision 1.1  2007/07/06 13:05:17  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model;

import java.io.File;
import java.io.FileWriter; 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.consilux.infra.SessaoFinaliza;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de negócio responsável pelos logons no sistema.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.8 $ $Date: 2009/05/11 12:50:41 $ $Author: raoni $
 */
public class LogonLogoff implements SessaoFinaliza {
	private Integer id;
	private Integer idUsuario;
	private Date logon;
	private String host;
	public static final String APP_GTW = "GTW";
		
	private static Logger logger = Logger.getLogger(LogonLogoff.class);
	private static List<String> ip = new ArrayList<String>();
	
	private LogonLogoff(Integer id, Integer idUsuario, Date logon, String host) {
		super();
		this.id = id;
		this.idUsuario = idUsuario;
		this.logon = logon;
		this.host = host;
	}

	/**
	 * Insere um novo registro de logon
	 * @param idUsuario Id do usuário.
	 * @param host Host de onde o usuário está se conectando.
	 * @param ip IP do usuário
	 * @return Objeto LogonLogoff materializado.
	 * @throws SQLException
	 * @throws ConexaoException
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static LogonLogoff inserirLogonLogoff(Integer idUsuario, String host, String ip) throws ConexaoException, SQLException, IOException {
		
		LogonLogoff lRet = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		//logger.debug(ip);
		
		if(hostConhecido(ip) == false)
			return lRet;

		try {
			conn = Conexao.getConexao();
			
			StringBuilder sbSQL = new StringBuilder();
			
			sbSQL.append( "INSERT INTO sis_logon_logoff_usuario (" );
			sbSQL.append( "	id_usuario,"                           );
			sbSQL.append( "	data_logon,"                           );
			sbSQL.append( "	maquina,"                              );
			sbSQL.append( "	app"                                   );
			sbSQL.append( "	) VALUES(?,?,?,?)"                     );
					
			ps = conn.prepareStatement(sbSQL.toString(), PreparedStatement.RETURN_GENERATED_KEYS );
			Date dataLogon = new Date();
			
			ps.setInt(1, idUsuario);
			ps.setTimestamp(2, new java.sql.Timestamp(dataLogon.getTime()));
			ps.setString(3, host);
			ps.setString(4, LogonLogoff.APP_GTW);
			
			if (ps.executeUpdate() > 0) {
				rs = ps.getGeneratedKeys();
				rs.next();
				lRet = new LogonLogoff(rs.getInt(1), idUsuario, dataLogon, host);
			}

		}
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return lRet;
	}
	
	/**
	 * Busca um log de logon do banco de dados por ID.
	 * @param id Identificador do logon no banco de dados.
	 * @return Objeto LogonLogoff materializado.
	 * @throws SQLException
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static LogonLogoff buscarLogonLogoffPorId(Integer id) throws ConexaoException, SQLException {
		LogonLogoff lRet = null;
		StringBuilder sbSQL = new StringBuilder(); 
		
		sbSQL.append("SELECT ");
		sbSQL.append("			id_logon,");
		sbSQL.append("			id_usuario,");
		sbSQL.append("			data_logon,");
		sbSQL.append("			maquina");
		sbSQL.append("		FROM");
		sbSQL.append("			sis_logon_logoff_usuario");
		sbSQL.append("		WHERE");
		sbSQL.append("			id_logon = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				lRet = new LogonLogoff(
						rs.getInt("id_logon"),
						rs.getInt("id_usuario"),
						rs.getTimestamp("data_logon"),
						rs.getString("maquina")
						);
			}
			
		}
		finally {
			if (conn != null)
				conn.close();							
		}
			
		return lRet;
	}
	
	/**
	 * Busca o último acesso do usuário ao sistema.
	 * @param usuario Login do usuário.
	 * @return Objeto LogonLogoff materializado.
	 * @throws SQLException
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static LogonLogoff buscarUltimoAcesso(Integer id_usuario) throws ConexaoException, SQLException {
		LogonLogoff lRet = null;
		String sSQL = "SELECT MAX(id_logon) FROM sis_logon_logoff_usuario WITH (NOLOCK) WHERE id_usuario = ? AND app = ?";
		
		Connection conn = Conexao.getConexao();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sSQL);
			ps.setInt(1, id_usuario);
			ps.setString(2, LogonLogoff.APP_GTW);
			rs  = ps.executeQuery();
			if (rs.next()) {
				Integer id = rs.getInt(1);
				if (id > 0)
					lRet = LogonLogoff.buscarLogonLogoffPorId(id);
			}
		}
		finally {
			if (conn != null)
				conn.close();							
		}			
		return lRet;
	}
	
	public void finaliza() throws ConexaoException, SQLException {
		Connection conn = Conexao.getConexao();
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("UPDATE sis_logon_logoff_usuario SET ");
		sbSQL.append("	data_logoff = ? ");
		sbSQL.append("WHERE");
		sbSQL.append("	id_logon = ?");
				
		try {
			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			ps.setInt(2, this.id);
			
			ps.executeUpdate();
		}
		finally {
			if (conn != null)
				conn.close();							
		}			
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the usuario
	 */
	public Integer getIdUsuario() {
		return idUsuario;
	}

	/**
	 * @return the logon
	 */
	public Date getLogon() {
		return logon;
	}

	/* (non-Javadoc)
	 * @see com.consilux.infra.SessaoFinaliza#doFinaliza(javax.servlet.http.HttpSession)
	 */
	@Override
	public void doFinaliza(HttpSession sessao) throws Exception {
		finaliza();
	}
	
	/**
	 *   Confere se o host que está solicitando a conexão está listado nos IP permitidos.
	 *   Se não houver IP listados, o arquivo for inexistente, 
	 * ou não for possível ler o arquivo, aceita todas as conexões.
	 *   Se o arquivo não existir, cria um arquivo vazio.
	 *   
	 * @param host (String): IP do cliente solicitando conexão
	 * 
	 * @return (boolean): true se a conexão for reconhecida, caso contrário false
	 */
	private static boolean hostConhecido(String host)
	{
//		logger.debug("Host: " + host);
		
		ip = new ArrayList<String>();
		File arquivo_ip = new File("ip_permitidos.txt");
		File arquivo_acessos = new File("ip_rejeitados.txt");
		
		/*=============================================================
		 * 		LÊ O ARQUIVO DE PRMISSÕES
		 =========================================================== */
		try
		{
			if(!arquivo_ip.exists())
				arquivo_ip.createNewFile();
			
			Scanner leitor = new Scanner(arquivo_ip);
			
			while (leitor.hasNextLine())
				ip.add(leitor.nextLine());
			
			leitor.close();
		}
		catch (FileNotFoundException e) {	return true;	}
		catch (IOException e)           {	return true;	}
				
		/*=============================================================
		 * 		VERIFICA SE O ACESSO É ABERTO OU SE O HOST ESTÁ LISTADO 
		 =========================================================== */
		if(ip!=null)
		{
			if (ip.isEmpty() || ip.contains(host))
				return true;
		}
		
		/*=============================================================
		 * 		INSERE O HOST NOS IP REJEITADOS QUE TENTARAM ACESSO
		 =========================================================== */
		try
		{
			if(!arquivo_acessos.exists())
				arquivo_acessos.createNewFile();
			
			FileWriter escritor = new FileWriter(arquivo_acessos);
			
			escritor.append(host);
			escritor.append("\n");
			
			escritor.close();
			
		}
		catch (FileNotFoundException e) { /*logger.debug("Sem lista de IP rejeitados"); 			 */	}
		catch (IOException e) 			{ /*logger.debug("Impossível abrir lista de IP rejeitados"); */	}
		
		
		/*=============================================================
		 * 		RETORNA FALSO POIS O HOST NÃO TEM PERMISSÃO
		 =========================================================== */
		return false;
	}
}
