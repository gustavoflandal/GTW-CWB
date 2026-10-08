package com.consilux.model;

import com.consilux.conf.ConfiguracaoProvider;

public class AcessoStorageProvider {

	public static AcessoStorageInterface ObterInterface() {
		String tipo_servidor = ConfiguracaoProvider.getInstance()
				.getConfiguracaoChaveValor().get("tipo_servidor");

		if (tipo_servidor.equalsIgnoreCase("dir"))
			return (AcessoStorageInterface) new AcessoDiretorio();
//		if (tipo_servidor.equalsIgnoreCase("smb"))
//			return (AcessoStorageInterface) new AcessoCompartilhamento();

		if (tipo_servidor.equalsIgnoreCase("sftp"))
			return (AcessoStorageInterface) new AcessoSFTP();

		return (AcessoStorageInterface) new AcessoFTP();
	}
	
	public static AcessoStorageInterface ObterInterface(String diretorio) {
		String tipo_servidor = ConfiguracaoProvider.getInstance()
				.getConfiguracaoChaveValor().get("tipo_servidor");

		if (tipo_servidor.equalsIgnoreCase("dir"))
			return (AcessoStorageInterface) new AcessoDiretorio();
//		if (tipo_servidor.equalsIgnoreCase("smb"))
//			return (AcessoStorageInterface) new AcessoCompartilhamento();

		if (tipo_servidor.equalsIgnoreCase("sftp"))
			return (AcessoStorageInterface) new AcessoSFTP(diretorio);

		return (AcessoStorageInterface) new AcessoFTP(diretorio);
	}

}
