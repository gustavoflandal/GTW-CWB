package com.consilux.ws.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.consilux.infra.LocalWS;
import com.consilux.infra.SerializadorXML;
import com.consilux.model.Evento;
import com.consilux.model.EventoCSX;
import com.consilux.model.TCSXEvents;

public class CSXEventsWS extends LocalWS{

	public void registrarEvento( String xmlCSXEvent ) throws Exception{

		System.out.println("WSCALL registrarEvento: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		try {

			StringBuffer buf = new StringBuffer(xmlCSXEvent);
			TCSXEvents event = new TCSXEvents();
			SerializadorXML ser = new SerializadorXML();

			ser.loadFromStringBuffer(buf); 
			ser.readObject( event , TCSXEvents.NAME_CLASS );

			EventoCSX eventoCSX = new EventoCSX( 
					event.eventOwner ,
					event.getEventDateTime() ,
					event.eventCategoryId ,
					event.eventId ,
					event.eventMessage ,
					event.eventPriority.ordinal()  ,
					event.eventLevel.ordinal(),
					event.eventUsername ); 

			Evento.incluirEventoCSX( eventoCSX );

		}
		catch(Exception e) {

			handleException( e );

		}


		System.out.println("END_WSCALL registrarEvento: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
	}

}
