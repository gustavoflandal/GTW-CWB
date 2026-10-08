/**
 * Projeto: GTW
 * Nome do Modulo: GTW
 * Empresa: Consilux Tecnologia
 * @author Thiago Surgik
 * Data: 26/01/2016
 */

package com.consilux.model;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de dados dos lotes reprovados
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 26/01/2016
 */

public  class LoteReprovado implements Serializable {
	
	protected final static Logger logger = Logger.getLogger(Remessa.class);
	private static final long serialVersionUID = 2035188461287315850L;
	private Integer idRemessa;
	private String tipo;
	private Integer codigoExterno;
	private Date dataInicial;
	private Integer revisao;
	private Date dataProcesso;
	private Date dataAtualizacao;
	private String mensagem;
	private Integer atraso;
	private Integer atrasoProcesso;
	private Integer totalInfracao;
	private boolean reposicionar;
	private boolean detalhes;


	public LoteReprovado(Integer idRemessa, String tipo, Integer codigoExterno, Date dataInicial, Integer revisao,
						 Date dataProcesso, Date dataAtualizacao, String mensagem, Integer atraso, Integer atrasoProcesso, Integer totalInfracao, boolean reposicionar, boolean detalhes){
		super();
		this.idRemessa = idRemessa;
		this.tipo = tipo;
		this.codigoExterno = codigoExterno;
		this.dataInicial = dataInicial;
		this.revisao = revisao;
		this.dataProcesso = dataProcesso;
		this.dataAtualizacao = dataAtualizacao;
		this.mensagem = mensagem;
		this.atraso = atraso;
		this.atrasoProcesso = atrasoProcesso;
		this.totalInfracao = totalInfracao;
		this.reposicionar = reposicionar;
		this.detalhes = detalhes;
	}


	public Integer getIdRemessa() {
		return idRemessa;
	}
	public void setIdRemessa(Integer idRemessa) {
		this.idRemessa = idRemessa;
	}


	public Integer getCodigoExterno() {
		return codigoExterno;
	}
	public void setCodigoExterno(Integer codigoExterno) {
		this.codigoExterno = codigoExterno;
	}


	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}


	public Integer getRevisao() {
		return revisao;
	}
	public void setRevisao(Integer revisao) {
		this.revisao = revisao;
	}


	public Date getDataProcesso() {
		return dataProcesso;
	}
	public void setDataProcesso(Date dataProcesso) {
		this.dataProcesso = dataProcesso;
	}


	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
	
	
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}


	public Integer getAtraso() {
		return atraso;
	}
	public void setAtraso(Integer atraso) {
		this.atraso = atraso;
	}

	public Integer getTotalInfracao() {
		return totalInfracao;
	}
	public void setTotalInfracao(Integer totalInfracao) {
		this.totalInfracao = totalInfracao;
	}


	/**
	 * Autor: Thiago Surgik 28/01/2016
	 * Obter lotes reprovados.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */	
	public static HashMap<Integer, LoteReprovado> obterLoteReprovado(int quantidade) throws ConexaoException, SQLException {

		logger.info("Iniciando busca de Lotes Reprovados");
		Date dt1 = Calendar.getInstance().getTime(), dt2 = null;
		
		HashMap<Integer, LoteReprovado> lRet = new LinkedHashMap<Integer, LoteReprovado>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT lr.id_remessa ");
		sbSQL.append("    	 ,lr.id_remessa_cav ");
		sbSQL.append("    	 ,lr.tipo ");
		sbSQL.append("    	 ,lr.codigo_externo ");
		sbSQL.append("    	 ,lr.data_inicial ");
		sbSQL.append("    	 ,lr.revisao ");
		sbSQL.append("    	 ,lr.data_processo ");
		sbSQL.append("    	 ,lr.data_atualizacao ");
		sbSQL.append("    	 ,lr.mensagem ");
		sbSQL.append("    	 ,lr.ativo ");
		sbSQL.append("    	 ,lr.atraso ");
		sbSQL.append("    	 ,lr.total_infracao ");
		sbSQL.append("    	 ,lr.atraso_processo ");
		sbSQL.append("    	 ,lr.reposicionar ");
		sbSQL.append("    	 ,lr.possui_detalhe ");
		sbSQL.append(" FROM   fcn_ObterLoteReprovado() lr ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  lr.atraso DESC ");
		sbSQL.append(" 		 ,lr.tipo ");
		sbSQL.append(" 		 ,lr.codigo_externo ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int quantidade_at = 0;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			
			while (rs.next() && (quantidade == 0 || quantidade_at < quantidade)) {
				quantidade_at++;
				
				LoteReprovado loteReprovado = new LoteReprovado(
						rs.getInt("id_remessa"),
						rs.getString("tipo"),
						rs.getInt("codigo_externo"),
						rs.getTimestamp("data_inicial"),
						rs.getInt("revisao"),
						rs.getTimestamp("data_processo"),
						rs.getTimestamp("data_atualizacao"),
						rs.getString("mensagem"),
						rs.getInt("atraso"),
						rs.getInt("atraso_processo"),
						rs.getInt("total_infracao"),
						rs.getBoolean("reposicionar"),
						rs.getBoolean("possui_detalhe")
				);
				
				lRet.put(loteReprovado.getIdRemessa(), loteReprovado);
				
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		
		dt2 = Calendar.getInstance().getTime();
		logger.info("Termino da busca de Lotes Repovados em " + (dt2.getTime() - dt1.getTime()) + " ms");
		
		return lRet;
	}

	/**
	 * Autor: Thiago Surgik 28/01/2016
	 * Reposicionar infrações do lote.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */	
	public static boolean reposicionaLoteReprovado(Integer idRemessa) throws ConexaoException, SQLException, ModelException {

		StringBuilder sbSQL = new StringBuilder();
		boolean retorno = false;
		
		sbSQL.append(" {? = call spu_reposiciona_lote_reprovado(?)} ");
		
		Connection conn = null;
		CallableStatement cs = null;

		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(sbSQL.toString());
			
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, idRemessa);

			cs.execute();
			retorno = cs.getInt(1) > 0;
			
		} catch (SQLException ex) {
			StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao reposicionar lote reprovado.");
			throw new ModelException(sbErro.toString(), ex);
		
		} finally {
			if (conn != null)
				conn.close();
		}
		return retorno;
	}


	public Integer getAtrasoProcesso() {
		return atrasoProcesso;
	}


	public void setAtrasoProcesso(Integer atrasoProcesso) {
		this.atrasoProcesso = atrasoProcesso;
	}


	public boolean isReposicionar() {
		return reposicionar;
	}


	public void setReposicionar(boolean reposicionar) {
		this.reposicionar = reposicionar;
	}


	public boolean isDetalhes() {
		return detalhes;
	}


	public void setDetalhes(boolean detalhes) {
		this.detalhes = detalhes;
	}
	
}
