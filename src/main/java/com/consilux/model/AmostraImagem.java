/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.model

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 13/05/2009

  Descricao: XXX

  Historico:

    $Log: AmostraImagem.java,v $
    Revision 1.3  2009/05/29 20:33:59  fos
    Agora separa por metrológica/não metológica.

    Revision 1.2  2009/05/18 14:26:34  fos
    Agora a amostra imagem contém dados do registro no banco de dados e não o registro para exportação de amostra.


*********************************************************************************/
package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Log.TipoLog;
import com.consilux.model.beans.AmostraVeiculoBean;
import com.consilux.model.beans.AmostraVeiculoThumbBean;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.AmostraVeiculoGwtBean;
import com.consilux.ui.tabela.PistaAmostraImagem;

/**
 * Classe de negócio relativa a amostras de imagens.
 * @author fos
 * @version $Revision: 1.3 $ $Date: 2009/05/29 20:33:59 $ $Author: fos $
 */

public class AmostraImagem {
	
	public static final Integer PONTO_MAX = 700;
	public static final Integer PONTO_MIN = 0;
	public static final Integer PONTO_RUIM = 150;
	public static final Integer PONTO_REGULAR = 500;

	/**
	 * Recupera a lista das amostras de um determinado período, dado uma
	 * combinação de local/pista.
	 * @param diaInicial
	 * @param diaFinal
	 * @param idLocal
	 * @param idPista
	 * @param metrologica 
	 * @return fila de amostras.
	 * @throws ModelException
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static Queue<AmostraVeiculoBean> buscaAmostrasPeriodo(Connection conn, Calendar diaInicial, Calendar diaFinal,
		Integer idLocal, Integer idPista, boolean metrologica) throws ModelException, SQLException, ConexaoException
	{
		if (diaInicial == null)
			throw new ModelException("Argumento nulo: diaInicial");

		if (diaFinal == null)
			throw new ModelException("Argumento nulo: diaFinal");		

		if (diaInicial.after(diaFinal))
			throw new ModelException("Argumento inválido: dia inicial deve ser anterior ao dia final.");			
		
		if (idLocal == null)
			throw new ModelException("Argumento nulo: idLocal");		
		
		if (idPista == null)
			throw new ModelException("Argumento nulo: idPista");		
		
		CallableStatement cs = null;
		Queue<AmostraVeiculoBean> qRet = new LinkedList<AmostraVeiculoBean>();

		cs = conn.prepareCall("{call spu_busca_amostras_periodo_local_pista(?, ?, ?, ?, ?)}");
		cs.setDate(1, new java.sql.Date(diaInicial.getTime().getTime()));
		cs.setDate(2, new java.sql.Date(diaFinal.getTime().getTime()));
		cs.setInt(3, idLocal);
		cs.setInt(4, idPista);
		cs.setBoolean(5, metrologica);
		
		if (cs.execute())
		{
			// Atenção: não devemos fechar a conexão neste método (pois estamos recebendo ela de fora)
			// Por isso, não temos a necessidade de encapsular este método em try/finally.
			AmostraVeiculoIterator itr = new AmostraVeiculoIterator(cs.getResultSet(), conn, false);
			
			while (itr.hasNext()) {
				AmostraVeiculoBean bean = itr.next();
				qRet.add(bean);
			}
		}			
		
		return qRet;
	}	
	
	/**
	 * Recupera a lista das amostras de um determinado período, dado uma
	 * combinação de local/pista.
	 * @param diaInicial
	 * @param diaFinal
	 * @param pistaAmostras
	 * @return fila de amostras.
	 * @throws ModelException
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static Queue<AmostraVeiculoBean> buscaAmostrasPeriodo(Connection conn, Calendar diaInicial, Calendar diaFinal,
		PistaAmostraImagem pistaAmostras) throws ModelException, SQLException, ConexaoException
	{
		// Versão sobrecarregada do método acima, que aceita uma "PistaAmostraImagem" como parâmetro 
		return buscaAmostrasPeriodo(conn, diaInicial, diaFinal, pistaAmostras.getIdLocal(), pistaAmostras.getPista(),
			pistaAmostras.isMetrologica());
	}	
	
	/**
	 * Recupera a lista das amostras de um determinado período, dado uma
	 * combinação de local/pista.
	 * @param diaInicial
	 * @param diaFinal
	 * @param pistaAmostras
	 * @return fila de amostras.
	 * @throws ModelException
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static Queue<AmostraVeiculoBean> buscaAmostrasPeriodo(Calendar diaInicial, Calendar diaFinal, PistaAmostraImagem pistaAmostras)
	throws ModelException, SQLException, ConexaoException
	{
		
		Queue<AmostraVeiculoBean> qRet = new LinkedList<AmostraVeiculoBean>();
		Connection conn = null;
		
		try {
			conn = Conexao.getConexao();
			qRet = buscaAmostrasPeriodo(conn, diaInicial, diaFinal, pistaAmostras);
		} finally {
			if (conn != null)
				conn.close();
		}
		
		return qRet;
	}	
	
	/**
	 * Conta quantas sugestões existem para a combinação de parâmetros
	 * @param idLocal
	 * @param idPista
	 * @param dia
	 * @param metrologica
	 * @return
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static int contarSugestoes(int idLocal, byte idPista,
			Date dia, boolean metrologica) throws SQLException, ConexaoException {
		
		Connection conn = null;
		CallableStatement cs = null;
		int iRet = 0;
		
		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall("{? = call fcn_contar_sugestoes_amostras (?,?,?,?)}");
			
			cs.registerOutParameter(1, Types.INTEGER) ;
			cs.setInt(2, idLocal);
			cs.setByte(3, idPista);
			cs.setDate(4, new java.sql.Date(dia.getTime()));
			cs.setBoolean(5, metrologica);
			
			cs.execute();
			iRet = cs.getInt(1);
		}
		finally
		{
			if (conn != null)
				conn.close();
		}
		return iRet;
		
	}
	
	/**
	 * Lista sugestões para a definição de amostras. 
	 * @param idLocal
	 * @param idPista
	 * @param dia
	 * @param metrologica
	 * @return uma lista com sugestões.
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static List<AmostraVeiculoThumbBean> listarSugestoes(int idLocal,
		byte idPista, Date dia, boolean metrologica, int pageOffset, int pageLimit) throws SQLException, ConexaoException {
		
		List<AmostraVeiculoThumbBean> lRet = new ArrayList<AmostraVeiculoThumbBean>();
		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall("{call spu_listar_sugestoes_amostras(?, ?, ?, ?, ?, ?)}");
			
			cs.setInt(1, idLocal);
			cs.setShort(2, idPista);
			cs.setDate(3, new java.sql.Date(dia.getTime()));
			cs.setBoolean(4, metrologica);
			cs.setInt(5, 1 + pageOffset);
			cs.setInt(6, pageLimit);
			
			if (cs.execute())
			{
				AmostraVeiculoThumbIterator itr = new AmostraVeiculoThumbIterator(cs.getResultSet(), conn, true);
				
				while (itr.hasNext()) {
					AmostraVeiculoThumbBean bean = itr.next();
					bean.setMetrologica(metrologica); // Engana o usuário, baseado não no ResultSet
													  // mas sim no parâmetro recebido nesta função.
					lRet.add(bean);
				}
				// O iterator já vai fechar a conexão. Para evitar e fechar duas vezes, vamos igualar
				// a null para evitar o segundo fechamento (logo abaixo)
				conn = null;
			}
		}
		finally {
			// Teoricamente este finally não é necessário, pois o iterator já vai fechar a conexão.
			// Colocado apenas como "boas práticas"
			if (conn != null)
				conn.close();
		}
		return lRet;
	}

	/**
	 * Define manualmente uma amostra para uma determinada combinação
	 * dos parâmetros. 
	 * @param dia o dia desejado
	 * @param idLocal o local desejado
	 * @param pista a pista desejada
	 * @param metrologica se é uma amostra metrológica (ou não)
	 * @param idVeiculo o id do veículo
	 * @throws SQLException
	 * @throws ConexaoException
	 * @throws ModelException
	 */
	public static void definirManualmenteIdImagem(int idUsuario, Date dia, int idLocal, byte idPista,
			boolean metrologica, long idVeiculo, boolean aplicavel) throws SQLException, ConexaoException, ModelException {
	
		if (dia == null)
			throw new ModelException("Argumento nulo: dia");

		Connection conn = null;
		
		try {
			conn = Conexao.getConexao();
			CallableStatement cs = conn.prepareCall("{call spu_adic_amostra_manual(?, ?, ?, ?, ?, ?, ?)}");
			
			cs.setDate(1, new java.sql.Date(dia.getTime()));
			cs.setInt(2, idLocal);
			cs.setShort(3, idPista);
			cs.setBoolean(4, metrologica);
			cs.setLong(5, idVeiculo);
			cs.setBoolean(6, aplicavel);
			cs.setInt(7, idUsuario);
			
			cs.execute();
			
			Log.gravaLog(conn, TipoLog.TIPO_ACESSO, idUsuario, "Amostra definida manualmente.",
					"ID Veiculo=" + idVeiculo);;
		}
		finally {
			if (conn != null)
				conn.close();
		}
		
	}

	/**
	 * Converte um bean do GTW para um bean do GWT (que é mais reduzido). 
	 * @param beanOriginal
	 * @throws ModelException
	 * @return um bean do GWT.
	 */
	public static AmostraVeiculoGwtBean toGwtBean(AmostraVeiculoBean beanOriginal) throws ModelException {
		
		if (beanOriginal == null)
			throw new ModelException("Argumento nulo: beanOriginal");		
		
		AmostraVeiculoGwtBean ret = new AmostraVeiculoGwtBean();
		ret.setData(beanOriginal.getData().getTime());
		ret.setIdEquipamento(beanOriginal.getIdLocal());
		ret.setPista(beanOriginal.getIdPista());
		ret.setCodPistaProdam(beanOriginal.getCodPistaProdam());
		ret.setMetrologica(beanOriginal.isMetrologica());
		ret.setPontuacao(beanOriginal.getScore());
		ret.setId(beanOriginal.getIdVeiculo() != null ? beanOriginal.getIdVeiculo().toString() : null);
		ret.setEscolhidaManualmente(beanOriginal.isEscolhidaManualmente());
		ret.setAplicavel(beanOriginal.isAplicavel());
		
		return ret;
	}
	
	/**
	 * Converte uma lista de beans do GTW para um bean do GWT (que é mais reduzido). 
	 * @param listaOriginal
	 * @throws ModelException
	 * @return uma lsita de beans do GWT.
	 * @throws ModelException 
	 */
	public static List<AmostraVeiculoGwtBean> toGwtBean(Iterable<AmostraVeiculoBean> listaAmostras) throws ModelException {
		
		if (listaAmostras == null)
			throw new ModelException("Argumento nulo: listaMenus");
		
		List<AmostraVeiculoGwtBean> lRet = new ArrayList<AmostraVeiculoGwtBean>();
		AmostraVeiculoGwtBean gwtBean = null;
		
		for (AmostraVeiculoBean beanOriginal : listaAmostras) {
			gwtBean = toGwtBean(beanOriginal);
			lRet.add(gwtBean);
		}
		return lRet;
		
	}	
	
	public static Date buscaUltimoDiaConfiavel(Date dataInicio) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();
		Date ret = null;
		
		sbSQL.append("SELECT MAX(data) as data FROM (");
		sbSQL.append("	SELECT AVG(score_total) as media_score,CAST(data AS DATE) AS data");
		sbSQL.append("	FROM amostra_imagem");
		sbSQL.append("	WHERE data > ?");
		sbSQL.append("	GROUP BY cast(data AS DATE)");
		sbSQL.append("	HAVING AVG(score_total) > ?");
		sbSQL.append(" ) AS t");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new Timestamp(dataInicio.getTime()));
			ps.setInt(2, AmostraImagem.PONTO_RUIM);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				ret = rs.getDate("data");
			}
			ps.close();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return ret;		
	}	
}
