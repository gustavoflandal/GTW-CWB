package muralha.digital.relatorios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class CalendariosIntensidades 
{
	@XmlTransient
	private static final Logger logger = Logger.getLogger(CalendariosIntensidades.class);
	
	@XmlElementWrapper		(name="ListaCalendariosIntensidades")
	@XmlElement				(name="CalendarioIntensidade")
	private List<CalendarioIntensidade> calendariosIntensidades;

	public List<CalendarioIntensidade> getCalendariosIntensidades() {
		return calendariosIntensidades;
	}

	public void setCalendariosIntensidades(List<CalendarioIntensidade> calendariosIntensidades) {
		this.calendariosIntensidades = calendariosIntensidades;
	}
	
	public CalendariosIntensidades() {}
	
	public static CalendariosIntensidades ObterCalendarioIntensidade(Date dataIni, Date dataFim, Integer idLocal, Integer tipoRelatorio, Integer municipio, Integer regiao) throws ConexaoException, SQLException 
	{
		CalendariosIntensidades calendario = new CalendariosIntensidades();
		
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
			
			sbSQL.append(" EXEC muralha.spu_getCalendarioIntensidade ?, ?, ?, ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			ps.setInt(4, tipoRelatorio);

			if (municipio == null) 
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, municipio);
			
			if (regiao == null) 
				ps.setNull(6, Types.INTEGER);
			else
				ps.setInt(6, regiao);
			
			rs = ps.executeQuery();
			
			List<CalendarioIntensidade> calendarioIntensidade = new ArrayList<CalendarioIntensidade>();
			
			while (rs.next()) 
			{
				CalendarioIntensidade calendarioItem = new CalendarioIntensidade();
				
				calendarioItem.setSemana(rs.getInt(1));
				
				calendarioItem.setDomingo(rs.getString(2));
				calendarioItem.setSegunda(rs.getString(3));
				calendarioItem.setTerca(rs.getString(4));
				calendarioItem.setQuarta(rs.getString(5));
				calendarioItem.setQuinta(rs.getString(6));
				calendarioItem.setSexta(rs.getString(7));
				calendarioItem.setSabado(rs.getString(8));
				
				calendarioIntensidade.add(calendarioItem);
				
			}
			
			calendario.setCalendariosIntensidades(calendarioIntensidade);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
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
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return calendario;
	}
}
