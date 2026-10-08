package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class ReposicionamentoInfracao {

	private List<InfracaoSimplificada> listaInfracoesSimplificada = new ArrayList<InfracaoSimplificada>();
	
	public ReposicionamentoInfracao( List<Integer> listaInfracoes ) throws ConexaoException, SQLException {

		InfracaoSimplificada infracao = null;
		
		for (Integer idInfracao : listaInfracoes) {
			
			infracao = InfracaoSimplificada.buscaInfracaoPorId(idInfracao);
			
			if ( infracao != null )
				listaInfracoesSimplificada.add( infracao );
			
		}
		
	}
	
	private void limparListaInfracoes(){
		
		this.listaInfracoesSimplificada.clear();
		
	}
	
	public void finalizaReposicionamento(){
		
		limparListaInfracoes();
		
	}
	
	private void reposicionarInfracao( Integer idInfracao , Integer idProcesso ) throws ConexaoException, SQLException {

		Connection conn = null;
		CallableStatement cs = null;

		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{call spu_reposiciona_infracao_processo(?, ?)}"
			);
			cs.setInt(1, idInfracao);
			cs.setInt(2, idProcesso);
			cs.execute();
			
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

	}	
	
	public void reposicioarListaInfracoes( Integer idProcesso ) throws ConexaoException, SQLException{

		for (InfracaoSimplificada infracao : listaInfracoesSimplificada) {
			reposicionarInfracao(infracao.getId(), idProcesso);			
		}
		
	}
	
	public List<InfracaoSimplificada> getInfracoesSelecionadas(){
		
		return this.listaInfracoesSimplificada;
		
	}

}
