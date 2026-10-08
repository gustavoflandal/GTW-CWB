package com.consilux.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.consilux.lib.Conexao;

/**
 * Classe de negócio para busca de dados dos lotes reprovados (Detalhes)
 * @author Felipe Rafailov
 * @since 29/01/16
 */

public class LoteReprovadoDetalhe implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6256342199376800505L;

	private int IdInfracao;
	private String PlacaCAI, PlacaCAV, InconsistenciaCAI, InconsistenciaCAV, MarcaCAI, MarcaCAV, ErroObliteracao, NomeAgente;
	
	public LoteReprovadoDetalhe(int IdInfracao, String PlacaCAI,String PlacaCAV,
			String InconsistenciaCAI,String InconsistenciaCAV,String MarcaCAI,String MarcaCAV,String ErroObliteracao,String NomeAgente)
	{
		this.IdInfracao = IdInfracao;
		this.PlacaCAI = PlacaCAI;
		this.PlacaCAV = PlacaCAV;
		this.InconsistenciaCAI = InconsistenciaCAI;
		this.InconsistenciaCAV = InconsistenciaCAV;
		this.MarcaCAI = MarcaCAI;
		this.MarcaCAV = MarcaCAV;
		this.ErroObliteracao = ErroObliteracao;
		this.NomeAgente = NomeAgente;
	}
	
	public static List<LoteReprovadoDetalhe> ObterDetalhe(int IdRemessa) 
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<LoteReprovadoDetalhe> list = new ArrayList<LoteReprovadoDetalhe>();
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT * FROM fcn_ObterLoteReprovadoDetalhe(?)");
			ps.setInt(1, IdRemessa);
			rs = ps.executeQuery();
			while(rs.next())
			{
				list.add(new LoteReprovadoDetalhe(
						rs.getInt(1), rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)));
			}
		}
		catch(Exception e)
		{
		}
		finally 
		{
			try 
			{
			if (conn != null)
				conn.close();
			if(ps != null)
				ps.close();
			if (rs != null)
				rs.close();
			}
			catch(Exception e) {}
		}
		
		return list;
	}
	
	public int getIdInfracao() {
		return IdInfracao;
	}
	public void setIdInfracao(int idInfracao) {
		IdInfracao = idInfracao;
	}
	public String getPlacaCAI() {
		return PlacaCAI;
	}
	public void setPlacaCAI(String placaCAI) {
		PlacaCAI = placaCAI;
	}
	public String getPlacaCAV() {
		return PlacaCAV;
	}
	public void setPlacaCAV(String placaCAV) {
		PlacaCAV = placaCAV;
	}
	public String getInconsistenciaCAI() {
		return InconsistenciaCAI;
	}
	public void setInconsistenciaCAI(String inconsistenciaCAI) {
		InconsistenciaCAI = inconsistenciaCAI;
	}
	public String getInconsistenciaCAV() {
		return InconsistenciaCAV;
	}
	public void setInconsistenciaCAV(String inconsistenciaCAV) {
		InconsistenciaCAV = inconsistenciaCAV;
	}
	public String getMarcaCAI() {
		return MarcaCAI;
	}
	public void setMarcaCAI(String marcaCAI) {
		MarcaCAI = marcaCAI;
	}
	public String getMarcaCAV() {
		return MarcaCAV;
	}
	public void setMarcaCAV(String marcaCAV) {
		MarcaCAV = marcaCAV;
	}
	public String getErroObliteracao() {
		return ErroObliteracao;
	}
	public void setErroObliteracao(String erroObliteracao) {
		ErroObliteracao = erroObliteracao;
	}
	public String getNomeAgente() {
		return NomeAgente;
	}
	public void setNomeAgente(String nomeAgente) {
		NomeAgente = nomeAgente;
	}
	
}
