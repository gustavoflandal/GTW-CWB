/**
 * 
 */
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.TConfigEquip.TAfericao;
import com.consilux.model.TConfigEquip.TCamera;
import com.consilux.model.TConfigEquip.TCameraTypes;
import com.consilux.model.TConfigEquip.TCanalPP;
import com.consilux.model.TConfigEquip.TChannelConfigs;
import com.consilux.model.TConfigEquip.TCanalPL;
import com.consilux.model.TConfigEquip.TCollection;
import com.consilux.model.TConfigEquip.TConfigAGD_Device;
import com.consilux.model.TConfigEquip.TConfigAGD_Road;
import com.consilux.model.TConfigEquip.TConfigAGD_Software;
import com.consilux.model.TConfigEquip.TConfigDimensoesML;
import com.consilux.model.TConfigEquip.TConfigLacoVirtualML;
import com.consilux.model.TConfigEquip.TConfigPesagem;
import com.consilux.model.TConfigEquip.TConfigPistaPesagem;
import com.consilux.model.TConfigEquip.TConfiguracaoDIV;
import com.consilux.model.TConfigEquip.TConfiguracaoGeralDivs;
import com.consilux.model.TConfigEquip.TConfiguracaoGeralPainel;
import com.consilux.model.TConfigEquip.TConfiguracaoPainel;
import com.consilux.model.TConfigEquip.TConfiguracaoRelevante;
import com.consilux.model.TConfigEquip.TControladorPL;
import com.consilux.model.TConfigEquip.TControladorPP;
import com.consilux.model.TConfigEquip.THorario;
import com.consilux.model.TConfigEquip.TLocal;
import com.consilux.model.TConfigEquip.TLocalidade;
import com.consilux.model.TConfigEquip.TLoopDetectorChannelConfig;
import com.consilux.model.TConfigEquip.TNivelVideo;
import com.consilux.model.TConfigEquip.TPista;
import com.consilux.model.TConfigEquip.TRegraInfracao;
import com.consilux.model.TConfigEquip.TResolucaoImagem;
import com.consilux.model.TConfigEquip.TRodizio;
import com.consilux.model.TConfigEquip.TRodovia;
import com.consilux.model.TConfigEquip.TSensorPiezo;
import com.consilux.model.TConfigEquip.TServidor;
import com.consilux.model.TConfigEquip.TStringList;
import com.consilux.model.beans.GrupoBean;
import com.consilux.model.exception.ModelException;

/**
 * @author fos
 *
 */
public class ConfiguracaoEquipamento {
	private static final int ERRO_FK = 547;
	Integer idConfigEquip;
	TConfigEquip tconfigEqup;
	
	public ConfiguracaoEquipamento(Integer idConfigEquip, TConfigEquip tconfigEqup) {
		super();
		this.idConfigEquip = idConfigEquip;
		this.tconfigEqup = tconfigEqup;
	}

	public static ConfiguracaoEquipamento buscarConfigEquipPorId(Integer idConfigEquip) throws ConexaoException {
		ConfiguracaoEquipamento ret = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....		
		sbSQL.append("   SELECT ");
		sbSQL.append("		serie_equipamento, ");
		sbSQL.append("		obs,");
		sbSQL.append("		watch_dog,");
		sbSQL.append("		controladora,");
		sbSQL.append("		iluminador,");
		sbSQL.append("		id_produto,");
		sbSQL.append("		com_controladora,");
		sbSQL.append("		com_auxiliar,");
		sbSQL.append("		com_iluminador,");
		sbSQL.append("		id_grupo_equipamento,");
		sbSQL.append("		flag_opcao,");
		sbSQL.append("		data_modificacao,");
		sbSQL.append("		data_inicio,");
		sbSQL.append("		categoria,");
		
		sbSQL.append("      distancia_equipamento,");
		sbSQL.append("      tempo_ciclagem,");
		
		sbSQL.append("      TempoTotalVideo,");
		sbSQL.append("      TempoVideoAntesInfracao,");
		
		sbSQL.append("      ativar_montante,");
		sbSQL.append("      ativar_jusante,");
		sbSQL.append("      porta_montante,");
		sbSQL.append("      codigo_montante,");
		
		sbSQL.append("      cod_GIT_Contrato,");
		sbSQL.append("      cod_GIT_Ponto,");
		
		sbSQL.append("      COALESCE(CodigoEquipCliente,'') AS CodigoEquipCliente,");
		
		sbSQL.append("  	tempo_adicional_faixa_exclusiva,");
		sbSQL.append("  	tempo_fluxo_zero,");
		sbSQL.append("  	diferenca_percentual_bloqueio_faixa ");
		
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			if (rs.next()) {
				TConfigEquip ce = new TConfigEquip();
				ce.serie = rs.getInt("serie_equipamento");
				ce.observacao = rs.getString("obs");
				ce.watchDog = rs.getInt("watch_dog");
				ce.controladora = rs.getInt("controladora");
				ce.iluminador = rs.getInt("iluminador");
				ce.idProduto = rs.getInt("id_produto");
				ce.comControladora = rs.getInt("com_controladora");
				ce.comAuxiliar = rs.getInt("com_auxiliar");
				ce.comIluminador = rs.getInt("com_iluminador");
				ce.idGrupoEquipamento = rs.getInt("id_grupo_equipamento");
				ce.flagOpcao.setCombinacao(rs.getInt("flag_opcao"));
				ce.dataHoraConfiguracao = rs.getTimestamp("data_modificacao") != null ? Funcoes.convertUTCToPascalDate(rs.getTimestamp("data_modificacao")) : 0;
				ce.inicioOperacao = rs.getTimestamp("data_inicio") != null ? Funcoes.convertUTCToPascalDate(rs.getTimestamp("data_inicio")) : 0;
				ce.categoria = rs.getInt("categoria");
				
				ce.local.cod_GIT_Contrato = rs.getInt("cod_GIT_Contrato");
				ce.local.cod_GIT_Ponto = rs.getInt("cod_GIT_Ponto");		
				
				ce.local.codigoEquipCliente = rs.getString("CodigoEquipCliente");
				
				ce.tempoAdicionalFaixaExclusiva = rs.getInt("tempo_adicional_faixa_exclusiva");
				ce.tempoFluxoZero = rs.getInt("tempo_fluxo_zero");
				ce.diferencaPercentualBloqueioFaixa = rs.getInt("diferenca_percentual_bloqueio_faixa");

				buscarLocal(idConfigEquip, ce.local, ce.localidade);
				buscarConfiguracaoRelevante(idConfigEquip, ce.configuracaoRelevante);
				buscarConfiguracaoPesagem(idConfigEquip, ce.Pesagem);
				buscarConfiguracaoLacoVirtualML(idConfigEquip, ce.LacoVirtualML);
				buscarConfiguracaoDimensoesML(idConfigEquip, ce.DimensoesML);
				buscarConfiguracaoResolucaoImagem(idConfigEquip, ce.ResolucaoImagem);
				buscarRodovia(idConfigEquip, ce.rodovia);
				buscarAfericao(idConfigEquip, 0, ce.afericao);
				buscarCameras(idConfigEquip, ce.cameras);
				buscarControladores(idConfigEquip, ce.controladoresPL);
				buscarControladoresPesagem(idConfigEquip, ce.controladoresPP);
				buscarPistas(idConfigEquip, ce.pistas);
				buscarHorarios(idConfigEquip, ce.horarios);
				buscarNiveisVideo(idConfigEquip, ce.niveisVideo);
				buscarConfiguracoesGeralDiv(idConfigEquip, ce.divsGeral);
				buscarConfiguracoesDIV(idConfigEquip, ce.configuracaoDIVs);
				buscarRodizios(idConfigEquip, ce.rodizios);
				buscarRegrasInfracao(idConfigEquip, ce.regrasInfracao);
				buscarParametrosAdicionais(idConfigEquip, ce.parametrosAdicionais);
				buscarConfiguracaoGeralPainel(idConfigEquip, ce.configuracaoGeralPainel);
				buscarConfiguracoesPainel(idConfigEquip, ce.configuracaoPaineis);
				buscarConfiguracoesAGDGeral(idConfigEquip, ce.configAGD_Device, ce.configAGD_Road, ce.configAGD_Software);

				ret = buscarServidor(idConfigEquip, 1, ce.servidorPrimario) ? null : null;
				ret = buscarServidor(idConfigEquip, 2, ce.servidorSecundario) ? null : null;
				ret = buscarServidor(idConfigEquip, 3, ce.servidorMontante) ? null : null;

				ce.distanciaEntreEquip =  			rs.getInt("distancia_equipamento");
				ce.tempoDeCiclagem = 				rs.getInt("tempo_ciclagem");
				
				ce.TempoTotalVideo = 				rs.getInt("TempoTotalVideo");
				ce.TempoVideoAntesInfracao = 		rs.getInt("TempoVideoAntesInfracao");
				
				ce.AtivarEquipamentoComoMontante = 	rs.getInt("ativar_montante");
				ce.AtivarEquipamentoComoJusante = 	rs.getInt("ativar_jusante");
				ce.PortaEquipamentoMontante = 		rs.getInt("porta_montante");
				ce.CodigoLocalMontante = 			rs.getInt("codigo_montante");
				
				ret = new ConfiguracaoEquipamento(idConfigEquip, ce);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarConfigEquipPorId(1)", e);
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				/*				if (ps != null && !ps.isClosed())
					ps.close(); */
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL buscarConfigEquipPorId(2)", e);
			}			
		}

		return ret;
	}
	
	private static Boolean buscarConfiguracaoResolucaoImagem(Integer idConfigEquip, TResolucaoImagem resolucaoImagem) throws ConexaoException {
		Boolean ret = false;
		Connection conn = null;
		PreparedStatement ps_1 = null;
		ResultSet rs1 = null;
		
		try {
			conn = Conexao.getConexao();
			ps_1 = conn.prepareStatement(
			"SELECT * FROM configuracao_equipamento_resolucao_imagem WHERE id_configuracao_equipamento = ?");
			ps_1.setInt(1, idConfigEquip);
			rs1 = ps_1.executeQuery();
			if (rs1.next()) {
				resolucaoImagem.LarguraImagemInfracao = rs1.getInt(2);
				resolucaoImagem.AlturaImagemInfracao = rs1.getInt(3);
				resolucaoImagem.LarguraImagemOcr = rs1.getInt(4);
				resolucaoImagem.AlturaImagemOcr = rs1.getInt(5);
			}
			rs1.close();
		}
		catch(SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarCameras(1)", e);
		}
		finally {
			try {
				if (ps_1 != null)
				ps_1.close();		
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL buscarCameras(2)", e);
			}			
		}
		
		return ret;	
	}
	
	private static Boolean buscarConfiguracaoLacoVirtualML(Integer idConfigEquip, TConfigLacoVirtualML lacoVirtualML) throws ConexaoException {
		Boolean ret = false;
		Connection conn = null;
		PreparedStatement ps_1 = null;
		ResultSet rs1 = null;
		
		try {
			conn = Conexao.getConexao();
			ps_1 = conn.prepareStatement(
			"SELECT * FROM configuracao_equipamento_laco_virtual_ml WHERE id_configuracao_equipamento = ?");
			ps_1.setInt(1, idConfigEquip);
			rs1 = ps_1.executeQuery();
			if (rs1.next()) {
				lacoVirtualML.HabilitarLV_ML = rs1.getBoolean(2);
				lacoVirtualML.PontosVirtuais.Text = rs1.getString(3);
			}
			rs1.close();
		}
		catch(SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarCameras(1)", e);
		}
		finally {
			try {
				if (ps_1 != null)
				ps_1.close();		
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL buscarCameras(2)", e);
			}			
		}
		
		return ret;	
	}

	private static Boolean buscarConfiguracaoDimensoesML(Integer idConfigEquip, TConfigDimensoesML dimensoesML) throws ConexaoException {
		Boolean ret = false;
		Connection conn = null;
		PreparedStatement ps_1 = null;
		ResultSet rs1 = null;
		
		try {
			conn = Conexao.getConexao();
			ps_1 = conn.prepareStatement(
			"SELECT * FROM configuracao_equipamento_dimensoes_ml WHERE id_configuracao_equipamento = ?");
			ps_1.setInt(1, idConfigEquip);
			rs1 = ps_1.executeQuery();
			if (rs1.next()) {
				dimensoesML.HabilitarMedicaoML = rs1.getBoolean(2);
				dimensoesML.TimerHabilitar = rs1.getBoolean(3);
				dimensoesML.TimerIntervalo = rs1.getInt(4);
				dimensoesML.PontosVirtuais.Text = rs1.getString(5);
			}
			rs1.close();
		}
		catch(SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarCameras(1)", e);
		}
		finally {
			try {
				if (ps_1 != null)
				ps_1.close();		
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL buscarCameras(2)", e);
			}			
		}
		
		return ret;	
	}

	private static Boolean buscarConfiguracaoPesagem(Integer idConfigEquip, TConfigPesagem pesagem) throws ConexaoException {
		Boolean ret = false;
		Connection conn = null;
		PreparedStatement ps_1 = null, ps_2 = null, ps_3 = null;
		ResultSet rs1 = null, rs2 = null, rs3 = null;
		
		try {
			conn = Conexao.getConexao();
			ps_1 = conn.prepareStatement(
			"SELECT * FROM configuracao_equipamento_pesagem WHERE id_configuracao_equipamento = ?");
			ps_2 = conn.prepareStatement(
			"SELECT * FROM configuracao_equipamento_pista_pesagem WHERE id_configuracao_equipamento = ? ORDER BY id_pista");
			ps_3 = conn.prepareStatement(
			"SELECT * FROM configuracao_equipamento_sensor_piezo WHERE id_configuracao_equipamento = ? AND id_pista = ? ORDER BY ord");
			
			ps_1.setInt(1, idConfigEquip);
			rs1 = ps_1.executeQuery();
			if (rs1.next()) {
				pesagem.PesagemHabilitada = rs1.getBoolean(2);
				pesagem.ConfigConvAd.Text = rs1.getString(3);
				pesagem.ConfigPesagemParam.Text = rs1.getString(4);
				pesagem.DataUltimaCalibracao = Funcoes.convertUTCToPascalDate(rs1.getTimestamp(5));
				
				ps_2.setInt(1, idConfigEquip);
				rs2 = ps_2.executeQuery();
				while(rs2.next()) {
					TConfigPistaPesagem pista = pesagem.ConfigPistasPesagem.add();
					pista.IdPista = rs2.getInt(2);
					pista.DistSegundoLacoSensor = rs2.getDouble(3);
					
					ps_3.setInt(1, idConfigEquip);
					ps_3.setInt(2, pista.IdPista);
					rs3 = ps_3.executeQuery();
					while(rs3.next()) {
						TSensorPiezo sensor = pista.Sensores.add();
						sensor.Id = rs3.getInt(4);
						sensor.FatorCal = rs3.getDouble(5);
					}
					rs3.close();
				}
				rs2.close();
			}
			rs1.close();
		}	
		catch(SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarCameras(1)", e);
		}
		finally {
			try {
				if (ps_1 != null)
				ps_1.close();
				if (ps_2 != null)
				ps_2.close();
				if (ps_3 != null)
				ps_3.close();			
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL buscarCameras(2)", e);
			}			
		}
		
		return ret;	
	}

	private static Boolean buscarControladoresCanais(Integer idConfigEquip, Integer id, TCollection<TCanalPL> canais) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("  SELECT");
		sbSQL.append("  modoHabilitar,");
		sbSQL.append("  modoSensibilidade,");
		sbSQL.append("  sensibilidadeEntrada,");
		sbSQL.append("  sensibilidadeSaida,");
		sbSQL.append("  configOscilador,");
		sbSQL.append("  eventosMonitorados, ");
		sbSQL.append("  divisorPerfil ");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_controlador_canais WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ? AND id = ?");
		sbSQL.append("  ORDER BY item");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, id);

			rs = ps.executeQuery();
			while (rs.next()) {
				
				TCanalPL canal = canais.add();
				canal.modoHabilitar = rs.getInt("modoHabilitar");
				canal.modoSensibilidade = rs.getInt("modoSensibilidade");
				canal.sensibilidadeEntrada = rs.getInt("sensibilidadeEntrada");
				canal.sensibilidadeSaida = rs.getInt("sensibilidadeSaida");
				canal.configOscilador = rs.getInt("configOscilador");
				canal.eventosMonitorados = rs.getInt("eventosMonitorados");
				canal.divisorPerfil = rs.getInt("divisorPerfil");
				
			}
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarCameras(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarCameras(2)", e);
			}			
		}
		return ret;
	}
	private static Boolean buscarControladoresCanaisV2(Integer idConfigEquip, Integer id, TCollection<TChannelConfigs> canais) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("  SELECT");
		sbSQL.append("  id,");
		sbSQL.append("  canal,");
		sbSQL.append("  operacao,");
		sbSQL.append("  registrador,");
		sbSQL.append("  valor");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_controlador_canaisv2 WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ? AND id_controlador = ?");
		sbSQL.append("	ORDER BY id");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, id);

			rs = ps.executeQuery();
			TChannelConfigs canal = null;
			while (rs.next()) {
				
				if (canal == null || canal.id != rs.getInt("id")) {
					canal = canais.add();
					canal.id = rs.getInt("id");
				}
				
				TLoopDetectorChannelConfig canal1 = canal.configuracoes.add();
				canal1.canal = rs.getInt("canal");
				canal1.operacao = rs.getInt("operacao");
				canal1.registrador = rs.getInt("registrador");
				canal1.valor = rs.getInt("valor");
				
			}
			
		} catch (Exception e) {
			throw new ConexaoException("ERRO de SQL buscarControladoresCanaisV2(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarControladoresCanaisV2(2)", e);
			}			
		}
		return ret;
	}

	private static Boolean buscarControladores(Integer idConfigEquip,
			TCollection<TControladorPL> controladoresPL) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("  SELECT");
		sbSQL.append("  id,");
		sbSQL.append("  porta,");
		sbSQL.append("  bitsPorSegundo,");
		sbSQL.append("  bitsDados,");
		sbSQL.append("  bitsParada,");
		sbSQL.append("  paridade ");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_controlador WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");
		sbSQL.append("  ORDER BY id");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			while (rs.next()) {
				TControladorPL controlador = controladoresPL.add();
				controlador.id = rs.getInt("id");
				controlador.porta = rs.getString("porta").trim();
				controlador.bitsPorSegundo = rs.getInt("bitsPorSegundo");
				controlador.bitsDados = rs.getInt("bitsDados");
				controlador.bitsParada = rs.getInt("bitsParada");
				controlador.paridade = rs.getInt("paridade");
				
				buscarControladoresCanais(idConfigEquip, controlador.id, controlador.canais);
				buscarControladoresCanaisV2(idConfigEquip, controlador.id, controlador.canaisV2);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarControladores(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarControladores(2)", e);
			}			
		}

		return ret;
		
	}
	
	private static Boolean buscarControladoresPesagem(Integer idConfigEquip,
			TCollection<TControladorPP> controladoresPesagem) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();
		
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("  SELECT");
		sbSQL.append("  id,");
		sbSQL.append("  porta_S1,");
		sbSQL.append("  bitsPorSegundo_S1,");
		sbSQL.append("  bitsDados_S1,");
		sbSQL.append("  bitsParada_S1,");
		sbSQL.append("  paridade_S1, ");
		sbSQL.append("  tempoReconexao_S1, ");
		sbSQL.append("  tamanhoBuffer_S1, ");
		sbSQL.append("  porta_S2,");
		sbSQL.append("  bitsPorSegundo_S2,");
		sbSQL.append("  bitsDados_S2,");
		sbSQL.append("  bitsParada_S2,");
		sbSQL.append("  paridade_S2, ");
		sbSQL.append("  tempoReconexao_S2, ");
		sbSQL.append("  tamanhoBuffer_S2 ");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_controlador_pesagem WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");
		sbSQL.append("  ORDER BY id");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			while (rs.next()) {
				TControladorPP controlador = controladoresPesagem.add();
				controlador.id = rs.getInt("id");
				controlador.porta_S1 = rs.getInt("porta_S1");
				controlador.bitsPorSegundo_S1 = rs.getInt("bitsPorSegundo_S1");
				controlador.bitsDados_S1 = rs.getInt("bitsDados_S1");
				controlador.bitsParada_S1 = rs.getInt("bitsParada_S1");
				controlador.paridade_S1 = rs.getString("paridade_S1").trim();
				controlador.tempoReconexao_S1 = rs.getInt("tempoReconexao_S1");
				controlador.tamanhoBuffer_S1 = rs.getInt("tamanhoBuffer_S1");
				controlador.porta_S2 = rs.getInt("porta_S2");
				controlador.bitsPorSegundo_S2 = rs.getInt("bitsPorSegundo_S2");
				controlador.bitsDados_S2 = rs.getInt("bitsDados_S2");
				controlador.bitsParada_S2 = rs.getInt("bitsParada_S2");
				controlador.paridade_S2 = rs.getString("paridade_S2").trim();
				controlador.tempoReconexao_S2 = rs.getInt("tempoReconexao_S2");
				controlador.tamanhoBuffer_S2 = rs.getInt("tamanhoBuffer_S2");
				
				buscarControladoresPesagemCanais(idConfigEquip, controlador.id, controlador.canais);
				
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarControladoresPesagem(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarControladoresPesagem(2)", e);
			}			
		}

		return ret;
		
	}
	
	private static Boolean buscarControladoresPesagemCanais(Integer idConfigEquip, Integer id, TCollection<TCanalPP> canais) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();
		
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("  SELECT");
		sbSQL.append("  canalFisico,");
		sbSQL.append("  offSet,");
		sbSQL.append("  inverterPolaridade");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_controlador_pesagem_canais WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ? AND id = ?");
		sbSQL.append("  ORDER BY item");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, id);

			rs = ps.executeQuery();
			while (rs.next()) {
				
				TCanalPP canal = canais.add();
				canal.canalFisico = rs.getInt("canalFisico");
				canal.offSet = rs.getInt("offSet");
				canal.inverterPolaridade = rs.getBoolean("inverterPolaridade");
				
			}
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarControladoresPesagemCanais(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarControladoresPesagemCanais(2)", e);
			}			
		}
		return ret;
	}

	/**
	 * @param idConfigEquip
	 * @return retorna a data de modificação da configuração do equipamento que esta gravado
	 * @throws ConexaoException
	 */
	public static Date buscaDataModificacaoConfigEquip(Integer idConfigEquip) throws ConexaoException {

		Date ret = null;
		String sSQL = "SELECT data_modificacao FROM configuracao_equipamento WITH (NOLOCK) WHERE id_configuracao_equipamento = ?";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);

			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			if (rs.next()) {
				ret = rs.getTimestamp(1) ;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscaDataModificacaoConfigEquip(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscaDataModificacaoConfigEquip(2)", e);
			}			
		}

		return ret;
	}

	public static String buscaUsuarioConfigEquip( Integer idConfigEquip ) throws ConexaoException {

		String ret = null;
		String sSQL = "SELECT su.nome FROM configuracao_equipamento ce WITH (NOLOCK) JOIN sis_usuario su WITH (NOLOCK) ON su.id_usuario = ce.id_usuario WHERE ce.id_configuracao_equipamento = ?";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);

			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			if (rs.next()) {
				ret = rs.getString(1) != null ? rs.getString(1).trim() : null;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscaUsuarioConfigEquip(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscaUsuarioConfigEquip(2)", e);
			}			
		}

		return ret;
	}

	private static boolean buscarServidor(Integer idConfigEquip, int idServidor,
			TServidor servidor) throws ConexaoException {

		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("   SELECT ");
		sbSQL.append("		host,");
		sbSQL.append("		port");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_servidor WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ? AND");
		sbSQL.append("		id_servidor = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, idServidor);

			rs = ps.executeQuery();
			if (rs.next()) {
				servidor.host = rs.getString("host").trim();
				servidor.port = rs.getInt("port");
				ret = true;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarServidor(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarServidor(2)", e);
			}			
		}
		return ret;
	}
	
	private static Boolean buscarConfiguracoesGeralDiv(Integer idConfigEquip, TConfiguracaoGeralDivs divsGeral) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....		
		sbSQL.append("   SELECT ");
		sbSQL.append("	verdeVermelhoTolerancia, ");
		sbSQL.append("	tipoTolerancia,");
		sbSQL.append("	mostrarVelocidade,");
		sbSQL.append("	velocidadeSeparador,");
		sbSQL.append("	toleranciaFixa,");
		sbSQL.append("	toleranciaPercentual");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_geral_divs WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			rs = ps.executeQuery();
			if (rs.next()) {
				divsGeral.verdeVermelhoTolerancia = rs.getBoolean("verdeVermelhoTolerancia");
				divsGeral.tipoTolerancia = rs.getInt("tipoTolerancia");
				divsGeral.mostrarVelocidade = rs.getBoolean("mostrarVelocidade");
				divsGeral.velocidadeSeparador = rs.getInt("velocidadeSeparador");
				divsGeral.toleranciaFixa = rs.getInt("toleranciaFixa");
				divsGeral.toleranciaPercentual = rs.getInt("toleranciaPercentual");
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarConfiguracoesDIV(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarConfiguracoesDIV(2)", e);
			}			
		}

		return ret;
	}

	private static Boolean buscarConfiguracoesDIV(Integer idConfigEquip, TCollection<TConfiguracaoDIV> configuracaoDIVs) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....		
		sbSQL.append("   SELECT ");
		sbSQL.append("		id_div,");
		sbSQL.append("		endereco,");
		sbSQL.append("		porta_com,");
		sbSQL.append("		versao,");
		sbSQL.append("		numero_digitos");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_div WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			rs = ps.executeQuery();
			while (rs.next()) {
				TConfiguracaoDIV configuracaoDIV = configuracaoDIVs.add();
				configuracaoDIV.divId = rs.getInt("id_div");
				configuracaoDIV.endereco = rs.getInt("endereco");
				configuracaoDIV.portaCOM = rs.getInt("porta_com");
				configuracaoDIV.versao = rs.getInt("versao");
				configuracaoDIV.numeroDigitos = rs.getInt("numero_digitos");
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarConfiguracoesDIV(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarConfiguracoesDIV(2)", e);
			}			
		}

		return ret;
	}

	private static Boolean buscarParametrosAdicionais(Integer idConfigEquip, TStringList parametrosAdicionais) throws ConexaoException {
		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....			
		sbSQL.append("   SELECT ");
		sbSQL.append("		parametros_adicionais");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_parametros_adicionais WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			if (rs.next()) {
				parametrosAdicionais.Text = rs.getString("parametros_adicionais");
				ret = true;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarParametrosAdicionais(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarParametrosAdicionais(2)", e);
			}			
		}
		return ret;
	}

	private static Boolean buscarConfiguracoesPainel(Integer idConfigEquip, TCollection<TConfiguracaoPainel> configuracaoPaineis) throws SQLException, ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("   SELECT ");
		sbSQL.append("		id_painel,");
		sbSQL.append("		endereco,");
		sbSQL.append("		porta_com");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_painel WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			rs = ps.executeQuery();
			while (rs.next()) {
				TConfiguracaoPainel configuracaoPainel = configuracaoPaineis.add();
				configuracaoPainel.painelId = rs.getInt("id_painel");
				configuracaoPainel.endereco = rs.getInt("endereco");
				configuracaoPainel.portaCOM = rs.getInt("porta_com");
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarConfiguracoesPainel(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarConfiguracoesPainel(2)", e);
			}			
		}

		return ret;
	}
	
	private static Boolean buscarConfiguracoesAGDGeral(Integer idConfigEquip, TConfigAGD_Device confAGDDevice, TConfigAGD_Road confAGDRoad, TConfigAGD_Software confAGDSoft) throws SQLException, ConexaoException {
		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("	SELECT ");
		sbSQL.append("		road_side,");
		sbSQL.append("		vertical_angle,");
		sbSQL.append("		horizontal_angle,");
		sbSQL.append("		port_name,");
		sbSQL.append("		baud_rate,");
		sbSQL.append("		parity,");
		sbSQL.append("		data_bits,");
		sbSQL.append("		stop_bits,");
		sbSQL.append("		high_range_threshold,");
		sbSQL.append("		high_speed_threshold,");
		sbSQL.append("		low_range_threshold,");
		sbSQL.append("		low_speed_threshold,");
		sbSQL.append("		power_threshold,");
		sbSQL.append("		channel,");
		sbSQL.append("		COALESCE(sense,0) AS sense,");
		sbSQL.append("		COALESCE(tracking_mode,0) AS tracking_mode");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_agd WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			rs = ps.executeQuery();
			if (rs.next()) {
				confAGDRoad.RoadSide = rs.getString("road_side").trim();
				confAGDRoad.VerticalAngle = rs.getString("vertical_angle").trim();
				confAGDRoad.HorizontalAngle = rs.getString("horizontal_angle").trim();
				confAGDSoft.PortName = rs.getString("port_name").trim();
				confAGDSoft.PortBaudRate = rs.getInt("baud_rate");
				confAGDSoft.PortParity = rs.getString("parity").trim();
				confAGDSoft.PortDataBits = rs.getInt("data_bits");
				confAGDSoft.PortStopBits = rs.getString("stop_bits").trim();
				confAGDDevice.HighRangeThreshold = rs.getString("high_range_threshold").trim();
				confAGDDevice.HighSpeedThreshold = rs.getString("high_speed_threshold").trim();
				confAGDDevice.LowRangeThreshold = rs.getString("low_range_threshold").trim();
				confAGDDevice.LowSpeedThreshold = rs.getString("low_speed_threshold").trim();
				confAGDDevice.PowerThreshold = rs.getString("power_threshold").trim();
				confAGDDevice.Channel = rs.getInt("channel");
				confAGDDevice.Sense = rs.getInt("sense");
				confAGDSoft.TrackingMode = rs.getInt("tracking_mode");
				
				ret = true;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarConfiguracoesAGDGeral(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarConfiguracoesAGDGeral(2)", e);
			}			
		}

		return ret;
	}		
	

	private static Boolean buscarConfiguracaoGeralPainel(Integer idConfigEquip, TConfiguracaoGeralPainel configuracaoGeralPainel) throws ConexaoException {
		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....					
		sbSQL.append("   SELECT ");
		sbSQL.append("		usar_ldr,");
		sbSQL.append("		id_painel_watchdog");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_painel_geral WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			if (rs.next()) {
				configuracaoGeralPainel.usarLDR = rs.getBoolean("usar_ldr");
				configuracaoGeralPainel.watchdogPainelId = rs.getInt("id_painel_watchdog");
				ret = true;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarConfiguracaoGeralPainel(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarConfiguracaoGeralPainel(2)", e);
			}			
		}
		return ret;
	}

	private static Boolean buscarRodizios(Integer idConfigEquip, TCollection<TRodizio> rodizios) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("   SELECT ");
		sbSQL.append("		horario_inicio,");
		sbSQL.append("		horario_fim,");
		sbSQL.append("		dia_semana,");
		sbSQL.append("		final_placa");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_rodizio WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");
		sbSQL.append("	ORDER BY id_rodizio");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			while (rs.next()) {
				TRodizio rodizio = rodizios.add();
				rodizio.horarioInicio = Funcoes.convertUTCToPascalDate(rs.getTimestamp("horario_inicio"));
				rodizio.horarioFim = Funcoes.convertUTCToPascalDate(rs.getTimestamp("horario_fim"));
				rodizio.diaSemana = rs.getInt("dia_semana");
				rodizio.finalPlaca = rs.getInt("final_placa");
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarRodizios(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarRodizios(2)", e);
			}			
		}

		return ret;
	}

	private static Boolean buscarRegrasInfracao(Integer idConfigEquip, TCollection<TRegraInfracao> regrasInfracao) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("   SELECT ");
		sbSQL.append("		id_pista,");
		sbSQL.append("		hora_ini,");
		sbSQL.append("		hora_fim,");
		sbSQL.append("		dia_ini,");
		sbSQL.append("		dia_fim,");
		sbSQL.append("		velocidade_limite,");
		sbSQL.append("		tolerancia,");
		sbSQL.append("		tolerancia_portaria,");
		sbSQL.append("		comprimento_ini,");
		sbSQL.append("		comprimento_fim,");
		sbSQL.append("		tipo,");
		sbSQL.append("		ativo,");
		sbSQL.append("		id_classe,");
		sbSQL.append("		tolerancia_vermelho,");
		sbSQL.append("		tolerancia_faixa,");
		sbSQL.append("		usar_panoramica,");
		sbSQL.append("		opcao_panoramica,");
		sbSQL.append("		fiscalizar_fase,");
		sbSQL.append("		num_imagens_pos_laco,");
		sbSQL.append("		intervalo_imagens_pos_laco,");
		sbSQL.append("		tolerancia_transversal,");
		sbSQL.append("		intervalo,");
		sbSQL.append("      final_placa,");
		sbSQL.append("      remover_isencao_taxi,");
		sbSQL.append("      ini_regra_taxi,");
		sbSQL.append("      fim_regra_taxi ");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_regra_infracao WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");
		sbSQL.append("	ORDER BY id_regra_infracao");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			rs = ps.executeQuery();

			while (rs.next()) {
				TRegraInfracao regra = regrasInfracao.add();
				regra.idPista = rs.getInt("id_pista");
				regra.iniRegra = Funcoes.convertUTCToPascalDate(rs.getTimestamp("hora_ini"));
				regra.fimRegra = Funcoes.convertUTCToPascalDate(rs.getTimestamp("hora_fim"));
				regra.diaRegraIni = rs.getInt("dia_ini");
				regra.diaRegraFim = rs.getInt("dia_fim");
				regra.limite = rs.getInt("velocidade_limite");
				regra.tolerancia = rs.getInt("tolerancia");
				regra.toleranciaPortaria = rs.getInt("tolerancia_portaria");
				regra.tamIni = rs.getDouble("comprimento_ini");
				regra.tamFim = rs.getDouble("comprimento_fim");
				regra.tipo = rs.getString("tipo");
				regra.ativo = rs.getBoolean("ativo");
				regra.perfil = rs.getString("id_classe").charAt(0);
				regra.toleranciaVermelho = rs.getDouble("tolerancia_vermelho");
				regra.toleranciaFaixa = rs.getDouble("tolerancia_faixa");
				regra.usarPanoramica = rs.getBoolean("usar_panoramica");
				regra.opcaoPanoramica = rs.getInt("opcao_panoramica");
				regra.fiscalizarFase = rs.getInt("fiscalizar_fase");
				regra.numImagensPosLaco = rs.getInt("num_imagens_pos_laco");
				regra.intervImagensPosLaco = rs.getDouble("intervalo_imagens_pos_laco");
				regra.toleranciaTransversal = rs.getInt("tolerancia_transversal");
				regra.intervalo = rs.getInt("intervalo");
				regra.finalPlaca = rs.getInt("final_placa");
				regra.removerIsencaoTaxi = rs.getBoolean("remover_isencao_taxi");
				regra.iniRegraTaxi = rs.getDouble("ini_regra_taxi");
				regra.fimRegraTaxi = rs.getDouble("fim_regra_taxi");
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarRegrasInfracao(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarRegrasInfracao(2)", e);
			}			
		}

		return ret;
	}

	private static Boolean buscarNiveisVideo(Integer idConfigEquip, TCollection<TNivelVideo> niveisVideo) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("   SELECT ");
		sbSQL.append("		id_pista,");
		sbSQL.append("		camera,");
		sbSQL.append("		horario_inicio,");
		sbSQL.append("		horario_fim,");
		sbSQL.append("		valor");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_nivel_video WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");
		sbSQL.append("	ORDER BY id_nivel_video");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			while (rs.next()) {
				TNivelVideo nivelVideo = niveisVideo.add();
				nivelVideo.idPista = rs.getInt("id_pista");
				nivelVideo.idCamera = rs.getInt("camera");
				nivelVideo.horarioInicio = Funcoes.convertUTCToPascalDate(rs.getTimestamp("horario_inicio"));
				nivelVideo.horarioFim = Funcoes.convertUTCToPascalDate(rs.getTimestamp("horario_fim"));
				nivelVideo.valor = rs.getDouble("valor");
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarNiveisVideo(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarNiveisVideo(2)", e);
			}			
		}
		return ret;
	}

	private static Boolean buscarHorarios(Integer idConfigEquip, TCollection<THorario> horarios) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("   SELECT ");
		sbSQL.append("		id_pista,");
		sbSQL.append("		horario_inicio,");
		sbSQL.append("		horario_fim");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_horario WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");
		sbSQL.append("	ORDER BY id_horario");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			while (rs.next()) {
				THorario horario = horarios.add();
				horario.idPista = rs.getInt("id_pista");
				horario.horarioInicio = Funcoes.convertUTCToPascalDate(rs.getTimestamp("horario_inicio"));
				horario.horarioFim = Funcoes.convertUTCToPascalDate(rs.getTimestamp("horario_fim"));
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarHorarios(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarHorarios(2)", e);
			}			
		}

		return ret;
	}

	private static Boolean buscarPistas(Integer idConfigEquip, TCollection<TPista> pistas) throws ConexaoException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("   SELECT ");
		sbSQL.append("		id_pista,");
		sbSQL.append("		cod_pista,");
		sbSQL.append("		nome_pista,");
		sbSQL.append("		RTRIM(sentido) AS sentido,");
		
		sbSQL.append("		TempoMaximoAmarelo,");
		sbSQL.append("		TempoMaximoVermelho,");
		sbSQL.append("		TempoMinimoAmarelo,");
		
		sbSQL.append("		conector,");
		sbSQL.append("		id_div,");
		sbSQL.append("		id_div_display,");
		sbSQL.append("		trigger_infravermelho,");
		sbSQL.append("		id_painel_infravermelho,");
		sbSQL.append("		id_painel_ldr,");
		sbSQL.append("		pin_ldr,");
		sbSQL.append("		cod_pista_alternativo,");
		sbSQL.append("		cod_pista_prodam,");
		sbSQL.append("		semaforo_painel_id,");
		sbSQL.append("		pista_1_transversal,");
		sbSQL.append("		pista_2_transversal,");
		sbSQL.append("		pista_3_transversal,");
		sbSQL.append("		pista_4_transversal,");
		sbSQL.append("		pista_5_transversal,");
		sbSQL.append("		pista_6_transversal,");
		sbSQL.append("		pista_7_transversal,");
		sbSQL.append("		pista_8_transversal,");
		sbSQL.append("		captura_obj_frente,");
		sbSQL.append("		captura_obj_tras,");
		sbSQL.append("		captura_obj_laco,");
		sbSQL.append("		cod_area,");
		
		sbSQL.append("		tipo_disparo,");
		sbSQL.append("		ctrl_nivel_iluminador,");
		sbSQL.append("		nivel_inicial,");
		sbSQL.append("		nivel_final,");
		sbSQL.append("		lista_niveis,");
		sbSQL.append("		camera_iluminador, ");
		
		sbSQL.append("		cod_pista_tarja, ");
		sbSQL.append("		cod_local_prodam_auxiliar, ");

		sbSQL.append("		paradaFaixaL1, ");
		sbSQL.append("		paradaFaixaL2,  ");
		
		sbSQL.append("		faixa_exclusiva_direita,  ");
		sbSQL.append("		faixa_exclusiva_esquerda,  ");
		
		sbSQL.append("		entre_faixa,  ");
		
		//Códigos de identificação GIT para o Rio 2016
		////////////////////////////////////////////////////////////////////
		sbSQL.append("		cod_GIT_Logradouro,  ");
		sbSQL.append("      cod_GIT_Pista, ");
		sbSQL.append("		cod_GIT_Sentido,  ");
		sbSQL.append("		cod_GIT_Faixa,  ");
		//////////////////////////////////////////////////////////////////
		
		sbSQL.append("  id_camera_frontal,");
		sbSQL.append("  id_camera_traseira,");
		sbSQL.append("  id_camera_pan_1,");
		sbSQL.append("  id_camera_pan_2,");
		sbSQL.append("  pista_relevante, ");
		sbSQL.append("  captura_reversa ");
		
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_pista WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			while (rs.next()) {
				TPista pista = pistas.add();
				pista.idPista = rs.getInt("id_pista");
				pista.codPista = rs.getInt("cod_pista");
				pista.nomePista = rs.getString("nome_pista") != null ? rs.getString("nome_pista").trim() : null;
				pista.sentido = rs.getString("sentido");
				
				pista.TempoMaximoAmarelo = rs.getInt("TempoMaximoAmarelo");
				pista.TempoMaximoVermelho = rs.getInt("TempoMaximoVermelho");
				pista.TempoMinimoAmarelo = rs.getInt("TempoMinimoAmarelo");
				
				pista.conector = rs.getInt("conector");
				pista.divId = rs.getInt("id_div");
				pista.divDisplayId = rs.getInt("id_div_display");
				pista.triggerInfravermelho = rs.getInt("trigger_infravermelho");
				pista.painelInfravermelhoId = rs.getInt("id_painel_infravermelho");
				pista.ldrPainelId = rs.getInt("id_painel_ldr");
				pista.ldrPin = rs.getInt("pin_ldr");
				pista.idPistaAlternativo = rs.getInt("cod_pista_alternativo");
				pista.codPistaProdam = rs.getInt("cod_pista_prodam");
				pista.semaforoPainelId = rs.getInt("semaforo_painel_id");

				pista.pista1Transversal = rs.getBoolean("pista_1_transversal");
				pista.pista2Transversal = rs.getBoolean("pista_2_transversal");
				pista.pista3Transversal = rs.getBoolean("pista_3_transversal");
				pista.pista4Transversal = rs.getBoolean("pista_4_transversal");
				pista.pista5Transversal = rs.getBoolean("pista_5_transversal");
				pista.Pista6Transversal = rs.getBoolean("pista_6_transversal");
				pista.Pista7Transversal = rs.getBoolean("pista_7_transversal");
				pista.Pista8Transversal = rs.getBoolean("pista_8_transversal");

				pista.CapturaObjFrente = rs.getBoolean("captura_obj_frente");
				pista.CapturaObjTras = rs.getBoolean("captura_obj_tras");

				pista.CapturaObjLaco = rs.getInt("captura_obj_laco");

				pista.CodArea = rs.getInt("cod_area");

				// xxxx
				pista.TipoDisparo = rs.getBoolean("tipo_disparo");
				
				if(pista.TipoDisparo) {
					pista.CtrlNivelIluminador = rs.getBoolean("ctrl_nivel_iluminador");
					pista.NivelInicialIluminador = rs.getInt("nivel_inicial");
					pista.NivelFinalIluminador = rs.getInt("nivel_final");
					pista.ListaNiveisIluminador = rs.getString("lista_niveis");
				}
				//Luiz Amaral 12/05/2015
				//Alterado para preencher os campos independente se o tipo de disparo é convencional ou mulltiplos disparos
				//[O.S 0059] - Ajustar ConfigEquipApp para salvar informações do iluminador
				pista.EndCameraIluminador = rs.getInt("camera_iluminador");
				
				pista.codPistaTarja = rs.getInt("cod_pista_tarja");
				pista.codLocalProdamAuxiliar = rs.getInt("cod_local_prodam_auxiliar");
				
				//[O.S 0067] - Modificar maquina de Estados do Captura para infrações de PF e AV
				pista.ParadaFaixaL1 = rs.getBoolean("paradaFaixaL1");
				pista.ParadaFaixaL2 = rs.getBoolean("paradaFaixaL2");
				
				//[O.S 0107] - Adicionado novos campos na pista para atender novo enquadramento 75870
				pista.FaixaExclusivaDireita = rs.getBoolean("faixa_exclusiva_direita");
				pista.FaixaExclusivaEsquerda = rs.getBoolean("faixa_exclusiva_esquerda");
				
				pista.entreFaixa = rs.getBoolean("entre_faixa");
				
				//Códigos de identificação GIT para o Rio 2016
				////////////////////////////////////////////////////////////////////
				pista.cod_GIT_Logradouro = 	rs.getInt("cod_GIT_Logradouro");
				pista.cod_GIT_Pista      =  rs.getInt("cod_GIT_Pista");
				pista.cod_GIT_Sentido = 	rs.getInt("cod_GIT_Sentido");		
				pista.cod_GIT_Faixa = 		rs.getInt("cod_GIT_Faixa");
				//////////////////////////////////////////////////////////////////
				
				pista.IDCameraFrontal = rs.getInt("id_camera_frontal");
				pista.IDCameraTraseira = rs.getInt("id_camera_traseira");
				pista.IDCameraPan1 = rs.getInt("id_camera_pan_1");
				pista.IDCameraPan2 = rs.getInt("id_camera_pan_2");
				pista.PistaRelevante = rs.getBoolean("pista_relevante");
				pista.CapturaReversa = rs.getBoolean("captura_reversa");
				
				buscarCapturaVeiculo(idConfigEquip, pista);
				buscarAfericao(idConfigEquip, pista.idPista, pista.afericao);
				buscarConfiguracaoAGDPista(idConfigEquip, pista);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarPistas(1)", e);
		}		
		finally {
			try {
				if (rs != null)
					rs.close();
				/*				if (ps != null)
					ps.close(); */
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL buscarPistas(2)", e);
			}			
		}
		return ret;
	}

	private static Boolean buscarCapturaVeiculo(Integer idConfigEquip, TPista pista) throws ConexaoException {
		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....			
		sbSQL.append("   SELECT ");
		sbSQL.append("		distancia_laco,");
		sbSQL.append("		largura_laco,");
		sbSQL.append("		com_perfil_magnetico,");
		sbSQL.append("		num_lacos,");
		sbSQL.append("		trigger_infra_vermelho,");
		sbSQL.append("		num_canal,");
		sbSQL.append("		num_imagens_pos_laco,");
		sbSQL.append("		interv_imagens_pos_laco,");
		sbSQL.append("		distancia_panoramica_pos_laco");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_captura_veiculo WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ? AND id_pista = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, pista.idPista);

			rs = ps.executeQuery();
			if (rs.next()) {
				pista.capturaVeiculo.distanciaLaco = rs.getDouble("distancia_laco");
				pista.capturaVeiculo.larguraLaco = rs.getDouble("largura_laco");
				pista.capturaVeiculo.comPerfilMagnetico = rs.getInt("com_perfil_magnetico");
				pista.capturaVeiculo.numLacos = rs.getInt("num_lacos");
				pista.capturaVeiculo.triggerInfravermelho = rs.getInt("trigger_infra_vermelho");
				pista.capturaVeiculo.numCanal = rs.getInt("num_canal");
				pista.capturaVeiculo.numImagensPosLaco = rs.getInt("num_imagens_pos_laco");
				pista.capturaVeiculo.intervImagensPosLaco = rs.getDouble("interv_imagens_pos_laco");
				pista.capturaVeiculo.distanciaPanoramicaPosLaco = rs.getInt("distancia_panoramica_pos_laco");
				ret = true;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarCapturaVeiculo(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarCapturaVeiculo(2)", e);
			}			
		}

		return ret;
	}
	
	private static Boolean buscarConfiguracaoAGDPista(Integer idConfigEquip, TPista pista) throws ConexaoException {
		
		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("	SELECT");
		sbSQL.append("		capture_distance,");
		sbSQL.append("		starting_border,");
		sbSQL.append("		ending_border,");
		sbSQL.append("		direction,");
		sbSQL.append("		min_samples_for_projection,");
		sbSQL.append("		speed_samples");		
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_agd_pista WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_configuracao_equipamento = ? AND id_pista = ?");		

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, pista.idPista);			
			
			rs = ps.executeQuery();
			if (rs.next()) {	
				pista.configAGD_Pista.CaptureDistance = rs.getString("capture_distance").trim();
				pista.configAGD_Pista.StartingBorder = rs.getString("starting_border").trim();
				pista.configAGD_Pista.EndingBorder = rs.getString("ending_border").trim();
				pista.configAGD_Pista.Direction = rs.getString("direction").trim();
				pista.configAGD_Pista.MinSamplesForProjection = rs.getInt("min_samples_for_projection");
				pista.configAGD_Pista.SpeedSamples = rs.getInt("speed_samples");
	
				ret = true;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarConfiguracaoAGDPista(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarConfiguracaoAGDPista(2)", e);
			}			
		}

		return ret;
	}

	private static Boolean buscarAfericao(Integer idConfigEquip, Integer idPista, TAfericao afericao) throws ConexaoException {
		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("   SELECT ");
		sbSQL.append("		RTRIM(referencia) AS referencia,");
		sbSQL.append("		RTRIM(selagem) AS selagem,");
		sbSQL.append("		laudo,");
		sbSQL.append("		data,");
		sbSQL.append("		data_validade");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_afericao WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ? AND");
		sbSQL.append("		");
		sbSQL.append(idPista < 1 ? "id_pista IS NULL" : "id_pista = " + idPista); 
		sbSQL.append("	ORDER BY id_afericao");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			if (rs.next()) {
				afericao.referencia = rs.getString("referencia");
				afericao.selagem = rs.getString("selagem");
				afericao.laudo = rs.getInt("laudo");
				afericao.dataAfericao = Funcoes.convertUTCToPascalDate(rs.getTimestamp("data"));
				afericao.dataValidadeAfericao = Funcoes.convertUTCToPascalDate(rs.getTimestamp("data_validade"));
				ret = true;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarAfericao(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarAfericao(2)", e);
			}			
		}

		return ret;
	}
	
	private static Boolean buscarCameras(Integer idConfigEquip, TCollection<TCamera> cameras) throws ConexaoException {
		
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....				
		sbSQL.append("  SELECT");
		sbSQL.append("  	id_camera,");
		sbSQL.append("  	tipo,");
		sbSQL.append("  	endereco,");
		sbSQL.append("  	relevante");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_camera WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			while (rs.next()) {
				TCamera camera = cameras.add();
				camera.ID = rs.getInt("id_camera");
				camera.Tipo = TCameraTypes.values()[rs.getInt("tipo")];
				camera.Endereco = rs.getString("endereco").trim();
				camera.Relevante = rs.getBoolean("relevante");
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarCameras(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarCameras(2)", e);
			}			
		}

		return ret;
	}

	private static Boolean buscarRodovia(Integer idConfigEquip, TRodovia rodovia) throws ConexaoException {
		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("   SELECT ");
		sbSQL.append("		sigla,");
		sbSQL.append("		numero,");
		sbSQL.append("		acesso,");
		sbSQL.append("		km,");
		sbSQL.append("		metros");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_rodovia WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			if (rs.next()) {
				rodovia.nome = rs.getString("sigla");
				rodovia.numero = rs.getInt("numero");
				rodovia.acesso = rs.getString("acesso");
				rodovia.quilometro = rs.getInt("km");
				rodovia.metros = rs.getInt("metros");
				ret = true;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarRodovia(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarRodovia(2)", e);
			}			
		}
		return ret;
	}

	private static Boolean buscarLocal(Integer idConfigEquip, TLocal local, TLocalidade localidade) throws ConexaoException {
		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("   SELECT ");
		sbSQL.append("		local.id_local, ");
		sbSQL.append("		local.sequencia_local,");
		sbSQL.append("		RTRIM(local.nome) AS nome,");
		sbSQL.append("		local.data_atualizacao,");
		sbSQL.append("		local.posicao_lat,");
		sbSQL.append("		local.posicao_lon,");
		sbSQL.append("		local.id_localidade,");
		sbSQL.append("		COALESCE(local.localidade_desc,cad_localidade.nome,'') AS nome_localidade,");
		sbSQL.append("		local.cep,");
		sbSQL.append("		local.complemento, ");
		sbSQL.append("		local.dataEnsaioNaoMetrol");
		sbSQL.append("	FROM");
		sbSQL.append("		local WITH (NOLOCK) ");
		sbSQL.append("		LEFT JOIN cad_localidade ON cad_localidade.id_localidade = local.id_localidade");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			if (rs.next()) {
				local.idLocal = rs.getInt("id_local");
				local.sequenciaLocal = rs.getInt("sequencia_local");
				local.nome = rs.getString("nome");
				localidade.idLocalidade = rs.getInt("id_localidade");
				localidade.nome = rs.getString("nome_localidade");
				local.longitude = rs.getDouble("posicao_lon");
				local.latitude = rs.getDouble("posicao_lat");
				local.cep = rs.getInt("cep");
				local.complemento = rs.getString("complemento");
				local.dataEnsaioNaoMetrol = rs.getTimestamp("dataEnsaioNaoMetrol") != null ? Funcoes.convertUTCToPascalDate(rs.getTimestamp("dataEnsaioNaoMetrol")) : 0;
				ret = true;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarLocal(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarLocal(2)", e);
			}			
		}
		return ret;
	}

	private static Boolean buscarConfiguracaoRelevante (Integer idConfigEquip, TConfiguracaoRelevante confRelevante) throws ConexaoException {
		
		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("	SELECT ");
		sbSQL.append("		time_zone,");
		sbSQL.append("		numero_imagens,");
		sbSQL.append("		tempo_autonomia_nobreak,");	
		sbSQL.append("		endereco_sistema_relevante,");	
		sbSQL.append("		delta_minimo_para_filtro,");
		sbSQL.append("		tempo_min_aciona_laco,");
		sbSQL.append("		diferenca_perc_delta_max,");
		sbSQL.append("		socket_controlador_1,");
		sbSQL.append("		socket_controlador_2");
		sbSQL.append("	FROM");
		sbSQL.append("		configuracao_equipamento_relevante WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_configuracao_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idConfigEquip);

			rs = ps.executeQuery();
			if (rs.next()) {
				confRelevante.TimeZone = rs.getString("time_zone").trim();
				confRelevante.NumeroImagens = rs.getInt("numero_imagens");
				confRelevante.TempoAutonomiaNobreak = rs.getInt("tempo_autonomia_nobreak");
				confRelevante.EnderecoSistemaRelevante = rs.getString("endereco_sistema_relevante").trim();
				confRelevante.DeltaMinimoParaFiltro = rs.getInt("delta_minimo_para_filtro");
				confRelevante.TempoMinAcionaLaco = rs.getInt("tempo_min_aciona_laco");
				confRelevante.DiferencaPercDeltaMax = rs.getInt("diferenca_perc_delta_max");
				confRelevante.SocketControlador1 = rs.getInt("socket_controlador_1");
				confRelevante.SocketControlador2 = rs.getInt("socket_controlador_2");
				ret = true;
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL buscarConfiguracaoRelevante(1)", e);
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
				throw new ConexaoException("ERRO de SQL buscarConfiguracaoRelevante(2)", e);
			}			
		}
		return ret;
	}	
	
	private static boolean verificarPermissaoEditar( Integer id_usuario ) throws ConexaoException, ConfiguracaoException {

		List<GrupoBean> grupos = Grupo.buscaGruposPorIdUsuario( id_usuario );

		for (GrupoBean grupoBean : grupos) {
			if(ConfiguracaoProvider.getInstance().getIdGrupoPermissaoEditarConfigEquip() == grupoBean.getId()) 
				return true;
		}

		return false;

	}

	public static ConfiguracaoEquipamento incluirConfigEquip( TConfigEquip conf , Integer id_usuario , Boolean tornarAtivo) throws ConexaoException, ModelException, ConfiguracaoException, SQLException {

		if ( verificarPermissaoEditar( id_usuario ) == false )
			throw new ModelException("Usuário sem permissão para editar Configurações do Equipamento!");

		ConfiguracaoEquipamento ret = null;

//		Todo: Voltar para a versão CET-RIO
		
		if(conf.versaoConfigEquipApp <= 0)
			throw new ModelException("Versão de Software antiga. Favor efetuar download da versão atualizada no GTW (Menu Ferramentas --> ConfigEquipApp)");
		
		else 
		{
			if(conf.versaoConfigEquipApp != buscarUltimaVersaoConfiEquipApp())
				throw new ModelException("Versão de Software desatualizada. Favor efetuar download da versão atualizada no GTW (Menu Ferramentas --> ConfigEquipApp)");
				
		}
		
		
		/*		
		if (conf.local.sequenciaLocal > 0) {//Então tenta subtituir...

			Local local = Local.buscaLocalPorId(conf.local.idLocal, conf.local.sequenciaLocal);

			if (local == null) {
				if ( verificarPermissaoNovo( id_usuario ) == false )
					throw new ModelException("Usuário sem permissão para criar nova Configurações do Equipamento!");
			}

			LocalVigente vigente = LocalVigente.buscaLocalVigentePorIdLocal( conf.local.idLocal );

			if ( local != null && vigente != null && (!local.getIdConfiguracaoEquipamento().equals( vigente.getIdConfiguracaEquipamento()) ) )
				throw new ModelException("Falha de consistância, tentando substibuir uma configuração anterior a vigente!");

			if ( vigente == null || vigente.getEmOperacao() || local == null || !excluirConfigEquip( local.getIdConfiguracaoEquipamento(), true ) ) {
				throw new ModelException("Falha de consistência, tentando substibuir uma configuração que já foi utilizada ou que não existe!");

			}
		}
 		*/
		
		LocalVigente vigente = LocalVigente.buscaLocalVigentePorSerieEquipamento( conf.serie );
		if (vigente == null) {
			if ( verificarPermissaoNovo( id_usuario ) == false )
				throw new ModelException("Usuário sem permissão para criar nova Configurações do Equipamento!");
		}

		ConfiguracaoEquipamentoBroker bk = new ConfiguracaoEquipamentoBroker();
		ret = bk.incluirConfiguracaoEquipamento(id_usuario, conf, tornarAtivo);

		return ret;
	}
	private static boolean verificarPermissaoNovo(Integer idUsuario) throws ConexaoException, ConfiguracaoException {

		List<GrupoBean> grupos = Grupo.buscaGruposPorIdUsuario(idUsuario);

		for (GrupoBean grupoBean : grupos) {
			if ( grupoBean.getId() == ConfiguracaoProvider.getInstance().getIdGrupoPermissaoNovoEquip() ) {
				return true;
			}
		}

		return false;
	}

	public static Boolean excluirConfigEquip(Integer idConfigEquip) throws SQLException, ConexaoException, ModelException {
		return excluirConfigEquip(idConfigEquip, false);
	}

	private static Boolean excluirConfigEquip(Integer idConfigEquip, Boolean trataErroFK) throws ConexaoException, ModelException {
		Boolean bRet = false;

		String sSQL = "DELETE FROM configuracao_equipamento" +
		"		WHERE id_configuracao_equipamento = ?";

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);
			ps.setInt(1, idConfigEquip);
			bRet = ps.executeUpdate() > 0;
		} catch (SQLException e) {

			if (trataErroFK && e.getErrorCode() == ERRO_FK) {
				bRet = false;
			}
			else {
				throw new ConexaoException("ERRO de SQL excluirConfigEquip(1)", e);
			}
		}		
		finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL excluirConfigEquip(2)", e);
			}
		}

		return bRet;
	}

	public static void setEmOperacao(Integer idConfigEquip, Boolean bValor) throws SQLException, ConexaoException, ModelException {

		String sSQL = "UPDATE configuracao_equipamento SET em_operacao = ?" +
		"		WHERE id_configuracao_equipamento = ?";

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);
			ps.setBoolean(1, bValor);
			ps.setInt(2, idConfigEquip);

			if (!(ps.executeUpdate() > 0))
				throw new ModelException("Equipamento não encontrado.");
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL setEmOperacao(1)", e);
		}		
		finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL setEmOperacao(2)", e);
			}			
		}		

	}

	public TConfigEquip getTconfigEqup() {
		return tconfigEqup;
	}

	public Integer getIdConfigEquip() {
		return idConfigEquip;
	}
	
	private static int buscarUltimaVersaoConfiEquipApp() throws ConexaoException {

		int ret = 0;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT max(versao) as Ult_Versao FROM versao_config_equip_app (nolock) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			if (rs.next()) {
				ret = rs.getInt("Ult_Versao");
			}
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL ao obter ultima versao_config_equip_app", e);
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
				throw new ConexaoException("ERRO de SQL ao obter ultima versao_config_equip_app", e);
			}			
		}
		
		return ret;
	}



}
