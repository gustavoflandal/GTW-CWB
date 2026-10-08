package muralha.digital.veiculo.imagem;

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
import com.consilux.model.Base64Utils;


@XmlRootElement		(name="VeiculoImagens") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class VeiculoImagens
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(VeiculoImagens.class);
	
	@XmlElementWrapper	(name = "ListaVeiculoImagens")
	@XmlElement			(name = "VeiculoImagem")	
	private List<VeiculoImagem> listaVeiculoImagens;	
	

	public List<VeiculoImagem> getListaVeiculoImagens() {
		return listaVeiculoImagens;
	}


	public void setListaVeiculoImagens(List<VeiculoImagem> listaVeiculoImagens) {
		this.listaVeiculoImagens = listaVeiculoImagens;
	}


	public VeiculoImagens()
	{
		super();
	}
	
	/*
	 * Retornar os dados da imagem do veículo, pelo ID.
	 */
	public static VeiculoImagem obterImagemPorId(UUID id) 
	{
		
		VeiculoImagem retorno = new VeiculoImagem();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, id_veiculo_tempo_real, imagem FROM muralha.veiculo_tempo_real_imagem (NOLOCK) WHERE id = ? ");

		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, id.toString());

			rs = ps.executeQuery();
			
			if (rs.next()) 
			{
				retorno.setId(UUID.fromString(rs.getString("id")));
				retorno.setIdVeiculoTempoReal(UUID.fromString(rs.getString("id_veiculo_tempo_real")));
				retorno.setImagem(rs.getBytes("imagem"));
			}
		}
		catch(Exception e) {
			logger.error("Erro ao obter imagem do veículo: " + e.getMessage(), e);
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
		
		}		
		
		return retorno;
	}
	
	/*
	 * Retornar lista de imagens por ID da Ocorrência
	 */
	public static List<VeiculoImagem> obterListaImagensPorIdAlerta(UUID idAlerta) 
	{
		
		List<VeiculoImagem> listaImagens = new ArrayList<VeiculoImagem>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT vtri.id, ");
		sbSQL.append(" 		  vtri.id_veiculo_tempo_real, ");
		sbSQL.append(" 		  vtri.imagem, ");
		sbSQL.append(" 		  vtri.indice_imagem, ");
		sbSQL.append(" 		  ROW_NUMBER() OVER(ORDER BY vtr.data, vtri.indice_imagem) AS num_imagem ");
		sbSQL.append(" FROM   muralha.veiculo_tempo_real_imagem vtri ");
		sbSQL.append(" 		  INNER JOIN muralha.veiculo_tempo_real vtr ");
		sbSQL.append(" 			   ON  vtr.id = vtri.id_veiculo_tempo_real ");
		sbSQL.append(" 		  INNER JOIN muralha.alerta_veiculo av ");
		sbSQL.append(" 			   ON  av.id_veiculo_tempo_real = vtr.id ");
		sbSQL.append(" WHERE  av.id_alerta = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  vtr.data, ");
		sbSQL.append(" 		  vtri.indice_imagem ");
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idAlerta.toString());

			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				VeiculoImagem item = new VeiculoImagem();
				
				item.setId(UUID.fromString(rs.getString("id")));
				item.setIdVeiculoTempoReal(UUID.fromString(rs.getString("id_veiculo_tempo_real")));
				item.setImagem(rs.getBytes("imagem"));
				item.setTpImagem(rs.getInt("indice_imagem"));
				item.setNumImagem(rs.getInt("num_imagem"));
				
				listaImagens.add(item);
			}
		}
		catch(Exception e) {
			logger.error("Erro ao obter lista de imagens da irregularidade: " + e.getMessage(), e);
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
		
		}		
		
		return listaImagens;
	}

	public static VeiculoImagem ObterImagemPorIdVeicTpImagem(UUID idVeic, int tpImagem) 
	{
		
		VeiculoImagem retorno = new VeiculoImagem();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT ");
		sbSQL.append(" id, id_veiculo_tempo_real, imagem, indice_imagem   ");
		sbSQL.append(" FROM muralha.veiculo_tempo_real_imagem ");
		sbSQL.append(" WHERE id_veiculo_tempo_real = ? AND indice_imagem = ? ");		
		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idVeic.toString());
			ps.setInt(2, tpImagem);

			rs = ps.executeQuery();
			
			if (rs.next()) 
			{
				retorno.setId(UUID.fromString(rs.getString("id")));
				retorno.setIdVeiculoTempoReal(UUID.fromString(rs.getString("id_veiculo_tempo_real")));
				retorno.setImagem(rs.getBytes("imagem"));
				retorno.setTpImagem(rs.getInt("indice_imagem"));
			}
		}
		catch(Exception e) {
			logger.error("Erro ao obter imagem do veículo por Idveic e TpImagem: " + e.getMessage(), e);
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
		
		}		
		
		return retorno;
	}
	
	
	/*
	 * Retornar lista de imagens por ID da Ocorrência
	 */
	public static List<VeiculoImagem> ObterListaImagensPorIdVeiculo(UUID idVeiculo, boolean trazer_imagem)
	{
		return ObterListaImagensPorIdVeiculo(idVeiculo, trazer_imagem, null);
	}
	public static List<VeiculoImagem> ObterListaImagensPorIdVeiculo(UUID idVeiculo, boolean trazer_imagem, Integer tpImagem) 
	{
		List<VeiculoImagem> listaImagens = new ArrayList<VeiculoImagem>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT vtri.id, ");
		sbSQL.append(" 		  vtri.id_veiculo_tempo_real, ");
		sbSQL.append(" 		  vtri.indice_imagem, ");
		sbSQL.append(" 		  ROW_NUMBER() OVER(ORDER BY vtr.data, vtri.indice_imagem) AS num_imagem ");
		sbSQL.append(" FROM   muralha.veiculo_tempo_real_imagem vtri ");
		sbSQL.append(" 		  INNER JOIN muralha.veiculo_tempo_real vtr ");
		sbSQL.append(" 			   ON  vtr.id = vtri.id_veiculo_tempo_real ");
		sbSQL.append(" WHERE  vtr.id = ? ");
		if (tpImagem != null)
		{
			sbSQL.append(" 	AND vtri.indice_imagem = ? ");
		}
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  vtri.indice_imagem ");
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idVeiculo.toString());
			if (tpImagem != null)
				ps.setInt(2, tpImagem);

			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				VeiculoImagem item = new VeiculoImagem();
				
				item.setId(UUID.fromString(rs.getString("id")));
				item.setIdVeiculoTempoReal(UUID.fromString(rs.getString("id_veiculo_tempo_real")));
				item.setTpImagem(rs.getInt("indice_imagem"));
				item.setNumImagem(rs.getInt("num_imagem"));
				
				if (trazer_imagem) 
				{
					VeiculoImagem imagem = VeiculoImagens.obterImagemPorId(item.getId());					
					item.setImagem(imagem.getImagem());
					item.setImgBase64(Base64Utils.EncodeBase64(imagem.getImagem()));
				}
				
				listaImagens.add(item);
			}
		}
		catch(Exception e) {
			logger.error("Erro ao obter lista de imagens do veículo: " + e.getMessage(), e);
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
		
		}		
		
		return listaImagens;
	}

	public static VeiculoImagem ObterImagensObjAlertaVinculado(UUID idAlerta) 
	{
		
		VeiculoImagem retorno = new VeiculoImagem();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id_alerta, ");
		sbSQL.append(" 		  obj1, ");
		sbSQL.append(" 		  obj2 ");
		sbSQL.append(" FROM   muralha.fcn_ObterImagensObjAlertaVinculado(?) ");
		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idAlerta.toString());

			rs = ps.executeQuery();
			
			if (rs.next()) 
			{
				retorno.setIdImgObj1(UUID.fromString(rs.getString("obj1")));
				retorno.setIdImgObj2(rs.getString("obj2") != null ? UUID.fromString(rs.getString("obj2")) : null);
			}
		}
		catch(Exception e) {
			logger.error("Erro ao obter imagens objetivas para alertas vinculados: " + e.getMessage(), e);
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
		
		}		
		return retorno;
	}
	
	/*
	 * Retornar lista de imagens por ID Alerta
	 */
	public static UUID obterImagemByIdAlerta(UUID idAlerta) 
	{
				
		Connection conn 		= null;
		PreparedStatement ps	= null;
		ResultSet rs 			= null;
		
		UUID ret = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT vtri.id, ");
		sbSQL.append(" 		  vtri.id_veiculo_tempo_real, ");
		sbSQL.append(" 		  vtri.imagem, ");
		sbSQL.append(" 		  vtri.indice_imagem, ");
		sbSQL.append(" 		  ROW_NUMBER() OVER(ORDER BY vtr.data, vtri.indice_imagem) AS num_imagem ");
		sbSQL.append(" FROM   muralha.veiculo_tempo_real_imagem vtri ");
		sbSQL.append(" 		  INNER JOIN muralha.veiculo_tempo_real vtr ");
		sbSQL.append(" 			   ON  vtr.id = vtri.id_veiculo_tempo_real ");
		sbSQL.append(" 		  INNER JOIN muralha.alerta_veiculo av ");
		sbSQL.append(" 			   ON  av.id_veiculo_tempo_real = vtr.id ");
		sbSQL.append(" WHERE  av.id_alerta = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  vtr.data, ");
		sbSQL.append(" 		  vtri.indice_imagem ");
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idAlerta.toString());

			rs = ps.executeQuery();
			
			if (rs.next()) 
			{
				VeiculoImagem item = new VeiculoImagem();
				
				item.setId(UUID.fromString(rs.getString("id")));
				item.setIdVeiculoTempoReal(UUID.fromString(rs.getString("id_veiculo_tempo_real")));
				item.setImagem(rs.getBytes("imagem"));
				item.setTpImagem(rs.getInt("indice_imagem"));
				item.setNumImagem(rs.getInt("num_imagem"));
				
				ret = item.getId();
			}
		}
		catch(Exception e) {
			logger.error("Erro ao obter imagem do Alerta: " + e.getMessage(), e);
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
		
		}		
		
		return ret;
	}
}
