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

@XmlRootElement(name = "RegistroDeFatoNaturezaDelituosas")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoNaturezaDelituosas {
    
    @XmlTransient
    private static Logger logger = LogManager.getLogger(RegistroDeFatoNaturezaDelituosas.class);
    
    @XmlElementWrapper(name = "ListaNaturezasDelituosas")
    @XmlElement(name = "RegistroDeFatoNaturezaDelituosa")
    private List<RegistroDeFatoNaturezaDelituosa> listaNaturezasDelituosas;
    
    public List<RegistroDeFatoNaturezaDelituosa> getListaNaturezasDelituosas() {
        return listaNaturezasDelituosas;
    }
    
    public void setListaNaturezasDelituosas(List<RegistroDeFatoNaturezaDelituosa> listaNaturezasDelituosas) {
        this.listaNaturezasDelituosas = listaNaturezasDelituosas;
    }
    
    public RegistroDeFatoNaturezaDelituosas() {
        super();
        this.listaNaturezasDelituosas = new ArrayList<>();
    }
    
    public static List<RegistroDeFatoNaturezaDelituosa> obterListaNaturezasDelituosas() throws ConexaoException, SQLException {
        List<RegistroDeFatoNaturezaDelituosa> listaRetorno = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT id, natureza_delituosa_desc ");
        sql.append("FROM muralha.registro_fato_natureza_delituosa ");
        sql.append("ORDER BY id ");
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql.toString());
            
            rs = ps.executeQuery();
            while (rs.next()) {
                RegistroDeFatoNaturezaDelituosa natureza = new RegistroDeFatoNaturezaDelituosa();
                natureza.setId(rs.getInt("id"));
                natureza.setDescricao(rs.getString("natureza_delituosa_desc"));
                
                listaRetorno.add(natureza);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao consultar naturezas delituosas: " + e.getMessage(), e);
            throw new SQLException("Erro ao consultar naturezas delituosas: ", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("Erro de SQL ao fechar recursos", e);
            }
        }
        
        return listaRetorno;
    }
}