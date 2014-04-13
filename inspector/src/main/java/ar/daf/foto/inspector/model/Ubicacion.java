package ar.daf.foto.inspector.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@Embeddable
public class Ubicacion {

	@Column(name="POSICIONAMIENTO")
	@NotNull
	private boolean posicionamiento;
	@Column(name="LATITUD")
	@NotNull
	private double latitud;
	@Column(name="LONGITUD")
	@NotNull
	private double longitud;
	@Column(name="DIRECCION")
	@Length(min=0, max=1024)
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
