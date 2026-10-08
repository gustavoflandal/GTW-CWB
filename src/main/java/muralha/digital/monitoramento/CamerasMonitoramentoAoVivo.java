package muralha.digital.monitoramento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

import muralha.digital._ini.Inicializacao;


@XmlRootElement(name = "CamerasMonitoramentoAoVivo")
@XmlAccessorType (XmlAccessType.FIELD)
public class CamerasMonitoramentoAoVivo
{
	@XmlTransient
	private static final Logger logger = Logger.getLogger(CamerasMonitoramentoAoVivo.class);
	
    private List<CameraMonitoramentoAoVivo> cameras;

    public CamerasMonitoramentoAoVivo()
	{
		cameras = new ArrayList<CameraMonitoramentoAoVivo>();
	}

	public List<CameraMonitoramentoAoVivo> getCameras() { return cameras; }
	public void setCameras(List<CameraMonitoramentoAoVivo> cameras) { this.cameras = cameras; }
	
	public void AdicionarCamera(CameraMonitoramentoAoVivo camera) {
		cameras.add(camera);
	}


	public static CamerasMonitoramentoAoVivo ObterCamerasMonitoramentoAoVivo(List<EquipamentoCameras> listaEquipamentoCameras, Integer idGrupoExibicao)
    {
		CamerasMonitoramentoAoVivo camerasMonAoVivo = new CamerasMonitoramentoAoVivo();
		
    	try
    	{
        	for (EquipamentoCameras equipamentoCameras : listaEquipamentoCameras)
        	{
        		Integer idLocal = equipamentoCameras.getIdLocal();
        		Integer serieEquipamento = equipamentoCameras.getSerieEquipamento();
        		String nomeEquipamento = equipamentoCameras.getNome();
        		
        		for (Camera camera : equipamentoCameras.getCameras())
        		{
        			if (idGrupoExibicao.equals(camera.getIdGrupoExibicao()))
        			{
        				CameraMonitoramentoAoVivo cameraAdd = new CameraMonitoramentoAoVivo();
        				
        				cameraAdd.setIdLocal(idLocal);
        				cameraAdd.setSerieEquipamento(serieEquipamento);
        				cameraAdd.setNomeEquipamento(nomeEquipamento);
        				cameraAdd.setDescricao(camera.getDescricao());
        				cameraAdd.setIp(camera.getIp());
        				cameraAdd.setIpLocal(camera.getIpLocal());
        				cameraAdd.setUrlStream(camera.getUrlStream());
        				cameraAdd.setQualidade(camera.getQualidade());
        				cameraAdd.setFramerate(camera.getFramerate());
        				cameraAdd.setResolution(camera.getResolution());
        				cameraAdd.setTipoCam(camera.getTipoCam());
        				cameraAdd.setPista(camera.getPista());
        				cameraAdd.setIdGrupoExibicao(camera.getIdGrupoExibicao());

        				camerasMonAoVivo.AdicionarCamera(cameraAdd);
        			}
    			}
    		}
		}
    	catch (Exception e)
    	{
    		logger.error("Erro ao obter câmeras para monitoramento ao vivo. " + e.getMessage(), e);
		}
    	
    	return camerasMonAoVivo;
    }
	
	
	public static List<EquipamentoCameras> ObterCamerasMonitoramentoAoVivoBD() throws ConexaoException, SQLException
    {
		List<EquipamentoCameras> listaEqptoCameras = new ArrayList<EquipamentoCameras>();
		
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Integer idGrupoExibicao = 1, contadorCameras = 1;
		
    	try
    	{
    		sbSQL.append(" EXEC muralha.spu_obterCamerasMonitoramentoAoVivo ");
    		
    		conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				Integer idLocal = null, serieEquipamento = null;
	            String nomeEquipamento = null;
	            
	            idLocal = rs.getInt("id_local");
	            serieEquipamento = rs.getInt("serie_equipamento");
	            nomeEquipamento = rs.getString("nome");
	            
	            logger.info("Equipamento Id : " + idLocal + " | Série: " + serieEquipamento + " | Nome: " + nomeEquipamento);
	            
	            EquipamentoCameras equipamentoCameras = new EquipamentoCameras();
	            
	            equipamentoCameras.setIdLocal(idLocal);
	            equipamentoCameras.setSerieEquipamento(serieEquipamento);
	            equipamentoCameras.setNome(nomeEquipamento);
	            
	            listaEqptoCameras.add(equipamentoCameras);
	            
	            if (contadorCameras > Inicializacao.totalCamerasGrupoExibicao)
            	{
            		idGrupoExibicao++;
            		contadorCameras = 1;
            	}
	            
	            String descricaoCamera = rs.getString("descricao_camera");
            	logger.info("Cameras de Monitoramento: " + serieEquipamento + " - " + descricaoCamera);
            	
            	Camera cam = new Camera();
            	cam.setDescricao(descricaoCamera);
            	cam.setIp(rs.getString("ip"));
            	cam.setIpLocal(rs.getString("ip_local"));
            	cam.setUrlStream(rs.getString("url_stream"));
            	
            	cam.setIdGrupoExibicao(idGrupoExibicao);
            	
            	equipamentoCameras.AdicionarCamera(cam);
            	
            	contadorCameras++;
			}
		}
    	catch (Exception e)
    	{
    		logger.error("Erro ao obter câmeras para monitoramento ao vivo. " + e.getMessage(), e);
		}
    	
    	return listaEqptoCameras;
    }
	

	public static CamerasMonitoramentoAoVivo ObterCamerasPorIdLocal(List<EquipamentoCameras> listaEquipamentoCameras, Integer idLocal)
    {
		CamerasMonitoramentoAoVivo camerasMonAoVivo = new CamerasMonitoramentoAoVivo();
		
    	try
    	{
        	for (EquipamentoCameras equipamentoCameras : listaEquipamentoCameras)
        	{
        		if (idLocal.equals(equipamentoCameras.getIdLocal()))
        		{
            		Integer serieEquipamento = equipamentoCameras.getSerieEquipamento();
            		String nomeEquipamento = equipamentoCameras.getNome();
        			for (Camera camera : equipamentoCameras.getCameras())
        			{
        				CameraMonitoramentoAoVivo cameraAdd = new CameraMonitoramentoAoVivo();
        				
        				cameraAdd.setIdLocal(idLocal);
        				cameraAdd.setSerieEquipamento(serieEquipamento);
        				cameraAdd.setNomeEquipamento(nomeEquipamento);
        				cameraAdd.setDescricao(camera.getDescricao());
        				cameraAdd.setIp(camera.getIp());
        				cameraAdd.setIpLocal(camera.getIpLocal());
        				cameraAdd.setUrlStream(camera.getUrlStream());
        				cameraAdd.setQualidade(camera.getQualidade());
        				cameraAdd.setFramerate(camera.getFramerate());
        				cameraAdd.setResolution(camera.getResolution());
        				cameraAdd.setTipoCam(camera.getTipoCam());
        				cameraAdd.setPista(camera.getPista());
        				cameraAdd.setIdGrupoExibicao(camera.getIdGrupoExibicao());

        				camerasMonAoVivo.AdicionarCamera(cameraAdd);
        			}
        		}
    		}
		}
    	catch (Exception e)
    	{
    		logger.error("Erro ao obter câmeras para monitoramento ao vivo. " + e.getMessage(), e);
		}
    	
    	return camerasMonAoVivo;
    }
	
	
	public static int ObterMaiorGrupoExibicao(List<EquipamentoCameras> listaEquipamentoCameras)
    {
		int maiorGrupo = 1;
		
    	try
    	{
        	for (EquipamentoCameras equipamentoCameras : listaEquipamentoCameras)
        	{
        		for (Camera camera : equipamentoCameras.getCameras())
        		{
    				if (camera.getIdGrupoExibicao() > maiorGrupo)
    					maiorGrupo = camera.getIdGrupoExibicao(); 
    			}
    		}
		}
    	catch (Exception e)
    	{
    		logger.error("Erro ao obter maior grupo de câmeras para monitoramento ao vivo. " + e.getMessage(), e);
		}
    	
    	return maiorGrupo;
    }
}
