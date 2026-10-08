package com.consilux.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.lib.Conexao;

public class AvisoMovimentoReprovado extends Thread {

	private String descricao;
	private String descricao_ext;
	private boolean reprovado_erros;
	private Integer id_remessa;
	private String movimento_lote;

	public AvisoMovimentoReprovado(Integer id_remessa, String movimento_lote, String descricao, String descricao_ext, boolean reprovado_erros) {
		this.id_remessa = id_remessa;
		this.movimento_lote = movimento_lote;
		this.descricao = descricao;
		this.descricao_ext = descricao_ext;
		this.reprovado_erros = reprovado_erros;
	}
	
	private String ObterErros() {
		String erros = "";
		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM fcn_getRelatorioValidacao(?)");
			ps.setInt(1, id_remessa);
			ResultSet rs = ps.executeQuery();
			erros = CSVUtils.ObterCVSdeResultSet(rs).toString();
		} catch(Exception e) {}
		finally {
			try {
				if(conn != null)
					conn.close();
			} catch(Exception e) {}
		}
		return erros;
	}
	
	@Override
	public void run() {

		String conteudo;
		
		AcessoStorageInterface acesso_storage = AcessoStorageProvider.ObterInterface();
		
		if(reprovado_erros) {
			conteudo = ObterErros();
		} else {
			conteudo = descricao + System.getProperty("line.separator") + (descricao_ext != null ? descricao_ext : "");
		}
		
		String caminho = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_movimento_lote");
		if(caminho.contains("@")) {
			int i_separador = caminho.indexOf('@');
			
			caminho = caminho.substring(0, i_separador);
		}
		caminho = caminho.replace("DF", "RP") + acesso_storage.Separador() + movimento_lote;
		
		try {
			acesso_storage.EnviarArquivo(caminho, conteudo.getBytes());
		} catch (IOException e) {}
		
	}
}
