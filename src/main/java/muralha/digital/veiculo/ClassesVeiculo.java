package muralha.digital.veiculo;

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


@XmlRootElement		(name="ClassesVeiculo") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class ClassesVeiculo
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(ClassesVeiculo.class);
	
	@XmlElementWrapper	(name = "ListaClassesVeiculo")
	@XmlElement			(name = "ClasseVeiculo")
	private List<ClasseVeiculo> listaClassesVeiculo;	
	

	public List<ClasseVeiculo> getListaClassesVeiculo() {
		return listaClassesVeiculo;
	}


	public void setListaClassesVeiculo(List<ClasseVeiculo> listaClassesVeiculo) {
		this.listaClassesVeiculo = listaClassesVeiculo;
	}


	public ClassesVeiculo()
	{
		super();
	}
	
	public static List<ClasseVeiculo> ObterListaClassificacoes() throws ConexaoException, SQLException 
	{
		
		List<ClasseVeiculo> listaRet = new ArrayList<ClasseVeiculo>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id_classe, RTRIM(descricao) AS classe FROM classe_veiculo WHERE id_classe != '' ORDER BY descricao ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) 
			{
				ClasseVeiculo item = new ClasseVeiculo();

				item.setIdClasse(rs.getString("id_classe"));
				item.setClasse(rs.getString("classe"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaClassificacoes):: ", e);
		}
		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaRet;
	}
}
