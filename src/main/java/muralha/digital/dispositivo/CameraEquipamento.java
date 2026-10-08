package muralha.digital.dispositivo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class CameraEquipamento {

    private String serieEquipamento;
    private String tipoCamera;
    private boolean desativado;
    private boolean emOperacao;
    private double posicaoLat;
    private double posicaoLon;
    private String tipo;
    private String usuario;

    public String getSerieEquipamento() {
        return serieEquipamento;
    }

    public void setSerieEquipamento(String serieEquipamento) {
        this.serieEquipamento = serieEquipamento;
    }

    public String getTipoCamera() {
        return tipoCamera;
    }

    public void setTipoCamera(String tipoCamera) {
        this.tipoCamera = tipoCamera;
    }

    public boolean isDesativado() {
        return desativado;
    }

    public void setDesativado(boolean desativado) {
        this.desativado = desativado;
    }

    public boolean isEmOperacao() {
        return emOperacao;
    }

    public void setEmOperacao(boolean emOperacao) {
        this.emOperacao = emOperacao;
    }

    public double getPosicaoLat() {
        return posicaoLat;
    }

    public void setPosicaoLat(double posicaoLat) {
        this.posicaoLat = posicaoLat;
    }

    public double getPosicaoLon() {
        return posicaoLon;
    }

    public void setPosicaoLon(double posicaoLon) {
        this.posicaoLon = posicaoLon;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    
	public static List<CameraEquipamento> ObterCamerasPorNumeroSerie(int numeroSerie) 
	        throws ConexaoException, SQLException 
	{
	    List<CameraEquipamento> listaRet = new ArrayList<>();
	    StringBuilder sbSQL = new StringBuilder();
	    
	    sbSQL.append("SELECT c.serie_equipamento, ec.tipo_camera, lv.desativado, lv.em_operacao, ");
	    sbSQL.append("lv.posicao_lat, lv.posicao_lon, lv.tipo, lv.serie_equipamento, lv.usuario ");
	    sbSQL.append("FROM local_vigente lv ");
	    sbSQL.append("LEFT JOIN configuracao_equipamento c ON c.id_configuracao_equipamento = lv.id_configuracao_equipamento ");
	    sbSQL.append("LEFT JOIN v_equipamento_cameras ec ON ec.id_local = lv.id_local ");
	    sbSQL.append("WHERE c.serie_equipamento = ?");

	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());
	        ps.setInt(1, numeroSerie);

	        rs = ps.executeQuery();
	        while (rs.next()) {
	        	CameraEquipamento disp = new CameraEquipamento();

	            disp.setSerieEquipamento(rs.getString("serie_equipamento"));
	            disp.setTipoCamera(rs.getString("tipo_camera"));
	            disp.setDesativado(rs.getBoolean("desativado"));
	            disp.setEmOperacao(rs.getBoolean("em_operacao"));
	            disp.setPosicaoLat(rs.getDouble("posicao_lat"));
	            disp.setPosicaoLon(rs.getDouble("posicao_lon"));
	            disp.setTipo(rs.getString("tipo"));
	            disp.setUsuario(rs.getString("usuario"));

	            listaRet.add(disp);
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
	    return listaRet;
	}
	
}
