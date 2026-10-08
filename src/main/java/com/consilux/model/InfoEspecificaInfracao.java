/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 14/05/2008

  Descricao: Decodifica o código do enquadramento e busca informações da infração.

  Histórico:

    $Log: InfoEspecificaInfracao.java,v $
    Revision 1.6  2009/03/12 13:07:38  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.5  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.3  2008/08/29 20:52:00  fos
    Renomeada métodos e atributos para enquadramento.

    Revision 1.2  2008/07/31 21:09:01  fos
    Quebrada a informação específica da velocidade em mais partes.

    Revision 1.1  2008/05/14 21:43:42  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Decodifica o código do enquadramento e busca informações da infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.6.2.2 $ $Date: 2009/06/16 20:12:54 $ $Author: fernando $
 */
public class InfoEspecificaInfracao {
	
	public enum TipoEnquadramento {
		
		VELOCIDADE('V', "Velocidade"),
		AVANCO_SEMAFORO('S', "Avanço de Semáforo"),
		PARADA_FAIXA('P', "Parada Faixa");
		
		private Character cTipo;
		private String tituloEspecifica;
		
		private TipoEnquadramento(Character cTipo, String tituloEspecifica) {
			this.cTipo = cTipo;
			this.tituloEspecifica = tituloEspecifica;
		}
		
		public String toString() {
			return String.valueOf(cTipo);
		}
		
		public String getTituloEspecifica() {
			return tituloEspecifica;
		}
		
		public static TipoEnquadramento valueOf(Character cTipo) {
			if (String.valueOf(cTipo).equals(VELOCIDADE.toString()))
				return VELOCIDADE;
			else if (String.valueOf(cTipo).equals(AVANCO_SEMAFORO.toString()))
				return AVANCO_SEMAFORO;
			else
				return null;
		}
	}
	
	private Integer idEnquadramento;
	private String descricao;
	
	private String tituloEspecifica;
	private List<String> descricaoEspecifica;
	
	/**
	 * Constrói o objeto InfoEspecificaInfracao com os seus respectivos atributos.
	 * @param idEnquadramento
	 * @param descricao
	 * @param tituloEspecifica
	 * @param descricaoEspecifica
	 */
	private InfoEspecificaInfracao(Integer idEnquadramento, String descricao,
			String tituloEspecifica, List<String> descricaoEspecifica)
	{
		super();
		this.idEnquadramento = idEnquadramento;
		this.descricao = descricao;
		this.tituloEspecifica = tituloEspecifica;
		
		this.descricaoEspecifica = new ArrayList<String>();
		if (descricaoEspecifica != null && descricaoEspecifica.size() > 0)
		{
			this.descricaoEspecifica.addAll(descricaoEspecifica);
		}
	}

	/**
	 * Busca os dados no BD para montar a descrição específica.
	 * @param idInfracao
	 * @return
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static InfoEspecificaInfracao buscaPorInfracao(Integer idInfracao) throws SQLException, ConexaoException {

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT e.id_enquadramento, e.descricao, e.tipo_info_especifica "); 
		sbSQL.append("FROM infracao i (NOLOCK) " );
		sbSQL.append("JOIN enquadramento e ON e.id_enquadramento = i.id_enquadramento ");
		sbSQL.append("WHERE i.id_infracao = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;				

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());				
			ps.setInt(1, idInfracao);
			rs = ps.executeQuery();

			int idEnquadramento = 0;
			String descricao = "";
			TipoEnquadramento tipo = null;

			if (rs.next()) {
				idEnquadramento = rs.getInt("id_enquadramento");
				descricao = rs.getString("descricao");
				tipo = TipoEnquadramento.valueOf(rs.getString("tipo_info_especifica").charAt(0));
			}

			List<String> textoEspecifica = new ArrayList<String>();
			String tituloEspecifica = "";
			
			if (tipo != null) {
				tituloEspecifica = tipo.getTituloEspecifica();
				switch (tipo) {
					case VELOCIDADE:
						textoEspecifica = getInfoEspecicaVelocidade(idInfracao);
						break;
					case AVANCO_SEMAFORO:
						textoEspecifica = getInfoEspecicaAvancoSemaforo(idInfracao);
						break;
					case PARADA_FAIXA:
						textoEspecifica = getInfoEspecicaParadaFaixa(idInfracao);
						break;
				}
			}

			return new InfoEspecificaInfracao(idEnquadramento, descricao,
				tituloEspecifica, textoEspecifica);
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
	}	

	/**
	 * @param idInfracao
	 * @return
	 * @throws SQLException
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	private static List<String> getInfoEspecicaVelocidade(Integer idInfracao) throws ConexaoException, SQLException {

		List<String> lRet = new ArrayList<String>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT i.velocidade_limite, v.velocidade, COALESCE(v.porteVeiculo, 'N/D') AS porteVeiculo "); 
		sbSQL.append("FROM infracao i (NOLOCK) ");
		sbSQL.append("JOIN veiculo v (NOLOCK) ON v.id_veiculo = i.id_veiculo ");
		sbSQL.append("WHERE i.id_infracao = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setInt(1, idInfracao);
			rs = ps.executeQuery();
			if (rs.next()) {
				
				int velocidade = rs.getInt("velocidade");
				lRet.add("Velocidade Medida: " + String.valueOf(velocidade) + "km/h.");
				
				lRet.add("Limite: " + String.valueOf(rs.getInt("velocidade_limite")) + "km/h.");
				
				lRet.add("Classificação: " + String.valueOf(rs.getString("porteVeiculo")));
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}		

		return lRet;
	}

	/**
	 * Recupera as informações específicas para avanço de semáforo.
	 * @param idInfracao
	 * @return
	 * @throws SQLException
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	private static List<String> getInfoEspecicaAvancoSemaforo(Integer idInfracao) throws ConexaoException, SQLException {

		List<String> lRet = new ArrayList<String>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT i.segundos_tolerancia, v.segundos, v.velocidade, i.tempo_vermelho_detec "); 
		sbSQL.append("FROM infracao i (NOLOCK)  ");
		sbSQL.append("JOIN veiculo v ON v.id_veiculo = i.id_veiculo ");
		sbSQL.append("WHERE i.id_infracao = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setInt(1, idInfracao);
			rs = ps.executeQuery();
			if (rs.next()) {
				lRet.add("Tempo Avanço: " + String.valueOf(rs.getFloat("segundos")) + "s.");
				lRet.add("Tolerância Avanço: " + String.valueOf(rs.getFloat("segundos_tolerancia")) + "s.");
				
				int velocidade = rs.getInt("velocidade");
				lRet.add("Velocidade Medida: " + (velocidade > 0  ? String.valueOf(velocidade) + "km/h."
					: ""));
				
				Double tempoVermelho = rs.getDouble("tempo_vermelho_detec");
				lRet.add("Tempo Vermelho Primeira Panorâmica: " + (rs.wasNull() ? "" :
					String.valueOf(tempoVermelho) + "s."));

				Double ddistancia = (rs.wasNull() || velocidade < 1 ? null : (velocidade / 3.6) * tempoVermelho);
				BigDecimal distancia = ddistancia == null ? null : new BigDecimal(ddistancia).setScale(1, RoundingMode.DOWN);  
				lRet.add("Dist. estim. na troca para o vermelho: " + (distancia == null ? "" :
					String.valueOf(distancia) + "m."));
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}		

		return lRet;
	}

	/**
	 * Recupera as informações específicas para parada sorbe faixa.
	 * @param idInfracao
	 * @return
	 * @throws SQLException
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	private static List<String> getInfoEspecicaParadaFaixa(Integer idInfracao) throws ConexaoException, SQLException {

		List<String> lRet = new ArrayList<String>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT i.segundos_tolerancia, v.segundos "); 
		sbSQL.append("FROM infracao i (NOLOCK)  ");
		sbSQL.append("JOIN veiculo v ON v.id_veiculo = i.id_veiculo ");
		sbSQL.append("WHERE i.id_infracao = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setInt(1, idInfracao);
			rs = ps.executeQuery();
			if (rs.next()) {
				lRet.add("Tempo Parada: " + String.valueOf(rs.getFloat("segundos")) + "s.");
				lRet.add("Tolerância Parada: " + String.valueOf(rs.getFloat("segundos_tolerancia")) + "s.");
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}		

		return lRet;
	}
	/**
	 * @return Retorna o valor de idEnquadramento atual.
	 */
	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}

	/**
	 * @return Retorna o valor de descricao atual.
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @return Retorna o valor de tituloEspecifica atual.
	 */
	public String getTituloEspecifica() {
		return tituloEspecifica;
	}

	/**
	 * @return Retorna uma lista com os valores de descricaoEspecifica atuais.
	 */
	public List<String> getDescricaoEspecifica() {
		return descricaoEspecifica;
	}

}
