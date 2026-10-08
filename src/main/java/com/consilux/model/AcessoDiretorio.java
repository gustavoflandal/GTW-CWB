package com.consilux.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import com.consilux.conf.ConfiguracaoChaveValor;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.model.AcessoFTP.eTipoArquivo;

class AcessoFiltro implements IOFileFilter {

	private String s_movimento_lote;
	private String extensao;

	public AcessoFiltro(String inicial, String codigo_empresa,
			int movimento_lote, String extensao) {

		this.s_movimento_lote = String.format("%2s%2s%06d", inicial,
				codigo_empresa, movimento_lote);
		this.extensao = extensao;
	}

	@Override
	public boolean accept(File arg0) {
		if (arg0 != null) {
			try {
				if (arg0.getName().substring(0, 10).equals(s_movimento_lote)
						&& (extensao == null || FilenameUtils.getExtension(
								arg0.getName()).equalsIgnoreCase(extensao)))
					return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	@Override
	public boolean accept(File arg0, String arg1) {
		if (arg1 != null) {
			try {
				if (arg1.substring(0, 10).equals(s_movimento_lote)
						&& (extensao == null || FilenameUtils
								.getExtension(arg1).equalsIgnoreCase(extensao)))
					return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

}

public class AcessoDiretorio implements AcessoStorageInterface {

//	private static Logger logger = Logger.getLogger(AcessoDiretorio.class);

	private String diretorio_movimento_lote;
	private String dominio_smb;
	@SuppressWarnings("unused")
	private String usuario_smb;
	@SuppressWarnings("unused")
	private String senha_smb;

	public AcessoDiretorio() {
		ConfiguracaoChaveValor ccv = ConfiguracaoProvider.getInstance()
				.getConfiguracaoChaveValor();
		diretorio_movimento_lote = ccv.get("diretorio_movimento_lote");

		dominio_smb = ccv.get("dominio_smb");
		if (dominio_smb.trim().length() == 0) {
			dominio_smb = null;
		}

		usuario_smb = ccv.get("usuario_smb");
		senha_smb = ccv.get("senha_smb");
	}

	@Override
	public String LerArquivoMovimentoLote(String movimento_lote)
			throws IOException {
		String caminho = String.format("%s\\%2s\\%4s\\%2s\\%2s\\%2s",
				diretorio_movimento_lote, 
				movimento_lote.substring(2, 4),
				movimento_lote.substring(10, 14),
				movimento_lote.substring(4, 6), 
				movimento_lote.substring(6, 8),
				movimento_lote.substring(8, 10));

		String sb_movimento_lote = null;

		File arquivo_movimento = FileUtils.getFile(caminho, movimento_lote);
		if (arquivo_movimento.exists() && arquivo_movimento.canRead()) {
			sb_movimento_lote = FileUtils.readFileToString(arquivo_movimento, Charset.defaultCharset());
		}
//		logger.debug(sb_movimento_lote);

		return sb_movimento_lote;
	}

	@Override
	public void EnviarArquivo(String Caminho, byte[] dados) throws IOException {

		File arquivo = FileUtils.getFile(Caminho);
		
		FileUtils.writeByteArrayToFile(arquivo, dados);
		
	}

	@Override
	public byte[] ObterArquivo(String Caminho) throws IOException {
		byte[] dados = null;

		File arquivo = FileUtils.getFile(Caminho);
		
		dados = FileUtils.readFileToByteArray(arquivo);

		return dados;
	}

	@Override
	public List<String> ListarArquivos(eTipoArquivo tipo, String movimento_lote)
			throws IOException {
		String codigo_empresa = movimento_lote.substring(2, 4);
		List<String> arquivos = new ArrayList<String>();
		String diretorio_smb, inicial, extensao = null;

		String caminho = String.format("%s\\%2s\\%4s\\%2s\\%2s\\%2s",
				diretorio_movimento_lote, codigo_empresa,
				movimento_lote.substring(10, 14),
				movimento_lote.substring(4, 6), movimento_lote.substring(6, 8),
				movimento_lote.substring(8, 10));
		Integer id_movimento_lote = Integer.parseInt(movimento_lote.substring(
				4, 10));

		switch (tipo) {
		case IMAGEM:
			inicial = "IM";
			diretorio_smb = caminho + "\\" + inicial;
			extensao = "JPG";
			break;
		case MOVIMENTO_LOTE:
			inicial = "RM";
			diretorio_smb = caminho;
			extensao = "TXT";
			break;
		case TEXTO:
			inicial = "TX";
			diretorio_smb = caminho + "\\" + inicial;
			extensao = "TXT";
			break;
		default:
			inicial = "";
			diretorio_smb = diretorio_movimento_lote;
			break;
		}

		AcessoFiltro filtro = new AcessoFiltro(inicial, codigo_empresa,
				id_movimento_lote, extensao);

//		logger.debug("XXXXXXXX     " + diretorio_smb);

		for (File arquivo : FileUtils.listFiles(
				new File(diretorio_smb), filtro, null)) {
			arquivos.add(arquivo.getName());
		}

		return arquivos;
	}

	@Override
	public void DeletarArquivos(eTipoArquivo tipo, String movimento_lote)
			throws IOException {
		String codigo_empresa = movimento_lote.substring(2, 4);
		String diretorio_smb, inicial;

		String caminho = String.format("%s\\%2s\\%4s\\%2s\\%2s\\%2s",
				diretorio_movimento_lote, codigo_empresa,
				movimento_lote.substring(10, 14),
				movimento_lote.substring(4, 6), movimento_lote.substring(6, 8),
				movimento_lote.substring(8, 10));
		Integer id_movimento_lote = Integer.parseInt(movimento_lote.substring(
				4, 10));

		switch (tipo) {
		case IMAGEM:
			inicial = "IM";
			diretorio_smb = caminho + "\\" + inicial;
			break;
		case MOVIMENTO_LOTE:
			inicial = "RM";
			diretorio_smb = caminho;
			break;
		case TEXTO:
			inicial = "TX";
			diretorio_smb = caminho + "\\" + inicial;
			break;
		default:
			inicial = "";
			diretorio_smb = diretorio_movimento_lote;
			break;
		}

		AcessoFiltro filtro = new AcessoFiltro(inicial, codigo_empresa,
				id_movimento_lote, null);

		for (File arquivo : FileUtils.listFiles(
				FileUtils.getFile(diretorio_smb), filtro, null)) {
			arquivo.delete();
		}

	}

	@Override
	public String Separador() {
		return "\\";
	}

	@Override
	public List<String> ListarArquivosDir(String dir) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
