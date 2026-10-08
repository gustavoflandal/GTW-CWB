package com.consilux.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoChaveValor;
import com.consilux.conf.ConfiguracaoProvider;

//Autor: Luiz Amaral
//Data: 30/01/2015
//Versão de acesso para arquivos de Medição - Versão entre CAI e CAV da classe AcessoFTP divergente - Medida para evitar paralização do CAV

public class AcessoArquivosFTP {

	private String diretorio_movimento_lote, endereco_ftp, usuario_ftp, senha_ftp;
	private Integer porta_ftp = 21;
	
	private static Logger logger = Logger.getLogger(AcessoArquivosFTP.class);
	private LogRemessa logRemessa = new LogRemessa();

	public AcessoArquivosFTP(String diretorio) {
		ConfiguracaoChaveValor ccv = ConfiguracaoProvider.getInstance()
				.getConfiguracaoChaveValor();
		diretorio_movimento_lote = diretorio;
		boolean usuario_padrao = true;
		if(diretorio_movimento_lote.contains("@")) {
			int i_separador = diretorio_movimento_lote.indexOf('@');
			
			endereco_ftp = diretorio_movimento_lote.substring(i_separador + 1, diretorio_movimento_lote.length());
			diretorio_movimento_lote = diretorio_movimento_lote.substring(0, i_separador);
			if (endereco_ftp.contains("[")) {
				String usrpass = endereco_ftp.substring(1, endereco_ftp.indexOf(']'));
				endereco_ftp = endereco_ftp.substring(endereco_ftp.indexOf(']') + 1);
				String[] usrpass_partes = usrpass.split(":");
				usuario_ftp = usrpass_partes[0];
				senha_ftp = usrpass_partes[1];
				usuario_padrao = false;
			}
		}
		else {
			endereco_ftp = ccv.get("endereco_ftp");
		}
		if (endereco_ftp.contains(":")) {
			int i_separador = endereco_ftp.indexOf(':');
			
			porta_ftp = Integer.valueOf( endereco_ftp.substring(i_separador + 1, endereco_ftp.length()) );
			endereco_ftp = endereco_ftp.substring(0, i_separador);
		}
		if (usuario_padrao) {
		usuario_ftp = ccv.get("usuario_ftp");
		senha_ftp = ccv.get("senha_ftp");
		}
	}
	
	public AcessoArquivosFTP() {
		ConfiguracaoChaveValor ccv = ConfiguracaoProvider.getInstance()
				.getConfiguracaoChaveValor();
		diretorio_movimento_lote = ccv.get("diretorio_movimento_lote");
		if(diretorio_movimento_lote.contains("@")) {
			int i_separador = diretorio_movimento_lote.indexOf('@');
			
			endereco_ftp = diretorio_movimento_lote.substring(i_separador + 1, diretorio_movimento_lote.length());
			diretorio_movimento_lote = diretorio_movimento_lote.substring(0, i_separador);
		}
		else {
			endereco_ftp = ccv.get("endereco_ftp");
		}
		if (endereco_ftp.contains(":")) {
			int i_separador = endereco_ftp.indexOf(':');
			
			porta_ftp = Integer.valueOf( endereco_ftp.substring(i_separador + 1, endereco_ftp.length()) );
			endereco_ftp = endereco_ftp.substring(0, i_separador);
		}
		usuario_ftp = ccv.get("usuario_ftp");
		senha_ftp = ccv.get("senha_ftp");
	}



	public byte[] ObterArquivo(String Caminho) throws IOException {
		FTPClient ftp = new FTPClient();

		byte[] dados = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			ftp.connect(endereco_ftp, porta_ftp);
			ftp.login(usuario_ftp, senha_ftp);
			
			//ftp.enterLocalPassiveMode(); // CAI

			if(ftp.retrieveFile(Caminho, baos))
				dados = baos.toByteArray();
		} finally {
			baos.close();
			if (ftp.isConnected())
				ftp.disconnect();
		}
		return dados;
	}

	public String Separador() {
		return "/";
	}
	
	public List<String> ListarArquivosDir(String diretorio_ftp) throws IOException {
		
		FTPClient ftp = new FTPClient();
		List<String> arquivos = new ArrayList<String>();

		try {
			ftp.connect(endereco_ftp, porta_ftp);
			ftp.login(usuario_ftp, senha_ftp);
			
			//ftp.enterLocalPassiveMode(); //CAI

			for (FTPFile arquivo_ftp : ftp.listFiles(diretorio_ftp))
				arquivos.add(arquivo_ftp.getName());
		}

		catch (Exception e) {
			new ServletException("Falha Acesso FTP: " + e.getMessage());
			return null;
		
		} finally {
			ftp.disconnect();
		}

		return arquivos;
	}
	
	//Luiz Amaral
	//27/11/2015
	//ALTERAÇÃO DE TESTE NO APAIT
	//CASO FUNCIONE CORRETAMENTE O TIMEOUT ENTÃO APLICAR PARA OS METODOS CONVENCIONAIS ACIMA E REMOVER ESTES DOIS METODOS
	//DE LEITURA E ESCRITA
	public boolean EnviarArquivoAPAIT(String Caminho, byte[] dados, int idRemessa) throws IOException 
	{
		
		FTPClient ftp = new FTPClient();
		ByteArrayInputStream bais = new ByteArrayInputStream(dados);
		boolean retorno = false;

		try {
			
			//logger.info("EnviarArquivoAPAIT::Abrindo Conexão. IdRemessa: " + idRemessa);
			
			ftp.setConnectTimeout(120*1000);				//TimeOut de 2 minuto
			ftp.connect(endereco_ftp, porta_ftp);
			ftp.login(usuario_ftp, senha_ftp);
			
			//logger.info("EnviarArquivoAPAIT::setSoTimeout e setDataTimeout e setControlKeepAliveTimeout. IdRemessa: " + idRemessa);
			ftp.setSoTimeout(1500*1000);    			//TimeOut de 25 minutos
			ftp.setDataTimeout(240*1000);				//TimeOut de 4 minutos
			ftp.setControlKeepAliveTimeout(300); 	

			ftp.enterLocalPassiveMode();
			
			//logger.info("EnviarArquivoAPAIT::setFileType. IdRemessa: " + idRemessa);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			logger.info("EnviarArquivoAPAIT::storeFile(). IdRemessa: " + idRemessa);
			ftp.mkd( FilenameUtils.getFullPath(Caminho) );
			retorno = ftp.storeFile(Caminho, bais);
			
			//logger.info("EnviarArquivoAPAIT::Arquivo gravado no disco. IdRemessa: " + idRemessa);
			
			//retorno = true;
		
		}
		catch(Exception ex)
		{
			logger.error("Falha no FTP. Timeout na função de ENVIO de arquivos - Envio ao APAIT", ex);
			logRemessa.insereLogRemessa(idRemessa, "Exportação FTP APAIT", "Timeout na função de ENVIO de arquivos. Dir: " + Caminho + " -->" + ex.getMessage());
			retorno = false;
		}
		finally 
		{
			//logger.info("EnviarArquivoAPAIT::Fechando Conexão FTP. IdRemessa: " + idRemessa);
			bais.close();
			if (ftp.isConnected())
				ftp.disconnect();
			//logger.info("EnviarArquivoAPAIT::Retorno. IdRemessa: " + idRemessa);
		}
		
		return retorno;
	}

	public byte[] ObterArquivoAPAIT(String Caminho, int idRemessa) throws IOException
	{
		
		FTPClient ftp = new FTPClient();
		byte[] dados = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			
			//logger.info("ObterArquivoAPAIT::Abrindo Conexão. IdRemessa: " + idRemessa);
			ftp.setConnectTimeout(120*1000);			//TimeOut de 2 minutos
			ftp.connect(endereco_ftp, porta_ftp);
			ftp.login(usuario_ftp, senha_ftp);
			
			
			//logger.info("ObterArquivoAPAIT::setSoTimeout e setDataTimeout e setControlKeepAliveTimeout. IdRemessa: " + idRemessa);
			ftp.setSoTimeout(1500*1000);				//TimeOut de 25 minutos
			ftp.setDataTimeout(240*1000);				//TimeOut de 4 minutos
			ftp.setControlKeepAliveTimeout(300);	
			
			ftp.enterLocalPassiveMode();
			
			logger.info("ObterArquivoAPAIT::Obtendo arquivo diretorio. IdRemessa: " + idRemessa);
			if(ftp.retrieveFile(Caminho, baos))
			{
				dados = baos.toByteArray();
			}
				
		} 
		catch(Exception ex)
		{
			logger.error("Falha no FTP. Timeout na função de LEITURA de arquivos. Envio ao APAIT", ex);
			logRemessa.insereLogRemessa(idRemessa, "Exportação FTP APAIT", "Timeout na função de LEITURA de arquivos. Dir: " + Caminho + " -->" + ex.getMessage());
		}
		finally 
		{
			//logger.info("ObterArquivoAPAIT::Fechando Conexão FTP. IdRemessa: " + idRemessa);
			baos.close();
			if (ftp.isConnected())
				ftp.disconnect();
			//logger.info("ObterArquivoAPAIT::Retorno. IdRemessa: " + idRemessa);
		}

		return dados;
	}
}
