package ar.daf.foto.inspector.model;

import org.joda.time.DateTime;

import ar.daf.foto.utilidades.json.JsonClass;
import ar.daf.foto.utilidades.json.JsonDateProperty;
import ar.daf.foto.utilidades.json.JsonProperty;

@JsonClass
public class AlbumInfo {

	/**
	 * Los campos versionMayor, versionMenor y versionRevision indican el nro de version de la base de datos
	 */
	@JsonProperty
	private int versionMayor;
	@JsonProperty
	private int versionMenor;
	@JsonProperty
	private int versionRevision;
	
	/**
	 * Este campo contiene la fecha de actualizacion de la carpeta que contiene el album.
	 * Se empla para determinar con rapidez si es necesario o no actualizar el album.
	 */
	@JsonDateProperty
	private DateTime fechaActualizacion;
	/**
	 * Este campo cotiene un hash usando SHA1 del archivo de texto (json) del album.
	 * El hash se calcula poniendo el campo hashContent y fechaActualizacion en null.
	 */
	@JsonProperty
	private String contentHash;

	public DateTime getFechaActualizacion() {
		return fechaActualizacion;
	}
	public void setFechaActualizacion(DateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
	public String getContentHash() {
		return contentHash;
	}
	public void setContentHash(String contentHash) {
		this.contentHash = contentHash;
	}
	public int getVersionMayor() {
		return versionMayor;
	}
	public void setVersionMayor(int versionMayor) {
		this.versionMayor = versionMayor;
	}
	public int getVersionMenor() {
		return versionMenor;
	}
	public void setVersionMenor(int versionMenor) {
		this.versionMenor = versionMenor;
	}
	public int getVersionRevision() {
		return versionRevision;
	}
	public void setVersionRevision(int versionRevision) {
		this.versionRevision = versionRevision;
	}
	
}
