package com.consilux.model.ferramenta;

/**********************************************************************************
Projeto: GTW
Nome do Modulo: GTW

Empresa: Consilux Tecnologia

Autor: Luiz Amaral
Data: 27/09/2016
*********************************************************************************/

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
* Classe de negócio para popula objeto de Trecho
* @author Luiz Fernando Amaral - Consilux Tecnologia
* Data: 27/09/2016
*/
public  class Percurso implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	
	Integer idPercurso;
	String nomePercurso;
	Integer idTipoPercurso;
	String descTipoPercurso;
	Integer idLocalOrigem;
	String nomeLocalOrigem;
	Integer idLocalDestino;
	String nomeLocalDestino;
	double distancia;
	Date dataCadastro;
	Integer idOperador;
	Integer faixa;
	
	public Percurso() {
		super();
	}
	
	public Percurso(Integer idPercurso, String nomePercurso){

		super();
		this.idPercurso = idPercurso;
		this.nomePercurso = nomePercurso;
	}
	
	public Percurso(Integer idPercurso, Integer faixa, String nomePercurso){

		super();
		this.idPercurso = idPercurso;
		this.faixa = faixa;
		this.nomePercurso = nomePercurso;
	}
	
	
	public Integer getIdPercurso() {
		return idPercurso;
	}
	public void setIdPercurso(Integer idPercurso) {
		this.idPercurso = idPercurso;
	}


	public String getNomePercurso() {
		return nomePercurso;
	}
	public void setNomePercurso(String nomePercurso) {
		this.nomePercurso = nomePercurso;
	}


	public Integer getIdTipoPercurso() {
		return idTipoPercurso;
	}
	public void setIdTipoPercurso(Integer idTipoPercurso) {
		this.idTipoPercurso = idTipoPercurso;
	}


	public String getDescTipoPercurso() {
		return descTipoPercurso;
	}
	public void setDescTipoPercurso(String descTipoPercurso) {
		this.descTipoPercurso = descTipoPercurso;
	}


	public Integer getIdLocalOrigem() {
		return idLocalOrigem;
	}
	public void setIdLocalOrigem(Integer idLocalOrigem) {
		this.idLocalOrigem = idLocalOrigem;
	}


	public String getNomeLocalOrigem() {
		return nomeLocalOrigem;
	}
	public void setNomeLocalOrigem(String nomeLocalOrigem) {
		this.nomeLocalOrigem = nomeLocalOrigem;
	}


	public Integer getIdLocalDestino() {
		return idLocalDestino;
	}
	public void setIdLocalDestino(Integer idLocalDestino) {
		this.idLocalDestino = idLocalDestino;
	}


	public String getNomeLocalDestino() {
		return nomeLocalDestino;
	}
	public void setNomeLocalDestino(String nomeLocalDestino) {
		this.nomeLocalDestino = nomeLocalDestino;
	}


	public double getDistancia() {
		return distancia;
	}
	public void setDistancia(double distancia) {
		this.distancia = distancia;
	}


	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}


	public Integer getIdOperador() {
		return idOperador;
	}
	public void setIdOperador(Integer idOperador) {
		this.idOperador = idOperador;
	}


	public Integer getFaixa() {
		return faixa;
	}
	public void setFaixa(Integer faixa) {
		this.faixa = faixa;
	}

	
	/**
	 * Método para obter percurso a partir do local de origem.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 11/11/2016
	 */
	public ArrayList<Percurso> consultaPercursoAPartirUnicoLocal(Long idLocalOrigem) throws SQLException, ConexaoException, ParseException{

		StringBuilder sbSQL = new StringBuilder();
	
		sbSQL.append(" SELECT p.id_percurso, "); 
		sbSQL.append(" 		  p.nome_percurso, "); 
		sbSQL.append(" 		  tp.id_tipo_percurso, "); 
		sbSQL.append(" 		  tp.descricao AS desc_tipo_percurso, "); 
		sbSQL.append(" 		  origem.id_local AS id_local_origem, "); 
		sbSQL.append(" 		  origem.nome AS nome_origem, "); 
		sbSQL.append(" 		  destino.id_local AS id_local_destino, ");
		sbSQL.append(" 		  destino.nome AS nome_destino, ");
		sbSQL.append(" 		  p.distancia, ");
		sbSQL.append(" 		  p.data_gravacao, ");
		sbSQL.append(" 		  p.id_usuario ");
		sbSQL.append(" FROM   percurso p (NOLOCK) "); 
		sbSQL.append(" 		  INNER JOIN tipo_percurso tp (NOLOCK) "); 
		sbSQL.append(" 			   ON  tp.id_tipo_percurso = p.id_tipo_percurso "); 
		sbSQL.append(" 		  INNER JOIN local_vigente origem (NOLOCK) "); 
		sbSQL.append(" 			   ON  origem.id_local = p.id_local_origem ");
		sbSQL.append(" 		  INNER JOIN local_vigente destino (NOLOCK) "); 
		sbSQL.append(" 			   ON  destino.id_local = p.id_local_destino ");
		sbSQL.append(" WHERE  p.id_local_origem = ? "); 
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Percurso percurso = null;
		ArrayList<Percurso> listaPercurso = new ArrayList<Percurso>();
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, idLocalOrigem);
	
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				percurso = new Percurso();
				percurso.setIdPercurso(rs.getInt("id_percurso"));
				percurso.setNomePercurso(rs.getString("nome_percurso"));
				percurso.setIdTipoPercurso(rs.getInt("id_tipo_percurso"));
				percurso.setDescTipoPercurso(rs.getString("desc_tipo_percurso"));
				percurso.setIdLocalOrigem(rs.getInt("id_local_origem"));
				percurso.setNomeLocalOrigem(rs.getString("nome_origem"));
				percurso.setIdLocalDestino(rs.getInt("id_local_destino"));
				percurso.setNomeLocalDestino(rs.getString("nome_destino"));
				percurso.setDistancia(rs.getDouble("distancia"));
				
				listaPercurso.add(percurso);
			}
			
			return listaPercurso;
		}
		catch (Exception e) {
			if (conn != null)
				conn.close();
    		e.printStackTrace();
    		return null;
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}

	
	/**
	 * Método para obter dados dos percursos a partir do local de origem e do local de destino.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 11/11/2016
	 */
	public ArrayList<Percurso> consultaPercurso(Long idLocalOrigem, Long idLocalDestino) throws SQLException, ConexaoException, ParseException{
		return consultaPercurso(idLocalOrigem, idLocalDestino, 0);
	}
	
	/**
	 * Método para obter dados dos percursos a partir do local de origem e do local de destino.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 11/11/2016
	 */
	public ArrayList<Percurso> consultaPercurso(Long idLocalOrigem, Long idLocalDestino, Integer idTipoPercurso) throws SQLException, ConexaoException, ParseException{

		StringBuilder sbSQL = new StringBuilder();
	
		sbSQL.append(" SELECT p.id_percurso, "); 
		sbSQL.append(" 		  p.nome_percurso, "); 
		sbSQL.append(" 		  tp.id_tipo_percurso, "); 
		sbSQL.append(" 		  tp.descricao AS desc_tipo_percurso, "); 
		sbSQL.append(" 		  origem.id_local AS id_local_origem, "); 
		sbSQL.append(" 		  origem.nome AS nome_origem, "); 
		sbSQL.append(" 		  destino.id_local AS id_local_destino, ");
		sbSQL.append(" 		  destino.nome AS nome_destino, ");
		sbSQL.append(" 		  p.distancia, ");
		sbSQL.append(" 		  p.data_gravacao, ");
		sbSQL.append(" 		  p.id_usuario ");
		sbSQL.append(" FROM   percurso p (NOLOCK) "); 
		sbSQL.append(" 		  INNER JOIN tipo_percurso tp (NOLOCK) "); 
		sbSQL.append(" 			   ON  tp.id_tipo_percurso = p.id_tipo_percurso "); 
		sbSQL.append(" 		  INNER JOIN local_vigente origem (NOLOCK) "); 
		sbSQL.append(" 			   ON  origem.id_local = p.id_local_origem ");
		sbSQL.append(" 		  INNER JOIN local_vigente destino (NOLOCK) "); 
		sbSQL.append(" 			   ON  destino.id_local = p.id_local_destino ");
		sbSQL.append(" WHERE  p.id_local_origem = ? "); 
		sbSQL.append(" 		  AND p.id_local_destino = ? ");
		
		if (idTipoPercurso > 0) {
			sbSQL.append(" 		  AND tp.id_tipo_percurso = ? ");	
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Percurso percurso = null;
		ArrayList<Percurso> listaPercurso = new ArrayList<Percurso>();
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, idLocalOrigem);
			ps.setLong(2, idLocalDestino);
			
			if (idTipoPercurso > 0) {
				ps.setInt(3, idTipoPercurso);
			}
	
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				percurso = new Percurso();
				percurso.setIdPercurso(rs.getInt("id_percurso"));
				percurso.setNomePercurso(rs.getString("nome_percurso"));
				percurso.setIdTipoPercurso(rs.getInt("id_tipo_percurso"));
				percurso.setDescTipoPercurso(rs.getString("desc_tipo_percurso"));
				percurso.setIdLocalOrigem(rs.getInt("id_local_origem"));
				percurso.setNomeLocalOrigem(rs.getString("nome_origem"));
				percurso.setIdLocalDestino(rs.getInt("id_local_destino"));
				percurso.setNomeLocalDestino(rs.getString("nome_destino"));
				percurso.setDistancia(rs.getDouble("distancia"));
				
				listaPercurso.add(percurso);
			}
			
			return listaPercurso;
		}
		catch (Exception e) {
			if (conn != null)
				conn.close();
    		e.printStackTrace();
    		return null;
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Método para obter percursos por tipo de percurso.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 22/11/2016
	 */
	public ArrayList<Percurso> obterPercursosPorTipo(Integer idTipoPercurso) throws SQLException, ConexaoException, ParseException{

		StringBuilder sbSQL = new StringBuilder();
	
		sbSQL.append(" SELECT p.id_percurso, "); 
		sbSQL.append(" 		  p.nome_percurso, "); 
		sbSQL.append(" 		  tp.id_tipo_percurso, "); 
		sbSQL.append(" 		  tp.descricao AS desc_tipo_percurso, "); 
		sbSQL.append(" 		  p.distancia, ");
		sbSQL.append(" 		  p.data_gravacao, ");
		sbSQL.append(" 		  p.id_usuario ");
		sbSQL.append(" FROM   percurso p (NOLOCK) "); 
		sbSQL.append(" 		  INNER JOIN tipo_percurso tp (NOLOCK) "); 
		sbSQL.append(" 			   ON  tp.id_tipo_percurso = p.id_tipo_percurso "); 
		sbSQL.append(" WHERE  tp.id_tipo_percurso = ? ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Percurso percurso = null;
		ArrayList<Percurso> listaPercurso = new ArrayList<Percurso>();
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, idTipoPercurso);
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				percurso = new Percurso();
				percurso.setIdPercurso(rs.getInt("id_percurso"));
				percurso.setNomePercurso(rs.getString("nome_percurso"));
				percurso.setIdTipoPercurso(rs.getInt("id_tipo_percurso"));
				percurso.setDescTipoPercurso(rs.getString("desc_tipo_percurso"));
				percurso.setDistancia(rs.getDouble("distancia"));
				
				listaPercurso.add(percurso);
			}
			
			return listaPercurso;
		}
		catch (Exception e) {
			if (conn != null)
				conn.close();
    		e.printStackTrace();
    		return null;
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	
	/**
	 * Método para obter corredores.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 28/08/2018
	 */
	public ArrayList<Percurso> obterCorredores() throws SQLException, ConexaoException, ParseException{

		StringBuilder sbSQL = new StringBuilder();
	
		sbSQL.append(" SELECT id_corredor, ");
		sbSQL.append("  	  descricao, ");
		sbSQL.append("  	  nome_corredor ");
		sbSQL.append(" FROM   dbo.fcn_obterCorredores() ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("  	  id_corredor ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Percurso percurso = null;
		ArrayList<Percurso> listaPercurso = new ArrayList<Percurso>();
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				percurso = new Percurso(rs.getInt("id_corredor"), rs.getString("nome_corredor"));
				listaPercurso.add(percurso);
				
			}
			
			return listaPercurso;
		}
		catch (Exception e) {
			if (conn != null)
				conn.close();
    		e.printStackTrace();
    		return null;
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Método para obter corredores por faixa.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 28/08/2018
	 */
	public ArrayList<Percurso> obterCorredoresPorFaixa() throws SQLException, ConexaoException, ParseException{

		StringBuilder sbSQL = new StringBuilder();
	
		sbSQL.append(" SELECT id_corredor, ");
		sbSQL.append("  	  descricao, ");
		sbSQL.append("  	  faixa, ");
		sbSQL.append("  	  nome_corredor ");
		sbSQL.append(" FROM   dbo.fcn_obterCorredoresPorFaixa() ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("  	  id_corredor, ");
		sbSQL.append("  	  faixa ");

		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Percurso percurso = null;
		ArrayList<Percurso> listaPercurso = new ArrayList<Percurso>();
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				percurso = new Percurso(rs.getInt("id_corredor"), rs.getInt("faixa"), rs.getString("nome_corredor"));
				listaPercurso.add(percurso);
				
			}
			
			return listaPercurso;
		}
		catch (Exception e) {
			if (conn != null)
				conn.close();
    		e.printStackTrace();
    		return null;
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Método para obter trechos.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 28/08/2018
	 */
	public ArrayList<Percurso> obterTrechos() throws SQLException, ConexaoException, ParseException{

		StringBuilder sbSQL = new StringBuilder();
	
		sbSQL.append(" SELECT id_corredor, ");
		sbSQL.append(" 		  id_trecho, ");
		sbSQL.append(" 		  nome_trecho ");
		sbSQL.append(" FROM   dbo.fcn_obterTrechos() ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  id_corredor, ");
		sbSQL.append(" 		  id_trecho ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Percurso percurso = null;
		ArrayList<Percurso> listaPercurso = new ArrayList<Percurso>();
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				percurso = new Percurso(rs.getInt("id_trecho"), rs.getString("nome_trecho"));
				listaPercurso.add(percurso);
				
			}
			
			return listaPercurso;
		}
		catch (Exception e) {
			if (conn != null)
				conn.close();
    		e.printStackTrace();
    		return null;
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Método para obter trechos por faixa.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 28/08/2018
	 */
	public ArrayList<Percurso> obterTrechosPorFaixa() throws SQLException, ConexaoException, ParseException{

		StringBuilder sbSQL = new StringBuilder();
	
		sbSQL.append(" SELECT id_corredor, ");
		sbSQL.append(" 		  id_trecho, ");
		sbSQL.append(" 		  faixa, ");
		sbSQL.append(" 		  nome_trecho ");
		sbSQL.append(" FROM   dbo.fcn_obterTrechosPorFaixa() ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  id_corredor, ");
		sbSQL.append(" 		  faixa, ");
		sbSQL.append(" 		  id_trecho ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Percurso percurso = null;
		ArrayList<Percurso> listaPercurso = new ArrayList<Percurso>();
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				percurso = new Percurso(rs.getInt("id_trecho"), rs.getInt("faixa"), rs.getString("nome_trecho"));
				listaPercurso.add(percurso);
				
			}
			
			return listaPercurso;
		}
		catch (Exception e) {
			if (conn != null)
				conn.close();
    		e.printStackTrace();
    		return null;
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Método para gravar um percurso no banco de dados.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 11/11/2016
	 */
	public void gravarPercurso(String nomePercurso, Integer idTipoPercurso, Long idLocalOrigem, Long idLocalDestino, Double distancia, Long idUsuario) 
			throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		PreparedStatement ps = null;
		Connection conn = null;
		
		try 
		{
			
			sbSQL.append(" INSERT INTO percurso ( nome_percurso, id_tipo_percurso, id_local_origem, id_local_destino, distancia, data_gravacao, id_usuario ) ");		
			sbSQL.append(" VALUES (?, ?, ?, ?, ?, GETDATE(), ?) ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, nomePercurso);
			ps.setInt(2, idTipoPercurso);
			ps.setLong(3, idLocalOrigem);
			ps.setLong(4, idLocalDestino);
			ps.setDouble(5, distancia);
			ps.setLong(6, idUsuario);
			
			ps.executeUpdate();


		} catch (SQLException e) {
			e.printStackTrace();
		
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}
	}
}
