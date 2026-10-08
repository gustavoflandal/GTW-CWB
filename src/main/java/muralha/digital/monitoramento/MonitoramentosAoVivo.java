package muralha.digital.monitoramento;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

import muralha.digital._ini.Inicializacao;


public class MonitoramentosAoVivo
{	
	private static Logger logger = LogManager.getLogger(MonitoramentosAoVivo.class);
	
	public MonitoramentosAoVivo()
	{
		super();
	}
	
	public static MonitoramentoAoVivo ObterConfigVigente() throws ConexaoException, SQLException 
	{
		MonitoramentoAoVivo configVigente = new MonitoramentoAoVivo();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";
		
		try
		{
			sbSQL.append(" SELECT TOP 1 ");
			sbSQL.append(" 		  id, ");
			sbSQL.append(" 		  segundos, ");
			sbSQL.append(" 		  data_configuracao, ");
			sbSQL.append(" 		  ativo, ");
			sbSQL.append(" 		  id_usuario, ");
			sbSQL.append(" 		  usuario, ");
			sbSQL.append(" 		  nome_usuario ");
			sbSQL.append(" FROM   muralha.v_config_vigente_monitoramento_ao_vivo ");
			


			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
				
			if (rs.next()) 
			{
				configVigente.setId(UUID.fromString(rs.getString("id")));
				configVigente.setSegundos(rs.getInt("segundos"));
				configVigente.setDataConfiguracao(rs.getTimestamp("data_configuracao"));
				configVigente.setAtivo(rs.getBoolean("ativo"));
				configVigente.setIdUsuario(rs.getInt("id_usuario"));
				configVigente.setUsuario(rs.getString("usuario"));
				configVigente.setNomeUsuario(rs.getString("nome_usuario"));
				configVigente.setGrupoCamerasEmExibicao(Inicializacao.grupoCamerasEmExibicao);
			}
		}
		catch(Exception e)
		{
			erro = true;
			msgErro = "Erro ao obter configurações do banco de dados!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro)
			{
				throw new SQLException("Erro ao consultar configuração vigente no banco de dados!");
			}
		}
		
		return configVigente;
	}
	
	
	public static boolean SalvarConfiguracoes(Integer segundos, Integer idUsuario) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		CallableStatement cs = null;
		boolean retorno = false;
		
		try
		{
			sbSQL.append(" {? = call muralha.spu_salvar_config_monitoramento_ao_vivo(?, ?)} ");

			conn = Conexao.getConexao();
			cs = conn.prepareCall(sbSQL.toString());
			
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, segundos);
			cs.setInt(3, idUsuario);
			
			cs.execute();
			
			retorno = cs.getInt(1) > 0;
		}
		catch(Exception e)
		{
			String msgErro = "Erro ao salvar configurações no banco de dados!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (cs != null)
					cs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		
		return retorno;
	}
}
