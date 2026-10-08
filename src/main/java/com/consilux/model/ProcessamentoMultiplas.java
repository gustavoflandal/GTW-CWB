package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpSession;

import com.consilux.infra.AcaoEvento;
import com.consilux.infra.AcaoListener;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Implementação da classe de processamento, focada em banco de dados.
 * @author fos
 */
public class ProcessamentoMultiplas extends Processamento {

	private LinkedList<Integer> infracoes; 
	private Integer buscandoAssincrono = 0; 
	private static ExecutorService threadPool = Executors.newFixedThreadPool(10);
	private Integer infracaoLimite = 0;

	/**
	 * Constrói o objeto Processamento com os seus respectivos atributos.
	 * @param etapa Etapa qual o processo esta.
	 * @param idUsuario Identificador do usuário que esta operando.
	 * @param idEnquadramento Identificador do enquadramento das infrações a serem processados, ou NULL para todas.
	 * @param consistencia Se 'True' somente consistentes, se 'False' somente inconsistentes, ou NULL para todas.
	 * @param espera Se 'True' somente esperas, se 'False' somente não-esperas, ou NULL para todas.
	 * @param periodoIni Data inicial ser selecionada.
	 * @param periodoFim Data final a ser selecionada.
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	protected ProcessamentoMultiplas(EtapaProcesso etapa, Integer idUsuario, Integer idRemessa, Boolean amostra, Integer idEnquadramento, Boolean consistencia, Boolean espera, Timestamp periodoIni, Timestamp periodoFim) throws InterruptedException, ExecutionException, TimeoutException, SQLException, ConexaoException {
		super(etapa, idUsuario, idRemessa, amostra, idEnquadramento, consistencia, espera, periodoIni, periodoFim);

		logger.debug("Iniciando processamento com infrações Multiplas");
//		logger.debug("EtapaProcesso " + etapa + ", Integer " + idUsuario + ", Integer "
//				+ idRemessa + ", Boolean " + amostra + ", Integer " + idEnquadramento + ", Boolean " + consistencia
//				+ ", Boolean " + espera + ", Timestamp " + periodoIni + ", Timestamp " + periodoFim + ")");
		infracoes = new LinkedList<Integer>();

		// garantir a finalização do processo "abandonado"
		finalizaProcessamento();		

		infracoes.addAll(
				buscaMultiplasInfracoesProcessamentoBD( null, null));

		logger.debug("Infracoes encontradas: " + infracoes.size());
		
		//buscarNovasInfracoes( true );
		if (infracoes.size() > 0)
			iniciaInfracao(infracoes.get(0));

	}

	private void buscarNovasInfracoesAssincrono() {

		int iPos = 0;
		int iSizeList = 0;
		boolean buscarNovasInfracoes = true;


		if (this.infracaoAtual != null){
			synchronized (this.infracoes) {
				iPos = this.infracoes.indexOf( this.infracaoAtual );
				iSizeList = this.infracoes.size();
			}
		}

		if( iSizeList > 0  ){
			if( iPos < iSizeList/2  )
				buscarNovasInfracoes = false;
		}
		
		logger.debug("DEBUG buscarNovasInfracoesAssincrono(). PosArray=[" + iPos + "] SizeArray=[" + iSizeList +"]" );

		if( buscarNovasInfracoes ){
			synchronized (this.buscandoAssincrono) {
				if ( this.buscandoAssincrono > 0) {
					buscarNovasInfracoes = false;
					logger.warn("Não foi realizado busca assincrona, pois já existe uma em execução. PosArray=[" + iPos + "] SizeArray=[" + iSizeList +"] buscandoAssincrono=[" + this.buscandoAssincrono + "]" );
				}
			}
		}
		
		if ( buscarNovasInfracoes ) {
			logger.debug("Ativando o mecanismo de buscas de novas infrações em background. Thread [" + Thread.currentThread().hashCode() + "]");

			synchronized (this.buscandoAssincrono) {
				this.buscandoAssincrono = iPos;
			}
			
			// Cria um work item (que vai realizar a busca no banco e preparar o retorno) 
			Runnable asyncTask = criaWorkItemBuscaInfracoes();

			// Dispara na threadPool
			submitTask(asyncTask);
		}

	}

	@Override
	public Integer buscaInfracao(Integer infracaoAtual, Alvo alvo, Connection conn) throws SQLException, ConexaoException {

		Date tempoIni = new Date();

		Integer iRet = 0;

		switch (alvo) {
		case ANTERIOR:
			iRet = buscaAnterior(conn);
			break;
		case PROXIMO:

			// se o usuário já processou a próxima então, pode ir para o proxímo, se não, somente com próximo novo.
			if (verificarSePodeProximo())
				iRet = buscaProxima(conn);
			else
				iRet = this.infracaoAtual; // retorna a atual, para não ir para a próxima da lista

			break;
			
		case ATUAL:
			iRet = buscaAtual(infracaoAtual, conn);
			break;
		case PROXIMO_NOVO:
			iRet = buscaProxima(conn);
			
			// se acabou as imagens e esta sendo executado uma busca de novas infrações, log como erro.
			if (iRet == 0){
				synchronized (this.buscandoAssincrono) {
					if (this.buscandoAssincrono > 0)
						logger.error("Não possuí mais infrações do buffer para o usuário=" + super.getIdUsuario() + " id_processo=[" + super.getEtapa().getId() + "]" );
				}
			}
			else{
				buscarNovasInfracoesAssincrono();
				atualizarInfracaoLimite( iRet );
			}

			break;
		}

		// Loga a duração da operação.
		logarTempoProcessamento("[TEMPO] busca_infracao: ", tempoIni.getTime());

		logger.debug("Retornando infração; usuário=" + 
				super.getIdUsuario() + ", origem=" + infracaoAtual + ", ret=" + iRet);

		if (iRet > 0)
			iniciaInfracao(iRet, tempoIni);

		return iRet;

	}

	private void atualizarInfracaoLimite(Integer novaInfracao) {

		int iPosNova = -1;
		int iPosLimite = -1;

		synchronized (this.infracoes) {
			iPosNova = this.infracoes.indexOf(novaInfracao);
			iPosLimite = this.infracoes.indexOf(this.infracaoLimite);
		}

		if ( iPosNova > iPosLimite  )
			this.infracaoLimite = novaInfracao;

	}

	/**
	 * Agenda a execução de uma função de maneira assíncrona. 
	 * @param asyncTask a função que será executada.
	 * @return um objeto Future, ou null
	 */
	public static Future<?> submitTask(Runnable asyncTask) {

		if (asyncTask != null) {
			return threadPool.submit(asyncTask);
		} else {
			return null;
		}
	}

	/**
	 * Método auxiliar que cria um work item para buscar as possíveis próximas infrações.
	 * Atenção: utiliza a function 'fcn_InfracaoDisponivelUsuario', ou seja: não vai travar
	 * a infração para o usuário. 
	 * @param idInfracao
	 * @return
	 */
	private Runnable criaWorkItemBuscaInfracoes() {

		return new Runnable() {

			@Override
			public void run() {
				logger.debug("Buscando de novas infrações em background. Thread [" + Thread.currentThread().hashCode() + "]");
				Connection conn = null;
				List<Integer> novasInfracoes;

				try {
					conn = Conexao.getConexao();
					novasInfracoes = buscaMultiplasInfracoesProcessamentoBD(0, conn);

					synchronized (ProcessamentoMultiplas.this.infracoes) {
						ProcessamentoMultiplas.this.infracoes.clear();
						ProcessamentoMultiplas.this.infracoes.addAll(novasInfracoes);
					}

				} catch (SQLException se) {
					logger.error("Erro na busca assíncrona de imagens (WorkItemBuscaInfrações).", se);
				} catch (ConexaoException ce) {
					logger.error("Erro na busca assíncrona de imagens WorkItemBuscaInfrações(.", ce);
				}
				finally {

					synchronized (ProcessamentoMultiplas.this.buscandoAssincrono) {
						buscandoAssincrono = 0;
					}

					logger.debug("Busca de infrações em background TERMINOU. Thread [" + Thread.currentThread().hashCode() + "]");

					if (conn != null)
					{
						try {
							conn.close();
						} catch (SQLException e) {
							logger.error("Erro na busca assíncrona de infrações (WorkItemBuscaInfrações).", e);
						}
					}
				}
			}
		};
	}		

	/**
	 * Metodo auxiliar (da implementação "BD"), que faz a busca propriamente dita no banco.
	 * @param infracaoAtual
	 * @param alvo
	 * @param iniciar
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	private List<Integer> buscaMultiplasInfracoesProcessamentoBD(Integer qtdeInfracoes, Connection conn) throws SQLException, ConexaoException {
		List<Integer> iRet = new LinkedList<Integer>();

		CallableStatement cs = null;
		boolean ownsConnection = false; 

		try {
			if (conn == null)
			{
				conn = Conexao.getConexao();
				ownsConnection = true;
			}

			if (super.getEtapa().getId() == 90 || super.getEtapa().getId() == 91) {
				cs = conn.prepareCall( 								//    1  2  3  4  5  6  7  8  9  10
						"{call spu_busca_multiplas_infracao_contestacao  (?, ?, ?, ?, ?, ?, ?, ?, ?,  ?)}");
			}
			else {
				cs = conn.prepareCall( 								//    1  2  3  4  5  6  7  8  9  10
						"{call spu_busca_multiplas_infracao_processamento(?, ?, ?, ?, ?, ?, ?, ?, ?,  ?)}");
			}
			int t = 1;
			cs.setInt(t++, super.getIdUsuario()); 		//1
			cs.setInt(t++, super.getEtapa().getId());   //2 

			if(idRemessa != null) {                  	//3
				cs.setInt(t++, idRemessa);
			} else {
				cs.setNull(t++, Types.INTEGER);
			}
			
			if (super.getIdEnquadramento() != null) 	//4
				cs.setInt(t++, super.getIdEnquadramento());
			else
				cs.setNull(t++, Types.INTEGER);

			if (super.getConsistencia() != null) 		//5
				cs.setBoolean(t++, super.getConsistencia());
			else
				cs.setNull(t++, Types.INTEGER);

			if (super.getEspera() != null) 				//6
				cs.setBoolean(t++, super.getEspera());
			else
				cs.setNull(t++, Types.INTEGER);

			if (super.getPeriodoIni() != null && super.getPeriodoFim() != null) {
				cs.setTimestamp(t++, super.getPeriodoIni()); 	//7
				cs.setTimestamp(t++, super.getPeriodoFim()); 	//8
			}
			else {
				cs.setNull(t++, Types.TIMESTAMP); 				//7
				cs.setNull(t++, Types.TIMESTAMP); 				//8
			}

			cs.setInt(t++, qtdeInfracoes == null ? 0 : qtdeInfracoes);  	//9

			cs.setInt(t++, amostra != null && amostra ? 1 : 0); 			//10
			
			cs.execute();
			if(cs.getResultSet() != null){
				while (cs.getResultSet().next()) {
					iRet.add(cs.getResultSet().getInt("id_infracao"));
				}
			}		

		}		
		finally {
			if (ownsConnection && conn != null)
				conn.close();
		}

		return iRet;
	}

	/**
	 * Finaliza o processamento o libera possíveis infrações presas.
	 * @throws SQLException 
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public void finalizaProcessamento() throws ConexaoException, SQLException {
		Processamento.finalizaJanelaProcessamento(super.getIdUsuario(), super.getEtapa() == EtapaProcesso.REMESSA_VALIDADA ? EtapaProcesso.VALIDACAO.getId() : super.getEtapa().getId());
		synchronized (this.infracoes) {
			this.infracoes.clear();
		}
	}

	@Override
	public void doFinaliza(HttpSession sessao) throws Exception {
		finalizaProcessamento();
	}

	@Override
	public Boolean processaDireto(AcaoListener listener) throws ConexaoException, SQLException {

		Connection conn = null;
		boolean bRet = false;
		Integer qtdAgendou = 0;

		try {
			conn = Conexao.getConexao();

			/*
			[spu_agendar_processamento]
				@id_usuario INT,
				@id_processo INT,
				@id_enquadramento INT,
				@consistencia BIT,
				@espera BIT,
				@periodo_ini DATETIME,
				@periodo_fim DATETIME		
			 */			
			final CallableStatement cs = conn.prepareCall("{? = call spu_agendar_processamento(?, ?, ?, ?, ?, ?, ?, ?)}");

			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, super.getIdUsuario());
			cs.setInt(3, super.getEtapa().getId());

			if(super.getIdRemessa() != null)
				cs.setInt(4, super.getIdRemessa());
			else
				cs.setNull(4, Types.INTEGER);
			
			if (super.getIdEnquadramento() != null)
				cs.setInt(5, super.getIdEnquadramento());
			else
				cs.setNull(5, Types.INTEGER);

			if (super.getConsistencia() != null)
				cs.setBoolean(6, super.getConsistencia());
			else
				cs.setNull(6, Types.INTEGER);

			if (super.getEspera() != null)
				cs.setBoolean(7, super.getEspera());
			else
				cs.setNull(7, Types.INTEGER);

			if (super.getPeriodoIni() != null && super.getPeriodoFim() != null) {
				cs.setTimestamp(8, super.getPeriodoIni());
				cs.setTimestamp(9, super.getPeriodoFim());
			}
			else {
				cs.setNull(8, Types.TIMESTAMP);
				cs.setNull(9, Types.TIMESTAMP);
			}

			Thread thExec = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						cs.execute();
					}
					catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			logger.debug("Iniciando Thread de processamento direto...");
			thExec.start();
			
			SQLWarning wnAnt = null;
			SQLWarning wn = null;
			while (thExec.isAlive()) {
				// Se tiver um listener interessado, avisa ele.
				//logger.debug("Thread vivo, verificando warnings...");

				if (wnAnt == null)
					wn = cs.getWarnings();
				else
					wn = wnAnt.getNextWarning();
				
				if (listener != null && wn != null) {
					do {
						logger.debug("Warning detectado ['"+wn.getMessage()+"'] disparando listener...");
						listener.acaoExecutada(new AcaoEvento(this, wn.getMessage()));
						wnAnt = wn;
					} 
					while ((wn = wn.getNextWarning()) != null);
				}
				else {
					listener.acaoExecutada(new AcaoEvento(this, "Qtd. Agendada: "+contarAgendamento()));
				}
				
				try {
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			
			qtdAgendou = cs.getInt(1);
			
			listener.acaoExecutada(new AcaoEvento(this, "Total agendado: "+qtdAgendou));

			bRet = qtdAgendou > 0;
			pushAviso(cs.getWarnings());
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return bRet;
	}
	
	
	private Integer contarAgendamento() throws SQLException, ConexaoException {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT ");
		sbSQL.append("	COUNT(*) ");		
		sbSQL.append(" FROM ");
		sbSQL.append("	agendamento_processamento ap WITH (NOLOCK) ");
		sbSQL.append(" WHERE ");
		sbSQL.append("	ap.id_processo = ?");
		
		int iRet = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, super.getEtapa().getId());
			
			rs = ps.executeQuery();
			if (rs.next())
				iRet = rs.getInt(1);
		}
		finally {
			if (conn != null)
				conn.close();
		}
		return iRet;
	}
	
	/**
	 * Retorna a infração anterior no vetor interno.
	 * @return Identificador da infração ou null se não existir uma disponivel
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	private Integer buscaAnterior(Connection conn) throws ConexaoException, SQLException {
		Integer iRet = 0;
		int iPos = -1;

		synchronized (this.infracoes) {
			iPos = this.infracoes.indexOf(this.infracaoAtual);
			if( iPos > 0 ) {
				iRet = this.infracoes.get(--iPos);
			}
		}
		return iRet;
	}

	private Boolean verificarSePodeProximo(){

		int iPosAtual = -1;
		int iPosLimite = -1;

		synchronized (this.infracoes) {
			iPosAtual = this.infracoes.indexOf(this.infracaoAtual);
			iPosLimite = this.infracoes.indexOf(this.infracaoLimite);
		}

		if ( iPosAtual+1 <= iPosLimite  )
			return true;
		else
			return false;

	}

	/**
	 * Retorna a próxima infração no vetor interno.
	 * @return Identificador da infração ou null se não existir uma disponível
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	private Integer buscaProxima(Connection conn) throws SQLException, ConexaoException {
		Integer iRet = 0;
		int iPos = -1;

		synchronized (this.infracoes) {
			iPos = this.infracoes.indexOf(this.infracaoAtual);
			if (( iPos >= 0 ) && ( iPos + 1 < infracoes.size() )) {
				iRet = this.infracoes.get(++iPos);
			}

			if (iRet > 0) {

				// Se der, ativa o mecanismo de READ - AHEAD
				// Neste caso, não precisa ser assíncrono, pois nós já temos o id da infração
				if ( isBuscaPrefetchImages() && (iPos + 1 < this.infracoes.size()) )
				{
					Integer proximaDaProxima = 0;
					for (int i = 1; i<=5; i++){ // cache das imagens das próximas 5 infrações
						if ( iPos + i < infracoes.size() ) {
							proximaDaProxima = this.infracoes.get(iPos + i);
							if (proximaDaProxima != null && proximaDaProxima.intValue() > 0) {
								List<Integer> listaImagens = Collections.emptyList();
								InfracaoRecenteCache.getInstance().addObjectToCache(proximaDaProxima, listaImagens);
							}
						}
					}
				}

			}
		}

		return iRet;
	}

	/**
	 * Retorna a infração atual no vetor interno.
	 * @return Identificador da infração ou null se não existir uma disponível
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	private Integer buscaAtual(Integer idInfracaoAtual, Connection conn) throws SQLException, ConexaoException {
		Integer iRet = 0;
		Integer iInfracaoRef = (idInfracaoAtual != null ? idInfracaoAtual : this.infracaoAtual);
		Integer iPos = null;

		synchronized (this.infracoes) {
			iPos = this.infracoes.indexOf(iInfracaoRef);
			if( iPos >= 0 ) {
				iRet = this.infracoes.get(iPos);
			}
		}

		return iRet;
	}

}
