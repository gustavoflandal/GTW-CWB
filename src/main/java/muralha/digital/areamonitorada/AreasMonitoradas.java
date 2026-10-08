package muralha.digital.areamonitorada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

@XmlRootElement(name = "AreasMonitoradas")
@XmlAccessorType(XmlAccessType.FIELD)
public class AreasMonitoradas {
	
    @XmlTransient
    private static final Logger logger = LogManager.getLogger(AreasMonitoradas.class);

    @XmlElementWrapper(name = "listaAreasMonitoradas")
    @XmlElement(name = "areasMonitoradas")
    private List<AreaMonitorada> areasMonitoradas;
    
    @XmlElementWrapper(name = "equipamentoAreaMonitorada")
    @XmlElement(name = "equipamentosAreaMonitorada")
    private List<EquipamentoAreaMonitorada> equipamentosAreaMonitorada;
    
    @XmlElementWrapper(name = "listaEquipamentos")
    @XmlElement(name = "equipamentos")
    private List<EquipamentoDTO> equipamentos;

	public List<AreaMonitorada> getAreasMonitoradas() {
		return areasMonitoradas;
	}

	public void setAreasMonitoradas(List<AreaMonitorada> areasMonitoradas) {
		this.areasMonitoradas = areasMonitoradas;
	}

	public List<EquipamentoAreaMonitorada> getEquipamentosAreaMonitorada() {
		return equipamentosAreaMonitorada;
	}

	public void setEquipamentosAreaMonitorada(List<EquipamentoAreaMonitorada> equipamentosAreaMonitorada) {
		this.equipamentosAreaMonitorada = equipamentosAreaMonitorada;
	}  
	
    
    public void setListaEquipamentos(List<EquipamentoDTO> equipamentos) {
        this.equipamentos = equipamentos;
    }
	
    public static int CadastrarAreaMonitorada(String nome, String jsonData)
            throws ConexaoException, SQLException {

    	String sql = "INSERT INTO muralha.area_monitorada (nome, data_cadastro, dados_json) " +
                "OUTPUT INSERTED.id " +
                "VALUES (?, GETDATE(), ?)"; 

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.setString(2, jsonData);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int idGerado = rs.getInt(1);
                
                return idGerado;
            }
            
            return 0;

        } catch (Exception e) {
            throw new SQLException("Erro ao montar SQL (CadastrarAreaMonitorada):", e);
        }
    }
    
    public static Boolean CadastrarEquipamentosAreaMonitorada(int idAreaMonitorada, int[] idsEquipamentos)
            throws ConexaoException, SQLException {

    	String sql = "INSERT INTO muralha.equipamentos_area_monitorada (id_area_monitorada, id_equipamento) VALUES (?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        	
        	 for (int idEquip : idsEquipamentos) {
                 ps.setInt(1, idAreaMonitorada);
                 ps.setInt(2, idEquip);
                 ps.addBatch();
             }
       	     
        	ps.executeBatch();
            return true;

        } catch (Exception e) {
            throw new SQLException("Erro ao montar SQL (CadastrarAreaMonitorada):", e);
        }
    }
    
    public static List<AreaMonitorada> BuscarAreasCadastradas(String nome, int offset)
            throws ConexaoException, SQLException {
    	
    	List<AreaMonitorada> lista = new ArrayList<>();
    	//offset = offset > 0 ? (offset * 10) + 1 : 0;
    	
    	String sql = "";
    	boolean filtroNome = nome != null && !nome.trim().isEmpty();
    	
    	System.out.println("filtroNome: " +filtroNome);
    	System.out.println("Nome: " +nome);

    	if(filtroNome) {
    		sql = "SELECT * FROM muralha.area_monitorada am " +
    	              "WHERE am.nome LIKE ? and am.deletado = 0" +
    	              "ORDER BY am.data_cadastro DESC ";
    	}else {
    		sql = "SELECT * FROM muralha.area_monitorada am " +
    				  "WHERE am.deletado = 0" +
    	              "ORDER BY am.data_cadastro DESC ";
    	}

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        	
        	if(filtroNome) {
        		ps.setString(1, "%" + nome + "%");
        	}
        	
        	 try (ResultSet rs = ps.executeQuery()) {
                 while (rs.next()) {
                     AreaMonitorada area = new AreaMonitorada();
                     area.setId(rs.getInt("id"));
                     area.setNome(rs.getString("nome"));
                     area.setData_cadastro(rs.getTimestamp("data_cadastro"));
                     area.setData_atualizacao(rs.getTimestamp("data_atualizacao"));
                     area.setDados_json(rs.getString("dados_json"));
                     area.setDeletado(rs.getBoolean("deletado"));

                     lista.add(area);
                 }
             }
        	 
        	 return lista;

        } catch (Exception e) {
            throw new SQLException("Erro ao montar SQL (BuscarAreasCadastradas):", e);
        }
    }
    
    public static AreaMonitorada BuscarAreaPorNome(String nome)
            throws ConexaoException, SQLException {   
    	
    	AreaMonitorada area = new AreaMonitorada();
    	
    	String sql = "SELECT * FROM muralha.area_monitorada am " +
	              "WHERE am.nome = ? and am.deletado = 0" +
	              "ORDER BY am.data_cadastro DESC ";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        	
        	ps.setString(1, nome);
        	
        	 try (ResultSet rs = ps.executeQuery()) {
                 while (rs.next()) {
                     area.setId(rs.getInt("id"));
                     area.setNome(rs.getString("nome"));
                     area.setData_cadastro(rs.getTimestamp("data_cadastro"));
                     area.setData_atualizacao(rs.getTimestamp("data_atualizacao"));
                     area.setDados_json(rs.getString("dados_json"));
                     area.setDeletado(rs.getBoolean("deletado"));
                 }
             }
        	 
        	 return area;

        } catch (Exception e) {
            throw new SQLException("Erro ao montar SQL (BuscarAreaPorNome):", e);
        }
    }
    
    public static int ExcluirAreaMonitorada(int id)
            throws ConexaoException, SQLException {

    	String sql = "update muralha.area_monitorada set deletado = 1 where id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);            
            int result = ps.executeUpdate();
            return result;
            
        } catch (Exception e) {
            throw new SQLException("Erro ao montar SQL (ExcluirAreaMonitorada):", e);
        }
    }
    
    public static List<EquipamentoDTO> ObterEquipamentos()
            throws ConexaoException, SQLException {

        List<EquipamentoDTO> listaRet = new ArrayList<>();
        
        String sql = "select id_local, sequencia_local, nome, id_localidade, posicao_lat, posicao_lon \r\n"
        		+ "from local_vigente \r\n"
        		+ "where posicao_lat is not null and posicao_lon is not null and desativado = 0\r\n"
        		+ "order by id_local asc";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	EquipamentoDTO item = new EquipamentoDTO();

                    item.setId_local(rs.getInt("id_local"));
                    item.setSequencia_local(rs.getInt("sequencia_local"));
                    item.setNome(rs.getString("nome"));
                    item.setId_localidade(rs.getInt("id_localidade"));
                    item.setPosicao_lat(rs.getBigDecimal("posicao_lat"));
                    item.setPosicao_lon(rs.getBigDecimal("posicao_lon"));                    

                    listaRet.add(item);
                }
            }

        } catch (Exception e) {
            throw new SQLException("Erro ao montar SQL (ObterEquipamentos):", e);
        }

        return listaRet;
    }
}
