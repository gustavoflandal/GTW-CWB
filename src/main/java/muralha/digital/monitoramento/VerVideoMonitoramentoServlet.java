package muralha.digital.monitoramento;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.consilux.model.AcessoDiretorio;

import muralha.digital._ini.Inicializacao;
import muralha.digital.util.RespostaRequisicaoXML;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.VideoAttributes;

@WebServlet("/MuralhaDigital/VideoMonitoramento/Video")
public class VerVideoMonitoramentoServlet extends HttpServlet
{
	private static Logger logger = Logger.getLogger(VerVideoMonitoramentoServlet.class); 
	private static final long serialVersionUID = 1L;
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
       
    public VerVideoMonitoramentoServlet()
    {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
      	try
    	{
       		String msg = null;
			String strAcao = request.getParameter("acao");
	    	
	    	if (strAcao == null || strAcao == "") 
	    	{
	    		msg = "Ação não informada!";
	    		logger.warn(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	else if(strAcao.equals("verVideoPorEndereco"))
	    		VerVideoPorEndereco(request, response);
	    	
	    	else if(strAcao.equals("verVideoPorListaEndereco"))
	    		VerVideoPorListaEndereco(request, response);
	    	
	    	else if(strAcao.equals("verVideoPorListaEnderecoTemp"))
	    		VerVideoPorListaEnderecoFFMPEG(request, response);
	    	
    	}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar videos de monitoramento!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void VerVideoPorEndereco(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		AcessoDiretorio acesso_diretorio = new AcessoDiretorio();

		String enderecoVideo = request.getParameter("enderecoVideo");
		
		if (enderecoVideo == null || enderecoVideo.equals("")) 
    	{
    		String msg = "Arquivo de video não informado!";
    		logger.warn(msg);	
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
    	}

		byte bVideo[] = null;
		try
		{
			bVideo = acesso_diretorio.ObterArquivo(enderecoVideo);
		}
		catch (Exception ex)
		{
			String msg = "Erro ao obter video!";
    		logger.error(msg, ex);	
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}

		if (bVideo == null)
		{
			String msg = "Video não encontrado!";
    		logger.warn(msg);
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}

		try
		{
			response.setContentType("video/ogg");
			response.setContentLength(bVideo.length);
			response.getOutputStream().write(bVideo, 0, bVideo.length);
			response.flushBuffer();
		}
		catch (Exception ex)
		{
			String msg = "Erro ao obter video para exibição!";
    		logger.error(msg, ex);
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}
	}
	
	private void VerVideoPorListaEndereco(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		AcessoDiretorio acesso_diretorio = new AcessoDiretorio();

		String sListaEnderecosVideos = request.getParameter("listaEnderecosVideos");
		
		if (sListaEnderecosVideos == null || sListaEnderecosVideos.equals("")) 
    	{
    		String msg = "Lista de videos não informada!";
    		logger.warn(msg);	
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
    	}

		String[] enderecosVideos = null;
		byte bVideo[] = null;
		String arquivoSaida = Inicializacao.DirTemporarioVideos + "\\" + UUID.randomUUID() + "." + Inicializacao.FormatoVideoTemp;
		boolean arquivoTemporario = false;
		
		try
		{
			int qtdeVideos = sListaEnderecosVideos.split(";").length;
			enderecosVideos = sListaEnderecosVideos.split(";");

			if (qtdeVideos > 1)
			{
				bVideo = ConcatenarVideo(enderecosVideos, arquivoSaida);
				arquivoTemporario = true;
			}
			else
			{
				bVideo = acesso_diretorio.ObterArquivo(sListaEnderecosVideos);
				arquivoTemporario = false;
			}
			
			
			if (arquivoTemporario && qtdeVideos > 1)
			{
			    File arquivoSaidaRemover = new File(arquivoSaida); 
			    if (arquivoSaidaRemover.delete())
			    	logger.debug("Arquivo temporário " + arquivoSaidaRemover.getName() + " removido com sucesso!");
			    else
			    	logger.warn("Falha ao remover arquivo temporário " + arquivoSaidaRemover.getName());
			}
		}
		catch (Exception ex)
		{
			String msg = "Erro ao obter videos!";
    		logger.error(msg, ex);	
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}

		if (bVideo == null)
		{
			String msg = "Video não encontrado!";
    		logger.warn(msg);
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}

		try
		{
			response.setContentType("video/ogg");
			response.setContentLength(bVideo.length);
			response.getOutputStream().write(bVideo, 0, bVideo.length);
			response.flushBuffer();
		}
		catch (Exception ex)
		{
			String msg = "Erro ao obter video para exibição!";
    		logger.error(msg, ex);
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}
	}
	
	private void VerVideoPorListaEnderecoFFMPEG(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		AcessoDiretorio acesso_diretorio = new AcessoDiretorio();

		String sListaEnderecosVideos = request.getParameter("listaEnderecosVideos");
		
		if (sListaEnderecosVideos == null || sListaEnderecosVideos.equals("")) 
    	{
    		String msg = "Lista de videos não informada!";
    		logger.warn(msg);	
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
    	}

		byte bVideo[] = null;
		String arquivoSaida = null;
		boolean arquivoTemporario = false;
		
		try
		{
			int qtdeVideos = sListaEnderecosVideos.split(";").length;
			boolean sucesso = false;
			
			if (qtdeVideos > 1)
			{
				String listaVideos = sListaEnderecosVideos.replace(";", "|");
				arquivoSaida = Inicializacao.DirTemporarioVideos + "\\" + UUID.randomUUID() + "." + Inicializacao.FormatoVideoTemp;
				
				String cmd = Inicializacao.DirExeFFMPEG + " -i \"concat:" + listaVideos + "\" -codec copy " + arquivoSaida;

				sucesso = ExecutarComando(cmd, Inicializacao.DirFFMPEG);
				arquivoTemporario = true;
			}
			else
			{
				arquivoSaida = sListaEnderecosVideos;
				sucesso = true;
			}
			
			if (sucesso)
				bVideo = acesso_diretorio.ObterArquivo(arquivoSaida);
			else
			{
				String msg = "Erro ao preparar videos para exibição!";
	    		logger.warn(msg);
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
		}
		catch (Exception ex)
		{
			String msg = "Erro ao obter videos!";
    		logger.error(msg, ex);	
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}
		finally
		{
			if (arquivoTemporario)
			{
			    File arquivoSaidaRemover = new File(arquivoSaida); 
			    if (arquivoSaidaRemover.delete())
			    	logger.debug("Arquivo temporário " + arquivoSaidaRemover.getName() + " removido com sucesso!");
			    else
			    	logger.warn("Falha ao remover arquivo temporário " + arquivoSaidaRemover.getName());
			}
		}

		if (bVideo == null)
		{
			String msg = "Video não encontrado!";
    		logger.warn(msg);
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}

		try
		{
			response.setContentType("video/ogg");
			response.setContentLength(bVideo.length);
			response.getOutputStream().write(bVideo, 0, bVideo.length);
			response.flushBuffer();
		}
		catch (Exception ex)
		{
			String msg = "Erro ao obter video para exibição!";
    		logger.error(msg, ex);
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}
	}
	
    public byte[] ConcatenarVideo(String[] enderecosVideos, String nomeArquivoSaida) throws Exception
    {
		File arquivoSaida = new File(nomeArquivoSaida);
		List<MultimediaObject> src = new ArrayList<>();
		Encoder encoder = new Encoder();
	
		if (arquivoSaida.exists())
			arquivoSaida.delete();

		VideoAttributes videoAttributes = new VideoAttributes();
		videoAttributes.setCodec("libtheora");
		
		EncodingAttributes attributes = new EncodingAttributes();
		attributes.setFormat("ogv");
		attributes.setVideoAttributes(videoAttributes);
		
		
		for (String video : enderecosVideos)
		{
			src.add(new MultimediaObject(new File(video)));
		}

		encoder.encode(src, arquivoSaida, attributes);
		
		return FileUtils.readFileToByteArray(arquivoSaida);
	}

    private boolean ExecutarComando(String comando, String dirBase)
    {
    	Process process = null;
    	Runtime runtime = Runtime.getRuntime();

    	try
    	{
    		process = runtime.exec(comando, null, new File(dirBase));
    		process.waitFor();
    	}
    	catch (Exception ex)
    	{
    		logger.error("Erro ao executar comando!", ex);
//    		process.destroy();
    		return false;
    	}
    	finally
    	{
    		process.destroy();
    	}
    	
    	return true;
    }
}
