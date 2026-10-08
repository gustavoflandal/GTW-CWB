package com.consilux.model;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.consilux.model.AcessoFTP.eTipoArquivo;

public class ApagarArquivosFTP extends Thread {

	protected final static Logger logger = Logger.getLogger(ApagarArquivosFTP.class);
	
	private String MovimentoLote;
	
	public ApagarArquivosFTP(String MovimentoLote) {
		this.MovimentoLote = MovimentoLote;
	}
	
	@Override
	public void run() {
		AcessoStorageInterface acesso = AcessoStorageProvider.ObterInterface();
		
		eTipoArquivo[] tipos = new eTipoArquivo[3];
		tipos[0] = eTipoArquivo.IMAGEM;
		tipos[1] = eTipoArquivo.MOVIMENTO_LOTE;
		tipos[2] = eTipoArquivo.TEXTO;
		
		for(eTipoArquivo tipo : tipos) {
		try {
			acesso.DeletarArquivos(tipo, MovimentoLote);
		}
		catch(IOException e) {
			logger.error("Não foi possível remover arquivos de " + tipo, e);
		}
		}
		
	}
	
}
