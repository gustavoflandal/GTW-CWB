package muralha.digital.dispositivo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.consilux.lib.Conexao;
import com.consilux.infra.exception.ConexaoException;

public class Camera {
	 private int idLocal;
     private int idCamera;
     private String ipCamera;
     private String tipoCamera;
     private int relevante;

     // Getters e setters
     public int getIdLocal() { return idLocal; }
     public void setIdLocal(int idLocal) { this.idLocal = idLocal; }

     public int getIdCamera() { return idCamera; }
     public void setIdCamera(int idCamera) { this.idCamera = idCamera; }

     public String getIpCamera() { return ipCamera; }
     public void setIpCamera(String ipCamera) { this.ipCamera = ipCamera; }

     public String getTipoCamera() { return tipoCamera; }
     public void setTipoCamera(String tipoCamera) { this.tipoCamera = tipoCamera; }

     public int getRelevante() { return relevante; }
     public void setRelevante(int relevante) { this.relevante = relevante; }

     @Override
     public String toString() {
         return "Camera{" +
                "idLocal=" + idLocal +
                ", idCamera=" + idCamera +
                ", ipCamera='" + ipCamera + '\'' +
                ", tipoCamera='" + tipoCamera + '\'' +
                ", relevante=" + relevante +
                '}';
     }

     public static List<Camera> ObterCamerasPorNumeroSerie(int id_dispositivo) 
    	        throws ConexaoException, SQLException 
    	{
    	    List<Camera> cameras = new ArrayList<>();

    	    String sql = "SELECT id_local, id_camera, ip_camera, tipo_camera, relevante " +
    	                 "FROM v_equipamento_cameras_todas WHERE id_local = ?";

    	    Connection conn = null;
    	    PreparedStatement ps = null;
    	    ResultSet rs = null;

    	    try {
    	        conn = Conexao.getConexao();
    	        ps = conn.prepareStatement(sql);
    	        ps.setInt(1, id_dispositivo);

    	        rs = ps.executeQuery();

    	        while (rs.next()) {
    	            Camera cam = new Camera();
    	            cam.setIdLocal(rs.getInt("id_local"));
    	            cam.setIdCamera(rs.getInt("id_camera"));
    	            cam.setIpCamera(rs.getString("ip_camera"));
    	            cam.setTipoCamera(rs.getString("tipo_camera"));
    	            cam.setRelevante(rs.getInt("relevante"));

    	            cameras.add(cam);
    	        }

    	    } catch (Exception e) {
    	        throw new SQLException("Erro ao montar SQL (ObterCamerasPorNumeroSerie):: ", e);
    	    } finally {
    	        try {
    	            if (rs != null) rs.close();
    	            if (ps != null) ps.close();
    	            if (conn != null) conn.close();
    	        } catch (SQLException e) {
    	            throw new ConexaoException("ERRO de SQL", e);
    	        }
    	    }

    	    return cameras;
    	}


}