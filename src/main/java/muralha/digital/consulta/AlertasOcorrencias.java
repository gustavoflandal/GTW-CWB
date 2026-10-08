package muralha.digital.consulta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

import muralha.digital.dispositivo.DispositivoEquipamento;
import muralha.digital.dispositivo.DispositivosEquipamentos;
import muralha.digital.util.Paginacao;
import muralha.digital.veiculo.imagem.VeiculoImagem;
import muralha.digital.veiculo.imagem.VeiculoImagens;

@XmlRootElement		(name="AlertasOcorrencias") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class AlertasOcorrencias
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(AlertasOcorrencias.class);
	
	@XmlElementWrapper	(name = "ListaAlertasOcorrencias")
	@XmlElement			(name = "AlertaOcorrencia")	
	private List<AlertaOcorrencia> listaAlertasOcorrencias;		
	
	private UUID idTipoRegistro;
	private UUID idTipoAlertaOcorrencia;
	private int quantitativo;
	
	private Paginacao paginacao;
	
	public List<AlertaOcorrencia> getListaAlertasOcorrencias() {
		return listaAlertasOcorrencias;
	}

	public void setListaAlertasOcorrencias(List<AlertaOcorrencia> listaAlertasOcorrencias) {
		this.listaAlertasOcorrencias = listaAlertasOcorrencias;
	}
	
	public int getQuantitativo() {
		return quantitativo; 	
	}

	public void setIdTipoRegistro(UUID idTipoRegistro) {
		this.idTipoRegistro = idTipoRegistro;
	}

	public UUID getIdTipoAlertaOcorrencia() {
		return idTipoAlertaOcorrencia;
	}

	public void setIdTipoAlertaOcorrencia(UUID idTipoAlertaOcorrencia) {
		this.idTipoAlertaOcorrencia = idTipoAlertaOcorrencia;
	}
	
	public void setQuantitativo(int quantitativo) { 
		this.quantitativo = quantitativo; 
	}
	
	public UUID getIdTipoRegistro() {
		return idTipoRegistro;
	}
	
	public Paginacao getPaginacao() {
		return paginacao;
	}

	public void setPaginacao(Paginacao paginacao) {
		this.paginacao = paginacao;
	}

	public AlertasOcorrencias()
	{
		super();
	}
	
	public static AlertasOcorrencias ObterListaAlertasOcorrencias(String funcaoSQL, UUID idTipoAlertaOcorrencia, UUID idStatus, String placa, 
			Date dataIni, Date dataFim, Integer idLocal, Integer idUsuario, boolean privado, boolean supervisionado,boolean assinadosPendentes , UUID idCadVeiculoMonitorado,
			Integer idRegistroFato, Integer idUsuarioAlerta, Integer assinado, Paginacao paginacao) throws ConexaoException, SQLException 
	{
		AlertasOcorrencias retorno = new AlertasOcorrencias();
		List<AlertaOcorrencia> listaRet = new ArrayList<AlertaOcorrencia>();
		StringBuilder sbSQL = new StringBuilder();
		
//		logger.info(funcaoSQL);
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
//			sbSQL.append(" SELECT TOP " + qtdeRegistros.toString());
			sbSQL.append(" SELECT ");
			sbSQL.append(" 		  id, ");
			sbSQL.append(" 		  id_tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  id_cad_veiculo_monitorado, ");
			sbSQL.append(" 		  id_status, ");
			sbSQL.append(" 		  status, ");
			sbSQL.append(" 		  data, ");
			sbSQL.append(" 		  placa, ");
			sbSQL.append(" 		  id_motivo_descarte, ");
			sbSQL.append(" 		  motivo_descarte, ");
			sbSQL.append(" 		  id_usuario, ");
			sbSQL.append(" 		  usuario, ");
			sbSQL.append(" 		  tipo_registro, ");
			sbSQL.append(" 		  id_alerta, ");
			sbSQL.append(" 		  cad_monitorado_ativo, ");
			sbSQL.append(" 		  cvm_privado, ");
			sbSQL.append(" 		  assinado, ");
			sbSQL.append(" 		  id_registro_fato, ");
			sbSQL.append(paginacao.QueryTotalRegistros());
			sbSQL.append(" FROM " + funcaoSQL);
			sbSQL.append(" WHERE  1 = 1 ");
//			sbSQL.append("		  AND id_cad_veiculo_monitorado = '0438324D-15F5-4898-BC7D-A0123AFEDE92' ");
			
			int paramIndex = 1;
			Map<Integer, Object> mapaParametros = new LinkedHashMap<Integer, Object>();
			
			if (idCadVeiculoMonitorado != null)
			{
				sbSQL.append("	AND id_cad_veiculo_monitorado = ? ");
				mapaParametros.put(paramIndex++, idCadVeiculoMonitorado);
			}
			if (idRegistroFato != null)
			{
				sbSQL.append("	AND id_registro_fato = ? ");
				mapaParametros.put(paramIndex++, idRegistroFato);
			}
			if (idUsuarioAlerta != null)
			{
				sbSQL.append("	AND id_usuario_alerta = ? ");
				mapaParametros.put(paramIndex++, idUsuarioAlerta);
			}
			if (assinado != null)
			{
				sbSQL.append("	AND assinado = ? ");
				mapaParametros.put(paramIndex++, assinado);
			}
			if (idTipoAlertaOcorrencia != null)
			{
				sbSQL.append("	AND id_tipo_alerta_ocorrencia = ? ");
				mapaParametros.put(paramIndex++, idTipoAlertaOcorrencia);
			}
			if (idStatus != null)
			{
				sbSQL.append("	AND id_status = ? ");
				mapaParametros.put(paramIndex++, idStatus);
			}
			if (placa != null) {
			    String[] arrPlacas = placa.split(",");
			    List<String> placasValidas = new ArrayList<>();

			    for (String p : arrPlacas) {
			        if (p != null && !p.trim().isEmpty()) {
			            placasValidas.add(p.trim().toUpperCase());
			        }
			    }

			    if (!placasValidas.isEmpty()) {
			        sbSQL.append(" AND (");

			        for (int i = 0; i < placasValidas.size(); i++) {
			            String p = placasValidas.get(i);

			            if (p.length() < 7) {
			                sbSQL.append(" placa LIKE ? ");
			                mapaParametros.put(paramIndex++, "%" + p + "%");
			            } else {
			                sbSQL.append(" placa = ? ");
			                mapaParametros.put(paramIndex++, p);
			            }

			            if (i < placasValidas.size() - 1) {
			                sbSQL.append(" OR ");
			            }
			        }

			        sbSQL.append(") ");
			    }
			}
			if (dataIni != null && dataFim != null)
			{
				sbSQL.append("	AND data BETWEEN ? AND ? ");
				mapaParametros.put(paramIndex++, new Timestamp(dataIni.getTime()));
				mapaParametros.put(paramIndex++, new Timestamp(dataFim.getTime()));
			}
			if (idLocal != null)
				sbSQL.append("	AND equipamentos LIKE '%" + String.format("%04d", idLocal) + "%'");
					
			if (privado) {
			    // Mostrar apenas registros privados do próprio usuário
			    sbSQL.append(" AND cvm_privado = 1 AND id_usuario = ? ");
			    mapaParametros.put(paramIndex++, idUsuario);
			} else {
			    // Mostrar tudo que não for privado ou for do próprio usuário
			    sbSQL.append(" AND (cvm_privado = 0 OR (cvm_privado = 1 AND id_usuario = ?)) ");
			    mapaParametros.put(paramIndex++, idUsuario);
			}

			final UUID STATUS_PENDENTE_SUPERVISAO = UUID.fromString("15EBBA5F-C805-449E-83CC-227ED3B3AD3C");

			if (assinadosPendentes) {
			    sbSQL.append(" AND assinado = 1 ");
			    sbSQL.append(" AND id_status = ? ");
			    mapaParametros.put(paramIndex++, STATUS_PENDENTE_SUPERVISAO);

			    // Caso o filtro supervisionado também esteja marcado,
			    // adiciona o filtro extra, mas deixa isso opcional
			    if (supervisionado) {
			        sbSQL.append(" AND cvm_supervisionado = 1 ");
			    }
			} 
			else if (supervisionado) {
			    sbSQL.append(" AND cvm_supervisionado = 1 ");
			}

			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  data DESC ");
			sbSQL.append(paginacao.QueryPaginacao());
			
//			logger.info(sbSQL.toString());

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Iterando e ajustando os valores dos parametros para os wheres (as interrogações):
			Object valorParam = null;
			for (Entry<Integer, Object> paramQuery : mapaParametros.entrySet()) {
				valorParam = paramQuery.getValue();
				
				if (valorParam == null)
					continue;				
				else if (valorParam instanceof UUID)
					ps.setString(paramQuery.getKey(), valorParam.toString());
				else if (valorParam instanceof String)
					ps.setString(paramQuery.getKey(), (String) valorParam);
				else if (valorParam instanceof Integer)
					ps.setInt(paramQuery.getKey(), ((Integer)valorParam).intValue());
				else if (valorParam instanceof Boolean)
					ps.setBoolean(paramQuery.getKey(), ((Boolean)valorParam).booleanValue());				
				else if (valorParam instanceof Timestamp)
					ps.setTimestamp(paramQuery.getKey(), (Timestamp)valorParam);
				else {
					erro = true;
					msgErro = "Ocorreu um erro ao preparar os filtros para a pesquisa!";
					break;
				}
			}
			
			if (!erro) {
				rs = ps.executeQuery();
				
				int registros = 0;
				
				while (rs.next()) 
				{
					if (registros == 0)
						registros = rs.getInt("total_registros");
					
					AlertaOcorrencia item = new AlertaOcorrencia();
	
					item.setId(UUID.fromString(		rs.getString("id")));
					item.setIdTipoAlertaOcorrencia(	UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
					item.setTipoAlertaOcorrencia(	rs.getString("tipo_alerta_ocorrencia"));
					item.setIdCadVeiculoMonitorado(	rs.getString("id_cad_veiculo_monitorado") != null ? UUID.fromString(rs.getString("id_cad_veiculo_monitorado")) : null);
					item.setIdStatus(				UUID.fromString(rs.getString("id_status")));
					item.setStatus(					rs.getString("status"));
					item.setData(					rs.getTimestamp("data"));
					item.setPlaca(					rs.getString("placa"));
					item.setIdMotivoDescarte(		rs.getString("id_motivo_descarte") != null ? UUID.fromString(rs.getString("id_motivo_descarte")) : null);
					item.setMotivoDescarte(			rs.getString("motivo_descarte"));
					item.setIdUsuario(				rs.getInt("id_usuario"));
					item.setUsuario(				rs.getString("usuario"));
					item.setTipoRegistro(			rs.getString("tipo_registro"));
					item.setIdAlerta(				UUID.fromString(rs.getString("id_alerta")));
					item.setCadMonitoradoAtivo(		rs.getBoolean("cad_monitorado_ativo"));
					item.setIdRegistroFato(         rs.getInt("id_registro_fato"));
	
					item.setDataFormatada(item.getDataFormatada());
					item.setHoraFormatada(item.getHoraFormatada());
					
					listaRet.add(item);
				}
				
				paginacao.TotalRegistros(registros);
				retorno.setPaginacao(paginacao);
				retorno.setListaAlertasOcorrencias(listaRet);
			}
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter Alertas/Irregularidades!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao consultar as pesagens no banco de dados!");
			}
		}
		
		return retorno;
	}
	
	public static AlertasOcorrencias ObterListaAlertasOcorrenciasAlt(String funcaoSqlAlt, Integer idLocal, Date dataIni, Date dataFim, UUID idTipoAlertaOcorrencia, Paginacao paginacao) throws ConexaoException, SQLException 
	{
		
		AlertasOcorrencias retorno = new AlertasOcorrencias();
		List<AlertaOcorrencia> listaRet = new ArrayList<AlertaOcorrencia>();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" SELECT id, ");
			sbSQL.append(" 		  id_tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  id_cad_veiculo_monitorado, ");
			sbSQL.append(" 		  id_status, ");
			sbSQL.append(" 		  status, ");
			sbSQL.append(" 		  data, ");
			sbSQL.append(" 		  placa, ");
			sbSQL.append(" 		  id_motivo_descarte, ");
			sbSQL.append(" 		  motivo_descarte, ");
			sbSQL.append(" 		  id_usuario, ");
			sbSQL.append(" 		  usuario, ");
			sbSQL.append(" 		  tipo_registro, ");
			sbSQL.append(" 		  id_alerta, ");
			sbSQL.append(paginacao.QueryTotalRegistros());
			sbSQL.append(" FROM " + funcaoSqlAlt);
			sbSQL.append(" WHERE  1 = 1 ");
			
			int paramIndex = 1;
			Map<Integer, Object> mapaParametros = new LinkedHashMap<Integer, Object>();
			
			if (idLocal != null) {
				sbSQL.append("	AND id_local = ? ");
				mapaParametros.put(paramIndex++, idLocal);
			}
			if (dataIni != null && dataFim != null) {
				sbSQL.append("	AND data BETWEEN ? AND ? ");
				mapaParametros.put(paramIndex++, new Timestamp(dataIni.getTime()));
				mapaParametros.put(paramIndex++, new Timestamp(dataFim.getTime()));
			}
			if (idTipoAlertaOcorrencia != null) {
				sbSQL.append("	AND id_tipo_alerta_ocorrencia = ? ");
				mapaParametros.put(paramIndex++, idTipoAlertaOcorrencia);
			}
			
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  data DESC ");
			sbSQL.append(paginacao.QueryPaginacao());

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Iterando e ajustando os valores dos parametros para os wheres (as interrogações):
			Object valorParam = null;
			for (Entry<Integer, Object> paramQuery : mapaParametros.entrySet()) {
				valorParam = paramQuery.getValue();
				
				if (valorParam == null)
					continue;				
				else if (valorParam instanceof UUID)
					ps.setString(paramQuery.getKey(), valorParam.toString());
				else if (valorParam instanceof String)
					ps.setString(paramQuery.getKey(), (String) valorParam);
				else if (valorParam instanceof Integer)
					ps.setInt(paramQuery.getKey(), ((Integer)valorParam).intValue());
				else if (valorParam instanceof Boolean)
					ps.setBoolean(paramQuery.getKey(), ((Boolean)valorParam).booleanValue());				
				else if (valorParam instanceof Timestamp)
					ps.setTimestamp(paramQuery.getKey(), (Timestamp)valorParam);
				else {
					erro = true;
					msgErro = "Ocorreu um erro ao preparar os filtros para a pesquisa!";
					break;
				}
			}
			
			if (!erro) {
				rs = ps.executeQuery();
				
				int registros = 0;
				
				while (rs.next()) 
				{
					if (registros == 0)
						registros = rs.getInt("total_registros");
					
					AlertaOcorrencia item = new AlertaOcorrencia();
	
					item.setId(UUID.fromString(rs.getString("id")));
					item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
					item.setTipoAlertaOcorrencia(rs.getString("tipo_alerta_ocorrencia"));
					item.setIdCadVeiculoMonitorado(UUID.fromString(rs.getString("id_cad_veiculo_monitorado")));
					item.setIdStatus(UUID.fromString(rs.getString("id_status")));
					item.setStatus(rs.getString("status"));
					item.setData(rs.getTimestamp("data"));
					item.setPlaca(rs.getString("placa"));
					item.setIdMotivoDescarte(rs.getString("id_motivo_descarte") != null ? UUID.fromString(rs.getString("id_motivo_descarte")) : null);
					item.setMotivoDescarte(rs.getString("motivo_descarte"));
					item.setIdUsuario(rs.getInt("id_usuario"));
					item.setUsuario(rs.getString("usuario"));
					item.setTipoRegistro(rs.getString("tipo_registro"));
					item.setIdAlerta(UUID.fromString(rs.getString("id_alerta")));
	
					item.setDataFormatada(item.getDataFormatada());
					item.setHoraFormatada(item.getHoraFormatada());
					
					listaRet.add(item);
				}
				
				paginacao.TotalRegistros(registros);
				retorno.setPaginacao(paginacao);
				retorno.setListaAlertasOcorrencias(listaRet);
			}
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter Alertas/Irregularidades!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao consultar as pesagens no banco de dados!");
			}
		}
		
		return retorno;
	}
	
	public static AlertasOcorrencias ObterAlertasPorCadMonitorado(UUID idCadMonitorado, UUID idStatusAlerta, Paginacao paginacao) throws ConexaoException, SQLException 
	{
		AlertasOcorrencias retorno = new AlertasOcorrencias();
		List<AlertaOcorrencia> listaRet = new ArrayList<AlertaOcorrencia>();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
			sbSQL.append(" SELECT id, ");
			sbSQL.append(" 		  id_tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  id_cad_veiculo_monitorado, ");
			sbSQL.append(" 		  status, ");
			sbSQL.append(" 		  placa, ");
			sbSQL.append(" 		  placa_lida, ");
			sbSQL.append(" 		  data, ");
			sbSQL.append(" 		  id_local, ");
			sbSQL.append(" 		  nome_local, ");
			sbSQL.append(" 		  equipamento, ");
			sbSQL.append(" 		  descartado, ");
			sbSQL.append(" 		  ocorrencia_gerada, ");
			sbSQL.append(" 		  alerta_vinculado ");
			if (paginacao.OperacaoValida())
				sbSQL.append(",").append(paginacao.QueryTotalRegistros());
			
			sbSQL.append(" FROM   muralha.fcn_ObterAlertasAlt() ");
			sbSQL.append(" WHERE  id_cad_veiculo_monitorado = ? ");
			
			if (idStatusAlerta != null)
				sbSQL.append(" 		  AND id_status = ? ");
			
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  data ");
			
			if (paginacao.OperacaoValida())
				sbSQL.append(paginacao.QueryPaginacao());

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			int param = 1;
			ps.setString(param++, idCadMonitorado.toString());
			
			if (idStatusAlerta != null)
				ps.setString(param++, idStatusAlerta.toString());
			
			
			if (!erro) {
				rs = ps.executeQuery();
				
				int registros = 0;
				
				while (rs.next()) 
				{
					if (paginacao.OperacaoValida() && registros == 0)
						registros = rs.getInt("total_registros");
					
					UUID idAlerta = UUID.fromString(rs.getString("id"));
					VeiculoImagem imagem = VeiculoImagens.ObterImagensObjAlertaVinculado(idAlerta);
					
					AlertaOcorrencia item = new AlertaOcorrencia();
					
					item.setId(idAlerta);
					item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
					item.setIdCadVeiculoMonitorado(UUID.fromString(rs.getString("id_cad_veiculo_monitorado")));
					item.setStatus(rs.getString("status"));
					item.setPlaca(rs.getString("placa"));
					item.setPlacaLida(rs.getString("placa_lida"));
					item.setData(rs.getTimestamp("data"));
					item.setIdLocal(rs.getInt("id_local"));
					item.setNomeLocal(rs.getString("nome_local"));
					item.setEquipamento(rs.getString("equipamento"));
					item.setDescartado(rs.getBoolean("descartado"));
					item.setOcorrenciaGerada(rs.getBoolean("ocorrencia_gerada"));
					item.setAlertaVinculado(rs.getBoolean("alerta_vinculado"));
					item.setIdImgObj1(imagem.getIdImgObj1());
					item.setIdImgObj2(imagem.getIdImgObj2());
	
					item.setDataFormatada(item.getDataFormatada());
					item.setHoraFormatada(item.getHoraFormatada());
					
					listaRet.add(item);
				}
				
				if (paginacao.OperacaoValida())
				{
					paginacao.TotalRegistros(registros);
					retorno.setPaginacao(paginacao);
				}
				retorno.setListaAlertasOcorrencias(listaRet);
			}
		}
		catch(Exception e) {
			erro = true;
			msgErro = String.format("Erro ao obter alertas %s por cadastro de monitorado!", (idStatusAlerta != null ? "pendentes" : ""));
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao consultar os alertas no banco de dados!");
			}
		}
		
		return retorno;
	}
	
	public static AlertasOcorrencias ObterAlertasPorVeiculo(UUID idVeiculo) throws ConexaoException, SQLException 
	{
		AlertasOcorrencias retorno = new AlertasOcorrencias();
		List<AlertaOcorrencia> listaRet = new ArrayList<AlertaOcorrencia>();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" SELECT id, ");
			sbSQL.append(" 		  id_tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  id_cad_veiculo_monitorado, ");
			sbSQL.append(" 		  status, ");
			sbSQL.append(" 		  placa, ");
			sbSQL.append(" 		  placa_lida, ");
			sbSQL.append(" 		  data, ");
			sbSQL.append(" 		  id_local, ");
			sbSQL.append(" 		  nome_local, ");
			sbSQL.append(" 		  descartado, ");
			sbSQL.append(" 		  ocorrencia_gerada, ");
			sbSQL.append(" 		  alerta_vinculado, ");
			sbSQL.append(" 		  nome_cad_monitorado, ");
			sbSQL.append(" 		  cad_monitorado_ativo ");
			
			sbSQL.append(" FROM   muralha.fcn_ObterAlertasAlt() ");
			sbSQL.append(" WHERE  id_veiculo_tempo_real = ? ");
			
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  data ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			int param = 1;
			ps.setString(param++, idVeiculo.toString());
			
			
			if (!erro) {
				rs = ps.executeQuery();
				
				while (rs.next()) 
				{
					UUID idAlerta = UUID.fromString(rs.getString("id"));
					VeiculoImagem imagem = VeiculoImagens.ObterImagensObjAlertaVinculado(idAlerta);
					
					AlertaOcorrencia item = new AlertaOcorrencia();
					
					item.setId(idAlerta);
					item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
					item.setTipoAlertaOcorrencia(rs.getString("tipo_alerta_ocorrencia"));
					item.setIdCadVeiculoMonitorado(UUID.fromString(rs.getString("id_cad_veiculo_monitorado")));
					item.setStatus(rs.getString("status"));
					item.setPlaca(rs.getString("placa"));
					item.setPlacaLida(rs.getString("placa_lida"));
					item.setData(rs.getTimestamp("data"));
					item.setIdLocal(rs.getInt("id_local"));
					item.setNomeLocal(rs.getString("nome_local"));
					item.setDescartado(rs.getBoolean("descartado"));
					item.setOcorrenciaGerada(rs.getBoolean("ocorrencia_gerada"));
					item.setAlertaVinculado(rs.getBoolean("alerta_vinculado"));
					item.setNomeCadMonitorado(rs.getString("nome_cad_monitorado"));
					item.setCadMonitoradoAtivo(rs.getBoolean("cad_monitorado_ativo"));
					item.setIdImgObj1(imagem.getIdImgObj1());
					item.setIdImgObj2(imagem.getIdImgObj2());
	
					item.setDataFormatada(item.getDataFormatada());
					item.setHoraFormatada(item.getHoraFormatada());
					
					listaRet.add(item);
				}
				
				retorno.setListaAlertasOcorrencias(listaRet);
			}
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter alertas por veiculo!";
			logger.error(msgErro, e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao consultar os alertas no banco de dados!");
			}
		}
		
		return retorno;
	}
	
	public static DispositivosEquipamentos ObterQuantitativosPorEquipamento( 	String tipoRegistro, 
																	   			UUID idTipoAlertaOcorrencia, 
																	   			int tempoHoras) throws ConexaoException, SQLException 
	{
		
		DispositivosEquipamentos ret = new DispositivosEquipamentos();
		ret.setListaDispositivos(new ArrayList<DispositivoEquipamento>());
		
		String strSQL = "";
		
		if (tipoRegistro.equals("ALERTAS"))
		{	
			strSQL = strSQL + "   SET NOCOUNT ON										 ";
			strSQL = strSQL + "   														 ";
			strSQL = strSQL + "   DECLARE @dataIni datetime, 							 ";
			strSQL = strSQL + "   		@dataFim datetime,                               ";
			strSQL = strSQL + "   		@idTipo uniqueidentifier,                        ";
			strSQL = strSQL + "   		@tempo int;                                      ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   set @tempo = ?                                         ";
			strSQL = strSQL + "   set @dataFim = getdate()                               ";
			strSQL = strSQL + "   set @dataIni = DATEADD(HOUR, -@tempo, @dataFim)        ";
			strSQL = strSQL + "   set @idTipo = ?  									 	 ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   SELECT                                                 ";
			strSQL = strSQL + "   	a.id,                                                ";
			strSQL = strSQL + "   	a.data,                                              ";
			strSQL = strSQL + "   	a.id_tipo_alerta_ocorrencia,                         ";
			strSQL = strSQL + "   	tao.tipo,                                            ";
			strSQL = strSQL + "   	vtr.id as id_veiculo,                                ";
			strSQL = strSQL + "   	lv.id_local                                          ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   	INTO #TEMP_RESULT                                    ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   FROM muralha.alerta a                                  ";
			strSQL = strSQL + "   INNER JOIN muralha.alerta_veiculo av                   ";
			strSQL = strSQL + "   	ON av.id_alerta = a.id                               ";
			strSQL = strSQL + "   INNER JOIN muralha.veiculo_tempo_real vtr              ";
			strSQL = strSQL + "   	ON vtr.id = av.id_veiculo_tempo_real                 ";
			strSQL = strSQL + "   INNER JOIN local_vigente lv                            ";
			strSQL = strSQL + "   	ON lv.id_local = vtr.id_local                        ";
			strSQL = strSQL + "   INNER JOIN muralha.tipo_alerta_ocorrencia tao          ";
			strSQL = strSQL + "   	ON tao.id = a.id_tipo_alerta_ocorrencia              ";
			strSQL = strSQL + "   WHERE                                                  ";
			strSQL = strSQL + "   	a.data BETWEEN @dataIni AND @dataFim                 ";
			strSQL = strSQL + "   	and a.id_tipo_alerta_ocorrencia = @idTipo            ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   SET NOCOUNT OFF;                                       ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   SELECT                                                 ";
			strSQL = strSQL + "   	COUNT(id) qtde,                                      ";
			strSQL = strSQL + "   	id_local,                  							 ";
			strSQL = strSQL + "   	tipo,                  								 ";
			strSQL = strSQL + "   	'Alertas' as tipoReg                  				 ";
			strSQL = strSQL + "   FROM #TEMP_RESULT                                      ";
			strSQL = strSQL + "   GROUP BY                                               ";
			strSQL = strSQL + "   	id_local,                                       	 ";
			strSQL = strSQL + "   	tipo                                       			 ";
		}
		
		else if ( tipoRegistro.equals("IRREGULARIDADES") )
		{
			strSQL = strSQL + "   SET NOCOUNT ON										 ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   DECLARE @dataIni datetime,                             ";
			strSQL = strSQL + "   		@dataFim datetime,                               ";
			strSQL = strSQL + "   		@idTipo uniqueidentifier,                        ";
			strSQL = strSQL + "   		@tempo int;                                      ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   set @tempo = ?                                       	 ";
			strSQL = strSQL + "   set @dataFim = getdate()                               ";
			strSQL = strSQL + "   set @dataIni = DATEADD(HOUR, -@tempo, @dataFim)        ";
			strSQL = strSQL + "   set @idTipo = ?  										 ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   SELECT                                                 ";
			strSQL = strSQL + "   	o.id,                                                ";
			strSQL = strSQL + "   	o.data,                                              ";
			strSQL = strSQL + "   	o.id_tipo_alerta_ocorrencia,                         ";
			strSQL = strSQL + "   	tao.tipo,                                            ";
			strSQL = strSQL + "   	vtr.id as id_veiculo,                                ";
			strSQL = strSQL + "   	lv.id_local                                          ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   	INTO #TEMP_RESULT                                    ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   FROM muralha.ocorrencia o                              ";
			strSQL = strSQL + "   INNER JOIN muralha.alerta a                            ";
			strSQL = strSQL + "   	ON a.id = o.id_alerta                                ";
			strSQL = strSQL + "   INNER JOIN muralha.alerta_veiculo av                   ";
			strSQL = strSQL + "   	ON av.id_alerta = a.id                               ";
			strSQL = strSQL + "   INNER JOIN muralha.veiculo_tempo_real vtr              ";
			strSQL = strSQL + "   	ON vtr.id = av.id_veiculo_tempo_real                 ";
			strSQL = strSQL + "   INNER JOIN local_vigente lv                            ";
			strSQL = strSQL + "   	ON lv.id_local = vtr.id_local                        ";
			strSQL = strSQL + "   INNER JOIN muralha.tipo_alerta_ocorrencia tao          ";
			strSQL = strSQL + "   	ON tao.id = a.id_tipo_alerta_ocorrencia              ";
			strSQL = strSQL + "   WHERE                                                  ";
			strSQL = strSQL + "   	o.data BETWEEN @dataIni AND @dataFim                 ";
			strSQL = strSQL + "   	and o.id_tipo_alerta_ocorrencia = @idTipo            ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   SET NOCOUNT OFF;                                       ";
			strSQL = strSQL + "                                                          ";
			strSQL = strSQL + "   SELECT                                                 ";
			strSQL = strSQL + "   	COUNT(id) qtde,                                      ";
			strSQL = strSQL + "   	id_local,                  							 ";
			strSQL = strSQL + "   	tipo,                  								 ";
			strSQL = strSQL + "   	'Ocorrencias' as tipoReg                  		     ";
			strSQL = strSQL + "   FROM #TEMP_RESULT                                      ";
			strSQL = strSQL + "   GROUP BY                                               ";
			strSQL = strSQL + "   	id_local,                                       	 ";
			strSQL = strSQL + "   	tipo                                       			 ";
		}		
		
		else
		{
			logger.info("Nao foi possivel realizar consulta. Filtro de Tipo de registro incorreto!");
			return ret;
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(strSQL);
			
			ps.setInt(1, tempoHoras);
			ps.setString(2, idTipoAlertaOcorrencia.toString());			
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				DispositivoEquipamento disp = new DispositivoEquipamento();
				disp.setIdDispositivo(rs.getInt("id_local"));
				disp.setQuantitativo(rs.getInt("qtde"));
				disp.setTipo(rs.getString("tipo"));
				disp.setTipoRegistro(rs.getString("tipoReg"));
				
				ret.getListaDispositivos().add(disp);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterQuantitativo):: ", e);
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
		
		return ret;
	}
	
	public static boolean VerificaAcessoPorCadVeiMoniUsuarioId(UUID idCadVeiculoMonitorado, Integer idUsuario) throws ConexaoException, SQLException {
	    StringBuilder sbSQL = new StringBuilder();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    boolean retorno = false;

	    try {
	        sbSQL.append("SELECT privado, id_usuario ");
	        sbSQL.append("FROM muralha.cad_veiculo_monitorado ");
	        sbSQL.append("WHERE id = ?");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());

	        ps.setString(1, idCadVeiculoMonitorado.toString());

	        rs = ps.executeQuery();
	        if (rs.next()) {
	            boolean privado = rs.getBoolean("privado");
	            int usuarioVeiculo = rs.getInt("id_usuario");

	            retorno = !privado || (usuarioVeiculo == idUsuario);
	        }
	    } catch (Exception e) {
	        String msgErro = "Erro ao verificar acesso ao veículo monitorado!";
	        logger.error(msgErro + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar conexão com banco de dados! " + e.getMessage(), e);
	        }
	    }

	    return retorno;
	}
}
