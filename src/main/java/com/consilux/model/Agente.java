/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 20/03/2007

  Descricao: Classe de negócio para controle de agentes de validação.

  Historico:

    $Log: Agente.java,v $
    Revision 1.8  2009/03/12 13:07:37  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.7  2009/01/28 19:23:07  fos
    Retirada query antiga para o lotesweb.

    Revision 1.6  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.4  2007/07/06 13:04:15  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.3  2007/04/18 18:45:13  fos
    Ajustes para o javadoc

    Revision 1.2  2007/04/17 17:44:43  fos
    Adicionado rotinas de sumarização das validações do agente.

    Revision 1.1  2007/03/20 20:14:33  fos
    Classe de informações sobre agentes.


*********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de negócio para controle de agentes de validação.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.8 $ $Date: 2009/03/12 13:07:37 $ $Author: raoni $
 */
public class Agente {
	public class SomaConcluidas {
		private Integer infracoesDisponiveis;
		private Integer infracoesConcluidas;
		protected SomaConcluidas(Integer infracoesDisponiveis, Integer infracoesConcluidas) {
			super();
			this.infracoesDisponiveis = infracoesDisponiveis;
			this.infracoesConcluidas = infracoesConcluidas;
		}
		public Integer getInfracoesConcluidas() {
			return infracoesConcluidas;
		}
		public Integer getInfracoesDisponiveis() {
			return infracoesDisponiveis;
		}
		
	}
	private String login;
	private String nome;
	/**
	 * Constrói o objeto Agente com os seus respectivos atributos.
	 * @param login Login do agente
	 * @param nome Nome do agente
	 */
	private Agente(String login, String nome) {
		super();
		this.login = login;
		this.nome = nome;
	}
	/**
	 * Busca uma lista de agentes disponíveis
	 * @param grupo Grupo qual os agentes devem pertencer
	 * @return Lista de agentes
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Agente> buscaAgentesDisponiveis(int grupo) throws ConexaoException {
		
		List<Agente> lRet = new ArrayList<Agente>();
		StringBuilder sbSQL = new StringBuilder();
		
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT u.usuario,");
		sbSQL.append("		u.nome");
		sbSQL.append("	FROM");
		sbSQL.append("		usuario u WITH (NOLOCK) ");
		sbSQL.append("	WHERE");
		sbSQL.append("		u.grupo = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1,grupo);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new Agente(
							rs.getString("usuario"),
							rs.getString("nome")
						)
				);
			}
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			try {
				if (rs != null)					
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}
	
	/**
	 * Login do usuário
	 * @return login
	 */
	public String getLogin() {
		return login;
	}
	/**
	 * Nome do usuário
	 * @return nome
	 */
	public String getNome() {
		return nome;
	}
}
