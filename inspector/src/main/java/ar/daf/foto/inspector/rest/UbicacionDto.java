package ar.daf.foto.inspector.rest;

import ar.daf.foto.inspector.model.Ubicacion;

public class UbicacionDto {

	private boolean posicionamiento;
	private double latitud;
	private double longitud;
	private String direccion;

	public static UbicacionDto fromUbicacion(Ubicacion ubicacion) {
		UbicacionDto result = null;
		if (ubicacion != null) {
			result = new UbicacionDto();
			result.setPosicionamiento(ubicacion.isPosicionamiento());
			result.setLatitud(ubicacion.getLatitud());
			result.setLongitud(ubicacion.getLongitud());
			result.setDireccion(ubicacion.getDireccion());
		}
		return result;
	}
	
	public static Ubicacion toUbicacion(UbicacionDto ubicacionDto) {
		Ubicacion result = null;
		if (ubicacionDto != null) {
			result = new Ubicacion();
			result.setPosicionamiento(ubicacionDto.isPosicionamiento());
			result.setLatitud(ubicacionDto.getLatitud());
			result.setLongitud(ubicacionDto.getLongitud());
			result.setDireccion(ubicacionDto.getDireccion());
		}
		return result;
	}

	public boolean isPosicionamiento() {
		return posicionamiento;
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
