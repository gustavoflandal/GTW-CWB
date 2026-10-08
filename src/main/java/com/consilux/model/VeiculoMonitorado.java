package com.consilux.model;

import static com.google.common.collect.Collections2.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.EventoCSX.TipoEvento;
import com.consilux.model.beans.AnexoEmailBean;
import com.consilux.model.beans.EmailEnviarBean;
import com.consilux.model.beans.VeiculoMonitoradoBean;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.GwtBean;
import com.consilux.ui.client.beans.VeiculoMonitoradoGwtBean;
import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;

public class VeiculoMonitorado {

	private static Logger logger = Logger.getLogger(VeiculoMonitorado.class);
	
	private static Semaphore semaforoEnviaEmail = new Semaphore(1);
	
	public static List<VeiculoMonitoradoBean> buscaUltimosVeiculo(int idUsuario, Date ultimoPooling)
	throws ConexaoException, SQLException {

		List<VeiculoMonitoradoBean> lRet = new ArrayList<VeiculoMonitoradoBean>();
		StringBuilder sbSQL = new StringBuilder();

		// Subquery para ordenar por data listando apenas os Últimos
		sbSQL.append("SELECT * FROM ( ");
		sbSQL.append("	SELECT TOP 20 ");
		sbSQL.append("		vm.[id_veiculo_monitorado] ");
		sbSQL.append("		,vm.[id_veiculo_local] ");
		sbSQL.append("		,vm.[data] ");
		sbSQL.append("		,vm.[velocidade] ");
		sbSQL.append("		,vm.[comprimento] ");
		sbSQL.append("		,vm.[pista] ");
		sbSQL.append("		,vm.[placa] ");
		sbSQL.append("		,vm.[flag] ");
		sbSQL.append("		,vm.[id_veiculo_unic] ");
		sbSQL.append("		,vm.[id_classe] ");
		sbSQL.append("		,vm.[id_local] ");
		sbSQL.append("		,l.[nome] as nome_local ");
		sbSQL.append("		,vm.[sequencia_local] ");
		sbSQL.append("		,vm.[falsoPositivo] ");
		sbSQL.append("		,vm.[dataAtualizacao] ");
		sbSQL.append("		,vmi.[id_imagem] ");
		sbSQL.append("		,cvm.[descricao] ");
		sbSQL.append("		,cvm.[data_cadastro] ");
		sbSQL.append("		,cv.[marca] ");
		sbSQL.append("		,cv.[cor] ");
		sbSQL.append("		,vmi.id_imagem_local ");
		sbSQL.append("		,lvg.serie_equipamento ");
		sbSQL.append("		,vm.id_email_enviar ");
		sbSQL.append("		,cvm.email_destino ");
		sbSQL.append("		,cs.descricao AS situacao");
		sbSQL.append("		FROM ");
		sbSQL.append("			[veiculo_monitorado] vm WITH (NOLOCK) ");
		sbSQL.append("			LEFT JOIN [cad_veiculo_monitorado] cvm WITH (NOLOCK) ON cvm.placa = vm.placa ");
		sbSQL.append("			LEFT JOIN cad_situacao cs WITH (NOLOCK) ON cvm.id_situacao = cs.id_situacao ");
		sbSQL.append("			JOIN [veiculo_monitorado_imagem] vmi WITH (NOLOCK) ON vm.id_veiculo_monitorado = vmi.id_veiculo_monitorado ");
		sbSQL.append("			JOIN [local] l WITH (NOLOCK) ON vm.id_local = l.id_local AND l.sequencia_local = vm.sequencia_local ");
		sbSQL.append("			LEFT JOIN [cadastro_veiculo] cv WITH (NOLOCK) ON cv.placa = vm.placa ");
		sbSQL.append("			JOIN [local_vigente] lvg ON lvg.id_local = vm.id_local ");

		if(ultimoPooling != null)
			sbSQL.append("		WHERE vm.[dataAtualizacao] >= ? ");
		
		sbSQL.append("		ORDER BY vm.[data] DESC ");
		sbSQL.append("	) AS sub ORDER BY sub.[data] ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			if( ultimoPooling != null)
				ps.setTimestamp(1, new java.sql.Timestamp(ultimoPooling.getTime()));

			rs = ps.executeQuery();
			VeiculoMonitoradoIterator itr = new VeiculoMonitoradoIterator(rs, conn, false);
			
			while (itr.hasNext()) {
				lRet.add(itr.next());
			}

		}
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			
			// Também verifica se a conexão está realmente fechada, pois informamos o iterator para fechar.
			if (conn != null && !conn.isClosed())
			{
				conn.close();
			}
						
		}

		return lRet;
	}

	public static boolean setFalsoPositivo( int id_veiculo_monitorado, boolean valor )
	throws ConexaoException, SQLException {

		boolean result = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("  UPDATE"); 
		sbSQL.append("		[veiculo_monitorado]");
		sbSQL.append("	SET"); 
		sbSQL.append("	    [falsoPositivo] = ? ,");
		sbSQL.append("	    [dataAtualizacao] = GETDATE()");
		sbSQL.append("	WHERE [id_veiculo_monitorado] = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setBoolean(1 , valor );
			ps.setInt(2 , id_veiculo_monitorado );

			result = (ps.executeUpdate() == 1);

		}	
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}

		return result;

	}

	public static boolean setIdEmailEnviado(Connection conn, int idVeiculoMonitorado, int idEmail)
	throws ConexaoException, SQLException {

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("  UPDATE"); 
		sbSQL.append("		[veiculo_monitorado]");
		sbSQL.append("	SET"); 
		sbSQL.append("	    [id_email_enviar] = ? ");
		sbSQL.append("	WHERE [id_veiculo_monitorado] = ?");

		PreparedStatement ps = null;
		
		try {
			
			ps =  conn.prepareStatement(sbSQL.toString());
			ps.setInt(1 , idVeiculoMonitorado);
			ps.setInt(2 , idEmail);
			
			return (ps.executeUpdate() == 1);
		} finally {
			if (ps != null)
				ps.close();			
		}
	}
	
	public static boolean insereVeiculoMonitorado(Integer idUsuario, VeiculoMonitoradoGwtBean veiculo)
	throws ConexaoException, SQLException {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" INSERT INTO [cad_veiculo_monitorado]");
		sbSQL.append("    (data_cadastro,data_exclusao,descricao,");
		sbSQL.append("    id_situacao,placa,id_usuario, email_destino)");
		sbSQL.append(" VALUES (GETDATE(), ?, ?, ?, ?, ?, ?)");

		boolean result = false;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			Calendar c = Calendar.getInstance();
			c.setTime(veiculo.getDataExclusao());
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			ps.setString(1, df.format(c.getTime()));
			ps.setString(2, StringEscapeUtils.escapeSql(veiculo.getDescricao()));
			ps.setInt(3, veiculo.getIdSituacao());
			ps.setString(4, StringEscapeUtils.escapeSql(veiculo.getPlaca()));
			ps.setInt(5, idUsuario);
			
			if (veiculo.getEmailDestino() != null && veiculo.getEmailDestino().length() > 0)
				ps.setString(6, veiculo.getEmailDestino());
			else
				ps.setNull(6, java.sql.Types.VARCHAR);

			
			result = (ps.executeUpdate() == 1);

		}	
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}

		return result;

	}

	// TODO: Não utilizar os beans do GWT diretamente.
	public static List<GwtBean> getSituacoes()
	throws SQLException, ConexaoException {

		List<GwtBean> situacoes = new ArrayList<GwtBean>(0);
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT id_situacao, descricao");
		sbSQL.append(" FROM [cad_situacao] ");
		sbSQL.append(" ORDER BY id_situacao ASC");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			GwtBean bean;
			while (rs.next()) {
				bean = new GwtBean(rs.getInt("id_situacao"),rs.getString("descricao"));
				situacoes.add(bean);
			}

		}	
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return situacoes;
	}

	public static int countQuery(String whereClause)
	throws SQLException, ConexaoException{

		int totalRows = 0;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT COUNT(*) AS total\n");
		sbSQL.append(" FROM\n");
		sbSQL.append(" [cad_veiculo_monitorado] cvm WITH (NOLOCK)\n");
		sbSQL.append(" INNER JOIN [cad_situacao] cs WITH (NOLOCK)\n");
		sbSQL.append("    ON (cvm.id_situacao = cs.id_situacao)\n");
		sbSQL.append(" INNER JOIN [sis_usuario] su WITH (NOLOCK)\n");
		sbSQL.append("    ON (cvm.id_usuario = su.id_usuario)\n");
		sbSQL.append(" WHERE ");
		sbSQL.append(whereClause);

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			rs.next();
			totalRows = rs.getInt(1);
			
		}
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}
		
		return totalRows;
	}

	// TODO: Não utilizar os beans do GWT diretamente no GTW.
	public static List<VeiculoMonitoradoGwtBean> buscaVeiculosCadastrados(String placa,
		boolean ativo, int limit, int offset, String sortField, String sortDirection)
		throws SQLException, ConexaoException {

		List<VeiculoMonitoradoGwtBean> veiculos = new ArrayList<VeiculoMonitoradoGwtBean>(0);
		List<String> whereClauseList = new ArrayList<String>(1);
		String whereClause;
		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT * FROM ( ");
		sbSQL.append("	SELECT ");
		sbSQL.append("		ROW_NUMBER() OVER(ORDER BY {field} {direction}) as rowNumber, ");
		sbSQL.append("		cvm.id_veiculo_monitorado, ");
		sbSQL.append("		cvm.data_cadastro, ");
		sbSQL.append("		cvm.data_exclusao, ");
		sbSQL.append("		cvm.descricao, ");
		sbSQL.append("		cvm.id_situacao, ");
		sbSQL.append("		cvm.id_usuario, ");
		sbSQL.append("		cvm.placa, ");
		sbSQL.append("		cs.descricao as situacao, ");
		sbSQL.append("		su.nome as usuario, ");
		sbSQL.append("		cvm.email_destino ");
		sbSQL.append("	FROM ");
		sbSQL.append("		[cad_veiculo_monitorado] cvm WITH(NOLOCK) ");
		sbSQL.append("		INNER JOIN [cad_situacao] cs WITH(NOLOCK) ON (cvm.id_situacao = cs.id_situacao) ");
		sbSQL.append("		INNER JOIN [sis_usuario] su WITH(NOLOCK) ON (cvm.id_usuario = su.id_usuario) ");
		sbSQL.append("	WHERE ");

		if (ativo){
			whereClauseList.add("		cvm.data_exclusao > GETDATE() ");
		} else {
			whereClauseList.add("		cvm.data_exclusao <= GETDATE() ");
		}
		if (placa != null && !"".equals(placa)){
			whereClauseList.add("cvm.placa LIKE '" + StringEscapeUtils.escapeSql(placa) + "%' ");
		}

		whereClause = Funcoes.concatStringArray(whereClauseList, "		AND ");
		sbSQL.append(whereClause);

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int totalRows = 0;
		
		try {
			
			totalRows = countQuery(whereClause);
			//limit clause
			sbSQL.append(") as t \nWHERE rowNumber BETWEEN "+(offset + 1) + " AND " + (Math.min(offset+limit,totalRows)) + "\n");

			String query = sbSQL.toString();
			if (sortField != null && !"".equals(sortField)){
				if ("id".equals(sortField)){
					sortField = "id_veiculo_monitorado";
				} else if ("dataCadastro".equals(sortField)){
					sortField = "data_cadastro";
				} else if ("dataExclusao".equals(sortField)){
					sortField = "data_exclusao";
				}
				query = query.replaceAll("\\{field\\}", sortField).replaceAll("\\{direction\\}", sortDirection);

			} else {
				query = query.replaceAll("\\{field\\}", "id_veiculo_monitorado")
							 .replaceAll("\\{direction\\}", "ASC");
			}
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			VeiculoMonitoradoGwtBean bean;
			while (rs.next()) {
				bean = new VeiculoMonitoradoGwtBean();
				
				bean.setId(rs.getInt("id_veiculo_monitorado"));
				
				bean.setDataCadastro(rs.getDate("data_cadastro"));
				bean.setDataExclusao(rs.getDate("data_exclusao"));
				bean.setDescricao(rs.getString("descricao"));
				bean.setIdSituacao(rs.getInt("id_situacao"));
				bean.setSituacao(rs.getString("situacao"));
				bean.setUsuario(rs.getString("usuario"));
				bean.setPlaca(rs.getString("placa"));
				bean.setEmailDestino(rs.getString("email_destino"));
				veiculos.add(bean);
			}

		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}
		if (veiculos.size() > 0)
			veiculos.get(0).set("total", totalRows);
		
		return veiculos;
	}
	
	private synchronized static PreparedStatement getRemoveStatement()
	throws SQLException, ConexaoException {
		
		PreparedStatement removeStatement = null;
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("UPDATE\n"); 
		sbSQL.append("		[cad_veiculo_monitorado]\n");
		sbSQL.append("SET\n");
		sbSQL.append("	id_usuario_exclusao = ?,\n");
		sbSQL.append("	descricao_exclusao = ?,\n");
		sbSQL.append("	data_exclusao = GETDATE()\n");
		sbSQL.append("WHERE\n");
		sbSQL.append("	id_veiculo_monitorado = ?\n");
		
		Connection conn = Conexao.getConexao();
		removeStatement = conn.prepareStatement(sbSQL.toString());
		
		return removeStatement;
	}
	
	public static boolean removeVeiculoMonitorado(int idUsuario, int idVeiculo, String motivo)
	throws SQLException, ConexaoException {

		boolean result = false;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = getRemoveStatement();
			ps.setInt(1, idUsuario);
			ps.setString(2, motivo);
			ps.setInt(3, idVeiculo);
			result = (ps.executeUpdate() == 1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}

		return result;
	}

	/**
	 * Busca os veículos monitorados
	 * @param ultimoVeiculo (opcional). Restringe a busca aos veículos cujo
	 * id_veiculo_monitorado seja posterior ao informado. 
	 * @param incluirEnviados define se serão listados somente os não enviados
	 * ou todos os veículos monitorados.
	 * @return
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<VeiculoMonitoradoBean> buscaUltimosVeiculos(Integer ultimoVeiculo, boolean incluirEnviados)
	throws ConexaoException, SQLException {

		List<VeiculoMonitoradoBean> result = new ArrayList<VeiculoMonitoradoBean>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT ");
		sbSQL.append("		 vm.id_veiculo_monitorado");
		sbSQL.append("		,vm.id_veiculo_local");
		sbSQL.append("		,vm.data");
		sbSQL.append("		,vm.velocidade");
		sbSQL.append("		,vm.comprimento");
		sbSQL.append("  	,vm.pista");
		sbSQL.append("  	,vm.placa");
		sbSQL.append("  	,vm.flag");
		sbSQL.append("  	,vm.id_veiculo_unic");
		sbSQL.append("  	,vm.id_classe");
		sbSQL.append("  	,vm.id_local");
		sbSQL.append("  	,l.nome as nome_local");
		sbSQL.append("  	,vm.sequencia_local");
		sbSQL.append("  	,vm.falsoPositivo");
		sbSQL.append("  	,vm.dataAtualizacao");
		sbSQL.append("  	,vmi.id_imagem");
		sbSQL.append("      ,cvm.descricao");
		sbSQL.append("      ,cvm.data_cadastro");
		sbSQL.append("      ,cv.marca");
		sbSQL.append("      ,cv.cor");
		sbSQL.append("		,vmi.id_imagem_local ");
		sbSQL.append("		,lvg.serie_equipamento ");
		sbSQL.append("		,cvm.email_destino ");
		sbSQL.append("		,vm.id_email_enviar ");
		sbSQL.append("		,cs.descricao AS situacao ");
		sbSQL.append("	FROM"); 
		sbSQL.append("  	veiculo_monitorado vm WITH (NOLOCK) ");
		sbSQL.append("  	JOIN cad_veiculo_monitorado cvm WITH (NOLOCK) ");
		sbSQL.append("	       ON vm.placa LIKE RTRIM(REPLACE(cvm.placa, '.', '_')) ");
		sbSQL.append("  	JOIN veiculo_monitorado_imagem vmi WITH (NOLOCK) ");
		sbSQL.append("	       ON vm.id_veiculo_monitorado = vmi.id_veiculo_monitorado ");
		sbSQL.append("  	JOIN local l WITH (NOLOCK) ");
		sbSQL.append("	       ON vm.id_local = l.id_local AND l.sequencia_local = vm.sequencia_local ");
		sbSQL.append("  	JOIN cad_situacao cs WITH (NOLOCK) ");
		sbSQL.append("	       ON cvm.id_situacao = cs.id_situacao");
		sbSQL.append("      LEFT JOIN cadastro_veiculo cv WITH (NOLOCK) ");
		sbSQL.append("	      ON cv.placa = vm.placa ");
		sbSQL.append("		JOIN [local_vigente] lvg ON lvg.id_local = vm.id_local ");
		sbSQL.append("	WHERE ");
		
		// Não pegar os que já foram excluídos.
		sbSQL.append("  	cvm.id_usuario_exclusao IS NULL ");
		sbSQL.append("  	AND cvm.data_exclusao > GETDATE() ");
		
		if (!incluirEnviados)
			sbSQL.append("  AND vm.id_email_enviar IS NULL ");

		if (ultimoVeiculo != null)
			sbSQL.append("  AND vm.id_veiculo_monitorado > ? ");

		sbSQL.append("	ORDER BY vm.id_veiculo_monitorado ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			if (ultimoVeiculo != null)
				ps.setInt(1, ultimoVeiculo);
			
			rs = ps.executeQuery();
			VeiculoMonitoradoIterator itr = new VeiculoMonitoradoIterator(rs, conn, false);
			
			while (itr.hasNext()) {
				result.add(itr.next());
			}

		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			
			// Também verifica se a conexão está realmente fechada, pois informamos o iterator para fechar.
			if (conn != null && !conn.isClosed())
			{
				conn.close();
			}
		}

		return result;
	}	
	
	public static Integer obterIdUltimoVeiculo()
	throws ConexaoException, SQLException {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		
		Integer idUltimoVeiculo = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT MAX(ID_VEICULO_MONITORADO) FROM VEICULO_MONITORADO WITH (NOLOCK) WHERE enviado = 1");
			rs = ps.executeQuery();
			
			if (rs.next()) {
				idUltimoVeiculo = rs.getInt(1);
			}

		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}

		return idUltimoVeiculo;
	}
	
	/**
	 * Busca a imagem (BLOB) de um veículo monitorado.
	 * @param idVeiculoMonitorados um id de veículo monitorado que se deseja.
	 * @return o seu BLOB.
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static byte[] obterImagem(int idVeiculoMonitorado)
	throws SQLException, ConexaoException {

		Connection conn = null;
		try {

			conn = Conexao.getConexao();
			return obterImagem(conn, idVeiculoMonitorado);
			
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
	}
	
	
	/**
	 * Busca a imagem (BLOB) de um veículo monitorado.
	 * @param conn conexão previamente aberta com o banco
	 * @param idVeiculoMonitorados um id de veículo monitorado que se deseja.
	 * @return o seu BLOB.
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static byte[] obterImagem(Connection conn, int idVeiculoMonitorado)
	throws SQLException, ConexaoException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT ");
		sbSQL.append("	img.imagem  ");
		sbSQL.append("FROM ");
		sbSQL.append("	veiculo_monitorado_imagem vmi WITH (NOLOCK) ");
		sbSQL.append("	JOIN imagem_monitorado img ON img.id_imagem_monitorado = vmi.id_imagem ");
		sbSQL.append("WHERE vmi.id_veiculo_monitorado = ? ");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		byte[] blobImagem = null;
		
		try {
			
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idVeiculoMonitorado);
			rs = ps.executeQuery();
			
			if (rs.next())
				blobImagem = rs.getBytes("imagem");
			
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}

		return blobImagem;
	}
	
	/**
	 * Converte um bean de veículo-monitorado em seu bean correspondente do GWT.
	 * @param beanOriginal o bean original a ser convertido.
	 * @return
	 */
	public static VeiculoMonitoradoGwtBean toGwtBean(VeiculoMonitoradoBean beanOriginal) {
		
		if (beanOriginal == null)
			return null;
		
		VeiculoMonitoradoGwtBean beanGwt = new VeiculoMonitoradoGwtBean();
		
		beanGwt.setVelocidade((int)beanOriginal.getVelocidade());
		beanGwt.setUsuario(beanOriginal.getUsuario());
		beanGwt.setSituacao(beanOriginal.getSituacao());
		beanGwt.setSerieEquipamento(beanOriginal.getSerieEquipamento());
		beanGwt.setSentido("");
		beanGwt.setPlaca(beanOriginal.getPlaca());
		beanGwt.setPista(beanOriginal.getPista());
		beanGwt.setNomeLocal(beanOriginal.getNomeLocal());
		beanGwt.setModelo(beanOriginal.getMarca());
		beanGwt.setIdSituacao(beanOriginal.getIdSituacao());
		beanGwt.setIdLocal(beanOriginal.getIdLocal());
		beanGwt.setIdImagemLocal(beanOriginal.getIdImagemLocal());
		beanGwt.setIdImagem(beanOriginal.getIdImagem());
		beanGwt.setId(beanOriginal.getId());
		beanGwt.setFalsoPositivo(beanOriginal.getFalsoPositivo());
		beanGwt.setDescricao(beanOriginal.getDescricao());
		
		beanGwt.setDataExclusao(beanOriginal.getDataExclusao());
		beanGwt.setDataCadastro(beanOriginal.getDataCadastro());
		beanGwt.setDataHora(beanOriginal.getDataHora());
		beanGwt.setDataHoraRecebido(null);
		beanGwt.setCor(beanOriginal.getCor());
		beanGwt.setEmailDestino(beanOriginal.getEmailDestino());
		
		return beanGwt;
	}
	
	/**
	 * Converte uma lista de beans de veículo-monitorado em sua lista de beans do GWT.
	 * @param listaOriginal a lista contendo os beans originais.
	 * @return
	 */
	public static List<VeiculoMonitoradoGwtBean> toGwtBeans(List<VeiculoMonitoradoBean> listaOriginal) {
		
		List<VeiculoMonitoradoGwtBean> lRet =
			new ArrayList<VeiculoMonitoradoGwtBean>(listaOriginal.size());
		
		for (VeiculoMonitoradoBean beanOriginal : listaOriginal) {
			lRet.add(toGwtBean(beanOriginal));
		}
		
		return lRet;
		
	}

	/**
	 * Método responsável por enviar (enfileirar) os emails de veículos monitorados.
	 * Na realidade, os emails não são enviados na hora, mas sim adicionados no 'SMTP'
	 * interno do GTW. 
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws ConfiguracaoException
	 * @throws ModelException
	 */
	public static void enviaEmailVeiculosMonitorados() throws ConexaoException,
	SQLException, ConfiguracaoException, ModelException {

		Connection conn = null;
		boolean lockExclusivo = false;
		
		try {
			
			// Tenta obter o lock.
			lockExclusivo = semaforoEnviaEmail.tryAcquire();
			
			if (!lockExclusivo) {
				return;
			}	
			
			// Abre a conexão
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			
			// Obtém a lista de veículos monitorados, desde a última atualização.
			Collection<VeiculoMonitoradoBean> veiculosMonitorados = VeiculoMonitorado.buscaUltimosVeiculos(null, false);

			// Filtra a lista, para somente trabalharmos com aqueles que possuem email.
			veiculosMonitorados = filter(veiculosMonitorados, new Predicate<VeiculoMonitoradoBean>() {
				@Override
				public boolean apply(VeiculoMonitoradoBean vm) {
					return vm.getEmailDestino() != null && vm.getEmailDestino().length() > 0;
				}
			});

			// Se não conseguimos ninguém, é porque a lista não mudou.
			// Neste caso, pode sair do método.
			if (veiculosMonitorados.size() == 0)
				return;

			// Efetua um sort na lista, baseado na data/hora
			veiculosMonitorados = Ordering.from(new Comparator<VeiculoMonitoradoBean>() {
				@Override
				public int compare(VeiculoMonitoradoBean o1, VeiculoMonitoradoBean o2) {
					return (int) (o1.getDataHora().getTime() - o2.getDataHora().getTime());
				}
			}).sortedCopy(veiculosMonitorados);

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			EmailEnviarBean emailEnviar;
			StringBuilder sbEmail = new StringBuilder();
			
			byte[] bytesImagem = null;
			
			for (VeiculoMonitoradoBean vm : veiculosMonitorados) {
				
				// Pega as imagem correspondentes (do banco)
				bytesImagem = VeiculoMonitorado.obterImagem(conn, vm.getId());				
				
				// Limpa o builder
				sbEmail.setLength(0);
				
				sbEmail.append("Veículo monitorado detectado. Dados do Veículo:\r\n");
				sbEmail.append("\r\nLocal: " + vm.getNomeLocal());

				if (vm.getSentido() != null) {
					sbEmail.append("\r\nSentido: ");					
					sbEmail.append(vm.getSentido());
				}

				sbEmail.append("\r\nData/Hora: " + df.format(vm.getDataHora()));

				if (vm.getSerieEquipamento() != null) {
					sbEmail.append("\r\nCódigo Equipamento: ");
					sbEmail.append(Integer.toString(vm.getSerieEquipamento()));
				}

				if (vm.getPista() != null) {
					sbEmail.append("\r\nPista: ");					
					sbEmail.append(Integer.toString(vm.getPista()));
				}

				sbEmail.append("\r\nPlaca: " + vm.getPlaca());

				if (vm.getMarca() != null) {
					sbEmail.append("\r\nMarca: ");					
					sbEmail.append(vm.getMarca());
				}

				if (vm.getCor() != null) {
					sbEmail.append("\r\nCor: ");
					sbEmail.append(vm.getCor());
				}
				
				if (vm.getIdImagemLocal() != null) {
					sbEmail.append("\r\nNúmero Imagem: ");					
					sbEmail.append(vm.getIdImagemLocal());
				}
				
				sbEmail.append("\r\nDescrição: ");
				if (vm.getDescricao() != null && vm.getDescricao().length() > 0)
				{
					sbEmail.append(vm.getDescricao());
				}

				sbEmail.append("\r\nSituação: ");
				if (vm.getSituacao() != null && vm.getSituacao().length() > 0)
				{
					sbEmail.append(vm.getSituacao());
				}
				
				sbEmail.append("\r\n\r\n");

				// Cria o bean de email.
				emailEnviar = new EmailEnviarBean();
				emailEnviar.setRemetente(new InternetAddress("admin@consilux.com.br",
					ConfiguracaoProvider.getInstance().getAssuntoEmailVeiculoMonitorado()));
				
				// Adiciona o destinatário.
				emailEnviar.getDestinatarios().add(new InternetAddress(vm.getEmailDestino()));

				emailEnviar.setAssunto(ConfiguracaoProvider.getInstance().getAssuntoEmailVeiculoMonitorado());
				emailEnviar.setCorpo(sbEmail.toString());

				if (bytesImagem != null) {
					emailEnviar.getAnexos().add(new AnexoEmailBean("image/jpg", "imagem_veiculo.jpg",
					bytesImagem));
				}			
				
				// Insere o email a ser enviado no banco.
				EmailEnviar.inserir(conn, emailEnviar, vm.getId());
				
				// Marca o id do email que foi criado para este veículo monitorado. 
				VeiculoMonitorado.setIdEmailEnviado(conn, vm.getId(), emailEnviar.getIdEmail());

			}// end for
			
			// Por fim, realiza o commit de toda a transação. 
			conn.commit();
				
		} catch (Exception e) {
			
			if (conn != null)
				conn.rollback();
			
			logger.error("Erro ao tentar criar email de veículo monitorado.", e);

			// Armazena um evento deste erro.
			EventoCSX eventoCSX = new EventoCSX(TipoEvento.EMAIL_ERROR,
				"Sistema", "Agendador de Tarefas", "Erro ao tentar criar email de veículo monitorado.");
			
			Evento.incluirEventoCSX(eventoCSX);
		}
		finally {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
			if (lockExclusivo) {
				semaforoEnviaEmail.release();
			}				
		}
	}

	
}
