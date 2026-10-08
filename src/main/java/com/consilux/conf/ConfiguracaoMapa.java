/**
 * 
 */
package com.consilux.conf;

import java.io.Serializable;

/**
 * @author charles.maske
 *
 */
public class ConfiguracaoMapa implements Serializable {

	
	private static final long serialVersionUID = 5068520846100108614L;
	
	private Double latitudeCentro;
	private Double longitudeCentro;
	private String googleMapsKey;
	
	public ConfiguracaoMapa() {
		super();
		this.latitudeCentro = 0.0;
		this.longitudeCentro = 0.0;
		this.googleMapsKey = "";
	}

	public ConfiguracaoMapa(Double latitudeCentro, Double longitudeCentro,
			String googleMapsKey) {
		super();
		this.latitudeCentro = latitudeCentro;
		this.longitudeCentro = longitudeCentro;
		this.googleMapsKey = googleMapsKey;
	}

	public Double getLatitudeCentro() {
		return latitudeCentro;
	}

	public void setLatitudeCentro(Double latitudeCentro) {
		this.latitudeCentro = latitudeCentro;
	}

	public Double getLongitudeCentro() {
		return longitudeCentro;
	}

	public void setLongitudeCentro(Double longitudeCentro) {
		this.longitudeCentro = longitudeCentro;
	}

	public String getGoogleMapsKey() {
		return googleMapsKey;
	}

	public void setGoogleMapsKey(String googleMapsKey) {
		this.googleMapsKey = googleMapsKey;
	}
	
	
	
	
}
