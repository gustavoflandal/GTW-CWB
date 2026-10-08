/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 04/08/2008

  Descricao: {descr}

  Historico:

    $Log: InfracaoObliteracao.java,v $
    Revision 1.4  2009/03/12 13:07:37  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.3  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.1  2008/08/12 13:00:53  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import com.consilux.infra.CriptografiaAES;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 *
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/03/12 13:07:37 $ $Author: raoni $
 */
public class InfracaoObliteracao {
	private Integer idInfracao;
	private Integer idImagem;
	private Integer sequenciaObliteracao;
	
	private List<Rectangle> listObliteracoes;
	
	public List<Rectangle> getListObliteracoes() {
		return listObliteracoes;
	}

	/**
	 * @param idInfracao
	 * @param x
	 * @param y
	 * @param largura
	 * @param altura
	 */
	public InfracaoObliteracao(Integer idInfracao, Integer idImagem) {
		super();
		this.idImagem = idImagem;
		this.idInfracao = idInfracao;

		this.listObliteracoes = new ArrayList<Rectangle>();
	}
	
	public void AdicionarObliteracao(int x, int y, int largura, int altura)
	{
		if(altura > 0 && largura > 0)
		listObliteracoes.add(new Rectangle(x, y, largura, altura));
	}
	
	public boolean PossuiObliteracoes() {
		return listObliteracoes.size() > 0;
	}

	/**
	 * Busca a obliteração relacionada a uma infração.
	 * @param idInfracao Identificador da infração.
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static InfracaoObliteracao buscaInfracaoObliteracaoPorIdInfracao(Integer idInfracao, Integer idImagem) throws ConexaoException, SQLException {
		
		InfracaoObliteracao obliteracao = new InfracaoObliteracao(idInfracao, idImagem);
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("		x,");
		sbSQL.append("		y,");
		sbSQL.append("		largura,");
		sbSQL.append("		altura");
		sbSQL.append("	FROM");
		sbSQL.append("		infracao_obliteracao WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_infracao = ? AND id_imagem = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idInfracao);
			ps.setInt(2, idImagem);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				obliteracao.AdicionarObliteracao(rs.getInt("x"), rs.getInt("y"), rs.getInt("largura"), rs.getInt("altura"));
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}	
		
		if (obliteracao.PossuiObliteracoes())
			return obliteracao;
		else
			return null;
	}
	public static InfracaoObliteracao buscaInfracaoObliteracaoPorIdImagem(Integer idImagem) throws ConexaoException, SQLException {
		
		InfracaoObliteracao obliteracao = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("		x,");
		sbSQL.append("		y,");
		sbSQL.append("		largura,");
		sbSQL.append("		altura,");
		sbSQL.append("		id_infracao");
		sbSQL.append("	FROM");
		sbSQL.append("		infracao_obliteracao WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_imagem = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idImagem);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				if (obliteracao == null)
					obliteracao = new InfracaoObliteracao(rs.getInt("id_infracao"), idImagem);
					
				obliteracao.AdicionarObliteracao(rs.getInt("x"), rs.getInt("y"), rs.getInt("largura"), rs.getInt("altura"));
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}	
		
		if (obliteracao.PossuiObliteracoes())
			return obliteracao;
		else
			return null;
	}

	/**
	 * Busca a obliteração relacionada a uma infração.
	 * @param idInfracaoProcesso Identificador da infração no processo.
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static InfracaoObliteracao buscaInfracaoObliteracaoPorIdInfracaoProcesso(Integer idInfracaoProcesso, Integer idImagem) throws ConexaoException, SQLException {
		
		InfracaoObliteracao obliteracao = new InfracaoObliteracao(0, idImagem);
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT TOP 1");
		sbSQL.append("		(SELECT TOP 1 id_infracao FROM infracao_processo ip WHERE ip.id_infracao_processo = id_infracao_processo) AS id_infracao,");
		sbSQL.append("		id_imagem,");
		sbSQL.append("		sequencia_obliteracao,");
		sbSQL.append("		x,");
		sbSQL.append("		y,");
		sbSQL.append("		largura,");
		sbSQL.append("		altura");
		sbSQL.append("	FROM");
		sbSQL.append("		infracao_processo_obliteracao WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_infracao_processo = ? AND id_imagem = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idInfracaoProcesso);

			if (idImagem != null)
				ps.setInt(2, idImagem);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				if(obliteracao.idInfracao == 0)
					obliteracao.idInfracao = rs.getInt("id_infracao");
				obliteracao.AdicionarObliteracao(rs.getInt("x"), rs.getInt("y"), rs.getInt("largura"), rs.getInt("altura"));
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}			
		
		if (obliteracao.PossuiObliteracoes())
			return obliteracao;
		else
			return null;
	}	
	/**
	 * @return Retorna o valor de idInfracao atual.
	 */
	public Integer getIdInfracao() {
		return idInfracao;
	}
	/**
	 * @return the idImagem
	 */
	public Integer getIdImagem() {
		return idImagem;
	}
	
	/**
	 * @return the sequenciaObliteracao
	 */
	public Integer getSequenciaObliteracao() {
		return sequenciaObliteracao;
	}
		
	public byte[] getImagemObliterada() throws ConexaoException, SQLException, ModelException, IOException {
		byte[] bImg = null;
		if (this.idImagem != null)
			bImg = VeiculoImagem.buscaVeiculoImagemPorIdImagem(this.idImagem).getImagem();
		else
			bImg = Infracao.buscaInfracaoPorId(this.idInfracao).buscaImagemObjetiva();

		for(Rectangle r : listObliteracoes) {
			bImg = obliteraImagem(bImg, r.x, r.y, r.height, r.width);
		}
		
		return bImg;
	}
	
//	public static byte[] obliteraImagem(byte[] bImg, int x, int y, int altura, int largura)
//	throws IOException	{
//		
//		byte[] bRet = bImg;
//		
//		BufferedImage bufImage = ImageIO.read(new ByteArrayInputStream(bImg));
//		Graphics2D g = bufImage.createGraphics();
//
//		g.setColor(Color.BLACK);
//		g.fillRect(x, y, largura, altura);
//		
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ImageIO.write(bufImage, "JPG", baos);
//		bRet = baos.toByteArray();
//		
//		return bRet;
//	}
	
	public static byte[] desobliteraImagem(byte[] bImg, byte[] bOblit, int x, int y) throws IOException {
		
		byte[] bRet;
		
		BufferedImage bufImage = ImageIO.read(new ByteArrayInputStream(bImg));
		BufferedImage bufOblit = ImageIO.read(new ByteArrayInputStream(bOblit));
		Graphics2D g = bufImage.createGraphics();
		
		g.drawImage(bufOblit, null, x, y);
		
		bRet = ObterBytesImagem(bufImage);
		
		return bRet;
	}
	
	public static byte[] desobliteraImagem(byte[] bImg, byte[] bArquivoObl, List<Rectangle> obliteracoesAtuais) throws IOException {
		
		CriptografiaAES criptografia = new CriptografiaAES();
		
		String sOblitAt;
		
		byte[] bImgCopy = new byte[bImg.length];
		System.arraycopy(bImg, 0, bImgCopy, 0, bImg.length);
		
		try {
			String sArquivoObl = new String(criptografia.descriptografaAES(bArquivoObl));
			BufferedReader br = new BufferedReader(new StringReader(sArquivoObl));
			while((sOblitAt = br.readLine()) != null) {
				String[] partes = sOblitAt.split(";");
				
				int x = Integer.parseInt(partes[0]);
				int y = Integer.parseInt(partes[1]);
				byte[] bObl = Base64Utils.DecodeBase64(partes[2]);
				if (obliteracoesAtuais != null) {
					BufferedImage imgObl = ImageIO.read(new ByteArrayInputStream(bObl));
					obliteracoesAtuais.add(new Rectangle(x, y, imgObl.getWidth(), imgObl.getHeight()));
				}
				partes = null;
				
				bImgCopy = desobliteraImagem(bImgCopy, bObl, x, y);
			}
			
			return bImgCopy;
		} catch (GeneralSecurityException e) {
			return bImg;
		}
	}
	
	public static byte[] obterParteObliterada(byte[] bImg, int x, int y, int altura, int largura) 
	throws IOException {
		
		byte[] bRet;
		
		BufferedImage bufImage = ImageIO.read(new ByteArrayInputStream(bImg));
		BufferedImage subImage = bufImage.getSubimage(x, y, largura, altura);
		
		bRet = ObterBytesImagem(subImage);
		
		return bRet;
	}
	
	public static byte[] obliteraImagem(byte[] bImg, int x, int y, int altura, int largura)
	throws IOException	{
		
		byte[] bRet = bImg;
		
		BufferedImage bufImage = ImageIO.read(new ByteArrayInputStream(bImg));
		Graphics2D g = bufImage.createGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(x, y, largura, altura);
		
		bRet = ObterBytesImagem(bufImage);
		
		return bRet;
	}
	
	private static byte[] ObterBytesImagem(BufferedImage bufImage) throws IOException {
		byte[] bRet = null;
		
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
		if (iter.hasNext()) {
		
			ImageWriter writer = iter.next();
			ImageWriteParam writeParams = writer.getDefaultWriteParam();
			writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			writeParams.setCompressionQuality(VeiculoImagem.QUALIDADE_JPEG);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
            writer.setOutput(ios);
			writer.write(null, new IIOImage(bufImage, null, null), writeParams);
			writer.dispose();
			
			bRet = baos.toByteArray();
		}
		
		return bRet;
	}
}
