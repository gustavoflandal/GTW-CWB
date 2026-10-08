package com.consilux.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import com.consilux.conf.ConfiguracaoChaveValor;
import com.consilux.conf.ConfiguracaoProvider;

class AcessoFTPFiltro implements FTPFileFilter {

	private String s_movimento_lote;
	private String extensao;

	public AcessoFTPFiltro(String inicial, String codigo_empresa,
			int movimento_lote, String extensao) {

		this.s_movimento_lote = String.format("%2s%2s%06d", inicial,
				codigo_empresa, movimento_lote);
		this.extensao = extensao;
	}

	@Override
	public boolean accept(FTPFile arg0) {
		if (arg0 != null) {
			try {
				if ( arg0.getName().substring(0, 10).equals(s_movimento_lote) && 
					(extensao == null || FilenameUtils.getExtension(arg0.getName()).equalsIgnoreCase(extensao) ) )
					return true;
			} catch (Exception e) {
			}
		}
		return false;
	}
}

// http://www.devmedia.com.br/desenvolvendo-um-cliente-ftp/3547
public class AcessoFTP implements AcessoStorageInterface {

	private String diretorio_movimento_lote,
			endereco_ftp, usuario_ftp, senha_ftp;
	private Integer porta_ftp = 21;
	
	public enum eTipoArquivo {
		MOVIMENTO_LOTE, TEXTO, IMAGEM
	}

	public AcessoFTP(String diretorio) {
		ConfiguracaoChaveValor ccv = ConfiguracaoProvider.getInstance()
				.getConfiguracaoChaveValor();
		diretorio_movimento_lote = diretorio;
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
	
	public AcessoFTP() {
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

	public String LerArquivoMovimentoLote(String movimento_lote)
			throws IOException {
		FTPClient ftp = new FTPClient();
		StringBuilder sb_movimento_lote = new StringBuilder();
		String s_linha;
		String s_newline = System.getProperty("line.separator");

		BufferedInputStream bis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		String caminho = String.format("%s/%2s/%4s/%2s/%2s/%2s",
						diretorio_movimento_lote,
						movimento_lote.substring(2, 4),
						movimento_lote.substring(11, 15),
						movimento_lote.substring(5, 7),
						movimento_lote.substring(7, 9),
						movimento_lote.substring(9, 11));

		try {
			ftp.connect(endereco_ftp, porta_ftp);
			ftp.login(usuario_ftp, senha_ftp);
			ftp.changeWorkingDirectory(caminho);

//			ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode(); // somente para teste
			
			bis = new BufferedInputStream(
					ftp.retrieveFileStream(movimento_lote));
			isr = new InputStreamReader(bis);
			br = new BufferedReader(isr);

			while ((s_linha = br.readLine()) != null) {
				sb_movimento_lote.append(s_linha);
				sb_movimento_lote.append(s_newline);
			}
		} finally {
			if (br != null)
				br.close();
			if (isr != null)
				isr.close();
			if (bis != null)
				bis.close();
			if (ftp.isConnected())
				ftp.disconnect();
		}

		return sb_movimento_lote.toString();
	}

	public void EnviarArquivo(String Caminho, byte[] dados) throws IOException {
		FTPClient ftp = new FTPClient();

		ByteArrayInputStream bais = new ByteArrayInputStream(dados);

		try {
			ftp.connect(endereco_ftp, porta_ftp);
			ftp.login(usuario_ftp, senha_ftp);
			
//			ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode(); // somente para teste

			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.storeFile(Caminho, bais);
		} finally {
			bais.close();
			if (ftp.isConnected())
				ftp.disconnect();
		}
	}

	public byte[] ObterArquivo(String Caminho) throws IOException {
		FTPClient ftp = new FTPClient();

		byte[] dados = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			ftp.connect(endereco_ftp, porta_ftp);
			ftp.login(usuario_ftp, senha_ftp);
			
//			ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode(); // somente para teste

			if(ftp.retrieveFile(Caminho, baos))
				dados = baos.toByteArray();
		} finally {
			baos.close();
			if (ftp.isConnected())
				ftp.disconnect();
		}

		return dados;
	}

	public List<String> ListarArquivos(eTipoArquivo tipo, String movimento_lote)
			throws IOException {
		FTPClient ftp = new FTPClient();
		String diretorio_ftp, inicial, extensao = null;
		List<String> arquivos = new ArrayList<String>();

		String codigo_empresa = movimento_lote.substring(2, 4);
		
		String caminho = String.format("%s/%2s/%4s/%2s/%2s/%2s",
				diretorio_movimento_lote,
				codigo_empresa,
				movimento_lote.substring(11, 15),
				movimento_lote.substring(5, 7),
				movimento_lote.substring(7, 9),
				movimento_lote.substring(9, 11));
		Integer id_movimento_lote = Integer.parseInt(movimento_lote.substring(4, 10));
		
		switch (tipo) {
		case IMAGEM:
			inicial = "IM";
			diretorio_ftp = caminho + "/" + inicial;
			extensao = "JPG";
			break;
		case MOVIMENTO_LOTE:
			inicial = "RM";
			diretorio_ftp = caminho;
			extensao = "TXT";
			break;
		case TEXTO:
			inicial = "TX";
			diretorio_ftp = caminho + "/" + inicial;
			extensao = "TXT";
			break;
		default:
			inicial = "";
			diretorio_ftp = diretorio_movimento_lote;
			break;
		}

		AcessoFTPFiltro filtro = new AcessoFTPFiltro(inicial, codigo_empresa,
				id_movimento_lote, extensao);

		try {
			ftp.connect(endereco_ftp, porta_ftp);
			ftp.login(usuario_ftp, senha_ftp);
			
//			ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode(); // somente para teste
			// ftp.changeWorkingDirectory(diretorio_ftp);

			for (FTPFile arquivo_ftp : ftp.listFiles(diretorio_ftp, filtro))
				arquivos.add(arquivo_ftp.getName());

		} finally {
			ftp.disconnect();
		}

		return arquivos;
	}

	public void DeletarArquivos(eTipoArquivo tipo, String movimento_lote)
			throws IOException {
		FTPClient ftp = new FTPClient();
		String diretorio_ftp, inicial;//, extensao = null;
//		List<String> arquivos = new ArrayList<String>();

		String codigo_empresa = movimento_lote.substring(2, 4);
		
		String caminho = String.format("%s/%2s/%4s/%2s/%2s/%2s",
				diretorio_movimento_lote,
				codigo_empresa,
				movimento_lote.substring(10, 14),
				movimento_lote.substring(4, 6),
				movimento_lote.substring(6, 8),
				movimento_lote.substring(8, 10));
		Integer id_movimento_lote = Integer.parseInt(movimento_lote.substring(4, 10));
		
		switch (tipo) {
		case IMAGEM:
			inicial = "IM";
			diretorio_ftp = caminho + "/" + inicial;
//			extensao = "JPG";
			break;
		case MOVIMENTO_LOTE:
			inicial = "RM";
			diretorio_ftp = caminho;
//			extensao = "TXT";
			break;
		case TEXTO:
			inicial = "TX";
			diretorio_ftp = caminho + "/" + inicial;
//			extensao = "TXT";
			break;
		default:
			inicial = "";
			diretorio_ftp = diretorio_movimento_lote;
			break;
		}

		AcessoFTPFiltro filtro = new AcessoFTPFiltro(inicial, codigo_empresa,
				id_movimento_lote, null);

		try {
			ftp.connect(endereco_ftp, porta_ftp);
			ftp.login(usuario_ftp, senha_ftp);
			
//			ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode(); // somente para teste
			// ftp.changeWorkingDirectory(diretorio_ftp);

			for (FTPFile arquivo_ftp : ftp.listFiles(diretorio_ftp, filtro))
				ftp.deleteFile(diretorio_ftp + "/" + arquivo_ftp.getName());

		} finally {
			ftp.disconnect();
		}
	}

	@Override
	public String Separador() {
		return "/";
	}
	
	public List<String> ListarArquivosDir(String diretorio_ftp) throws IOException {
		
		FTPClient ftp = new FTPClient();
		List<String> arquivos = new ArrayList<String>();

		try {
			ftp.connect(endereco_ftp, porta_ftp);
			ftp.login(usuario_ftp, senha_ftp);
			
			//ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode();

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
	
}
