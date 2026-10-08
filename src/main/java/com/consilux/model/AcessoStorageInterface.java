package com.consilux.model;

import java.io.IOException;
import java.util.List;

import com.consilux.model.AcessoFTP.eTipoArquivo;

public interface AcessoStorageInterface {
	public String LerArquivoMovimentoLote(String movimento_lote)
			throws IOException;
	public void EnviarArquivo(String Caminho, byte[] dados) throws IOException;
	public byte[] ObterArquivo(String Caminho) throws IOException;
	public List<String> ListarArquivos(eTipoArquivo tipo, String movimento_lote) throws IOException;
	public List<String> ListarArquivosDir(String diretorio_ftp) throws IOException;
	public void DeletarArquivos(eTipoArquivo tipo, String movimento_lote)
			throws IOException;
	
	public String Separador();
}
