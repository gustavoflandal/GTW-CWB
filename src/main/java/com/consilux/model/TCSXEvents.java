package com.consilux.model;

import java.util.Date;
import com.consilux.infra.Funcoes;



/**
 * Classe serializavel semelhante ao do Delphi
 * @author Fernando de Souza
 *
 */

public class TCSXEvents {

	public static final String NAME_CLASS = "CSXEvent";
	
	public enum CSXEventPriority {ep_Low, ep_Normal, ep_High};
	public enum CSXEventLevel {el_Info, el_Warning, el_Error, el_Critical, el_Debug};
	
	public String eventOwner;
	public Double eventDateTime;
	public Integer eventCategoryId;
	public Integer eventId;
	public String eventDescription;
	public String eventMessage;
	public CSXEventPriority eventPriority;
	public CSXEventLevel eventLevel;
	public String eventUsername;

	public Date getEventDateTime(){
	
		return Funcoes.convertPascalDateToUTC( eventDateTime );
		
	}
	
}
