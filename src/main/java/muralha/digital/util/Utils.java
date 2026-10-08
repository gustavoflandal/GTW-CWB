package muralha.digital.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;

public class Utils 
{
	
	private static Logger logger = LogManager.getLogger(Utils.class);
	
	public static String getClientIp(HttpServletRequest request) {

        String remoteAddr = null;

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }
	
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit)
	{
	    long diffInMillies = date2.getTime() - date1.getTime();
	    
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	public static boolean convertToBoolean(String str)
	{
		boolean ret = false;
		
		if(str.equals("true"))
			ret = true;
		else if(str.equals("false"))
			ret = false;
		else
			logger.error("Falha na conversão para BOOLEAN. str: " + str);
		
		return ret;
	}
	
	 public static Date parseDate(String date) 
	 {
	     try 
	     {

	    	 return new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date);    
	    	 
	     } 
	     
	     catch (Exception e) {
	    	 logger.error("Falha no processo parseDate()", e);
	         return null;
	     }
	  }
	 
	 public static String formatarData(Date date) 
	 {
	     try 
	     {
	    	 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    	 return sdf.format(date);    
	     } 
	     catch (Exception e) {
	    	 logger.error("Falha no processo formataDataBR()", e);
	         return null;
	     }
	  }
	 
	 /**
		 * Converte uma String para um objeto Date. Caso a String seja vazia ou nula, 
		 * retorna null - para facilitar em casos onde formulários podem ter campos
		 * de datas vazios.
		 * @param data String no formato dd/MM/yyyy a ser formatada
		 * @return Date Objeto Date ou null caso receba uma String vazia ou nula
		 * @throws Exception Caso a String esteja no formato errado
	*/
	public static Date formataData(String data) throws Exception 
	{ 
		if (data == null || data.equals(""))
			return null;
		
        Date date = null;
    
        try 
        {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            date = (java.util.Date)formatter.parse(data);
        } 
        catch (ParseException e) 
        {            
        	logger.error("Falha ao converter data do veiculo");
        }
        
        return date;
	}	
	
	/**
	*  Convenience method to add a specified number of minutes to a Date object
	*  From: http://stackoverflow.com/questions/9043981/how-to-add-minutes-to-my-date
	*  @param  minutes  The number of minutes to add
	*  @param  beforeTime  The time that will have minutes added to it
	*  @return  A date object with the specified number of minutes added to it 
	*/
	public static Date addMinutesToDate(int minutes, Date beforeTime)
	{
		Date retorno = new Date();
		
		try
		{
		    final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
	
		    long curTimeInMs = beforeTime.getTime();
		    Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
		    
		    retorno = afterAddingMins;
	    } 
	    catch (Exception e) 
	    {            
	    	logger.error("Falha ao adicionar data do veiculo");
	    }
		
		return retorno;
    
	}

    public static byte[] rotacionarImagem(byte[] bImg, int graus) throws IOException
    {
    	byte[] bRet = bImg;
    	
    	BufferedImage image = ImageIO.read(new ByteArrayInputStream(bImg));
    	
    	double theta = Math.toRadians(graus);
    	double sin = Math.abs(Math.sin(theta));
    	double cos = Math.abs(Math.cos(theta));
    	
    	int w = image.getWidth();
    	int h = image.getHeight();
    	int newW = (int) Math.floor(w * cos + h * sin);
    	int newH = (int) Math.floor(h * cos + w * sin);
    	
    	BufferedImage tmp = new BufferedImage(newW, newH, image.getType());
    	
    	Graphics2D g2d = tmp.createGraphics();
    	g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    	g2d.translate((newW - w) / 2, (newH - h) / 2);
    	g2d.rotate(theta, w / 2, h / 2);
    	g2d.drawImage(image, 0, 0, null);
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(tmp, "jpeg", baos);
        bRet = baos.toByteArray();
    	
        return bRet;
    }
    
    
	public static String FormatarPlacaConsultaBD(String placa, String placaAux, int tamanhoMinimoPlaca)
	{
		if (placa.length() < 7 && placa.length() >= tamanhoMinimoPlaca)
		{
			if (placaAux.length() == 3 && ExpValida.PLACA_PARCIAL_LETRAS.validar(placaAux.substring(0, placaAux.length())))
				placa = placa + (!placa.substring(placa.length() - 1, placa.length()).equals("*") ? "%" : "");
			
			if (placaAux.length() == 4 && ExpValida.PLACA_PARCIAL_ALFANUMERICO.validar(placaAux.substring(0, 4)))
				placa = (!placa.substring(0, 1).equals("*") ? "%" : "") + placa;
			
			if (!ExpValida.PLACA_PARCIAL_LETRAS.validar(placaAux.substring(0, placaAux.length())) && !ExpValida.PLACA_PARCIAL_ALFANUMERICO.validar(placaAux.substring(0, placaAux.length())))
			{
				if (placaAux.length() > 3 && !ExpValida.PLACA_PARCIAL_LETRAS.validar(placaAux.substring(0, 3)))
					placa = "%" + placa;
				if (placaAux.length() >= 4 && !ExpValida.PLACA_PARCIAL_ALFANUMERICO.validar(placaAux.substring(placaAux.length() - 4, placaAux.length())))
					placa += "%";
			}
		}
		
		return placa.replace("*", "%").replace("%%", "%");
	}
	
	public static boolean PlacaPossuiCaracterEspecial(String placa)
	{
		Pattern pattern = Pattern.compile("[*]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(placa);
		int count = 0;
		
		while (matcher.find())
		    count++;
		
		return count > 0;
	}
	
	public static boolean ValidarCaracterEspecialPlaca(String placa, int qtdeMaxCaracterEspecialPlaca)
	{
		Pattern pattern = Pattern.compile("[*]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(placa);
		int count = 0;
		
		while (matcher.find())
		    count++;
		
		return !(count > qtdeMaxCaracterEspecialPlaca);
	}
	
	public static String TratarCaracterEspecialPlaca(String placa)
	{
		for(var i = 0; i < placa.length(); i++)
		{
		    if (placa.substring(i, i+1).equals("*"))
		    	placa = SubstituirCaracterEspecialPlaca(placa, i);
		}
		
		return placa;
	}
	
	public static String SubstituirCaracterEspecialPlaca(String placa, int posicao)
	{
		StringBuilder placaAux = new StringBuilder(placa);
		switch (posicao)
		{
			case 0:
			case 1:
			case 2:
				placaAux.setCharAt(posicao, 'A');
				break;
		  	case 3:
		  	case 4:
		  	case 5:
		  	case 6:
		  		placaAux.setCharAt(posicao, '1');
		    	break;
		}
		
		return placaAux.toString();
	}
	
	public static String SubstituirCaracter(String texto, char caracter, int posicao)
	{
		StringBuilder textoAux = new StringBuilder(texto);
		textoAux.setCharAt(posicao, caracter);
		return textoAux.toString();
	}
}
