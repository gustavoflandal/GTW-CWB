package muralha.digital.pontointeresse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

@XmlRootElement		(name="PontosInteresse")
@XmlAccessorType	(XmlAccessType.FIELD)
public class PontosInteresse 
{
	@XmlTransient
	private static Logger logger = LogManager.getLogger(PontosInteresse.class);
	
	@XmlElementWrapper	(name = "ListaPontosInteresse")
	@XmlElement 		(name = "PontoInteresse")
	private List<PontoInteresse> listaPontosInteresse;
	
	public List<PontoInteresse> getListaPontosInteresse()
	{
		return listaPontosInteresse;
	}
	
	public void setListaPontosInteresse(List<PontoInteresse> listaPontosInteresse)
	{
		this.listaPontosInteresse = listaPontosInteresse;
	}
	
	public static boolean inserirPontoInteresse(PontoInteresse pontoInteresse)
		throws ConexaoException, SQLException
	{
		StringBuilder sbSQL_local = new StringBuilder();
		
		sbSQL_local.append(" INSERT INTO muralha.ponto_interesse ");
		sbSQL_local.append(" (id, nome, descricao, id_tipo, latitude, longitude, data_cadastro, ativo) ");
		sbSQL_local.append(" VALUES (");
		sbSQL_local.append("'"+ pontoInteresse.getId() + "'" + ", ");
		sbSQL_local.append("'"+ pontoInteresse.getNome() + "'" + ", ");
		sbSQL_local.append("'"+ pontoInteresse.getDescricao() + "'" + ", ");
		sbSQL_local.append(pontoInteresse.getTipo() + ", ");
		sbSQL_local.append(pontoInteresse.getLatitude() + ", ");
		sbSQL_local.append(pontoInteresse.getLongitude() + ", ");
		sbSQL_local.append("GETDATE(), ");
		sbSQL_local.append("1) ");
		
		logger.info(sbSQL_local);
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL_local.toString());
			ps.executeUpdate();
			
			String equipamentos = pontoInteresse.getEquipamentos();
			String[] equips = equipamentos.split(";");
			
			for(String equip:equips)
			{
				UUID uuid_equip = UUID.randomUUID();
				StringBuilder sbSQL_equip = new StringBuilder();
				
				sbSQL_equip.append(" INSERT INTO muralha.ponto_interesse_equipamentos ");
				sbSQL_equip.append(" (id, id_ponto_interesse, id_local) ");
				sbSQL_equip.append(" VALUES (");
				sbSQL_equip.append("'"+ uuid_equip + "'" + ", ");
				sbSQL_equip.append("'"+ pontoInteresse.getId() + "'" + ", ");
				sbSQL_equip.append(equip + ")");
				
				ps = conn.prepareStatement(sbSQL_equip.toString());
				ps.executeUpdate();
			}
		}
		catch (Exception e)
		{
			throw new SQLException("Erro ao montar SQL (inserirPontoInteresse):: ", e);
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
		return true;
	}
	
	public static List<PontoInteresse> consultarPontosInteresse()
		throws ConexaoException, SQLException
	{
		logger.info("Chamando consultarPontosInteresse");
		
		List<PontoInteresse> pontosInteresse = new ArrayList<PontoInteresse>();
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT pin.id, ");
		sbSQL.append(" 		  pin.nome, ");
		sbSQL.append(" 		  pit.descricao, ");
		sbSQL.append(" 		  pin.latitude, ");
		sbSQL.append(" 		  pin.longitude, ");
		sbSQL.append(" 		  pin.data_cadastro, ");
		sbSQL.append(" 		  LTRIM(STUFF((SELECT ', ' + RTRIM(CAST(pie.id_local AS VARCHAR(6))) AS [text()] ");
		sbSQL.append(" 		  			   FROM   muralha.ponto_interesse_equipamentos pie ");
		sbSQL.append(" 		  			   WHERE  pie.id_ponto_interesse = pin.id ");
		sbSQL.append(" 		  			   ORDER BY ");
		sbSQL.append(" 		  					  pie.id_local ");
		sbSQL.append(" 		  			   FOR XML PATH('')), 1, 1, '' )) AS [equipamentos], ");
		sbSQL.append(" 		  LTRIM(STUFF((SELECT ', ' + RTRIM(lv.codigos_equipamentos) AS [text()] ");
		sbSQL.append(" 		  			   FROM   muralha.ponto_interesse_equipamentos pie2 ");
		sbSQL.append(" 		  					  JOIN local_vigente lv ");
		sbSQL.append(" 		  						   ON  lv.id_local = pie2.id_local ");
		sbSQL.append(" 		  			   WHERE  pie2.id_ponto_interesse = pin.id ");
		sbSQL.append(" 		  			   ORDER BY ");
		sbSQL.append(" 		  					  pie2.id_local ");
		sbSQL.append(" 		  			   FOR XML PATH('')), 1, 1, '' )) AS [codigos_equipamentos] ");
		sbSQL.append(" FROM   muralha.ponto_interesse pin ");
		sbSQL.append(" 		  JOIN muralha.ponto_interesse_tipos pit ");
		sbSQL.append(" 			  ON  pit.id = pin.id_tipo ");
		sbSQL.append(" GROUP BY ");
		sbSQL.append(" 		  pin.id, ");
		sbSQL.append(" 		  pin.nome, ");
		sbSQL.append(" 		  pit.descricao, ");
		sbSQL.append(" 		  pin.latitude, ");
		sbSQL.append(" 		  pin.longitude, ");
		sbSQL.append(" 		  pin.data_cadastro ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  pin.data_cadastro DESC, ");
		sbSQL.append(" 		  pin.nome ");

		
		//logger.info(sbSQL);
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				PontoInteresse pontoInteresse = new PontoInteresse();
				
				pontoInteresse.setId(UUID.fromString(rs.getString("id")));
				pontoInteresse.setNome(rs.getString("nome"));
				pontoInteresse.setDescricao(rs.getString("descricao"));
				pontoInteresse.setLatitude(rs.getFloat("latitude"));
				pontoInteresse.setLongitude(rs.getFloat("longitude"));
				pontoInteresse.setEquipamentos(rs.getString("equipamentos"));
				pontoInteresse.setCodigosEquipamentos(rs.getString("codigos_equipamentos"));
				
				pontosInteresse.add(pontoInteresse);
			}
		}
		catch(Exception e)
		{
			throw new SQLException("Erro ao montar SQL (consultarPontosInteresse):: ", e);
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
		logger.info("Requisicao concluida");
		
		return pontosInteresse;
	}
}
