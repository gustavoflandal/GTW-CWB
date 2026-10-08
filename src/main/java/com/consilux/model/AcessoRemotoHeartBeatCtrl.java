package com.consilux.model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.EventoCSX.TipoEvento;
import com.consilux.model.beans.AcessoRemotoHeartBeat;


public class AcessoRemotoHeartBeatCtrl {

	private static AcessoRemotoHeartBeatCtrl instance = new AcessoRemotoHeartBeatCtrl();
	private List<AcessoRemotoHeartBeat> listaBeans = new ArrayList<AcessoRemotoHeartBeat>();

	private class ThreadHeartBeat extends Thread  {

		private static final long TEMPO_MAXIMO_POLLING = 10000;
		private AcessoRemotoHeartBeat bean; 
		public ThreadHeartBeat(AcessoRemotoHeartBeat acessoRemotoHeartBeat) {

			super();
			this.bean = acessoRemotoHeartBeat;

		}

		@Override
		public void run() {

			esperar();

			long agora = new Date().getTime();

			long tempoPooling = ( agora - this.bean.getUltimoPooling() ); 

			boolean acessoRemotoEncerrado = tempoPooling >= TEMPO_MAXIMO_POLLING;

			if( acessoRemotoEncerrado ){
				
				try {
					System.out.println(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + " > Encerrado Sessão Remota do equipamento " + bean.getSerieEquipamento() + " ...[" + bean.getLoginUsuario() + "]" );
					Evento.incluirEventoCSX( 
							new EventoCSX( TipoEvento.REMOTE_CLOSED , 
							bean.getLoginUsuario() , 
							Integer.toString( bean.getSerieEquipamento() ),
							"" ) );
				} catch (ConexaoException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				finally{
					listaBeans.remove( bean );
				}
				
				
			}
			
			
		}

		private synchronized void esperar() {

			try {

				wait( TEMPO_MAXIMO_POLLING );

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	};	

	public static AcessoRemotoHeartBeatCtrl getInstance(){

		return instance;

	}

	public void notificar( Usuario usuario , LocalVigente local ){

		AcessoRemotoHeartBeat acessoRemotoHeartBeat = new AcessoRemotoHeartBeat( usuario , local );
		int idx = listaBeans.indexOf( acessoRemotoHeartBeat );

		if ( idx >= 0 ){
			acessoRemotoHeartBeat = listaBeans.get( idx );
		}
		else{
			listaBeans.add( acessoRemotoHeartBeat );
		}

		acessoRemotoHeartBeat.atualizaUltimoPooling();

		new ThreadHeartBeat( acessoRemotoHeartBeat ).start();
	}




}
