package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
 * Implementação da classe de processamento, focada em banco de dados.
 * @author fos
 */
public class ProcessamentoBD extends Processamento {

	/**
	 * Constrói o objeto Processamento com os seus respectivos atributos.
	 * @param etapa Etapa qual o processo esta.
	 * @param idUsuario Identificador do usuário que esta operando.
	 * @param idEnquadramento Identificador do enquadramento das infrações a serem processados, ou NULL para todas.
	 * @param consistencia Se 'True' somente consistentes, se 'False' somente inconsistentes, ou NULL para todas.
	 * @param espera Se 'True' somente esperas, se 'False' somente não-esperas, ou NULL para todas.
	 * @param periodoIni Data inicial ser selecionada.
	 * @param periodoFim Data final a ser selecionada.
	 */
	protected ProcessamentoBD(EtapaProcesso etapa, Integer idUsuario, Integer idRemessa, Boolean amostra, Integer idEnquadramento, Boolean consistencia, Boolean espera, Timestamp periodoIni, Timestamp periodoFim) {
		super(etapa, idUsuario, idRemessa, amostra, idEnquadramento, consistencia, espera, periodoIni, periodoFim);
	}

	@Override
	public Integer buscaInfracao(Integer infracaoAtual, Alvo alvo, Connection conn) throws SQLException, ConexaoException {

		Date tempoIni = new Date();

		final Integer iRet = buscaInfracaoProcessamentoBD(infracaoAtual, alvo, true, conn);

		// Ativando o mecanismo de READ-AHEAD...

		if (isBuscaPrefetchImages() && ((iRet > 0 && alvo == Alvo.PROXIMO_NOVO) || (infracaoAtual == null && alvo == Alvo.ATUAL))) {
			logger.debug("Ativando o mecanismo de READ-AHEAD. thread" + Thread.currentThread().hashCode() + "]");

			// Cria um work item (que vai realizar a busca no banco e preparar o retorno) 
			Runnable asyncTask = criaWorkItemReadAhead(etapa.getId(), idUsuario, consistencia, espera, idEnquadramento, periodoIni, periodoFim);

			// Dispara na threadPool
			InfracaoRecenteCache.getInstance().submitTask(asyncTask);			
		}

		// Loga a duração da operação.
		logarTempoProcessamento("[TEMPO] busca_infracao: ", tempoIni.getTime());

		logger.debug("Retornando infração; usuário=" + 
				super.getIdUsuario() + ", origem=" + infracaoAtual + ", ret=" + iRet);

		return iRet; 

	}

	/**
	 * Método auxiliar que cria um work item para buscar as possíveis próximas infrações.
	 * Atenção: utiliza a function 'fcn_InfracaoDisponivelUsuario', ou seja: não vai travar
	 * a infração para o usuário. 
	 * @param idInfracao
	 * @return
	 */
	private Runnable criaWorkItemReadAhead(final Integer idProcesso, final Integer idUsuario, final Boolean consistente, final Boolean espera,
			final Integer idEnquadramento, final Timestamp periodoIni, final Timestamp periodoFim) {

		return new Runnable() {

			@Override
			public void run() {
				logger.debug("WorkItemReadAhead. thread [" + Thread.currentThread().hashCode() + "]");
				Connection conn = null;

				try {
					StringBuilder sbSQL = new StringBuilder();
					sbSQL.append("SELECT TOP 3 id_infracao FROM fcn_InfracaoDisponivelUsuario(?,?)");
					sbSQL.append("	WHERE data IS NOT NULL ");
					sbSQL.append((idEnquadramento != null ? "AND id_enquadramento = ? " : ""));
					sbSQL.append((espera != null && espera ? "AND espera = 1 " : "AND espera IS NULL "));
					sbSQL.append((consistente != null && consistente ? "AND id_inconsistencia = 0 " : ""));
					sbSQL.append((consistente != null && !consistente ? "AND id_inconsistencia > 0 " : ""));
					sbSQL.append((periodoIni != null && periodoFim != null ? "AND data BETWEEN ? AND ? " : ""));
					sbSQL.append("ORDER BY CAST(data AS date), id_local, pista, data");					

					conn = Conexao.getConexao();
					PreparedStatement ps = conn.prepareStatement(sbSQL.toString());

					// Ajusta so parâmetros
					ps.setInt(1, idProcesso);
					ps.setInt(2, idUsuario);

					Integer iParam = 3;

					if (idEnquadramento != null)
						ps.setInt(iParam++, idEnquadramento);

					if (periodoIni != null && periodoFim != null) {
						ps.setTimestamp(iParam++, periodoIni);
						ps.setTimestamp(iParam++, periodoFim);
					}					

					ResultSet rs = ps.executeQuery();

					// Cria uma lista vazia de imagens (pois neste momento buscamos apenas as infrações).
					List<Integer> listaImagens = Collections.emptyList();

					while (rs.next()) {
						InfracaoRecenteCache.getInstance().addObjectToCache(rs.getInt("id_infracao"), listaImagens);
					}
				} catch (SQLException se) {
					logger.error("Erro na busca assíncrona de imagens (WorkItemReadAhead).", se);
				} catch (ConexaoException ce) {
					logger.error("Erro na busca assíncrona de imagens WorkItemReadAhead(.", ce);
				}
				finally {
					if (conn != null)
					{
						try {
							conn.close();
						} catch (SQLException e) {
							logger.error("Erro na busca assíncrona de imagens (WorkItemReadAhead).", e);
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
	private Integer buscaInfracaoProcessamentoBD(Integer infracaoAtual, Alvo alvo, boolean iniciar, Connection conn) throws SQLException, ConexaoException {
		Integer iRet = null;

//		logger.debug("buscaInfracaoProcessamentoBD(" + infracaoAtual + ", " + alvo + ", " + iniciar + ", " + conn + ")");
		
		CallableStatement cs = null;
		boolean ownsConnection = false; 

		Date tempoIni = new Date();
		
		try {
			if (conn == null)
			{
				conn = Conexao.getConexao();
				ownsConnection = true;
			}

			if(idRemessa != null) {
				cs = conn.prepareCall( 							//  2  3  4  5  6  7  8  9 10 11 12  
			  "{? = call spu_busca_infracao_processamento_validacao(?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?)}"
						);
			} else {
				cs = conn.prepareCall( 							//  2  3  4  5  6  7  8  9 10 
						"{? = call spu_busca_infracao_processamento(?, ?, ?, ?, ?, ?, ?, ?, ?)}"
						);
			}
			int t = 1;
			cs.registerOutParameter(t++, java.sql.Types.INTEGER); 	//1
			cs.setInt(t++, super.getIdUsuario()); 					//2
			cs.setInt(t++, super.getEtapa().getId()); 				//3

			if(idRemessa != null) {
				cs.setInt(t++, idRemessa); 							//4
			}
			
			if (super.getIdEnquadramento() != null)
				cs.setInt(t++, super.getIdEnquadramento()); 		//4/5
			else
				cs.setNull(t++, Types.INTEGER); 					//4/5

			if (super.getConsistencia() != null)
				cs.setBoolean(t++, super.getConsistencia()); 		//5/6
			else
				cs.setNull(t++, Types.INTEGER); 					//5/6

			if (super.getEspera() != null)
				cs.setBoolean(t++, super.getEspera()); 				//6/7
			else
				cs.setNull(t++, Types.INTEGER); 					//6/7

			if (super.getPeriodoIni() != null && super.getPeriodoFim() != null) {
				cs.setTimestamp(t++, super.getPeriodoIni()); 		//7/8
				cs.setTimestamp(t++, super.getPeriodoFim()); 		//8/9
			}
			else {
				cs.setNull(t++, Types.TIMESTAMP); 					//7/8
				cs.setNull(t++, Types.TIMESTAMP); 					//8/9
			}

			cs.setInt(t++, infracaoAtual == null ? 0 : infracaoAtual); //9/10

			switch (alvo) {
			case ANTERIOR:
				cs.setInt(t++, 0); //10/11
				break;
			case PROXIMO:
				cs.setInt(t++, 1); //10/11
				break;
			case ATUAL:
				cs.setInt(t++, 2); //10/11
				break;
			case PROXIMO_NOVO:
				cs.setInt(t++, 3); //10/11
				break;
			}
			
			if(idRemessa != null)
				cs.setInt(t++, amostra != null && amostra ? 1 : 0); //12

			cs.execute(); 
			iRet = cs.getInt(1); //1
		}		
		finally {
			if (ownsConnection && conn != null)
				conn.close();
		}
		if (iRet > 0 && iniciar)
			iniciaInfracao(iRet, tempoIni);

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
	}

	@Override
	public void doFinaliza(HttpSession sessao) throws Exception {
		finalizaProcessamento();
	}

	@Override
	public Boolean processaDireto(AcaoListener listener) throws ConexaoException, SQLException {

		CallableStatement cs = null;
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
				@periodo_fim DATETIME,
				@id_inconsistencia INT = NULL
			 */			
			cs = conn.prepareCall("{? = call spu_agendar_processamento(?, ?, ?, ?, ?, ?, ?, ?, ?)}");

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
				cs.setNull(7, Types.BOOLEAN);

			if (super.getPeriodoIni() != null && super.getPeriodoFim() != null) {
				cs.setTimestamp(8, super.getPeriodoIni());
				cs.setTimestamp(9, super.getPeriodoFim());
			}
			else {
				cs.setNull(8, Types.TIMESTAMP);
				cs.setNull(9, Types.TIMESTAMP);
			}

			// id_inconsistencia
			if (super.idInconsistencia != null)
				cs.setInt(10, super.idInconsistencia);
			else
				cs.setNull(10, Types.INTEGER);			

			cs.execute();
			qtdAgendou = cs.getInt(1);

			// Se tiver um listener interessado, avisa ele.
			if (listener != null)
				listener.acaoExecutada(new AcaoEvento(this, qtdAgendou));			

			bRet = qtdAgendou > 0;
			pushAviso(cs.getWarnings());
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return bRet;
	}
}
