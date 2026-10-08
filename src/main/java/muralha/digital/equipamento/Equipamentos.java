package muralha.digital.equipamento;

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


@XmlRootElement		(name="Equipamentos") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class Equipamentos
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(Equipamentos.class);
	
	@XmlElementWrapper	(name = "ListaEquipamentos")
	@XmlElement			(name = "Equipamento")	
	private List<Equipamento> listaEquipamentos;	
	

	public List<Equipamento> getListaEquipamentos() {
		return listaEquipamentos;
	}


	public void setListaEquipamentos(List<Equipamento> listaEquipamentos) {
		this.listaEquipamentos = listaEquipamentos;
	}


	public Equipamentos()
	{
		super();
	}
	
	public static List<Equipamento> ObterListaEquipamentos() throws ConexaoException, SQLException 
	{
		return ObterListaEquipamentos(null, null);
	}
	

	public static List<Equipamento> ObterListaEquipamentos(Integer municipio, Integer regiao) throws ConexaoException, SQLException 
	{
		
		List<Equipamento> listaRet = new ArrayList<Equipamento>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT l.id_local, ");
		sbSQL.append(" 		  l.sequencia_local, ");
		sbSQL.append(" 		  l.id_configuracao_equipamento, ");
		sbSQL.append(" 		  l.serie_equipamento, ");
		sbSQL.append(" 		  RTRIM(l.nome) AS nome, ");
		sbSQL.append(" 		  l.em_operacao, ");
		sbSQL.append(" 		  l.posicao_lat, ");
		sbSQL.append(" 		  l.posicao_lon, ");
		sbSQL.append(" 		  l.data_inicio, ");
		sbSQL.append(" 		  l.codigos_equipamentos ");
		sbSQL.append(" FROM   local_vigente l (NOLOCK) ");
		sbSQL.append(" WHERE  desativado = 0 ");
		if (municipio != null) {
			sbSQL.append("	AND id_localidade = ? ");
		}
		if (regiao != null) {
			sbSQL.append("	AND id_regiao = ? ");
		}
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  l.serie_equipamento ");


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			int i = 1;
			if (municipio != null) {
				ps.setInt(i, municipio);
				i++;
			}
			if (regiao != null) {
				ps.setInt(i, regiao);
				i++;
			}
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				Equipamento item = new Equipamento();

				item.setIdLocal(rs.getInt("id_local"));
				item.setSequenciaLocal(rs.getInt("sequencia_local"));
				item.setIdConfiguracaEquipamento(rs.getInt("id_configuracao_equipamento"));
				item.setSerieEquipamento(rs.getInt("serie_equipamento"));
				item.setNome(rs.getString("nome"));
				item.setEmOperacao(rs.getBoolean("em_operacao"));
				item.setLatitude(rs.getDouble("posicao_lat"));
				item.setLongitude(rs.getDouble("posicao_lon"));
				item.setDataInicioOperacao(rs.getDate("data_inicio"));
				item.setCodigoEquipamento(rs.getString("codigos_equipamentos"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaEquipamentos):: ", e);
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
	
	
	public static List<Equipamento> ObterListaMunicipiosEquipamentos() throws ConexaoException, SQLException 
	{
		
		List<Equipamento> listaRet = new ArrayList<Equipamento>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id_localidade, ");
		sbSQL.append("    	  dbo.InitCap(nome) AS nome, ");
		sbSQL.append("    	  uf ");
		sbSQL.append(" FROM   v_municipios_equipamentos ");
		sbSQL.append(" GROUP BY ");
		sbSQL.append("    	  id_localidade, ");
		sbSQL.append("    	  nome, ");
		sbSQL.append("    	  uf ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("    	  nome ");


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				Equipamento item = new Equipamento();

				item.setIdMunicipio(rs.getInt("id_localidade"));
				item.setMunicipio(rs.getString("nome"));
				item.setUfMunicipio(rs.getString("uf"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaMunicipiosEquipamentos):: ", e);
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
	
	public static List<Equipamento> ObterListaRegioesEquipamentos() throws ConexaoException, SQLException 
	{
		
		List<Equipamento> listaRet = new ArrayList<Equipamento>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT cr.id_regiao, ");
		sbSQL.append(" 		  cr.descricao ");
		sbSQL.append(" FROM   cad_regiao cr ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  cr.id_regiao ");		


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				Equipamento item = new Equipamento();

				item.setIdRegiao(rs.getInt("id_regiao"));
				item.setRegiao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaMunicipiosEquipamentos):: ", e);
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
