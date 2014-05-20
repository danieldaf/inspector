package ar.daf.foto.inspector.syncro;

import ar.daf.foto.inspector.model.Ubicacion;


public class SyncUbicacionDto {

	private boolean posicionamiento;
	private double latitud;
	private double longitud;
	private String direccion;
	
	public static SyncUbicacionDto fromUbicacion(Ubicacion ubicacion) {
		SyncUbicacionDto result = null;
		if (ubicacion != null) {
			result = new SyncUbicacionDto();
			result.setPosicionamiento(ubicacion.isPosicionamiento());
			result.setLatitud(ubicacion.getLatitud());
			result.setLongitud(ubicacion.getLongitud());
			result.setDireccion(ubicacion.getDireccion());
		}
		return result;
	}
	
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
