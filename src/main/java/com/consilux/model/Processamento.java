/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 17/01/2008

  Descricao: Classe de negócio para processamento

  Historico:

    $Log: Processamento.java,v $
    Revision 1.30  2009/06/10 18:36:02  fos
    Consertado o bug do tempo de processamento.

    Revision 1.29  2009/05/28 14:15:00  fos
    Colocada a função toString novamente que foi retirada por engano na última revisão.

    Revision 1.28  2009/05/28 13:50:54  fos
    Reorganizadas as funções de finalização de processamento para agora ser chamada por um Servlet externo.

    Revision 1.27  2009/05/18 14:29:20  fosinfracoesRecentes
    Agora carrega a infração atual para debug via toString.

    Revision 1.26  2009/05/08 18:54:39  fos
    Agora armazena a espécie para realimentação do cadastro.

    Revision 1.25  2009/04/15 19:50:09  fos
    Agora ele se destroi avisando o banco que o usuário saiu.

    Revision 1.24  2009/03/19 23:05:57  fos
    Agora possui controle para infrações em espera e processamento direto.

    Revision 1.23  2009/03/12 13:07:36  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.22  2009/03/03 21:36:02  fos
    Colocado filtro de consistência no processamento.

    Revision 1.21  2009/02/17 19:05:09  fos
    Criada a etapa de Liberação.

    Revision 1.20  2009/02/06 20:11:53  fos
    Feitos ajustes para filtrar por enquadramento.

    Revision 1.19  2009/01/28 19:26:56  fos
    Colocado log de monitoramento para procedure de processamento.

    Revision 1.18  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.16  2008/10/13 17:14:37  fos
    Renomeada procedure para nomenclatura sp'U'.

    Revision 1.15  2008/09/19 20:57:14  fos
    ASSIGNED - bug 53: Criar regras de passagem entre processos com aviso para o usuário.
    http://bugzilla.consilux.net/show_bug.cgi?id=53

    Revision 1.14  2008/09/17 14:08:25  fos
    Agora as etapas de processamento possui um enumeração classificada por id.

    Revision 1.13  2008/09/03 13:06:36  fos
    ASSIGNED - bug 92: Infração sem imagem
    http://bugzilla.consilux.net/show_bug.cgi?id=92

    Revision 1.12  2008/08/21 21:10:32  fos
    Ajustes de hortografia nos comentários.

    Revision 1.11  2008/08/12 13:01:54  fos
    Agora armazena informação sobre a obliteração.

    Revision 1.10  2008/07/31 21:10:58  fos
    Consertado bug da data negativa.

    Revision 1.9  2008/07/23 14:27:05  fos
    Agora repassa o id da imagem escolhida.

    Revision 1.8  2008/07/10 19:49:29  fos
    Agora passa a data para procedure de infração.

    Revision 1.7  2008/05/09 21:16:04  fos
    Agora faz o processamento da validação corretamente.

    Revision 1.6  2008/05/08 21:30:33  fos
    Agora faz o processo adequado com múltiplas iterações.
    Agora faz a processamento da digitação e validação.
    Agora finaliza o processo.

    Revision 1.5  2008/02/28 18:43:21  fos
    Agora controla o processo pelo BD ou no próprio server.

    Revision 1.4  2008/02/21 21:07:16  fos
    Agora o processamento já navega entras as infrações no banco de dados utilizando as procedures adequadas.

    Revision 1.3  2008/02/18 21:10:46  fos
    Agora a triagem envia a etapa para o processamento.

    Revision 1.2  2008/02/06 19:20:24  fos
    Carga da infração na nova tela funcional.

    Revision 1.1  2008/01/18 17:21:47  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;


import java.awt.Rectangle;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.consilux.infra.AcaoListener;
import com.consilux.infra.SessaoFinaliza;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para processamento
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.30 $ $Date: 2009/06/10 18:36:02 $ $Author: fos $
 */
public abstract class Processamento implements SessaoFinaliza {

	protected final static Logger logger = Logger.getLogger(Processamento.class);

	protected static void logarTempoProcessamento(String mensagem, long inicio) {
		long fim = System.currentTimeMillis();
		logger.debug(mensagem + Long.toString(fim - inicio));
	}	

	public enum EtapaProcesso { 
		TRIAGEM(1),
		DIGITACAO(2),
		VALIDACAO(3),
		REMESSA_GERAL(4),
		REMESSA_VELOCIDADE(7),
		REMESSA_RODIZIO(8),
		REMESSA_ZMRC(9),
		INCONSISTENTES_GERAL(10),
		LIBERACAO(11),
		REMESSA_ZMRF(12),
		REMESSA_AVANCO_SINAL(13),
		REMESSA_PARADA_FAIXA(14),
		REMESSA_RETORNO_PROIBIDO(15),
		IMAGENS_TESTE_PROCESSADAS(16),
		REMESSA_CONVERSAO_PROIBIDA(17),
		REMESSA_FAIXA_EXCLUSIVA(18),
		REMESSA_NAO_CONSERVAR_FAIXA(19),
		FILTRO_TRIAGEM(20),
		FILTRO_DIGITACAO(21),
		FILTRO_VALIDACAO(22),
		IMAGENS_TESTE(23),
		DIGITACAO_SUPERVISOR(24),
		REMESSA_VALIDADA(25),
		CONTESTACAO(90), CONTESTACAO_CAV(91);

		private Integer id;
		EtapaProcesso(Integer id) {
			this.id = id;
		}
		public Integer getId() {
			return id;
		}
		public static EtapaProcesso valueOfId(Integer id) {
			for (EtapaProcesso etapa: values()) {
				if (etapa.getId() == id.intValue())
					return etapa;
			}
			return null;
		}
	}

	public enum Acao {
		CONSISTE,
		INCONSISTE,
		PROXIMO,
		ANTERIOR,
		ATUAL,
		ESPERA,
		PENDENTE
	};

	public enum Alvo {

		ANTERIOR(0),
		PROXIMO(1),
		ATUAL(2),
		PROXIMO_NOVO(3);

		private Integer id;

		Alvo(Integer id) {
			this.id = id;
		}
		public Integer getId() {
			return id;
		}
	}

	protected Integer idUsuario;
	protected EtapaProcesso etapa;
	protected Integer idRemessa;
	protected Boolean amostra;
	protected Integer idEnquadramento;
	protected Boolean consistencia;
	protected Boolean espera;
	protected Date tempoInicio;
	private String placa;
	protected Integer idInconsistencia;
	private Integer idMarcaProcesso;
	private Integer idEspecieProcesso;
	private String ufProcesso;
	private Integer idImagem;
	private String avisos;
	private String mensagens;
	private Boolean bloqueio;
	private Integer idInfracaoProcesso;
	private Long tempo_cliente;
	protected Timestamp periodoIni;
	protected Timestamp periodoFim;
	protected Integer infracaoAtual;
	protected boolean buscaPrefetchImages;
	protected String usuarioDigitado = null;
	protected int codigoUsuarioDigitado = 0;
	protected String observacao = null;
	protected String classificacaoTarja = null;
	protected boolean botaoVoltarPressionado = false;
	protected Integer idInfracaoProcessada = null;

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
	protected Processamento(EtapaProcesso etapa, Integer idUsuario, Integer idRemessa, Boolean amostra, Integer idEnquadramento, Boolean consistencia, Boolean espera, Timestamp periodoIni, Timestamp periodoFim) {
		super();
		this.idRemessa = idRemessa;
		this.amostra = amostra;
		this.idEnquadramento = idEnquadramento;
		this.espera = espera;
		this.consistencia = consistencia;
		this.idUsuario = idUsuario;
		this.etapa = etapa;
		this.periodoIni = periodoIni;
		this.periodoFim = periodoFim;
		this.tempoInicio = new Date();
		this.bloqueio = false;
		this.idInfracaoProcesso = null;
		this.buscaPrefetchImages = true;
	}

	/**
	 * @return Retorna o valor de aviso atual.
	 */
	public String popAviso() {
		String ret = this.avisos;
		this.avisos = null;
		return ret;
	}

	public String popMensagem() {
		String ret = this.mensagens;
		this.mensagens = null;
		return ret;
	}

	/**
	 * @param avisos Novo valor para o atributo aviso.
	 */
	protected void pushAviso(SQLWarning warn) {
		String aviso = "";
		if (warn != null && warn.getErrorCode() > 0) {
			aviso = warn.getMessage();
			while ((warn = warn.getNextWarning()) != null && warn.getErrorCode() > 0) {
				aviso += "\n"+warn.getMessage();
			}
			this.avisos = (this.avisos == null ? "" : this.avisos) + aviso + '\n';
		}
	}

	protected void pushMensagem(String mensagem) {
		this.mensagens = (this.mensagens == null ? "" : this.mensagens) + mensagem + '\n';
	}

	/**
	 * Realiza o processamento das infrações de forma automática.
	 * @param idInfracao Identificador da infração
	 * @return 'True' se o processo foi concluído com sucesso, 'False' se não foi possível concluir.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public Boolean processaDireto() throws ConexaoException, SQLException {
		return processaDireto(null);
	}

	/**
	 * Realiza o processamento das infrações de forma automática.
	 * @param idInfracao Identificador da infração
	 * @return 'True' se o processo foi concluído com sucesso, 'False' se não foi possível concluir.
	 * @throws ConexaoException
	 * @throws SQLException
	 */	
	public abstract Boolean processaDireto(AcaoListener listener) throws ConexaoException, SQLException;


	/**
	 * Marca uma infração com consistente/inconsistente.
	 * @return 'True' se o processo foi concluído com sucesso, 'False' se não foi possível concluir. 
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws ModelException 
	 */
	public Boolean processa() throws ConexaoException, SQLException, ModelException {
		Integer idInfracaoProcesso = 0;
		Boolean bRet = false;

//		if (idInconsistencia != null && idInconsistencia > 0)
//			this.placa = null;

		long inicio = System.currentTimeMillis();
//		this.setIdInfracaoProcessada(this.infracaoAtual);

		//Inserindo dados no banco...
		switch (this.etapa) {
		case TRIAGEM:
			idInfracaoProcesso = processaInfracao();
			break;
		case DIGITACAO:
			idInfracaoProcesso = processaInfracaoComDigitacao();
			break;
		case VALIDACAO:
			idInfracaoProcesso = processaInfracaoComDigitacao();
			break;
		case REMESSA_VALIDADA:
			idInfracaoProcesso = processaInfracaoComDigitacao();
			break;
		case IMAGENS_TESTE:
			idInfracaoProcesso = processaInfracaoComDigitacao();
			break;
		case DIGITACAO_SUPERVISOR:
			idInfracaoProcesso = processaInfracaoComDigitacao();
			break;
			/// XXX: TODO: ADICIONAR CONTESTACAO
		case CONTESTACAO:
			idInfracaoProcesso = processaInfracaoContestacao();
			break;
		case CONTESTACAO_CAV:
			idInfracaoProcesso = processaInfracaoContestacao();
			break;
		default:
			throw new ModelException("Processo desconhecido para precessamento.");
		}

		if (idInfracaoProcesso == 0)
			throw new ModelException("Não foi possível gravar informações no banco de dados.");
		else
			this.idInfracaoProcesso = idInfracaoProcesso;

		logger.info("TempoProcessar InfracaoProcesso " + (new Date().getTime() - inicio));

		/* Quando a validaInfracaoProcesso retornar um código bloqueante, o usuário
		 * será obrigado a trocar o estado de processamento.
		 * Já no caso de um retorno não bloqueante, será processada normalmente, porêm
		 * irá permitir o usuário mudar de opinião se quiser. Mais é importante afirmar
		 * que se o usuário decidir mudar de opinião e acabar saindo antes de alterar os
		 * dados, será considerado pelo sistema a última opinião do usuário.
		 */
		
		inicio = System.currentTimeMillis();
		
		if (validaInfracaoProcesso(idInfracaoProcesso)) {
			this.bloqueio = false;
			bRet = concluiInfracaoProcesso(idInfracaoProcesso);
		}
		else
			this.bloqueio = true;

		logger.info("TempoProcessar validaInfracaoProcesso " + (new Date().getTime() - inicio));
		return bRet;
	}

	private Integer processaInfracaoContestacao() throws ConexaoException, SQLException {
		Integer iRet = 0;

		Connection conn = null;
		CallableStatement cs = null;

		try {
			//		testar();
			// XXX: FELIPE
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{? = call spu_processa_infracao_contestacao(?, ?, ?, ?)}"
			);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, this.infracaoAtual);
			cs.setInt(3, this.idUsuario);
			cs.setInt(4, this.etapa.getId());
			cs.setInt(5, this.idInconsistencia);
					
			cs.execute();
			iRet = cs.getInt(1);
			pushAviso(cs.getWarnings());
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return iRet;
	}

	/**
	 * Conclui o processamento, pois está tudo ok.
	 * @param idInfracaoProcesso Identificador do registro que deve ser validado.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	private Boolean concluiInfracaoProcesso(Integer idInfracaoProcesso) throws ConexaoException, SQLException {
		boolean bRet = false;
		long inicio = System.currentTimeMillis();
		
		Connection conn = null;
		CallableStatement cs = null;

		try {
			//		testar();
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{? = call spu_conclui_infracao_processo(?, ?)}"
			);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, idInfracaoProcesso);
			cs.setInt(3, (int)(new Date().getTime() - this.tempoInicio.getTime())); //diferença é em milisegundos. 		
			cs.execute();
			bRet = cs.getInt(1) > 0;
			pushAviso(cs.getWarnings());

			logger.debug("[TEMPO] conclui_infracao: " + (new Date().getTime() - inicio));
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return bRet;
	}

	/**
	 * Reliza testes de consistência nos dados do processamento.
	 * @param idInfracaoProcesso Identificador do registro que deve ser validado.
	 * @return 'True' se é permitido a conclusão do processamento, 'False' se o processamento não pode ser concluído.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	private boolean validaInfracaoProcesso(Integer idInfracaoProcesso) throws ConexaoException, SQLException, ModelException {
		boolean bRet = false;

		Connection conn = null;
		CallableStatement cs = null;

		try {
			//		testar();
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{? = call spu_valida_infracao_processo(?)}"
			);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, idInfracaoProcesso);

			ResultSet rs = cs.executeQuery();

			if (rs == null) {
				throw new ModelException("Não foi possível capturar as mensagens de retorno na validação");
			}
			else {
				while (rs.next())
					pushMensagem(rs.getString(1));
			}

			bRet = cs.getInt(1) > 0;

		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return bRet;
	}

	/**
	 * Marca uma infração como espera.
	 * @param idInfracao Identificador da infração a ser processada.
	 * @return 'True' se o processo foi concluído com sucesso, 'False' se não foi possível concluir. 
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public Boolean espera(Integer idInfracao) throws ConexaoException, SQLException {
		boolean bRet = false;
		long inicio = System.currentTimeMillis();

		bRet = esperaProcesso(idInfracao);

		if (bRet) {
			this.idImagem = null;
			this.placa = null;
			this.idMarcaProcesso = null;
			this.idEspecieProcesso = null;
			this.ufProcesso = null;
			this.tempo_cliente = null;
		}

		logarTempoProcessamento("[TEMPO] processamento: ", inicio);
		return bRet;
	}

	/**
	 * Retorna uma infração disponível para processamento.
	 * @param conn uma conexão com o banco de dados, ou nulo (para o GTW escolher uma).
	 * @return Identificador da infração adequada.
	 * @throws SQLException
	 * @throws ConexaoException
	 * @see #buscaInfracao(Integer, Boolean)
	 */
	public Integer buscaInfracao(Connection conn) throws SQLException, ConexaoException {

		return buscaInfracao(null, Alvo.ATUAL, conn);
	}
	
	public boolean processaObliteracao(Integer idInfracaoProcesso, Integer idImagem, Integer sequenciaOliteracao, Rectangle obliteracao) throws ConexaoException, SQLException, ModelException {
		boolean bRet = false;

		Connection conn = null;
		CallableStatement cs = null;

		try {
			//		testar();
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{call spu_processa_obliteracao(?, ?, ?, ?, ?, ?, ?)}"
			);

			cs.setInt(1, idInfracaoProcesso);
			cs.setInt(2, idImagem);
			cs.setInt(3, sequenciaOliteracao);
			cs.setInt(4, obliteracao.x);
			cs.setInt(5, obliteracao.y);
			cs.setInt(6, obliteracao.width);
			cs.setInt(7, obliteracao.height);

			cs.execute();
			
			pushAviso(cs.getWarnings());
			
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return bRet;
	}
	

	/**
	 * Retorna uma infração disponível para processamento.
	 * @param iInfracaoAtual Identificador da infração atual a ser utilizada como referência.
	 * @param conn uma conexão com o banco de dados, ou nulo (para o GTW escolher uma). 
	 * @return Identificador da infração adequada.
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public Integer buscaInfracao(Integer iInfracaoAtual, Connection conn) throws SQLException, ConexaoException {
		return buscaInfracao(iInfracaoAtual, Alvo.PROXIMO_NOVO, conn);
	}

	/**
	 * Retorna uma infração disponível para processamento.
	 * @param iInfracaoAtual Identificador da infração atual a ser utilizada como referência.
	 * @param alvo Indica que ação de busca deve ser realizada.
	 * 
	 * @return Identificador da infração adequada.
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public abstract Integer buscaInfracao(Integer iInfracaoAtual, Alvo alvo, Connection conn) throws SQLException, ConexaoException;

	/**
	 * iniciaProcessamento
	 * @param etapa
	 * @param idUsuario
	 * @param idEnquadramento
	 * @param consistencia
	 * @param espera
	 * @param periodoIni
	 * @param periodoFim
	 * @return
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public static Processamento iniciaProcessamento(EtapaProcesso etapa, Integer idUsuario, Integer idRemessa, Boolean amostra, Integer idEnquadramento, Boolean consistencia, Boolean espera, Timestamp periodoIni, Timestamp periodoFim) throws InterruptedException, ExecutionException, TimeoutException, SQLException, ConexaoException {
		return new ProcessamentoMultiplas(etapa, idUsuario, idRemessa, amostra, idEnquadramento, consistencia, espera, periodoIni, periodoFim);
	}

	/**
	 * @param etapa Identificador da etapa que o processamento deve controlar.
	 * @param idUsuario Identificador do usuário que esta processando as infrações.
	 * @param idEnquadramento Identificador do enquadramento das infrações a serem processados, ou NULL para todas.
	 * @param consistencia Se 'True' somente consistentes, se 'False' somente inconsistentes, ou NULL para todas.
	 * @param espera Se 'True' somente esperas, se 'False' somente não-esperas, ou NULL para todas.
	 * @param periodoIni Data inicial ser selecionada.
	 * @param periodoFim Data final a ser selecionada.
	 * @param infracoes Lista de infrações previamente selecionadas pelo usuário.
	 * @return
	 */
	public static Processamento iniciaProcessamento(EtapaProcesso etapa, Integer idUsuario, Integer idRemessa, Boolean amostra, Integer idEnquadramento, Boolean consistencia, Boolean espera, Timestamp periodoIni, Timestamp periodoFim, List<Integer> infracoes) {
		return new ProcessamentoAplicacao(etapa, idUsuario, idRemessa, amostra, idEnquadramento, consistencia, espera, periodoIni, periodoFim, infracoes);
	}

	/**
	 * Finaliza o processamento o libera possível infrações presas.
	 */
	public abstract void finalizaProcessamento() throws SQLException, ConexaoException;

	/**
	 * @return Retorna o valor de idUsuario atual.
	 */
	public Integer getIdUsuario() {
		return idUsuario;
	}

	/**
	 * @return Retorna o valor de etapa atual.
	 */
	public EtapaProcesso getEtapa() {
		return etapa;
	}

	public Integer getIdRemessa() {
		return idRemessa;
	}
	/**
	 * @return Retorna o valor do enquadramento atual.
	 */
	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}

	/**
	 * @return Retorna o valor do consistencia atual.
	 */
	public Boolean getConsistencia() {
		return consistencia;
	}

	public Boolean getEspera() {
		return espera;
	}

	/**
	 * Retorna o valor do campo 'periodoIni' atual.
	 * @return the periodoIni
	 */
	public Timestamp getPeriodoIni() {
		return this.periodoIni;
	}

	/**
	 * Retorna o valor do campo 'periodoFim' atual.
	 * @return the periodoFim
	 */
	public Timestamp getPeriodoFim() {
		return this.periodoFim;
	}

	/**
	 * @param placa Novo valor para o atributo placa.
	 */
	public void setPlaca(String placa) {
		this.placa = placa;
	}

	/**
	 * Ajusta o valor do campo 'idInconsistencia' no objeto.
	 * @param idInconsistencia the idInconsistencia to set
	 */
	public void setIdInconsistencia(Integer idInconsistencia) {
		this.idInconsistencia = idInconsistencia;
	}

	/**
	 * Retorna o valor do campo 'idInconsistencia' atual.
	 * @return the idInconsistencia
	 */
	public Integer getIdInconsistencia() {
		return this.idInconsistencia;
	}

	/**
	 * @param idMarcaProcesso
	 */
	public void setIdMarcaProcesso(Integer idMarcaProcesso) {
		this.idMarcaProcesso = idMarcaProcesso;
	}

	/**
	 * @param idEspecieProcesso
	 */
	public void setIdEspecieProcesso(Integer idEspecieProcesso) {
		this.idEspecieProcesso = idEspecieProcesso;
	}

	/**
	 * Ajusta o valor do campo 'ufProcesso' no objeto.
	 * @param ufProcesso the ufProcesso to set
	 */
	public void setUfProcesso(String ufProcesso) {
		this.ufProcesso = ufProcesso;
	}

	/**
	 * @return Retorna o valor de idMarcaProcesso atual.
	 */
	public Integer getIdMarcaProcesso() {
		return idMarcaProcesso;
	}

	/**
	 * @return Retorna o valor de idEspecieProcesso atual.
	 */
	public Integer getIdEspecieProcesso() {
		return idEspecieProcesso;
	}
	
	/**
	 * @return the tempo_cliente
	 */
	public long getTempoCliente() {
		return tempo_cliente;
	}

	/**
	 * @param tempoCliente the tempo to set
	 */
	public void setTempoCliente(long tempoCliente) {
		this.tempo_cliente = tempoCliente;
	}

	public String getUsuarioDigitado() {
		return usuarioDigitado;
	}

	public void setUsuarioDigitado(String usuarioDigitado) {
		this.usuarioDigitado = usuarioDigitado;
	}

	public int getCodigoUsuarioDigitado() {
		return codigoUsuarioDigitado;
	}

	public void setCodigoUsuarioDigitado(int codigoUsuarioDigitado) {
		this.codigoUsuarioDigitado = codigoUsuarioDigitado;
	}
	
	/**
	 * Marca uma infração como espera.
	 * @param idInfracao Identificador da infração a ser processada.
	 * @return 'True' se o processo foi concluído com sucesso, 'False' se não foi possível concluir. 
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	private Boolean esperaProcesso(Integer idInfracao) throws ConexaoException, SQLException {
		boolean bRet = false;

		Connection conn = null;
		CallableStatement cs = null;

		try {

			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{call spu_espera_infracao(?, ?, ?)}"
			);
			cs.setInt(1, idInfracao);
			cs.setInt(2, this.idUsuario);
			cs.setInt(3, this.etapa.getId());
			bRet = cs.execute();

			pushAviso(cs.getWarnings());
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return bRet;
	}

	/**
	 * Grava o processamento de uma infração.
	 * @return Identificador do registro gravado. 
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	private Integer processaInfracao() throws ConexaoException, SQLException {
		Integer iRet = 0;
		java.sql.Date dataProc = new java.sql.Date(new Date().getTime());

		Connection conn = null;
		CallableStatement cs = null;

		try {
			//		testar();
			// XXX: FELIPE
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{? = call spu_processa_infracao(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"
			);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, this.infracaoAtual);
			cs.setInt(3, this.idUsuario);
			cs.setInt(4, this.etapa == EtapaProcesso.REMESSA_VALIDADA ? EtapaProcesso.VALIDACAO.getId() : this.etapa.getId());
			cs.setInt(5, this.idInconsistencia);
			if (idImagem != null)
				cs.setInt(6, idImagem);
			else
				cs.setNull(6, Types.INTEGER);
/*			if (this.obliteracao != null) {
				cs.setInt(7, this.obliteracao.x);
				cs.setInt(8, this.obliteracao.y);
				cs.setInt(9, this.obliteracao.width);
				cs.setInt(10, this.obliteracao.height);
			}
			else {*/
				cs.setNull(7, Types.INTEGER);
				cs.setNull(8, Types.INTEGER);
				cs.setNull(9, Types.INTEGER);
				cs.setNull(10, Types.INTEGER);
//			}
			cs.setInt(11, (int)(dataProc.getTime() - this.tempoInicio.getTime())); //diferença é em milisegundos.
			if (this.tempo_cliente != null)
				cs.setInt(12, Integer.parseInt(String.valueOf(this.tempo_cliente))); //Tempo cliente.
			else
				cs.setNull(12, Types.INTEGER);

//			cs.setInt(13, codigoUsuarioDigitado);
//			if(usuarioDigitado != null)
//				cs.setString(14, usuarioDigitado);
//			else
				cs.setNull(13, Types.VARCHAR);
				cs.setNull(14, Types.VARCHAR);
			
			cs.execute();
			iRet = cs.getInt(1);
			pushAviso(cs.getWarnings());
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return iRet;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	/**
	 * Grava o processamento de uma infração.
	 * @return 'True' se o processo foi concluido com sucesso, 'False' se não foi possível concluir. 
	 * @throws ConexaoException
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	private Integer processaInfracaoComDigitacao() throws ConexaoException, SQLException, ModelException {
		Integer iRet = 0;
		java.sql.Date dataProc = new java.sql.Date(new Date().getTime());

		Connection conn = null;
		CallableStatement cs = null;

		// XXX: FELIPE
		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
				//    1                                            2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20   
					"{? = call spu_processa_infracao_com_digitacao(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"
			);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, this.infracaoAtual);
			cs.setInt(3, this.idUsuario);
			cs.setInt(4, this.etapa == EtapaProcesso.REMESSA_VALIDADA ? EtapaProcesso.VALIDACAO.getId() : this.etapa.getId());
			cs.setInt(5, this.idInconsistencia);
			if (idImagem != null)
				cs.setInt(6, idImagem);
			else
				cs.setNull(6, Types.INTEGER);
/*			if (this.obliteracao != null) {
				cs.setInt(7, this.obliteracao.x);
				cs.setInt(8, this.obliteracao.y);
				cs.setInt(9, this.obliteracao.width);
				cs.setInt(10, this.obliteracao.height);
			}
			else {*/
				cs.setNull(7, Types.INTEGER);
				cs.setNull(8, Types.INTEGER);
				cs.setNull(9, Types.INTEGER);
				cs.setNull(10, Types.INTEGER);
//			}
			if (idInconsistencia == 0 && (placa == null || placa.trim().length() == 0))
				throw new ModelException("Placa nula!");
			
			if (idInconsistencia > 0 && (placa != null && placa.trim().length() == 0)) {
				this.placa = null;
			}

			if (this.placa != null)
				cs.setString(11, this.placa);
			else
				cs.setNull(11, Types.CHAR);
			
			if (this.idMarcaProcesso != null)
				cs.setInt(12, this.idMarcaProcesso);
			else
				cs.setNull(12, Types.INTEGER);
			
			if (this.idEspecieProcesso != null)
				cs.setInt(13, this.idEspecieProcesso);
			else
				cs.setNull(13, Types.INTEGER);
			
			if (this.ufProcesso != null)
				cs.setString(14, this.ufProcesso);
			else
				cs.setNull(14, Types.CHAR);
			
			cs.setInt(15, (int)(dataProc.getTime() - this.tempoInicio.getTime())); //diferença é em milisegundos.
			
			if (this.tempo_cliente != null)
				cs.setInt(16, Integer.parseInt(String.valueOf(this.tempo_cliente))); //Tempo cliente. 		
			else
				cs.setNull(16, Types.INTEGER);
			
//			cs.setInt(17, codigoUsuarioDigitado);
//			if(usuarioDigitado != null)
//				cs.setString(18, usuarioDigitado);
//			else
				cs.setNull(17, Types.VARCHAR);
				cs.setNull(18, Types.VARCHAR);
			
//			logger.info(cs.toString());
				
			
				if(observacao != null && observacao.trim().length() > 0)
					cs.setString(19, observacao);
				else
					cs.setNull(19, Types.VARCHAR);
				
			
			if(classificacaoTarja != null && classificacaoTarja.trim().length() > 0)
				cs.setString(20, classificacaoTarja);
			else
				cs.setNull(20, Types.VARCHAR);
				
			cs.execute();
			pushAviso(cs.getWarnings());
			iRet = cs.getInt(1);
		}		
		finally {
			if (conn != null)
				conn.close();							
		}		
		return iRet;
	}

	/**
	 * @return Retorna o valor de idImagem atual.
	 */
	public Integer getIdImagem() {
		return idImagem;
	}

	/**
	 * @param idImagem Novo valor para o atributo idImagem.
	 */
	public void setIdImagem(Integer idImagem) {
		this.idImagem = idImagem;
	}

	public Boolean getBloqueio() {
		return bloqueio;
	}

	/**
	 * @return the idInfracaoProcesso
	 */
	public Integer getIdInfracaoProcesso() {
		return idInfracaoProcesso;
	}

	/**
	 * Finaliza um processamento no banco de dados.
	 * @param idUsuario
	 * @param etapa
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public static void finalizaJanelaProcessamento(Integer idUsuario, Integer idProcesso) throws ConexaoException, SQLException {

		Connection conn = null;
		CallableStatement cs = null;

		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{call spu_ajusta_janela(?, ?, 0, 0)}"
			);
			cs.setInt(1, idUsuario);
			cs.setInt(2, idProcesso);

			cs.execute();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
	}

	@Override
	public String toString() {
		return super.toString()+" [Processo: "+this.etapa+", Infracao atual: "+this.infracaoAtual+"]";
	}

	/**
	 * Retorna se a infração passada é a infração atual.
	 * @param idInfracao
	 * @return
	 */
	public boolean verifInfracaoAtual(int idInfracao) {
		return idInfracao == this.infracaoAtual;
	}

	/**
	 * Ajusta todas as variáveis do sistema para o processamento da infração dada.
	 * @param iRet
	 */
	protected void iniciaInfracao(Integer iInfracao) {
		iniciaInfracao(iInfracao, new Date());
	}

	/**
	 * Ajusta todas as variáveis do sistema para o processamento da infração dada.
	 * @param iRet
	 */
	protected void iniciaInfracao(Integer iInfracao, Date tempoIni) {
		this.infracaoAtual = iInfracao;
		this.tempoInicio = tempoIni;
		this.idImagem = null;
		this.tempo_cliente = null;
	}

	public final boolean isBuscaPrefetchImages()  {
		return buscaPrefetchImages;
	}

	public static Boolean isPrimeiraDataDoProcesso(EtapaProcesso p, Integer idEnquadramento, Boolean consistente, Date data) throws ConexaoException, SQLException {
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("IF EXISTS(SELECT 1 FROM infracao (NOLOCK) ");
		sbSQL.append("WHERE id_processo = ? ");
		
		if (idEnquadramento != null) {
			sbSQL.append(" AND id_enquadramento = " + String.valueOf(idEnquadramento));
		}
		if (consistente != null) {
			sbSQL.append(consistente ? " AND id_inconsistencia = 0 " : " AND id_inconsistencia > 0 ");
		}
		
		sbSQL.append(" AND data < ?) SELECT 1 ELSE SELECT 0 ");

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			//Ajustando os valores dos parametros:
			ps.setInt(1, p.getId());
			ps.setTimestamp(2, (Timestamp) data);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				bRet = rs.getInt(1) == 1 ? false : true;
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return bRet;
	}

	
	public String getClassificacaoTarja() {
		return classificacaoTarja;
	}

	public void setClassificacaoTarja(String classificacaoTarja) {
		this.classificacaoTarja = classificacaoTarja;
	}

	public Integer getIdInfracaoProcessada() {
		return idInfracaoProcessada;
	}

	public void setIdInfracaoProcessada(Integer idInfracaoProcessada) {
		if(!botaoVoltarPressionado) {
			this.idInfracaoProcessada = idInfracaoProcessada;
		}
		logger.debug("Infracao Atual = " + this.idInfracaoProcessada + " ; Infracao Processada = " + idInfracaoProcessada + " ; Botao Voltar Pressionado = " + botaoVoltarPressionado);
	}

	public boolean isBotaoVoltarPressionado() {
		return botaoVoltarPressionado;
	}

	public void setBotaoVoltarPressionado(boolean botaoVoltarPressionado) {
		this.botaoVoltarPressionado = botaoVoltarPressionado;
	}

	public Integer getInfracaoAtual() {
		return infracaoAtual;
	}

	public void setInfracaoAtual(Integer infracaoAtual) {
		this.infracaoAtual = infracaoAtual;
	}
	
	
}