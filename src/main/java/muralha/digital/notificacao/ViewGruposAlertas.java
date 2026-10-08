package muralha.digital.notificacao;

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

@XmlRootElement(name="ViewGruposAlertas")
@XmlAccessorType(XmlAccessType.FIELD)
public class ViewGruposAlertas {

	@XmlTransient
	private static Logger logger = LogManager.getLogger(ViewGruposAlertas.class);
	
	@XmlElementWrapper(name = "ListaGrupos")
    @XmlElement(name = "ViewGrupoAlertas")
	private List<ViewGrupoAlertas> viewGrupos;
	
	public List<ViewGrupoAlertas> getListaViewGrupoAlertas() {
		return viewGrupos;
	}

	public void setListaViewGrupoAlertas (List<ViewGrupoAlertas> listaGrupos) {
		this.viewGrupos = listaGrupos;
	}
	
	public static List<ViewGrupoAlertas> consultarView() 
			throws SQLException, ConexaoException
		{			
			List<ViewGrupoAlertas> listaRet = new ArrayList<ViewGrupoAlertas>();
			StringBuilder sbSQL = new StringBuilder();
			
			sbSQL.append("select * from muralha.v_grupo_alertas");
					
			Connection 			conn	= null;
			PreparedStatement 	ps 		= null;
			ResultSet 			rs 		= null;
			
			try
			{
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sbSQL.toString());
				rs = ps.executeQuery();
				
				while(rs.next()) {
					try
					{
						ViewGrupoAlertas item = new ViewGrupoAlertas();
						
						item.setId_grupo(rs.getInt("id_grupo"));
						item.setDescricao(rs.getString("descricao"));
						item.setId_grupo_pai(rs.getInt("id_grupo_pai"));
						
						listaRet.add(item);
					}
					catch(Exception e)
					{
						logger.error("Erro ao buscar os grupos na view:" + e);
					}
				}			
			}
			catch(Exception e)
			{
				throw new SQLException("Erro ao montar SQL (consultarView):: ", e);
			}
			finally 
			{
				try 
				{
					if (ps != null)
						ps.close();
					if (conn != null)
						conn.close();
					if (rs != null)
						rs.close();
				} 
				catch (SQLException e) 
				{
					throw new ConexaoException("ERRO de SQL", e);
				}			
			}
			return listaRet;
		}
}
