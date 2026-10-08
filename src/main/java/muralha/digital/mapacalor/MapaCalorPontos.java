package muralha.digital.mapacalor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
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


@XmlRootElement		(name="PontosMapaCalor") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class MapaCalorPontos extends HttpServlet 
{
	
	@XmlTransient
	private static final long serialVersionUID = 1L;
	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(MapaCalorPontos.class);
	
	@XmlElementWrapper	(name = "ListaPontosMapaCalor")
	@XmlElement			(name = "itemPontoCalor")	
	private List<MapaCalorPonto>  	listaPontosMapaCalor;	
	
	

	public List<MapaCalorPonto> getListaPontosMapaCalor() {
		return listaPontosMapaCalor;
	}

	public void setListaPontosMapaCalor(List<MapaCalorPonto> listaPontosMapaCalor) {
		this.listaPontosMapaCalor = listaPontosMapaCalor;
	}

	public MapaCalorPontos()
	{
		super();
	}
	
	public static List<MapaCalorPonto> ObterQuantitativosPorEquipamento(Date dtIni, Date dtFim, Boolean dtAtual) throws ConexaoException, SQLException 
	{
		
		List<MapaCalorPonto> listaRet = new ArrayList<MapaCalorPonto>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("   SELECT 										 ");
		sbSQL.append("   	lv.id_local,                                 ");
		sbSQL.append("   	lv.nome,                                     ");
		sbSQL.append("   	lv.posicao_lat,                              ");
		sbSQL.append("   	lv.posicao_lon,                              ");
		sbSQL.append("   	COALESCE                                     ");
		sbSQL.append("   	( (                                          ");
		sbSQL.append("   			SELECT COUNT(*)                      ");
		sbSQL.append("   			FROM   veiculo_sumarizado vs         ");
		sbSQL.append("   			WHERE vs.id_local = lv.id_local      ");
		
		if ( ! dtAtual)
			sbSQL.append("     			  AND vs.data BETWEEN ? AND ? 								  	 ");
		

		sbSQL.append("   	   ),	0                                    ");
		sbSQL.append("   	) AS total                                   ");
		sbSQL.append("                                                   ");
		sbSQL.append("   FROM local_vigente lv                           ");
		sbSQL.append("   WHERE lv.posicao_lat IS NOT NULL  and desativado = 0              ");
		sbSQL.append("   ORDER BY 5             				     	 ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			if ( ! dtAtual)
			{
				ps.setDate(1, new java.sql.Date(dtIni.getTime()));
				ps.setDate(2, new java.sql.Date(dtFim.getTime()));
				logger.info("ObterQuantitativosPorEquipamento:: Obtendo por Datas escolhidas pelo usuario(fluxo)");
			}
			else
				logger.info("ObterQuantitativosPorEquipamento:: Obtendo por DtAtual");
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				MapaCalorPonto disp = new MapaCalorPonto();
				
				disp.setIdLocal(rs.getInt("id_local"));
				disp.setDescLocal(rs.getString("nome"));
				disp.setLatitude(rs.getDouble("posicao_lat"));
				disp.setLongitude(rs.getDouble("posicao_lon"));
				disp.setQtde(rs.getInt("total"));
				
				listaRet.add(disp);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterQuantitativosPorEquipamento):: ", e);
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
	
	public static List<MapaCalorPonto> ObterContagemPorTipoAlerta(Date dtIni, Date dtFim, Boolean dtAtual,String idTipoAlertaOcorrencia) throws ConexaoException, SQLException {
	    
	    List<MapaCalorPonto> listaRet = new ArrayList<>();
	    StringBuilder sbSQL = new StringBuilder();
	    
	    sbSQL.append("SELECT                                              ");
	    sbSQL.append("    vtr.id_local,                                   ");
	    sbSQL.append("    lv.nome,                                        ");
	    sbSQL.append("    lv.posicao_lat,                                 ");
	    sbSQL.append("    lv.posicao_lon,                                 ");
	    sbSQL.append("    COUNT(vtr.id_local) AS contagem                 ");
	    sbSQL.append("FROM muralha.alerta a                               ");
	    sbSQL.append("JOIN muralha.alerta_veiculo av ON a.id = av.id_alerta ");
	    sbSQL.append("JOIN muralha.veiculo_tempo_real vtr ON vtr.id = av.id_veiculo_tempo_real ");
	    sbSQL.append("JOIN local_vigente lv ON vtr.id_local = lv.id_local ");
	    sbSQL.append("WHERE a.id_tipo_alerta_ocorrencia = ?          ");
	     
	    if ( ! dtAtual)
		   sbSQL.append("     			  AND CAST(a.data AS DATE) BETWEEN ? AND ? 								  	 ");
		
	    
	    sbSQL.append("GROUP BY                                            ");
	    sbSQL.append("    vtr.id_local,                                   ");
	    sbSQL.append("    lv.nome,                                        ");
	    sbSQL.append("    lv.posicao_lat,                                 ");
	    sbSQL.append("    lv.posicao_lon,                                 ");
	    sbSQL.append("    a.id_tipo_alerta_ocorrencia,                   ");
	    sbSQL.append("    vtr.id_local                                    ");
	    
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    
	    try {
	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());
	        ps.setString(1, idTipoAlertaOcorrencia);
	        
	        if ( ! dtAtual)
			{
	        	ps.setDate(2, new java.sql.Date(dtIni.getTime()));
				ps.setDate(3, new java.sql.Date(dtFim.getTime()));
				logger.info("--ObterQuantitativosPorEquipamento:: Obtendo por Datas escolhidas pelo usuario (Alerta) ");

			}
			else
				logger.info("--ObterQuantitativosPorEquipamento:: Obtendo por DtAtual - dtAtual: " + new java.util.Date());
	    
			
	        
	      
	        rs = ps.executeQuery();
	       
	        
	        while (rs.next()) {
	            MapaCalorPonto ponto = new MapaCalorPonto();
	            
	            ponto.setIdLocal(rs.getInt("id_local"));
	            ponto.setDescLocal(rs.getString("nome"));
	            ponto.setLatitude(rs.getDouble("posicao_lat"));
	            ponto.setLongitude(rs.getDouble("posicao_lon"));
	            ponto.setQtde(rs.getInt("contagem"));
	            
	            listaRet.add(ponto);
	        }
	        
	    } catch (Exception e) {
	        throw new SQLException("Erro ao executar SQL (ObterContagemPorTipoAlerta):: ", e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            throw new ConexaoException("Erro ao fechar recursos de conexão", e);
	        }
	    }
	    
	    return listaRet;
	}
	
	public static List<MapaCalorPonto> ObterContagemPorTipoIregularidade(Date dtIni, Date dtFim, Boolean dtAtual,String idTipoAlertaOcorrencia) throws ConexaoException, SQLException {
		    
		    List<MapaCalorPonto> listaRet = new ArrayList<>();
		    StringBuilder sbSQL = new StringBuilder();
		    
		    sbSQL.append("SELECT                                              ");
		    sbSQL.append("    vtr.id_local,                                   ");
		    sbSQL.append("    lv.nome,                                        ");
		    sbSQL.append("    lv.posicao_lat,                                 ");
		    sbSQL.append("    lv.posicao_lon,                                 ");
		    sbSQL.append("    COUNT(vtr.id_local) AS contagem                 ");
		    sbSQL.append("FROM muralha.ocorrencia o                               ");
		    sbSQL.append("JOIN muralha.alerta_veiculo av ON o.id_alerta = av.id_alerta ");
		    sbSQL.append("JOIN muralha.veiculo_tempo_real vtr ON vtr.id = av.id_veiculo_tempo_real ");
		    sbSQL.append("JOIN local_vigente lv ON vtr.id_local = lv.id_local ");
		    sbSQL.append("WHERE o.id_tipo_alerta_ocorrencia = ?          ");
		    		   
		    if ( ! dtAtual)
				sbSQL.append("     			  AND CAST(o.data AS DATE) BETWEEN ? AND ? 								  	 ");
			

		    
		    sbSQL.append("GROUP BY                                            ");
		    sbSQL.append("    vtr.id_local,                                   ");
		    sbSQL.append("    lv.nome,                                        ");
		    sbSQL.append("    lv.posicao_lat,                                 ");
		    sbSQL.append("    lv.posicao_lon,                                 ");
		    sbSQL.append("    o.id_tipo_alerta_ocorrencia,                   ");
		    sbSQL.append("    vtr.id_local                                    ");
		    
		    Connection conn = null;
		    PreparedStatement ps = null;
		    ResultSet rs = null;
		    
		    try {
		        conn = Conexao.getConexao();
		        ps = conn.prepareStatement(sbSQL.toString());
		        ps.setString(1, idTipoAlertaOcorrencia);
		        
		        if ( ! dtAtual)
				{
					ps.setDate(2, new java.sql.Date(dtIni.getTime()));
					ps.setDate(3, new java.sql.Date(dtFim.getTime()));
					logger.info("ObterQuantitativosPorEquipamento:: Obtendo por Datas escolhidas pelo usuario(Iregularidade)");
				}
				else
					logger.info("ObterQuantitativosPorEquipamento:: Obtendo por DtAtual");
				
		     
		        rs = ps.executeQuery();
		        while (rs.next()) {
		            MapaCalorPonto ponto = new MapaCalorPonto();
		            
		            ponto.setIdLocal(rs.getInt("id_local"));
		            ponto.setDescLocal(rs.getString("nome"));
		            ponto.setLatitude(rs.getDouble("posicao_lat"));
		            ponto.setLongitude(rs.getDouble("posicao_lon"));
		            ponto.setQtde(rs.getInt("contagem"));
		            
		            listaRet.add(ponto);
		        }
		        
		    } catch (Exception e) {
		        throw new SQLException("Erro ao executar SQL (ObterContagemPorTipoAlerta):: ", e);
		    } finally {
		        try {
		            if (rs != null) rs.close();
		            if (ps != null) ps.close();
		            if (conn != null) conn.close();
		        } catch (SQLException e) {
		            throw new ConexaoException("Erro ao fechar recursos de conexão", e);
		        }
		    }
		    
		    return listaRet;
		}

}

