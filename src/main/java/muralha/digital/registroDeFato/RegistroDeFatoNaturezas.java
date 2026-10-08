package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

@XmlRootElement(name = "RegistroDeFatoNaturezas") 
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoNaturezas {
    
    @XmlTransient
    private static Logger logger = LogManager.getLogger(RegistroDeFatoNaturezas.class);
    
    @XmlElementWrapper(name = "RegistroDeFatoNaturezaLista")
    @XmlElement(name = "RegistroDeFatoNatureza")    
    private List<RegistroDeFatoNatureza> listaNaturezas;    

    public List<RegistroDeFatoNatureza> getListaNaturezas() {
        return listaNaturezas;
    }

    public void setListaNaturezas(List<RegistroDeFatoNatureza> listaNaturezas) {
        this.listaNaturezas = listaNaturezas;
    }

    public RegistroDeFatoNaturezas() {
        super();
    }
    
    public static List<RegistroDeFatoNatureza> ObterListaNaturezas() throws ConexaoException, SQLException {
        List<RegistroDeFatoNatureza> listaRet = new ArrayList<RegistroDeFatoNatureza>();
        StringBuilder sbSQL = new StringBuilder();
        
        sbSQL.append(" SELECT id, ");
        sbSQL.append("        id_registro_tipo, ");
        sbSQL.append("        natureza_desc, ");
        sbSQL.append("        requer_bo ");
        sbSQL.append(" FROM   muralha.registro_fato_natureza ");
        sbSQL.append(" ORDER BY ");
        sbSQL.append("        natureza_desc ");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());
            
            rs = ps.executeQuery();
            while (rs.next()) {
                RegistroDeFatoNatureza item = new RegistroDeFatoNatureza();

                item.setId(rs.getInt("id"));
                item.setIdRegistroTipo(rs.getInt("id_registro_tipo"));
                item.setNaturezaDesc(rs.getString("natureza_desc"));
                item.setRequerBo(rs.getBoolean("requer_bo"));
                
                listaRet.add(item);
            }
            
        } catch (Exception e) {
            throw new SQLException("Erro ao consultar naturezas de Registro de Fato: ", e);
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
    
    public static List<RegistroDeFatoNatureza> ObterNaturezasPorTipo(Integer idTipoRegistro) throws ConexaoException, SQLException {
        List<RegistroDeFatoNatureza> listaRet = new ArrayList<RegistroDeFatoNatureza>();
        StringBuilder sbSQL = new StringBuilder();
        
        sbSQL.append(" SELECT id, ");
        sbSQL.append("        id_registro_tipo, ");
        sbSQL.append("        natureza_desc, ");
        sbSQL.append("        requer_bo ");
        sbSQL.append(" FROM   muralha.registro_fato_natureza ");
        sbSQL.append(" WHERE  id_registro_tipo = ? ");
        sbSQL.append(" ORDER BY ");
        sbSQL.append("        natureza_desc ");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());
            ps.setInt(1, idTipoRegistro);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                RegistroDeFatoNatureza item = new RegistroDeFatoNatureza();

                item.setId(rs.getInt("id"));
                item.setIdRegistroTipo(rs.getInt("id_registro_tipo"));
                item.setNaturezaDesc(rs.getString("natureza_desc"));
                item.setRequerBo(rs.getBoolean("requer_bo"));
                
                listaRet.add(item);
            }
            
        } catch (Exception e) {
            throw new SQLException("Erro ao consultar naturezas por tipo de Registro de Fato: ", e);
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