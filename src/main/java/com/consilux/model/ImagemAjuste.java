/**********************************************************************************
  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Ederson Luiz Silva
  Data: 24/01/2012

  Descricao: {descr}

  Historico:

    Revision 1.1  2008/08/12 13:00:53  fos
    Primeira versão postada no CVS.

*********************************************************************************/
package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 *
 * @author Ederson Luiz Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2012/01/24 13:07:37 $ $Author: ederson.silva $
 */
public class ImagemAjuste {
	private int IdImagem = 0;
	private int Brilho = 0;
	private float Contraste = 0;

	public ImagemAjuste(){
		
	}
		
	public ImagemAjuste(Integer IdImagem, int brilho, float contraste) {
		super();
		this.setIdImagem(IdImagem);
		this.setBrilho(brilho);
		this.setContraste(contraste);
	}

	public void setBrilho(int brilho) {
		Brilho = brilho;
	}

	public int getBrilho() {
		return Brilho;
	}

	public void setContraste(float contraste) {
		Contraste = contraste;
	}

	public float getContraste() {
		return Contraste;
	}

	public void setIdImagem(int idImagem) {
		IdImagem = idImagem;
	}

	public int getIdImagem() {
		return IdImagem;
	}
	
	/**
	 * Busca o ajuste da imagem relacionada a uma imagem
	 * @param idImagem Identificador da imagem.
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static ImagemAjuste GetImagemAjusteById(int IdImagem) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT 	");
		sbSQL.append("			id_imagem, ");
		sbSQL.append("			brilho, ");
		sbSQL.append("			contraste ");
		sbSQL.append("FROM		imagem_ajuste ");
		sbSQL.append("WHERE 	id_imagem = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, IdImagem);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				return new ImagemAjuste(
						IdImagem,
						rs.getInt("brilho"),
						rs.getFloat("contraste")
					);
			}
			else {
				return null;
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}			
	}
	
	public static void SetImagemAjuste(int idImagem, int brilho, float contraste) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("{call spu_imagem_ajuste (?, ?, ?)}");

		Connection conn = null;
		CallableStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareCall(sbSQL.toString());
			ps.setInt(1, idImagem);
			ps.setInt(2, brilho);
			ps.setFloat(3, contraste);

			ps.execute();
		}
		finally
		{
			if (conn != null)
				conn.close();	
		}
	}
}