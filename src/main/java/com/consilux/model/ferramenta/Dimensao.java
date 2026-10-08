/**
 * 
 */
package com.consilux.model.ferramenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * @author Thiago Surgik
 *
 */
public class Dimensao {
	
	private Integer idVeiculoPesagem, idVeiculo, idClassificacaoQfv, numeroEixos, idVeiculoPesagemDimensao, idVeiculoPesagemEixo;
	private Double pbt, excessoPbt, limitePbtQfv, limitePbtToleradoQfv, altura, comprimento, largura, distanciaEixoAnterior, peso, excesso, limiteCargaQfv;
	private String classificacaoQfv, todosIdClassificacaoQfv, todasClassificacaoQfv, descEixo, descGrupo, descTipoRodado;
	private Boolean possivelInfratorPeso, direcionaEstacionamento, pesagemValida; 
	private byte imagem[];
	
	public Dimensao() {
		super();
	}
	
	private Dimensao(Integer idVeiculoPesagem, Integer idVeiculo, Double pbt, Double excessoPbt, Double limitePbtQfv, Double limitePbtToleradoQfv,
					 Integer idClassificacaoQfv, String classificacaoQfv, String todosIdClassificacaoQfv, String todasClassificacaoQfv,
					 Boolean possivelInfratorPeso, Boolean direcionaEstacionamento, Boolean pesagemValida) {
		this.idVeiculoPesagem = idVeiculoPesagem;
		this.idVeiculo = idVeiculo;
		this.pbt = pbt;
		this.excessoPbt = excessoPbt;
		this.limitePbtQfv = limitePbtQfv;
		this.limitePbtToleradoQfv = limitePbtToleradoQfv;
		this.idClassificacaoQfv = idClassificacaoQfv;
		this.classificacaoQfv = classificacaoQfv;
		this.todosIdClassificacaoQfv = todosIdClassificacaoQfv;
		this.todasClassificacaoQfv = todasClassificacaoQfv;
		this.possivelInfratorPeso = possivelInfratorPeso;
		this.direcionaEstacionamento = direcionaEstacionamento;
		this.pesagemValida = pesagemValida;
	}
	
	private Dimensao(Integer idVeiculoPesagemDimensao, Integer idVeiculoPesagem, Integer idVeiculo, Double altura, Double largura, Double comprimento) {
		this.idVeiculoPesagemDimensao = idVeiculoPesagemDimensao;
		this.idVeiculoPesagem = idVeiculoPesagem;
		this.idVeiculo = idVeiculo;
		this.altura = altura;
		this.largura = largura;
		this.comprimento = comprimento;
	}
	
	private Dimensao(Integer idVeiculoPesagemEixo, Integer idVeiculoPesagem, Integer idVeiculo,
					 String descEixo, Double peso, Double distanciaEixoAnterior, String descTipoRodado) {
		this.idVeiculoPesagemEixo = idVeiculoPesagemEixo;
		this.idVeiculoPesagem = idVeiculoPesagem;
		this.idVeiculo = idVeiculo;
		this.descEixo = descEixo;
		this.peso = peso;
		this.distanciaEixoAnterior = distanciaEixoAnterior;
		this.descTipoRodado = descTipoRodado;
	}
	
	private Dimensao(Integer idVeiculoPesagemEixo, Integer idVeiculoPesagem, Integer idVeiculo,
			 		 String descGrupo, Double peso, Double excesso, Double limiteCargaQfv) {
		this.idVeiculoPesagemEixo = idVeiculoPesagemEixo;
		this.idVeiculoPesagem = idVeiculoPesagem;
		this.idVeiculo = idVeiculo;
		this.descGrupo = descGrupo;
		this.peso = peso;
		this.excesso = excesso;
		this.limiteCargaQfv = limiteCargaQfv;
	}
	
	private Dimensao(Integer idVeiculoPesagemDimensao, Integer idVeiculoPesagem, Integer idVeiculo,
					 Integer idClassificacaoQfv, String classificacaoQfv, String todosIdClassificacaoQfv, String todasClassificacaoQfv,
					 Double pbt, Double excessoPbt, Double altura, Double largura, Double comprimento) {
		this.idVeiculoPesagemDimensao = idVeiculoPesagemDimensao;
		this.idVeiculoPesagem = idVeiculoPesagem;
		this.idVeiculo = idVeiculo;
		this.idClassificacaoQfv = idClassificacaoQfv;
		this.classificacaoQfv = classificacaoQfv;
		this.todosIdClassificacaoQfv = todosIdClassificacaoQfv;
		this.todasClassificacaoQfv = todasClassificacaoQfv;
		this.pbt = pbt;
		this.excessoPbt = excessoPbt;
		this.altura = altura;
		this.largura = largura;
		this.comprimento = comprimento;
	}
	
	private Dimensao(byte imagem[]) {
		this.imagem = imagem;
	}
	
	/**
	 * Busca dados gerais de pesagem do veículo no BD.
	 */
	public static Dimensao buscaDadosPesagemPorIdVeiculo(Integer idVeiculo) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT vp.id_veiculo_pesagem, ");
		sbSQL.append("   	  vp.id_veiculo, ");
		sbSQL.append("   	  vp.pbt, ");
		sbSQL.append("   	  vp.excesso_pbt, ");
		sbSQL.append("   	  vp.limite_pbt_qfv, ");
		sbSQL.append("   	  vp.limite_pbt_tolerado_qfv, ");
		sbSQL.append("   	  vp.id_classificacao_qfv, ");
		sbSQL.append("   	  vp.classificacao_qfv, ");
		sbSQL.append("   	  vp.todos_id_classificacao_qfv, ");
		sbSQL.append("   	  vp.todas_classificacao_qfv, ");
		sbSQL.append("   	  vp.possivel_infrator_peso, ");
		sbSQL.append("   	  vp.direciona_estacionamento, ");
		sbSQL.append("   	  vp.pesagem_valida ");
		sbSQL.append(" FROM   veiculo_pesagem vp (NOLOCK) ");
		sbSQL.append(" WHERE  vp.id_veiculo = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("   	  vp.id_veiculo_pesagem ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idVeiculo);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				return new Dimensao(
							rs.getInt("id_veiculo_pesagem"),
							rs.getInt("id_veiculo"),
							rs.getDouble("pbt"),
							rs.getDouble("excesso_pbt"),
							rs.getDouble("limite_pbt_qfv"),
							rs.getDouble("limite_pbt_tolerado_qfv"),
							rs.getInt("id_classificacao_qfv"),
							rs.getString("classificacao_qfv"),
							rs.getString("todos_id_classificacao_qfv"),
							rs.getString("todas_classificacao_qfv"),
							rs.getBoolean("possivel_infrator_peso"),
							rs.getBoolean("direciona_estacionamento"),
							rs.getBoolean("pesagem_valida")
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
	
	
	/**
	 * Busca dimensões do veículo no BD.
	 */
	public static List<Dimensao> buscaDimensaoPorIdVeiculo(Integer idVeiculo) throws ConexaoException, SQLException {
		
		List<Dimensao> lRet = new ArrayList<Dimensao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT vpd.id_veiculo_pesagem_dimensao, ");
		sbSQL.append("   	  vpd.id_veiculo_pesagem, ");
		sbSQL.append("   	  vpd.id_veiculo, ");
		sbSQL.append("   	  vpd.altura, ");
		sbSQL.append("   	  vpd.largura, ");
		sbSQL.append("   	  vpd.comprimento ");
		sbSQL.append(" FROM   veiculo_pesagem_dimensao vpd (NOLOCK) ");
		sbSQL.append(" WHERE  vpd.id_veiculo = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("   	  vpd.id_veiculo_pesagem, ");
		sbSQL.append("   	  vpd.id_veiculo_pesagem_dimensao ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idVeiculo);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new Dimensao(
							rs.getInt("id_veiculo_pesagem_dimensao"),
							rs.getInt("id_veiculo_pesagem"),
							rs.getInt("id_veiculo"),
							rs.getDouble("altura"),
							rs.getDouble("largura"),
							rs.getDouble("comprimento")
						));
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return lRet;
		
	}
	
	
	/**
	 * Busca peso por eixo do veículo no BD.
	 */
	public static List<Dimensao> buscaPesoEixoPorIdVeiculo(Integer idVeiculo) throws ConexaoException, SQLException {
		
		List<Dimensao> lRet = new ArrayList<Dimensao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT vpe.id_veiculo_pesagem_eixo, ");
		sbSQL.append("   	  vpe.id_veiculo_pesagem, ");
		sbSQL.append("   	  vpe.id_veiculo, ");
		sbSQL.append("   	  LTRIM(RTRIM(vpe.descricao)) AS descricao, ");
		sbSQL.append("   	  vpe.peso, ");
		sbSQL.append("   	  vpe.distancia_eixo_anterior, ");
		sbSQL.append("   	  LTRIM(RTRIM(vpe.tipo_rodado)) AS tipo_rodado ");
		sbSQL.append(" FROM   veiculo_pesagem_eixo vpe (NOLOCK) ");
		sbSQL.append(" WHERE  vpe.id_veiculo = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("   	  vpe.id_veiculo_pesagem, ");
		sbSQL.append("   	  vpe.id_veiculo_pesagem_eixo ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idVeiculo);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new Dimensao(
							rs.getInt("id_veiculo_pesagem_eixo"),
							rs.getInt("id_veiculo_pesagem"),
							rs.getInt("id_veiculo"),
						    rs.getString("descricao"),
							rs.getDouble("peso"),
							rs.getDouble("distancia_eixo_anterior"),
							rs.getString("tipo_rodado")
						));
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return lRet;
		
	}
	
	
	/**
	 * Busca peso por grupo de eixos do veículo no BD.
	 */
	public static List<Dimensao> buscaPesoGrupoPorIdVeiculo(Integer idVeiculo) throws ConexaoException, SQLException {
		
		List<Dimensao> lRet = new ArrayList<Dimensao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT vpg.id_veiculo_pesagem_grupo, ");
		sbSQL.append("   	  vpg.id_veiculo_pesagem, ");
		sbSQL.append("   	  vpg.id_veiculo, ");
		sbSQL.append("   	  LTRIM(RTRIM(vpg.descricao)) AS descricao, ");
		sbSQL.append("   	  vpg.peso, ");
		sbSQL.append("   	  vpg.excesso, ");
		sbSQL.append("   	  vpg.limite_carga_qfv ");
		sbSQL.append(" FROM   veiculo_pesagem_grupo vpg (NOLOCK) ");
		sbSQL.append(" WHERE  vpg.id_veiculo = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("   	  vpg.id_veiculo_pesagem, ");
		sbSQL.append("   	  vpg.id_veiculo_pesagem_grupo ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idVeiculo);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new Dimensao(
						rs.getInt("id_veiculo_pesagem_grupo"),
						rs.getInt("id_veiculo_pesagem"),
						rs.getInt("id_veiculo"),
					    rs.getString("descricao"),
						rs.getDouble("peso"),
						rs.getDouble("excesso"),
						rs.getDouble("limite_carga_qfv")
					));
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return lRet;
		
	}
	
	
	/**
	 * Busca peso e dimensão do veículo no BD.
	 */
	public static Dimensao buscaPesoDimensaoPorIdVeiculo(Integer idVeiculo) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT vpd.id_veiculo_pesagem_dimensao, ");
		sbSQL.append("   	  vpd.id_veiculo_pesagem, ");
		sbSQL.append("   	  vpd.id_veiculo, ");
		sbSQL.append("   	  vp.id_classificacao_qfv, ");
		sbSQL.append("   	  LTRIM(RTRIM(vp.classificacao_qfv)) AS classificacao_qfv, ");
		sbSQL.append("   	  LTRIM(RTRIM(vp.todos_id_classificacao_qfv)) AS todos_id_classificacao_qfv, ");
		sbSQL.append("   	  LTRIM(RTRIM(vp.todas_classificacao_qfv)) AS todas_classificacao_qfv, ");
		sbSQL.append("   	  vp.pbt, ");
		sbSQL.append("   	  vp.excesso_pbt, ");
		sbSQL.append("   	  vpd.altura, ");
		sbSQL.append("   	  vpd.largura, ");
		sbSQL.append("   	  vpd.comprimento ");
		sbSQL.append(" FROM   veiculo_pesagem vp (NOLOCK) ");
		sbSQL.append(" 		  INNER JOIN veiculo_pesagem_dimensao vpd (NOLOCK) ");
		sbSQL.append(" 			   ON  vpd.id_veiculo = vp.id_veiculo ");
		sbSQL.append(" 				   AND vpd.id_veiculo_pesagem = vp.id_veiculo_pesagem ");
		sbSQL.append(" WHERE  vpd.id_veiculo = ? ");
//		sbSQL.append(" 		  AND (vp.todas_classificacao_qfv IS NOT NULL AND LTRIM(RTRIM(vp.todas_classificacao_qfv)) <> 'N.A.') ");
		sbSQL.append(" 		  AND vp.pbt > 0  ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("   	  vpd.id_veiculo_pesagem, ");
		sbSQL.append("   	  vpd.id_veiculo_pesagem_dimensao ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idVeiculo);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				return new Dimensao(
							rs.getInt("id_veiculo_pesagem_dimensao"),
							rs.getInt("id_veiculo_pesagem"),
							rs.getInt("id_veiculo"),
							rs.getInt("id_classificacao_qfv"),
							rs.getString("classificacao_qfv"),
							rs.getString("todos_id_classificacao_qfv"),
							rs.getString("todas_classificacao_qfv"),
							rs.getDouble("pbt"),
							rs.getDouble("excesso_pbt"),
							rs.getDouble("altura"),
							rs.getDouble("largura"),
							rs.getDouble("comprimento")
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
	
	
	/**
	 * Busca imagem da classificação do veículo no BD.
	 */
	public static Dimensao buscaImagemClassificacaoPorIdVeiculo(Integer idVeiculo) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT TOP(1) pqt.imagem ");
		sbSQL.append(" FROM   ppv_veiculo_pesagem pvp (NOLOCK) ");
		sbSQL.append(" 		  INNER JOIN ppv_qfv_tipos pqt (NOLOCK) ");
		sbSQL.append(" 			   ON  pqt.id_classificacao = pvp.id_classificacao ");
		sbSQL.append(" WHERE  pvp.id_veiculo = ? ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idVeiculo);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				return new Dimensao(
							rs.getBytes("imagem")
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
	
	
	/**
	 * Busca imagem da classificação do veículo no BD.
	 */
	public static Dimensao buscaImagemClassificacaoPorIdClassificacao(String classificacaoQfv) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT TOP(1) pqt.imagem ");
		sbSQL.append(" FROM   ppv_qfv_tipos pqt (NOLOCK) ");
		sbSQL.append(" WHERE  LTRIM(RTRIM(pqt.id_classificacao)) = ? ");
		sbSQL.append(" 		  AND pqt.imagem IS NOT NULL ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, classificacaoQfv.trim());
			rs = ps.executeQuery();
			
			if (rs.next()) {
				return new Dimensao(
							rs.getBytes("imagem")
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

	
	public Integer getIdVeiculoPesagem() {
		return idVeiculoPesagem;
	}
	public void setIdVeiculoPesagem(Integer idVeiculoPesagem) {
		this.idVeiculoPesagem = idVeiculoPesagem;
	}


	public Integer getIdVeiculo() {
		return idVeiculo;
	}
	public void setIdVeiculo(Integer idVeiculo) {
		this.idVeiculo = idVeiculo;
	}

	
	public Integer getIdClassificacaoQfv() {
		return idClassificacaoQfv;
	}
	public void setIdClassificacaoQfv(Integer idClassificacaoQfv) {
		this.idClassificacaoQfv = idClassificacaoQfv;
	}

	
	public Integer getNumeroEixos() {
		return numeroEixos;
	}
	public void setNumeroEixos(Integer numeroEixos) {
		this.numeroEixos = numeroEixos;
	}
	
		
	public Integer getIdVeiculoPesagemDimensao() {
		return idVeiculoPesagemDimensao;
	}
	public void setIdVeiculoPesagemDimensao(Integer idVeiculoPesagemDimensao) {
		this.idVeiculoPesagemDimensao = idVeiculoPesagemDimensao;
	}

		
	public Integer getIdVeiculoPesagemEixo() {
		return idVeiculoPesagemEixo;
	}
	public void setIdVeiculoPesagemEixo(Integer idVeiculoPesagemEixo) {
		this.idVeiculoPesagemEixo = idVeiculoPesagemEixo;
	}

	
	public Double getPbt() {
		return pbt;
	}
	public void setPbt(Double pbt) {
		this.pbt = pbt;
	}

	
	public Double getExcessoPbt() {
		return excessoPbt;
	}
	public void setExcessoPbt(Double excessoPbt) {
		this.excessoPbt = excessoPbt;
	}

	
	public Double getLimitePbtQfv() {
		return limitePbtQfv;
	}
	public void setLimitePbtQfv(Double limitePbtQfv) {
		this.limitePbtQfv = limitePbtQfv;
	}

	
	public Double getLimitePbtToleradoQfv() {
		return limitePbtToleradoQfv;
	}
	public void setLimitePbtToleradoQfv(Double limitePbtToleradoQfv) {
		this.limitePbtToleradoQfv = limitePbtToleradoQfv;
	}

	
	public Double getAltura() {
		return altura;
	}
	public void setAltura(Double altura) {
		this.altura = altura;
	}

	
	public Double getComprimento() {
		return comprimento;
	}
	public void setComprimento(Double comprimento) {
		this.comprimento = comprimento;
	}

	
	public Double getLargura() {
		return largura;
	}
	public void setLargura(Double largura) {
		this.largura = largura;
	}

	
	public Double getDistanciaEixoAnterior() {
		return distanciaEixoAnterior;
	}
	public void setDistanciaEixoAnterior(Double distanciaEixoAnterior) {
		this.distanciaEixoAnterior = distanciaEixoAnterior;
	}


	public Double getPeso() {
		return peso;
	}
	public void setPeso(Double peso) {
		this.peso = peso;
	}

	
	public Double getExcesso() {
		return excesso;
	}
	public void setExcesso(Double excesso) {
		this.excesso = excesso;
	}
	
	
	public Double getLimiteCargaQfv() {
		return limiteCargaQfv;
	}
	public void setLimiteCargaQfv(Double limiteCargaQfv) {
		this.limiteCargaQfv = limiteCargaQfv;
	}

	
	public String getClassificacaoQfv() {
		return classificacaoQfv;
	}
	public void setClassificacaoQfv(String classificacaoQfv) {
		this.classificacaoQfv = classificacaoQfv;
	}

	
	public String getTodosIdClassificacaoQfv() {
		return todosIdClassificacaoQfv;
	}
	public void setTodosIdClassificacaoQfv(String todosIdClassificacaoQfv) {
		this.todosIdClassificacaoQfv = todosIdClassificacaoQfv;
	}

	
	public String getTodasClassificacaoQfv() {
		return todasClassificacaoQfv;
	}
	public void setTodasClassificacaoQfv(String todasClassificacaoQfv) {
		this.todasClassificacaoQfv = todasClassificacaoQfv;
	}

	
	public String getDescEixo() {
		return descEixo;
	}
	public void setDescEixo(String descEixo) {
		this.descEixo = descEixo;
	}
	
	
	public String getDescGrupo() {
		return descGrupo;
	}
	public void setDescGrupo(String descGrupo) {
		this.descGrupo = descGrupo;
	}

	
	public String getDescTipoRodado() {
		return descTipoRodado;
	}
	public void setDescTipoRodado(String descTipoRodado) {
		this.descTipoRodado = descTipoRodado;
	}

	
	public Boolean getPossivelInfratorPeso() {
		return possivelInfratorPeso;
	}
	public void setPossivelInfratorPeso(Boolean possivelInfratorPeso) {
		this.possivelInfratorPeso = possivelInfratorPeso;
	}


	public Boolean getDirecionaEstacionamento() {
		return direcionaEstacionamento;
	}
	public void setDirecionaEstacionamento(Boolean direcionaEstacionamento) {
		this.direcionaEstacionamento = direcionaEstacionamento;
	}

	
	public Boolean getPesagemValida() {
		return pesagemValida;
	}
	public void setPesagemValida(Boolean pesagemValida) {
		this.pesagemValida = pesagemValida;
	}

	
	public byte[] getImagem() {
		return imagem;
	}
	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}	
}
