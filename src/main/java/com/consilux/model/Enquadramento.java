/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 24/07/2008

  Descricao: Classe de modelos para acesso aos enquadramentos.

  Histórico:

    $Log: Enquadramento.java,v $
    Revision 1.6  2009/03/12 13:07:37  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.5  2009/03/09 18:47:57  fos
    Criada função para agrupar enquadramentos de velocidade.

    Revision 1.4  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.2  2008/09/17 14:15:52  fos
    ASSIGNED - bug 90: Passar relatórios CET Excel para o GTW.
    http://bugzilla.consilux.net/show_bug.cgi?id=90

    Revision 1.1  2008/08/29 20:48:06  fos
    Primeira versão postada no CVS.

    Revision 1.1  2008/07/31 21:09:55  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de modelos para acesso aos enquadramentos.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.6.2.1 $ $Date: 2009/06/16 00:12:01 $ $Author: fos $
 */
public class Enquadramento {

	private Integer idEnquadramento;
	private String descricao;
	private String tipoInfoEspecifica;

	private static Set<Integer> enquadramentosVelocidade = new HashSet<Integer>(3);

	static
	{
		enquadramentosVelocidade.add(74550); // Trans. veloc. superior a máx. perm. em até de 20%
		enquadramentosVelocidade.add(74630); // Trans. veloc. superior a máx. perm. em mais de 20% até 50%
		enquadramentosVelocidade.add(74710); // Trans. veloc. superior a máx. perm. em mais de 50%
	}

	/**
	 * @param idEnquadramento
	 * @param descricao
	 * @param tipoInfoEspecifica
	 */
	public Enquadramento(Integer idEnquadramento, String descricao,
			String tipoInfoEspecifica) {
		super();
		this.idEnquadramento = idEnquadramento;
		this.descricao = descricao;
		this.tipoInfoEspecifica = tipoInfoEspecifica;
	}

	/**
	 * Busca todos os enquadramentos no BD.
	 * @return Lista com os objetos Enquadramento materializados.
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static List<Enquadramento> buscaTodosEnquadramentos() throws ConexaoException, SQLException  {

		List<Enquadramento> lRet = new ArrayList<Enquadramento>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT id_enquadramento,");
		sbSQL.append("		descricao,");
		sbSQL.append("		tipo_info_especifica");
		sbSQL.append("	FROM");
		sbSQL.append("		enquadramento WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		tipo_info_especifica <> ' ' AND not tipo_info_especifica IS NULL");
		sbSQL.append("	ORDER BY id_enquadramento ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.add(new Enquadramento(
						rs.getInt("id_enquadramento"),
						rs.getString("descricao"),
						rs.getString("tipo_info_especifica")
				)
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
	 * Busca todos os enquadramentos no BD.
	 * @return Lista com os objetos Enquadramento materializados.
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static List<Enquadramento> buscaEnquadramentoDisponivelPorIdProcesso(Integer idProcesso) throws ConexaoException, SQLException  {

		List<Enquadramento> lRet = new ArrayList<Enquadramento>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT id_enquadramento,");
		sbSQL.append("		descricao,");
		sbSQL.append("		tipo_info_especifica");
		sbSQL.append("	FROM");
		sbSQL.append("		enquadramento WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		tipo_info_especifica <> ' ' AND not tipo_info_especifica IS NULL");
		sbSQL.append("		AND id_enquadramento IN (SELECT DISTINCT id_enquadramento FROM infracao i (NOLOCK) WHERE i.id_processo = ?)");
		sbSQL.append("	ORDER BY id_enquadramento ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idProcesso);

			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.add(new Enquadramento(
						rs.getInt("id_enquadramento"),
						rs.getString("descricao"),
						rs.getString("tipo_info_especifica")
				)
				);
			}
		}
		finally {
			if (conn != null)
				conn.close();
		}
		return lRet;
	}

	public static List<Enquadramento> buscaEnquadramentosPorRemessa(Integer id_remessa) throws SQLException, ConexaoException {
		
		List<Enquadramento> lRet = new ArrayList<Enquadramento>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT enq.id_enquadramento, enq.descricao, enq.tipo_info_especifica FROM infracao i (NOLOCK) ");
		sbSQL.append("JOIN enquadramento enq (NOLOCK) ON i.id_enquadramento = enq.id_enquadramento ");
		sbSQL.append("JOIN infracao_remessa ir (NOLOCK) ON i.id_infracao = ir.id_infracao ");
		sbSQL.append("WHERE ir.id_remessa = ? ");
		sbSQL.append("GROUP BY enq.id_enquadramento, enq.descricao, enq.tipo_info_especifica ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id_remessa);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new Enquadramento(
						rs.getInt("id_enquadramento"),
						rs.getString("descricao"),
						rs.getString("tipo_info_especifica")
				)
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
	 * Busca um enquadramento no BD.
	 * @param idEnquadramento Identificador do enquadramento.
	 * @return Objeto Enquadramento materializado.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Enquadramento buscaEnquadramentosPorId(Integer idEnquadramento) throws ConexaoException {

		Enquadramento ret = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT id_enquadramento,");
		sbSQL.append("		descricao,");
		sbSQL.append("		tipo_info_especifica");
		sbSQL.append("	FROM");
		sbSQL.append("		enquadramento WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_enquadramento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idEnquadramento);
			rs = ps.executeQuery();

			if (rs.next()) {
				ret = new Enquadramento(
						rs.getInt("id_enquadramento"),
						rs.getString("descricao"),
						rs.getString("tipo_info_especifica")
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

		return ret;
	}

	/**
	 * @return Retorna o valor de idEnquadramento atual.
	 */
	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}

	/**
	 * @return Retorna a descricão do enquadramento.
	 */
	public String getDescricao() {
		return descricao;
	}
	
	/**
	 * @return the tipoInfoEspecifica
	 */
	public String getTipoInfoEspecifica() {
		return tipoInfoEspecifica;
	}

	public static List<Integer> getCodigosEnquadramentosVelocidade() {
		return new ArrayList<Integer>(enquadramentosVelocidade);
	}

	public static String[] getListaEnquadramentosVelocidade() {

		String[] lRet = new String[enquadramentosVelocidade.size()];

		int i = 0;
		for (Integer enq : enquadramentosVelocidade)
		{
			lRet[i] = Integer.toString(enq);
			i++;
		}
		return lRet;
	}

	/**
	 * Verifica se um determinado enquadramento é de velocidade (ou não)
	 * @param idEnquadramento
	 * @return
	 */
	public static boolean isEnquadramentoVelocidade(Integer idEnquadramento) {
		return idEnquadramento != null && enquadramentosVelocidade.contains(idEnquadramento);
	}
	
	/**
	 * Verifica se um determinado enquadramento é metrológico (ou não)
	 * @param idEnquadramento
	 * @return
	 */
	public static boolean isEnquadramentoMetrologico(int idEnquadramento) {
		// Hoje, para saber se é metrológico basta verificar se é velocidade
		return enquadramentosVelocidade.contains(idEnquadramento);
	}	

	/**
	 * Verifica se um determinado enquadramento é de velocidade (ou não)
	 * @param enquadramento
	 * @return
	 */
	public static boolean isEnquadramentoVelocidade(Enquadramento enquadramento) {
		return enquadramento != null && enquadramentosVelocidade.contains(enquadramento.getIdEnquadramento());
	}

	public static String getEnquadramentosVelocidade() {
		return Funcoes.concatStringArray(getListaEnquadramentosVelocidade(), "|");
	}

}
