package com.consilux.model.descarga;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.time.DateUtils;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.Funcoes;
import com.consilux.infra.beans.Tuple;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.descarga.beans.DescargaBean;
import com.consilux.model.exception.ModelException;

/**
 * Classe de modelo de descarga.
 * @author raoni
 */
public class Descarga {

	public static final long TAMANHO_MIDIA_MIN = 10L * 1024L * 1024L; // 10 MB
	public static final long TAMANHO_MIDIA_MAX = 10L * 1024 * 1024L * 1024L; // 10 GB
	public static final double OVER_HEAD_ISO_STRUCT_FACTOR = 1.05D; // 5%
	
	public static DescargaBean buscarDescargaPorId(Integer idDescarga) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("	id_descarga, dia_inicio, dia_fim, data_criacao, total_veiculos, ");
		sbSQL.append("	total_imagens, total_infracoes, data_confirmacao, id_usuario_confirmacao,");
		sbSQL.append("	uc.usuario as usuario_confirmacao");
		sbSQL.append(" FROM ");
		sbSQL.append("	descarga d WITH (NOLOCK) ");
		sbSQL.append("	LEFT JOIN sis_usuario uc WITH (NOLOCK) ON uc.id_usuario = d.id_usuario_confirmacao");
		sbSQL.append(" WHERE ");
		sbSQL.append("		id_descarga = ?");		

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idDescarga);
			
			rs = ps.executeQuery();
			
			DescargaBean retBean = null;
			if (rs.next())
			{
			
				retBean = new DescargaBean();
				retBean.setId(rs.getInt("id_descarga"));
				retBean.setDiaInicio(new Date(rs.getDate("dia_inicio").getTime()));
				retBean.setDiaFim(new Date(rs.getDate("dia_fim").getTime()));
				retBean.setDataCriacao(new Date(rs.getTimestamp("data_criacao").getTime()));
				retBean.setTotalVeiculos(rs.getInt("total_veiculos"));
				retBean.setTotalImagens(rs.getInt("total_imagens"));
				retBean.setTotalInfracoes(rs.getInt("total_infracoes"));
				retBean.setDataConfirmcao(rs.getTimestamp("data_confirmacao") != null ? new Date(rs.getTimestamp("data_confirmacao").getTime()) : null);
				retBean.setUsuarioConfirmacao(rs.getString("usuario_confirmacao"));
				
			}
			
			return retBean;
		}		
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	public static List<DescargaBean> buscarDescargaPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {

		List<DescargaBean> lRet = new ArrayList<DescargaBean>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("	id_descarga, dia_inicio, dia_fim, data_criacao, total_veiculos, ");
		sbSQL.append("	total_imagens, total_infracoes, data_confirmacao, id_usuario_confirmacao,");
		sbSQL.append("	uc.usuario as usuario_confirmacao");
		sbSQL.append(" FROM ");
		sbSQL.append("	descarga d WITH (NOLOCK) ");
		sbSQL.append("	LEFT JOIN sis_usuario uc WITH (NOLOCK) ON uc.id_usuario = d.id_usuario_confirmacao");
		sbSQL.append("	WHERE ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("data_ini", "data_criacao >= ?");
		mRegras.put("data_fim", "data_criacao <= ?");
		mRegras.put("nao_exportada", "data_exportacao IS NULL");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY data_criacao");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
			rs = ps.executeQuery();
			
			DescargaBean descargaBean = null;
			while (rs.next()) {
			
				descargaBean = new DescargaBean();
				descargaBean.setId(rs.getInt("id_descarga"));
				descargaBean.setDiaInicio(new Date(rs.getDate("dia_inicio").getTime()));
				descargaBean.setDiaFim(new Date(rs.getDate("dia_fim").getTime()));
				descargaBean.setDataCriacao(new Date(rs.getTimestamp("data_criacao").getTime()));
				descargaBean.setTotalVeiculos(rs.getInt("total_veiculos"));
				descargaBean.setTotalImagens(rs.getInt("total_imagens"));
				descargaBean.setTotalInfracoes(rs.getInt("total_infracoes"));
				descargaBean.setDataConfirmcao(rs.getTimestamp("data_confirmacao") != null ? new Date(rs.getTimestamp("data_confirmacao").getTime()) : null);
				descargaBean.setUsuarioConfirmacao(rs.getString("usuario_confirmacao"));
				
				lRet.add(descargaBean);
			}
			
		}				
		finally {
			if (conn != null)
				conn.close();							
		}
		return lRet;		
		
	}
	
	public static List<DescargaBean> listarDescargas() throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("	id_descarga, dia_inicio, dia_fim, data_criacao, total_veiculos, ");
		sbSQL.append("	total_imagens, total_infracoes, data_confirmacao ");
		sbSQL.append("FROM ");
		sbSQL.append("	descarga WITH (NOLOCK) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<DescargaBean> lRet= new ArrayList<DescargaBean>();
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			DescargaBean descargaBean = null;
			while (rs.next())
			{
			
				descargaBean = new DescargaBean();
				descargaBean.setId(rs.getInt("id_descarga"));
				descargaBean.setDiaInicio(new Date(rs.getDate("dia_inicio").getTime()));
				descargaBean.setDiaFim(new Date(rs.getDate("dia_fim").getTime()));
				descargaBean.setDataCriacao(new Date(rs.getTimestamp("data_criacao").getTime()));
				descargaBean.setTotalVeiculos(rs.getInt("total_veiculos"));
				descargaBean.setTotalImagens(rs.getInt("total_imagens"));
				descargaBean.setTotalInfracoes(rs.getInt("total_infracoes"));
			
				// Campos que podem ser nulos
				Timestamp tmpStamp = rs.getTimestamp("data_confirmacao");
				if (!rs.wasNull())
					descargaBean.setDataCriacao(new Date(tmpStamp.getTime()));
				
				lRet.add(descargaBean);
			}
			
			return lRet;
		}		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
	}	
	
	public static int criarDescarga(Date dataIni, Date dataFim, int idUsuario) throws ModelException, ConexaoException, SQLException {
		Integer iRet = null;
		
		if (dataIni == null)
			throw new ModelException("Argumento inválido: dataIni não pode ser nulo.");

		if (dataFim == null)
			throw new ModelException("Argumento inválido: dataFim não pode ser nulo.");
		
		if (dataIni.after(dataFim))
			throw new ModelException("Argumento inválido: dataIni deve ser anterior a dataFim.");

		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			conn = Conexao.getConexao();
			
			cs = conn.prepareCall("{? = call spu_gera_descarga (?,?,?)}");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setDate(2, new java.sql.Date(dataIni.getTime()));
			cs.setDate(3, new java.sql.Date(dataFim.getTime()));
			cs.setInt(4, idUsuario);
			
			cs.execute();
			iRet = cs.getInt(1);

			if (iRet == 0) {
				// Se deu erro, tenta recuperar alguma informação que veio do SQL.
				SQLWarning sqlWarn = cs.getWarnings();
				String msgErro = "Erro na geração da descarga.";
				if (sqlWarn != null) {
					msgErro += sqlWarn.getMessage();
				}
				throw new ModelException(msgErro);
			}
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
		return iRet;
		
	}
	
	public static Tuple<Date,Date> buscarProximoPeriodoDescarga(long tamanhoMidia) throws ModelException, SQLException, ConexaoException {
		
		if (tamanhoMidia < TAMANHO_MIDIA_MIN)
			throw new ModelException("O tamanho mínimo da mídia aceitado é de [" + TAMANHO_MIDIA_MIN + "] bytes.");

		if (tamanhoMidia > TAMANHO_MIDIA_MAX)
			throw new ModelException("O tamanho máximo da mídia aceitado é de [" + TAMANHO_MIDIA_MAX + "] bytes.");

		Date diaIni = null;
		Date diaFim = null;
		Date diaTemp = null;
		
		Connection conn = null;;
		PreparedStatement ps = null;
		ResultSet rs = null;
		CallableStatement cs = null;
		
		try {
			
			// Limite máximo para geração de descarga (para garantir que existe pelo menos N dias de dados no banco)
			int maxDiasDescarga = ConfiguracaoProvider.getInstance().getConfiguracaoDescarga().getNumeroMaximoDiasFinalDescarga();
			Date maxDiaDescarga = DateUtils.addDays(new Date(), -maxDiasDescarga);
			maxDiaDescarga = DateUtils.truncate(maxDiaDescarga, Calendar.DAY_OF_MONTH); // Remove hora,minuto,segundo,mili.
			
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement("SELECT MAX(d.dia_fim) FROM descarga d");
			rs = ps.executeQuery();
			rs.next();
			diaIni = rs.getDate(1);
			
			if (rs.wasNull()) {
				// Se não achou, significa que não temos nenhuma descarga no sistema até este momento.
				// Pega então o dia do primeiro veículo menos um, porque é como se tivesse feito discarga até o dia anterior a este...

				ps = conn.prepareStatement("SELECT CAST(MIN(data) AS DATE) FROM veiculo WITH (NOLOCK)");
				rs = ps.executeQuery();

				if (!rs.next()) {
					// Não achou nem na tabela veículo neste caso, é erro mesmo.
					throw new ModelException("Erro: não foi possível determinar o dia de início para geração de descarga.");
				} else {
					diaIni = DateUtils.addDays(rs.getDate(1), -1);					
				}
			}
			
			// Soma um dia, no dia inicial (que veio do banco).
			diaIni = DateUtils.addDays(diaIni, 1);
			diaTemp = new Date(diaIni.getTime());
			
			Long tamanhoAcumulado = 0L;
			cs = conn.prepareCall("{? = call fcn_calcula_espaco_imagens_dia(?)}");
			cs.registerOutParameter(1, Types.BIGINT);
			boolean keepWalking = true;
			
			do 
			{
				cs.setDate(2, new java.sql.Date(diaTemp.getTime()));
				cs.execute();
				
				Long tamanhoDia = cs.getLong(1);
				if (!cs.wasNull()) {
					tamanhoAcumulado += (long)((double)tamanhoDia*OVER_HEAD_ISO_STRUCT_FACTOR);
				}
				
				keepWalking = (tamanhoAcumulado < tamanhoMidia) &&  diaTemp.before(maxDiaDescarga);
				
				// Verifica se por acaso não estamos excedendo o limite.
				if (diaTemp.after(maxDiaDescarga))
				{
					throw new ModelException("Erro: excedido o limite máximo do dia [" + maxDiaDescarga + "] para geração de descarga.");
				}
				
				if (keepWalking) {
					diaFim = new Date(diaTemp.getTime());
					diaTemp = DateUtils.addDays(diaTemp, 1);
				}
			}
			while (keepWalking);
			
			if (diaTemp == null)
			{
				throw new ModelException("Erro: não é possível armazenar as imagens do dia [" + diaIni + "] para este tamanho de mídia.");
			}
			
		}
		finally {
			if (conn != null)
			{
				conn.close();
			}
		}
		
		return new Tuple<Date, Date>(diaIni, diaFim);
	}
	
	public static String getIsoVolumeName(int idDescarga) throws ConfiguracaoException {
		return Integer.toString(idDescarga);
	}
	
	public static String getIsoFileName(int idDescarga, Date diaInicio, Date diaFim) throws ConfiguracaoException {
		
		String nomeContrato = ConfiguracaoProvider.getInstance().getNomeContrato();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String isoFileName = nomeContrato + "_" + df.format(diaInicio)  + "_" + df.format(diaFim) + "_" + idDescarga + ".iso";
		
		return isoFileName;
	}
	
	public static Boolean confirmaDescarga(Integer idDescarga, Integer idUsuario) throws ConexaoException, SQLException {
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("UPDATE descarga SET ");
		sbSQL.append("	data_confirmacao = ?,");
		sbSQL.append("	id_usuario_confirmacao = ? ");
		sbSQL.append(" WHERE ");
		sbSQL.append("	id_descarga = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setTimestamp(1, new Timestamp(new Date().getTime()));
			ps.setInt(2, idUsuario);
			ps.setInt(3, idDescarga);
			
			bRet = ps.executeUpdate() > 0;
			
			ps.close();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return bRet;
	}
	
	public static Boolean confirmaExportacaoDescarga(Integer idDescarga, Map<Integer,String> mapaMd5) throws ConexaoException, SQLException, ModelException {
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("UPDATE imagem_info SET ");
		sbSQL.append("	md5 = ?");
		sbSQL.append(" WHERE ");
		sbSQL.append("	id_imagem = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			for (Entry<Integer, String> entrada: mapaMd5.entrySet()) {
				ps.setString(1, entrada.getValue());
				ps.setInt(2, entrada.getKey());
				bRet = ps.executeUpdate() > 0;
				if (!bRet)
					throw new ModelException("Existe pelo menos uma imagem que não foi ajustada o MD5.");
			}
			
			ps.close();

			sbSQL.setLength(0);
			sbSQL.append("UPDATE descarga SET ");
			sbSQL.append("	data_exportacao = ?");
			sbSQL.append(" WHERE ");
			sbSQL.append("	id_descarga = ?");
	
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setTimestamp(1, new Timestamp(new Date().getTime()));
			ps.setInt(2, idDescarga);
			
			bRet = ps.executeUpdate() > 0;
			
			ps.close();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return bRet;
	}
}
