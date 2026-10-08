//package com.consilux.model;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
//import jcifs.smb.NtlmPasswordAuthentication;
//import jcifs.smb.SmbFile;
//
//import org.apache.commons.io.FilenameUtils;
//
//import com.consilux.conf.ConfiguracaoChaveValor;
//import com.consilux.conf.ConfiguracaoProvider;
//import com.consilux.model.AcessoFTP.eTipoArquivo;
//
//public class AcessoCompartilhamento implements AcessoStorageInterface {
//
////	private static Logger logger = Logger.getLogger(AcessoCompartilhamento.class);
//	
//	private String diretorio_movimento_lote;
//	private String dominio_smb;
//	private String usuario_smb;
//	private String senha_smb;
//	
//	private NtlmPasswordAuthentication auth;
//
//	public AcessoCompartilhamento() {
//		ConfiguracaoChaveValor ccv = ConfiguracaoProvider.getInstance()
//				.getConfiguracaoChaveValor();
//		diretorio_movimento_lote = ccv.get("diretorio_movimento_lote");
//		
//		dominio_smb = ccv.get("dominio_smb");
//		if(dominio_smb.trim().length() == 0) {
//			dominio_smb = null;
//		}
//		
//		usuario_smb = ccv.get("usuario_smb");
//		senha_smb = ccv.get("senha_smb");
//		
//		auth = new NtlmPasswordAuthentication(dominio_smb,usuario_smb,senha_smb);
//	}
//	
//	@Override
//	public String LerArquivoMovimentoLote(String movimento_lote)
//			throws IOException {
//		
//		String caminho = String.format("%s/%2s/%4s/%2s/%2s/%2s/%s",
//				diretorio_movimento_lote,
//				movimento_lote.substring(2, 4),
//				movimento_lote.substring(10, 14),
//				movimento_lote.substring(4, 6),
//				movimento_lote.substring(6, 8),
//				movimento_lote.substring(8, 10),
//				movimento_lote);
//		
//		SmbFile arquivo_lote = new SmbFile(caminho, auth);
//		
//		StringBuilder sb_movimento_lote = new StringBuilder();
//		String s_newline = System.getProperty("line.separator");
//		
//		if(arquivo_lote.exists() && arquivo_lote.canRead()) {
//			
//			BufferedInputStream bis = new BufferedInputStream(arquivo_lote.getInputStream());
//			InputStreamReader isr = new InputStreamReader(bis);
//			BufferedReader br = new BufferedReader(isr);
//
//			String s_linha;
//			
//			while ((s_linha = br.readLine()) != null) {
//				sb_movimento_lote.append(s_linha);
//				sb_movimento_lote.append(s_newline);
//			}
//			
//		}
//		
//		return sb_movimento_lote.toString();
//	}
//
//	@Override
//	public void EnviarArquivo(String Caminho, byte[] dados) throws IOException {
//		
//		SmbFile smb = new SmbFile(Caminho, auth);
//		
//		BufferedOutputStream bos = null;
//		
//		try {
//			bos = new BufferedOutputStream(smb.getOutputStream());
//			bos.write(dados);
//		} finally {
//			if(bos != null)
//				bos.close();
//		}
//		
//	}
//
//	@Override
//	public byte[] ObterArquivo(String Caminho) throws IOException {
//		byte[] dados = null;
//		
//		SmbFile smb = new SmbFile(Caminho, auth);
//		
//		if(smb.exists() && smb.canRead()) {
//			
//			BufferedInputStream bis = null;
//		
//			try {
//				dados = new byte[(int) smb.length()];
//				bis = new BufferedInputStream(smb.getInputStream());
//				bis.read(dados);
//			} finally {
//				if(bis != null)
//					bis.close();
//			}
//		}
//		
//		return dados;
//	}
//
//	@Override
//	public List<String> ListarArquivos(eTipoArquivo tipo, String movimento_lote)
//			throws IOException {
//
//		String codigo_empresa = movimento_lote.substring(2, 4);
//		List<String> arquivos = new ArrayList<String>();
//		String diretorio_smb, inicial, s_movimento_lote, extensao = null;
//
//		String caminho = String.format("%s/%2s/%4s/%2s/%2s/%2s",
//				diretorio_movimento_lote,
//				codigo_empresa,
//				movimento_lote.substring(10, 14),
//				movimento_lote.substring(4, 6),
//				movimento_lote.substring(6, 8),
//				movimento_lote.substring(8, 10));
//		Integer id_movimento_lote = Integer.parseInt(movimento_lote.substring(4, 10));
//		
//		switch (tipo) {
//		case IMAGEM:
//			inicial = "IM";
//			diretorio_smb = caminho + "/" + inicial + "/";
//			extensao = "JPG";
//			break;
//		case MOVIMENTO_LOTE:
//			inicial = "RM";
//			diretorio_smb = caminho + "/";
//			extensao = "TXT";
//			break;
//		case TEXTO:
//			inicial = "TX";
//			diretorio_smb = caminho + "/" + inicial + "/";
//			extensao = "TXT";
//			break;
//		default:
//			inicial = "";
//			diretorio_smb = diretorio_movimento_lote + "/";
//			break;
//		}
//		
//		s_movimento_lote = String.format("%2s%2s%06d", inicial,
//				codigo_empresa, id_movimento_lote);
//		
//		SmbFile dir = new SmbFile(diretorio_smb, auth);
//		for (SmbFile f : dir.listFiles()) {
//			if(f.isFile() && f.getName().substring(0, 10).equals(s_movimento_lote) &&
//					(extensao == null || FilenameUtils.getExtension(f.getName()).equalsIgnoreCase(extensao))) {
//				arquivos.add(f.getName());
//			}
//		}
//		
//		return arquivos;
//	}
//
//	@Override
//	public void DeletarArquivos(eTipoArquivo tipo, String movimento_lote)
//			throws IOException {
//		String codigo_empresa = movimento_lote.substring(2, 4);
//		String diretorio_smb, inicial, s_movimento_lote;
//
//		String caminho = String.format("%s/%2s/%4s/%2s/%2s/%2s",
//				diretorio_movimento_lote,
//				codigo_empresa,
//				movimento_lote.substring(10, 14),
//				movimento_lote.substring(4, 6),
//				movimento_lote.substring(6, 8),
//				movimento_lote.substring(8, 10));
//		Integer id_movimento_lote = Integer.parseInt(movimento_lote.substring(4, 10));
//		
//		switch (tipo) {
//		case IMAGEM:
//			inicial = "IM";
//			diretorio_smb = caminho + "/" + inicial;
//			break;
//		case MOVIMENTO_LOTE:
//			inicial = "RM";
//			diretorio_smb = caminho;
//			break;
//		case TEXTO:
//			inicial = "TX";
//			diretorio_smb = caminho + "/" + inicial;
//			break;
//		default:
//			inicial = "";
//			diretorio_smb = diretorio_movimento_lote;
//			break;
//		}
//		
//		s_movimento_lote = String.format("%2s%2s%06d", inicial,
//				codigo_empresa, id_movimento_lote);
//		
//		SmbFile dir = new SmbFile(diretorio_smb, auth);
//		for (SmbFile f : dir.listFiles()) {
//			if(f.isFile() && f.getName().substring(0, 10).equals(s_movimento_lote)) {
//				f.delete();
//			}
//		}
//		
//	}
//
//	@Override
//	public String Separador() {
//		return "/";
//	}
//
//}
