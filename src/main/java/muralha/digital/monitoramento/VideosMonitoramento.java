package muralha.digital.monitoramento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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


@XmlRootElement		(name="VideosMonitoramento") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class VideosMonitoramento
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(VideosMonitoramento.class);
	
	@XmlElementWrapper	(name = "ListaVideosMonitoramento")
	@XmlElement			(name = "VideoMonitoramento")	
	private List<VideoMonitoramento> listaVideosMonitoramento;	
	private Integer quantidadeCameras;
	

	public List<VideoMonitoramento> getListaVideosMonitoramento() {
		return listaVideosMonitoramento;
	}

	public void setListaVideosMonitoramento(List<VideoMonitoramento> listaVideosMonitoramento) {
		this.listaVideosMonitoramento = listaVideosMonitoramento;
	}
	

	public Integer getQuantidadeCameras() {
		return quantidadeCameras;
	}
	public void setQuantidadeCameras(Integer quantidadeCameras) {
		this.quantidadeCameras = quantidadeCameras;
	}

	
	public VideosMonitoramento() { super(); }
	
	
	public static List<VideoMonitoramento> ObterVideosMonitoramento(Date dataIni, Date dataFim, Integer idLocal) throws ConexaoException, SQLException 
	{
		
		List<VideoMonitoramento> listaRet = new ArrayList<VideoMonitoramento>();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
		
			sbSQL.append(" SELECT vm.id, ");
			sbSQL.append(" 		  vm.id_local, ");
			sbSQL.append(" 		  vm.ip_camera, ");
			sbSQL.append(" 		  vm.data_hora, ");
			sbSQL.append(" 		  vm.endereco, ");
			sbSQL.append(" 		  vm.formato, ");
			sbSQL.append(" 		  vm.data_importacao ");
			sbSQL.append(" FROM   muralha.video_monitoramento vm ");
			sbSQL.append(" WHERE  vm.data_hora BETWEEN ? AND ? ");
			sbSQL.append(" 	 	  AND vm.id_local = ? ");
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  vm.ip_camera, ");
			sbSQL.append(" 		  vm.data_hora ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(dataIni.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			ps.setInt(3, idLocal);
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				VideoMonitoramento item = new VideoMonitoramento();
				
				item.setId(UUID.fromString(rs.getString("id")));
				item.setIdLocal(rs.getInt("id_local"));
				item.setIpCamera(rs.getString("ip_camera"));
				item.setDataHora(rs.getTimestamp("data_hora"));
				item.setEndereco(rs.getString("endereco"));
				item.setFormato(rs.getString("formato"));
				item.setDataImportacao(rs.getTimestamp("data_importacao"));
				
				listaRet.add(item);
			}
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter videos de monitoramento! " + e.getMessage(), e);
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
		}
		
		return listaRet;
	}
	
	
	public static List<VideoMonitoramento> ObterListaVideosExibicao(Date dataIni, Date dataFim, Integer idLocal) throws ConexaoException, SQLException 
	{
		
		List<VideoMonitoramento> listaRet = new ArrayList<VideoMonitoramento>();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			sbSQL.append(" EXEC muralha.spu_obterListaVideosMonitoramentoExibicao ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(dataIni.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			ps.setInt(3, idLocal);
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				VideoMonitoramento item = new VideoMonitoramento();
				
				item.setIdLocal(rs.getInt("id_local"));
				item.setSerieEquipamento(rs.getInt("serie_equipamento"));
				item.setIpCamera(rs.getString("ip_camera"));
				item.setTipoCamera(rs.getString("tipo_camera"));
				item.setListaIdsVideos(rs.getString("lista_id_videos"));
				item.setListaEnderecoVideos(rs.getString("lista_endereco_videos"));
				
				listaRet.add(item);
			}
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter videos de monitoramento! " + e.getMessage(), e);
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
		}
		
		return listaRet;
	}
}
