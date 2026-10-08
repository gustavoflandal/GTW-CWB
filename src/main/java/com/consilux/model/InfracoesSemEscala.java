package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

public class InfracoesSemEscala {

	private Date data;
	private int idLocal;
	private String descricaoLocal;
	private int numeroImagens;
	
	public InfracoesSemEscala(Date data,	int idLocal,	 String descricaoLocal, 	int numeroImagens)
	{
		this.data = data;
		this.idLocal = idLocal;
		this.descricaoLocal = descricaoLocal;
		this.numeroImagens = numeroImagens;
	}

	public Date getData() {
		return data;
	}



	public void setData(Date data) {
		this.data = data;
	}



	public int getIdLocal() {
		return idLocal;
	}



	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}



	public String getDescricaoLocal() {
		return descricaoLocal;
	}



	public void setDescricaoLocal(String descricaoLocal) {
		this.descricaoLocal = descricaoLocal;
	}



	public int getNumeroImagens() {
		return numeroImagens;
	}



	public void setNumeroImagens(int numeroImagens) {
		this.numeroImagens = numeroImagens;
	}
	
	
	public static List<InfracoesSemEscala> ObterInfracoesSemEscala() {
		List<InfracoesSemEscala> ise = new ArrayList<InfracoesSemEscala>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT * FROM fcn_InfracoesSemEscala() ORDER BY data");
			rs = ps.executeQuery();
			while(rs.next())
				ise.add(new InfracoesSemEscala(rs.getDate("data"), rs.getInt("id_local"), rs.getString("descricao_local"), rs.getInt("numero_imagens")));
		} 
		catch(Exception e)
		{
		}
		finally
		{
			try {
			if(conn != null)
				conn.close();
			if(ps != null)
				ps.close();
			if(rs != null)
				rs.close();
			} catch(Exception e) {}
		}
		
		
		return ise;
	}
	
	public static int ReposicionarInfracoesSemEscala(Date data, Integer id_local) {
		int rows = -1;
		
		Logger logger = Logger
				.getLogger(InfracoesSemEscala.class);
		
		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall("{ ? = call spu_reposiciona_infracoes_sem_escala(?, ?) }");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setDate(2, new java.sql.Date(data.getTime()));
			cs.setInt(3, id_local);
			
			cs.execute();
			rows = cs.getInt(1);
		} 
		catch(Exception e)
		{
			logger.error("Erro ao reposicionar infrações", e);
		}
		finally
		{
			try {
			if(conn != null)
				conn.close();
			if(cs != null)
				cs.close();
			} catch(Exception e) {}
		}
		
		return rows;
	}
	
}
