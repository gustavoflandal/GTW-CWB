package com.consilux.ui.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoMapa;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.model.LocalStatusCompleto;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.ui.client.MonitoramentoMapService;
import com.consilux.ui.client.beans.MapGwtBean;
import com.consilux.ui.client.beans.RadarGwtBean;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("MonitoramentoMapService")
public class MonitoramentoMapServiceImpl extends GwtBaseServlet implements MonitoramentoMapService {

	private static MonitoramentoMapService instance = null;
	private static final Logger logger = Logger.getLogger(MonitoramentoMapServiceImpl.class);
	
	public static void notificar() {

		if( instance == null)
			return;

		synchronized (instance) {
			instance.notify();
		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
		if (instance == null){
			instance = this;
		}
	}

	private static final long serialVersionUID = 8331871454339793007L;

	private static final long TEMPO_MAXIMO_ESPERA_POOLING = 15000;

	public class Coordinate {
		private double latitude;
		private double longitude;

		public Coordinate(double lat, double lng) {
			super();
			this.latitude = lat;
			this.longitude = lng;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

	}

	public class Bounds {
		private Coordinate northEast;
		private Coordinate southWest;

		public Bounds(Coordinate northEast, Coordinate southWest) {
			super();
			this.northEast = northEast;
			this.southWest = southWest;
		}

		public Coordinate getNorthEast() {
			return northEast;
		}
		public void setNorthEast(Coordinate northEast) {
			this.northEast = northEast;
		}
		public Coordinate getSouthWest() {
			return southWest;
		}
		public void setSouthWest(Coordinate southWest) {
			this.southWest = southWest;
		}

		public boolean containsCoordinate(Coordinate coordinate){
			if (coordinate == null)
				return false;

			if (Double.compare(coordinate.getLatitude(),northEast.getLatitude()) > 0 || 
					Double.compare(coordinate.getLatitude(),southWest.getLatitude()) < 0  ) 
				return false;

			if (Double.compare(coordinate.getLongitude(),northEast.getLongitude()) > 0 || 
					Double.compare(coordinate.getLongitude(),southWest.getLongitude()) < 0  ) 
				return false;

			return true;
		}
	}

	public double getRandomPoint(double maxRadio, double x){
		double p;
		Random rand = new Random();
		if (Math.round(Math.abs(x)) > 25){
			p = x + maxRadio * Math.cos(rand.nextInt(361));
		} else {
			p = x + maxRadio* Math.sin(rand.nextInt(361));
		}
		return p;
	}


	private RadarGwtBean localStatusCompletoToRadarGwtBean(LocalStatusCompleto lsc){
		RadarGwtBean radar = new RadarGwtBean();
		radar.setDescricao(String.valueOf(lsc.getSerieEquipamento()));
		radar.setCodigoLocal(lsc.getIdLocal());
		radar.setLocal(lsc.getNome());
		radar.setStatusConexao(lsc.getStatusConexao());
		radar.setStatusEnergia(lsc.getStatusEnergia());
		radar.setStatusDiv(lsc.getStatusDIV());
		radar.setIP(lsc.getIP());
		radar.setUltimaDeteccao(lsc.getUltimaDeteccao());
		radar.setVersao(lsc.getVersao());
		radar.setTempoConectado(lsc.getTempoConectado());
		radar.setTempoDesconectado(lsc.getTempoDesconectado());
		radar.setTempoExecutando(String.valueOf(lsc.getTempoExecutando()/(1000*60)));
		radar.setVeiculoIrregular(lsc.getVeiculoIrregular());
		radar.setCopiaArquivos(lsc.getStatusCopia());
		radar.setLatitude(lsc.getPosicaoLat());
		radar.setLongitude(lsc.getPosicaoLon());
		return radar;
	}

	public List<LocalStatusCompleto> updateLocais(MapGwtBean mapBean, String timestamp, boolean getAll) throws Exception{
		
		return 
			LocalStatusCompleto.buscaLocalStatusCompletoPorLatLng(
				mapBean.getSouth(),
				mapBean.getNorth(), 
				mapBean.getWest(), 
				mapBean.getEast(),
				timestamp,
				getAll);
	}

	@Override
	public List<RadarGwtBean> getRadares(MapGwtBean mapBean) throws Exception {
		
		// TODO: verificar porque não é feto a validação da sessão..
		//verificarSessaoLogada();
		
		List<RadarGwtBean> radares = new ArrayList<RadarGwtBean>();
		try {
		
			boolean getEveryOne = mapBean.isGetEveryone();
			Calendar ultimoContato = null;
			Calendar timestamp = Calendar.getInstance();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			
			HttpSession session = super.getSession();
			if (session != null)
			{
				ultimoContato = (Calendar) session.getAttribute("ultimoContato");
			} else {
				session = super.createSession();
			}
	
			if (ultimoContato == null){
				ultimoContato = Calendar.getInstance();
			}
	
			String strTimestamp = df.format(ultimoContato.getTime());
	
			List<LocalStatusCompleto> locais = new ArrayList<LocalStatusCompleto>(0);
	
			synchronized (this) {
	
				locais = updateLocais(mapBean, strTimestamp, getEveryOne);
	
				if (locais.size() == 0){
	
					this.wait( TEMPO_MAXIMO_ESPERA_POOLING );
	
					updateLocais(mapBean, strTimestamp, getEveryOne);
	
					this.notifyAll();
				}
			}
	
			for (LocalStatusCompleto local : locais) {
				radares.add(localStatusCompletoToRadarGwtBean(local));
			}
	
			session.setAttribute("ultimoContato", timestamp);
			

		} catch (Exception ex)
		{
			logger.error("Erro no serviço do mapa de monitoramento.", ex);
			return null;
		}
		
		return radares;
	}

	@Override
	public MapGwtBean getConfiguracoes() throws Exception {
		Configuracao configuracao = ConfiguracaoProvider.getInstance();
		ConfiguracaoMapa mapConf = configuracao.getConfiguracaoMapa();
		MapGwtBean bean = new MapGwtBean();
		bean.setCenter(mapConf.getLatitudeCentro(), mapConf.getLongitudeCentro());
		return bean;
	}
	
	
}
