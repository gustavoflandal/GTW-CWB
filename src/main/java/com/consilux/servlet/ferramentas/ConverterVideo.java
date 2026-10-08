package com.consilux.servlet.ferramentas;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ws.schild.jave.Encoder;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.VideoAttributes;

public class ConverterVideo {

	private static Logger logger = LogManager.getLogger(ConverterVideo.class);
	
	public static byte[] ConverteVideo(byte[] video_original, Long id)
	{
		byte[] ret = null;
		File source = null, target = null;

		long inicio = System.currentTimeMillis();
		
		try {
			source = File.createTempFile("org_" + id, ".avi");
			target = File.createTempFile("cnv_" + id, ".mp4");
			
			FileUtils.writeByteArrayToFile(source, video_original);
			
			VideoAttributes video = new VideoAttributes();
			video.setCodec("h264");
			EncodingAttributes attrs = new EncodingAttributes();
			attrs.setFormat("mp4");
			attrs.setVideoAttributes(video);
			
			Encoder encoder = new Encoder();
			encoder.encode(new MultimediaObject(source), target, attrs);
			
			ret = FileUtils.readFileToByteArray(target);
		}
		catch (Exception e) {
			logger.error("Erro ao converter vídeo", e);
		}

		try
		{
			if (source != null)
				source.delete();
			if (target != null)
				target.delete();
		}
		catch(Exception e)
		{
			logger.debug("Erro ao excluir arquivos temporarios", e);
		}
		
		logger.debug("Tempo Conversão: " + (System.currentTimeMillis() - inicio));
		
		return ret;
	}
	
}
