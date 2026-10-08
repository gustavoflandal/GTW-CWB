package muralha.digital.dispositivo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


@XmlRootElement		(name="DispositivosEquipamentos") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class DispositivosEquipamentos
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(DispositivosEquipamentos.class);
	
	@XmlElementWrapper	(name = "ListaDispositivos")
	@XmlElement			(name = "Dispositivo")	
	private List<DispositivoEquipamento> listaDispositivos;	
	

	public List<DispositivoEquipamento> getListaDispositivos() {
		return listaDispositivos;
	}


	public void setListaDispositivos(List<DispositivoEquipamento> listaDispositivos) {
		this.listaDispositivos = listaDispositivos;
	}


	public DispositivosEquipamentos(){
		listaDispositivos = new ArrayList<DispositivoEquipamento>();
	}
	
	public static List<DispositivoEquipamento> ObterListaDispositivosContagens(List<Integer> equipamentos) throws ConexaoException, SQLException 
	{
		List<DispositivoEquipamento> listaRet = new ArrayList<DispositivoEquipamento>();
		StringBuilder sbSQL = new StringBuilder();
		String sqlAux = "";
		
		sbSQL.append(" SET NOCOUNT ON ");
		sbSQL.append(" DECLARE @equipamentos ListaEquipamentos ");
		
		for (Integer idLocal : equipamentos)
			sqlAux = sqlAux + String.format(" INSERT INTO @equipamentos VALUES (%d) ", idLocal);
		
		sbSQL.append(sqlAux);
		
		sbSQL.append(" EXEC muralha.spuObterListaDispositivosContagens @equipamentos ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				DispositivoEquipamento disp = new DispositivoEquipamento();
				
				disp.setIdDispositivo(rs.getInt("id_local"));
				disp.setSerieEquipamento(rs.getInt("serie_equipamento"));
				disp.setDescDispositivo(rs.getString("nome"));
				disp.setLatitude(rs.getDouble("posicao_lat"));
				disp.setLongitude(rs.getDouble("posicao_lon"));
				disp.setCodigosEquipamentos(rs.getString("codigos_equipamentos"));
				disp.setConectado(rs.getBoolean("conectado"));
				disp.setStatusTrafego(rs.getInt("status_trafego"));
				disp.setPassagensDiasRecentes(rs.getInt("qtde_ultimos_10_dias"));
				disp.setPassagensUltimaHora(rs.getInt("qtde_ultima_hora"));
				disp.setPassagensUltimos15Min(rs.getInt("qtde_ultimos_15_min"));
				disp.setVelMediaUltimos15Min(rs.getInt("vel_media_ultimos_15_min"));
				disp.setInfracoesRegistradas(rs.getInt("infracoes_registradas"));
				disp.setJsonFluxoVelMediaDiario(rs.getString("contagens_fluxo_diario"));
				
				listaRet.add(disp);
			}
		}
		catch (Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ObterListaDispositivosContagens):: ", e);
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
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaRet;
	}
	
	public static List<DispositivoEquipamento> ObterListaDispositivosContagensSimplificado(List<Integer> equipamentos) throws ConexaoException, SQLException 
	{
		List<DispositivoEquipamento> listaRet = new ArrayList<DispositivoEquipamento>();
		StringBuilder sbSQL = new StringBuilder();
		String sqlAux = "";
		
		sbSQL.append(" SET NOCOUNT ON ");
		sbSQL.append(" DECLARE @equipamentos ListaEquipamentos ");
		
		for (Integer idLocal : equipamentos)
			sqlAux = sqlAux + String.format(" INSERT INTO @equipamentos VALUES (%d) ", idLocal);
		
		sbSQL.append(sqlAux);
		
		sbSQL.append(" EXEC muralha.spuObterListaDispositivosContagensSimplificado @equipamentos ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				DispositivoEquipamento disp = new DispositivoEquipamento();
				
				disp.setIdDispositivo(rs.getInt("id_local"));
				disp.setSerieEquipamento(rs.getInt("serie_equipamento"));
				disp.setDescDispositivo(rs.getString("nome"));
				disp.setLatitude(rs.getDouble("posicao_lat"));
				disp.setLongitude(rs.getDouble("posicao_lon"));
				disp.setCodigosEquipamentos(rs.getString("codigos_equipamentos"));
				disp.setConectado(rs.getBoolean("conectado"));
				disp.setStatusTrafego(rs.getInt("status_trafego"));
				
				listaRet.add(disp);
			}
		}
		catch (Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ObterListaDispositivosContagens):: ", e);
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
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaRet;
	}	
	
	public static List<DispositivoEquipamento> ObterListaDispositivosContagensMisto(List<Integer> equipamentos) throws ConexaoException, SQLException 
	{
		List<DispositivoEquipamento> listaRet = new ArrayList<DispositivoEquipamento>();
		StringBuilder sbSQL = new StringBuilder();
		String sqlAux = "";
		
		sbSQL.append(" SET NOCOUNT ON ");
		sbSQL.append(" DECLARE @equipamentos ListaEquipamentos ");
		
		for (Integer idLocal : equipamentos)
			sqlAux = sqlAux + String.format(" INSERT INTO @equipamentos VALUES (%d) ", idLocal);
		
		sbSQL.append(sqlAux);
		
		sbSQL.append(" EXEC muralha.spuObterListaDispositivosContagensMisto @equipamentos ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				DispositivoEquipamento disp = new DispositivoEquipamento();
				
				disp.setIdDispositivo(rs.getInt("id_local"));
				disp.setSerieEquipamento(rs.getInt("serie_equipamento"));
				disp.setDescDispositivo(rs.getString("nome"));
				disp.setLatitude(rs.getDouble("posicao_lat"));
				disp.setLongitude(rs.getDouble("posicao_lon"));
				disp.setCodigosEquipamentos(rs.getString("codigos_equipamentos"));
				disp.setConectado(rs.getBoolean("conectado"));
				disp.setStatusTrafego(rs.getInt("status_trafego"));
				disp.setPassagensUltimos15Min(rs.getInt("qtde_ultimos_15_min"));
				disp.setVelMediaUltimos15Min(rs.getInt("vel_media_ultimos_15_min"));
				disp.setInfracoesRegistradas(rs.getInt("infracoes_registradas"));
				
				List<Camera> cameras = Camera.ObterCamerasPorNumeroSerie(disp.getIdDispositivo());
				
				disp.setCameras(cameras);
				
				listaRet.add(disp);
			}
		}
		catch (Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ObterListaDispositivosContagens):: ", e);
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
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaRet;
	}	
	
	public static List<DispositivoEquipamento> ObterListaDispositivos() throws ConexaoException, SQLException 
	{
		List<DispositivoEquipamento> listaRet = new ArrayList<DispositivoEquipamento>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT l.id_local, ");
		sbSQL.append(" 		  l.sequencia_local, ");
		sbSQL.append(" 		  l.id_configuracao_equipamento, ");
		sbSQL.append(" 		  l.serie_equipamento, ");
		sbSQL.append(" 		  RTRIM(l.nome) AS nome, ");
		sbSQL.append(" 		  l.em_operacao, ");
		sbSQL.append(" 		  l.posicao_lat, ");
		sbSQL.append(" 		  l.posicao_lon, ");
		sbSQL.append(" 		  l.codigos_equipamentos ");
		sbSQL.append(" FROM   local_vigente l (NOLOCK)  ");
		sbSQL.append(" WHERE  desativado = 0 ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  l.serie_equipamento ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				DispositivoEquipamento disp = new DispositivoEquipamento();
				
				disp.setIdDispositivo(rs.getInt("id_local"));
				disp.setDescDispositivo(rs.getString("nome"));
				disp.setLatitude(rs.getDouble("posicao_lat"));
				disp.setLongitude(rs.getDouble("posicao_lon"));
				disp.setSerieEquipamento(rs.getInt("serie_equipamento"));
				disp.setCodigosEquipamentos(rs.getString("codigos_equipamentos"));
				listaRet.add(disp);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaDispositivos):: ", e);
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
		return listaRet;
	}	
	
	public static List<DispositivoEquipamento> ObterListaDispositivosPorCategoria(String categoria) 
	        throws ConexaoException, SQLException {
	    
	    List<DispositivoEquipamento> listaRet = new ArrayList<>();
	    StringBuilder sbSQL = new StringBuilder();
	    
	    sbSQL.append(" SELECT ");
	    sbSQL.append("     l.id_local, ");
	    sbSQL.append("     l.sequencia_local, ");
	    sbSQL.append("     l.id_configuracao_equipamento, ");
	    sbSQL.append("     l.serie_equipamento, ");
	    sbSQL.append("     RTRIM(l.nome) AS nome, ");
	    sbSQL.append("     l.em_operacao, ");
	    sbSQL.append("     l.posicao_lat, ");
	    sbSQL.append("     l.posicao_lon, ");
	    sbSQL.append("     l.codigos_equipamentos, ");
	    sbSQL.append("     ge.nome AS categoria ");
	    sbSQL.append(" FROM dbo.local_vigente l (NOLOCK) ");
	    sbSQL.append(" JOIN dbo.configuracao_equipamento ce (NOLOCK) ");
	    sbSQL.append("     ON l.id_configuracao_equipamento = ce.id_configuracao_equipamento ");
	    sbSQL.append(" JOIN dbo.grupo_equipamento ge (NOLOCK) ");
	    sbSQL.append("     ON ce.id_grupo_equipamento = ge.id_grupo_equipamento ");
	    sbSQL.append(" WHERE ge.nome = ? ");
	    sbSQL.append("   AND l.desativado = 0 ");
	    sbSQL.append(" ORDER BY ge.nome, l.nome ");
	    
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    
	    try {
	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());
	        ps.setString(1, categoria);
	        
	        rs = ps.executeQuery();
	        while (rs.next()) {
	            DispositivoEquipamento disp = new DispositivoEquipamento();
	            disp.setIdDispositivo(rs.getInt("id_local"));
	            disp.setDescDispositivo(rs.getString("nome"));
	            disp.setLatitude(rs.getDouble("posicao_lat"));
	            disp.setLongitude(rs.getDouble("posicao_lon"));
	            disp.setSerieEquipamento(rs.getInt("serie_equipamento"));
	            disp.setCodigosEquipamentos(rs.getString("codigos_equipamentos"));
	            disp.setCategoria(rs.getString("categoria")); 
	            listaRet.add(disp);
	        }
	        
	    } catch (Exception e) {
	        throw new SQLException("Erro ao montar SQL (ObterListaDispositivosPorCategoria):: ", e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            throw new ConexaoException("ERRO de SQL", e);
	        }
	    }
	    return listaRet;
	}

	

	public static List<DispositivoEquipamento> ObterListaDispositivosSentido() throws ConexaoException, SQLException 
	{
		List<DispositivoEquipamento> listaRet = new ArrayList<DispositivoEquipamento>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT	lv.serie_equipamento, 										");
		sbSQL.append(" 		cep.nome_pista,           										");
		sbSQL.append(" 		cep.sentido,              										");
		sbSQL.append(" 		lv.codigos_equipamentos,  										");
		sbSQL.append(" 		MIN(cep.id_pista) as pista_sentido								");
		sbSQL.append(" FROM local_vigente lv (NOLOCK) 										");
		sbSQL.append(" JOIN configuracao_equipamento_pista cep								");
		sbSQL.append(" 	on lv.id_configuracao_equipamento = cep.id_configuracao_equipamento	");
		sbSQL.append(" WHERE                          										");
		sbSQL.append(" 	lv.desativado = 0             										");
		sbSQL.append(" GROUP BY                       										");
		sbSQL.append(" 	lv.serie_equipamento,         										");
		sbSQL.append(" 	cep.nome_pista,               										");
		sbSQL.append(" 	cep.sentido,                  										");
		sbSQL.append(" 	lv.codigos_equipamentos       										");
		sbSQL.append(" ORDER BY                       										");
		sbSQL.append(" 	lv.serie_equipamento,         										");
		sbSQL.append(" 	MIN(cep.id_pista)             										");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				DispositivoEquipamento disp = new DispositivoEquipamento();
				
				disp.setDescDispositivo(rs.getString("nome_pista") + " " + rs.getString("sentido"));
				disp.setSerieEquipamento(rs.getInt("serie_equipamento"));
				disp.setPistaSentido(rs.getInt("pista_sentido"));
				disp.setCodigosEquipamentos(rs.getString("codigos_equipamentos"));
				listaRet.add(disp);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaDispositivos):: ", e);
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
		return listaRet;
	}
	
	
	public static DispositivoEquipamento ObterDispositivoById(int idLocal) throws ConexaoException, SQLException 
	{
		
		DispositivoEquipamento epqtoRet = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT 												 ");
		sbSQL.append(" 	 l.id_local                                          ");
		sbSQL.append(" 	,l.sequencia_local                                   ");
		sbSQL.append(" 	,l.id_configuracao_equipamento                       ");
		sbSQL.append(" 	,l.serie_equipamento                                 ");
		sbSQL.append(" 	,l.nome                                              ");
		sbSQL.append(" 	,l.em_operacao                                       ");
		sbSQL.append(" 	,l.posicao_lat                                       ");
		sbSQL.append(" 	,l.posicao_lon                                       ");
		sbSQL.append(" FROM   local_vigente l (NOLOCK)                       ");
		sbSQL.append(" WHERE l.id_local = ?                           	     ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, idLocal);
			
			rs = ps.executeQuery();
			if (rs.next()) 
			{
				epqtoRet = new DispositivoEquipamento();
				
				epqtoRet.setIdDispositivo(rs.getInt("id_local"));
				epqtoRet.setDescDispositivo(rs.getString("nome"));
				epqtoRet.setLatitude(rs.getDouble("posicao_lat"));
				epqtoRet.setLongitude(rs.getDouble("posicao_lon"));
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterDispositivoById):: ", e);
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
		return epqtoRet;
	}		
	

	public static List<DispositivoEquipamento> ObterDispositivos3DQuantitativo(Date dataIni, Date dataFim, Integer tipoRelatorio, Integer municipio, Integer regiao) throws ConexaoException, SQLException 
	{
		
		List<DispositivoEquipamento> listaRet = new ArrayList<DispositivoEquipamento>();
		StringBuilder sbSQL = new StringBuilder();
		
		// TODO - Temporário para teste
		sbSQL.append(" EXEC muralha.spu_getFluxoEquipamentoPorCategoria ?, ?, ?, ?, ? ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			ps.setInt(3, tipoRelatorio);
			
			if (municipio == null)
				ps.setNull(4, Types.INTEGER);
			else
				ps.setInt(4, municipio);
			
			if (regiao == null)
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, regiao);
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				DispositivoEquipamento disp = new DispositivoEquipamento();
				
				disp.setIdDispositivo(rs.getInt("id_local"));
				disp.setDescDispositivo(rs.getString("nome"));
				disp.setLatitude(rs.getDouble("posicao_lat"));
				disp.setLongitude(rs.getDouble("posicao_lon"));
				disp.setQuantitativo(rs.getInt("total"));
				
				listaRet.add(disp);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterDispositivos3DQuantitativo):: ", e);
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
		return listaRet;
	}	
	
}

