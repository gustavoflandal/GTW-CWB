package muralha.digital.anomalia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

@XmlRootElement		(name="AlertasAnomalias") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class Anomalias  extends HttpServlet 
{
	@XmlTransient
	private static final long serialVersionUID = 1L;

	@XmlTransient
	private static Logger logger = LogManager.getLogger(Anomalias.class);

	@XmlElementWrapper		(name="ListaAlertasAnomalias")
	@XmlElement				(name="AlertaAnomalia")
	private List<Anomalia> alertasAnomalias;

	public List<Anomalia> getAlertasAnomalias() {
		return alertasAnomalias;
	}

	public void setCalendariosIntensidades(List<Anomalia> alertasAnomalias) {
		this.alertasAnomalias = alertasAnomalias;
	}
	
	public Anomalias() {}
	
	
	public static Anomalias ObterDadosAlertaAnomalias() throws ConexaoException, SQLException 
	{
		Anomalias anomalias = new Anomalias();
		
		StringBuilder sbSQL = new StringBuilder();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";

		try {
			
			sbSQL.append(" select ");
			sbSQL.append("		id, ");
			sbSQL.append("		tipo, ");
			sbSQL.append("		possui_anomalia, ");
			sbSQL.append("		desc_anomalia, ");
			sbSQL.append("		data_update ");
			sbSQL.append(" from muralha.anomalia ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			List<Anomalia> listAnomalias = new ArrayList<Anomalia>();
			
			while (rs.next()) 
			{
				Anomalia anomalia = new Anomalia();
				
				anomalia.setId(rs.getInt(1));
				anomalia.setTipo(rs.getString(2));
				anomalia.setPossuiAnomalia(rs.getInt(3));
				anomalia.setDescAnomalia(rs.getString(4));
				
				listAnomalias.add(anomalia);
			}
			
			anomalias.setCalendariosIntensidades(listAnomalias);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados de anomalias!";
			logger.error(msgErro, e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados de anomalias!");
			}
		}
		
		return anomalias;
	}
}
