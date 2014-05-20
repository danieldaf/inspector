package ar.daf.foto.inspector.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

@Embeddable
public class AlbumInfo {
	
	/**
	 * Los campos versionMayor, versionMenor y versionRevision indican el nro de version de la base de datos
	 */
	@Column(name="VERSION_MAYOR")
	@NotNull
	@Min(0)
	private int versionMayor;
	@Column(name="VERSION_MENOR", nullable=false)
	@NotNull
	@Min(0)
	private int versionMenor;
	@Column(name="VERSION_REVISION", nullable=false)
	@NotNull
	@Min(0)
	private int versionRevision;
	
	/**
	 * Este campo cotiene un hash usando SHA1 del path relativo del album dentro de la carpeta base.
	 * Es decir es un hash del los campos path+File.separator+fileName del Album.
	 * Es el id que permite identificar un album univocamente entre distintos servidores.
	 */
	@Column(name="HASH_ID")
	@NotNull
	@Length(min=0, max=128)
	private String hashId;
	
	/**
	 * Este campo contiene la fecha de actualizacion de la carpeta que contiene el album.
	 * Se empla para determinar con rapidez si es necesario o no actualizar el album.
	 */
	@Column(name="FECHA_ACTUALIZACION")
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaActualizacion;
	/**
	 * Este campo cotiene un hash usando SHA1 del archivo de texto (json) del album.
	 * El hash se calcula poniendo el campo hashContent y fechaActualizacion en null.
	 */
	@Column(name="CONTENT_HASH")
	@Length(min=0, max=128)
	private String contentHash;
	
	public String getHashId() {
		return hashId;
	}
	public void setHashId(String hashId) {
		this.hashId = hashId;
	}
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
