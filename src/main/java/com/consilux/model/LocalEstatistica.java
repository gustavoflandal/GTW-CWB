/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 25/01/2007

  Descricao: Classe de negócio para busca de estatísticas do local.

  Historico:

    $Log: LocalEstatistica.java,v $
    Revision 1.9  2009/03/12 13:07:38  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.8  2009/03/10 14:35:46  raoni
    Otimizado o uso de StringBuilder.
    Utiliza LinkedList quando possível.
    Adicionado lógica para realizar o clean up (close) dos Statements.

    Revision 1.7  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.5  2008/07/23 14:24:50  fos
    Ajustado o nome da função.

    Revision 1.4  2007/07/06 13:04:54  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.3  2007/03/16 13:28:25  fos
    Acertado links inválidos nos comentários

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de estatísticas do local.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.9 $ $Date: 2009/03/12 13:07:38 $ $Author: raoni $
 */
public class LocalEstatistica {
	private int codigo;
	private int local;
	private Date dataHora;
	private int total;
	private float velMed;
	/**
	 * Constrói o objeto LocalEstatistica com os seus respectivos atributos.
	 * @param codigo Identificador do local
	 * @param local Código do local
	 * @param dataHora Data e hora do agrupamento
	 * @param total Quantidade de veículos
	 * @param velMed Velocidade média dos veículos
	 */
	private LocalEstatistica(int codigo, int local, Date dataHora, int total, float velMed) {
		super();
		this.codigo = codigo;
		this.local = local;
		this.dataHora = dataHora;
		this.total = total;
		this.velMed = velMed;
	}
	/**
	 * Busca estatísticas de locais no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: datahora_ini, datahora_fim.
	 * @return Lista de objetos LocalEstatistica
	 * @throws ConexaoException
	 * @throws SQLException
	 * @see #buscaLocalEstatisticaPor(Map<String,Object>, String)
	 */
	public static List<LocalEstatistica> buscaLocalEstatisticaPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException {
		return buscaLocalEstatisticaPor(mFiltros,"3");
	}
	/**
	 * Busca estatísticas de locais no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: datahora_ini, datahora_fim.
	 * @param orderBy Coluna qual o relatório será ordenada
	 * @return Lista de objetos LocalEstatistica
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<LocalEstatistica> buscaLocalEstatisticaPor(Map<String,Object> mFiltros,String orderBy) throws ConexaoException, SQLException {
		List<LocalEstatistica> lRet = new ArrayList<LocalEstatistica>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT codigo,");
		sbSQL.append("		local,");
		sbSQL.append("		dataHora,");
		sbSQL.append("		total,");
		sbSQL.append("		velMed");
		sbSQL.append("	FROM ");
		sbSQL.append("		local_estatistica WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("datahora_ini", "dataHora >= ?");
		mRegras.put("datahora_fim", "dataHora < ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY " + orderBy);
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.add(new LocalEstatistica(
						rs.getInt("codigo"),
						rs.getInt("local"),
						rs.getTimestamp("dataHora"),
						rs.getInt("total"),
						rs.getFloat("velMed")
				)
				);
			}

		} catch (ModelException e) {
			throw new SQLException("Erro ao montar SQL.", e);
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
	 * Identificador do local
	 * @return Identificador
	 */
	public int getCodigo() {
		return codigo;
	}
	/**
	 * Data e hora do agrupamento
	 * @return Data e hora
	 */
	public Date getDataHora() {
		return dataHora;
	}
	/**
	 * Código do local
	 * @return Código
	 */
	public int getLocal() {
		return local;
	}
	/**
	 * Quantidade de veículos
	 * @return Quantidade
	 */
	public int getTotal() {
		return total;
	}
	/**
	 * Velocidade média
	 * @return Velocidade
	 */
	public float getVelMed() {
		return velMed;
	}

}
