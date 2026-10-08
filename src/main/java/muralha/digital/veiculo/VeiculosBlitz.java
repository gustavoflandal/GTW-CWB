package muralha.digital.veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

import muralha.digital.util.Paginacao;


@XmlRootElement		(name="Veiculos") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class VeiculosBlitz
{	
	@XmlTransient
	private static final Logger logger = LogManager.getLogger(VeiculosBlitz.class);
	
	@XmlElementWrapper	(name = "ListaVeiculos")
	@XmlElement			(name = "Veiculo")	
	private List<VeiculoBlitz> listaVeiculos;
	
	private Paginacao paginacao;
	
	
	public VeiculosBlitz() {super();}
	

	public List<VeiculoBlitz> getListaVeiculos() {
		return listaVeiculos;
	}
	public void setListaVeiculos(List<VeiculoBlitz> listaVeiculos) {
		this.listaVeiculos = listaVeiculos;
	}
	
	public Paginacao getPaginacao() {
		return paginacao;
	}
	public void setPaginacao(Paginacao paginacao) {
		this.paginacao = paginacao;
	}
		

	/*
	 * Retornar lista com as novas passagens de veículos
	 */
	public static VeiculosBlitz ObterVeiculosBlitzEletronica() 
	{
		VeiculosBlitz retVeiculos = new VeiculosBlitz();
		List<VeiculoBlitz> listaVeiculos = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		try
		{
			sbSQL.append(" EXEC muralha.spu_obterVeiculosBlitzEletronica ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{				
				VeiculoBlitz veic = new VeiculoBlitz();
				
				veic.setId(UUID.fromString(rs.getString("id")));
				veic.setIdLocal(rs.getInt("id_local"));
				veic.setDescLocal(rs.getString("nome"));
				veic.setIdPista(rs.getInt("id_pista"));
				veic.setFaixa(rs.getInt("faixa"));
				veic.setPlaca(rs.getString("placa") != null ? rs.getString("placa") : "");
				veic.setVelocidade(rs.getInt("velocidade"));
				veic.setClassificacao(rs.getString("classificacao") != null ? rs.getString("classificacao").trim() : "");
				veic.setDataVeic(rs.getTimestamp("data"));
				
				veic.setTiposAlertas(rs.getString("tipos_alertas"));
				
				listaVeiculos.add(veic); 
			}
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter Lista de Veiculos para Blitz Eletronica: " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}	
		
		retVeiculos.setListaVeiculos(listaVeiculos);
		return retVeiculos;
	}    	
}
