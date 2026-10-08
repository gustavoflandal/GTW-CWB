package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class MovimentoTarja {

		private Integer idMovimentoArquivo;
		private Date dataInfracao;
		private String classif;
		private String localSentido;
		private Integer codigoEquipamento;
		private Integer tempoDecorVerm;
		private Integer tempoPerm;
		private Integer tempoRetar;
		private Date dataAfericao;
		private Date horaInicio;
		private Date horaFim;
		private Integer idPista;
		private Integer velocidadeRegul;
		private Integer velocidadeMedida;
		private Integer velocidadeConsiderada;
		private Boolean cadastroSP;
		private Integer diaSem;
		private Integer idVeiculoLocal;
		private Integer idEnquadramento;
		private String descricao;
	
		public MovimentoTarja(Integer id_movimento_arquivo, Date data_infracao, String classif, 
				String local_sentido, Integer codigo_equipamento, Integer tempo_decor_verm, 
				Integer tempo_perm, Integer tempo_retar, Date data_afericao, Date hora_inicio, 
				Date hora_fim, Integer id_pista, Integer velocidade_regul, Integer velocidade_medida, 
				Integer velocidade_considerada, Boolean cadastro_sp, Integer dia_sem, Integer id_veiculo_local, 
				Integer id_enquadramento, String descricao) {
			this.idMovimentoArquivo = id_movimento_arquivo;
			this.dataInfracao = data_infracao;
			this.classif = classif;
			this.localSentido = local_sentido;
			this.codigoEquipamento = codigo_equipamento;
			this.tempoDecorVerm = tempo_decor_verm;
			this.tempoPerm = tempo_perm;
			this.tempoRetar = tempo_retar;
			this.dataAfericao = data_afericao;
			this.horaInicio = hora_inicio;
			this.horaFim = hora_fim;
			this.idPista = id_pista;
			this.velocidadeRegul = velocidade_regul;
			this.velocidadeMedida = velocidade_medida;
			this.velocidadeConsiderada = velocidade_considerada;
			this.cadastroSP = cadastro_sp;
			this.diaSem = dia_sem;
			this.idVeiculoLocal = id_veiculo_local;
			this.idEnquadramento = id_enquadramento;
			this.descricao = descricao;
		}
		
		public static MovimentoTarja ObterMovimentoTarjaPorInfracao(Integer id_infracao) throws ConexaoException {
			MovimentoTarja lRet = null;
			StringBuilder sbSQL = new StringBuilder();

			sbSQL.append("SELECT mt.* FROM infracao i (NOLOCK) "); 
			sbSQL.append("JOIN movimento_tarja mt (NOLOCK)  ");
			sbSQL.append("ON i.id_imagem_local = mt.id_veiculo_local "); 
			sbSQL.append("AND i.data = mt.data_infracao ");
			sbSQL.append("AND i.id_enquadramento = mt.id_enquadramento ");
			sbSQL.append("JOIN movimento_arquivo mat (NOLOCK) ");
			sbSQL.append("ON mt.id_movimento_arquivo = mat.id_movimento_arquivo ");
			sbSQL.append("AND mat.indice_imagem = 0 ");
			sbSQL.append("WHERE i.id_infracao = ? ");
			
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sbSQL.toString());
				ps.setInt(1, id_infracao);

				rs = ps.executeQuery();
				if (rs.next()) {
					lRet = new MovimentoTarja(
							rs.getInt("id_movimento_arquivo"),
							rs.getDate("data_infracao"),
							rs.getString("classif"),
							rs.getString("local_sentido"),
							rs.getInt("codigo_equipamento"),
							rs.getInt("tempo_decor_verm"),
							rs.getInt("tempo_perm"),
							rs.getInt("tempo_retar"),
							rs.getDate("data_afericao"),
							rs.getTime("hora_inicio"),
							rs.getTime("hora_fim"),
							rs.getInt("id_pista"),
							rs.getInt("velocidade_regul"),
							rs.getInt("velocidade_medida"),
							rs.getInt("velocidade_considerada"),
							rs.getBoolean("cadastro_sp"),
							rs.getInt("dia_sem"),
							rs.getInt("id_veiculo_local"),
							rs.getInt("id_enquadramento"),
							rs.getString("descricao"));
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

		public Date getDataInfracao() {
			return dataInfracao;
		}

		public String getClassif() {
			return classif;
		}

		public String getLocalSentido() {
			return localSentido;
		}

		public Integer getCodigoEquipamento() {
			return codigoEquipamento;
		}

		public Integer getTempoDecorVerm() {
			return tempoDecorVerm;
		}

		public Integer getTempoPerm() {
			return tempoPerm;
		}

		public Integer getTempoRetar() {
			return tempoRetar;
		}

		public Date getDataAfericao() {
			return dataAfericao;
		}

		public Date getHoraInicio() {
			return horaInicio;
		}

		public Date getHoraFim() {
			return horaFim;
		}

		public Integer getIdPista() {
			return idPista;
		}

		public Integer getVelocidadeRegul() {
			return velocidadeRegul;
		}

		public Integer getVelocidadeMedida() {
			return velocidadeMedida;
		}

		public Integer getVelocidadeConsiderada() {
			return velocidadeConsiderada;
		}

		public Boolean getCadastroSP() {
			return cadastroSP;
		}

		public Integer getDiaSem() {
			return diaSem;
		}

		public Integer getIdVeiculoLocal() {
			return idVeiculoLocal;
		}

		public Integer getIdEnquadramento() {
			return idEnquadramento;
		}

		public String getDescricao() {
			return descricao;
		}
}
