package muralha.digital.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.Range;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import muralha.digital._ini.Inicializacao;

public class EncurtadorURL {

	private static Logger logger = Logger.getLogger(EncurtadorURL.class);
	
    private static final String BITLY_SHORTEN_ENDPOINT = Inicializacao.BitlyShortenEndpoint; 
    private static final String BITLY_TOKEN = Inicializacao.BitlyToken;
    private static final String BITLY_DOMAIN = Inicializacao.BitlyDomain;
    private static final String BITLY_GROUP_GUID = Inicializacao.BitlyGroupGuid;
    
	public static String EncurtarUrlBitly(String longURL)
	{
		String link = null;
		
		URL url = null;
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		
		try
		{
			url = new URL(BITLY_SHORTEN_ENDPOINT);
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true); 
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer " + BITLY_TOKEN);

			String data = "{\"long_url\": \"" + longURL + "\", \"domain\": \"" + BITLY_DOMAIN + "\", \"group_guid\": \"" + BITLY_GROUP_GUID + "\"}";

			os = conn.getOutputStream();
			os.write(data.getBytes());
			os.flush();

			logger.debug(conn.getResponseCode() + " " + conn.getResponseMessage());

			Range<Integer> codigosSucesso = Range.between(200, 201);
			if (codigosSucesso.contains(conn.getResponseCode()))
			{
				br = new BufferedReader(new InputStreamReader((conn.getInputStream()))); // Getting the response from the webservice

				String output;
				logger.debug("Output from Server ....");
				StringBuilder aux = new StringBuilder();
				while ((output = br.readLine()) != null) {
					aux.append(output);
				}
				
				logger.debug(aux.toString());
				
				JsonObject jobj = new Gson().fromJson(aux.toString(), JsonObject.class);
				link = jobj.get("link").getAsString();
				logger.debug("Link curto gerado: " + link);
			}
		}
		catch (Exception e)
		{
			logger.error("Falha ao encurtar link!", e);
		}
		finally
		{
			try
			{
				if (url != null)
					url = null;
					
				if (conn != null)
					conn.disconnect();
				
				if (os != null)
					os.close();
				
				if (br != null)
					br.close();
			}
			catch (IOException e)
			{
				logger.error("Erro ao destruir objetos!", e);
			}
		}
		
		return link;
	}
}
