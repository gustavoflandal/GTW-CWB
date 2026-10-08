package com.consilux.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoChaveValor;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.model.AcessoFTP.eTipoArquivo;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelSftp.LsEntrySelector;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

class AcessoSFTPFiltro implements LsEntrySelector {

	private String s_movimento_lote;
	private String extensao;
	private List<String> nomes_arquivos;

	public AcessoSFTPFiltro(String inicial, String codigo_empresa,
			int movimento_lote, String extensao) {

		this.s_movimento_lote = String.format("%2s%2s%06d", inicial,
				codigo_empresa, movimento_lote);
		this.extensao = extensao;

		nomes_arquivos = new ArrayList<String>();
	}

	@Override
	public int select(LsEntry arg0) {
		if (arg0 != null) {
			try {
				if (!(arg0.getFilename().equals(".") || arg0.getFilename()
						.equals(".."))) {

					if (arg0.getFilename().substring(0, 10)
							.equals(s_movimento_lote)
							&& (extensao == null || FilenameUtils.getExtension(
									arg0.getFilename()).equalsIgnoreCase(
									extensao)))
						nomes_arquivos.add(arg0.getFilename());
				}
			} catch (Exception e) {
			}
		}
		return ChannelSftp.LsEntrySelector.CONTINUE;
	}

	public List<String> ObterNomes() {
		return nomes_arquivos;
	}

}

public class AcessoSFTP implements AcessoStorageInterface {

	protected final static Logger logger = Logger.getLogger(AcessoSFTP.class);

//	private String codigo_empresa;
	private String diretorio_movimento_lote;
	private String endereco_ftp;
	private String usuario_ftp;
	private String senha_ftp;

	public AcessoSFTP() {
		ConfiguracaoChaveValor ccv = ConfiguracaoProvider.getInstance()
				.getConfiguracaoChaveValor();
//		codigo_empresa = ccv.get("codigo_empresa");
		diretorio_movimento_lote = ccv.get("diretorio_movimento_lote");
		if(diretorio_movimento_lote.contains("@")) {
			int i_separador = diretorio_movimento_lote.indexOf('@');
			
			endereco_ftp = diretorio_movimento_lote.substring(i_separador + 1, diretorio_movimento_lote.length());
			diretorio_movimento_lote = diretorio_movimento_lote.substring(0, i_separador);
		}
		else {
			endereco_ftp = ccv.get("endereco_ftp");
		}
		usuario_ftp = ccv.get("usuario_ftp");
		senha_ftp = ccv.get("senha_ftp");
	}

	public AcessoSFTP(String diretorio) {
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
		usuario_ftp = ccv.get("usuario_ftp");
		senha_ftp = ccv.get("senha_ftp");
	}

	@Override
	public String LerArquivoMovimentoLote(String movimento_lote)
			throws IOException {

		String caminho = String.format("%s/%2s/%4s/%2s/%2s/%2s/%s",
				diretorio_movimento_lote, movimento_lote.substring(2, 4),
				movimento_lote.substring(10, 14),
				movimento_lote.substring(4, 6), movimento_lote.substring(6, 8),
				movimento_lote.substring(8, 10), movimento_lote);
		String s_newline = System.getProperty("line.separator");
		StringBuilder sb_movimento_lote = null;

		byte[] bArquivo = this.ObterArquivo(caminho);
		if (bArquivo != null) {
			sb_movimento_lote = new StringBuilder();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(bArquivo)));

			String s_linha;
			while ((s_linha = reader.readLine()) != null) {
				sb_movimento_lote.append(s_linha);
				sb_movimento_lote.append(s_newline);
			}
		}

		return sb_movimento_lote != null ? sb_movimento_lote.toString() : null;
	}

	@Override
	public void EnviarArquivo(String Caminho, byte[] dados) throws IOException {

		JSch jsch = new JSch();
		ByteArrayInputStream bais = new ByteArrayInputStream(dados);

		try {

			Session session = jsch.getSession(usuario_ftp, endereco_ftp);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(senha_ftp);
			session.connect();

			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			channel.put(bais, Caminho);

			channel.exit();
			session.disconnect();
		} catch (Exception e) {
			logger.error("Não foi possível enviar arquivo " + Caminho, e);
		}

	}

	@Override
	public byte[] ObterArquivo(String Caminho) throws IOException {

		JSch jsch = new JSch();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bArquivo = null;

		try {

			Session session = jsch.getSession(usuario_ftp, endereco_ftp);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(senha_ftp);
			session.connect();

			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			channel.get(Caminho, baos);

			bArquivo = baos.toByteArray();

			channel.exit();
			session.disconnect();
		} catch (Exception e) {
			logger.error("Não foi possível obter arquivo " + Caminho, e);
		}

		return bArquivo;
	}

	@Override
	public List<String> ListarArquivos(eTipoArquivo tipo, String movimento_lote)
			throws IOException {

		JSch jsch = new JSch();

		String diretorio_ftp, inicial, extensao = null;
		List<String> arquivos = null;

		String codigo_empresa = movimento_lote.substring(2, 4);
		
		String caminho = String.format("%s/%2s/%4s/%2s/%2s/%2s",
				diretorio_movimento_lote, 
				codigo_empresa,
				movimento_lote.substring(10, 14),
				movimento_lote.substring(4, 6), movimento_lote.substring(6, 8),
				movimento_lote.substring(8, 10));
		Integer id_movimento_lote = Integer.parseInt(movimento_lote.substring(
				4, 10));

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

		try {

			Session session = jsch.getSession(usuario_ftp, endereco_ftp);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(senha_ftp);
			session.connect();

			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();

			AcessoSFTPFiltro filtro = new AcessoSFTPFiltro(inicial,
					codigo_empresa, id_movimento_lote, extensao);
			channel.ls(diretorio_ftp, filtro);

			arquivos = filtro.ObterNomes();

			channel.exit();
			session.disconnect();
		} catch (Exception e) {
			logger.error("Não foi possível listar diretório " + diretorio_ftp,
					e);
		}

		return arquivos;
	}

	@Override
	public void DeletarArquivos(eTipoArquivo tipo, String movimento_lote)
			throws IOException {

		JSch jsch = new JSch();

		String diretorio_ftp, inicial; // , extensao = null;
		// List<String> arquivos = null;

		String caminho = String.format("%s/%2s/%4s/%2s/%2s/%2s",
				diretorio_movimento_lote, movimento_lote.substring(2, 4),
				movimento_lote.substring(10, 14),
				movimento_lote.substring(4, 6), movimento_lote.substring(6, 8),
				movimento_lote.substring(8, 10));
		// Integer id_movimento_lote =
		// Integer.parseInt(movimento_lote.substring(
		// 4, 10));

		switch (tipo) {
		case IMAGEM:
			inicial = "IM";
			diretorio_ftp = caminho + "/" + inicial;
			// extensao = "JPG";
			break;
		case MOVIMENTO_LOTE:
			inicial = "RM";
			diretorio_ftp = caminho;
			// extensao = "TXT";
			break;
		case TEXTO:
			inicial = "TX";
			diretorio_ftp = caminho + "/" + inicial;
			// extensao = "TXT";
			break;
		default:
			inicial = "";
			diretorio_ftp = diretorio_movimento_lote;
			break;
		}

		try {

			Session session = jsch.getSession(usuario_ftp, endereco_ftp);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(senha_ftp);
			session.connect();

			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();

			// AcessoSFTPFiltro filtro = new AcessoSFTPFiltro(inicial, caminho,
			// id_movimento_lote, null);
			for (Object obj_entry : channel.ls(diretorio_ftp)) {
				LsEntry entry = (LsEntry) obj_entry;
				
				if(!entry.getAttrs().isDir())
					channel.rm(entry.getLongname());
			}
			// channel.ls(caminho, filtro);

			// arquivos = filtro.ObterNomes();

			channel.exit();
			session.disconnect();
		} catch (Exception e) {
			logger.error("Não foi possível listar diretório " + diretorio_ftp,
					e);
		}

	}

	@Override
	public String Separador() {
		return "/";
	}

	@Override
	public List<String> ListarArquivosDir(String dir) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
