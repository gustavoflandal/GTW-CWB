/**
 * 
 */
package com.consilux.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.SerializadorXML;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.exception.ModelException;

/**
 * @author fos
 *
 */


public class TConfigEquip {

	public static final String NAME_CLASS = "configEquip";
	
	public static TConfigEquip deserializeConfigEquip( String xmlConfigEquip ) throws ConexaoException, ModelException, ConfiguracaoException, SQLException, ParserConfigurationException, IOException, SAXException, IllegalArgumentException, IllegalAccessException, InstantiationException {

		StringBuffer buf = new StringBuffer(xmlConfigEquip);
		TConfigEquip ce = new TConfigEquip();
		SerializadorXML ser = new SerializadorXML();
			
		ser.loadFromStringBuffer(buf);
		ser.readObject(ce, TConfigEquip.NAME_CLASS );

		return ce;
		
	}
	
	public class TCollection<E extends TCollectionItem> extends ArrayList<E> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Class<E> tipo;
		public TCollection(Class<E> tipo) {
			this.tipo = tipo;
		}
		public E add() {
			try {
				E ret = this.tipo.getConstructor(TConfigEquip.class).newInstance(TConfigEquip.this);
				add(ret);
				return ret;
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	}
	public class TCollectionItem {
		
	}
	
	public enum TCameraTypes {
		eCameraTypeNenhum("eCameraTypeNenhum"),
        eCameraTypePumatronix("eCameraTypePumatronix");
        
//        private String val;
        
        TCameraTypes(String sVal) {
  //      	this.val = sVal;
		}
        /*public static TCameraTypes valueOf(String sVal) {
        	TCameraTypes ret = null;
        	for (TCameraTypes e: values()) {
				if (e.val.equals(sVal)) {
					ret = e;
				}
			}
        	return ret;
        }*/
	}
	
	public class TStringList {
		public String Text = "";
	}
	public class TLocalidade {
		public Integer idLocalidade = 0;
		public String nome = "";
	}
	public class TLocal {
		public Integer idLocal = 0;
		public Integer sequenciaLocal = 0;
		public String nome = "";
		public Double latitude = 0D;
		public Double longitude = 0D;

		//Códigos de identificação GIT para o Rio 2016
		////////////////////////////////////////////////////////////////////
		public Integer cod_GIT_Contrato 		= 0;
		public Integer cod_GIT_Ponto 			= 0;
		////////////////////////////////////////////////////////////////////
		public String 	complemento = "";
		public Integer 	cep = 0;
		public Double 	dataEnsaioNaoMetrol = 0D;
		public String codigoEquipCliente = "";
	}
	public class TConfiguracaoRelevante {
		public String TimeZone = "";
		public Integer NumeroImagens = 0;
		public Integer TempoAutonomiaNobreak = 0;
		public String EnderecoSistemaRelevante = "";
		public Integer DeltaMinimoParaFiltro = 0;
		public Integer TempoMinAcionaLaco = 20;
		public Integer DiferencaPercDeltaMax = 15;
		public Integer SocketControlador1 = 9001;
		public Integer SocketControlador2 = 9002;
	}
	public class TSensorPiezo extends TCollectionItem {
		public Integer Id = 0;
		public Double FatorCal = 0.0;
	}
	public class TConfigPistaPesagem extends TCollectionItem {
		public Integer IdPista = 0;
		public Double DistSegundoLacoSensor = 0.0;
		public TCollection<TSensorPiezo> Sensores = new TCollection<TSensorPiezo>(TSensorPiezo.class);
	}
	public class TConfigPesagem {
		public Boolean PesagemHabilitada = false;
		public TStringList ConfigConvAd = new TStringList();
		public TStringList ConfigPesagemParam = new TStringList();
		public TCollection<TConfigPistaPesagem> ConfigPistasPesagem = new TCollection<TConfigPistaPesagem>(TConfigPistaPesagem.class);
		public Double DataUltimaCalibracao = 0D;
	}
	public class TConfigDimensoesML {
		public Boolean HabilitarMedicaoML = false;
		public Boolean TimerHabilitar = false;
		public Integer TimerIntervalo = 0;
		public TStringList PontosVirtuais = new TStringList();
	}
	public class TConfigLacoVirtualML {
		public Boolean HabilitarLV_ML = false;
		public TStringList PontosVirtuais = new TStringList();
	}
	public class TResolucaoImagem {
		public Integer LarguraImagemInfracao = 0;
		public Integer AlturaImagemInfracao = 0;
		public Integer LarguraImagemOcr = 0;
		public Integer AlturaImagemOcr = 0;
	}
	public class TRodovia {
		public String nome = "";
		public Integer numero = 0;
		public String acesso = "";
		public Integer quilometro = 0;
		public Integer metros = 0;
	}
	public class TFlagOpcao {
		public Boolean ativo = false;
		public Boolean velocidade = false;
		public Boolean rodizio = false;
		public Boolean paradaFaixa = false;
		public Boolean contraMao = false;
		public Boolean avancoSinal = false;
		public Boolean avancoSinalVideo = false;
		public Boolean capturaFrontal = false;
		public Boolean capturaTraseira = false;
		public Boolean OCR = false;
		public Boolean irregular = false;
		public Boolean placaTarja = false;
		public Integer getCombinacao() {
			int ret = 0;
			//<colocar novo aqui>
			ret = (placaTarja ? 1 : 0) << 1;
			ret = (ret | (irregular ? 1 : 0)) << 1;
			ret = (ret | (OCR ? 1 : 0)) << 1;
			ret = (ret | (capturaTraseira ? 1 : 0)) << 1;
			ret = (ret | (capturaFrontal ? 1 : 0)) << 1;
			ret = (ret | (avancoSinalVideo ? 1 : 0)) << 1;
			ret = (ret | (avancoSinal ? 1 : 0)) << 1;
			ret = (ret | (contraMao ? 1 : 0)) << 1;
			ret = (ret | (paradaFaixa ? 1 : 0)) << 1;
			ret = (ret | (rodizio ? 1 : 0)) << 1;
			ret = (ret | (velocidade ? 1 : 0)) << 1;
			ret = (ret | (ativo ? 1 : 0));
			return ret;
		}
		public void setCombinacao(Integer combinacao) {
			ativo = (combinacao & 1) > 0;
			velocidade = (combinacao & 2) > 0;
			rodizio = (combinacao & 4) > 0;
			paradaFaixa = (combinacao & 8) > 0;
			contraMao = (combinacao & 16) > 0;
			avancoSinal = (combinacao & 32) > 0;
			avancoSinalVideo = (combinacao & 64) > 0;
			capturaFrontal = (combinacao & 128) > 0;
			capturaTraseira = (combinacao & 256) > 0;
			OCR = (combinacao & 512) > 0;
			irregular = (combinacao & 1024) > 0;
			placaTarja = (combinacao & 2048) > 0;
			//<colocar novo aqui>
		}

	}
	public class TAfericao {
		public String referencia = "";
		public String selagem = "";
		public Integer laudo = 0;
		public Double dataAfericao = 0D;
		public Double dataValidadeAfericao = 0D;
	}
	public class THorario extends TCollectionItem {
		public Integer idPista = 0;
		public Double horarioInicio = 0D;
		public Double horarioFim = 0D;
	}

	public class TNivelVideo extends TCollectionItem {
		public Integer idPista = 0;
		public Integer idCamera = 0;
		public Double horarioInicio = 0D;
		public Double horarioFim = 0D;
		public Double valor = 0D;
		public TNivelVideo() {}
	}
	public class TCapturaVeiculo {
		public Double distanciaLaco = 0D;
		public Double larguraLaco = 0D;
		public Integer comPerfilMagnetico = 0;
		public Integer numLacos = 1;
		public Integer triggerInfravermelho = 0;
		public Integer numCanal = 0;
		public Integer numImagensPosLaco = 0;
		public Double intervImagensPosLaco = 0D;
		public Integer distanciaPanoramicaPosLaco = 0;
	}
	public class TCamera extends TCollectionItem {
		public Integer ID = 0;
		public TCameraTypes Tipo = TCameraTypes.eCameraTypeNenhum;
		public String Endereco = "";
		public Boolean Relevante = false; 
	}
	public class TControladorPL extends TCollectionItem {
		public Integer id = 0;
		public String porta = "";
		public Integer bitsPorSegundo = 0;
		public Integer bitsDados = 0;
		public Integer bitsParada = 0;
		public Integer paridade = 0;
		public TCollection<TCanalPL> canais = new TCollection<TCanalPL>(TCanalPL.class);
		public TCollection<TChannelConfigs> canaisV2 = new TCollection<TChannelConfigs>(TChannelConfigs.class);
	}
	public class TCanalPL extends TCollectionItem {
		public Integer modoHabilitar = 0;
		public Integer modoSensibilidade = 0;
		public Integer sensibilidadeEntrada = 0;
		public Integer sensibilidadeSaida = 0;
		public Integer configOscilador = 0;
		public Integer eventosMonitorados = 0;
		public Integer divisorPerfil = 0;
	}
	public class TChannelConfigs extends TCollectionItem {
		public Integer id = 0;
		public TCollection<TLoopDetectorChannelConfig> configuracoes = new TCollection<TLoopDetectorChannelConfig>(TLoopDetectorChannelConfig.class); 
	}
	public class TLoopDetectorChannelConfig extends TCollectionItem {
		public Integer canal = 0;
		public Integer operacao = 0;
		public Integer registrador = 0;
		public Integer valor = 0;
	}
	public class TControladorPP extends TCollectionItem {
		public Integer id = 0;
		public Integer porta_S1 = 0;
		public Integer bitsPorSegundo_S1 = 0;
		public Integer bitsDados_S1 = 0;
		public Integer bitsParada_S1 = 0;
		public String paridade_S1 = "";
		public Integer tempoReconexao_S1 = 0;
		public Integer tamanhoBuffer_S1 = 0;
		public Integer porta_S2 = 0;
		public Integer bitsPorSegundo_S2 = 0;
		public Integer bitsDados_S2 = 0;
		public Integer bitsParada_S2 = 0;
		public String paridade_S2 = "";
		public Integer tempoReconexao_S2 = 0;
		public Integer tamanhoBuffer_S2 = 0;
		public TCollection<TCanalPP> canais = new TCollection<TCanalPP>(TCanalPP.class);
	}

	public class TCanalPP extends TCollectionItem {
		public Integer canalFisico = 0;
		public Integer offSet = 0;
		public Boolean inverterPolaridade = false;
	}
	public class TPista extends TCollectionItem {
		public Integer idPista = 0;
		public Integer codPista = 0;
		public String nomePista = "";
		public String sentido = "";
		
		public Integer TempoMaximoAmarelo = 0;
		public Integer TempoMaximoVermelho = 0;
		public Integer TempoMinimoAmarelo = 0;
		
		public Integer conector = 0;
		public Integer divId = 0;
		public Integer divDisplayId = 0;
		public Integer triggerInfravermelho = 0;
		public Integer painelInfravermelhoId = 0;
		public Integer ldrPainelId = 0;
		public Integer ldrPin = 0;
		public Integer codPistaProdam = 0;
		public TCapturaVeiculo capturaVeiculo = new TCapturaVeiculo();

		public Integer semaforoPainelId = 0;
		public TAfericao afericao = new TAfericao();
		public Boolean pista1Transversal = false;
		public Boolean pista2Transversal = false;
		public Boolean pista3Transversal = false;
		public Boolean pista4Transversal = false;
		public Boolean pista5Transversal = false;
		public Boolean Pista6Transversal = false;
		public Boolean Pista7Transversal = false;
		public Boolean Pista8Transversal = false;

		public Boolean CapturaObjFrente = false;
		public Boolean CapturaObjTras = false;

		public Integer CapturaObjLaco = 1; // Iniciado com 1, pois o padrão é capturar no laço 2. (LAÇO 1 = 0, LAÇO 2 = 1)
		public Integer CodArea = 0;
		
		public Boolean TipoDisparo = false;
		public Boolean CtrlNivelIluminador = false;
		public Integer NivelInicialIluminador = 1000;
		public Integer NivelFinalIluminador = 1000;
		public String  ListaNiveisIluminador = "1000";
		public Integer EndCameraIluminador = 1;
		
		public Boolean ParadaFaixaL1 = true;
	    public Boolean ParadaFaixaL2 = true;
		
	    public Boolean FaixaExclusivaEsquerda = false;
	    public Boolean FaixaExclusivaDireita = false;
	    
		public Integer codPistaTarja = 0;
		public Boolean entreFaixa = false;
		public Integer codLocalProdamAuxiliar = 0;
		
		//Códigos de identificação GIT para o Rio 2016
		////////////////////////////////////////////////////////////////////
		public Integer cod_GIT_Logradouro 		= 0;
		public Integer cod_GIT_Pista 			= 0;
		public Integer idPistaAlternativo 		= 0;
		public Integer cod_GIT_Sentido 			= 0;
		public Integer cod_GIT_Faixa 			= 0;
		////////////////////////////////////////////////////////////////////
		
		public Integer IDCameraFrontal = 0;
		public Integer IDCameraTraseira = 0;
		public Integer IDCameraPan1 = 0;
		public Integer IDCameraPan2 = 0;
		public Boolean PistaRelevante = false;
		
		public Boolean CapturaReversa = false;
		
		public TConfigAGD_Pista configAGD_Pista = new TConfigAGD_Pista();
	}
	public class TRegraInfracao extends TCollectionItem {
		public Integer idPista = 0;
		public Double iniRegra = 0D;
		public Double fimRegra = 0D;
		public Integer diaRegraIni = 0;
		public Integer diaRegraFim = 0;
		public Integer limite = 0;
		public Integer tolerancia = 0;
		public Integer toleranciaPortaria = 0;
		public Double tamIni = 0D;
		public Double tamFim = 0D;
		public Character perfil = ' ';
		public String tipo = "";
		public Boolean ativo = false;
		public Double toleranciaVermelho = 0D;
		public Double toleranciaFaixa = 0D;
		public Boolean usarPanoramica = false;
		public Integer opcaoPanoramica = 0;
		public Integer fiscalizarFase = 0;
		public Integer numImagensPosLaco = 0;
		public Double intervImagensPosLaco = 0D;
		public Integer toleranciaTransversal = 0;
		public Integer intervalo = 0;
		public Integer finalPlaca = -1;
		
		public Boolean removerIsencaoTaxi = false;
		public Double iniRegraTaxi = 0D;
		public Double fimRegraTaxi = 0D;
	}
	public class TRodizio extends TCollectionItem {
		public Double horarioInicio = 0D;
		public Double horarioFim = 0D;
		public Integer diaSemana = 0;
		public Integer finalPlaca = 0;
	}
	public class TServidor {
		public String host = "";
		public Integer port = 0;
	}
	public class TConfiguracaoDIV extends TCollectionItem {
		public Integer divId = 0;
		public Integer endereco = 0;
		public Integer portaCOM = 0;
		public Integer versao = 0;
		public Integer numeroDigitos = 0;
	}
	public class TConfiguracaoGeralDivs {
		public Boolean verdeVermelhoTolerancia = false;
		public Integer tipoTolerancia = 0;
		public Boolean mostrarVelocidade = true;
		public Integer velocidadeSeparador = 100;
		public Integer toleranciaFixa = 7;
		public Integer toleranciaPercentual = 7;
	}
	public class TConfiguracaoPainel extends TCollectionItem {
		public Integer painelId = 0;
		public Integer endereco = 0;
		public Integer portaCOM = 0;
	}
	public class TConfiguracaoGeralPainel extends TCollectionItem {
		public Boolean usarLDR = false;
		public Integer watchdogPainelId = 0;
	}
	public class TConfigAGD_Device extends TCollectionItem {
		public Integer Channel = 0;
		public String HighRangeThreshold = "";
		public String HighSpeedThreshold = "";
		public String LowRangeThreshold = "";
		public String LowSpeedThreshold = "";
		public String Text = "";
		public String PowerThreshold = "";
		public Integer Sense = 0;
	}
	public class TConfigAGD_Pista extends TCollectionItem {
		public String CaptureDistance = "";
		public String StartingBorder = "";
		public String EndingBorder = "";
		public String Direction = "";
		public Integer MinSamplesForProjection = 0;
		public Integer SpeedSamples = 0;
	}
	public class TConfigAGD_Road extends TCollectionItem {
		public String RoadSide = "";
		public String VerticalAngle = "";
		public String HorizontalAngle = "";
		public Integer SimMinSamples = 0;
	    public String SimSpeedTolerance = ""; 
	}
	public class TConfigAGD_Software extends TCollectionItem {
		public String PortName = "";
		public Integer PortBaudRate = 0;
		public String PortParity = "";
		public Integer PortDataBits = 0;
		public String PortStopBits = "";
		public Boolean ModoSimulacaoDoppler = false;
		public Integer TrackingMode = 0;
	}
	public TLocal local = new TLocal();
	public TConfiguracaoRelevante configuracaoRelevante = new TConfiguracaoRelevante();
	public TRodovia rodovia = new TRodovia();
	public TFlagOpcao flagOpcao = new TFlagOpcao();
	public TAfericao afericao = new TAfericao();
	public TCollection<THorario> horarios = new TCollection<THorario>(THorario.class);
	public TCollection<TNivelVideo> niveisVideo = new TCollection<TNivelVideo>(TNivelVideo.class);
	public TCollection<TCamera> cameras = new TCollection<TCamera>(TCamera.class);
	public TCollection<TControladorPL> controladoresPL = new TCollection<TControladorPL>(TControladorPL.class);
	public TCollection<TControladorPP> controladoresPP = new TCollection<TControladorPP>(TControladorPP.class);
	public TCollection<TPista> pistas = new TCollection<TPista>(TPista.class);
	public TCollection<TRegraInfracao> regrasInfracao = new TCollection<TRegraInfracao>(TRegraInfracao.class);
	public TCollection<TRodizio> rodizios = new TCollection<TRodizio>(TRodizio.class);
	public TConfiguracaoGeralDivs divsGeral = new TConfiguracaoGeralDivs();
	public TCollection<TConfiguracaoDIV> configuracaoDIVs = new TCollection<TConfiguracaoDIV>(TConfiguracaoDIV.class);
	public Integer serie = 0;
	public Integer idProduto = 0;
	public String observacao = "";
	public Integer watchDog = 0;
	public Integer iluminador = 0;
	public Integer comIluminador = 0;
	public Integer comControladora = 0;
	public Integer comAuxiliar = 0;
	public Integer controladora = 0;
	public TServidor servidorPrimario = new TServidor();
	public TServidor servidorSecundario = new TServidor();
	
    public TServidor servidorMontante = new TServidor();
    public Integer distanciaEntreEquip = 0;
    public Integer tempoDeCiclagem = 0;
    
    public Integer TempoTotalVideo = 0;
    public Integer TempoVideoAntesInfracao = 0;
    
    public Integer AtivarEquipamentoComoMontante = 0;
    public Integer AtivarEquipamentoComoJusante = 0;
    public Integer PortaEquipamentoMontante = 0;
    public Integer CodigoLocalMontante = 0;
	
	public Integer idGrupoEquipamento = 0;
	public Double dataHoraConfiguracao = 0D;
	public Double inicioOperacao = 0D;
	
	public Integer categoria = 0;
	public Integer versaoConfigEquipApp = 0;
	
	public TLocalidade localidade = new TLocalidade();
	public TStringList parametrosAdicionais = new TStringList();
	public TConfiguracaoGeralPainel configuracaoGeralPainel = new TConfiguracaoGeralPainel();
	public TCollection<TConfiguracaoPainel> configuracaoPaineis = new TCollection<TConfiguracaoPainel>(TConfiguracaoPainel.class);
	
	public TConfigAGD_Device configAGD_Device = new TConfigAGD_Device();
	public TConfigAGD_Road configAGD_Road = new TConfigAGD_Road();
	public TConfigAGD_Software configAGD_Software = new TConfigAGD_Software();
	
	public Integer tempoAdicionalFaixaExclusiva = 60;
	public Integer tempoFluxoZero = 600;
	public Integer diferencaPercentualBloqueioFaixa = 100;
	
	public TConfigPesagem Pesagem = new TConfigPesagem();
	public TConfigDimensoesML DimensoesML = new TConfigDimensoesML();
	public TConfigLacoVirtualML LacoVirtualML = new TConfigLacoVirtualML();
	public TResolucaoImagem ResolucaoImagem = new TResolucaoImagem();
}
