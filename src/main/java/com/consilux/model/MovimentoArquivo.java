package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

public class MovimentoArquivo {

	protected final static Logger logger = Logger.getLogger(MovimentoArquivo.class);
	
	public enum TipoArquivoML {
		IMAGEM(1),
		OBLITERACAO(2),
		VIDEO(3),
		TARJA(4),
		MOVIMENTO_LOTE(5),
		LOTE_VALIDADO(6);
	
		private Integer id;
		TipoArquivoML(Integer id) {
			this.id = id;
		}
		public Integer getId() {
			return id;
		}
		public static TipoArquivoML valueOfId(Integer id) {
			for (TipoArquivoML tipo: values()) {
				if (tipo.getId() == id.intValue())
					return tipo;
			}
			return null;
		}
	}
	

	private Integer idMovimentoArquivo;
	private Integer idTipo;
	private Integer idMovimento;
	private Integer sequencia;
	private Integer indiceImagem;
	private Date dataMovimento;
	private String dsCaminho;
	private String nomeArquivo;
	private Integer revisao;
	private Integer numeroRegistros;
	private Date dataValidacao;
	private Date dataArquivo;
	private Date dataImportacao;
	
	
	public MovimentoArquivo(Integer idMovimentoArquivo, Integer idTipo, Integer idMovimento, Integer sequencia, Integer indiceImagem, Date dataMovimento,
			String dsCaminho, String nomeArquivo, Integer revisao, Integer numeroRegistros, Date dataValidacao, Date dataArquivo, Date dataImportacao) {
		super();
		this.idMovimentoArquivo = idMovimentoArquivo;
		this.idTipo = idTipo;
		this.idMovimento = idMovimento;
		this.sequencia = sequencia;
		this.indiceImagem = indiceImagem;
		this.dataMovimento = dataMovimento;
		this.dsCaminho = dsCaminho;
		this.nomeArquivo = nomeArquivo;
		this.revisao = revisao;
		this.numeroRegistros = numeroRegistros;
		this.revisao = revisao;
		this.dataValidacao = dataValidacao;
		this.dataArquivo = dataArquivo;
		this.dataImportacao = dataImportacao;
	}
	

	/**
	 * Autor: Thiago Surgik 25/11/2015
	 * Busca os dados do Movimento Arquivo.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	public static MovimentoArquivo getDadosMovimentoArquivo(Integer codigoExterno, String tipo_remessa) throws ConexaoException, SQLException, ModelException {

		StringBuilder sbSQL = new StringBuilder();
		MovimentoArquivo maRet = null;

		sbSQL.append(" SELECT dma.id_movimento_arquivo ");
		sbSQL.append("		 ,dma.id_tipo ");
		sbSQL.append("		 ,dma.id_movimento ");
		sbSQL.append("		 ,dma.sequencia ");
		sbSQL.append("		 ,dma.indice_imagem ");
		sbSQL.append("		 ,dma.data_movimento ");
		sbSQL.append("		 ,dma.ds_caminho ");
		sbSQL.append("		 ,dma.nome_arquivo ");
		sbSQL.append("		 ,dma.revisao ");
		sbSQL.append("		 ,dma.numero_registros ");
		sbSQL.append("		 ,dma.data_validacao ");
		sbSQL.append("		 ,dma.data_arquivo ");
		sbSQL.append("		 ,dma.data_importacao ");
		sbSQL.append(" FROM   dbo.fcn_getDadosMovimentoArquivo(?, ?, ?) dma ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, codigoExterno);
			ps.setString(2, tipo_remessa);
			ps.setInt(3, TipoArquivoML.MOVIMENTO_LOTE.getId());

			rs = ps.executeQuery();
			
			if (rs.next()) {
				maRet = new MovimentoArquivo(rs.getInt("id_movimento_arquivo"),
											 rs.getInt("id_tipo"),
											 rs.getInt("id_movimento"),
											 rs.getInt("sequencia"),
											 rs.getInt("indice_imagem"),
											 rs.getDate("data_movimento"),
											 rs.getString("ds_caminho"),
											 rs.getString("nome_arquivo"),
											 rs.getInt("revisao"),
											 rs.getInt("numero_registros"),
											 rs.getDate("data_validacao"),
											 rs.getDate("data_arquivo"),
											 rs.getDate("data_importacao"));
			}
		} catch (SQLException ex) {
			StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao buscar dados do movimento.");
			throw new ModelException(sbErro.toString(), ex);
		} finally {
			if (conn != null)
				conn.close();
		}
		return maRet;
	}
	
	
	public static List<String> ListarArquivosML(TipoArquivoML tipoArquivoML, Integer codigoExterno, String tipo_remessa) throws ConexaoException, SQLException {

		List<String> arquivos = new ArrayList<String>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT lam.id_movimento_arquivo ");
		sbSQL.append("		 ,lam.id_tipo ");
		sbSQL.append("		 ,lam.id_movimento ");
		sbSQL.append("		 ,lam.sequencia ");
		sbSQL.append("		 ,lam.indice_imagem ");
		sbSQL.append("		 ,lam.data_movimento ");
		sbSQL.append("		 ,lam.ds_caminho ");
		sbSQL.append("		 ,lam.nome_arquivo ");
		sbSQL.append("		 ,lam.revisao ");
		sbSQL.append("		 ,lam.numero_registros ");
		sbSQL.append("		 ,lam.data_validacao ");
		sbSQL.append("		 ,lam.data_arquivo ");
		sbSQL.append("		 ,lam.data_importacao ");
		sbSQL.append(" FROM   dbo.fcn_listarArquivosML(?, ?, ?) lam ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, codigoExterno);
			ps.setString(2, tipo_remessa);
			ps.setInt(3, tipoArquivoML.getId());

			rs = ps.executeQuery();

			while (rs.next()) {
				arquivos.add(rs.getString("nome_arquivo"));
			}
			
		} catch (SQLException e) {
			throw new SQLException("Erro ao montar SQL.", e);
		} finally {
			if (conn != null)
				conn.close();
		}
		return arquivos;
	}


	public static List<Integer> getSequenciaMovimentoArquivo(Integer codigoExterno, String tipo_remessa) throws ConexaoException, SQLException, ModelException {

		StringBuilder sbSQL = new StringBuilder();
		List<Integer> sequenciaLote = new ArrayList<Integer>();

		sbSQL.append(" SELECT ir.sequencia ");
		sbSQL.append(" FROM   remessa r (NOLOCK) ");
		sbSQL.append(" 		  INNER JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append(" 			   ON  ir.id_remessa = r.id_remessa ");
		sbSQL.append(" 		  INNER JOIN infracao i (NOLOCK) ");
		sbSQL.append(" 			   ON  i.id_infracao = ir.id_infracao ");
		sbSQL.append(" 		  INNER JOIN veiculo_imagem vi (NOLOCK) ");
		sbSQL.append(" 			   ON  vi.id_veiculo = i.id_veiculo ");
		sbSQL.append(" 		  INNER JOIN imagem img (NOLOCK) ");
		sbSQL.append(" 			   ON  img.id_imagem = vi.id_imagem ");
		sbSQL.append(" WHERE  r.codigo_externo = ? ");
		sbSQL.append(" 		  AND r.tipo = ? ");
		sbSQL.append(" 		  AND img.indice_imagem = 0 ");
		sbSQL.append(" GROUP BY ");
		sbSQL.append(" 		  ir.sequencia ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  ir.sequencia ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, codigoExterno);
			ps.setString(2, tipo_remessa);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				sequenciaLote.add(rs.getInt("sequencia"));
			}
			
		} catch (SQLException ex) {
			StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao buscar sequencia do movimento.");
			throw new ModelException(sbErro.toString(), ex);
		} finally {
			if (conn != null)
				conn.close();
		}
		return sequenciaLote;
	}
	
	public Integer getIdMovimentoArquivo() {
		return idMovimentoArquivo;
	}
	public void setIdMovimentoArquivo(Integer idMovimentoArquivo) {
		this.idMovimentoArquivo = idMovimentoArquivo;
	}

	public Integer getIdTipo() {
		return idTipo;
	}
	public void setIdTipo(Integer idTipo) {
		this.idTipo = idTipo;
	}

	public Integer getIdMovimento() {
		return idMovimento;
	}
	public void setIdMovimento(Integer idMovimento) {
		this.idMovimento = idMovimento;
	}

	public Integer getSequencia() {
		return sequencia;
	}
	public void setSequencia(Integer sequencia) {
		this.sequencia = sequencia;
	}

	public Integer getIndiceImagem() {
		return indiceImagem;
	}
	public void setIndiceImagem(Integer indiceImagem) {
		this.indiceImagem = indiceImagem;
	}

	public Date getDataMovimento() {
		return dataMovimento;
	}
	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public String getDsCaminho() {
		return dsCaminho;
	}
	public void setDsCaminho(String dsCaminho) {
		this.dsCaminho = dsCaminho;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public Integer getRevisao() {
		return revisao;
	}
	public void setRevisao(Integer revisao) {
		this.revisao = revisao;
	}

	public Integer getNumeroRegistros() {
		return numeroRegistros;
	}
	public void setNumeroRegistros(Integer numeroRegistros) {
		this.numeroRegistros = numeroRegistros;
	}

	public Date getDataValidacao() {
		return dataValidacao;
	}
	public void setDataValidacao(Date dataValidacao) {
		this.dataValidacao = dataValidacao;
	}

	public Date getDataArquivo() {
		return dataArquivo;
	}
	public void setDataArquivo(Date dataArquivo) {
		this.dataArquivo = dataArquivo;
	}

	public Date getDataImportacao() {
		return dataImportacao;
	}
	public void setDataImportacao(Date dataImportacao) {
		this.dataImportacao = dataImportacao;
	}

}
