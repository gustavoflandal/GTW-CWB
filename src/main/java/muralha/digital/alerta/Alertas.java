package muralha.digital.alerta;

import java.io.StringWriter;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

import muralha.digital._ini.Inicializacao;
import muralha.digital.veiculo.Veiculo;
import muralha.digital.veiculo.VeiculoAlerta;
import muralha.digital.veiculo.VeiculosAlerta;
import muralha.digital.websocket.ClienteSessoes;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Alertas {

	@XmlTransient
	private Thread tNovosAlertas;
	@XmlTransient
	private Boolean iniciaThread = false;
	@XmlTransient
	public static boolean continua = true;
	@XmlTransient
	private static final Logger logger = Logger.getLogger(Alertas.class);

	@XmlElementWrapper(name = "listaAlertas")
	@XmlElement(name = "alerta")
	private List<Alerta> alertas;

	public List<Alerta> getAlertas() {
		return alertas;
	}

	public void setAlertas(List<Alerta> alerts) {
		this.alertas = alerts;
	}

	// Construtor comum que não inicia
	// thread de processamento
	public Alertas() {
	}



	public Alertas(boolean iniTh) {

		if (Inicializacao.AlertasAtivo.equals("1")) {
			this.iniciaThread = iniTh;
			if (iniciaThread)
				buscaNovosAlertas();
		} else
			logger.info("--- Alertas não habilitados para Muralha Digital");
	}

	public void buscaNovosAlertas() {

		tNovosAlertas = new Thread() {
			public void run() {
				while (continua) {
					try {
						Thread.sleep(2000);

						// logger.info("Thread buscaNovosAlertas:: Sessoes WebSock abertas:: " +
						// ClienteSessoes.ObterQtdeSessoes()) ;

						Boolean isClientes = ClienteSessoes.IsClienteConectado("ALERTA-NOTIFICACAO");

						Alertas alertas = Alertas.ObterNovosAlertas(isClientes);

						if (alertas.getAlertas().size() > 0) {
							logger.info(
									"Thread tNovosAlertas:: Itens encontrados, qtde: " + alertas.getAlertas().size());

							// Formando dados para envio
							JAXBContext alertas_context = JAXBContext.newInstance(Alertas.class);
							Marshaller marsHall = alertas_context.createMarshaller();
							marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

							StringWriter sw = new StringWriter();
							marsHall.marshal(alertas, sw);
							String xml = sw.toString();
							sw.close();

//							logger.info(xml);

							ClienteSessoes.EnviaTextoClientes("ALERTA-NOTIFICACAO", xml);
						}
					} catch (Exception e) {
						logger.error("Falha na thread de buscar de alertas e envio aos clientes websocket(browser)."
								+ e.getMessage(), e);
					}
				}
			}

		};

		tNovosAlertas.start();
	}

	/*
	 * Retornar lista com as Alertas NOVOS e pendentes
	 */
	public static Alertas ObterNovosAlertas(Boolean isClientes) {

		Alertas retAlertas = new Alertas();
		List<Alerta> listaAlerta = new ArrayList<Alerta>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		try {

			sbSQL.append(" EXEC muralha.spu_ObterNovosAlertas ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setBoolean(1, isClientes);

			rs = ps.executeQuery();

			while (rs.next()) {
				Alerta alert = new Alerta();

				logger.info("NOVO ALERTA");

				alert.setId(UUID.fromString(rs.getString("id")));
				alert.setIdTipoAlerta(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				alert.setTipoAlerta(rs.getString("tipo_alerta_ocorrencia"));
				alert.setIdVeiculoMonitorado(UUID.fromString(rs.getString("id_cad_veiculo_monitorado")));
				alert.setPlacaCadastro(rs.getString("placa"));
				alert.setDataCadVeicMonitorado(rs.getTimestamp("veiculo_monitorado_datacad"));
				alert.setIdStatusAlerta(UUID.fromString(rs.getString("id_status_alerta")));
				alert.setStatusAlertaDesc(rs.getString("status_alerta_desc"));
				alert.setDataAlerta(rs.getTimestamp("data_alerta"));
				alert.setDataPassagem(rs.getTimestamp("data_passagem"));
				alert.setEnviadoAoCliente(rs.getInt("enviado_cliente"));
				alert.setDataEnviadoCliente(rs.getTimestamp("data_enviado"));
				alert.setEquipamento(rs.getString("equipamento"));
				alert.setSupervisionado(rs.getInt("supervisionado"));
				alert.setCom_semelhanca(rs.getInt("com_semelhanca"));
				alert.setCom_semelhanca_erros(rs.getInt("com_semelhanca_erros"));
				alert.setCom_semelhanca_desc(rs.getString("com_semelhanca_desc"));
				alert.setSom(rs.getString("som"));
				alert.setIdUsuarioResponsavel(rs.getInt("id_usuario_responsavel"));
				alert.setPossui_bo_alerta(rs.getString("requer_e_possui"));

				listaAlerta.add(alert);
			}
		} catch (Exception e) {
			logger.error("Erro ao obter Lista de Novos Alertas: " + e.getMessage(), e);
		} finally {

			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}

		}

		retAlertas.setAlertas(listaAlerta);
		return retAlertas;
	}

	/*
	 * Retornar dados do alerta para ID solicitado.
	 */
	public static List<Alerta> obterAlertaPorId(UUID id) {
		List<Alerta> alerta = new ArrayList<Alerta>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		try {
			sbSQL.append(" EXEC muralha.spu_ObterDadosAlertaOcorrencia ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setString(1, id.toString());

			rs = ps.executeQuery();

			List<VeiculoAlerta> veiculos = null;

			while (rs.next()) {
				if (veiculos == null)
					veiculos = VeiculosAlerta.ObterListaVeiculosPorIdAlerta(UUID.fromString(rs.getString("id")));

				Alerta item = new Alerta();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setIdTipoAlerta(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				item.setTipoAlerta(rs.getString("tipo_alerta_ocorrencia"));

				item.setIdCadVeicMonitorado(rs.getString("id_cad_veiculo_monitorado") != null
						? UUID.fromString(rs.getString("id_cad_veiculo_monitorado"))
						: null);
				item.setPlacaCadastro(rs.getString("placa_cadastro"));
				item.setStatusAlertaDesc(rs.getString("status_alerta"));
				item.setDataAlerta(rs.getTimestamp("data_alerta"));
				item.setIdLocal(rs.getInt("id_local"));
				item.setSerieEquipamento(rs.getInt("serie_equipamento"));
				item.setEquipamento(rs.getString("equipamento"));
				item.setIdPista(rs.getInt("id_pista"));
				item.setFaixa(rs.getInt("faixa"));
				item.setVelocidade(rs.getInt("velocidade"));
				item.setLatitude(rs.getFloat("latitude"));
				item.setLongitude(rs.getFloat("longitude"));
				item.setPlacaVeiculo(rs.getString("placa_veiculo"));
				item.setDataVeiculo(rs.getTimestamp("data_veiculo"));
				//item.setEmAtendimentoPor(rs.getString("em_atendimento_por"));
				//item.setAtendido(rs.getInt("atendido"));

				item.setIdMotivoDescarte(
						rs.getString("id_motivo_descarte") != null ? UUID.fromString(rs.getString("id_motivo_descarte"))
								: null);
				item.setMotivoDescarte(rs.getString("motivo_descarte"));
				item.setObservacao(rs.getString("observacao"));
				item.setDescartado(rs.getBoolean("descartado"));

				item.setIdOcorrencia(
						rs.getString("id_ocorrencia") != null ? UUID.fromString(rs.getString("id_ocorrencia")) : null);
				item.setOcorrenciaGerada(rs.getBoolean("ocorrencia_gerada"));
				item.setOcorrenciaComNotificacao(rs.getBoolean("ocorrencia_com_notificacao"));
				item.setIdTipoRegistro(UUID.fromString(rs.getString("id_tipo_registro")));
				item.setTipoRegistro(rs.getString("tipo_registro"));

				item.setIdStatusOcorrencia(rs.getString("id_status_ocorrencia") != null
						? UUID.fromString(rs.getString("id_status_ocorrencia"))
						: null);
				item.setStatusOcorrencia(rs.getString("status_ocorrencia"));
				item.setOcorrenciaFinalizada(rs.getBoolean("ocorrencia_finalizada"));
				item.setObsFinalizarOcorrencia(rs.getString("obs_finalizar_ocorrencia"));

				item.setIdPontoInteresse(
						rs.getString("id_ponto_interesse") != null ? UUID.fromString(rs.getString("id_ponto_interesse"))
								: null);
				item.setNomePontoInteresse(rs.getString("nome_ponto_interesse"));
				item.setAlertaVinculado(rs.getBoolean("alerta_vinculado"));
				item.setIdAlertaVinculado(rs.getString("id_alerta_vinculado") != null
						? UUID.fromString(rs.getString("id_alerta_vinculado"))
						: null);
				
				item.setPermiteAtendimento(rs.getBoolean("permite_atendimento"));
				item.setPermiteAlterarAtendimento(rs.getBoolean("permite_alterar_atendimento"));

				item.setVeiculosAlerta(veiculos);

				item.setDataAlertaFormatada(item.getDataAlertaFormatada());
				item.setHoraAlertaFormatada(item.getHoraAlertaFormatada());

				item.setDataVeiculoFormatada(item.getDataVeiculoFormatada());
				item.setHoraVeiculoFormatada(item.getHoraVeiculoFormatada());
				item.setSupervisionado(rs.getInt("supervisionado"));		
				item.setAssinado(rs.getBoolean("assinado"));
				alerta.add(item);
			}
		}catch (Exception e) {
			logger.error("Erro ao obter Alerta [" + id + "]: " + e.getMessage(), e);
			throw new RuntimeException("Erro inesperado ao obter alerta.", e);
		}finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}

		}

		return alerta;
	}

	/*
	 * Retornar dados do alerta para ID solicitado.
	 */
	public static List<Veiculo> ObterVeiculosAlertaMapaPorId(UUID id) {
		List<Veiculo> veiculosAlerta = new ArrayList<Veiculo>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		try {
			sbSQL.append(" SELECT id_veiculo_tempo_real, ");
			sbSQL.append(" 		  placa_veiculo, ");
			sbSQL.append(" 		  data_veiculo, ");
			sbSQL.append(" 		  id_local, ");
			sbSQL.append(" 		  serie_equipamento, ");
			sbSQL.append(" 		  equipamento, ");
			sbSQL.append(" 		  id_pista, ");
			sbSQL.append(" 		  latitude, ");
			sbSQL.append(" 		  longitude, ");
			sbSQL.append(" 		  velocidade ");
			sbSQL.append(" FROM   muralha.fcn_ObterDadosAlertaOcorrencia(?) ");
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  data_veiculo ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setString(1, id.toString());

			rs = ps.executeQuery();

			while (rs.next()) {
				Veiculo item = new Veiculo();

				item.setId(UUID.fromString(rs.getString("id_veiculo_tempo_real")));
				item.setPlaca(rs.getString("placa_veiculo"));
				item.setDataVeic(rs.getTimestamp("data_veiculo"));
				item.setIdLocal(rs.getInt("id_local"));
				item.setSerieEquipamento(rs.getInt("serie_equipamento"));
				item.setDescLocal(rs.getString("equipamento"));
				item.setIdPista(rs.getInt("id_pista"));
				item.setLatitude(rs.getFloat("latitude"));
				item.setLongitude(rs.getFloat("longitude"));
				item.setVelocidade(rs.getInt("velocidade"));

				veiculosAlerta.add(item);
			}
		} catch (Exception e) {
			logger.error("Erro ao obter Alerta: " + e.getMessage(), e);
		} finally {

			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}

		}

		return veiculosAlerta;
	}

	public static boolean DescartarAlerta(UUID idAlerta, UUID idStatusAlertaDescartado, UUID idMotivoDescarte,
			String strObsDescarteAlerta, Integer idUsuario) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false;

		try {
			sbSQL.append(" UPDATE muralha.alerta ");
			sbSQL.append(" SET    id_status_alerta = ?, ");
			sbSQL.append(" 		  id_motivo_descarte = ?, ");
			sbSQL.append(" 		  observacao = ?, ");
			sbSQL.append(" 		  id_usuario = ?, ");
			sbSQL.append(" 		  data_descarte = ?, ");
			sbSQL.append(" 		  lembrete_visualizado = 2 ");
			sbSQL.append(" WHERE id = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setString(1, idStatusAlertaDescartado.toString());
			ps.setString(2, idMotivoDescarte.toString());
			ps.setString(3, strObsDescarteAlerta.toString());
			ps.setInt(4, idUsuario);
			ps.setTimestamp(5, new Timestamp(new Date().getTime()));
			ps.setString(6, idAlerta.toString());

			retorno = (ps.executeUpdate() == 1);
		} catch (Exception e) {
			String msgErro = "Erro ao descartar alerta!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {

			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}

		return retorno;
	}

	public static boolean DescartarAlertasVinculados(UUID idAlertaVinculado, UUID idMotivoDescarteAlertaVinculado,
			String msgDescartadeAlertaVinculado, Integer idUsuario, AlertasVinculadosAtualizar alertasVinculados)
			throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();
		String sqlAux = "";

		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false, erro = false;
		String msgErro = "";

		try {
			int paramIndex = 1;
			Map<Integer, Object> mapaParametros = new LinkedHashMap<Integer, Object>();

			Date dataDescarte = new Date();

			for (AlertaVinculadoAtualizar alertaVinculado : alertasVinculados.getAlertasVinculadosAtualizar()) {
				sqlAux = sqlAux
						+ " UPDATE muralha.alerta SET id_status_alerta = ?, id_motivo_descarte = ?, observacao = ?, id_usuario = ?,"
						+ " data_descarte = ?, lembrete_visualizado = 2, alerta_vinculado = ?, id_alerta_vinculado = ?, data_modificacao = ? WHERE id = ? ";

				mapaParametros.put(paramIndex++, alertaVinculado.getIdStatusAlerta());
				mapaParametros.put(paramIndex++, idMotivoDescarteAlertaVinculado);
				mapaParametros.put(paramIndex++, msgDescartadeAlertaVinculado);
				mapaParametros.put(paramIndex++, idUsuario);
				mapaParametros.put(paramIndex++, new java.sql.Timestamp(dataDescarte.getTime()));
				mapaParametros.put(paramIndex++, true);
				mapaParametros.put(paramIndex++, idAlertaVinculado);
				mapaParametros.put(paramIndex++, new java.sql.Timestamp(dataDescarte.getTime()));
				mapaParametros.put(paramIndex++, alertaVinculado.getIdAlerta());
			}

			sbSQL.append(sqlAux);

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Iterando e ajustando os valores dos parametros para os wheres (as
			// interrogações):
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
					ps.setInt(paramQuery.getKey(), ((Integer) valorParam).intValue());
				else if (valorParam instanceof Boolean)
					ps.setBoolean(paramQuery.getKey(), ((Boolean) valorParam).booleanValue());
				else if (valorParam instanceof Timestamp)
					ps.setTimestamp(paramQuery.getKey(), (Timestamp) valorParam);
				else {
					erro = true;
					msgErro = "Ocorreu um erro ao preparar os filtros para processamento!";
					break;
				}
			}

			if (!erro) {
				retorno = (ps.executeUpdate() == 1);
			}

		} catch (Exception e) {
			erro = true;
			retorno = false;
			msgErro = "Erro ao processar Alertas Vinculados!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}

		return retorno;
	}

	public static boolean ProcessarAlertasVinculados(UUID idAlertaVinculado, Integer idUsuario,
			AlertasVinculadosAtualizar alertasVinculados) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();
		String sqlAux = "";

		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false, erro = false;
		String msgErro = "";

		try {
			int paramIndex = 1;
			Map<Integer, Object> mapaParametros = new LinkedHashMap<Integer, Object>();

			Date dataModificacao = new Date();

			for (AlertaVinculadoAtualizar alertaVinculado : alertasVinculados.getAlertasVinculadosAtualizar()) {
				sqlAux = sqlAux
						+ " UPDATE muralha.alerta SET id_status_alerta = ?, alerta_vinculado = ?, id_alerta_vinculado = ?, id_usuario = ?, data_modificacao = ?, lembrete_visualizado = 2 WHERE id = ? ";

				mapaParametros.put(paramIndex++, alertaVinculado.getIdStatusAlerta());
				mapaParametros.put(paramIndex++, true);
				mapaParametros.put(paramIndex++, idAlertaVinculado);
				mapaParametros.put(paramIndex++, idUsuario);
				mapaParametros.put(paramIndex++, new java.sql.Timestamp(dataModificacao.getTime()));
				mapaParametros.put(paramIndex++, alertaVinculado.getIdAlerta());
			}

			sbSQL.append(sqlAux);

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Iterando e ajustando os valores dos parametros para os wheres (as
			// interrogações):
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
					ps.setInt(paramQuery.getKey(), ((Integer) valorParam).intValue());
				else if (valorParam instanceof Boolean)
					ps.setBoolean(paramQuery.getKey(), ((Boolean) valorParam).booleanValue());
				else if (valorParam instanceof Timestamp)
					ps.setTimestamp(paramQuery.getKey(), (Timestamp) valorParam);
				else {
					erro = true;
					msgErro = "Ocorreu um erro ao preparar os filtros para processamento!";
					break;
				}
			}

			if (!erro) {
				retorno = (ps.executeUpdate() == 1);
			}

		} catch (Exception e) {
			erro = true;
			retorno = false;
			msgErro = "Erro ao processar Alertas Vinculados!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}

		return retorno;
	}

	public static List<Alerta> ObterAlertasNaoTratados() {
		List<Alerta> alerta = new ArrayList<Alerta>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		try {
			sbSQL.append(" EXEC muralha.spuObterNotificacoesNaoTratadas ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				Alerta item = new Alerta();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setIdTipoAlerta(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				item.setTipoAlerta(rs.getString("tipo"));
				item.setDtAlertaStr(rs.getString("data"));
				item.setPlacaVeiculo(rs.getString("placa"));
				item.setLembrete(rs.getInt("lembrete_visualizado"));
				item.setTotalRegistros(rs.getInt("total_registros"));				

				alerta.add(item);
			}
		} catch (Exception e) {
			logger.error("Erro ao obter ObterAlertasNaoTratados(): " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return alerta;
	}
	
	public static void AtualizaAtendente(String idAlerta, String usuario) {
		
		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false;
		
		StringBuilder sbSQL = new StringBuilder();

		try {
			sbSQL.append("UPDATE muralha.alerta SET em_atendimento_por = ? WHERE id = ?");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setString(1, usuario);
			ps.setString(2, idAlerta);

			retorno = (ps.executeUpdate() == 1);
		} catch (Exception e) {
			String msgErro = "Erro ao descartar alerta!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {

			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		
		logger.debug("Atendente atualizado! retorno: " + String.valueOf(retorno));
	}
	
	public static String PodeAtender(String idAlerta, String usuario) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String emAtendimentoPor = null;

		try {
			String sql = "EXEC muralha.spu_ObterDadosAlertaOcorrencia ?";
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);
			ps.setString(1, idAlerta);
			rs = ps.executeQuery();

			if (rs.next()) {
				emAtendimentoPor = rs.getString("em_atendimento_por");
			}

		} catch (Exception e) {
			logger.error("Erro ao obter responsável pelo atendimento: " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos do banco: " + e.getMessage(), e);
			}
		}

		return emAtendimentoPor;
	}
	
	public static Alerta ObtemAlerta(String idAlerta) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Alerta alerta = null;

		try {
			String sql = "EXEC muralha.spu_ObterDadosAlertaOcorrencia ?";
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);
			ps.setString(1, idAlerta);
			rs = ps.executeQuery();

			if (rs.next()) {
				alerta = new Alerta();
				alerta.setId(UUID.fromString(rs.getString("id")));
				alerta.setPlacaVeiculo(rs.getString("placa"));
				alerta.setEmAtendimentoPor(rs.getString("em_atendimento_por"));
				alerta.setAtendido(rs.getInt("atendido"));
			}

		} catch (Exception e) {
			logger.error("Erro ao obter alerta: " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos do banco: " + e.getMessage(), e);
			}
		}

		return alerta;
	}
	
	public static boolean criarAlerta(UUID idTipoAlerta, UUID idVeiculoMonitorado, UUID idVeiculoTempoReal) {
		Connection conn = null;
		PreparedStatement pstAlerta = null;
		PreparedStatement pstVeiculo = null;

		try {
			UUID idAlerta = UUID.randomUUID();

			conn = Conexao.getConexao();
			conn.setAutoCommit(false);

			StringBuilder sbAlerta = new StringBuilder();
			sbAlerta.append("INSERT INTO muralha.alerta (");
			sbAlerta.append("id, id_tipo_alerta_ocorrencia, data, ");
			sbAlerta.append("lembrete_visualizado, id_cad_veiculo_monitorado, ");
			sbAlerta.append("em_atendimento_por, atendido, id_status_alerta) ");
			sbAlerta.append("VALUES (?, ?, GETDATE(), 0, ?, NULL, 0, ");
			sbAlerta.append("'5479C6D9-7381-4492-99BE-442EF2E741B0')"); 

			String sqlAlerta = sbAlerta.toString();
			pstAlerta = conn.prepareStatement(sqlAlerta);
			pstAlerta.setObject(1, idAlerta);
			pstAlerta.setObject(2, idTipoAlerta);
			pstAlerta.setObject(3, idVeiculoMonitorado);
			pstAlerta.executeUpdate();

			StringBuilder sbVeiculo = new StringBuilder();
			sbVeiculo.append("INSERT INTO muralha.alerta_veiculo (id_alerta, id_veiculo_tempo_real) ");
			sbVeiculo.append("VALUES (?, ?)");

			String sqlVeiculo = sbVeiculo.toString();
			pstVeiculo = conn.prepareStatement(sqlVeiculo);
			pstVeiculo.setObject(1, idAlerta);
			pstVeiculo.setObject(2, idVeiculoTempoReal);
			pstVeiculo.executeUpdate();

			conn.commit();
			return true;

		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				if (conn != null) conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;

		} finally {
			try {
				if (pstVeiculo != null) pstVeiculo.close();
				if (pstAlerta != null) pstAlerta.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static UUID getIdVeiculoMonitoradoPorPlaca(String placa) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT id FROM muralha.veiculo_monitorado WHERE placa = ?";
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);
			ps.setString(1, placa);
			rs = ps.executeQuery();

			if (rs.next()) {
				return UUID.fromString(rs.getString("id"));
			}

		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}

		return null; 
	}

	public static UUID getIdVeiculoTempoRealPorPlaca(String placa) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT id FROM muralha.veiculo_tempo_real WHERE placa = ?";
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);
			ps.setString(1, placa);
			rs = ps.executeQuery();

			if (rs.next()) {
				return UUID.fromString(rs.getString("id"));
			}

		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}

		return null; 
	}
	
	public static boolean AtualizarAlertaModalAcao(UUID idAlerta, boolean assinado, Integer idUsuario) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false;

		try {
			sbSQL.append(" UPDATE muralha.alerta ");
			sbSQL.append(" SET    assinado = ?, ");
			sbSQL.append("        data_modificacao = ? ");
			sbSQL.append(" WHERE  id = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setBoolean(1, assinado);
			ps.setTimestamp(2, new Timestamp(new Date().getTime()));
			ps.setString(3, idAlerta.toString());

			retorno = (ps.executeUpdate() == 1);
		} catch (Exception e) {
			String msgErro = "Erro ao atualizar campo 'assinado' do alerta!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
						conn.close();
				if (ps != null)
						ps.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão com banco de dados! " + e.getMessage(), e);
			}
		}

		return retorno;
	}
	
	public static List<Alerta> obterAssinadoAlertaPorId(UUID idAlerta) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Alerta> alertas = new ArrayList<Alerta>();
		try {
			sbSQL.append("SELECT id, assinado ");
			sbSQL.append("FROM muralha.alerta ");
			sbSQL.append("WHERE id = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, idAlerta.toString());

			rs = ps.executeQuery();

			if (rs.next()) {
				Alerta alerta = new Alerta();
				alerta.setId(UUID.fromString(rs.getString("id")));
				alerta.setAssinado(rs.getBoolean("assinado"));
				alertas.add(alerta);
				return alertas;
			} else {
				return null;
			}
		} catch (Exception e) {
			String msgErro = "Erro ao consultar alerta por ID!";
			logger.error(msgErro + ": " + e.getMessage(), e);
			throw new SQLException(msgErro, e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos do banco! " + e.getMessage(), e);
			}
		}
	}
	
	public static boolean VerificaAcessoPorAlertaUsuarioId(UUID idAlerta, Integer idUsuario) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean retorno = false;

		try {
			sbSQL.append("SELECT cvm.privado, cvm.id_usuario ");
			sbSQL.append("FROM muralha.alerta a ");
			sbSQL.append("JOIN muralha.cad_veiculo_monitorado cvm ON a.id_cad_veiculo_monitorado = cvm.id ");
			sbSQL.append("WHERE a.id = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setString(1, idAlerta.toString());

			rs = ps.executeQuery();
			if (rs.next()) {
				boolean privado = rs.getBoolean("privado");
				int usuarioVeiculo = rs.getInt("id_usuario");

				// Se não for privado, qualquer um pode acessar
				// Se for privado, só o usuário dono pode acessar
				retorno = !privado || (usuarioVeiculo == idUsuario);
			}
		} catch (Exception e) {
			String msgErro = "Erro ao verificar acesso ao alerta!";
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

	/* 
		Nessa função abaixo, estarei iniciando com um valor de retorno falso, então faço a chamada do
		banco para pegar a quantia de alertas pendentes tem, caso tenha uma ou mais, o valor de retorno será true,
		assim a tela pós login será a de alertas, caso seja falso, será a tela de login padrão.
	*/
	public static boolean buscarAlertasPendentes() throws ConexaoException, SQLException {
		boolean retorno = false;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();

		try {
			sbSQL.append("SELECT COUNT(a.id) AS alerta FROM muralha.alerta a ");
			sbSQL.append("JOIN muralha.status_alerta sa ");
			sbSQL.append("ON sa.id = a.id_status_alerta ");
			sbSQL.append("WHERE UPPER(sa.descricao) = UPPER('pendente') ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();

			if(rs.next()) {
				int alerta = rs.getInt("alerta");

				retorno = alerta > 0 ? true : false;
			}
		} catch (Exception e) {
			String msgErro = "Erro ao verificar acesso ao alerta!";
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

	/*
		Nessa função abaixo, estarei iniciando com um valor de retorno falso, então faço a chamada do
		banco para pegar o id_registro_fato do veículo do alerta selecionado, caso esse id não seja nulo e nem zero
		o valor de retorno será true, permitindo a abertura (ou não) do atendimento, caso seja falso, a checkbox será desabilitada.
	*/
	public static boolean permitirAberturaAtendimento(String strIdOcorrencia) throws ConexaoException, SQLException {
		boolean retorno = false;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();

		try {
			sbSQL.append("SELECT cvm.id_registro_fato FROM muralha.cad_veiculo_monitorado cvm 	");
			sbSQL.append("JOIN muralha.alerta a on a.id_cad_veiculo_monitorado = cvm.id 			");
			sbSQL.append("LEFT JOIN muralha.ocorrencia o ON o.id_alerta = a.id 							");
			sbSQL.append("WHERE a.id = ?																			");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setObject(1, UUID.fromString(strIdOcorrencia));
			rs = ps.executeQuery();

			if(rs.next()) {
				int id = rs.getInt("id_registro_fato");

				if (!rs.wasNull() && id != 0) {
					retorno = true;
				}
			}
		} catch(Exception e) {
			logger.error("Erro ao obter Abertura do Atendimento: " + e.getMessage(), e);
		} finally {
			try {				
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return retorno;
	}
	
	public static Alerta ObterAlertasPendentesAssinatura(int idUsuario) throws ConexaoException, SQLException {
		Alerta retorno = new Alerta();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();

		try {
			sbSQL.append(" SELECT COUNT(a.assinado) AS pendentes FROM muralha.alerta a								");
			sbSQL.append(" JOIN muralha.ocorrencia o on o.id_alerta = a.id											");
			sbSQL.append(" JOIN muralha.cad_veiculo_monitorado cvm ON cvm.id = a.id_cad_veiculo_monitorado			");
			sbSQL.append(" WHERE a.assinado = 0 AND a.id_status_alerta = '15EBBA5F-C805-449E-83CC-227ED3B3AD3C'		");
			sbSQL.append(" AND o.id_status_ocorrencia = '3C0612D6-3950-4861-8CA4-2A261AE787AF' AND a.id_usuario = ?	");
			sbSQL.append(" AND (cvm.privado = 0 OR (cvm.privado = 1 AND a.id_usuario = ?))							");
			

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);
			ps.setInt(2, idUsuario);
			rs = ps.executeQuery();

			while(rs.next()) {
				retorno.setNao_assinados(rs.getInt("pendentes"));
				
			}
		} catch(Exception e) {
			logger.error("Erro ao obter Abertura do Atendimento: " + e.getMessage(), e);
		} finally {
			try {				
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return retorno;
	}
	
}