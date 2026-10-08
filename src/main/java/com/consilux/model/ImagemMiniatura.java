/**********************************************************************************
  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 13/07/2021

  Descricao: Classe para manipulação de configurações de imagem miniatura para AITs

  Historico:

*********************************************************************************/
package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 13/07/2021
 */
public class ImagemMiniatura {
	
	private static Logger logger = Logger.getLogger(ImagemMiniatura.class);
	
	private Integer IdInfracao;
	private Integer idImagemPrincipal;
	private Integer idImagemMiniatura;
	private Integer idPosicao;
	private String posicao;
	private Integer idTamanho;
	private Float porcentagem;
	private String tamanho;
	
	public enum PosicaoMiniatura { 
		SUPERIOR_ESQUERDO(1, "Canto superior esquerdo"),
		SUPERIOR_DIREITO(2, "Canto superior direito"),
		INFERIOR_ESQUERDO(3, "Canto inferior esquerdo"),
		INFERIOR_DIREITO(4, "Canto inferior direito");

		private Integer id;
		private String valor;
		
		PosicaoMiniatura(Integer id, String valor) {
			this.id = id;
			this.valor = valor;
		}
		
		public Integer getId() {
			return id;
		}
		public String getValor() {
			return valor;
		}
		
		public static PosicaoMiniatura valueOfId(Integer id) {
			for (PosicaoMiniatura posicao: values()) {
				if (posicao.getId() == id.intValue())
					return posicao;
			}
			return null;
		}
	}
	public enum TamanhoMiniatura {
		PEQUENA(1, 0.2F),
		MEDIA(2, 0.35F),
		GRANDE(3, 0.5F);
		
		private Integer id;
		private float porcentagem;
		
		TamanhoMiniatura(Integer id, float porcentagem) {
			this.id = id;
			this.porcentagem = porcentagem;
		}
		
		public Integer getId() {
			return id;
		}
		public float getPorcentagem() {
			return porcentagem;
		}
		
		public static TamanhoMiniatura valueOfId(Integer id) {
			for (TamanhoMiniatura tamanho: values()) {
				if (tamanho.getId() == id.intValue())
					return tamanho;
			}
			return null;
		}
	}


	public ImagemMiniatura(){
		
	}
		
	public ImagemMiniatura(Integer IdInfracao, Integer idImagemPrincipal, Integer idImagemMiniatura, Integer idPosicao, String posicao, Integer idTamanho, Float porcentagem, String tamanho) {
		super();
		this.IdInfracao = IdInfracao;
		this.idImagemPrincipal = idImagemPrincipal;
		this.idImagemMiniatura = idImagemMiniatura;
		this.idPosicao = idPosicao;
		this.posicao = posicao;
		this.idTamanho = idTamanho;
		this.porcentagem = porcentagem;
		this.tamanho = tamanho;
	}
	
	public Integer getIdInfracao() {
		return IdInfracao;
	}
	public void setIdInfracao(Integer idInfracao) {
		IdInfracao = idInfracao;
	}

	public Integer getIdImagemPrincipal() {
		return idImagemPrincipal;
	}
	public void setIdImagemPrincipal(Integer idImagemPrincipal) {
		this.idImagemPrincipal = idImagemPrincipal;
	}

	public Integer getIdImagemMiniatura() {
		return idImagemMiniatura;
	}
	public void setIdImagemMiniatura(Integer idImagemMiniatura) {
		this.idImagemMiniatura = idImagemMiniatura;
	}

	public Integer getIdPosicao() {
		return idPosicao;
	}
	public void setIdPosicao(Integer idPosicao) {
		this.idPosicao = idPosicao;
	}

	public String getPosicao() {
		return posicao;
	}
	public void setPosicao(String posicao) {
		this.posicao = posicao;
	}

	public Integer getIdTamanho() {
		return idTamanho;
	}
	public void setIdTamanho(Integer idTamanho) {
		this.idTamanho = idTamanho;
	}

	public Float getPorcentagem() {
		return porcentagem;
	}
	public void setPorcentagem(Float porcentagem) {
		this.porcentagem = porcentagem;
	}

	public String getTamanho() {
		return tamanho;
	}
	public void setTamanho(String tamanho) {
		this.tamanho = tamanho;
	}

	
	/**
	 * Busca a configuração de miniatura
	 * @param idInfracao Identificador da infração.
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static ImagemMiniatura obterImagemMiniaturaPorIdInfracao(int idInfracao) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT im.id_infracao, ");
		sbSQL.append(" 		  im.id_imagem_principal, ");
		sbSQL.append(" 		  im.id_imagem_miniatura, ");
		sbSQL.append(" 		  pm.id_posicao, ");
		sbSQL.append(" 		  pm.descricao AS posicao, ");
		sbSQL.append(" 		  tm.id_tamanho, ");
		sbSQL.append(" 		  tm.porcentagem, ");
		sbSQL.append(" 		  tm.descricao AS tamanho ");
		sbSQL.append(" FROM   imagem_miniatura im (NOLOCK) ");
		sbSQL.append(" 		  JOIN posicao_miniatura pm (NOLOCK) ON pm.id_posicao = im.id_posicao ");
		sbSQL.append(" 		  JOIN tamanho_miniatura tm (NOLOCK) ON tm.id_tamanho = im.id_tamanho ");
		sbSQL.append(" WHERE  im.id_infracao = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idInfracao);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				return new ImagemMiniatura(
						rs.getInt("id_infracao"),
						rs.getInt("id_imagem_principal"),
						rs.getInt("id_imagem_miniatura"),
						rs.getInt("id_posicao"),
						rs.getString("posicao"),
						rs.getInt("id_tamanho"),
						rs.getFloat("porcentagem"),
						rs.getString("tamanho")
					);
			}
			else {
				logger.warn("Não foi encontrado nenhum registro em obterImagemMiniaturaPorIdInfracao(). idInfracao: " + idInfracao);
				return null;
			}
		} 
		catch(Exception e)
		{
			logger.error("Falha na busca de Imagem Miniatura por Infração. idInfracao: " + idInfracao, e);
			throw new SQLException("Falha na busca de Imagem Miniatura por Infração. idInfracao: " + idInfracao, e);
		}
		finally {
			if (conn != null)
				conn.close();							
		}			
	}
	
	/**
	 * Busca a configuração de miniatura
	 * @param idImagem Identificador da imagem.
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static ImagemMiniatura obterImagemMiniaturaPorIdImagem(int idImagem) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT im.id_infracao, ");
		sbSQL.append(" 		  im.id_imagem_principal, ");
		sbSQL.append(" 		  im.id_imagem_miniatura, ");
		sbSQL.append(" 		  pm.id_posicao, ");
		sbSQL.append(" 		  pm.descricao AS posicao, ");
		sbSQL.append(" 		  tm.id_tamanho, ");
		sbSQL.append(" 		  tm.porcentagem, ");
		sbSQL.append(" 		  tm.descricao AS tamanho ");
		sbSQL.append(" FROM   imagem_miniatura im (NOLOCK) ");
		sbSQL.append(" 		  JOIN posicao_miniatura pm (NOLOCK) ON pm.id_posicao = im.id_posicao ");
		sbSQL.append(" 		  JOIN tamanho_miniatura tm (NOLOCK) ON tm.id_tamanho = im.id_tamanho ");
		sbSQL.append(" WHERE  im.id_infracao IN (SELECT i.id_infracao FROM infracao i (NOLOCK) INNER JOIN veiculo_imagem vi (NOLOCK) ON vi.id_veiculo = i.id_veiculo WHERE vi.id_imagem = ?) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idImagem);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				return new ImagemMiniatura(
						rs.getInt("id_infracao"),
						rs.getInt("id_imagem_principal"),
						rs.getInt("id_imagem_miniatura"),
						rs.getInt("id_posicao"),
						rs.getString("posicao"),
						rs.getInt("id_tamanho"),
						rs.getFloat("porcentagem"),
						rs.getString("tamanho")
					);
			}
			else {
				logger.warn("Não foi encontrado nenhum registro em obterImagemMiniaturaPorIdInfracao(). idInfracao: " + idImagem);
				return null;
			}
		} 
		catch(Exception e)
		{
			logger.error("Falha na busca de Imagem Miniatura por Infração. idInfracao: " + idImagem, e);
			throw new SQLException("Falha na busca de Imagem Miniatura por Infração. idInfracao: " + idImagem, e);
		}
		finally {
			if (conn != null)
				conn.close();							
		}			
	}
	
	/**
	 * Verificar se a infração possui configuração de miniatura
	 * @param idImagem Identificador da imagem.
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static boolean possuiImagemMiniatura(int idImagem) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		boolean retorno = false;

		sbSQL.append(" SELECT 1 AS existe ");
		sbSQL.append(" FROM   imagem_miniatura im (NOLOCK) ");
		sbSQL.append(" WHERE  im.id_infracao IN (SELECT i.id_infracao FROM infracao i (NOLOCK) INNER JOIN veiculo_imagem vi (NOLOCK) ON vi.id_veiculo = i.id_veiculo WHERE vi.id_imagem = ?) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idImagem);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				if(rs.getInt("existe") > 0){
					retorno = true;
				}				
			}
			else {
				logger.warn("Não foi encontrado nenhum registro em obterImagemMiniaturaPorIdInfracao(). idInfracao: " + idImagem);
				retorno = false;
			}
		} 
		catch(Exception e)
		{
			logger.error("Falha na busca de Imagem Miniatura por Infração. idInfracao: " + idImagem, e);
			throw new SQLException("Falha na busca de Imagem Miniatura por Infração. idInfracao: " + idImagem, e);
		}
		finally {
			if (conn != null)
				conn.close();
		}
		
		return retorno;
	}
	
	public static void gravarImagemMiniatura(Integer IdInfracao, Integer idImagemPrincipal, Integer idImagemMiniatura, Integer idPosicao, Integer idTamanho) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("{? = call spu_imagem_miniatura (?, ?, ?, ?, ?)}");

		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(sbSQL.toString());
			
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, IdInfracao);
			cs.setInt(3, idImagemPrincipal);
			cs.setInt(4, idImagemMiniatura);
			cs.setInt(5, idPosicao);
			cs.setInt(6, idTamanho);

			cs.execute();
		} 
		catch(Exception e)
		{
			logger.error("Falha na busca de Imagem Miniatura por Infração. idInfracao: " + IdInfracao, e);
			throw new SQLException("Falha na busca de Imagem Miniatura por Infração. idInfracao: " + IdInfracao, e);
		}
		finally
		{
			if (conn != null)
				conn.close();	
		}
	}
}