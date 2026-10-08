/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 11/01/2006

  Descricao: Classe de suporte com funções de uso geral.

  Historico:

    $Log: Funcoes.java,v $
    Revision 1.14  2009/04/22 14:53:26  fernando
    - Usando senha criptografada

    Revision 1.13  2009/04/22 13:28:41  fos
    Criada função que criptografa os dados para armazenar a senha do usuário.

    Revision 1.12  2009/04/15 19:04:49  fernando
    - Buscar no banco de dados a data de modificação da configuração de equipamento

    Revision 1.11  2009/03/09 18:46:19  fos
    Colocada função que retira acentos.

    Revision 1.10  2009/01/16 13:55:57  fos
    Colocado funções de conversão de datas Java<->Delphi.

    Revision 1.9  2009/01/12 12:49:50  fos
    Recuperação de repositório.

    Revision 1.7  2008/08/20 13:37:58  fos
    Função que concatenava Array entrava em loop infinito.

    Revision 1.6  2008/07/31 21:08:05  fos
    Dispara uma exceção caso não consiga converter.

    Revision 1.5  2008/07/23 14:21:55  fos
    Ajustado o nome da função.

    Revision 1.4  2007/04/11 11:58:08  fos
    Agora a função ajustaPreparedStatement trabalha com filtros sem parâmetros bind.

    Revision 1.3  2007/03/16 13:28:25  fos
    Acertado links inválidos nos comentários

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.infra;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.consilux.lib.Tuple;
import com.consilux.model.ImagemAjuste;
import com.consilux.model.LocalVigente;
import com.consilux.model.VeiculoImagem;
import com.consilux.model.exception.ModelException;
import com.consilux.model.ImagemMiniatura.PosicaoMiniatura;

/**
 * Classe de suporte com funções de uso geral.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.11.2.4 $ $Date: 2009/06/21 23:05:41 $ $Author: fos $
 */
public class Funcoes {

	private static Logger logger = Logger.getLogger(Funcoes.class); 

	/**
	 * Monta string SQL para WHEREs a partir de listas.
	 * @param mFiltros Mapa com os pares NOME_REGRA,VALOR caso não encontre a regra monta a String NOME_REGRA = ?  
	 * @param mRegras Mapa com os pares NOME_REGRA,STR_REGRA onde o STR_REGRA é a String que adaptada para Query
	 * @return A string pronta com os filtros para ser adiciona a SQL
	 */
	public static String preparaCondicoesFiltro(Map<String,Object> mFiltros, Map<String,String> mRegras) throws ModelException {
		
		ArrayList<String> lWheres = new ArrayList<String>(mFiltros.size());
		
		for (Entry<String,Object> p: mFiltros.entrySet())
		{
			String chave = p.getKey();
			Object valor = p.getValue();
			String regra = mRegras.get(chave);
			
			if (regra != null)
			{
				lWheres.add(regra);
			}
			else
			{
				if (valor instanceof List)
				{
					List<?> lista = (List<?>) valor;
					if (lista.isEmpty())
						throw new ModelException("Ero ao preparar condições de filtro. [" + chave + "] é uma lista vazia.");
					
					if (lista.size() == 1)
					{
						lWheres.add(chave + " = ? ");
					} else {
						lWheres.add(chave + " IN (" + concatStringArray(repeteString("?", lista.size()), ",") + ") ");
					}
				}
				else
				{
					lWheres.add(chave + " = ? ");
				}
			}
		}
		return Funcoes.concatStringArray(lWheres, " AND ");
	}
	
	public static Tuple<String, String> preparaRegraMultipla(String nomeRegra, String nomeColuna, int qtd) {
		return new Tuple<String, String>(nomeRegra,  " " + nomeColuna + " IN (" + concatStringArray(repeteString("?", qtd), ",") + ") ");
	}
	
	/**
	 * Cria uma lista de strings, baseada na repetição de uma string original.
	 * @param srcString a strign que se deseja repetir.
	 * @param nVezes o número de vezes a se repetir a string.
	 * @return
	 */
	public static List<String> repeteString(String srcString, int nVezes) {
		
		List<String> lRet = new ArrayList<String>(nVezes);
		if (srcString != null)
		{
			for (int i = 0; i < nVezes; i++) {
				lRet.add(srcString);
			}
		}
		return lRet;
	}
	
	/**
	 * Monta string SQL para WHEREs a partir de listas, com operador OR.
	 * @param mFiltros Mapa com os pares NOME_REGRA,VALOR caso não encontre a regra monta a String NOME_REGRA = ?  
	 * @param mRegras Mapa com os pares NOME_REGRA,STR_REGRA onde o STR_REGRA é a String que adaptada para Query
	 * @return A string pronta com os filtros para ser adiciona a SQL
	 */
	public static String preparaCondicoesFiltroOr(Map<String,Object> mFiltros, Map<String,String[]> mRegrasOr) {
		ArrayList<String> lWheres = new ArrayList<String>(mFiltros.size());
		
		for (String chave: mRegrasOr.keySet()) {
			String regra[] = mRegrasOr.get(chave);
			for (int j = 0 ; j < regra.length ; j++) {
				if (regra[j] != null)
					lWheres.add(regra[j]);
			
			}
		}
		lWheres.set(0, "( " + lWheres.get(0));
		lWheres.set(lWheres.size() -1, lWheres.get(lWheres.size() -1) + " ) AND ");
		return Funcoes.concatStringArray(lWheres, " OR ");
		
	}
	/**
	 * Ajusta vários atributos de um PreparedStatement através de uma Coleção.
	 * @param ps PreparedStatement a ser ajustado
	 * @param iIni Número de inicio para o ajutes dos atributos
	 * @param setParams Coleção com os valores a serem ajustados
	 * @throws SQLException
	 */
	public static int ajustaPreparedStatement(PreparedStatement ps, int iIni, Collection<Object> setParams) throws SQLException {
		
		int iConta = iIni;
		
		for (Object oVal: setParams) {
			
			if (oVal == null)
				continue;
			
			if (oVal instanceof String)
			{
				ps.setString(iConta++, (String)oVal);
			}
			else if (oVal instanceof Integer)
			{
				ps.setInt(iConta++, ((Integer)oVal).intValue());
			}
			else if (oVal instanceof Long)
			{
				ps.setLong(iConta++, ((Long)oVal).longValue());
			}
			else if (oVal instanceof Timestamp)
			{
				ps.setTimestamp(iConta++, ((Timestamp)oVal));
			}
			else if (oVal instanceof Date)
			{
				Date dt = (Date) oVal;
				ps.setTimestamp(iConta++, new Timestamp(dt.getTime()));
			}			
			else if (oVal instanceof List)
			{
				@SuppressWarnings("unchecked")
				List<Object> paramLista = (List<Object>) oVal;
				int subAjustes = ajustaPreparedStatement(ps, iConta, paramLista);
				iConta = subAjustes;
			}
			else
			{
				throw new SQLException("Tipo [" + oVal.getClass() +"] do parâmetro [" + oVal + "] inválido na preparação da query.");
			}
		}
		return iConta;
	}
	/**
	 * Concatena um Array de strings com o separador especificado.
	 * @param aVal Array a ser concatenado
	 * @param sSep Separador
	 * @return String concatenada
	 * @see #concatStringArray(List<String>, String)
	 */
	public static String concatStringArray(String[] aVal, String sSep) {
		return StringUtils.join(aVal, sSep);
	}
	/**
	 * Concatena uma Lista de strings com o separador especificado.
	 * @param lVal Lista de strings a serem concatenadas
	 * @param sSep Separador
	 * @return String concatenada
	 * @see #concatStringArray(String[], String)
	 */
	public static String concatStringArray(List<String> lVal, String sSep) {
		return concatStringArray(lVal.toArray(new String[lVal.size()]), sSep);
	}
	
	public static Date convertPascalDateToUTC(Double pascalDate) {
		Date ret = new Date();
		TimeZone tz = TimeZone.getDefault();
		Integer gmtOffSet = tz.getRawOffset();
		
		ret.setTime((long) (86400000 * (pascalDate - 25569)));

		if (tz.inDaylightTime(ret))
			gmtOffSet += tz.getDSTSavings();

		return new Date(ret.getTime() - gmtOffSet);
	}

	public static Double convertUTCToPascalDate(Date utcDate) {
		TimeZone tz = TimeZone.getDefault();
		Integer gmtOffSet = tz.getRawOffset();
		
		if (tz.inDaylightTime(utcDate))
			gmtOffSet += tz.getDSTSavings();
		
		return ((double)(utcDate.getTime() + gmtOffSet))/ 86400000d + 25569d;
	}
	
	public static String retirarEspacos(String sVal) {
		
		String sRet = sVal.replaceAll("\\s", "");

        return sRet;
	}	
	
	public static String retirarAcentos(String sVal) {
		String sRet = sVal;
		
        sRet = sRet.replaceAll("[àáâãä]","a");
        sRet = sRet.replaceAll("[èéêë]","e");
        sRet = sRet.replaceAll("[ìíîï]","i");
        sRet = sRet.replaceAll("[òóôõ]","o");
        sRet = sRet.replaceAll("[ùúûü]","u");
        sRet = sRet.replaceAll("[ç]","c");

        sRet = sRet.replaceAll("[ÀÁÂÃÄ]","A");
        sRet = sRet.replaceAll("[ÈÉÊË]","E");
        sRet = sRet.replaceAll("[ÌÍÎÏ]","I");
        sRet = sRet.replaceAll("[ÒÓÔÕÖ]","O");	
        sRet = sRet.replaceAll("[ÙÚÛÜ]","U");
        sRet = sRet.replaceAll("[Ç]","C");

        return sRet;
	}
	
	public static String criptografaTripleDES(String chave, String texto) throws InvalidKeyException {
		String sRet = "";
		Cipher cipher;
		SecretKey key;
		
		while (chave.length() < 24) { //No Triple DES a chave precisa ser de 24 bytes.
			chave += chave;
		}
		chave = chave.substring(0, 24);
		
		try {
			
			key = SecretKeyFactory.getInstance("DESede").generateSecret(new DESedeKeySpec(chave.getBytes()));
			cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			String bString;
			for (byte b : cipher.doFinal(texto.getBytes())) {
				bString = Integer.toHexString(b & 0xFF); //0xFF para tranformar o signed byte para unsigned byte.
				
				if (bString.length() == 1)
					bString = "0" + bString;
				
				sRet += (bString.length() == 1) ? "0" : "";
				sRet += bString; 
			}
			
		} 
		catch (InvalidKeyException e) {
			throw e;
		} 
		catch (Exception e) {
			logger.error("Erro ao criptografar dados.", e);
		}
		return sRet;
	}
	
	public static String decriptografaTripleDES(String chave, byte[] dados) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		String sRet = "";
		Cipher cipher;
		SecretKey key;
    	
		while (chave.length() < 24) { //No Triple DES a chave precisa ser de 24 bytes.
			chave += chave;
		}
		chave = chave.substring(0, 24);

		try {
			
			key = SecretKeyFactory.getInstance("DESede").generateSecret(new DESedeKeySpec(chave.getBytes()));
			cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.DECRYPT_MODE, key);
			
			sRet = new String(cipher.doFinal(dados));
			
		} catch (InvalidKeyException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Erro ao descriptografar dados.", e);
		}
		return sRet;
    }

	public static String geraMD5(byte[] dados) {
		MessageDigest md = null;
		String sRet = "";

		try {  
	        md = MessageDigest.getInstance("MD5");  
			sRet = toHexString(md.digest(dados));
	    } 
		catch (NoSuchAlgorithmException e) {  
			logger.error("Erro ao gerar hash.", e);
	    }
	    return sRet; 
	}

	
	public static String geraMD5(InputStream is) {
		MessageDigest md = null;
		String sRet = null;
		byte[] buffer = new byte[1024];
		Integer lidos = 0;
		
		try {  
	        md = MessageDigest.getInstance("MD5");  
	        
	        do {
	        	lidos = is.read(buffer);
	        	if (lidos > 0)
	        		md.update(buffer, 0, lidos);
        	}
	        while (lidos != -1);
	        
			sRet = toHexString(md.digest());
	    } 
		catch (Exception e) {  
			logger.error("Erro ao gerar hash.", e);
	    }
	    return sRet; 
	}
	
	public static String geraMD5(File arq) {
		String sRet = null;
		InputStream fis = null;
		
		try {  
			fis = new FileInputStream(arq);
			sRet = geraMD5(fis);
	    } 
		catch (Exception e) {  
			logger.error("Erro ao gerar hash.", e);
	    }
		finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("Erro ao fechar o input stream.", e);
				}
			}
		}
	    return sRet; 
	}

	public static String toHexString(byte[] dados) {
        String bString;
		String sRet = "";
		
		for (byte b : dados) {
			
			bString = Integer.toHexString(b & 0xFF); //0xFF para tranformar o signed byte para unsigned byte.;

			if (bString.length() == 1)
				bString = "0" + bString;
			
			sRet += (bString.length() == 1) ? "0" : "";
			sRet += bString; 				
			
		}
		return sRet;
	}
	
	public static byte[] hexToBytes(String str) {
		int len = str.length() / 2;
		byte[] ret = new byte[len];
		 
		for (int i=0; i<len; i++) {
		    ret[i] =(byte) Integer.parseInt(str.substring(i*2,i*2+2).toUpperCase(), 16);
		}
		 
		return ret;
	}
	
	public static String codigoDeBarrasBoleto(Integer numConvenio, Integer tipoCobranca, Integer codBanco, Byte moeda, Long nossoNumero, Double valor, Date vencimento) {
		String sRet = null;
		
		Date base = new GregorianCalendar(1997, 10, 7).getTime();
		
		Long diff = vencimento.getTime() - base.getTime();
		
		int fatorVencimento = (int) (diff / (1000 * 60 * 60 * 24));
		
		String sRetSemDV = String.format("%03d%01d%04d%010d%06d%017d%02d", codBanco, moeda, fatorVencimento, (int) (valor * 100), numConvenio, nossoNumero, tipoCobranca);
		
		sRet = String.format("%s%01d%s", sRetSemDV.substring(0, 4),  modulo11(sRetSemDV), sRetSemDV.substring(4));
		
		return sRet;
	}

	public static String[] linhaDigitavelBoleto(String codigoDeBarras) {
		String sRet[] = new String[5];

		sRet[0] = codigoDeBarras.substring(0, 4) + codigoDeBarras.substring(19, 24);
		sRet[0] = sRet[0].substring(0, 5) + "." + sRet[0].substring(5, 9)  + modulo10(sRet[0]);
		
		sRet[1] = codigoDeBarras.charAt(24) + codigoDeBarras.substring(25, 34);
		sRet[1] = sRet[1].substring(0, 5) + "." + sRet[1].substring(5, 10)  + modulo10(sRet[1]);
		
		sRet[2] = codigoDeBarras.substring(34, 44);
		sRet[2] = sRet[2].substring(0, 5) + "." + sRet[2].substring(5, 10)  + modulo10(sRet[2]);

		sRet[3] = String.valueOf(codigoDeBarras.charAt(4));

		sRet[4] = codigoDeBarras.substring(5, 19);

		return sRet;
	}
	
	public static String formatarCPF(long cpf) {
		
		String cpfString = Long.toString(cpf);
		
		if (cpfString.length() > 11) {
			cpfString = cpfString.substring(0, 11);
		}
		else {
			while (cpfString.length() < 11) {
				cpfString = '0' + cpfString;
			}
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(cpfString.substring(0, 3));
		sb.append('.');
		sb.append(cpfString.substring(3, 6));
		sb.append('.');
		sb.append(cpfString.substring(6, 9));
		sb.append('-');
		sb.append(cpfString.substring(9, 11));
		
		return sb.toString();
	}
	public static String formatarCEP(int cep) {
		
		String cepString = Integer.toString(cep);

		if (cepString.length() > 8) {
			cepString = cepString.substring(0, 8);
		}
		else {
			while (cepString.length() < 8) {
				cepString = '0' + cepString;
			}
		}		
		
		StringBuilder sb = new StringBuilder();
		sb.append(cepString.substring(0, 5));
		sb.append('-');
		sb.append(cepString.substring(5, 8));
		
		return sb.toString();
		
	}	
	public static int modulo10(String valor){  
		int mult, soma, val, ret;
	    soma = 0;
	    mult = 2;
		for (int i = valor.length()-1; i >= 0; i--) {
			val = Integer.valueOf(""+valor.charAt(i)) * mult;
			if (val > 9)
				val = (val / 10) + (val % 10);
		    soma += Integer.valueOf(""+valor.charAt(i)) * mult;
		    if (mult == 2) 
		    	mult = 1;
		    else
		    	mult = 2;
		};
		ret = (((soma / 10) + 1) * 10) - soma;

		if (ret == 10)
			ret = 1;
		
		return ret;
	} 
	public static int modulo11(String valor){  
		int mult, soma, ret;
		soma = 0;
		mult = 2;
		for (int i = valor.length()-1; i >= 0; i--) {
		    soma += Integer.valueOf(""+valor.charAt(i)) * mult;
		    if (mult == 9) 
		    	mult = 2;
		    else
		    	mult++;
		};
		ret = soma % 11;
		if (ret > 9)
			ret = 0;

		return ret;
	}  


	public static byte[] redimensionaImagem(byte[] bImg, int paraLargura, int paraAltura)
	throws IOException	{
		
		Graphics2D g2 = null;
		ImageWriter writer = null;
		
		byte[] bRet = bImg;

		try {
			BufferedImage bufImageIn = ImageIO.read(new ByteArrayInputStream(bImg));
			BufferedImage bufImageRedim = new BufferedImage(paraLargura, paraAltura, BufferedImage.TYPE_INT_RGB);
	
			g2 = bufImageRedim.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        g2.drawImage(bufImageIn, 0, 0, paraLargura, paraAltura, null);
			
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
			if (iter.hasNext()) {
			
				writer = iter.next();
				ImageWriteParam writeParams = writer.getDefaultWriteParam();
				writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				writeParams.setCompressionQuality(VeiculoImagem.QUALIDADE_JPEG);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
	            writer.setOutput(ios);
				writer.write(null, new IIOImage(bufImageRedim, null, null), writeParams);
				
				bRet = baos.toByteArray();
			}
		} finally {
			if (writer != null)
			{
				writer.dispose();
			}
			if (g2 != null)
			{
				g2.dispose();
			}			
		}
		return bRet;
	}
/*
	public static byte[] redimensionaImagem(byte[] bImg, int paraLargura, int paraAltura, int densX, int densY)
	throws IOException	{
		
		Graphics2D g2 = null;
		ImageWriter writer = null;
		
		byte[] bRet = bImg;

		try {
			BufferedImage bufImageIn = ImageIO.read(new ByteArrayInputStream(bImg));
			BufferedImage bufImageRedim = new BufferedImage(paraLargura, paraAltura, BufferedImage.TYPE_INT_RGB);
			
			g2 = bufImageRedim.createGraphics();
			g2.setComposite(AlphaComposite.Src);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.drawImage(bufImageIn, 0, 0, paraLargura, paraAltura, null);
			
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        g2.dispose();
			
			ImageIO.write(bufImageRedim, "jpg", baos);
	        byte[] dados_ret = baos.toByteArray();
	        baos.close();
	        
	        bRet = dados_ret;
	        
		} finally {
			if (writer != null)
			{
				writer.dispose();
			}
			if (g2 != null)
			{
				g2.dispose();
			}			
		}
		return bRet;
	}
*/
	public static byte[] drawX(byte[] bImg) throws IOException	{
		
		Graphics2D g2 = null;
		ImageWriter writer = null;
		
		byte[] bRet = bImg;
		
		try
		{
			BufferedImage bufImageIn = ImageIO.read(new ByteArrayInputStream(bImg));
	    	int imgHeight = bufImageIn.getHeight();
	    	int imgWidth = bufImageIn.getWidth();
	    	
	        g2 = bufImageIn.createGraphics();
	    	g2.setColor(Color.RED);
	    	g2.drawLine(0, 0, imgWidth, imgHeight);
	    	g2.drawLine(0, imgHeight, imgWidth, 0);
		
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
			if (iter.hasNext()) {
			
				writer = iter.next();
				ImageWriteParam writeParams = writer.getDefaultWriteParam();
				writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				writeParams.setCompressionQuality(VeiculoImagem.QUALIDADE_JPEG);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
	            writer.setOutput(ios);
				writer.write(null, new IIOImage(bufImageIn, null, null), writeParams);
				bRet = baos.toByteArray();
			}
		} finally {
			if (writer != null)
			{
				writer.dispose();
			}
			if (g2 != null)
			{
				g2.dispose();
			}
		}
		return bRet;
	}	
	
	public static Color propVermelhorAmareloVerde(Integer max, Integer min, Integer val) {
		final int LIMEAR = 0xFF; 
		final int MAX_BRILHO_VERDE = 0xCC; 
		final double PROP = ((double)(LIMEAR * 2)) / (max - min);
		
		if (val == null)
			return Color.BLACK;
		
		val = (val < min ? min : val); //corta min
		val = (val > max ? max : val); //corta max
		
		int vm = 0x00;
		int vd = 0x00;
		int az = 0x00;

		int valor_prop = (int)(val * PROP);
		
		if (valor_prop > LIMEAR) { //Bom vamos mexer no byte vermelho.
			valor_prop &= 0xFF; //pega só o último byte porque é esse que vai ajustar o vermelho
			valor_prop = LIMEAR - valor_prop; //calcula a diferença porque o vermelho têm que começar ligado
			vm = valor_prop;
			vd = MAX_BRILHO_VERDE;
		}
		else {
			vm = 0xFF;
			vd = valor_prop;
		}
		
		return new Color(vm, vd, az);
	}
	
    public static void copyBytes(InputStream input, OutputStream output) throws IOException {
    	
    	byte[] buffer = new byte[4096];

        int bytesRead;
        while ((bytesRead = input.read(buffer))!= -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
    
    public static void fastCopyBytes(ReadableByteChannel input, WritableByteChannel output) throws IOException {
    	
    	ByteBuffer buffer = ByteBuffer.allocate(1024);
    	
    	int lido = input.read(buffer);
    	while (lido >= 0) {
    		buffer.flip();
    		output.write(buffer);
    		buffer.clear();
    	}
    	
    }

	/**
	 * Copia um arquivo de um lugar para outro, utilizando NIO.
	 * @param sourceFile o arquivo de origem
	 * @param destFile o arquivo de destino.
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		
		 FileChannel source = null;
		 FileChannel destination = null;

		 if(!destFile.exists()) {
		  destFile.createNewFile();
		 }
		 
		 FileOutputStream fosSourceFile = new FileOutputStream(sourceFile);
		 FileOutputStream fosDestFile = new FileOutputStream(destFile);
		 
		 try {
			 source = fosSourceFile.getChannel();
			 destination = fosDestFile.getChannel();
			 destination.transferFrom(source, 0, source.size());
		 }
		 finally {
			 if (fosSourceFile != null) {
				 fosSourceFile.close();
			 }
			 if (fosDestFile != null) {
				 fosDestFile.close();
			 }
			 if(source != null) {
				 source.close();
			 }
			 if(destination != null) {
				 destination.close();
			 }
		}
	}
	
	public static Integer execCmd(String cmd) {
		return execCmd(cmd, null, null, null);
	}
	
	public static Integer execCmd(String cmd, StringBuffer stdOut) {
		return execCmd(cmd, null, null, stdOut);
	}
	
	public static Integer execCmd(String cmd, Logger log, Priority pri) {
		return execCmd(cmd, log, pri, null);
	}
	
	public static Integer execCmd(String cmd, Logger log, Priority pri, StringBuffer stdOut) {
		Integer ret = null;
		try {  
			ProcessBuilder builder = new ProcessBuilder(cmd);
			builder.redirectErrorStream(true);
			Process proc = builder.start();
			
			BufferedReader in = new BufferedReader(  
							    new InputStreamReader(proc.getInputStream()));  

			String line = null;  
			while ((line = in.readLine()) != null) {
				if (log != null)
					log.log(pri,"EXECCMD: '"+cmd+"': "+line);
				if (stdOut != null)
					stdOut.append(line);
				else
					System.out.println("EXECCMD: '"+cmd+"': "+line);
			}
     
			if (log == null) {
				System.out.println("EXECCMD: '"+cmd+"': Aguardando término...");
				ret = proc.waitFor();
				System.out.println("EXECCMD: '"+cmd+"': Pronto, (exit code: "+ret+")");
			}
			else {
				log.log(pri,"EXECCMD: '"+cmd+"': Aguardando término...");
				ret = proc.waitFor();
				log.log(pri,"EXECCMD: '"+cmd+"': Pronto. (exit code: "+ret+")");
			}
		}	
		catch (Exception e) {
			if (log != null)
				log.error(e.getMessage(), e);
			else
				e.printStackTrace();  
		}
		return ret;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 14/07/2016
	 * @return Definir cor para celula Excel.
	 */	
	public static HSSFColor setColor(HSSFWorkbook workbook, byte r,byte g, byte b){
		HSSFPalette palette = workbook.getCustomPalette();
		HSSFColor hssfColor = null;
		try {
		hssfColor= palette.findColor(r, g, b); 
		if (hssfColor == null ){
		    palette.setColorAtIndex(IndexedColors.WHITE.index, r, g, b);
		    hssfColor = palette.getColor(IndexedColors.WHITE.index);
		}
		 } catch (Exception e) {
		logger.error(e);
		}

		 return hssfColor;
	}
	/**
	 * Converter coordenada
	 */
    public static String converterDecimalParaDMS(Double latitude, Double longitude){
        return converterLatitude(latitude) + ' ' + converterLongitude(longitude);
    }
 
    public static String converterLatitude(Double latitude){
        String resultado = "";
        if(latitude != null){
            String direcao = "N";
            if(latitude < 0){
                direcao = "S";
            }
            resultado = converter(latitude) + direcao;
        }
        return resultado;
    }
 
    public static String converterLongitude(Double longitude){
        String resultado = "";
        if(longitude != null){
            String direcao = "L";
            if(longitude < 0){
                direcao = "O";
            }
            resultado = converter(longitude) + direcao;
        }
        return resultado;
    }
         
    private static String converter(Double d){
        DecimalFormat df = new DecimalFormat("#.000");
    	
    	d = Math.abs(d);
 
        //degrees
        Integer i = d.intValue();
        String s = String.valueOf(i) + '°';
         
        //minutes
        d = d - i;
        d = d * 60;
        i = d.intValue();
//        s = s + String.valueOf(i) + '\'';
        s = s + String.format("%03d", i) + '\'';
         
        //seconds
        d = d - i;
        d = d * 60;
//        i = (int) Math.round(d);
//        s = s + String.valueOf(i) + '"';
        s = s + String.format("%07.3f", Float.parseFloat(df.format(d).replace(",", "."))).replace(",", ".") + '"';
         
        return s;
    }
    
    public static String verificaNomeAba(Workbook wb, LocalVigente localRelat, String origem) {

		String strNomePlanilha = null;
		strNomePlanilha = removeAcentos(localRelat.getNomeAbreviado().trim());
		
		Sheet oSheet;
		oSheet = wb.getSheet(strNomePlanilha);
		
		if (oSheet != null) {
			logger.info(origem + " - aba [" + strNomePlanilha + "] já existe. Formatando nome com Id. Local...");
			strNomePlanilha = strNomePlanilha.substring(0, (strNomePlanilha.length() - 3)) + String.format("%03d", localRelat.getIdLocal());
			logger.info(origem + " - nome da aba formatado para [" + strNomePlanilha + "].");
		}
		
		return strNomePlanilha;
	}
    
	/**
	 * Remover apenas acentos
	 */
	public static String removeAcentosAlt(String palavra) {      
        palavra = palavra.replaceAll("[aáàãâä]","a");
        palavra = palavra.replaceAll("[AÁÀÃÂÄ]","A");
        palavra = palavra.replaceAll("[eéèêë]", "e");
        palavra = palavra.replaceAll("[EÉÈÊË]", "E");
        palavra = palavra.replaceAll("[iíìîï]", "i");
        palavra = palavra.replaceAll("[IÍÌÎÏ]", "I");
        palavra = palavra.replaceAll("[oóòõôö]", "o");
        palavra = palavra.replaceAll("[OÓÒÕÔÖ]", "O");
        palavra = palavra.replaceAll("[uúùûü]", "u");
        palavra = palavra.replaceAll("[UÚÙÛÜ]", "U");
        palavra = palavra.replaceAll("ç", "c");
        palavra = palavra.replaceAll("Ç", "C");
        
        return palavra;    
    }
	
	/**
	 * Remover acentos e caracteres especiais
	 */
	public static String removeAcentos(String palavra) {      
        palavra = palavra.replaceAll("[aáàãâä]","a");
        palavra = palavra.replaceAll("[AÁÀÃÂÄ]","A");
        palavra = palavra.replaceAll("[eéèêë]", "e");
        palavra = palavra.replaceAll("[EÉÈÊË]", "E");
        palavra = palavra.replaceAll("[iíìîï]", "i");
        palavra = palavra.replaceAll("[IÍÌÎÏ]", "I");
        palavra = palavra.replaceAll("[oóòõôö]", "o");
        palavra = palavra.replaceAll("[OÓÒÕÔÖ]", "O");
        palavra = palavra.replaceAll("[uúùûü]", "u");
        palavra = palavra.replaceAll("[UÚÙÛÜ]", "U");
        palavra = palavra.replaceAll("ç", "c");
        palavra = palavra.replaceAll("Ç", "C");
        palavra = palavra.replaceAll("[°ºª]", "");
        palavra = palavra.replaceAll("[:;><]", "");
        
        return palavra;    
    }
	
	/**
	 * Remover acentos e caracteres especiais
	 */
	public static String removeCaracterEspecial(String palavra) {      
        palavra = palavra.replaceAll("[aáàãâä]","a");
        palavra = palavra.replaceAll("[AÁÀÃÂÄ]","A");
        palavra = palavra.replaceAll("[eéèêë]", "e");
        palavra = palavra.replaceAll("[EÉÈÊË]", "E");
        palavra = palavra.replaceAll("[iíìîï]", "i");
        palavra = palavra.replaceAll("[IÍÌÎÏ]", "I");
        palavra = palavra.replaceAll("[oóòõôö]", "o");
        palavra = palavra.replaceAll("[OÓÒÕÔÖ]", "O");
        palavra = palavra.replaceAll("[uúùûü]", "u");
        palavra = palavra.replaceAll("[UÚÙÛÜ]", "U");
        palavra = palavra.replaceAll("ç", "c");
        palavra = palavra.replaceAll("Ç", "C");
        
        return palavra;    
    }
	
	// ===============================================================================================
	// Novas funções para edição de IMAGENS
	// ===============================================================================================
	
	public static byte[] GrayScale(byte[] bImg) throws Exception {
		BufferedImage colorImage = ImageIO.read(new ByteArrayInputStream(bImg));
		BufferedImage image = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = image.getGraphics();
		g.drawImage(colorImage, 0, 0, null);
		g.dispose();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] dados_ret = baos.toByteArray();
        baos.close();
        return dados_ret;
	}
	public static byte[] BlackWhite(byte[] bImg) throws Exception {
		BufferedImage colorImage = ImageIO.read(new ByteArrayInputStream(bImg));
		BufferedImage image = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(),
				BufferedImage.TYPE_BYTE_BINARY);
		Graphics g = image.getGraphics();
		g.drawImage(colorImage, 0, 0, null);
		g.dispose();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] dados_ret = baos.toByteArray();
        baos.close();
        return dados_ret;
	}
	
	public static byte[] invertImage(byte[] bImg) {
        BufferedImage inputFile = null;
        try {
            inputFile = ImageIO.read(new ByteArrayInputStream(bImg));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int x = 0; x < inputFile.getWidth(); x++) {
            for (int y = 0; y < inputFile.getHeight(); y++) {
                int rgba = inputFile.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(),
                                255 - col.getGreen(),
                                255 - col.getBlue());
                inputFile.setRGB(x, y, col.getRGB());
            }
        }
        
        byte[] dados_ret = null;
        try {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(inputFile, "jpg", baos);
            dados_ret = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dados_ret;
    }
	public static byte[] convertImage(byte[] bImg, int red, int green, int blue) {
        BufferedImage inputFile = null;
        try {
            inputFile = ImageIO.read(new ByteArrayInputStream(bImg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int n_red, n_green, n_blue;

        for (int x = 0; x < inputFile.getWidth(); x++) {
            for (int y = 0; y < inputFile.getHeight(); y++) {
                int rgba = inputFile.getRGB(x, y);
                Color col = new Color(rgba, true);
                
                n_red = col.getRed() + red;
                n_green = col.getGreen() + green;
                n_blue = col.getBlue() + blue;
                
                if (n_red < 0)
                	n_red = 0;
                if (n_red > 255)
                	n_red = 255;

                if (n_green < 0)
                	n_green = 0;
                if (n_green > 255)
                	n_green = 255;
                
                if (n_blue < 0)
                	n_blue = 0;
                if (n_blue > 255)
                	n_blue = 255;
                
                
                col = new Color(n_red,
                				n_green,
                				n_blue);
                inputFile.setRGB(x, y, col.getRGB());
            }
        }

        byte[] dados_ret = null;
        try {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(inputFile, "jpg", baos);
            dados_ret = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dados_ret;
	}
	
	public static byte[] ObterImagemModificada(int tipo_imagem, byte[] imagem, int red, int green, int blue) throws Exception
	{
		switch (tipo_imagem) {
		case 1:
			return GrayScale(imagem);
		case 2:
			return BlackWhite(imagem);
		case 3:
			return invertImage(imagem);
		case 4:
			return convertImage(imagem, red, green, blue);

		default:
			return imagem;
		}
	}
	
	// ===============================================================================================
	
	
	public static byte[] obterPanoramica(List<VeiculoImagem> imagens) throws Exception {
		byte[] imagemPanoramica = null;
		
		try {
				imagemPanoramica = imagens.get(1).getImagem();
		} catch (Exception e) {
			throw new Exception("Erro ao obter imagem panorâmica.", e);
		}
		
		return imagemPanoramica;
	}
	
	public static byte[] obterMiniatura(List<VeiculoImagem> imagens, Integer idImagemMiniatura) throws Exception {
		byte[] imagemMiniatura = null;
		
		try {
			if (imagens.size() > 1 && (idImagemMiniatura == null || idImagemMiniatura == 0)) {
				imagemMiniatura = imagens.get(1).getImagem();
			} else {
				for (VeiculoImagem vi : imagens) {
					if (vi.getIdImagem().equals(idImagemMiniatura)) {
						imagemMiniatura = vi.getImagem();
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Erro ao obter imagem panorâmica.", e);
		}
		
		return imagemMiniatura;
	}
	
	public static byte[] recortaImagem(byte[] bImg, double proporcao) {
		
		byte[] imagem = null;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(bImg));
			BufferedImage dest = bufImg.getSubimage(0, 0, bufImg.getWidth(), bufImg.getHeight() - 57);
			ImageIO.write(dest, "JPG", baos);
			imagem = baos.toByteArray();
			
			if (proporcao > 0 && proporcao != 1.0)
				imagem = Funcoes.redimensionaImagem(imagem, (int)Math.round( dest.getWidth() * proporcao ), (int)Math.round( dest.getHeight() * proporcao ));
				
		} catch (IOException e) {
			
		}
		
		return imagem;
	}
	public static byte[] recortaImagem(byte[] bImg, Rectangle rec, double proporcao) {
		
		byte[] imagem = null;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(bImg));
			BufferedImage dest = bufImg.getSubimage(rec.x, rec.y, rec.width, rec.height);
			ImageIO.write(dest, "JPG", baos);
			imagem = baos.toByteArray();
			
			if (proporcao > 0 && proporcao != 1.0)
				imagem = Funcoes.redimensionaImagem(imagem, (int)Math.round( bufImg.getWidth() * proporcao ), (int)Math.round( bufImg.getHeight() * proporcao ));
				
		} catch (IOException e) {
			
		}
		
		return imagem;
	}
	public static byte[] recortaImagemRatio(byte[] bImg, Rectangle rec, double proporcao) {
		
		byte[] imagem = null;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(bImg));
			
			BufferedImage blackBox = new BufferedImage(
					(int)Math.round( bufImg.getWidth()  * proporcao ),
					(int)Math.round( bufImg.getHeight() * proporcao ), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphBlackBox = blackBox.createGraphics();
			graphBlackBox.setColor(Color.BLACK);
			graphBlackBox.fillRect(0, 0, blackBox.getWidth(), blackBox.getHeight());
			
			BufferedImage recorte = bufImg.getSubimage(rec.x, rec.y, rec.width, rec.height);
			
			Float prop = 0.0f;
			Integer n_recorte_h,n_recorte_w;
			if (rec.height > rec.width) {
				prop = (float)rec.width / (float)rec.height;
				n_recorte_h = blackBox.getHeight();
				n_recorte_w = (int)Math.round(n_recorte_h * prop);
			} else {
				prop = (float)rec.height / (float)rec.width;
				n_recorte_w = blackBox.getWidth();
				n_recorte_h = (int)Math.round(n_recorte_w * prop);
			}
			
			ImageIO.write(recorte, "JPG", baos);
			imagem = baos.toByteArray();
			
			if (proporcao > 0 && proporcao != 1.0)
				imagem = Funcoes.redimensionaImagem(imagem, n_recorte_w, n_recorte_h);
				
			BufferedImage bufImageIn = ImageIO.read(new ByteArrayInputStream(imagem));
			
			Integer x, y;
			if (rec.height > rec.width) {
				x = (int)Math.round((((float)blackBox.getWidth() / 2.0) - ((float)n_recorte_w / 2.0)));
				y = 0;
			} else {
				x = 0;
				y = (int)Math.round((((float)blackBox.getHeight() / 2.0) - ((float)n_recorte_h / 2.0)));
			}
			graphBlackBox.drawImage(bufImageIn, x, y, bufImageIn.getWidth(), bufImageIn.getHeight(), null);

			ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
			ImageIO.write(blackBox, "JPG", baos1);
			
			imagem = baos1.toByteArray();
		} catch (IOException e) {
			
		}
		
		return imagem;
	}
	public static byte[] recortaImagem(byte[] bImg, double proporcao1, double proporcao2) {
		
		byte[] imagem = null;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(bImg));
			BufferedImage dest = bufImg.getSubimage(0, 0, bufImg.getWidth(), bufImg.getHeight() - 57);
			ImageIO.write(dest, "JPG", baos);
			imagem = baos.toByteArray();
			
			if ((proporcao1 > 0 && proporcao1 != 1.0)||(proporcao2 > 0 && proporcao2 != 1.0))
				imagem = Funcoes.redimensionaImagem(imagem, (int)Math.round( dest.getWidth() * proporcao1 ), (int)Math.round( dest.getHeight() * proporcao2 ));
				
		} catch (IOException e) {
			
		}
		
		return imagem;
	}
	
	public static byte[] recortaImagemSemBorda(byte[] bImg, Rectangle rec) {
		
		byte[] imagem = null;
				
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(bImg));
			BufferedImage recorte = bufImg.getSubimage(rec.x, rec.y, rec.width, rec.height);
			ImageIO.write(recorte, "JPG", baos);
			imagem = baos.toByteArray();
				
		} catch (IOException ie) {
			logger.error("Erro ao recortar imagem.", ie);
		}
		
		return imagem;
	}
	
	public static byte[] sobreporImagens(byte[] bImg1, byte[] bImg2, PosicaoMiniatura posicao) {
		
		byte[] dados = null;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			BufferedImage bufImg1 = ImageIO.read(new ByteArrayInputStream(bImg1));
			BufferedImage bufImg2 = ImageIO.read(new ByteArrayInputStream(bImg2));
			
			int x = 0, y = 0;
			
			switch (posicao) {
			case SUPERIOR_ESQUERDO:
				x = 0;
				y = 0;
				break;
				
			case SUPERIOR_DIREITO:
				x = bufImg1.getWidth() - bufImg2.getWidth();
				y = 0;
				break;
				
			case INFERIOR_ESQUERDO:
				x = 0;
				y = bufImg1.getHeight() - bufImg2.getHeight() - 57;
				break;
				
			case INFERIOR_DIREITO:
				x = bufImg1.getWidth() - bufImg2.getWidth();
				y = bufImg1.getHeight() - bufImg2.getHeight() - 57;
				break;

			default:
				x = 0;
				y = 0;
				break;
			}
		
		    BufferedImage result = new BufferedImage(
	                bufImg1.getWidth(), bufImg1.getHeight(), //work these out
	                BufferedImage.TYPE_INT_RGB);
		    Graphics2D g = result.createGraphics();
		    g.drawImage(bufImg1, 0, 0, bufImg1.getWidth(), bufImg1.getHeight(), null);
		    g.drawImage(bufImg2, x, y, bufImg2.getWidth(), bufImg2.getHeight(), null);
		    
		    ImageIO.write(result, "jpg", baos);
		    dados = baos.toByteArray();
		}
		catch(Exception e) {
			
		}
		
		return dados;
	}
	
	public static byte[] sobreporImagensInferiorDireita(byte[] bImg1, byte[] bImg2) {
		
		byte[] dados = null;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			BufferedImage bufImg1 = ImageIO.read(new ByteArrayInputStream(bImg1));
			BufferedImage bufImg2 = ImageIO.read(new ByteArrayInputStream(bImg2));
			
			int x = bufImg1.getWidth() - bufImg2.getWidth();
			int y = bufImg1.getHeight() - bufImg2.getHeight() - 57;
		
		    BufferedImage result = new BufferedImage(
	                bufImg1.getWidth(), bufImg1.getHeight(), //work these out
	                BufferedImage.TYPE_INT_RGB);
		    Graphics2D g = result.createGraphics();
		    g.drawImage(bufImg1, 0, 0, bufImg1.getWidth(), bufImg1.getHeight(), null);
		    g.drawImage(bufImg2, x, y, bufImg2.getWidth(), bufImg2.getHeight(), null);
		    
		    ImageIO.write(result, "jpg", baos);
		    dados = baos.toByteArray();
		}
		catch(Exception e) {
			
		}
		
		return dados;
	}
	public static int[] printPixelARGB(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;

		return new int[] { alpha, red, green, blue };
	}
	public static int printPixel(int[] pixel_rgb) {
		int p = 0;
		p = p | (pixel_rgb[0] << 24);
		p = p | (pixel_rgb[1] << 16);
		p = p | (pixel_rgb[2] << 8);
		p = p | pixel_rgb[3];

		return p;
	}
	private static BufferedImage AjustaImagem(int brightness, float contrast,
			BufferedImage bufferedImage) {

		float brightMul;

		brightMul = 1.0f + Math.min(150, Math.max(-150, brightness)) / 150.0f;
		contrast = Math.max(0, contrast + 1);

		int w = bufferedImage.getWidth();// rect.width;
		int h = bufferedImage.getHeight();// rect.height;

		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);

		float mul, add;
		if (contrast != 1) {
			mul = brightMul * contrast;
			add = -contrast * 128 + 128;
		} else {
			mul = brightMul;
			add = 0;
		}

		int pixel, rgb;
		int[] pixel_rgb;
		int[] pixel_rgb_n = new int[] { 0, 0, 0, 0 };

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {

				pixel = bufferedImage.getRGB(x, y);
				pixel_rgb = printPixelARGB(pixel);

				pixel_rgb_n[0] = pixel_rgb[0];
				pixel_rgb_n[1] = Math.round((pixel_rgb[1] * mul) + add);
				pixel_rgb_n[2] = Math.round((pixel_rgb[2] * mul) + add);
				pixel_rgb_n[3] = Math.round((pixel_rgb[3] * mul) + add);

				if (pixel_rgb_n[1] > 255)
					pixel_rgb_n[1] = (byte) 255;
				else if (pixel_rgb_n[1] < 0)
					pixel_rgb_n[1] = 0;

				if (pixel_rgb_n[2] > 255)
					pixel_rgb_n[2] = (byte) 255;
				else if (pixel_rgb_n[2] < 0)
					pixel_rgb_n[2] = 0;

				if (pixel_rgb_n[3] > 255)
					pixel_rgb_n[3] = (byte) 255;
				else if (pixel_rgb_n[3] < 0)
					pixel_rgb_n[3] = 0;

				rgb = printPixel(pixel_rgb_n);

				image.setRGB(x, y, rgb);

			}
		}

		return image;
	}
	public static byte[] AjustaImagemBytes(int brightness, float contrast, byte[] imagem) throws IOException {
		
		byte[] img_ret = null;
		
		BufferedImage img_org = ImageIO.read(new ByteArrayInputStream(imagem));
		BufferedImage img_dst = AjustaImagem(brightness, contrast, img_org);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img_dst, "jpg", baos);
		img_ret = baos.toByteArray();
		
		return img_ret;
	}
	public static byte[] AjusteBrilhoContraste(byte[] bImg, ImagemAjuste ia) throws IOException {
		
		byte[] img_ret = null;
		
		img_ret = AjustaImagemBytes(ia.getBrilho(), ia.getContraste(), bImg);
		
		return img_ret;
	}

    public static boolean isDataValida(String data, String formato)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
        dateFormat.setLenient(false);
        try
        {
            dateFormat.parse(data.trim());
        }
        catch (Exception e)
        {
            return false;
        }
        
        return true;
    }
}
