package com.consilux.model.remessa;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Enquadramento;
import com.consilux.model.EnquadramentoRegraInfracao;
import com.consilux.model.Inconsistencia;
import com.consilux.model.RemessaAutomatico;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.Remessa;
import com.consilux.model.exception.ModelException;

/**
 * Classe abstrata base para o isolamento da geração da Remessa
 * @author raoni
 */
public abstract class RemessaFactory {

	/**
	 * Método que deve ser implementado pelas classes filhas.
	 * É responsável por definir um CallableStatement para a criação da remessa.
	 * A única exigência é que o primeiro parametro do CallableStatement seja
	 * registrado como "out" e seja um int.
	 * @return um CallableStatement que será executado ao criar uma remessa.
	 * @throws ConexaoException 
	 */
	public abstract CallableStatement getGeraRemessaStatement(Connection conn, Enquadramento enquadramento, EtapaProcesso etapaProcesso, Date dataInicial,
			Date dataFinal, Date dataRemessa, boolean residual, int infracoes, int id_usuario, Inconsistencia inconsistencia, Integer idRemessaAutomatico)
		throws ModelException, SQLException, ConfiguracaoException, ConexaoException;
	
	public abstract CallableStatement getGeraRemessaAutomaticoStatement(Connection conn, EtapaProcesso etapaProcesso, Date dataInicial, Date dataFinal,
			Date dataRemessa, int infracoesPorLote, int id_usuario)
			throws ModelException, SQLException, ConfiguracaoException, ConexaoException;
	
	public final String getTipoRemessa(Integer id_enquadramento) throws ConexaoException, SQLException {
		Map<Integer, String> tipos_apait = EnquadramentoRegraInfracao.ObterTiposAPAIT();
		return tipos_apait.get(id_enquadramento);
	}
	
	/**
	 * Implementação básica que recupera o tipo da remessa.
	 * @return uma string contendo o código da remessa, baseado na
	 * sua configuração no confGTW.xml.
	 * @param etapaProcesso 
	 * @throws ConfiguracaoException
	 */
	@SuppressWarnings("incomplete-switch")
	public final String getTipoRemessa(EtapaProcesso etapaProcesso) throws ConfiguracaoException
	{
		String sRet = null;
		Configuracao conf = ConfiguracaoProvider.getInstance();
		
		switch (etapaProcesso) {
			case REMESSA_VELOCIDADE:
				sRet = conf.getTipoRemessaVELOCIDADE();
				break;
			case REMESSA_RODIZIO:
				sRet = conf.getTipoRemessaRODIZIO();
				break;
			case REMESSA_ZMRC:
				sRet = conf.getTipoRemessaZMRC();
				break;
			case REMESSA_ZMRF:
				sRet = conf.getTipoRemessaZMRF();
				break;
			case REMESSA_CONVERSAO_PROIBIDA:
				sRet = conf.getTipoRemessaConversao();
				break;
			case REMESSA_RETORNO_PROIBIDO:
				sRet = conf.getTipoRemessaRetorno();
				break;
			case REMESSA_AVANCO_SINAL:
				sRet = conf.getTipoRemessaAvancoSinal();
				break;
			case REMESSA_PARADA_FAIXA:
				sRet = conf.getTipoRemessaParadaFaixa();
				break;
			case REMESSA_FAIXA_EXCLUSIVA:
				sRet = conf.getTipoRemessaFaixaExclusiva();
				break;
			case REMESSA_NAO_CONSERVAR_FAIXA:
				sRet = conf.getTipoRemessaNaoConservarFaixa();
				break;
			case REMESSA_GERAL:
				sRet = conf.getTipoRemessaGeral();
				break;
		}
		return sRet;		
	}
	
	/**
	 * Não faço idéia para que serve este método.
	 * @param tipoRemessa
	 * @return
	 * @throws ModelException
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public final Integer buscaCodigoExterno(Connection conn, String tipoRemessa) throws ModelException, SQLException {
		
		if (tipoRemessa == null)
			throw new ModelException("Argumento nulo: tipoRemessa");
		
		Integer iRet = null;
		String sSQL = "SELECT MAX(CAST(codigo_externo AS INT)) FROM remessa WHERE tipo = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;
		
		ps = conn.prepareStatement(sSQL);
		ps.setString(1, tipoRemessa);
		rs = ps.executeQuery();
		
		if (rs.next()) {
			iRet = rs.getInt(1) + 1;
		}
		
		return iRet;
	}	
	
	/**
	 * Método reponsável por gerar uma remessa. 
	 * @return
	 * @throws ModelException
	 * @throws ConexaoException
	 * @throws ConfiguracaoException
	 * @throws SQLException
	 */
	public final Remessa gerarRemessa(Enquadramento enquadramento, EtapaProcesso etapaProcesso, Date dataInicial, Date dataFinal, Date dataRemessa, boolean residual, int infracoes, int id_usuario, Inconsistencia inconsistencia, Integer idRemessaAutomatico)
	throws ModelException, ConexaoException, ConfiguracaoException, SQLException
	{
	
		Remessa ret = null;
		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			conn = Conexao.getConexao();
			
			if(Remessa.validarRemessaZerada(enquadramento.getIdEnquadramento(), dataInicial, dataFinal)){
				cs = getGeraRemessaStatement(conn, enquadramento, etapaProcesso, dataInicial, dataFinal, dataRemessa, residual, infracoes, id_usuario, inconsistencia, idRemessaAutomatico);
				cs.execute();
				ret = Remessa.buscarRemessaPorId(cs.getInt(1));
			}
		}
		finally {
			if (cs != null)
				cs.close();
			if (conn != null)
				conn.close();
		}
		return ret;		
	}

	
	/**
	 * Método reponsável por gerar uma remessa. 
	 * @return
	 * @throws ModelException
	 * @throws ConexaoException
	 * @throws ConfiguracaoException
	 * @throws SQLException
	 */
	public final Boolean gerarRemessaAutomatico(EtapaProcesso etapaProcesso, Date dataInicial, Date dataFinal, Date dataRemessa, int infracoesPorLote, int id_usuario)
	throws ModelException, ConexaoException, ConfiguracaoException, SQLException
	{
	
		Boolean bRet = false;
		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			conn = Conexao.getConexao();
			
			if(RemessaAutomatico.validarRemessaAutomatico(dataInicial, dataFinal) && RemessaAutomatico.validarRemessaZerada(dataInicial, dataFinal)){
				cs = getGeraRemessaAutomaticoStatement(conn,  etapaProcesso, dataInicial, dataFinal, dataRemessa, infracoesPorLote, id_usuario);
				cs.execute();
				bRet = cs.getInt(1) > 0;
			}
		}
		finally {
			if (cs != null)
				cs.close();
			if (conn != null)
				conn.close();
		}
		return bRet;		
	}
	
}
