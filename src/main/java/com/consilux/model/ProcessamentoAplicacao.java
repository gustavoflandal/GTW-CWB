package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.consilux.infra.AcaoEvento;
import com.consilux.infra.AcaoListener;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Implementação da classe de processamento, focada na aplicação.
 * @author fos
 */
class ProcessamentoAplicacao extends Processamento {

	private List<Integer> infracoes; 

	/**
	 * Constrói o objeto Processamento com os seus respectivos atributos.
	 * @param etapa Identificador da etapa que o processamento deve controlar.
	 * @param idUsuario Identificador do usuário que esta processando as infrações.
	 * @param idEnquadramento Identificador do enquadramento das infrações a serem processados, ou NULL para todas.
	 * @param infracoes Lista de infrações previamente selecionadas pelo usuário.
	 */
	protected ProcessamentoAplicacao(EtapaProcesso etapa, Integer idUsuario, Integer idRemessa, Boolean amostra, Integer idEnquadramento, Boolean consistencia, Boolean espera, Timestamp periodoIni, Timestamp periodoFim, List<Integer> infracoes) {
		super(etapa, idUsuario, idRemessa, amostra, idEnquadramento, consistencia, espera, periodoIni, periodoFim);

		assert infracoes.size() > 0;

		this.infracoes = infracoes;
		iniciaInfracao(infracoes.get(0));
	}


	public Boolean processaDireto(AcaoListener listener) throws ConexaoException, SQLException {

		boolean bRet = false;
		Connection conn = null;
		Integer idInfracao;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO agendamento_processamento ");
		sbSQL.append(" (id_infracao, id_processo, data_requisicao, id_usuario, status_agendamento, id_inconsistencia) ");
		sbSQL.append(" VALUES (?,?,?,?,?,?) ");

		// Armazena o estado atual, a respeito do mecanismo de prefecth 
		boolean wasBuscaPrefetchImages = isBuscaPrefetchImages();

		boolean wasAutoCommit = false;

		try {
			// A data de início desta operação (a data da requisição).
			Timestamp dataRequisicao = new Timestamp(System.currentTimeMillis());

			// Como é um agendamento, vamos desligar o prefetch.
			buscaPrefetchImages = false;

			// Pega uma conexão do pool
			conn = Conexao.getConexao();

			// Desliga o auto-commit (mas armazena como estava antes disso).
			wasAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			// Statement que insere a infração no agendamento
			PreparedStatement psInsereAgendamento = conn.prepareStatement(sbSQL.toString());

			// Statement que marca a infração como "bloqueada".
			PreparedStatement psBloqueiaInfracao = conn.prepareStatement("UPDATE infracao SET status_bloqueio = 1 WHERE id_infracao = ?");

			// Busca a primeira infração
			idInfracao = buscaInfracao(conn);
			while (idInfracao > 0) {

				// Ajusta os parâmetros
				psInsereAgendamento.setInt(1, this.infracaoAtual);	// id_infracao
				psInsereAgendamento.setInt(2, this.etapa.getId());	// id_processo
				psInsereAgendamento.setTimestamp(3, dataRequisicao); // data_requisicao
				psInsereAgendamento.setInt(4, this.idUsuario);		// id_usuario
				psInsereAgendamento.setInt(5, 0);					// status_agendamento

				// id_inconsistencia
				if (super.idInconsistencia != null)
					psInsereAgendamento.setInt(6, super.idInconsistencia);
				else
					psInsereAgendamento.setNull(6, Types.INTEGER);

				psBloqueiaInfracao.setInt(1, this.infracaoAtual);

				// Dispara para o banco.
				psInsereAgendamento.executeUpdate();
				psBloqueiaInfracao.executeUpdate();				

				// Se tiver um listener interessado, avisa ele.
				if (listener != null)
					listener.acaoExecutada(new AcaoEvento(this, 1));

				// Busca a próxima
				idInfracao = buscaInfracao(idInfracao, conn);
			}

			// Faz o commit
			conn.commit();
			bRet = true;

		} catch (ConexaoException ce) {
			conn.rollback();
			logger.error("Erro de conexão ao agendar infrações para processamento.", ce);
			throw ce;
		} catch (SQLException se) {
			conn.rollback();
			logger.error("Erro de SQL ao agendar infrações para processamento.", se);
			throw se;
		} catch (Exception ex) {
			conn.rollback();
			logger.error("Erro inesperado ao agendar infrações para processamento.", ex);
		} 		
		finally {
			if (conn != null)
			{
				// Restaura o auto-commit para o que estava antes.
				conn.setAutoCommit(wasAutoCommit);
				// Fecha a conexão
				conn.close();
			}
			// Restaura a flag a respeito do prefetch.
			buscaPrefetchImages = wasBuscaPrefetchImages;
		}

		return bRet;
	}	


	@Override
	public Integer buscaInfracao(Integer iInfracaoAtual, Alvo alvo, Connection conn) throws SQLException, ConexaoException {
		Date tempoIni = new Date();
		Integer iRet = 0;

		switch (alvo) {
		case ANTERIOR:
			iRet = buscaAnterior(conn);
			break;
		case PROXIMO:
			iRet = buscaProxima(conn);
			break;
		case ATUAL:
			iRet = buscaAtual(iInfracaoAtual, conn);
			break;
		case PROXIMO_NOVO:
			iRet = buscaProxima(conn);
			break;
		}

		if (iRet > 0)
			iniciaInfracao(iRet, tempoIni);

		return iRet;
	}

	/**
	 * Finaliza o processamento o libera possível infrações presas.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public void finalizaProcessamento() throws SQLException, ConexaoException {
		finalizaProcessamentoBD();
		this.infracoes.clear();
	}

	/**
	 * Tenta buscar no banco de dados a infração dada se disponivel.
	 * @param iInfracao Identificador da infração a ser buscada no banco.
	 * @return O mesmo identificador passado, ou 0 se a infração não esta disponivel.
	 * @throws SQLException
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	private Integer buscaInfracaoBD(Integer iInfracao, Connection conn) throws ConexaoException, SQLException {
		Integer iRet = 0;

		assert iInfracao != null; 
		assert iInfracao > 0;

		CallableStatement cs = null;

		boolean ownsConnection = false; 
		
		try {
			if (conn == null)
			{
				conn = Conexao.getConexao();
				ownsConnection = true;
			}

			if(idRemessa != null) {
				cs = conn.prepareCall(
			  "{? = call spu_busca_infracao_processamento_validacao(?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?)}"
						);
			} else {
				cs = conn.prepareCall(
						"{? = call spu_busca_infracao_processamento(?, ?, ?, ?, ?, ?, ?, ? ,?)}"
						);
			}
			int t = 1;
			cs.registerOutParameter(t++, java.sql.Types.INTEGER);
			cs.setInt(t++, super.getIdUsuario());
			cs.setInt(t++, super.getEtapa().getId());

			if(idRemessa != null) {
				cs.setInt(t++, idRemessa);
			}
			
			if (super.getIdEnquadramento() != null)
				cs.setInt(t++, super.getIdEnquadramento());
			else
				cs.setNull(t++, Types.INTEGER);

			if (super.getConsistencia() != null)
				cs.setBoolean(t++, super.getConsistencia());
			else
				cs.setNull(t++, Types.INTEGER);

			if (super.getEspera() != null)
				cs.setBoolean(t++, super.getEspera());
			else
				cs.setNull(t++, Types.INTEGER);

			if (super.getPeriodoIni() != null && super.getPeriodoFim() != null) {
				cs.setTimestamp(t++, super.getPeriodoIni());
				cs.setTimestamp(t++, super.getPeriodoFim());
			}
			else {
				cs.setNull(t++, Types.TIMESTAMP);
				cs.setNull(t++, Types.TIMESTAMP);
			}

			cs.setInt(t++, iInfracao);
			cs.setInt(t++, 2); //2: Atual.

			if(idRemessa != null)
				cs.setInt(t++, amostra != null && amostra ? 1 : 0);
			
			cs.execute(); 
			iRet = cs.getInt(1);
		}		
		finally {
			if (ownsConnection && conn != null)
				conn.close();							
		}		
		return iRet;
	}

	/**
	 * Desmarca as infração no banco de dados.
	 * @throws SQLException
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	private void finalizaProcessamentoBD() throws ConexaoException, SQLException {
		Processamento.finalizaJanelaProcessamento(super.getIdUsuario(), super.getEtapa() == EtapaProcesso.REMESSA_VALIDADA ? EtapaProcesso.VALIDACAO.getId() : super.getEtapa().getId());
	}

	/**
	 * Retorna a infração anterior no vetor interno.
	 * @return Identificador da infração ou null se não existir uma disponivel
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	private Integer buscaAnterior(Connection conn) throws ConexaoException, SQLException {
		Integer iRet = 0;
		Integer iInfracaoRef = null;
		Integer iPos = null;

		iPos = this.infracoes.indexOf(this.infracaoAtual);

		while (iPos > 0) { //Vai retornando nas infrações até encontrar uma disponível.
			iInfracaoRef = this.infracoes.get(--iPos);
			iRet = buscaInfracaoBD(iInfracaoRef, conn);
			if (iRet > 0) {
				break;
			}
		}

		return iRet;
	}

	/**
	 * Retorna a próxima infração no vetor interno.
	 * @return Identificador da infração ou null se não existir uma disponível
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	private Integer buscaProxima(Connection conn) throws SQLException, ConexaoException {
		Integer iRet = 0;
		Integer iInfracaoRef = null;
		Integer iPos = null;

		iPos = this.infracoes.indexOf(this.infracaoAtual);

		while (iPos < (infracoes.size() - 1)) { //Vai passando nas infrações até encontrar uma disponível.
			iInfracaoRef = this.infracoes.get(++iPos);
			iRet = buscaInfracaoBD(iInfracaoRef, conn);

			if (iRet > 0) {

				// Se der, ativa o mecanismo de READ - AHEAD
				// Neste caso, não precisa ser assíncrono, pois nós já temos o id da infração
				if (isBuscaPrefetchImages() && iPos + 1 <= this.infracoes.size() - 1)
				{
					Integer proximaDaProxima = this.infracoes.get(iPos + 1);
					if (proximaDaProxima != null && proximaDaProxima > 0) {
						List<Integer> listaImagens = Collections.emptyList();
						InfracaoRecenteCache.getInstance().addObjectToCache(proximaDaProxima, listaImagens);
					}
				}

				// Quebra o laço. O fato é: achou uma infração. 
				break;
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

		iPos = this.infracoes.indexOf(iInfracaoRef);

		while (iPos < (infracoes.size())) { //Primeiro tenta a atual, depois vai passando nas infrações até encontrar uma disponível.
			iInfracaoRef = this.infracoes.get(iPos++);
			iRet = buscaInfracaoBD(iInfracaoRef, conn);
			if (iRet > 0)
				break;
		}

		return iRet;
	}

	@Override
	public void doFinaliza(HttpSession sessao) throws Exception {
		finalizaProcessamento();
	}

}

