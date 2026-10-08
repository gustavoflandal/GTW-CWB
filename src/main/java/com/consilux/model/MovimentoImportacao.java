package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class MovimentoImportacao {
	private Integer idMovimentoArquivo;
	private Date dataMovimento;
	private Integer idMovimento;
	private Integer sequencia;
	private Integer idVeiculoLocal;
	private String placa;
	private Integer pais;
	private Integer idMarcaCet;
	private Integer idEspecie;
	private Integer idEnquadramento;
	private Integer idLocal;
	private String descricaoLocal;
	private Integer codPistaProdam;
	private Date dataHora;
	private Integer velocidadeConstatada;
	private Integer velocidadeConsiderada;
	private Integer velocidadeRegulamentada;
	private String pista;
	private Integer codOperador;
	private Date dataAnalise;
	private Integer registroMontante;
	private Integer consistencia;
	private Integer idInconsistencia;
	private Integer indiceImagem;
	private Boolean validacao;
	
	private String marcaCet;
	private String especie;

	public MovimentoImportacao(Integer id_movimento_arquivo, Date data_movimento, Integer id_movimento, 
			Integer sequencia, Integer id_veiculo_local, String placa, Integer pais, Integer id_marca_cet, 
			Integer id_especie, Integer id_enquadramento, Integer id_local, String descricao_local, 
			Integer cod_pista_prodam, Date data_hora, Integer velocidade_constatada, Integer velocidade_considerada, 
			Integer velocidade_regulamentada, String pista, Integer cod_operador, Date data_analise, 
			Integer registro_montante, Integer consistencia, Integer id_inconsistencia, Integer indice_imagem, 
			Boolean validacao, String marca_cet, String especie) {
		this.idMovimentoArquivo = id_movimento_arquivo;
		this.dataMovimento = data_movimento;
		this.idMovimento = id_movimento;
		this.sequencia = sequencia;
		this.idVeiculoLocal = id_veiculo_local;
		this.placa = placa;
		this.pais = pais;
		this.idMarcaCet = id_marca_cet;
		this.idEspecie = id_especie;
		this.idEnquadramento = id_enquadramento;
		this.idLocal = id_local;
		this.descricaoLocal = descricao_local;
		this.codPistaProdam = cod_pista_prodam;
		this.dataHora = data_hora;
		this.velocidadeConstatada = velocidade_constatada;
		this.velocidadeConsiderada = velocidade_considerada;
		this.velocidadeRegulamentada = velocidade_regulamentada;
		this.pista = pista;
		this.codOperador = cod_operador;
		this.dataAnalise = data_analise;
		this.registroMontante = registro_montante;
		this.consistencia = consistencia;
		this.idInconsistencia = id_inconsistencia;
		this.indiceImagem = indice_imagem;
		this.validacao = validacao;
		
		this.marcaCet = marca_cet;
		this.especie = especie;
	}
	
	public static MovimentoImportacao ObterMovimentoImportacaoPorInfracao(Integer id_infracao) throws ConexaoException {
		MovimentoImportacao lRet = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT mi.*, ");
		sbSQL.append(" cmc.descricao AS marca_cet, ce.descricao AS especie ");
		sbSQL.append(" FROM infracao i (NOLOCK) ");
		sbSQL.append(" JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append("  ON i.id_infracao = ir.id_infracao  ");
		sbSQL.append(" JOIN remessa r (NOLOCK) ");
		sbSQL.append("  ON r.id_remessa = ir.id_remessa  ");
		sbSQL.append(" JOIN movimento_importacao mi (NOLOCK)  ");
		sbSQL.append("  ON r.id_movimento_arquivo = mi.id_movimento_arquivo ");
		sbSQL.append("  AND ir.sequencia = mi.sequencia ");
		sbSQL.append(" LEFT JOIN cad_marca_cet cmc (NOLOCK) ");
		sbSQL.append("  ON cmc.id_marca_cet = mi.id_marca_cet ");
		sbSQL.append(" LEFT JOIN cad_especie ce (NOLOCK) ");
		sbSQL.append("  ON ce.id_especie = mi.id_especie ");
		sbSQL.append(" WHERE i.id_infracao = ? ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id_infracao);

			rs = ps.executeQuery();
			if (rs.next()) {
				lRet = new MovimentoImportacao(
						rs.getInt("id_movimento_arquivo"),
						rs.getDate("data_movimento"),
						rs.getInt("id_movimento"),
						rs.getInt("sequencia"),
						rs.getInt("id_veiculo_local"),
						rs.getString("placa"),
						rs.getInt("pais"),
						rs.getInt("id_marca_cet"),
						rs.getInt("id_especie"),
						rs.getInt("id_enquadramento"),
						rs.getInt("id_local"),
						rs.getString("descricao_local"),
						rs.getInt("cod_pista_prodam"),
						rs.getTimestamp("data_hora"),
						rs.getInt("velocidade_constatada"),
						rs.getInt("velocidade_considerada"),
						rs.getInt("velocidade_regulamentada"),
						rs.getString("pista"),
						rs.getInt("cod_operador"),
						rs.getDate("data_analise"),
						rs.getInt("registro_montante"),
						rs.getInt("consistencia"),
						rs.getInt("id_inconsistencia"),
						rs.getInt("indice_imagem"),
						rs.getBoolean("validacao"),
						rs.getString("marca_cet"),
						rs.getString("especie"));
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

	public Integer getIdMovimentoArquivo() {
		return idMovimentoArquivo;
	}

	public Date getDataMovimento() {
		return dataMovimento;
	}

	public Integer getIdMovimento() {
		return idMovimento;
	}

	public Integer getSequencia() {
		return sequencia;
	}

	public Integer getIdVeiculoLocal() {
		return idVeiculoLocal;
	}

	public String getPlaca() {
		return placa;
	}

	public Integer getPais() {
		return pais;
	}

	public Integer getIdMarcaCet() {
		return idMarcaCet;
	}

	public Integer getIdEspecie() {
		return idEspecie;
	}

	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}

	public Integer getIdLocal() {
		return idLocal;
	}

	public String getDescricaoLocal() {
		return descricaoLocal;
	}

	public Integer getCodPistaProdam() {
		return codPistaProdam;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public Integer getVelocidadeConstatada() {
		return velocidadeConstatada;
	}

	public Integer getVelocidadeConsiderada() {
		return velocidadeConsiderada;
	}

	public Integer getVelocidadeRegulamentada() {
		return velocidadeRegulamentada;
	}

	public String getPista() {
		return pista;
	}

	public Integer getCodOperador() {
		return codOperador;
	}

	public Date getDataAnalise() {
		return dataAnalise;
	}

	public Integer getRegistroMontante() {
		return registroMontante;
	}

	public Integer getConsistencia() {
		return consistencia;
	}

	public Integer getIdInconsistencia() {
		return idInconsistencia;
	}

	public Integer getIndiceImagem() {
		return indiceImagem;
	}

	public Boolean getValidacao() {
		return validacao;
	}

	public String getMarcaCet() {
		return marcaCet;
	}

	public String getEspecie() {
		return especie;
	}

}
