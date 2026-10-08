package muralha.digital.notificacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class ConfiguracaoSons {
	private static final long serialVersionUID = 1L;

	@XmlTransient
	private static final Logger logger = Logger.getLogger(ConfiguracaoSons.class);

	/*
		Aqui abaixo é criado e populado o objeto que irá receber as configurações, sendo eles:
		getConfiguracaoTempos -> Tempo de execução do alerta sonoro;
		getSonsAtivo -> Se o alerta sonoro está ativo ou não;
		getAlertasContinuo -> Se o alerta sonoro é contínuo ou não;
		Também possui o "set" de cada um, porém, como o nome diz, é para atualizar os valores.
	*/
	@XmlElement(name = "configuracao")
	private ConfiguracaoSom configuracao;

   public ConfiguracaoSom Historico;
	
	public ConfiguracaoSom getConfiguracaoTempos() {
		return configuracao;
	}
	public void setConfiguracaoTempos(ConfiguracaoSom sons) {
		this.configuracao = sons;
	}

	public ConfiguracaoSom getSonsAtivo() {
		return configuracao;
	}
	public void setSonsAtivo(ConfiguracaoSom sons) {
		this.configuracao = sons;
	}

	public ConfiguracaoSom getAlertasContinuo() {
		return configuracao;
	}
	public void setAlertasContinuo(ConfiguracaoSom sons) {
		this.configuracao = sons;
	}

	public ConfiguracaoSom getTempoMaximoEmissao() {
		return configuracao;
	}
	public void setTempoMaximoEmissao(ConfiguracaoSom tempoMaximo) {
		this.configuracao = tempoMaximo;
	}
	
	@XmlElementWrapper(name = "tiposAlertas")
	@XmlElement(name = "alerta")
	private List<ConfiguracaoSom> tiposAlertas;

	public List<ConfiguracaoSom> getTiposAlertas() {
	    return tiposAlertas;
	}

	public void setTiposAlertas(List<ConfiguracaoSom> tiposAlertas) {
	    this.tiposAlertas = tiposAlertas;
	}
	
	/*
		Aqui abaixo é criado a função para chamada do Tempo de Execução do Alerta Sonoro,
		pegando o valor no banco da chave (tempo_exec) e o valor (5, 10, 15 segundos, etc).
	*/
	public static ConfiguracaoSom ObterConfigTempos() {
		ConfiguracaoSom alarme_tempo_execucao = new ConfiguracaoSom();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		try {
         sbSQL.append(" SELECT * FROM muralha.config_chave_valor WHERE chave = 'alarme_tempo_execucao' ");

         conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				alarme_tempo_execucao.setValor(rs.getString("valor"));
				alarme_tempo_execucao.setChave(rs.getString("chave"));
			}
		} catch (Exception e) {
			logger.error("Erro ao obter Tempo de Execução dos Alertas: " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return alarme_tempo_execucao;
	}
	
	/*
		Aqui abaixo é criado a função para chamada do Alerta Som Ativo,
		pegando o valor no banco da chave (alarme_som_ativo) e o valor (1 para ativo ou 0 para inativo).
	*/
	public static ConfiguracaoSom ObterSonsAtivo() {
		ConfiguracaoSom alarme_som_ativo = new ConfiguracaoSom();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		try {
			sbSQL.append(" SELECT * FROM muralha.config_chave_valor WHERE chave = 'alarme_som_ativo' ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			while(rs.next()) {
				alarme_som_ativo.setValor(rs.getString("valor"));
				alarme_som_ativo.setChave(rs.getString("chave"));
			}
		} catch(Exception e) {
			logger.error("Erro ao obter Som Ativo dos Alertas: " + e.getMessage(), e);
		} finally {
			try {				
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return alarme_som_ativo;
	}

	/*
		Aqui abaixo é criado a função para chamada do Alerta Sonoro Contínuo,
		pegando o valor no banco da chave (alarme_continuo) e o valor (1 para ativo ou 0 para inativo).
	*/
	public static ConfiguracaoSom ObterAlertasContinuo() {
		ConfiguracaoSom alarme_continuo = new ConfiguracaoSom();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		try {
			sbSQL.append(" SELECT * FROM muralha.config_chave_valor WHERE chave = 'alarme_continuo' ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while(rs.next()) {
				alarme_continuo.setValor(rs.getString("valor"));
				alarme_continuo.setChave(rs.getString("chave"));
			}
		} catch(Exception e) {
			logger.error("Erro ao obter Alarme Sonoro Contínuo: " + e.getMessage(), e);
		} finally {
			try {				
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return alarme_continuo;
	}

	/*
		Aqui abaixo é criado a função para atualizar as configurações dos Alertas Sonoros,
		pegando os valores aplicados pelo usuário para atualizar no banco as chaves.
	*/
	public static ConfiguracaoSom AtualizarConfigAlarmeSonoro(String tempo, String ativo, String alarme_continuo, int idUsuario) {
		if("true".equals(ativo)) {
			ativo = "1";
		}
		else if("false".equals(ativo)) {
			ativo = "0";
		}

		if("true".equals(alarme_continuo)) {
			alarme_continuo = "1";
		} 
		else if("false".equals(alarme_continuo)) {
			alarme_continuo = "0";
		}

		ConfiguracaoSom config = new ConfiguracaoSom();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL1 = new StringBuilder();
		StringBuilder sbSQL2 = new StringBuilder();
		StringBuilder sbSQL3 = new StringBuilder();
		
		try {
			conn = Conexao.getConexao();
			
			if(ativo != null && !ativo.trim().isEmpty()) {
				sbSQL1.append(" UPDATE muralha.config_chave_valor ");
				sbSQL1.append(" SET valor = ? ");
				sbSQL1.append(" WHERE chave = 'alarme_som_ativo' ");

				ps = conn.prepareStatement(sbSQL1.toString());
				ps.setString(1, ativo);
				ps.executeUpdate();
				ps.close();
			}

			if(tempo != null && !tempo.trim().isEmpty()) {
				sbSQL2.append(" UPDATE muralha.config_chave_valor ");
				sbSQL2.append(" SET valor = ? ");
				sbSQL2.append(" WHERE chave = 'alarme_tempo_execucao' ");

				ps = conn.prepareStatement(sbSQL2.toString());
				ps.setString(1, tempo);
				ps.executeUpdate();
				ps.close();
			}

			if(alarme_continuo != null && !alarme_continuo.trim().isEmpty()) {
				sbSQL3.append(" UPDATE muralha.config_chave_valor ");
				sbSQL3.append(" SET valor = ? ");
				sbSQL3.append(" WHERE chave = 'alarme_continuo' ");

				ps = conn.prepareStatement(sbSQL3.toString());
				ps.setString(1, alarme_continuo);
				ps.executeUpdate();
				ps.close();
			}
		} catch(Exception e) {
			logger.error("Erro ao obter Atualização das Configurações dos Alarmes: " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return config;
	}

	/*
		Nessa função abaixo, primeiramente vai iniciar o retorno como falso, depois fará a consulta no banco
		para pegar o valor da chave 'login_redireciona_tela_alerta', caso o valor seja 1, retornará true e
		a tela inicial será a de alertas, caso seja 0, retornará false a tela inicial será a padrão.
	*/
	public static Boolean buscarLoginRedirecionaTelaAlerta() throws Exception {
		ConfiguracaoSom redirecionamento = new ConfiguracaoSom();
		boolean retorno = false;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();

		try {
			sbSQL.append(" SELECT * FROM muralha.config_chave_valor WHERE chave = 'login_redireciona_tela_alerta' ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			if(rs.next()) {
				
				redirecionamento.setValor(rs.getString("valor"));
				if ("1".equals(redirecionamento.getValor())) {
					retorno = true;
				}
			}
		} catch (Exception e) {
			String msgErro = "Erro ao Redirecionar o Login para a Tela de Alerta!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return retorno;
	}

	/*
		Nessa função abaixo, está inserindo no banco os valores da tela,
		pegando o que o usuário digitar e salvando tudo numa linha só.
	*/
	public static ConfiguracaoSom Historico(int idUsuario, String evento) {
		ConfiguracaoSom config = new ConfiguracaoSom();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
	
		try {
			conn = Conexao.getConexao();
			
			sbSQL.append(" insert into muralha.config_chave_valor_hist(id_usuario, data_atualizacao, evento) 	");
			sbSQL.append(" values (?, GETDATE(), ?)																				");
			
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);
			ps.setString(2, evento);
			ps.executeUpdate();
			ps.close();
		} catch(Exception e) {
			logger.error("Erro ao obter o Histórico dos Alertas: " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return config;
	}

	public static ConfiguracaoSom ObterTempoMaximoEmissao() {
		ConfiguracaoSom tempo_maximo_emissao = new ConfiguracaoSom();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		try {
			sbSQL.append(" SELECT * FROM muralha.config_chave_valor WHERE chave = 'tempo_maximo_para_emissao_do_alerta' ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				tempo_maximo_emissao.setValor(rs.getString("valor"));
				tempo_maximo_emissao.setChave(rs.getString("chave"));
			}
		} catch (Exception e) {
			logger.error("Erro ao obter Tempo Máximo para Emissão do Alerta: " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return tempo_maximo_emissao;
	}

	public static ConfiguracaoSom AtualizarTempoMaximoEmissao(String tempo, int idUsuario) {
		ConfiguracaoSom config = new ConfiguracaoSom();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		try {
			conn = Conexao.getConexao();
			
			sbSQL.append(" UPDATE muralha.config_chave_valor ");
			sbSQL.append(" SET valor = ? ");
			sbSQL.append(" WHERE chave = 'tempo_maximo_para_emissao_do_alerta' ");
			
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, tempo);
			int rowsAffected = ps.executeUpdate();
			
			// Se não existir a chave, inserir
			if (rowsAffected == 0) {
				sbSQL.setLength(0);
				sbSQL.append(" INSERT INTO muralha.config_chave_valor (chave, valor) ");
				sbSQL.append(" VALUES ('tempo_maximo_para_emissao_do_alerta', ?) ");
				
				ps = conn.prepareStatement(sbSQL.toString());
				ps.setString(1, tempo);
				ps.executeUpdate();
			}
			
			ps.close();
		} catch(Exception e) {
			logger.error("Erro ao atualizar Tempo Máximo para Emissão do Alerta: " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return config;
	}




	public static List<ConfiguracaoSom> obterTiposAlertas() {
	    List<ConfiguracaoSom> lista = new ArrayList<ConfiguracaoSom>();
	
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	
	    StringBuilder sbSQL = new StringBuilder();
	
	    try {
	        conn = Conexao.getConexao();
	
	        sbSQL.append("SELECT id, tipo ");
	        sbSQL.append("FROM muralha.tipo_alerta_ocorrencia");
	
	        ps = conn.prepareStatement(sbSQL.toString());
	        rs = ps.executeQuery();
	
	        while (rs.next()) {
	        	ConfiguracaoSom tipo = new ConfiguracaoSom();
	
	        	tipo.setId(UUID.fromString(rs.getString("id")));
	            tipo.setTipo(rs.getString("tipo"));
	
	            lista.add(tipo);
	        }
	
	    } catch (Exception e) {
	        logger.error("Erro ao obter tipos de alerta: " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null)
	                rs.close();
	
	            if (ps != null)
	                ps.close();
	
	            if (conn != null)
	                conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao destruir conexão com banco de dados! " + e.getMessage(), e);
	        }
	    }
	
	    return lista;
	}
	
	public static ConfiguracaoSom SalvarPrioridade(String idTipo, int prioridade) {
		ConfiguracaoSom result = new ConfiguracaoSom();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		try {
			conn = Conexao.getConexao();
			
			sbSQL.append(" UPDATE muralha.tipo_alerta_ocorrencia ");
			sbSQL.append(" SET prioridade = ? ");
			sbSQL.append(" WHERE id = ? ");
			
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, prioridade);
			ps.setString(2, idTipo);			
		
			ps.executeUpdate();
		
			
			ps.close();
		} catch(Exception e) {
			logger.error("Erro ao gravar a prioridade do alerta " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return result;
	}
	
	public static ConfiguracaoSom ObterPrioridade(String idTipo) {
		ConfiguracaoSom result = new ConfiguracaoSom();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		try {
			conn = Conexao.getConexao();
			
			sbSQL.append(" SELECT  prioridade  FROM muralha.tipo_alerta_ocorrencia WHERE prioridade = ? ");			
			
			ps = conn.prepareStatement(sbSQL.toString());			
			ps.setString(1, idTipo);			
		
			rs = ps.executeQuery();

	        if (rs.next()) {

	            result = new ConfiguracaoSom();
	            result.setPrioridade(rs.getInt("prioridade"));
	        }
		
			
			ps.close();
		} catch(Exception e) {
			logger.error("Erro ao obter prioridade " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return result;
	}
}