/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/03/2007

  Descricao: Classe de negócio para busca de infrações.

  Historico:

    $Log: Infracao.java,v $
    Revision 1.11  2009/03/12 13:07:38  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.10  2009/01/28 19:24:34  fos
    Agora a tabela infracao carrega os atributos de data e local.

    Revision 1.9  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.7  2008/08/27 14:00:41  fos
    ASSIGNED - bug 17: Seleção/Visualização das múltiplas imagens da infração.
    http://bugzilla.consilux.net/show_bug.cgi?id=17

    Revision 1.6  2008/08/12 13:00:23  fos
 *** empty log message ***

    Revision 1.5  2008/07/23 14:24:49  fos
    Ajustado o nome da função.

    Revision 1.4  2007/07/06 13:04:15  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.3  2007/04/11 11:59:20  fos
    Agora trabalha com atributo 'infracao' inteiro e não mais string

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


 *********************************************************************************/
package com.consilux.model;

import java.io.IOException;
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
 * Classe de negócio para busca de infrações.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.11.2.2 $ $Date: 2009/06/22 01:02:55 $ $Author: raoni $
 */
public class Infracao {
	private Integer idInfracao;
	private String placa;
	private Date data;
	private Integer idInconsistencia;
	private Integer idImagemOBJ;
	private Long idVeiculo;

	/**
	 * Constrói o objeto Infracao com os seus respectivos atributos.
	 * @param idInfracao Identificador da infração
	 * @param placa Placa do veículo
	 * @param data Data da infração
	 * @param data Data da infração
	 * @param idInfracao Identificador da inconsistência da infração.
	 * @param idImagemOBJ Identificador da imagem objetiva para esta infração.
	 * @param idVeiculo Identificador do veículo relacionado a infração.
	 */
	private Infracao(Integer infracao, String placa, Date data, Integer idInconsistencia, Integer idImagemOBJ, Long idVeiculo) {
		this.idInfracao = infracao;
		this.placa = placa;
		this.data = data;
		this.idInconsistencia = idInconsistencia;
		this.idImagemOBJ = idImagemOBJ;
		this.idVeiculo = idVeiculo;
	}

	/**
	 * Busca uma infração no BD.
	 * @param iInfracao Identificador da infração
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Infracao buscaInfracaoPorId(Integer iInfracao) throws ConexaoException, SQLException {

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT i.id_infracao,");
		sbSQL.append("		i.placa,");
		sbSQL.append("		data,");
		sbSQL.append("		i.id_inconsistencia,");
		sbSQL.append("		im.id_imagem_obj,");
		sbSQL.append("		i.id_veiculo");
		sbSQL.append("	FROM");
		sbSQL.append("		infracao i WITH (NOLOCK)");
		sbSQL.append("		LEFT JOIN infracao_imagem im ON im.id_infracao = i.id_infracao");
		sbSQL.append("	WHERE");
		sbSQL.append("		i.id_infracao = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, iInfracao);

			rs = ps.executeQuery();
			if (rs.next()) {
				return new Infracao(
						rs.getInt("id_infracao"), 
						rs.getString("placa"), 
						rs.getTimestamp("data"),
						rs.getInt("id_inconsistencia"),
						rs.getInt("id_imagem_obj"),
						rs.getLong("id_veiculo")
				);
			}
			else {
				return null;
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
	}
	
	public static Infracao buscarInfracaoPorIdVeiculo(Integer idVeiculo) throws ConexaoException, SQLException, ModelException {
		List<Infracao> lret = null;
		
		Map<String,Object> mFiltros = new HashMap<String, Object>();
		mFiltros.put("id_veiculo", idVeiculo);
		lret = buscaInfracaoPor(mFiltros);
		
		return lret != null && lret.size() > 0 ? lret.get(0) : null; 
	}

	/**
	 * Busca infrações no BD
	 * @param mFiltros Filtros para a busca.
	 * @return Lista de objetos Infracao
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static List<Infracao> buscaInfracaoPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException {
		
		List<Infracao> lRet = new ArrayList<Infracao>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT i.id_infracao,");
		sbSQL.append("		i.placa,");
		sbSQL.append("		i.data,");
		sbSQL.append("		i.id_inconsistencia,");
		sbSQL.append("		im.id_imagem_obj,");
		sbSQL.append("		i.id_veiculo");
		sbSQL.append("	FROM");
		sbSQL.append("		infracao i WITH (NOLOCK)");
		sbSQL.append("		LEFT JOIN infracao_imagem im ON im.id_infracao = i.id_infracao");
		sbSQL.append("	WHERE ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY 1");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Infracao(
						rs.getInt("id_infracao"), 
						rs.getString("placa"), 
						rs.getTimestamp("data"),
						rs.getInt("id_inconsistencia"),
						rs.getInt("id_imagem_obj"),
						rs.getLong("id_veiculo")
				)

				);
			}

		} catch (ModelException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return lRet;
	}

	/**
	 * Identificador da infração.
	 * @return IdInfracao
	 */
	public Integer getIdInfracao() {
		return idInfracao;
	}

	/**
	 * Data da infração.
	 * @return Data
	 */
	public Date getData() {
		return data;
	}

	/**
	 * Placa da infração.
	 * @return Placa
	 */
	public String getPlaca() {
		return placa;
	}

	/**
	 * @return Retorna o valor de idInconsistencia atual.
	 */
	public Integer getIdInconsistencia() {
		return idInconsistencia;
	}

	/**
	 * @return Retorna o valor de idImagemOBJ atual.
	 */
	public Integer getIdImagemOBJ() {
		return idImagemOBJ;
	}

	public Long getIdVeiculo() {
		return idVeiculo;
	}

	public byte[] buscaImagemObjetiva() throws ConexaoException, SQLException, ModelException, IOException {
		byte[] bImg = null;

		Integer idImagem = this.idImagemOBJ;

		Map<String,Object> mFiltro = new HashMap<String,Object>();
		mFiltro.put("id_imagem",idImagem);
		List<VeiculoImagem> infracaoImgs = VeiculoImagem.buscaVeiculoImagemPor(mFiltro);

		assert infracaoImgs != null;

		if (infracaoImgs.size() > 0)
			bImg = infracaoImgs.get(0).getImagem();
		else
			throw new ModelException("Infração sem imagem objetiva.");

		return bImg;

	}

	/**
	 * Conta quantas infrações (que podem ser eventuais amostras) existem em um determinado
	 * local, pista, data e metrológica.
	 * @param idLocal
	 * @param idPista
	 * @param dia
	 * @param metrologica
	 * @return quantidade de infrações no banco com os critérios definidos.
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static int contarInfracoes(int idLocal, short idPista, Date dia,
		boolean metrologica) throws SQLException, ConexaoException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT ");
		sbSQL.append("	COUNT(*) ");		
		sbSQL.append(" FROM ");
		sbSQL.append("	infracao inf WITH (NOLOCK) ");
		sbSQL.append("	JOIN enquadramento enq WITH (NOLOCK) ");
		sbSQL.append("		ON inf.id_enquadramento = enq.id_enquadramento ");
		sbSQL.append(" WHERE ");
		sbSQL.append("	inf.id_local = ? ");
		sbSQL.append("	AND inf.pista = ? ");
		sbSQL.append("	AND CAST(inf.data AS DATE) = ? ");
		sbSQL.append("	AND (enq.id_enquadramento = 1 "); ;// Imagem teste
		sbSQL.append("	OR enq.infracao_metrologia= ?) "); 
		
		int iRet = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idLocal);
			ps.setShort(2, idPista);
			ps.setDate(3, new java.sql.Date(dia.getTime()));
			ps.setBoolean(4, metrologica);
			
			rs = ps.executeQuery();
			if (rs.next())
				iRet = rs.getInt(1);
		}
		finally {
			if (conn != null)
				conn.close();
		}
		return iRet;
	}	
	
}
