package ar.daf.foto.inspector.model;

import ar.daf.foto.utilidades.json.JsonClass;
import ar.daf.foto.utilidades.json.JsonProperty;

@JsonClass
public class Ubicacion {
	@JsonProperty
	private boolean posicionamiento;
	@JsonProperty
	private double latitud;
	@JsonProperty
	private double longitud;
	@JsonProperty
	private String direccion;
	
	public boolean isPosicionamiento() {
		return this.posicionamiento;
	}
	public void setPosicionamiento(boolean posicionamiento) {
		this.posicionamiento = posicionamiento;
	}
	public double getLatitud() {
		return latitud;
	}
	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}
	public double getLongitud() {
		return longitud;
	}
	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
}
