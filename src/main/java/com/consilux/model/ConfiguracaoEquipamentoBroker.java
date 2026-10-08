/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.model

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 15/05/2009

  Descricao: XXX

  Historico:

    $Log: ConfiguracaoEquipamentoBroker.java,v $
    Revision 1.1  2009/05/18 14:27:47  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.TConfigEquip.TAfericao;
import com.consilux.model.TConfigEquip.TCanalPL;
import com.consilux.model.TConfigEquip.TCapturaVeiculo;
import com.consilux.model.TConfigEquip.TChannelConfigs;
import com.consilux.model.TConfigEquip.TCollection;
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
import com.consilux.model.TConfigEquip.TCamera;
import com.consilux.model.TConfigEquip.TCanalPP;
import com.consilux.model.TConfigEquip.TPista;
import com.consilux.model.TConfigEquip.TRegraInfracao;
import com.consilux.model.TConfigEquip.TResolucaoImagem;
import com.consilux.model.TConfigEquip.TRodizio;
import com.consilux.model.TConfigEquip.TRodovia;
import com.consilux.model.TConfigEquip.TSensorPiezo;
import com.consilux.model.TConfigEquip.TServidor;
import com.consilux.model.TConfigEquip.TStringList;
import com.consilux.model.TConfigEquip.TConfigAGD_Device;
import com.consilux.model.TConfigEquip.TConfigAGD_Pista;
import com.consilux.model.TConfigEquip.TConfigAGD_Road;
import com.consilux.model.TConfigEquip.TConfigAGD_Software;
import com.consilux.model.TConfigEquip.TConfigDimensoesML;
import com.consilux.model.TConfigEquip.TConfigLacoVirtualML;
import com.consilux.model.TConfigEquip.TConfigPesagem;
import com.consilux.model.TConfigEquip.TConfigPistaPesagem;
import com.consilux.model.exception.ModelException;

/**
 * XXX
 * @author fos
 * @version $Revision: 1.1 $ $Date: 2009/05/18 14:27:47 $ $Author: fos $
 */

public class ConfiguracaoEquipamentoBroker {
	private final Connection conn;

	private static Logger logger = Logger.getLogger(ConfiguracaoEquipamentoBroker.class);
	/**
	* Constrói o objeto ConfiguracaoEquipamentoBroker a partir dos parâmetros dados.
	* @param conn
	 * @throws ConexaoException 
	*/
	protected ConfiguracaoEquipamentoBroker() throws ConexaoException {
		this.conn = Conexao.getConexao();
		try {
			this.conn.setAutoCommit(false);
		} catch (SQLException ex) {
			ex.printStackTrace();
			new ConexaoException("Erro ao ajustar na conexão para autoCommit=false.",ex);
		}
	}
	
	protected ConfiguracaoEquipamento incluirConfiguracaoEquipamento(Integer idUsuario, TConfigEquip conf, Boolean tornarAtivo) throws SQLException, ConfiguracaoException, ModelException {
		ConfiguracaoEquipamento ret = null;
		StringBuilder sbSQL = new StringBuilder();
		
		if(tornarAtivo == null) {
			tornarAtivo = true;
		}

		sbSQL.append("INSERT INTO configuracao_equipamento (");
		sbSQL.append("	serie_equipamento, ");
		sbSQL.append("	ativo,");
		sbSQL.append("	obs,");
		sbSQL.append("	watch_dog,");
		sbSQL.append("	controladora,");
		sbSQL.append("	iluminador,");
		sbSQL.append("	id_produto,");
		sbSQL.append("	com_controladora,");
		sbSQL.append("	com_auxiliar,");
		sbSQL.append("	com_iluminador,");
		sbSQL.append("	id_grupo_equipamento,");
		sbSQL.append("	flag_opcao,");
		sbSQL.append("	data_modificacao,");
		sbSQL.append("	data_inicio,");
		sbSQL.append("	categoria,");
		sbSQL.append("	id_usuario,");
		sbSQL.append("  distancia_equipamento,");
		sbSQL.append("  tempo_ciclagem,");
		
		sbSQL.append("  TempoTotalVideo,");
		sbSQL.append("  TempoVideoAntesInfracao,");
		
		sbSQL.append("  ativar_montante,");
		sbSQL.append("  ativar_jusante,");
		sbSQL.append("  porta_montante,");
		sbSQL.append("  codigo_montante,");
		
		sbSQL.append("  cod_GIT_Contrato,");
		sbSQL.append("  cod_GIT_Ponto,");
		
		sbSQL.append("  CodigoEquipCliente,");
		
		sbSQL.append("  tempo_adicional_faixa_exclusiva,");
		sbSQL.append("  tempo_fluxo_zero,");
		sbSQL.append("  diferenca_percentual_bloqueio_faixa ) ");
		
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);

		try {
			//Ajustando os valores dos parametros:
			ps.setInt(1, conf.serie);
			ps.setInt(2, 0); //Ativo = NÃO
			ps.setString(3, conf.observacao);
			ps.setInt(4, conf.watchDog);
			ps.setInt(5, conf.controladora);
			ps.setInt(6, conf.iluminador);
			ps.setInt(7, conf.idProduto);
			ps.setInt(8, conf.comControladora);
			ps.setInt(9, conf.comAuxiliar);
			ps.setInt(10, conf.comIluminador);
	
			if ( conf.idGrupoEquipamento > 0)
				ps.setInt(11, conf.idGrupoEquipamento);
			else
				ps.setInt(11, ConfiguracaoProvider.getInstance().getIdGrupoEquipamento());
	
			ps.setInt(12, conf.flagOpcao.getCombinacao());
			
			ps.setTimestamp(13, new Timestamp(Funcoes.convertPascalDateToUTC(conf.dataHoraConfiguracao).getTime()));
			ps.setTimestamp(14, new Timestamp(Funcoes.convertPascalDateToUTC(conf.inicioOperacao).getTime()));
			ps.setInt(15, conf.categoria);
			ps.setInt(16, idUsuario);

			ps.setInt(17, conf.distanciaEntreEquip);
			ps.setInt(18, conf.tempoDeCiclagem);
			
			ps.setInt(19, conf.TempoTotalVideo);
			ps.setInt(20, conf.TempoVideoAntesInfracao);
			
			ps.setInt(21, conf.AtivarEquipamentoComoMontante);
			ps.setInt(22, conf.AtivarEquipamentoComoJusante);
			ps.setInt(23, conf.PortaEquipamentoMontante);
			ps.setInt(24, conf.CodigoLocalMontante);
			
			ps.setInt(25, conf.local.cod_GIT_Contrato);
			ps.setInt(26, conf.local.cod_GIT_Ponto);
			
			ps.setString(27, conf.local.codigoEquipCliente);
			
			ps.setInt(28, conf.tempoAdicionalFaixaExclusiva);
			ps.setInt(29, conf.tempoFluxoZero);
			ps.setInt(30, conf.diferencaPercentualBloqueioFaixa);
			
			try {
				if (ps.executeUpdate() > 0) {
					ResultSet rs = ps.getGeneratedKeys(); //pegando o identity.
					if (rs.next()) {
						ret = new ConfiguracaoEquipamento(rs.getInt(1), conf);
						
						incluirLocal(ret.getIdConfigEquip(), conf.local, conf.localidade);
						incluirConfiguracaoRelevante(ret.getIdConfigEquip(), conf.configuracaoRelevante);
						incluirConfiguracaoPesagem(ret.getIdConfigEquip(), conf.Pesagem);
						incluirConfiguracaoLacoVirtualML(ret.getIdConfigEquip(), conf.LacoVirtualML);
						incluirConfiguracaoDimensoesML(ret.getIdConfigEquip(), conf.DimensoesML);
						incluirConfiguracaoResolucaoImagem(ret.getIdConfigEquip(), conf.ResolucaoImagem);
						incluirRodovia(ret.getIdConfigEquip(), conf.rodovia);
						incluirAfericao(ret.getIdConfigEquip(), 1, 0, conf.afericao);
						incluirCameras(ret.getIdConfigEquip(), conf.cameras);
						incluirControladores(ret.getIdConfigEquip(), conf.controladoresPL);
						incluirControladoresPesagem(ret.getIdConfigEquip(), conf.controladoresPP);
						incluirPistas(ret.getIdConfigEquip(), conf.pistas);
						incluirHorarios(ret.getIdConfigEquip(), conf.horarios);
						incluirNiveisVideo(ret.getIdConfigEquip(), conf.niveisVideo);
						incluirRodizios(ret.getIdConfigEquip(), conf.rodizios);
						incluirRegrasInfracao(ret.getIdConfigEquip(), conf.regrasInfracao);
						incluirParametrosAdicionais(ret.getIdConfigEquip(), conf.parametrosAdicionais);
						incluirConfiguracoesGeralDiv(ret.getIdConfigEquip(), conf.divsGeral);
						incluirConfiguracoesDIV(ret.getIdConfigEquip(), conf.configuracaoDIVs);
						incluirConfiguracoesPainel(ret.getIdConfigEquip(), conf.configuracaoPaineis);
						incluirConfiguracaoGeralPainel(ret.getIdConfigEquip(), conf.configuracaoGeralPainel);
						incluirServidor(ret.getIdConfigEquip(), 1, conf.servidorPrimario);
						incluirServidor(ret.getIdConfigEquip(), 2, conf.servidorSecundario);
						incluirServidor(ret.getIdConfigEquip(), 3, conf.servidorMontante);
						incluirConfiguracaoAGDGeral(ret.getIdConfigEquip(), conf.configAGD_Device, conf.configAGD_Road, conf.configAGD_Software);
					}
					this.conn.commit();
					//Já foi grava a configuração do equipamento, agora pode ativá-la.
					Local l = Local.buscaLocalPorId(conf.local.idLocal, conf.local.sequenciaLocal);
					
					try {
						logger.info("Id Config Equip: " + l.getIdConfiguracaoEquipamento() + "; Ativo: " + tornarAtivo + "; Local: " + conf.local.idLocal + "; Sequencia: " + conf.local.sequenciaLocal);
					} catch(Exception e) {
						logger.error("ERRO LOGGER", e);
					}
					
					// se for para tornar ativo, 
					if(tornarAtivo.equals(true)) { 
						l.setLocalAtivo(true);
					}
				}
			}
			catch (SQLException ex) {
				this.conn.rollback();
				throw ex;
			}
			catch(ModelException ex) {
				this.conn.rollback();
				throw ex;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				this.conn.rollback();
			}
		}
		finally {
			ps.close();
		}
		return ret;
	}
	
	private Boolean incluirConfiguracaoResolucaoImagem(Integer idConfigEquip, TResolucaoImagem resolucaoImagem) throws SQLException, ModelException {
		Boolean ret = false;

		PreparedStatement ps = this.conn.prepareStatement(
				"INSERT INTO configuracao_equipamento_resolucao_imagem VALUES (?,?,?,?,?)");
		
		try {
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, resolucaoImagem.LarguraImagemInfracao);
			ps.setInt(3, resolucaoImagem.AlturaImagemInfracao);
			ps.setInt(4, resolucaoImagem.LarguraImagemOcr);
			ps.setInt(5, resolucaoImagem.AlturaImagemOcr);
			
			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}
		
		return ret;	
	}
	
	private Boolean incluirConfiguracaoLacoVirtualML(Integer idConfigEquip, TConfigLacoVirtualML lacoVirtualML) throws SQLException, ModelException {
		Boolean ret = false;

		PreparedStatement ps = this.conn.prepareStatement(
				"INSERT INTO configuracao_equipamento_laco_virtual_ml VALUES (?,?,?)");
		
		try {
			ps.setInt(1, idConfigEquip);
			ps.setBoolean(2, lacoVirtualML.HabilitarLV_ML);
			ps.setString(3, lacoVirtualML.PontosVirtuais.Text);
			
			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}
		
		return ret;	
	}

	private Boolean incluirConfiguracaoDimensoesML(Integer idConfigEquip, TConfigDimensoesML dimensoesML) throws SQLException, ModelException {
		Boolean ret = false;

		PreparedStatement ps = this.conn.prepareStatement(
				"INSERT INTO configuracao_equipamento_dimensoes_ml VALUES (?,?,?,?,?)");
		
		try {
			ps.setInt(1, idConfigEquip);
			ps.setBoolean(2, dimensoesML.HabilitarMedicaoML);
			ps.setBoolean(3, dimensoesML.TimerHabilitar);
			ps.setInt(4, dimensoesML.TimerIntervalo);
			ps.setString(5, dimensoesML.PontosVirtuais.Text);
			
			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}
		
		return ret;	
	}

	private Boolean incluirConfiguracaoPesagem(Integer idConfigEquip, TConfigPesagem pesagem) throws SQLException, ModelException {
		Boolean ret = false;
		
		PreparedStatement ps_1 = this.conn.prepareStatement(
		"INSERT INTO configuracao_equipamento_pesagem VALUES (?,?,?,?,?)");
		PreparedStatement ps_2 = this.conn.prepareStatement(
		"INSERT INTO configuracao_equipamento_pista_pesagem VALUES (?,?,?)");
		PreparedStatement ps_3 = this.conn.prepareStatement(
		"INSERT INTO configuracao_equipamento_sensor_piezo VALUES (?,?,?,?,?)");
		
		try {
			ps_1.setInt(1, idConfigEquip);
			ps_1.setBoolean(2, pesagem.PesagemHabilitada);
			ps_1.setString(3, pesagem.ConfigConvAd.Text);
			ps_1.setString(4, pesagem.ConfigPesagemParam.Text);
			ps_1.setTimestamp(5, new Timestamp(Funcoes.convertPascalDateToUTC(pesagem.DataUltimaCalibracao).getTime()));
			
			ret = ps_1.executeUpdate() > 0;
			
			for (TConfigPistaPesagem pista : pesagem.ConfigPistasPesagem) {
				ps_2.setInt(1, idConfigEquip);
				ps_2.setInt(2, pista.IdPista);
				ps_2.setDouble(3, pista.DistSegundoLacoSensor);
				
				ret = ret && (ps_2.executeUpdate() > 0);
				
				int seq = 1;
				for (TSensorPiezo sensor : pista.Sensores) {
					ps_3.setInt(1, idConfigEquip);
					ps_3.setInt(2, pista.IdPista);
					ps_3.setInt(3, seq++);
					ps_3.setInt(4, sensor.Id);
					ps_3.setDouble(5, sensor.FatorCal);
					
					ret = ret && (ps_3.executeUpdate() > 0);
				}
			}
		}		
		finally {
			ps_1.close();
			ps_2.close();
			ps_3.close();
		}
		
		return ret;	
	}

	private Boolean incluirControladoresCanais(Integer idConfigEquip, Integer id, TCollection<TCanalPL> canais) throws SQLException, ModelException {
		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("INSERT INTO configuracao_equipamento_controlador_canais (");
		sbSQL.append("  id_configuracao_equipamento,");
		sbSQL.append("  id,");
		sbSQL.append("  item,");
		sbSQL.append("  modoHabilitar,");
		sbSQL.append("  modoSensibilidade,");
		sbSQL.append("  sensibilidadeEntrada,");
		sbSQL.append("  sensibilidadeSaida,");
		sbSQL.append("  configOscilador,");
		sbSQL.append("  eventosMonitorados,");
		sbSQL.append("  divisorPerfil)");
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());
		
		try {

			int item = 0;
			for (TCanalPL canal : canais) {
				ps.setInt(1, idConfigEquip);
				ps.setInt(2, id);
				ps.setInt(3, item++);
				ps.setInt(4, canal.modoHabilitar);
				ps.setInt(5, canal.modoSensibilidade);
				ps.setInt(6, canal.sensibilidadeEntrada);
				ps.setInt(7, canal.sensibilidadeSaida);
				ps.setInt(8, canal.configOscilador);
				ps.setInt(9, canal.eventosMonitorados);
				ps.setInt(10, canal.divisorPerfil);
				
				ret = ps.executeUpdate() > 0;
			}
		}		
		finally {
			ps.close();
		}
		
		return ret;	
	}
	
	private Boolean incluirControladoresCanaisv2(Integer idConfigEquip, Integer id, TCollection<TChannelConfigs> canais) throws SQLException, ModelException {
		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("INSERT INTO configuracao_equipamento_controlador_canaisv2 (");
		sbSQL.append("  id_configuracao_equipamento,");
		sbSQL.append("  id_controlador, id,");
		sbSQL.append("  canal,");
		sbSQL.append("  operacao,");
		sbSQL.append("  registrador,");
		sbSQL.append("  valor)");
		sbSQL.append("VALUES (?,?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());
		
		try {

			for (TChannelConfigs canal : canais) {
				for(TLoopDetectorChannelConfig canal1 : canal.configuracoes)
				{
					ps.setInt(1, idConfigEquip);
					ps.setInt(2, id);
					ps.setInt(3, canal.id);
					ps.setInt(4, canal1.canal);
					ps.setInt(5, canal1.operacao);
					ps.setInt(6, canal1.registrador);
					ps.setLong(7, canal1.valor);
					
					ret = ps.executeUpdate() > 0;
				}
			}
		}		
		finally {
			ps.close();
		}
		
		return ret;	
	}
	
	private Boolean incluirControladores(Integer idConfigEquip,
			TCollection<TControladorPL> controladoresPL) throws SQLException, ModelException { 
		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("INSERT INTO configuracao_equipamento_controlador (");
		sbSQL.append("  id_configuracao_equipamento,");
		sbSQL.append("  id,");
		sbSQL.append("  porta,");
		sbSQL.append("  bitsPorSegundo,");
		sbSQL.append("  bitsDados,");
		sbSQL.append("  bitsParada,");
		sbSQL.append("  paridade )");
		sbSQL.append("VALUES (?,?,?,?,?,?,?)");
		
		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());
		
		try {
			
			for (TControladorPL controlador : controladoresPL) {

				ps.setInt(1, idConfigEquip);
				ps.setInt(2, controlador.id);
				ps.setString(3, controlador.porta);
				ps.setInt(4, controlador.bitsPorSegundo);
				ps.setInt(5, controlador.bitsDados);
				ps.setInt(6, controlador.bitsParada);
				ps.setInt(7, controlador.paridade);
				
				ret = ps.executeUpdate() > 0;
				if (ret) {
				ret = incluirControladoresCanais(idConfigEquip, controlador.id, controlador.canais);
				ret = ret && incluirControladoresCanaisv2(idConfigEquip, controlador.id, controlador.canaisV2);
				}
			}
		}		
		finally {
			ps.close();
		}
		
		return ret;		
		
	}
	
	
	private Boolean incluirControladoresPesagem(Integer idConfigEquip,
			TCollection<TControladorPP> controladoresPP) throws SQLException, ModelException { 
		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("INSERT INTO configuracao_equipamento_controlador_pesagem ");
		sbSQL.append("	(");
		sbSQL.append("  	id_configuracao_equipamento,");
		sbSQL.append("  	id,");
		sbSQL.append("  	porta_S1,");
		sbSQL.append("  	bitsPorSegundo_S1,");
		sbSQL.append("  	bitsDados_S1,");
		sbSQL.append("  	bitsParada_S1,");
		sbSQL.append("  	paridade_S1, ");
		sbSQL.append("  	tempoReconexao_S1, ");
		sbSQL.append("  	tamanhoBuffer_S1, ");
		sbSQL.append("  	porta_S2,");
		sbSQL.append("  	bitsPorSegundo_S2,");
		sbSQL.append("  	bitsDados_S2,");
		sbSQL.append("  	bitsParada_S2,");
		sbSQL.append("  	paridade_S2, ");
		sbSQL.append("  	tempoReconexao_S2, ");
		sbSQL.append("  	tamanhoBuffer_S2 ");
		sbSQL.append("  )");
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());
		
		try {
			
			for (TControladorPP controlador : controladoresPP) {

				ps.setInt(1, idConfigEquip);
				ps.setInt(2, controlador.id);
				ps.setInt(3, controlador.porta_S1);
				ps.setInt(4, controlador.bitsPorSegundo_S1);
				ps.setInt(5, controlador.bitsDados_S1);
				ps.setInt(6, controlador.bitsParada_S1);
				ps.setString(7, controlador.paridade_S1);
				ps.setInt(8, controlador.tempoReconexao_S1);
				ps.setInt(9, controlador.tamanhoBuffer_S1);
				ps.setInt(10, controlador.porta_S2);
				ps.setInt(11, controlador.bitsPorSegundo_S2);
				ps.setInt(12, controlador.bitsDados_S2);
				ps.setInt(13, controlador.bitsParada_S2);
				ps.setString(14, controlador.paridade_S2);
				ps.setInt(15, controlador.tempoReconexao_S2);
				ps.setInt(16, controlador.tamanhoBuffer_S2);
				
				ret = ps.executeUpdate() > 0;
				if (ret)
				ret = incluirControladoresPesagemCanais(idConfigEquip, controlador.id, controlador.canais);
				
			}
		}		
		finally {
			ps.close();
		}
		
		return ret;		
		
	}
	
	private Boolean incluirControladoresPesagemCanais(Integer idConfigEquip, Integer id, TCollection<TCanalPP> canais) throws SQLException, ModelException {
		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("INSERT INTO configuracao_equipamento_controlador_pesagem_canais (");
		sbSQL.append("  id_configuracao_equipamento,");
		sbSQL.append("  id,");
		sbSQL.append("  item,");
		sbSQL.append("  canalFisico,");
		sbSQL.append("  offSet,");
		sbSQL.append("  inverterPolaridade)");
		sbSQL.append("VALUES (?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());
		
		try {

			int item = 0;
			for (TCanalPP canal : canais) {
				ps.setInt(1, idConfigEquip);
				ps.setInt(2, id);
				ps.setInt(3, item++);
				ps.setInt(4, canal.canalFisico);
				ps.setInt(5, canal.offSet);
				ps.setBoolean(6, canal.inverterPolaridade);
				
				ret = ps.executeUpdate() > 0;
				
			}
		}		
		finally {
			ps.close();
		}
		
		return ret;	
	}

	private Boolean incluirLocal(Integer idConfigEquip, TLocal local, TLocalidade localidade) throws SQLException, ModelException {
		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO local (");
		sbSQL.append("	id_local, ");
		sbSQL.append("	sequencia_local,");
		sbSQL.append("	nome,");
		sbSQL.append("	data_atualizacao,");
		sbSQL.append("	id_configuracao_equipamento,");
		sbSQL.append("	posicao_lat,");
		sbSQL.append("	posicao_lon,");
		sbSQL.append("	id_localidade,");
		sbSQL.append("	cep,");
		sbSQL.append("	complemento,");
		sbSQL.append("	dataEnsaioNaoMetrol,");
		sbSQL.append("  localidade_desc) ");
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());
		try {
			//Ajustando os valores dos parametros:
			ps.setInt(1, local.idLocal);

			local.sequenciaLocal = buscaProximaSequenciaLocal(local.idLocal); 

			ps.setInt(2, local.sequenciaLocal);
			ps.setString(3, local.nome);
			ps.setTimestamp(4, new Timestamp(new Date().getTime()));
			ps.setInt(5, idConfigEquip);

			if (local.latitude != 0)
				ps.setDouble(6, local.latitude);
			else
				ps.setNull(6, Types.INTEGER);

			if (local.longitude != 0)
				ps.setDouble(7, local.longitude);
			else
				ps.setNull(7, Types.INTEGER);

			if (localidade.idLocalidade > 0)
				ps.setInt(8, localidade.idLocalidade);
			else
				ps.setNull(8, Types.INTEGER);

			ps.setInt(9, local.cep);
			ps.setString(10, local.complemento);	
			ps.setTimestamp(11, new Timestamp(Funcoes.convertPascalDateToUTC(local.dataEnsaioNaoMetrol).getTime()));
			ps.setString(12, localidade.nome);

			ret = ps.executeUpdate() > 0;
			
		} catch (Exception ex) {
			logger.error("Erro ao inserir o local: ", ex);
			throw new ModelException("Erro ao inserir o local: "+ex.getMessage());
		} 
		finally {
			ps.close();
		}
		return ret;
	}
	
	private Boolean incluirConfiguracaoRelevante(Integer idConfigEquip, TConfiguracaoRelevante confRelevante) throws SQLException, ModelException {
		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_relevante (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	time_zone,");
		sbSQL.append("	numero_imagens,");
		sbSQL.append("	tempo_autonomia_nobreak,");	
		sbSQL.append("	endereco_sistema_relevante, ");
		sbSQL.append("	delta_minimo_para_filtro, ");
		sbSQL.append("	tempo_min_aciona_laco,");
		sbSQL.append("	diferenca_perc_delta_max,");
		sbSQL.append("	socket_controlador_1,");
		sbSQL.append("	socket_controlador_2)");
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());
		try {
			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);
			ps.setString(2, confRelevante.TimeZone);
			ps.setInt(3, confRelevante.NumeroImagens);
			ps.setInt(4, confRelevante.TempoAutonomiaNobreak);
			ps.setString(5, confRelevante.EnderecoSistemaRelevante);
			ps.setInt(6, confRelevante.DeltaMinimoParaFiltro);	
			ps.setInt(7,  confRelevante.TempoMinAcionaLaco);
			ps.setInt(8,  confRelevante.DiferencaPercDeltaMax);
			ps.setInt(9,  confRelevante.SocketControlador1);
			ps.setInt(10, confRelevante.SocketControlador2);

			ret = ps.executeUpdate() > 0;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ModelException("Erro ao incluir configuracao relevante: " + ex.getMessage());
		} 
		finally {
			ps.close();
		}
		return ret;
	}
	
	private Boolean incluirRodovia(Integer idConfigEquip, TRodovia rodovia) throws SQLException {

		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_rodovia (");
		sbSQL.append("	id_configuracao_equipamento,");
		sbSQL.append("	sigla,");
		sbSQL.append("	numero,");
		sbSQL.append("	acesso,");
		sbSQL.append("	km,");
		sbSQL.append("	metros)");
		sbSQL.append("VALUES (?,?,?,?,?,?)");

		PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
		try {
			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);
			ps.setString(2, rodovia.nome);
			ps.setInt(3, rodovia.numero);
			ps.setString(4, rodovia.acesso);
			ps.setInt(5, rodovia.quilometro);
			ps.setInt(6, rodovia.metros);

			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}

		return ret;
	}

	private Boolean incluirAfericao(Integer idConfigEquip, Integer idAfericao, Integer idPista, TAfericao afericao) throws SQLException {

		Boolean ret = false;
		StringBuffer sbSQL = new StringBuffer();

		sbSQL.append("INSERT INTO configuracao_equipamento_afericao (");
		sbSQL.append("	id_configuracao_equipamento,");
		sbSQL.append("	id_afericao,");
		sbSQL.append("	id_pista,");
		sbSQL.append("	referencia,");
		sbSQL.append("	selagem,");
		sbSQL.append("	laudo,");
		sbSQL.append("	data,");
		sbSQL.append("	data_validade)");
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());;

		try {
			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, idAfericao);

			if (idPista > 0)
				ps.setInt(3, idPista);
			else
				ps.setNull(3, Types.INTEGER);

			ps.setString(4, afericao.referencia);
			ps.setString(5, afericao.selagem);
			ps.setInt(6, afericao.laudo);
			ps.setTimestamp(7, new Timestamp(Funcoes.convertPascalDateToUTC(afericao.dataAfericao).getTime()));
			ps.setTimestamp(8, new Timestamp(Funcoes.convertPascalDateToUTC(afericao.dataValidadeAfericao).getTime()));

			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}

		return ret;
	}

	private Boolean incluirCameras(Integer idConfigEquip, TCollection<TCamera> cameras) throws SQLException {
		
		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("INSERT INTO configuracao_equipamento_camera (");
		sbSQL.append("  id_configuracao_equipamento,");
		sbSQL.append("  id_camera,");
		sbSQL.append("  tipo,");
		sbSQL.append("  endereco,");
		sbSQL.append("  relevante ) ");
		sbSQL.append("VALUES (?,?,?,?,?)");
		
		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());
		
		try {
			
			for (TCamera camera : cameras) {

				ps.setInt(1, idConfigEquip);
				ps.setInt(2, camera.ID);
				ps.setInt(3, camera.Tipo.ordinal());
				ps.setString(4, camera.Endereco);
				ps.setBoolean(5, camera.Relevante);
				
				ret = ps.executeUpdate() > 0;
			}
		}		
		finally {
			ps.close();
		}
		
		return ret;		
	}
	
	private Boolean incluirPistas(Integer idConfigEquip, TCollection<TPista> pistas) throws SQLException {

		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_pista (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	id_pista,");
		sbSQL.append("	cod_pista,");
		sbSQL.append("	nome_pista,");
		sbSQL.append("	sentido,");
		
		sbSQL.append("	TempoMaximoAmarelo,");
		sbSQL.append("	TempoMaximoVermelho,");
		sbSQL.append("	TempoMinimoAmarelo,");
		
		sbSQL.append("	conector,");
		sbSQL.append("	id_div,");
		sbSQL.append("	id_div_display,");
		sbSQL.append("	trigger_infravermelho,");
		sbSQL.append("	id_painel_infravermelho,");
		sbSQL.append("	id_painel_ldr,");
		sbSQL.append("	pin_ldr,");
		sbSQL.append("	cod_pista_alternativo,");
		sbSQL.append("	cod_pista_prodam,");
		sbSQL.append("	semaforo_painel_id,");
		sbSQL.append("	pista_1_transversal,");
		sbSQL.append("	pista_2_transversal,");
		sbSQL.append("	pista_3_transversal,");
		sbSQL.append("	pista_4_transversal,");
		sbSQL.append("	pista_5_transversal,");
		sbSQL.append("	pista_6_transversal,");
		sbSQL.append("	pista_7_transversal,");
		sbSQL.append("	pista_8_transversal,");
		sbSQL.append("	captura_obj_frente,");
		sbSQL.append("	captura_obj_tras,");
		sbSQL.append("	captura_obj_laco,");
		sbSQL.append("	cod_area,");
		
		sbSQL.append("	tipo_disparo,");
		sbSQL.append("	ctrl_nivel_iluminador,");
		sbSQL.append("	nivel_inicial,");
		sbSQL.append("	nivel_final,");
		sbSQL.append("	lista_niveis,");
		sbSQL.append("	camera_iluminador,");
		
		sbSQL.append("  cod_pista_tarja,");
		sbSQL.append("  cod_local_prodam_auxiliar,");
		
		sbSQL.append("  paradaFaixaL1,");
		sbSQL.append("  paradaFaixaL2,");
		
		sbSQL.append("  faixa_exclusiva_direita,");
		sbSQL.append("  faixa_exclusiva_esquerda,");
		
		sbSQL.append("  entre_faixa,");
		
		//Códigos de identificação GIT para o Rio 2016
		////////////////////////////////////////////////////////////////////
		sbSQL.append("  cod_GIT_Logradouro,");
		sbSQL.append("  cod_GIT_Pista,");
		sbSQL.append("  cod_GIT_Sentido,");		
		sbSQL.append("  cod_GIT_Faixa,");
		////////////////////////////////////////////////////////////////////
		
		sbSQL.append("  id_camera_frontal,");
		sbSQL.append("  id_camera_traseira,");
		sbSQL.append("  id_camera_pan_1,");
		sbSQL.append("  id_camera_pan_2, ");
		sbSQL.append("  pista_relevante, ");
		sbSQL.append("  captura_reversa ) ");
		
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());;

		try {
			//Ajustando os valores dos parametros:
			Integer idAfericao = 2; //Começa em 2 porque a aferição 1 � a aferição geral.
			for (TPista pista : pistas) {

				ps.setInt(1, idConfigEquip);
				ps.setInt(2, pista.idPista);
				ps.setInt(3, pista.codPista);
				ps.setString(4, pista.nomePista);
				ps.setString(5, pista.sentido);
				
				ps.setInt(6, pista.TempoMaximoAmarelo);
				ps.setInt(7, pista.TempoMaximoVermelho);
				ps.setInt(8, pista.TempoMinimoAmarelo);
				
				ps.setInt(9, pista.conector);
				ps.setInt(10, pista.divId);
				ps.setInt(11, pista.divDisplayId);
				ps.setInt(12, pista.triggerInfravermelho);
				ps.setInt(13, pista.painelInfravermelhoId);
				ps.setInt(14, pista.ldrPainelId);
				ps.setInt(15, pista.ldrPin);
				ps.setInt(16, pista.idPistaAlternativo);
				ps.setInt(17, pista.codPistaProdam);
				ps.setInt(18, pista.semaforoPainelId);
				ps.setBoolean(19, pista.pista1Transversal);
				ps.setBoolean(20, pista.pista2Transversal);
				ps.setBoolean(21, pista.pista3Transversal);
				ps.setBoolean(22, pista.pista4Transversal);
				ps.setBoolean(23, pista.pista5Transversal);
				ps.setBoolean(24, pista.Pista6Transversal);
				ps.setBoolean(25, pista.Pista7Transversal);
				ps.setBoolean(26, pista.Pista8Transversal);
				ps.setBoolean(27, pista.CapturaObjFrente);
				ps.setBoolean(28, pista.CapturaObjTras);
				ps.setInt(29, pista.CapturaObjLaco);
				ps.setInt(30, pista.CodArea);
				
				// xxxx
				logger.info("TipoDisparo: " + pista.TipoDisparo + "; CtrlNivelIluminador: " 
				+ pista.CtrlNivelIluminador + "; NivelInicialIluminador: " + pista.NivelInicialIluminador
				+ "; NivelFinalIluminador: " + pista.NivelFinalIluminador + "; ListaNiveisIluminador: "
				+ pista.ListaNiveisIluminador + "; " + pista.EndCameraIluminador);
				
				ps.setBoolean	(31, pista.TipoDisparo);
				ps.setBoolean	(32, pista.CtrlNivelIluminador);
				ps.setInt		(33, pista.NivelInicialIluminador);
				ps.setInt		(34, pista.NivelFinalIluminador);
				ps.setString	(35, pista.ListaNiveisIluminador);
				ps.setInt		(36, pista.EndCameraIluminador);
				
				ps.setInt(37, pista.codPistaTarja);
				ps.setInt(38, pista.codLocalProdamAuxiliar);
				
				//[O.S 0067] - Modificar maquina de Estados do Captura para infrações de PF e AV
				ps.setBoolean(39, pista.ParadaFaixaL1);
				ps.setBoolean(40, pista.ParadaFaixaL2);
				
				//[O.S 0107] - Adicionado novos campos na pista para atender novo enquadramento 75870
				ps.setBoolean(41, pista.FaixaExclusivaDireita);
				ps.setBoolean(42, pista.FaixaExclusivaEsquerda);
				
				ps.setBoolean(43, pista.entreFaixa);
				
				//Códigos de identificação GIT para o Rio 2016
				////////////////////////////////////////////////////////////////////
				ps.setInt(44, pista.cod_GIT_Logradouro);
				ps.setInt(45, pista.cod_GIT_Pista);
				ps.setInt(46, pista.cod_GIT_Sentido);
				ps.setInt(47, pista.cod_GIT_Faixa);				
				
				// xxxx
				logger.info("cod_GIT_Logradouro: " + pista.cod_GIT_Logradouro);
				////////////////////////////////////////////////////////////////////
				
				ps.setInt(48, pista.IDCameraFrontal);
				ps.setInt(49, pista.IDCameraTraseira);
				ps.setInt(50, pista.IDCameraPan1);
				ps.setInt(51, pista.IDCameraPan2);
				ps.setBoolean(52, pista.PistaRelevante);
				ps.setBoolean(53, pista.CapturaReversa);
				
				incluirAfericao(idConfigEquip, idAfericao++, pista.idPista, pista.afericao);

				if (ps.executeUpdate() > 0) {
					ret = true;					
					ret = incluirCapturaVeiculo(idConfigEquip, pista.idPista, pista.capturaVeiculo);
					ret = ret && incluirConfiguracaoAGDPista(idConfigEquip, pista.idPista, pista.configAGD_Pista);
				}
				if (!ret)
					break;

			}
		}		
		finally {
			ps.close();
		}

		return ret;
	}
	
	private Boolean incluirCapturaVeiculo(Integer idConfigEquip, Integer idPista, TCapturaVeiculo capturaVeiculo) throws SQLException {

		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_captura_veiculo (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	id_pista,");
		sbSQL.append("	distancia_laco,");
		sbSQL.append("	largura_laco,");
		sbSQL.append("	com_perfil_magnetico,");
		sbSQL.append("	num_lacos,");
		sbSQL.append("	trigger_infra_vermelho,");
		sbSQL.append("	num_canal,");
		sbSQL.append("	num_imagens_pos_laco,");
		sbSQL.append("	interv_imagens_pos_laco,");
		sbSQL.append("	distancia_panoramica_pos_laco)");
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?,?,?,?)");
		
		PreparedStatement ps = conn.prepareStatement(sbSQL.toString());;

		try {
			ps = conn.prepareStatement(sbSQL.toString());

			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, idPista);
			ps.setDouble(3, capturaVeiculo.distanciaLaco);
			ps.setDouble(4, capturaVeiculo.larguraLaco);
			ps.setInt(5, capturaVeiculo.comPerfilMagnetico);
			ps.setInt(6, capturaVeiculo.numLacos);
			ps.setInt(7, capturaVeiculo.triggerInfravermelho);
			ps.setInt(8, capturaVeiculo.numCanal);
			ps.setInt(9, capturaVeiculo.numImagensPosLaco);
			ps.setDouble(10, capturaVeiculo.intervImagensPosLaco);
			ps.setInt(11, capturaVeiculo.distanciaPanoramicaPosLaco);

			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}

		return ret;
	}
	
	private Boolean incluirConfiguracaoAGDPista(Integer idConfigEquip, Integer idPista, TConfigAGD_Pista confAGD) throws SQLException {

		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_agd_pista (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	id_pista,");
		sbSQL.append("	capture_distance,");
		sbSQL.append("	starting_border,");
		sbSQL.append("	ending_border,");
		sbSQL.append("	direction,");
		sbSQL.append("	min_samples_for_projection,");
		sbSQL.append("	speed_samples ) ");
		
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?)");
		
		PreparedStatement ps = conn.prepareStatement(sbSQL.toString());;

		try {
			ps = conn.prepareStatement(sbSQL.toString());

			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, idPista);
			ps.setString(3, confAGD.CaptureDistance);
			ps.setString(4, confAGD.StartingBorder);
			ps.setString(5, confAGD.EndingBorder);
			ps.setString(6, confAGD.Direction);
			ps.setInt(7, confAGD.MinSamplesForProjection);
			ps.setInt(8, confAGD.SpeedSamples);

			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}

		return ret;
	}	

	private Boolean incluirHorarios(Integer idConfigEquip, TCollection<THorario> horarios) throws SQLException {
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_horario (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	id_horario,");
		sbSQL.append("	id_pista,");
		sbSQL.append("	horario_inicio,");
		sbSQL.append("	horario_fim)");
		sbSQL.append("VALUES (?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());

		try {

			//Ajustando os valores dos parametros:
			Integer idHorario = 1;
			for (THorario horario : horarios) {

				ps.setInt(1, idConfigEquip);
				ps.setInt(2, idHorario++);

				if (horario.idPista > 0)
					ps.setInt(3, horario.idPista);
				else
					ps.setNull(3, Types.INTEGER);

				ps.setTimestamp(4, new Timestamp(Funcoes.convertPascalDateToUTC(horario.horarioInicio).getTime()));
				ps.setTimestamp(5, new Timestamp(Funcoes.convertPascalDateToUTC(horario.horarioFim).getTime()));

				ret = ret && ps.executeUpdate() > 0;
				if (!ret)
					break;
			}
		}		
		finally {
			ps.close();
		}

		return ret;
	}

	private Boolean incluirNiveisVideo(Integer idConfigEquip, TCollection<TNivelVideo> niveisVideo) throws SQLException {

		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_nivel_video (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	id_nivel_video,");
		sbSQL.append("	id_pista,");
		sbSQL.append("	camera,");
		sbSQL.append("	horario_inicio,");
		sbSQL.append("	horario_fim,");
		sbSQL.append("	valor)");
		sbSQL.append("VALUES (?,?,?,?,?,?,?)");

		PreparedStatement ps = conn.prepareStatement(sbSQL.toString());

		try {
			//Ajustando os valores dos parametros:
			Integer idNivelVideo = 1;
			for (TNivelVideo nivelVideo : niveisVideo) {

				ps.setInt(1, idConfigEquip);
				ps.setInt(2, idNivelVideo++);

				if (nivelVideo.idPista > 0)
					ps.setInt(3, nivelVideo.idPista);
				else
					ps.setNull(3, Types.INTEGER);

				ps.setInt(4, nivelVideo.idCamera);
				ps.setTimestamp(5, new Timestamp(Funcoes.convertPascalDateToUTC(nivelVideo.horarioInicio).getTime()));
				ps.setTimestamp(6, new Timestamp(Funcoes.convertPascalDateToUTC(nivelVideo.horarioFim).getTime()));
				ps.setDouble(7, nivelVideo.valor);

				ret = ret && ps.executeUpdate() > 0;
				if (!ret)
					break;

			}
		}		
		finally {
			ps.close();
		}

		return ret;
	}

	private Boolean incluirRodizios(Integer idConfigEquip, TCollection<TRodizio> rodizios) throws SQLException {

		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_rodizio (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	id_rodizio,");
		sbSQL.append("	horario_inicio,");
		sbSQL.append("	horario_fim,");
		sbSQL.append("	dia_semana,");
		sbSQL.append("	final_placa)");
		sbSQL.append("VALUES (?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());

		try {
			//Ajustando os valores dos parametros:
			Integer idRodizio = 1;
			for (TRodizio rodizio : rodizios) {

				ps.setInt(1, idConfigEquip);
				ps.setInt(2, idRodizio++);
				ps.setTimestamp(3, new Timestamp(Funcoes.convertPascalDateToUTC(rodizio.horarioInicio).getTime()));
				ps.setTimestamp(4, new Timestamp(Funcoes.convertPascalDateToUTC(rodizio.horarioFim).getTime()));
				ps.setInt(5, rodizio.diaSemana);
				ps.setInt(6, rodizio.finalPlaca);

				ret = ret && ps.executeUpdate() > 0;
				if (!ret)
					break;
			}
		}		
		finally {
			ps.close();
		}

		return ret;
	}

	private Boolean incluirRegrasInfracao(Integer idConfigEquip, TCollection<TRegraInfracao> regrasInfracao) throws SQLException {

		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_regra_infracao (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	id_regra_infracao,");
		sbSQL.append("	id_pista,");
		sbSQL.append("	hora_ini,");
		sbSQL.append("	hora_fim,");
		sbSQL.append("	dia_ini,");
		sbSQL.append("	dia_fim,");
		sbSQL.append("	velocidade_limite,");
		sbSQL.append("	tolerancia,");
		sbSQL.append("	tolerancia_portaria,");
		sbSQL.append("	comprimento_ini,");
		sbSQL.append("	comprimento_fim,");
		sbSQL.append("	tipo,");
		sbSQL.append("	ativo,");
		sbSQL.append("	id_classe,");
		sbSQL.append("	tolerancia_vermelho,");
		sbSQL.append("	tolerancia_faixa,");
		sbSQL.append("	usar_panoramica,");
		sbSQL.append("	opcao_panoramica,");
		sbSQL.append("	fiscalizar_fase,");
		sbSQL.append("	num_imagens_pos_laco,");
		sbSQL.append("	intervalo_imagens_pos_laco,");
		sbSQL.append("	tolerancia_transversal,");
		sbSQL.append("	intervalo,");
		sbSQL.append("  final_placa, ");
		sbSQL.append("  remover_isencao_taxi, ");
		sbSQL.append("  ini_regra_taxi, ");
		sbSQL.append("  fim_regra_taxi ");
		sbSQL.append(")");
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());;

		try {
			//Ajustando os valores dos parâmetros:
			Integer idRegra = 1;
			for (TRegraInfracao regra : regrasInfracao) {

				ps.setInt(1, idConfigEquip);
				ps.setInt(2, idRegra++);

				if (regra.idPista > 0)
					ps.setInt(3, regra.idPista);
				else
					ps.setNull(3, Types.INTEGER);

				ps.setTimestamp(4, new Timestamp(Funcoes.convertPascalDateToUTC(regra.iniRegra).getTime()));
				ps.setTimestamp(5, new Timestamp(Funcoes.convertPascalDateToUTC(regra.fimRegra).getTime()));
				ps.setInt(6, regra.diaRegraIni);
				ps.setInt(7, regra.diaRegraFim);
				ps.setInt(8, regra.limite);
				ps.setInt(9, regra.tolerancia);
				ps.setInt(10, regra.toleranciaPortaria);
				ps.setDouble(11, regra.tamIni);
				ps.setDouble(12, regra.tamFim);
				ps.setString(13, regra.tipo);
				ps.setBoolean(14, regra.ativo);
				ps.setString(15, regra.perfil.toString());
				ps.setDouble(16, regra.toleranciaVermelho);
				ps.setDouble(17, regra.toleranciaFaixa);
				ps.setBoolean(18, regra.usarPanoramica);
				ps.setInt(19, regra.opcaoPanoramica);
				ps.setInt(20, regra.fiscalizarFase);
				ps.setInt(21, regra.numImagensPosLaco);
				ps.setDouble(22, regra.intervImagensPosLaco);
				ps.setInt(23, regra.toleranciaTransversal);
				ps.setInt(24, regra.intervalo);
				if(regra.finalPlaca == null)
					ps.setNull(25, Types.INTEGER);
				else
					ps.setInt(25, regra.finalPlaca);
				ps.setBoolean(26, regra.removerIsencaoTaxi);
				ps.setDouble(27, regra.iniRegraTaxi);
				ps.setDouble(28, regra.fimRegraTaxi);

				ret = ret && ps.executeUpdate() > 0;
				if (!ret)
					break;
			}
		}		
		finally {
			ps.close();
		}

		return ret;
	}

	private Boolean incluirParametrosAdicionais(Integer idConfigEquip, TStringList parametrosAdicionais) throws SQLException {

		Boolean ret = false;
		StringBuffer sbSQL = new StringBuffer();

		sbSQL.append("INSERT INTO configuracao_equipamento_parametros_adicionais (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	parametros_adicionais)");
		sbSQL.append("VALUES (?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());

		try {
			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);
			ps.setString(2, parametrosAdicionais.Text);

			ret = ps.executeUpdate() > 0;

		}		
		finally {
			ps.close();
		}

		return ret;
	}
	
	private Boolean incluirConfiguracoesGeralDiv(Integer idConfigEquip, TConfiguracaoGeralDivs divsGeral) throws SQLException {
		
		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_geral_divs (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	verdeVermelhoTolerancia, ");
		sbSQL.append("	tipoTolerancia,");
		sbSQL.append("	mostrarVelocidade,");
		sbSQL.append("	velocidadeSeparador,");
		sbSQL.append("	toleranciaFixa,");
		sbSQL.append("	toleranciaPercentual)");
		sbSQL.append("VALUES (?,?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());;

		try {
			ps.setInt(1, idConfigEquip);
			ps.setBoolean(2, divsGeral.verdeVermelhoTolerancia);
			ps.setInt(3, divsGeral.tipoTolerancia);
			ps.setBoolean(4, divsGeral.mostrarVelocidade);
			ps.setInt(5, divsGeral.velocidadeSeparador);
			ps.setInt(6, divsGeral.toleranciaFixa);
			ps.setInt(7, divsGeral.toleranciaPercentual);

			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}

		return ret;
	}

	private Boolean incluirConfiguracoesDIV(Integer idConfigEquip, TCollection<TConfiguracaoDIV> configuracoesDIV) throws SQLException {

		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_div (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	id_div,");
		sbSQL.append("	endereco,");
		sbSQL.append("	porta_com,");
		sbSQL.append("	versao,");
		sbSQL.append("	numero_digitos)");
		sbSQL.append("VALUES (?,?,?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());;

		try {
			//Ajustando os valores dos parametros:
			for (TConfiguracaoDIV configuracaoDIV : configuracoesDIV) {

				ps.setInt(1, idConfigEquip);
				ps.setInt(2, configuracaoDIV.divId);
				ps.setInt(3, configuracaoDIV.endereco);
				ps.setInt(4, configuracaoDIV.portaCOM);
				ps.setInt(5, configuracaoDIV.versao);
				ps.setInt(6, configuracaoDIV.numeroDigitos);

				ret = ret && ps.executeUpdate() > 0;
				if (!ret)
					break;
			}

		}		
		finally {
			ps.close();
		}

		return ret;
	}

	private Boolean incluirConfiguracoesPainel(Integer idConfigEquip, TCollection<TConfiguracaoPainel> configuracoesPainel) throws SQLException {

		Boolean ret = true;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_painel (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	id_painel,");
		sbSQL.append("	endereco,");
		sbSQL.append("	porta_com)");
		sbSQL.append("VALUES (?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());;

		try {
			for (TConfiguracaoPainel configuracaoPainel : configuracoesPainel) {

				//Ajustando os valores dos parametros:
				ps.setInt(1, idConfigEquip);
				ps.setInt(2, configuracaoPainel.painelId);
				ps.setInt(3, configuracaoPainel.endereco);
				ps.setInt(4, configuracaoPainel.portaCOM);

				ret = ret && ps.executeUpdate() > 0;
				if (!ret)
					break;
			}
		}		
		finally {
			ps.close();
		}		
		return ret;
	}

	private Boolean incluirConfiguracaoGeralPainel(Integer idConfigEquip, TConfiguracaoGeralPainel configuracaoGeralPainel) throws SQLException {

		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_painel_geral (");
		sbSQL.append("	id_configuracao_equipamento, ");
		sbSQL.append("	usar_ldr,");
		sbSQL.append("	id_painel_watchdog)");
		sbSQL.append("VALUES (?,?,?)");

		PreparedStatement ps = conn.prepareStatement(sbSQL.toString());;

		try {
			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);
			ps.setBoolean(2, configuracaoGeralPainel.usarLDR);
			ps.setInt(3, configuracaoGeralPainel.watchdogPainelId);

			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}

		return ret;
	}

	private Boolean incluirServidor(Integer idConfigEquip, int idServidor, TServidor servidor) throws SQLException {

		Boolean ret = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_servidor (");
		sbSQL.append("	id_configuracao_equipamento,");
		sbSQL.append("	id_servidor,");
		sbSQL.append("	host,");
		sbSQL.append("	port)");
		sbSQL.append("VALUES (?,?,?,?)");

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());

		try {
			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);
			ps.setInt(2, idServidor);
			ps.setString(3, servidor.host);
			ps.setInt(4, servidor.port);

			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}

		return ret;
	}

	private Boolean incluirConfiguracaoAGDGeral(Integer idConfigEquip, TConfigAGD_Device confAGDDevice, TConfigAGD_Road confAGDRoad, TConfigAGD_Software confAGDSoft) throws SQLException {
		
		Boolean ret = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO configuracao_equipamento_agd (");
		sbSQL.append("	id_configuracao_equipamento,");
		sbSQL.append("	road_side,");
		sbSQL.append("	vertical_angle,");
		sbSQL.append("	horizontal_angle,");
		sbSQL.append("	port_name,");
		sbSQL.append("	baud_rate,");
		sbSQL.append("	parity,");
		sbSQL.append("	data_bits,");
		sbSQL.append("	stop_bits,");
		sbSQL.append("	high_range_threshold,");
		sbSQL.append("	high_speed_threshold,");
		sbSQL.append("	low_range_threshold,");
		sbSQL.append("	low_speed_threshold,");
		sbSQL.append("	power_threshold,");
		sbSQL.append("	channel,");
		sbSQL.append("	sense,");
		sbSQL.append("	tracking_mode )");
		sbSQL.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");	

		PreparedStatement ps = this.conn.prepareStatement(sbSQL.toString());

		try {
			//Ajustando os valores dos parametros:
			ps.setInt(1, idConfigEquip);
			ps.setString(2, confAGDRoad.RoadSide);
			ps.setString(3, confAGDRoad.VerticalAngle);
			ps.setString(4, confAGDRoad.HorizontalAngle);
			ps.setString(5, confAGDSoft.PortName);
			ps.setInt(6, confAGDSoft.PortBaudRate);
			ps.setString(7, confAGDSoft.PortParity);
			ps.setInt(8, confAGDSoft.PortDataBits);
			ps.setString(9, confAGDSoft.PortStopBits);
			ps.setString(10, confAGDDevice.HighRangeThreshold);
			ps.setString(11, confAGDDevice.HighSpeedThreshold);
			ps.setString(12, confAGDDevice.LowRangeThreshold);
			ps.setString(13, confAGDDevice.LowSpeedThreshold);
			ps.setString(14, confAGDDevice.PowerThreshold);
			ps.setInt(15, confAGDDevice.Channel);
			ps.setInt(16, confAGDDevice.Sense);
			ps.setInt(17, confAGDSoft.TrackingMode);

			ret = ps.executeUpdate() > 0;
		}		
		finally {
			ps.close();
		}

		return ret;	
	}
	
	private Integer buscaProximaSequenciaLocal(Integer idLocal) throws SQLException {

		Integer ret = null;
		String sSQL = "SELECT COALESCE(MAX(sequencia_local),0) + 1 FROM local WHERE id_local = ?";

		PreparedStatement ps = conn.prepareStatement(sSQL);
		ResultSet rs = null;

		try {
			ps = this.conn.prepareStatement(sSQL);

			//Ajustando os valores dos parametros:
			ps.setInt(1, idLocal);

			rs = ps.executeQuery();
			if (rs.next()) {
				ret = rs.getInt(1);
			}
		}
		finally {
			ps.close();
		}

		return ret;
	}
	
	protected void finaliza() throws SQLException {
		this.conn.close();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		finaliza();
//		super.finalize();
	}
}
