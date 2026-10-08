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

import muralha.digital.util.Paginacao;

@XmlRootElement(name = "RegistroDeFatoIndividuoTipos")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoIndividuoTipos {

	@XmlTransient
	private static Logger logger = LogManager.getLogger(RegistroDeFatos.class);
	
	@XmlElementWrapper(name = "ListaRegistroDeFatos")
	@XmlElement(name = "RegistroDeFato")
	private List<RegistroDeFatoIndividuoTipo> listaTiposIndividuos;

	public List<RegistroDeFatoIndividuoTipo> getListaTiposIndividuos() {
		return listaTiposIndividuos;
	}

	public void setListaIndividuos(List<RegistroDeFatoIndividuoTipo> listaRegitrosTiposIndividuos) {
		this.listaTiposIndividuos = listaRegitrosTiposIndividuos;
	}

	public RegistroDeFatoIndividuoTipos() {
		super();
	}
	
	public static RegistroDeFatoIndividuoTipos BuscarTiposIndividuo()
	        throws ConexaoException, SQLException {

	    StringBuilder sbSQL = new StringBuilder();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    List<RegistroDeFatoIndividuoTipo> lista = new ArrayList<>();
	    RegistroDeFatoIndividuoTipos result = new RegistroDeFatoIndividuoTipos();
	    boolean erro = false;
	    String msgErro = "";

	    try {
	        sbSQL.append("SELECT * FROM muralha.registro_fato_individuo_tipo");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());
	        rs = ps.executeQuery();

	        while (rs.next()) {
	            RegistroDeFatoIndividuoTipo item = new RegistroDeFatoIndividuoTipo();        	
	            item.setId(Integer.parseInt(rs.getString("id")));
	            item.setDescricao(rs.getString("descricao"));
	            lista.add(item);
	        }
	    } catch (Exception e) {
	        erro = true;
	        msgErro = "Erro ao buscar tipos de indivíduo!";
	        logger.error(msgErro + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }

	        if (erro) throw new SQLException(msgErro);
	    }

	    result.setListaIndividuos(lista);
	    return result;
	}
}
