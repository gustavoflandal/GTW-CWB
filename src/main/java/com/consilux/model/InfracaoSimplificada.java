/**********************************************************************************

 Projeto: GTW
 Nome do Modulo: GTW

 Empresa: Consilux Tecnologia

 Autor: Fernando de Souza
 Data: 17/01/2008

 Descricao: Classe responsável para listar as infrações, tendo apenas os dados necessarios

 Historico:

   $Log: InfracaoSimplificada.java,v $
   Revision 1.19  2009/05/19 11:33:45  fos
   Agora mostra as infrações em espera, mesmo sendo as últimas infrações digitadas do operador.

   Revision 1.18  2009/05/08 18:53:42  fos
   Colocado nolock na query lenta.

   Revision 1.17  2009/03/19 23:05:02  fos
   Agora possui controle para infrações em espera.

   Revision 1.16  2009/03/12 13:07:38  raoni
   Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
   Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
   Modificado de StringBuilder para StringBuffrer.
   Utilizando append do StringBuillder.
   Utilizando ArrayList.

   Revision 1.15  2009/03/10 14:35:46  raoni
   Otimizado o uso de StringBuilder.
   Utiliza LinkedList quando possível.
   Adicionado lógica para realizar o clean up (close) dos Statements.

   Revision 1.14  2009/03/03 21:36:03  fos
   Colocado filtro de consistência no processamento.

   Revision 1.13  2009/02/26 17:22:19  fos
   Agora possui limit dinâmico do número de linhas retornadas.

   Revision 1.12  2009/02/06 20:14:28  fos
   Feitos ajustes para filtrar por enquadramento.

   Revision 1.11  2009/01/28 19:25:14  fos
   Agora a tabela infracao carrega os atributos de data e local.

   Revision 1.10  2009/01/16 13:59:10  fos
   Faltou ligar uma parte da chave do local.

   Revision 1.9  2009/01/12 12:49:42  fos
   Recuperação de repositório.

   Revision 1.7  2008/10/13 17:11:55  fos
   Agora respeita também a seqüência do local.

   Revision 1.6  2008/09/17 14:08:01  fos
   Agora as etapadas de processamento possui um enumeração classificada por id.

   Revision 1.5  2008/08/29 20:52:00  fos
   Renomeada métodos e atributos para enquadramento.

   Revision 1.4  2008/08/21 21:09:14  fos
   Identificador do equipamento alterado para identificador do local.

   Revision 1.3  2008/05/09 21:15:34  fos
   Aceratado ordem da query para busca da infração.

   Revision 1.2  2008/05/08 21:28:22  fos
   Adequado o nome do método.

   Revision 1.1  2008/02/06 19:20:25  fos
   Carga da infração na nova tela funcional.

   Revision 1.1  2008/01/17 18:44:27  fernando
   Versão Inicial


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 *
 * @author Fernando de Souza - Consilux Tecnologia
 * @version $Revision: 1.19 $ $Date: 2009/05/19 11:33:45 $ $Author: fos $
 */
public class InfracaoSimplificada {
	private Integer idInfracao;
	private Date dataInfracao;
	private Integer idEnquadramento;
	private Integer idLocal;
	private String local;

//	private static int PREFETCH_SIZE = 10;

	/**
	 * Constrói o objeto InfracaoSimplificada com os seus respectivos atributos.
	 * @param idInfracao Identificador da infração.
	 * @param data_infracao Data da infração
	 * @param idEnquadramento Código do enquadramento da infração.
	 * @param idLocal Identificador do local.
	 * @param local Nome do local.
	 */
	private InfracaoSimplificada(Integer idInfracao, Date data_infracao, Integer idEnquadramento,
			Integer idLocal, String local) {
		super();
		this.idInfracao = idInfracao;
		this.dataInfracao = data_infracao;
		this.idEnquadramento = idEnquadramento;
		this.idLocal = idLocal;
		this.local = local != null ? local.trim() : null;
	}

	/**
	 * Busca uma infração no BD.
	 * @param iInfracao Identificador da infração
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException 
	 * @throws SQLException
	 */
	public static InfracaoSimplificada buscaInfracaoPorId(Integer id_infracao) throws ConexaoException, SQLException {

		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT top 1 "); 
		sbSQL.append("	i.id_infracao, ");
		sbSQL.append("	i.data, ");
		sbSQL.append("	i.id_enquadramento, ");
		sbSQL.append("	i.id_local, ");
		sbSQL.append("	COALESCE(NULLIF(RTRIM(cp.nome_pista),''), l.nome) AS nome_pista");	 
		sbSQL.append(" FROM 	infracao i ");
		sbSQL.append(" LEFT JOIN local l on i.id_local = l.id_local AND i.sequencia_local = l.sequencia_local ");
		sbSQL.append(" LEFT JOIN configuracao_equipamento_pista cp on cp.id_configuracao_equipamento = l.id_configuracao_equipamento AND cp.id_pista = i.pista ");
		sbSQL.append(" WHERE ");
		sbSQL.append("		id_infracao = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());				
			ps.setInt(1, id_infracao);

			rs = ps.executeQuery();
			if (rs.next()) {
				return new InfracaoSimplificada(
						rs.getInt("id_infracao"), 
						rs.getTimestamp("data") ,
						rs.getInt("id_enquadramento"),
						rs.getInt("id_local"),
						rs.getString("nome_pista") 
				);
			}
			else {
				return null;
			}

		}		
		finally {
			if (conn != null)
				conn.close();							
		}

	}

	public static List<InfracaoSimplificada> buscaInfracaoPorEtapaProcessoUsuario(Integer id_processo, Integer idUsuario, Integer idEnquadramento, Boolean consistente, Integer numReg, Boolean espera, Timestamp periodoIni, Timestamp periodoFim, List<String> infracoes) throws ConexaoException, SQLException {
		return buscaInfracaoPorEtapaProcessoUsuario(id_processo, idUsuario, idEnquadramento, consistente, numReg, espera, periodoIni, periodoFim, infracoes, (Integer)null, false);
	}
	
	public static List<InfracaoSimplificada> buscaInfracaoPorEtapaProcessoUsuario(Integer id_processo, Integer idUsuario, Integer idEnquadramento, Boolean consistente, Integer numReg, Boolean espera, Timestamp periodoIni, Timestamp periodoFim, List<String> infracoes, Integer idRemessa, Boolean amostra) throws ConexaoException, SQLException {
		return buscaInfracaoPorEtapaProcessoUsuario(id_processo, idUsuario, idEnquadramento, consistente, numReg, espera, periodoIni, periodoFim, null, infracoes, idRemessa, amostra);
	}

	public static List<InfracaoSimplificada> buscaInfracaoPorEtapaProcessoUsuario(Integer id_processo, Integer idUsuario, Integer idEnquadramento, Boolean consistente, Integer numReg, Boolean espera, Timestamp periodoIni, Timestamp periodoFim, Integer numImagens, List<String> infracoes) throws ConexaoException, SQLException {
		return buscaInfracaoPorEtapaProcessoUsuario(id_processo, idUsuario, idEnquadramento, consistente, numReg, espera, periodoIni, periodoFim, numImagens, infracoes, (Integer)null, false);
	}
	
	public static List<InfracaoSimplificada> buscaInfracaoContestacao(Integer id_processo, Timestamp periodoIni, Timestamp periodoFim, Integer numReg) throws SQLException, ConexaoException {
		List<InfracaoSimplificada> lRet = new ArrayList<InfracaoSimplificada>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT ");
		if (numReg != null) {				
			sbSQL.append("top ");
			sbSQL.append(String.valueOf(numReg));					
		}
		sbSQL.append("	* FROM (");
		sbSQL.append("	SELECT");
		sbSQL.append("		idu.id_infracao, ");
		sbSQL.append("		idu.data, ");
		sbSQL.append("		idu.id_enquadramento, ");
		sbSQL.append("		idu.id_local, ");
		sbSQL.append("		idu.pista, ");
		sbSQL.append("      idu.id_remessa, ");
		sbSQL.append("		idu.id_inconsistencia, ");
		sbSQL.append("		idu.espera, ");
		sbSQL.append("		idu.nome_pista, ");
		sbSQL.append("      idu.id_infracao_amostra ");
		sbSQL.append("	FROM fcn_InfracaoDisponivelContestacao(?) idu ");
		sbSQL.append(") t");
		sbSQL.append((periodoIni != null && periodoFim != null ? " WHERE data BETWEEN ? AND ? " : ""));
		sbSQL.append(" ORDER BY data");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			Integer iParam = 1;
			
			if (id_processo == 90)
				ps.setInt(iParam++, 1);
			if (id_processo == 91)
				ps.setInt(iParam++, 2);
			
			if (periodoIni != null && periodoFim != null) {
				ps.setTimestamp(iParam++, periodoIni);
				ps.setTimestamp(iParam++, periodoFim);
			}

			rs = ps.executeQuery();

			while (rs.next()) {

				int idInfracao = rs.getInt("id_infracao");

				lRet.add(new InfracaoSimplificada(
						idInfracao, 
						rs.getTimestamp("data") ,
						rs.getInt("id_enquadramento"),
						rs.getInt("id_local"),
						rs.getString("nome_pista") 
				));
			}

		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return lRet;
	}
	
	public static List<InfracaoSimplificada> buscaInfracaoPorEtapaProcessoUsuario(Integer id_processo, Integer idUsuario, Integer idEnquadramento, Boolean consistente, Integer numReg, Boolean espera, Timestamp periodoIni, Timestamp periodoFim, Integer numImagens, List<String> infracoes, Integer idRemessa, Boolean amostra) throws ConexaoException, SQLException {
		
		List<InfracaoSimplificada> lRet = new ArrayList<InfracaoSimplificada>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT ");
		if (numReg != null) {				
			sbSQL.append("top ");
			sbSQL.append(String.valueOf(numReg));					
		}
		sbSQL.append("	* FROM (");
		sbSQL.append("	SELECT");
		sbSQL.append("		idu.id_infracao, ");
		sbSQL.append("		idu.data, ");
		sbSQL.append("		idu.id_enquadramento, ");
		sbSQL.append("		idu.id_local, ");
		sbSQL.append("		idu.pista, ");
		sbSQL.append("      idu.id_remessa, ");
		sbSQL.append("		idu.id_inconsistencia, ");
		sbSQL.append("		idu.espera, ");
		sbSQL.append("		idu.nome_pista, ");
		sbSQL.append("      idu.id_infracao_amostra ");
		sbSQL.append((numImagens != null ? "		,Rank() over (Partition BY idu.id_local, idu.pista, cast(idu.data as date), (datepart(hour, idu.data) / (24/"+numImagens+")) order by idu.data ASC) as Rank" : ""));	 
		
		if (idUsuario != null)
			sbSQL.append("	FROM fcn_InfracaoDisponivelUsuario(" + id_processo + ", " + idUsuario +") idu ");
		else 
			sbSQL.append("	FROM fcn_InfracaoDisponivelUsuario(" + id_processo + ", NULL) idu ");
		
		sbSQL.append(") t");
		sbSQL.append("	WHERE data IS NOT NULL ");
		sbSQL.append((idEnquadramento != null ? "AND id_enquadramento = ? " : ""));
		sbSQL.append(espera != null ? (espera ? "AND espera = 1 " : "AND espera IS NULL ") : "");
		sbSQL.append(amostra != null && amostra ? " AND id_infracao_amostra IS NOT NULL " : "");
		sbSQL.append(idRemessa != null ? " AND id_remessa = " + idRemessa + " " : "");
		sbSQL.append((consistente != null && consistente ? "AND id_inconsistencia = 0 " : ""));
		sbSQL.append((consistente != null && !consistente ? "AND id_inconsistencia > 0 " : ""));
		sbSQL.append((periodoIni != null && periodoFim != null ? "AND data BETWEEN ? AND ? " : ""));
		sbSQL.append((numImagens != null ? "AND Rank = 1" : ""));
		sbSQL.append((infracoes != null && infracoes.size() > 0 ? "AND id_infracao in ("+Funcoes.concatStringArray(infracoes, ",")+")" : ""));
		sbSQL.append("ORDER BY data");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

//			if (id_processo!=null){
//				ps.setInt(1, id_processo);
//			} else {
//				ps.setNull(1, Types.INTEGER);
//			}
//
//			if (idUsuario != null){
//				ps.setInt(2, idUsuario);
//			}
//			else{
//				ps.setNull(2, Types.INTEGER);
//			}

			Integer iParam = 1;
			
			if (idEnquadramento != null)
				ps.setInt(iParam++, idEnquadramento);
			
			if (periodoIni != null && periodoFim != null) {
				ps.setTimestamp(iParam++, periodoIni);
				ps.setTimestamp(iParam++, periodoFim);
			}

			rs = ps.executeQuery();

			// Cria uma lista para fazer prefetch das imagens
			//			List<Integer> listaPrefetch = new ArrayList<Integer>(0);

			while (rs.next()) {

				int idInfracao = rs.getInt("id_infracao");

				//				//  Coloca um limite no tamanho do prefetch, senão vai ter que buscar muita coisa.
				//				if (listaPrefetch.size() < PREFETCH_SIZE) {
				//					listaPrefetch.add(idInfracao);
				//				}

				lRet.add(new InfracaoSimplificada(
						idInfracao, 
						rs.getTimestamp("data") ,
						rs.getInt("id_enquadramento"),
						rs.getInt("id_local"),
						rs.getString("nome_pista") 
				));
			}

			//			VeiculoImagem.prefetchImagensInfracao(listaPrefetch);

		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return lRet;
	}

	public static Integer contaInfracoesPorEtapaProcessoUsuario(Integer id_processo, Integer idUsuario, Integer idEnquadramento, Boolean consistente, Timestamp periodoIni, Timestamp periodoFim) throws ConexaoException, SQLException  {
		return contaInfracoesPorEtapaProcessoUsuario(id_processo, idUsuario, idEnquadramento, consistente, periodoIni, periodoFim, (Integer)null, false);
	}
	
	public static Integer contaInfracoesPorEtapaProcessoUsuario(Integer id_processo, Integer idUsuario, Integer idEnquadramento, Boolean consistente, Timestamp periodoIni, Timestamp periodoFim, Integer idRemessa, Boolean amostra) throws ConexaoException, SQLException  {
		Integer iRet = 0; 
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT "); 
		sbSQL.append("	COUNT(id_infracao) as total_infracoes ");
		
		if (idUsuario != null)
			sbSQL.append("	FROM fcn_InfracaoDisponivelUsuario(" + id_processo + ", " + idUsuario +") idu ");
		else 
			sbSQL.append("	FROM fcn_InfracaoDisponivelUsuario(" + id_processo + ", NULL) idu ");
		
		sbSQL.append("	WHERE data IS NOT NULL ");
		sbSQL.append((idEnquadramento != null ? "AND id_enquadramento = ? " : ""));
		sbSQL.append(idRemessa != null ? " AND id_remessa = " + idRemessa + " " : "");
		sbSQL.append(amostra ? " AND id_infracao_amostra IS NOT NULL " : "");
		sbSQL.append((consistente != null && consistente ? "AND id_inconsistencia = 0 " : ""));
		sbSQL.append((consistente != null && !consistente ? "AND id_inconsistencia > 0 " : ""));
		sbSQL.append((periodoIni != null && periodoFim != null ? "AND data BETWEEN ? AND ? " : ""));
//		sbSQL.append(" OPTION (MAXDOP 1) ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

//			if (id_processo != null){
//				ps.setInt(1, id_processo);
//			}else{
//				ps.setNull(1, Types.INTEGER);
//			}
//
//			if ( idUsuario != null){
//				ps.setInt(2, idUsuario);
//			} else {
//				ps.setNull(2, Types.INTEGER);
//			}
			
			Integer iParam = 1;
			if (idEnquadramento != null)
				ps.setInt(iParam++, idEnquadramento);
			if (periodoIni != null && periodoFim != null) {
				ps.setTimestamp(iParam++, periodoIni);
				ps.setTimestamp(iParam++, periodoFim);
			}

			rs = ps.executeQuery();
			if (rs.next()) {
				iRet = rs.getInt("total_infracoes");
			}
		}		
		finally {
				if (conn != null)
					conn.close();							
		}		
		return iRet;
	}

	public static Integer contaInfracoesPorEtapaProcessoUsuario(Integer id_processo, Integer idUsuario, Integer idEnquadramento, Boolean consistente, Timestamp periodoIni, Timestamp periodoFim, List<String> infracoes) throws ConexaoException, SQLException  {
		if(infracoes == null)
			return contaInfracoesPorEtapaProcessoUsuario(id_processo, idUsuario, idEnquadramento, consistente, periodoIni, periodoFim);
		else
			return contaInfracoesPorEtapaProcessoUsuario(id_processo, idUsuario, idEnquadramento, consistente, periodoIni, periodoFim, null, infracoes);
	}

	public static Integer contaInfracoesPorEtapaProcessoUsuario(Integer id_processo, Integer idUsuario, Integer idEnquadramento, Boolean consistente, Timestamp periodoIni, Timestamp periodoFim, Integer numImagens, List<String> infracoes) throws ConexaoException, SQLException  {
		return contaInfracoesPorEtapaProcessoUsuario(id_processo, idUsuario, idEnquadramento, consistente, periodoIni, periodoFim, numImagens, infracoes, null, false);
	}
	
	public static Integer contaInfracoesPorEtapaProcessoUsuario(Integer id_processo, Integer idUsuario, Integer idEnquadramento, Boolean consistente, Timestamp periodoIni, Timestamp periodoFim, Integer numImagens, List<String> infracoes, Integer idRemessa, Boolean amostra) throws ConexaoException, SQLException  {
		Integer iRet = 0; 
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT "); 
		sbSQL.append("	Count(id_infracao) as total_infracoes FROM (");
		sbSQL.append("	SELECT");
		sbSQL.append("		idu.id_infracao, ");
		sbSQL.append("		idu.data, ");
		sbSQL.append("		idu.id_enquadramento, ");
		sbSQL.append("		idu.id_local, ");
		sbSQL.append("		idu.pista, ");
		sbSQL.append("      idu.id_remessa, ");
		sbSQL.append("		idu.id_inconsistencia, ");
		sbSQL.append("		idu.espera, ");
		sbSQL.append("      idu.id_infracao_amostra ");
		sbSQL.append((numImagens != null ? "		,Rank() over (Partition BY idu.id_local, idu.pista, cast(idu.data as date), (datepart(hour, idu.data) / (24/"+numImagens+")) order by idu.data ASC) as Rank" : ""));	 
		
		if (idUsuario != null)
			sbSQL.append("	FROM fcn_InfracaoDisponivelUsuario(" + id_processo + ", " + idUsuario +") idu ");
		else 
			sbSQL.append("	FROM fcn_InfracaoDisponivelUsuario(" + id_processo + ", NULL) idu ");
		
		sbSQL.append(") t");
		sbSQL.append("	WHERE data IS NOT NULL ");
		sbSQL.append((idEnquadramento != null ? "AND id_enquadramento = ? " : ""));
		sbSQL.append(idRemessa != null ? "AND id_remessa = " + idRemessa + " " : "");
		sbSQL.append(amostra ? " AND id_infracao_amostra IS NOT NULL " : "");
		sbSQL.append((consistente != null && consistente ? "AND id_inconsistencia = 0 " : ""));
		sbSQL.append((consistente != null && !consistente ? "AND id_inconsistencia > 0 " : ""));
		sbSQL.append((periodoIni != null && periodoFim != null ? "AND data BETWEEN ? AND ? " : ""));
		sbSQL.append((numImagens != null ? "AND Rank = 1" : ""));
		sbSQL.append((infracoes != null && infracoes.size() > 0 ? "AND id_infracao in ("+Funcoes.concatStringArray(infracoes, ",")+")" : ""));

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

//			if (id_processo != null){
//				ps.setInt(1, id_processo);
//			}else{
//				ps.setNull(1, Types.INTEGER);
//			}
//
//			if ( idUsuario != null){
//				ps.setInt(2, idUsuario);
//			} else {
//				ps.setNull(2, Types.INTEGER);
//			}
			
			Integer iParam = 1;
			if (idEnquadramento != null)
				ps.setInt(iParam++, idEnquadramento);
			if (periodoIni != null && periodoFim != null) {
				ps.setTimestamp(iParam++, periodoIni);
				ps.setTimestamp(iParam++, periodoFim);
			}

			rs = ps.executeQuery();
			if (rs.next()) {
				iRet = rs.getInt("total_infracoes");
			}
		}		
		finally {
				if (conn != null)
					conn.close();							
		}		
		return iRet;
	}


	/**
	 * @return Retorna o valor de id_infracao atual.
	 */
	public Integer getId() {
		return idInfracao;
	}

	/**
	 * @return Retorna o valor de data_infracao atual.
	 */
	public Date getData() {
		return dataInfracao;
	}

	/**
	 * @return Retorna o valor de idEnquadramento atual.
	 */
	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}

	/**
	 * @return Retorna o valor de id_local atual.
	 */
	public Integer getIdLocal() {
		return idLocal;
	}

	/**
	 * @return Retorna o valor de local atual.
	 */
	public String getLocal() {
		return local;
	}



}
