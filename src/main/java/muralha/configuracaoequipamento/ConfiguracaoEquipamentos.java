package muralha.configuracaoequipamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement		(name="ConfiguracaoEquipamentos")
@XmlAccessorType	(XmlAccessType.FIELD)
public class ConfiguracaoEquipamentos 
{
	@XmlTransient
	private static Logger logger = LogManager.getLogger(ConfiguracaoEquipamentos.class);
	
	@XmlElementWrapper	(name = "ConfiguracoesEquipamento")
	@XmlElement 		(name = "ConfiguracaoEquipamento")
	private ConfiguracaoEquipamento configEquip = new ConfiguracaoEquipamento();
	
	public ConfiguracaoEquipamento obterConfiguracoes()
	{
		return configEquip;
	}
	
	public void definirConfiguracoes() 
		throws SQLException, ConexaoException
	{
		consultaStatus();
		consultaComboio();
		consultaBanco();
		consultaClandestino_m();
		consultaClandestino_t();
		//consultarConfigs();	
	}
	
	private void consultaStatus() 
		throws SQLException, ConexaoException
	{
		logger.info("[consultaStatus] Buscando configs...");
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT   *, ");
		sbSQL.append("          (SELECT top(1) erros_permitidos from muralha.config_semelhanca_placa  " );
		sbSQL.append(" 			 WHERE data_exclusao is null order by data_cadastro desc) as qtde_erros_semelhanca, "); 
		sbSQL.append("          ISNULL((SELECT TOP(1) CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_BAIXA'), 3) as qtd_pas_correlacao_baixa, ");
		sbSQL.append("          ISNULL((SELECT TOP(1) CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_MEDIA'), 4) as qtd_pas_correlacao_media, ");
		sbSQL.append("          ISNULL((SELECT TOP(1) CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_ALTA'), 5) as qtd_pas_correlacao_alta ");
		sbSQL.append("FROM muralha.v_config_status_tipo_alerta ");
		
		
		Connection 			conn	= null;
		PreparedStatement 	ps 		= null;
		ResultSet 			rs 		= null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			if(rs.next())
			{
				try
				{
					configEquip.setStatusSequestro(		rs.getInt("sequestro_relampago")	);
					configEquip.setStatusRoubado(		rs.getInt("roubado")				);
					configEquip.setStatusComboio(		rs.getInt("comboio")				);
					configEquip.setStatusClandestino(	rs.getInt("clandestino")			);
					configEquip.setStatusBanco(			rs.getInt("roubo_banco")			);
					configEquip.setStatusFurtado(		rs.getInt("furtado")				);
					configEquip.setStatusClonado(		rs.getInt("clonado")				);
					configEquip.setStatusLicenciamento(	rs.getInt("licenciamento")			);
					configEquip.setStatusMonitorado(	rs.getInt("monitorado")				);
					configEquip.setQtdeErrosSemelhanca( rs.getInt("qtde_erros_semelhanca") 	);
					configEquip.setQtdPasCorrelacaoBaixa(rs.getInt("qtd_pas_correlacao_baixa"));
					configEquip.setQtdPasCorrelacaoMedia(rs.getInt("qtd_pas_correlacao_media"));
					configEquip.setQtdPasCorrelacaoAlta(rs.getInt("qtd_pas_correlacao_alta"));
				}
				catch(Exception e)
				{
					logger.error("[consultaStatus] ERRO: " + e);
				}
			}
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
				if (rs != null)
					rs.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[consultaStatus] Pronto");
	}
	
	private void consultaComboio() 
		throws SQLException, ConexaoException
	{
		logger.info("[consultaComboio] Buscando configs...");
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT * FROM muralha.config_alerta_comboio");
		
		Connection 			conn	= null;
		PreparedStatement 	ps 		= null;
		ResultSet 			rs 		= null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			if(rs.next())
				configEquip.setIntervaloComboio(rs.getString("tempo_entre_passagens_sec"));
			
			logger.debug(configEquip.getIntervaloComboio());
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
				if (rs != null)
					rs.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[consultaComboio] Pronto");
	}
	
	private void consultaBanco()
		throws SQLException, ConexaoException
	{
		logger.info("[consultaBanco] Buscando configs...");
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT * FROM muralha.config_alerta_roubo_ponto_interesse");
		
		Connection 			conn	= null;
		PreparedStatement 	ps 		= null;
		ResultSet 			rs 		= null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			if(rs.next())
				configEquip.setIntervaloBanco(rs.getString("tempo_entre_passagens_sec"));
			
			logger.debug(configEquip.getIntervaloBanco());
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
				if (rs != null)
					rs.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[consultaBanco] Pronto");
	}
	
	private void consultaClandestino_m()
		throws SQLException, ConexaoException
	{
		logger.info("[consultaClandestino_m] Buscando configs...");
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT * FROM muralha.config_alerta_transp_clandestino WHERE id = '77A119DF-5758-4850-8A2B-8F8A5C796DD1'");
		
		Connection 			conn	= null;
		PreparedStatement 	ps 		= null;
		ResultSet 			rs 		= null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			if(rs.next())
			{
				configEquip.setInitManhaClandestino(rs.getString("periodo_inicial"));
				configEquip.setFimManhaClandestino(rs.getString("periodo_final"));
			}
			
			logger.debug(configEquip.getIntervaloBanco());
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
				if (rs != null)
					rs.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[consultaClandestino_m] Pronto");
	}
	
	private void consultaClandestino_t()
		throws SQLException, ConexaoException
	{
		logger.info("[consultaClandestino_t] Buscando configs...");
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT * FROM muralha.config_alerta_transp_clandestino WHERE id = '5FC39CB4-C5A9-4707-AA0A-E43E500E4859'");
		
		Connection 			conn	= null;
		PreparedStatement 	ps 		= null;
		ResultSet 			rs 		= null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			if(rs.next())
			{
				configEquip.setInitTardeClandestino(rs.getString("periodo_inicial"));
				configEquip.setFimTardeClandestino(rs.getString("periodo_final"));
				configEquip.setPassagensClandestino(rs.getInt("qtde_passagens"));
				configEquip.setTipoClandestino(rs.getString("classificacao") != null ? rs.getString("classificacao").trim() : "");
			}
			
			logger.debug(configEquip.getIntervaloBanco());
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
				if (rs != null)
					rs.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[consultaClandestino_t] Pronto");
	}
	
/*
 * =====================================================================================
 * 
 * 							CONFIGURACAO DE COMBOIO
 * 
 * =====================================================================================
 */
	public static void configComboio(int usuario, String tempo, int status) 
		throws SQLException, ConexaoException
	{
		logger.info("[configComboio] Inserindo dados...");
		
		StringBuilder sbSQL_config = new StringBuilder();
		StringBuilder sbSQL_ativo = new StringBuilder();
		
		sbSQL_config.append("UPDATE muralha.config_alerta_comboio"					);
		sbSQL_config.append("	SET tempo_entre_passagens_sec="	+ tempo 	+ ","	);
		sbSQL_config.append("		id_usuario_alteracao="		+ usuario	+ ","	);
		sbSQL_config.append("		data_alteracao=GETDATE()"						);
		
		sbSQL_ativo.append("UPDATE muralha.tipo_alerta_ocorrencia"	);
		sbSQL_ativo.append("	SET tarefa_ativa = " + status		);
		sbSQL_ativo.append("	WHERE tipo = 'Comboio de Veículos'"	);
		
		
		logger.debug("[configComboio] Query: " + sbSQL_config.toString());
		logger.debug("[configComboio] Query: " + sbSQL_ativo.toString());
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			logger.info("[configComboio] Executando query...");
			
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL_config.toString());
			ps.execute();
			ps.close();
			
			ps = conn.prepareStatement(sbSQL_ativo.toString());
			ps.execute();
			ps.close();
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("Requisicao concluida");
	}

/*
 * =====================================================================================
 * 
 * 							CONFIGURACAO DE SEQUESTRO
 * 
 * =====================================================================================
 */
	public static void configSequestro(int status)
		throws SQLException, ConexaoException
	{
		logger.info("[configSequestro] Inserindo dados...");
		
		StringBuilder sbSQL_ativo = new StringBuilder();
		
		sbSQL_ativo.append("UPDATE muralha.tipo_alerta_ocorrencia"						);
		sbSQL_ativo.append("	SET tarefa_ativa = " + status							);
		sbSQL_ativo.append("	WHERE tipo = 'Veículo Suspeito de Sequestro Relâmpago'"	);
		
		
		logger.debug("[configSequestro] Query: " + sbSQL_ativo.toString());
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			logger.info("[configSequestro] Executando query...");
			
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL_ativo.toString());
			ps.execute();
			ps.close();
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[configSequestro] Requisicao concluida");
	}
	
/*
 * =====================================================================================
 * 
 * 							CONFIGURACAO DE ROUBO
 * 
 * =====================================================================================
 */
	public static void configRoubo(int status)
		throws SQLException, ConexaoException
	{
		logger.info("[configRoubo] Inserindo dados...");
		
		StringBuilder sbSQL_ativo = new StringBuilder();
		
		sbSQL_ativo.append("UPDATE muralha.tipo_alerta_ocorrencia"	);
		sbSQL_ativo.append("	SET tarefa_ativa = " + status		);
		sbSQL_ativo.append("	WHERE tipo = 'Veículo Roubado'"		);
		
		
		logger.debug("[configRoubo] Query: " + sbSQL_ativo.toString());
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			logger.info("[configRoubo] Executando query...");
			
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL_ativo.toString());
			ps.execute();
			ps.close();
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[configClonado] Requisicao concluida");
	}
	
/*
 * =====================================================================================
 * 
 * 							CONFIGURACAO DE FURTO
 * 
 * =====================================================================================
 */
	public static void configFurto(int status)
		throws SQLException, ConexaoException
	{
		logger.info("[configFurto] Inserindo dados...");
		
		StringBuilder sbSQL_ativo = new StringBuilder();
		
		sbSQL_ativo.append("UPDATE muralha.tipo_alerta_ocorrencia"	);
		sbSQL_ativo.append("	SET tarefa_ativa = " + status		);
		sbSQL_ativo.append("	WHERE tipo = 'Veículo Furtado'"		);
		
		
		logger.debug("[configFurto] Query: " + sbSQL_ativo.toString());
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			logger.info("[configFurto] Executando query...");
			
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL_ativo.toString());
			ps.execute();
			ps.close();
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[configFurto] Requisicao concluida");
	}
	
	/*
	 * =====================================================================================
	 * 
	 * 							CONFIGURACAO DE ATRASO DE LICENCIAMENTO
	 * 
	 * =====================================================================================
	 */
		public static void configLicenciamento(int status)
			throws SQLException, ConexaoException
		{
			logger.info("[configLicenciamento] Inserindo dados...");
			
			StringBuilder sbSQL_ativo = new StringBuilder();
			
			sbSQL_ativo.append("UPDATE muralha.tipo_alerta_ocorrencia"	);
			sbSQL_ativo.append("	SET tarefa_ativa = " + status		);
			sbSQL_ativo.append("	WHERE tipo = 'Veículo com Atraso de Licenciamento'"		);
			
			
			logger.debug("[configLicenciamento] Query: " + sbSQL_ativo.toString());
			
			Connection conn = null;
			PreparedStatement ps = null;
			
			try
			{
				logger.info("[configLicenciamento] Executando query...");
				
				conn = Conexao.getConexao();
				
				ps = conn.prepareStatement(sbSQL_ativo.toString());
				ps.execute();
				ps.close();
			}
			catch(Exception e)
			{
				throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
			}
			finally 
			{
				try 
				{
					if (ps != null)
						ps.close();
					if (conn != null)
						conn.close();
				} 
				catch (SQLException e) 
				{
					throw new ConexaoException("ERRO de SQL", e);
				}			
			}
			logger.info("[configLicenciamento] Requisicao concluida");
		}
		
		/*
		 * =====================================================================================
		 * 
		 * 							CONFIGURACAO DE MONITORADO
		 * 
		 * =====================================================================================
		 */
			public static void configMonitorado(int status)
				throws SQLException, ConexaoException
			{
				logger.info("[configMonitorado] Inserindo dados...");
				
				StringBuilder sbSQL_ativo = new StringBuilder();
				
				sbSQL_ativo.append("UPDATE muralha.tipo_alerta_ocorrencia"	);
				sbSQL_ativo.append("	SET tarefa_ativa = " + status		);
				sbSQL_ativo.append("	WHERE tipo = 'Veiculo Monitorado'"		);
				
				
				logger.debug("[configMonitorado] Query: " + sbSQL_ativo.toString());
				
				Connection conn = null;
				PreparedStatement ps = null;
				
				try
				{
					logger.info("[configMonitorado] Executando query...");
					
					conn = Conexao.getConexao();
					
					ps = conn.prepareStatement(sbSQL_ativo.toString());
					ps.execute();
					ps.close();
				}
				catch(Exception e)
				{
					throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
				}
				finally 
				{
					try 
					{
						if (ps != null)
							ps.close();
						if (conn != null)
							conn.close();
					} 
					catch (SQLException e) 
					{
						throw new ConexaoException("ERRO de SQL", e);
					}			
				}
				logger.info("[configMonitorado] Requisicao concluida");
			}

/*
 * =====================================================================================
 * 
 * 							CONFIGURACAO DE CLANDESTINO
 * 
 * =====================================================================================
 */
	public static void configClandestino(int idUsuario, int status, 
			String init_manha, String fim_manha, String init_tarde, String fim_tarde, String passagens, String tipo)
		throws SQLException, ConexaoException
	{
		logger.info("[configClandestino] Inserindo dados...");
		
		StringBuilder sbSQL_ativo = new StringBuilder();
		StringBuilder sbSQL_config_m = new StringBuilder();
		StringBuilder sbSQL_config_t = new StringBuilder();
		
		sbSQL_ativo.append("UPDATE muralha.tipo_alerta_ocorrencia"		);
		sbSQL_ativo.append("	SET tarefa_ativa = " + status			);
		sbSQL_ativo.append("	WHERE tipo = 'Transporte Clandestino'"	);
		
		sbSQL_config_m.append("DECLARE @ini_manha time(2) = '" + init_manha + "' "	);
		sbSQL_config_m.append("DECLARE @fim_manha time(2) = '" + fim_manha + "' "	);
		sbSQL_config_m.append("UPDATE muralha.config_alerta_transp_clandestino "	);
		sbSQL_config_m.append("	SET qtde_passagens=" + passagens + ","				);
		sbSQL_config_m.append("		periodo_inicial=@ini_manha, "					);
		sbSQL_config_m.append("		periodo_final=@fim_manha, "						);
		sbSQL_config_m.append("		classificacao= " + (tipo == null ? "NULL" : "'"+ tipo +"'") + ","					);
		sbSQL_config_m.append("		id_usuario_alteracao= " + idUsuario + ","		);
		sbSQL_config_m.append("		data_alteracao=GETDATE() "						);
		sbSQL_config_m.append("	WHERE id = '77A119DF-5758-4850-8A2B-8F8A5C796DD1' "	);
		
		sbSQL_config_t.append("DECLARE @ini_tarde time(2) = '" + init_tarde + "' "	);
		sbSQL_config_t.append("DECLARE @fim_tarde time(2) = '" + fim_tarde + "' "	);
		sbSQL_config_t.append("UPDATE muralha.config_alerta_transp_clandestino "	);
		sbSQL_config_t.append("	SET qtde_passagens=" + passagens + ","				);
		sbSQL_config_t.append("		periodo_inicial=@ini_tarde, "					);
		sbSQL_config_t.append("		periodo_final=@fim_tarde, "						);
		sbSQL_config_t.append("		classificacao= " + (tipo == null ? "NULL" : "'"+ tipo +"'") + ","					);
		sbSQL_config_t.append("		id_usuario_alteracao= " + idUsuario + ","		);
		sbSQL_config_t.append("		data_alteracao=GETDATE() "						);
		sbSQL_config_t.append("	WHERE id = '5FC39CB4-C5A9-4707-AA0A-E43E500E4859' "	);
		
		logger.debug("[configClandestino] Query: " + sbSQL_ativo.toString());
		logger.debug("[configClandestino] Query: " + sbSQL_config_m.toString());
		logger.debug("[configClandestino] Query: " + sbSQL_config_t.toString());
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			logger.info("[configClandestino] Executando query...");
			
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL_ativo.toString());
			ps.execute();
			ps.close();
			
			ps = conn.prepareStatement(sbSQL_config_m.toString());
			ps.execute();
			ps.close();
			
			ps = conn.prepareStatement(sbSQL_config_t.toString());
			ps.execute();
			ps.close();
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[configClandestino] Requisicao concluida");
	}
	
/*
 * =====================================================================================
 * 
 * 							CONFIGURACAO DE PONTO
 * 
 * =====================================================================================
 */
	public static void configPonto(int usuario, int status, String tempo)
		throws SQLException, ConexaoException
	{
		logger.info("[configPonto] Inserindo dados...");
		
		StringBuilder sbSQL_ativo = new StringBuilder();
		StringBuilder sbSQL_config = new StringBuilder();
		
		sbSQL_config.append("UPDATE muralha.config_alerta_roubo_ponto_interesse"	);
		sbSQL_config.append("	SET tempo_entre_passagens_sec="	+ tempo 	+ ","	);
		sbSQL_config.append("		id_usuario_alteracao="		+ usuario 	+ ","	);
		sbSQL_config.append("		data_alteracao=GETDATE()"						);
		
		sbSQL_ativo.append("UPDATE muralha.tipo_alerta_ocorrencia"					);
		sbSQL_ativo.append("	SET tarefa_ativa = " + status						);
		sbSQL_ativo.append("	WHERE tipo = 'Veículo Suspeito de Roubo à Banco'"	);
		
		
		logger.debug("[configPonto] Query: " + sbSQL_ativo.toString());
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			logger.info("[configPonto] Executando query...");
			
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL_config.toString());
			ps.execute();
			ps.close();
			
			ps = conn.prepareStatement(sbSQL_ativo.toString());
			ps.execute();
			ps.close();
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[configPonto] Requisicao concluida");
	}
	
/*
 * =====================================================================================
 * 
 * 							CONFIGURACAO DE CLONADO
 * 
 * =====================================================================================
 */
	public static void configClonado(int status)
		throws SQLException, ConexaoException
	{
		logger.info("[configClonado] Inserindo dados...");
		
		StringBuilder sbSQL_ativo = new StringBuilder();
		
		sbSQL_ativo.append("UPDATE muralha.tipo_alerta_ocorrencia");
		sbSQL_ativo.append("	SET tarefa_ativa = " + status);
		sbSQL_ativo.append("	WHERE tipo = 'Veículo Clonado'");
		
		
		logger.debug("[configClonado] Query: " + sbSQL_ativo.toString());
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			logger.info("[configClonado] Executando query...");
			
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL_ativo.toString());
			ps.execute();
			ps.close();
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) 
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		logger.info("[configClonado] Requisicao concluida");
	}
	
	public static void configSemelhanca(int usuario, int caracteres)
		throws SQLException, ConexaoException
	{

		logger.info("[configSemelhanca] Inserindo dados...");
		
		if (caracteres > 6 ) 
		{
			logger.error("Numero de Caracteres de Semelhanca incorreto. " + caracteres);
			return;
		}
		
		StringBuilder sbSQL_ativo = new StringBuilder();
		
		sbSQL_ativo.append(" UPDATE muralha.config_semelhanca_placa set id_usuario_exclusao = " + Integer.toString(usuario) + " , data_exclusao = GETDATE() ");
		sbSQL_ativo.append(" WHERE data_exclusao is null;");

		sbSQL_ativo.append(" INSERT INTO muralha.config_semelhanca_placa (id, erros_permitidos, id_usuario, data_cadastro, id_usuario_exclusao, data_exclusao) ");
		sbSQL_ativo.append(" VALUES ( NEWID(), "   + Integer.toString(caracteres) +   ", "    + Integer.toString(usuario) +    ", GETDATE(), NULL, NULL ) ;" );		
		
		
		logger.debug("[configSemelhanca] Query: " + sbSQL_ativo.toString());
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			logger.info("[configSemelhanca] Executando query...");
			
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL_ativo.toString());
			ps.execute();
			ps.close();
		}
		catch(Exception e)
		{
			logger.error("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally 
		{
			try 
			{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) 
			{
				logger.error("ERRO de SQL", e);
			}			
		}
		logger.info("[configSemelhanca] Requisicao concluida");
	
	
	}

	public static void configVeiculosCorrelacionados(int usuario, int qtdPasCorrelacaoBaixa, int qtdPasCorrelacaoMedia, int qtdPasCorrelacaoAlta)
		throws SQLException, ConexaoException
	{
		logger.info("[configVeiculosCorrelacionados] Inserindo dados...");

		if (qtdPasCorrelacaoBaixa < 1 || qtdPasCorrelacaoMedia <= qtdPasCorrelacaoBaixa || qtdPasCorrelacaoAlta <= qtdPasCorrelacaoMedia)
		{
			logger.error("Faixas de passagens para correlacao invalidas. Usuario=" + usuario + ", Baixa=" + qtdPasCorrelacaoBaixa + ", Media=" + qtdPasCorrelacaoMedia + ", Alta=" + qtdPasCorrelacaoAlta);
			return;
		}

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
		String valorAtualBaixa = null;
		String valorAtualMedia = null;
		String valorAtualAlta = null;

		try
		{
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);

			sbSQL.append(" SELECT TOP(1) valor FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_BAIXA' ");
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			if (rs.next())
			{
				valorAtualBaixa = rs.getString("valor");
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;

			sbSQL.setLength(0);
			sbSQL.append(" SELECT TOP(1) valor FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_MEDIA' ");
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			if (rs.next())
			{
				valorAtualMedia = rs.getString("valor");
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;

			sbSQL.setLength(0);
			sbSQL.append(" SELECT TOP(1) valor FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_ALTA' ");
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			if (rs.next())
			{
				valorAtualAlta = rs.getString("valor");
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;

			if (!Integer.toString(qtdPasCorrelacaoBaixa).equals(valorAtualBaixa))
			{
				Historico(conn, usuario, "VEICULO_CORRELACIONADO_QTD_PASSAGENS_BAIXA = " + (valorAtualBaixa == null ? "NULL" : valorAtualBaixa) + " -> " + qtdPasCorrelacaoBaixa);
			}

			if (!Integer.toString(qtdPasCorrelacaoMedia).equals(valorAtualMedia))
			{
				Historico(conn, usuario, "VEICULO_CORRELACIONADO_QTD_PASSAGENS_MEDIA = " + (valorAtualMedia == null ? "NULL" : valorAtualMedia) + " -> " + qtdPasCorrelacaoMedia);
			}

			if (!Integer.toString(qtdPasCorrelacaoAlta).equals(valorAtualAlta))
			{
				Historico(conn, usuario, "VEICULO_CORRELACIONADO_QTD_PASSAGENS_ALTA = " + (valorAtualAlta == null ? "NULL" : valorAtualAlta) + " -> " + qtdPasCorrelacaoAlta);
			}

			sbSQL.setLength(0);
			sbSQL.append(" UPDATE muralha.config_chave_valor ");
			sbSQL.append(" SET valor = ? ");
			sbSQL.append(" WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_BAIXA' ");
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, Integer.toString(qtdPasCorrelacaoBaixa));
			int rowsAffected = ps.executeUpdate();
			ps.close();
			ps = null;

			if (rowsAffected == 0)
			{
				sbSQL.setLength(0);
				sbSQL.append(" INSERT INTO muralha.config_chave_valor (chave, valor) ");
				sbSQL.append(" VALUES ('VEICULO_CORRELACIONADO_QTD_PASSAGENS_BAIXA', ?) ");
				ps = conn.prepareStatement(sbSQL.toString());
				ps.setString(1, Integer.toString(qtdPasCorrelacaoBaixa));
				ps.executeUpdate();
				ps.close();
				ps = null;
			}

			sbSQL.setLength(0);
			sbSQL.append(" UPDATE muralha.config_chave_valor ");
			sbSQL.append(" SET valor = ? ");
			sbSQL.append(" WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_MEDIA' ");
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, Integer.toString(qtdPasCorrelacaoMedia));
			rowsAffected = ps.executeUpdate();
			ps.close();
			ps = null;

			if (rowsAffected == 0)
			{
				sbSQL.setLength(0);
				sbSQL.append(" INSERT INTO muralha.config_chave_valor (chave, valor) ");
				sbSQL.append(" VALUES ('VEICULO_CORRELACIONADO_QTD_PASSAGENS_MEDIA', ?) ");
				ps = conn.prepareStatement(sbSQL.toString());
				ps.setString(1, Integer.toString(qtdPasCorrelacaoMedia));
				ps.executeUpdate();
				ps.close();
				ps = null;
			}

			sbSQL.setLength(0);
			sbSQL.append(" UPDATE muralha.config_chave_valor ");
			sbSQL.append(" SET valor = ? ");
			sbSQL.append(" WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_ALTA' ");
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, Integer.toString(qtdPasCorrelacaoAlta));
			rowsAffected = ps.executeUpdate();
			ps.close();
			ps = null;

			if (rowsAffected == 0)
			{
				sbSQL.setLength(0);
				sbSQL.append(" INSERT INTO muralha.config_chave_valor (chave, valor) ");
				sbSQL.append(" VALUES ('VEICULO_CORRELACIONADO_QTD_PASSAGENS_ALTA', ?) ");
				ps = conn.prepareStatement(sbSQL.toString());
				ps.setString(1, Integer.toString(qtdPasCorrelacaoAlta));
				ps.executeUpdate();
				ps.close();
				ps = null;
			}

			conn.commit();
		}
		catch(Exception e)
		{
			if (conn != null)
			{
				try
				{
					conn.rollback();
				}
				catch (SQLException ex)
				{
					logger.error("ERRO de SQL", ex);
				}
			}
			logger.error("Erro ao montar SQL (ConfigurarEquipamentos):: ", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			}
			catch (SQLException e)
			{
				logger.error("ERRO de SQL", e);
			}
		}

		logger.info("[configVeiculosCorrelacionados] Requisicao concluida");
	}

	private static void Historico(Connection conn, int idUsuario, String evento)
		throws SQLException
	{
		PreparedStatement ps = null;
		StringBuilder sbSQL = new StringBuilder();

		try
		{
			sbSQL.append(" insert into muralha.config_chave_valor_hist(id_usuario, data_atualizacao, evento) ");
			sbSQL.append(" values (?, GETDATE(), ?) ");
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);
			ps.setString(2, evento);
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				ps.close();
			}
		}
	}
	
	
}